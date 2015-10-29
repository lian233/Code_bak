package com.wofu.intf.best;
/**
 * 取消发货
 */
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
public class AsynSalesOrderCancel extends Thread {
	
	private static String jobname = "同步发货单取消作业";
	private static String serviceType="SyncSalesOrderInfo";
	private static String sheettype="220902";

	public AsynSalesOrderCancel() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//取得要处理的单据号   对应customerdelive0的refsheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					
					String sheetid=(String) it.next();
					System.out.println("sheetid: "+sheetid);
					
					String sql="select count(*) from customerdelive0 with(nolock) "
						+"where refsheetid='"+sheetid+"'";
					if (SQLHelper.intSelect(conn, sql)==0)
					{
						Log.info("单据不存在或者已处理,接口单号:"+sheetid);
						continue;
					}
					sql="select count(*) from it_infsheetlist with(nolock)  where sheetid='"+sheetid+"' and sheettype=2209";
					if (SQLHelper.intSelect(conn, sql)==0) continue; //发货单还没创建，取消通知等待，避免由于通信原因取消通知先于发货通知
					
					try
					{
						StringBuffer bizData=new StringBuffer();
						bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
						bizData.append("<"+serviceType+">");
						bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
						
						//取customerdelive0取得订单信息
						sql="select inshopid,outshopid,sheetid,customersheetid,convert(char(19),delivedate,120) delivedate,"
									+"paymode,payfee,delivery,notes,isnull(invoiceflag,0) invoiceflag,isnull(invoicetitle,'') invoicetitle,"
									+"detailid,linktele,linkman,address,zipcode from customerdelive0  with(nolock) "
									+"where refsheetid='"+sheetid+"'";
						Hashtable htorder=SQLHelper.oneRowSelect(conn, sql);
	
						String inshopid=htorder.get("inshopid").toString();
	
						String outshopid=htorder.get("outshopid").toString();
	
						String outbuzcode=htorder.get("sheetid").toString();
	
						String customersheetid=htorder.get("customersheetid").toString();
						String delivedate=htorder.get("delivedate").toString();
						String paymode=htorder.get("paymode").toString();
						String payfee=htorder.get("payfee").toString();
						String delivery=htorder.get("delivery").toString();
						String notes=htorder.get("notes").toString();
						
						notes=StringUtil.replace(notes, "<", "&lt;");
						notes=StringUtil.replace(notes, ">", "&gt;");
						notes=StringUtil.replace(notes, "&", "&amp;");
						
						String invoiceflag=htorder.get("invoiceflag").toString();
						String invoicetitle=htorder.get("invoicetitle").toString();
						String detailid=htorder.get("detailid").toString();
						String linktele=htorder.get("linktele").toString();
						linktele=StringUtil.replace(linktele, "<", "&lt;");
						linktele=StringUtil.replace(linktele, ">", "&gt;");
						linktele=StringUtil.replace(linktele, "&", "&amp;");
						String linkman=htorder.get("linkman").toString();
						String address=htorder.get("address").toString();
						String zipcode=htorder.get("zipcode").toString();
	
						
						bizData.append("<warehouseCode>"+(Params.warehouseMulti?BestUtil.getWarehouseCode(conn,Params.customercode,outshopid,serviceType):BestUtil.getWarehouseCode(conn,Params.customercode,outshopid))+"</warehouseCode>");
						bizData.append("<warehouseAddressCode>").append("</warehouseAddressCode>");
						bizData.append("<orderCode>"+outbuzcode+"</orderCode>");
						bizData.append("<actionType>CANCEL</actionType>");
						bizData.append("<extTradeId>"+customersheetid+"</extTradeId>");
						bizData.append("<orderType>NORMAL</orderType>");
						bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
						
						//sql="select shortname from ecs_platform a,ecs_org_params b,ecs_org c "
						//	+"where a.platformid=b.platformid and b.orgid=c.orgid and c.orgcode='"+inshopid+"'";
						//String tradeplatform=SQLHelper.strSelect(conn, sql);
						
						bizData.append("<orderSource></orderSource>");
						bizData.append("<orderTime>"+delivedate+"</orderTime>");
						
						sql="select sum(customprice*purqty) from customerdeliveitem  with(nolock) where sheetid='"+outbuzcode+"'";
						
						String totalfee=SQLHelper.strSelect(conn, sql);
						
						bizData.append("<totalAmount>"+totalfee+"</totalAmount>");
						
						if (paymode.equals("2"))  //货到付款写代收款信息
						{
							bizData.append("<isPaymentCollected>true</isPaymentCollected>");
							bizData.append("<collectingPaymentAmount>"+payfee+"</collectingPaymentAmount>");
							bizData.append("<extOrderType>COD</extOrderType>");
						}
						bizData.append("<logisticsProviderCode>"+delivery+"</logisticsProviderCode>");
						bizData.append("<note>"+notes+"</note>");
						sql="select name from shop with(nolock) where id='"+inshopid+"'";
						
						String shopname=SQLHelper.strSelect(conn, sql);
						bizData.append("<sellerName>"+shopname+"</sellerName>");
						
						if (invoiceflag.equals("1"))	//需要开发货写发票信息
						{
							bizData.append("<invoiceFlag>true</invoiceFlag>");
							bizData.append("<invoiceTitle>"+invoicetitle+"</invoiceTitle>");
							bizData.append("<invoiceAmount>"+totalfee+"</invoiceAmount>");
						}
						bizData.append("<buyerName>"+detailid+"</buyerName>");
						bizData.append("<buyerPhone>"+linktele+"</buyerPhone>");
						
						linkman=StringUtil.replace(linkman, "<", "&lt;");
						linkman=StringUtil.replace(linkman, ">", "&gt;");
						linkman=StringUtil.replace(linkman, "&", "&amp;");
						
						//写收货人信息
						bizData.append("<recipient>");
						bizData.append("<name>"+linkman+"</name>");
						bizData.append("<postalCode>"+zipcode+"</postalCode>");
						
						address=StringUtil.replace(address, "<", "&lt;");
						address=StringUtil.replace(address, ">", "&gt;");
						address=StringUtil.replace(address, "&", "&amp;");
						address=StringUtil.replace(address, (char)12+"", "");  //ascii 12替换成空
						
						if(linktele.indexOf(" ")>=0)
						{
							bizData.append("<phoneNumber>"+linktele.substring(linktele.indexOf(" ")+1)+"</phoneNumber>");
							bizData.append("<mobileNumber>"+linktele.substring(0,linktele.indexOf(" "))+"</mobileNumber>");
						}
						else
							bizData.append("<mobileNumber>"+linktele+"</mobileNumber>");
						
						if (address.indexOf(" ")>=0)
						{
							bizData.append("<province>"+address.substring(0,address.indexOf(" "))+"</province>");
							address=address.substring(address.indexOf(" ")+1);	
						}
						
						if (address.indexOf(" ")>=0)
						{
							bizData.append("<city>"+address.substring(0,address.indexOf(" "))+"</city>");
							address=address.substring(address.indexOf(" ")+1);	
						}
						
						if (address.indexOf(" ")>=0)
						{
							bizData.append("<district>"+address.substring(0,address.indexOf(" "))+"</district>");
							address=address.substring(address.indexOf(" ")+1);	
						}
						
						bizData.append("<shippingAddress>"+address+"</shippingAddress>");
						
						bizData.append("</recipient>");
						//大唐多个dc对应一个物理仓   这时候要把dc值也传给百事系统，它们也会原样返回这个值
						if(Params.isMultiDcToONeWare){
							bizData.append("<udfFlag>true</udfFlag>")
							.append("<udf1>").append(outshopid).append("</udf1>");
						}
						bizData.append("<items>");
						
						/*sql="select b.custombc,a.customprice,a.purqty,a.title,a.notes "
							+" from customerdeliveitem0 a,barcode b "
							+" where a.sheetid='"+outbuzcode+"' and a.barcodeid=b.barcodeid ";*/
						//从customerdeliveitem0 ,barcode表中取得退货明细
						sql = new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
						.append("a.customprice,a.purqty,a.title,a.notes ")
								.append("from customerdeliveitem0 a with(nolock) ,barcode b  with(nolock) ")
										.append("where a.sheetid='").append(outbuzcode)
										.append("' and a.barcodeid=b.barcodeid").toString();
										
						Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
						for (int i=0;i<vtsku.size();i++)
						{
							Hashtable htsku=(Hashtable) vtsku.get(i);
							
							String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString();
							int purqty=Double.valueOf(htsku.get("purqty").toString()).intValue();
							String title=htsku.get("title").toString();
							title=StringUtil.replace(title, "<", "&lt;");
							title=StringUtil.replace(title, ">", "&gt;");
							title=StringUtil.replace(title, "&", "&amp;");
							String customprice=htsku.get("customprice").toString();
							String itemnotes=htsku.get("notes").toString();
							itemnotes=StringUtil.replace(itemnotes, "<", "&lt;");
							itemnotes=StringUtil.replace(itemnotes, ">", "&gt;");
							itemnotes=StringUtil.replace(itemnotes, "&", "&amp;");
							bizData.append("<item>");
							bizData.append("<itemSkuCode>"+custombc+"</itemSkuCode>");
			
							bizData.append("<itemName>"+title+"</itemName>");
							
							bizData.append("<itemQuantity>"+purqty+"</itemQuantity>");
							bizData.append("<itemUnitPrice>"+customprice+"</itemUnitPrice>");
							bizData.append("<itemNote>"+itemnotes+"</itemNote>");				
			
							bizData.append("</item>");
						}										
						bizData.append("</items>");
						bizData.append("</"+serviceType+">");
						
						String bizData1 = BestUtil.filterChar(bizData.toString() );
						//Log.info("bizData: "+bizData.toString());
						String msgId=BestUtil.makeMsgId(conn);
						List signParams=BestUtil.makeSignParams(bizData1, serviceType,Params.msgtype,
								Params.partnerid,Params.partnerkey,Params.serviceversion,Params.callbackurl,msgId);
						String sign=BestUtil.makeSign(signParams);
		
						Map requestParams=BestUtil.makeRequestParams(bizData1, serviceType, 
								msgId, Params.msgtype, sign,Params.callbackurl,
								Params.serviceversion,Params.partnerid);
						
	
						String result=CommHelper.sendRequest(Params.url, requestParams, "");
						//Log.info("result: "+result);
						result=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
	
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
													
							Log.error(jobname, "同步发货单取消失败,接口单号:"+sheetid+",错误信息："+errorMsg);
							
						}else
						{
							conn.setAutoCommit(false);
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"220902");
							BestUtil.recordMsg(conn, msgId,outbuzcode,220902,serviceType);
							conn.commit();
							conn.setAutoCommit(true);
							Log.info(jobname,"同步发货单取消成功,接口单号:"+sheetid);
						}
						
						
					} catch (Exception e) {
						try {
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
						} catch (Exception e1) {
							Log.error(jobname, "回滚事务失败");
						}
						Log.error("105", jobname, Log.getErrorMessage(e));
					}
				}
			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit()){
						conn.rollback();
						conn.setAutoCommit(true);
						
					}
						
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
