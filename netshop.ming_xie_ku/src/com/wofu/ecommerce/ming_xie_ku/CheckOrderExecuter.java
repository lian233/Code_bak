package com.wofu.ecommerce.ming_xie_ku;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer 
{

	private String tradecontactid="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="�����Ь�ⶩ��";
	private static String url="�����Ь�ⶩ��";
	private static String app_key="�����Ь�ⶩ��";
	private static String app_Secret="�����Ь�ⶩ��";
	private static String format="�����Ь�ⶩ��";
	private static String ver="�����Ь�ⶩ��";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url","18");
		app_key=prop.getProperty("app_key","18");
		app_Secret=prop.getProperty("app_Secret","18");
		tradecontactid=prop.getProperty("tradecontactid","18");
		format=prop.getProperty("format","18");
		ver=prop.getProperty("ver","18");

		try {		
			
			updateJobFlag(1);
	
			getOrderList();
			
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"���´����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
	
	
	}

	
	/*
	 * ��ȡһ��֮������ж���
	 */
	private void getOrderList() throws Exception
	{		
		UTF8_transformer utf8_transformer=new UTF8_transformer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		Date now=new Date();
		String method="scn.vendor.order.full.get";
		String ver=Params.ver;
		long pageno=1L;
		//Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					/***data����***/
					JSONObject data=new JSONObject();
					//��Ҫ���ص��ֶΣ�
					data.put("Fields","seller_id, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair");	
					/**���¶����Ǳ����**/
					data.put("PageNo", String.valueOf(pageno));          //ҳ��
					/**sign����***/
					String sign=Utils.get_sign(app_Secret,app_key,data, method, now,ver,format);
					/***�ϲ�Ϊ������****/
					String output_to_server=Utils.post_data_process(method, data, app_key,now, sign).toString();
					
			        String responseOrderListData = Utils.sendByPost(url,output_to_server);
					
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					if(!responseproduct.get("ErrCode").equals(null) || !responseproduct.get("ErrMsg").equals(null))
					{
						String errdesc="";
						errdesc=errdesc+" "+responseproduct.get("ErrCode").toString()+" "+responseproduct.get("ErrMsg").toString(); 
						
						Log.error(username, "ȡ�����б�ʧ��:"+errdesc);
						k=10;
						break;
					}
					int totalCount=responseproduct.getInt("TotalResults");
					if(!responseproduct.get("ErrCode").equals(null) || !responseproduct.get("ErrMsg").equals(null))

					{
						String errdesc="";
						JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
						for(int j=0;j<errlist.length();j++)
						{
							JSONObject errinfo=errlist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
					}
					
										
					
					int i=1;
			
			
								
					if (responseproduct.getInt("TotalResults")==0)
					{									
						k=10;
						break;
					}
					
					
					JSONArray orderlist=responseproduct.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets");
					
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						/***data����***/
						data=new JSONObject();
						//��Ҫ���ص��ֶΣ�
						data.put("Fields","seller_id, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair");	
						/**���¶����Ǳ����**/
						data.put("VendorOrderNo", order.getString("VendorOrderNo"));   //�����̶�����
						/**sign����***/
						sign=Utils.get_sign(app_Secret,app_key,data, method, now,ver,format);
						/***�ϲ�Ϊ������****/
						output_to_server=Utils.post_data_process(method, data, app_key,now, sign).toString();
						
						String responseOrderData = Utils.sendByPost(url,output_to_server);
						
						responseproduct=new JSONObject(responseOrderListData);

						JSONObject responseorder=new JSONObject(responseOrderData);
						
						if (responseorder.getBoolean("IsError"))
						{
							String errdesc = "";
							errdesc = errdesc + " "
									+ responseproduct.get("ErrCode").toString()
									+ " "
									+ responseproduct.get("ErrMsg").toString();

							Log.error(username, "ȡ�����б�ʧ��:" + errdesc);
							k = 10;
							break;						
						}
						
						
						JSONObject orderdetail=responseorder.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets").getJSONObject(j);
						
						
						Order o=new Order();
						o.setObjValue(o, orderdetail);
										
						
						JSONArray orderItemList=responseorder.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets");
						
						o.setFieldValue(o, "OrderDets", orderItemList);
						
				
						Log.info(o.getVendorOrderNo()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateDate(),Formatter.DATE_TIME_FORMAT));
						/*
						 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 *2��ɾ���ȴ���Ҹ���ʱ��������� 
						 */		
						String sku;
						String sql="";
						if(/*o.getOrderStatus()==1||*/o.getOrderStatus()==2||o.getOrderStatus()==3/*||o.getOrderStatus()==4*/) //����״̬(1-δ���� 2-��ȷ�� 3-�ѷ��� 4-������)
						{	
							
							if (!OrderManager.isCheck("�����Ь�ⶩ��", this.getDao().getConnection(), o.getVendorOrderNo()/*getOrderCode()*/))
							{
								if (!OrderManager.TidLastModifyIntfExists("�����Ь�ⶩ��", this.getDao().getConnection(), o.getVendorOrderNo(),new Date(o.getUpdateDate())))
								{
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getVendorSkuId();
										
										StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getVendorOrderNo(),sku);
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, String.valueOf(o.getOrderStatus()),o.getVendorOrderNo(), sku, -0,false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						
						else if (o.getOrderStatus()==1)
						{						
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
							
								StockManager.addWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getVendorSkuId(), sku,Integer.parseInt(item.getVendorSkuId())/*getOrderItemNum()*/);
								StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, String.valueOf(o.getOrderStatus()),o.getVendorOrderNo(), sku, Integer.parseInt(item.getVendorSkuId()),false);
							}
							
							 
				  
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����						
						}						else if(o.getOrderStatus()==1)//1-δ����
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku);
								if(StockManager.WaitPayStockExists(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobName, this.getDao(), tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
								
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
								StockManager.deleteWaitPayStock(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku);
								if(StockManager.WaitPayStockExists(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku))
									StockManager.addSynReduceStore(jobName, this.getDao(), tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
							}
						}
						else if(o.getOrderStatus()==2)  //2-��ȷ��
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku);
							}
						}
					
					}
						
						
						
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					pageno++;
					
					i=i+1;
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
	

}
