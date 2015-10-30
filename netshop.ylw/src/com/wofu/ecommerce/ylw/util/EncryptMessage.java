
package com.wofu.ecommerce.ylw.util;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
public class EncryptMessage {

	public static final String MD5_CODE = "MD5";
	public static  final String SHA_CODE = "SHA";
	public static  final String SHA256_CODE = "SHA-256";
	public static  final String SHA512_CODE = "SHA-512";
	private static  final String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 字节数组转成二进制字符串
	 * @param byteArr
	 * @return
	 */
	private static String byteArrayToHexString(byte[] byteArr) {
		StringBuffer sb = new StringBuffer();
		for (byte b : byteArr) {
			sb.append(byteToHexString(b));
		}
		return sb.toString();
	}

	/**
	 * 字节转成二进制字符串
	 * @param b
	 * @return
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		return hexDigits[n / 16] + hexDigits[n % 16];
	}

	/**
	 * 加密
	 * @param code
	 * @param message
	 * @return
	 */
	private static String Encode(String codeFormat, String message) {
		MessageDigest md;
		String encode = null;
		try {
			md = MessageDigest.getInstance(codeFormat);
			encode = byteArrayToHexString(md.digest(message.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return encode;
	}

	/**
	 * @param message
	 * @return
	 */
	public static String encryptMessage(String codeFormat, String message) {
		return Encode(codeFormat, message);
	}

	/**
	 * 验证数据
	 * @param code
	 * @param tag
	 * @return
	 */
	public static boolean validate(String code, String tag) {
		if (code.equals(tag))
			return true;
		return false;
	}

	/**
	 * Base64加密
	 * @param toEncodeContent
	 * @return
	 */
	public static String base64Encode(String toEncodeContent) {
		if (toEncodeContent == null) {
			return null;
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(toEncodeContent.getBytes());
	}

	public static String base64Encode(byte[] toEncodeContent) {
		return base64Encode(new String(toEncodeContent));
	}

	/**
	 * Base64解密
	 * @param toDecodeContent
	 * @return
	 */
	public String base64Decode(String toDecodeContent) {
		if (toDecodeContent == null) {
			return null;
		}
		byte[] buf = null;
		try {
			buf = new BASE64Decoder().decodeBuffer(toDecodeContent);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return String.valueOf(buf);
	}

}
