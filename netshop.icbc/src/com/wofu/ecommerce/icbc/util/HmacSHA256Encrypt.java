package com.wofu.ecommerce.icbc.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;

/**
 * hmacSHA256加密
 * @author kfzx-wugang01
 * @version 
 * 		2013-9-25下午10:29:39.1
 */
public class HmacSHA256Encrypt extends Encrypt {
	
	@Override
	public String sign(String app_key, String auth_code, String req_data,
			String app_secret) throws Exception {
		String charset="UTF-8";
		String content="app_key="+app_key+"&auth_code="+auth_code+"&req_data="+req_data;
		//System.out.println(content);
		byte[] bytes = encryptHMAC(content.getBytes(charset), app_secret.getBytes(charset));
		BASE64Encoder be= new BASE64Encoder();
		return be.encode(bytes);//作为sign的密文内�?
	}

	public static final String KEY_MAC = "HmacSHA256";

	public byte[] encryptHMAC(byte[] data, byte[] key) throws Exception {
		SecretKey sk = new SecretKeySpec(key, KEY_MAC);
		Mac mac = Mac.getInstance(sk.getAlgorithm());
		mac.init(sk);
		return mac.doFinal(data);
	}
	
}
