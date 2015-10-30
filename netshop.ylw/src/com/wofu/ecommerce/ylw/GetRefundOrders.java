package com.wofu.ecommerce.ylw;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ylw.util.CommHelper;
/**
 * 
 * ��ȡ�����˻�������ҵ
 *
 */
public class GetRefundOrders extends Thread {

	private static String jobName = "��ȡ�����˻�������ҵ";
	private static long daymillis=24*60*60*1000L;

	public GetRefundOrders() {
		setDaemon(true);
		setName(jobName);
	}

	public void run() {
		
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.ylw.Params.dbname);	
				getRefund(connection) ;
				
			} catch (Exception e) {
				try {
					e.printStackTrace() ;
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {

				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000 * Params.timeInterval))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	

	public void getRefund(Connection conn) throws Exception
	{
		String resultText = "" ;
		for(int k=0;k<5;)
		{
			try 
			{	
					//��ȡ���˻�������
					String apiMethod="suning.custom.batchrejectedOrd.query";
					 HashMap<String,String> reqMap = new HashMap<String,String>();
					 reqMap.put("startTime", Formatter.format(new Date(System.currentTimeMillis()-daymillis), Formatter.DATE_TIME_FORMAT));
				     reqMap.put("endTime",Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT) );
				     HashMap<String,String> map = new HashMap<String,String>();
				     map.put("appSecret", Params.appsecret);
				     map.put("appMethod", apiMethod);
				     map.put("format", Params.format);
				     map.put("versionNo", "v1.2");
				     map.put("appKey", Params.appKey);
				     //��������
					 String responseText = CommHelper.doRequest(map,Params.url);
					Log.info("�˻�������: "+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
					//������� 
					if(responseText.indexOf("sn_error")!=-1){   //��������
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if(!"".equals(operCode))
						{
							Log.error("������ȡ�˻�����", "��ȡ�˻�����ʧ��,operCode:"+operCode);
						}
						return;
						
					}
					
					
					JSONArray ReturnCodeList = responseObj.getJSONObject("sn_body").getJSONArray("batchQueryRejectedOrd");
					for(int i = 0 ; i < ReturnCodeList.length() ; i++)
					{	
						try{
							String orderCode=ReturnCodeList.getJSONObject(i).getString("orderCode");
							Order o = new Order();
							OrderUtils.createRefundOrder("���������˻����ӿڶ���", conn, Params.tradecontactid, o,Params.url,Params.appKey,Params.appsecret,Params.format) ;
						}catch(Exception ex){
							if(conn!=null && !conn.getAutoCommit()){
								conn.rollback();
							}
							Log.error(jobName, ex.getMessage());
							continue;
							
						}
						
					}
					
				break;
				
			}catch (Exception e) 
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
}