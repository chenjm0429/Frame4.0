package com.ztesoft.level1.util.encrypt;

import java.io.IOException;

/**
 * 文件名称 : TranspositionHelp
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 移位混淆加密帮助类
 * <p>
 * 创建时间 : 2017/5/22 16:33
 * <p>
 */
public class TranspositionHelp {

    /**
     * 将明文字符数组分组
     *
     * @param plaintext 明文字符数组
     * @param key       密钥
     */
    private static char[][] groupPlaintext(char[] plaintext, Integer key) {
        char[][] plaintextGroup;
        Integer length = plaintext.length;
//
//		if (length % key != 0) {// 当字符数组的长度不是密钥的整数倍时，用字符'$'填补
//			String plaintextStr = new String(plaintext);
//
//			BigInteger lengthBI = new BigInteger(length.toString());
//			BigInteger keyBI = new BigInteger(key.toString());
//			BigInteger mod = lengthBI.mod(keyBI);
//
//			int fillNumber = keyBI.intValue() - mod.intValue();
//			for (int i = 0; i < fillNumber; i++) {
//				plaintextStr = plaintextStr + "$";
//			}
//			plaintext = plaintextStr.toCharArray();
//			length = plaintext.length;
//		}

        int groupAmount = length / key;
        plaintextGroup = new char[groupAmount][key];
        for (int i = 0; i < groupAmount; i++) {
            for (int j = 0; j < key; j++) {
                // 将明文字符数组分割为二维数组
                plaintextGroup[i][j] = plaintext[i * key + j];
            }
        }

        return plaintextGroup;
    }

    /**
     * 将密文分组
     *
     * @param ciphertext 密文
     * @param key        密钥
     */
    private static char[][] groupCiphertext(char[] ciphertext, Integer key) {
        char[][] ciphertextGroup = null;
        int length = ciphertext.length;

        int groupAmount = length / key;
        ciphertextGroup = new char[groupAmount][key];
        for (int i = 0; i < groupAmount; i++) {
            for (int j = 0; j < key; j++) {
                ciphertextGroup[i][j] = ciphertext[i * key + j];
            }
        }

        return ciphertextGroup;
    }

    /**
     * 按照加密规则，将分割后的各个子字符数组分别进行移位
     *
     * @param group 每一个子字符数组
     */
    private static char[] changePlaintext(char[] group) {
        char[] newGroup = new char[group.length];
        for (int i = 0; i < group.length; i++) {
            if (i == 0) {
                newGroup[0] = group[group.length - 1];
                continue;
            }

            newGroup[i] = group[i - 1];
        }
        return newGroup;
    }

    /**
     * @param group 按照加密规则,将密文分割后的各个子字符数组按照相反的方式分别进行移位
     */
    private static char[] changeCiphertext(char[] group) {
        char[] newGroup = new char[group.length];
        for (int i = 0; i < group.length; i++) {
            if (i == group.length - 1) {
                newGroup[group.length - 1] = group[0];
                break;
            }

            newGroup[i] = group[i + 1];
        }
        return newGroup;
    }

    private static String getCipherFromGroup(char[][] plaintextGroup) {
        String ciphertext = "";

        int rowAccount = plaintextGroup.length;
        int lowAccount = plaintextGroup[0].length;

        for (int i = 0; i < rowAccount; i++) {
            char[] rowChar = new char[lowAccount];
            for (int j = 0; j < lowAccount; j++) {
                rowChar[j] = plaintextGroup[i][j];
            }
            ciphertext = ciphertext + new String(rowChar);
        }

        return ciphertext;
    }

    private static String getPlaintextFromGroup(char[][] ciphertextGroup) {
        String plaintext = "";

        int rowAccount = ciphertextGroup.length;
        int lowAccount = ciphertextGroup[0].length;

        for (int i = 0; i < rowAccount; i++) {
            char[] rowChar = new char[lowAccount];
            for (int j = 0; j < lowAccount; j++) {
                rowChar[j] = ciphertextGroup[i][j];
            }
            plaintext = plaintext + new String(rowChar);
        }

        return plaintext;
    }

    /**
     * 对明文进行加密
     *
     * @param data   明文
     * @param keyStr 密钥
     * @return 加密后的数据
     */
    public static String encrypt(String data, String keyStr) {

        if (data == null || keyStr == null)
            return null;

        char[] plaintext = data.toCharArray();

        char[][] plaintextGroup;
        Integer key = Integer.parseInt(keyStr);
        // 调用groupPlaintext(plaintext, key)方法对明文分组
        plaintextGroup = groupPlaintext(plaintext, key);
        for (int i = 0; i < plaintextGroup.length; i++) {
            // 调用changePlaintext(char[])进行位变换
            char[] rowText = changePlaintext(plaintextGroup[i]);
            plaintextGroup[i] = rowText;
        }
        String ciphertext = getCipherFromGroup(plaintextGroup);
        Integer length = data.length();
        if (length % key != 0) {
            ciphertext = ciphertext + data.substring(length / key * key);
        }
        return ciphertext;
    }

    /**
     * 对密文进行解密
     *
     * @param data   密文
     * @param keyStr 密钥
     * @return 解密后的数据
     */
    public static String decrypt(String data, String keyStr) {

        if (data == null || keyStr == null)
            return null;

        Integer key = Integer.parseInt(keyStr);
        char[] ciphertext = data.toCharArray();
        String plaintextStr;
        char[][] ciphertextGroup;

        ciphertextGroup = groupCiphertext(ciphertext, key);
        for (int i = 0; i < ciphertextGroup.length; i++) {
            char[] rowText = changeCiphertext(ciphertextGroup[i]);
            ciphertextGroup[i] = rowText;
        }

        plaintextStr = getPlaintextFromGroup(ciphertextGroup);

        Integer length = data.length();
        if (length % key != 0) {
            plaintextStr = plaintextStr + data.substring(length / key * key);
        }

        return plaintextStr;
    }

    public static void main(String[] args) throws IOException {
        TranspositionHelp bean = new TranspositionHelp();
        String plaintext = "{'我的家r':'ert','2':'dff哈哈'}";
        String key = "8";
        String ciphertext = bean.encrypt(plaintext, key);
        System.out.println("明文加密后为: " + ciphertext);

        String plaintextStr = bean.decrypt(ciphertext, key);
        System.out.println("密文解密后为: " + plaintextStr);
    }
}