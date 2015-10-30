package com.wofu.ecommerce.lefeng;


import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.Coded;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;

import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class getOrders extends Thread {

	private static String jobname = "��ȡ�ַ嶩����ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static String methodApi="sellerSearchDealList";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public getOrders() {
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
						com.wofu.ecommerce.lefeng.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				
			
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.lefeng.Params.waittime * 1000))		
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
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		while(hasNextPage)
		{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("shopId", Params.shopid) ;
			Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
			Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
			params.put("createTimeStart",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT)) ;
			params.put("createTimeEnd", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));
			params.put("pageNo", String.valueOf(pageIndex)) ;
			params.put("pageSize", "40") ;
	
			String sign=LefengUtil.getSign(params, methodApi, Params.secretKey, Params.encoding);
			
			params.put("sign", sign);
			
		

			String responseText = LefengUtil.filterResponseText(CommHelper.sendRequest(Params.url+methodApi+".htm",params,"",Params.encoding));

			
			responseText=StringUtil.replace(responseText, "null", "\"\"");

			
			JSONObject jo = new JSONObject(responseText);
			
			int retcode=jo.optInt("result");
			
			if (retcode!=0)
			{
				hasNextPage = false ;
				if (retcode==7171)
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
					Log.info("ȡ����ʧ��,�����ڶ�����Ϣ��");
				}
				else
					Log.warn("ȡ����ʧ��,������Ϣ:"+LefengUtil.errList.get(retcode));
				break ;
			}
			
			int pageTotal=jo.optInt("pageTotal"); //��ҳ��
			pageIndex=jo.optInt("pageIndex");//��ǰҳ��
			
			JSONArray dealList=jo.optJSONArray("dealList");
			
			if (dealList.length()==0)
			{
				hasNextPage = false ;
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
				Log.info("��������Ҫ����Ķ���!");
				break ;
			}
			
			boolean isNeedDealList=false;
			for(int i=0;i<dealList.length();i++)
			{
				JSONObject deal=dealList.getJSONObject(i);
				
			
				Order o=new Order();
				o.setObjValue(o, deal);
		
				//ÿ�ζ�ֻ��ȡһ��������ݣ������ظ�����������ʱ��С�ڵ������´���ʱ��ʱ����
				if (Formatter.parseDate(o.getCreateTime(), Formatter.DATE_TIME_FORMAT).compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))<=0)					
					continue;
		
				Log.info(o.getOrderCode()+" "+LefengUtil.getStatusName(o.getOrderStatus())+" "+o.getCreateTime());
				
				isNeedDealList=true;
				
				/*
				 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
				 *2��ɾ���ȴ���Ҹ���ʱ��������� 
				 */		
				String sku;
				String sql="";
				if (o.getOrderStatus()==6 || o.getOrderStatus()==3)
				{	
					
					if (!OrderManager.isCheck("����ַ嶩��", conn, o.getOrderCode()))
					{
						if (!OrderManager.TidLastModifyIntfExists("����ַ嶩��", conn, o.getOrderCode(),Formatter.parseDate(o.getCreateTime(),Formatter.DATE_TIME_FORMAT)))
						{
							OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
							
							for(Iterator ito=o.getItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getItemCode();
							
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(),sku);
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()),o.getOrderCode(), sku, -item.getItemQuantity(),false);
							}
						}
					}

					//�ȴ���Ҹ���ʱ��¼�������
				}
				
				
				else if (o.getOrderStatus()==2)
				{						
					for(Iterator ito=o.getItemList().getRelationData().iterator();ito.hasNext();)
					{
						OrderItem item=(OrderItem) ito.next();
						sku=item.getItemCode();
					
						StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid,  o.getOrderCode(),sku,item.getItemQuantity());
						StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()),o.getOrderCode(), sku, -item.getItemQuantity(),false);
					}
					//�����Ժ��û��˿�ɹ��������Զ��ر�
					//�ͷſ��,����Ϊ����
				} else if (o.getOrderStatus()==9)
				{					
					OrderManager.CancelOrderByCID(jobname, conn, String.valueOf(o.getOrderCode()));
					for(Iterator ito=o.getItemList().getRelationData().iterator();ito.hasNext();)
					{
						OrderItem item=(OrderItem) ito.next();
						sku=item.getItemCode();
						
						StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku);
						
					}

					//������ǰ�����һ���������رս���
					//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
				}else if (o.getOrderStatus()==5)
				{
					for(Iterator ito=o.getItemList().getRelationData().iterator();ito.hasNext();)
					{
						OrderItem item=(OrderItem) ito.next();
						sku=item.getItemCode();
			
						StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku);
						if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, o.getOrderCode(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
							StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()),o.getOrderCode(), sku, -item.getItemQuantity(),false);
					}					
		
				}
				else if (o.getOrderStatus()==8)
				{
					for(Iterator ito=o.getItemList().getRelationData().iterator();ito.hasNext();)
					{
						OrderItem item=(OrderItem) ito.next();
						sku=item.getItemCode();
			
						StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku);								
					}
	
				}
				
				
				
				//����ͬ����������ʱ��
                if (Formatter.parseDate(o.getCreateTime(),Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
                {
                	modified=Formatter.parseDate(o.getCreateTime(),Formatter.DATE_TIME_FORMAT);
                }
			}
			
			if (!isNeedDealList)
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
			}
		
			
			//�ж��Ƿ�����һҳ
			if(pageTotal>pageIndex)
				pageIndex ++ ;
			else
			{
				hasNextPage = false ;
				break;
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
				
		
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
