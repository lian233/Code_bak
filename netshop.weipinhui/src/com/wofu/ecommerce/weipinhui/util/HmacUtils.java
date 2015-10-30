package com.wofu.ecommerce.weipinhui.util;

import java.io.IOException;
import java.security.GeneralSecurityException;

//import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * hmac公用方法类
 * @author weihui.tang
 *
 */
public class HmacUtils {
	public static final String CHARSET_UTF8 = "UTF-8";
	
	public static final String KEY_MAC = "HmacMD5";
	
	private static final String HEXSTR =  "0123456789ABCDEF";
	
	private static String[] binaryArray =   
        {"0000","0001","0010","0011",  
        "0100","0101","0110","0111",  
        "1000","1001","1010","1011",  
        "1100","1101","1110","1111"}; 
	
	
	/**
	 * hmac-md5签名
	 * @param data
	 * @param secret
	 * @return
	 * @throws IOException
	 */
	public static byte[] encryptHMAC(String data, String secret) throws IOException {
		byte[] bytes = null;
		try {
			SecretKey secretKey = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), KEY_MAC);
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			bytes = mac.doFinal(data.getBytes(CHARSET_UTF8));
		} catch (GeneralSecurityException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}
	
	/** 
	 * TODO 性能优化
	 * 将byte转化为十六进制字符串
	 * @param bytes
	 * @return
	 */
	public static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sign.append(HEXSTR.charAt((bytes[i]&0xF0)>>4)); //字节高4位  
			sign.append(HEXSTR.charAt(bytes[i]&0x0F));  //字节低4位  
		}
		return sign.toString();
	}
	
	 /** 
     * TODO 性能优化
     * @param hexString 
     * @return 将十六进制转换为字节数组 
     */  
    public static byte[] hexToBinary(String hexString){
    	if(hexString.length() % 2 != 0)
    		throw new IllegalArgumentException("input length must be even");
    	
        int len = hexString.length()/2;  
        byte[] bytes = new byte[len];  
		for (int i = 0; i < len; i++) {  
			char h = hexString.charAt(2*i);
			char l = hexString.charAt(2*i+1);
			int high = (h >= 'a') ? (h-'a'+10) : ((h >= 'A') ? h - 'A' + 10 : h - '0');
			int low = (l >= 'a') ? (l -'a'+10) : (l >= 'A') ? h - 'A' + 10 : l - '0';
            bytes[i] = (byte) ((high << 4)|low);//高地位做或运算  
        }  
        return bytes;  
    }  
    
    /** 
     *  
     * @param str 
     * @return 转换为二进制字符串 
     */  
    public static String bytes2BinaryStr(byte[] bArray){  
        String outStr = "";  
		for (byte b : bArray) {
			// 高四位
			int pos = (b & 0xF0) >> 4;
			outStr += binaryArray[pos];
			// 低四位
			pos = b & 0x0F;
			outStr += binaryArray[pos];
		}
        return outStr;  
          
    }  
}
