package com.wofu.intf.best;

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
import com.wofu.common.tools.util.log.Log;
/**
 * 退供应商  这里商品是从百世流到供应商的，是出库的另一种形式
 * @author Administrator
 *
 */


public class AsynReturnInfo extends Thread {
	
	private static String jobname = "同步供应商退货单作业";
	private static String serviceType="SyncSalesOrderInfo";
	private static String sheettype="2322";
	

	public AsynReturnInfo() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//取得要处理的单据编号：对应ret0,retitem0表的sheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					StringBuffer bizData=new StringBuffer();
					bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					bizData.append("<"+serviceType+">");
					bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
					//取订单信息
					String sql="select shopid,isnull(notes,'') notes,checkdate,flag from ret0 with(nolock) where sheetid='"+sheetid+"'";
					Hashtable htorder=SQLHelper.oneRowSelect(conn, sql);
					if(htorder.size()==0) continue;
					String shopid=htorder.get("shopid").toString();
					String notes=htorder.get("notes").toString();
					String checkdate=htorder.get("checkdate").toString();
					int sheetflag=Integer.valueOf(htorder.get("flag").toString()).intValue();
					
					bizData.append("<warehouseCode>"+(Params.warehouseMulti?BestUtil.getWarehouseCode(conn,Params.customercode,shopid,serviceType):BestUtil.getWarehouseCode(conn,Params.customercode,shopid))+"</warehouseCode>");
					bizData.append("<warehouseAddressCode>").append("</warehouseAddressCode>");
					bizData.append("<orderCode>"+sheetid+"</orderCode>");
					if (sheetflag==97 || sheetflag==98)
						bizData.append("<actionType>CANCEL</actionType>");
					else
						bizData.append("<actionType>ADD</actionType>");
					bizData.append("<orderType>WDO</orderType>");
					bizData.append("<orderTime>"+checkdate+"</orderTime>");
					bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
					
					sql="select sum(Price*planqty) from retitem0 where sheetid='"+sheetid+"'";					
					String totalfee=SQLHelper.strSelect(conn, sql);					
					bizData.append("<totalAmount>"+totalfee+"</totalAmount>");					
					bizData.append("<note>"+notes+"</note>");

					//写收货人信息
					bizData.append("<recipient>");
					bizData.append("<name>"+Params.linkman+"</name>");
					bizData.append("<postalCode>"+Params.zipcode+"</postalCode>");			
					bizData.append("<phoneNumber>"+Params.phone+"</phoneNumber>");
					bizData.append("<mobileNumber>"+Params.mobile+"</mobileNumber>");			
					bizData.append("<province>"+Params.province+"</province>");	
					bizData.append("<city>"+Params.city+"</city>");			
					bizData.append("<district>"+Params.district+"</district>");					
					bizData.append("<shippingAddress>"+Params.address+"</shippingAddress>");
					bizData.append("</recipient>");
					//大唐多个dc对应一个物理仓   这时候要把dc值也传给百事系统，它们也会原样返回这个值
					if(Params.isMultiDcToONeWare){
						bizData.append("<udfFlag>true</udfFlag>")
						.append("<udf1>").append(shopid).append("</udf1>");
					}
					bizData.append("<items>");
					
					/*sql="select b.custombc,a.price,a.planqty,c.name goodsname,isnull(a.reason,'') reason,d.name reasontype "
						+" from retitem0 a,barcode b,goods c,RetReasonType d "
						+" where a.sheetid='"+sheetid+"' and a.barcodeid=b.barcodeid " 
						+"and a.goodsid=c.goodsid and a.ReasonTypeID=d.id";*/
					//从retitem0 barcode goods retresontype表中取得相应的数据	
					sql = new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
					.append("a.price,a.planqty,c.name goodsname,isnull(a.reason,'') reason,d.name reasontype ")
					.append(" from retitem0 a with(nolock),barcode b with(nolock),goods c with(nolock),RetReasonType d with(nolock) ")
					.append(" where a.sheetid='").append(sheetid)
					.append("' and a.barcodeid=b.barcodeid and a.goodsid=c.goodsid and a.ReasonTypeID=d.id").toString();
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString();
						int planqty=Double.valueOf(htsku.get("planqty").toString()).intValue();
						String goodsname=htsku.get("goodsname").toString();
						goodsname=goodsname.replaceAll("&", "&amp;");
						goodsname=goodsname.replaceAll("\"", "&quot;");
						goodsname=goodsname.replaceAll("’", "&apos;");
						goodsname=goodsname.replaceAll("<", "&lt;");
						goodsname=goodsname.replaceAll(">", "&gt;");
						String price=htsku.get("price").toString();
						String reason=htsku.get("reason").toString();
						String reasontype=htsku.get("reasontype").toString();
						bizData.append("<item>");
						bizData.append("<itemSkuCode>"+custombc+"</itemSkuCode>");
						bizData.append("<itemName>"+goodsname+"</itemName>");
						bizData.append("<itemQuantity>"+planqty+"</itemQuantity>");
						bizData.append("<itemUnitPrice>"+price+"</itemUnitPrice>");
						bizData.append("<itemNote>"+reasontype+" "+reason+"</itemNote>");				
						bizData.append("</item>");
					}										
					bizData.append("</items>");
					bizData.append("</"+serviceType+">");
					String bizData1 = BestUtil.filterChar(bizData.toString() );

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
												
						Log.error(jobname, "同步发货单失败,接口单号:"+sheetid+",错误信息："+errorMsg);
						
					}else
					{
						conn.setAutoCommit(false);
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2322");
						BestUtil.recordMsg(conn, msgId,sheetid,2322,serviceType);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info(jobname,"同步供应商退货单成功,接口单号:"+sheetid);
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
