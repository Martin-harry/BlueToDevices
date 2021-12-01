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
import com.harry.bluetodevices.base.app.BaseActivity;
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
 * @date 2021/7/13
 * @address
 * @Desc DevicesActivity数据处理
 */
public class DevicesActivity extends BaseActivity implements View.OnClickListener {
    public static final String KEY_DATA = "key_data";
    private EditText et_server;
    private EditText et_rx;
    private EditText et_tx;
    private TextView devName;
    private RelativeLayout rela;
    private TextView notifyType = null;
    private ImageView back;
    //设备
    private BleDevice bleDevice;
    private String serverUuid;
    private String Rid;
    private String Tid;
    //通知返回数据
    private byte[] bytes;
    private TextView math;
    private TextView txType;
    private TextView txt;
    private TextView oil;
    private TextView oil_txt;
    private TextView high;
    private TextView high_txt;
    private TextView dataMath;
    private Button wrtMath;
    private TextView nearTxt;
    private RelativeLayout nearRet;
    private TextView nearOil;
    private TextView nearType;
    private TextView txts;
    private TextView dataMaths;
    private String txMath;
    private RelativeLayout nearXz;
    private TextView nearOils;
    private TextView nearTypes;
    private TextView txt_oil;
    private TextView dataMaths_oil;
    private TextView nearXoi;
    private TextView nearHigh;

    @Override
    protected void initView() {
        setStatusBg(4);
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);

        et_server = findViewById(R.id.et_server);
        et_rx = findViewById(R.id.et_rx);
        et_tx = findViewById(R.id.et_tx);
        devName = findViewById(R.id.devName);
        rela = findViewById(R.id.rela);
        notifyType = findViewById(R.id.notifyType);
        math = findViewById(R.id.math);
        txType = findViewById(R.id.txType);
        txt = findViewById(R.id.txt);
        oil = findViewById(R.id.oil);
        oil_txt = findViewById(R.id.oil_txt);
        high = findViewById(R.id.high);
        high_txt = findViewById(R.id.high_txt);
        dataMath = findViewById(R.id.dataMath);
        wrtMath = findViewById(R.id.wrtMath);

