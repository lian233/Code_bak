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
	
	private static String jobName = "ȡ�����Ʒ������ҵ";

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
				
				updateJobFlag(0);
				
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

	private void getOnSaleItems() throws Exception
	{
		int i=0;
		int j=0;
		int pageIndex=1;
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		Log.info("��ʼȡ����ϼ���Ʒ����");
		

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
					request.setPageNo(pageIndex);				//�ڼ�ҳ
					request.setPageSize(10);	
					ItemsGetOnsaleResponse response=cc.execute(request);
					
					Log.info("����ϼ���Ʒ������"+response.getTotalResult());
					//��ȡ������
					int totalCount=response.getTotalResult();
					
					for(int m=0;m<response.getProductPop().size();m++)
					{
						try{
							//������Ʒ
							ProductPop pp=response.getProductPop().get(m);
							i=i+1;
							String produntid=pp.getProduct_no();
							//����ID��õ�����Ʒ����ϸ��Ϣ��SKU
							
							StockManager.stockConfig(this.getDao(),orgid, Integer.valueOf(tradecontactid),pp.getProduct_no(),
									"",pp.getProduct_name(),0) ;
							
							Log.info("��ȡ�����Ʒ���ϳɹ���prpductid:"+produntid);
							
							if(pp.getGoodsList()!=null&&pp.getGoodsList().size()>0){
								for(GoodsPop gp:pp.getGoodsList()){
									
									j=j+1;
											
									Log.info("�����ƷSKU "+gp.getSku()+" "+gp.getModify_time());
									if(gp.getShow_quantity()==null){
										gp.setShow_quantity(0);
										Log.info("SKU"+gp.getSku()+"���Ϊ�գ�����Ĭ��Ϊ"+gp.getShow_quantity());
									}
									StockManager.addStockConfigSku(this.getDao(), orgid,pp.getProduct_no(),
											gp.getSku(),gp.getGoods_no(),gp.getShow_quantity()) ;
									
								}
								
							}else{
								Log.info("��ƷSKUΪ��");
							}
						}catch(Exception ex){
							if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
							Log.error(jobName, ex.getMessage());
						}
							
						}
							
					//��ҳ��
					if (pageIndex==(Double.valueOf(Math.ceil(totalCount/10.0))).intValue()) break;
					
					pageIndex++;
				}
					
				Log.info("ȡ����ϼ�����Ʒ��:"+String.valueOf(i)+" ��SKU��:"+String.valueOf(j));
				break;
				}catch (Exception e) {
					if (++k >= 5)
					throw e;
					if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
					Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
					Thread.sleep(10000L);
			} 	
		}
}
	
	private void getInStockItems() throws Exception
	{
		int i=0;
		int j=0;
		int pageIndex=1;
		
		Log.info("��ʼȡ��Ͳֿ���Ʒ����");
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
							"item.detail,pic.imgId,pic.imgUrl,item.pic.index");				//�����ֶ�
					request.setPageNo(pageIndex);				//�ڼ�ҳ
					request.setPageSize(10);	
					ProducstGetResponse response=cc.execute(request);

					Log.info("���������Ʒ������"+response.getTotalResult());
					
					int totalCount=response.getTotalResult();
					for(int m=0;m<response.getProductPop().size();m++)
					{	
						try{
							//������Ʒ
							ProductPop pp=response.getProductPop().get(m);
							i=i+1;
							
							String produntid=pp.getProduct_no();
							//����ID��õ�����Ʒ����ϸ��Ϣ��SKU
							
							Log.info("��ȡ�����Ʒ���ϳɹ���prpductid:"+produntid);
							
							
							StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),pp.getProduct_no(),
									"",pp.getProduct_name(),0) ;
							
							
							if(pp.getGoodsList()!=null&&pp.getGoodsList().size()>0){
								for(GoodsPop gp:pp.getGoodsList()){
									
									j=j+1;
											
									Log.info("�����ƷSKU "+gp.getSku()+" "+gp.getModify_time());
									if(gp.getShow_quantity()==null){
										gp.setShow_quantity(0);
										Log.info("SKU"+gp.getSku()+"���Ϊ�գ�����Ĭ��Ϊ"+gp.getShow_quantity());
									}		
									StockManager.addStockConfigSku(this.getDao(), orgid,pp.getProduct_no(),
											gp.getSku(),gp.getGoods_no(),gp.getShow_quantity()) ;
									
								}
								
							}else{
								Log.info("��ƷSKUΪ��");
							}
						}catch(Exception ex){
							if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
							Log.error(jobName, ex.getMessage());
						}
						
					}

					if (pageIndex==(Double.valueOf(Math.ceil(totalCount/10.0))).intValue()) break;
					
					pageIndex++;
				}
				Log.info("ȡ����ϼ�����Ʒ��:"+String.valueOf(i)+" ��SKU��:"+String.valueOf(j));
				break;
				
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
}
