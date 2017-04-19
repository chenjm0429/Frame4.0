package com.ztesoft.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件名称 : StringTools
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 字符串的工具类
 * <p>
 * 创建时间 : 2017/3/24 10:17
 * <p>
 */
public class StringTools {
    /**
     * 方法描述：判断传入的字符串是否非空，即：字符串是否等于null、""或" "。
     *
     * @param str 字符串
     * @return false：字符串为空 true：字符串非空
     */
    public static boolean isNotEmpty(String str) {
        if ((null == str) || ("".equals(str.trim()))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 方法描述：将传入的字符串转换成短整型数据，如果转换过程中发生异常，则返回默认值：defaultValue。
     * <p>
     * 注意事项：编写本方法的目的，是为了让使用者不必顾及转型过程中是否会抛出异常。 如需抛出异常，则可直接调用转型方法，并增加异常捕获的代码。
     *
     * @param str          字符串
     * @param defaultValue 转型失败后需返回的默认值
     * @return 短整型数据
     */
    public static short convertIntoShort(String str, short defaultValue) {
        // 定义一个返回值，假如转型过程中发生异常，则返回此默认值
        short retData = defaultValue;
        try {
            retData = Short.parseShort(str.trim());
        } catch (NumberFormatException e) {
        } catch (Exception ex) {
        }

        return retData;
    }

    /**
     * 方法描述：将传入的字符串转换成整型数据，如果转换过程中发生异常，则返回默认值：defaultValue。
     * <p>
     * 注意事项：编写本方法的目的，是为了让使用者不必顾及转型过程中是否会抛出异常。 如需抛出异常，则可直接调用转型方法，并增加异常捕获的代码。
     *
     * @param str          字符串
     * @param defaultValue 转型失败后需返回的默认值
     * @return 整型数据
     */
    public static int convertIntoInt(String str, int defaultValue) {
        // 定义一个返回值，假如转型过程中发生异常，则返回此默认值
        int retData = defaultValue;
        try {
            retData = Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
        } catch (Exception ex) {
        }

        return retData;
    }

    /**
     * 方法描述：将传入的字符串转换成长整型数据，如果转换过程中发生异常，则返回默认值：defaultValue。
     * <p>
     * 注意事项：编写本方法的目的，是为了让使用者不必顾及转型过程中是否会抛出异常。 如需抛出异常，则可直接调用转型方法，并增加异常捕获的代码。
     *
     * @param str          字符串
     * @param defaultValue 转型失败后需返回的默认值
     * @return 长整型数据
     */
    public static long convertIntoLong(String str, long defaultValue) {
        // 定义一个返回值，假如转型过程中发生异常，则返回此默认值
        long retData = defaultValue;
        try {
            retData = Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
        } catch (Exception ex) {
        }

        return retData;
    }

    /**
     * 方法描述：将传入的字符串转换成浮点型数据，如果转换过程中发生异常，则返回默认值：defaultValue。
     * <p>
     * 注意事项：编写本方法的目的，是为了让使用者不必顾及转型过程中是否会抛出异常。 如需抛出异常，则可直接调用转型方法，并增加异常捕获的代码。
     *
     * @param str          字符串
     * @param defaultValue 转型失败后需返回的默认值
     * @return 浮点型数据
     */
    public static float convertIntoFloat(String str, float defaultValue) {
        // 定义一个返回值，假如转型过程中发生异常，则返回此默认值
        float retData = defaultValue;

        try {
            retData = Float.parseFloat(str.trim());
        } catch (NumberFormatException e) {
        } catch (Exception ex) {
        }

        return retData;
    }

    /**
     * 方法描述：将传入的字符串转换成双精度型数据，如果转换过程中发生异常，则返回默认值：defaultValue。
     * <p>
     * 注意事项：编写本方法的目的，是为了让使用者不必顾及转型过程中是否会抛出异常。 如需抛出异常，则可直接调用转型方法，并增加异常捕获的代码。
     *
     * @param str          字符串
     * @param defaultValue 转型失败后需返回的默认值
     * @return 双精度型数据
     */
    public static double convertIntoDouble(String str, double defaultValue) {
        // 定义一个返回值，假如转型过程中发生异常，则返回此默认值
        double retData = defaultValue;
        try {
            retData = Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
        } catch (Exception ex) {
        }

        return retData;
    }

    /**
     * 方法描述：设置字符串的数值精确位数。
     * <p>
     * 注意事项：数值精确位数可正可负，如果为正数，则设置小数位数，不够则补零，多余则四舍五入到指定位数。 如果为负数，则按四舍五入设置精确到多少位。
     * <p>
     * 例如：str=2304，precision=1，则返回2304.0。如果precision=-1，则返回2300。
     *
     * @param str       字符串
     * @param precision 数值精确位数
     * @return 设置完成后的字符串
     */
    public static String setStrPrecision(String str, int precision) {
        BigDecimal bigDecimal1 = new BigDecimal(str);

        // 如果数值精确位数为负
        if (precision < 0) {
            // 对数值精确位数取正值，并将其转换为10的precision次方
            precision = Math.abs(precision);
            BigDecimal bigDecimal2 = new BigDecimal(Math.pow(10, precision));

            // 除以10的precision次方，并按照四舍五入的方法精确到小数位数的第0位
            bigDecimal1 = bigDecimal1.divide(bigDecimal2, 0,
                    BigDecimal.ROUND_HALF_UP);

            // 再乘以10的precision次方
            bigDecimal1 = bigDecimal1.multiply(bigDecimal2);
            
            return bigDecimal1.toString();
        }
        return bigDecimal1.setScale(precision, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 方法描述：两个字符串相加的方法。
     * <p>
     * 注意事项：返回结果的小数位数是以两个字符串中最大小数位数为准。
     * <p>
     * 例如：21.230 + 8.1 = 29.330
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 两个字符串相加的结果
     */
    public static String add(String str1, String str2) {
        BigDecimal bigDecimal1 = new BigDecimal(str1);
        BigDecimal bigDecimal2 = new BigDecimal(str2);
        BigDecimal retData = bigDecimal1.add(bigDecimal2);
        
        return retData.toString();
    }

    /**
     * 方法描述：两个字符串相加的方法，并设置相加结果的数值精确位数。
     * <p>
     * 注意事项：数值精确位数precision可正可负。如果为正数，则设置小数位数，不够则补零，多余则四舍五入到指定位数。
     * 如果为负数，则按四舍五入设置精确到多少位。
     * <p>
     * 例如：str1=11，str2=20.1，precision=1，则返回31.1。如果precision=-1，则返回30。
     *
     * @param str1      字符串1
     * @param str2      字符串2
     * @param precision 数值精确位数
     * @return 两个字符串相加的结果
     */
    public static String add(String str1, String str2, int precision) {
        // 对两个字符串进行相加操作
        String str = add(str1, str2);

        // 设置返回结果的数值精确位数
        return setStrPrecision(str, precision);
    }

    /**
     * 方法描述：两个字符串相减的方法。
     * <p>
     * 注意事项：返回结果的小数位数是以两个字符串中最大小数位数为准。
     * <p>
     * 例如：21.230 - 8.1 = 13.130
     *
     * @param str1 被减数字符串
     * @param str2 减数字符串
     * @return 两个字符串相减的结果
     */
    public static String subtract(String str1, String str2) {
        BigDecimal bigDecimal1 = new BigDecimal(str1);
        BigDecimal bigDecimal2 = new BigDecimal(str2);
        BigDecimal retData = bigDecimal1.subtract(bigDecimal2);
        
        return retData.toString();
    }

    /**
     * 方法描述：两个字符串相减的方法，并设置相减结果的数值精确位数。
     * <p>
     * 注意事项：数值精确位数precision可正可负。如果为正数，则设置小数位数，不够则补零，多余则四舍五入到指定位数。
     * 如果为负数，则按四舍五入设置精确到多少位。
     * <p>
     * 例如：str1=110，str2=20.1，precision=1，则返回89.9。如果precision=-1，则返回90。
     *
     * @param str1      被减数字符串
     * @param str2      减数字符串
     * @param precision 数值精确位数
     * @return 两个字符串相减的结果
     */
    public static String subtract(String str1, String str2, int precision) {
        // 对两个字符串进行减法操作
        String str = subtract(str1, str2);

        // 设置返回结果的数值精确位数
        return setStrPrecision(str, precision);
    }

    /**
     * 方法描述：两个字符串相乘的方法。
     * <p>
     * 注意事项：返回结果的小数位数是以两个字符串小数位数的和。
     * <p>
     * 例如：4.10 * 4.150 = 17.01500
     *
     * @param str1 被减数字符串
     * @param str2 减数字符串
     * @return 两个字符串相减的结果
     */
    public static String multiply(String str1, String str2) {
        BigDecimal bigDecimal1 = new BigDecimal(str1);
        BigDecimal bigDecimal2 = new BigDecimal(str2);
        BigDecimal retData = bigDecimal1.multiply(bigDecimal2);
        
        return retData.toString();
    }

    /**
     * 方法描述：两个字符串相乘的方法，并设置相乘结果的数值精确位数。
     * <p>
     * 注意事项：数值精确位数precision可正可负。如果为正数，则设置小数位数，不够则补零，多余则四舍五入到指定位数。
     * 如果为负数，则按四舍五入设置精确到多少位。
     * <p>
     * 例如：str1=30，str2=4.15，precision=1，则返回124.5。如果precision=-1，则返回120。
     *
     * @param str1      字符串1
     * @param str2      字符串2
     * @param precision 数值精确位数
     * @return 两个字符串相乘的结果
     */
    public static String multiply(String str1, String str2, int precision) {
        // 对两个字符串进行乘法操作
        String str = multiply(str1, str2);

        // 设置返回结果的数值精确位数
        return setStrPrecision(str, precision);
    }

    /**
     * 方法描述：两个字符串相除的方法，并设置相除结果的数值精确位数。
     * <p>
     * 注意事项：数值精确位数precision可正可负。如果为正数，则设置小数位数，不够则补零，多余则四舍五入到指定位数。
     * 如果为负数，则按四舍五入设置精确到多少位。
     * <p>
     * 例如：str1=13000，str2=3，precision=2，则返回4333.33。如果precision=-1，则返回4330。
     *
     * @param str1      被除数
     * @param str2      除数
     * @param precision 数值精确位数
     * @return 两个字符串相除的结果
     */
    public static String divide(String str1, String str2, int precision) {
        BigDecimal bigDecimal1 = new BigDecimal(str1);
        BigDecimal bigDecimal2 = new BigDecimal(str2);

        // 如果数值精确位数为负
        if (precision < 0) {
            // 设置相除结果为四舍五入，并且小数位数为0。
            BigDecimal retData = bigDecimal1.divide(bigDecimal2, 0,
                    BigDecimal.ROUND_HALF_UP);

            // 设置数值精确位数
            return setStrPrecision(retData.toString(), precision);
        } else {
            BigDecimal retData = bigDecimal1.divide(bigDecimal2, precision,
                    BigDecimal.ROUND_HALF_UP);
            return retData.toString();
        }
    }

    /**
     * 方法描述：两个字符串求余的方法。
     * <p>
     * 注意事项：返回结果的小数位数是以两个字符串中最大小数位数为准。
     * <p>
     * 例如：13000.15 % 0.03 = 0.01
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 两个字符串求余的结果
     */
    public static String remainder(String str1, String str2) {
        BigDecimal bigDecimal1 = new BigDecimal(str1);
        BigDecimal bigDecimal2 = new BigDecimal(str2);

        // 两数相除，得出的结果必须为整数，并且舍入方式为“接近零的舍入模式”。
        BigDecimal result = bigDecimal1.divide(bigDecimal2, 0, BigDecimal.ROUND_DOWN);
        
        return bigDecimal1.subtract(bigDecimal2.multiply(result)).toString();
    }

    /**
     * 方法描述：两个字符串求余的方法，并设置求余结果的数值精确位数。
     * <p>
     * 注意事项：数值精确位数precision可正可负。如果为正数，则设置小数位数，不够则补零，多余则四舍五入到指定位数。
     * 如果为负数，则按四舍五入设置精确到多少位。
     * <p>
     * 例如：str1=13000.15，str2=33，precision=2，则返回31.15。如果precision=-1，则返回30。
     *
     * @param str1      字符串1
     * @param str2      字符串2
     * @param precision 数值精确位数
     * @return 两个字符串求余的结果
     */
    public static String remainder(String str1, String str2, int precision) {
        // 对两个字符串进行求余操作
        String str = remainder(str1, str2);

        // 设置返回结果的数值精确位数
        return setStrPrecision(str, precision);
    }

    /**
     * 方法描述：求字符串1占字符串2的百分比，并设置百分比的数值精确位数。
     * <p>
     * 注意事项：数值精确位数precision可正可负。如果为正数，则设置小数位数，不够则补零，多余则四舍五入到指定位数。
     * 如果为负数，则按四舍五入设置精确到多少位。
     * <p>
     * 例如：str1=33，str2=120，precision=2，则返回27.50%。如果precision=-1，则返回30%。
     *
     * @param str1      被除数
     * @param str2      除数
     * @param precision 数值精确位数
     * @return 百分比
     */
    public static String percent(String str1, String str2, int precision) {
        return multiply(divide(str1, str2, precision + 2), "100", precision) + "%";
    }

    /**
     * 方法描述：判断字符串是否可以转换成浮点型的数据
     *
     * @param str 字符串
     * @return true：可以转换为浮点型数据 false：不能转换为浮点型数据
     */
    public static boolean CheckNum(String str) {
        Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(CheckNum("023.13"));
    }
}
