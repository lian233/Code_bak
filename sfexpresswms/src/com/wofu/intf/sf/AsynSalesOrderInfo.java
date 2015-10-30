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
	private static DecimalFormat sf= new DecimalFormat("0.00");  //保留二位小数，四舍五入
	private static String jobname = "同步发货单作业";
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
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	

				List infsheetlist=null;
				if (this.getThreadcount()>1)  //多线程处理
				{
					String sql="select sheetid from IT_InfSheetThreadList where SheetType="+sheettype
						+" and interfacesystem='"+Params.interfacesystem+"' and threadid="+this.getThreadid()
						+" and sheetid in(select sheetid from it_infsheetlist0 where sheettype="+sheettype+")";
	
					infsheetlist=SQLHelper.multiRowListSelect(conn, sql);
				}else    //单线程处理
				{
					infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);
				}


				Log.info("本次处理的订单数为："+infsheetlist.size());
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					try{
						String sql="select count(*) from customerdelive0 "
							+"where refsheetid='"+sheetid+"'";
						if (SQLHelper.intSelect(conn, sql)==0)
						{
							Log.info("单据不存在或者已处理,接口单号:"+sheetid);
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
						
						//从customerdelive0取订单信息
						sql="select a.inshopid,a.outshopid,a.sheetid,a.customersheetid,convert(char(19),a.delivedate,120) as delivedate,"
									+"a.paymode,a.payfee,a.delivery,a.notes,isnull(a.invoiceflag,0) as invoiceflag,isnull(a.invoicetitle,'') as invoicetitle,"
									+"a.detailid,a.linktele,isnull(a.linkman,'张三') as linkman,a.address,a.zipcode,isnull(a.invoiceNote,'') as invoiceNote,b.purchaseflag from customerdelive0  a with(nolock),customerorder b "
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
						.append("<order_type>销售订单</order_type>")
						.append("<order_date>").append(delivedate).append("</order_date>")
						.append("<ship_to_name>").append(linkman).append("</ship_to_name>")
						.append("<ship_to_attention_to>").append(linkman).append("</ship_to_attention_to>")
						.append("<ship_to_address>").append(address).append("</ship_to_address>")
						.append("<ship_to_postal_code>").append(zipcode).append("</ship_to_postal_code>")
						.append("<ship_to_phone_num>").append(linktele).append("</ship_to_phone_num>")
						//.append("<carrier>").append(delivery).append("</carrier>");
						.append("<carrier>").append("顺丰速运").append("</carrier>");//测试用，生产环境要先维护好
						if (paymode.equals("2"))  //货到付款写代收款信息
						{
							bizData.append("<cod>Y</cod>")
							.append("<amount>"+payfee+"</amount>");
						}
						if (invoiceflag.equals("1"))	//需要开发货写发票信息
						{
							bizData.append("<invoice>Y</invoice>")
							.append("<invoice_type>"+"普通发票"+"</invoice_type>")
							.append("<invoice_title>"+invoicetitle+"</invoice_title>")
							//发票的银行帐号
							.append("<invoice_content>"+invoiceNote+"</invoice_content>");
						}
						bizData.append("<order_note>"+notes+"</order_note>")
						//邮费支付方式 ：寄方付款要写这个，如果没有指定payment_of_charge，则默认是寄方付的
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
							//单价改为这个sku的货值，也就是这个sku单价x数量
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
							//storage_template （存储模板
							//.append("<uom>").append(pkname).append("</uom>")
							.append("<uom>").append("只").append("</uom>")
							.append("<qty>"+purqty+"</qty>")
							.append("<item_price>"+customprice+"</item_price>")
							.append("</item>");
						}										
						bizData.append("</detailList></wmsSailOrderRequest>");
						Log.info("bizdata: "+bizData.toString());
						
						//发送post请求，并取得返回结果
						String result=GetoutsideToLscService.getoutsideToLscServices(bizData.toString());
						Log.info("result: "+result);

						//返回结果还原成document
						Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
						Element productinforspele = productinforspdoc.getDocumentElement();
						
						String flag=DOMHelper.getSubElementVauleByName(productinforspele, "result");
						//判断传单成功与否
						if (flag.equalsIgnoreCase("2")) //失败
						{
							String errorMsg=DOMHelper.getSubElementVauleByName(productinforspele, "remark");
							Log.error(jobname, "同步发货单失败,接口单号:"+sheetid+",错误信息："+errorMsg);
							
						}else
						{
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2209");
							sfUtil.recordMsg(conn, CommHelper.getMsgid(),outbuzcode,2209,serviceType);
							Log.info(jobname,"同步发货单成功,接口单号:"+sheetid);
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
							Log.error(jobname, "回滚事务失败");
						}
						Log.error(jobname, "处理客户出库单错误,sheetid: "+sheetid+"错误详细信息: "+e.getMessage());
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
