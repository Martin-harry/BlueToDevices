package com.harry.bluetodevices.application;

import android.app.Application;

import com.clj.fastble.BleManager;

/**
 * @author Martin-harry
 * @date 2021/7/13
 * @address
 * @Desc BlueTooth全局配置
 */
public class BlueTooth extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        //初始化及配置
        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);

        /*BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(null)
                .setDeviceName(false, null)   // 只扫描指定广播名的设备，可选g
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);*/
    }
}
