package com.wofu.netshop.alibaba.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.alibaba.fenxiao.api.ApiCallService;
import com.wofu.netshop.alibaba.fenxiao.util.CommonUtil;
/**
 * ����Ͱͷ����߳���
 * @author bolinli
 *
 */
public class DeliveryRunnable implements Runnable{
	private String jobName="����Ͱͷ���������ҵ";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	public DeliveryRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	
	public void run() {
		Connection conn=null;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			delivery(conn,getDeliveryOrders(conn));
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
	
	
	private void delivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception{
		Log.info("���η������� Ϊ��"+vdeliveryorder.size());
		String sql = "" ;
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("post_company").toString().toUpperCase();
			String postNo = hto.get("post_no").toString();
			String sheetType = String.valueOf(hto.get("sheetType"));

			//���Ӷ���ID��ȡ��Ʒ��������Ķ�����ϸID
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("orderId", orderID);
			//Log.info("tid: "+orderID);
			params.put("sellerMemberId", param.sellerMemberId);
			params.put("access_token",param.token);
			//trade.order.orderList.get
			String urlPath=CommonUtil.buildInvokeUrlPath(param.namespace,"trade.order.orderList.get",param.version,param.requestmodel,param.appkey);
			String response = ApiCallService.callApiTest(param.url, urlPath, param.secretKey, params);
			//Log.info("result: "+response);
			JSONObject res=new JSONObject(response);
			JSONArray orderEntries=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).getJSONArray("orderEntries");
			
			String orderdetailids="";
			for(int j=0; j<orderEntries.length();j++){
				JSONObject o=orderEntries.getJSONObject(j);
				orderdetailids=orderdetailids+o.getLong("id");
				if(j>=0&&j<orderEntries.length()-1){
					orderdetailids=orderdetailids+",";
				}
			}
			try 
			{
				boolean success = false ;
				
				//����
				if("3".equals(sheetType))
				{
					success = delivery(jobName, orderID, postCompany, postNo,orderdetailids, param.url, param.token, param.appkey, param.secretKey) ;
				}
				else if("4".equals(sheetType))
					success = ModifyExpressInfo(jobName, orderID, postCompany, postNo,orderdetailids, param.url, Params.token, param.appkey, param.secretKey) ;
				else
				{
					Log.error(jobName, "δ֪��������:"+sheetType) ;
					continue ;
				}
				
				if(success)
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
			
			sql = "select a.id,a.tid,a.companycode,a.outsid from itf_delivery a,Inf_UpNote b "
				+"where a.id=b.OperData and a.sheettype=3 and a.shopid="+param.shopid;

			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				
				
				Hashtable<String,Object> ht=new Hashtable<String,Object>();
			
				Hashtable<String, Object> hto = (Hashtable<String,Object>) vt.get(i);
				
				
				
				ht.put("sheetid", hto.get("id"));
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
	
	private String getCompnayID(String companycode)
	{
		String companyid="";
		
	
		String com[] = param.company.split(";") ;
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
	
	
	//����  --�Լ���ϵ����������������
	private boolean delivery(String jobname,String orderId,String logisticsId,String waybill,String orderdetailids,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{

			String timenow=Formatter.format(new Date(), Formatter.DATE_TIME_MS_FORMAT);
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("memberId",param.sellerMemberId);
			params.put("orderId", orderId);
			params.put("orderEntryIds", orderdetailids);				//������ϸIDs
			params.put("tradeSourceType", "cbu-trade");
			params.put("logisticsCompanyId", logisticsId);			//������˾ID
																	//����������˾����
			params.put("logisticsBillNo", waybill);					//�˻�����
			params.put("gmtSystemSend", timenow);						//ϵͳ����ʱ��
			params.put("gmtLogisticsCompanySend", timenow);				//���ҷ���ʱ��
			params.put("gmtLogisticsCom", timenow);				//���ҷ���ʱ��
			params.put("access_token", token);
			String urlPath=CommonUtil.buildInvokeUrlPath(param.namespace,"e56.logistics.offline.send",param.version,param.requestmodel,param.appkey);
			String response = ApiCallService.callApiTest(param.url, urlPath, param.secretKey, params);
			
			JSONObject res=new JSONObject(response);
			//״̬��
			boolean code = res.getBoolean("success") ;
			
			if(code)
			{
				flag = true ;
				Log.info("���·�����Ϣ�ɹ�������Ͱ͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "��") ;
			}
			else
			{
				if("35".equals(code) || "61".equals(code))
					flag = true ;
				Log.info("���·�����Ϣʧ�ܣ�����Ͱ͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + res.getString("resultMsg") + "," + res.getString("resultCode")) ;
			}
		} catch (Exception e) {
			flag = false ;
			Log.info("���·�����Ϣʧ�ܣ�����Ͱ͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + e.getMessage()) ;
		}
		return flag ;
	}
	
	
	//�޸Ŀ����Ϣ V2
	private boolean ModifyExpressInfo(String jobname,String orderId,String logisticsId,String waybill,String orderdetailids,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{
			String timenow=Formatter.format(new Date(), Formatter.DATE_TIME_MS_FORMAT);
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("memberId",param.sellerMemberId);
			params.put("orderId", orderId);
			params.put("orderEntryIds", orderdetailids);				//������ϸIDs
			params.put("tradeSourceType", "cbu-trade");
			params.put("logisticsCompanyId", logisticsId);			//������˾ID
																//����������˾����
			params.put("logisticsBillNo", waybill);					//�˻�����
			params.put("gmtSystemSend", timenow);						//ϵͳ����ʱ��
			params.put("gmtLogisticsCompanySend", timenow);				//���ҷ���ʱ��
			
			params.put("access_token", token);
			String urlPath=CommonUtil.buildInvokeUrlPath(param.namespace,"e56.logistics.offline.send",param.version,param.requestmodel,param.appkey);
			String response = ApiCallService.callApiTest(param.url, urlPath, param.secretKey, params);
			
			JSONObject res=new JSONObject(response);
			//״̬��
			boolean code = res.getBoolean("success") ;
			if(code)
			{
				flag = true ;
				Log.info("���ת���ɹ�,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill +"��") ;
			}
			else
			{
				flag = false ;
				Log.error(jobname, "���ת��ʧ��,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill + "��,������Ϣ:"+res.getString("resultMsg") + "," + res.getString("resultCode")) ;
			}
			 
		} catch (Exception e) {
			flag = false ;
			Log.error(jobname, "���ת��ʧ��,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill + "��,������Ϣ:"+e.getMessage()) ;
		}
		return flag ;
	}

}
