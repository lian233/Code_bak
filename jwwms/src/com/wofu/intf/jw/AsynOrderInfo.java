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
public class AsynOrderInfo extends Thread {
	
	private static String jobname = "ͬ������������ҵ";
	private static String service="subAddSaleOrder";
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");

		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				List infsheetlist=JwUtil.getintfsheetlist(conn,900000,100);
				Order order=null;
				for(Iterator it = infsheetlist.iterator();it.hasNext();){
					try{
						Hashtable ht = (Hashtable)it.next();
						Integer serialID = (Integer)ht.get("SerialID");
						String operData = (String)ht.get("OperData");
						String sql ="SELECT * from Inf_downNotebak where operdata='"+operData+"' AND SheetType='900000' AND result='success' and opertype='100'";
						Vector duplicateNum  = SQLHelper.multiRowSelect(conn, sql);
						if(duplicateNum!=null && duplicateNum.size()>0){
							conn.setAutoCommit(false);
							JwUtil.delBackUpIntsheetData(conn,serialID);
							conn.commit();
							conn.setAutoCommit(true);
							Log.info("�˵��ѷ���,ɾ���ظ��ĵ��ɹ�,������: "+order.getOrderCode());
							continue;
						}
							sql ="select a.custompursheetid orderCode,a.custompursheetid orderDetailCode,a.tax "
							+"orderTax,convert(varchar,a.notifydate,20) createDate,convert(varchar,a.notifydate,20) "+
							"updateDate,convert(varchar,a.notifydate,20) payTime,a.postfee postPrice,a.CustomsOrderNo "+
							"invoiceName,a.note buyerMessage,a.payfee amountReceivable,a.payfee actualPayment,"+
							"a.linkman name, a.tele mobilePhone,a.address,isnull(a.zipcode,'000000') zip,b.notifyqty num,"+
							"b.title,b.price,  b.price*b.notifyqty payment,b.price*b.notifyqty totalPrice,"+
							"c.custombc skuID ,rtrim(c.outerSkuId) outerSkuId,a.customsbarcode hgBarcode "+
							"from outstock0 a with(nolock),outstockitem0 b with(nolock),barcode c with(nolock) where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid and a.sheetid='"+operData+"'";
						Vector sqlresult = SQLHelper.multiRowSelect(conn, sql);
						if(sqlresult.size()==0){
							conn.setAutoCommit(false);
							JwUtil.backUpIntsheetData(conn,serialID);
							conn.commit();
							conn.setAutoCommit(true);
							Log.info("ͬ������ʧ�ܣ������Ѿ�����򲻴���,������: "+order.getOrderCode());
							continue;
						}
						

						
						order = new Order();
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
									String[] add = address.split(" ");//���õ�ַ��ϸ
									if(add.length<3){
										System.out.println(operData+" ��ַ������Ҫ��");
									}
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
								mobilePhone = mobilePhone.replaceAll(" +", " ");
								if(mobilePhone.indexOf(" ")>0){
									String[] contacts = mobilePhone.split(" ");
										receiveInfo.setMobilephone(contacts[0]);
										if(contacts[1].length()==11)//������ֻ���������ã���������֤�Ͳ�������
										receiveInfo.setPhone(contacts[1]);
								}else if(mobilePhone.indexOf("/")>0){
									String[] contacts = mobilePhone.split("/");
									if(contacts.length==2){
										receiveInfo.setMobilephone(contacts[0]);
										if(contacts[1].length()==11)//������ֻ���������ã���������֤�Ͳ�������
										receiveInfo.setPhone(contacts[1]);
									}
								}else{
									receiveInfo.setMobilephone(mobilePhone);
								}
								}
							det = new detail();
							det.getMapData(htTemp);
							order.getDetail().getRelationData().add(det);
							i++;
						}
						//Log.info("address: "+receiveInfo.getAddress());
						order.setReceiver(receiveInfo.toJSONObject());
						String temp = order.toJSONObject().replaceAll("\"\\{", "\\{");
						temp = temp.replaceAll("\",\"detail\"", ",\"detail\"");
						//Log.info("temp: "+temp);
						String bizData1 = "{\"saleOrderList\":["+temp+"]}";
						String sign=JwUtil.makeSign(bizData1);
						Map requestParams=JwUtil.makeRequestParams(bizData1, service, 
								Params.appkey,Params.format, sign);
						String result=CommHelper.sendRequest(Params.url, requestParams, "");
						Log.info("result: "+result);
						JSONObject re = new JSONObject(result);
						if(re.getBoolean("isSuccess")){
							conn.setAutoCommit(false);
							JwUtil.backUpIntsheetData(conn,serialID);
							conn.commit();
							conn.setAutoCommit(true);
							Log.info("ͬ�������ɹ�,������: "+order.getOrderCode());
							
						}						
							else if(re.getString("body").contains("���Ͷ�������,�����Ѵ���")){
							conn.setAutoCommit(false);
							JwUtil.delBackUpIntsheetData(conn,serialID);
							conn.commit();
							conn.setAutoCommit(true);
							Log.info("ɾ���ظ��ĵ��ɹ�,������: "+order.getOrderCode());
						}

						else{
							Log.error(jobname, "sheetid: "+operData+" "+re.getString("body"));
						}
					}catch(Exception e){
						Log.error(jobname, e.getMessage());
						if(conn !=null && !conn.getAutoCommit()) conn.rollback();
						continue;
					}
					
				}
			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
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
