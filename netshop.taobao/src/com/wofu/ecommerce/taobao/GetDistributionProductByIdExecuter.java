//定时获取分销商品列表作业
package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.FenxiaoProduct;
import com.taobao.api.domain.FenxiaoSku;
import com.taobao.api.request.FenxiaoProductsGetRequest;
import com.taobao.api.response.FenxiaoProductsGetResponse;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.stock.StockManager;
public class GetDistributionProductByIdExecuter extends Executer {

	private static String jobName = "定时指定分销商品列表作业";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private String username="";
	private String url="";
	private String appkey="";
	private String appsecret="";
	private String authcode="";
	private String tradecontactid="";
	private String productId="";
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		username=prop.getProperty("username");
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		tradecontactid=prop.getProperty("tradecontactid");
		productId=prop.getProperty("productId");
		
			try {
				updateJobFlag(1);
				getProduct();
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
					
				} catch (Exception e1) {
					Log.error(jobName,"回滚事务失败");
					Log.error(jobName, e1.getMessage());
				}
				
				try{
					if (this.getExecuteobj().getSkip() == 1) {
						UpdateTimerJob();
					} else
						UpdateTimerJob(Log.getErrorMessage(e));
				}catch(Exception ex){
					Log.error(jobName,"更新任务信息失败");
					Log.error(jobName, ex.getMessage());
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
					Log.error(jobName, e.getMessage());
					Log.error(jobName,"更新处理标志失败");
					TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
				}
				
				try {
					if (this.getConnection() != null){
						this.getConnection().setAutoCommit(true);
						this.getConnection().close();
					}
						
				} catch (Exception e) {
					Log.error(jobName,"关闭数据库连接失败");
				}
			}
		
	}

	
	/*
	 * 获取一天之类的所有产品列表
	 * taobao.fenxiao.products.get  收费
	 */
	private void getProduct() throws Exception
	{		
		int i=0;
		int j=0;
		long pageno=1L;
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		try
			{
				TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
				FenxiaoProductsGetRequest req=new FenxiaoProductsGetRequest();
				req.setFields("skus");
				req.setPids(productId);
				FenxiaoProductsGetResponse response = client.execute(req , authcode);
				Log.info("response: "+response);
					if (response.getProducts()==null || response.getProducts().size()<=0)
					{		
						Log.info("test");
						return;
					}
					
					for(Iterator it=response.getProducts().iterator();it.hasNext();)
					{
						FenxiaoProduct product=(FenxiaoProduct) it.next();
						i=i+1;

						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(product.getPid()),
								product.getOuterId(),product.getName(),product.getQuantity().intValue()) ;
						if (product.getSkus()!=null)						
						{
							for(Iterator itsku=product.getSkus().iterator();itsku.hasNext();)
							{
								j=j+1;
								
								FenxiaoSku sku=(FenxiaoSku) itsku.next();
															
								Log.info("SKU "+sku.getOuterId()+" "+Formatter.format(product.getModified(),Formatter.DATE_TIME_FORMAT));
	
								
								StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(product.getPid()),
										String.valueOf(sku.getId()),sku.getOuterId(),sku.getQuantity().intValue()) ;
							}
						}
					}
					
				Log.info("取到分销总商品数:"+String.valueOf(i)+" 总SKU数:"+String.valueOf(j));
				
				//执行成功后不再循环
			} catch (Exception e) {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				
			}
	}
	
}
