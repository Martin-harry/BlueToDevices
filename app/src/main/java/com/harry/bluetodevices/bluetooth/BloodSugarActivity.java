package com.harry.bluetodevices.bluetooth;

import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.harry.bluetodevices.R;
import com.harry.bluetodevices.base.BaseActivity;
import com.harry.bluetodevices.bean.BleService;
import com.harry.bluetodevices.util.DealNum;
import com.harry.bluetodevices.util.HexUtil;
import com.harry.bluetodevices.util.LogUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.text.TextUtils.isEmpty;

/**
 * @author Martin-harry
 * @date 2021/11/24
 * @address
 * @Desc 动态血糖上位机
 */
public class BloodSugarActivity extends BaseActivity implements View.OnClickListener {
    public static final String KEY_DATA = "key_data";
    private EditText et_server;
    private EditText et_rx;
    private EditText et_tx;
    private ImageView back;
    private TextView devName;
    private Button wrtMath;
    private TextView notifyType = null;
    //设备
    private BleDevice bleDevice;
    private String serverUuid;
    private String Rid;
    private String Tid;
    //通知返回数据
    private byte[] bytes;
    private String txMath;
    private TextView math;
    //BLE通讯
    private RelativeLayout batteryRet;
    private TextView batteryLevel;
    private TextView levelType;
    private RelativeLayout bloodRet;
    private TextView devProdct;
    private TextView mathNumber;
    private TextView currenTxt;
    private TextView tempTxt;
    private TextView batteryTime;
    private RelativeLayout systemRet;
    private TextView systemTxt;
    private TextView systemType;
    private RelativeLayout timeRet;
    private TextView time;
    private TextView runTime;

    @Override
    protected void initView() {
        setStatusBg(4);
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);

        et_server = findViewById(R.id.et_server);
        et_rx = findViewById(R.id.et_rx);
        et_tx = findViewById(R.id.et_tx);
        devName = findViewById(R.id.devName);
        wrtMath = findViewById(R.id.wrtMath);
        math = findViewById(R.id.math);
        back = findViewById(R.id.back);
        wrtMath.setOnClickListener(this);
        back.setOnClickListener(this);

        //电池服务
        batteryRet = findViewById(R.id.batteryRet);
        batteryLevel = findViewById(R.id.batteryLevel);
        levelType = findViewById(R.id.levelType);
        //血糖服务
        bloodRet = findViewById(R.id.bloodRet);
        devProdct = findViewById(R.id.devProdct);
        mathNumber = findViewById(R.id.mathNumber);
        currenTxt = findViewById(R.id.currenTxt);
        tempTxt = findViewById(R.id.tempTxt);
        batteryTime = findViewById(R.id.batteryTime);
        //系统信息服务
        systemRet = findViewById(R.id.systemRet);
        systemTxt = findViewById(R.id.systemTxt);
        systemType = findViewById(R.id.systemType);
        //时间服务
        timeRet = findViewById(R.id.timeRet);
        time = findViewById(R.id.time);
        runTime = findViewById(R.id.runTime);

        if (bleDevice == null) {
            finish();
        }
        boolean connected = BleManager.getInstance().isConnected(bleDevice);
        Log.e("蓝牙设备连接状态", "connected: >>>> " + connected);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences settings = getSharedPreferences("Selector", 0);
        Boolean user_first = settings.getBoolean("FIRST", true);
        if (user_first) {
            settings.edit().putBoolean("FIRST", false).commit();
            LogUtils.e("进入activity", "第一次进入");
        } else {
            readParameter();
            LogUtils.e("进入activity", "第二次进入");
        }

