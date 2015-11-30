package com.wofu.intf.yjn;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import com.wofu.common.tools.util.log.Log;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @Filename DigestUtil.java
 * @Description 签名工具 
 * @Version 1.0z
 * @Author bohr
 * @Email qzhanbo@yiji.com
 * @History <li>Author: bohr.qiu</li> <li>Date: 2012-10-15</li> <li>Version: 1.0
 * </li> <li>Content: create</li>
 */
public class DigestUtil {
	
	
	private static final String TIME_ZONE = "UTC";
	private static final String UTC_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
	/**
	 * 签名编码
	 */
	public static final String UTF8 = "utf-8";
	/**
	 * 签名key
	 */
	public static final String SIGN_KEY = "sign";
	
	/**
	 * 渠道id key
	 */
	public static final String CHANNEL_ID_KEY = "channelId";
	
	/**
	 * 签名类型key，支持DigestALGEnum
	 */
	public static final String SIGN_TYPE_KEY = "signType";
	
	/**
	 * utc时间key
	 */
	public static final String TIMESTAMP_KEY = "utc_time_stamp";
	
	/**
	 * 签名算法
	 *
	 * @Filename DigestUtil.java
	 * @Description
	 * @Version 1.0
	 * @Author bohr.qiu
	 * @Email qzhanbo@yiji.com
	 * @History <li>Author: bohr.qiu</li> <li>Date: 2013-1-5</li> <li>Version:
	 * 1.0</li> <li>Content: create</li>
	 */
	public static enum DigestALGEnum {
		SHA256("SHA-256"),
		MD5("MD5");
		private String name;
		
		DigestALGEnum(String name) {
			this.name = name;
		}
		
		public static DigestALGEnum getByName(String name) {
			for (DigestALGEnum _enum : values()) {
				if (_enum.getName().equals(name)) {
					return _enum;
				}
			}
			return null;
		}
		
		public String getName() {
			return name;
		}
	}
	
	/**
	 * 以Map中key的字符顺序排序后签名，如果secretKey不为空，排在 后面签名 <br/>
	 * 比如：Map中 ?如下：<br/>
	 * keyA=valueA<br/>
	 * keyB=valueB<br/>
	 * keyA1=valueA1<br/>
	 * <br/>
	 * security_check_code为yjf<br/>
	 * <p/>
	 * 待签名字符串为：<br/>
	 * keyA=valueA&keyA1=valueA1&keyB=valueByjf<br/>
	 * <b>注意:</b>SIGN_KEY不会被签 
	 *
	 * @param dataMap
	 * @param securityCheckKey 密钥
	 * @param de 摘要算法
	 * @return
	 */
	public static <T> String digest(Map<String, T> dataMap, String securityCheckKey,
									DigestALGEnum de) {
		return digest(dataMap, securityCheckKey, de, UTF8);
	}
	
	/**
	 * 签名并带上时间戳
	 *
	 * @param dataMap
	 * @param securityCheckKey
	 * @param de
	 * @param charset
	 * @return
	 */
	public static String digestWithTimeStamp(Map<String, Object> dataMap, String securityCheckKey,
												DigestALGEnum de, String charset) {
		dataMap.put(TIMESTAMP_KEY, getUTCTime());
		return digest(dataMap, securityCheckKey, de, charset);
	}
	
	/**
	 * 签名并带上时间戳，使用utf-8编码
	 *
	 * @param dataMap
	 * @param securityCheckKey
	 * @param de
	 * @return
	 */
	public static String digestWithTimeStamp(Map<String, Object> dataMap, String securityCheckKey,
												DigestALGEnum de) {
		return digestWithTimeStamp(dataMap, securityCheckKey, de, UTF8);
	}
	
