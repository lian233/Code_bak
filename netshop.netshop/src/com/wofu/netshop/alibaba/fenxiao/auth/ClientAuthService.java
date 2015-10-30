package com.wofu.netshop.alibaba.fenxiao.auth;

import java.util.HashMap;
import java.util.Map;

import com.wofu.netshop.alibaba.fenxiao.util.CommonUtil;

/**
 * ���̼�ÿһ����Ҫ�����ȡcode: app������Ȩ����
 * �ڶ������û������û������룬��ȷ����Ȩ
 * ��������������ʱ��Ȩ��code��app   code=9db89a66-4c49-465c-a3c7-864401bbbdfc
 * ���Ĳ�: ʹ��code��ȡ����  AuthService.getToken()
 * 
 * �ͻ���/WEB����Ȩ�����࣬��Ҫ�����û���ʹ�ÿͻ��˻���Web�����͵�appʱ������Ȩ
 * ע�⣺��Ӧ���г�������app����ʹ�����ַ�ʽ����ʹ���й�ʽ��Ȩ
 * ���̼�Ҫ�������ȡ�����������������������������������Լ����û�������󣬾Ϳ��Եõ�code
 */
public class ClientAuthService extends AuthService{
    
    /**
     * ���ؿͻ��˺�Web����Ȩʱ��ȡ��ʱ����code��url
     * @param host ����������������������Ͷ˿�
     * @param params �������map������client_id,site,redirect_uri�Լ���ѡ��state��scope��view
     * @param appSecretKey appǩ����Կ
     * @return ���������url���û���������д򿪴�urlȻ�������Լ����û������������Ȩ��֮��ͻ�õ�code
     */
    public static String getClientAuthUrl(String host, Map<String, String> params, String appSecretKey){
        String url = "http://" + host + "/auth/authorize.htm";
        if(params == null){
            return null;
        }
        if(params.get("client_id") == null || params.get("site") == null
                || params.get("redirect_uri") == null){
            System.out.println("params is invalid, lack neccessary key!");
            return null;
        }
        String signature = CommonUtil.signatureWithParamsOnly(params, appSecretKey);
        params.put("_aop_signature", signature);
        return CommonUtil.getWholeUrl(url, params);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String host = "gw.open.1688.com";//���ʽ�������"gw.api.alibaba.com"
        String site = "china";//���ʽ�������"aliexpress"
        String client_id = "1008771";
        String appSecret = "AmUvfEqcm2U";
        //��Ϊ�ͻ�����Ȩ����ô�ص���ַ������������ʽ������ɲο��ĵ���
        //(1)urn:ietf:wg:oauth:2.0:oob
        //(2)http://localhost:port
        //(3)http://gw.open.1688.com/auth/authCode.htm�����ʽ�������"gw.api.alibaba.com"��
        //��ΪWEB����Ȩ����ô�ص���ַӦ����app����ڵ�ַ
        String redirect_uri = "http://121.196.132.134:30002/login.html";
       // String state = "test";//�û��Զ��������������д
        
        //���Ի�ȡ�ͻ�����Ȩ����ʱ����code    
        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("site", site);
        params2.put("client_id", client_id);
        params2.put("redirect_uri", redirect_uri);
       // params2.put("state", state);
        String getCodeForClientResult = getClientAuthUrl(host, params2, appSecret);
        System.out.println("����������з������µ�ַ��������վ�û������������Ȩ: " + getCodeForClientResult);
        
    }

}
