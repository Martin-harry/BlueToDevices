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

import com.harry.bluetodevices.R;
import com.harry.bluetodevices.base.BaseNfcActivity;
import com.harry.bluetodevices.nfc.read.NdefMessageParser;
import com.harry.bluetodevices.nfc.read.ParsedNdefRecord;
import com.harry.bluetodevices.util.NfcUtil;
import com.harry.bluetodevices.util.TimeFormatUtils;

import java.util.List;

/**
 * @author Martin-harry
 * @date 2021/11/11
 * @address
 * @Desc NFC读取
 */
public class ReadNfcActivity extends BaseNfcActivity implements View.OnClickListener {

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

    @Override
    protected void initView() {
        setStatusBg(4);
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
        bt = findViewById(R.id.bt);
        back.setOnClickListener(this);
        bt.setOnClickListener(this);

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
        }
    }
}
