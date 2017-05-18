package com.ztesoft.level1.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件名称 : Eval
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 表达式计算类
 * <p>
 * 创建时间 : 2017/3/24 9:54
 * <p>
 */
public class Eval {
    /**
     * 计算字符串四则运算表达式
     *
     * @param string
     * @return
     */
    public String eval(String string) {
        String regexCheck = "[\\(\\)\\d\\<\\>\\==\\>=\\<=\\!=\\+\\-\\*/\\.]*";// 是否是合法的表达式

        if (!Pattern.matches(regexCheck, string)) {
            if (string.contains("==")) {
                String[] temp;
                temp = string.split("==");
                if (temp.length < 2)
                    return null;
                String value = String.valueOf(temp[0].equals(temp[1]));
                return value;
            } else {
                return null;
            }
        }

        Matcher matcher;
        String temp;
        int index = -1;
        String regex = "\\([\\d\\.\\+\\-\\*/]+\\)";// 提取括号表达式
        string = string.replaceAll("\\s", "");// 去除空格
        try {
            Pattern pattern = Pattern.compile(regex);
            // 循环计算所有括号里的表达式
            while (pattern.matcher(string).find()) {
                matcher = pattern.matcher(string);
                while (matcher.find()) {
                    temp = matcher.group();
                    index = string.indexOf(temp);
                    string = string.substring(0, index) + computeStirngNoBracket(temp) + string
                            .substring(index + temp.length());
                }
            }
            // 最后计算总的表达式结果
            string = computeStirngNoBracket(string);
        } catch (NumberFormatException e) {
            return e.getMessage();
        }
        return string;
    }

    /**
     * 计算不包含括号的表达式
     *
     * @param string
     * @return
     */
    private String computeStirngNoBracket(String string) {
        string = string.replaceAll("(^\\()|(\\)$)", "");
        String regexMultiAndDivision = "[\\d\\.]+(\\*|\\/)[\\d\\.]+";
        String regexAdditionAndSubtraction = "(^\\-)?[\\d\\.]+(\\+|\\-)[\\d\\.]+";

        String regexMultiAndDivision1 = "[\\d\\.]+(\\>|\\<|\\==|\\>=|\\<=|\\!=)[\\d\\.]+";

        String temp;
        int index = -1;

        // 解析乘除法
        Pattern pattern = Pattern.compile(regexMultiAndDivision);
        Matcher matcher;
        while (pattern.matcher(string).find()) {
            matcher = pattern.matcher(string);
            if (matcher.find()) {
                temp = matcher.group();
                index = string.indexOf(temp);
                string = string.substring(0, index) + doMultiAndDivision(temp) + string.substring
                        (index + temp.length());
            }
        }

        // 解析加减法
        pattern = Pattern.compile(regexAdditionAndSubtraction);
        while (pattern.matcher(string).find()) {
            matcher = pattern.matcher(string);
            if (matcher.find()) {
                temp = matcher.group();
                index = string.indexOf(temp);
                if (temp.startsWith("-")) {
                    string = string.substring(0, index) + doNegativeOperation(temp) + string
                            .substring(index + temp.length());
                } else {
                    string = string.substring(0, index) + doAdditionAndSubtraction(temp) + string
                            .substring(index + temp.length());
                }
            }
        }

        // 大于小于等于
        pattern = Pattern.compile(regexMultiAndDivision1);
        matcher = null;
        while (pattern.matcher(string).find()) {
            matcher = pattern.matcher(string);
            if (matcher.find()) {
                temp = matcher.group();
                index = string.indexOf(temp);
                string = string.substring(0, index) + doMultiAndPar(temp) + string.substring
                        (index + temp.length());
            }
        }

        return string;
    }

    /**
     * 执行乘除法
     *
     * @param string
     * @return
     */
    private String doMultiAndDivision(String string) {
        String value;
        double d1, d2;
        String[] temp;
        if (string.contains("*")) {
            temp = string.split("\\*");
        } else {
            temp = string.split("/");
        }

        if (temp.length < 2)
            return null;

        d1 = Double.valueOf(temp[0]);
        d2 = Double.valueOf(temp[1]);
        if (string.contains("*")) {
            value = String.valueOf(d1 * d2);
        } else {
            value = String.valueOf(d1 / d2);
        }

        return value;
    }

    /**
     * 执行加减法
     *
     * @param string
     * @return
     */
    private String doAdditionAndSubtraction(String string) {
        double d1, d2;
        String[] temp;
        String value;
        if (string.contains("+")) {
            temp = string.split("\\+");
        } else {
            temp = string.split("\\-");
        }

        if (temp.length < 2)
            return null;

        d1 = Double.valueOf(temp[0]);
        d2 = Double.valueOf(temp[1]);
        if (string.contains("+")) {
            value = String.valueOf(d1 + d2);
        } else {
            value = String.valueOf(d1 - d2);
        }

        return value;
    }

    /**
     * 执行大于、小于、等于
     *
     * @param string
     * @return
     */
    private String doMultiAndPar(String string) {
        String value;
        double d1, d2;
        String[] temp;
        if (string.contains("<=")) {
            temp = string.split("\\<=");
        } else if (string.contains(">=")) {
            temp = string.split("\\>=");
        } else if (string.contains("!=")) {
            temp = string.split("\\!=");
        } else if (string.contains(">")) {
            temp = string.split("\\>");
        } else if (string.contains("<")) {
            temp = string.split("\\<");
        } else {
            temp = string.split("==");
        }

        if (temp.length < 2)
            return null;

        d1 = Double.valueOf(temp[0]);
        d2 = Double.valueOf(temp[1]);
        if (string.contains("<=")) {
            value = String.valueOf(d1 <= d2);
        } else if (string.contains(">=")) {
            value = String.valueOf(d1 >= d2);
        } else if (string.contains("!=")) {
            value = String.valueOf(d1 != d2);
        } else if (string.contains(">")) {
            value = String.valueOf(d1 > d2);
        } else if (string.contains("<")) {
            value = String.valueOf(d1 < d2);
        } else {
            value = String.valueOf(d1 == d2);
        }

        return value;
    }

    /**
     * 执行负数运算
     *
     * @param string
     * @return
     */
    private String doNegativeOperation(String string) {
        String temp = string.substring(1);
        if (temp.contains("+")) {
            temp = temp.replace("+", "-");
        } else {
            temp = temp.replace("-", "+");
        }
        temp = doAdditionAndSubtraction(temp);
        if (temp.startsWith("-")) {
            temp = temp.substring(1);
        } else {
            temp = "-" + temp;
        }
        return temp;
    }

    public static void main(String args[]) {
        Eval eval = new Eval();
        System.out.println(eval.eval("1+2==3<="));
    }
}