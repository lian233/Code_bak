package com.wofu.intf.jw;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.tempuri.Cqems_electronic_business_all;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class SendEmsInfo extends Thread {
	private static String jobname = "发送物流信息到ems";
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {	
			Connection conn = null;
			try {		
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				List infsheetlist=JwUtil.getintfsheetlist(conn,900002,100);
				for(Iterator it = infsheetlist.iterator();it.hasNext();){
					Hashtable ht = (Hashtable)it.next();
					Integer serialID = (Integer)ht.get("SerialID");
					String operData = (String)ht.get("OperData");
					String sql ="select custompursheetid,certname,certno,address,tele,deliverysheetid from outstock0 where sheetid='"+operData+"'";
					Hashtable re = SQLHelper.oneRowSelect(conn, sql);
					if(re.size()==0){
						conn.setAutoCommit(false);
						JwUtil.backUpIntsheetData(conn,serialID);
						conn.commit();
						conn.setAutoCommit(true);
						Log.error(operData+"向ems发送失败,找不到订单号","  转入备份表完成");
						continue;
					}
					String orderId =re.get("custompursheetid").toString();
					String certname =re.get("certname").toString();
					String certno =re.get("certno").toString();
					String address =re.get("address").toString();
					String deliverysheetid =re.get("deliverysheetid").toString();
					String tele =re.get("tele").toString();
					sql ="select cast(sum(notifyqty) as int) from outstockitem0 where sheetid='"+operData+"'";
					int qty = SQLHelper.intSelect(conn, sql);
					//回传快递信息到ems
					StringBuilder data = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					data.append("<NewDataSet>")
					.append("<EMS_DS_TMP>")
					.append("<EMS_CODE>").append(Params.emscode).append("</EMS_CODE>")
					.append("<BUSINESSTYPE>4</BUSINESSTYPE>")//EMS业务类型    1标快 4经快
					.append("<ORIGINAL_ORDER_NO>").append(orderId).append("</ORIGINAL_ORDER_NO>")  //erp的订单号
					.append("<BIZ_TYPE_CODE>I20</BIZ_TYPE_CODE>")          //
					.append("<BIZ_TYPE_NAME>保税进口</BIZ_TYPE_NAME>")
					.append("<ESHOP_ENT_CODE>").append(Params.EshopEntCode).append("</ESHOP_ENT_CODE>")
					.append("<ESHOP_ENT_NAME>").append(Params.EshopEntName).append("</ESHOP_ENT_NAME>")
					.append("<QTY>").append(qty).append("</QTY>")
					.append("<RECEIVER_ID_NO>").append(certno).append("</RECEIVER_ID_NO>")
					.append("<RECEIVER_NAME>").append(certname).append("</RECEIVER_NAME>")
					.append("<RECEIVER_ADDRESS>").append(address).append("</RECEIVER_ADDRESS>")
					.append("<RECEIVER_TEL>").append(tele).append("</RECEIVER_TEL>")
					.append("<TRANSPORT_BILL_NO>").append(deliverysheetid).append("</TRANSPORT_BILL_NO>")
					.append("</EMS_DS_TMP>")
					.append("</NewDataSet>");
					String result=Cqems_electronic_business_all.cqems_electronic_business_all(data.toString(), Params.emscode);
					Log.info("resutl: "+result);
					if("0".equals(result)){//发送数据成功
						conn.setAutoCommit(false);
						JwUtil.backUpIntsheetData(conn,serialID);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info("同步订单数据到ems成功,订单编号: "+operData);
					}else if(result.indexOf("已申报过的号码")==0){
						conn.setAutoCommit(false);
						JwUtil.backUpIntsheetData(conn,serialID);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info("EMS订单已经审报,订单编号: "+operData);
					}else{
						Log.info("发送数据到ems出错，订单编号: "+operData+",错误信息: "+result);
					}
				}
			} catch (Exception e) {
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
