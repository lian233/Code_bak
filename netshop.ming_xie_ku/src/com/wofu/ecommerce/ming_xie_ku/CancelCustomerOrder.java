package com.wofu.ecommerce.ming_xie_ku;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;

public class CancelCustomerOrder extends Thread
{
	private static String jobname = "ϵͳ�Ѿ�ȡ���Ķ���������Ь������";
	
	private boolean is_gening=false;
	private String method ="scn.vendor.order.invalid";

	@Override
	public void run() 
	{
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do{
			Connection connection = null;
			is_gening = true;
			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.ming_xie_ku.Params.dbname);	
				String sql ="select sheetid,sender from it_upnote where SheetType=99 and receiver='"+Params.tradecontactid+"'";
				Vector vts = SQLHelper.multiRowSelect(connection, sql);
				for (int i=0;i<vts.size();i++) {
					Hashtable hts=(Hashtable) vts.get(i);
					String sheetid=hts.get("sheetid").toString();
					String tid = hts.get("sender").toString().trim();
					Log.info("������: "+tid);
					JSONObject obj = new JSONObject();
					obj.put("VendorOrderNo", tid);
					Date now = new Date();
					String sign=Utils.get_sign(Params.app_Secret,Params.app_key,obj, method,now,Params.ver,Params.format);
					String output_to_server=Utils.post_data_process(method, obj, Params.app_key,now, sign).toString();
					String responseOrderListData=Utils.sendByPost(Params.url, output_to_server);
					Log.info("response: "+responseOrderListData);
					JSONObject result = new JSONObject(responseOrderListData);
					if(!result.getBoolean("IsError")){//�������ϳɹ�
						connection.setAutoCommit(false);
						sql="insert into it_upnotebak select * from it_upnote where sheetid='"+sheetid+"'";
						SQLHelper.executeSQL(connection, sql);
						sql ="delete it_upnote where sheetid='"+sheetid+"'";
						SQLHelper.executeSQL(connection, sql);
						connection.commit();
						connection.setAutoCommit(true);
						Log.info("�������ϳɹ�: ������"+tid);
					}else{
						Log.info("��������ʧ��: ������"+tid+" ʧ��ԭ��: "+result.getString("ErrMsg"));
					}
					
				}
			} catch (Throwable e) {
				e.printStackTrace();
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			}finally {
				is_gening = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (com.wofu.ecommerce.ming_xie_ku.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		}while(true);
//		super.run();
	}
	
	@Override
	public String toString()
	{
		return jobname + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
