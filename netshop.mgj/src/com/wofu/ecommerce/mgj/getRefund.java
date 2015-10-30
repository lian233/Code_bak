package com.wofu.ecommerce.mgj;


import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.mgj.utils.Utils;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class getRefund extends Thread {

	private static String jobName = "��ȡһ�ŵ��˻���ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ�˻�������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public getRefund() {
		setDaemon(true);
		setName(jobName);
	}

	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.mgj.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getRefundList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.mgj.Params.waittime * 1000*5))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮��������˻���
	 */
	private void getRefundList(Connection conn) throws Exception
	{		
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					
					Map<String, String> refundlistparams = new HashMap<String, String>();
			        //ϵͳ����������
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					
					refundlistparams.put("appKey", Params.app_key);
					refundlistparams.put("sessionKey", Params.token);
					refundlistparams.put("format", Params.format);
					refundlistparams.put("method", "yhd.refund.get");
					refundlistparams.put("ver", Params.ver);
					refundlistparams.put("dateType", "5");
					refundlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        
					refundlistparams.put("curPage", String.valueOf(pageno));
					refundlistparams.put("pageRows", "50");
			        refundlistparams.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        refundlistparams.put("endTime", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        refundlistparams.put("dateType",String.valueOf(1));
			        
			        String responseOrderListData = Utils.sendByPost(refundlistparams, Params.app_secret, Params.url);
			        //Log.info("�˻�: "+responseOrderListData);
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					if (responseOrderListData.indexOf("errInfoList")>=0)
					{
						JSONArray errinfolist=responseproduct.getJSONObject("response").optJSONObject("errInfoList").optJSONArray("errDetailInfo");
						String errdesc="";
						
						for(int j=0;j<errinfolist.length();j++)
						{
							JSONObject errinfo=errinfolist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						Log.error(jobName, "ȡ�˻����б�ʧ��:"+errdesc);
						
						if (errdesc.indexOf("����ָ���Ĳ����鲻����Ӧ���˻���Ϣ")>=0)
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
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
						if(errdesc.indexOf("�ýӿڵ��óɹ��ʹ��ͣ�������")>=0){
							Log.info("�ӿڵ��ù���Ƶ����");
							break;
						}
						k=10;
						break;
					}
					
					int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					int errorCount=responseproduct.getJSONObject("response").getInt("errorCount");
					
					if (errorCount>0)
					{
						String errdesc="";
						JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
						for(int j=0;j<errlist.length();j++)
						{
							JSONObject errinfo=errlist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						if (errdesc.indexOf("�˻��б���Ϣ������")<0)
						{
							k=10;
							throw new JException(errdesc);
						}
					}
					
										
					
					int i=1;
			
			
								
					if (totalCount==0)
					{				
						if (i==1)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
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
						k=10;
						break;
					}
					
					
					JSONArray refundlist=responseproduct.getJSONObject("response").getJSONObject("refundList").getJSONArray("refund");
					
					
					for(int j=0;j<refundlist.length();j++)
					{
						try{
							JSONObject refund=refundlist.getJSONObject(j);
							
							Map<String, String> orderparams = new HashMap<String, String>();
					        //ϵͳ����������
							orderparams.put("appKey", Params.app_key);
							orderparams.put("sessionKey", Params.token);
							orderparams.put("format", Params.format);
							orderparams.put("method", "yhd.refund.detail.get");
							orderparams.put("ver", Params.ver);
							orderparams.put("dateType", "5");
							orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					        
					    
							orderparams.put("refundCode", refund.getString("refundCode"));
					     
					        
					        
							String responseData =Utils.sendByPost(orderparams, Params.app_secret, Params.url);
							//Log.info("responseData: "+responseData);

							JSONObject responseorder=new JSONObject(responseData);
							
							int errorOrderCount=responseorder.getJSONObject("response").getInt("errorCount");
							
							if (errorOrderCount>0)
							{
								String errdesc="";
								JSONArray errlist=responseorder.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
								for(int n=0;n<errlist.length();n++)
								{
									JSONObject errinfo=errlist.getJSONObject(n);
									
									errdesc=errdesc+" "+errinfo.getString("errorDes"); 
														
								}
								
								k=10;
								throw new JException(errdesc);						
							}
							
							
							JSONObject refunddetail=responseorder.getJSONObject("response").getJSONObject("refundInfoMsg").getJSONObject("refundDetail");
							
							
							RefundDetail r=new RefundDetail();
							r.setObjValue(r, refunddetail);
											
							
							JSONArray refundItemList=responseorder.getJSONObject("response").getJSONObject("refundInfoMsg").getJSONObject("refundItemList").getJSONArray("refundItem");
							
							r.setFieldValue(r, "refundItemList", refundItemList);
							
							//�˻�״̬(0:�����;3:�ͷ��ٲ�;4:�Ѿܾ�;11:�˻���-���˿ͼĻ�;12:�˻���-��ȷ���˿�;13:������;27:�˿����;33:�������;34:�ѳ���;40:�ѹر�)
					
							Log.info(r.getOrderCode()+" "+r.getRefundStatus()+" "+Formatter.format(r.getApplyDate(),Formatter.DATE_TIME_FORMAT));
							/*
							 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
							 *2��ɾ���ȴ���Ҹ���ʱ��������� 
							 */		
							String sku;
							String sql="";
							if(r.getRefundStatus()==40) {
								Log.info("������: "+r.getOrderCode()+",�Ѿ��ر�");
								continue;
								
							}else if(r.getRefundStatus()==27){
								Log.info("������: "+r.getOrderCode()+",�˿��Ѿ����");
								continue;
							}
					
							if (!OrderManager.RefundIntfExists("���һ�ŵ��˻���", conn, r.getOrderCode(),r.getRefundCode()))
							{
								//OrderUtils.createRefund(conn,r,
										//Integer.valueOf(Params.tradecontactid).intValue(),Params.app_key,Params.token,Params.format,Params.ver);
							
							}
									
							
							//����ͬ����������ʱ��
			                if (r.getApplyDate().compareTo(modified)>0)
			                {
			                	modified=r.getApplyDate();
			                }
						}catch(Exception e){
							Log.error(jobName, e.getMessage());
							continue;
						}
						
					}
						
						
						
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					
					pageno++;
					
					i=i+1;
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(jobName+", Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
