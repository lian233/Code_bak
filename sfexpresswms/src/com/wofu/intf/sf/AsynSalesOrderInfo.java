package com.wofu.intf.sf;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sf.integration.warehouse.service.GetoutsideToLscService;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
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
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					try{
						String sql="select count(*) from customerdelive0 "
							+"where refsheetid='"+sheetid+"'";
						if (SQLHelper.intSelect(conn, sql)==0)
						{
							Log.info("���ݲ����ڻ����Ѵ���,�ӿڵ���:"+sheetid);
							continue;
						}
						

						
						conn.setAutoCommit(false);
						boolean isVip=false;
						StringBuffer bizData=new StringBuffer();
						bizData.append("<wmsSailOrderRequest>");
						bizData.append("<checkword>").append(Params.checkword).append("</checkword>")
						.append("<header>")
						.append("<company>").append(Params.company).append("</company>")
						.append("<warehouse>").append(Params.warehouse).append("</warehouse>");
						
						//��customerdelive0ȡ������Ϣ
						sql="select a.inshopid,a.outshopid,a.sheetid,a.customersheetid,convert(char(19),a.delivedate,120) as delivedate,"
									+"a.paymode,a.payfee,a.delivery,a.notes,isnull(a.invoiceflag,0) as invoiceflag,isnull(a.invoicetitle,'') as invoicetitle,"
									+"a.detailid,a.linktele,isnull(a.linkman,'����') as linkman,a.address,a.zipcode,isnull(a.invoiceNote,'') as invoiceNote,b.purchaseflag from customerdelive0  a with(nolock),customerorder b "
									+"where a.refsheetid='"+sheetid+"' and a.refsheetid=b.sheetid";
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
						String notes=htorder.get("notes").toString();
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
						bizData.append("<shop_name>").append(inshopid).append("</shop_name>")
						.append("<erp_order>"+outbuzcode+"</erp_order>")
						.append("<order_type>���۶���</order_type>")
						.append("<order_date>").append(delivedate).append("</order_date>")
						.append("<ship_to_name>").append(linkman).append("</ship_to_name>")
						.append("<ship_to_attention_to>").append(linkman).append("</ship_to_attention_to>")
						.append("<ship_to_address>").append(address).append("</ship_to_address>")
						.append("<ship_to_postal_code>").append(zipcode).append("</ship_to_postal_code>")
						.append("<ship_to_phone_num>").append(linktele).append("</ship_to_phone_num>")
						//.append("<carrier>").append(delivery).append("</carrier>");
						.append("<carrier>").append("˳������").append("</carrier>");//�����ã���������Ҫ��ά����
						if (paymode.equals("2"))  //��������д���տ���Ϣ
						{
							bizData.append("<cod>Y</cod>")
							.append("<amount>"+payfee+"</amount>");
						}
						if (invoiceflag.equals("1"))	//��Ҫ������д��Ʊ��Ϣ
						{
							bizData.append("<invoice>Y</invoice>")
							.append("<invoice_type>"+"��ͨ��Ʊ"+"</invoice_type>")
							.append("<invoice_title>"+invoicetitle+"</invoice_title>")
							//��Ʊ�������ʺ�
							.append("<invoice_content>"+invoiceNote+"</invoice_content>");
						}
						bizData.append("<order_note>"+notes+"</order_note>")
						//�ʷ�֧����ʽ ���ķ�����Ҫд��������û��ָ��payment_of_charge����Ĭ���Ǽķ�����
						.append("<monthly_account>").append(Params.monthly_account).append("</monthly_account>")
						.append("</header>")
						.append("<detailList>");
						sql = new StringBuilder().append("select b.custombc,")
							.append("a.customprice,a.pkname,a.purqty,a.title,a.notes,c.name goodsname ")
								.append(" from customerdeliveitem0 a,barcode b,goods c ")
								.append(" where a.sheetid='").append(outbuzcode)
								.append("' and a.barcodeid=b.barcodeid and a.goodsid=c.goodsid").toString();
						
						Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
						for (int i=0;i<vtsku.size();i++)
						{
							Hashtable htsku=(Hashtable) vtsku.get(i);
							String customprice = htsku.get("customprice").toString();
							String pkname = htsku.get("pkname").toString();
							String custombc=htsku.get("custombc").toString();
							int purqty=Double.valueOf(htsku.get("purqty").toString()).intValue();
							String title=htsku.get("title").toString();
							
							title=StringUtil.replace(title, "<", "&lt;");
							title=StringUtil.replace(title, ">", "&gt;");
							title=StringUtil.replace(title, "&", "&amp;");
							//���۸�Ϊ���sku�Ļ�ֵ��Ҳ�������sku����x����
							String itemnotes=htsku.get("notes").toString();
							String goodsname=htsku.get("goodsname").toString();
							goodsname=StringUtil.replace(goodsname, "<", "&lt;");
							goodsname=StringUtil.replace(goodsname, ">", "&gt;");
							goodsname=StringUtil.replace(goodsname, "&", "&amp;");
							
							if (title.equals("")) title=goodsname;
							bizData.append("<item>")
							.append("<erp_order_line_num>").append(i+1).append("</erp_order_line_num>")
							.append("<item>").append(custombc).append("</item>")
							.append("<item_name>"+title+"</item_name>")
							//storage_template ���洢ģ��
							//.append("<uom>").append(pkname).append("</uom>")
							.append("<uom>").append("ֻ").append("</uom>")
							.append("<qty>"+purqty+"</qty>")
							.append("<item_price>"+customprice+"</item_price>")
							.append("</item>");
						}										
						bizData.append("</detailList></wmsSailOrderRequest>");
						Log.info("bizdata: "+bizData.toString());
						
						//����post���󣬲�ȡ�÷��ؽ��
						String result=GetoutsideToLscService.getoutsideToLscServices(bizData.toString());
						Log.info("result: "+result);

						//���ؽ����ԭ��document
						Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
						Element productinforspele = productinforspdoc.getDocumentElement();
						
						String flag=DOMHelper.getSubElementVauleByName(productinforspele, "result");
						//�жϴ����ɹ����
						if (flag.equalsIgnoreCase("2")) //ʧ��
						{
							String errorMsg=DOMHelper.getSubElementVauleByName(productinforspele, "remark");
							Log.error(jobname, "ͬ��������ʧ��,�ӿڵ���:"+sheetid+",������Ϣ��"+errorMsg);
							
						}else
						{
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2209");
							sfUtil.recordMsg(conn, CommHelper.getMsgid(),outbuzcode,2209,serviceType);
							Log.info(jobname,"ͬ���������ɹ�,�ӿڵ���:"+sheetid);
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
