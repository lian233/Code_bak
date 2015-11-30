package com.wofu.intf.yjn;

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
import com.wofu.common.tools.util.log.Log;

public class ProcPaymentInfo extends Thread {

	private static String jobname = "处理支付单作业";
		
	private static String messageType="PAYMENT_INFO";
	private static String sheetType = "880021";
	private static DecimalFormat sf= new DecimalFormat("0.00");  //保留二位小数，四舍五入
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {
			Connection conn = null;
			try {		
				
					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//取接口表
				List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
				//----生成报文
				//报文头				
				//bizData.append(DtcTools.createHead(messageType));
				
				//商品
				for(Iterator it=infsheetlist.iterator();it.hasNext();){
					//
					Hashtable t = (Hashtable)it.next();
					Integer SerialID = (Integer)t.get("SerialID");
					String sheetID= t.get("OperData").toString();
					
					String actionType="1";//1、新增  2、退货 3、取消
					if (t.get("OperType").toString().equals("99")){//取消
						actionType = "3";
					}
					else if (t.get("OperType").toString().equals("101")){//变更
						actionType = "2";
					}
					else{
						actionType = "1";
					}
					String sql = "select count(*) from inf_downnotebak where sheettype=880020 and operdata='"+sheetID+"'";
					if(SQLHelper.intSelect(conn, sql)==0){
						Log.info("单据编号: "+sheetID+",订单信息还没有发送,稍后再处理这个支付单!");
						continue;
					}
					   sql ="select sum(A.price*A.notifyqty*p.taxrate/100.00) from "
						+ " outstocknoteitem a,Merchandise m ,PostTariff p "
						+ " where a.MID = m.MID and m.PostTaxNo = p.code and a.sheetID='"+sheetID+"'";
					String totalPrice = SQLHelper.strSelect(conn, sql);
					if (totalPrice == "") {
						totalPrice = "0";
					}
					//Log.info("totalPrice: "+totalPrice);
					if (Double.parseDouble(totalPrice) <= 50) {
						totalPrice = "0";
					}					
					//Log.info("totalPrice: "+totalPrice);

					sql = "select * from OutStockNote where sheetID = '"+ sheetID+"'";
					
					Vector vt=SQLHelper.multiRowSelect(conn, sql);
					for (int i=0;i<vt.size();i++){	
						
						StringBuilder bizData=new StringBuilder();
						
						Hashtable ht=(Hashtable) vt.get(i);
						bizData.append("<"+messageType+">");
						bizData.append(DtcTools.CreateItem("CUSTOMS_CODE" , Params.customercode , null));//申报海关代码
						bizData.append(DtcTools.CreateItem("BIZ_TYPE_CODE" , Params.biz_type_code , null));//业务类型
						bizData.append(DtcTools.CreateItem("ESHOP_ENT_CODE" , Params.EshopEntCode , null));//电商企业代码
						bizData.append(DtcTools.CreateItem("ESHOP_ENT_NAME" , Params.EshopEntName , null));//电商企业名称
						bizData.append(DtcTools.CreateItem("PAYMENT_ENT_CODE" , Params.PaymentEntCode , null));//支付企业代码
						bizData.append(DtcTools.CreateItem("PAYMENT_ENT_NAME" , Params.PaymentEntName , null));//支付企业名称
						//支付交易编号
						String strPayNo = ht.get("PayNo").toString();
						if ("".equals(strPayNo)){
							strPayNo = ht.get("SheetID").toString();
						}
						bizData.append(DtcTools.CreateItem("PAYMENT_NO" , strPayNo, null));
						
						bizData.append(DtcTools.CreateItem("ORIGINAL_ORDER_NO" , "CustomPurSheetID" , ht));//原始订单编号
						bizData.append(DtcTools.CreateItem("PAYMENT_ID_NO" , "CertNo" , ht));//支付人身份证号	
						bizData.append(DtcTools.CreateItem("PAYMENT_NAME" , "LinkMan" , ht));//支付人名称
						bizData.append(DtcTools.CreateItem("PAYMENT_TEL" , "Tele" , ht));//支付人电话
						String fee = DtcTools.GetOrderTotalAmount(conn , sheetID);
						Double totalFee = Double.parseDouble(ht.get("PostFee").toString().equals("") ? "0" : ht.get("PostFee").toString())
						+ Double.parseDouble(fee == "" ? "0" : fee);

						bizData.append(DtcTools.CreateItem("PAY_AMOUNT" , sf.format(totalFee) , null));

						bizData.append(DtcTools.CreateItem("GOODS_FEE" , fee , null));
						String taxFee = sf.format(Double.parseDouble(totalPrice));
						bizData.append(DtcTools.CreateItem("TAX_FEE" , taxFee, null));
						bizData.append(DtcTools.CreateItem("CURRENCY_CODE" , "142" , null));
						
						bizData.append("<MEMO />");
						bizData.append("</"+messageType+">");
						
						DtcTools.createBody(bizData);
						bizData.insert(0, DtcTools.createHead(messageType , actionType));
						DtcTools.AddHeadRear(bizData);
						Log.info("data: "+bizData.toString());
						
						String bizData1 = DtcUtil.filterChar(bizData.toString() );
						Map requestParams=DtcUtil.makeRequestParams(bizData1);
						conn.setAutoCommit(false);
						String result=CommHelper.sendRequest(Params.url, requestParams, "");
						Log.info("result:　"+result);
						
						if (result.equalsIgnoreCase("false")) //失败
						{
							
							DtcTools.backUpInf(conn,SerialID,result);
							Log.error(jobname, jobname + "失败,接口单号:"+SerialID+",错误信息："+result);
							
						}else   //同步成功，备份接口数据，写入ecs_bestlogisticsmsg表
						{
							DtcTools.backUpInf(conn,SerialID,result);
							Log.error(jobname, jobname + "成功,接口单号:"+SerialID);
						}
						
						conn.commit();
						conn.setAutoCommit(true);												
					}
					
					/*
					if (bizData.length() > 0){
						DtcTools.createBody(bizData);
						bizData.insert(0, DtcTools.createHead(messageType , actionType));
						DtcTools.AddHeadRear(bizData);						
					}
					Log.info("data: "+bizData.toString());
					*/
										
					
					//num=0;
					//bizData.delete(bizData.indexOf("<products>")+10, bizData.length());
					//Log.info("删除后的数据为:　"+bizData.toString());
				}					
								
			} catch (Exception e) {
				e.printStackTrace();
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
