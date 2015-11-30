package com.wofu.ecommerce.miya;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.miya.Params;
import com.wofu.ecommerce.miya.utils.Utils;
public class GetItemExecuter extends Executer{
	
	private static String jobName = "ȡ��ѿ��Ʒ������ҵ";
	
	private String url="";

	private String vendor_key = "";
	
	private String secret_key="";

	private String tradecontactid="";



	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		tradecontactid=prop.getProperty("tradecontactid");
		vendor_key=prop.getProperty("vendor_key");
		secret_key=prop.getProperty("secret_key");
		
		System.out.println(url);
		System.out.println(tradecontactid);
		System.out.println(vendor_key);
		System.out.println(secret_key);
		try {		
			updateJobFlag(1);
			
			getOnSaleItems();
	
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
		
		Log.info("��ʼȡ��ѿ����Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Map<String, String> itemlistparams = new HashMap<String, String>();
			        //ϵͳ����������
					itemlistparams.put("method", "mia.item.list");
					itemlistparams.put("vendor_key", vendor_key);
					itemlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			        //Ӧ�ü��������
					itemlistparams.put("page", String.valueOf(pageno));
					itemlistparams.put("page_size", "20");
					String responseOrderListData = Utils.sendByPost(itemlistparams, secret_key, url);
					JSONObject responseproduct=new JSONObject(responseOrderListData);
//					Log.info("��ȡ��Ʒ���ص�����"+Utils.Unicode2GBK(responseOrderListData));
					JSONObject item_list_response=responseproduct.getJSONObject("content");
					int totalCount=item_list_response.optInt("total");
					JSONArray itemlist=item_list_response.getJSONArray("item_list");
					System.out.println(itemlist.length());
					System.out.println("����"+totalCount+"��ε�����"+itemlist.length()+"��ҳ��Ϊ"+(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()+"��ǰҳ��Ϊ"+String.valueOf(pageno));
					for(int i=0;i<itemlist.length();i++)
					{
						JSONObject product=itemlist.getJSONObject(i);
						String productId=product.optString("sku_id");//����
						String productCode=product.optString("sku_id");//����
						String productCname=product.optString("name");//��Ʒ����
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),productId,productCode+"_"+i,productCname,0) ;
						//stockConfig(DataCentre dao,int orgid,int tradecontactid,String itemid,String itemcode,String title,int qty) 
						JSONArray responsestock=product.getJSONArray("size_barcode");
						System.out.println("sku����"+responsestock.length());
						for(int m=0;m<responsestock.length();m++)
						{
							JSONObject childserial=responsestock.optJSONObject(m);
							String sku=childserial.optString("item_barcode");//sku
							String skuid=product.optString("sku_id");//����
							int qty=childserial.optInt("item_quantity");//����
							Log.info("skuΪ"+ sku +" ����Ϊ "+qty+"����Ϊ"+orgid);
							StockManager.addStockConfigSku(this.getDao(), orgid,productId,skuid+"_"+m,sku,qty) ;
							//addStockConfigSku(DataCentre dao,int orgid,String itemid,String skuid,String sku,int qty) 
							
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
	
	public String time() {
		Long time= System.currentTimeMillis()/1000;
		return time.toString();
	}
	
}
