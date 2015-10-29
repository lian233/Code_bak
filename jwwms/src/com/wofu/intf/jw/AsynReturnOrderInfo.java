package com.wofu.intf.jw;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynReturnOrderInfo extends Thread {
	
	private static String jobname = "同步退货订单资料作业";
	private static String service="subAddSaleReturnOrder";
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				String sql ="select a.custompursheetid orderCode,a.custompursheetid orderDetailCode,convert(varchar,a.notifydate,20) createDate,convert(varchar,a.notifydate,20) updateDate,convert(varchar,a.notifydate,20) payTime,a.postfee postPrice,isnull(a.invoiceTitle,'') invoiceName,a.note buyerMessage,a.payfee amountReceivable,a.payfee actualPayment,a.linkman name, a.tele mobilePhone,a.address,a.zipcode zip,b.notifyqty num,b.title,b.price,  b.price*b.notifyqty payment,b.price*b.notifyqty totalPrice,c.custombc skuID ,rtrim(c.outerSkuId) outerSkuId from outstock0 a,outstockitem0 b,barcode c where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid and a.sheetid='01ISOX1412100001'";
				Vector sqlresult = SQLHelper.multiRowSelect(conn, sql);
				Order order = new Order();
				ReceiveInfo receiveInfo = new ReceiveInfo();
				int i=0;
				for(Iterator it=sqlresult.iterator();it.hasNext();)
				{	
					Hashtable ht = (Hashtable)it.next();
					if(i==0){
						order.getMapData(ht);
						receiveInfo.getMapData(ht);
					}
					detail det = new detail();
					det.getMapData(ht);
					order.getDetail().getRelationData().add(det);
					i++;
				}
				order.setReceiver(receiveInfo.toJSONObject());
				//Log.info("order: "+order.toJSONObject());
				String temp = order.toJSONObject().replaceAll("\"\\{", "\\{");
				temp = temp.replaceAll("\",\"detail\"", ",\"detail\"");
				Log.info("temp: "+temp);
				String bizData1 = "{\"saleOrderList\":["+temp+"]}";
				String sign=JwUtil.makeSign(bizData1);
				Map requestParams=JwUtil.makeRequestParams(bizData1, service, 
						Params.appkey,Params.format, sign);
				String result=CommHelper.sendRequest(Params.url, requestParams, "");
				Log.info("result: "+result);
				JSONObject re = new JSONObject(result);
				if(re.getBoolean("isSuccess")){
					Log.info("同步订单成功,订单号: ");
				}else{
					Log.error(jobname, re.getString("body"));
				}
				/**
				conn.commit();
				conn.setAutoCommit(true);
				**/
				
				
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
