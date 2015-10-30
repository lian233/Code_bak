package org.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import vipapis.delivery.PickDetail;
import vipapis.delivery.JitDeliveryServiceHelper.JitDeliveryServiceClient;
import com.vip.osp.sdk.context.InvocationContext;
import com.wofu.ecommerce.jit.utils.Utils;
public class Main {
	/**
	 * vendor_id：550
		PO:2000011849
	 */
	private static final String app_key="dc8eba3a";
	private static final String app_secret="06BCB91602A8B0AEFAC80F0BE5E314BB";
	private static final String url="http://sandbox.vipapis.com/?";
	private static final String version="1.0.0";
	private static final String format="json";
	private static final String method="createPick";
	private static final String service="vipapis.delivery.JitDeliveryService";
	//{"returnCode":"0","result":[{"pick_no":"PICK-2000011849-5","pick_type":"normal","warehouse":"VIP_NH"}]}
	public static void main(String[] args) throws Exception{
		String request = "{\"po_no\":\"2000011849\",\"vendor_id\":550}";
		System.out.println(request);
		String[] test = createRequestSign(request,method,app_secret);
		System.out.println(test[1]);
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("appKey", app_key);
		map.put("format",format );
		map.put("method", method);
		map.put("service", service);
		map.put("sign", test[1]);
		map.put("timestamp", test[0]);
		map.put("version", version);
		System.out.println(Utils.sendByPost(map, request, url));
	}
	
	public static PickDetail getPickDetail(String pick_no,String po_no) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2、设置调用参数，必须
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(app_key);
		instance.setAppSecret(app_secret);
		instance.setAppURL(url);
		return client.getPickDetail(po_no, 550, pick_no, null, null);
	}
	
	

	private static String[] createRequestSign(String request,String method, 
            String appSecret)
    {
		String[] test = new String[2];
        StringBuilder builder = new StringBuilder();
        builder.append("appKey").append("dc8eba3a");
        builder.append("format").append("json");
        builder.append("method").append(method);
        builder.append("service").append("vipapis.delivery.JitDeliveryService");
        String currentTime =String.valueOf(System.currentTimeMillis() / 1000L);
        builder.append("timestamp").append(currentTime);
        builder.append("version").append("1.0.0");
        builder.append(request);
        System.out.println(builder.toString());
        try
        {
        	test[0]=currentTime;
        	test[1]= byte2hex(encryptHMAC(builder.toString(), appSecret));
        }
       
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return test;
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
 