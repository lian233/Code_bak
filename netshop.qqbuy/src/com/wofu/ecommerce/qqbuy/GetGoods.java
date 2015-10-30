package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;

public class GetGoods extends Thread {

	private static String jobname = "��ȡQQ�����ϼ���Ʒ��ҵ";
	private static String lasttimeconfvalue=Params.username+"ȡ��Ʒ����ʱ��";
	private static long daymillis=24*60*60*1000L;
	
	private static String accessToken = Params.accessToken ;
	private static String appOAuthID = Params.appOAuthID ;
	private static String secretOAuthKey = Params.secretOAuthKey ;
	private static String cooperatorId = Params.cooperatorId ;
	private static String uin = Params.uin ;
	private static String encoding = Params.encoding ;
	private static String pageSize = Params.pageSize ;
	private static String format = Params.format ;
	private static String timeType = Params.timeType ;
	private static String orderState = Params.orderState ;
	private static String tradecontactid = Params.tradecontactid ;
	private static String username = Params.username ;
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;

	public GetGoods() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection conn = null;
			is_importing = true;
			try 
			{	
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				Date configTime = new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()) ;
				Date sTime=new Date(configTime.getTime()+1000L);
				Date eTime=new Date(configTime.getTime()+daymillis);
				String startTime = Formatter.format(sTime, Formatter.DATE_TIME_FORMAT);
				String endTime = Formatter.format(eTime, Formatter.DATE_TIME_FORMAT) ;
				
				//��Ʒ����޸�ʱ��
				Date lastmodifiedTime = sTime ;
				
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("accessToken", accessToken);
				params.put("appOAuthID", appOAuthID);
				params.put("cooperatorId", cooperatorId);
				params.put("secretOAuthKey", secretOAuthKey);
				params.put("uin", String.valueOf(uin));
				params.put("encoding", encoding);
				params.put("startTime", startTime);
				params.put("endTime", endTime);
				params.put("pageSize", pageSize);

				//��ȡ�˶�ʱ�����޸Ĺ�����Ʒ
				List<Goods> goodsList = StockUtils.getSkuList(jobname, params) ;			
				String sql = "" ;
				int orgid = 0 ;
				if(goodsList.size() > 0)
				{
					sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
					orgid=SQLHelper.intSelect(conn, sql);
				}
				
				for(int i = 0 ; i < goodsList.size() ; i ++)
				{
					Goods goods = goodsList.get(i) ;
					ArrayList<SkuInfo> stockList = goods.getStockList() ;
					SkuInfo skuInfo = stockList.get(0) ;
					//����STOCK_STATE_SELLING
					if("STOCK_STATE_SELLING".equalsIgnoreCase(skuInfo.getStockSaleState()))
					{
						sql="select count(*) from ecs_stockconfig with(nolock) where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"'";
						if (SQLHelper.intSelect(conn, sql) > 0)
						{
							sql="update ecs_stockconfig set status='1' where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"'" ;
							SQLHelper.executeSQL(conn, sql) ;
						}
						else
							StockManager.StockConfig(conn, skuInfo.getStockLocalcode(), Integer.parseInt(tradecontactid), 1) ;
					}
					else
					{
						//����ϼܲ�Ʒ�¼�,����״̬
						sql="select count(*) from ecs_stockconfig with(nolock) where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"' and status=1";
						if (SQLHelper.intSelect(conn, sql) > 0)
						{
							sql="update ecs_stockconfig set status='0' where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"' and status='1'" ;
							SQLHelper.executeSQL(conn, sql) ;
						}
					}
					//����ʱ��
					if(lastmodifiedTime.compareTo(goods.getLastUpdateTime()) < 0)
						lastmodifiedTime = goods.getLastUpdateTime() ;
				}
				try
				{
				   //�統ǰ����������죬��ȡ��Ʒ����ʱ�����Ϊ������ڶ�������
					if(lastmodifiedTime.compareTo(sTime) > 0)
					{
						String timeValue = Formatter.format(lastmodifiedTime, Formatter.DATE_TIME_FORMAT) ;
						PublicUtils.setConfig(conn, lasttimeconfvalue, timeValue) ;
					}
					else if (dateformat.parse(Formatter.format(new Date(),Formatter.DATE_FORMAT)).compareTo(dateformat.parse(Formatter.format(configTime,Formatter.DATE_FORMAT)))>0)
					{
						String timeValue = Formatter.format((new Date(configTime.getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00" ;
	               		PublicUtils.setConfig(conn,lasttimeconfvalue,timeValue);
					}
				}
				catch (ParseException e)
				{
					// TODO: handle exception
				   throw new JException("�����õ����ڸ�ʽ!"+e.getMessage());
				}
			} 
			catch (Exception e) 
			{
				try 
				{
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} 
				catch (Exception e1) 
				{
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} 
			finally 
			{
				is_importing = false;
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.qqbuy.Params.waittime * 1000))	
				try 
				{
					sleep(1000L);
				} 
				catch (Exception e) 
				{
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	
	
}

