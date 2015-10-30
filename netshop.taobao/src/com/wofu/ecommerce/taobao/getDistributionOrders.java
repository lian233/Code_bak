package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.PurchaseOrder;
import com.taobao.api.domain.SubPurchaseOrder;
import com.taobao.api.request.FenxiaoOrdersGetRequest;
import com.taobao.api.response.FenxiaoOrdersGetResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class getDistributionOrders extends Thread {

	private static String jobname = "��ȡ�Ա�����������ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private  String lasttimeconfvalue=Params.username+"����ȡ��������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public getDistributionOrders() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			
				getPurchaseOrderList(connection);
			} catch (Exception e) {
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))		
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
	private void getPurchaseOrderList(Connection conn) throws Exception
	{		
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret,"xml");
				FenxiaoOrdersGetRequest req=new FenxiaoOrdersGetRequest();		
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartCreated(startdate);
				req.setEndCreated(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				req.setTimeType("update_time_type");
				FenxiaoOrdersGetResponse response = client.execute(req ,Params.authcode);

			
				int i=1;
			
				while(true)
				{
								
					if (response.getPurchaseOrders()==null || response.getPurchaseOrders().size()<=0)
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
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						break;
					}
					
					for(Iterator it=response.getPurchaseOrders().iterator();it.hasNext();)
					{
						PurchaseOrder po=(PurchaseOrder) it.next();
						
												
						Log.info(po.getId()+" "+po.getStatus()+" "+Formatter.format(po.getModified(),Formatter.DATE_TIME_FORMAT));
							
						String sku;
						String sql="";
						
						
						if (po.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
						{	
						
							if (!OrderManager.isCheck("����Ա�����", conn, String.valueOf(po.getId())))
							{
								if (!OrderManager.TidLastModifyIntfExists("����Ա�����", conn, String.valueOf(po.getId()),po.getModified()))
								{
								
									OrderUtils.createDistributionOrder(jobname,conn,po,Params.tradecontactid);
									
									for(Iterator ito=po.getSubPurchaseOrders().iterator();ito.hasNext();)
									{
										SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
										sku=o.getSkuOuterId();
									
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(po.getTcOrderId()),sku);		
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, po.getStatus(),String.valueOf(po.getTcOrderId()), sku, -o.getNum(),false);
									}
								}
							}
							
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						else if (po.getStatus().equals("WAIT_BUYER_PAY"))
						{						
							for(Iterator ito=po.getSubPurchaseOrders().iterator();ito.hasNext();)
							{
								SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
								sku=o.getSkuOuterId();
								
							
							
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(po.getTcOrderId()), sku, o.getNum());
								
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, po.getStatus(),String.valueOf(po.getTcOrderId()), sku, -o.getNum(),false);
								
							}
												
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����
						} else if (po.getStatus().equals("TRADE_CLOSED"))
						{					
							OrderManager.CancelOrderByCID(jobname, conn, String.valueOf(po.getId()));
							for(Iterator ito=po.getSubPurchaseOrders().iterator();ito.hasNext();)
							{
								SubPurchaseOrder o=(SubPurchaseOrder) ito.next();		
								sku=o.getSkuOuterId();
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(po.getTcOrderId()), sku);
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, po.getStatus(),String.valueOf(po.getTcOrderId()),sku, o.getNum(),false);
							}
							//������ǰ�����һ���������رս���
							//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
						}else if (po.getStatus().equals("TRADE_FINISHED"))
						{
							for(Iterator ito=po.getSubPurchaseOrders().iterator();ito.hasNext();)
							{
								SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
								sku=o.getSkuOuterId();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(po.getTcOrderId()), sku);								
							}
						}
						
						//�����˻�
						for(Iterator ito=po.getSubPurchaseOrders().iterator();ito.hasNext();)
						{
							SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
							sku=o.getSkuOuterId();
							
							if (o.getStatus().equals("TRADE_REFUNDED")||o.getStatus().equals("TRADE_REFUNDING"))
							{
								try {
									OrderUtils.getDistributeRefund(jobname,conn,Params.url,Params.appkey,
											Params.appsecret,Params.authcode,Params.tradecontactid,po,o);
								} catch (Exception e) {
									Log.error(">�����˻�<"+po.getId(),e.getMessage());
								}
							}
						}
						
						//����ͬ����������ʱ��
		                if (po.getModified().compareTo(modified)>0)
		                {
		                	modified=po.getModified();
		                }
					}
				
					if (pageno==(Double.valueOf(Math.ceil(response.getTotalResults()/40.0))).intValue()) break;
					
					pageno++;
					req.setPageNo(pageno);
					response=client.execute(req , Params.authcode);
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
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
				break;
			}catch (JException e) {
				
				throw e;
				
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
