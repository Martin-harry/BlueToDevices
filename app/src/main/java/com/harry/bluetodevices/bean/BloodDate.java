package com.harry.bluetodevices.bean;

/**
 * @author Martin-harry
 * @date 2021/11/30
 * @address
 * @Desc 血糖数据
 */
public class BloodDate {

    /**
     * code : 200
     * msg : 操作成功
     * data : {"originalData":"123456789","batteryLevel":"20%","batteryStatus":"充电中","productInformation":"动态血糖上位机","dataSerialNumber":"BJYC-CM-01","cellValue":"20%","ocValue":"36.2°C","runDate":"2021-11-29 11:12:12","macAddress":"192.168.12.2","macAddressLayout":"FF:00:00:44:00","currentSystemTime":"2021-11-29 11:12:12","currentRunTime":"2021-11-29 11:12:12"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * originalData : 123456789
         * batteryLevel : 20%
         * batteryStatus : 充电中
         * productInformation : 动态血糖上位机
         * dataSerialNumber : BJYC-CM-01
         * cellValue : 20%
         * ocValue : 36.2°C
         * runDate : 2021-11-29 11:12:12
         * macAddress : 192.168.12.2
         * macAddressLayout : FF:00:00:44:00
         * currentSystemTime : 2021-11-29 11:12:12
         * currentRunTime : 2021-11-29 11:12:12
         */

        private String originalData;
        private String batteryLevel;
        private String batteryStatus;
        private String productInformation;
        private String dataSerialNumber;
        private String cellValue;
        private String ocValue;
        private String runDate;
        private String macAddress;
        private String macAddressLayout;
        private String currentSystemTime;
        private String currentRunTime;

        public String getOriginalData() {
            return originalData;
        }

        public void setOriginalData(String originalData) {
            this.originalData = originalData;
        }

        public String getBatteryLevel() {
            return batteryLevel;
        }

        public void setBatteryLevel(String batteryLevel) {
            this.batteryLevel = batteryLevel;
        }

        public String getBatteryStatus() {
            return batteryStatus;
        }

        public void setBatteryStatus(String batteryStatus) {
            this.batteryStatus = batteryStatus;
        }

        public String getProductInformation() {
            return productInformation;
        }

        public void setProductInformation(String productInformation) {
            this.productInformation = productInformation;
        }

        public String getDataSerialNumber() {
            return dataSerialNumber;
        }

        public void setDataSerialNumber(String dataSerialNumber) {
            this.dataSerialNumber = dataSerialNumber;
        }

        public String getCellValue() {
            return cellValue;
        }

        public void setCellValue(String cellValue) {
            this.cellValue = cellValue;
        }

        public String getOcValue() {
            return ocValue;
        }

        public void setOcValue(String ocValue) {
            this.ocValue = ocValue;
        }

        public String getRunDate() {
            return runDate;
        }

        public void setRunDate(String runDate) {
            this.runDate = runDate;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public String getMacAddressLayout() {
            return macAddressLayout;
        }

        public void setMacAddressLayout(String macAddressLayout) {
            this.macAddressLayout = macAddressLayout;
        }

        public String getCurrentSystemTime() {
            return currentSystemTime;
        }

        public void setCurrentSystemTime(String currentSystemTime) {
            this.currentSystemTime = currentSystemTime;
        }

        public String getCurrentRunTime() {
            return currentRunTime;
        }

        public void setCurrentRunTime(String currentRunTime) {
            this.currentRunTime = currentRunTime;
        }
    }
}
