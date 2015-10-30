package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;

public class OrderUtils 
{
	private static long daymillis=24*60*60*1000L;
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 更新QQ网购快递配送状态
	 * @param jobname
	 * @param orderResult	订单号,配送状态[0:,1:]
	 * @return	List<Hashtable>,key:orderID,errorCode,errorMessage
	 * @throws Exception
	 */
	public static List<Hashtable> updateDeliveryResult(String jobname, Hashtable orderResult) throws Exception
	{
		String encoding = "gbk" ; 
		String format = "xml" ;
		String cooperatorId = "855005035" ;
		String appOAuthID = "700044939" ;
		String secretOAuthKey = "s1TGmTwb43gftUYX" ;
		String accessToken = "eba8f41a64718ac25c75926d32cb2102" ;
		long uin = 855005035L ;
		
		List<Hashtable> list = new ArrayList<Hashtable>() ;
		String uri = "/deal/signRecvStateV2.xhtml" ;
		String responseText = "" ;
		for (Iterator it = orderResult.keySet().iterator() ; it.hasNext() ;) 
		{
			Hashtable<String, String> returnResunt = new Hashtable<String, String>() ;
			String orderID = String.valueOf(it.next()) ;
			String buyerRecv = String.valueOf(orderResult.get(orderID)) ;
			
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", format) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("dealId", orderID) ;
			params.put("buyerRecv", buyerRecv) ;
			
			responseText = sdk.invoke() ;
			
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
			
			returnResunt.put("orderID", orderID) ;
			returnResunt.put("errorCode", errorCode) ;
			returnResunt.put("errorMessage", errorMessage) ;
			
			list.add(returnResunt) ;			
		}
		return list ;
	}
	
	//获取退换货订单
	public static void getRefundOrders(String jobname,Connection conn, String timeType,String lasttimeconfvalue, String tradecontactid, Hashtable<String, String> inputParams) throws ParseException, JException
	{
		String serviceStates = "SERVICE_STATE_REFUND;SERVICE_STATE_REPLACE;SERVICE_STATE_REPAIR" ;
		String sql = "" ;
		Date configTime = new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime());
		Date lastModifiedTime = configTime ;
		Date sTime=new Date(configTime.getTime()+1000L);
		Date eTime=new Date(configTime.getTime()+daymillis);
		
		String timeBegin = Formatter.format(sTime, Formatter.DATE_TIME_FORMAT) ;
		String timeEnd = Formatter.format(eTime, Formatter.DATE_TIME_FORMAT) ;
		try 
		{
			List<Hashtable<String, String>> orderIdList = new ArrayList<Hashtable<String,String>>() ;
			//获取所有售后单id
			String states[] = serviceStates.split(";") ;
			for(int i = 0 ; i < states.length ; i ++)
				orderIdList.addAll(getRefundOrderIdList(jobname, states[i], timeType, timeBegin, timeEnd, inputParams)) ;
			//获取所有售后单信息
			for(int j = 0 ; j < orderIdList.size() ; j++)
			{
				Hashtable<String, String> orderInfo = orderIdList.get(j) ;
				String serviceId = orderInfo.get("serviceId") ;
				RefundOrder order = getRefundOrderByID(jobname, serviceId,inputParams) ;
				if(order == null || !serviceId.equals(order.getServiceId()))
					continue ;//获取订单失败
				//获取商家skuid
				String outerSkuId = getItemIdBySkuId(jobname, order.getItemSkuId(), inputParams) ;
				if("".equals(outerSkuId))
				{
					Log.error(jobname, "找不到QQ网购商品资料,售后单号【"+ serviceId +"】,商品编码【"+ order.getItemSkuId() +"】") ;
					continue ;
				}
				
				sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+tradecontactid;
	            String inshopid=SQLHelper.strSelect(conn, sql);
				
	            conn.setAutoCommit(false);	
	            
	            sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
				String sheetid=SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql,"取接口单号出错!");
				//退款金额不进去系统，暂做清零处理
				order.setSuggestRefundMoney("0.0") ;
				order.setRefundMoney("0.0") ;
				order.setItemPrice("0.0") ;
				order.setTotalFee("0.0") ;
				
				sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , "
					+ "Created , Modified , OrderStatus , Status , GoodStatus , "
                    + " HasGoodReturn ,RefundFee , Payment , Reason,Description ,"
                    + " Title , Price , Num , GoodReturnTime , Sid , "
                    + " TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ," 
                    + " Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo,skuid)"
                    + " values('" + sheetid + "' , '" + serviceId + "' , '" + order.getDisTradeId() + "' , '"+ order.getCftDealId() +"' , '"+ order.getBuyerId() +"' ,"
                    + "'" + order.getCreateTime() + "','" + order.getLastUpdateTime() + "','','" + order.getServiceState() + "','',"
                    + "'1','"+ order.getSuggestRefundMoney() +"','"+ order.getRefundMoney() +"','" + order.getReturnReason() + "','" + order.getReturnDetail() + "',"
                    + "'" + order.getItemTitle() + "','" + order.getItemPrice() + "','"+ order.getItemCount() +"','" + order.getAcceptTime() + "','"+ order.getRecvLogistics() +"',"
                    + "'"+ order.getTotalFee() +"','','','" + outerSkuId + "','"+ order.getWlCompany() +"'," 
                    + "'','"+ order.getRecvAddress() +"','" + inshopid + "','" + order.getDisWdealId() + "','" + order.getRecvName() + "','"+ order.getRecvMobile() +"','"+ order.getCftDealId() +"','"+ order.getItemSkuId() +"')" ;

					SQLHelper.executeSQL(conn,sql);
					Log.info(sql) ;
				
				 //加入到通知表
	            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
	                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					SQLHelper.executeSQL(conn, sql);
					
				Log.info(jobname,"接口单号:"+sheetid+",订单号:"+ order.getDisWdealId() +",退货订单号:"+serviceId+"，明细单号："+ order.getDisTradeId() +"，订单更新时间:"+ order.getLastUpdateTime());
				conn.commit();
				conn.setAutoCommit(true);
				
				Date lastUpdateTime = Formatter.parseDate(orderInfo.get("lastUpdateTime"), Formatter.DATE_TIME_FORMAT) ;
				if(lastModifiedTime.compareTo(lastUpdateTime) < 0)
					lastModifiedTime = lastUpdateTime ;
			}
			
