package com.wofu.intf.jw;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynCancelOrderInfo extends Thread {
	
	private static String jobname = "同步取消订单资料作业";
	private static String service="subAddSaleOrder";
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				List infsheetlist=JwUtil.getintfsheetlist(conn,900000,99);
				CancelOrder order=null;
				for(Iterator it = infsheetlist.iterator();it.hasNext();){
					try{
						Hashtable ht = (Hashtable)it.next();
						Integer serialID = (Integer)ht.get("SerialID");
						String operData = (String)ht.get("OperData");
						String sql ="select a.custompursheetid orderCode,a.custompursheetid orderDetailCode,a.tax "
							+"orderTax,convert(varchar,a.notifydate,20) createDate,convert(varchar,a.notifydate,20) "+
							"updateDate,convert(varchar,a.notifydate,20) payTime,a.postfee postPrice,isnull(a.invoiceTitle,'') "+
							"invoiceName,a.note buyerMessage,a.payfee amountReceivable,a.payfee actualPayment,"+
							"a.linkman name, a.tele mobilePhone,a.address,a.zipcode zip,b.notifyqty num,"+
							"b.title,b.price,  b.price*b.notifyqty payment,b.price*b.notifyqty totalPrice,"+
							"c.custombc skuID ,rtrim(c.outerSkuId) outerSkuId,a.customsbarcode hgBarcode "+
							"from outstock a with(nolock),outstockitem b with(nolock),barcode c with (nolock) where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid and a.sheetid='"+operData+"'";
						Vector sqlresult = SQLHelper.multiRowSelect(conn, sql);
						if(sqlresult.size()==0)
							sql ="select a.custompursheetid orderCode,a.custompursheetid orderDetailCode,a.tax "
								+"orderTax,convert(varchar,a.notifydate,20) createDate,convert(varchar,a.notifydate,20) "+
								"updateDate,convert(varchar,a.notifydate,20) payTime,a.postfee postPrice,isnull(a.invoiceTitle,'') "+
								"invoiceName,a.note buyerMessage,a.payfee amountReceivable,a.payfee actualPayment,"+
								"a.linkman name, a.tele mobilePhone,a.address,a.zipcode zip,b.notifyqty num,"+
								"b.title,b.price,  b.price*b.notifyqty payment,b.price*b.notifyqty totalPrice,"+
								"c.custombc skuID ,rtrim(c.outerSkuId) outerSkuId,a.customsbarcode hgBarcode "+
								"from outstock0 a with(nolock),outstockitem0 b with(nolock),barcode c with (nolock) where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid and a.sheetid='"+operData+"'";
						sqlresult = SQLHelper.multiRowSelect(conn, sql);
						if(sqlresult.isEmpty()) continue;
						order = new CancelOrder();
						ReceiveInfo receiveInfo = new ReceiveInfo();
						int i=0;
						detail det=null;
						for(Iterator t=sqlresult.iterator();t.hasNext();)
						{	
							Hashtable htTemp = (Hashtable)t.next();
							if(i==0){
								order.getMapData(htTemp);
								receiveInfo.getMapData(htTemp);
								String address = receiveInfo.getAddress();
								if(address.indexOf(" ")>0){
									String[] add = address.split(" ");//设置地址明细
									if(add.length==4){
										receiveInfo.setProvince(add[0]);
										receiveInfo.setCity(add[1]);
										receiveInfo.setDistrict(add[2]);
										receiveInfo.setAddress(add[3]);
										
									}else if(add.length==5){
										receiveInfo.setProvince(add[0]);
										receiveInfo.setCity(add[1]);
										receiveInfo.setDistrict(add[2]);
										receiveInfo.setAddress(add[4].trim());
									}else {
										receiveInfo.setProvince(add[0]);
										receiveInfo.setCity(add[1]);
										receiveInfo.setDistrict(add[2]);
									}
								}
								String mobilePhone = receiveInfo.getMobilePhone();
								if(mobilePhone.indexOf(" ")>0){
									String[] contacts = mobilePhone.split(" ");
									if(contacts.length==2){
										receiveInfo.setMobilephone(contacts[0]);
										receiveInfo.setPhone(contacts[1]);
									}
								}else if(mobilePhone.indexOf("/")>0){
									String[] contacts = mobilePhone.split("/");
									if(contacts.length==2){
										receiveInfo.setMobilephone(contacts[0]);
										receiveInfo.setPhone(contacts[1]);
									}
								}
								}
							det = new detail();
							det.getMapData(htTemp);
							order.getDetail().getRelationData().add(det);
							i++;
						}
						order.setReceiver(receiveInfo.toJSONObject());
						String temp = order.toJSONObject().replaceAll("\"\\{", "\\{");
						temp = temp.replaceAll("\",\"detail\"", ",\"detail\"");
						//Log.info("temp: "+temp);
						String bizData1 = "{\"saleOrderList\":["+temp+"]}";
						String sign=JwUtil.makeSign(bizData1);
						Map requestParams=JwUtil.makeRequestParams(bizData1, service, 
								Params.appkey,Params.format, sign);
						String result=CommHelper.sendRequest(Params.url, requestParams, "");
						//Log.info("result: "+result);
						// {"isSuccess":false,"body":"推送订单错误:当前没有推送订单信息!"}
						JSONObject re = new JSONObject(result);
						if(re.getBoolean("isSuccess")){
							conn.setAutoCommit(false);
							JwUtil.backUpIntsheetData(conn,serialID);
							conn.commit();
							Log.info("同步取消订单成功,订单号: "+order.getOrderCode());
							
						}else{
							if("推送订单错误:当前没有推送订单信息!".equals(re.getString("body"))){
								conn.setAutoCommit(false);
								JwUtil.backUpIntsheetData(conn,serialID);
								conn.commit();
								Log.info("同步取消订单失败,wms没有这个订单记录,订单号: "+order.getOrderCode());
							}else
							Log.error(jobname, re.getString("body"));
						}
					}catch(Exception e){
						Log.error(jobname, e.getMessage());
						if(conn!=null && !conn.getAutoCommit()) conn.rollback();
						continue;
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
