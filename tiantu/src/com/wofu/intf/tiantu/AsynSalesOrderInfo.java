package com.wofu.intf.tiantu;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
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
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
public class AsynSalesOrderInfo extends Thread {
	private static DecimalFormat sf= new DecimalFormat("0.00");  //������λС������������
	private static String jobname = "ͬ����������ҵ";
	private static String serviceType="SyncSalesOrderInfo";
	private static String sheettype="2209";
	private int threadid;
	private int threadcount;
	
	public int getThreadid() {
		return threadid;
	}

	public void setThreadid(int threadid) {
		this.threadid = threadid;
	}

	
	public int getThreadcount() {
		return threadcount;
	}

	public void setThreadcount(int threadcount) {
		this.threadcount = threadcount;
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	

				List infsheetlist=null;
				if (this.getThreadcount()>1)  //���̴߳���
				{
					String sql="select sheetid from IT_InfSheetThreadList where SheetType="+sheettype
						+" and interfacesystem='"+Params.interfacesystem+"' and threadid="+this.getThreadid()
						+" and sheetid in(select sheetid from it_infsheetlist0 where sheettype="+sheettype+")";
	
					infsheetlist=SQLHelper.multiRowListSelect(conn, sql);
				}else    //���̴߳���
				{
					infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);
				}


