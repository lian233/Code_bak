package com.wofu.ecommerce.zdt;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.systemmanager.PublicUtils;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.zdt.utils.Utils;
public class GetItems extends Thread{
	private static String jobName = "ȡ�Ƶ�ͨ��Ʒ����";
	private static String lasttime = Params.username+"ȡ��Ʒ�����޸�ʱ��";
	private static String lasttimeValue;//ȡ��Ʒ�����޸�ʱ��
	private static final Long daymillis = 24*60*60*1000L;
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public void run() {
		Connection conn=null;
		while(true){
			try {
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttimeValue = PublicUtils.getConfig(conn, lasttime, Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOnSaleItems(conn);
				long currentTime = System.currentTimeMillis();
				while(System.currentTimeMillis()-currentTime<Params.waittime*1000L){
					Thread.sleep(1000L);
				}
			} catch (Exception e) {
				try {
					if(conn!=null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobName,"�ع�����ʧ��");
				}
				Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
				
			} finally {
				if(conn!=null)
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		
		
		
	}

	private void getOnSaleItems(Connection conn) throws Exception
	{

		int pageno=1;
		DataCentre dao = new ECSDao(conn);
		Log.info("��ʼȡ�Ƶ�ͨ�ϼ���Ʒ����");
		Date startdate = new Date(Formatter.parseDate(lasttimeValue, Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		Date modified = Formatter.parseDate(lasttimeValue, Formatter.DATE_TIME_FORMAT);
		Date enddate = new Date(startdate.getTime()+daymillis);
		//Log.info(startdate.toString());
		//Log.info(enddate.toString());
		Log.info(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
		Log.info(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn, sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Map<String, String> productparams = new HashMap<String, String>();
			        //ϵͳ����������
					//ϵͳ����������
					productparams.put("app_key", Params.app_key);
					productparams.put("format", Params.format);
					productparams.put("method", "ecm.product.list.get");
					productparams.put("sign_method", "MD5");
					productparams.put("v", Params.ver);
					productparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					productparams.put("start_modified", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					productparams.put("end_modified", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					productparams.put("page_no", String.valueOf(pageno));
					productparams.put("page_size", Params.pageSize);
			        String responseProductData = Utils.sendByPost(productparams, Params.app_secret, Params.url);
			        Log.info("ȡ��Ʒ��������:��"+responseProductData);
			        //{"res_data":{"total":0,"page_no":1,"page_size":50,"product_list":[]}}
					JSONObject responseproduct=new JSONObject(responseProductData);
					int totalCount=responseproduct.getJSONObject("res_data").getInt("total");
					if (totalCount==0)
					{				
						if (pageno==1L)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttime,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttime,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttime, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
					JSONArray productlist=responseproduct.getJSONObject("res_data").getJSONArray("product_list");
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						long productId=product.optLong("id");
						String productCode=product.optString("product_no");
						String productCname=product.optString("product_name");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						
						JSONArray childseriallist=product.getJSONArray("sku_list");
						
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("outer_id");
							String skuid=childserial.optString("id");
							int quantity = Integer.parseInt(childserial.getString("quantity"));
							StockManager.addStockConfigSku(dao, orgid,String.valueOf(productId),skuid,sku,quantity) ;
								
						}
						Date current  = Formatter.parseDate(product.getString("modify_time"), Formatter.DATE_TIME_FORMAT);
						if(current.compareTo(modified)>0)
							modified=current;
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/Float.parseFloat(Params.pageSize)))).intValue()) break;
					
					pageno++;
				}
				if (modified.compareTo(Formatter.parseDate(lasttimeValue, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttime, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
					
				}
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}
		Log.info(jobName+"ִ����ϣ�");
	}
	
}
