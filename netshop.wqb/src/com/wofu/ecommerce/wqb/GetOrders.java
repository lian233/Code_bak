package com.wofu.ecommerce.wqb;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.wqb.utils.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class GetOrders extends Thread {

	private static String jobname = "��ȡ������������ҵ";
	
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
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.wqb.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.wqb.Params.waittime * 1000))		
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
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					String method ="IOpenAPI.GetSaleStock";
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
					orderlistparams.put("user", Params.app_key);
					orderlistparams.put("appKey", Params.app_key);
			        orderlistparams.put("format", Params.format);
			        orderlistparams.put("method", method);
			        orderlistparams.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("endTime", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        
//			        orderlistparams.put("startTime", "2015-10-07 15:01:46");
//			        orderlistparams.put("endTime", "2015-10-07 16:00:46");
			        orderlistparams.put("status", "0");//״̬0��������
			        orderlistparams.put("pageIndex", String.valueOf(pageno));
			        orderlistparams.put("pageSize", Params.pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams,Params.app_secret,method, Params.url).replace("\"ReceiptSpec\":null","\"ReceiptSpec\":[]");
					System.out.println("�������"+orderlistparams);
					Log.info("responseOrderListData: "+responseOrderListData);
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					if (!"101".equals(responseproduct.optString("Code")))
					{
						String errdesc=responseproduct.optString("Message");
						Log.error(jobname, "ȡ�����б�ʧ��:"+errdesc);
						if("�Ҳ���������ĳ��ⵥ��Ϣ".equals(errdesc)){
							if(pageno==1l){
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
						}
						k=10;
						break;
					}
	
					int totalCount=responseproduct.getInt("SumNum");
					
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
					JSONArray orderlist=responseproduct.getJSONArray("Result");
					Log.info("��������: "+orderlist.length());
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						JSONArray items = order.getJSONArray("ProSpec");
						JSONArray spec = order.getJSONArray("ReceiptSpec");
						Order o=new Order();
						o.setObjValue(o, order);
						o.setFieldValue(o,"proSpec",items);
						o.setFieldValue(o,"receiptSpec",spec);
						
						Log.info("������"+o.getOrderId()+"����״̬"+o.getStockOrder_Flag()+"����ʱ��"+Formatter.format(o.getAddTime(),Formatter.DATE_TIME_FORMAT));
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						 		
						String sku;
						String sql="";
						try{
							if (o.getStockOrder_Flag().equals("����"))
							{	
								
								if (!OrderManager.isCheck("�������������", conn, o.getOrderId()))
								{
									if (!OrderManager.TidLastModifyIntfExists("�������������", conn, o.getOrderId(),o.getAddTime()))
									{
										OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
										
										for(Iterator ito=o.getProSpec().getRelationData().iterator();ito.hasNext();)
										{
											ProSpec item=(ProSpec) ito.next();
											sku=item.getProSku();
											
											//StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderNo(),sku);
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStockOrder_Flag(),o.getOrderId(), sku, -item.getProCount(),false);
										}
									}
								}
		
								//�ȴ���Ҹ���ʱ��¼�������
							}
						}catch(Exception e){
							Log.error(jobname, e.getMessage());
							continue;
						}
						
						//����ͬ����������ʱ��
		                if (o.getAddTime().compareTo(modified)>0)
		                {
		                	modified=o.getAddTime();
		                }
					}
						
					
					int totalPage = totalCount % Integer.parseInt(Params.pageSize)==0?totalCount / Integer.parseInt(Params.pageSize):totalCount>Integer.parseInt(Params.pageSize)?totalCount/Integer.parseInt(Params.pageSize):1;
					Log.info("totalPage: "+totalPage);
					//�ж��Ƿ�����һҳ
					if (pageno>=totalPage) break;
					
					pageno++;
					
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
	
	/*
	 * ��ȡһ��֮��δ���������ж���
	 */
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
