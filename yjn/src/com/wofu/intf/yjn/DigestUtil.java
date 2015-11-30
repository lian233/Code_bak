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
 * @Description ǩ������ 
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
	 * ǩ������
	 */
	public static final String UTF8 = "utf-8";
	/**
	 * ǩ��key
	 */
	public static final String SIGN_KEY = "sign";
	
	/**
	 * ����id key
	 */
	public static final String CHANNEL_ID_KEY = "channelId";
	
	/**
	 * ǩ������key��֧��DigestALGEnum
	 */
	public static final String SIGN_TYPE_KEY = "signType";
	
	/**
	 * utcʱ��key
	 */
	public static final String TIMESTAMP_KEY = "utc_time_stamp";
	
	/**
	 * ǩ���㷨
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
	 * ��Map��key���ַ�˳�������ǩ�������secretKey��Ϊ�գ����� ����ǩ�� <br/>
	 * ���磺Map�� ?���£�<br/>
	 * keyA=valueA<br/>
	 * keyB=valueB<br/>
	 * keyA1=valueA1<br/>
	 * <br/>
	 * security_check_codeΪyjf<br/>
	 * <p/>
	 * ��ǩ���ַ���Ϊ��<br/>
	 * keyA=valueA&keyA1=valueA1&keyB=valueByjf<br/>
	 * <b>ע��:</b>SIGN_KEY���ᱻǩ 
	 *
	 * @param dataMap
	 * @param securityCheckKey ��Կ
	 * @param de ժҪ�㷨
	 * @return
	 */
	public static <T> String digest(Map<String, T> dataMap, String securityCheckKey,
									DigestALGEnum de) {
		return digest(dataMap, securityCheckKey, de, UTF8);
	}
	
	/**
	 * ǩ��������ʱ���
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
	 * ǩ��������ʱ�����ʹ��utf-8����
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
	 * ��Map��key���ַ�˳�������ǩ�������secretKey��Ϊ�գ����� ����ǩ�� <br/>
	 * ���磺Map�����£�<br/>
	 * keyA=valueA<br/>
	 * keyB=valueB<br/>
	 * keyA1=valueA1<br/>
	 * <br/>
	 * security_check_codeΪyjf<br/>
	 * <p/>
	 * ��ǩ���ַ���Ϊ��<br/>
	 * keyA=valueA&keyA1=valueA1&keyB=valueByjf<br/>
	 * <b>ע��:</b>SIGN_KEY���ᱻǩ 
	 *
	 * @param dataMap
	 * @param securityCheckKey ��Կ
	 * @param de ժҪ�㷨
	 * @return
	 */
	public static <T> String digest(Map<String, T> dataMap, String securityCheckKey,
									DigestALGEnum de, String encoding) {
		if (dataMap == null) {
			throw new IllegalArgumentException("���ݲ���Ϊ��");
		}
		if (dataMap.isEmpty()) {
			return null;
		}
		if (securityCheckKey == null) {
			throw new IllegalArgumentException("��ȫУ�������ݲ���Ϊ��");
		}
		if (de == null) {
			throw new IllegalArgumentException("ժҪ�㷨����Ϊ��");
		}
		if (StringUtils.isBlank(encoding)) {
			throw new IllegalArgumentException("�ַ�������Ϊ��");
		}
		
		TreeMap<String, T> treeMap = new TreeMap<String, T>(dataMap);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, T> entry : treeMap.entrySet()) {
			if (entry.getValue() == null) {
				throw new IllegalArgumentException(entry.getKey() + " ��ǩ������Ϊ��");
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
			Log.info("��ǩ��url:" + str);
			MessageDigest md = MessageDigest.getInstance(de.getName());
			md.update(toDigest);
			return new String(Hex.encodeHex(md.digest()));
		} catch (Exception e) {
			throw new RuntimeException("ǩ��ʧ��", e);
		}
	}
	
	private static String getSign(Map<String, ?> params) {
		Object para = params.get(SIGN_KEY);
		if (para == null) {
			throw new IllegalArgumentException(SIGN_KEY + "����Ϊ��");
		}
		return StringUtils.trimToEmpty(para.toString());
	}
	
	private static String getParameter(HttpServletRequest request, String parameter) {
		String para = request.getParameter(parameter);
		if (para == null) {
			throw new IllegalArgumentException(parameter + "����Ϊ��");
		}
		return StringUtils.trimToEmpty(para);
	}
	
	/**
	 * ��request�л�ȡ��ǩ�����ݣ�У��ǩ���Ƿ���ȷ
	 *
	 * @param request
	 * @param securityCheckKey
	 * @param de ǩ���㷨,���request����SIGN_TYPE_KEY������SIGN_TYPE_KEYָ����ժҪ�㷨ժ 
	 * @param charset �ַ� 
	 * @param expireTime ����ʱ��
	 * @param timeUnit ����ʱ�䵥λ
	 */
	public static void checkWithTimestamp(HttpServletRequest request, String securityCheckKey,
											DigestALGEnum de, String charset, long expireTime,
											TimeUnit timeUnit) {
		String timestamp = getParameter(request, TIMESTAMP_KEY);
		checkTimeout(timestamp, expireTime, timeUnit);
		check(request, securityCheckKey, de, charset);
	}
	
	/**
	 * ��request�л�ȡ��ǩ�����ݣ�У��ǩ���Ƿ��� 
	 *
	 * @param request
	 * @param securityCheckKey
	 * @param de ǩ���㷨,���request���У�����SIGN_TYPE_KEYָ����ժҪ�㷨ժҪ
	 * @param charset ����
	 * @return
	 */
	public static void check(HttpServletRequest request, String securityCheckKey, DigestALGEnum de,
								String charset) {
		if (securityCheckKey == null) {
			throw new IllegalArgumentException("��ȫУ���벻��Ϊ��");
		}
		if (request == null) {
			throw new IllegalArgumentException("request������Ϊ��");
		}
		String signType = request.getParameter(SIGN_TYPE_KEY);
		signType = (signType == null) ? de.getName() : signType;
		if (DigestALGEnum.getByName(signType) == null) {
			throw new IllegalArgumentException("��֧�ֵ�ժҪ�㷨����:" + signType);
		}
		String sign = getParameter(request, SIGN_KEY);
		@SuppressWarnings("unchecked")
		TreeMap<String, String[]> treeMap = new TreeMap<String, String[]>(request.getParameterMap());
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String[]> entry : treeMap.entrySet()) {
			if (entry.getValue() == null) {
				throw new IllegalArgumentException(entry.getKey() + " ��ǩ������Ϊ��");
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
			Log.info("��ǩ��url:" + str);
			MessageDigest md = MessageDigest.getInstance(signType);
			md.update(toDigest);
			digest = new String(Hex.encodeHex(md.digest()));
		} catch (Exception e) {
			throw new RuntimeException("ǩ��ʧ��", e);
		}
		if (!sign.equals(digest)) {
			Log.info("ǩ��ժҪ��������{}", digest);
			throw new RuntimeException("ǩ��У��ʧ��");
		}
	}
	
	/**
	 * ��request�л�ȡ��ǩ�����ݣ�У��ǩ���Ƿ��� 
	 *
	 * @param params �����������
	 * @param securityCheckKey
	 * @param de ǩ���㷨,���request����SIGN_TYPE_KEY������SIGN_TYPE_KEYָ����ժҪ�㷨ժ 
	 * @param charset ����
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static void check(Map<String, ?> params, String securityCheckKey, DigestALGEnum de,
								String charset) {
		if (securityCheckKey == null) {
			throw new IllegalArgumentException("��ȫУ���벻��Ϊ��");
		}
		if (params == null) {
			throw new IllegalArgumentException("params������Ϊ��");
		}
		if (de == null) {
			throw new IllegalArgumentException("DigestALGEnum������Ϊ��");
		}
		
		Object signTypeObj = params.get(SIGN_TYPE_KEY);
		String signType = (signTypeObj == null) ? de.getName() : signTypeObj.toString();
		if (DigestALGEnum.getByName(signType) == null) {
			throw new IllegalArgumentException("��֧�ֵ�ժҪ�㷨����:" + signType);
		}
		String sign = getSign(params);
		@SuppressWarnings("unchecked")
		TreeMap<String, ?> treeMap = new TreeMap(params);
		StringBuilder sb = new StringBuilder();
		for (Entry<String, ?> entry : treeMap.entrySet()) {
			if (entry.getValue() == null) {
				throw new IllegalArgumentException(entry.getKey() + " ��ǩ������Ϊ��");
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
			Log.info("��ǩ��url:" + str);
			MessageDigest md = MessageDigest.getInstance(signType);
			md.update(toDigest);
			digest = new String(Hex.encodeHex(md.digest()));
		} catch (Exception e) {
			throw new RuntimeException("ǩ��ʧ��", e);
		}
		if (!sign.equals(digest)) {
			Log.info("ǩ��ժҪ��������{}", digest);
			throw new RuntimeException("ǩ��У��ʧ��");
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
	 * ���utcʱ��
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
	 * ��֤�����Ƿ�ʱ
	 *
	 * @param timestamp ����֤��ʱ��
	 * @param expireTime ʱ��
	 * @param timeUnit ʱ�䵥λ
	 */
	public static void checkTimeout(String timestamp, long expireTime, TimeUnit timeUnit) {
		DateFormat utcFormat = new SimpleDateFormat(UTC_TIME_FORMAT);
		TimeZone utcTime = TimeZone.getTimeZone(TIME_ZONE);
		utcFormat.setTimeZone(utcTime);
		Date dt = null;
		try {
			dt = utcFormat.parse(timestamp);
			
		} catch (ParseException e) {
			throw new RuntimeException("ʱ���ʽ�쳣", e);
		}
		Date now = new Date();
		if (now.getTime() - dt.getTime() > timeUnit.toMillis(expireTime)) {
			throw new RuntimeException("�����ѹ�");
		}
	}
}
