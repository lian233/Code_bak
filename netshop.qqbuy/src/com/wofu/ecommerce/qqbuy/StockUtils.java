package com.wofu.ecommerce.qqbuy;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;

public class StockUtils 
{

	//ȫ��sku
	private static Hashtable<String, String> allSkuInfo = new Hashtable<String, String>() ;
	//����sku������Ʒ��Ϣ
	public static String getSkuID(String sku) 
	{
		try 
		{
			return allSkuInfo.get(sku) ;
		} catch (Exception e) {
			return null ;
		}
	}
	//��ʼ����Ʒ��Ϣ
	public static void setAllSkuInfo(String jobname,Hashtable<String, String> params) 
	{
		 try 
		 {
			List<Goods> goodsList = getSkuList(jobname, params) ;
			for(int i = 0 ; i< goodsList.size() ; i++)
			{
				Goods g = goodsList.get(i) ;
				//��QQ����ƽ̨��ֻ��һ����,����sku��ʱ��
				ArrayList<SkuInfo> stockList = g.getStockList() ;
				if(stockList.size() > 0)
					allSkuInfo.put(stockList.get(0).getStockLocalcode(),g.getSkuId()) ;
			}
		 } catch (UnsupportedEncodingException e) {
			Log.error(jobname, "��ȡQQ������Ʒ����ʧ��,������Ϣ:"+e.getMessage()) ;
		}
	}
	//��ȡȫ��sku�б�
	public static List<Goods> getSkuList(String jobname,Hashtable<String, String> inputParams) 
		throws UnsupportedEncodingException
	{
		List<Goods> goodsList = new Vector<Goods>() ;
		
		String uri = "/item/getSKUListByTime.xhtml" ;
		String responseText = "" ;
		String subAccountId = "" ;
		
		String accessToken = inputParams.get("accessToken") ;
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		long uin = Long.parseLong(inputParams.get("uin")) ;
		String encoding = inputParams.get("encoding") ;
		String startTime =  inputParams.get("startTime") ;
		String endTime = inputParams.get("endTime") ;
		int pageSize = Integer.parseInt(inputParams.get("pageSize")) ;
		
		ArrayList<String> subAccountIdList = getSubAccountIdList(jobname,inputParams) ;
		for(int i = 0 ; i < subAccountIdList.size() ; i++)
		{
			subAccountId = subAccountIdList.get(i) ;
			try 
			{
				int pageIndex = 1 ;
				boolean hasNextPage = true ;
				while(hasNextPage)
				{
					PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
					sdk.setCharset(encoding) ;
					HashMap<String, Object> params = sdk.getParams(uri);
					params.put("charset", encoding) ;
					params.put("format", "xml") ;
					params.put("cooperatorId", cooperatorId) ;
					params.put("subAccountId", subAccountId) ;
					params.put("startTime", startTime) ;
					params.put("endTime", endTime) ;
					params.put("pageIndex", String.valueOf(pageIndex)) ;
					params.put("pageSize", String.valueOf(pageSize)) ;
					
					responseText=sdk.invoke() ;
					
					Document doc = DOMHelper.newDocument(responseText, encoding);
					Element resultElement = doc.getDocumentElement();
					String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
					if("1435".equals(errorCode))
					{
						hasNextPage = false ;
						continue ;
					}
					if(!"0".equals(errorCode))
					{
						String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
						Log.info(jobname, "��ѯsku�б���Ϣʧ��,������Ϣ:"+errorCode+errorMessage+",����ֵ:"+responseText) ;
						hasNextPage = false ;
						continue ;
					}
					
					Element skuList = (Element) resultElement.getElementsByTagName("skuList").item(0) ;
					NodeList skuNodeList = skuList.getElementsByTagName("sku") ;
					for(int j= 0 ; j < skuNodeList.getLength() ; j++)
					{
						Goods g = new Goods() ;
						Element sku = (Element) skuNodeList.item(j) ;
						String skuId = DOMHelper.getSubElementVauleByName(sku, "skuId") ;
						String skuTitle = DOMHelper.getSubElementVauleByName(sku, "skuTitle") ;
						String attr = DOMHelper.getSubElementVauleByName(sku, "attr") ;
						String buyLimit = DOMHelper.getSubElementVauleByName(sku, "buyLimit") ;
						String skuLocalCode = DOMHelper.getSubElementVauleByName(sku, "skuLocalCode") ;//�̼�sku
						String producerBarCode = DOMHelper.getSubElementVauleByName(sku, "producerBarCode") ;
						String skuBarcode = DOMHelper.getSubElementVauleByName(sku, "skuBarcode") ;
						String marketPrice = DOMHelper.getSubElementVauleByName(sku, "marketPrice") ;
						String weight = DOMHelper.getSubElementVauleByName(sku, "weight") ;
						String classId = DOMHelper.getSubElementVauleByName(sku, "classId") ;
						String skuState = DOMHelper.getSubElementVauleByName(sku, "skuState") ;
						String spuId = DOMHelper.getSubElementVauleByName(sku, "spuId") ;
						String shopCategoryAttr = DOMHelper.getSubElementVauleByName(sku, "shopCategoryAttr") ;
						String lastUpdateTime = DOMHelper.getSubElementVauleByName(sku, "lastUpdateTime") ;
						String itemAttr = DOMHelper.getSubElementVauleByName(sku, "itemAttr") ;

						g.setSkuId(skuId) ;
						g.setSkuTitle(skuTitle) ;
						g.setAttr(attr) ;
						g.setBuyLimit(buyLimit) ;
						g.setSkuLocalCode(skuLocalCode) ;
						g.setProducerBarCode(producerBarCode) ;
						g.setSkuBarcode(skuBarcode) ;
						g.setMarketPrice(Float.parseFloat(marketPrice)) ;
						g.setWeight(Float.parseFloat(weight)) ;
						g.setClassId(classId) ;
						g.setSkuState(skuState) ;
						g.setSpuId(spuId) ;
						g.setShopCategoryAttr(shopCategoryAttr) ;
						g.setLastUpdateTime(Formatter.parseDate(lastUpdateTime, Formatter.DATE_TIME_FORMAT)) ;
						
						//�ֲ�
						Element stockList = (Element) sku.getElementsByTagName("stockList").item(0) ;
						NodeList stockNodeList = stockList.getElementsByTagName("stock") ;
						ArrayList<SkuInfo> skuInfoList = new ArrayList<SkuInfo>() ;
						for(int k = 0 ; k < stockNodeList.getLength() ; k++)
						{
							SkuInfo skuInfo = new SkuInfo() ;
							Element stock = (Element) stockNodeList.item(k) ;
							String stockhouseId = DOMHelper.getSubElementVauleByName(stock, "stockhouseId") ;
							String stockLocalcode = DOMHelper.getSubElementVauleByName(stock, "stockLocalcode") ;
							String stockLocalBarcode = DOMHelper.getSubElementVauleByName(stock, "stockLocalBarcode") ;
							String primeCost = DOMHelper.getSubElementVauleByName(stock, "primeCost") ;
							String price = DOMHelper.getSubElementVauleByName(stock, "price") ;
							String stockCount = DOMHelper.getSubElementVauleByName(stock, "stockCount") ;
							String restrictedAreas = DOMHelper.getSubElementVauleByName(stock, "restrictedAreas") ;
							String stockSaleState = DOMHelper.getSubElementVauleByName(stock, "stockSaleState") ;
							String stockestimatedispatch = DOMHelper.getSubElementVauleByName(stock, "stockestimatedispatch") ;
							String stockPayedNum = DOMHelper.getSubElementVauleByName(stock, "stockPayedNum") ;// 
							
							skuInfo.setStockhouseId(stockhouseId) ;
							skuInfo.setStockLocalcode(stockLocalcode) ;
							skuInfo.setStockLocalBarcode(stockLocalBarcode) ;
							skuInfo.setPrimeCost(Float.parseFloat(primeCost)) ;
							skuInfo.setPrice(Float.parseFloat(price)) ;
							skuInfo.setStockCount(Integer.parseInt(stockCount)) ;
							skuInfo.setRestrictedAreas(restrictedAreas) ;
							skuInfo.setStockSaleState(stockSaleState) ;
							skuInfo.setStockPayedNum(Integer.parseInt(stockPayedNum)) ;
							
							skuInfoList.add(skuInfo) ;
						}
						g.setStockList(skuInfoList) ;
						goodsList.add(g) ;
					}
					//�ж��Ƿ�����һҳ
					int totalNum = Integer.parseInt(DOMHelper.getSubElementVauleByName(resultElement, "totalNum")) ;
					if( pageSize*pageIndex < totalNum )
						pageIndex =pageIndex+1;
					else
						hasNextPage = false ;									
				}
			} catch (Exception e) {
				Log.error(jobname, "��ѯsku�б���Ϣʧ��,������Ϣ:" + e.getMessage() + ",����ֵ:" + responseText) ;
			}			
		}
	
		return goodsList ;
	}
	//��ȡ����������˺�id
	public static ArrayList<String> getSubAccountIdList(String jobname,Hashtable<String, String> inputParams)
	{
		ArrayList<String> idList = new ArrayList<String>() ;
		String uri = "/user/getCooperatorBaseInfo.xhtml" ;
		String responseText = "" ;
		
		String accessToken = inputParams.get("accessToken") ;
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String encoding = inputParams.get("encoding") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		long uin = Long.parseLong(inputParams.get("uin")) ;
		
		try 
		{
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", "xml") ;
			params.put("cooperatorId", cooperatorId) ;
			
			responseText=sdk.invoke() ;
						
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			if(!"0".equals(errorCode))
			{
				String errorMessage  = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname, "��ȡ����������˺�idʧ��,������Ϣ:"+ errorCode + errorMessage) ;
				return idList;
			}
			Element subAccountList = (Element) resultElement.getElementsByTagName("subAccountList").item(0) ;
			NodeList subAccountNodeList = subAccountList.getElementsByTagName("subAccount") ;
			for(int i = 0 ; i < subAccountNodeList.getLength() ; i++)
			{
				Element subAccount = (Element) subAccountNodeList.item(i) ;
				String subId = DOMHelper.getSubElementVauleByName(subAccount, "subId") ;
				idList.add(subId) ;
			}		
		} catch (Exception e) 
		{
			Log.error(jobname, "��ȡ����������˺�idʧ��,������Ϣ:"+e.getMessage()+",����ֵ:"+responseText) ;
		}
		return idList ;
	}
	//����QQ�������
	public static void updateStock(String jobname,Connection conn,String tradecontactid,int orgid,String tid,String sku,int modifyQty,Hashtable<String, String> inputParams)
	{
		String responseText = "" ;
		String uri = "/item/modifySKUStock.xhtml" ;
		String accessToken = inputParams.get("accessToken") ;
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String encoding = inputParams.get("encoding") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		long uin = Long.parseLong(inputParams.get("uin")) ;
		String stockHouseId = "" ;
		int oldQty = 0 ;
		int newQty = 0 ;
		try
		{
			String skuid = StockUtils.getSkuID(sku) ;
			if(skuid == null)
			{
				//�Ҳ���QQ������Ʒ����
				backupSynReduceStoreNote(jobname, conn, tradecontactid, sku, tid) ;
				Log.error(jobname, "�Ҳ���QQ��������,�����š�"+ tid +"��,sku��"+ sku +"��") ;
				return ;
			}
			//����skuid��ȡQQ����������Ϣ
			Goods goods = StockUtils.getSKUInfo(jobname, skuid, inputParams) ;
			SkuInfo skuInfo = goods.getStockList().get(0) ;
			if(skuInfo == null || !sku.equals(skuInfo.getStockLocalcode()))
			{
				//�Ҳ���QQ������Ʒ����
				Log.error(jobname, "�Ҳ���QQ��������,�����š�"+ tid +"��,sku��"+ sku +"��") ;
				return ;
			}
			
			//������º���
			oldQty = skuInfo.getStockCount() ;
			newQty = oldQty + modifyQty ;
			//�����þ����棬ȡ������Ϣ
			String sql="select count(*) from ecs_stockconfig with(nolock) where orgid="+orgid+" and sku='"+sku+"'";
			if (SQLHelper.intSelect(conn, sql) > 0)
			{
				sql="select alarmqty,isneedsyn,alarmstatus from ecs_stockconfig with(nolock) where orgid="+orgid+" and sku='"+sku+"'";
				Hashtable ht=SQLHelper.oneRowSelect(conn, sql);
				
				int alarmqty=Integer.valueOf(ht.get("alarmqty").toString());
				int isneedsyn=Integer.valueOf(ht.get("isneedsyn").toString());
				int alarmstatus=Integer.valueOf(ht.get("alarmstatus").toString());
				//����Ҫͬ�����
				if (isneedsyn==0)
				{
					Log.info(jobname,"����Ҫͬ�����,������:"+tid+" SKU��"+ sku +"��");
					backupSynReduceStoreNote(jobname, conn, tradecontactid, sku, tid) ;
					return ;
				}
				
				if(newQty <= alarmqty)
				{
					newQty = 0 ;
					if(alarmstatus == 0)
					{
						//���¾���״̬
						sql="update ecs_stockconfig set alarmstatus=1,alarmtime=getdate() where orgid="+orgid+" and sku='"+sku+"'";
						SQLHelper.executeSQL(conn, sql);
						Log.warn("����QQ�������","��Ʒ����Ѵﵽ������:"+alarmqty+",QQ�������:"+ goods.getStockList().get(0).getStockCount() +",�������:"+ modifyQty +",SKU:"+sku);
					}
				}
				else
				{
					if(alarmstatus == 1)
					{
						sql="update ecs_stockconfig set alarmstatus=0 where orgid="+orgid+" and sku='"+sku+"'";
						SQLHelper.executeSQL(conn, sql);
						Log.info("����QQ�������","��Ʒ�������,SKU:"+sku);
					}
				}
			}
			else
			{
				//�¼���Ʒδ���ÿ�棬ȡĬ�Ͼ�����
				sql = "select defaultalarmqty from tradecontacts with(nolock)where tradecontactid='"+ tradecontactid +"'" ;
				int defaultalarmqty = SQLHelper.intSelect(conn, sql) ;
				if(newQty <= defaultalarmqty)
					newQty = 0 ;
				Log.error(jobname,"����Ʒδ���þ�����,ȡϵͳĬ�Ͼ�����:"+ defaultalarmqty +",SKU:"+sku);
			}

			//QQ�����ֿ�id
			stockHouseId = skuInfo.getStockhouseId() ;
			
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", "xml") ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("skuId", skuid) ;
			params.put("stockhouseId", stockHouseId) ;
			params.put("stockCount", String.valueOf(newQty)) ;
			
			responseText = sdk.invoke() ;
			
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			if("0".equals(errorCode))
			{
				backupSynReduceStoreNote(jobname, conn, tradecontactid, sku, tid) ;
				Log.info("����QQ������Ʒ���ɹ�,�����š�"+ tid +"��,sku��"+ sku +"��,ԭ���:"+ oldQty +",�¿��:"+ newQty +",������:"+ skuInfo.getStockPayedNum() +",״̬:"+ skuInfo.getStockSaleState() +"") ;
			}
			else if("3831".equals(errorCode))
			{
				Log.error(jobname, "����QQ������Ʒ���ʧ��,�����š�"+ tid +"��,sku��"+ sku +"��,������������"+ newQty +"��,������Ϣ:"+errorCode+"����Ʒ�ѱ���������ܼ��ٿ��������") ;
				backupSynReduceStoreNote(jobname, conn, tradecontactid, sku, tid) ;
			}
			else if("3743".equals(errorCode) || "3756".equals(errorCode))//��3756����Ʒɾ��,��3831����Ʒ��������ܼ��ٿ��
			{
				backupSynReduceStoreNote(jobname, conn, tradecontactid, sku, tid) ;
				String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname, "����QQ������Ʒ���ʧ��,�����š�"+ tid +"��,sku��"+ sku +"��,������������"+ newQty +"��,������Ϣ:"+errorCode+errorMessage) ;
				Log.error("", "encoding="+encoding) ;
				Log.error("", "cooperatorId="+cooperatorId) ;
				Log.error("", "skuid="+skuid) ;
				Log.error("", "stockHouseId="+stockHouseId) ;
				Log.error("", "newQty="+newQty) ;
			}
			else
			{
				String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname, "����QQ������Ʒ���ʧ��,�����š�"+ tid +"��,sku��"+ sku +"��,������������"+ newQty +"��,������Ϣ:"+errorCode+errorMessage) ;
				Log.error("", "encoding="+encoding) ;
				Log.error("", "cooperatorId="+cooperatorId) ;
				Log.error("", "skuid="+skuid) ;
				Log.error("", "stockHouseId="+stockHouseId) ;
				Log.error("", "newQty="+newQty) ;
			}
		} catch (Exception e) 
		{
			Log.error(jobname, "����QQ������Ʒ���ʧ��,�����š�"+ tid +"��,sku��"+ sku +"��,������Ϣ:"+e.getMessage()+",����:"+responseText) ;
		}
	}
	
	//��ȡQQ������Ʒ��Ϣ
	public static Goods getSKUInfo(String jobname,String skuid,Hashtable<String, String> inputParams)
	{
		String accessToken = inputParams.get("accessToken") ;
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String encoding = inputParams.get("encoding") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		long uin = Long.parseLong(inputParams.get("uin")) ;
		
		Goods goods = new Goods() ;
		String responseText = "" ;
		String uri = "/item/getSKUInfo.xhtml" ;
		try
		{
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", "xml") ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("skuId", skuid) ;
			
			responseText = sdk.invoke() ;
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			if(!"0".equals(errorCode))
			{
				String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname, "��ȡQQ������Ʒ��Ϣʧ��,skuid��"+ skuid +"��,������Ϣ:"+errorCode+","+errorMessage) ;
				return goods ;
			}
			
			String skuId = DOMHelper.getSubElementVauleByName(resultElement, "skuId") ;
			String skuTitle = DOMHelper.getSubElementVauleByName(resultElement, "skuTitle") ;
			String skufrontTitle = DOMHelper.getSubElementVauleByName(resultElement, "skufrontTitle") ;
			String skuSubtitle = DOMHelper.getSubElementVauleByName(resultElement, "skuSubtitle") ;
			String attr = DOMHelper.getSubElementVauleByName(resultElement, "attr") ;
			String classId = DOMHelper.getSubElementVauleByName(resultElement, "classId") ;
			String buyLimit = DOMHelper.getSubElementVauleByName(resultElement, "buyLimit") ;
			String sellerPayFreight = DOMHelper.getSubElementVauleByName(resultElement, "sellerPayFreight") ;
			String skuLocalCode = DOMHelper.getSubElementVauleByName(resultElement, "skuLocalCode") ;
			String producerBarCode = DOMHelper.getSubElementVauleByName(resultElement, "producerBarCode") ;
			String skuBarcode = DOMHelper.getSubElementVauleByName(resultElement, "skuBarcode") ;
			String marketPrice = DOMHelper.getSubElementVauleByName(resultElement, "marketPrice") ;
			String size = DOMHelper.getSubElementVauleByName(resultElement, "size") ;
			String weight = DOMHelper.getSubElementVauleByName(resultElement, "weight") ;
			String skuSearchfactor = DOMHelper.getSubElementVauleByName(resultElement, "skuSearchfactor") ;
			String skuVatrate = DOMHelper.getSubElementVauleByName(resultElement, "skuVatrate") ;
			String present = DOMHelper.getSubElementVauleByName(resultElement, "present") ;
			String spuId = DOMHelper.getSubElementVauleByName(resultElement, "spuId") ;
			String shopCategoryAttr = DOMHelper.getSubElementVauleByName(resultElement, "shopCategoryAttr") ;
			String lastUpdateTime = DOMHelper.getSubElementVauleByName(resultElement, "lastUpdateTime") ;
			String cannotReturned = DOMHelper.getSubElementVauleByName(resultElement, "cannotReturned") ;
			String cannotChange = DOMHelper.getSubElementVauleByName(resultElement, "cannotChange") ;
			String secondhand = DOMHelper.getSubElementVauleByName(resultElement, "secondhand") ;
			String noPostage = DOMHelper.getSubElementVauleByName(resultElement, "noPostage") ;
			
			if("".equals(skuSearchfactor) || skuSearchfactor == null)
				skuSearchfactor = "0.0" ;
						
			goods.setSkuId(skuId) ;
			goods.setSkuTitle(skuTitle) ;
			goods.setSkufrontTitle(skufrontTitle) ;
			goods.setSkuSubtitle(skuSubtitle) ;
			goods.setAttr(attr) ;
			goods.setClassId(classId) ;
			goods.setBuyLimit(buyLimit) ;
			goods.setSellerPayFreight(sellerPayFreight) ;
			goods.setSkuLocalCode(skuLocalCode) ;
			goods.setSkuBarcode(skuBarcode) ;
			goods.setProducerBarCode(producerBarCode) ;
			goods.setMarketPrice(Float.valueOf(marketPrice)) ;
			goods.setSize(Float.valueOf(size)) ;
			goods.setWeight(Float.valueOf(weight)) ;
			goods.setSkuSearchfactor(Float.valueOf(skuSearchfactor)) ;
			goods.setSkuVatrate(Float.valueOf(skuVatrate)) ;
			goods.setPresent(present) ;
			goods.setSpuId(spuId) ;
			goods.setShopCategoryAttr(shopCategoryAttr) ;
			goods.setCannotReturned(cannotReturned) ;
			goods.setCannotChange(cannotChange) ;
			goods.setSecondhand(secondhand) ;
			goods.setNoPostage(noPostage) ;
			
			Element stockListElement = (Element) resultElement.getElementsByTagName("stockList").item(0) ;
			NodeList stockNodeList = stockListElement.getElementsByTagName("stock") ;
			ArrayList<SkuInfo> stockList = new ArrayList<SkuInfo>() ;
			for(int i = 0 ; i < stockNodeList.getLength() ; i++)
			{
				Element stock = (Element) stockNodeList.item(i) ;
				String stockhouseId = DOMHelper.getSubElementVauleByName(stock, "stockhouseId") ;
				String stockLocalcode = DOMHelper.getSubElementVauleByName(stock, "stockLocalcode") ;
				String stockLocalBarcode  = DOMHelper.getSubElementVauleByName(stock, "stockLocalBarcode ") ;
				String primeCost = DOMHelper.getSubElementVauleByName(stock, "primeCost") ;
				String price = DOMHelper.getSubElementVauleByName(stock, "price") ;
				String stockCount = DOMHelper.getSubElementVauleByName(stock, "stockCount") ;
				String stockPayedNum = DOMHelper.getSubElementVauleByName(stock, "stockPayedNum") ;
				String restrictedAreas = DOMHelper.getSubElementVauleByName(stock, "restrictedAreas") ;
				String stockSaleState = DOMHelper.getSubElementVauleByName(stock, "stockSaleState") ;
				String stockPromotDesc = DOMHelper.getSubElementVauleByName(stock, "stockPromotDesc") ;
				String vatInvoice = DOMHelper.getSubElementVauleByName(stock, "vatInvoice") ;
				String b2b2cCoupon = DOMHelper.getSubElementVauleByName(stock, "b2b2cCoupon") ;
				String coopertorCoupon = DOMHelper.getSubElementVauleByName(stock, "coopertorCoupon") ;
				String plainInvoice = DOMHelper.getSubElementVauleByName(stock, "plainInvoice") ;
				String plainInvoiceFlag = DOMHelper.getSubElementVauleByName(stock, "plainInvoiceFlag") ;
				
				if("".equals(primeCost) || primeCost == null)
					primeCost = "0" ;
				if("".equals(price) || price == null)
					price = "0" ;
				SkuInfo skuInfo = new SkuInfo() ;
				skuInfo.setStockhouseId(stockhouseId) ;
				skuInfo.setStockLocalcode(stockLocalcode) ;
				skuInfo.setStockLocalBarcode(stockLocalBarcode) ;
				skuInfo.setPrimeCost(Float.valueOf(primeCost)) ;
				skuInfo.setPrice(Float.valueOf(price)) ;
				skuInfo.setStockCount(Integer.parseInt(stockCount)) ;
				skuInfo.setStockPayedNum(Integer.parseInt(stockPayedNum)) ;
				skuInfo.setRestrictedAreas(restrictedAreas) ;
				skuInfo.setStockSaleState(stockSaleState) ;
				skuInfo.setStockPromotDesc(stockPromotDesc) ;
				skuInfo.setVatInvoice(vatInvoice) ;
				skuInfo.setB2b2cCoupon(b2b2cCoupon) ;
				skuInfo.setCoopertorCoupon(coopertorCoupon) ;
				skuInfo.setPlainInvoice(plainInvoice) ;
				skuInfo.setPlainInvoiceFlag(plainInvoiceFlag) ;
				
				stockList.add(skuInfo) ;
			}
			
			goods.setStockList(stockList) ;
			
		} catch (Exception e) 
		{
			Log.error(jobname, "��ȡQQ������Ʒ����ʧ��,������Ϣ:"+e.getMessage()+"����ֵ:"+responseText) ;
			e.printStackTrace() ;
		}
		return goods ;
	}
	
	//��ȡQQ������Ʒ����
	public static String getItemIdBySkuId(String jobname,String sku,Hashtable<String, String> inputParams)
	{
		String accessToken = inputParams.get("accessToken") ;
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String encoding = inputParams.get("encoding") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		long uin = Long.parseLong(inputParams.get("uin")) ;
		
		String itemID = "" ;
		String responseText = "" ;
		String uri = "/item/getItemIdBySkuId.xhtml" ;
		try 
		{
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", "xml") ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("skuId", sku) ;
			
			responseText=sdk.invoke() ;
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			if("1435".equals(errorCode))
			{
				return "" ;
			}
			if(!"0".equals(errorCode))
			{
				String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname, "��ȡQQ������Ʒ����ʧ��,������Ϣ:"+errorCode+errorMessage) ;
				return "" ;
			}
			itemID = DOMHelper.getSubElementVauleByName(resultElement, "itemSkuId") ;
		} catch (Exception e) {
			Log.error(jobname, "��ȡQQ������Ʒ����ʧ��,sku��"+ sku +"��,������Ϣ:"+e.getMessage()+",����:"+responseText) ;
			return null ;
		}
		return itemID ;
	}
	
	/*����SKU�Ϳ������
	 * SKU qty
	 */
	private Vector getGoodsInfo(String jobname,Connection conn,String tradecontactid)
	{
		Vector vtinfo=null;
		try
		{			
			String sql="select tid,sku,qty from ECO_SynReduceStore with(nolock) "
				+"where tradecontactid='"+tradecontactid+"' "
				+"and synflag=0 and sku is not null and sku<>''";
			vtinfo=SQLHelper.multiRowSelect(conn, sql);
		}
		catch(JSQLException e)
		{
			Log.error(jobname, "ȡ��Ʒ��������Ϣ����:"+e.getMessage());
		}
		return vtinfo;
	}
	
	//�ϲ�ͬ���������
	public static Vector sumSynQty(Vector vector) throws Exception
	{
		Vector<Hashtable<String, String>> resultVector = new Vector<Hashtable<String, String>>() ;
		Hashtable<String,String> tempHt = new Hashtable<String, String>() ;
		//��ͬsku�ۼ�
		for(int i = 0 ; i < vector.size() ; i++)
		{
			Hashtable<String, String> ht = (Hashtable<String, String>) vector.get(i) ;
			String sku = ht.get("sku").trim() ;
			String qty = String.valueOf(ht.get("qty")) ;
			if(tempHt.containsKey(sku))
			{
				int newQty = Integer.parseInt(tempHt.get(sku)) + Integer.parseInt(qty);
				tempHt.put(sku, String.valueOf(newQty)) ;
			}
			else
				tempHt.put(sku, qty) ;
		}
		//�����ۼӺ���
		boolean isContain = false ;
		for(Iterator it = tempHt.keySet().iterator() ; it.hasNext() ; )
		{
			String key = (String) it.next() ;
			String value = tempHt.get(key) ;
			isContain = false ;
			for(int j = 0 ; j < vector.size() ; j++)
			{
				Hashtable<String,String> ht = (Hashtable<String, String>) vector.get(j) ;
				
				if(key.equals(ht.get("sku").toString().trim()))
				{
					if(isContain)
					{
						ht.put("qty", "0") ;
						resultVector.add(ht) ;
					}
					else
					{
						ht.put("qty",value) ;
						resultVector.add(ht) ;
						isContain = true ;
					}
				}
			}
		}
		return resultVector ;
	}

	public static Vector sumSynQty2(Vector vector) throws Exception
	{
		Vector resultVector = new Vector<Hashtable<String, String>>() ;
		Hashtable resuleHashtable = new Hashtable<String, Hashtable<String, String>>() ;
		for(int i = 0 ; i < vector.size() ; i++)
		{
			Hashtable<String, String> ht = (Hashtable<String, String>)vector.get(i) ;
			String sku = ht.get("sku").trim() ;
			String tid = ht.get("tid").trim() ;
			String qty = String.valueOf(ht.get("qty")) ;
			if(resuleHashtable.containsKey(sku))
			{
				Hashtable<String, String> tempHt = (Hashtable<String, String>)resuleHashtable.get(sku) ;
				resuleHashtable.remove(sku) ;
				String newTid = tempHt.get("tid")+","+tid ;
				int newQty = Integer.parseInt(String.valueOf(tempHt.get("qty"))) + Integer.parseInt(qty) ;
				tempHt.put("tid", newTid) ;
				tempHt.put("qty", String.valueOf(newQty)) ;
				resuleHashtable.put(sku, tempHt) ;
			}
			else
				resuleHashtable.put(sku, ht) ;
		}
		resultVector.addAll(resuleHashtable.values()) ;
		return resultVector ;
	}
	public static void backupSynReduceStoreNote(String jobname,Connection conn,String tradecontactid,String sku,String tid) throws Exception
	{
		String[] tidArray = tid.split(",") ;
		for(int i = 0 ; i < tidArray.length ; i++)
			StockManager.bakSynReduceStore(jobname, conn, tradecontactid, tidArray[i], sku);
	}
}
