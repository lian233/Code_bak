package com.wofu.ecommerce.ecshop;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import com.wofu.base.systemmanager.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.util.CommHelper;
/**
 * 
 * ��ȡecshop�˻�������ҵ
 *
 */
public class GetRefundOrders extends Thread {

	private static String jobName = "��ȡecshop�˻�������ҵ";
	private static long daymillis=24*60*60*1000L;
	private static String lastRefundTime=Params.username+"ȡ�˻�����ʱ��";
	private static String lastRefundvalue="";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public void run() {
		
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			Ecshop.setCurrentDate_getRefundOrder(new Date());
			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.ecshop.Params.dbname);
				lastRefundvalue = PublicUtils.getConfig(connection, lastRefundTime, "");
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
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lastRefundvalue,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					Date startdate=new Date(Formatter.parseDate(lastRefundvalue,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lastRefundvalue,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//������
					String apimethod="search_order_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L);
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", Params.pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        
			        Log.info("��"+pageIndex+"ҳ");
					String responseText = CommHelper.doRequest(reqMap,Params.url);
					//Log.info("��������Ϊ:��"+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText);
					  //sn_error
					if(!"success".equals(responseObj.getString("result"))){   //��������
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //û�н��
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lastRefundTime, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
								return;
							}catch(ParseException e)
							{
								Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
							Log.info("û�п��õĶ���!");
						}else{
							Log.warn("ȡ����������,������: "+operCode);
						}
						
						break;
					}
					
					
					
					JSONObject orderInfos = responseObj.getJSONObject("info");

					//��ҳ��
					
					String orderTotal = String.valueOf(orderInfos.getString("counts"));
					int orderTotaltemp = Integer.parseInt(orderTotal);
					int pageTotalTemp  = Double.valueOf(Math.ceil(orderTotaltemp/Double.parseDouble(Params.pageSize))).intValue();
					String pageTotal =String.valueOf(pageTotalTemp);
					Log.info("�ܶ�����Ϊ�� "+orderTotal);
					Log.info("��ҳ��Ϊ�� "+pageTotal);
					if (orderTotal==null || orderTotal.equals("") || orderTotal.equals("0"))
					{				
						if (n==1)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lastRefundTime, value);			    
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
					//����Ԫ��
					JSONArray ordersList = orderInfos.getJSONArray("data_info");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//ĳ������
						JSONObject orderInfo = ordersList.getJSONObject(i);
						int returnOrderCount =0;
						//������� 
						String orderCode = (String)orderInfo.get("order_sn");
						//������Ʒ����
						JSONArray items = orderInfo.getJSONArray("shop_info");
						//����һ����������
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "shop_info", items);
						Log.info("����״̬:��"+o.getShipping_status()+"����״̬: "+o.getPay_status());
						if(o != null)
						{	
							if("4".equals(o.getPay_status())){   //��������
								Log.info("������:��"+o.getOrder_sn());
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
									
										try
											{
												OrderUtils.createRefundOrder(jobName,conn, Params.tradecontactid, o);
												
											} catch(Exception sqle)
											{
												throw new JException("�����˻���������!" + sqle.getMessage());
											}
											//�����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
											//����ͬ����������ʱ��
											
							                if (new Date(o.getAdd_time()*1000L).compareTo(modified)>0)
							                {
							                	modified=new Date(o.getAdd_time()*1000L);
							                }		
								}

						}
							
						}
					//�ж��Ƿ�����һҳ
					if("".equals(pageTotal) || pageTotal == null)
						pageTotal="0" ;
					if(pageIndex >= Integer.parseInt(pageTotal))
						hasNextPage = false ;
					else
						pageIndex ++ ;
					
					n++;
						
				}
				
				if (modified.compareTo(Formatter.parseDate(lastRefundvalue, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lastRefundTime, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				break;
			} catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("����ȡecshop�˻��������������!");
	}

}