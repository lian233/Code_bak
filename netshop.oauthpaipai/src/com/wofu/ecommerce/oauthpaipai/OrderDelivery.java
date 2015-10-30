package com.wofu.ecommerce.oauthpaipai;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;
public class OrderDelivery extends Thread {

	private static String jobName = "���Ķ�������������ҵ";
	
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {
				//�ı侲̬ʱ��
				PaiPai.setCurrentDate_DevOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.oauthpaipai.Params.dbname);
			
				doDelivery(connection,getDeliveryOrders(connection));		
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.oauthpaipai.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Throwable{
	

		Log.info("������ҵ��ʼ");
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			try{
				Hashtable hto = (Hashtable) vdeliveryorder.get(i);
				String sheetid = hto.get("sheetid").toString();
				String orderid = hto.get("orderid").toString();
				String post_company = hto.get("post_company").toString();
				String post_no = hto.get("post_no").toString();
				
				//��鶩��״̬
				String sql = "";
				
				if (!doVerifyStatus(orderid))
				{
					Log.warn(jobName,"������"+orderid+"��״̬����ȷ,���鲢�ֹ�����ö���!");
					
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
				
				PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(Params.spid, Params.secretkey, Params.token, Long.valueOf(Params.uid));
				
				sdk.setCharset(Params.encoding);
				
				HashMap<String, Object> params = sdk.getParams("/deal/sellerConsignDealItem.xhtml");
		
				params.put("sellerUin", Params.uid);
				params.put("dealCode", orderid);
				params.put("logisticsName", post_company);
				params.put("logisticsCode", post_no);
				params.put("arriveDays", hto.get("arrivedays").toString());

				
				String result = sdk.invoke();;	

				Document doc = DOMHelper.newDocument(result.toString(),Params.encoding);
				Element urlset = doc.getDocumentElement();
				String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
				String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage");

				
				if (errorcode.equals("0")) {						
					
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);						

					Log.info("��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
				} else {
					Log.error(jobName,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no+ "��,������Ϣ:" + errormessage);
				}
			}catch(Throwable ex){
				try{
					if(conn!=null && !conn.getAutoCommit())
						conn.rollback();
				}catch(Throwable e){
					Log.error(jobName, e.getMessage());
				}
				
				Log.error(jobName, ex.getMessage());
				
			}
			

		}
		Log.info("������ҵִ�����");
	}
	
	//��־��������״̬ǰ��鶩���Ƿ�Ϊ�ȴ����ҷ�����Ƶ�����÷����ӿ�ʧ�ܻᱻ����
	private boolean doVerifyStatus(String orderid) throws Throwable
	{
		boolean status_flag=true;
			
		PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(Params.spid, Params.secretkey, Params.token, Long.valueOf(Params.uid));
		
		sdk.setCharset(Params.encoding);
		
		HashMap<String, Object> params = sdk.getParams("/deal/getDealDetail.xhtml");
	
		params.put("sellerUin", Params.uid);			
		params.put("dealCode", orderid);
			
		
		String result = sdk.invoke();
		//Log.info("result: "+ result);
		Document doc = DOMHelper.newDocument(result.toString(), Params.encoding);
		Element urlset = doc.getDocumentElement();
		//���߸�����������������ֲ�ͬ��״̬
		if (!DOMHelper.getSubElementVauleByName(urlset,"dealState").equalsIgnoreCase("DS_WAIT_SELLER_DELIVERY"))
		{
			status_flag=false;
		}
		else
		{
			status_flag=true;				
		}
		
		return status_flag;
	}

	
	private Vector<Hashtable> getDeliveryOrders(Connection conn) throws Throwable
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays from it_upnote a , ns_delivery b,deliveryref c "
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ Params.tradecontactid + "' and b.companycode=c.companycode";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString());
				ht.put("post_company", hto.get("companycode").toString());
				ht.put("post_no", hto.get("outsid").toString());
				ht.put("arrivedays",hto.get("defaultarrivedays"));
				vtorders.add(ht);
			}
		}
		catch(Throwable sqle)
		{
			Log.error(jobName, "��ѯ��������Ϣ����:"+sqle.getMessage());
		}		
		return vtorders;
	}
	
	
	public String toString()
	{
		return jobName + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
