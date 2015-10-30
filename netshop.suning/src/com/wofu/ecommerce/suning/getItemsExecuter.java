package com.wofu.ecommerce.suning;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.suning.util.CommHelper;
import com.wofu.business.stock.StockManager;
/**
 * 取苏宁商品执行器
 * @author Administrator
 *
 */
public class getItemsExecuter extends Executer {
	private static String jobName = "获取苏宁商品作业";
	private String tradecontactid="";
	private static String pageSize = "" ;
	private static String appSecret = "" ;
	private static String format = "" ;
	private static String versionNo = "" ;
	private static String appKey = "" ;
	private static String url = "" ;


	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize=prop.getProperty("pageSize");
		appSecret=prop.getProperty("appsecret");
		format=prop.getProperty("format");
		versionNo=prop.getProperty("versionNo");
		appKey=prop.getProperty("appkey");
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
		
		Log.info("开始取苏宁商品作业开始");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for(int k=0;k<10;)
		{
			
			try 
			{

				int pageIndex = 1 ;
				boolean hasNextpage = true ;
				
				while(hasNextpage)
				{
					//方法名
					 String apiMethod="suning.custom.item.query";
					 HashMap<String,String> reqMap = new HashMap<String,String>();
				     reqMap.put("status", "2");  //只取在售商品
				     reqMap.put("pageNo", String.valueOf(pageIndex));
				     reqMap.put("pageSize", pageSize);
				     String ReqParams = CommHelper.getJsonStr(reqMap, "item");
				     Log.info("取商品资料："+ReqParams);
				     HashMap<String,Object> map = new HashMap<String,Object>();
				     map.put("appSecret", appSecret);
				     map.put("appMethod", apiMethod);
				     map.put("format", format);
				     map.put("versionNo", versionNo);
				     map.put("appRequestTime", CommHelper.getNowTime());
				     map.put("appKey", appKey);
				     map.put("resparams", ReqParams);
				     //发送请求
					 String responseText = CommHelper.doRequest(map,url);
					Log.info("返回数据: "+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
					if(responseText.indexOf("sn_error")!=-1){   //发生错误
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //没有结果
						Log.info("没有可用的商品！");
						return;
					}else{
						if(!"".equals(operCode))
						{
							Log.error("苏宁获取商品作业", "获取商品作业失败,operCode:"+operCode);
						}
						return;
						}
					}
					
					
					//统计信息
					JSONObject totalInfo = responseObj.getJSONObject("sn_head");
					//总页数
					String pageTotal = String.valueOf(totalInfo.get("pageTotal"));
					
					if (pageTotal==null || pageTotal.equals("") || pageTotal.equals("0"))
					{				
						k=10;
						break;
					}
					//商品集合
					JSONArray items = responseObj.getJSONObject("sn_body").getJSONArray("item");
					for(int i = 0 ; i < items.length() ; i++)
					{
						try{
							JSONObject itemInfo = items.getJSONObject(i) ;
							//苏宁商品编号
							String itemID = itemInfo.getString("productCode");
							//商品标题 
							String itemName = itemInfo.getString("productName");
							//货号
							String outerItemID =itemInfo.getString("itemCode");
							if("".equals(itemID)){  //商品编码为空，跳过
								break;
							}
							//商品库存
							String stockCount="";
							if(itemInfo.toString().indexOf("childItem") == -1){  //没有子商品的情况
								stockCount=StockUtils.getInventoryByproductCode(itemID,appKey,appSecret,format,url);
								StockManager.stockConfig(dao, orgid,Integer.valueOf(tradecontactid),itemID,outerItemID,itemName,Integer.valueOf(stockCount).intValue()) ;
								m++;
							}else{     //有子商品情况下写stockconfigsku表
								JSONArray chileItem = itemInfo.getJSONArray("childItem");
								int totalCount=0;
									for(int j = 0 ; j < chileItem.length() ; j++)
									{	
										try{
											JSONObject item = chileItem.getJSONObject(j) ;
											//sku
											String sku = item.getString("itemCode");
											//外部sku
											String subItemID = item.getString("productCode");
											if("".equals(subItemID)){  //商品编码为空，跳过
												Log.info("子商品编码为空,主商品编码为:　"+itemID);
												break;
											}
											Log.info("产品编号: "+subItemID);
											//库存   String produceCode,String app_key,String app_Secret,String format,String url
											stockCount=StockUtils.getInventoryByproductCode(subItemID,appKey,appSecret,format,url);
											totalCount+=Integer.parseInt(stockCount);
											Log.info("获取到新的SKU: "+sku);
											StockManager.addStockConfigSku(dao, orgid,itemID,subItemID,sku,Integer.valueOf(stockCount).intValue()) ;
										}catch(Exception ex){
											Log.warn("苏宁取商品写入sku信息出错,错误信息: "+ex.getMessage());
											if (conn != null && !conn.getAutoCommit())
												conn.rollback();
											continue;
										}
										
									}
									//
									StockManager.stockConfig(dao, orgid,Integer.valueOf(tradecontactid),itemID,outerItemID,itemName,totalCount) ;
									m++;
								}
						}catch(Exception ex){
							Log.warn("苏宁取商品出错,错误信息: "+ex.getMessage());
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
							continue;
						}
						
						}
			
					//是否还有下一页
					if(pageIndex < Integer.parseInt(pageTotal))
					{
						hasNextpage = true ;
						pageIndex ++ ;
						Log.info("页数:"+pageIndex);
					}
					else
					{
						hasNextpage = false ;
					}
			}
				
				Log.info("取到苏宁总商品数:"+String.valueOf(m)+" 总SKU数:"+String.valueOf(n));
				
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