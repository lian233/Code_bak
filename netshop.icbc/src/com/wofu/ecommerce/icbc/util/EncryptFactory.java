package com.wofu.ecommerce.icbc.util;

public class EncryptFactory {

	/**
	 * 获得加密算法
	 * 
	 * @param encryptName 算法名称
	 * @return 
	 * @throws Exception
	 */
	public static Encrypt getEncrypt(String encryptName) throws Exception {
		Encrypt encrypt = null;
		if (null == encryptName || "HMACSHA256".equalsIgnoreCase(encryptName)) {
			encrypt = new HmacSHA256Encrypt();
		} else if ("SHA256".equals(encryptName)) {
			encrypt = new SHA256Encrypt();
		} else {
			throw new Exception("算法未定义！");
		}
		return encrypt;
	}

}
