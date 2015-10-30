package com.wofu.ecommerce.meilisuo;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.ecommerce.meilisuo.Params;
import com.wofu.ecommerce.meilisuo.util.CommHelper;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class UpdateStatus extends Thread {
	
	private static String jobname = "当当订单状态更新作业";
	private static String url = Params.url ;
	private static String encoding = Params.encoding ;
	private static String tradecontactid = Params.tradecontactid ;
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
	
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.meilisuo.Params.dbname);
				//确认审核订单
				doUpdateCheckStatus(connection,Params.tradecontactid);
				//取消当当订单订单
			//	doCancleOrder(jobname, connection, tradecontactid) ;

			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
			
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.meilisuo.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	//订单审核后，更新当当订单状态为：1
	private static void doUpdateCheckStatus(Connection conn ,String tradecontactid) throws Exception
	{
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "1");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			String sql="select tid from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
			String tid=SQLHelper.strSelect(conn, sql);
			
			try
			{
				IntfUtils.backupUpNote(conn, "yongjun",sheetid, "1");
				Log.info("更新订单审核状态成功，订单号："+tid+"，审核状态：1") ;
			}
			catch(Exception je)
			{
				throw new JException(je.getMessage()+" 单号:"+tid+" 更新状态:1");
			}
		}	
	}

	/**
	 * 更新当当订单审核
	 * @param jobname
	 * @param orderID
	 * @param apprStatus	1:审核通过	2:审核不通过
	 */
	/*private static boolean updateOrderState(String jobname,String gShopID,String orderID,String apprStatus)
	{
		boolean flag = false ;
		try 
		{
			String gbkValuesStr = Coded.getEncode(gShopID, encoding).concat(Coded.getEncode(key, encoding)) ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(url).append("/updateMultiOrdersApprStatus.php") ;
			String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("gShopID", gShopID) ;
			params.put("validateString", validateString) ;
			
			String requestUrl = sb.toString() ;
			
			StringBuffer xml = new StringBuffer() ;
			xml.append("updateMultiOrdersApprStatus=") ;
			xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>") ;
			xml.append("<request>") ;
			xml.append("<functionID> updateMultiOrdersApprStatus</functionID>") ;
			xml.append("<time>").append(sdf.format(new Date())).append("</time>") ;
			xml.append("<OrdersList>") ;
			xml.append("<OrderInfo>") ;
			xml.append("<orderID>").append(orderID).append("</orderID>") ;
			xml.append("<apprStatus>").append(apprStatus).append("</apprStatus>") ;
			xml.append("</OrderInfo>") ;
			xml.append("</OrdersList>") ;
			xml.append("</request>") ;
			
			String responseText = CommHelper.sendRequest(requestUrl, "POST", params, xml.toString()) ;
			
			System.out.println(responseText) ;
			Document doc = DOMHelper.newDocument(responseText, "GBK") ;
			Element result = doc.getDocumentElement() ;
			
//			判断返回是否正确
			try
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error("更新当当订单审核状态", "上传发货信息失败，审核状态："+apprStatus+"，错误信息："+operCode+"："+operation);
					flag = false ;
					return flag ;
				}
			} catch (Exception e) {
			}
			
			Element response = (Element)result.getElementsByTagName("response").item(0) ;
			Element resultInfo = (Element) response.getElementsByTagName("Result").item(0) ;
			String orderid = DOMHelper.getSubElementVauleByName(resultInfo, "orderID") ;
			String operCode = DOMHelper.getSubElementVauleByName(resultInfo, "operCode") ;
			String operation = DOMHelper.getSubElementVauleByName(resultInfo, "operation") ;
			if("0".equals(operCode) && orderID.equals(orderid))
			{
				Log.info("更新订单审核状态成功，订单号："+orderID+"，审核状态："+apprStatus) ;
				flag = true ;
			}
			else
			{
				Log.error(jobname, "更新订单审核状态失败，订单号："+orderID+"，审核状态："+apprStatus+"。错误信息："+operCode+":"+operation) ;
				flag = false ;
			}
			
			
		} catch (Exception e) 
		{
			Log.error(jobname, "更新当当订单审核状态失败，订单号：审核状态：错误信息："+e.getLocalizedMessage()) ;
			flag = false ;
		}
		return flag ;
	}*/

	/*//取消当当订单		800003:全部缺货 800004：无法送达 800005：顾客要求退款
	private static boolean canaleOrder(String jobname,String orderID,String cancleReason)
	{
		boolean flag = false ;
		try 
		{
			//方法名
			String methodName="suning.order.cancel";
			//生成验证码 --md5;加密
			String sign = CommHelper.getSign(Params.appsecret, Params.appKey, methodName, Params.session) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			params.put("timestamp",URLEncoder.encode(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT),"GBK"));
			params.put("app_key",Params.app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",Params.session);
			params.put("sign_method","md5");
			params.put("cr", cancleReason) ;
			params.put("o", orderID) ;
			
			String resultText = CommHelper.sendRequest(url, "GET", params, "") ;
			Document doc = DOMHelper.newDocument(resultText, encoding) ;
			Element result = doc.getDocumentElement() ;
//			判断返回是否正确
			try
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error(jobname, "取消订单订单失败,订单号【"+orderID+"】,取消原因:"+cancleReason+",错误原因:"+operCode+operation) ;
					flag = false ;
					return flag ;
				}
			} catch (Exception e) {
			}
			
			Element resultInfo = (Element)result.getElementsByTagName("Result").item(0) ; 
			String orderid = DOMHelper.getSubElementVauleByName(resultInfo, "orderID") ;
			String operCode = DOMHelper.getSubElementVauleByName(resultInfo, "operCode") ;
			String operation = DOMHelper.getSubElementVauleByName(resultInfo, "operation") ;
			if(orderID.equals(orderid) && "0".equals(operCode))
			{
				flag = true ;
				return flag ;
			}
			else
			{
				Log.error(jobname, "取消订单订单失败,订单号【"+orderID+"】,取消原因:"+cancleReason+",错误原因:"+operCode+operation) ;
				flag = false ;
				return flag ;
			}
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "取消订单订单失败,订单号【"+orderID+"】,取消原因:"+cancleReason+",错误原因:"+e.getMessage()) ;
			flag = false ;
			return flag ;
		}
	}
*/
	/*private static void doCancleOrder(String jobname,Connection conn,String tradecontactid)
	{
		try 
		{
			Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "2");
			for (int i=0;i<vts.size();i++)
			{
				Hashtable hts=(Hashtable) vts.get(i);
				String sheetid=hts.get("sheetid").toString();
				
				String sql="select tid,memo from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
				Hashtable htd=SQLHelper.oneRowSelect(conn, sql);
				
				String tid=htd.get("tid").toString();
				String memo=htd.get("memo").toString();
				
				boolean success = canaleOrder(jobname, tid, "800003") ;
				if(success)
				{
					IntfUtils.backupUpNote(conn, "yongjun",sheetid, "2");
//					取消增加其他店库存
					sql="select c.custombc,b.purqty from customerorder a with(nolock),"
							+" customerorderitem b with(nolock),barcode c with(nolock) "
							+" where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid "
							+" and a.refsheetid='"+tid+"'";
					Vector vtc=SQLHelper.multiRowSelect(conn, sql);
					for(int j=0;j<vtc.size();j++)
					{
						Hashtable htc=(Hashtable) vtc.get(j);
						String sku=htc.get("custombc").toString();
						long qty=Double.valueOf(htc.get("purqty").toString()).intValue();
						StockManager.addSynReduceStore(jobname, conn, tradecontactid, "3",tid, sku, qty,false);
					}
					Log.info("取消当当订单成功,订单号【"+tid+"】,取消原因:"+"800003") ;
				}
			}
			
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "取消当当订单失败,错误信息:"+e.getMessage()) ;
		}
	}*/
	

}