package com.ztesoft.level1.util;

import java.text.DecimalFormat;

/**
 * 文件名称 : NumericalUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 数值格式化
 * <p>
 * 创建时间 : 2017/5/22 16:57
 * <p>
 */
public class NumericalUtil {
    private static NumericalUtil css_ = null;

    public static NumericalUtil getInstance() {
        if (css_ == null)
            css_ = new NumericalUtil();
        return css_;
    }

    /**
     * 数值进行千分位格式化
     *
     * @param value 数值
     * @return
     */
    public String setThousands(String value) {
        if (value == null || "".equals(value)) {
            return value;
        }
        if (value.startsWith(".")) {
            value = "0" + value;
        }
        String decimal = "";
        StringBuffer returnValue = new StringBuffer();
        if (value.indexOf(".") != -1) {
            decimal = value.substring(value.lastIndexOf("."), value.length());
            value = value.substring(0, value.lastIndexOf("."));
        }
        if (value.charAt(0) == '-') {
            returnValue.append("-");
            value = value.substring(1, value.length());
        }
        if (value.length() > 3) {
            returnValue.append(value.substring(0, value.length() % 3));
            for (int i = value.length() % 3; i < value.length(); ) {
                if (i != 0) {
                    returnValue.append(",");
                }
                returnValue.append(value.substring(i, i + 3));
                i = i + 3;
            }
        } else {
            returnValue.append(value);
        }
        returnValue.append(decimal);
        return returnValue.toString();
    }

    /**
     * 设置小数位位数
     *
     * @param v   传入的值
     * @param num 需要保留的小数位位数
     * @return
     */
    public String setDecimalPlace(Object v, int num) {
        if (null == v)
            return "0";
        String value = (String) v;
        boolean minusFlag = false;
        if (value.startsWith("-")) {
            value = value.substring(1, value.length());
            minusFlag = true;
        }
        if (value.startsWith(".")) {
            value = "0" + value;
        }
        if (value.indexOf(".") != -1) {// 本身有小数点
            int digits = value.lastIndexOf(".");
            String temp = value.substring(digits + 1, value.length());
            if (temp.length() > num) {
                String formatStr;
                if (num == 0) {
                    double val1 = Double.parseDouble(value);
                    int val2 = (int) (val1 + 0.5);
                    value = String.valueOf(val2);
                } else {// 如果有小数位，则需要计算小数点位，因此+1
                    formatStr = "##0.";
                    for (int i = 0; i < num; i++) {
                        formatStr += "0";
                    }
                    DecimalFormat df = new DecimalFormat(formatStr);
                    value = df.format(Double.parseDouble(value));
                }
            } else {
                for (int j = 0; j < num - temp.length(); j++) {
                    value += "0";
                }
            }
        } else if (num > 0) {// 本身无小数点，但需要保留小数时，添加0
            value = value + ".";
            for (int i = 0; i < num; i++) {
                value = value + "0";
            }
        }
        if (minusFlag) {
            value = "-" + value;
        }
        return value;
    }
}