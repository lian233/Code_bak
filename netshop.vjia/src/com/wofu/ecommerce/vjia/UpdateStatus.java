package com.wofu.ecommerce.vjia;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.ecommerce.vjia.Params;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class UpdateStatus extends Thread {
	
	private static String jobname = "vjia订单状态更新作业";
	private static final String strkey = Params.strkey ;
	private static final String striv = Params.striv ;
	private static final String passWord = Params.suppliersign ;
	private static final String userName = Params.supplierid ;
	private static final String swsSupplierID = Params.swssupplierid ;
	private static final String URI = Params.uri ;	
	private static final String wsurl = Params.wsurl ;
	private static final String tradecontactid = String.valueOf(Params.tradecontactid);


	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.vjia.Params.dbname);
				//确认订单
				doUpdateCheckStatus(connection,tradecontactid) ;
				//更新不发货订单
				doUpdateCancelStatus(connection,tradecontactid) ;
				//更新退换货订单入库\拒收状态
				updateReturnOrderStatus(connection) ;
				
			}
			catch (Exception e) 
			{
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} 
			finally 
			{
			
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.vjia.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	//发货
	private static void doUpdateCheckStatus(Connection conn,String tradecontacti) throws Exception
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
				//更新订单状态（订单确认）
				if(updateOrderStatus(conn, tid, "1"))
				{
					IntfUtils.backupUpNote(conn, "yongjun",sheetid, "1");
					Log.info("更新审核状态成功,单号:"+tid+"");
				}
				
			}catch(JException je)
			{
				throw new JException(je.getMessage()+" 单号:"+tid+" 更新状态:1");
			}
		}
	}
	//不发货
	private static void doUpdateCancelStatus(Connection conn, String tradecontactid) throws Exception
	{
		//取得需要同步到vjia不能发货的订单信息
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "2");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			
			String sql="select tid,memo from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
			Hashtable htd=SQLHelper.oneRowSelect(conn, sql);
			
			String tid=htd.get("tid").toString();
			try
			{
				//更新订单状态（订单不能发货）
				if(updateOrderStatus(conn, tid,"0"))
				{
					IntfUtils.backupUpNote(conn, "yongjun",sheetid, "2");
					//取消增加其他店库存
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
					Log.info("更新审核状态成功,单号:"+tid+" 更新状态:2");
				}
				else
					Log.info("更新审核状态失败,单号:"+tid+" 更新状态:2");
				
			}catch(JException je)
			{
				throw new JException(je.getMessage()+" 单号:"+tid+" 更新状态:2");
			}
		}
	}

	
	/**
	 * 更新订单审核状态
	 * @param conn
	 * @param orderID	商城单号
	 * @param isSend	0：不发货　1：发货
	 * @return	成功：true	失败：false
	 * @throws Exception
	 */
	private static boolean updateOrderStatus(Connection conn, String orderID, String isSend) throws Exception
	{
		String DESBarCodeList = getBarcdoeListByOrderID(orderID) ;//订单商品barcode列表
		DESBarCodeList =  DesUtil.DesEncode(DESBarCodeList, strkey, striv) ;
		orderID = DesUtil.DesEncode(orderID, strkey, striv) ;
		
		Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
		bodyParams.put("swsSupplierID", swsSupplierID) ;
		bodyParams.put("DesFormCode", orderID) ;
		bodyParams.put("DESBarCodeList", DESBarCodeList) ;
		bodyParams.put("IsSend", isSend) ;
		
		SoapHeader soapHeader = new SoapHeader() ;
		DESBarCodeList =  DesUtil.DesEncode(DESBarCodeList, strkey, striv) ;
		soapHeader.setUname(userName) ;
		soapHeader.setPassword(passWord) ;
		soapHeader.setUri(URI) ;
		
		SoapBody soapBody = new SoapBody() ;
		soapBody.setRequestname("OrderStatusSetExpress") ;
		soapBody.setUri(URI) ;
		soapBody.setBodyParams(bodyParams) ;
		
		SoapServiceClient client = new SoapServiceClient() ;
		client.setUrl(Params.wsurl+"/SupplierIsCanSendSkuService.asmx") ;
		client.setSoapheader(soapHeader) ;
		client.setSoapbody(soapBody) ;
		
		String result = client.request() ;
		
		Document resultdoc=DOMHelper.newDocument(result);
		Element resultelement=resultdoc.getDocumentElement();
		
		String errorCode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
		if("0".equals(errorCode))
		{
			return true ;
		}
		else
		{
			String resultdetail = DOMHelper.getSubElementVauleByName(resultelement, "resultdetail").trim() ;
			Log.error("更新订单审核状态","更新审核状态失败,错误信息："+ errorCode + "," + resultdetail);
			return false ;
		}
	}
	
	/**
	 * 按订单号取订单信息 商品条码
	 */
	private static String getBarcdoeListByOrderID(String orderID)
	{
		String str = "" ;
		try 
		{
			Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
			bodyParams.put("swsSupplierID", swsSupplierID) ;
			bodyParams.put("DESFormCode", DesUtil.DesEncode(orderID, strkey, striv)) ;
			
			SoapHeader soapHeader = new SoapHeader() ;
			soapHeader.setUname(userName) ;
			soapHeader.setPassword(passWord) ;
			soapHeader.setUri(URI) ;
			
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("GetFormCodeInfo") ;
			soapBody.setUri(URI) ;
			soapBody.setBodyParams(bodyParams) ;
			
			SoapServiceClient client = new SoapServiceClient() ;
			client.setUrl(Params.wsurl+"/GetOrderService.asmx") ;
			client.setSoapheader(soapHeader) ;
			client.setSoapbody(soapBody) ;
			
			String result = client.request() ;
			
			Document resultdoc=DOMHelper.newDocument(result);
			Element resultelement=resultdoc.getDocumentElement();
			Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
			Element[] orderDetail = DOMHelper.getSubElementsByName(resultdetail, "orderdetail") ;
			 for(int j = 0 ; j < orderDetail.length ; j++)
			   {
				   Element detail=(Element) orderDetail[j] ; 
				   String barcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "barcode"), strkey, striv) ;  
				   str += (barcode + "|") ;
			   }
			return str.substring(0, str.length()-1) ;
			
		} catch (Exception e) 
		{
			// TODO: handle exception
			return null ;
		}
	}
	
	private static void updateReturnOrderStatus(Connection conn)
	{
		String sql = "" ;
		try
		{
			//取得需要同步到vjia同意入库的退换货订单
			Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "6");
			for (int i=0;i<vts.size();i++)
			{
				Hashtable hts=(Hashtable) vts.get(i);
				String sheetid=hts.get("sheetid").toString();
				sql="select tid,companyCode,outSid,memo from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
				Hashtable refund = SQLHelper.oneRowSelect(conn, sql) ;
				String orderid = refund.get("tid").toString() ;
				String parcelCompany = Params.htCom.get(refund.get("companyCode").toString()) ;
				String parcelNo = refund.get("sid").toString() ;
				String returnRemark = refund.get("memo").toString() ;

		        if(updateAgreeInStock(orderid, parcelNo, parcelCompany, returnRemark))
		        {
		        	IntfUtils.backupUpNote(conn, "yongjun",sheetid, "2");
		        	Log.info("更新vjia退换货入库成功，接口单号："+sheetid+"，原单号："+refund.get("tid").toString()+",运单号："+parcelNo) ;
		        }
			}
			//取得需要同步到vjia拒收的退换货订单
			vts.clear() ;
			vts=IntfUtils.getUpNotes(conn, tradecontactid, "7");
			for (int i=0;i<vts.size();i++)
			{
				Hashtable hts=(Hashtable) vts.get(i);
				String sheetid=hts.get("sheetid").toString();
				sql="select tid,memo from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
				Hashtable refund = SQLHelper.oneRowSelect(conn, sql) ;
				String orderid = refund.get("tid").toString() ;
				String returnRemark = refund.get("memo").toString() ;
				
		        if(updateRejectInStock(jobname, orderid, returnRemark))
		        {
		        	IntfUtils.backupUpNote(conn, "yongjun",sheetid, "2");
		        	Log.info("更新vjia退换货拒收状态成功，vjia单号："+orderid+"，拒收原因："+returnRemark);
		        }
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "更新退换货订单状态失败，错误信息："+e.getMessage()) ;
		}
		
	}
	//同意退换货签收
	private static boolean updateAgreeInStock(String orderid, String parcelNo, String parcelCompany, String returnRemark) throws Exception
	{
		boolean flag = false ;
		try 
		{
			SoapHeader soapheader=new SoapHeader();
		    soapheader.setPassword(passWord);
		    soapheader.setUname(userName);
		    soapheader.setUri(URI);
		   
		    SoapBody soapbody=new SoapBody();
		    soapbody.setRequestname("GetReturnMakeBill");
		    soapbody.setUri(URI);
		   
		    Hashtable<String,String> bodyparams=new Hashtable<String,String>();
		    bodyparams.put("supplierId", swsSupplierID);
		    bodyparams.put("DesAcceptCode", DesUtil.DesEncode(orderid, strkey, striv));
		    bodyparams.put("parcelNo", parcelNo);
		    bodyparams.put("parcelCompany", parcelCompany);
		    bodyparams.put("returnRemark", returnRemark);
		    
		    soapbody.setBodyParams(bodyparams);
		   
		    SoapServiceClient client=new SoapServiceClient();
		    client.setUrl(wsurl+"/ReturnMakeBillService.asmx");
		    client.setSoapbody(soapbody);
		    client.setSoapheader(soapheader);
		    String result=client.request();
		    
		    Document resultdoc=DOMHelper.newDocument(result);
			Element resultelement=resultdoc.getDocumentElement();
			String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
			String remark = DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim() ;
		    
		    if("0".equals(resultcode))
		    {
		    	flag = true ;
		    }
		    else
		    {
		    	Log.error(jobname, "更新vjia退换货入库失败，，原单号："+DesUtil.DesDecode(orderid, strkey, striv)+",运单号："+parcelNo+"。错误信息："+remark) ;
		    }
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "更新vjia退换货入库失败，，原单号："+DesUtil.DesDecode(orderid, strkey, striv)+",运单号："+parcelNo+"。错误信息："+e.getMessage()) ;
			return false ;
		}
		return flag ;
	}

	//退换货拒收入库
	public static boolean updateRejectInStock(String jobname, String orderid,String reasonText) throws Exception
	{
		try 
		{
	        SoapHeader soapheader=new SoapHeader();
		    soapheader.setPassword(passWord);
		    soapheader.setUname(userName);
		    soapheader.setUri(URI);
		   
		    SoapBody soapbody=new SoapBody();
		    soapbody.setRequestname("GetRejectMakeBill");
		    soapbody.setUri(URI);
		   
		    Hashtable<String,String> bodyparams=new Hashtable<String,String>();
		    bodyparams.put("supplierId", swsSupplierID);
		    bodyparams.put("DesAcceptCode", DesUtil.DesEncode(orderid, strkey, striv));
		    bodyparams.put("reasonText", reasonText);

		    soapbody.setBodyParams(bodyparams);
		   
		    SoapServiceClient client=new SoapServiceClient();
		    client.setUrl(wsurl+"/ReturnMakeBillService.asmx");
		    client.setSoapbody(soapbody);
		    client.setSoapheader(soapheader);

		    String result=client.request();
		    
		    Document resultdoc=DOMHelper.newDocument(result);
			Element resultelement=resultdoc.getDocumentElement();
			String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
			String remark = DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim() ;
		    
		    if("0".equals(resultcode))
		    {
		    	return true ;
		    }
		    else
		    {
		    	Log.error(jobname, "更新vjia退换货拒收状态失败！错误信息：" + remark+"，vjia单号："+orderid+"拒收原因："+reasonText);
		    	return false ;
		    }
		} 
		catch (Exception e) 
		{
			return false ;
		}
	}


}