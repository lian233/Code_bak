package com.wofu.ecommerce.ming_xie_ku;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;
public class OrderDelivery extends Thread{
	private static String jobname = "��Ь�ⶩ������������ҵ";
	
	private boolean is_exporting = false;

	@Override
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				delivery(connection);	
				
//				modifiRemark(connection);

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
			while (System.currentTimeMillis() - startwaittime < (Params.waittime * 1000))
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
			String orderid = hto.get("tid").toString(); //������   �����ƶ����ⲿ������
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
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
					continue;
				}
			}
			if("".equals(post_no)){
				Log.warn(jobname, "��ݵ���δ���ã���ݹ�˾��"+post_company+" ������:"+orderid+"");
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
				continue;
			}
			if("".equals(postcompanyid)){
				Log.warn(jobname, "��ݹ�˾���δ���ã���ݹ�˾��"+post_company+" ������:"+orderid+"");
				continue;
			}
	        Date now=new Date();
			String method="scn.vendor.order.delivery";
			/**�����Ϣ**/
			JSONArray kuai_di=new JSONArray();
			JSONObject OrdExpress=new JSONObject();
			//OrdExpress.put("ExpressCompanyId",new String(post_company.getBytes("ISO-8859-1"),"utf-8"));
			OrdExpress.put("ExpressCompanyId",postcompanyid);
			OrdExpress.put("ExpressNo", post_no);
			kuai_di.put(OrdExpress);
			/***data����***/
			JSONObject data=new JSONObject();
			//��Ҫ���ص��ֶΣ�
			data.put("ShippingFee", 0);   //���ͷ���
			data.put("VendorOrderNo", orderid);   //�����̶�����
//			data.put("VendorMemo", "");    //�����̱�ע
			data.put("OrdExpress", kuai_di);    //�����ϸ
			/**sign����***/
			String sign=Utils.get_sign(Params.app_Secret,Params.app_key,data, method, now,Params.ver,Params.format);
			/***�ϲ�Ϊ������****/
			String output_to_server=Utils.post_data_process(method, data, Params.app_key,now, sign).toString();
			//System.out.println(output_to_server);
			String responseOrderListData = Utils.sendByPost(Params.url, output_to_server);
			Log.info("responseOrderListData: "+responseOrderListData);
			JSONObject responseproduct=new JSONObject(responseOrderListData);
					
			//int errorCount=responseproduct.getJSONObject("response").getInt("errorCount");  /*��Щ�ж����һ��Ҫ�ο�GetOrders�����޸ģ�������Ϊû�б���������*/
			
			if (responseproduct.getBoolean("IsError"))
			{
				String errdesc="";
				errdesc=errdesc+" "+responseproduct.get("ErrCode").toString()+" "+responseproduct.get("ErrMsg").toString(); 
				Log.warn("��������ʧ��,������:["+orderid+"],��ݹ�˾:["+post_company+"],��ݵ���:["+post_no+"] ������Ϣ:"+errdesc);
				if (errdesc.indexOf("����״̬�޷�����")>=0/* ||errdesc.indexOf("��������ʧ�ܣ����Ҷ���ʧ�ܣ�")>0*/)
				{
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
				}
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
			String[] temp  =cy.split(":");
			if(temp[0].equals(companycode)){
				companyid=temp[1];
				break;
			}
				
		}
		return companyid;//
	}
	
	
	private String getRemark(String orderId) throws Exception{
		String result="";
		
		return result;
	}
	
	
	@Override
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
