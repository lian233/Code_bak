/**
 *
 * FileName：MD5Util.java
 *
 * Description：MD5校验码生成工�?
 */

package com.wofu.ecommerce.rke2.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.wofu.common.tools.util.log.Log;

/**
 * MD5������
 * 
 */
public class Md5Util {
	/**
	 * 生成md5校验�?
	 * 
	 * @param srcContent
	 *            �?要加密的数据
	 * @return 加密后的md5校验码�?�出错则返回null�?
	 */
	public static String makeMd5Sum(byte[] srcContent) {
		if (srcContent == null) {
			return null;
		}

		String strDes = null;

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(srcContent);
			strDes = bytes2Hex(md5.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return strDes;
	}

	private static String bytes2Hex(byte[] byteArray) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] >= 0 && byteArray[i] < 16) {
				strBuf.append("0");
			}
			String hex = Integer.toHexString(byteArray[i] & 0xFF);
			strBuf.append(hex.toLowerCase());
		}
		return strBuf.toString();
	}

	/**
	 * 新的md5签名，首尾放secret�?
	 * 
	 * @param params
	 *            传给服务器的参数
	 * 
	 * @param secret
	 *            分配给您的APP_SECRET
	 */
	public static String md5Signature(Map<String, String> params,
			String secret,String method) {
		String result = null;
		String orgin = getBeforeSign(params,secret,method);
		if (orgin == null)
			return result;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = bytes2Hex(md.digest(orgin.toString().getBytes("gb2312")));
		} catch (Exception e) {
			throw new java.lang.RuntimeException("sign error !");
		}
		return result;
	}

	/**
	 * 
	 * ƴװ������
	 * @param params
	 * @param orgin
	 * @return
	 */
	private static String getBeforeSign(Map<String, String> params,String secret,String method) {
		if (params == null)
			return null;
		StringBuilder sb = new StringBuilder(secret).append(method);
		
		Iterator<String> iter = params.keySet().iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			if("method".equals(name)|| "user".equals(name)|| "format".equals(name)){
				continue;
			}else{
				sb.append(name).append(params.get(name));
			}
		}
		
		char[] temp = sb.toString().replaceAll(" ", "").toLowerCase().toCharArray();
		Arrays.sort(temp);
		sb.delete(0, sb.length());
		for(char e:temp){
			sb.append(e);
		}
		return sb.toString();
	}

}
