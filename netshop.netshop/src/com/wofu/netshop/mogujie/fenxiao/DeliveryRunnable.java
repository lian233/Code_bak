package com.wofu.netshop.mogujie.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.common.fenxiao.Utils;
/**
 * Ģ���ַ����߳���
 * @author bolinli
 *
 */
public class DeliveryRunnable implements Runnable{
	private String jobName="Ģ���ַ���������ҵ";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	public DeliveryRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {
		Connection conn=null;
		username = param.username;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			delivery(conn);
		}catch(Exception e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(username,"�ر����ݿ��������  "+e1.getMessage(),null);
				}
				Log.info(username,"�����̴߳���: "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"�ر����ݿ����ӳ���: "+e.getMessage(),null);
				}
				watch.countDown();
		}
		
	}
	
	
	private void delivery(Connection conn)  throws Exception
	{
		String sql = "select a.id,a.tid,a.companycode,a.outsid from itf_delivery a,Inf_UpNote b "
			+"where a.id=b.OperData and a.sheettype=3 and a.shopid="+param.shopid;
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("����Ҫ����Ķ�����������Ϊ: "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			int sheetid = (Integer)hto.get("id");
			String orderid = hto.get("tid").toString().trim();
			String post_company = hto.get("companycode").toString().trim();
			String post_no = hto.get("outsid").toString().trim();		
			
			//���������˾Ϊ������Դ���
			if (post_company.trim().equals(""))
			{
				Log.warn(jobName, "��ݹ�˾Ϊ�գ�������:"+orderid+"");
				continue;
			}
			
			String postcompanyid=getCompanyID(post_company);
			
			if (postcompanyid.equals(""))
			{
				//���������˾Ϊ������Դ���
				if (post_company.trim().equals(""))
				{
					Log.warn(jobName, "��ݹ�˾δ���ã���ݹ�˾��"+post_company+" ������:"+orderid+"");
					conn.setAutoCommit(false);

					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
					continue;
				}
			}
			if("".equals(post_no)){
				Log.warn(jobName, "��ݵ���δ���ã���ݹ�˾��"+post_company+" ������:"+orderid+"");
				conn.setAutoCommit(false);

				sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
					+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
					+ " where operdata = "+ sheetid+ " and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
				continue;
			}
			if("".equals(postcompanyid)){
				Log.warn(jobName, "��ݹ�˾���δ���ã���ݹ�˾��"+post_company+" ������:"+orderid+"");
				continue;
			}

	
			Map<String, String> para = new HashMap<String, String>();
	        //ϵͳ����������
			para.put("app_key", param.app_key);
			para.put("access_token", param.token);
			para.put("method", "youdian.logistics.send");
			para.put("tid", orderid);
			para.put("company_code",postcompanyid);
			para.put("out_sid", post_no);
			String responseOrderListData = Utils.sendByPost(para, param.app_secret, param.url);
			//Log.info("deliveryresult:��"+responseOrderListData);
			JSONObject responseproduct=new JSONObject(responseOrderListData).getJSONObject("status");
			int errorCount=responseproduct.getInt("code");
			
			if (errorCount!=10001)
			{
				String errdesc=responseproduct.getString("msg");
				
				Log.warn("��������ʧ��,������:["+orderid+"],��ݹ�˾:["+post_company+"],��ݵ���:["+post_no+"] ������Ϣ:"+errdesc);
				if (errdesc.indexOf("״̬�쳣")>=0 ||errdesc.indexOf("��������ʧ�ܣ����Ҷ���ʧ�ܣ�")>0)
				{
					conn.setAutoCommit(false);

					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
				}
				continue ;
			}
				
			
			try {
				conn.setAutoCommit(false);

				sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
					+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
					+ " where operdata = "+ sheetid+ " and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

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
			Log.info(jobName,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");

		}
	}
	
	private String getCompanyID(String companycode) throws Exception
	{
		String companyid="";
		Object[] cys=StringUtil.split(param.company, ";").toArray();
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
	
	

}
