package com.wofu.intf.yjn;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class ProcessOrderInfo extends Thread {

	private static String jobname = "处理易江南订单作业";		
	private static String messageType="ORDER_INFO";
	private static String sheetType = "880020";
	private static DecimalFormat sf= new DecimalFormat("0.00");  //保留二位小数，四舍五入
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {
			Connection conn = null;
			try {										
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//取接口表
				List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
				//单据
				for(Iterator it=infsheetlist.iterator();it.hasNext();){
					//
					Hashtable t = (Hashtable)it.next();
					Integer SerialID = (Integer)t.get("SerialID");
					//String CustomsNo = (String)t.get("Owner");
					String sheetID = t.get("OperData").toString();
					String actionType="I";//1、新增  2、退货 3、取消
					if (t.get("OperType").toString().equals("99")){//取消
						actionType = "3";
					}
					else if (t.get("OperType").toString().equals("101")){//变更
						actionType = "2";
					}
					else{
						actionType = "I";
					}
					
					String sql = "select * from OutStockNote where SheetID = '"+sheetID+"'";
								
					Hashtable dtOut=SQLHelper.oneRowSelect(conn, sql);
					if (dtOut.size() <= 0){
						Log.info("出库单不存在：" + sheetID);
						continue;
					}
						
					
					//明细
					StringBuilder bizItem = new StringBuilder();

					sql = "select CustomBC, CustomsListNO, NotifyQty, a.Price, Weigh/1000 Weigh, NetWeigh/1000 NetWeigh, a.Notes, "
						+ " b.ColorID , b.SizeID, s.Name SizeName , c.Name ColorName , UnitName, a.Price * NotifyQty TotalPrice, "
						//+ " IsNull(C.Name, '') + ' ' + IsNull(S.Name, '') Spec , PostTaxNo, Country" 
						+ " IsNull(B.HSCode, '') HSCode, m.Spec , PostTaxNo, Country"
					  + " from OutStockNoteItem a , Barcode b , Merchandise m , Size s , Color c "
					  + " where a.BarcodeID = b.BarcodeID and a.MID= b.MID and b.MID = m.MID and a.MID = m.MID "
					  + "  and M.MeasureType = s.MeasureTypeID and b.SizeID *= s.SizeID and b.ColorID *= c.ColorID "
					  + " and SheetID = '" + sheetID + "' ";
					
					Vector vtItem = SQLHelper.multiRowSelect(conn, sql);
					BigDecimal totalTaxFee = new BigDecimal(0) ;
					String taxFee = "";
					String strCountryCode = "";
					for (int j=0; j<vtItem.size(); j++) {
						Hashtable htItem=(Hashtable) vtItem.get(j);
						
						bizItem.append("<ORDER_DETAIL>");
						bizItem.append(DtcTools.CreateItem("SKU" , "CustomBC" , htItem));//商品货号 SKU
						if ("".equals(strCountryCode)){
							strCountryCode = htItem.get("Country").toString();
						}
						//bizItem.append(DtcTools.CreateItem("GOODS_SPEC" , spec, null));//规格型号
						bizItem.append(DtcTools.CreateItem("GOODS_SPEC" , "Spec" , htItem));//规格型号
						bizItem.append(DtcTools.CreateItem("CURRENCY_CODE" , "142" , null));//CURRENCY_CODE 142 人民币
						bizItem.append(DtcTools.CreateItem("PRICE" , "Price" , htItem));//商品单价
						bizItem.append(DtcTools.CreateItem("QTY" , "NotifyQty" , htItem));//商品数量
						bizItem.append(DtcTools.CreateItem("GOODS_FEE" , "TotalPrice" , htItem));//商品总价
						taxFee = sf.format(Double.parseDouble(DtcTools.CalTax(conn ,htItem.get("PostTaxNo").toString() , 
								htItem.get("TotalPrice").toString() )));
						bizItem.append(DtcTools.CreateItem("TAX_FEE" , 	 taxFee, null));//税款金额
						
						bizItem.append("</ORDER_DETAIL>");
						totalTaxFee = totalTaxFee.add(new BigDecimal(Double.parseDouble(taxFee)));
					}
					if (bizItem.length() <= 0){//没有明细
						Log.info("找不到明细：" + sql);
						continue;
					}
					//生成数据
					//表头
					StringBuilder bizSheet = new StringBuilder();
					bizSheet.append(DtcTools.CreateItem("CUSTOMS_CODE", Params.CustomsCode , null));//申报海关代码
					bizSheet.append(DtcTools.CreateItem("BIZ_TYPE_CODE", Params.biz_type_code, null));//业务类型  直购进口：I10,网购保税进口：I20
					bizSheet.append(DtcTools.CreateItem("ORIGINAL_ORDER_NO" , "CustomPurSheetID" , dtOut));//原始订单编号
					bizSheet.append(DtcTools.CreateItem("ESHOP_ENT_CODE" , Params.EshopEntCode , null));//电商企业代码
					bizSheet.append(DtcTools.CreateItem("ESHOP_ENT_NAME" , Params.EshopEntName , null));//电商企业名称
					bizSheet.append(DtcTools.CreateItem("DESP_ARRI_COUNTRY_CODE" , strCountryCode , null));//起运国  116	日本
					bizSheet.append(DtcTools.CreateItem("SHIP_TOOL_CODE" , "Y" , null));//运输方式
					bizSheet.append(DtcTools.CreateItem("CHECK_TYPE" , "R" , null));//验证类型R:收货人 P:支付人
					bizSheet.append(DtcTools.CreateItem("RECEIVER_ID_NO" , "CertNo" , dtOut));//收货人身份证号码
					bizSheet.append(DtcTools.CreateItem("RECEIVER_NAME" , "CertName" , dtOut));//收货人姓名
					bizSheet.append(DtcTools.CreateItem("RECEIVER_ADDRESS" , "Address" , dtOut));//收货人地址
					bizSheet.append(DtcTools.CreateItem("RECEIVER_TEL" , "Tele" , dtOut));//收货人电话
					//Double fee = DtcTools.GetOrderTotalAmount(conn , sheetID);					
					bizSheet.append(DtcTools.CreateItem("GOODS_FEE" , DtcTools.GetOrderTotalAmount(conn , sheetID) , null));	//货款总额
					//bizSheet.append(DtcTools.CreateItem("TAX_FEE" , "Tax" , dtOut));//税金总额
					Log.info("totalTaxFee: "+totalTaxFee);
					if(totalTaxFee.compareTo(new BigDecimal(50))>0)
						bizSheet.append(DtcTools.CreateItem("TAX_FEE" , sf.format(totalTaxFee.doubleValue()), null));//税金总额
					else{
						bizSheet.append(DtcTools.CreateItem("TAX_FEE", "0.0000" , null));//税金总额
					}

					bizSheet.append(DtcTools.CreateItem("GROSS_WEIGHT" , DtcTools.GetWeigh(conn , sheetID , 2), null));//毛重
					
					//bizData.append("<PROXY_ENT_CODE />");
					//bizData.append("<PROXY_ENT_NAME />");
					bizSheet.append(DtcTools.CreateItem("SORTLINE_ID" , Params.SortLineID , null));//分拣线ID SORTLINE01：代表寸滩空港  SORTLINE02：代表重庆西永 SORTLINE03：代表寸滩水港"
					//整合
					bizSheet.insert(0, "<ORDER_HEAD>");
					bizSheet.append(bizItem.toString());
					bizSheet.append("</ORDER_HEAD>");
					
					DtcTools.createBody(bizSheet);
					bizSheet.insert(0, DtcTools.createHead(messageType,actionType));
					DtcTools.AddHeadRear(bizSheet);											
					
					Log.info("data : "+bizSheet.toString());
					
					
					/* test close*/
					String bizData1 = DtcUtil.filterChar(bizSheet.toString() );
					
					Map requestParams=DtcUtil.makeRequestParams(bizData1);
//					conn.setAutoCommit(false);
//					String result=CommHelper.sendRequest(Params.url, requestParams, "");
//					Log.info("result:　"+result);
//					
//					if (result.equalsIgnoreCase("false")) //失败
//					{	
//						Log.error(jobname, jobname + "失败,接口单号:"+SerialID+",错误信息："+result);
//					}else   //同步成功，备份接口数据，写入ecs_bestlogisticsmsg表
//					{	
//						Log.error(jobname, jobname + "成功,接口单号:"+SerialID);
//					}
//					
//					DtcTools.backUpInf(conn,SerialID,result);
					
				}					
				conn.commit();
				conn.setAutoCommit(true);
								
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
