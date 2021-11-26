package com.harry.bluetodevices.base;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.harry.bluetodevices.R;
import com.harry.bluetodevices.util.StatusBarUtils;

/**
 * @author Martin-harry
 * @date 2021/11/11
 * @address
 * @Desc 子类在onNewIntent方法中进行NFC标签相关操作。
 * 在onNewIntent方法中执行intent传递过来的Tag数据
 */
public abstract class BaseNfcActivity extends AppCompatActivity {
    protected NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createViews());//初始化视图
        initView();//初始化控件
        initData();//初始化数据
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract int createViews();

    /**
     * onCreat->onStart->onResume->onPause->onStop->onDestroy
     * 启动Activity，界面可见时.
     */
    @Override
    protected void onStart() {
        super.onStart();
        //此处adapter需要重新获取，否则无法获取message
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //一旦截获NFC消息，就会通过PendingIntent调用窗口
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    /**
     * 获得焦点，按钮可以点击
     */
    @Override
    public void onResume() {
        super.onResume();
        //设置处理优于所有其他NFC的处理
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    /**
     * 暂停Activity，界面获取焦点，按钮可以点击
     */
    @Override
    public void onPause() {
        super.onPause();
        //恢复默认状态
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    //设置状态栏颜色
    public void setStatusBg(int i) {
        if (i == 1) {
            StatusBarUtils.setWindowStatusBarColor(this, R.color.SplGreen);//设置状态栏颜色为绿色
        } else if (i == 2) {
            StatusBarUtils.setWindowStatusBarColorText(this, R.color.white);//设置状态栏颜色为白色
        } else if (i == 3) {
            StatusBarUtils.setWindowStatusBarColor(this, R.color.startCor);//设置状态栏颜色为橘橙色
        } else if (i == 4) {
            StatusBarUtils.setWindowStatusBarColor(this, R.color.black);//设置状态栏颜色为黑色
        }
    }
}