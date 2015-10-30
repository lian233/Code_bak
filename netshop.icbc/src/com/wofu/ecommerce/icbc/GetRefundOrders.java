package com.wofu.ecommerce.icbc;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import com.wofu.base.systemmanager.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.icbc.util.CommHelper;
/**
 * 
 * ��ȡ������˵��˻�������ҵ
 *
 */
public class GetRefundOrders extends Thread {

	private static String jobName = "��ȡ������˵��˻�������ҵ";
	private static long daymillis=24*60*60*1000L;
	private static String lasttime="";
	private static final String lastReturnValue= Params.username+"ȡ�˻���������ʱ��";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public void run() {
		
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			
			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.icbc.Params.dbname);
				lasttime=PublicUtils.getConfig(connection, lastReturnValue,"");// Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
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
		int pageIndex=1;
		String resultText = "" ;
		boolean hasNextPage=true;
		long modified = Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()/1000L;
		for(int k=0;k<5;)
		{
			try 
			{	
				int n=1;
				while(hasNextPage){
					long startdate=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()/1000+1L;
					long enddate=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()/1000+daymillis;
					//������
					String apimethod="get_refund_list.php";
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("start_time",startdate);
			        map.put("end_time",enddate);
			        map.put("page", String.valueOf(pageIndex));
			        map.put("limit", Params.pageSize);
			        map.put("apimethod", apimethod);
			        //map.put("key", MD5Util.getMD5Code((Params.vcode+startdate).getBytes()));
			        //��������
					String responseText = CommHelper.doPost(map,Params.url);
					Log.info("��������Ϊ: "+responseText);
						//�ѷ��ص�����ת��json����
						JSONObject responseObj= new JSONObject(responseText);
						if(responseObj.getInt("status")==5){   //codeΪ1����û�����ݷ���
							Log.info("����ȡ�����˻�����");
							if (n==1)		
							{
								try
								{
									//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
									if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
											compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lastReturnValue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
									{
										try
					                	{
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lastReturnValue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
											PublicUtils.setConfig(conn, lastReturnValue, value);			    
					                	}catch(JException je)
					                	{
					                		Log.error(jobName, je.getMessage());
					                	}
									}
								}catch(ParseException e)
								{
									Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
								}
							}
							break;
						}
						//������� 
						if(responseObj.getInt("status")!=1){   //��������
							int operCode= responseObj.getInt("status");
								Log.error("������˵��ȡ�˻�����", "��ȡ�˻�����ʧ��,operCode:"+operCode);
							return;
							
						}
						
						//��ҳ��
						int  pageTotal = responseObj.getInt("total_page");
						Log.info("��ҳ���� "+pageTotal);
						if (pageTotal==0)
						{				
							if (n==1)		
							{
								try
								{
									//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
									if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
											compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lastReturnValue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
									{
										try
					                	{
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lastReturnValue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
											PublicUtils.setConfig(conn, lastReturnValue, value);			    
					                	}catch(JException je)
					                	{
					                		Log.error(jobName, je.getMessage());
					                	}
									}
								}catch(ParseException e)
								{
									Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
								}
							}
							break;
						}
						
						JSONArray ReturnCodeList = responseObj.getJSONArray("list");
						for(int i = 0 ; i < ReturnCodeList.length() ; i++)
						{	RefundOrder o=null;
							try{
								String orderCode=ReturnCodeList.getJSONObject(i).getString("order_sn");
								o = OrderUtils.getRefundOrderByCode( orderCode, Params.url);
								OrderUtils.createRefundOrder( jobName,conn, Params.tradecontactid, o) ;
							}catch(Exception ex){
								if(conn!=null && !conn.getAutoCommit()){
									conn.rollback();
								}
								Log.error(jobName, ex.getMessage());
								continue;
								
							}
							if(o.getRefund_time()>modified) modified=o.getRefund_time();
							
						}
						if(pageIndex >= pageTotal-1)
							hasNextPage = false ;
						else
							pageIndex ++ ;
						
						n++;
						
				}
				if(modified>Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()/1000L){
					try{
						String value = Formatter.format(new Date(modified*1000L), Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lastReturnValue, value);
					}catch(Exception e){
						Log.error(jobName, e.getMessage());
					}
					
				}//��ȡ���˻�������
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
		Log.info("����ȡ�˻��������");
	}
}