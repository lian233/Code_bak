package com.wofu.ecommerce.papago8;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.systemmanager.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.papago8.util.CommHelper;
import com.wofu.business.stock.StockManager;
public class getItems extends Thread {
	private static String jobname = "��ȡpapago8��Ʒ��ҵ";
	private static String lasttime="";
	private static String lastTimeConfig = Params.username+"ȡ�����޸���Ʒʱ��";
	private static long daymills = 24*60*60*1000L;
	
	private static String pageSize = "20" ;
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection conn = null;

			try {												
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttime = PublicUtils.getConfig(conn, lastTimeConfig, "");
				getAllItems(conn);
				Log.info("papago8ȡ��Ʒ��ҵ���!");
			} catch (Exception e) {
				try {
					e.printStackTrace() ;
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	/*
	 * status=����״̬��1�����ڴ���2������ɹ���3������ʧ�ܡ�
	 */
	private void getAllItems(Connection conn) throws Exception
	{
		int m=0,n=0;
		String startTime = Formatter.format(new Date(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()+1000L), Formatter.DATE_TIME_FORMAT);
		String endTime = Formatter.format(new Date(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()+daymills),Formatter.DATE_TIME_FORMAT);
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		
		Log.info("��ʼȡpapago8��Ʒ");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=dao.intSelect(sql);
		for(int k=0;k<5;)
		{
			
			try 
			{

				int pageIndex = 0 ;
				boolean hasNextpage = true ;
				while(hasNextpage)
				{
					//������
					String apimethod="QueryProduct.aspx?";
					HashMap<String,Object> map = new HashMap<String,Object>();
			        map.put("page", String.valueOf(pageIndex));
			        map.put("page_size", Params.pageSize);
			        map.put("Key", Params.Key);
			        map.put("apimethod", apimethod);
			        map.put("start_modified", URLEncoder.encode(startTime,"utf-8"));
			        map.put("end_modified", URLEncoder.encode(endTime,"utf-8"));
			        map.put("format", "json");
			        String responseText = CommHelper.doGet(map,Params.url);
					Log.info("��������: "+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("products_response");
					if("0".equals(responseObj.getString("total_results"))){   //û�����ݷ���
						if(Formatter.parseDate(lasttime, Formatter.DATE_FORMAT).compareTo(Formatter.parseDate(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT), Formatter.DATE_FORMAT))<0){
							try{
								String value = Formatter.format(new Date(Formatter.parseDate(lasttime, Formatter.DATE_FORMAT).getTime()+daymills), Formatter.DATE_FORMAT)+" 00:00:00";
								PublicUtils.setConfig(conn, lastTimeConfig, value);
							}catch(Exception e){
								Log.error("�޸�ȡ��Ʒ����ʱ�����", e.getMessage());
							}
							
						}
						return;
						
					}
					
					//��ҳ��
					int goodsNum= Integer.parseInt(responseObj.getString("total_results"));
					int  pageTotal =goodsNum>=Integer.parseInt(pageSize)?(goodsNum %Integer.parseInt(pageSize)==0?goodsNum /Integer.parseInt(pageSize):(goodsNum /Integer.parseInt(pageSize)+1)):1;
					
					if (pageTotal ==0)
					{				
						k=5;
						break;
					}
					//��Ʒ����
					JSONArray items = responseObj.getJSONObject("products").getJSONArray("product");
					for(int i = 0 ; i < items.length() ; i++)
					{
						
						JSONObject itemInfo = items.getJSONObject(i) ;
						//papago8��Ʒ���
						String itemID = itemInfo.getString("num_iid");
						//��Ʒ���� 
						String itemName = itemInfo.getString("pro_name");
						//����
						String goods_no =itemInfo.getString("pro_no");
						/*if("".equals(itemID)){  //��Ʒ����Ϊ�գ�����
							break;
						}*/
						//��Ʒ���
						String stockCount="";
						     //������Ʒ�����дstockconfigsku��
							JSONArray chileItem = itemInfo.getJSONObject("skulist").getJSONArray("sku");
							int totalCount=0;
								for(int j = 0 ; j < chileItem.length() ; j++)
								{	
									JSONObject item = chileItem.getJSONObject(j) ;
									String skuid = item.getString("sys_id");
									//�ⲿsku
									String sku = item.getString("skuid");
									
									Log.info("��Ʒ���: "+sku);
									//���  
									stockCount=item.getString("num");
									totalCount+=Integer.parseInt(stockCount);
									Log.info("��ȡ���µ�SKU: "+sku);
									StockManager.addStockConfigSku(dao, orgid,itemID,skuid,sku,Integer.valueOf(stockCount).intValue()) ;
									n++;
								}
								StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),itemID,goods_no,itemName,totalCount) ;
								m++;
								//��Ʒ�޸�ʱ��
								Date itemModified = Formatter.parseDate(itemInfo.getString("modified"), Formatter.DATE_TIME_FORMAT);
								if(modified.compareTo(itemModified)<0)  modified=itemModified;
							}
					
					//�Ƿ�����һҳ
					if(pageIndex < pageTotal-1)
					{
						hasNextpage = true ;
						pageIndex ++ ;
						Log.info("ҳ��:"+pageIndex+1);
					}
					else
					{
						hasNextpage = false ;
					}
				
						}
				if(modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0){
					try{
						String value = Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lastTimeConfig, value);
					}catch(Exception e){
						
					}
				}
			
				Log.info("ȡ��papago8����Ʒ��:"+String.valueOf(m)+" ��SKU��:"+String.valueOf(n));
				
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
}