			//更新下次取订单时间
			if(lastModifiedTime.compareTo(configTime) > 0)
			{
				String timeValue = Formatter.format(lastModifiedTime, Formatter.DATE_TIME_FORMAT) ;
				PublicUtils.setConfig(conn, lasttimeconfvalue, timeValue);
			}
			else if (dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).compareTo(dateformat.parse(Formatter.format(lastModifiedTime,Formatter.DATE_FORMAT)))>0)
			{							            	
				PublicUtils.setConfig(conn,lasttimeconfvalue,Formatter.format((new Date(lastModifiedTime.getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00");
			}
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "获取QQ网购退换货单失败,错误信息:" + e.getMessage()) ;
		}
	}
	
	//获取退货单明细
	public static RefundOrder getRefundOrderByID(String jobname,String serviceId,Hashtable<String, String> inputParams)
	{
		RefundOrder refundOrder = new RefundOrder() ;
		String uri = "/afterservice/queryServiceDetail.xhtml" ;
		String responseText = "" ;
		
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String accessToken = inputParams.get("accessToken") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		String encoding = inputParams.get("encoding") ;
		String uin = inputParams.get("uin") ;
		String format = inputParams.get("format") ;
		try 
		{
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", format) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("serviceId", serviceId) ;
			params.put("isClose", "SERVICE_OPEN") ;
			
			responseText = sdk.invoke() ;
			
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			if(!"0".equals(errorCode))
			{
				String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname, "获取退换货订单明细失败,售后单号【"+ serviceId +"】,错误信息:" + errorCode + "," + errorMessage) ;
				return null ;
			}
			
			String serviceState = DOMHelper.getSubElementVauleByName(resultElement, "serviceState") ;
			String serviceType = DOMHelper.getSubElementVauleByName(resultElement, "serviceType") ;
			String wish = DOMHelper.getSubElementVauleByName(resultElement, "wish") ;
			String isStress = DOMHelper.getSubElementVauleByName(resultElement, "isStress") ;
			String createTime = DOMHelper.getSubElementVauleByName(resultElement, "createTime") ;
			String acceptTime = DOMHelper.getSubElementVauleByName(resultElement, "acceptTime") ;
			String verifyTime = DOMHelper.getSubElementVauleByName(resultElement, "verifyTime") ;
			String doneTime = DOMHelper.getSubElementVauleByName(resultElement, "doneTime") ;
			String lastUpdateTime = DOMHelper.getSubElementVauleByName(resultElement, "lastUpdateTime") ;
			String isClose = DOMHelper.getSubElementVauleByName(resultElement, "isClose") ;
			
			refundOrder.setServiceState(serviceState) ;
			refundOrder.setServiceType(serviceType) ;
			refundOrder.setWish(wish) ;
			refundOrder.setIsStress(isStress) ;
			refundOrder.setCreateTime(createTime) ;
			refundOrder.setAcceptTime(acceptTime) ;
			refundOrder.setVerifyTime(verifyTime) ;
			refundOrder.setDoneTime(doneTime) ;
			refundOrder.setLastUpdateTime(lastUpdateTime) ;
			refundOrder.setIsClose(isClose) ;
			
			String cftDealId = DOMHelper.getSubElementVauleByName(resultElement, "cftDealId") ;
			String bdealId = DOMHelper.getSubElementVauleByName(resultElement, "bdealId") ;
			String disWdealId = DOMHelper.getSubElementVauleByName(resultElement, "disWdealId") ;
			String disTradeId = DOMHelper.getSubElementVauleByName(resultElement, "disTradeId") ;
			String tradeEndTime = DOMHelper.getSubElementVauleByName(resultElement, "tradeEndTime") ;
			String payType = DOMHelper.getSubElementVauleByName(resultElement, "payType") ;
			String payChannel = DOMHelper.getSubElementVauleByName(resultElement, "payChannel") ;
			String refundPayType = DOMHelper.getSubElementVauleByName(resultElement, "refundPayType") ;
			String refundChannel = DOMHelper.getSubElementVauleByName(resultElement, "refundChannel") ;
			String refundBank = DOMHelper.getSubElementVauleByName(resultElement, "refundBank") ;
			
			refundOrder.setCftDealId(cftDealId) ;
			refundOrder.setBdealId(bdealId) ;
			refundOrder.setDisWdealId(disWdealId) ;
			refundOrder.setDisTradeId(disTradeId) ;
			refundOrder.setTradeEndTime(tradeEndTime) ;
			refundOrder.setPayType(payType) ;
			refundOrder.setPayChannel(payChannel) ;
			refundOrder.setRefundPayType(refundPayType) ;
			refundOrder.setRefundChannel(refundChannel) ;
			refundOrder.setRefundBank(refundBank) ;
			
			String refundAccount = DOMHelper.getSubElementVauleByName(resultElement, "refundAccount") ;
			String refundUserName = DOMHelper.getSubElementVauleByName(resultElement, "refundUserName") ;
			String cooperatorID = DOMHelper.getSubElementVauleByName(resultElement, "cooperatorId") ;
			String cooperatorName = DOMHelper.getSubElementVauleByName(resultElement, "cooperatorName") ;
			String buyerId = DOMHelper.getSubElementVauleByName(resultElement, "buyerId") ;
			String itemSkuId = DOMHelper.getSubElementVauleByName(resultElement, "itemSkuId") ;
			String itemTitle = DOMHelper.getSubElementVauleByName(resultElement, "itemTitle") ;
			String itemClassId = DOMHelper.getSubElementVauleByName(resultElement, "itemClassId") ;
			String itemSoldPrice = DOMHelper.getSubElementVauleByName(resultElement, "itemSoldPrice") ;
			String itemPrice = DOMHelper.getSubElementVauleByName(resultElement, "itemPrice") ;
			
			refundOrder.setRefundAccount(refundAccount) ;
			refundOrder.setRefundUserName(refundUserName) ;
			refundOrder.setCooperatorId(cooperatorID) ;
			refundOrder.setCooperatorName(cooperatorName) ;
			refundOrder.setBuyerId(buyerId) ;
			refundOrder.setItemSkuId(itemSkuId) ;
			refundOrder.setItemTitle(itemTitle) ;
			refundOrder.setItemClassId(itemClassId) ;
			refundOrder.setItemSoldPrice(itemSoldPrice) ;
			refundOrder.setItemPrice(itemPrice) ;
			
			String itemCount = DOMHelper.getSubElementVauleByName(resultElement, "itemCount") ;
			String refuseItem = DOMHelper.getSubElementVauleByName(resultElement, "refuseItem") ;
			String totalFee = DOMHelper.getSubElementVauleByName(resultElement, "totalFee") ;
			String refundNum = DOMHelper.getSubElementVauleByName(resultElement, "refundNum") ;
			String suggestRefundMoney = DOMHelper.getSubElementVauleByName(resultElement, "suggestRefundMoney") ;
			String refundMoney = DOMHelper.getSubElementVauleByName(resultElement, "refundMoney") ;
			String refundPostFee = DOMHelper.getSubElementVauleByName(resultElement, "refundPostFee") ;
			String returnWay = DOMHelper.getSubElementVauleByName(resultElement, "returnWay") ;
			String returnAddr = DOMHelper.getSubElementVauleByName(resultElement, "returnAddr") ;
			String regionID = DOMHelper.getSubElementVauleByName(resultElement, "regionID") ;
			
			refundOrder.setItemCount(itemCount) ;
			refundOrder.setRefuseItem(refuseItem) ;
			refundOrder.setTotalFee(totalFee) ;
			refundOrder.setRefundNum(refundNum) ;
			refundOrder.setSuggestRefundMoney(suggestRefundMoney) ;
			refundOrder.setRefundMoney(refundMoney) ;
			refundOrder.setRefundPostFee(refundPostFee) ;
			refundOrder.setReturnWay(returnWay) ;
			refundOrder.setReturnAddr(returnAddr) ;
			refundOrder.setRegionID(regionID) ;
			
			String recvName = DOMHelper.getSubElementVauleByName(resultElement, "recvName") ;
			String postCode = DOMHelper.getSubElementVauleByName(resultElement, "postCode") ;
			String recvMobile = DOMHelper.getSubElementVauleByName(resultElement, "recvMobile") ;
			String recvPhone = DOMHelper.getSubElementVauleByName(resultElement, "recvPhone") ;
			String recvAddress = DOMHelper.getSubElementVauleByName(resultElement, "recvAddress") ;
			String recvLogistics = DOMHelper.getSubElementVauleByName(resultElement, "recvLogistics") ;
			String wlCompany = DOMHelper.getSubElementVauleByName(resultElement, "wlCompany") ;
			String recvState = DOMHelper.getSubElementVauleByName(resultElement, "recvState") ;
			String returnReason = DOMHelper.getSubElementVauleByName(resultElement, "returnReason") ;
			String returnDetail = DOMHelper.getSubElementVauleByName(resultElement, "returnDetail") ;
			
			refundOrder.setRecvName(recvName) ;
			refundOrder.setPostCode(postCode) ;
			refundOrder.setRecvMobile(recvMobile) ;
			refundOrder.setRecvPhone(recvPhone) ;
			refundOrder.setRecvAddress(recvAddress) ;
			refundOrder.setRecvLogistics(recvLogistics) ;
			refundOrder.setWlCompany(wlCompany) ;
			refundOrder.setRecvState(recvState) ;
			refundOrder.setReturnReason(returnReason) ;
			refundOrder.setReturnDetail(returnDetail) ;
			
			String attachment = DOMHelper.getSubElementVauleByName(resultElement, "attachment") ;
			String supplierQc = DOMHelper.getSubElementVauleByName(resultElement, "supplierQc") ;
			String supplierAccept = DOMHelper.getSubElementVauleByName(resultElement, "supplierAccept") ;
			String supplierAudit = DOMHelper.getSubElementVauleByName(resultElement, "supplierAudit") ;
			String supplierRefund = DOMHelper.getSubElementVauleByName(resultElement, "supplierRefund") ;
			String supplierRemark = DOMHelper.getSubElementVauleByName(resultElement, "supplierRemark") ;
			String wgRemark = DOMHelper.getSubElementVauleByName(resultElement, "wgRemark") ;
			String adminRemark = DOMHelper.getSubElementVauleByName(resultElement, "adminRemark") ;
			String complain = DOMHelper.getSubElementVauleByName(resultElement, "complain") ;
			String appeal = DOMHelper.getSubElementVauleByName(resultElement, "appeal") ;
			String userCancel = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
			
			refundOrder.setAttachment(attachment) ;
			refundOrder.setSupplierQc(supplierQc) ;
			refundOrder.setSupplierAccept(supplierAccept) ;
			refundOrder.setSupplierAudit(supplierAudit) ;
			refundOrder.setSupplierRefund(supplierRefund) ;
			refundOrder.setSupplierRemark(supplierRemark) ;
			refundOrder.setWgRemark(wgRemark) ;
			refundOrder.setAdminRemark(adminRemark) ;
			refundOrder.setComplain(complain) ;
			refundOrder.setAppeal(appeal) ;
			refundOrder.setUserCancel(userCancel) ;
			
		} catch (Exception e) {
			Log.error(jobname, "获取退换货订单明细失败,售后单号【"+ serviceId +"】,错误信息:"+e.getMessage()+"。返回值:"+ responseText) ;
			return null ;
		}
		return refundOrder ;
	}
	
	//获取退货单订单号列表 售后单状态serviceState:SERVICE_STATE_REFUND 同意退货 SERVICE_STATE_REPLACE 同意换货 SERVICE_STATE_REPAIR 同意返修 
	public static List<Hashtable<String, String>> getRefundOrderIdList(String jobname, String serviceState, String timeType,String timeBegin,String timeEnd, Hashtable<String, String> inputParams)
	{
		List<Hashtable<String, String>> refundOrderIdList = new ArrayList<Hashtable<String, String>>() ;
		String uri = "/afterservice/queryServiceList.xhtml" ;
		String responseText = "" ;
		int pageIndex = 1 ;
		int pageSize = 20 ;
		boolean hasNextPage = true ;
		
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String accessToken = inputParams.get("accessToken") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		String encoding = inputParams.get("encoding") ;
		String uin = inputParams.get("uin") ;
		String format = inputParams.get("format") ;
		try 
		{
			while(hasNextPage)
			{
				PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
				sdk.setCharset(encoding) ;
				HashMap<String, Object> params = sdk.getParams(uri);
				params.put("charset", encoding) ;
				params.put("format", format) ;
				params.put("cooperatorId", cooperatorId) ;
				params.put("serviceState", serviceState) ;
				params.put("timeType", timeType) ;
				params.put("timeBegin", timeBegin) ;
				params.put("timeEnd", timeEnd) ;
				params.put("pageIndex", String.valueOf(pageIndex)) ;
				params.put("pageSize", String.valueOf(pageSize)) ;
				
				responseText = sdk.invoke() ;
				
				Document doc = DOMHelper.newDocument(responseText, encoding);
				Element resultElement = doc.getDocumentElement();
				String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
				if(!"0".equals(errorCode))
				{
					String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
					Log.error(jobname, "获取退换货订单列表失败,状态【"+ serviceState +"】,错误信息:" + errorCode + "," + errorMessage) ;
					return new ArrayList<Hashtable<String, String>>() ;
				}
				Element serviceList = (Element) resultElement.getElementsByTagName("serviceList").item(0) ;
				NodeList serviceInfoNodeList = serviceList.getElementsByTagName("serviceInfo") ;
				for(int i = 0 ; i < serviceInfoNodeList.getLength() ; i++)
				{
					Element serviceInfo = (Element) serviceInfoNodeList.item(i) ; 
					String serviceId = DOMHelper.getSubElementVauleByName(serviceInfo, "serviceId") ;
					String lastUpdateTime = DOMHelper.getSubElementVauleByName(serviceInfo, "lastUpdateTime") ;
					
					Hashtable<String, String> orderId = new Hashtable<String, String>() ;
					orderId.put("serviceId", serviceId) ;
					orderId.put("lastUpdateTime", lastUpdateTime) ;
					
					refundOrderIdList.add(orderId) ;
				}
				//判断是否有下一页
				int pageTotal = Integer.parseInt(DOMHelper.getSubElementVauleByName(resultElement, "pageTotal").trim()) ;
				if(pageIndex < pageTotal)
					pageIndex ++ ;
				else
					hasNextPage = false ;
			}
		} 
		catch (Exception e)
		{
			Log.error(jobname, "获取退换货订单列表失败,状态【"+ serviceState +"】,错误信息:"+e.getMessage()+"。返回值:"+ responseText) ;
		}
		return refundOrderIdList ;
	}
	
	//更新订单审核状态
	public static boolean updateOrderStatus(String jobname,Connection conn,String sheetid,String orderId,String dealCheckVersion,String dealCheckResult,Hashtable<String, String> inputParams)
	{
		boolean flag = false ;
		String responseText = "" ;
		String uri = "/deal/signCheckResultV2.xhtml" ;
		try 
		{
			String appOAuthID = inputParams.get("appOAuthID") ;
			String secretOAuthKey = inputParams.get("secretOAuthKey") ;
			String accessToken = inputParams.get("accessToken") ;
			String cooperatorId = inputParams.get("cooperatorId") ;
			String encoding = inputParams.get("encoding") ;
			String uin = inputParams.get("uin") ;
			String format = inputParams.get("format") ;
			
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", format) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("dealId", orderId) ;
			params.put("dealCheckVersion", dealCheckVersion) ;
			params.put("dealCheckResult", dealCheckResult) ;
			
			responseText = sdk.invoke() ;
			
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			String dealId = DOMHelper.getSubElementVauleByName(resultElement, "dealId").trim() ;
			if("0".equals(errorCode) && orderId.equals(dealId))
			{
				flag = true ;
				Log.info("更新订单审核状态成功,订单号【"+ orderId +"】,状态【"+ dealCheckResult +"】") ;
			}
			else
			{
				flag = false ;
				String errorMessage  = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname,"更新订单审核状态【"+ dealCheckResult +"】失败,错误信息:"+errorCode+errorMessage) ;
			}
		} 
		catch (Exception e) {
			Log.error(jobname, "更新订单审核状态【"+ dealCheckResult +"】失败,错误信息:"+e.getMessage() + "返回值:" + responseText) ;
		}
		return flag ;
	}
	
	//获取QQ网购订单
	public static void getOrdersList(String jobname,Connection conn,String orderState,String timeType,String lasttimeconfvalue,String tradecontactid,String username,boolean isNeedInvoice,Hashtable<String, String> params)
	{
		try
		{
			Date lastModifiedTime = new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime());
			Date sTime=new Date(lastModifiedTime.getTime()+1000L);
			Date eTime=new Date(lastModifiedTime.getTime()+daymillis);
			
			String startTime = Formatter.format(sTime, Formatter.DATE_TIME_FORMAT) ;
			String endTime = Formatter.format(eTime, Formatter.DATE_TIME_FORMAT) ;
			
			//根据订单时间和订单状态获取订单列表
			ArrayList<Hashtable<String, String>> orderIdList = new ArrayList<Hashtable<String, String>>() ;
			//String orderStateList = "STATE_POL_WAIT_PAY;STATE_WAIT_CHECK;STATE_WAIT_SHIPPING;STATE_WAIT_CONFIRM;STATE_DEAL_SUCCESS;STATE_POL_CANCEL;STATE_POL_END;STATE_DEAL_REFUNDING" ;
			String orderStateList = "STATE_POL_WAIT_PAY;STATE_WAIT_SHIPPING;STATE_POL_CANCEL;STATE_POL_END" ;
			String stateArray[] = orderStateList.split(";") ;
			for(int i = 0 ; i < stateArray.length ; i++)
				orderIdList.addAll(getOrderIdList(jobname, stateArray[i], timeType, startTime, endTime, params)) ;
			//遍历每个订单
			for(int j = 0 ; j < orderIdList.size() ; j++)
			{
				Hashtable<String, String> ht = orderIdList.get(j) ;
				String orderID = ht.get("dealId") ;
				String lastUpdateTime = ht.get("lastUpdateTime") ;
				Order order = getOrderByID(jobname, orderID, params) ;
				if(order == null)
				{
					Log.error(jobname, "查询QQ网购订单详细信息失败,订单号【"+ orderState +"】") ;
					return ;
				}
				String state = order.getDealState() ;
				Log.info("订单号【"+ orderID +"】,状态【"+ state +"】,最后修改时间【"+ lastUpdateTime +"】") ;
				//等待买家付款
				if("STATE_POL_WAIT_PAY".equals(state))
				{
					for(int k=0;k<order.getItemList().size();k++)
					{
						OrderItem item = order.getItemList().get(k) ;
						String sku = item.getSkuLocalCode() ;
						long qty = item.getBuyNum() ;
						StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, order.getDealId(), sku, qty);
						StockManager.addSynReduceStore(jobname, conn, tradecontactid, order.getDealState(),order.getDealId(), sku, -qty,false);
					}
				}
				//等待发货
				else if(orderState.equalsIgnoreCase(state))
				{
					if(createInterOrder(conn, order, tradecontactid, username, orderState,lastUpdateTime,isNeedInvoice))
					{
						//创建订单成功减其它店库存
						for(int k=0;k<order.getItemList().size();k++)
						{
							OrderItem item = order.getItemList().get(k) ;
							String sku = item.getSkuLocalCode() ;
							StockManager.deleteWaitPayStock(jobname, conn,tradecontactid, order.getDealId(),sku);
						}
					}
				}
				//交易关闭
				else if("STATE_POL_CANCEL".equals(state) || "STATE_POL_END".equals(state))
				{
					//取消订单
					String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderID + "';select @ret ret;";
					/**
					 * 0
					 * 1
					 * 2 失败
					 * 3
					 * 4
					 * 5
					 */
					int resultCode = SQLHelper.intSelect(conn, sql) ;
					//取消订单失败
					if(resultCode == 2)
					{
						Log.info("QQ网购请求取消订单失败,单号:"+orderID+"");
					}
					else
					{
						for(int k=0;k<order.getItemList().size();k++)
						{
							OrderItem item = order.getItemList().get(k) ;
							String sku = item.getSkuLocalCode() ;
							long qty = item.getBuyNum() ;
							StockManager.deleteWaitPayStock(jobname, conn,tradecontactid, order.getDealId(),sku);
							StockManager.addSynReduceStore(jobname, conn, tradecontactid, order.getDealState(),order.getDealId(), sku, qty,false);
						}
						Log.info("QQ网购请求取消订单成功,单号:"+orderID+"");
					}
				}
				
				//如果当前订单修改时间大于开始时间，则更新最后修改时间
				if(lastModifiedTime.compareTo(Formatter.parseDate(lastUpdateTime, Formatter.DATE_TIME_FORMAT)) < 0)
					lastModifiedTime = Formatter.parseDate(lastUpdateTime, Formatter.DATE_TIME_FORMAT);
			}
			
			try
			   {
				   //如果当前取到的订单时间大于上次取订单时间，则更新取订单时间
				   if (lastModifiedTime.compareTo(sTime)>=0)
				   {
		        		String value=Formatter.format(lastModifiedTime,Formatter.DATE_TIME_FORMAT);
		        		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
				   }
				   else if (dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).compareTo(dateformat.parse(Formatter.format(lastModifiedTime,Formatter.DATE_FORMAT)))>0)
				   {							            	
					   PublicUtils.setConfig(conn,lasttimeconfvalue,Formatter.format((new Date(lastModifiedTime.getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00");
				   }
			   } catch (ParseException e) 
			   {
				// TODO: handle exception
				   throw new JException("不可用的日期格式!"+e.getMessage());
			   }
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "获取QQ网购订单失败,错误信息:"+ e.getMessage()) ;
		}
	}
	
	//生成系统订单
	public static boolean createInterOrder(Connection conn, Order o, String tradecontactid, String username,String orderState,String lastUpdateTime,boolean isNeedInvoice)
	{
		boolean flag = false ;
		try 
		{
			//已审
			if(OrderManager.isCheck("检查QQ网购订单", conn,o.getDealId()))
				return false ;

			//接口表已存在
			if(OrderManager.TidLastModifyIntfExists("检查QQ网购订单", conn,o.getDealId() ,Formatter.parseDate(lastUpdateTime,Formatter.DATE_TIME_FORMAT)))
				return false ;
			
			String sheetid="";
			String sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			/**
			 * 支付方式
			 * PAY_ONLINE:在线支付
			 * PAY_ONRECV:货到付款
			 */
			//接口业务模式，0其它，1在线支付，2货到付款，3自动发货，4分销
			String dealPayType = o.getDealPayType() ;
			if("PAY_ONLINE".equals(dealPayType))
				dealPayType = "1" ;
			else if("PAY_ONRECV".equals(dealPayType))
				dealPayType = "2" ;
			else
				dealPayType = "0" ;
			
			int needInvoice ;//是否需发票：1需要，0不需要,needinvoice 返回值
			//强制加发票
			if(isNeedInvoice)
			{
				needInvoice = 1 ;
				if(o.getInvoiceTitle() == null || o.getInvoiceTitle().equals("个人") || o.getInvoiceTitle().equals("") )
					o.setInvoiceTitle(o.getRecvName()) ;//个人发票开人名
			}
			else
			{
				if("1".equals(o.getHaveInvoice()))
				{
					needInvoice = 1 ;
					if(o.getInvoiceTitle().equals("个人"))
						o.setInvoiceTitle(o.getRecvName()) ;//个人发票开人名
				}
				else
					needInvoice = 0 ;
			}
			
			Log.info("needInvoice="+needInvoice) ;
			
			conn.setAutoCommit(false);
			sheetid=SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql,"取接口单号出错!");

			float postFee = o.getDealShippingFee() ;
			float totalPrice = o.getItemTotalFee() ;
			float sellerDiscount = 0f ;
			
			//加入到单据表
			sql =  "insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , " 
            	+ " type , created , buyermessage , shippingtype , payment , "
				+ " discountfee , adjustfee , status , buyermemo , sellermemo , "
				+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
				+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
				+ " buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , "
				+ " receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , "
				+ " buyeremail , commissionfee , availableconfirmfee , haspostFee , receivedpayment , "
				+ " codfee , codstatus , timeoutactiontime , delivery , deliverySheetID , "
				+ " alipayNo , buyerflag , sellerflag,price , num , title , snapshoturl , snapshot , "
				+ " sellerrate,buyerrate,dealRateState,numiid,promotion,tradefrom,alipayurl,PromotionDetails,TradeContactID,paymode,InvoiceFlag) values(" 
                + "'" + sheetid + "','" + sheetid + "','yongjun','" + o.getDealId() + "','','" + username + "',"
                + "'','" + Formatter.format(o.getDealGenTime(), Formatter.DATE_TIME_FORMAT) + "','" + o.getWdealBuyerRemark() + "','','" + (totalPrice-sellerDiscount+postFee) + "',"
                + "'" + sellerDiscount + "','0.0','"+orderState+"','" + o.getWdealBuyerRemark() + "','"+ o.getSellerMark() +"',"
                + "'','" + Formatter.format(o.getDealPayTime(), Formatter.DATE_TIME_FORMAT) + "','','"+ lastUpdateTime +"','',"
                + "'','','" + o.getItemTotalFee() + "','" + o.getDealShippingFee() + "','"+ o.getCftDealId() +"',"
                + "'" + o.getCftDealId() + "','"+ o.getCftDealId() +"','" + o.getRecvName() + "','','','',"
                + "'" + o.getRecvAddress() +"','','" + o.getRecvMobile() + "','" + o.getRecvPhone() + "','',"
                + "'','','','','',"
            	+ "'','','','','',"
            	+ "'','"+ o.getDealCheckVersion() +"','','" + o.getDealTotalFee() + "','','','','',"
            	+ "'','','','','','qqbuy','','','" + tradecontactid + "','"  + dealPayType + "','" + needInvoice + "')";
			
			SQLHelper.executeSQL(conn, sql);
			
			//加入明细
			ArrayList<OrderItem> itemList = o.getItemList() ;
			for(int i = 0 ; i < itemList.size() ; i++)
			{
				OrderItem item = itemList.get(i) ;
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , " 
                    + " title , sellernick , buyernick , type , created , " 
                    + " refundstatus , outeriid , outerskuid , totalfee , payment , " 
                    + " discountfee , adjustfee , status , timeoutactiontime , owner , " 
                    + " iid , skuPropertiesName , num , price , picPath , " 
                    + " oid , snapShotUrl , snapShot ,modified) values( " 
                    + "'" + sheetid + "','"  + sheetid+"-"+o.getDealId() + String.valueOf(i+1) + "','" + sheetid + "','" + item.getSkuId() + "','" + item.getItemTitle() +"',"
                    + "'" + item.getItemTitle() + "','','" + o.getRecvName() + "','','" + Formatter.format(o.getDealGenTime(),Formatter.DATE_TIME_FORMAT) + "',"
                    + "'','','" + item.getSkuLocalCode() + "','" + item.getTotalFee() + "','" + item.getTotalFee() + "',"
                    + "'','','"+orderState+"','','yongjun',"
                    + "'','','" + item.getBuyNum() + "','" + (item.getTotalFee()/item.getBuyNum()) + "','',"
                    + "'','','','" + o.getLastUpdateTime() + "')";

				SQLHelper.executeSQL(conn, sql);
				
				if(needInvoice == 1)
				{
					//增加发票明细
        			int qty = item.getBuyNum() ;
        			float unitPrice = item.getTotalFee()/item.getBuyNum() ;
        			String itemName = getInvoiceDetail("获取商品款号名称", conn, item.getSkuLocalCode()) ;
        			String unitName = getGoodsUnitName("获取商品单位", conn, item.getSkuLocalCode()) ;

        			if(!"".equals(itemName) && itemName != null)
        			{
        				sql = "insert into ns_InvoiceItem(SheetID,tid,InvoiceTitle,Name,Unit," 
    	    				+ "Qty,Price,Amount,Note) values(" 
    	    				+ "'" + sheetid + "','"+ o.getDealId() + "','" + o.getInvoiceTitle() + "','" + itemName + "','"+ unitName +"'," 
    	    				+ "'" + qty + "','" + unitPrice + "','" + (qty*unitPrice) + "','')" ;
            			
        				SQLHelper.executeSQL(conn, sql);
        			}
        			else
        			{
        				//取捆绑销售商品信息,增加发票信息
        				sql = "select customerCode,PriceRatio from multiskuref with(nolock) where refcustomercode='"+ item.getSkuLocalCode() +"'" ;
        				Vector multisku = SQLHelper.multiRowSelect(conn, sql) ;
        				if(multisku.size() <= 0)
        					Log.error("生成客户发票", "生成客户发票信息失败,找不到内部sku信息,订单号【"+ o.getDealId() +"】,sku【"+ item.getSkuLocalCode() +"】") ;
        				for(int j = 0 ; j < multisku.size() ; j++)
        				{
        					Hashtable skuinfo = (Hashtable) multisku.get(j) ;
        					String sku = skuinfo.get("customerCode").toString() ;
        					float priceRatio = Float.parseFloat(String.valueOf(skuinfo.get("PriceRatio"))) ;
        					itemName = getInvoiceDetail("获取商品款号名称", conn, sku) ;
                			unitName = getGoodsUnitName("获取商品单位", conn, sku) ;
                			sql = "insert into ns_InvoiceItem(SheetID,tid,InvoiceTitle,Name,Unit," 
        	    				+ "Qty,Price,Amount,Note) values(" 
        	    				+ "'" + sheetid + "','"+ o.getDealId() + "','" + o.getInvoiceTitle() + "','" + itemName + "','"+ unitName +"'," 
        	    				+ "'" + item.getBuyNum() + "','" + (unitPrice*priceRatio) + "','" + (qty*unitPrice)*priceRatio + "','')" ;
                			
            				SQLHelper.executeSQL(conn, sql);
        				}
        			}
				}
			}
			 //加入到通知表
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				

            SQLHelper.executeSQL(conn, sql);
        	
        	conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成订单【" + o.getDealId() + "】接口数据成功，接口单号【" + sheetid + "】");      
			flag = true ;
		} catch (Exception e) {
			Log.error("生成接口订单失败", "错误信息:"+e.getMessage()) ;
			flag = false ;
		}
		return flag ;
	}
	
	//获取订单详细信息
	public static Order getOrderByID(String jobname,String orderid,Hashtable<String, String> inputParams)
	{
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String accessToken = inputParams.get("accessToken") ;
		String cooperatorID = inputParams.get("cooperatorId") ;
		String encoding = inputParams.get("encoding") ;
		String uin = inputParams.get("uin") ;
		String format = inputParams.get("format") ;
		
		String uri = "/deal/queryDealDetailV2.xhtml" ;
		String responseText = "" ;
		Order order = new Order() ;
		try 
		{
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
			sdk.setCharset("UTF-8") ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", "UTF-8") ;
			params.put("format", format) ;
			params.put("cooperatorId", cooperatorID) ;
			params.put("dealId", orderid) ;
			
			responseText = sdk.invoke() ;
			
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			if(!"0".equals(errorCode))
			{
				String errorMessage  = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname,"获取QQ网购订单详细信息失败,错误信息:"+errorCode+","+errorMessage) ;
				return null ;
			}
			
			Element dealInfo = (Element) resultElement.getElementsByTagName("dealInfo").item(0) ;
			
			String bdealId = DOMHelper.getSubElementVauleByName(dealInfo, "bdealId") ;
			String dealId = DOMHelper.getSubElementVauleByName(dealInfo, "dealId") ;
			String buyerId = DOMHelper.getSubElementVauleByName(dealInfo, "buyerId") ;
			String cooperatorId = DOMHelper.getSubElementVauleByName(dealInfo, "cooperatorId") ;
			String cooperatorName  = DOMHelper.getSubElementVauleByName(dealInfo, "cooperatorName") ;
			String dealPayType = DOMHelper.getSubElementVauleByName(dealInfo, "dealPayType") ;
			String dealPayTypeDesc = DOMHelper.getSubElementVauleByName(dealInfo, "dealPayTypeDesc") ;
			String cftDealId = DOMHelper.getSubElementVauleByName(dealInfo, "cftDealId") ;
			String dealGenTime = DOMHelper.getSubElementVauleByName(dealInfo, "dealGenTime") ;
			String dealPayTime = DOMHelper.getSubElementVauleByName(dealInfo, "dealPayTime") ;
			String dealCheckTime = DOMHelper.getSubElementVauleByName(dealInfo, "dealCheckTime") ;
			String dealConsignTime = DOMHelper.getSubElementVauleByName(dealInfo, "dealConsignTime") ;
			String dealEndTime = DOMHelper.getSubElementVauleByName(dealInfo, "dealEndTime") ;
			String lastUpdateTime = DOMHelper.getSubElementVauleByName(dealInfo, "lastUpdateTime") ;
			String dealType = DOMHelper.getSubElementVauleByName(dealInfo, "dealType") ;
			String dealTypeDesc = DOMHelper.getSubElementVauleByName(dealInfo, "dealTypeDesc") ;
			String dealState = DOMHelper.getSubElementVauleByName(dealInfo, "dealState") ;
			String dealStateDesc = DOMHelper.getSubElementVauleByName(dealInfo, "dealStateDesc") ;
			
			order.setBdealId(bdealId) ;
			order.setDealId(dealId) ;
			order.setBuyerId(buyerId) ;
			order.setCooperatorId(cooperatorId) ;
			order.setCooperatorName(cooperatorName) ;
			order.setDealPayType(dealPayType) ;
			order.setDealPayTypeDesc(dealPayTypeDesc) ;
			order.setCftDealId(cftDealId) ;
			order.setDealGenTime(dealGenTime) ;
			order.setDealCheckTime(dealCheckTime) ;
			order.setDealPayTime(dealPayTime) ;
			order.setDealConsignTime(dealConsignTime) ;
			order.setDealEndTime(dealEndTime) ;
			order.setLastUpdateTime(lastUpdateTime) ;
			order.setDealType(dealType) ;
			order.setDealTypeDesc(dealTypeDesc) ;
			order.setDealState(dealState) ;
			order.setDealStateDesc(dealStateDesc) ;
			
			//订单属性列表(可出现多个属性;每个属性会对应一个子节点<propertyInfo></propertyInfo>)
			Element dealProperty = (Element) dealInfo.getElementsByTagName("dealProperty").item(0) ;
			NodeList propertyInfoNodeList = dealProperty.getElementsByTagName("propertyInfo") ;
			ArrayList<String> propertyInfoList = new ArrayList<String>() ;
			for(int ii = 0 ; ii < propertyInfoNodeList.getLength() ; ii++)
			{
				Element propertyInfoElement = (Element) propertyInfoNodeList.item(ii) ;
				String propertyInfo = DOMHelper.getSubElementVauleByName(propertyInfoElement, "propertyInfo") ;
				propertyInfoList.add(propertyInfo) ;
			}
			order.setDealProperty(propertyInfoList) ;
			
			String dealCheckVersion = DOMHelper.getSubElementVauleByName(dealInfo, "dealCheckVersion") ;
			String dealCheckDesc = DOMHelper.getSubElementVauleByName(dealInfo, "dealCheckDesc") ;
			String itemTotalFee = DOMHelper.getSubElementVauleByName(dealInfo, "itemTotalFee") ;
			String dealShippingFee = DOMHelper.getSubElementVauleByName(dealInfo, "dealShippingFee") ;
			String recvRegionId = DOMHelper.getSubElementVauleByName(dealInfo, "recvRegionId") ;
			
			order.setDealCheckVersion(dealCheckVersion) ;
			order.setDealCheckDesc(dealCheckDesc) ;
			order.setItemTotalFee(Float.parseFloat(itemTotalFee)/100) ;//默认单位为：分
			order.setDealShippingFee(Float.parseFloat(dealShippingFee)/100) ;
			order.setRecvRegionId(recvRegionId) ;
			
			//收货人信息
			String recvName = DOMHelper.getSubElementVauleByName(dealInfo, "recvName") ;
			String recvMobile = DOMHelper.getSubElementVauleByName(dealInfo, "recvMobile") ;
			String recvPhone = DOMHelper.getSubElementVauleByName(dealInfo, "recvPhone") ;
			String recvPostCode = DOMHelper.getSubElementVauleByName(dealInfo, "recvPostCode") ;
			String recvAddress = DOMHelper.getSubElementVauleByName(dealInfo, "recvAddress") ;
			String wdealBuyerRemark = DOMHelper.getSubElementVauleByName(dealInfo, "wdealBuyerRemark") ;
			String expectRecvDate = DOMHelper.getSubElementVauleByName(dealInfo, "expectRecvDate") ;
			String expectRecvTime = DOMHelper.getSubElementVauleByName(dealInfo, "expectRecvTime") ;
			String haveInvoice = DOMHelper.getSubElementVauleByName(dealInfo, "haveInvoice") ;
			String invoiceTitle = DOMHelper.getSubElementVauleByName(dealInfo, "invoiceTitle") ;
			String expressCompanyId = DOMHelper.getSubElementVauleByName(dealInfo, "expressCompanyId") ;
			String expressName = DOMHelper.getSubElementVauleByName(dealInfo, "expressName") ;
			String expressDealId = DOMHelper.getSubElementVauleByName(dealInfo, "expressDealId") ;
			String expArriveDays = DOMHelper.getSubElementVauleByName(dealInfo, "expArriveDays") ;
			String sendDesc = DOMHelper.getSubElementVauleByName(dealInfo, "sendDesc") ;
			String transportType = DOMHelper.getSubElementVauleByName(dealInfo, "transportType") ;
			String transportTypeDesc = DOMHelper.getSubElementVauleByName(dealInfo, "transportTypeDesc") ;
			String coopConfirmRecvTime = DOMHelper.getSubElementVauleByName(dealInfo, "coopConfirmRecvTime") ;
			String coopConsignTime = DOMHelper.getSubElementVauleByName(dealInfo, "coopConsignTime") ;
			String refundShippingFee = DOMHelper.getSubElementVauleByName(dealInfo, "refundShippingFee") ;
			String realStorehouseId = DOMHelper.getSubElementVauleByName(dealInfo, "realStorehouseId") ;
			String fouthPartyLogisticsType = DOMHelper.getSubElementVauleByName(dealInfo, "fouthPartyLogisticsType") ;
			String dealTotalFee = DOMHelper.getSubElementVauleByName(dealInfo, "dealTotalFee") ;
			String sellerMark = DOMHelper.getSubElementVauleByName(dealInfo, "sellerMark") ;
			
			order.setRecvName(recvName) ;
			order.setRecvMobile(recvMobile) ;
			order.setRecvPhone(recvPhone) ;
			order.setRecvPostcode(recvPostCode) ;
			order.setRecvAddress(recvAddress) ;
			order.setWdealBuyerRemark(wdealBuyerRemark) ;
			order.setExpectRecvDate(expectRecvDate) ;
			order.setExpectRecvTime(expectRecvTime) ;
			order.setHaveInvoice(haveInvoice) ;
			order.setInvoiceTitle(invoiceTitle) ;
			order.setExpressCompanyId(expressCompanyId) ;
			order.setExpressName(expressName) ;
			order.setExpressDealId(expressDealId) ;
			order.setExpArriveDays(expArriveDays) ;
			order.setSendDesc(sendDesc) ;
			order.setTransportType(transportType) ;
			order.setTransportTypeDesc(transportTypeDesc) ;
			order.setCoopConfirmRecvTime(coopConfirmRecvTime) ;
			order.setCoopConsignTime(coopConsignTime) ;
			order.setRefundShippingFee(Float.parseFloat(refundShippingFee)/100) ;
			order.setRealStorehouseId(realStorehouseId) ;
			order.setFouthPartyLogisticsType(fouthPartyLogisticsType) ;
			order.setDealTotalFee(Float.parseFloat(dealTotalFee)/100) ;
			order.setSellerMark(sellerMark) ;
			
			//明细
			Element tradeList = (Element) dealInfo.getElementsByTagName("tradeList").item(0) ;
			NodeList tradeInfoNodeList = tradeList.getElementsByTagName("tradeInfo") ;
			ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>() ;
			for(int i = 0 ; i < tradeInfoNodeList.getLength() ; i++)
			{
				OrderItem item = new OrderItem() ;
				Element tradeInfo = (Element) tradeInfoNodeList.item(i) ;
				String tradeId = DOMHelper.getSubElementVauleByName(tradeInfo, "dealPayType") ;
				String splitPackFlag = DOMHelper.getSubElementVauleByName(tradeInfo, "splitPackFlag") ;
				String tradePayType = DOMHelper.getSubElementVauleByName(tradeInfo, "tradePayType") ;
				String tradePayTypeDesc = DOMHelper.getSubElementVauleByName(tradeInfo, "tradePayTypeDesc") ;
				String totalFee = DOMHelper.getSubElementVauleByName(tradeInfo, "totalFee") ;
				String tradeGenTime = DOMHelper.getSubElementVauleByName(tradeInfo, "tradeGenTime") ;
				String tradeEndTime = DOMHelper.getSubElementVauleByName(tradeInfo, "tradeEndTime") ;
				String tradeCheckTime = DOMHelper.getSubElementVauleByName(tradeInfo, "tradeCheckTime") ;
				String tradePayTime = DOMHelper.getSubElementVauleByName(tradeInfo, "tradePayTime") ;
				String tradeConsignTime = DOMHelper.getSubElementVauleByName(tradeInfo, "tradeConsignTime") ;
				String goodsLastUpdateTime = DOMHelper.getSubElementVauleByName(tradeInfo, "lastUpdateTime") ;
				
				item.setTradeId(tradeId) ;
				item.setSplitPackFlag(splitPackFlag) ;
				item.setTradePayType(tradePayType) ;
				item.setTradePayTypeDesc(tradePayTypeDesc) ;
				item.setTotalFee(Float.parseFloat(totalFee)/100) ;
				item.setTradeGenTime(tradeGenTime) ;
				item.setTradeEndTime(tradeEndTime) ;
				item.setTradeCheckTime(tradeCheckTime) ;
				item.setTradePayTime(tradePayTime) ;
				item.setTradeConsignTime(tradeConsignTime) ;
				item.setGoodsLastUpdateTime(goodsLastUpdateTime) ;
				
				Element tradeProperty = (Element) tradeInfo.getElementsByTagName("tradeProperty").item(0) ;
				String goodsPropertyInfo = DOMHelper.getSubElementVauleByName(tradeProperty, "propertyInfo") ;
				
				
				String buyNum = DOMHelper.getSubElementVauleByName(tradeInfo, "buyNum") ;
				String skuId = DOMHelper.getSubElementVauleByName(tradeInfo, "skuId") ;
				String skuLocalCode = DOMHelper.getSubElementVauleByName(tradeInfo, "skuLocalCode") ;
				String itemId = DOMHelper.getSubElementVauleByName(tradeInfo, "itemId") ;
				String itemPhisicStorage = DOMHelper.getSubElementVauleByName(tradeInfo, "itemPhisicStorage") ;
				String stockhouseId = DOMHelper.getSubElementVauleByName(tradeInfo, "stockhouseId") ;
				String itemStoreId = DOMHelper.getSubElementVauleByName(tradeInfo, "itemStoreId") ;
				String attr = DOMHelper.getSubElementVauleByName(tradeInfo, "attr") ;
				String attrCode = DOMHelper.getSubElementVauleByName(tradeInfo, "attrCode") ;
				String itemSoldPrice = DOMHelper.getSubElementVauleByName(tradeInfo, "itemSoldPrice") ;
				String itemPrice = DOMHelper.getSubElementVauleByName(tradeInfo, "itemPrice") ;
				String itemTitle = DOMHelper.getSubElementVauleByName(tradeInfo, "itemTitle") ;
				String itemType = DOMHelper.getSubElementVauleByName(tradeInfo, "itemType") ;
				String refund = DOMHelper.getSubElementVauleByName(tradeInfo, "refund") ;
				String closeReasonType = DOMHelper.getSubElementVauleByName(tradeInfo, "closeReasonType") ;
				String closeReason = DOMHelper.getSubElementVauleByName(tradeInfo, "closeReason") ;
				String consignItemNum = DOMHelper.getSubElementVauleByName(tradeInfo, "consignItemNum") ;
				String noStockNum = DOMHelper.getSubElementVauleByName(tradeInfo, "noStockNum") ;
				String noStockRefund = DOMHelper.getSubElementVauleByName(tradeInfo, "noStockRefund") ;
				String refuseNum = DOMHelper.getSubElementVauleByName(tradeInfo, "refuseNum") ;
				String refuseRefund = DOMHelper.getSubElementVauleByName(tradeInfo, "refuseRefund") ;
				
				if("".equals(refund))
					refund = "0.0" ;
				
				item.setBuyNum(Integer.parseInt(buyNum)) ;
				item.setSkuId(skuId) ;
				item.setSkuLocalCode(skuLocalCode) ;
				item.setItemId(itemId) ;
				item.setItemPhisicStorage(itemPhisicStorage) ;
				item.setStockhouseId(stockhouseId) ;
				item.setItemStoreId(itemStoreId) ;
				item.setAttr(attr) ;
				item.setAttrCode(attrCode) ;
				item.setItemSoldPrice(Float.parseFloat(itemSoldPrice)/100) ;
				item.setItemPrice(Float.parseFloat(itemPrice)/100) ;
				item.setItemTitle(itemTitle) ;
				item.setItemType(itemType) ;
				item.setRefund(Float.parseFloat(refund)/100) ;
				item.setCloseReasonType(closeReasonType) ;
				item.setCloseReason(closeReason) ;
				item.setConsignItemNum(consignItemNum) ;
				item.setNoStockNum(Integer.parseInt(noStockNum)) ;
				item.setNoStockRefund(Integer.parseInt(noStockRefund)) ;
				item.setRefuseNum(Integer.parseInt(refuseNum)) ;
				item.setRefuseRefund(Float.parseFloat(refuseRefund)/100) ;
				
				
				//活动列表,由活动信息activeInfo组成
				Element activeList = (Element) tradeInfo.getElementsByTagName("activeList").item(0) ;
				NodeList activeInfoNodeList = activeList.getElementsByTagName("activeInfo") ;
				ArrayList<Hashtable<String, String>> activeInfoList = new ArrayList<Hashtable<String,String>>() ;
				for(int j = 0 ; j < activeInfoNodeList.getLength() ; j++)
				{
					Element activeInfo = (Element) activeInfoNodeList.item(j) ;
					String activeId = DOMHelper.getSubElementVauleByName(activeInfo, "activeId") ;
					String activeType = DOMHelper.getSubElementVauleByName(activeInfo, "activeType") ;
					String activeRuleId = DOMHelper.getSubElementVauleByName(activeInfo, "activeRuleId") ;
					String activeDesc = DOMHelper.getSubElementVauleByName(activeInfo, "activeDesc") ;
					String preActivePrice = DOMHelper.getSubElementVauleByName(activeInfo, "preActivePrice") ;
					String favorFee = DOMHelper.getSubElementVauleByName(activeInfo, "favorFee") ;
					String activeParam1 = DOMHelper.getSubElementVauleByName(activeInfo, "activeParam1") ;
					String activeParam2 = DOMHelper.getSubElementVauleByName(activeInfo, "activeParam2") ;
					String activeParam3 = DOMHelper.getSubElementVauleByName(activeInfo, "activeParam3") ;
					String activeParam4 = DOMHelper.getSubElementVauleByName(activeInfo, "activeParam4") ;
					String activeParam5 = DOMHelper.getSubElementVauleByName(activeInfo, "activeParam5") ;		
					
					Hashtable<String, String> ht = new Hashtable<String, String>() ;
					ht.put("activeId", activeId) ;
					ht.put("activeType", activeType) ;
					ht.put("activeRuleId", activeRuleId) ;
					ht.put("activeDesc", activeDesc) ;
					ht.put("preActivePrice", String.valueOf(Float.parseFloat(preActivePrice)/100)) ;
					ht.put("favorFee", String.valueOf(Float.parseFloat(favorFee)/100)) ;
					ht.put("activeParam1", activeParam1) ;
					ht.put("activeParam2", activeParam2) ;
					ht.put("activeParam3", activeParam3) ;
					ht.put("activeParam4", activeParam4) ;
					ht.put("activeParam5", activeParam5) ;
					
					activeInfoList.add(ht) ;
				}
				item.setActiveList(activeInfoList) ;
				
				orderItemList.add(item) ;

			}
			order.setItemList(orderItemList) ;
			//异常商品单列表
			Element abnormalGoodsListElement = (Element) dealInfo.getElementsByTagName("abnormalGoodsList").item(0) ;
			NodeList abnormalGoodsInfoNodeList = abnormalGoodsListElement.getElementsByTagName("abnormalGoodsInfo") ;
			ArrayList<Hashtable<String, String>> abnormalGoodsList = new ArrayList<Hashtable<String,String>>() ;
			for(int l = 0 ; l < abnormalGoodsInfoNodeList.getLength() ; l++)
			{
				Element abnormalGoodsInfo = (Element) abnormalGoodsInfoNodeList.item(l) ;
				String abnormalGoodsId = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "dealPayType") ;
				String tradeId = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "tradeId") ;
				String abnormalType = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "abnormalType") ;
				String abnormalTypeDesc = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "abnormalTypeDesc") ;
				String skuId = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "skuId") ;
				String abnormalNum = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "abnormalNum") ;
				String abnormalDesc = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "abnormalDesc") ;
				String refundFlag = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "refundFlag") ;
				String needRe = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "needRe") ;
				String refundedFee = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "refundedFee") ;
				String abnormalGoodsGenTime = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "abnormalGoodsGenTime") ;
				String abnormalGoodsRefundedTime = DOMHelper.getSubElementVauleByName(abnormalGoodsInfo, "abnormalGoodsRefundedTime") ;
				
				Hashtable<String, String> ht = new Hashtable<String, String>() ;
				ht.put("abnormalGoodsId", abnormalGoodsId) ;
				ht.put("tradeId", tradeId) ;
				ht.put("abnormalType", abnormalType) ;
				ht.put("abnormalTypeDesc", abnormalTypeDesc) ;
				ht.put("skuId", skuId) ;
				ht.put("abnormalNum", abnormalNum) ;
				ht.put("abnormalDesc", abnormalDesc) ;
				ht.put("refundFlag", refundFlag) ;
				ht.put("needRe", needRe) ;
				ht.put("refundedFee", String.valueOf(Float.parseFloat(refundedFee)/100)) ;
				ht.put("abnormalGoodsGenTime", abnormalGoodsGenTime) ;
				ht.put("abnormalGoodsRefundedTime", abnormalGoodsRefundedTime) ;
				
				abnormalGoodsList.add(ht) ;
			}
			order.setAbnormalGoodsList(abnormalGoodsList) ;
			
			String subPackageVersion = DOMHelper.getSubElementVauleByName(dealInfo, "subPackageVersion") ;
			String subPackFlag = DOMHelper.getSubElementVauleByName(dealInfo, "subPackFlag") ;		
			order.setSubPackageVersion(subPackageVersion) ;
			order.setSubPackFlag(subPackFlag) ;
		}
		catch (Exception e) {
			Log.error(jobname, "获取QQ网购订单详细信息失败,错误信息:"+ e.getMessage() + ",返回值:" + responseText) ;
			order = null ;
		}
		return order ;
	}
	//获取QQ网购的订单列表
	public static ArrayList<Hashtable<String, String>> getOrderIdList(String jobname,String dealState,String timeType,String timeBegin,String timeEnd,Hashtable<String, String> inputParams)
	{		
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String accessToken = inputParams.get("accessToken") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		String encoding = inputParams.get("encoding") ;
		String uin = inputParams.get("uin") ;
		String format = inputParams.get("format") ;
		
		String uri = "/deal/queryDealListV2.xhtml" ;
		int pageIndex = 1 ;
		int pageSize = 20 ;
		
		boolean hasNextPage = true ;
		ArrayList<Hashtable<String, String>> orderIdList = new ArrayList<Hashtable<String, String>>() ;
		String responseText = "" ;
		try 
		{
			while(hasNextPage)
			{
				PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
				sdk.setCharset("UTF-8") ;
				HashMap<String, Object> params = sdk.getParams(uri);
				params.put("charset", "UTF-8") ;
				params.put("format", format) ;
				params.put("cooperatorId", cooperatorId) ;
				params.put("dealState", dealState) ;
				params.put("timeType", timeType) ;
				params.put("timeBegin", timeBegin) ;
				params.put("timeEnd", timeEnd) ;
				params.put("pageIndex", String.valueOf(pageIndex)) ;
				params.put("pageSize", String.valueOf(pageSize)) ;
				
				responseText = sdk.invoke() ;
				
				Document doc = DOMHelper.newDocument(responseText, encoding);
				Element resultElement = doc.getDocumentElement();
				String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
				if(!"0".equals(errorCode))
				{
					String errorMessage  = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
					Log.error(jobname,"获取QQ网购订单列表失败,错误信息:"+errorCode+","+errorMessage+",查询状态:"+dealState) ;
					return orderIdList ;
				}
				Element dealList = (Element) resultElement.getElementsByTagName("dealList").item(0) ;
				NodeList dealInfoList = dealList.getElementsByTagName("dealInfo") ;
				for(int i = 0 ; i < dealInfoList.getLength() ; i++)
				{
					Element dealInfo = (Element) dealInfoList.item(i) ;
					String dealId = DOMHelper.getSubElementVauleByName(dealInfo, "dealId") ;
					String lastUpdateTime = DOMHelper.getSubElementVauleByName(dealInfo, "lastUpdateTime") ;
					Hashtable<String, String> ht = new Hashtable<String, String>() ;
					ht.put("dealId", dealId) ;
					ht.put("lastUpdateTime", lastUpdateTime) ;
					orderIdList.add(ht) ;
				}
				//判断是否还有下一页
				String pageTotal = DOMHelper.getSubElementVauleByName(resultElement, "pageTotal").trim() ;
				if(pageIndex < Integer.parseInt(pageTotal))
					pageIndex ++ ;
				else
					hasNextPage = false ;
			}
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "获取QQ网购订单列表失败,错误信息:"+ e.getMessage() +",返回值:"+ responseText) ;
		}
		return orderIdList ;
	}

	//获取发票明细信息
	public static String getInvoiceDetail(String jobname,Connection conn,String sku)
	{
		String detail = "" ;
		try 
		{
			String sql = "select a.customBC+c.name from barcode as a with(nolock),goods as b with(nolock),dept as c with(nolock) " +
			"where a.goodsid=b.goodsid and b.deptid=c.id and a.customBC='"+ sku +"'" ;
			detail = SQLHelper.strSelect(conn, sql) ;
		}
		catch (Exception e) 
		{
			Log.error(jobname, "获取商品款号名称失败,错误信息:"+e.getMessage()) ;
			detail = "" ;
		}
		
		return detail ;
	}
	//获取发票单位
	public static String getGoodsUnitName(String jobname,Connection conn,String sku)
	{
		String unitName = "" ;
		if("".equals(sku) || sku == null)
			return unitName ;
		try 
		{
			String sql ="select unitname from goods as a with(nolock),barcode as b with(nolock) where a.goodsid=b.goodsid and b.customBC='"+ sku +"'" ;
			unitName = SQLHelper.strSelect(conn, sql) ;
		} catch (Exception e) {
			Log.error(jobname, "获取商品单位失败,错误信息:"+e.getMessage()+",sku:"+sku) ;
		}
		return unitName ;
	}
	
	//根据QQ网购商品编码查询商家sku
	public static String getItemIdBySkuId(String jobname,String skuId,Hashtable<String, String> inputParams)
	{
		String itemID = "" ;
		String responseText = "" ;
		String uri = "/item/getItemIdBySkuId.xhtml" ;

		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String accessToken = inputParams.get("accessToken") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		String encoding = inputParams.get("encoding") ;
		String uin = inputParams.get("uin") ;
		String format = inputParams.get("format") ;
		
		try
		{
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", format) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("skuId", skuId) ;
			
			responseText = sdk.invoke() ;
			
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			if("1435".equals(errorCode))
			{
				Log.error(jobname, "找不到QQ网购商品资料,sku【"+ skuId +"】") ;
				return "" ;
			}
			if(!"0".equals(errorCode))
			{
				String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname, "获取QQ网购商品编码失败,错误信息:"+errorCode+errorMessage) ;
				return "" ;
			}
			
			itemID = DOMHelper.getSubElementVauleByName(resultElement, "itemSkuId") ;
			Log.info(itemID) ;
		} catch (Exception e) {
			Log.error(jobname, "获取QQ网购商品编码失败,sku【"+ skuId +"】,错误信息:"+e.getMessage()+",返回:"+responseText) ;
			return "" ;
		}
		return itemID ;
	}
}
