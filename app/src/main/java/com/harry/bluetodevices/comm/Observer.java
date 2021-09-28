package com.harry.bluetodevices.comm;

import com.clj.fastble.data.BleDevice;

/**
 * @author Martin-harry
 * @date 2021/7/13
 * @address
 * @Desc Observer
 */
public interface Observer {
    void disConnected(BleDevice bleDevice);
}
