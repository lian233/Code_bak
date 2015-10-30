package com.wofu.intf.sf;
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
 * �����˻�Ҳ��Ҫ����⴦���
 * @author Administrator
 *
 */


public class AsynRmaInfo extends Thread {
	
	private static String jobname = "ͬ���˻�����ҵ";  //�����˻�
	private static String serviceType="SyncRmaInfo";
	private static String sheettype="2222";

	public AsynRmaInfo() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//ȡ��Ҫ����ĵ��ݺ�: ��Ӧ�ͻ��˻����յ�CustomerRetRcv0 ��refsheetid  --�ͻ��˻���������
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid="";
					try{
						sheetid=(String) it.next();
						
						String sql="select count(*) from CustomerRetRcv0 "
							+"where refsheetid='"+sheetid+"'";
						if (SQLHelper.intSelect(conn, sql)==0)
						{
							Log.info("���ݲ����ڻ����Ѵ���,�ӿڵ���:"+sheetid);
							continue;
						}
						
						conn.setAutoCommit(false);
						
						StringBuffer bizData=new StringBuffer();
						bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
						bizData.append("<"+serviceType+">");
						bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
						
						//ȡ������Ϣ
						sql="select sheetid,inshopid,sheetflag,isnull(linkman,'') linkman,isnull(linktele,'') linktele,"
									+"isnull(address,'') address,isnull(customersheetid,'') customersheetid,flag from CustomerRetRcv0 "
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
						address=address.replaceAll("&", "&amp;");
						address=address.replaceAll("\"", "&quot;");
						address=address.replaceAll("��", "&apos;");
						address=address.replaceAll("<", "&lt;");
						address=address.replaceAll(">", "&gt;");
						String mobile="";
						String province="";
						String city="";
						String district="";
						
						if (!linktele.equals("")&& linktele.indexOf(" ")>=0)
						{
							mobile=linktele.substring(0, linktele.indexOf(" "));
							linktele=linktele.substring(linktele.indexOf(" ")+1);
						}
						Log.info("address: "+address);
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
						
						bizData.append("<warehouseCode>"+sfUtil.getWarehouseCode(conn,Params.customercode,inshopid)+"</warehouseCode>");
						bizData.append("<rmaCode>"+retsheetid+"</rmaCode>");
						if (refflag==97 || refflag==98)
							bizData.append("<actionType>CANCEL</actionType>");
						else
							bizData.append("<actionType>ADD</actionType>");
						bizData.append("<refOrderCode>"+refordercode+"</refOrderCode>");  //��ƽ̨������
						bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
						bizData.append("<note></note>");
					
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
						
						bizData.append("<items>");
						
					/*	sql="select b.custombc,b.goodsname,a.outqty,b.colorname,b.sizename "
							+" from CustomerRetRcvitem0 a,v_barcodeall b "
							+" where a.sheetid='"+retsheetid+"' and a.barcodeid=b.barcodeid ";*/
						//��CustomerRetRcvitem0 v_barcodeall����ȡ����Ӧ���˻���Ϣ
						sql = new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
						.append("b.goodsname,a.outqty,b.colorname,b.sizename ")
								.append("from CustomerRetRcvitem0 a,v_barcodeall b ")
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
							goodsname=goodsname.replaceAll("��", "&apos;");
							goodsname=goodsname.replaceAll("<", "&lt;");
							goodsname=goodsname.replaceAll(">", "&gt;");
							String itemnote="��ɫ:"+htsku.get("colorname").toString().trim()+" ����:"+htsku.get("sizename").toString().trim();
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
						
						
						String msgId=sfUtil.makeMsgId(conn);
						List signParams=sfUtil.makeSignParams(bizData.toString(), serviceType,Params.msgtype,
								"","",Params.serviceversion,Params.callbackurl,msgId);
						String sign=sfUtil.makeSign(signParams);
					
						
						Map requestParams=sfUtil.makeRequestParams(bizData.toString(), serviceType, 
								msgId, Params.msgtype, sign,Params.callbackurl,
								Params.serviceversion,"");
						
						String result=CommHelper.sendRequest(Params.url, requestParams, "");
						result=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
		
						Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
						Element productinforspele = productinforspdoc.getDocumentElement();
						
						String flag=DOMHelper.getSubElementVauleByName(productinforspele, "flag");
						
						if (flag.equalsIgnoreCase("FAILURE")) //ʧ��
						{
							String errorMsg="";
							Element errorsele=(Element) productinforspele.getElementsByTagName("errors").item(0);
							NodeList errorlist=errorsele.getElementsByTagName("error");
							for(int j=0;j<errorlist.getLength();j++)
							{
								Element errorele=(Element) errorlist.item(j);
								String errorcode=DOMHelper.getSubElementVauleByName(errorele, "errorCode");
								String errordesc=DOMHelper.getSubElementVauleByName(errorele, "errorDescription");
								
								errorMsg=errorMsg+"�������:"+errorcode+",������Ϣ:"+errordesc+" ";	
							}
							
							Log.error(jobname, "ͬ���˻���ʧ��,�ӿڵ���:"+sheetid+",������Ϣ��"+errorMsg);
							
						}else
						{
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2222");
							
							sfUtil.recordMsg(conn, msgId,retsheetid,2222,serviceType);
							
							Log.info(jobname,"ͬ���˻����ɹ�,�ӿڵ���:"+sheetid);
						}
						
						conn.commit();
						conn.setAutoCommit(true);
					}catch(Exception e){
						try {
							if (conn != null && !conn.getAutoCommit()){
								conn.rollback();
								conn.setAutoCommit(true);
							}
								
						} catch (Exception e1) {
							Log.error(jobname, "�ع�����ʧ��");
						}
						Log.error(jobname, "����ͻ��˻���ⵥ����: sheetid:"+sheetid+"��ϸ������Ϣ: "+e.getMessage());
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
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
}
