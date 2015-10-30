package com.wofu.ecommerce.coo8;
import java.util.Properties;
import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.items.ItemsGetOnsaleRequest;
import com.coo8.api.request.proudct.ProductsGetRequest;
import com.coo8.api.response.items.ItemsGetOnsaleResponse;
import com.coo8.api.response.product.ProducstGetResponse;
import com.coo8.open.product.GoodsPop;
import com.coo8.open.product.ProductPop;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
public class ItemExecuter extends Executer{
	
	private static String jobName = "取库巴商品资料作业";

	private String tradecontactid="10";
	
	private String url="http://api.coo8.com/ApiControl";
	
	private String appKey="80000167";
	
	private String secretKey="d646ab2210e44306bcf015c8595101f6";

	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		
		appKey=prop.getProperty("appKey");
		
		secretKey=prop.getProperty("secretKey");
		
		tradecontactid=prop.getProperty("tradecontactid");
		
		try {		
			
			updateJobFlag(1);
			
			getOnSaleItems();
			getInStockItems();
	
			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
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

	private void getOnSaleItems() throws Exception
	{
		int i=0;
		int j=0;
		int pageIndex=1;
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		Log.info("开始取库巴上架商品资料");
		

		for (int k=0;k<5;)
		{
			try
			{
				
				while(true)
				{
					Coo8Client cc = new DefaultCoo8Client(url, appKey, secretKey);
					ItemsGetOnsaleRequest request=new ItemsGetOnsaleRequest();
					request.setFields("productId,productName,items,catalogId,brandId,productarea,provinceName," +
							"munit,weight,descUrl,gift,phaseAdver,startPhaseTime,endPhaseTime,volume,updater,templateId," +
							"pros,brandName,description,item.outId,item.itemId,item.goodsName,item.originalPrice,item.color," +
							"item.status,item.updater,item.updateTime,item.version,item.brandId,item.catalogId,item.quantity," +
							"item.detail,pic.imgId,pic.imgUrl,item.pic.index");
					request.setPageNo(pageIndex);				//第几页
					request.setPageSize(10);	
					ItemsGetOnsaleResponse response=cc.execute(request);
					
					Log.info("库巴上架商品数量："+response.getTotalResult());
					//获取总条数
					int totalCount=response.getTotalResult();
					
					for(int m=0;m<response.getProductPop().size();m++)
					{
						try{
							//迭代商品
							ProductPop pp=response.getProductPop().get(m);
							i=i+1;
							String produntid=pp.getProduct_no();
							//根据ID获得单个商品的详细信息和SKU
							
							StockManager.stockConfig(this.getDao(),orgid, Integer.valueOf(tradecontactid),pp.getProduct_no(),
									"",pp.getProduct_name(),0) ;
							
							Log.info("获取库巴商品资料成功，prpductid:"+produntid);
							
							if(pp.getGoodsList()!=null&&pp.getGoodsList().size()>0){
								for(GoodsPop gp:pp.getGoodsList()){
									
									j=j+1;
											
									Log.info("库巴商品SKU "+gp.getSku()+" "+gp.getModify_time());
									if(gp.getShow_quantity()==null){
										gp.setShow_quantity(0);
										Log.info("SKU"+gp.getSku()+"库存为空，设置默认为"+gp.getShow_quantity());
									}
									StockManager.addStockConfigSku(this.getDao(), orgid,pp.getProduct_no(),
											gp.getSku(),gp.getGoods_no(),gp.getShow_quantity()) ;
									
								}
								
							}else{
								Log.info("商品SKU为空");
							}
						}catch(Exception ex){
							if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
							Log.error(jobName, ex.getMessage());
						}
							
						}
							
					//总页数
					if (pageIndex==(Double.valueOf(Math.ceil(totalCount/10.0))).intValue()) break;
					
					pageIndex++;
				}
					
				Log.info("取库巴上架总商品数:"+String.valueOf(i)+" 总SKU数:"+String.valueOf(j));
				break;
				}catch (Exception e) {
					if (++k >= 5)
					throw e;
					if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
					Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
			
					Thread.sleep(10000L);
			} 	
		}
}
	
	private void getInStockItems() throws Exception
	{
		int i=0;
		int j=0;
		int pageIndex=1;
		
		Log.info("开始取库巴仓库商品资料");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);

		for (int k=0;k<5;)
		{
			try
			{
				
				while(true)
				{	
					Coo8Client cc = new DefaultCoo8Client(url, appKey, secretKey);
					ProductsGetRequest request=new ProductsGetRequest();
					request.setFields("productId,productName,items,catalogId,brandId,productarea,provinceName," +
							"munit,weight,descUrl,gift,phaseAdver,startPhaseTime,endPhaseTime,volume,updater,templateId," +
							"pros,brandName,description,item.outId,item.itemId,item.goodsName,item.originalPrice,item.color," +
							"item.status,item.updater,item.updateTime,item.version,item.brandId,item.catalogId,item.quantity," +
							"item.detail,pic.imgId,pic.imgUrl,item.pic.index");				//返回字段
					request.setPageNo(pageIndex);				//第几页
					request.setPageSize(10);	
					ProducstGetResponse response=cc.execute(request);

					Log.info("库巴所有商品数量："+response.getTotalResult());
					
					int totalCount=response.getTotalResult();
					for(int m=0;m<response.getProductPop().size();m++)
					{	
						try{
							//迭代商品
							ProductPop pp=response.getProductPop().get(m);
							i=i+1;
							
							String produntid=pp.getProduct_no();
							//根据ID获得单个商品的详细信息和SKU
							
							Log.info("获取库巴商品资料成功，prpductid:"+produntid);
							
							
							StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),pp.getProduct_no(),
									"",pp.getProduct_name(),0) ;
							
							
							if(pp.getGoodsList()!=null&&pp.getGoodsList().size()>0){
								for(GoodsPop gp:pp.getGoodsList()){
									
									j=j+1;
											
									Log.info("库巴商品SKU "+gp.getSku()+" "+gp.getModify_time());
									if(gp.getShow_quantity()==null){
										gp.setShow_quantity(0);
										Log.info("SKU"+gp.getSku()+"库存为空，设置默认为"+gp.getShow_quantity());
									}		
									StockManager.addStockConfigSku(this.getDao(), orgid,pp.getProduct_no(),
											gp.getSku(),gp.getGoods_no(),gp.getShow_quantity()) ;
									
								}
								
							}else{
								Log.info("商品SKU为空");
							}
						}catch(Exception ex){
							if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
							Log.error(jobName, ex.getMessage());
						}
						
					}

					if (pageIndex==(Double.valueOf(Math.ceil(totalCount/10.0))).intValue()) break;
					
					pageIndex++;
				}
				Log.info("取库巴上架总商品数:"+String.valueOf(i)+" 总SKU数:"+String.valueOf(j));
				break;
				
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
}
