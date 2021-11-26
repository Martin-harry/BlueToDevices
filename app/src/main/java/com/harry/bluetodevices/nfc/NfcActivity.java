package com.harry.bluetodevices.nfc;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.harry.bluetodevices.R;
import com.harry.bluetodevices.base.BaseActivity;

/**
 * @author Martin-harry
 * @date 2021/11/11
 * @address
 * @Desc NFC功能
 */
public class NfcActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout read;
    private RelativeLayout write;

    @Override
    protected void initView() {
        setStatusBg(4);
        read = findViewById(R.id.read);
        write = findViewById(R.id.write);
        read.setOnClickListener(this);
        write.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int createViews() {
        return R.layout.activity_nfc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read:
//                startActivity(new Intent(this, ReadNfcActivity.class));

                break;
            case R.id.write:
                startActivity(new Intent(this, WriteNfcActivity.class));

                break;
        }
    }
}
