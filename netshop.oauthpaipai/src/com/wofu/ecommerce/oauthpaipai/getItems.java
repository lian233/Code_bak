package com.wofu.ecommerce.oauthpaipai;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;
public class getItems extends Thread{
	private static String jobName = "ȡ��������Ʒ������ҵ";
	private boolean is_updating=false;
	private static String lasttimeconfvalue=Params.username+"ȡ��Ʒ����ʱ��";
	private String lasttime;
	private static long daymillis=24*60*60*1000L;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private static int orgid=0;
	
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		Connection connection = null;
		do {		
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getAllItems(connection);
			
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_updating = false;
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

	private void getAllItems(Connection conn) throws Exception
	{
		int pageindex=1;
		int countTotal=0;
		int pagesize=10;
		
		Log.info("��ʼȡ��Ʒ����");
	
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
	
		ECSDao dao=new ECSDao(conn);
		if(orgid==0){
			String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
			orgid=dao.intSelect(sql);
		}
		Log.info("orgid: "+orgid);
		int itemcount=0;
		int skucount=0;
		for(int k=0;k<5;)
		{
			try
			{
				/*
				 * 	1.�����У� 
					2.�ֿ��У����״̬���������¼ܵ�+�����¼ܵ�+��ʱ�ϼ�+��δ�ϼܵ� 
					3.���¼ܵ� 
					4.�����¼ܵ� 
					5.�ȴ��ϼ� 
					6.��ʱ�ϼ� 
					7.��δ�ϼ� 
					8.����� 
					9.�ȴ����� 
					10.ɾ������Ʒ
				 */
				
				while(true)
				{
				
					PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(Params.spid,Params.secretkey, Params.token, Long.valueOf(Params.uid));
					
					sdk.setCharset(Params.encoding);
					
					HashMap<String, Object> params = sdk.getParams("/item/sellerSearchItemList.xhtml");
					
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
			
					params.put("pageIndex", String.valueOf(pageindex));
					params.put("pageSize", String.valueOf(pagesize));
					params.put("sellerUin", Params.uid);
					params.put("orderType", "0");
					params.put("modifyTimeBegin", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					params.put("modifyTimeEnd", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					
					String result = sdk.invoke();
					//Log.info("result: "+result);
					Document itemlistdoc = DOMHelper.newDocument(result.toString(),Params.encoding);
					Element itemlisturlset = itemlistdoc.getDocumentElement();
					String errorcode = DOMHelper.getSubElementVauleByName(itemlisturlset, "errorCode");
					String errormessage = DOMHelper.getSubElementVauleByName(itemlisturlset, "errorMessage");
					
					if(errorcode.equals("0"))
					{	
				
						NodeList itemlist=itemlisturlset.getElementsByTagName("item");
						
						if (itemlist.getLength()==0)
						{
							if (pageindex==1)		
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
							k=10;
							break;
						}
						
						for(int i=0;i<itemlist.getLength();i++)
						{
							try{
								itemcount=itemcount+1;
								
								Element item=(Element) itemlist.item(i);
								String itemcode=DOMHelper.getSubElementVauleByName(item, "itemCode");
								String itemlocalcode=DOMHelper.getSubElementVauleByName(item, "itemLocalCode");
								String itemname=DOMHelper.getSubElementVauleByName(item, "itemName");
								String stockcount=DOMHelper.getSubElementVauleByName(item, "stockCount");
								String lastModifyTime=DOMHelper.getSubElementVauleByName(item, "lastModifyTime");
								
								if (stockcount.equals("")||stockcount==null) stockcount="0";
								
								Log.info("���� "+itemlocalcode+" "+lastModifyTime);
								
								StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),itemcode,
										itemlocalcode,itemname,Integer.valueOf(stockcount).intValue()) ;
				
								PaiPaiOpenApiOauth itemsdk = new PaiPaiOpenApiOauth(Params.spid, Params.secretkey, Params.token, Long.valueOf(Params.uid));
								
								itemsdk.setCharset(Params.encoding);
								
								HashMap<String, Object> itemparams = itemsdk.getParams("/item/getItem.xhtml");
						
								itemparams.put("itemCode", itemcode);
							
								String itemresult = itemsdk.invoke();;	
								//Log.info("itemresult: "+itemresult);
								Document itemdetaildoc = DOMHelper.newDocument(itemresult.toString(),Params.encoding);
								Element itemdetailset = itemdetaildoc.getDocumentElement();
								String itemerrorcode = DOMHelper.getSubElementVauleByName(itemdetailset, "errorCode");
								String itemerrormessage = DOMHelper.getSubElementVauleByName(itemdetailset, "errorMessage");
								
								if(itemerrorcode.equals("0"))
								{	
										
									NodeList stocknodes = itemdetailset.getElementsByTagName("stock");
									
				
					
									for (int j = 0; j < stocknodes.getLength(); j++) {
										try{
											Element stockelement = (Element) stocknodes.item(j);	
											
											String stockLocalCode=DOMHelper.getSubElementVauleByName(stockelement, "stockLocalCode");
											String skuid=DOMHelper.getSubElementVauleByName(stockelement, "skuId");
											String skulastModifyTime=DOMHelper.getSubElementVauleByName(stockelement, "lastModifyTime");
											String skustockcount=DOMHelper.getSubElementVauleByName(stockelement, "stockCount");
											if (skustockcount.equals("")||skustockcount==null) skustockcount="0";
											
											int qty=Integer.valueOf(skustockcount).intValue();
											
											skucount=skucount+1;
											
											Log.info("SKU "+stockLocalCode+" "+skulastModifyTime);
											
											StockManager.addStockConfigSku(dao, orgid,itemcode,
													skuid,stockLocalCode,qty) ;
										}catch(Exception ex){
											if(conn!=null && !conn.getAutoCommit()){
												conn.rollback();
											}
											Log.error(jobName, ex.getMessage());
										}
							
										
										
						
				
									}
								}
								else
								{
									throw new JException("ȡ������Ʒ��ϸ����! ������Ϣ:"+errormessage);
								}
								
							
								//����ͬ����������ʱ��
				                if (Formatter.parseDate(lastModifyTime, Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
				                {
				                
				                	modified=Formatter.parseDate(lastModifyTime, Formatter.DATE_TIME_FORMAT);
				                }
							}catch(Exception ex){
								if(conn!=null && !conn.getAutoCommit()){
									conn.rollback();
								}
								Log.error(jobName, ex.getMessage());
							}
							
						}
						
						countTotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(itemlisturlset, "countTotal"));
						if(pageindex>=Math.ceil((double) countTotal/pagesize))
						{
							k=10;			
							break;
						}
						
						pageindex=pageindex+1;	
						
					}else
					{
						Log.info("ȡ������Ʒ�б����! ������Ϣ:"+errormessage);
						k=5;			
						break;
					}
					
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				break;
			}catch(Exception e)
			{	
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit())
					conn.rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			} 
		}
		Log.info("����Ʒ��:"+String.valueOf(itemcount)+" ��SKU��:"+String.valueOf(skucount));
	}
	
	public String toString()
	{
		return jobName + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
