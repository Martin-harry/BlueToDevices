package com.harry.bluetodevices.view;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.harry.bluetodevices.R;
import com.harry.bluetodevices.base.BaseActivity;
import com.harry.bluetodevices.bluetooth.BluetoothActivity;
import com.harry.bluetodevices.nfc.NfcActivity;

/**
 * @author Martin-harry
 * @date 2021/11/11
 * @address
 * @Desc WelcomeActivity
 */
public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView bluetooth;
    private ImageView nfc;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void initView() {
        setStatusBg(2);
        bluetooth = findViewById(R.id.bluetooth);
        nfc = findViewById(R.id.nfc);
        bluetooth.setOnClickListener(this);
        nfc.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected int createViews() {
        return R.layout.activity_welcome;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
            case R.id.nfc:
                if (!ifNFCUse()) {
                    Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                    startActivity(setNfc);
                } else {
                    startActivity(new Intent(this, NfcActivity.class));
                }
                break;
        }
    }

    /**
     * 检测工作,判断设备的NFC支持情况
     *
     * @return
     */
    protected Boolean ifNFCUse() {
        if (mNfcAdapter == null) {
            Toast.makeText(this, "当前设备不支持NFC！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
