package com.wofu.ecommerce.alibaba;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class getOrders extends Thread {

	private static String jobName = "��ȡ����ͰͶ�����ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private static String apiName="trade.order.orderList.get";
	
	private String lasttime;

	public void run() {
		
		Log.info(jobName, "����[" + jobName + "]ģ��");
		
		//��ȡ��Ȩ����
		Connection connection = null;
		do {		
			is_importing = true;
			String orgId=null;
			//�ı��ȡ�����ľ�̬ʱ�� 
			Alibaba.setCurrentDate(new Date());
			try {			
		    	Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("client_id", Params.appkey);
			    params.put("redirect_uri", Params.redirect_uri);
			    params.put("client_secret", Params.secretKey);
			    params.put("refresh_token", Params.refresh_token);
			    String returns=AuthService.refreshToken(Params.host, params);
			    //String returns=AuthService.getToken(Params.host, params,true);
			    JSONObject access=new JSONObject(returns);
		    	Params.token=access.getString("access_token");
		    	
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				if(orgId==null){
					String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
					orgId=SQLHelper.strSelect(connection,sql);
				}							
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			
				getOrderList(connection,orgId);
				
				
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮�ڵ����ж���
	 */
	private void getOrderList(Connection conn,String orgId) throws Throwable
	{		
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<5;)
		{
			try
			{
				while(hasNextPage)
				{
						Hashtable<String, String> params = new Hashtable<String, String>() ;
						params.put("sellerMemberId", Params.sellerMemberId) ;
						Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
						Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
						params.put("modifyStartTime",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT)) ;
						params.put("modifyEndTime", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));
						params.put("pageSize","20") ;
						params.put("pageNO", String.valueOf(pageIndex)) ;
						String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,apiName,Params.version,Params.requestmodel,Params.appkey);
						params.put("access_token", Params.token);
						String responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
						Log.info("ȡ������ʼʱ��:"+Formatter.format(startdate,Formatter.DATE_TIME_FORMAT));
						Log.info("ȡ������������Ϊ: "+responseText);
						JSONObject res=new JSONObject(responseText);
						JSONObject jo = res.getJSONObject("result");
						/**
						if (!jo.getBoolean("success"))
						{
							hasNextPage = false ;
							Log.warn(jobName,"ȡ����ʧ��,������Ϣ:"+res.optString("message"));
							break ;
						}**/
						
						//��ȡ������
						int total=jo.getInt("total");
						//��ҳ��
						int pageTotal=total%20==0?total/20==0?1:total/20:total/20+1;//Double.valueOf(Math.ceil(total/20)).intValue();
						Log.info("��ҳ��Ϊ: "+pageTotal);
						
						//���صĶ����б�����
						JSONArray jresult=jo.getJSONArray("toReturn");
						
						if(jresult.length()==0){
							
							if (pageIndex==1)		
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(Throwable je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
								
								Log.info(jobName,"��������Ҫ����Ķ���!");
								hasNextPage=false;								
								break ;
							}
						}
						
						boolean isNeedDealList=false;
						for(int i=0; i<jresult.length(); i++){
							JSONObject deal=jresult.getJSONObject(i);
							long id = deal.getLong("id");
							//��ȡ��������
							params = new Hashtable<String, String>() ;
							params.put("orderId", String.valueOf(id)) ;
							urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.orderDetail.get",Params.version,Params.requestmodel,Params.appkey);
							params.put("access_token", Params.token);
							responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
							Log.info("ȡ�������鷵������Ϊ: "+responseText);
							
							Order o=new Order();
							o.setObjValue(o, deal);
							OrderUtils.setOrderItemSKU(conn,o,orgId);//setsku
							
							//ÿ�ζ�ֻ��ȡһ��������ݣ������ظ�����������ʱ��С�ڵ������´���ʱ��ʱ����
							if (Formatter.parseDate(o.getGmtModified(), Formatter.DATE_TIME_FORMAT).compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))<=0)					
								continue;
						
							Log.info(o.getId()+" "+o.getStatus()+" "+o.getGmtModified());
							
							
							isNeedDealList=true;
							
							 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
							 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
							 		
							String sku;
							Double quantity;
							String sql="";
							if (o.getStatus().equals("waitsellersend"))
							{	
								
								if (!OrderManager.isCheck("��鰢��ͰͶ���", conn, String.valueOf(o.getId())))
								{	
									if (!OrderManager.TidLastModifyIntfExists("��鰢��ͰͶ���", conn, String.valueOf(o.getId()),Formatter.parseDate(o.getGmtModified(), Formatter.DATE_TIME_FORMAT)))
									{	
										//----------
										OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,Params.token,Params.appkey,Params.secretKey);
														
										for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											//����SKUID����ƷID�õ�sku
											sku=item.getSku();											
											quantity=item.getQuantity();
											
											StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, String.valueOf(o.getId()),sku);
											StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, -quantity.longValue(),false);
										}
									
									}
								}
		
							}
							//�ȴ���Ҹ���ʱ��¼�������
							else if (o.getStatus().equals("waitbuyerpay")){						
								for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									//����SKUID����ƷID�õ�sku
									sku=item.getSku();	
									quantity=item.getQuantity();
									
									StockManager.addWaitPayStock(jobName, conn,Params.tradecontactid,  String.valueOf(o.getId()),sku,quantity.longValue());
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, -quantity.longValue(),false);
								}
								
							}
							//������ǰ�����һ���������رս���
							//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
							else if (o.getStatus().equals("cancel"))
							{
								//���Ƶ�ȡ������ҲҪ����ϵͳ
								if (!OrderManager.TidLastModifyIntfExists("��鰢��ͰͶ���", conn, String.valueOf(o.getId()),Formatter.parseDate(o.getGmtModified(), Formatter.DATE_TIME_FORMAT)))
								{
									//---
									//OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,Params.token,Params.appkey,Params.secretKey);
									//ȡ������
									sql="declare @ret int; execute  @ret = IF_CancelCustomerOrder '" + o.getId() + "';select @ret ret;";									//ŷ�����
									int resultCode =SQLHelper.intSelect(conn, sql) ;
									//ȡ������ʧ��
									if(resultCode == 0)
									{
										Log.info("����δ���-ȡ���ɹ�,����:"+o.getId()+"");
									}else if(resultCode == 1)
									{
										Log.info("���������-�ص�,����:"+o.getId()+"");
									}else if(resultCode ==2)
									{
										Log.info("�����Ѿ�����-ȡ��ʧ��,����:"+o.getId()+"");
									}else if(resultCode ==3)
									{
										Log.info("���������ڻ���ȡ��-ȡ��ʧ��,����:"+o.getId()+"");
									}
									else
									{
										Log.info("ȡ��ʧ��,����:"+o.getId()+"");
									}
								}
								for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									//����SKUID����ƷID�õ�sku
									sku=item.getSku();	
									quantity=item.getQuantity();
									
									StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, String.valueOf(o.getId()), sku);
									if (StockManager.WaitPayStockExists(jobName,conn,Params.tradecontactid, String.valueOf(o.getId()), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
										StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, quantity.longValue(),false);
								}					
					
							}
							//�����
							else if (o.getStatus().equals("success")||o.getStatus().equals("signinsuccess"))
							{
								for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									//����SKUID����ƷID�õ�sku
									sku=item.getSku();	
						
									StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, String.valueOf(o.getId()), sku);								
								}
				
							}
							//����ͬ����������ʱ��
			                if (Formatter.parseDate(o.getGmtModified(),Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
			                {
			                	modified=Formatter.parseDate(o.getGmtModified(),Formatter.DATE_TIME_FORMAT);
			                }
							
						}//forδ
						
					//�ж��Ƿ�����һҳ
					if(pageTotal>pageIndex)
						pageIndex ++ ;
					else
					{
						hasNextPage = false ;
						break;
					}
					
					
				}//whileδ		
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
		        	{
		        		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
		        		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
		        	}catch(Throwable je)
		        	{
		        		Log.error(jobName,je.getMessage());
		        	}
				}
			break;
			} catch (Throwable e) {
				e.printStackTrace();
				if (++k >= 5)
					throw e;
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
		Log.info("���ζ����������!");
	}
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
	
	

}
