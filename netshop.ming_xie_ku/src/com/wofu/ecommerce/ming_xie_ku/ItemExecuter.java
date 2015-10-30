package com.wofu.ecommerce.ming_xie_ku;
import java.util.Date;
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "ȡ��Ь����Ʒ������ҵ";

	private String tradecontactid="";
	private String url="";
	private String app_key="";
	private String app_Secret="";
	private String ver="";
	private String format="";



	@Override
	public void run() {
		//System.out.println("ȡ��Ь����Ʒ������ҵ");
		//Log.info("ȡ��Ь����Ʒ������ҵ");
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		tradecontactid=prop.getProperty("tradecontactid","18");
		url=prop.getProperty("url","18");
		app_key=prop.getProperty("app_key","18");
		app_Secret=prop.getProperty("app_Secret","18");
		ver=prop.getProperty("ver","18");
		format=prop.getProperty("format","18");
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

	private void getOnSaleItems() throws Exception
	{

		int pageno=1;
		
		Log.info("��ʼȡ��Ь���ϼ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Date now=new Date();
					String method="scn.vendor.item.full.get";
					JSONObject data=new JSONObject();
					data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
					data.put("PageNo", String.valueOf(pageno));    //ҳ��
					data.put("PageSize", "20");  //ÿҳ������Ĭ��40�����100
					data.put("ItemStatus", "1");  //��Ʒ״̬(1-���� 2-�¼�)
					String sign=Utils.get_sign(app_Secret,app_key,data, method, now,ver,format);
					String output_to_server=Utils.post_data_process(method, data, app_key,now, sign).toString();
			        String responseProductData = Utils.sendByPost(url,output_to_server);
			        //Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount=responseproduct.getInt("TotalResults");
					
					JSONArray productlist=responseproduct.getJSONArray("Result");
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
						
						String productId= product.optString("VendorItemId");
						String productCode=product.optString("ProductNo");
						String productCname=product.optString("ItemName");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),productId,productCode,productCname,0) ;
						JSONArray childseriallist=product.getJSONArray("Skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);

							String sku=childserial.optString("VendorSkuId");
							int quantity=0;
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(sku),sku,quantity) ;
						}
					}
					Log.info("�� "+pageno+" ҳ");
					//�ж��Ƿ�����һҳ
					//if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					if(!responseproduct.optBoolean("HasNext")) break;
					pageno++;
				}
				k=10;
				break;
			} catch (Exception e) {
				//e.printStackTrace();
				if (++k >= 10)
					throw e;
				Log.warn("1Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
	
	private void getInStockItems() throws Exception
	{

		int pageno=1;
		
		Log.info("��ʼȡ��Ь��ֿ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Date now=new Date();
					String method="scn.vendor.item.full.get";
					JSONObject data=new JSONObject();
					data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
					data.put("PageNo", String.valueOf(pageno));    //ҳ��
					data.put("PageSize", "20");  //ÿҳ������Ĭ��40�����100
					data.put("ItemStatus", "2");  //��Ʒ״̬(1-���� 2-�¼�)
					String sign=Utils.get_sign(app_Secret,app_key,data, method, now,ver,format);
					String output_to_server=Utils.post_data_process(method, data, app_key,now, sign).toString();
			        String responseProductData = Utils.sendByPost(url,output_to_server);
			       // Log.info("ȡ�ֿ���Ʒ��������:��"+responseProductData);
			        
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount=responseproduct.getInt("TotalResults");
					
					JSONArray productlist=responseproduct.getJSONArray("Result");
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						String productId=product.optString("VendorItemId");
						String productCode=product.optString("ProductNo");
						String productCname=product.optString("ItemName");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),productId,productCode,productCname,0) ;
						
						JSONArray childseriallist=product.getJSONArray("Skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);

							String sku=childserial.optString("VendorSkuId");

							int quantity=0;

							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),sku,sku,quantity) ;
						}
					}
					Log.info("�� "+pageno+" ҳ");
					//�ж��Ƿ�����һҳ
					if(!responseproduct.optBoolean("HasNext")) break;
					pageno++;
					
				}
				k=10;
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(jobName+", Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
}
