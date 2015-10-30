package com.wofu.ecommerce.s;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.s.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "ȡ��Ь����Ʒ������ҵ";

	private String tradecontactid="";



	@Override
	public void run() {
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
		
		Log.info("��ʼȡһ�ŵ��ϼ���Ʒ����");
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
//					data.put("VendorItemId","111128");  //��������ƷID
//					data.put("ProductNo", "111128");  //����
//					data.put("ItemName", String.valueOf(null)); //��Ʒ����
//					data.put("BrandName", String.valueOf(null)); //Ʒ������
//					data.put("ItemStatus", String.valueOf(null));  //��Ʒ״̬(1-���� 2-�¼�)
//					data.put("StartShowDate", String.valueOf(null)); //�ϼܿ�ʼʱ��
//					data.put("EndShowDate", String.valueOf(null));    //�ϼܽ���ʱ��
//					data.put("StartDownDate", String.valueOf(null));  //�¼ܿ�ʼʱ��
//					data.put("EndDownDate", String.valueOf(null));    //�¼ܽ���ʱ��
					data.put("PageNo", String.valueOf(pageno));    //ҳ��
					data.put("PageSize", "20");  //ÿҳ������Ĭ��40�����100
					String sign=Utils.get_sign(data, method, now);
					String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
			        String responseProductData = Utils.sendByPost(Params.url,output_to_server);
			        Log.info("ȡ��Ʒ��������:��"+responseProductData);
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
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						
						
//						Map<String, String> stockparams = new HashMap<String, String>();
//				        //ϵͳ����������
//						stockparams.put("appKey", app_key);
//						stockparams.put("sessionKey", token);
//						stockparams.put("format", format);
//						stockparams.put("method", "yhd.serial.product.get");
//						stockparams.put("ver", ver);
//						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//						stockparams.put("productId", String.valueOf(productId));				
						now=new Date();
						data=new JSONObject();
						data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
						data.put("VendorItemId",String.valueOf(productId));  //��������ƷID
//						data.put("ProductNo", "111128");  //����
//						data.put("ItemName", String.valueOf(null)); //��Ʒ����
//						data.put("BrandName", String.valueOf(null)); //Ʒ������
//						data.put("ItemStatus", String.valueOf(null));  //��Ʒ״̬(1-���� 2-�¼�)
//						data.put("StartShowDate", String.valueOf(null)); //�ϼܿ�ʼʱ��
//						data.put("EndShowDate", String.valueOf(null));    //�ϼܽ���ʱ��
//						data.put("StartDownDate", String.valueOf(null));  //�¼ܿ�ʼʱ��
//						data.put("EndDownDate", String.valueOf(null));    //�¼ܽ���ʱ��
//						data.put("PageNo", String.valueOf(null));    //ҳ��
//						data.put("PageSize", String.valueOf(null));  //ÿҳ������Ĭ��40�����100
						sign=Utils.get_sign(data, method, now);
						output_to_server=Utils.post_data_process(method, data, now, sign).toString();
			        
						String responseData = Utils.sendByPost(Params.url,output_to_server);
						 Log.info("ȡ��Ʒ��ϸ��������:��"+responseData);
						
						
						JSONObject responsestock=new JSONObject(responseData);
						
				
						
						//JSONArray childseriallist=responsestock.getJSONObject("response").getJSONObject("serialChildProdList").getJSONArray("serialChildProd");
						JSONArray childseriallist=responsestock.getJSONArray("Result").getJSONObject(0).getJSONArray("Skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("VendorSkuId");
							//long skuid=childserial.optLong("VendorSkuId");
							
							
							//JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							

								int quantity=0;
								long warehouseId=0;
								
								StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(sku)+"-"+String.valueOf(warehouseId),sku,quantity) ;

						}
					}

					
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					
					pageno++;
				}
				
			
				break;
				
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
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
//					Map<String, String> productparams = new HashMap<String, String>();
//			        //ϵͳ����������
//					productparams.put("appKey", app_key);
//					productparams.put("sessionKey", token);
//					productparams.put("format", format);
//					productparams.put("method", "yhd.serial.products.search");
//					productparams.put("ver", ver);
//					productparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//			       
//			        productparams.put("canShow", "1");
//			        productparams.put("canSale", "0");
//			        productparams.put("curPage", String.valueOf(pageno));
//			        productparams.put("pageRows", "20");
//			        productparams.put("verifyFlg", "2");
					UTF8_transformer utf8_transformer=new UTF8_transformer();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
					Date now=new Date();
					String method="scn.vendor.item.full.get";
					String ver=Params.ver;
					JSONObject data=new JSONObject();
					data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
