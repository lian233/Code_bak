package com.wofu.ecommerce.coo8;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.items.ItemsGetOnsaleRequest;
import com.coo8.api.request.proudct.ProductGetRequest;
import com.coo8.api.request.proudct.ProductsGetRequest;
import com.coo8.api.response.items.ItemsGetOnsaleResponse;
import com.coo8.api.response.product.ProducstGetResponse;
import com.coo8.api.response.product.ProductGetResponse;
import com.coo8.open.product.GoodsPop;
import com.coo8.open.product.ProductPop;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class getProducts extends Thread {
	private static String jobName = "获取库巴商品列表";
	
	private static String lasttimeconfvalue=Params.username+"取商品最新时间";
	
	private static long daymillis=24*60*60*1000L;
	
	private boolean is_importing=false;
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static int interval = 30 ;
	
	
	private static String returnFields="productId,productName,items,catalogId,brandId,productarea,provinceName," +
	"munit,weight,descUrl,gift,phaseAdver,startPhaseTime,endPhaseTime,volume,updater,templateId," +
	"pros,brandName,description,item.outId,item.itemId,item.goodsName,item.originalPrice,item.color," +
	"item.status,item.updater,item.updateTime,item.version,item.brandId,item.catalogId,item.quantity," +
	"item.detail,item.pic.imgId,item.pic.imgUrl,item.pic.index";
	
	private static String lasttime;
	
	private static String access_token=null;
	
	public void run() {
		//获取授权令牌的参数
		Log.info(jobName, "启动[" + jobName + "]模块");

		do {		
			Connection conn = null;
			is_importing = true;
			try {		
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttime=PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
	
				getOnSaleProducts(conn);

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
	
	private void getOnSaleProducts(Connection conn)throws Exception{

		
		int i=0;
		int j=0;
		int pageIndex=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		ECSDao dao=new ECSDao(conn);
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn, sql);
		Log.info("开始取库巴上架商品");
		
		for(int k=0;k<5;)
		{
			try
			{	
				Coo8Client cc = new DefaultCoo8Client(Params.url, Params.appKey, Params.secretKey);
				ItemsGetOnsaleRequest request=new ItemsGetOnsaleRequest();
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				request.setStartModified(startdate);
				request.setEndModified(enddate);
				request.setFields(returnFields);				//返回字段
				request.setPageNo(pageIndex);				//第几页
				request.setPageSize(5);					//每页多少个
				ItemsGetOnsaleResponse response=cc.execute(request);
				Log.info(startdate+" "+enddate);
				while(true)
				{
					if (response.getTotalResult()==0)
					{				//Log.info("第一");
						if (i==0)		
						{			//Log.info("第二");
							try
							{
									//Log.info("第三");
									if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
											compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
									{	
										try
						                {	
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
											PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
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
					
					
					
					for(int m=0;m<response.getProductPop().size();m++){
						try{
							//迭代商品
							ProductPop pp=response.getProductPop().get(m);
							i=i+1;
							
							String produntid=pp.getProduct_no();
							//根据ID获得单个商品的详细信息和SKU
							Coo8Client coo8=new DefaultCoo8Client(Params.url,Params.appKey,Params.secretKey);
							ProductGetRequest req=new ProductGetRequest();
							req.setProductId(produntid);
							ProductGetResponse res=coo8.execute(req);
							
							if(res.getGoods()==null){
								Log.info("获取库巴商品详细资料失败,productid:"+produntid+"错误信息:"+ res.getMsg() + "," + res.getSubMsg());
								continue;
							}
							
							
							StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),pp.getProduct_no(),
									"",pp.getProduct_name(),0) ;
							
							String modifytime="";
							//商品SKU
							if(res.getGoods().size()>0){
								for(GoodsPop gp:res.getGoods()){
									modifytime=gp.getModify_time();
									
									j=j+1;
											
									Log.info("SKU "+gp.getSku()+" "+gp.getModify_time());
											
									StockManager.addStockConfigSku(dao, orgid,pp.getProduct_no(),
											gp.getSku(),gp.getGoods_no(),gp.getShow_quantity()) ;
									
								}
								
							}
							//System.out.println("最后修改时间："+modifytime);
							//System.out.println("modified时间"+modified);
							//更新同步订单最新时间
				            if (Formatter.parseDate(modifytime,Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
				            {
				                modified=Formatter.parseDate(modifytime,Formatter.DATE_TIME_FORMAT);
				                System.out.println("modified时间2"+modified);
				            }
						}catch(Exception ex){
							if(conn!=null && !conn.getAutoCommit()) conn.rollback();
							Log.error(jobName, ex.getMessage());
						}
						
					}
					
					//获取总条数
					int total=response.getTotalResult();
					//总页数
					int pageTotal=Double.valueOf(Math.ceil(total/5)).intValue();
					Log.info("分页：当前页"+pageIndex+" 总页数"+pageTotal+" 总条数："+total);
					//判断是否有下一页
					if(pageTotal>pageIndex)
						pageIndex ++ ;
					else
					{
						break;
					}
					
						
				}//while未
				
				Log.info("取库巴上架总商品数:"+String.valueOf(i)+" 总SKU数:"+String.valueOf(j));
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
						Log.info(modified.toString()+"时间"+lasttime);
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
}
