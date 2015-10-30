/**
 *
 * FileName：MD5Util.java
 *
 * Description：MD5校验码生成工�?
 */

package com.wofu.ecommerce.jit.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * MD5校验码生成工�?
 * 
 */
public class Md5Util {
	
	public static String createRequestSign(Map<String,String> map,String request,String appSecret) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        builder.append("appKey").append(map.get("appKey"));
        builder.append("format").append(map.get("format"));
        builder.append("method").append(map.get("method"));
        builder.append("service").append(map.get("service"));
        builder.append("timestamp").append(map.get("timestamp"));
        builder.append("version").append(map.get("version"));
        builder.append(request);
       // System.out.println(builder.toString());
        try
        {
        	return byte2hex(encryptHMAC(builder.toString(), appSecret));
        }
       
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
	
	public static String byte2hex(byte bytes[])
    {
        StringBuilder sign = new StringBuilder();
        for(int i = 0; i < bytes.length; i++)
        {
            sign.append("0123456789ABCDEF".charAt((bytes[i] & 240) >> 4));
            sign.append("0123456789ABCDEF".charAt(bytes[i] & 15));
        }

        return sign.toString();
    }
	
	
	 public static byte[] encryptHMAC(String data, String secret)
     throws IOException
 {
     byte bytes[] = null;
     try
     {
         SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacMD5");
         Mac mac = Mac.getInstance(secretKey.getAlgorithm());
         mac.init(secretKey);
         bytes = mac.doFinal(data.getBytes("UTF-8"));
     }
     catch(GeneralSecurityException ex)
     {
         ex.printStackTrace();
     }
     return bytes;
 }

}
