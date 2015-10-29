package com.wofu.intf.chinapay;

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
public class ProcOrderSetTransportNo extends Thread {


	private static String jobname = "处理订单对应分运单号作业";		
	private static String messageType="ORDER_SET_TRANSPORT_NO";
	private static String sheetType = "880022";
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {
			Connection conn = null;
			try {										
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//取接口表
				List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
				//----生成报文
				//单据
				String sheetid=null;
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
 
					String sql = "select * from OutStockNote where SheetID = '"+sheetID+"'";
								
					Hashtable dt=SQLHelper.oneRowSelect(conn, sql);
					if (dt.size() <= 0){
						Log.info("出库单不存在：" + sheetID);
						continue;
					}
					
					//生成数据
					//表头
					StringBuilder bizSheet=new StringBuilder();

					bizSheet.insert(0, "<ORDER_SET_TRANSPORT_NO>");
					
					bizSheet.append(DtcTools.CreateItem("ESHOP_ENT_CODE" , Params.EshopEntCode , null));//电商企业代码
					bizSheet.append(DtcTools.CreateItem("ESHOP_ENT_NAME" , Params.EshopEntName , null));//电商企业名称
					bizSheet.append(DtcTools.CreateItem("ORIGINAL_ORDER_NO" , "CustomPurSheetID" , dt));//原始订单编号
					bizSheet.append(DtcTools.CreateItem("TRANSPORT_BILL_NO" , "DeliverySheetID" , dt));//运单号

					bizSheet.append("</ORDER_SET_TRANSPORT_NO>");
					
					DtcTools.createBody(bizSheet);
					bizSheet.insert(0, DtcTools.createHead(messageType,actionType));
					DtcTools.AddHeadRear(bizSheet);											
					
					Log.info("data : "+bizSheet.toString());

					/* test close*/
					String bizData1 = DtcUtil.filterChar(bizSheet.toString() );
					
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
					
					//num=0;
					//bizData.delete(bizData.indexOf("<products>")+10, bizData.length());
					//Log.info("删除后的数据为:　"+bizData.toString());
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
