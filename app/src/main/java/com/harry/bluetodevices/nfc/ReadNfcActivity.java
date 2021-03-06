package com.harry.bluetodevices.nfc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.harry.bluetodevices.R;
import com.harry.bluetodevices.base.app.BaseNfcActivity;
import com.harry.bluetodevices.bean.BloodDate;
import com.harry.bluetodevices.nfc.read.NdefMessageParser;
import com.harry.bluetodevices.nfc.read.ParsedNdefRecord;
import com.harry.bluetodevices.present.BloodPresent;
import com.harry.bluetodevices.util.LogUtils;
import com.harry.bluetodevices.util.NfcUtil;
import com.harry.bluetodevices.util.TimeFormatUtils;
import com.harry.bluetodevices.view.IView.IBloodView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * @author Martin-harry
 * @date 2021/11/11
 * @address
 * @Desc NFC读取
 */
public class ReadNfcActivity extends BaseNfcActivity implements IBloodView, View.OnClickListener {

    private BloodPresent bloodPresent;
    private TextView mNfcText;
    private ImageView back;
    private LinearLayout linTxt;
    private ImageView readPic;
    private TextView systemTxt;
    private TextView batteryNum;
    private TextView levelType;
    private TextView mathNumber;
    private TextView currenTxt;
    private TextView tempTxt;
    private TextView time;
    private TextView systemTime;
    private Button bt;
    private Button upLoad;
    private Gson gson = new Gson();
    private LinearLayout linBt;

    @Override
    protected void initView() {
        setStatusBg(4);
        bloodPresent = new BloodPresent(this);
        back = findViewById(R.id.back);
        readPic = findViewById(R.id.readPic);
        linTxt = findViewById(R.id.linTxt);
        mNfcText = findViewById(R.id.nfcTxt);
        systemTxt = findViewById(R.id.systemTxt);
        batteryNum = findViewById(R.id.batteryNum);
        levelType = findViewById(R.id.levelType);
        mathNumber = findViewById(R.id.mathNumber);
        currenTxt = findViewById(R.id.currenTxt);
        tempTxt = findViewById(R.id.tempTxt);
        time = findViewById(R.id.time);
        systemTime = findViewById(R.id.systemTime);
        linBt = findViewById(R.id.linBt);
        bt = findViewById(R.id.bt);
        upLoad = findViewById(R.id.upLoad);
        back.setOnClickListener(this);
        bt.setOnClickListener(this);
        upLoad.setOnClickListener(this);

        resolveIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (mNfcAdapter != null) {
            if (mNfcAdapter.isEnabled()) {
                resolveIntent(getIntent());
            }
        }
    }

    /**
     * 初次判断卡片的类型
     *
     * @param intent
     */
    private void resolveIntent(Intent intent) {
        NdefMessage[] msgs = NfcUtil.getNdefMsg(intent); //解析nfc标签中的数据

        if (msgs == null) {
            Toast.makeText(ReadNfcActivity.this, "非NFC启动", Toast.LENGTH_SHORT).show();
        } else {
            setNFCMsgView(msgs);
        }
    }

    /**
     * 显示扫描后的信息
     *
     * @param ndefMessages ndef数据
     */
    @SuppressLint("SetTextI18n")
    private void setNFCMsgView(NdefMessage[] ndefMessages) {
        if (ndefMessages == null || ndefMessages.length == 0) {
            return;
        }

//        mNfcText.setText("Payload:" + new String(ndefMessages[0].getRecords()[0].getPayload()) + "\n");
        readPic.setVisibility(View.GONE);
        linTxt.setVisibility(View.VISIBLE);
        linBt.setVisibility(View.VISIBLE);

        //将数据信息存储到本地
        try {
            FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/NFC读取.txt", true);
            OutputStreamWriter ost = new OutputStreamWriter(fos);

            List<ParsedNdefRecord> records = NdefMessageParser.parse(ndefMessages[0]);
            final int size = records.size();
            for (int i = 0; i < size; i++) {
                ParsedNdefRecord record = records.get(i);
                mNfcText.setText("Tag ID (hex): " + record.getViewText());

                systemTxt.setText("产品信息：");
                batteryNum.setText("电池电量：");
                levelType.setText("当前电池状态：");
                mathNumber.setText("数据序号：");
                currenTxt.setText("电流值：");//nA
                tempTxt.setText("温度值：");//℃
                time.setText("当前运行时间：");
                String currentTime = TimeFormatUtils.getCurrentTime();
                systemTime.setText("当前系统时间：" + currentTime);
                Log.e("扫描后的数据信息 >>>>", "setNFCMsgView: " + record.getViewText());

                ost.write("Tag ID(hex)：" + record.getViewText());
                ost.write("产品信息：" + "\n");
                ost.write("电池电量：" + "\n");
                ost.write("当前电池状态：" + "\n");
                ost.write("数据序号：" + "\n");
                ost.write("电流值：" + "\n");
                ost.write("温度值：" + "\n");
                ost.write("当前运行时间：" + "\n");
                ost.write("当前系统时间：" + currentTime);
                ost.write("\n");
                ost.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int createViews() {
        return R.layout.activity_read_nfc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bt:
                mNfcText.setText("");
                systemTxt.setText("");
                batteryNum.setText("");
                levelType.setText("");
                mathNumber.setText("");
                currenTxt.setText("");
                tempTxt.setText("");
                time.setText("");
                systemTime.setText("");
                break;
            case R.id.upLoad:
                bloodPresent.getBloodMsg("123456789", "100%", "已充满", "动态血糖上位机",
                        "BJYC-CM-01", "100%", "36.2℃", "2021-11-30 09:37:10",
                        "192.168.12.2", "FF:00:55:88",
                        "2021-11-30 15:41:10", "2021-11-30 09:37:10");
                break;
        }
    }

    @Override
    public void onSuccess(Object data) {
        if (data != null) {
            LogUtils.e("血糖数据上传 Success>>>>", data.toString());
            String json = gson.toJson(data);
            BloodDate bloodDate = gson.fromJson(json, BloodDate.class);
            if (bloodDate.getCode() == 200) {
                Toast.makeText(this, "数据上传成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "数据上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onError(String code, String msg) {
        LogUtils.e("血糖数据上传 Error>>>>", code + "---" + msg);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showLoadingFileDialog() {

    }

    @Override
    public void hideLoadingFileDialog() {

    }

    @Override
    public void onProgress(long totalSize, long downSize) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void onErrorCode(int code, String msg) {

    }
}
