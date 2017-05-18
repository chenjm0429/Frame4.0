package com.ztesoft.level1.util.encrypt;

import org.apache.commons.codec.binary.Base64;

/**
 * 文件名称 : Base64Help
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : Base64加解密帮助类
 * <p>
 * 创建时间 : 2017/5/22 16:41
 * <p>
 */
public class Base64Help {
    private final static String languageCode = "UTF-8";

    /**
     * 加密数据
     *
     * @param input 待加密数据
     * @return 加密后的数据
     */
    public static String encrypt(String input) throws Exception {

        if (input == null)
            return null;

        Base64 base = new Base64();

        return new String(base.encode(input.getBytes(languageCode)), languageCode);
    }

    /**
     * 解密数据
     *
     * @param input 待解密的数据
     * @return 解密后的数据
     */
    public static String decrypt(String input) throws Exception {

        if (input == null)
            return null;

        Base64 base = new Base64();
        byte[] result = base.decode(input.getBytes(languageCode));

        return new String(result, languageCode);
    }

    public static void main(String[] args) throws Exception {
//		String key = "20140422";   
        String input = "{\"funcInfo哈哈\":}";
        Base64Help crypt = new Base64Help();
        System.out.println("Encode:" + crypt.encrypt(input));
        System.out.println("Decode:" + crypt.decrypt(crypt.encrypt(input)));
    }
}
