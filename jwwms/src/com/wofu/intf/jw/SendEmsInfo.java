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
	private static String jobname = "����������Ϣ��ems";
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
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
						Log.error(operData+"��ems����ʧ��,�Ҳ���������","  ת�뱸�ݱ����");
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
					//�ش������Ϣ��ems
					StringBuilder data = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					data.append("<NewDataSet>")
					.append("<EMS_DS_TMP>")
					.append("<EMS_CODE>").append(Params.emscode).append("</EMS_CODE>")
					.append("<BUSINESSTYPE>4</BUSINESSTYPE>")//EMSҵ������    1��� 4����
					.append("<ORIGINAL_ORDER_NO>").append(orderId).append("</ORIGINAL_ORDER_NO>")  //erp�Ķ�����
					.append("<BIZ_TYPE_CODE>I20</BIZ_TYPE_CODE>")          //
					.append("<BIZ_TYPE_NAME>��˰����</BIZ_TYPE_NAME>")
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
					if("0".equals(result)){//�������ݳɹ�
						conn.setAutoCommit(false);
						JwUtil.backUpIntsheetData(conn,serialID);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info("ͬ���������ݵ�ems�ɹ�,�������: "+operData);
					}else if(result.indexOf("���걨���ĺ���")==0){
						conn.setAutoCommit(false);
						JwUtil.backUpIntsheetData(conn,serialID);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info("EMS�����Ѿ���,�������: "+operData);
					}else{
						Log.info("�������ݵ�ems�����������: "+operData+",������Ϣ: "+result);
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