//					data.put("VendorItemId",String.valueOf(productId));  //��������ƷID
//					data.put("ProductNo", "111128");  //����
//					data.put("ItemName", String.valueOf(null)); //��Ʒ����
//					data.put("BrandName", String.valueOf(null)); //Ʒ������
//					data.put("ItemStatus", String.valueOf(null));  //��Ʒ״̬(1-���� 2-�¼�)
//					data.put("StartShowDate", String.valueOf(null)); //�ϼܿ�ʼʱ��
//					data.put("EndShowDate", String.valueOf(null));    //�ϼܽ���ʱ��
//					data.put("StartDownDate", String.valueOf(null));  //�¼ܿ�ʼʱ��
//					data.put("EndDownDate", String.valueOf(null));    //�¼ܽ���ʱ��
					data.put("PageNo", String.valueOf(pageno));    //ҳ��
					data.put("PageSize", "20");  //ÿҳ������Ĭ��40�����100
					String sign=Utils.get_sign(data, method, now);
					String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
			        String responseProductData = Utils.sendByPost(Params.url,output_to_server);
			        Log.info("ȡ�ֿ���Ʒ��������:��"+responseProductData);
			        
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
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						
						
//						Map<String, String> stockparams = new HashMap<String, String>();
//				        //ϵͳ����������
//						stockparams.put("appKey", app_key);
//						stockparams.put("sessionKey", token);
//						stockparams.put("format", format);
//						stockparams.put("method", "yhd.serial.product.get");
//						stockparams.put("ver", ver);
//						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//						
//						stockparams.put("productId", String.valueOf(productId));
						now=new Date();
						data=new JSONObject();
						data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
//						data.put("VendorItemId",String.valueOf(productId));  //��������ƷID
						data.put("ProductNo", String.valueOf(productId));  //����
//						data.put("ItemName", String.valueOf(null)); //��Ʒ����
//						data.put("BrandName", String.valueOf(null)); //Ʒ������
//						data.put("ItemStatus", String.valueOf(null));  //��Ʒ״̬(1-���� 2-�¼�)
//						data.put("StartShowDate", String.valueOf(null)); //�ϼܿ�ʼʱ��
//						data.put("EndShowDate", String.valueOf(null));    //�ϼܽ���ʱ��
//						data.put("StartDownDate", String.valueOf(null));  //�¼ܿ�ʼʱ��
//						data.put("EndDownDate", String.valueOf(null));    //�¼ܽ���ʱ��
//						data.put("PageNo", String.valueOf(pageno));    //ҳ��
//						data.put("PageSize", "20");  //ÿҳ������Ĭ��40�����100
						sign=Utils.get_sign(data, method, now);
						output_to_server=Utils.post_data_process(method, data, now, sign).toString();        
						String responseData = Utils.sendByPost(Params.url,output_to_server);
						 Log.info("ȡ�ֿ���Ʒ���鷵������:��"+responseData);
						JSONObject responsestock=new JSONObject(responseData);
						
						
						JSONArray childseriallist=responsestock.getJSONArray("Result").getJSONObject(0).getJSONArray("Skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("VendorSkuId");
							//long skuid=childserial.optLong("VendorSkuId");
							
							
							//JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							

								int quantity=0;
								long warehouseId=0;
								
								StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),sku+"-"+String.valueOf(warehouseId),sku,quantity) ;

						}
					}

					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					
					pageno++;
				}
				
			
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
