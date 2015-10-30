package com.wofu.netshop.alibaba.fenxiao.auth;
import java.util.HashMap;
import java.util.Map;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.alibaba.fenxiao.api.ApiCallService;
import com.wofu.netshop.alibaba.fenxiao.util.CommonUtil;
/**
 * ��Ȩ�����࣬��Ҫ�ṩ��������Ȩ���񶼻��õ��Ļ�ȡ��Ȩ���ƵĹ���
 */
public class AuthService {

    /**
     * ͨ����ʱ���ƻ�ȡ��Ȩ����
     * @param host ����������������������Ͷ˿�
     * @param params �������������client_id��client_secret��redirect_uri��code��scope��view��ѡ
     * @param needRefreshToken �Ƿ���Ҫ����refreshToken
     * @return getToken�����json��
     * ��ȡaccessToken��urlʾ�����£�
       https://gw.open.1688.com/openapi/param2/1/system.oauth2/getToken/YOUR_APPKEY
      ����������£�
      grant_type=refresh_token&client_id=YOUR_APPKEY&client_secret=YOUR_APPSECRET&refresh_token=REFRESH_TOKEN
     */
    public static String getToken(String host, Map<String, String> params, boolean needRefreshToken) throws Exception{
        String urlHead = "https://" + host + "/openapi/";
        String namespace = "system.oauth2";
        String name = "getToken";
        int version = 1;
        String protocol = "json";
        if(params != null){
            if(params.get("client_id") == null || params.get("client_secret") == null
                    || params.get("redirect_uri") == null || params.get("code") == null){
                System.out.println("params is invalid, lack neccessary key!");
                return null;
            }
            params.put("grant_type", "authorization_code");
            params.put("need_refresh_token", Boolean.toString(needRefreshToken));
            String appKey = params.get("client_id");
            String urlPath = CommonUtil.buildInvokeUrlPath(namespace, name, version, protocol, appKey);
            String result = ApiCallService.callApiTest(urlHead, urlPath, null, params);
            return result;
        }
        return "";
    }
    
    /**
     * ͨ����ʱ���ƻ�ȡ��Ȩ����
     * @param host ����������������������Ͷ˿�
     * @param params �������������client_id��client_secret��redirect_uri��refresh_token��scope��view��ѡ
     * @return
     */
    public static String refreshToken(String host, Map<String, String> params) throws Exception{
        String urlHead = "https://" + host + "/openapi/";
        String namespace = "system.oauth2";
        String name = "getToken";
        int version = 1;
        String protocol = "param2";
        if(params != null){
            if(params.get("client_id") == null || params.get("client_secret") == null
                    || params.get("redirect_uri") == null || params.get("refresh_token") == null){
                System.out.println("params is invalid, lack neccessary key!");
                return null;
            }
            params.put("grant_type", "refresh_token");
            String appKey = params.get("client_id");
            String urlPath = CommonUtil.buildInvokeUrlPath(namespace, name, version, protocol, appKey);
            String result = ApiCallService.callApiTest(urlHead, urlPath, null, params);
            return result;
        }
        return "";
    }
    /**
     * {"uid":"337798750","refresh_token_timeout":"20140914180835000+0800",
     * "aliId":"1104295386",
     * "resource_owner":"meetlove168",
     * "memberId":"meetlove168",
     * "expires_in":"36000",
     * "refresh_token":"a134612a-a305-46a8-ab2c-774eb0f36753",
     * "access_token":"f3591124-2b69-4dc4-ab33-74716b137ed6"}
     * @param args
     */
    public static void main(String args[]){
    	String host ="";
    	HashMap<String,String> map = new HashMap<String,String>();
    	String client_id="1008771";
    	String client_secret="AmUvfEqcm2U";
    	String redirect_uri="www.163.com";
    	String code="7c8868a6-31cb-4e80-b6f5-ae6dfb923fac";
    	map.put("client_id",client_id);
    	map.put("client_secret",client_secret);
    	map.put("redirect_uri",redirect_uri);
    	map.put("code", code);
    	try {
			String result = getToken(host,map,true);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
}
