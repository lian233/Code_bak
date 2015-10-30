package com.wofu.ecommerce.icbc;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.icbc.util.CommHelper;
import com.wofu.business.stock.StockManager;
public class getItems extends Thread {
	private static String jobname = "获取美丽说商品作业";
	private static String pageSize = "20" ;
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection conn = null;

			try {												
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				getAllItems(conn);
				Log.info("美丽说取商品作业完成!");
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
		//Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		
		Log.info("开始取美丽说商品");
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
					String apimethod="/goods/goods_list?";
					HashMap<String,Object> map = new HashMap<String,Object>();
			        map.put("page", String.valueOf(pageIndex));
			        map.put("page_size", Params.pageSize);
			       // map.put("vcode", Params.vcode);
			        map.put("apimethod", apimethod);
			        String responseText = CommHelper.doPost(map,Params.url);
					Log.info("返回数据: "+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText);
					if(responseObj.getInt("code")==1){   //没有数据返回
						
						return;
					}
					if(!responseObj.isNull("error_code")){   //发生错误
						Log.info("获取美丽说商品列表出错，错误码: "+responseObj.getString("error_code"));
						return;
					}
					
					//总页数
					int goodsNum= Integer.parseInt(responseObj.getString("total_num"));
					int  pageTotal =goodsNum>=Integer.parseInt(pageSize)?(goodsNum %Integer.parseInt(pageSize)==0?goodsNum /Integer.parseInt(pageSize):(goodsNum /Integer.parseInt(pageSize)+1)):1;
					
					if (pageTotal ==0)
					{				
						k=5;
						break;
					}
					//商品集合
					JSONArray items = responseObj.getJSONArray("info");
					for(int i = 0 ; i < items.length() ; i++)
					{
						
						JSONObject itemInfo = items.getJSONObject(i) ;
						//美丽说商品编号
						String itemID = itemInfo.getString("twitter_id");
						//商品标题 
						String itemName = new String(itemInfo.getString("goods_title").getBytes(),"gbk");
						//货号
						String goods_no =itemInfo.getString("goods_no");
						/*if("".equals(itemID)){  //商品编码为空，跳过
							break;
						}*/
						//商品库存
						String stockCount="";
						     //有子商品情况下写stockconfigsku表
							JSONArray chileItem = itemInfo.getJSONArray("stocks");
							int totalCount=0;
								for(int j = 0 ; j < chileItem.length() ; j++)
								{	
									JSONObject item = chileItem.getJSONObject(j) ;
									//sku
									StringBuilder str = new StringBuilder();
									if(!item.isNull("1st"))
									str.append("1st=").append(URLEncoder.encode(new String(item.getString("1st").getBytes(),"gbk")));
									if(!item.isNull("2rd"))
										str.append("&2rd=").append(URLEncoder.encode(new String(item.getString("2rd").getBytes(),"gbk")));
									//外部sku
									String sku = item.getString("goods_code");
									
									Log.info("产品编号: "+sku);
									//库存  
									stockCount=String.valueOf(item.getInt("repertory"));
									totalCount+=Integer.parseInt(stockCount);
									Log.info("获取到新的SKU: "+sku);
									StockManager.addStockConfigSku(dao, orgid,itemID,str.toString(),sku,Integer.valueOf(stockCount).intValue()) ;
									n++;
								}
								//
								StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),itemID,goods_no,itemName,totalCount) ;
								m++;
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
			
				Log.info("取到美丽说总商品数:"+String.valueOf(m)+" 总SKU数:"+String.valueOf(n));
				
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