        LogUtils.e("进入activity", "onStart---");
    }

    @Override
    protected void onStop() {
        super.onStop();

        //存储edit数据
        SharedPreferences sharedPreferences = getSharedPreferences("editMath",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("server", et_server.getText().toString());
        editor.putString("rid", et_rx.getText().toString());
        editor.putString("tid", et_tx.getText().toString());
        editor.commit();

        LogUtils.e("进入activity", "onStop---");
    }

    @Override
    protected void initData() {
        String name = bleDevice.getName();
        if (name != null) {
            devName.setText(name + "");
        } else {
            devName.setText(R.string.otherDevice);
        }
        LogUtils.e("当前设备名", name + "");

        //判断应用的蓝牙协议
        if (name.equals("BJYC-CGM-01")) {//动态血糖上位机
            et_server.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String server = et_server.getText().toString().trim();
                    String s1 = DealNum.substring(BleService.serviceUuid_BATTERY, 4, 8);
                    String s2 = DealNum.substring(BleService.serviceUuid_BLOOD_SUGAR, 4, 8);
                    String s3 = DealNum.substring(BleService.serviceUuid_SYSTEM_MSG, 4, 8);
                    String s4 = DealNum.substring(BleService.serviceUuid_TIME, 4, 8);
                    LogUtils.e("服务以及特征值：---", s1 + "---" + s2 + "---" + s3 + "---" + s4);
                    if (server != null && !isEmpty(server)) {
                        if (s1.equalsIgnoreCase(server)) {//1808(电池服务)
                            serverUuid = BleService.serviceUuid_BATTERY;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        } else if (s2.equalsIgnoreCase(server)) {//cdd0(血糖服务)
                            serverUuid = BleService.serviceUuid_BLOOD_SUGAR;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        } else if (s3.equalsIgnoreCase(server)) {//cdd0(系统信息服务)
                            serverUuid = BleService.serviceUuid_SYSTEM_MSG;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        } else if (s4.equalsIgnoreCase(server)) {//ff00(时间服务)
                            serverUuid = BleService.serviceUuid_TIME;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            et_rx.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String rid = et_rx.getText().toString().trim();
                    String BATTERY = DealNum.substring(BleService.RUuid_BATTERY, 4, 8);
                    String BLOOD = DealNum.substring(BleService.RUuid_BLOOD_SUGAR, 4, 8);
                    String SYSTEM = DealNum.substring(BleService.RUuid_SYSTEM_MSG, 4, 8);
                    String TIME = DealNum.substring(BleService.RUuid_TIME, 4, 8);
                    LogUtils.e("服务以及特征值：---", BATTERY + "---" + BLOOD + "---" + SYSTEM + "---" + TIME);
                    if (rid != null && !isEmpty(rid)) {
                        if (BATTERY.equalsIgnoreCase(rid)) {//2a18(电池服务)
                            Rid = BleService.RUuid_BATTERY;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---标准（读）", Rid);
                        } else if (BLOOD.equalsIgnoreCase(rid)) {//cdd1(血糖服务)
                            Rid = BleService.RUuid_BLOOD_SUGAR;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（读）", Rid);
                        } else if (SYSTEM.equalsIgnoreCase(rid)) {//cdd1(系统信息服务)
                            Rid = BleService.RUuid_SYSTEM_MSG;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（读）", Rid);
                        } else if (TIME.equalsIgnoreCase(rid)) {//ff01(时间服务)
                            Rid = BleService.RUuid_TIME;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（读）", Rid);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            et_tx.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private void readParameter() {
        //获取edit数据
        SharedPreferences sharedPreferences = getSharedPreferences("editMath",
                MODE_PRIVATE);
        String server = sharedPreferences.getString("server", "");
        String rid = sharedPreferences.getString("rid", "");
        String tid = sharedPreferences.getString("tid", "");
        LogUtils.e("edit存储的数据信息：", server + "---" + rid + "---" + tid);
        if (!isEmpty(server) || !isEmpty(rid) || !isEmpty(tid)) {
            et_server.setText(server);
            et_rx.setText(rid);
            et_tx.setText(tid);

//            if(bleDevice != null){
//                opeNotify(bleDevice);
//                LogUtils.e("edit存储的数据信息：", "执行打开通知的操作！！！");
//            }
        }
    }

    @Override
    protected int createViews() {
        return R.layout.activity_blood_sugar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.wrtMath:
                String rxMath = et_rx.getText().toString();
                txMath = et_tx.getText().toString().trim();
                if (TextUtils.isEmpty(txMath) || TextUtils.isEmpty(rxMath)) {
                    Toast.makeText(getApplicationContext(), R.string.paramWrit, Toast.LENGTH_SHORT).show();
                } else {
                    String BATTERY = DealNum.substring(BleService.TUuid_BATTERY, 4, 8);
                    String BLOOD = DealNum.substring(BleService.TUuid_BLOOD_SUGAR, 4, 8);
                    String SYSTEM = DealNum.substring(BleService.TUuid_SYSTEM_MSG, 4, 8);
                    String TIME = DealNum.substring(BleService.TUuid_TIME, 4, 8);
                    LogUtils.e("服务以及特征值：---", BATTERY + "---" + BLOOD + "---" + SYSTEM + "---" + TIME);
                    if (BATTERY.equalsIgnoreCase(txMath)) {//ffe1(电池服务)
                        Tid = BleService.TUuid_BATTERY;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    } else if (BLOOD.equalsIgnoreCase(txMath)) {//cdd2(血糖服务)
                        Tid = BleService.TUuid_BLOOD_SUGAR;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    } else if (SYSTEM.equalsIgnoreCase(txMath)) {//cdd2(系统信息服务)
                        Tid = BleService.TUuid_SYSTEM_MSG;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    } else if (TIME.equalsIgnoreCase(txMath)) {//ff01(时间服务)
                        Tid = BleService.TUuid_TIME;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    }
                }
                break;
        }

    }

    /**
     * 发送数据
     *
     * @param ble
     */
    private void sendWrite(BleDevice ble) {//发送数据 ---> 头字节 + 命令码 + 尾字节(异或校验)
        if (serverUuid == null || Tid == null) {
            return;
        }
        LogUtils.e("UUID---发送数据（serverUuid/Tid）", serverUuid + "---" + Tid);

        String sendName = ble.getName();
        LogUtils.e("当前发送数据的设备", sendName + "");
        bytes = new byte[3];
        bytes[0] = (byte) 0xc5;
        bytes[1] = (byte) 0x05;
        bytes[2] = (byte) 0xc4;

        BleManager.getInstance().write(
                bleDevice,
                serverUuid,
                Tid,
                bytes,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                        Toast.makeText(BloodSugarActivity.this, R.string.writSuccess, Toast.LENGTH_LONG).show();
                        LogUtils.e("txt---writeSuccess", "数据发送成功");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        // 发送数据到设备失败
                        Toast.makeText(BloodSugarActivity.this, R.string.writFaile, Toast.LENGTH_LONG).show();
                        LogUtils.e("txt---writeFailure", "数据发送失败");
                    }
                }
        );
    }

    /**
     * 打开通知
     *
     * @param ble
     */
    private void opeNotify(BleDevice ble) {
        if (serverUuid == null || Rid == null) {
            return;
        }
        LogUtils.e("UUID---打开通知（serverUuid/Rid）", serverUuid + "---" + Rid);

        BleManager.getInstance().notify(
                ble,
                serverUuid,
                Rid,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {//打开通知操作成功
                        LogUtils.e("txt---打开通知成功", "notify success");
                        notifyType.setText("客户端状态：打开通知成功");
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {//打开通知操作失败
                        LogUtils.e("txt---打开通知失败", exception.toString());
                        notifyType.setText("客户端状态：打开通知失败");
                    }

                    @Override
                    public void onCharacteristicChanged(final byte[] data) {//通知成功后，返回数据
                        if (data != null && data.length > 0) {
                            new Handler().postDelayed(new Runnable() {//延时处理
                                @Override
                                public void run() {
                                    String hexStr = HexUtil.encodeHexStr(data, true);
                                    LogUtils.e("txt---16进制字符串", "数据：" + hexStr + "；长度：" + data.length);

                                    //接收数据
                                    notifyData(bleDevice, hexStr);
                                }
                            }, 100);
                        } else {
                            LogUtils.e("txt---data为空", "无数据");
//                            addText(txt, "数据信息为空！");
                        }
                    }
                }
        );
    }

    /**
     * 通知成功返回的数据
     *
     * @param ble
     * @param hexStr
     */
    private void notifyData(BleDevice ble, String hexStr) {
        LogUtils.e("txt---通知接收的数据", hexStr);
//        addText(math, hexStr);
        if (!TextUtils.isEmpty(txMath)) {
//            nearOil.setText(hexStr);
//            nearOils.setText(hexStr);
        }

        //将数据信息存储到本地
        try {
            FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/math.txt", true);
            OutputStreamWriter ost = new OutputStreamWriter(fos);
            ost.write(hexStr);
            ost.write("\n");
            ost.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String bleName = ble.getName();
        if (bleName.equals("BJYC")) {
            try {
                //将数据信息存储到本地
                FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/BJYC数据.txt", true);
                OutputStreamWriter ost = new OutputStreamWriter(fos);

                //拆分字符串，16转10进制
                String subStr = DealNum.substring(hexStr, 20, 22);
                int anInt = Integer.parseInt(subStr, 16);
                float MathUnit = anInt / 10F;
//                addText(txType, "血糖值：");
//                addText(txt, MathUnit + "mmol/L");
                ost.write("血糖值：" + MathUnit + "mmol/L；");

                String a = DealNum.substring(hexStr, 6, 8);
                String b = DealNum.substring(hexStr, 8, 10);
                String c = new StringBuilder().append(b).append(a).toString();
                int year = Integer.parseInt(c, 16);
                String month = DealNum.substring(hexStr, 10, 12);
                int months = Integer.parseInt(month, 16);
                String day = DealNum.substring(hexStr, 12, 14);
                int days = Integer.parseInt(day, 16);
                String hour = DealNum.substring(hexStr, 14, 16);
                int hours = Integer.parseInt(hour, 16);
                String minute = DealNum.substring(hexStr, 16, 18);
                int minutes = Integer.parseInt(minute, 16);
                String sec = DealNum.substring(hexStr, 18, 20);
                int secs = Integer.parseInt(sec, 16);
                LogUtils.e("txt---拆分日期", year + "年" + months + "月" + days + "日" + hours + "时" + minutes + "分" + secs + "秒");

//                addText(dataMath, "测量时间：" + year + "-" + months + "-" + days + " " + hours + ":" + minutes + ":" + secs);
                ost.write("测量时间：" + year + "-" + months + "-" + days + " " + hours + ":" + minutes + ":" + secs);
                ost.write("\n");
                ost.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bleName.equals("BJYC-XY-3A")) {
            try {
                //将数据信息存储到本地
                FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/BJYC-XY-3A数据.txt", true);
                OutputStreamWriter ost = new OutputStreamWriter(fos);

                //拆分字符串，16转10进制
                String subStr = DealNum.substring(hexStr, 26, 30);
                int anInt = Integer.parseInt(subStr, 16);
                float MathUnit = anInt / 10F;
                String type = DealNum.substring(hexStr, 5, 6);
                if (type.equals("1")) {
//                    addText(txType, "血糖值：");
                    if (!TextUtils.isEmpty(txMath)) {
//                        nearType.setText("血糖值：");
                    }

                    ost.write("血糖值：");
                } else if (type.equals("2")) {
//                    addText(txType, "血尿酸值：");
                    ost.write("血尿酸值：");
                } else if (type.equals("3")) {
//                    addText(txType, "血酮体值：");
                    ost.write("血酮体值：");
                } else if (type.equals("4")) {
//                    addText(txType, "血乳酸值：");
                    ost.write("血乳酸值：");
                }

                String subNum = DealNum.substring(hexStr, 33, 34);
                if (subNum.equals("1")) {
//                    addText(txt, MathUnit + "mmol/L");
                    if (!TextUtils.isEmpty(txMath)) {
//                        txts.setText(MathUnit + "mmol/L");
                    }

                    ost.write(MathUnit + "mmol/L；");
                } else if (subNum.equals("2")) {
//                    addText(txt, MathUnit + "mg/dl");
                    ost.write(MathUnit + "mg/dl；");
                } else if (subNum.equals("3")) {
//                    addText(txt, MathUnit + "umol/L");
                    ost.write(MathUnit + "umol/L；");
                }

                String year = DealNum.substring(hexStr, 14, 16);
                String month = DealNum.substring(hexStr, 16, 18);
                String day = DealNum.substring(hexStr, 18, 20);
                String hour = DealNum.substring(hexStr, 20, 22);
                String minute = DealNum.substring(hexStr, 22, 24);
                String sec = DealNum.substring(hexStr, 24, 26);
                LogUtils.e("txt---拆分日期", year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + sec + "秒");

//                addText(dataMath, "测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                if (!TextUtils.isEmpty(txMath)) {
//                    dataMaths.setText("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                }
                ost.write("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                ost.write("\n");
                ost.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 日志打印
     *
     * @param textView
     * @param content
     */
    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

