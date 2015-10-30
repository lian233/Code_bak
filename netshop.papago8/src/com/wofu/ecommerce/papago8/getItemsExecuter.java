package com.wofu.ecommerce.papago8;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.papago8.util.CommHelper;
import com.wofu.business.stock.StockManager;
/**
 * 取美丽说商品执行器
 * @author Administrator
 *
 */
public class getItemsExecuter extends Executer {
	private static String jobName = "获取美丽说商品作业";
	private String tradecontactid="";
	private static String pageSize = "" ;
	private static String url = "" ;
	private static String vcode = "" ;


	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize=prop.getProperty("pageSize");
		vcode=prop.getProperty("vcode");
		url=prop.getProperty("url");
		tradecontactid=prop.getProperty("tradecontactid");
		
			Connection conn = null;

			try {			
				conn = this.getDao().getConnection();
				updateJobFlag(1);
				getAllItems(conn);
				UpdateTimerJob();
				Log.info(jobName, "执行作业成功 ["
						+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
						+ "] 下次处理时间: "
						+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		
			} catch (Exception e) {
				try {
					
					if (this.getConnection() != null && !this.getConnection().getAutoCommit())
						this.getConnection().rollback();
					
					if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
						this.getExtconnection().rollback();
					
					if (this.getExecuteobj().getSkip() == 1) {
						UpdateTimerJob();
					} else
						UpdateTimerJob(Log.getErrorMessage(e));
					
					
				} catch (Exception e1) {
					Log.error(jobName,"回滚事务失败");
				}
				Log.error(jobName,"错误信息:"+Log.getErrorMessage(e));
				
				Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
						+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
						+ Log.getErrorMessage(e));
				
			} finally {
				try
				{
					updateJobFlag(0);
				} catch (Exception e) {
					Log.error(jobName,"更新处理标志失败");
				}
				
				try {
					if (this.getConnection() != null)
						this.getConnection().close();
					if (this.getExtconnection() != null)
						this.getExtconnection().close();
					
				} catch (Exception e) {
					Log.error(jobName,"关闭数据库连接失败");
				}
			}
			
		}
	/*
	 * status=处理状态。1：正在处理；2：处理成功；3：处理失败。
	 */
	private void getAllItems(Connection conn) throws Exception
	{
		int m=0,n=0;
		//Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=dao.intSelect(sql);
		Log.info("开始取美丽说商品作业开始");
		
		for(int k=0;k<10;)
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
			        map.put("page_size", pageSize);
			        map.put("vcode", vcode);
			        map.put("apimethod", apimethod);
				     //发送请求
					 String responseText = CommHelper.doRequest(map,url,"get");
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
					
					
					//统计信息
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
									if(!item.isNull("2rd") && item.getString("2rd").length()<3){
										Log.info("2rd: "+new String(item.getString("2rd").getBytes(),"gbk"));
										str.append("&2rd=").append(URLEncoder.encode(new String(item.getString("2rd").getBytes(),"gbk")));
									}
										
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
								StockManager.stockConfig(dao, orgid,Integer.valueOf(tradecontactid),itemID,goods_no,itemName,totalCount) ;
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
				if (++k >= 10)
					throw e;
				if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
					this.getDao().getConnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
}