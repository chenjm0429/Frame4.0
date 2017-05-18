package com.ztesoft.level1.util.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 文件名称 : DESHelp
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : DES加解密帮助类
 * <p>
 * 创建时间 : 2017/5/22 16:18
 * <p>
 */
public class DESHelp {

    private final static String languageCode = "UTF-8";

    private static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};

    private static byte[] desEncrypt(byte[] plainText, byte[] desKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(desKey, "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte data[] = plainText;
        byte encryptedData[] = cipher.doFinal(data);

        return encryptedData;
    }

    private static byte[] desDecrypt(byte[] encryptText, byte[] desKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(desKey, "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte encryptedData[] = encryptText;
        byte decryptedData[] = cipher.doFinal(encryptedData);

        return decryptedData;
    }

    /**
     * 加密数据
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的数据
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {

        if (data == null || key == null)
            return null;

        Base64 base = new Base64();

        return new String(base.encode(desEncrypt(data.getBytes(languageCode), key.getBytes())),
                languageCode);
    }

    /**
     * 解密数据
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return 解密后的数据
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws Exception {

        if (data == null || key == null)
            return null;

        Base64 base = new Base64();
        byte[] result = base.decode(data.getBytes());

        return new String(desDecrypt(result, key.getBytes()), languageCode);
    }

    public static void main(String[] args) throws Exception {
        String key = "20140422";
        String input = "{\"funcInfo\":}";
        DESHelp crypt = new DESHelp();
        System.out.println("Encode:" + crypt.encrypt(input, key));
        System.out.println("Decode:" + crypt.decrypt(crypt.encrypt(input, key), key));
    }
}