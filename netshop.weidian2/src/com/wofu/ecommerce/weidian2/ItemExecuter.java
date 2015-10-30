package com.wofu.ecommerce.weidian2;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weidian2.utils.Utils;

public class ItemExecuter extends Executer
{
	
	private static String jobName = "ȡ΢����Ʒ������ҵ";
	
	private String tradecontactid="24";
	@Override
	public void run() 
	{	
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		tradecontactid=prop.getProperty("tradecontactid","24");
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
	
	private void getInStockItems() throws Exception
	{
		int pageno=1;
		Log.info("��ʼȡ΢��ֿ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);//24
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					JSONObject data=new JSONObject();
					StringBuffer  buffer = new StringBuffer(); 
					buffer.append("service=goods&");
					buffer.append("vcode="+Params.vcode+"&");
					buffer.append("status=0&");
					buffer.append("mtime_start=1970-01-01+00%3A00%3A00"+"&"); //ʱ��Ҫ�޸�Ϊstartdate��enddate,UTF8����
					buffer.append("mtime_end="+URLEncoder.encode(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT),"UTF-8"));
					String responseProductData = Utils.sendByPost("http://www.royalrose.com.cn/api/Goods/PostGood",buffer.toString());
					char[] rsp_cleaned = responseProductData.replace("\\", "").toCharArray();
					JSONObject responseproduct=new JSONObject(String.valueOf(rsp_cleaned, 1, rsp_cleaned.length-2));
					int totalCount=responseproduct.getInt("totalcount");//��Ʒ����
					JSONArray productlist=responseproduct.getJSONArray("productlist");//��Ʒ��
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);

						String productId= product.optString("mid");
						String productCode=product.optString("goods_no");
						String productCname=product.optString("title");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						//д��ECS_StockConfig�������
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						JSONArray childseriallist=product.getJSONArray("Skuinfo");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String skuid=childserial.optString("skuid");
							String sku=childserial.optString("sku");
							
							
							//JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							

								int quantity=0;
								long warehouseId=0;
								//д��ECS_StockConfigSku����
								StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),skuid/*+"-"+String.valueOf(warehouseId)*/,sku,quantity) ;

						}
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					
					pageno++;
				}
				break;
			}catch(Exception e)
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				Log.warn("1Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			}
		}
	}

	private void getOnSaleItems() throws Exception
	{
		int pageno=1;
		
		Log.info("��ʼȡ΢���ϼ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					StringBuffer  buffer = new StringBuffer(); 
					buffer.append("service=goods&");
					buffer.append("vcode="+Params.vcode+"&");
					buffer.append("status=1&");
					buffer.append("mtime_start=1970-01-01+00%3A00%3A00"+"&"); //ʱ��Ҫ�޸�Ϊstartdate��enddate,UTF8����
					buffer.append("mtime_end="+URLEncoder.encode(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT),"UTF-8"));
					String responseProductData = Utils.sendByPost("http://www.royalrose.com.cn/api/Goods/PostGood",buffer.toString());
					char[] rsp_cleaned = responseProductData.replace("\\", "").toCharArray();
					Log.info("���صĽ��"+responseProductData.replace("\\", ""));
					JSONObject responseproduct=new JSONObject(String.valueOf(rsp_cleaned, 1, rsp_cleaned.length-2));
					int totalCount=responseproduct.getInt("totalcount");
					JSONArray productlist=responseproduct.getJSONArray("productlist");
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);

						String productId= product.optString("mid");
						String productCode=product.optString("goods_no");
						String productCname=product.optString("title");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						JSONArray childseriallist=product.getJSONArray("Skuinfo");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String skuid=childserial.optString("skuid");
							String sku=childserial.optString("sku");
							
							
							//JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							

								int quantity=0;
								long warehouseId=0;
								StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),skuid/*+"-"+String.valueOf(warehouseId)*/,sku,quantity) ;

						}
					}
					
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					
					pageno++;
					
				}
				break;
			}catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				Log.warn("1Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}
	}
}