				Log.info("���δ���Ķ�����Ϊ��"+infsheetlist.size());
				int p=0;
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					p++;
					String sheetid=(String) it.next();
					try{
						String sql="select count(*) from customerdelive0  with(nolock) "
							+"where refsheetid='"+sheetid+"'";
						if (SQLHelper.intSelect(conn, sql)==0)
						{
							Log.info("���ݲ����ڻ����Ѵ���,�ӿڵ���:"+sheetid);
							continue;
						}
						if(p>50000) break;
						
						boolean isVip=false;
						StringBuffer bizData=new StringBuffer();
						bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
						bizData.append("<"+serviceType+">");
						bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
						
						//��customerdelive0ȡ������Ϣ
						
						sql="select a.inshopid,a.outshopid,a.sheetid,a.customersheetid,convert(char(19),a.PurDate,120) as delivedate,"
									+"a.paymode,a.payfee,a.delivery,b.notes,isnull(a.invoiceflag,0) as invoiceflag,isnull(a.invoicetitle,'') as invoicetitle,"
									+"a.detailid,a.linktele,isnull(a.linkman,'����') as linkman,isnull(a.deliverysheetid,'') deliverysheetid,a.address,a.zipcode,isnull(a.invoiceNote,'') as invoiceNote,b.purchaseflag from customerdelive0  a with(nolock),customerorder b "
									+"with(nolock) where a.refsheetid='"+sheetid+"' and a.refsheetid=b.sheetid";
						/**			
						sql ="select a.inshopid,a.outshopid,a.sheetid,a.customersheetid,convert(char(19),a.delivedate,120) as delivedate,"
									+"a.paymode,a.payfee,a.delivery,a.notes,isnull(a.invoiceflag,0) as invoiceflag,isnull(a.invoicetitle,'') as invoicetitle,"
									+"a.detailid,a.linktele,isnull(a.linkman,'����') as linkman,a.address,a.zipcode,isnull(a.invoiceNote,'') as invoiceNote from customerdelive0  a with(nolock) "
									+"where a.refsheetid='"+sheetid+"'";
									**/
						Hashtable htorder=SQLHelper.oneRowSelect(conn, sql);
						String inshopid=htorder.get("inshopid").toString();
						String outshopid=htorder.get("outshopid").toString();
						String outbuzcode=htorder.get("sheetid").toString();
						String customersheetid=htorder.get("customersheetid").toString();
						String delivedate=htorder.get("delivedate").toString();
						String paymode=htorder.get("paymode").toString();
						String payfee=htorder.get("payfee").toString();
						String PurchaseFlag= htorder.get("purchaseflag").toString();
					
						String delivery=htorder.get("delivery").toString();
						String notes=htorder.get("notes").toString();//������ע
						String deliverysheetid=htorder.get("deliverysheetid").toString();//������ע
						notes=StringUtil.replace(notes, "<", "&lt;");
						notes=StringUtil.replace(notes, ">", "&gt;");
						notes=StringUtil.replace(notes, "&", "&amp;");
						String invoiceflag=htorder.get("invoiceflag").toString();
						String invoicetitle=htorder.get("invoicetitle").toString();
						invoicetitle=StringUtil.replace(invoicetitle, "<", "&lt;");
						invoicetitle=StringUtil.replace(invoicetitle, ">", "&gt;");
						invoicetitle=StringUtil.replace(invoicetitle, "&", "&amp;");
						String detailid=htorder.get("detailid").toString();
						String linktele=htorder.get("linktele").toString();
						linktele=StringUtil.replace(linktele, "<", "&lt;");
						linktele=StringUtil.replace(linktele, ">", "&gt;");
						linktele=StringUtil.replace(linktele, "&", "&amp;");
						String linkman=htorder.get("linkman").toString();
						String address=htorder.get("address").toString();
						String zipcode=htorder.get("zipcode").toString();
						String invoiceNote=htorder.get("invoiceNote").toString();
						invoiceNote=StringUtil.replace(invoiceNote, "<", "&lt;");
						invoiceNote=StringUtil.replace(invoiceNote, ">", "&gt;");
						invoiceNote=StringUtil.replace(invoiceNote, "&", "&amp;");
						bizData.append("<warehouseCode>"+(Params.warehouseMulti?TianTuUtil.getWarehouseCode(conn,Params.customercode,outshopid,serviceType):TianTuUtil.getWarehouseCode(conn,Params.customercode,outshopid))+"</warehouseCode>");
						bizData.append("<warehouseAddressCode>").append("</warehouseAddressCode>");
						bizData.append("<orderCode>"+outbuzcode+"</orderCode>");
						bizData.append("<actionType>ADD</actionType>");
						bizData.append("<extTradeId>"+customersheetid+"</extTradeId>");
						if("5".equals(PurchaseFlag)){  //�����̳ǳ���
							bizData.append("<orderType>NORMAL</orderType>");
						}else{   //���
							bizData.append("<orderType>WDO</orderType>");
						}
						
						bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
						
						sql="select a.tradefrom from customerorder a with(nolock),customerdelive0 b with(nolock) where a.sheetid= b.refsheetid and b.refsheetid='"+sheetid+"'";
						
						String tradeplatform=SQLHelper.strSelect(conn, sql);
						if("TAOBAO".equals(tradeplatform)){   //�����Ψ��Ʒ��������"vip��־"
							bizData.append("<orderSource>TAOBAO</orderSource>");
							bizData.append("<noStackTag>�ǻ</noStackTag>");
						}else if(tradeplatform.indexOf("JHS")!=-1 || tradeplatform.indexOf("jhs")!=-1){
							bizData.append("<orderSource>TAOBAO</orderSource>");
							bizData.append("<noStackTag>�ۻ���</noStackTag>");
						}
						else if(tradeplatform.indexOf("WAP")!=-1 || tradeplatform.indexOf("wap")!=-1){
							bizData.append("<orderSource>TAOBAO</orderSource>");
							bizData.append("<noStackTag>�ǻ</noStackTag>");
						}else if("360buy".equals(tradeplatform)){
							bizData.append("<orderSource>360BUY</orderSource>");
							bizData.append("<noStackTag>�ǻ</noStackTag>");
						}else if("WEIPINHUI".equals(tradeplatform)){
							isVip=true;
							bizData.append("<orderSource>VIP</orderSource>");
							bizData.append("<noStackTag>�ǻ</noStackTag>");
						}else if("paipai".equals(tradeplatform)){
							bizData.append("<orderSource>PAIPAI</orderSource>");
							bizData.append("<noStackTag>�ǻ</noStackTag>");
						}else if("dangdang".equals(tradeplatform)){
							bizData.append("<orderSource>DANGDANG</orderSource>");
							bizData.append("<noStackTag>�ǻ</noStackTag>");
						}
						else{
							bizData.append("<orderSource>OTHER</orderSource>");
							bizData.append("<noStackTag>�ǻ</noStackTag>");
						}
						
						bizData.append("<orderTime>"+delivedate+"</orderTime>");
						sql="select sum(customprice*purqty) from customerdeliveitem0 with(nolock) where sheetid='"+outbuzcode+"'";
						String totalfee=SQLHelper.strSelect(conn, sql);
						if(!"".equals(totalfee)){
							totalfee=sf.format(Double.parseDouble(totalfee));
							bizData.append("<totalAmount>"+totalfee+"</totalAmount>");
						}
						
						if (paymode.equals("2"))  //��������д���տ���Ϣ
						{
							bizData.append("<isPaymentCollected>true</isPaymentCollected>");
							bizData.append("<collectingPaymentAmount>"+payfee+"</collectingPaymentAmount>");
							if("ZJS".equalsIgnoreCase(delivery))   
								bizData.append("<extOrderType>ZJS-COD</extOrderType>");
							else
								bizData.append("<extOrderType>COD</extOrderType>");
						}
						bizData.append("<logisticsProviderCode>"+delivery+"</logisticsProviderCode>");
						if(Params.jdkdSend && !"".equals(deliverysheetid) && "JDKD".equalsIgnoreCase(delivery) )
							bizData.append("<shippingOrderNo>"+deliverysheetid+"</shippingOrderNo>");
						bizData.append("<note>"+notes+"</note>");
						

						
						sql="select name from shop with(nolock) where id='"+inshopid+"'";
						
						String shopname=SQLHelper.strSelect(conn, sql);
						bizData.append("<sellerName>"+shopname+"</sellerName>");
						if (invoiceflag.equals("1"))	//��Ҫ������д��Ʊ��Ϣ
						{
							bizData.append("<invoiceFlag>true</invoiceFlag>");
							bizData.append("<invoiceTitle>"+invoicetitle+"</invoiceTitle>");
							bizData.append("<invoiceAmount>"+totalfee+"</invoiceAmount>");
							//��Ʊ�������ʺ�
							Log.info("��Ʊ����: "+invoiceNote);
							bizData.append("<invoiceNote>"+invoiceNote+"</invoiceNote>");
						}
						bizData.append("<buyerName>"+detailid+"</buyerName>");
						bizData.append("<buyerPhone>"+linktele+"</buyerPhone>");
						
						linkman=StringUtil.replace(linkman, "<", "&lt;");
						linkman=StringUtil.replace(linkman, ">", "&gt;");
						linkman=StringUtil.replace(linkman, "&", "&amp;");
						
						//д�ջ�����Ϣ
						bizData.append("<recipient>");
						bizData.append("<name>"+linkman+"</name>");
						bizData.append("<postalCode>"+zipcode+"</postalCode>");
						
						address=StringUtil.replace(address, "<", "&lt;");
						address=StringUtil.replace(address, ">", "&gt;");
						address=StringUtil.replace(address, "&", "&amp;");
						address=StringUtil.replace(address, (char)12+"", "");  //ascii 12�滻�ɿ�
						
						

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
						//���ƶ��dc��Ӧһ�������   ��ʱ��Ҫ��dcֵҲ��������ϵͳ������Ҳ��ԭ���������ֵ
						if(Params.isMultiDcToONeWare){
							bizData.append("<udfFlag>true</udfFlag>")
							.append("<udf1>").append(outshopid).append("</udf1>");
						}
						//ƴװ��Ʒ����
						bizData.append("<items>");
						
						/*sql="select b.custombc,a.customprice,a.purqty,a.title,a.notes,c.name goodsname "
							+" from customerdeliveitem0 a,barcode b,goods c "
							+" where a.sheetid='"+outbuzcode+"' and a.barcodeid=b.barcodeid and a.goodsid=c.goodsid";*/
						//��customerdeliveitem0��barcode goods����ȡ����Ӧ�Ķ�����ϸ��Ϣ
						if(isVip)
						sql = new StringBuilder().append("select b.barcodeid,b.custombc,")
						.append("a.customprice*a.purqty as customprice,a.purqty,a.title,a.notes,c.name goodsname ,d.name brandName")
							.append(" from customerdeliveitem0 a  with(nolock) ,barcode b  with(nolock) ,goods c  with(nolock) ,brand d  with(nolock) ")
							.append(" where a.sheetid='").append(outbuzcode)
							.append("' and a.barcodeid=b.barcodeid and a.goodsid=c.goodsid and c.brandid=d.id").toString();
						else{
							sql = new StringBuilder().append("select b.barcodeid,b.custombc,")
							.append("a.customprice*a.purqty as customprice,a.purqty,a.title,a.notes,c.name goodsname ")
								.append(" from customerdeliveitem0 a with(nolock) ,barcode b with(nolock) ,goods c  with(nolock) ")
								.append(" where a.sheetid='").append(outbuzcode)
								.append("' and a.barcodeid=b.barcodeid and a.goodsid=c.goodsid").toString();
						}
						Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
						BigDecimal resultPrice= new BigDecimal("0");
						for (int i=0;i<vtsku.size();i++)
						{
							Hashtable htsku=(Hashtable) vtsku.get(i);
							
							String custombc=htsku.get("custombc").toString();
							String barcodeid=htsku.get("barcodeid").toString();
							int purqty=Double.valueOf(htsku.get("purqty").toString()).intValue();
							String title=htsku.get("title").toString();
							title=StringUtil.replace(title, "&", "&amp;");
							title=StringUtil.replace(title, "<", "&lt;");
							title=StringUtil.replace(title, ">", "&gt;");
							
							//���۸�Ϊ���sku�Ļ�ֵ��Ҳ�������sku����x����
							
							String customprice="";
							if(i==vtsku.size()-1){
								customprice=sf.format(new BigDecimal(Double.parseDouble(totalfee)).subtract(resultPrice).doubleValue());
							}else{
								customprice=htsku.get("customprice").toString();
								customprice = sf.format(Double.parseDouble(customprice));
								resultPrice=new BigDecimal(Double.parseDouble(customprice)).add(resultPrice);
							}
							//Log.info("customprice: "+customprice);
							String itemnotes=htsku.get("notes").toString();
							String goodsname=htsku.get("goodsname").toString();
							goodsname=StringUtil.replace(goodsname, "&", "&amp;");
							goodsname=StringUtil.replace(goodsname, "<", "&lt;");
							goodsname=StringUtil.replace(goodsname, ">", "&gt;");
							
							
							//Ʒ����
							String brandName = htsku.get("brandName")!=null?htsku.get("brandName").toString():"";
							
							if (title.equals("")) title=goodsname;
							
							bizData.append("<item>");
							bizData.append("<itemSkuCode>"+custombc+"</itemSkuCode>");
							bizData.append("<BarCode>"+barcodeid+"</BarCode>");
							bizData.append("<itemName>"+title.trim()+"</itemName>");
							bizData.append("<itemQuantity>"+purqty+"</itemQuantity>");
							bizData.append("<itemUnitPrice>"+customprice+"</itemUnitPrice>");
							bizData.append("<itemNote>"+itemnotes.trim()+"</itemNote>");
							//Ʒ����
							if(isVip){
								bizData.append("<userDefined1>"+brandName+"</userDefined1>");
							}
							
							bizData.append("</item>");
						}										
						bizData.append("</items>");
						bizData.append("</"+serviceType+">");
						String bizData1 = TianTuUtil.filterChar(bizData.toString() );
						Log.info("bizdata1: "+bizData1);
						//ȡ�õ��ţ�����id+���)
						String msgId=TianTuUtil.makeMsgId(conn);
						//
						List signParams=TianTuUtil.makeSignParams(bizData1, serviceType,Params.msgtype,
								Params.partnerid,Params.partnerkey,Params.serviceversion,Params.callbackurl,msgId);
						//����ǩ��-MD5����
						String sign=TianTuUtil.makeSign(signParams);
					
						//ƴװ�������
						Map requestParams=TianTuUtil.makeRequestParams(bizData1, serviceType, 
								msgId, Params.msgtype, sign,Params.callbackurl,
								Params.serviceversion,Params.partnerid);
						//����post���󣬲�ȡ�÷��ؽ��
						
						String result=CommHelper.sendRequestT(Params.url, requestParams, "");
						//Log.info("result: "+result);

					
						try
						{
							result=result.substring(result.indexOf("<SalesOrderInfo>"),result.indexOf("</SalesOrderInfo>")+17);
						}catch(Exception e)
						{
							
							throw new JException(result.concat(e.getMessage()));
						}
						Log.info("result: "+result);
						//���ؽ����ԭ��document
						Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
						Element productinforspele = productinforspdoc.getDocumentElement();
						
						String flag=DOMHelper.getSubElementVauleByName(productinforspele, "flag");
						//�жϴ����ɹ����
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
								if("���ⵥ�Ѵ���".equals(errordesc)){
									conn.setAutoCommit(false);
									IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2209");
									conn.commit();
									conn.setAutoCommit(true);
								}
								errorMsg=errorMsg+"�������:"+errorcode+",������Ϣ:"+errordesc+" ";	
							}
													
							Log.error(jobname, "ͬ��������ʧ��,�ӿڵ���:"+sheetid+",������Ϣ��"+errorMsg);
							
						}else
						{
							conn.setAutoCommit(false);
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2209");
							conn.commit();
							conn.setAutoCommit(true);
							TianTuUtil.recordMsg(conn, msgId,outbuzcode,2209,serviceType);
							Log.info(jobname,"ͬ���������ɹ�,�ӿڵ���:"+sheetid);
						}
						
						
					}catch(Exception e){
						try {
							if (conn != null && !conn.getAutoCommit()){
								conn.rollback();
								conn.setAutoCommit(true);
							}
								
						} catch (Exception e1) {
							Log.error(jobname, "�ع�����ʧ��");
						}
						Log.error(jobname, "����ͻ����ⵥ����,sheetid: "+sheetid+"������ϸ��Ϣ: "+e.getMessage());
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
