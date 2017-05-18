package com.ztesoft.level1.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Random;

/**
 * 文件名称 : Cn2Spell
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 汉字转换位汉语拼音，英文字符不变
 * <p>
 * 创建时间 : 2017/4/20 11:13
 * <p>
 */
public class Cn2Spell {

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i],
                            defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i],
                            defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    /**
     * 方法说明： 该函数获得随机数字符串
     *
     * @param iLen  int :需要获得随机数的长度
     * @param iType int:随机数的类型：'0':表示仅获得数字随机数；'1'：表示仅获得字符随机数；'2'：表示获得数字字符混合随机数
     * @since 1.0.0
     */
    public static final String generateRandom(int iLen, int iType) {
        StringBuffer strRandom = new StringBuffer();// 随机字符串
        Random rnd = new Random();
        if (iLen < 0) {
            iLen = 12;
        }
        if ((iType > 2) || (iType < 0)) {
            iType = 2;
        }
        switch (iType) {
            case 0:
                for (int iLoop = 0; iLoop < iLen; iLoop++) {
                    strRandom.append(Integer.toString(rnd.nextInt(10)));
                }
                break;
            case 1:
                for (int iLoop = 0; iLoop < iLen; iLoop++) {
                    strRandom.append(Integer.toString((35 - rnd.nextInt(10)), 36));
                }
                break;
            case 2:
                for (int iLoop = 0; iLoop < iLen; iLoop++) {
                    strRandom.append(Integer.toString(rnd.nextInt(36), 36));
                }
                break;
        }
        return strRandom.toString();
    }
}