	/**
	 * 以Map中key的字符顺序排序后签名，如果secretKey不为空，排在 后面签名 <br/>
	 * 比如：Map中如下：<br/>
	 * keyA=valueA<br/>
	 * keyB=valueB<br/>
	 * keyA1=valueA1<br/>
	 * <br/>
	 * security_check_code为yjf<br/>
	 * <p/>
	 * 待签名字符串为：<br/>
	 * keyA=valueA&keyA1=valueA1&keyB=valueByjf<br/>
	 * <b>注意:</b>SIGN_KEY不会被签 
	 *
	 * @param dataMap
	 * @param securityCheckKey 密钥
	 * @param de 摘要算法
	 * @return
	 */
	public static <T> String digest(Map<String, T> dataMap, String securityCheckKey,
									DigestALGEnum de, String encoding) {
		if (dataMap == null) {
			throw new IllegalArgumentException("数据不能为空");
		}
		if (dataMap.isEmpty()) {
			return null;
		}
		if (securityCheckKey == null) {
			throw new IllegalArgumentException("安全校验码数据不能为空");
		}
		if (de == null) {
			throw new IllegalArgumentException("摘要算法不能为空");
		}
		if (StringUtils.isBlank(encoding)) {
			throw new IllegalArgumentException("字符集不能为空");
		}
		
		TreeMap<String, T> treeMap = new TreeMap<String, T>(dataMap);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, T> entry : treeMap.entrySet()) {
			if (entry.getValue() == null) {
				throw new IllegalArgumentException(entry.getKey() + " 待签名不能为空");
			}
			if (entry.getKey().equals(SIGN_KEY)) {
				continue;
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue().toString()).append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		sb.append(securityCheckKey);
		
		byte[] toDigest;
		try {
			String str = sb.toString();
			toDigest = str.getBytes(encoding);
			Log.info("待签名url:" + str);
			MessageDigest md = MessageDigest.getInstance(de.getName());
			md.update(toDigest);
			return new String(Hex.encodeHex(md.digest()));
		} catch (Exception e) {
			throw new RuntimeException("签名失败", e);
		}
	}
	
	private static String getSign(Map<String, ?> params) {
		Object para = params.get(SIGN_KEY);
		if (para == null) {
			throw new IllegalArgumentException(SIGN_KEY + "不能为空");
		}
		return StringUtils.trimToEmpty(para.toString());
	}
	
	private static String getParameter(HttpServletRequest request, String parameter) {
		String para = request.getParameter(parameter);
		if (para == null) {
			throw new IllegalArgumentException(parameter + "不能为空");
		}
		return StringUtils.trimToEmpty(para);
	}
	
	/**
	 * 从request中获取待签名数据，校验签名是否正确
	 *
	 * @param request
	 * @param securityCheckKey
	 * @param de 签名算法,如果request中有SIGN_TYPE_KEY，则以SIGN_TYPE_KEY指定的摘要算法摘 
	 * @param charset 字符 
	 * @param expireTime 过期时间
	 * @param timeUnit 过期时间单位
	 */
	public static void checkWithTimestamp(HttpServletRequest request, String securityCheckKey,
											DigestALGEnum de, String charset, long expireTime,
											TimeUnit timeUnit) {
		String timestamp = getParameter(request, TIMESTAMP_KEY);
		checkTimeout(timestamp, expireTime, timeUnit);
		check(request, securityCheckKey, de, charset);
	}
	
	/**
	 * 从request中获取待签名数据，校验签名是否正 
	 *
	 * @param request
	 * @param securityCheckKey
	 * @param de 签名算法,如果request中有，则以SIGN_TYPE_KEY指定的摘要算法摘要
	 * @param charset 编码
	 * @return
	 */
	public static void check(HttpServletRequest request, String securityCheckKey, DigestALGEnum de,
								String charset) {
		if (securityCheckKey == null) {
			throw new IllegalArgumentException("安全校验码不能为空");
		}
		if (request == null) {
			throw new IllegalArgumentException("request对象不能为空");
		}
		String signType = request.getParameter(SIGN_TYPE_KEY);
		signType = (signType == null) ? de.getName() : signType;
		if (DigestALGEnum.getByName(signType) == null) {
			throw new IllegalArgumentException("不支持的摘要算法类型:" + signType);
		}
		String sign = getParameter(request, SIGN_KEY);
		@SuppressWarnings("unchecked")
		TreeMap<String, String[]> treeMap = new TreeMap<String, String[]>(request.getParameterMap());
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String[]> entry : treeMap.entrySet()) {
			if (entry.getValue() == null) {
				throw new IllegalArgumentException(entry.getKey() + " 待签名不能为空");
			}
			if (entry.getKey().equals(SIGN_KEY)) {
				continue;
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue()[0]).append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		sb.append(securityCheckKey);
		
		byte[] toDigest;
		String digest;
		try {
			String str = sb.toString();
			toDigest = str.getBytes(charset);
			Log.info("待签名url:" + str);
			MessageDigest md = MessageDigest.getInstance(signType);
			md.update(toDigest);
			digest = new String(Hex.encodeHex(md.digest()));
		} catch (Exception e) {
			throw new RuntimeException("签名失败", e);
		}
		if (!sign.equals(digest)) {
			Log.info("签名摘要计算结果：{}", digest);
			throw new RuntimeException("签名校验失败");
		}
	}
	
