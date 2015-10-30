package com.wofu.ecommerce.jumei;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;


public class OrderDelivery extends Thread {

	private static String jobname = "������Ʒ��������������ҵ";
	
	private boolean is_exporting = false;

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
		String method="Order/SetShipping";
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+"upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderid = hto.get("tid").toString();
			String post_company = hto.get("companycode").toString().toUpperCase();
			String post_no = hto.get("outsid").toString();		
	
			//���������˾Ϊ������Դ���
			if (post_company.trim().equals(""))
			{
				Log.warn(jobname, "��ݹ�˾Ϊ�գ�������:"+orderid+"");
				continue;
			}
			
			String postcompanyid=getCompanyID(post_company);
	
			if (postcompanyid.equals(""))
			{
				//���������˾Ϊ������Դ���
				if (post_company.trim().equals(""))
				{
					Log.warn(jobname, "��ݹ�˾δ���ã���ݹ�˾��"+post_company+" ������:"+orderid+"");
					continue;
				}
			}

			Map<String, String> paramMap = new HashMap<String, String>();
	        //ϵͳ����������
	        paramMap.put("client_id", Params.clientid);
	        paramMap.put("client_key", Params.clientkey);
	        paramMap.put("order_id", orderid);
	        paramMap.put("logistic_id", postcompanyid);	        
	        paramMap.put("logistic_track_no", post_no);
	        
	        String sign=JuMeiUtils.getSign(paramMap, Params.signkey, Params.encoding);
			   
	        paramMap.put("sign", sign);
	        
	        String responseData=CommHelper.sendRequest(Params.url+method, paramMap, "", Params.encoding);
			JSONObject responseresult=new JSONObject(responseData);
			
			
			int errorCount=responseresult.getInt("error");
			
			if (errorCount>0)
			{
				
				String errdesc=responseresult.getString("message");
				//����Ѿ�����-��ֱ�ӱ��ݵ�IT_UpNoteBak����  {"error":"2","message":"no exchange!"}�����Ѿ�����������״̬�����ģ�ֻ���Ŀ����Ϣ
				if(!"no exchange!".equals(errdesc)){
					Log.warn("��������ʧ��,������:["+orderid+"],��ݹ�˾:["+post_company+"],��ݵ���:["+post_no+"] ������Ϣ:"+errdesc);
					continue ;
				}
				
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
			Log.info(jobname,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");

		}
	}
	
	private void confirm(Connection conn,String orderId)  throws Exception
	{
		String apiMethod = "Order/SetOrderStock";
		HashMap<String,String> paramMap  = new HashMap();
		paramMap.put("client_id", Params.clientid);
        paramMap.put("client_key", Params.clientkey);
		paramMap.put("order_ids", orderId);
		
		String sign=JuMeiUtils.getSign(paramMap, Params.signkey, Params.encoding);
		   
        paramMap.put("sign", sign);
        String responseData=CommHelper.sendRequest(Params.url+apiMethod, paramMap, "", Params.encoding);
	}
	
	private String getCompanyID(String companycode) throws Exception
	{
		String companyid="";
		Object[] cys=StringUtil.split(Params.company, ";").toArray();
		for(int i=0;i<cys.length;i++)
		{
			String cy=(String) cys[i];
			
			Object[] cs=StringUtil.split(cy, ":").toArray();
			
			String ccode=(String) cs[0];
			String cid=(String) cs[1];
			
			if(ccode.equals(companycode))
			{
				companyid=cid;
				break;
			}
		}
		
		return companyid;
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
