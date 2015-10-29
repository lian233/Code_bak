package com.wofu.ecommerce.jingdong;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.internal.util.CodecUtil;
import com.jd.open.api.sdk.internal.util.StringUtil;
import com.jd.open.api.sdk.request.delivery.EtmsWaybillcodeGetRequest;
import com.jd.open.api.sdk.request.order.OrderVenderRemarkQueryByOrderIdRequest;
import com.jd.open.api.sdk.request.order.OrderVenderRemarkUpdateRequest;
import com.jd.open.api.sdk.response.delivery.EtmsWaybillcodeGetResponse;
import com.jd.open.api.sdk.response.order.OrderVenderRemarkQueryByOrderIdResponse;
import com.jd.open.api.sdk.response.order.OrderVenderRemarkUpdateResponse;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
/**
 * A��������ݷ������̣�cod����)��
 1����ȡ���������˵��Žӿڣ�����ȡ�ؾ����������ţ�
 2�����������ӵ��ӿڣ��򾩶�����ϵͳ�ش��˵���Ϣ����Ʒ�ĳ���ߣ�������Լ��ջ�����Ϣ�ȣ�
 3��SOP���⣨ԭҵ���߼����䣬����ƥ���˵��źͶ����š����������ش�������˾id:2087���˵��ż��ɣ�
 ע���밴��˳����ýӿڡ�1����ȡ�˵��ţ�2���ύ�˵���Ϣ��3������
 ���˳����ȷ�������˵��ŵ��������ͣ�Ҫ��Ϊstring��
 �̼Һ�̨�ľ��������˵���Ϊ9��ͷ���������ڽӿڳ���ʹ��
 * @author windows7
 *
 */
public class OrderDelivery extends Thread {

	private static String jobName = "������������������ҵ";
	private static String company = Params.companycode ;

//	api V2
	private static String SERVER_URL = Params.SERVER_URL ;
	private static String appKey = Params.appKey ;
	private static String appSecret = Params.appSecret ;
	private boolean is_exporting = false;
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {	
				Jingdong.setCurrentDate_DevOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.jingdong.Params.dbname);
				Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				doDelivery(connection,getDeliveryOrders(connection));
				//modifiRemark(connection);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jingdong.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	/**
	 * ������ݷ�����һ������ȡ���������˵���
	 * @throws Exception
	 */
	private String getJDPostNo()
	{	
		String result="";
		try{
			JdClient client = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
			EtmsWaybillcodeGetRequest  request = new EtmsWaybillcodeGetRequest ();
			request.setPreNum("1");
			request.setCustomerCode(Params.JBDCustomerCode);
			EtmsWaybillcodeGetResponse response = client.execute(request);
			Log.info("�˵���: "+response.getResultInfo().getDeliveryIdList().get(0));
			result=  response.getResultInfo().getDeliveryIdList().get(0);
		}catch(Exception ex){
			Log.error("��ȡ���������˵��ų���", ex.getMessage());
		}
		return result;
		
	}
	
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Throwable
	{
		Log.info("���η������� Ϊ��"+vdeliveryorder.size());
		String sql = "" ;
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("post_company").toString().toUpperCase();
			String companycode = hto.get("companycode").toString().toUpperCase();
			String postNo = hto.get("post_no").toString();
			String sheetType = String.valueOf(hto.get("sheetType"));
			try 
			{
				boolean success = false ;
				
				if (companycode.equalsIgnoreCase("JDKD"))
				{
					if(!Params.jdkdNeedDelivery)
						success = true ;
					else
						success = StockUtils.SOPOrderDelivery(jobName, orderID, postCompany, postNo, SERVER_URL, Params.token, appKey, appSecret) ;
				}
				else
				{
				
					//����
					if("3".equals(sheetType))
					{
						if (Params.isLBP){
							success = StockUtils.LBPOrderDelivery(jobName, orderID, postCompany, postNo, SERVER_URL, Params.token, appKey, appSecret) ;
						}
							
						else{
									success = StockUtils.SOPOrderDelivery(jobName, orderID, postCompany, postNo, SERVER_URL, Params.token, appKey, appSecret) ;
							}
							
							
					}
					//ת��
					else if("4".equals(sheetType))
						success = StockUtils.SOPModifyExpressInfo(jobName, orderID, postCompany, postNo, SERVER_URL, Params.token, appKey, appSecret) ;
					else
					{
						Log.error(jobName, "δ֪��������:"+sheetType) ;
						continue ;
					}
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
				}
			}
			catch (Throwable e) 
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.info("���·�����Ϣʧ�ܣ��������š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������룺" + e.getMessage()) ;
			}
			
		}
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)
	{	

		
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			
			sql = "select a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock) "
				+ "where (a.sheettype=3 or a.sheettype=4) and a.sheetid=b.sheetid and a.receiver='"
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
				ht.put("companycode", hto.get("companycode").toString().trim());
				
				String postno=hto.get("outsid").toString().trim();
				if(postno.indexOf("-")!=-1){
					postno=postno.substring(0,postno.indexOf("-"));
				}
				ht.put("post_no", postno);
				ht.put("sheetType", String.valueOf(hto.get("sheettype"))) ;
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobName, "��ѯ��������Ϣ����:"+sqle.getMessage());
		}
		catch(Throwable e)
		{
			e.printStackTrace() ;
		}
		return vtorders;
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
				String tempRemark = remarks[1];
				String remark = getRemark(Long.parseLong(tid));
				if(!"".equals(tempRemark)) remark=tempRemark +"  "+remark;
				JdClient client = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
				OrderVenderRemarkUpdateRequest request = new OrderVenderRemarkUpdateRequest();
				request.setOrderId(tid);
				request.setRemark(remark);
				OrderVenderRemarkUpdateResponse response = client.execute(request);
				if("0".equals(response.getCode())){
					try{
						conn.setAutoCommit(false);
						sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
								+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
								+ " where SheetID = '"+ sheetid+ "' and SheetType = 5";
						SQLHelper.executeSQL(conn, sql);

						sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=5";

						SQLHelper.executeSQL(conn, sql);
						conn.commit();
						conn.setAutoCommit(true);	
						Log.info(jobName,"��������" + tid + "�� sheetid��"+sheetid+"�� �޸ı�ע�ɹ�");
					}catch(Exception ex){
						try{
							if(!conn.getAutoCommit()){
								conn.rollback();
							}
						}catch(Exception e){
							
						}
						try{
							conn.setAutoCommit(true);
						}catch(Exception els){}
						throw new Exception("�ع�����ʧ��!");
					}
					
				}else{
					Log.info(jobName,"��������" + tid + "�� sheetid��"+sheetid+"�� �޸ı�עʧ��");
				}
			}catch(Exception ex){
				Log.error(jobName, ex.getMessage());
			}
			
		}
		Log.info(jobName+",�޸Ķ�����ע���");
		
	}
	
	private String getRemark(Long orderId) throws Exception {
		String result ="";
		JdClient client = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
		OrderVenderRemarkQueryByOrderIdRequest request=new OrderVenderRemarkQueryByOrderIdRequest();
		request.setOrderId( orderId );
		OrderVenderRemarkQueryByOrderIdResponse response=client.execute(request);
		if("�ɹ�".equals(response.getVenderRemarkQueryResult().getApiJosResult().getResultDescribe()))
		result=response.getVenderRemarkQueryResult().getVenderRemark().getRemark();
		return result!=null?result:"";
	}
	
	private String getCompnayID(String companycode)
	{
		String companyid="";
		
	
		String com[] = company.split(";") ;
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
	
	
	public String toString()
	{
		return jobName + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
	
	
	
}
