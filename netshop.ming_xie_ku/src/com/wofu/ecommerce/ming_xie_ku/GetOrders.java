package com.wofu.ecommerce.ming_xie_ku;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;

public class GetOrders extends Thread
{
	private static String jobName = "��ȡ��Ь�ⶩ����ҵ";
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";  //Parmas���Ǵ������ط����ƹ����ģ��Ѿ������޸�
	private static long daymillis=24*60*60*1000L;
	private boolean is_importing=false;
	private String lasttime;
	private static String  orderLineStatus="1";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public void run() 
	{

		System.out.println();
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do 
		{
			Connection connection = null;
			is_importing = true;
			try 
			{
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.ming_xie_ku.Params.dbname); //���ݿ�����ʱ����
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);  //��ʱû����
			} catch (Exception e)
			{
				try 
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) 
				{
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				is_importing = false;
				try
				{
					if (connection != null) connection.close();
				} catch (Exception e)
				{
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long current = System.currentTimeMillis();
			while(System.currentTimeMillis()-current<Params.waittime*1000){
				try {
					Thread.sleep(100L);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (true);
//		super.run();
	}
	
	
	/*****�����̻�ȡ������ϸ��Ϣ*****/
	public void getOrderList(Connection conn) throws Throwable
	{
		JSONObject responseResult=new JSONObject();
		Date now=new Date();
		String method="scn.vendor.order.full.get";
		String ver=Params.ver;
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
					/***data����***/
					JSONObject data=new JSONObject();
					//��Ҫ���ص��ֶΣ�
					data.put("Fields","seller_id, suggest_express_no, is_cod, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair");	
					/**���¶����Ǳ����**/
//					data.putOpt("StartUpdateDate", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)); //�������¿�ʼʱ��
//					data.putOpt("EndUpdateDate", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));   //�������½���ʱ��
//					data.putOpt("StartSubmitDate","2015-09-01 00:00:01");
//					data.putOpt("EndSubmitDate", "2015-09-11 00:00:01");
					data.putOpt("StartSubmitDate", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)); //�����ύ��ʼʱ��
					data.putOpt("EndSubmitDate", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));   //�����ύ����ʱ��
//					data.putOpt("SellerId", Params.SellerId);        //������ID(�ⲿ�̼Ҹ�ֵ����)
//					data.putOpt("SellerOrderNo", Params.SellerOrderNo);   //�����̶����� 	
//					data.putOpt("VendorOrderNo", Params.VendorOrderNo);   //�����̶�����
//					data.putOpt("OrderStatus", Integer.parseInt(orderLineStatus));     //����״̬(1-δ���� 2-��ȷ�� 3-�ѷ��� 4-������)
//					data.putOpt("PageNo", pageno);          //ҳ��
//					data.putOpt("PageSize", "50");        //ÿҳ������Ĭ��40�����100
					/**sign����***/
					String sign=Utils.get_sign(Params.app_Secret,Params.app_key,data, method,now,Params.ver,Params.format);
					/***�ϲ�Ϊ������****/
					String output_to_server=Utils.post_data_process(method, data, Params.app_key,now, sign).toString();
					System.out.println("����Ĳ���"+output_to_server);
					String responseOrderListData=Utils.sendByPost(Params.url, output_to_server);
					
//					Log.info(output_to_server);
					Log.info("�������󷵻ص���Ϣ"+responseOrderListData);
					responseResult=new JSONObject(responseOrderListData);    //������Ϣ
					//JSONObject Result = responseResult.getJSONArray("Result").getJSONObject(0);   //һ�ݶ�����������ϸ��Ϣ
//					for(int j=0;j<responseResult.getInt("TotalResults");j++)
//					{
//						
//					}
					String errdesc="";
					if(!responseResult.get("ErrCode").equals(null) || !responseResult.get("ErrMsg").equals(null))
					{
						errdesc=errdesc+" "+responseResult.get("ErrCode").toString()+" "+responseResult.get("ErrMsg").toString(); 
					}
					if (responseResult.getInt("TotalResults")==0)  //�����б���Ϣ�����ڣ���¼����һ����û��
					{
						if (pageno==1L)
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
						Log.error(jobName, /*"ȡ�����б�ʧ��:"*/"û�иö����������ѷ�������û�ж�����Ҫ����:"+errdesc);
					}
					k=10;
					break;
				}
				
				int totalCount=responseResult.getInt("TotalResults");
				
				if(!responseResult.get("ErrCode").equals(null) || !responseResult.get("ErrMsg").equals(null))
				{
					String errdesc="";
					errdesc=errdesc+" "+responseResult.get("ErrCode").toString()+" "+responseResult.get("ErrMsg").toString(); 
					Log.error(jobName, errdesc);
					k=10;
					break;
				}
				if(responseResult.getInt("TotalResults")==0)
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
				JSONArray orderlist=responseResult.getJSONArray("Result");
				Log.info("��������: "+orderlist.length());
				for(int j=0;j<orderlist.length();j++)
				{	
					Log.info("��"+j+"������");
					JSONObject order=orderlist.getJSONObject(j);
					Order o=OrderUtils.getOrderByID(Params.app_Secret,order.getString("VendorOrderNo"),Params.app_key,Params.ver,Params.format);
					/**�۲��Ƿ���Եõ�order����**/
					Log.info("orderId: "+o.getVendorOrderNo());
					System.out.println("�Ƿ��������:"+o.getIsCod()+"     ��ݹ�˾:"+o.getSuggestExpress()+"    ��ݺ�:"+o.getSuggestExpressNo());
					Log.info(o.getVendorOrderNo()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateDate(),Formatter.DATE_TIME_FORMAT));
					 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
					String sku;
					String sql="";
					if(o.getOrderStatus()==1/*||o.getOrderStatus()==2||o.getOrderStatus()==3||o.getOrderStatus()==4*/) //����״̬(1-δ���� 2-��ȷ�� 3-�ѷ��� 4-������)
					{
						if (!OrderManager.isCheck("�����Ь�ⶩ��", conn, o.getVendorOrderNo()))
						{
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date getUpdateDate_Date_type = null;
							try {
								getUpdateDate_Date_type = format.parse(o.getUpdateDate());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (!OrderManager.TidLastModifyIntfExists("�����Ь�ⶩ��", conn, o.getVendorOrderNo(),getUpdateDate_Date_type))
							{
								OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									sku=item.getVendorSkuId();
									StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku);
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
								}
							}
							//�ȴ���Ҹ���ʱ��¼�������
						}
					}
						else if(o.getOrderStatus()==2||o.getOrderStatus()==3)
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								/**�������Ƿ������е�������֣�������**/
//								System.out.println("�������Ƿ������е��������:");
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku);
								if(StockManager.WaitPayStockExists(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
								/**���δ�����Ķ����Ƿ��������д�����ݿ�**/
//								System.out.println(sku);
							}
						}
						//�����Ժ��û��˿�ɹ��������Զ��ر�
						//�ͷſ��,����Ϊ����		
						else if(o.getOrderStatus()==4) //4-������
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku);
								if(StockManager.WaitPayStockExists(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku))
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
							}
						}
						else if(o.getOrderStatus()==2)  //2-��ȷ��
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku);
							}
						}
						//����ͬ����������ʱ��
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date getUpdateDate_date = null;
						try 
						{
							getUpdateDate_date = format.parse(o.getUpdateDate());
						} 
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						if(getUpdateDate_date.compareTo(modified)>0)
						{
							modified=getUpdateDate_date;
						}
						
					}
					if(modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
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
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					pageno++;
				//����������
			}catch (Exception e) 
			{
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				e.printStackTrace();
				Thread.sleep(10000L);
			}

		}

	}
	
	@Override
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
	
}