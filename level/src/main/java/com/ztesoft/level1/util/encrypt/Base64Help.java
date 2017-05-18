package com.ztesoft.level1.util.encrypt;

import org.apache.commons.codec.binary.Base64;

public class Base64Help {
	final String languageCode="utf-8";
	private static Base64Help des_=null;
	public static Base64Help getInstance(){
		if(des_==null){
			des_=new Base64Help();
		}
		return des_;
	}
	
	public String encrypt(String input) throws Exception {   
		Base64 base=new Base64();
		return new String(base.encode(input.getBytes(languageCode)),languageCode);   
	}   

	public String decrypt(String input) throws Exception {  
		Base64 base=new Base64();
		byte[] result = base.decode(input.getBytes(languageCode));   
		return new String(result,languageCode);   
	}
	
	public static void main(String[] args) throws Exception {   
//		String key = "20140422";   
		String input = "{\"funcInfo哈哈\":}";   
		Base64Help crypt = new Base64Help();   
		System.out.println("Encode:" + crypt.encrypt(input));   
		System.out.println("Decode:" + crypt.decrypt(crypt.encrypt(input)));   
	} 
}
