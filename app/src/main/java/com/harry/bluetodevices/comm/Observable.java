package com.harry.bluetodevices.comm;

import com.clj.fastble.data.BleDevice;

/**
 * @author Martin-harry
 * @date 2021/7/13
 * @address
 * @Desc Observable
 */
public interface Observable {
    void addObserver(Observer obj);

    void deleteObserver(Observer obj);

    void notifyObserver(BleDevice bleDevice);
}
