package com.ztesoft.level1.util.encrypt;

import org.apache.commons.codec.binary.Hex;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 文件名称 : RSAHelper
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 用于非堆成加密
 * <p>
 * 创建时间 : 2017/3/24 10:11
 * <p>
 */
public class RSAHelper {

    private final static String encode = "UTF-8";

    private static final String ALGORITHM = "RSA";
    private static final int KEYSIZE = 1024;
    private KeyPair keyPair;
    private static RSAHelper rsa_ = null;

    public static RSAHelper getInstance() {
        if (rsa_ == null) {
            rsa_ = new RSAHelper();
        }
        return rsa_;
    }

    public RSAHelper() {
        invokeKeys();
    }

    /***
     * 用于生成密钥对
     */
    public void invokeKeys() {

        try {
            KeyPairGenerator keypairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keypairGenerator.initialize(KEYSIZE);
            keyPair = keypairGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从公钥字符串得到公钥
     *
     * @param key 密钥字符串
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromStr(String key) throws Exception {
        byte[] keyBytes;
        // keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        keyBytes = Hex.decodeHex(key.toCharArray());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        return publicKey;
    }

    /**
     * 从私钥字符串得到私钥
     *
     * @param key 密钥字符串
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromStr(String key) throws Exception {
        byte[] keyBytes;
        // keyBytes = (new BASE64Decoder()).decodeBuffer(key);

        keyBytes = Hex.decodeHex(key.toCharArray());

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }

    /**
     * 得到密钥字符串
     *
     * @return
     */
    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        // String s = (new BASE64Encoder()).encode(keyBytes);
        String s = new String(Hex.encodeHex(keyBytes));

        return s;
    }

    /***
     * 用公钥加密
     *
     * @param publicKey
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /***
     * 用公钥加密 加密字符串超过117字节的处理
     *
     * @param text
     * @param key
     * @return
     * @throws Exception
     */
    public static String getEncryptedValue(String text, PublicKey key) throws Exception {

        String encryptedText;
        try {
            byte[] textBytes = text.getBytes("GBK");

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            cipher.init(Cipher.ENCRYPT_MODE, key);

            int textBytesChunkLen = 100;
            int encryptedChunkNum = (textBytes.length - 1) / textBytesChunkLen + 1;

            // RSA returns 128 bytes as output for 100 text bytes

            int encryptedBytesChunkLen = 128;
            int encryptedBytesLen = encryptedChunkNum * encryptedBytesChunkLen;

            // Define the Output array.

            byte[] encryptedBytes = new byte[encryptedBytesLen];
            int textBytesChunkIndex = 0;
            int encryptedBytesChunkIndex = 0;

            for (int i = 0; i < encryptedChunkNum; i++) {

                if (i < encryptedChunkNum - 1) {
                    encryptedBytesChunkIndex = encryptedBytesChunkIndex + cipher.doFinal
                            (textBytes, textBytesChunkIndex, textBytesChunkLen, encryptedBytes,
                                    encryptedBytesChunkIndex);
                    textBytesChunkIndex = textBytesChunkIndex + textBytesChunkLen;

                } else {
                    cipher.doFinal(textBytes, textBytesChunkIndex, textBytes.length -
                            textBytesChunkIndex, encryptedBytes, encryptedBytesChunkIndex);
                }
            }

            // encryptedText = new BASE64Encoder().encode(encryptedBytes);
            encryptedText = new String(Hex.encodeHex(encryptedBytes));
        } catch (Exception e) {
            throw e;
        }

        return encryptedText;

    }

    /***
     * 用私钥解密,超过117的处理
     *
     * @param text
     * @param key
     * @return
     * @throws Exception
     */
    public static String getDecryptedValue(String text, PrivateKey key) {

        String result = null;

        try {
            // byte[] encryptedBytes = new BASE64Decoder().decodeBuffer(text);

            byte[] encryptedBytes = Hex.decodeHex(text.toCharArray());

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            cipher.init(Cipher.DECRYPT_MODE, key);

            int encryptedByteChunkLen = 128;

            int encryptedChunkNum = encryptedBytes.length / encryptedByteChunkLen;

            int decryptedByteLen = encryptedChunkNum * encryptedByteChunkLen;

            byte[] decryptedBytes = new byte[decryptedByteLen];

            int decryptedIndex = 0;

            int encryptedIndex = 0;

            for (int i = 0; i < encryptedChunkNum; i++) {

                if (i < encryptedChunkNum - 1) {
                    decryptedIndex = decryptedIndex + cipher.doFinal(encryptedBytes,
                            encryptedIndex, encryptedByteChunkLen, decryptedBytes, decryptedIndex);
                    encryptedIndex = encryptedIndex + encryptedByteChunkLen;

                } else {
                    decryptedIndex = decryptedIndex + cipher.doFinal(encryptedBytes,
                            encryptedIndex, encryptedBytes.length - encryptedIndex,
                            decryptedBytes, decryptedIndex);
                }
            }

            result = new String(decryptedBytes, "GBK").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /***
     * 用私钥解密
     *
     * @param privateKey
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decrypt(PrivateKey privateKey, byte[] data) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /***
     * 得到公钥
     *
     * @return
     */
    public PublicKey getPublicKey() {

        return keyPair.getPublic();
    }

    /***
     * 得到私钥
     *
     * @return
     */
    public PrivateKey getPrivateKey() {

        return keyPair.getPrivate();
    }

    /***
     * 得到密钥对
     *
     * @return
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }

    public static void main(String[] args) throws Exception {
        RSAHelper rsa = RSAHelper.getInstance();
        // 公钥
        PublicKey publicKey = (RSAPublicKey) rsa.getKeyPair().getPublic();

        // 私钥
        PrivateKey privateKey = (RSAPrivateKey) rsa.getKeyPair().getPrivate();

        String publicKeyString = getKeyString(publicKey);
        // System.out.println("public:\n" + publicKeyString);

        String privateKeyString = getKeyString(privateKey);
        // System.out.println("private:\n" + privateKeyString);
        // 明文
        String plainText = "我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina" +
                ".com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina" +
                ".com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina" +
                ".com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina" +
                ".com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina" +
                ".com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina.com我们都很好！邮件：@sina" +
                ".com我们都很好！邮件：@sina.com哈哈";

        // 用公钥加密
        String aa = getEncryptedValue(plainText, publicKey);
        // System.out.println("公钥加密后的字符串：");
        // System.out.println(aa);
        // 通过密钥字符串得到密钥
        publicKey = getPublicKeyFromStr(publicKeyString);
        privateKey = getPrivateKeyFromStr(privateKeyString);

        // 用私钥解密 公钥加密的字符串
        aa = getDecryptedValue(aa, privateKey);
        // System.out.println("用私钥解密  公钥加密的字符串：");
        // System.out.println(aa);

        // publicKeyString = getKeyString(publicKey);
        // System.out.println("public:\n" +publicKeyString);

        // privateKeyString = getKeyString(privateKey);
        // System.out.println("private:\n" + privateKeyString);
    }
}