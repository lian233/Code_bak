package com.wofu.ecommerce.uwuku;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;


import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;


public class OrderDelivery extends Thread {

	private static String jobname = "����ⶩ������������ҵ";
	private boolean is_exporting = false;

	private static String requesttype="order";
	private static String requestmethod="youwuku.logistics.send";
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);

				delivery(connection);	

			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
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

	private void delivery(Connection conn)  throws Exception
	{
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,c.name companyname,"
			+"upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock), deliveryref c with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0 and b.companycode=c.companycode ";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderid = hto.get("tid").toString();
			String companycode = hto.get("companycode").toString();
			String companyname = hto.get("companyname").toString();
			String post_no = hto.get("outsid").toString();		
			
			//���������˾Ϊ������Դ���
			if (companycode.trim().equals(""))
			{
				Log.warn(jobname, "��ݹ�˾Ϊ�գ�������:"+orderid+"");
				continue;
			}
		

			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("uid", Params.clientid) ;
			params.put("order_id", orderid) ;
			params.put("logistics_name", companyname) ;
			params.put("invoice_no", post_no) ;
			if (companycode.equalsIgnoreCase("EMS"))
				params.put("transport_type", "EMS") ;
			else
				params.put("transport_type", "EXPRESS");
			
			String sign=UwukuUtil.makeSign(Params.clientid, requesttype, 
					"","",Params.appsecret);
			
			params.put("sign", sign);
			
			params.put("method", requestmethod);
			params.put("request_type", requesttype);
			params.put("platform", Params.platform);
			params.put("sign_type", Params.signtype);
			params.put("format", Params.format);
			params.put("v", Params.version);
			
			String responseText = CommHelper.sendRequest(Params.url+requesttype,params);

			JSONObject jo = new JSONObject(responseText);

			
			int retcode=jo.optInt("code");
			
			if (retcode!=0)
			{
				Log.warn("��������ʧ��,������:["+orderid+"],��ݹ�˾:["+companycode+"],��ݵ���:["+post_no+"] ������Ϣ:"+jo.optString("error"));
				continue ;
			}
				
			
			try {
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				if (!conn.getAutoCommit())
					try {
						conn.rollback();
					} catch (Exception e1) {
					}
				try {
					conn.setAutoCommit(true);
				} catch (Exception e1) {
				}
				throw new JSQLException(sql, sqle);
			}
			Log.info(jobname,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ companycode + "��,��ݵ��š�" + post_no + "��");

		}
	}

	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
