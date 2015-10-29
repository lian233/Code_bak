package com.wofu.ecommerce.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.wofu.business.order.OrderManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.Alibaba;
import com.wofu.ecommerce.alibaba.Children;
import com.wofu.ecommerce.alibaba.Goods;
import com.wofu.ecommerce.alibaba.GoodsSKU;
import com.wofu.ecommerce.alibaba.Order;
import com.wofu.ecommerce.alibaba.OrderItem;
import com.wofu.ecommerce.alibaba.Params;
import com.wofu.ecommerce.alibaba.ProductFeatureList;
import com.wofu.ecommerce.alibaba.Version;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
/***
 * ���ԣ���ȡ������Ʒ��Ϣ
 * @author Administrator
 *
 */
public class test2 {
	private static String lasttime;
	private static long daymillis=24*60*60*1000L;
	private static String access_token=null;
	private static String type="ALL";
	
	private static String returnFields="offerId,offerStatus,subject,amount,amountOnSale,saledCount,type,gmtCreate,gmtModified,sendGoodsAddressId";
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	public static Connection getConnection() throws Exception
	{

		String driver="com.microsoft.jdbc.sqlserver.SQLServerDriver";
		String url="jdbc:microsoft:sqlserver://172.20.11.116:1433;DatabaseName=ErpDKBMConnect";
		String user="sa";
		String password="sa";
		 
		if (driver != null && !driver.equals("")) {
			DriverManager.registerDriver(
				(Driver) Class.forName(driver).newInstance());
		}
		if (user != null) {
			return DriverManager.getConnection(url, user, password);
		} else {
			return DriverManager.getConnection(url);
		}
			
	}
	
	
	public static void main(String[] args) throws Exception{
//		String sql="select isnull(value,0) from config where name='�Ƿ���֤����վIP'";
		//Connection conn = getConnection();
//		String t = SQLHelper.strSelect(conn, sql);
//		System.out.println(t);
//		JSONObject json = new JSONObject(t);
//		System.out.println(json.get("empty"));
//		System.out.println(json);
//		Alibaba a = new Alibaba();
//		a.start();
//		String s=PublicUtils.getConfig(conn,Params.username+"ȡ��������ʱ��","");
//		System.out.println(s);
//		Date modified=Formatter.parseDate(s,Formatter.DATE_TIME_FORMAT);
//		System.out.println(modified);
		
		//lasttime=PublicUtils.getConfig(conn,lasttimeconfvalue,"2013-10-10 00:00:00");
//		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("offerId", "1257893833");
		//params.put("type", type) ;
		//params.put("memberId", "b2b-1704364314");
		//params.put("gmtModifiedBegin","2013-10-17 10:00:00");
		//params.put("gmtModifiedEnd", "2013-10-17 13:00:00");
		//params.put("p","��ܽ������ ˮ�ܴ��� ��˿�������ſ۾�£�������ո�����������");
		//Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		//Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
		params.put("returnFields", returnFields);
		//params.put("modifyStartTime",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT)) ;
		//params.put("modifyEndTime", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));
		//params.put("page", String.valueOf(1)) ;
		//params.put("pageSize", "10") ;
		
		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
		System.out.println(urlPath);
		//����ǩ��signature
		//String signature=CommonUtil.signatureWithParamsAndUrlPath(urlPath, params, Params.secretKey);
		//params.put("_aop_signature", signature);
		
		//��ȡ��Ȩ���ƵĲ���
//		params.put("client_id", Params.appkey);
//	    params.put("redirect_uri", Params.redirect_uri);
//	    params.put("client_secret", Params.secretKey);
//	    params.put("refresh_token", Params.refresh_token);
//	    //ͨ���������ƻ�ȡ��Ȩ����
//	    if(access_token==null){
//	    	access_token=AuthService.refreshToken(Params.host, params);
//	    }
//	    params.remove("client_id");
//	    params.remove("redirect_uri");
//	    params.remove("client_secret");
//	    params.remove("refresh_token");
//	    
//	    JSONObject js=new JSONObject(access_token);
//	    System.out.println(js.getString("access_token"));
	    params.put("access_token", "04128b77-5282-4a61-b027-bcc7b5921786");
	    //
		
		String responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
		System.out.println("responseText��"+responseText);
		
		//���ؽ����
		JSONObject jresp=new JSONObject(responseText);
		System.out.println(jresp);
//		
//		JSONObject jres=(JSONObject) jresp.getJSONObject("result");
//		System.out.println(jres.getBoolean("success"));
//		//��ȡ������
//		int total=jres.getInt("total");
//		System.out.println(total);
//		//��ҳ��
//		int pageTotal=Double.valueOf(Math.ceil(total/50)).intValue();
//		
//		//���ص���Ʒ�б�����
//		JSONArray jresult=jres.getJSONArray("toReturn");
//		System.out.println(jresult);
//		
//		for(int m=0;m<jresult.length();m++){
//			JSONObject j1=jresult.getJSONObject(m);
//			Goods gd=new Goods();
//			gd.setObjValue(gd, j1);
//			//System.out.println("offerid:"+gd.getOfferId()+"  modefytime:"+gd.getGmtModified());
//			
//			Hashtable<String, String> params1 = new Hashtable<String, String>() ;
//			params1.put("offerId", String.valueOf(gd.getOfferId()));
//			params1.put("returnFields", "offerId,productFeatureList,offerStatus,subject,amount,amountOnSale,saledCount,type,gmtCreate,gmtModified,skuArray");
//			
//			String urlPath1=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
//			String response = ApiCallService.callApiTest(Params.url, urlPath1, Params.secretKey, params1);
//			
//			System.out.println("response:"+response);
//			JSONObject res=new JSONObject(response);
//			
//			JSONArray jarray=res.getJSONObject("result").getJSONArray("toReturn");
//			Goods oo=new Goods();
//			oo.setObjValue(oo,jarray.getJSONObject(0));
//			//ProductFeatureList pl=(ProductFeatureList)oo.getProductFeatureList().getRelationData().get(8);
//			
//			
//			
////			for(Iterator s=oo.getSkuArray().getRelationData().iterator();s.hasNext();){
////				GoodsSKU ch=(GoodsSKU)s.next();
////				for(Iterator y=ch.getChildren().getRelationData().iterator();y.hasNext();){
////					Children cc=(Children)y.next();
////					cc.setCanBookCount(1001);
////					System.out.println(cc.getCargoNumber());
////				}
////			}
//			Integer huohao=null;
//			for(Iterator o =oo.getProductFeatureList().getRelationData().iterator();o.hasNext();){
//				ProductFeatureList pfl=(ProductFeatureList)o.next();
//				if(pfl.getName().equals("����")){
//					huohao=Integer.parseInt(pfl.getValue());
//				}
//				
//			}
//			System.out.println("huo:"+huohao);
//			System.out.println(oo.getAmountOnSale());
//		}
		
	}
}
