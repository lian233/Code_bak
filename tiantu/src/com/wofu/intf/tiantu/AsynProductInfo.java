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
import com.wofu.common.tools.util.log.Log;
public class AsynProductInfo extends Thread {
	
	private static String jobname = "ͬ����Ʒ������ҵ";
	private static String serviceType="SyncProductInfo";
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");

		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				
				//ȡ��Ҫ��������ݵĵ���  ������Ŷ�Ӧbarcodetranlist��sheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,"2201");
				//ÿһ�����ŷ���һ������
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					Log.info("sheetid: "+sheetid);
					//ÿ�ŵ�������һ������
					StringBuffer bizData=new StringBuffer();
					//bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					bizData.append("<"+serviceType+">");
					bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
					bizData.append("<products>");
					//deptname������Ҫ�ȸ�����ͼ
					String sql= new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
					.append("a.action,b.goodsname,b.customno,b.deptname,b.barcodeid,b.goodsid,")
						.append("b.colorname,b.sizename,b.pkname,b.baseprice,isnull(b.SeriesID,'') as SeriesID,isnull(b.weigh,0) weight from BarcodeTranList a with(nolock),v_barcodeall b with(nolock)")
						.append(" where a.sheetid='")
						.append(sheetid)
						.append("' and a.barcodeid=b.barcodeid").toString();
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					Boolean isSuccess=false;
					int num=0;
					String msgid="";
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						
						String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString().trim();
						String action=htsku.get("action").toString();
						String name=htsku.get("goodsname").toString().trim();

						String customno=htsku.get("customno").toString().trim();
						String deptname=htsku.get("deptname").toString().trim();
						String barcodeid=htsku.get("barcodeid").toString().trim();

						String goodsid=htsku.get("goodsid").toString();
						String colorname=htsku.get("colorname").toString().trim();
						String sizename=htsku.get("sizename").toString().trim();
						String SeriesID=htsku.get("SeriesID").toString().trim();
						//���۹��
						String pkname=htsku.get("pkname").toString().trim();
						String baseprice=htsku.get("baseprice").toString();
						String weight=htsku.get("weight").toString();

