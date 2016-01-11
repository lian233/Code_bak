package com.wofu.ecommerce.miya;
import java.sql.Connection;
import java.sql.SQLException;
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
import com.wofu.ecommerce.miya.Params;
import com.wofu.ecommerce.miya.utils.Utils;
public class OrderDelivery extends Thread {

	private static String jobname = "��������������������ҵ";
	
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
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+"upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0 ";
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
			Map<String, String> confirmlistparams = new HashMap<String, String>();
	        //ϵͳ����������
			confirmlistparams.put("method", "mia.order.confirm");
			confirmlistparams.put("vendor_key", Params.vendor_key);
			confirmlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			confirmlistparams.put("version", Params.ver);
			//Ӧ�ü��������
			confirmlistparams.put("order_id", orderid);
			String responseConfirmListData = Utils.sendByPost(confirmlistparams, Params.secret_key, Params.url);
		    JSONObject confirmData = new JSONObject(responseConfirmListData);
			//�ж��Ƿ��Ѿ��򵥻��ߴ򵥳ɹ�
			int code = confirmData.optInt("code");
			String msg = confirmData.optString("msg");
			if(code!=200){
				Log.error("��ʧ��ʧ�ܣ������˵�","������:"+orderid+" ������Ϣ:"+ msg+" �����:"+code);
				if(code==181){
					Log.info("�����Ѿ�������ɾ��IT_UpNote" +sheetid+"������"+orderid);
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
			}
			//��ѯiid ��Ʒ��ˮ��
			Vector<Hashtable> itemList = new Vector<Hashtable>();
			sql = "select iid from ns_orderitem(NOLOCK) where oid = '"+orderid+"'";
			itemList=SQLHelper.multiRowSelect(conn, sql);

			if(itemList.size()<=0){
				Log.error("�����Ҳ�����Ʒ��ϸ","������:"+orderid);
				continue;
			}
			StringBuffer item_id = new StringBuffer();
			System.out.println(itemList.size());
			for(int j=0;j<itemList.size();j++){
				Hashtable ht=(Hashtable) itemList.get(j);
				String item = ht.get("iid").toString().trim();
				if(j<1){
					item_id.append(item);
				}else{
					item_id.append(","+item);
				}
			}
			
			JSONArray sheet_code_info = new  JSONArray();
			JSONObject express = new JSONObject();
			express.put("sheet_code", post_no);
			express.put("logistics_id", postcompanyid);
			sheet_code_info.put(express);
			
			System.out.println(sheet_code_info.toString());
			System.out.println(item_id.toString());
			
			
			Map<String, String> deliverlistparams = new HashMap<String, String>();
	        //ϵͳ����������
			deliverlistparams.put("method", "mia.order.deliver.upgrade");
			deliverlistparams.put("vendor_key", Params.vendor_key);
			deliverlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			deliverlistparams.put("version", Params.ver);
			//Ӧ�ü��������
			deliverlistparams.put("sheet_code_info", sheet_code_info.toString());
			deliverlistparams.put("item_id", item_id.toString());
			deliverlistparams.put("order_id", orderid);
			
			
			String responseOrderListData2 = Utils.sendByPost(deliverlistparams, Params.secret_key, Params.url);
			JSONObject responseproduct=new JSONObject(responseOrderListData2);
			System.out.println(Utils.Unicode2GBK(responseOrderListData2));
			String msg2 = responseproduct.optString("msg");
			int code2 = responseproduct.optInt("code");

			if(code2!=200&&code2!=164){
				Log.warn("��������ʧ��,������:["+orderid+"],��ݹ�˾:["+post_company+"],��ݵ���:["+post_no+"] ������Ϣ:"+msg2);
//				if (errdesc.indexOf("״̬�쳣")>=0 ||errdesc.indexOf("��������ʧ�ܣ����Ҷ���ʧ�ܣ�")>0)
//				{
//					conn.setAutoCommit(false);
//
//					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
//							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
//							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
//					SQLHelper.executeSQL(conn, sql);
//
//					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
//
//					SQLHelper.executeSQL(conn, sql);
//					conn.commit();
//					conn.setAutoCommit(true);
//				}
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
	
	

	public String time() {
		Long time= System.currentTimeMillis()/1000;
		return time.toString();
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
