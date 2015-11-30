package com.wofu.ecommerce.miya;


import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.ParseException;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.miya.OrderItem;
import com.wofu.ecommerce.miya.OrderUtils;
import com.wofu.ecommerce.miya.Order;
import com.wofu.ecommerce.miya.Params;
import com.wofu.ecommerce.miya.utils.Utils;
import com.wolf.common.tools.util.JException;

public class CheckOrder extends Thread {

	private static String jobname = "��ȡ��ѿ��������ҵ";
	
	private static long daymillis=5*60*60*1000L+12000L;
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
				sleep(100000L);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.miya.Params.waittime * 1000*60*2))
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
					Date startdate=new Date((new Date()).getTime()-daymillis);
					//�趨��ǰʱ�䣬������һ���жϣ��Ƿ����ʱ����ڵ�ǰʱ�䣬������ڣ��Ͱ����ڵ�ʱ�丳ֵ������ʱ��
					SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date=new Date();
					String endtime = dateFormater.format(date.getTime()-10000L);
					//����һ��map��������KEY��VALUESE��
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
			        orderlistparams.put("method", "mia.orders.search");
					orderlistparams.put("vendor_key", Params.vendor_key);
			        orderlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			        orderlistparams.put("version", Params.ver);
			        
			        //Ӧ�ü��������
			        //����״̬1.������2. �Ѹ��������3. ������4. �������5. ������� 6. ��ȡ��
			        orderlistparams.put("order_state", "2");
			        orderlistparams.put("start_date", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_date", endtime);
//			        orderlistparams.put("start_date", "2015-11-25 11:26:10");
//			        orderlistparams.put("end_date", "2015-11-25 15:27:50");
//			        orderlistparams.put("date_type", "0");//��ѯʱ�����ͣ�Ĭ�ϰ��޸�ʱ���ѯ��1Ϊ����������ʱ���ѯ����������Ϊ�������޸�ʱ�� ��
			        orderlistparams.put("page", String.valueOf(pageno));
			        orderlistparams.put("page_size", "20");
			        //��ϵͳ��������Ӧ�ü�����������,post�����ȥ����������˸��Ӽ��ܷ�����ÿ������ļ��ܷ�������һ����
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.secret_key, Params.url);
					Log.info(Utils.Unicode2GBK(responseOrderListData));
					//��õ�responseOrderListData�ַ�����ת��Ϊjson����
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					System.out.println("��"+Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)+"��ʼץȡ����"+"��"+endtime);
					//����json����
					String msg = responseproduct.optString("msg");
					int code = responseproduct.optInt("code");
					if(code!=200){
						Log.error("ʧ�ܣ��˳�����ѭ��,������Ϣ��", msg);
						break;
					}
					JSONObject orders_list_response=responseproduct.getJSONObject("content").getJSONObject("orders_list_response");
					int count=0;
					count=orders_list_response.optInt("total");
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
					
					
					JSONArray orderlist=orders_list_response.getJSONArray("order_list");
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject data =orderlist.getJSONObject(j);
						Order o=new Order();
						o.setObjValue(o, data);
						OrderItem item=new OrderItem();
						JSONArray orderItemList =data.getJSONArray("item_info_list");
						Log.info("������Ϊ:"+o.getOrder_id()+"�޸�ʱ��Ϊ:"+Formatter.format(o.getModify_time(), Formatter.DATE_TIME_FORMAT)+" ��"+pageno+"ҳ | ��������Ϊ"+orderlist.length()+" ��ǰ����Ϊ"+(j+1));
						if (o.getOrder_state().equals("2"))
						{
							//��ѿ��ⶩ��ר��

							if (!OrderManager.isCheck("�����ѿ����", conn, o.getOrder_id()))
							{
								String sql="select count(*) from ns_customerorder with(nolock) where TradeContactID = '"+Params.tradecontactid+"' and tid='"+o.getOrder_id()+"' ";
								if(SQLHelper.intSelect(conn, sql)==0)
								{
									if (!OrderManager.TidLastModifyIntfExists("�����ѿ����", conn, o.getOrder_id(),o.getModify_time()))
									{	
										OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,data);
										for(int i=0;i<orderItemList.length();i++)
										{	
											JSONObject orderItem =orderItemList.getJSONObject(i);
											item.setObjValue(item, orderItem);
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_state(),o.getOrder_id(), item.getSku_id(), -item.getItem_total(),false);
										}
									}
								}else{
									System.out.println("��ns�ҵ��ö�������");
								}
								
							}
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						//����ͬ����������ʱ��
		                if (o.getModify_time().compareTo(modified)>0)
		                {
		                	modified=o.getModify_time();
		                }
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(count/20.0))).intValue())
					{	
						break;
					}
					pageno++;
				}
//				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
//				{
//					System.out.println("��ѿץ����ɣ��޸�ץ��ʱ��Ϊ"+modified);
//					String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
//					PublicUtils.setConfig(conn, lasttimeconfvalue, value);
//				}
				System.out.println("ץ©�����");
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
