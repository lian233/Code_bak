package com.wofu.ecommerce.ylw;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ylw.util.CommHelper;
import com.wofu.business.stock.StockManager;
/**
 * ȡ������Ʒִ����
 * @author Administrator
 *
 */
public class getItemsExecuter extends Executer {
	private static String jobName = "��ȡ������Ʒ��ҵ";
	private String tradecontactid="";
	private static String pageSize = "" ;
	private static String appSecret = "" ;
	private static String format = "" ;
	private static String versionNo = "" ;
	private static String appKey = "" ;
	private static String url = "" ;


	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize=prop.getProperty("pageSize");
		appSecret=prop.getProperty("appsecret");
		format=prop.getProperty("format");
		versionNo=prop.getProperty("versionNo");
		appKey=prop.getProperty("appkey");
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
		
		Log.info("��ʼȡ������Ʒ��ҵ��ʼ");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for(int k=0;k<10;)
		{
			
			try 
			{

				int pageIndex = 1 ;
				boolean hasNextpage = true ;
				
				while(hasNextpage)
				{
					//������
					 String apiMethod="suning.custom.item.query";
					 HashMap<String,String> reqMap = new HashMap<String,String>();
				     reqMap.put("status", "2");  //ֻȡ������Ʒ
				     reqMap.put("pageNo", String.valueOf(pageIndex));
				     reqMap.put("pageSize", pageSize);
				     HashMap<String,String> map = new HashMap<String,String>();
				     map.put("appSecret", appSecret);
				     map.put("appMethod", apiMethod);
				     map.put("format", format);
				     map.put("versionNo", versionNo);
				     map.put("appKey", appKey);
				     //��������
					 String responseText = CommHelper.doRequest(map,url);
					Log.info("��������: "+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
					if(responseText.indexOf("sn_error")!=-1){   //��������
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //û�н��
						Log.info("û�п��õ���Ʒ��");
						return;
					}else{
						if(!"".equals(operCode))
						{
							Log.error("������ȡ��Ʒ��ҵ", "��ȡ��Ʒ��ҵʧ��,operCode:"+operCode);
						}
						return;
						}
					}
					
					
					//ͳ����Ϣ
					JSONObject totalInfo = responseObj.getJSONObject("sn_head");
					//��ҳ��
					String pageTotal = String.valueOf(totalInfo.get("pageTotal"));
					
					if (pageTotal==null || pageTotal.equals("") || pageTotal.equals("0"))
					{				
						k=10;
						break;
					}
					//��Ʒ����
					JSONArray items = responseObj.getJSONObject("sn_body").getJSONArray("item");
					for(int i = 0 ; i < items.length() ; i++)
					{
						try{
							JSONObject itemInfo = items.getJSONObject(i) ;
							//������Ʒ���
							String itemID = itemInfo.getString("productCode");
							//��Ʒ���� 
							String itemName = itemInfo.getString("productName");
							//����
							String outerItemID =itemInfo.getString("itemCode");
							if("".equals(itemID)){  //��Ʒ����Ϊ�գ�����
								break;
							}
							//��Ʒ���
							String stockCount="";
							if(itemInfo.toString().indexOf("childItem") == -1){  //û������Ʒ�����
								stockCount=StockUtils.getInventoryByproductCode(itemID,appKey,appSecret,format,url);
								StockManager.stockConfig(dao, orgid,Integer.valueOf(tradecontactid),itemID,outerItemID,itemName,Integer.valueOf(stockCount).intValue()) ;
								m++;
							}else{     //������Ʒ�����дstockconfigsku��
								JSONArray chileItem = itemInfo.getJSONArray("childItem");
								int totalCount=0;
									for(int j = 0 ; j < chileItem.length() ; j++)
									{	
										try{
											JSONObject item = chileItem.getJSONObject(j) ;
											//sku
											String sku = item.getString("itemCode");
											//�ⲿsku
											String subItemID = item.getString("productCode");
											if("".equals(subItemID)){  //��Ʒ����Ϊ�գ�����
												Log.info("����Ʒ����Ϊ��,����Ʒ����Ϊ:��"+itemID);
												break;
											}
											Log.info("��Ʒ���: "+subItemID);
											//���   String produceCode,String app_key,String app_Secret,String format,String url
											stockCount=StockUtils.getInventoryByproductCode(subItemID,appKey,appSecret,format,url);
											totalCount+=Integer.parseInt(stockCount);
											Log.info("��ȡ���µ�SKU: "+sku);
											StockManager.addStockConfigSku(dao, orgid,itemID,subItemID,sku,Integer.valueOf(stockCount).intValue()) ;
										}catch(Exception ex){
											Log.warn("����ȡ��Ʒд��sku��Ϣ����,������Ϣ: "+ex.getMessage());
											if (conn != null && !conn.getAutoCommit())
												conn.rollback();
											continue;
										}
										
									}
									//
									StockManager.stockConfig(dao, orgid,Integer.valueOf(tradecontactid),itemID,outerItemID,itemName,totalCount) ;
									m++;
								}
						}catch(Exception ex){
							Log.warn("����ȡ��Ʒ����,������Ϣ: "+ex.getMessage());
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
							continue;
						}
						
						}
			
					//�Ƿ�����һҳ
					if(pageIndex < Integer.parseInt(pageTotal))
					{
						hasNextpage = true ;
						pageIndex ++ ;
						Log.info("ҳ��:"+pageIndex);
					}
					else
					{
						hasNextpage = false ;
					}
			}
				
				Log.info("ȡ����������Ʒ��:"+String.valueOf(m)+" ��SKU��:"+String.valueOf(n));
				
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