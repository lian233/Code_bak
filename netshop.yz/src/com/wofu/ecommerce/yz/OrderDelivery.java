package com.wofu.ecommerce.yz;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.yz.utils.Utils;
public class OrderDelivery extends Thread {

	private static String jobname = "���޶�������������ҵ";
	
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);

				delivery(connection);	
				
				//modifiRemark(connection);

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
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+"upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("����Ҫ����Ķ�����������Ϊ: "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderid = hto.get("tid").toString();
			String post_company = hto.get("companycode").toString();
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
	
			Map<String, String> params = new HashMap<String, String>();
	        //ϵͳ����������
			params.put("app_id", Params.app_id);
			params.put("format", Params.format);
			params.put("method", "kdt.logistics.online.confirm");
			params.put("sign_method", "MD5");
			params.put("v", Params.ver);
			params.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			params.put("tid", orderid);
			params.put("is_no_express", "0");
			params.put("out_stype",postcompanyid);
			params.put("out_sid", post_no);
		
			String responseOrderListData = Utils.sendByPost(params, Params.AppSecret, Params.url);
			Log.info("result:��"+responseOrderListData);
			//{"status":{"code":10006,"msg":"\u5fc5\u8981\u53c2\u6570\u7f3a\u5931"},"result":null}
			JSONObject responseDelivery=new JSONObject(responseOrderListData);
					
			if (!responseDelivery.isNull("error_response"))
			{
					String errdesc=responseDelivery.getJSONObject("error_response").getString("msg");
					if("\u65e0\u6548\u7684\u8ba2\u5355\u53f7".equals(errdesc)){
						Log.info(jobname+" �����Ų�����: "+orderid);
						
					}else{
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
	
	
	
	//�޸Ķ�����ע
	private void modifiRemark(Connection conn) throws Exception{
		String sql = "select sheetid,sender from it_upnote where sheettype=5 and flag=0 and receiver='"+Params.tradecontactid+"'";
		Vector modifiReList = SQLHelper.multiRowSelect(conn, sql);
		if(modifiReList.size()==0) return;
		for(int i=0;i<modifiReList.size();i++){
			try{
				Hashtable item = (Hashtable)modifiReList.get(i);
				String sheetid = item.get("sheetid").toString();
				String sender = item.get("sender").toString();
				String[] remarks = sender.split(":");
				//������
				String tid = remarks[0];
				//��ע
				String remark = remarks[1];
				Map<String, String> params = new HashMap<String, String>();
		        //ϵͳ����������
				params.put("appKey", Params.app_id);
				params.put("sessionKey", Params.token);
				params.put("format", Params.format);
				params.put("method", "yhd.order.merchant.remark.update");
				params.put("ver", Params.ver);
				params.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				params.put("orderCode", tid);
				params.put("remark",remark);
		        
				String response = Utils.sendByPost(params, Params.AppSecret, Params.url);
				//Log.info("�޸ı�ע:��"+response);
				
				JSONObject responseCount=new JSONObject(response);
				if(0==(responseCount.getJSONObject("response").getInt("errorCount"))){
					try{
						conn.setAutoCommit(false);
						sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 5";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=5";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
					Log.info(jobname,"��������" + tid + "�� sheetid��"+sheetid+"�� �޸ı�ע�ɹ�");	
					}catch(Exception ex){
						if(!conn.getAutoCommit()){
							try{
								conn.rollback();
							}catch(Exception el){}
							
						}
						try{
							conn.setAutoCommit(true);
						}catch(Exception es){}
						throw new Exception ("����ع�ʧ�ܣ�");
					}
				}else{
					Log.info(jobname,"��������" + tid + "�� sheetid��"+sheetid+"�� �޸ı�עʧ��");
				}
			}catch(Exception ex){
				Log.error(jobname, ex.getMessage());
			}
			
		}

		Log.info(jobname+",�޸Ķ�����ע���");
		
	}
	
	private String getRemark(String orderId) throws Exception{
		String result="";
		
		return result;
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
