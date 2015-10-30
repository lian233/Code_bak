package com.wofu.ecommerce.papago8;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.systemmanager.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.papago8.util.CommHelper;
import com.wofu.business.stock.StockManager;
public class getItems extends Thread {
	private static String jobname = "获取papago8商品作业";
	private static String lasttime="";
	private static String lastTimeConfig = Params.username+"取最新修改商品时间";
	private static long daymills = 24*60*60*1000L;
	
	private static String pageSize = "20" ;
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection conn = null;

			try {												
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttime = PublicUtils.getConfig(conn, lastTimeConfig, "");
				getAllItems(conn);
				Log.info("papago8取商品作业完成!");
			} catch (Exception e) {
				try {
					e.printStackTrace() ;
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	/*
	 * status=处理状态。1：正在处理；2：处理成功；3：处理失败。
	 */
	private void getAllItems(Connection conn) throws Exception
	{
		int m=0,n=0;
		String startTime = Formatter.format(new Date(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()+1000L), Formatter.DATE_TIME_FORMAT);
		String endTime = Formatter.format(new Date(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()+daymills),Formatter.DATE_TIME_FORMAT);
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		
		Log.info("开始取papago8商品");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=dao.intSelect(sql);
		for(int k=0;k<5;)
		{
			
			try 
			{

				int pageIndex = 0 ;
				boolean hasNextpage = true ;
				while(hasNextpage)
				{
					//方法名
					String apimethod="QueryProduct.aspx?";
					HashMap<String,Object> map = new HashMap<String,Object>();
			        map.put("page", String.valueOf(pageIndex));
			        map.put("page_size", Params.pageSize);
			        map.put("Key", Params.Key);
			        map.put("apimethod", apimethod);
			        map.put("start_modified", URLEncoder.encode(startTime,"utf-8"));
			        map.put("end_modified", URLEncoder.encode(endTime,"utf-8"));
			        map.put("format", "json");
			        String responseText = CommHelper.doGet(map,Params.url);
					Log.info("返回数据: "+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("products_response");
					if("0".equals(responseObj.getString("total_results"))){   //没有数据返回
						if(Formatter.parseDate(lasttime, Formatter.DATE_FORMAT).compareTo(Formatter.parseDate(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT), Formatter.DATE_FORMAT))<0){
							try{
								String value = Formatter.format(new Date(Formatter.parseDate(lasttime, Formatter.DATE_FORMAT).getTime()+daymills), Formatter.DATE_FORMAT)+" 00:00:00";
								PublicUtils.setConfig(conn, lastTimeConfig, value);
							}catch(Exception e){
								Log.error("修改取商品最新时间出错", e.getMessage());
							}
							
						}
						return;
						
					}
					
					//总页数
					int goodsNum= Integer.parseInt(responseObj.getString("total_results"));
					int  pageTotal =goodsNum>=Integer.parseInt(pageSize)?(goodsNum %Integer.parseInt(pageSize)==0?goodsNum /Integer.parseInt(pageSize):(goodsNum /Integer.parseInt(pageSize)+1)):1;
					
					if (pageTotal ==0)
					{				
						k=5;
						break;
					}
					//商品集合
					JSONArray items = responseObj.getJSONObject("products").getJSONArray("product");
					for(int i = 0 ; i < items.length() ; i++)
					{
						
						JSONObject itemInfo = items.getJSONObject(i) ;
						//papago8商品编号
						String itemID = itemInfo.getString("num_iid");
						//商品标题 
						String itemName = itemInfo.getString("pro_name");
						//货号
						String goods_no =itemInfo.getString("pro_no");
						/*if("".equals(itemID)){  //商品编码为空，跳过
							break;
						}*/
						//商品库存
						String stockCount="";
						     //有子商品情况下写stockconfigsku表
							JSONArray chileItem = itemInfo.getJSONObject("skulist").getJSONArray("sku");
							int totalCount=0;
								for(int j = 0 ; j < chileItem.length() ; j++)
								{	
									JSONObject item = chileItem.getJSONObject(j) ;
									String skuid = item.getString("sys_id");
									//外部sku
									String sku = item.getString("skuid");
									
									Log.info("产品编号: "+sku);
									//库存  
									stockCount=item.getString("num");
									totalCount+=Integer.parseInt(stockCount);
									Log.info("获取到新的SKU: "+sku);
									StockManager.addStockConfigSku(dao, orgid,itemID,skuid,sku,Integer.valueOf(stockCount).intValue()) ;
									n++;
								}
								StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),itemID,goods_no,itemName,totalCount) ;
								m++;
								//商品修改时间
								Date itemModified = Formatter.parseDate(itemInfo.getString("modified"), Formatter.DATE_TIME_FORMAT);
								if(modified.compareTo(itemModified)<0)  modified=itemModified;
							}
					
					//是否还有下一页
					if(pageIndex < pageTotal-1)
					{
						hasNextpage = true ;
						pageIndex ++ ;
						Log.info("页数:"+pageIndex+1);
					}
					else
					{
						hasNextpage = false ;
					}
				
						}
				if(modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0){
					try{
						String value = Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lastTimeConfig, value);
					}catch(Exception e){
						
					}
				}
			
				Log.info("取到papago8总商品数:"+String.valueOf(m)+" 总SKU数:"+String.valueOf(n));
				
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
}