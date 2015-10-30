package com.wofu.ecommerce.qqbuy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import java.util.Properties;

import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.timer.TimerJob;
import com.wofu.base.job.Executer;
public class CheckOrderExecuter extends Executer {

	private String dbname="";
	
	private String jobname="";
	
	private static long daymillis=24*60*60*1000L;
	
	private String orderStatus = "" ;
	
	private String accessToken = "" ;
	
	private String appOAuthID = "" ;
	
	private String secretOAuthKey = "" ;
	
	private String cooperatorId = "" ;
	
	private String uin = "" ;
	
	private String encoding = "" ;
	
	private String format = "" ;
	
	private String tradecontactid = "" ;
	
	private String username = "" ;
	
	private boolean isNeedInvoice = false ;
	
	private Date nextactive=null;
	

	@Override
	public void execute() throws Exception {
		TimerJob job=(TimerJob) this.getExecuteobj();
		Properties prop=StringUtil.getStringProperties(job.getParams());

		dbname=prop.getProperty("dbname");
		jobname = prop.getProperty("jobname") ;
		orderStatus=prop.getProperty("orderStatus") ;
		accessToken=prop.getProperty("accessToken") ;
		appOAuthID=prop.getProperty("appOAuthID") ;
		secretOAuthKey=prop.getProperty("secretOAuthKey") ;
		cooperatorId=prop.getProperty("cooperatorId") ;
		uin=prop.getProperty("uin") ;
		encoding=prop.getProperty("encoding") ;
		format=prop.getProperty("format") ;
		tradecontactid=prop.getProperty("tradecontactid") ;
		username=prop.getProperty("username") ;
		isNeedInvoice = Boolean.parseBoolean(prop.getProperty("isNeedInvoice","false")) ;
		
		nextactive=job.getNextactive();

		Connection conn=null;
		try 
		{			 
			conn= PoolHelper.getInstance().getConnection(dbname);
			checkcOrders(conn) ;
		}catch (Exception e) 
		{
			Log.error("���QQ����δ�붩��","������Ϣ:" + e.getMessage());
			throw new JException("����Զ�̷���ʧ��,������Ϣ:" + e.getMessage());
			
		} finally 
		{
			try 
			{
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				throw new JException("�ر����ݿ�����ʧ��");
			}
		}
	}
	
	private void checkcOrders(Connection conn) throws Exception
	{
		int orderCount = 0 ;
		for (int i=0;i<100;)
		{
			try
			{
				//ȡ��ǰʱ��Ϊ����ʱ�䣬ȡ��ǰʱ��ǰ7���ڵĴ����ⶩ��������Ƿ���©��
				Date endDate = new Date() ;
				Date startDate = new Date(endDate.getTime()-7*daymillis) ;
				String startTime = Formatter.format(startDate, Formatter.DATE_TIME_FORMAT) ;
				String endTime = Formatter.format(endDate, Formatter.DATE_TIME_FORMAT) ;
				Log.info("startTime="+startTime) ;
				Log.info("endTime="+endTime) ;
				
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("accessToken", accessToken) ;
				params.put("appOAuthID", appOAuthID) ;
				params.put("secretOAuthKey", secretOAuthKey) ;
				params.put("cooperatorId", cooperatorId) ;
				params.put("uin", uin) ;
				params.put("encoding", encoding) ;
				params.put("format", format) ;
				
				ArrayList<Hashtable<String, String>> orderIdList = new ArrayList<Hashtable<String,String>>() ;
				String orderStateArray[] = orderStatus.split(":") ;
				for(int k = 0 ; k < orderStateArray.length ; k++)
					orderIdList.addAll(OrderUtils.getOrderIdList(jobname, orderStateArray[k], "UPDATE", startTime, endTime, params)) ;
				
				//����ÿ������
				for(int j = 0 ; j < orderIdList.size() ; j++)
				{
					Hashtable<String, String> ht = orderIdList.get(j) ;
					String orderID = ht.get("dealId") ;
					String lastUpdateTime = ht.get("lastUpdateTime") ;
					Order order = OrderUtils.getOrderByID(jobname, orderID, params) ;
					if(order == null)
					{
						Log.error(jobname, "��ѯQQ����������ϸ��Ϣʧ��,�����š�"+ orderID +"��") ;
						return ;
					}
					String state = order.getDealState() ;
					Log.info("�����š�"+ orderID +"��,״̬��"+ state +"��,����޸�ʱ�䡾"+ lastUpdateTime +"��") ;
					//�ȴ���Ҹ���
					if("STATE_POL_WAIT_PAY".equals(state))
					{
						for(int k=0;k<order.getItemList().size();k++)
						{
							OrderItem item = order.getItemList().get(k) ;
							String sku = item.getSkuLocalCode() ;
							long qty = item.getBuyNum() ;
							StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, order.getDealId(), sku, qty);
							StockManager.addSynReduceStore(jobname, conn, tradecontactid, order.getDealState(),order.getDealId(), sku, -qty,false);
						}
					}
					//�ȴ�����
					else if("STATE_WAIT_SHIPPING".equalsIgnoreCase(state))
					{
						if(OrderUtils.createInterOrder(conn, order, tradecontactid, username, state,lastUpdateTime,isNeedInvoice))
						{
							//���������ɹ�����������
							for(int k=0;k<order.getItemList().size();k++)
							{
								OrderItem item = order.getItemList().get(k) ;
								String sku = item.getSkuLocalCode() ;
								StockManager.deleteWaitPayStock(jobname, conn,tradecontactid, order.getDealId(),sku);
							}
							orderCount ++ ;
						}
					}
				}
				
				Log.info("���QQ����δ�붩���ɹ����������붩������"+orderCount) ;
				i=100 ;
			}
			catch(Exception e)
			{
				if (++i >= 100)
					throw e;
				Log.warn("Զ������ʧ��[" + i + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				e.printStackTrace() ;
				Thread.sleep(10000L);
			}
		}
	}
		
}
