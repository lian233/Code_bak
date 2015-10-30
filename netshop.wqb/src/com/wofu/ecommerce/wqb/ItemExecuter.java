package com.wofu.ecommerce.wqb;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.wqb.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "ȡ��������Ʒ������ҵ";
	
	private String url="";

	private String app_key  = "";
	
	private String username="";
	
	private String app_secret="";
	
	private String format="";
	
	private String tradecontactid="";
	private String pageSize="";

	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		tradecontactid=prop.getProperty("tradecontactid");
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");
		pageSize=prop.getProperty("pageSize");
		try {		
			updateJobFlag(1);
			
			getOnSaleItems();
			//getInStockItems();
	
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
		
		Log.info("��ʼȡ�������ϼ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Map<String, String> productparams = new HashMap<String, String>();
			        //ϵͳ����������
					productparams.put("appKey", app_key);
					productparams.put("user", app_key);
					productparams.put("format", format);
					productparams.put("method", "IOpenAPI.GetProducts");
					productparams.put("pageSize", pageSize);
			        productparams.put("pageIndex", String.valueOf(pageno));
			        String responseProductData = Utils.sendByPost(productparams, app_secret, "IOpenAPI.GetProducts",url);
			        Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount=Integer.parseInt(responseproduct.getString("SumNum"));
					
					JSONArray productlist=responseproduct.getJSONArray("Result");
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						String productId=product.optString("ProId");
						String productCode=product.optString("ProNo");
						String productCname=product.optString("ProTitle");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						if(productCname.indexOf("�ʷѲ���")>0) continue;
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),productId,productCode,productCname,0) ;
						
						JSONArray childseriallist=product.getJSONArray("ProductSpec");
						
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("ProSkuNo");
								
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),productCode+"-"+sku,sku,0) ;
								
						}
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/Float.parseFloat(pageSize)))).intValue()) break;
					
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
}
