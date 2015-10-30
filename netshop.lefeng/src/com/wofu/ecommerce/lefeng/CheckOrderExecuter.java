package com.wofu.ecommerce.lefeng;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;


import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;

import com.wofu.common.json.JSONObject;
import com.wofu.common.json.JSONArray;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;


public class CheckOrderExecuter extends Executer {
	
	private String url="";

	private String encoding="";

	private String shopid="";

	private String secretKey="";

	private String tradecontactid="";


	private String username="";
	
	private String methodApi="sellerSearchDealList";
	
	private static String jobname="定时检查乐峰订单";

	public void run(){

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		encoding=prop.getProperty("encoding");
		shopid=prop.getProperty("shopid");
		secretKey=prop.getProperty("secretKey");
		tradecontactid=prop.getProperty("tradecontactid");
		username=prop.getProperty("username");

		
		try 
		{	
			updateJobFlag(1);
	
			getOrderList();
	
			UpdateTimerJob();
			
			Log.info(jobname, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobname,"回滚事务失败");
			}
			Log.error(jobname,"错误信息:"+Log.getErrorMessage(e));
			
			
			Log.error(jobname, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobname,"更新处理标志失败");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobname,"关闭数据库连接失败");
			}
		}
		
		
	
	}
	
	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList() throws Exception
	{		
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
				

		while(hasNextPage)
		{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("shopId", shopid) ;
			params.put("shopOrderStatus","3");
			params.put("pageNo", String.valueOf(pageIndex)) ;
			params.put("pageSize", "40") ;
	
			String sign=LefengUtil.getSign(params, methodApi, secretKey, encoding);
			
			params.put("sign", sign);
			
		

			String responseText = LefengUtil.filterResponseText(CommHelper.sendRequest(url+methodApi+".htm",params,"",encoding));
			
			System.out.println(responseText);
			
			JSONObject jo = new JSONObject(responseText);
			
			int retcode=jo.optInt("result");
			
			if (retcode!=0)
			{
				hasNextPage = false ;
				if (retcode==7171)
				{
				
					Log.info("取订单失败,不存在订单信息！");
				}
				else
					Log.warn("取订单失败,错误信息:"+LefengUtil.errList.get(retcode));
				break ;
			}
			
			int pageTotal=jo.optInt("pageTotal"); //总页数
			pageIndex=jo.optInt("pageIndex");//当前页数
			
			JSONArray dealList=jo.optJSONArray("dealList");
			
			if (dealList.length()==0)
			{
				hasNextPage = false ;
				
				Log.info("不存在需要处理的订单!");
				break ;
			}
			
			boolean isNeedDealList=false;
			for(int i=0;i<dealList.length();i++)
			{
				JSONObject deal=dealList.getJSONObject(i);
				
			
				Order o=new Order();
				o.setObjValue(o, deal);
		
		
				Log.info(o.getOrderCode()+" "+LefengUtil.getStatusName(o.getOrderStatus())+" "+o.getCreateTime());
				
				isNeedDealList=true;
				
				/*
				 *1、如果状态为等待卖家发货则生成接口订单
				 *2、删除等待买家付款时的锁定库存 
				 */		
				String sku;
				String sql="";
				if (o.getOrderStatus()==6 || o.getOrderStatus()==3)
				{	
					
					if (!OrderManager.isCheck("检查乐峰订单", this.getDao().getConnection(), o.getOrderCode()))
					{
						if (!OrderManager.TidLastModifyIntfExists("检查乐峰订单", this.getDao().getConnection(), o.getOrderCode(),Formatter.parseDate(o.getCreateTime(),Formatter.DATE_TIME_FORMAT)))
						{
							OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
							
							for(Iterator ito=o.getItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getItemCode();
							
								StockManager.deleteWaitPayStock("检查乐峰订单", this.getDao().getConnection(),tradecontactid, o.getOrderCode(),sku);
								StockManager.addSynReduceStore("检查乐峰订单", this.getDao().getConnection(), tradecontactid, String.valueOf(o.getOrderStatus()),o.getOrderCode(), sku, -item.getItemQuantity(),false);
							}
						}
					}

					//等待买家付款时记录锁定库存
				}
				
				
			}
			
	
		
			
			//判断是否有下一页
			if(pageTotal>pageIndex)
				pageIndex ++ ;
			else
			{
				hasNextPage = false ;
				break;
			}
				
		}			
		
	}
	
	
}
