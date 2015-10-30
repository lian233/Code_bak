package com.wofu.ecommerce.miya;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.beibei.Params;
import com.wofu.ecommerce.miya.utils.Utils;
public class GetItemExecuter extends Executer{
	
	private static String jobName = "ȡ��������Ʒ������ҵ";
	
	private String url="";

	private String token = "";

	private String app_key  = "";
	
	private String username="";
	
	private String app_secret="";
	
	private String format="";
	
	private String ver="";

	private String tradecontactid="";



	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");

		token=prop.getProperty("token");
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");
		
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
		
		Log.info("��ʼȡ��������Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
			        orderlistparams.put("method", "beibei.outer.item.onsale.get");
					orderlistparams.put("app_id", Params.appid);
			        orderlistparams.put("session", Params.session);
			        orderlistparams.put("timestamp", time());
			        orderlistparams.put("version", Params.ver);
			        //Ӧ�ü��������
			        orderlistparams.put("page_no", String.valueOf(pageno));
			        orderlistparams.put("page_size","20");
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.secret, Params.url);
			        Log.info("ȡ��Ʒ��������:��"+responseOrderListData);
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					int totalCount=responseproduct.getInt("count");
					
					JSONArray productlist=responseproduct.getJSONArray("data");
					
					System.out.println(productlist.length());
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						long productId=product.optLong("iid");//��Ʒ���
						String productCode=product.optString("goods_num");//����
						String productCname=product.optString("title");//��Ʒ����
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						JSONArray responsestock=product.getJSONArray("sku");
						System.out.println("sku����"+responsestock.length());
						for(int m=0;m<responsestock.length();m++)
						{
							JSONObject childserial=responsestock.optJSONObject(m);
							String sku=childserial.optString("outer_id");
							long skuid=childserial.optLong("id");
							int qty=childserial.optInt("num");
							System.out.println("skuΪ"+ sku +" ����Ϊ "+qty);
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid),sku,qty) ;
								
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
