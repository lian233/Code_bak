package com.wofu.intf.sf;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.business.intf.IntfUtils;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;



public class AsynTranferNote extends Thread {
	
	private static String jobname = "同步调拨出库单作业";
	private String serviceType="SyncSalesOrderInfo";
	private static String sheettype="2341";

	public AsynTranferNote() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//取得要处理的单据号：跟transfer0的refsheetid字段对应
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();

					conn.setAutoCommit(false);
					
					StringBuffer bizData=new StringBuffer();
					bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					bizData.append("<"+serviceType+">");
					bizData.append("<customerCode>"+Params.customercode+"</customerCode>");

					//取订单信息
					String sql="select sheetid,outshopid,inshopid,note,checkdate,flag from transfer0 where refsheetid='"+sheetid+"'";
					
					Hashtable htorder=SQLHelper.oneRowSelect(conn, sql);
					
					String orderCode=htorder.get("sheetid").toString();
					String outshopid=htorder.get("outshopid").toString();
					String inshopid=htorder.get("inshopid").toString();
					String note=htorder.get("note").toString();
					note=StringUtil.replace(note, "<", "&lt;");
					note=StringUtil.replace(note, ">", "&gt;");
					note=StringUtil.replace(note, "&", "&amp;");
					String checkdate=htorder.get("checkdate").toString();
					int sheetflag=Integer.valueOf(htorder.get("flag").toString()).intValue();

					
					bizData.append("<warehouseCode>"+sfUtil.getWarehouseCode(conn,Params.customercode,outshopid)+"</warehouseCode>");
					bizData.append("<orderCode>"+sheetid+"</orderCode>");
					if (sheetflag==97 || sheetflag==98)
						bizData.append("<actionType>CANCEL</actionType>");
					else
						bizData.append("<actionType>ADD</actionType>");
					bizData.append("<orderType>WDO</orderType>");
					bizData.append("<orderTime>"+checkdate+"</orderTime>");
					bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
					
					
					sql="select sum(OutPrice*notifyqty) from transferitem0 where sheetid='"+orderCode+"'";					
					String totalfee=SQLHelper.strSelect(conn, sql);					
					bizData.append("<totalAmount>"+totalfee+"</totalAmount>");					
					bizData.append("<note>"+note+"</note>");

					
					sql="select isnull(linkman,'"+Params.linkman+"') linkman,zipno,linktele,mobileno,address from shop where id='"+inshopid+"'";
					Hashtable htshop=SQLHelper.oneRowSelect(conn, sql);
					
					String linkman=htshop.get("linkman").toString();
					String zipcode=htshop.get("zipno").toString();
					String linktele=htshop.get("linktele").toString();
					linktele=StringUtil.replace(linktele, "<", "&lt;");
					linktele=StringUtil.replace(linktele, ">", "&gt;");
					linktele=StringUtil.replace(linktele, "&", "&amp;");
					String mobileno=htshop.get("mobileno").toString();
					String address=htshop.get("address").toString();
					address=StringUtil.replace(address, "<", "&lt;");
					address=StringUtil.replace(address, ">", "&gt;");
					address=StringUtil.replace(address, "&", "&amp;");
					
					//写收货人信息
					bizData.append("<recipient>");
					bizData.append("<name>"+linkman+"</name>");
					bizData.append("<postalCode>"+zipcode+"</postalCode>");			
					bizData.append("<phoneNumber>"+linktele+"</phoneNumber>");
					bizData.append("<mobileNumber>"+mobileno+"</mobileNumber>");			
					bizData.append("<province></province>");	
					bizData.append("<city></city>");			
					bizData.append("<district></district>");					
					bizData.append("<shippingAddress>"+address+"</shippingAddress>");
					bizData.append("</recipient>");

					bizData.append("<items>");
					
					/*sql="select b.custombc,a.outprice,a.notifyqty,c.name goodsname "
						+" from transferitem0 a,barcode b,goods c"
						+" where a.sheetid='"+orderCode+"' and a.barcodeid=b.barcodeid " 
						+"and a.goodsid=c.goodsid ";*/
					//从transferitem0 barcode goods表中取得相应的数据
					sql = new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
					.append("a.outprice,a.notifyqty,c.name goodsname")
						.append(" from transferitem0 a,barcode b,goods c")
						.append(" where a.sheetid='").append(orderCode)
						.append("' and a.barcodeid=b.barcodeid ")
						.append("and a.goodsid=c.goodsid ").toString();
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						
						String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString();
						int notifyqty=Double.valueOf(htsku.get("notifyqty").toString()).intValue();
						String goodsname=htsku.get("goodsname").toString();
						goodsname=StringUtil.replace(goodsname, "<", "&lt;");
						goodsname=StringUtil.replace(goodsname, ">", "&gt;");
						goodsname=StringUtil.replace(goodsname, "&", "&amp;");
						String outprice=htsku.get("outprice").toString();
						
					
						bizData.append("<item>");
						bizData.append("<itemSkuCode>"+custombc+"</itemSkuCode>");
		
						bizData.append("<itemName>"+goodsname+"</itemName>");
						bizData.append("<itemQuantity>"+notifyqty+"</itemQuantity>");
						bizData.append("<itemUnitPrice>"+outprice+"</itemUnitPrice>");
						bizData.append("<itemNote></itemNote>");				
		
						bizData.append("</item>");
					}										
					bizData.append("</items>");
					bizData.append("</"+serviceType+">");
					Log.info("bizdata: "+bizData.toString());
					String msgId=sfUtil.makeMsgId(conn);
					Log.info("msgId: "+msgId);
					List signParams=sfUtil.makeSignParams(bizData.toString(), serviceType,Params.msgtype,
							"","",Params.serviceversion,Params.callbackurl,msgId);
					String sign=sfUtil.makeSign(signParams);
									
					
					Map requestParams=sfUtil.makeRequestParams(bizData.toString(), serviceType, 
							msgId, Params.msgtype, sign,Params.callbackurl,
							Params.serviceversion,"");
					
					String result=CommHelper.sendRequest(Params.url, requestParams, "");
					
					result=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
					Log.info("result: "+result);
					Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
					Element productinforspele = productinforspdoc.getDocumentElement();
					
					String flag=DOMHelper.getSubElementVauleByName(productinforspele, "flag");
					
					if (flag.equalsIgnoreCase("FAILURE")) //失败
					{
						String errorMsg="";
						Element errorsele=(Element) productinforspele.getElementsByTagName("errors").item(0);
						NodeList errorlist=errorsele.getElementsByTagName("error");
						for(int j=0;j<errorlist.getLength();j++)
						{
							Element errorele=(Element) errorlist.item(j);
							String errorcode=DOMHelper.getSubElementVauleByName(errorele, "errorCode");
							String errordesc=DOMHelper.getSubElementVauleByName(errorele, "errorDescription");
							
							errorMsg=errorMsg+"错误代码:"+errorcode+",错误信息:"+errordesc+" ";	
						}
												
						Log.error(jobname, "同步调拨出库单失败,接口单号:"+sheetid+",错误信息："+errorMsg);
						
					}else
					{
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,sheettype);
						
						sfUtil.recordMsg(conn, msgId,sheetid,Integer.valueOf(sheettype).intValue(),serviceType);
						
						Log.info(jobname,"同步调拨出库单成功,接口单号:"+sheetid);
					}
					
					conn.commit();
					conn.setAutoCommit(true);
				}
				
			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
}
