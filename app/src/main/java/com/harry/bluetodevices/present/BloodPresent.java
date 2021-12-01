package com.harry.bluetodevices.present;

import com.google.gson.Gson;
import com.harry.bluetodevices.base.BaseObserver;
import com.harry.bluetodevices.base.BasePresenter;
import com.harry.bluetodevices.bean.JsonData;
import com.harry.bluetodevices.util.LogUtils;
import com.harry.bluetodevices.view.IView.IBloodView;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author Martin-harry
 * @date 2021/11/29
 * @address
 * @Desc BloodPresent
 */
public class BloodPresent extends BasePresenter<IBloodView> {
    private Gson gson = new Gson();
    private HashMap<String, String> hashMap;

    public BloodPresent(IBloodView baseView) {
        super(baseView);
    }

    /**
     * 动态血糖数据
     *
     * @param originalData       原始数据
     * @param batteryLevel       电池电量
     * @param batteryStatus      电池状态
     * @param productInformation 产品信息
     * @param dataSerialNumber   数据序号
     * @param cellValue          电池值
     * @param ocValue            温度值
     * @param runDate            运行时间
     * @param macAddress         mac地址
     * @param macAddressLayout   mac地址数据格式
     * @param currentSystemTime  当前系统时间
     * @param currentRunTime     当前运行时间
     */
    public void getBloodMsg(String originalData, String batteryLevel, String batteryStatus,
                            String productInformation, String dataSerialNumber, String cellValue,
                            String ocValue, String runDate, String macAddress, String macAddressLayout,
                            String currentSystemTime, String currentRunTime) {
        hashMap = new HashMap<>();
        hashMap.put("originalData", originalData);
        hashMap.put("batteryLevel", batteryLevel);
        hashMap.put("batteryStatus", batteryStatus);
        hashMap.put("productInformation", productInformation);
        hashMap.put("dataSerialNumber", dataSerialNumber);
        hashMap.put("cellValue", cellValue);
        hashMap.put("ocValue", ocValue);
        hashMap.put("runDate", runDate);
        hashMap.put("macAddress", macAddress);
        hashMap.put("macAddressLayout", macAddressLayout);
        hashMap.put("currentSystemTime", currentSystemTime);
        hashMap.put("currentRunTime", currentRunTime);

        String json = gson.toJson(hashMap);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
        addSubscription(retrofitService.getSugar(requestBody), new BaseObserver<JsonData>(baseView, true) {
            @Override
            public void onSuccess(JsonData o) {
                baseView.onSuccess(o);
                LogUtils.e("BloodPresent onSuccess", o.getCode() + "");
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
                LogUtils.e("BloodPresent onError", msg + "");
            }

            @Override
            public void onErrorCode(int code, String msg) {
                baseView.onErrorCode(code, msg);
                LogUtils.e("BloodPresent onErrorCode", code + "----" + msg);
            }
        });
    }
}
