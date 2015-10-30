package com.wofu.ecommerce.ecshop;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import sun.misc.GC.LatencyRequest;

import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.util.CommHelper;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
/**
 * ȡecshop��Ʒִ����
 * @author Administrator
 *
 */
public class GetItems extends Thread {
	private static String jobName = "��ȡecshop��Ʒ��ҵ";
	private String tradecontactid="";
	private static String lasttimeconfvalue = Params.username+"��ȡecshop�����޸���Ʒ";
	private static String lasttime = "";
	private static long daymillis = 24*60*60*60*1000L;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public void run() {

		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	

				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				/**
				 * ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
				 */
				//��ȡecshop�¶��� 
				getAllItems(connection) ;
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
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
		
		Log.info("��ʼȡecshop��Ʒ��ҵ��ʼ");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn,sql);
		//��ǰ������Ʒ�޸�ʱ��
		long modifiedDate = Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()/1000L;
		for(int k=0;k<10;)
		{
			
			try 
			{

				int pageIndex = 1 ;
				boolean hasNextpage = true ;
				
				while(hasNextpage)
				{
					//������
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//������
					String apimethod="search_goods_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L );
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", Params.pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        //��������
			        
			        Log.info("��"+pageIndex+"ҳ");
					String responseText = CommHelper.doRequest(reqMap,Params.url);
					Log.info("��������Ϊ:��"+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText);
					if(!"success".equals(responseObj.getString("result"))){   //��������
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //û�н��
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
								return;
							}catch(ParseException e)
							{
								Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
							Log.info("û�п��õ���Ʒ!");
						}else{
							Log.warn("ȡ����������,������: "+operCode);
						}
						
						break;
					}
					
					
					//��ҳ��
					String pageTotal ="";
					JSONObject itemInfos =responseObj.getJSONObject("info");
					String itemTotal = itemInfos.getString("counts");
					Log.info("����Ʒ��Ϊ�� "+itemTotal);
					if (itemTotal==null || itemTotal.equals("") || itemTotal.equals("0"))
					{				
						if (n==1)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
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
						break;
					}
					int itemTotaltemp=Integer.parseInt(itemTotal);
					int pageSizeTemp = Integer.parseInt(Params.pageSize);
					pageTotal=String.valueOf(itemTotaltemp<pageSizeTemp?1:(itemTotaltemp/pageSizeTemp+itemTotaltemp%pageSizeTemp));
					//��Ʒ����
					JSONArray items = itemInfos.getJSONArray("data_info");
					for(int i = 0 ; i < items.length() ; i++)
					{
						try{
							JSONObject itemInfo = items.getJSONObject(i) ;
							//ecshop��Ʒ���
							String itemID = itemInfo.getString("goods_id");
							//��Ʒ���� 
							String itemName = new String(itemInfo.getString("goods_name").getBytes(),"gbk");
							//����
							String outerItemID =itemInfo.getString("goods_sn");
							long modifiedTemp = Long.parseLong(itemInfo.getString("last_modify"));
							if(modifiedTemp>modifiedDate) modifiedDate=modifiedTemp;
							String stockCount="0";
							if(!"null".equals(itemInfo.get("product_number")))
								stockCount = itemInfo.getString("product_number");
							Log.info("stockCount: "+stockCount);
							if("".equals(itemID)){  //��Ʒ����Ϊ�գ�����
								break;
							}
							//��Ʒ���
											//sku
							String sku = itemInfo.getString("product_sn");
							String skuId = itemInfo.getString("product_id");
											//�ⲿsku
								//���   String produceCode,String app_key,String app_Secret,String format,String url
								Log.info("��ȡ���µ�SKU: "+sku);
								StockManager.addStockConfigSku(dao, orgid,itemID,skuId,sku,Integer.valueOf(stockCount).intValue()) ;
										
									StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),itemID,outerItemID,itemName,Integer.parseInt(stockCount)) ;
									m++;
									
									
						}catch(Exception ex){
							ex.printStackTrace();
							Log.warn("ecshopȡ��Ʒ����,������Ϣ: "+ex.getMessage());
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
				//�޸�ȡ��Ʒ����ʱ��
				if(modifiedDate > Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()/1000L){
					try{
						String value=Formatter.format(new Date(modifiedDate*1000L), Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lasttimeconfvalue, value);
					}catch(JException ex){
						Log.error("�޸�ȡ��Ʒ�����޸�ʱ�����", ex.getMessage());
					}
				}
				
				Log.info("ȡ��ecshop����Ʒ��:"+String.valueOf(m)+" ��SKU��:"+String.valueOf(n));
				
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				if(conn!=null && !conn.getAutoCommit())
					conn.rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
}