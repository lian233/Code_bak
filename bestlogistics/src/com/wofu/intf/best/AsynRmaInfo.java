package com.wofu.intf.best;
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
/**
 * 销售退货也是要做入库处理的
 * @author Administrator
 *
 */


public class AsynRmaInfo extends Thread {
	
	private static String jobname = "同步退货单作业";  //销售退货
	private static String serviceType="SyncRmaInfo";
	private static String sheettype="2222";

	public AsynRmaInfo() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//取得要处理的单据号: 对应客户退货验收单CustomerRetRcv0 的refsheetid  --客户退货审批单号
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid="";
					try{
						sheetid=(String) it.next();
						
						String sql="select count(*) from CustomerRetRcv0 with(nolock) "
							+"where refsheetid='"+sheetid+"'";
						if (SQLHelper.intSelect(conn, sql)==0)
						{
							Log.info("单据不存在或者已处理,接口单号:"+sheetid);
							continue;
						}
						
						StringBuffer bizData=new StringBuffer();
						bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
						bizData.append("<"+serviceType+">");
						bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
						
						//取订单信息
						sql="select sheetid,inshopid,sheetflag,isnull(linkman,'') linkman,isnull(linktele,'') linktele,"
									+"isnull(address,'') address,isnull(customersheetid,'') customersheetid,isnull(note,'') note,flag from CustomerRetRcv0 with(nolock) "
									+"where refsheetid='"+sheetid+"'";
						Hashtable htret=SQLHelper.oneRowSelect(conn, sql);
						String retsheetid=htret.get("sheetid").toString();
						String inshopid=htret.get("inshopid").toString();
						int refflag=Integer.valueOf(htret.get("flag").toString()).intValue();
						int sheetflag=Integer.valueOf(htret.get("sheetflag").toString()).intValue();
						String refordercode=htret.get("customersheetid").toString();
						String linkman=htret.get("linkman").toString();
						String linktele=htret.get("linktele").toString();
						String address=htret.get("address").toString();
						String note=htret.get("note").toString();
						address=address.replaceAll("&", "&amp;");
						address=address.replaceAll("\"", "&quot;");
						address=address.replaceAll("’", "&apos;");
						address=address.replaceAll("<", "&lt;");
						address=address.replaceAll(">", "&gt;");
						note=note.replaceAll("&", "&amp;");
						note=note.replaceAll("\"", "&quot;");
						note=note.replaceAll("’", "&apos;");
						note=note.replaceAll("<", "&lt;");
						note=note.replaceAll(">", "&gt;");
						String mobile="";
						String province="";
						String city="";
						String district="";
						
						if (!linktele.equals("")&& linktele.indexOf(" ")>=0)
						{
							mobile=linktele.substring(0, linktele.indexOf(" "));
							linktele=linktele.substring(linktele.indexOf(" ")+1);
						}
						if (!address.equals("")){
							if(address.indexOf(" ")!=-1){
								province=address.substring(0, address.indexOf(" "));
								address=address.substring(address.indexOf(" ")+1);
								if(address.indexOf(" ")!=-1){
									city=address.substring(0, address.indexOf(" "));
									address=address.substring(address.indexOf(" ")+1);
									if(address.indexOf(" ")!=-1){
										district=address.substring(0, address.indexOf(" "));
										address=address.substring(address.indexOf(" ")+1);
									}
									
								}
							}
							
						}
						
						bizData.append("<warehouseCode>"+(Params.warehouseMulti?BestUtil.getWarehouseCode(conn,Params.customercode,inshopid,serviceType):BestUtil.getWarehouseCode(conn,Params.customercode,inshopid))+"</warehouseCode>");
						bizData.append("<warehouseAddressCode>").append("</warehouseAddressCode>");
						bizData.append("<rmaCode>"+retsheetid+"</rmaCode>");
						if (refflag==97 || refflag==98)
							bizData.append("<actionType>CANCEL</actionType>");
						else
							bizData.append("<actionType>ADD</actionType>");
						bizData.append("<refOrderCode>"+refordercode+"</refOrderCode>");  //填平台订单号
						bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
						bizData.append("<note>").append(note).append("</note>");
					
						if (sheetflag==1)
						{
							bizData.append("<sender>");
							bizData.append("<name>"+linkman+"</name>");
							bizData.append("<phoneNumber>"+linktele+"</phoneNumber>");
							bizData.append("<mobileNumber>"+mobile+"</mobileNumber>");
							bizData.append("<province>"+province+"</province>");
							bizData.append("<city>"+city+"</city>");
							bizData.append("<district>"+district+"</district>");
							bizData.append("<shippingAddress>"+address+"</shippingAddress>");
							
							bizData.append("</sender>");
						}else
						{
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
						}
						//大唐多个dc对应一个物理仓   这时候要把dc值也传给百事系统，它们也会原样返回这个值
						if(Params.isMultiDcToONeWare){
							bizData.append("<udfFlag>true</udfFlag>")
							.append("<udf1>").append(inshopid).append("</udf1>");
						}
						bizData.append("<items>");
						
					/*	sql="select b.custombc,b.goodsname,a.outqty,b.colorname,b.sizename "
							+" from CustomerRetRcvitem0 a,v_barcodeall b "
							+" where a.sheetid='"+retsheetid+"' and a.barcodeid=b.barcodeid ";*/
						//从CustomerRetRcvitem0 v_barcodeall表中取得相应的退货信息
						sql = new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
						.append("b.goodsname,a.outqty,b.colorname,b.sizename ")
								.append("from CustomerRetRcvitem0 a with(nolock) ,v_barcodeall b with(nolock) ")
								.append("where a.sheetid='").append(retsheetid)
								.append("' and a.barcodeid=b.barcodeid").toString();
						Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
						for (int i=0;i<vtsku.size();i++)
						{
							Hashtable htsku=(Hashtable) vtsku.get(i);
							String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString();
							int qty=Double.valueOf(htsku.get("outqty").toString()).intValue();
							String goodsname=htsku.get("goodsname").toString();
							goodsname=goodsname.replaceAll("&", "&amp;");
							goodsname=goodsname.replaceAll("\"", "&quot;");
							goodsname=goodsname.replaceAll("’", "&apos;");
							goodsname=goodsname.replaceAll("<", "&lt;");
							goodsname=goodsname.replaceAll(">", "&gt;");
							String itemnote="颜色:"+htsku.get("colorname").toString().trim()+" 尺码:"+htsku.get("sizename").toString().trim();
							Log.info("restssss");
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
						
						String msgId=BestUtil.makeMsgId(conn);
						List signParams=BestUtil.makeSignParams(bizData1, serviceType,Params.msgtype,
								Params.partnerid,Params.partnerkey,Params.serviceversion,Params.callbackurl,msgId);
						String sign=BestUtil.makeSign(signParams);
					
						
						Map requestParams=BestUtil.makeRequestParams(bizData1, serviceType, 
								msgId, Params.msgtype, sign,Params.callbackurl,
								Params.serviceversion,Params.partnerid);
						
						String result=CommHelper.sendRequest(Params.url, requestParams, "");
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
							
							Log.error(jobname, "同步退货单失败,接口单号:"+sheetid+",错误信息："+errorMsg);
							
						}else
						{
							conn.setAutoCommit(false);
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2222");
							BestUtil.recordMsg(conn, msgId,retsheetid,2222,serviceType);
							conn.commit();
							conn.setAutoCommit(true);
							Log.info(jobname,"同步退货单成功,接口单号:"+sheetid);
						}
						
					}catch(Exception e){
						try {
							if (conn != null && !conn.getAutoCommit()){
								conn.rollback();
								conn.setAutoCommit(true);
							}
								
						} catch (Exception e1) {
							Log.error(jobname, "回滚事务失败");
						}
						Log.error(jobname, "处理客户退货入库单出错: sheetid:"+sheetid+"详细错误信息: "+e.getMessage());
						continue;
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
