package com.wofu.ecommerce.miya;


import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.ParseException;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.miya.OrderItem;
import com.wofu.ecommerce.miya.OrderUtils;
import com.wofu.ecommerce.miya.Order;
import com.wofu.ecommerce.miya.Params;
import com.wofu.ecommerce.miya.utils.Utils;
import com.wolf.common.tools.util.JException;

public class GetOrders extends Thread {

	private static String jobname = "��ȡ�����߶�����ҵ";
	
	private static long daymillis=24*60*60*1000L;
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {		
				//��ȡ���ݿ�����
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.miya.Params.dbname);
				//��ȡ�ϴ�ץ������ʱ��
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				//��ʼץ��
				getOrderList(connection);
				
			} catch (Exception e) {
				try {
					//������ݿ����Ӳ�Ϊ�ղ������ݿ⻹û�ύCommit��
					if (connection != null && !connection.getAutoCommit())
						//ִ�лع�����
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					//������ݿ����Ӳ�Ϊ�գ���ô�ر����ݿ�����
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			//��java���л���
			System.gc();
			//�趨��ǰϵͳʱ��Ϊ��ʼ�ȴ�ʱ��
			long startwaittime = System.currentTimeMillis();
			//��ʼһ��ѭ����Ϊ�˵ȴ�ʱ��һ��ʱ���ٽ�����һ����������ǰϵͳʱ���ȥ��ʼʱ�����С���趨��ʱ�䣬��Ϊtrue��sleep1�롣
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.miya.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮������ж���
	 */
	private void getOrderList(Connection conn) throws Exception
	{	
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{	//�趨��ʼץ����ʱ��
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					//�趨����ץ����ʱ��
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//����һ��map��������KEY��VALUESE��
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
			        orderlistparams.put("method", "mia.orders.search");
					orderlistparams.put("vendor_key", Params.appid);
			        orderlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			        orderlistparams.put("version", Params.ver);
			        
			        //Ӧ�ü��������
			        //����״̬1.������2. �Ѹ��������3. ������4. �������5. ������� 6. ��ȡ��
			        orderlistparams.put("order_state", "2");
			        orderlistparams.put("time_range", "modified_time");
//			        orderlistparams.put("start_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
//			        orderlistparams.put("end_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("start_date", "2015-09-15 00:00:00");
			        orderlistparams.put("end_date", "2015-09-30 00:00:00");
			        orderlistparams.put("date_type", "2");//��ѯʱ�����ͣ�Ĭ�ϰ��޸�ʱ���ѯ��1Ϊ����������ʱ���ѯ����������Ϊ�������޸�ʱ�� ��
			        orderlistparams.put("page", String.valueOf(pageno));
			        orderlistparams.put("page_size", "20");
			        //��ϵͳ��������Ӧ�ü�����������,post�����ȥ����������˸��Ӽ��ܷ�����ÿ������ļ��ܷ�������һ����
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.secret, Params.url);
//					System.out.println("���� "+responseOrderListData);
					//��õ�responseOrderListData�ַ�����ת��Ϊjson����
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					System.out.println("��"+Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)+"��ʼץȡ����");
					//����json����
					int count=responseproduct.optInt("count");
					String message=responseproduct.optString("message");
					boolean success = responseproduct.optBoolean("success");
					if(!success){
						Log.error("ʧ�ܣ��˳�����ѭ��"+"������Ϣ��"+message, message);
						break;
					}
					if (count==0)
					{				
						if (pageno==1L)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
									PublicUtils.setConfig(conn, lasttimeconfvalue, value);
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
					
					JSONArray orderlist=responseproduct.getJSONArray("data");
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject data =orderlist.getJSONObject(j);
						Order o=new Order();
						o.setObjValue(o, data);
						OrderItem item=new OrderItem();
						JSONArray orderItemList =data.getJSONArray("item");
						Log.info("������Ϊ:"+o.getOid()+" ��"+pageno+"ҳ | ��������Ϊ"+orderlist.length()+" ��ǰ����Ϊ"+(j+1));				 		
						if (o.getStatus().equals("1"))
						{
							if (!OrderManager.isCheck("��鱴��������", conn, o.getOid()))
							{	
								if (!OrderManager.TidLastModifyIntfExists("��鱴��������", conn, o.getOid(),o.getModified_time()))
								{	
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,data);
									for(int i=0;i<orderItemList.length();i++)
									{	
										JSONObject orderItem =orderItemList.getJSONObject(i);
										item.setObjValue(item, orderItem);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getOid(), item.getOuter_id(), -item.getNum(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						//����ͬ����������ʱ��
		                if (o.getModified_time().compareTo(modified)>0)
		                {
		                	modified=o.getModified_time();
		                }
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(count/20.0))).intValue())
					{	
						break;
					}
					pageno++;
				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
					PublicUtils.setConfig(conn, lasttimeconfvalue, value);
				}
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	






	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
