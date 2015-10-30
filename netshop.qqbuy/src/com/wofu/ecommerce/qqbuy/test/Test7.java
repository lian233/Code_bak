package com.wofu.ecommerce.qqbuy.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.qqbuy.Goods;
import com.wofu.ecommerce.qqbuy.Order;
import com.wofu.ecommerce.qqbuy.OrderUtils;
import com.wofu.ecommerce.qqbuy.StockUtils;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;

public class Test7 {

	public final static String encoding = "gbk" ; 
	public static String charset = "utf-8" ;
	public static String format = "xml" ;
	public static String host = "http://api.buy.qq.com" ;
	private static long monthMillis = 30 * 24 * 60 * 60 * 1000L ; 
	private static String timeType = "UPDATE" ;
	//����
//	public static String cooperatorId = "855010773" ;
//	public static String appOAuthID = "700043070" ;
//	public static String secretOAuthKey = "pEOO6eUeNeU926qK" ;
//	public static String accessToken = "7faff45d7bd43cae61c72f3101c0572b" ;
//	public static String uin = "855010773" ;
	//��ʽ
	public static String cooperatorId = "855005035" ;
	public static String appOAuthID = "700044939" ;
	public static String secretOAuthKey = "s1TGmTwb43gftUYX" ;
	public static String accessToken = "eba8f41a64718ac25c75926d32cb2102" ;
	public static String uin = "855005035" ;

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("accessToken", accessToken);
		params.put("appOAuthID", appOAuthID);
		params.put("cooperatorId", cooperatorId);
		params.put("secretOAuthKey", secretOAuthKey);
		params.put("uin", String.valueOf(uin));
		params.put("encoding", encoding);
		params.put("startTime", "2012-08-08 00:00:00");
		params.put("endTime", "2012-09-10 00:00:00");
		params.put("pageSize", "20");
		params.put("format", format) ;
		Log.info(params.toString()) ;
		
		//orderDelivery("", "0925-12282944-12382704", "YTO", "7090608539", "056yuantong", URLEncoder.encode("Բͨ�ٵ�", "utf-8"), params) ;
		//String skuid = StockUtils.getSkuID("JF205401AE") ;
		//System.out.println("skuid="+skuid) ;
		//Goods goods = StockUtils.getSKUInfo("", "270801614427", params) ;
		Order order = OrderUtils.getOrderByID("", "0608-14174824-14434090", params) ;
		System.out.println(order.getDealId()) ;
//		���ݶ���ʱ��Ͷ���״̬��ȡ�����б�
		String startTime="2012-11-19 09:34:06" ;
		String endTime="2012-11-20 09:34:05" ;
		ArrayList<Hashtable<String, String>> orderIdList = new ArrayList<Hashtable<String, String>>() ;
		String orderStateList = "STATE_POL_WAIT_PAY;STATE_WAIT_CHECK;STATE_WAIT_SHIPPING;STATE_WAIT_CONFIRM;STATE_DEAL_SUCCESS;STATE_POL_CANCEL;STATE_POL_END;STATE_DEAL_REFUNDING" ;
		String stateArray[] = orderStateList.split(";") ;
		//for(int i = 0 ; i < stateArray.length ; i++)
		//	orderIdList.addAll(OrderUtils.getOrderIdList("", stateArray[i], timeType, startTime, endTime, params)) ;
		orderIdList.addAll(OrderUtils.getOrderIdList("", "STATE_WAIT_CONFIRM", timeType, startTime, endTime, params)) ;
		for(int j = 0 ; j < orderIdList.size() ; j++)
		{
			Hashtable<String, String> ht = orderIdList.get(j) ;
			String orderID = ht.get("dealId") ;
			String lastUpdateTime = ht.get("lastUpdateTime") ;
			Order o = OrderUtils.getOrderByID("", orderID, params) ;
			String state = o.getDealState() ;
			Log.info("�����š�"+ orderID +"��,״̬��"+ state +"��,����޸�ʱ�䡾"+ lastUpdateTime +"��") ;
		}
	}

//	���¶���������Ϣ
	public static boolean orderDelivery(String jobname,String orderId,String companyCode,String outsid,String expressCompanyId,String expressCompanyName,Hashtable<String, String> inputParams)
	{
		boolean flag = false ;
		String responseText = "" ;
		String uri = "/deal/signShipV2.xhtml" ;
		String transportType = "" ;//����������ʽ 1:��������� 2:�Խ����� 3:EMS���� 4:�û���ȡ
		
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String accessToken = inputParams.get("accessToken") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		String encoding = inputParams.get("encoding") ;
		String uin = inputParams.get("uin") ;
		String format = inputParams.get("format") ;
		try 
		{
			if("ems".equalsIgnoreCase(companyCode))
				transportType = "3" ;
			else if("����".equals(companyCode))
				transportType = "4" ;
			else
				transportType = "1" ;
				
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", format) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("dealId", orderId) ;
			params.put("transportType", transportType) ;
			params.put("expArriveDays", "5") ;
			params.put("expressCompanyId", expressCompanyId) ;
			params.put("expressName", expressCompanyName) ;
			params.put("expressDealId", outsid) ;
			
			Log.info(params.toString()) ;
			responseText = sdk.invoke() ;
			System.out.println("responseText="+responseText) ;
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			String dealId = DOMHelper.getSubElementVauleByName(resultElement, "dealId").trim() ;
			if("0".equals(errorCode) && orderId.equals(dealId))
			{
				flag = true ;
				Log.info("����QQ�����������������Ϣ�ɹ�,�����š�"+ orderId +"��,��ݹ�˾��"+ companyCode +"��,��ݵ��š�"+ outsid +"��") ;
			}
			else
			{
				flag = false ;
				String errorMessage  = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname,"����QQ�����������������Ϣʧ��,�����š�"+ orderId +"��,������Ϣ:"+errorCode+errorMessage) ;
			}
		} 
		catch (Exception e) {
			flag = false ;
			Log.error(jobname, "����QQ�����������������Ϣʧ��,������Ϣ:"+ e.getMessage() + ",����ֵ:"+ responseText) ;
			e.printStackTrace() ;
		}
		return flag ;
	}
	
}
