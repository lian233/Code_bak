package com.wofu.ecommerce.alibaba.api;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
/**
 * api���õķ�����
 */
public class ApiCallService {

    /**
     * ����api����
     * @param urlHead �����url��openapi�Ĳ��֣���http://gw.open.1688.com/openapi/
     * @param urlPath protocol/version/namespace/name/appKey
     * @param appSecretKey ���Ե�app��Կ�����Ϊ�ձ�ʾ����Ҫǩ��
     * @param params api�������map�����api��Ҫ�û���Ȩ���ʣ���ô���������Ȩ���̣�params�б������access_token����
     * @return json��ʽ�ĵ��ý��
     */
    public static String callApiTest(String urlHead, String urlPath, 
    		String appSecretKey, Map<String, String> params) throws Exception{
        final HttpClient httpClient = new HttpClient();
        final PostMethod method = new PostMethod(urlHead + urlPath);
        method.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        if(params != null){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                method.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if(appSecretKey != null){
            method.setParameter("_aop_signature", CommonUtil.signatureWithParamsAndUrlPath(urlPath, params, appSecretKey));
        }
        String response = "";
        try{
            int status = httpClient.executeMethod(method);
           /* if(status >= 300 || status < 200){
                throw new RuntimeException("invoke api failed, urlPath:" + urlPath
                        + " status:" + status + " response:" + method.getResponseBodyAsString());
            }*/
            response = CommonUtil.parserResponse(method);
 
        }finally{
            method.releaseConnection();
        }
        return response;
    }
    
}
