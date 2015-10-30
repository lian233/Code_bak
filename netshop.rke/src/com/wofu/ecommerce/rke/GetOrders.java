package com.wofu.ecommerce.rke;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke.utils.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class GetOrders extends Thread {
	private static String jobname = "��ȡ��˹������������ҵ";
	
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
				RKE.setCurrentDate_getOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.rke.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.rke.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮��δ���������ж���
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		int pageno=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		int orderCount =0;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					String method ="search_order_list";
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
					orderlistparams.put("api_version", Params.ver);
			        orderlistparams.put("act", method);
			        orderlistparams.put("last_modify_st_time", String.valueOf(startdate.getTime()/1000L));
			        orderlistparams.put("last_modify_en_time", String.valueOf(enddate.getTime()/1000L));
			        orderlistparams.put("pages", String.valueOf(pageno));
			        orderlistparams.put("counts", Params.pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.url);
					Log.info("responseOrderListData: "+responseOrderListData);
					
					Document doc = DOMHelper.newDocument(responseOrderListData, "gbk");
					Element ele = doc.getDocumentElement();
					String result = DOMHelper.getSubElementVauleByName(ele, "result");
					if (!"success".equals(result))
					{
						String errdesc=DOMHelper.getSubElementVauleByName(ele, "msg");
						if("0x003".equals(errdesc)){//û�ж�������
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
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
							Log.error(jobname, "ȡ�����б�ʧ��:"+errdesc);
							k=10;
							break;
						}
						Log.error(jobname, "ȡ�����б�ʧ��:"+errdesc);
						k=10;
						break;
					}
					Element info = DOMHelper.getSubElementsByName(ele, "info")[0];
					int totalCount=Integer.parseInt(DOMHelper.getSubElementVauleByName(info, "counts"));
					Log.debug("totalCount: "+totalCount);
					if (totalCount==0)
					{				
						if (pageno==1L)		
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
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
		
					Element[] orderList = DOMHelper.getSubElementsByName(ele,"item");
					for(int j=0;j<orderList.length;j++)
					{
						try{
							Element order=orderList[j];
							if(!DOMHelper.ElementIsExists(order, "order_id")) continue;
							Order o = OrderUtils.getOrderByElement(order);
							
							Log.info(o.getOrder_sn()+" ����״̬: "+o.getOrder_status()+"����״̬��"+o.getShipping_status()+"����״̬: "+o.getPay_status()+"����ʱ��: "+Formatter.format(o.getPay_time(),Formatter.DATE_TIME_FORMAT));
							//����״̬;0δȷ��1��ȷ��2��ȡ��3��Ч,4�˻�,5�ѷֵ�6���ֵַ�
							//֧��״̬;0δ����;1������;2�Ѹ��δ���ˣ�3�Ѹ���ѵ��ˣ�
							//����״̬;0δ����,1�ѷ���2���ջ�3�����4�ѷ�����������Ʒ��5������
							 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���getOrder_status()1 getPay_status()3
							 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
							 		
							String sku;
							if ("3".equals(o.getPay_status()) && "0".equals(o.getShipping_status()))
							{	
								
								if (!OrderManager.isCheck("�����˹����������", conn, o.getOrder_sn()))
								{
									if (!OrderManager.TidLastModifyIntfExists("�����˹����������", conn, o.getOrder_sn(),o.getPay_time()))
									{
										orderCount++;
										OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
										
										for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											sku=item.getProduct_sn();
											
											//StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sn(),sku);
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status(),o.getOrder_sn(), sku, -item.getGoods_number(),false);
										}
									}
								}
		
								//�ȴ���Ҹ���ʱ��¼�������
							}else if ("0".equals(o.getPay_status()))
							{	
								for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getProduct_sn();
											
										StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sn(),sku,item.getGoods_number());
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status(),o.getOrder_sn(), sku, -item.getGoods_number(),false);
									}
		
								//�ȴ���Ҹ���ʱ��¼�������
							}else if ("5".equals(o.getOrder_status()))
							{	
								for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getProduct_sn();
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sn(),sku);
									}
		
								//�ȴ���Ҹ���ʱ��¼�������
							}
							
							//����ͬ����������ʱ��
			                if (o.getPay_time().compareTo(modified)>0)
			                {
			                	modified=o.getPay_time();
			                }
						}catch(Exception ee){
							if(conn!=null && !conn.getAutoCommit()){
								conn.rollback();
								conn.setAutoCommit(true);
							}
							Log.error(jobname, ee.getMessage());
							continue;
								
						}
						
					}
					int totalPage = totalCount % Integer.parseInt(Params.pageSize)==0?totalCount / Integer.parseInt(Params.pageSize):totalCount>Integer.parseInt(Params.pageSize)?totalCount/Integer.parseInt(Params.pageSize):1;
					Log.debug("totalPage: "+totalPage);
					//�ж��Ƿ�����һҳ
					if (pageno>=totalPage) break;
					
					pageno++;
					
				}
				Log.info("orderCount: "+orderCount);
				if(orderCount==0){//һ��û��ȡ�������Ķ���
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
		                		Log.error(jobname, je.getMessage());
		                	}
						}
					}catch(ParseException e)
					{
						Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
					}
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
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
