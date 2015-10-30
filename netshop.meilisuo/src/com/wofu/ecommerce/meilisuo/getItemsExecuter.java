package com.wofu.ecommerce.meilisuo;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilisuo.util.CommHelper;
import com.wofu.business.stock.StockManager;
/**
 * ȡ����˵��Ʒִ����
 * @author Administrator
 *
 */
public class getItemsExecuter extends Executer {
	private static String jobName = "��ȡ����˵��Ʒ��ҵ";
	private String tradecontactid="";
	private static String pageSize = "" ;
	private static String url = "" ;
	private static String vcode = "" ;


	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize=prop.getProperty("pageSize");
		vcode=prop.getProperty("vcode");
		url=prop.getProperty("url");
		tradecontactid=prop.getProperty("tradecontactid");
		
			Connection conn = null;

			try {			
				conn = this.getDao().getConnection();
				updateJobFlag(1);
				getAllItems(conn);
				UpdateTimerJob();
				Log.info(jobName, "ִ����ҵ�ɹ� ["
						+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
						+ "] �´δ���ʱ��: "
						+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		
			} catch (Exception e) {
				try {
					
					if (this.getConnection() != null && !this.getConnection().getAutoCommit())
						this.getConnection().rollback();
					
					if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
						this.getExtconnection().rollback();
					
					if (this.getExecuteobj().getSkip() == 1) {
						UpdateTimerJob();
					} else
						UpdateTimerJob(Log.getErrorMessage(e));
					
					
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
	/*
	 * status=����״̬��1�����ڴ���2������ɹ���3������ʧ�ܡ�
	 */
	private void getAllItems(Connection conn) throws Exception
	{
		int m=0,n=0;
		//Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=dao.intSelect(sql);
		Log.info("��ʼȡ����˵��Ʒ��ҵ��ʼ");
		
		for(int k=0;k<10;)
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
			        map.put("page_size", pageSize);
			        map.put("vcode", vcode);
			        map.put("apimethod", apimethod);
				     //��������
					 String responseText = CommHelper.doRequest(map,url,"get");
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
					
					
					//ͳ����Ϣ
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
									if(!item.isNull("2rd") && item.getString("2rd").length()<3){
										Log.info("2rd: "+new String(item.getString("2rd").getBytes(),"gbk"));
										str.append("&2rd=").append(URLEncoder.encode(new String(item.getString("2rd").getBytes(),"gbk")));
									}
										
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
								StockManager.stockConfig(dao, orgid,Integer.valueOf(tradecontactid),itemID,goods_no,itemName,totalCount) ;
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
				if (++k >= 10)
					throw e;
				if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
					this.getDao().getConnection().rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
}