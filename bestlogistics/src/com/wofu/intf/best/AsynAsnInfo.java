package com.wofu.intf.best;
/**
 * 把来货计划单推送到百世系统
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
import com.wofu.common.tools.util.log.Log;



public class AsynAsnInfo extends Thread {
	
	private static String jobname = "同步补货单作业";
	private static String serviceType="SyncAsnInfo";
	private static String sheettype="2227";
	

	public AsynAsnInfo() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//取得要处理的单号  对应planreceipt,planreceiptitem 的sheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					Log.info("sheetid: "+sheetid);
					conn.setAutoCommit(false);
					
					StringBuffer bizData=new StringBuffer();
					bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					bizData.append("<"+serviceType+">");
					bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
					
					//取来货计划单信息
					String sql="select shopid,flag,note from planreceipt with(nolock) "
								+"where sheetid='"+sheetid+"'";
					
					Hashtable htplan=SQLHelper.oneRowSelect(conn, sql);
					
					String shopid=htplan.get("shopid").toString();
					String note=htplan.get("note").toString();
					int sheetflag=Integer.valueOf(htplan.get("flag").toString()).intValue();
					
					bizData.append("<warehouseCode>"+(Params.warehouseMulti?BestUtil.getWarehouseCode(conn,Params.customercode,shopid,serviceType):BestUtil.getWarehouseCode(conn,Params.customercode,shopid))+"</warehouseCode>");
					bizData.append("<warehouseAddressCode>").append(Params.warehouseAddressCode).append("</warehouseAddressCode>");
					bizData.append("<asnCode>"+sheetid+"</asnCode>");
					if (sheetflag==97 || sheetflag==98)
						bizData.append("<actionType>CANCEL</actionType>");
					else
						bizData.append("<actionType>ADD</actionType>");
					bizData.append("<extTradeId>"+sheetid+"</extTradeId>");
					bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
					bizData.append("<note>"+note+"</note>");
					
				
					bizData.append("<sender>");
					bizData.append("<name>"+Params.linkman+"</name>");
					bizData.append("<postalCode>"+Params.zipcode+"</postalCode>");
					bizData.append("<phoneNumber>"+Params.phone+"</phoneNumber>");
					bizData.append("<mobileNumber>"+Params.mobile+"</mobileNumber>");
					bizData.append("<province>"+Params.province+"</province>");
					bizData.append("<city>"+Params.city+"</city>");
					bizData.append("<district>"+Params.district+"</district>");
					bizData.append("<shippingAddress>"+Params.address+"</shippingAddress>");
					bizData.append("<email>"+Params.email+"</email>");
					bizData.append("</sender>");
					//大唐多个dc对应一个物理仓   这时候要把dc值也传给百事系统，它们也会原样返回这个值
					if(Params.isMultiDcToONeWare){
						bizData.append("<udfFlag>true</udfFlag>")
						.append("<udf1>").append(shopid).append("</udf1>");
					}
					
					bizData.append("<items>");
					
				/*	sql="select b.custombc,b.goodsname,a.qty,b.colorname,b.sizename "
						+" from planreceiptitem a,v_barcodeall b "
						+" where a.sheetid='"+sheetid+"' and a.barcodeid=b.barcodeid ";*/
					sql= new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
						.append("b.goodsname,a.qty,b.colorname,b.sizename from planreceiptitem a with(nolock) ,v_barcodeall b with(nolock)  where a.sheetid='")
						.append(sheetid).append("' and a.barcodeid=b.barcodeid").toString();
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						
						String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString();
						int qty=Double.valueOf(htsku.get("qty").toString()).intValue();
						String goodsname=htsku.get("goodsname").toString();
						goodsname=goodsname.replaceAll("&", "&amp;");
						goodsname=goodsname.replaceAll("\"", "&quot;");
						goodsname=goodsname.replaceAll("’", "&apos;");
						goodsname=goodsname.replaceAll("<", "&lt;");
						goodsname=goodsname.replaceAll(">", "&gt;");
						String itemnote="颜色:"+htsku.get("colorname").toString()+" 尺码:"+htsku.get("sizename").toString().trim();
					
						bizData.append("<item>");
						bizData.append("<itemSkuCode>"+custombc+"</itemSkuCode>");
		
						bizData.append("<itemName>"+goodsname+"</itemName>");
						bizData.append("<itemQuantity>"+qty+"</itemQuantity>");
						bizData.append("<itemNote>"+itemnote+"</itemNote>");				
		
						bizData.append("</item>");
					}										
					bizData.append("</items>");
					bizData.append("</"+serviceType+">");
					String bizData1 = BestUtil.filterChar(bizData.toString() );					
					//Log.info("bizdata: "+bizData.toString());
					String msgid=BestUtil.makeMsgId(conn);
					List signParams=BestUtil.makeSignParams(bizData1, serviceType,Params.msgtype,
							Params.partnerid,Params.partnerkey,Params.serviceversion,Params.callbackurl,msgid);
					String sign=BestUtil.makeSign(signParams);
					
					Map requestParams=BestUtil.makeRequestParams(bizData1, serviceType, 
							msgid, Params.msgtype, sign,Params.callbackurl,
							Params.serviceversion,Params.partnerid);
					
					String result=CommHelper.sendRequest(Params.url, requestParams, "");
					//Log.info("result:　"+result);
					
					result=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
					/*
					Document productinfodoc = DOMHelper.newDocument(result, Params.encoding);
					Element productinfoele = productinfodoc.getDocumentElement();	
	
					Element responsesele=(Element) productinfoele.getElementsByTagName("response").item(0);
					
					String bizDataRsp=DOMHelper.getSubElementVauleByName(responsesele, "bizData");
					*/
					
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
						
						Log.error(jobname, "同步补货单失败,接口单号:"+sheetid+",错误信息："+errorMsg);
						
					}else  //成功后备份接口表数据，更新ecs_bestlogisticsmsg表的数据
					{
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2227");
						
						BestUtil.recordMsg(conn, msgid,sheetid,2227,serviceType);
						
						Log.info(jobname,"同步补货单成功,接口单号:"+sheetid);
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
