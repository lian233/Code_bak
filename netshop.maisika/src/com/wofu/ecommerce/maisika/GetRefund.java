package com.wofu.ecommerce.maisika;


import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;



import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.maisika.OrderUtils;
import com.wofu.ecommerce.maisika.ReturnOrder;
import com.wofu.ecommerce.maisika.util.CommHelper;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class GetRefund extends Thread {

	private static String jobName = "��ȡ��˹���˻���ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ�˻�������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public GetRefund() {
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
						com.wofu.ecommerce.maisika.Params.dbname);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.maisika.Params.waittime * 1000*5))		
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
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					LinkedHashMap<String,Object> map = new LinkedHashMap<String,Object>();
					map.put("&op","refund");
			        map.put("service","refund");
			        map.put("vcode", Params.vcode);
			        map.put("mtime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        map.put("mtime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        map.put("page", String.valueOf(pageno));
			        map.put("page_size", Params.pageSize);
			        map.put("status", "3");
			        //��������
			        Log.info("��"+String.valueOf(pageno)+"ҳ");

					String responseOrderListData = CommHelper.doGet(map,Params.url);
			        //Log.info("�˻�: "+responseOrderListData);
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					int totalCount=responseproduct.getInt("counts");
//					
					int i=1;
//								
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
										System.out.println("���˻���");
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
					
					
					JSONArray refundlist=responseproduct.getJSONArray("refund_list");
					for(int j=0;j<refundlist.length();j++)
					{
						try{
							JSONObject refund=refundlist.getJSONObject(j);//�������ҵ���Ϣ
							JSONArray orderlist = refund.getJSONArray("extend_order_goods");
							ReturnOrder r=new ReturnOrder();
							r.setObjValue(r, refund);
							r.setFieldValue(r, "returnItemList", orderlist);
							System.out.println("���鳤��"+r.getReturnItemList().size());
							String express_company = "";
//							String express_company=responseproduct.getJSONArray("refund_list").getJSONObject(j).getJSONObject("express_company").optString("e_name");
							for(Iterator ito=r.getReturnItemList().getRelationData().iterator();ito.hasNext();)
							{
							ReturnOrderItem item=(ReturnOrderItem) ito.next();
							//���ʱ��
							Log.info("����ID:"+r.getOrder_sn()+" �˻�״̬:"+r.getRefund_state()+" �˻�ʱ������:"+Formatter.format(new Date(r.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT));
					
							/*
							 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
							 *2��ɾ���ȴ���Ҹ���ʱ��������� 
							*/		

							if (!OrderManager.RefundIntfExists("�����˹���˻���", conn, r.getOrder_sn(),r.getRefund_sn()))
							{
								OrderUtils.createRefund(conn,r,item,
										Integer.valueOf(Params.tradecontactid).intValue(),express_company);
							
							}
									
							}
							//����ͬ����������ʱ��
			                if (new Date(r.getAdd_time()*1000L).compareTo(modified)>0)
			                {
			                	modified=new Date(r.getAdd_time()*1000L);
			                }
						}catch(Exception e){
							Log.error(jobName, e.getMessage());
							continue;
						}
						
					}
						
						
						
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(i/20.0))).intValue()) break;
					
					pageno++;
					
					i=i+1;
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{	
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            		System.out.println("�����˻�����ȡʱ��"+value);
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
