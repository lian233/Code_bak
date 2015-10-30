package com.wofu.intf.tiantu;
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

//调拨入库 transfer0.sheetid为外部单号

public class AsynTansferIn extends Thread {
	
	private static String jobname = "同步调拨入库单作业";
	private String serviceType="SyncAsnInfo";
	private static String sheettype="2342";
	

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//取得要处理的数据的单据号:　对应transfer0表的sheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					System.out.println("--sheedid"+sheetid);
					//取订单信息
					String sql="select sheetid,outshopid,inshopid,note,checkdate,flag from transfer0 with(nolock) where sheetid='"+sheetid+"'";
					Hashtable htorder=SQLHelper.oneRowSelect(conn, sql);
					if((htorder.get("sheetid")==null)){
						continue;
					}
					StringBuffer bizData=new StringBuffer();
					bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					bizData.append("<"+serviceType+">");
					bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
					
					//String orderCode=htorder.get("sheetid").toString();
					String outshopid=htorder.get("outshopid").toString();
					String inshopid=htorder.get("inshopid").toString();
					String note=htorder.get("note").toString();
					//String checkdate=htorder.get("checkdate").toString();
					int sheetflag=Integer.valueOf(htorder.get("flag").toString()).intValue();
					
			
					bizData.append("<warehouseCode>"+(Params.warehouseMulti?TianTuUtil.getWarehouseCode(conn,Params.customercode,inshopid,serviceType):TianTuUtil.getWarehouseCode(conn,Params.customercode,inshopid))+"</warehouseCode>");
					bizData.append("<warehouseAddressCode>").append(Params.warehouseAddressCode).append("</warehouseAddressCode>");
					bizData.append("<asnCode>"+sheetid+"</asnCode>");
					if (sheetflag==97 || sheetflag==98)
						bizData.append("<actionType>CANCEL</actionType>");
					else
						bizData.append("<actionType>ADD</actionType>");
					bizData.append("<extTradeId>"+sheetid+"</extTradeId>");
					bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
					bizData.append("<note>"+note+"</note>");
				
					
					sql="select isnull(linkman,'"+Params.linkman+"') linkman,zipno,linktele,mobileno,email,address from shop with(nolock) where id='"+outshopid+"'";
					
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
					String email=htshop.get("email").toString();
					
					bizData.append("<sender>");
					bizData.append("<name>"+linkman+"</name>");
					bizData.append("<postalCode>"+zipcode+"</postalCode>");
					bizData.append("<phoneNumber>"+linktele+"</phoneNumber>");
					bizData.append("<mobileNumber>"+mobileno+"</mobileNumber>");
					bizData.append("<province></province>");
					bizData.append("<city></city>");
					bizData.append("<district></district>");
					bizData.append("<shippingAddress>"+address+"</shippingAddress>");
					bizData.append("<email>"+email+"</email>");
					bizData.append("</sender>");
					//大唐多个dc对应一个物理仓   这时候要把dc值也传给百事系统，它们也会原样返回这个值
					if(Params.isMultiDcToONeWare){
						bizData.append("<udfFlag>true</udfFlag>")
						.append("<udf1>").append(outshopid).append("</udf1>");
					}
					bizData.append("<items>");
					
				/*	sql="select b.custombc,b.goodsname,a.outqty,b.colorname,b.sizename "
						+" from transferitem0 a,v_barcodeall b "
						+" where a.sheetid='"+sheetid+"' and a.barcodeid=b.barcodeid and outqty<>0";*/
					//从transferitem0,v_barcodeall取得调拨入库单的明细
					sql = new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
						.append(" b.goodsname,a.outqty,b.colorname,b.sizename")
						.append(" from transferitem0 a with(nolock),v_barcodeall b with(nolock) ")
								.append(" where a.sheetid='").append(sheetid)
								.append("' and a.barcodeid=b.barcodeid and outqty<>0").toString();
					
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						
						String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString();
						int qty=Double.valueOf(htsku.get("outqty").toString()).intValue();
						String goodsname=htsku.get("goodsname").toString();
						goodsname=StringUtil.replace(goodsname, "<", "&lt;");
						goodsname=StringUtil.replace(goodsname, ">", "&gt;");
						goodsname=StringUtil.replace(goodsname, "&", "&amp;");
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
					String bizData1 = TianTuUtil.filterChar(bizData.toString() );					
					//Log.info("bizData: "+bizData1);
					String msgid=TianTuUtil.makeMsgId(conn);
					List signParams=TianTuUtil.makeSignParams(bizData1, serviceType,Params.msgtype,
							Params.partnerid,Params.partnerkey,Params.serviceversion,Params.callbackurl,msgid);
					String sign=TianTuUtil.makeSign(signParams);
					
					Map requestParams=TianTuUtil.makeRequestParams(bizData1, serviceType, 
							msgid, Params.msgtype, sign,Params.callbackurl,
							Params.serviceversion,Params.partnerid);
					
					String result=CommHelper.sendRequestT(Params.url, requestParams, "");
					
					Log.info("result: "+result);
					
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
						
						Log.error(jobname, "同步调拨入库单失败,接口单号:"+sheetid+",错误信息："+errorMsg);
						
					}else
					{
						conn.setAutoCommit(false);
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,sheettype);
						TianTuUtil.recordMsg(conn, msgid,sheetid,Integer.valueOf(sheettype).intValue(),serviceType);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info(jobname,"同步调拨入库单成功,接口单号:"+sheetid);
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
				e.printStackTrace();
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
