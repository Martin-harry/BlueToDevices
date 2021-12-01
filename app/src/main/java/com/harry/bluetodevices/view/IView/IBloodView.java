package com.harry.bluetodevices.view.IView;

import com.harry.bluetodevices.base.BaseView;

/**
 * @author Martin-harry
 * @date 2021/11/30
 * @address
 * @Desc IBloodView
 */
public interface IBloodView<T> extends BaseView {
    void onSuccess(T data);
    void onError(String code, String msg);
}
