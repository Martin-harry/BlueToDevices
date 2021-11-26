package com.harry.bluetodevices.nfc;

import android.app.AlertDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harry.bluetodevices.R;
import com.harry.bluetodevices.base.BaseNfcActivity;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * @author Martin-harry
 * @date 2021/11/11
 * @address
 * @Desc NFC写入
 */
public class WriteNfcActivity extends BaseNfcActivity implements View.OnClickListener {

    private ImageView back;
    private String mTxt;

    @Override
    public void onResume() {
        super.onResume();

        showPopu();
    }

    @Override
    protected void initView() {
        setStatusBg(4);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    private void showPopu() {
        View inflate = LayoutInflater.from(WriteNfcActivity.this).inflate(R.layout.write_math_pop, null);
        Button btn_agree = inflate.findViewById(R.id.btn_agree);
        final EditText wriEdt = inflate.findViewById(R.id.wriEdt);
        final AlertDialog dialog = new AlertDialog
                .Builder(WriteNfcActivity.this)
                .setView(inflate)
                .show();
        // 通过WindowManager获取
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = dm.widthPixels * 4 / 5;
        params.height = dm.heightPixels * 1 / 2;
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxt = wriEdt.getText().toString().trim();
                if (mTxt != null && !TextUtils.isEmpty(mTxt)) {
                    dialog.dismiss();
                    Toast.makeText(WriteNfcActivity.this, mTxt, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WriteNfcActivity.this, "数据写入信息不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (mTxt == null)
            return;
        Log.e("NFC写入 >>>> ", "onNewIntent: " + mTxt);

        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[]{createTextRecord(mTxt)});
        boolean result = writeTag(ndefMessage, detectedTag);
        if (result) {
            Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void initData() {

    }

    /**
     * 创建NDEF文本数据
     *
     * @param text
     * @return
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        //将文本转换为UTF-8格式
        byte[] textBytes = text.getBytes(utfEncoding);
        //设置状态字节编码最高位数为0
        int utfBit = 0;
        //定义状态字节
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置第一个状态字节，先将状态码转换成字节
        data[0] = (byte) status;
        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
        //到textBytes.length的位置
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        //通过字节传入NdefRecord对象
        //NdefRecord.RTD_TEXT：传入类型 读写
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }

    /**
     * 写数据
     *
     * @param ndefMessage 创建好的NDEF文本数据
     * @param tag         标签
     * @return
     */
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    protected int createViews() {
        return R.layout.activity_write_nfc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