        //读取最近一条数据
        nearTxt = findViewById(R.id.nearTxt);
        nearRet = findViewById(R.id.nearRet);
        nearOil = findViewById(R.id.nearOil);
        nearType = findViewById(R.id.nearType);
        txts = findViewById(R.id.txts);
        dataMaths = findViewById(R.id.dataMaths);
        nearXz = findViewById(R.id.nearXz);
        nearOils = findViewById(R.id.nearOils);
        nearTypes = findViewById(R.id.nearTypes);
        txt_oil = findViewById(R.id.txt_oil);
        dataMaths_oil = findViewById(R.id.dataMaths_oil);
        nearXoi = findViewById(R.id.nearXoi);
        nearHigh = findViewById(R.id.nearHigh);
        back = findViewById(R.id.back);
        wrtMath.setOnClickListener(this);
        back.setOnClickListener(this);

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
        if (name.equals("BJYC")) {//标准
            wrtMath.setVisibility(View.GONE);
            et_server.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String server = et_server.getText().toString().trim();
                    String s1 = DealNum.substring(BleService.serviceUuid_BJYC_DF, 4, 8);
                    LogUtils.e("服务以及特征值：---", s1);
                    if (server != null && !isEmpty(server)) {
                        if (s1.equalsIgnoreCase(server)) {//1808(BJYC)
                            serverUuid = BleService.serviceUuid_BJYC_DF;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---标准（服务）", serverUuid + "---");
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
                    String DFS = DealNum.substring(BleService.RUuid_BJYC__DF, 4, 8);
                    String DFSL = DealNum.substring(BleService.RUuid_BJYC__DFL, 4, 8);
                    LogUtils.e("服务以及特征值：---", DFS);
                    if (rid != null && !isEmpty(rid)) {
                        if (DFS.equalsIgnoreCase(rid)) {//2a18(BJYC)
                            Rid = BleService.RUuid_BJYC__DF;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---标准（读）", Rid);
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
        } else {//非标准
            wrtMath.setVisibility(View.VISIBLE);
            et_server.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String server = et_server.getText().toString().trim();
                    String s1 = DealNum.substring(BleService.serviceUuid_XY_3A, 4, 8);
                    String s2 = DealNum.substring(BleService.serviceUuid_5DU, 4, 8);
                    String s3 = DealNum.substring(BleService.serviceUuid_5DT, 4, 8);
                    String s4 = DealNum.substring(BleService.serviceUuid_T3, 4, 8);
                    String s5 = DealNum.substring(BleService.serviceUuid_TCL, 4, 8);
                    String s6 = DealNum.substring(BleService.serviceUuid_XY_COST, 4, 8);
                    LogUtils.e("服务以及特征值：---", s1 + "---" + s2 + "---" + s3 + "---" + s4 + "---" + s5 + "---" + s6);
                    if (server != null && !isEmpty(server)) {
                        if (s1.equalsIgnoreCase(server)) {//ffe0(XY-3A)
                            serverUuid = BleService.serviceUuid_XY_3A;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        } else if (s2.equalsIgnoreCase(server)) {//cdd0(5DU2)
                            serverUuid = BleService.serviceUuid_5DU;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        } else if (s3.equalsIgnoreCase(server)) {//cdd0(5TD-4B)
                            serverUuid = BleService.serviceUuid_5DT;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        } else if (s4.equalsIgnoreCase(server)) {//ff00(T3)
                            serverUuid = BleService.serviceUuid_T3;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        } else if (s5.equalsIgnoreCase(server)) {//ff00(TCL-1)
                            serverUuid = BleService.serviceUuid_TCL;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（服务）", serverUuid + "---");
                        } else if (s6.equalsIgnoreCase(server)) {//ff00(XY-3A 低成本)
                            serverUuid = BleService.serviceUuid_XY_COST;
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
                    String RXY = DealNum.substring(BleService.RUuid_XY_3A, 4, 8);
                    String RDU = DealNum.substring(BleService.RUuid_RDU, 4, 8);
                    String RDT = DealNum.substring(BleService.RUuid_RDT, 4, 8);
                    String RT3 = DealNum.substring(BleService.RUuid_RT3, 4, 8);
                    String RTC = DealNum.substring(BleService.RUuid_RTC, 4, 8);
                    String RCOST = DealNum.substring(BleService.RUuid_XY_COST, 4, 8);
                    LogUtils.e("服务以及特征值：---", RXY + "---" + RDU + "---" + RDT + "---" + RT3 + "---" + RTC + "---" + RCOST);
                    if (rid != null && !isEmpty(rid)) {
                        if (RXY.equalsIgnoreCase(rid)) {//ffe1(XY-3A)
                            Rid = BleService.RUuid_XY_3A;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（读）", Rid);
                        } else if (RDU.equalsIgnoreCase(rid)) {//cdd1(5DU2)
                            Rid = BleService.RUuid_RDU;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（读）", Rid);
                        } else if (RDT.equalsIgnoreCase(rid)) {//cdd1(5TD-4B)
                            Rid = BleService.RUuid_RDT;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（读）", Rid);
                        } else if (RT3.equalsIgnoreCase(rid)) {//ff01(T3)
                            Rid = BleService.RUuid_RT3;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（读）", Rid);
                        } else if (RTC.equalsIgnoreCase(rid)) {//ff01(TCL-1)
                            Rid = BleService.RUuid_RTC;
                            if (bleDevice != null) {
                                opeNotify(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（读）", Rid);
                        } else if (RCOST.equalsIgnoreCase(rid)) {//ff01(XY-3A 低成本)
                            Rid = BleService.RUuid_XY_COST;
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

            /*et_tx.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String txText = et_tx.getText().toString().trim();
                    String TXY = DealNum.substring(BleService.TUuid_XY_3A, 4, 8);
                    String TDU = DealNum.substring(BleService.TUuid_TDU, 4, 8);
                    String T4B = DealNum.substring(BleService.TUuid_T4B, 4, 8);
                    String TF3 = DealNum.substring(BleService.TUuid_TF3, 4, 8);
                    String TC1 = DealNum.substring(BleService.TUuid_TC1, 4, 8);
                    LogUtils.e("服务以及特征值：---", TXY + "---" + TDU + "---" + T4B + "---" + TF3 + "---" + TC1);
                    if (txText != null && !isEmpty(txText)) {
                        if (TXY.equalsIgnoreCase(txText)) {//ffe1(XY-3A)
                            Tid = BleService.TUuid_XY_3A;
                            if (bleDevice != null) {
                                sendWrite(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                        } else if (TDU.equalsIgnoreCase(txText)) {//cdd2(5DU2)
                            Tid = BleService.TUuid_TDU;
                            if (bleDevice != null) {
                                sendWrite(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                        } else if (T4B.equalsIgnoreCase(txText)) {//cdd2(5TD-4B)
                            Tid = BleService.TUuid_T4B;
                            if (bleDevice != null) {
                                sendWrite(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                        } else if (TF3.equalsIgnoreCase(txText)) {//ff01(T3)
                            Tid = BleService.TUuid_TF3;
                            if (bleDevice != null) {
                                sendWrite(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                        } else if (TC1.equalsIgnoreCase(txText)) {//ff01(TCL-1)
                            Tid = BleService.TUuid_TC1;
                            if (bleDevice != null) {
                                sendWrite(bleDevice);
                            }
                            LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });*/
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
        return R.layout.activity_devices;
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
                    String TXY = DealNum.substring(BleService.TUuid_XY_3A, 4, 8);
                    String TDU = DealNum.substring(BleService.TUuid_TDU, 4, 8);
                    String T4B = DealNum.substring(BleService.TUuid_T4B, 4, 8);
                    String TF3 = DealNum.substring(BleService.TUuid_TF3, 4, 8);
                    String TC1 = DealNum.substring(BleService.TUuid_TC1, 4, 8);
                    String TCOST = DealNum.substring(BleService.TUuid_XY_COST, 4, 8);
                    LogUtils.e("服务以及特征值：---", TXY + "---" + TDU + "---" + T4B + "---" + TF3 + "---" + TC1 + "---" + TCOST);
                    if (TXY.equalsIgnoreCase(txMath)) {//ffe1(XY-3A)
                        Tid = BleService.TUuid_XY_3A;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    } else if (TDU.equalsIgnoreCase(txMath)) {//cdd2(5DU2)
                        Tid = BleService.TUuid_TDU;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    } else if (T4B.equalsIgnoreCase(txMath)) {//cdd2(5TD-4B)
                        Tid = BleService.TUuid_T4B;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    } else if (TF3.equalsIgnoreCase(txMath)) {//ff01(T3)
                        Tid = BleService.TUuid_TF3;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    } else if (TC1.equalsIgnoreCase(txMath)) {//ff01(TCL-1)
                        Tid = BleService.TUuid_TC1;
                        if (bleDevice != null) {
                            sendWrite(bleDevice);
                        }
                        LogUtils.e("服务以及特征值：---非标准（写）", Tid);
                    } else if (TCOST.equalsIgnoreCase(txMath)) {//ff01(XY-3A 低成本)
                        Tid = BleService.TUuid_XY_COST;
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
        if (sendName.equals("BJYC-XY-3A") || sendName.equals("BJYC-5DU2")
                || sendName.equals("BJYC-5DT-4B") || sendName.equals("BJYC-T3")) {
            nearTxt.setVisibility(View.VISIBLE);
            nearRet.setVisibility(View.VISIBLE);
            nearTxt.setText(R.string.nearMath);

            bytes[0] = (byte) 0xc5;
            bytes[1] = (byte) 0x05;
            bytes[2] = (byte) 0xc4;
        } else if (sendName.equals("BJYC-TCL-1")) {
            nearTxt.setVisibility(View.VISIBLE);
            nearXz.setVisibility(View.VISIBLE);
            nearTxt.setText(R.string.nearOil);

            bytes[0] = (byte) 0x75;
            bytes[1] = (byte) 0x02;
            bytes[2] = (byte) 0x77;
        }

        BleManager.getInstance().write(
                bleDevice,
                serverUuid,
                Tid,
                bytes,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                        Toast.makeText(DevicesActivity.this, R.string.writSuccess, Toast.LENGTH_LONG).show();
                        LogUtils.e("txt---writeSuccess", "数据发送成功");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        // 发送数据到设备失败
                        Toast.makeText(DevicesActivity.this, R.string.writFaile, Toast.LENGTH_LONG).show();
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
                            addText(txt, "数据信息为空！");
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
        addText(math, hexStr);
        if (!TextUtils.isEmpty(txMath)) {
            nearOil.setText(hexStr);
            nearOils.setText(hexStr);
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
                String subMat = DealNum.substring(hexStr, 20, 22);
                String subMaH = DealNum.substring(hexStr, 23, 24);
                String subStr = new StringBuilder().append(subMaH).append(subMat).toString();
                Log.e("BJYC数据值 notifyData >>>>", subStr);

                int anInt = Integer.parseInt(subStr, 16);
                float MathUnit = anInt / 10F;
                addText(txType, "血糖值：");
                addText(txt, MathUnit + "mmol/L");
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

                addText(dataMath, "测量时间：" + year + "-" + months + "-" + days + " " + hours + ":" + minutes + ":" + secs);
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
                    addText(txType, "血糖值：");
                    if (!TextUtils.isEmpty(txMath)) {
                        nearType.setText("血糖值：");
                    }

                    ost.write("血糖值：");
                } else if (type.equals("2")) {
                    addText(txType, "血尿酸值：");
                    ost.write("血尿酸值：");
                } else if (type.equals("3")) {
                    addText(txType, "血酮体值：");
                    ost.write("血酮体值：");
                } else if (type.equals("4")) {
                    addText(txType, "血乳酸值：");
                    ost.write("血乳酸值：");
                }

                String subNum = DealNum.substring(hexStr, 33, 34);
                if (subNum.equals("1")) {
                    addText(txt, MathUnit + "mmol/L");
                    if (!TextUtils.isEmpty(txMath)) {
                        txts.setText(MathUnit + "mmol/L");
                    }

                    ost.write(MathUnit + "mmol/L；");
                } else if (subNum.equals("2")) {
                    addText(txt, MathUnit + "mg/dl");
                    ost.write(MathUnit + "mg/dl；");
                } else if (subNum.equals("3")) {
                    addText(txt, MathUnit + "umol/L");
                    ost.write(MathUnit + "umol/L；");
                }

                String year = DealNum.substring(hexStr, 14, 16);
                String month = DealNum.substring(hexStr, 16, 18);
                String day = DealNum.substring(hexStr, 18, 20);
                String hour = DealNum.substring(hexStr, 20, 22);
                String minute = DealNum.substring(hexStr, 22, 24);
                String sec = DealNum.substring(hexStr, 24, 26);
                LogUtils.e("txt---拆分日期", year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + sec + "秒");

                addText(dataMath, "测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                if (!TextUtils.isEmpty(txMath)) {
                    dataMaths.setText("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                }
                ost.write("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                ost.write("\n");
                ost.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bleName.equals("BJYC-T3")) {
            try {
                //将数据信息存储到本地
                FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/BJYC-T3数据.txt", true);
                OutputStreamWriter ost = new OutputStreamWriter(fos);

                //拆分字符串，16转10进制
                String subStr = DealNum.substring(hexStr, 26, 30);
                int anInt = Integer.parseInt(subStr, 16);
                float MathUnit = anInt / 10F;
                String type = DealNum.substring(hexStr, 5, 6);
                if (type.equals("1")) {
                    addText(txType, "血糖值：");
                    if (!TextUtils.isEmpty(txMath)) {
                        nearType.setText("血糖值：");
                    }

                    ost.write("血糖值：");
                } else if (type.equals("2")) {
                    addText(txType, "血尿酸值：");
                    ost.write("血尿酸值：");
                } else if (type.equals("3")) {
                    addText(txType, "血酮体值：");
                    ost.write("血酮体值：");
                } else if (type.equals("4")) {
                    addText(txType, "血乳酸值：");
                    ost.write("血乳酸值：");
                }

                String subNum = DealNum.substring(hexStr, 33, 34);
                if (subNum.equals("1")) {
                    addText(txt, MathUnit + "mmol/L");
                    if (!TextUtils.isEmpty(txMath)) {
                        txts.setText(MathUnit + "mmol/L");
                    }

                    ost.write(MathUnit + "mmol/L；");
                } else if (subNum.equals("2")) {
                    addText(txt, MathUnit + "mg/dl");
                    ost.write(MathUnit + "mg/dl；");
                } else if (subNum.equals("3")) {
                    addText(txt, MathUnit + "umol/L");
                    ost.write(MathUnit + "umol/L；");
                }

                String year = DealNum.substring(hexStr, 14, 16);
                String month = DealNum.substring(hexStr, 16, 18);
                String day = DealNum.substring(hexStr, 18, 20);
                String hour = DealNum.substring(hexStr, 20, 22);
                String minute = DealNum.substring(hexStr, 22, 24);
                String sec = DealNum.substring(hexStr, 24, 26);
                LogUtils.e("txt---拆分日期", year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + sec + "秒");

                addText(dataMath, "测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                if (!TextUtils.isEmpty(txMath)) {
                    dataMaths.setText("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                }
                ost.write("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                ost.write("\n");
                ost.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bleName.equals("BJYC-5DT-4B")) {
            try {
                //将数据信息存储到本地
                FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/BJYC-5DT-4B数据.txt", true);
                OutputStreamWriter ost = new OutputStreamWriter(fos);

                //拆分字符串，16转10进制
                String subStr = DealNum.substring(hexStr, 26, 30);
                int anInt = Integer.parseInt(subStr, 16);
                float MathUnit = anInt / 10F;
                String type = DealNum.substring(hexStr, 5, 6);
                if (type.equals("1")) {
                    addText(txType, "血糖值：");
                    if (!TextUtils.isEmpty(txMath)) {
                        nearType.setText("血糖值：");
                    }

                    ost.write("血糖值：");
                } else if (type.equals("2")) {
                    addText(txType, "血尿酸值：");
                    ost.write("血尿酸值：");
                } else if (type.equals("3")) {
                    addText(txType, "血酮体值：");
                    ost.write("血酮体值：");
                } else if (type.equals("4")) {
                    addText(txType, "血乳酸值：");
                    ost.write("血乳酸值：");
                }

                String subNum = DealNum.substring(hexStr, 33, 34);
                if (subNum.equals("1")) {
                    addText(txt, MathUnit + "mmol/L");
                    if (!TextUtils.isEmpty(txMath)) {
                        txts.setText(MathUnit + "mmol/L");
                    }

                    ost.write(MathUnit + "mmol/L；");
                } else if (subNum.equals("2")) {
                    addText(txt, MathUnit + "mg/dl");
                    ost.write(MathUnit + "mg/dl；");
                } else if (subNum.equals("3")) {
                    addText(txt, MathUnit + "umol/L");
                    ost.write(MathUnit + "umol/L；");
                }

                String year = DealNum.substring(hexStr, 14, 16);
                String month = DealNum.substring(hexStr, 16, 18);
                String day = DealNum.substring(hexStr, 18, 20);
                String hour = DealNum.substring(hexStr, 20, 22);
                String minute = DealNum.substring(hexStr, 22, 24);
                String sec = DealNum.substring(hexStr, 24, 26);
                LogUtils.e("txt---拆分日期", year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + sec + "秒");

                addText(dataMath, "测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                if (!TextUtils.isEmpty(txMath)) {
                    dataMaths.setText("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                }
                ost.write("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                ost.write("\n");
                ost.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bleName.equals("BJYC-5DU2")) {
            try {
                //将数据信息存储到本地
                FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/BJYC-5DU2数据.txt", true);
                OutputStreamWriter ost = new OutputStreamWriter(fos);

                //拆分字符串，16转10进制
                String subStr = DealNum.substring(hexStr, 26, 30);
                int anInt = Integer.parseInt(subStr, 16);
                float MathUnit = anInt / 10F;
                String type = DealNum.substring(hexStr, 5, 6);
                if (type.equals("1")) {
                    addText(txType, "血糖值：");
                    if (!TextUtils.isEmpty(txMath)) {
                        nearType.setText("血糖值：");
                    }

                    ost.write("血糖值：");
                } else if (type.equals("2")) {
                    addText(txType, "血尿酸值：");
                    ost.write("血尿酸值：");
                } else if (type.equals("3")) {
                    addText(txType, "血酮体值：");
                    ost.write("血酮体值：");
                } else if (type.equals("4")) {
                    addText(txType, "血乳酸值：");
                    ost.write("血乳酸值：");
                }

                String subNum = DealNum.substring(hexStr, 33, 34);
                if (subNum.equals("1")) {
                    addText(txt, MathUnit + "mmol/L");
                    if (!TextUtils.isEmpty(txMath)) {
                        txts.setText(MathUnit + "mmol/L");
                    }

                    ost.write(MathUnit + "mmol/L；");
                } else if (subNum.equals("2")) {
                    addText(txt, MathUnit + "mg/dl");
                    ost.write(MathUnit + "mg/dl；");
                } else if (subNum.equals("3")) {
                    addText(txt, MathUnit + "umol/L");
                    ost.write(MathUnit + "umol/L；");
                }

                String year = DealNum.substring(hexStr, 14, 16);
                String month = DealNum.substring(hexStr, 16, 18);
                String day = DealNum.substring(hexStr, 18, 20);
                String hour = DealNum.substring(hexStr, 20, 22);
                String minute = DealNum.substring(hexStr, 22, 24);
                String sec = DealNum.substring(hexStr, 24, 26);
                LogUtils.e("txt---拆分日期", year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + sec + "秒");

                addText(dataMath, "测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                if (!TextUtils.isEmpty(txMath)) {
                    dataMaths.setText("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                }
                ost.write("测量时间：" + "20" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec);
                ost.write("\n");
                ost.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bleName.equals("BJYC-TCL-1")) {
            try {
                //将数据信息存储到本地
                FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/BJYC-TCL-1数据.txt", true);
                OutputStreamWriter ost = new OutputStreamWriter(fos);

                rela.setVisibility(View.VISIBLE);
                String a = DealNum.substring(hexStr, 16, 18);
                String b = DealNum.substring(hexStr, 18, 20);
                String c = new StringBuilder().append(a).append(b).toString();
                int total = Integer.parseInt(c, 16);//总胆固醇
                float total_txt = total / 100F;
                double total_oil = total_txt * 38.66;

                String e = DealNum.substring(hexStr, 20, 22);
                String f = DealNum.substring(hexStr, 22, 24);
                String g = new StringBuilder().append(e).append(f).toString();
                int fat = Integer.parseInt(g, 16);//脂肪甘油
                float fat_txt = fat / 100F;
                double fat_oil = fat_txt * 88.6;

                String h = DealNum.substring(hexStr, 24, 26);
                String i = DealNum.substring(hexStr, 26, 28);
                String j = new StringBuilder().append(h).append(i).toString();
                int dens = Integer.parseInt(j, 16);//高密度脂蛋白
                float dens_txt = dens / 100F;
                double dens_oil = dens_txt * 38.66;

                addText(txType, "总胆固醇：");
                addText(oil, "脂肪甘油：");
                addText(high, "高密度脂蛋白：");
                if (!TextUtils.isEmpty(txMath)) {
                    nearTypes.setText("总胆固醇：");
                }
                String uniType = DealNum.substring(hexStr, 2, 3);
                if (uniType.equals("1")) {//mg/dl
                    addText(txt, total_oil + "mg/dl");
                    addText(oil_txt, fat_oil + "mg/dl");
                    addText(high_txt, dens_oil + "mg/dl");
                    if (!TextUtils.isEmpty(txMath)) {
                        txt_oil.setText(total_oil + "mg/dl");
                        nearXoi.setText("脂肪甘油：" + fat_oil + "mg/dl");
                        nearHigh.setText("高密度脂蛋白：" + dens_oil + "mg/dl");
                    }

                    ost.write("总胆固醇：" + total + "mg/dl；" + "脂肪甘油：" + fat + "mg/dl；" + "高密度脂蛋白：" + dens + "mg/dl；");
                } else if (uniType.equals("0")) {//mmol/L
                    addText(txt, total_txt + "mmol/L");
                    addText(oil_txt, fat_txt + "mmol/L");
                    addText(high_txt, dens_txt + "mmol/L");
                    if (!TextUtils.isEmpty(txMath)) {
                        txt_oil.setText(total_txt + "mmol/L");
                        nearXoi.setText("脂肪甘油：" + fat_txt + "mmol/L");
                        nearHigh.setText("高密度脂蛋白：" + dens_txt + "mmol/L");
                    }

                    ost.write("总胆固醇：" + total_txt + "mmol/L；" + "脂肪甘油：" + fat_txt + "mmol/L；" + "高密度脂蛋白：" + dens_txt + "mmol/L；");
                }

                String month = DealNum.substring(hexStr, 8, 10);
                int months = Integer.parseInt(month, 16);
                String day = DealNum.substring(hexStr, 10, 12);
                int days = Integer.parseInt(day, 16);
                String hour = DealNum.substring(hexStr, 12, 14);
                int hours = Integer.parseInt(hour, 16);
                String minute = DealNum.substring(hexStr, 14, 16);
                int minutes = Integer.parseInt(minute, 16);
                LogUtils.e("txt---拆分日期", months + "月" + days + "日" + hours + "时" + minutes + "分");

                addText(dataMath, "测量时间：" + months + "月" + days + "日" + " " + hours + "时" + minutes + "分");
                if (!TextUtils.isEmpty(txMath)) {
                    dataMaths_oil.setText("测量时间：" + months + "月" + days + "日" + " " + hours + "时" + minutes + "分");
                }
                ost.write("测量时间：" + months + "月" + days + "日" + " " + hours + "时" + minutes + "分");
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