	/**
	 * 从request中获取待签名数据，校验签名是否正 
	 *
	 * @param params 请求参数对象
	 * @param securityCheckKey
	 * @param de 签名算法,如果request中有SIGN_TYPE_KEY，则以SIGN_TYPE_KEY指定的摘要算法摘 
	 * @param charset 编码
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static void check(Map<String, ?> params, String securityCheckKey, DigestALGEnum de,
								String charset) {
		if (securityCheckKey == null) {
			throw new IllegalArgumentException("安全校验码不能为空");
		}
		if (params == null) {
			throw new IllegalArgumentException("params对象不能为空");
		}
		if (de == null) {
			throw new IllegalArgumentException("DigestALGEnum对象不能为空");
		}
		
		Object signTypeObj = params.get(SIGN_TYPE_KEY);
		String signType = (signTypeObj == null) ? de.getName() : signTypeObj.toString();
		if (DigestALGEnum.getByName(signType) == null) {
			throw new IllegalArgumentException("不支持的摘要算法类型:" + signType);
		}
		String sign = getSign(params);
		@SuppressWarnings("unchecked")
		TreeMap<String, ?> treeMap = new TreeMap(params);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, ?> entry : treeMap.entrySet()) {
			if (entry.getValue() == null) {
				throw new IllegalArgumentException(entry.getKey() + " 待签名不能为空");
			}
			
			if (entry.getKey().equals(SIGN_KEY)) {
				continue;
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		sb.append(securityCheckKey);
		
		byte[] toDigest;
		String digest;
		try {
			String str = sb.toString();
			toDigest = str.getBytes(charset == null ? UTF8 : charset);
			Log.info("待签名url:" + str);
			MessageDigest md = MessageDigest.getInstance(signType);
			md.update(toDigest);
			digest = new String(Hex.encodeHex(md.digest()));
		} catch (Exception e) {
			throw new RuntimeException("签名失败", e);
		}
		if (!sign.equals(digest)) {
			Log.info("签名摘要计算结果：{}", digest);
			throw new RuntimeException("签名校验失败");
		}
	}
	
	/**
	 * @param request
	 * @param securityCheckKey
	 * @param de
	 * @return
	 */
	public static void check(HttpServletRequest request, String securityCheckKey, DigestALGEnum de) {
		check(request, securityCheckKey, de, UTF8);
	}
	
	public static <T> String digest(Map<String, T> data, DigestALGEnum de) {
		return digest(data, null, de);
	}
	
	public static <T> String digestWithSHA256(Map<String, T> data) {
		return digest(data, null, DigestALGEnum.SHA256);
	}
	
	public static <T> String digestWithSHA256(Map<String, T> data, String securityCheckKey) {
		return digest(data, securityCheckKey, DigestALGEnum.SHA256);
	}
	
	public static <T> String digestWithMD5(Map<String, T> data) {
		return digest(data, null, DigestALGEnum.MD5);
	}
	
	public static <T> String digestWithMD5(Map<String, T> data, String securityCheckKey) {
		return digest(data, securityCheckKey, DigestALGEnum.MD5);
	}
	
	/**
	 * 获得utc时间
	 *
	 * @return
	 */
	public static String getUTCTime() {
		DateFormat utcFormat = new SimpleDateFormat(UTC_TIME_FORMAT);
		TimeZone utcTime = TimeZone.getTimeZone(TIME_ZONE);
		utcFormat.setTimeZone(utcTime);
		Calendar calendar = java.util.Calendar.getInstance();
		return utcFormat.format(calendar.getTime());
	}
	
	/**
	 * 验证请求是否超时
	 *
	 * @param timestamp 待验证的时间
	 * @param expireTime 时长
	 * @param timeUnit 时间单位
	 */
	public static void checkTimeout(String timestamp, long expireTime, TimeUnit timeUnit) {
		DateFormat utcFormat = new SimpleDateFormat(UTC_TIME_FORMAT);
		TimeZone utcTime = TimeZone.getTimeZone(TIME_ZONE);
		utcFormat.setTimeZone(utcTime);
		Date dt = null;
		try {
			dt = utcFormat.parse(timestamp);
			
		} catch (ParseException e) {
			throw new RuntimeException("时间格式异常", e);
		}
		Date now = new Date();
		if (now.getTime() - dt.getTime() > timeUnit.toMillis(expireTime)) {
			throw new RuntimeException("请求已过");
		}
	}
}
