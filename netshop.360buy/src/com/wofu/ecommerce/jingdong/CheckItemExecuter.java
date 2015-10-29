package com.wofu.ecommerce.jingdong;
import java.util.Iterator;
import java.util.Properties;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.ware.Sku;
import com.jd.open.api.sdk.domain.ware.Ware;
import com.jd.open.api.sdk.request.ware.WareGetRequest;
import com.jd.open.api.sdk.response.ware.WareGetResponse;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
public class CheckItemExecuter extends Executer {

	private String wareId = "" ;
	
	private String SERVER_URL = "" ;
	
	private String token = "" ;
	
	private String appKey = "" ;
	
	private String appSecret = "" ;
	
	private String tradecontactid = "" ;
	
	private String username = "" ;
	
	private static String jobName="����id��ѯ��Ʒ����";
	
	public void run()  {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		SERVER_URL=prop.getProperty("SERVER_URL") ;
		token=prop.getProperty("token") ;
		appKey=prop.getProperty("appKey") ;
		appSecret=prop.getProperty("appSecret") ;
		tradecontactid=prop.getProperty("tradecontactid") ;
		username=prop.getProperty("username") ;
		wareId=prop.getProperty("wareId") ;//�����ö��ŷָ�

		try 
		{			 
			updateJobFlag(1);
			
			checkItem() ;
			
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				//updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {

			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"���´����־ʧ��");
			}
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
	}
	
	private void checkItem() throws Exception
	{
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
			try
			{
				//ȡ��ǰʱ��Ϊ����ʱ�䣬ȡ��ǰʱ��ǰ7���ڵĴ����ⶩ��������Ƿ���©��
				JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);

				WareGetRequest  wareGetRequest= new WareGetRequest();
				if(wareId.indexOf(",")>0){
					String[] wareIds = wareId.split(",");
					for(String e:wareIds){
						
								wareGetRequest.setWareId(e);

								wareGetRequest.setFields("");

								WareGetResponse response= client.execute(wareGetRequest);
						
							if(!response.getCode().equals("0")){   //��ȡ��Ʒ������
								Log.error(jobName, response.getMsg());
								continue;
							}
							Ware result = response.getWare() ;
							StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(result.getWareId()),
									result.getItemNum(),result.getTitle(),Long.valueOf(result.getStockNum()).intValue()) ;
							if (result.getSkus()!=null && result.getSkus().size()!=0) 						
							{
								for(Iterator it=result.getSkus().iterator();it.hasNext();)
								{
									try{
										Sku skuinfo=(Sku) it.next();						
										
										Log.info("SKU "+skuinfo.getOuterId());
										
										StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(result.getWareId()),
												String.valueOf(skuinfo.getSkuId()),skuinfo.getOuterId(),Long.valueOf(skuinfo.getStockNum()).intValue()) ;
									}catch(Exception ex){
										if(this.getDao()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
										Log.error(jobName, ex.getMessage());
									}
									
									
								}
							}
					}
					
				}else{
					wareGetRequest.setWareId(wareId);

					wareGetRequest.setFields("");

					WareGetResponse response= client.execute(wareGetRequest);
			
				if(!response.getCode().equals("0")){   //��ȡ��Ʒ������
					Log.error(jobName, response.getMsg());
					return;
				}
				Ware result = response.getWare() ;
				StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(result.getWareId()),
						result.getItemNum(),result.getTitle(),Long.valueOf(result.getStockNum()).intValue()) ;
				if (result.getSkus()!=null && result.getSkus().size()!=0) 						
				{
					for(Iterator it=result.getSkus().iterator();it.hasNext();)
					{
						try{
							Sku skuinfo=(Sku) it.next();						
							
							Log.info("SKU "+skuinfo.getOuterId());
							
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(result.getWareId()),
									String.valueOf(skuinfo.getSkuId()),skuinfo.getOuterId(),Long.valueOf(skuinfo.getStockNum()).intValue()) ;
						}catch(Exception ex){
							if(this.getDao()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
							Log.error(jobName, ex.getMessage());
						}
						
						
					}
				}
				}
				
				
				
			}
			catch(Exception e)
			{
				if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()){
					this.getDao().getConnection().rollback();
				}
				e.printStackTrace();
				//Thread.sleep(10000L);
			}
			Log.info("��ȡָ��ID��Ʒ���");
	}
	
		
}
