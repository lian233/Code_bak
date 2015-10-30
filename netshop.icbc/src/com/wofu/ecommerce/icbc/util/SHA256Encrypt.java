package com.wofu.ecommerce.icbc.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class SHA256Encrypt extends Encrypt {
	private static final String MESSAGEDIGEST_ENCRYPT = "SHA-256";
	private static final String FORMATSTR = "app_secret=%1$s&app_key=%2$s&auth_code=%3$s&req_data=%4$s";
	@Override
	public String sign(String app_key, String auth_code, String req_data,
			String app_secret) throws Exception {

		String origin_content = String.format(FORMATSTR, app_secret, app_key, auth_code, req_data);

		String result = encrypt(origin_content);

		return result;
	}

	public String encrypt(String strSrc) throws UnsupportedEncodingException {
		MessageDigest md = null;
		String strDes = null;
		
		String charset="UTF-8";

		byte[] bt = strSrc.getBytes(charset);
		try {
			md = MessageDigest.getInstance(MESSAGEDIGEST_ENCRYPT);
			md.update(bt);
			strDes = bytes2Hex(md.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return strDes;
	}

	private String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

}