						bizData.append("<product>");
						bizData.append("<skuCode>"+custombc+"</skuCode>");
						if (action.equals("A"))  //ADD-����
							bizData.append("<actionType>ADD</actionType>");
						else   //OW - ����
							bizData.append("<actionType>OW</actionType>");
						//xml�����ַ�ת��
						name=name.replaceAll("&", "&amp;");
						name=name.replaceAll("\"", "&quot;");
						name=name.replaceAll("��", "&apos;");
						name=name.replaceAll("<", "&lt;");
						name=name.replaceAll(">", "&gt;");
						bizData.append("<name>"+name+"</name>");
						customno=customno.replaceAll("&", "&amp;");
						customno=customno.replaceAll("\"", "&quot;");
						customno=customno.replaceAll("��", "&apos;");
						customno=customno.replaceAll("<", "&lt;");
						customno=customno.replaceAll(">", "&gt;");
						bizData.append("<englishName>"+customno+"</englishName>");
						bizData.append("<category>"+deptname+"</category>");
						bizData.append("<barCode>"+barcodeid+"</barCode>");
						bizData.append("<serialNo>"+goodsid+"</serialNo>");						
						bizData.append("<property>"+sizename+"</property>");
						bizData.append("<volume>0</volume>");
						bizData.append("<length>0</length>");
						bizData.append("<width>0</width>");
						bizData.append("<height>0</height>");
						bizData.append("<weight>"+weight+"</weight>");
						bizData.append("<unit>"+pkname+"</unit>");
						bizData.append("<packageSpec>"+colorname+"</packageSpec>");
						bizData.append("<unitPrice>"+baseprice+"</unitPrice>");
						/**
						//�ж��Ƿ�Ҫsn�����
						if("y".equalsIgnoreCase(SeriesID)){
							bizData.append("<snFlag>true</snFlag>");
						}else{
							bizData.append("<snFlag>false</snFlag>");
						}
						**/
						bizData.append("</product>");
						num++;
						if(num>=100){
							bizData.append("</products>");
							bizData.append("</"+serviceType+">");
							Log.info("bizData: "+bizData.toString());
							String bizData1 = TianTuUtil.filterChar(bizData.toString() );
							msgid=TianTuUtil.makeMsgId(conn);
							//Log.info("msgid: "+msgid);
							List signParams=TianTuUtil.makeSignParams(bizData1, serviceType,Params.msgtype,
									Params.partnerid,Params.partnerkey,Params.serviceversion,Params.callbackurl,msgid);
							String sign=TianTuUtil.makeSign(signParams);
								
							
							Map requestParams=TianTuUtil.makeRequestParams(bizData1, serviceType, 
									msgid, Params.msgtype, sign,Params.callbackurl,
									Params.serviceversion,Params.partnerid);
							Log.info("url: "+Params.url);
							String result=CommHelper.sendRequestT(Params.url, requestParams, "");
							Log.info("result:��"+result);
							result=result.substring(result.indexOf("<ProductInfo>"),result.indexOf("</ProductInfo>")+14);
							Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
							Element productinforspele = productinforspdoc.getDocumentElement();
							
							String flag=DOMHelper.getSubElementVauleByName(productinforspele, "flag");
							productinforspele.toString();
							if (flag.equalsIgnoreCase("FAILURE")) //ʧ��
							{	
								isSuccess=false;
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
								
						
								Log.error(jobname, "ͬ����Ʒ����ʧ��,�ӿڵ���:"+sheetid+"s,������Ϣ��"+errorMsg);
								
							}else   //ͬ���ɹ������ݽӿ����ݣ�д��ecs_bestlogisticsmsg��
							{
								isSuccess=true;
								if (DOMHelper.ElementIsExists(productinforspele, "products"))
								{
									Element productsele=(Element) productinforspele.getElementsByTagName("products").item(0);
									
									for (int j=0;j<productsele.getElementsByTagName("product").getLength();j++)
									{
										Element productele=(Element) productsele.getElementsByTagName("product").item(j);
										String skucode=DOMHelper.getSubElementVauleByName(productele, "skuCode");
										
										Log.info(jobname,"ͬ����Ʒ���ϳɹ�,�ӿڵ���:"+sheetid+", SKU:"+skucode);
									}
								}
								else
								{
									Log.info(jobname,"ͬ����Ʒ���ϳɹ�,�ӿڵ���:"+sheetid);
								}
							}
							num=0;
							bizData.delete(bizData.indexOf("<products>")+10, bizData.length());
							//Log.info("ɾ���������Ϊ:��"+bizData.toString());
						}					
						}
					if(bizData.indexOf("<product>")!=-1){
						//Log.info("β��:��");
						bizData.append("</products>");
						bizData.append("</"+serviceType+">");
						String bizData1 = TianTuUtil.filterChar(bizData.toString() );
						Log.info("β��bizData: "+bizData.toString());
						
						msgid=TianTuUtil.makeMsgId(conn);
						//Log.info("msgid: "+msgid);
						List signParams=TianTuUtil.makeSignParams(bizData1, serviceType,Params.msgtype,
								Params.partnerid,Params.partnerkey,Params.serviceversion,Params.callbackurl,msgid);
						String sign=TianTuUtil.makeSign(signParams);
							
						
						Map requestParams=TianTuUtil.makeRequestParams(bizData1, serviceType, 
								msgid, Params.msgtype, sign,Params.callbackurl,
								Params.serviceversion,Params.partnerid);
						Log.info("url: "+Params.url);
						String result=CommHelper.sendRequestT(Params.url, requestParams, "");
						Log.info("result: "+result);
						result=result.substring(result.indexOf("<ProductInfo>"),result.indexOf("</ProductInfo>")+14);
						Log.info("result: "+result);
						//Document productinfodoc = DOMHelper.newDocument(result, Params.encoding);
						//Element productinfoele = productinfodoc.getDocumentElement();
						
						//Element responsesele=(Element) productinfoele.getElementsByTagName("response").item(0);
						
						//String bizDataRsp=DOMHelper.getSubElementVauleByName(responsesele, "bizData");
						
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
							
					
							Log.error(jobname, "ͬ����Ʒ����ʧ��,�ӿڵ���:"+sheetid+",������Ϣ��"+errorMsg);
							
						}else   //ͬ���ɹ������ݽӿ����ݣ�д��ecs_bestlogisticsmsg��
						{
							isSuccess=true;
							if (DOMHelper.ElementIsExists(productinforspele, "products"))
							{
								Element productsele=(Element) productinforspele.getElementsByTagName("products").item(0);
								
								for (int j=0;j<productsele.getElementsByTagName("product").getLength();j++)
								{
									Element productele=(Element) productsele.getElementsByTagName("product").item(j);
									String skucode=DOMHelper.getSubElementVauleByName(productele, "skuCode");
									
									Log.info(jobname,"ͬ����Ʒ���ϳɹ�,�ӿڵ���:"+sheetid+", SKU:"+skucode);
								}
							}
							else
							{
								Log.info(jobname,"ͬ����Ʒ���ϳɹ�,�ӿڵ���:"+sheetid);
							}
						}
					}
					if(isSuccess){
						conn.setAutoCommit(false);
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2201");
						TianTuUtil.recordMsg(conn,msgid,sheetid,2201,serviceType);
						conn.commit();
						conn.setAutoCommit(true);
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
