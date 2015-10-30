package com.wofu.ecommerce.qqbuy.test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.qqbuy.Goods;
import com.wofu.ecommerce.qqbuy.SkuInfo;
import com.wofu.ecommerce.qqbuy.StockUtils;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;

public class Test6 {

	public final static String encoding = "gbk" ; 
	public static String charset = "utf-8" ;
	public static String format = "xml" ;
	public static String host = "http://api.buy.qq.com" ;

	//��ʽ
	public static String cooperatorId = "855005035" ;
	public static String appOAuthID = "700044939" ;
	public static String secretOAuthKey = "s1TGmTwb43gftUYX" ;
	public static String accessToken = "eba8f41a64718ac25c75926d32cb2102" ;
	public static long uin = 855005035L ;
	
	private static String uri="/item/modifySKUStock.xhtml";
	private static String tradecontactid="7";
	private static String dbname="";
	private static String username="";
	private static String pageSize = "20" ;
	private static String jobname = "" ;
	private static String gShopID = "" ;
	private static String key = "" ;
	private static int stockAlarmQty = 7 ;

	public static void main(String[] args) {

		try {
			String startTime = "" ;
			String endTime = "" ;
			Connection conn=DataBaseTool.conDB() ;
			Hashtable<String, String> inputParams = new Hashtable<String, String>() ;
			inputParams.put("jobname", "") ;
			inputParams.put("accessToken", accessToken) ;
			inputParams.put("appOAuthID", appOAuthID) ;
			inputParams.put("secretOAuthKey", secretOAuthKey) ;
			inputParams.put("cooperatorId", cooperatorId) ;
			inputParams.put("uin", String.valueOf(uin)) ;
			inputParams.put("encoding", encoding) ;
			inputParams.put("startTime", startTime) ;
			inputParams.put("endTime", endTime) ;
			inputParams.put("pageSize", "5") ;
			/**
			 * String accessToken = inputParams.get("accessToken") ;
				String appOAuthID = inputParams.get("appOAuthID") ;
				String secretOAuthKey = inputParams.get("secretOAuthKey") ;
				String cooperatorId = inputParams.get("cooperatorId") ;
				long uin = Long.parseLong(inputParams.get("uin")) ;
				String encoding = inputParams.get("encoding") ;
				String startTime =  inputParams.get("startTime") ;
				String endTime = inputParams.get("endTime") ;
				int pageSize = Integer.parseInt(inputParams.get("pageSize")) ;
			 */
			
			//��ȡQQ���������ϼ���ƷID
			List<Goods> goodsList = StockUtils.getSkuList(jobname, inputParams) ;
			Log.info(jobname,"��ȡ��"+goodsList.size()+"��QQ������Ʒ��") ;
			
			String sql = "" ;
			for(int i = 0 ; i < goodsList.size() ; i++)
			{
				try 
				{
					Goods goods = goodsList.get(i) ;
					SkuInfo skuInfo = goods.getStockList().get(0) ;
					String stockHourseId = skuInfo.getStockhouseId() ;
					int stockCount = skuInfo.getStockCount() ;
					String sku = skuInfo.getStockLocalcode() ;
					//����skuȡ��ϵͳ��棬���µ�QQ����
					int[] syninfo=StockManager.getSynStockQty("ͬ��QQ�������",conn,tradecontactid,sku,0,-1);
					if (syninfo[0]==1)
					{
						//�¿��=ϵͳ���ÿ��+QQ�����Ѿ�������
						int newQty = syninfo[1] + skuInfo.getStockPayedNum() ;
						if(newQty < stockAlarmQty)
						{
							newQty = 0 ;
							Log.info("��Ʒ��"+ sku +"���Ѿ��ﵽ�����桾"+ stockAlarmQty +"��,ͬ�����Ϊ��"+ newQty +"��") ;
						}
						int oldQty = skuInfo.getStockCount() ;
						//QQ�����ֿ�id
						String stockHouseId = skuInfo.getStockhouseId() ;
						String skuid = goods.getSkuId() ;
						
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
						sdk.setCharset(encoding) ;
						HashMap<String, Object> params = sdk.getParams(uri);
						params.put("charset", encoding) ;
						params.put("format", "xml") ;
						params.put("cooperatorId", cooperatorId) ;
						params.put("skuId", skuid) ;
						params.put("stockhouseId", stockHouseId) ;
						params.put("stockCount", String.valueOf(newQty)) ;
						
						String responseText = sdk.invoke() ;
						
						//Log.info("responseText="+responseText) ;
						
						Document doc = DOMHelper.newDocument(responseText, encoding);
						Element resultElement = doc.getDocumentElement();
						String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
						if("0".equals(errorCode))
						{
							Log.info("����QQ������Ʒ���ɹ�,sku��"+ sku +"��,ԭ��桾"+ oldQty +"��,�¿�桾"+ newQty +"��,�����桾"+ skuInfo.getStockPayedNum() +"��,״̬��"+ skuInfo.getStockSaleState() +"��") ;
						}
						else
						{
							String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
							Log.error(jobname, "����QQ������Ʒ���ʧ��,sku��"+ sku +"��,������Ϣ:"+errorCode+errorMessage) ;
						}
					}
				
				} catch (Exception e) {
					Log.error(jobname, "����QQ������Ʒ������:"+e.getMessage()) ;
					e.printStackTrace() ;
				}
			}
			conn.close() ;
		} catch (Exception e) {
			e.printStackTrace() ;
		}

	}

}
