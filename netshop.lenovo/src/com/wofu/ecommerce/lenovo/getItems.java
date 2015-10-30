package com.wofu.ecommerce.lenovo;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.lenovo.util.CommHelper;
import com.wofu.business.stock.StockManager;
public class getItems extends Thread {
	private static String jobname = "��ȡ����˵��Ʒ��ҵ";
	private static String pageSize = "20" ;
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection conn = null;

			try {												
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				getAllItems(conn);
				Log.info("����˵ȡ��Ʒ��ҵ���!");
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
		//Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		
		Log.info("��ʼȡ����˵��Ʒ");
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
					String apimethod="/goods/goods_list?";
					HashMap<String,Object> map = new HashMap<String,Object>();
			        map.put("page", String.valueOf(pageIndex));
			        map.put("page_size", Params.pageSize);
			        map.put("vcode", Params.vcode);
			        map.put("apimethod", apimethod);
			        String responseText = CommHelper.doGet(map,Params.url);
					Log.info("��������: "+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText);
					if(responseObj.getInt("code")==1){   //û�����ݷ���
						
						return;
					}
					if(!responseObj.isNull("error_code")){   //��������
						Log.info("��ȡ����˵��Ʒ�б����������: "+responseObj.getString("error_code"));
						return;
					}
					
					//��ҳ��
					int goodsNum= Integer.parseInt(responseObj.getString("total_num"));
					int  pageTotal =goodsNum>=Integer.parseInt(pageSize)?(goodsNum %Integer.parseInt(pageSize)==0?goodsNum /Integer.parseInt(pageSize):(goodsNum /Integer.parseInt(pageSize)+1)):1;
					
					if (pageTotal ==0)
					{				
						k=5;
						break;
					}
					//��Ʒ����
					JSONArray items = responseObj.getJSONArray("info");
					for(int i = 0 ; i < items.length() ; i++)
					{
						
						JSONObject itemInfo = items.getJSONObject(i) ;
						//����˵��Ʒ���
						String itemID = itemInfo.getString("twitter_id");
						//��Ʒ���� 
						String itemName = new String(itemInfo.getString("goods_title").getBytes(),"gbk");
						//����
						String goods_no =itemInfo.getString("goods_no");
						/*if("".equals(itemID)){  //��Ʒ����Ϊ�գ�����
							break;
						}*/
						//��Ʒ���
						String stockCount="";
						     //������Ʒ�����дstockconfigsku��
							JSONArray chileItem = itemInfo.getJSONArray("stocks");
							int totalCount=0;
								for(int j = 0 ; j < chileItem.length() ; j++)
								{	
									JSONObject item = chileItem.getJSONObject(j) ;
									//sku
									StringBuilder str = new StringBuilder();
									if(!item.isNull("1st"))
									str.append("1st=").append(URLEncoder.encode(new String(item.getString("1st").getBytes(),"gbk")));
									if(!item.isNull("2rd"))
										str.append("&2rd=").append(URLEncoder.encode(new String(item.getString("2rd").getBytes(),"gbk")));
									//�ⲿsku
									String sku = item.getString("goods_code");
									
									Log.info("��Ʒ���: "+sku);
									//���  
									stockCount=String.valueOf(item.getInt("repertory"));
									totalCount+=Integer.parseInt(stockCount);
									Log.info("��ȡ���µ�SKU: "+sku);
									StockManager.addStockConfigSku(dao, orgid,itemID,str.toString(),sku,Integer.valueOf(stockCount).intValue()) ;
									n++;
								}
								//
								StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),itemID,goods_no,itemName,totalCount) ;
								m++;
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
			
				Log.info("ȡ������˵����Ʒ��:"+String.valueOf(m)+" ��SKU��:"+String.valueOf(n));
				
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