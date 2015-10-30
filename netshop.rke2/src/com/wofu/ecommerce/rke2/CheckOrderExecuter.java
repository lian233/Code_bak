package com.wofu.ecommerce.rke2;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke2.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer {

	private String url="";
	private String pageSize="";

	
	
	private String ver="";

	private String tradecontactid="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="�����˹����������";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");
		username=prop.getProperty("username");
		pageSize=prop.getProperty("pageSize");
		try {		
			updateJobFlag(1);
	
			//getOrderList();
			
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
	/**
	private void getOrderList() throws Exception
	{		
		int pageno=1;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Map<String, String> orderlistparams = new HashMap<String, String>();
			    	Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
					orderlistparams.put("api_version", ver);
			        orderlistparams.put("act", "search_order_list");
			        orderlistparams.put("last_modify_st_time", String.valueOf(startdate.getTime()/1000L));
			        orderlistparams.put("last_modify_en_time", String.valueOf(enddate.getTime()/1000L));
			       
			        orderlistparams.put("pages", String.valueOf(pageno));
			        orderlistparams.put("counts", pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, url);
					//Log.info("responseOrderListData: "+responseOrderListData);
					
					Document doc = DOMHelper.newDocument(responseOrderListData, "GBK");
					Element ele = doc.getDocumentElement();
					String result = DOMHelper.getSubElementVauleByName(ele, "result");
					if (!"success".equals(result))
					{
						String errdesc=DOMHelper.getSubElementVauleByName(ele, "msg");
						Log.error(jobName, "ȡ�����б�ʧ��:"+errdesc);
						k=10;
						break;
					}
					Element info = DOMHelper.getSubElementsByName(ele, "info")[0];
					int totalCount=Integer.parseInt(DOMHelper.getSubElementVauleByName(info, "counts"));
					Log.info("totalCount: "+totalCount);
					
					if (totalCount==0)
					{				
						k=10;
						break;
					}
		
					Element[] orderList = DOMHelper.getSubElementsByName(ele,"item");
					
					for(int j=0;j<orderList.length;j++)
					{
						Element order=orderList[j];
						if(!DOMHelper.ElementIsExists(order, "order_id")) continue;
						Order o = OrderUtils.getOrderByElement(order);
						
						Log.info(o.getOrder_sn()+" ����״̬: "+o.getOrder_status()+" ����״̬: "+o.getPay_status()+"����ʱ��: "+Formatter.format(o.getPay_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						 		
						String sku;
						if ("2".equals(o.getPay_status()) && "0".equals(o.getShipping_status()))
						{	
							
							if (!OrderManager.isCheck("�����˹����������", this.getDao().getConnection(), o.getOrder_sn()))
							{
								if (!OrderManager.TidLastModifyIntfExists("�����˹����������", this.getDao().getConnection(), o.getOrder_sn(),o.getPay_time()))
								{
									//OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										//sku=item.getProduct_sn();
										//StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrder_status(),o.getOrder_sn(), sku, -item.getGoods_number(),false);
									}
								}
							}
	
						}
					}
					int totalPage = totalCount % Integer.parseInt(pageSize)==0?totalCount / Integer.parseInt(pageSize):totalCount>Integer.parseInt(pageSize)?totalCount/Integer.parseInt(pageSize):1;
					//�ж��Ƿ�����һҳ
					if (pageno>=totalPage) break;
					pageno++;
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
	**/

}
