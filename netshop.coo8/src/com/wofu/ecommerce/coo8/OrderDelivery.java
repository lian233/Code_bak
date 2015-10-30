package com.wofu.ecommerce.coo8;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.order.OrderSendRequest;
import com.coo8.api.response.order.OrderSendResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
public class OrderDelivery extends Thread {

	private static String jobName = "��Ͷ�������������ҵ";

	private boolean is_exporting = false;

	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
		    	
				doDelivery(connection,getDeliveryOrders(connection));		
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		String sql = "" ;
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("post_company").toString().toUpperCase();
			String postNo = hto.get("post_no").toString();
			String sheetType = String.valueOf(hto.get("sheetType"));

			try 
			{
				boolean success = false ;
				//����
				if("3".equals(sheetType))
				{
					success = delivery(jobName, orderID, postCompany, postNo) ;
				}
				else
				{
					Log.error(jobName, "δ֪��������:"+sheetType) ;
					continue ;
				}
				
				if(success)
				{
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = "+sheetType;
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype="+sheetType;

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);	
					Log.info("���·�����Ϣ�ɹ�����͵��š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����") ;
				}
			}
			catch (Exception e) 
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				e.printStackTrace() ;
				Log.info("���·�����Ϣʧ�ܣ���͵��š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������룺" + e.getMessage()) ;
			}
			
		}
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)throws Exception
	{	

		
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			
			sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock) "
				+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
				+ Params.tradecontactid + "' and b.companycode=c.companycode";

			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				
				
				Hashtable<String,String> ht=new Hashtable<String,String>();
			
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				
				
				
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString().trim());
				String companyid=getCompnayID(hto.get("companycode").toString().trim());
				
				if (companyid.equals("")) 
				{
					Log.info("δ����������˾����:"+hto.get("companycode").toString());
					continue;
				}
				
				ht.put("post_company", companyid);
				
				String postno=hto.get("outsid").toString().trim();
				
				ht.put("post_no", postno);
				ht.put("sheetType", String.valueOf(hto.get("sheettype"))) ;
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobName, "��ѯ��������Ϣ����:"+sqle.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
		return vtorders;
	}
	
	private String getCompnayID(String companycode)throws Exception
	{
		String companyid="";
		
	
		String com[] = Params.company.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			if(s[0].equals(companycode))
			{
				companyid=s[1];
				break;
			}
		}
		
		return companyid;
		
	}
	

	//����
	private boolean delivery(String jobName,String orderId,String logisticsId,String waybill)
	throws Exception{
		boolean flag = false ;
		try 
		{

			String timenow=Formatter.format(new Date(), Formatter.DATE_TIME_MS_FORMAT);
			Coo8Client cc = new DefaultCoo8Client(Params.url,Params.appKey, Params.secretKey);
			OrderSendRequest og=new OrderSendRequest();
			og.setOrderid(orderId);
			og.setCarriersName(logisticsId);
			
			og.setLogisticsNumber(waybill);
			OrderSendResponse getResponse = cc.execute(og);
			//
			String success = getResponse.getMsg() ;
			Log.info("���ؽ����"+success);
			if(success.endsWith("SUCCESS"))
			{
				flag = true ;
				Log.info("���·�����Ϣ�ɹ�����͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "��") ;
			}
			else
			{
				if("FAILURE".equals(success))
					flag = true ;
				else if(success.indexOf("�ö���״̬����'��ʼ'��'����������'")==0){
					flag = true ;
				}
					
				else{
					Log.info("���·�����Ϣʧ�ܣ���͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + getResponse.getMsg() + "," + getResponse.getErrorCode()) ;
				}
				
			}
		} catch (Exception e) {
			flag = false ;
			Log.info("���·�����Ϣʧ�ܣ���͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + e.getMessage()) ;
		}
		return flag ;
	}

	public String toString()
	{
		return jobName + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
