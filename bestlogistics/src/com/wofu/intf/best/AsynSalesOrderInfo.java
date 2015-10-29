package com.wofu.intf.best;
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
							Log.info("单据不存在或者已处理,接口单号:"+sheetid);
							continue;
						}
						if(p>50000) break;
						
						boolean isVip=false;
						StringBuffer bizData=new StringBuffer();
						bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
						bizData.append("<"+serviceType+">");
						bizData.append("<customerCode>"+Params.customercode+"</customerCode>");
						
						//从customerdelive0取订单信息
						
						sql="select a.inshopid,a.outshopid,a.sheetid,a.customersheetid,convert(char(19),a.PurDate,120) as delivedate,"
									+"a.paymode,a.payfee,a.delivery,b.notes,isnull(a.invoiceflag,0) as invoiceflag,isnull(a.invoicetitle,'') as invoicetitle,"
									+"a.detailid,a.linktele,isnull(a.linkman,'张三') as linkman,isnull(a.deliverysheetid,'') deliverysheetid,a.address,a.zipcode,isnull(a.invoiceNote,'') as invoiceNote,b.purchaseflag from customerdelive0  a with(nolock),customerorder b "
									+"with(nolock) where a.refsheetid='"+sheetid+"' and a.refsheetid=b.sheetid";
						/**			
						sql ="select a.inshopid,a.outshopid,a.sheetid,a.customersheetid,convert(char(19),a.delivedate,120) as delivedate,"
									+"a.paymode,a.payfee,a.delivery,a.notes,isnull(a.invoiceflag,0) as invoiceflag,isnull(a.invoicetitle,'') as invoicetitle,"
									+"a.detailid,a.linktele,isnull(a.linkman,'张三') as linkman,a.address,a.zipcode,isnull(a.invoiceNote,'') as invoiceNote from customerdelive0  a with(nolock) "
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
						String notes=htorder.get("notes").toString();//订单备注
						String deliverysheetid=htorder.get("deliverysheetid").toString();//订单备注
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
						bizData.append("<warehouseCode>"+(Params.warehouseMulti?BestUtil.getWarehouseCode(conn,Params.customercode,outshopid,serviceType):BestUtil.getWarehouseCode(conn,Params.customercode,outshopid))+"</warehouseCode>");
						bizData.append("<warehouseAddressCode>").append("</warehouseAddressCode>");
						bizData.append("<orderCode>"+outbuzcode+"</orderCode>");
						bizData.append("<actionType>ADD</actionType>");
						bizData.append("<extTradeId>"+customersheetid+"</extTradeId>");
						if("5".equals(PurchaseFlag)){  //网上商城出库
							bizData.append("<orderType>NORMAL</orderType>");
						}else{   //大货
							bizData.append("<orderType>WDO</orderType>");
						}
						
						bizData.append("<extOrderType>"+sheettype+"</extOrderType>");
						
						sql="select a.tradefrom from customerorder a with(nolock),customerdelive0 b with(nolock) where a.sheetid= b.refsheetid and b.refsheetid='"+sheetid+"'";
						
						String tradeplatform=SQLHelper.strSelect(conn, sql);
						if("TAOBAO".equals(tradeplatform)){   //如果是唯会品订单加上"vip标志"
							bizData.append("<orderSource>TAOBAO</orderSource>");
							bizData.append("<noStackTag>非活动</noStackTag>");
						}else if(tradeplatform.indexOf("JHS")!=-1 || tradeplatform.indexOf("jhs")!=-1){
							bizData.append("<orderSource>TAOBAO</orderSource>");
							bizData.append("<noStackTag>聚划算</noStackTag>");
						}
						else if(tradeplatform.indexOf("WAP")!=-1 || tradeplatform.indexOf("wap")!=-1){
							bizData.append("<orderSource>TAOBAO</orderSource>");
							bizData.append("<noStackTag>非活动</noStackTag>");
						}else if("360buy".equals(tradeplatform)){
							bizData.append("<orderSource>360BUY</orderSource>");
							bizData.append("<noStackTag>非活动</noStackTag>");
						}else if("WEIPINHUI".equals(tradeplatform)){
							isVip=true;
							bizData.append("<orderSource>VIP</orderSource>");
							bizData.append("<noStackTag>非活动</noStackTag>");
						}else if("paipai".equals(tradeplatform)){
							bizData.append("<orderSource>PAIPAI</orderSource>");
							bizData.append("<noStackTag>非活动</noStackTag>");
						}else if("dangdang".equals(tradeplatform)){
							bizData.append("<orderSource>DANGDANG</orderSource>");
							bizData.append("<noStackTag>非活动</noStackTag>");
						}
						else{
							bizData.append("<orderSource>OTHER</orderSource>");
							bizData.append("<noStackTag>非活动</noStackTag>");
						}
						
						bizData.append("<orderTime>"+delivedate+"</orderTime>");
						sql="select sum(customprice*purqty) from customerdeliveitem0 with(nolock) where sheetid='"+outbuzcode+"'";
						String totalfee=SQLHelper.strSelect(conn, sql);
						if(!"".equals(totalfee)){
							totalfee=sf.format(Double.parseDouble(totalfee));
							bizData.append("<totalAmount>"+totalfee+"</totalAmount>");
						}
						
						if (paymode.equals("2"))  //货到付款写代收款信息
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
						if (invoiceflag.equals("1"))	//需要开发货写发票信息
						{
							bizData.append("<invoiceFlag>true</invoiceFlag>");
							bizData.append("<invoiceTitle>"+invoicetitle+"</invoiceTitle>");
							bizData.append("<invoiceAmount>"+totalfee+"</invoiceAmount>");
							//发票的银行帐号
							Log.info("发票内容: "+invoiceNote);
							bizData.append("<invoiceNote>"+invoiceNote+"</invoiceNote>");
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
						//拼装商品详情
						bizData.append("<items>");
						
						/*sql="select b.custombc,a.customprice,a.purqty,a.title,a.notes,c.name goodsname "
							+" from customerdeliveitem0 a,barcode b,goods c "
							+" where a.sheetid='"+outbuzcode+"' and a.barcodeid=b.barcodeid and a.goodsid=c.goodsid";*/
						//从customerdeliveitem0，barcode goods表中取得相应的订单明细信息
						if(isVip)
						sql = new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
						.append("a.customprice*a.purqty as customprice,a.purqty,a.title,a.notes,c.name goodsname ,d.name brandName")
							.append(" from customerdeliveitem0 a  with(nolock) ,barcode b  with(nolock) ,goods c  with(nolock) ,brand d  with(nolock) ")
							.append(" where a.sheetid='").append(outbuzcode)
							.append("' and a.barcodeid=b.barcodeid and a.goodsid=c.goodsid and c.brandid=d.id").toString();
						else{
							sql = new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
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
							
							String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString();
							int purqty=Double.valueOf(htsku.get("purqty").toString()).intValue();
							String title=htsku.get("title").toString();
							
							title=StringUtil.replace(title, "<", "&lt;");
							title=StringUtil.replace(title, ">", "&gt;");
							title=StringUtil.replace(title, "&", "&amp;");
							//单价改为这个sku的货值，也就是这个sku单价x数量
							
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
							goodsname=StringUtil.replace(goodsname, "<", "&lt;");
							goodsname=StringUtil.replace(goodsname, ">", "&gt;");
							goodsname=StringUtil.replace(goodsname, "&", "&amp;");
							itemnotes=StringUtil.replace(itemnotes, "<", "&lt;");
							itemnotes=StringUtil.replace(itemnotes, ">", "&gt;");
							itemnotes=StringUtil.replace(itemnotes, "&", "&amp;");
							//品牌名
							String brandName = htsku.get("brandName")!=null?htsku.get("brandName").toString():"";
							
							if (title.equals("")) title=goodsname;
							
							bizData.append("<item>");
							bizData.append("<itemSkuCode>"+custombc+"</itemSkuCode>");
			
							bizData.append("<itemName>"+title+"</itemName>");
							bizData.append("<itemQuantity>"+purqty+"</itemQuantity>");
							bizData.append("<itemUnitPrice>"+customprice+"</itemUnitPrice>");
							bizData.append("<itemNote>"+itemnotes+"</itemNote>");
							//品牌名
							if(isVip){
								bizData.append("<userDefined1>"+brandName+"</userDefined1>");
							}
							
							bizData.append("</item>");
						}										
						bizData.append("</items>");
						bizData.append("</"+serviceType+">");
						String bizData1 = BestUtil.filterChar(bizData.toString() );
						//Log.info("bizdata1: "+bizData1);
						//取得单号（主键id+店号)
						String msgId=BestUtil.makeMsgId(conn);
						//
						List signParams=BestUtil.makeSignParams(bizData1, serviceType,Params.msgtype,
								Params.partnerid,Params.partnerkey,Params.serviceversion,Params.callbackurl,msgId);
						//生成签名-MD5加密
						String sign=BestUtil.makeSign(signParams);
					
						//拼装请求参数
						Map requestParams=BestUtil.makeRequestParams(bizData1, serviceType, 
								msgId, Params.msgtype, sign,Params.callbackurl,
								Params.serviceversion,Params.partnerid);
						//发送post请求，并取得返回结果
						
						String result=CommHelper.sendRequest(Params.url, requestParams, "");
						//Log.info("result: "+result);

					
						try
						{
							result=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
						}catch(Exception e)
						{
							
							throw new JException(result.concat(e.getMessage()));
						}

						//返回结果还原成document
						Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
						Element productinforspele = productinforspdoc.getDocumentElement();
						
						String flag=DOMHelper.getSubElementVauleByName(productinforspele, "flag");
						//判断传单成功与否
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
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2209");
							conn.commit();
							conn.setAutoCommit(true);
							BestUtil.recordMsg(conn, msgId,outbuzcode,2209,serviceType);
							Log.info(jobname,"同步发货单成功,接口单号:"+sheetid);
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
