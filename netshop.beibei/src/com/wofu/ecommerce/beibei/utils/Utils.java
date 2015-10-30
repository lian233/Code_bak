package com.wofu.ecommerce.beibei.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.HttpClient;

import com.wofu.common.tools.util.log.Log;

public class Utils {
	
	public static String sendByPost(Map<String, String> appParamMap, String secret, String urlStr ) {
		BufferedReader reader=null;
		InputStream inputStream=null;
		HttpClient httpClient=null;
		try {
			httpClient = new DefaultHttpClient();
			
			HttpPost httpPost = new HttpPost(urlStr);

			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			if (appParamMap != null) {
				treeMap.putAll(appParamMap);
			}
			String sign = Utils.getSign(appParamMap, secret);
			treeMap.put("sign", sign);
//			System.out.println("����\n"+treeMap);
			Iterator<String> iterator = treeMap.keySet().iterator();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				params.add(new BasicNameValuePair(key, treeMap.get(key)));
			}
//			System.out.println("params \n"+params);
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(uefEntity);

			HttpResponse response = httpClient.execute(httpPost);
			
			HttpEntity httpEntity = response.getEntity();
			inputStream = httpEntity.getContent();
			//��ȡ���ص�������Ϣ
			StringBuffer postResult = new StringBuffer();
			String readLine = null ;
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((readLine = reader.readLine()) != null) {
				postResult.append(readLine);
			}

			httpClient.getConnectionManager().shutdown();
			return postResult.toString();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
				}
				httpClient.getConnectionManager().shutdown();
			}
			return null;
	}
	
	//��������ƴ��
	public static StringBuffer splicing(Map<String, String> orderlistparams) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		//��treeMap����
		treeMap.putAll(orderlistparams);
		Set <String> set = treeMap.keySet();
		StringBuffer merge = new StringBuffer();
		for (String s : set){
			if(s.equals("secret")){
				continue;
			}
			String key = s;
			String value = treeMap.get(key);
			merge.append(key).append(value);
//			System.out.println("key  "+key +" value  "+ value);
		}
		return merge;
	}
	
	//MD5�����㷨
    public static String getMD5(String message) {
        String md5str = "";
        try {
            //1 ����һ���ṩ��ϢժҪ�㷨�Ķ��󣬳�ʼ��Ϊmd5�㷨����
            MessageDigest md = MessageDigest.getInstance("MD5");
 
            //2 ����Ϣ���byte����
            byte[] input = message.getBytes();
 
            //3 ��������ֽ�����,�������128λ��
            byte[] buff = md.digest(input);
 
            //4 ������ÿһ�ֽڣ�һ���ֽ�ռ��λ������16��������md5�ַ���
            md5str = bytesToHex(buff);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }
 
    /**
     * ������תʮ������
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //������ÿһ�ֽڻ���16��������md5�ַ���
        int digital;
        for (int i = 0; i < bytes.length; i++) {
             digital = bytes[i];
 
            if(digital < 0) {
                digital += 256;
            }
            if(digital < 16){
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }


	//����ȫ��ƴ��
	public static String getSign(Map<String, String> orderlistparams,
			String secret) {
		StringBuffer splicing = new StringBuffer();
		splicing = splicing(orderlistparams); //ϵͳ������ƴ��
//		System.out.println("ϵͳ������ƴ��\n"+splicing);
		splicing.insert(0,secret).insert(splicing.length(),secret);//��secret���뿪ͷ�ͽ�β
//		Log.info("\nϵͳ����Ӧ�ü�����ƴ��\n"+splicing);
		String sign = Utils.getMD5(splicing.toString());
		return sign;
	}


}
