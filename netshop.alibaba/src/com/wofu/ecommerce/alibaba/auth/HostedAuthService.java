package com.wofu.ecommerce.alibaba.auth;

import java.util.HashMap;
import java.util.Map;

import com.wofu.ecommerce.alibaba.util.CommonUtil;

/**
 * �й�ʽ��Ȩ�����ֻ࣬��������Ӧ���г�������app
 * ע�⣺��Ӧ���г�������app��ʹ���й�ʽ��Ȩ
 */
public class HostedAuthService extends AuthService{

    /**
     * �����й�ʽ��Ȩ�����л�ȡcode��һ����url
     * @param host ����������������������Ͷ˿�
     * @param params �������map������client_id,redirect_uri�Լ���ѡ��state��scope��view
     * @param appSecretKey appǩ����Կ
     * @return ���������url���û���������д򿪴�urlȻ�������Լ����û������������Ȩ��֮��ͻ�õ�code
     */
    public static String getHostedAuthUrl(String host, Map<String, String> params, String appSecretKey){
        if(params == null){
            return null;
        }
        String url = "https://" + host + "/openapi/";
        String namespace = "system.oauth2";
        String name = "startAuth";
        int version = 1;
        String protocol = "json";
        if(params.get("client_id") == null || params.get("redirect_uri") == null){
            System.out.println("params is invalid, lack neccessary key!");
            return null;
        }
        params.put("client_user_id", "testApiTools");
        params.put("response_type", "code");
        params.put("need_refresh_token", "true");
        String appKey = params.get("client_id");
        String urlPath = CommonUtil.buildInvokeUrlPath(namespace, name, version, protocol, appKey);
        String signature = CommonUtil.signatureWithParamsAndUrlPath(urlPath, params, appSecretKey);
        params.put("_aop_signature", signature);
        url += urlPath;
        return CommonUtil.getWholeUrl(url, params);
    }

    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String host = "gw.open.1688.com";//���ʽ�������"gw.api.alibaba.com"
        String client_id = "yourAppKey";
        String appSecret = "yourAppSecret";
        String redirect_uri = "http://localhost:12315";//��дapp���url
        String state = "test";//�û��Զ��������������д
        
        //���Ի�ȡ�й�ʽ��Ȩ����ʱ����code
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", client_id);
        params.put("redirect_uri", redirect_uri);
        params.put("state", state);
        String startAuthResult = getHostedAuthUrl(host, params, appSecret);
        System.out.println("����������з������µ�ַ��������վ�û������������Ȩ: " + startAuthResult);
    }
}

