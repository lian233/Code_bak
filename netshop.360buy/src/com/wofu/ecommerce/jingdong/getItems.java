package com.wofu.ecommerce.jingdong;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.domain.ware.Sku;
import com.jd.open.api.sdk.domain.ware.Ware;
import com.jd.open.api.sdk.request.ware.WareDelistingGetRequest;
import com.jd.open.api.sdk.request.ware.WareGetRequest;
import com.jd.open.api.sdk.request.ware.WareInfoByInfoRequest;
import com.jd.open.api.sdk.request.ware.WareListingGetRequest;
import com.jd.open.api.sdk.response.ware.WareDelistingGetResponse;
import com.jd.open.api.sdk.response.ware.WareGetResponse;
import com.jd.open.api.sdk.response.ware.WareInfoByInfoSearchResponse;
import com.jd.open.api.sdk.response.ware.WareListingGetResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
public class getItems extends Thread {
	private static String jobName = "获取京东商品资料作业";
	private static long daymillis=24*60*60*1000L;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private static String selflasttimeconfvalue=Params.username+"取最新修改商品最新时间";
	private static int orgid=0;
	private boolean is_importing=false;
	private String selflasttime;

	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {		
			Connection conn = null;
			is_importing = true;
			try {												
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				Jingdong.setCurrentDate_getItem(new Date());
				selflasttime=PublicUtils.getConfig(conn,selflasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				Params.token = PublicUtils.getToken(conn, Integer.parseInt(Params.tradecontactid));
				getOnSaleItems(conn);

			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 *  取在售商品
	 */
	private void getOnSaleItems(Connection conn) throws Exception
	{		
		Log.error("连接池数监测,getItems连接数为"+conn.getMetaData(),"");
		int i=0;
		int j=0;
		int m=0;
		int pageno=1;
		Date modified=Formatter.parseDate(selflasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		Log.info("开始取京东最新修改商品");
		String sql="";
		if(orgid==0){
			sql= "select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
			orgid=dao.intSelect(sql);
			
		}
		
		for(int k=0;k<10;)
		{
			try
			{
				DefaultJdClient wareListClient = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
				WareInfoByInfoRequest wareInfoByInfoRequest= new WareInfoByInfoRequest();
				//取前一天的商品  有延迟
				Date startdate=new Date(Formatter.parseDate(selflasttime,Formatter.DATE_TIME_FORMAT).getTime()-daymillis+1000L);
				Date enddate=new Date(Formatter.parseDate(selflasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);

				wareInfoByInfoRequest.setPage(String.valueOf(pageno));

				wareInfoByInfoRequest.setPageSize("40");
				wareInfoByInfoRequest.setStartModified(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
				wareInfoByInfoRequest.setEndModified(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));



				WareInfoByInfoSearchResponse response = wareListClient.execute(wareInfoByInfoRequest);

			
				while(true)
				{
								
					if (response.getWareInfos()==null || response.getWareInfos().size()<=0)
					{				
						if (i==0)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,selflasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,selflasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, selflasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobName, "不可用的日期格式!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
					
					if (!response.getCode().equals("0")){
						Log.info("获取京东商品列表失败,错误信息:"+response.getCode() + "," + response.getZhDesc());
					}
					
					
					
					for(Iterator ititems=response.getWareInfos().iterator();ititems.hasNext();)
					{
						try{
							Ware item=(Ware) ititems.next();
							if(Formatter.parseDate(item.getModified(),Formatter.DATE_TIME_FORMAT).compareTo(Formatter.parseDate(selflasttime, Formatter.DATE_TIME_FORMAT))<=0){
								continue;
							}
							i++;
							m++;
							long wareid=item.getWareId();
							
							String queryFields = "ware_id,skus,ware_status,title,item_num,stock_num,creator,status,created,modified,outer_id" ;
							DefaultJdClient wareClient = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
							WareGetRequest  wareGetRequest = new WareGetRequest();
							wareGetRequest.setWareId(String.valueOf(wareid));
							wareGetRequest.setFields(queryFields) ;
							WareGetResponse res= wareClient.execute(wareGetRequest);
							
							if (!res.getCode().equals("0"))
							{
								Log.info("获取京东商品资料失败,货号:"+ item.getItemNum() +"错误信息:"+ res.getCode() + "," + res.getZhDesc());
								continue;
							}

							StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),String.valueOf(res.getWare().getWareId()),
									res.getWare().getItemNum(),res.getWare().getTitle(),Long.valueOf(res.getWare().getStockNum()).intValue()) ;
							
								
								if (res.getWare().getSkus()!=null && res.getWare().getSkus().size()!=0) 						
								{
									for(Iterator it=res.getWare().getSkus().iterator();it.hasNext();)
									{
										try{
											j=j+1;
											Sku skuinfo=(Sku) it.next();						
											Log.info("SKU "+skuinfo.getOuterId()+" "+item.getModified());
											
											StockManager.addStockConfigSku(dao, orgid,String.valueOf(res.getWare().getWareId()),
													String.valueOf(skuinfo.getSkuId()),skuinfo.getOuterId(),Long.valueOf(skuinfo.getStockNum()).intValue()) ;
										}catch(Exception ex){
											if(conn!=null && !conn.getAutoCommit()) conn.rollback();
											Log.error(jobName, ex.getMessage());
										}
										
										
									}
								}
							
							//更新同步订单最新时间
			                if (Formatter.parseDate(item.getModified(),Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
			                {
			                	modified=Formatter.parseDate(item.getModified(),Formatter.DATE_TIME_FORMAT);
			                }
						}catch(Exception ex){
							if(conn!=null && !conn.getAutoCommit()) conn.rollback();
							Log.error(jobName, ex.getMessage());
						}
						
		                		
					}
					if (pageno==(Double.valueOf(Math.ceil(response.getTotal()/40.0))).intValue()) break;
					
					Log.info("页数:"+pageno);
					pageno=pageno+1;
					wareInfoByInfoRequest.setPage(String.valueOf(pageno));			
					response=wareListClient.execute(wareInfoByInfoRequest);			
				}
				
				Log.info("取京东上架总商品数:"+String.valueOf(i)+" 总SKU数:"+String.valueOf(j));
		
				
				if (modified.compareTo(Formatter.parseDate(selflasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, selflasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				
				if(m==0){
					try
					{
						//如取到的全部是旧商品，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
						if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
								compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,selflasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
						{
							try
		                	{
								String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,selflasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
								PublicUtils.setConfig(conn, selflasttimeconfvalue, value);			    
		                	}catch(JException je)
		                	{
		                		Log.error(jobName, je.getMessage());
		                	}
						}
					}catch(ParseException e)
					{
						Log.error(jobName, "不可用的日期格式!"+e.getMessage());
					}
				}
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
