package com.harry.bluetodevices.bean;

/**
 * @author Martin-harry
 * @date 2021/7/13
 * @address
 * @Desc 服务、特征标识
 */
public class BleService {
    //标准蓝牙协议
    //BJYC、BJYC-5DM-9C、BJYC-5DM-9、BJYC-5DM-9A
    public static final String serviceUuid_BJYC_DF = "00001808-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_BJYC__DF = "00002a18-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_BJYC__DFL = "00002A18-0000-1000-8000-00805f9b34fb";

    //非标准蓝牙协议
    //BJYC-XY-3A
    public static final String serviceUuid_XY_3A = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_XY_3A = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static final String TUuid_XY_3A = "0000ffe1-0000-1000-8000-00805f9b34fb";
    //低成本
    public static final String serviceUuid_XY_COST = "0000ff00-0000-1000-8000-00805f9bb34fb";
    public static final String RUuid_XY_COST = "0000ff01-0000-1000-8000-00805f9bb34f";
    public static final String TUuid_XY_COST = "0000ff01-0000-1000-8000-00805f9bb34f";
    //BJYC-5DU2
    public static final String serviceUuid_5DU = "0003cdd0-0000-1000-8000-00805f9b0131";
    public static final String RUuid_RDU = "0003cdd1-0000-1000-8000-00805f9b0131";
    public static final String TUuid_TDU = "0003cdd2-0000-1000-8000-00805f9b0131";
    //BJYC-5DT-4B
    public static final String serviceUuid_5DT = "0003cdd0-0000-1000-8000-00805f9b0131";
    public static final String RUuid_RDT = "0003cdd1-0000-1000-8000-00805f9b0131";
    public static final String TUuid_T4B = "0003cdd2-0000-1000-8000-00805f9b0131";
    //BJYC-T3
    public static final String serviceUuid_T3 = "0000ff00-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_RT3 = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String TUuid_TF3 = "0000ff01-0000-1000-8000-00805f9b34fb";
    //BJYC-TCL-1
    public static final String serviceUuid_TCL = "0000ff00-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_RTC = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String TUuid_TC1 = "0000ff01-0000-1000-8000-00805f9b34fb";
    //BJYC-CGM-01
    //电池服务（标准服务）
    public static final String serviceUuid_BATTERY = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_BATTERY = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String TUuid_BATTERY = "0000ff01-0000-1000-8000-00805f9b34fb";
    //血糖服务
    public static final String serviceUuid_BLOOD_SUGAR = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_BLOOD_SUGAR = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String TUuid_BLOOD_SUGAR = "0000ff01-0000-1000-8000-00805f9b34fb";
    //系统信息服务
    public static final String serviceUuid_SYSTEM_MSG = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_SYSTEM_MSG = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String TUuid_SYSTEM_MSG = "0000ff01-0000-1000-8000-00805f9b34fb";
    //时间服务（定制服务）
    public static final String serviceUuid_TIME = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String RUuid_TIME = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String TUuid_TIME = "0000ff01-0000-1000-8000-00805f9b34fb";
}
