package com.ztesoft.level1.util.encrypt;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
/***
 * des加密解密
 * @author wangsq
 *
 */
public class DESHelp {
	final String languageCode="utf-8";
	private static DESHelp des_=null;
	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };
	public static DESHelp getInstance(){
		if(des_==null){
			des_=new DESHelp();
		}
		return des_;
	}
	public byte[] desEncrypt(byte[] plainText,byte[] desKey) throws Exception {   
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(desKey, "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);  
		byte data[] = plainText;   
		byte encryptedData[] = cipher.doFinal(data);   
		return encryptedData;   
	}   

	public byte[] desDecrypt(byte[] encryptText,byte[] desKey) throws Exception {   
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(desKey, "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv); 
		byte encryptedData[] = encryptText;   
		byte decryptedData[] = cipher.doFinal(encryptedData);   
		return decryptedData;   
	}   

	public String encrypt(String input,String desKey) throws Exception {   
		Base64 base=new Base64();
		return new String(base.encode(desEncrypt(input.getBytes(languageCode),desKey.getBytes())),languageCode);   
	}   

	public String decrypt(String input,String desKey) throws Exception {  
		Base64 base=new Base64();
		byte[] result = base.decode(input.getBytes());   
		return new String(desDecrypt(result,desKey.getBytes()),languageCode);   
	}   
	public static void main(String[] args) throws Exception {   
		String key = "20140422";   
		String input = "{\"funcInfo\":}";   
		DESHelp crypt = new DESHelp();   
		System.out.println("Encode:" + crypt.encrypt(input,key));   
		System.out.println("Decode:" + crypt.decrypt(crypt.encrypt(input,key),key));   
	}   

}
