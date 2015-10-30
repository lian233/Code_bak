package com.wofu.ecommerce.dangdang;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;

public class OrderDelivery extends Thread {

	private static String jobname = "当当订单发货处理作业";
	private static String encoding = Params.encoding ;
	private static String codDeliveryBeginTime = Params.codDeliveryBeginTime ;
	private static String codDeliveryEndTime = Params.codDeliveryEndTime ;
	private static String tradecontactid=Params.tradecontactid ;
	private static Hashtable<String, String> htComCode = Params.htComCode ;
	private static Hashtable<String, String> htComTel = Params.htComTel ;
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {
				Dangdang.setCurrentDate_DevOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.dangdang.Params.dbname);
				doDelivery(connection,getDeliveryOrders(connection));		
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.dangdang.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Throwable
	{
		String sql = "" ;
		Log.info("本次订单发货的数量为:　"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString().replaceAll("[?]", "");
			String postCompany = hto.get("post_company").toString();
			String postNo = hto.get("post_no").toString();
			System.out.println(postCompany);
			try 
			{
				boolean success = false ;
				if("COD".equalsIgnoreCase(postCompany) || "当当自发".equals(postCompany) || "DANGDANG".equals(postCompany))
					success = codOrderPrint(jobname,orderID) ;//只需要打单即可，不需要标志取货操作success = codDelivery(jobname, orderID) ;
				else
					success = delivery(jobname, conn, hto) ;
				if(success)
				{
					conn.setAutoCommit(false);
	
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
	
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
					SQLHelper.executeSQL(conn, sql);
					
					/*
					if(!"COD".equalsIgnoreCase(postCompany) && !"当当自发".equals(postCompany))
					{
						sql = "select count(*) from ecs_deliveryresult with(nolock) where ordercode='"+orderID+"'";
						if(SQLHelper.intSelect(conn, sql) <= 0)
						{
							sql = "select purdate from customerorder with(nolock) where refsheetid='"+ orderID +"'" ;
							String createTime = SQLHelper.strSelect(conn, sql) ;
							sql = "insert into ecs_deliveryresult(orgid,ordercode,companycode,outsid,trancompanycode,tranoutsid,status,isupdate,resultflag,msg,createtime,updatetime) "
			            		+ "values('28','"+ orderID +"','"+ postCompany +"','"+ postNo +"','','','-2','0','0','','"+ createTime +"','"+createTime+"')" ;
			        		SQLHelper.executeSQL(conn, sql) ;
						}
						else
						{
							sql = "update ecs_deliveryresult set companycode='"+ postCompany +"',outsid='"+ postNo +"' where ordercode='"+ orderID +"'" ;
							Log.info(sql) ;
							SQLHelper.executeSQL(conn, sql) ;
						}
					}
					*/
					
					conn.commit();
					conn.setAutoCommit(true);	
				}
			}
			catch (Throwable e) 
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.info("更新发货信息失败，当当单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
				continue ;
			}
			
		}
		Log.info("本次订单发货任务完毕!");
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			//当当COD订单只在该时间段内更新取货标志
			Date currentDateTime = new Date(System.currentTimeMillis()) ;
			Date begin = Formatter.parseDate(Formatter.format(currentDateTime, Formatter.DATE_FORMAT) + " "+codDeliveryBeginTime, Formatter.DATE_TIME_FORMAT) ;
			Date end = Formatter.parseDate(Formatter.format(currentDateTime, Formatter.DATE_FORMAT) + " "+codDeliveryEndTime, Formatter.DATE_TIME_FORMAT) ;
			
			int beginFlag = currentDateTime.compareTo(begin) ;
			int endFlag = currentDateTime.compareTo(end) ;
			if(beginFlag >= 0 && endFlag <= 0)
			{
				sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ tradecontactid + "' and b.companycode=c.companycode";
			}
			else
			{
				sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ tradecontactid + "' and b.companycode=c.companycode and b.companyCode <> 'COD'";
			}
			
	
			Vector vt=SQLHelper.multiRowSelect(conn, sql);

			for(int k=0; k<vt.size();k++)
			{
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(k);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString());
				ht.put("post_company", hto.get("companycode").toString());
				ht.put("post_company_tel", hto.get("companycode")) ;
				ht.put("post_no", hto.get("outsid").toString());
				//Log.info(ht.toString()) ;
				vtorders.add(ht);
			}
		}
		catch(Throwable e)
		{
			Log.error(jobname, "查询发货单信息出错:"+e.getMessage());
			e.printStackTrace() ;
		}
		return vtorders;
	}
	
	private static String getDeliveryXmlStr(Connection conn,Hashtable ht)
	{
		StringBuffer sb = new StringBuffer() ;
		//String sheetid = ht.get("sheetid").toString() ;
		String orderid = ht.get("orderid").toString() ;
		String post_company = htComCode.get(ht.get("post_company").toString()) ;
		String post_company_tel = htComTel.get(ht.get("post_company_tel").toString()) ;
		String post_no = ht.get("post_no").toString() ;
		String sql ="select distinct a.skuid,a.num from ns_orderItem a with(nolock),ns_customerOrder  b with(nolock) where a.sheetid=b.sheetid and b.tid='"+orderid+"'";// order by a.sheetid desc";
		//String sql = "select skuid,num from ns_orderItem with(nolock) where sheetid=(select top 1 sheetid from ns_customerOrder with(nolock) where tid='"+orderid+"' order by sheetid desc)" ;
		try 
		{	
			sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>") ;
			sb.append("<request>") ;
			sb.append("<functionID>dangdang.order.goods.send</functionID>");
			sb.append("<time>").append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("</time>");
			sb.append("<OrdersList>");
			sb.append("<OrderInfo>");
			sb.append("<orderID>").append(orderid).append("</orderID>");
			sb.append("<logisticsName>").append(post_company).append("</logisticsName>");
			sb.append("<logisticsTel>").append(post_company_tel).append("</logisticsTel>");
			sb.append("<logisticsOrderID>").append(post_no).append("</logisticsOrderID>");
			sb.append("<SendGoodsList>") ;
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			if(vt.size()==0){
				sql="select distinct a.skuid,a.num from ns_orderItembak a with(nolock),ns_customerOrderbak  b with(nolock) where a.sheetid=b.sheetid and b.tid='"+orderid+"'";// order by a.sheetid desc";
				vt=SQLHelper.multiRowSelect(conn, sql);
			}
			for(int i=0; i<vt.size();i++)
			{
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				sb.append("<ItemInfo>");
				sb.append("<itemID>").append(hto.get("skuid")).append("</itemID>");
				sb.append("<sendGoodsCount>").append(String.valueOf(hto.get("num"))).append("</sendGoodsCount>");
				sb.append("</ItemInfo>");
			}
			sb.append("</SendGoodsList>");
			sb.append("</OrderInfo>");
			sb.append("</OrdersList>");
			sb.append("</request> ");
		} catch (Throwable e) {
			Log.error("更新当当发货信息", "生成xml字符串失败，错误信息："+e.getMessage()) ;
			return "" ;
		}
		return sb.toString() ;
	}
		
	//获取当当打单数据
	private static boolean codOrderPrint(String jobname,String orderid)
	{
		boolean flag = false ;
		try 
		{
			Date temp = new Date();
			//方法名
			String methodName="dangdang.order.receipt.details.get";
			//生成验证码 --md5;加密
			String sign = CommHelper.getSign(Params.app_Secret, Params.app_key, methodName, Params.session,temp) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
			params.put("app_key",Params.app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",Params.session);
			params.put("sign_method","md5");
			params.put("o", orderid) ;
			//Log.info(requestUrl);
			String responseText = CommHelper.sendRequest(Params.url,"GET",params,"");
			
			Document doc = DOMHelper.newDocument(responseText, encoding) ;
			Element result = doc.getDocumentElement() ;
			
			//判断有无错误
			if(DOMHelper.ElementIsExists(result, "Error"))
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				
				Log.error("COD订单打单", "打单失败,订单号:"+ orderid +",错误信息:"+operCode+","+operation);
				return flag;
			}
			
			Element orderCourierReceiptDetails = (Element) result.getElementsByTagName("orderCourierReceiptDetails").item(0) ;
			Element courierReceiptDetail = (Element) orderCourierReceiptDetails.getElementsByTagName("courierReceiptDetail").item(0) ;
			String orderID = DOMHelper.getSubElementVauleByName(courierReceiptDetail, "orderID") ;
			String operCode = DOMHelper.getSubElementVauleByName(courierReceiptDetail, "operCode") ;
			if(orderid.equals(orderID) && "".equals(operCode))
			{
				Log.info("COD订单打单成功，当当订单号："+orderid) ;
				flag = true ;
			}
			else
			{
				String operation = DOMHelper.getSubElementVauleByName(courierReceiptDetail, "operation") ;
				Log.error("COD订单打单", "打单失败,订单号:"+ orderid +",错误信息:"+operCode+","+operation);
				flag = false ;
			}
			
			return flag ;
			
		} catch (Throwable e) {
			Log.error(jobname, "获取失败，当当订单号："+orderid+",错误信息："+e.getMessage()) ;
			return false ;
		}
	}
	//当当COD订单发货 不需要操作
	/*private static boolean codDelivery(String jobname,String orderid)
	{
		boolean flag = false ;
		try 
		{
			if(!codOrderPrint(jobname,orderid))
				return flag;
			String gbkValuesStr = Coded.getEncode(gShopID, encoding).concat(Coded.getEncode(orderid, encoding)).concat(Coded.getEncode(key, encoding)) ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(url).append("/updateOrdersSendGoodsStatus.php") ;
			String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("gShopID", gShopID) ;
			params.put("o", orderid) ;
			params.put("validateString", validateString) ;
			
			String requestUrl = sb.toString() ;
			String responseText = CommHelper.sendRequest(requestUrl, "GET", params, "=") ;
			//System.out.println(responseText) ;
			Document doc = DOMHelper.newDocument(responseText, encoding) ;
			Element result = doc.getDocumentElement() ;
//			判断返回是否正确
			try
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error("更新当当COD发货", "上传发货信息失败，"+operCode+":"+operation);
					return flag;
				}
			} catch (Exception e) {
			}
			Element itemsIDList = (Element) result.getElementsByTagName("ItemsIDList").item(0) ;
			NodeList itemIDInfoList = result.getElementsByTagName("ItemIDInfo") ;
			for(int i = 0 ; i < itemIDInfoList.getLength() ; i++ )
			{
				Element itemIDInfo = (Element) itemIDInfoList.item(i) ;
				String itemID = DOMHelper.getSubElementVauleByName(itemIDInfo, "itemID") ;
				String operCode = DOMHelper.getSubElementVauleByName(itemIDInfo, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(itemIDInfo, "operation") ;
				if("0".equals(operCode))
				{
					Log.info("更新当当COD订单发货成功，订单号:"+orderid) ;
					flag = true ;
				}
				else
				{
					Log.error("更新当当COD发货订单","更新当当COD订单发货失败，订单号:"+orderid+"。错误信息:"+operCode+operation) ;
					flag = false ;
				}
			}
			return flag ;
		} catch (Exception e) {
			// TODO: handle exception
			Log.error("更新当当COD发货订单","更新当当COD订单发货成功，订单号:"+orderid+".错误信息:"+e.getMessage()) ;
			return false ;
		}
	}
*/
	//自发货订单--更新发货状态
	private  static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto)
	{
		boolean flag = false ;
		String orderID = hto.get("orderid").toString();
		String postCompany = htComCode.get(hto.get("post_company").toString());
		String postNo = hto.get("post_no").toString();
		try 
		{	
			//方法名
			Date temp = new Date();
			String methodName="dangdang.order.goods.send";
			//生成验证码 --md5;加密
			String sign =  CommHelper.getSign(Params.app_Secret, Params.app_key, methodName, Params.session,temp)  ;
			String xml = getDeliveryXmlStr(conn,hto) ;
		
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			//SimpleDateFormat sf = 
			params.put("timestamp",Formatter.format(temp,Formatter.DATE_TIME_FORMAT));
			params.put("app_key",Params.app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",Params.session);
			params.put("sign_method","md5");
			//Log.info("xml: "+xml);
			String responseText = CommHelper.sendRequest(Params.url,"POST",params, "sendGoods="+xml) ;
			//Log.info("resonseText: "+responseText);
			
			Document doc = DOMHelper.newDocument(responseText, "GBK") ;
			Element result = doc.getDocumentElement() ;
			
			//判断返回是否正确
		
			if (DOMHelper.ElementIsExists(result, "Error"))
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					if("1207".equals(operCode) || "51".equals(operCode)){//1207 订单编号不存在，备份数据  51 订单状态错误
						Log.error("更新当当发货", "上传发货信息失败，当当订单号: "+orderID+",operCode="+operCode+",operation="+operation);
						return true;
					}else{
						Log.error("更新当当发货", "上传发货信息失败，当当订单号: "+orderID+",operCode="+operCode+",operation="+operation);
						return flag;
					}
					
				}
			}else if(DOMHelper.ElementIsExists(result, "errorCode")){
				
				if("SHOP访问API一分钟频次超限".equals(DOMHelper.getSubElementVauleByName(result, "errorMessage")) ||DOMHelper.getSubElementVauleByName(result, "errorMessage").indexOf("访问频率过高")!=-1){
					long starttime = System.currentTimeMillis();
					while(System.currentTimeMillis()<starttime+2*60*1000L){
						Thread.sleep(1000L);
					}
				}
			}
			else if(DOMHelper.ElementIsExists(result, "errorCode")){
				String operCode = DOMHelper.getSubElementVauleByName(result, "errorCode") ;
				String errorMessage = DOMHelper.getSubElementVauleByName(result, "errorMessage") ;
				Log.error("更新当当发货", "上传发货信息失败，当当订单号: "+orderID+",errorCode="+operCode+",errorMessage="+errorMessage);
			}
			else
			{
				Element resultInfo = (Element) result.getElementsByTagName("Result").item(0) ;
				Element ordersList = (Element) resultInfo.getElementsByTagName("OrdersList").item(0) ;
				NodeList orderInfoList = ordersList.getElementsByTagName("OrderInfo") ;
				for(int j = 0 ; j < orderInfoList.getLength() ; j++)
				{
					Element orderInfo = (Element) orderInfoList.item(j) ;
					String orderid = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
					String orderOperCode = DOMHelper.getSubElementVauleByName(orderInfo, "orderOperCode") ;
					String orderOperation = DOMHelper.getSubElementVauleByName(orderInfo, "orderOperation") ;
	
					//如果返回状态码为0，且返回单号与提交单号相同，则本次更新成功
					if("0".equals(orderOperCode) && orderID.equals(orderid))
					{
						flag = true ;
						Log.info("更新发货信息成功，当当单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。") ;
					}
					//补发单号，不能上传，重复发货
					else if("35".equals(orderOperCode) || "605".equals(orderOperCode) || "51".equals(orderOperCode))  //订单号错误和订单状态不对
					{
						Log.error(jobname,"更新发货信息失败，当当单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误代码：" + orderOperCode + "错误信息：" + orderOperation) ;
						flag = true ;
					}
					else
					{
						flag = false ;
						Log.error(jobname,"更新发货信息失败，当当单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误代码：" + orderOperCode + "错误信息：" + orderOperation) ;
					}
				}
			}
			
		} catch (Throwable e) {
			Log.info("更新发货信息失败，当当单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			flag=false ;
		}
		
		return flag ;
	}
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
