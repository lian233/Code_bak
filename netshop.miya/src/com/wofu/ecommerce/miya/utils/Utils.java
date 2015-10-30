package com.wofu.ecommerce.miya.utils;
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
import com.wofu.ecommerce.miya.utils.Utils;

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
//			System.out.println("排列\n"+treeMap);
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
			//获取返回的数据信息
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
	
	//参数排序拼接
	public static StringBuffer splicing(Map<String, String> orderlistparams) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		//用treeMap排序
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
	
	//MD5加密算法
    public static String getMD5(String message) {
        String md5str = "";
        try {
            //1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
 
            //2 将消息变成byte数组
            byte[] input = message.getBytes();
 
            //3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);
 
            //4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }
 
    /**
     * 二进制转十六进制
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //把数组每一字节换成16进制连成md5字符串
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


	//参数全部拼接
	public static String getSign(Map<String, String> orderlistparams,
			String secret) {
		StringBuffer splicing = new StringBuffer();
		splicing = splicing(orderlistparams); //系统级参数拼接
//		System.out.println("系统级参数拼接\n"+splicing);
		splicing.insert(0,secret).insert(splicing.length(),secret);//把secret插入开头和结尾
//		Log.info("\n系统级和应用级参数拼接\n"+splicing);
		String sign = Utils.getMD5(splicing.toString());
		return sign;
	}


}
