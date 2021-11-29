package com.harry.bluetodevices.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin-harry
 * @date 2021/7/13
 * @address
 * @Desc 数字分割处理工具
 */
public class DealNum {
    /**
     * 数字加分割
     *
     * @param numStr:字符串格式的数字
     * @param divider:分割的字符
     * @param num:分割的位数
     */
    public static String addDivider(String numStr, String divider, int num) {

        if (TextUtils.isEmpty(numStr)) {
            return null;
        }
        String[] strs = null;
        StringBuilder sb1;
        if (numStr.contains(".")) {
            strs = numStr.split("\\.");
            sb1 = new StringBuilder(strs[0]);
        } else {
            sb1 = new StringBuilder(numStr);
        }

        StringBuilder sb2 = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < sb1.length(); i = 0) {

            if (sb1.length() > num) {
                temp.append(divider);
                temp.append(sb1.substring(sb1.length() - num, sb1.length()));
                sb2.insert(0, temp);
                sb1.delete(sb1.length() - num, sb1.length());
            } else {
                sb2.insert(0, sb1);
                break;
            }
            temp.delete(0, temp.length());

        }

        if (strs != null) {
            return sb2.append("." + strs[1]).toString();
        } else {
            return sb2.toString();
        }

    }

    /**
     * 实现按字符串位数在前面补0
     *
     * @param str
     * @param length
     * @return
     */
    public static String fillZeroBeforeString(String str, int length) {
        return fillStringBeforeString(str, "0", length);
    }

    public static String fillStringBeforeString(String str, String fill, int length) {
        if (str.length() < length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length - str.length(); i++) {
                sb.append(fill);
            }
            sb.append(str);
            return sb.toString();
        } else {
            return str;
        }
    }

    /**
     * 数字去分割
     *
     * @param numStr:字符串格式的数字
     * @param divider:分割的字符
     */
    public static String delDivider(String numStr, String divider) {
        if (TextUtils.isEmpty(numStr)) {
            return null;
        }
        numStr = numStr.replaceAll(divider, "");
        return numStr;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @return
     */
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @param size        指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length,
                                          int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str 原始字符串
     * @param f   开始位置
     * @param t   结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    /**
     * 提供精确加法计算的add方法
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确减法运算的sub方法
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算的mul方法
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double mul(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供精确的除法运算方法div
     *
     * @param value1 被除数
     * @param value2 除数
     * @param scale  精确范围
     * @return 两个参数的商
     * @throws IllegalAccessException
     */
    public static double div(double value1, double value2, int scale) throws IllegalAccessException {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        //默认保留两位会有错误，这里设置保留小数点后4位
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
