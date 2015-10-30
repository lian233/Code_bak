/**
 * ��ȡ������Ʒ-���¿����
 */
package com.wofu.ecommerce.dangdang;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.business.stock.StockManager;
public class getItems extends Thread {
	private static String jobName = "��ȡ������Ʒ��ҵ";
	private static String lasttimeconfvalue=Params.username+"ȡ��Ʒ����ʱ��";
	private static long daymillis=24*60*60*1000L;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private String lasttime;

	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection conn = null;

			try {												
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttime=PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				
				getAllItems(conn);
				
			} catch (Exception e) {
				try {
					Log.error(jobName, e.getMessage());
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
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
		int m=0,n=0;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		Log.info("��ʼȡ������Ʒ");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn, sql);
		for(int k=0;k<5;)
		{
			
			try 
			{

				int pageIndex = 1 ;
				boolean hasNextpage = true ;
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date();
				
				while(hasNextpage)
				{
					Date temp = new Date();
					//������
					String methodName="dangdang.items.list.get";
					//������֤�� --md5;����
					String sign = CommHelper.getSign(Params.app_Secret, Params.app_key, methodName, Params.session,temp)  ;
					Hashtable<String, String> param = new Hashtable<String, String>() ;
					param.put("sign", sign) ;
					param.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
					param.put("app_key",Params.app_key);
					param.put("method",methodName);
					param.put("format","xml");
					param.put("session",Params.session);
					param.put("sign_method","md5");
					param.put("its", "9999") ;
					param.put("mte",Formatter.format(enddate, Formatter.DATE_FORMAT)) ;
					param.put("mts", Formatter.format(startdate, Formatter.DATE_FORMAT)) ;
					param.put("p", String.valueOf(pageIndex)) ;
					param.put("pageSize", "15") ;
					String responseText = CommHelper.sendRequest(Params.url,"GET",param,"") ;
					Document doc = DOMHelper.newDocument(responseText,Params.encoding) ;
					Element urlset = doc.getDocumentElement() ;
					if(DOMHelper.ElementIsExists(urlset, "Error")) 
					{
						Element error = (Element)urlset.getElementsByTagName("Error").item(0) ;
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode) || operCode != null)
						{
							Log.error(jobName, "��ȡ������Ʒ����ʧ�ܣ�������Ϣ��"+operCode+":"+operation) ;
							hasNextpage = false ;
							break;
						}
					}
					
					
					Element totalInfo = (Element)urlset.getElementsByTagName("totalInfo").item(0) ;
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal") ;
					Log.info("��ҳ�� ��"+pageTotal);
					Log.info("��ǰҳ: "+pageIndex);
					if (pageTotal==null || pageTotal.equals("") || pageTotal.equals("0"))
					{				
						if (pageIndex==1)		
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
						k=5;
						break;
					}
					
					Element itemsList = (Element)urlset.getElementsByTagName("ItemsList").item(0) ;
					NodeList itemInfoList = itemsList.getElementsByTagName("ItemInfo") ;
					for(int i = 0 ; i < itemInfoList.getLength() ; i++)
					{
						try{
							Element itemInfo = (Element)itemInfoList.item(i) ;
							String itemID = DOMHelper.getSubElementVauleByName(itemInfo, "itemID") ;
							String stockCount = DOMHelper.getSubElementVauleByName(itemInfo, "stockCount") ;
							String itemName = DOMHelper.getSubElementVauleByName(itemInfo, "itemName") ;
							
							
							String outerItemID ="";
							if (DOMHelper.ElementIsExists(itemInfo, "outerItemID"))
								outerItemID=DOMHelper.getSubElementVauleByName(itemInfo, "outerItemID") ;
							else
								outerItemID=DOMHelper.getSubElementVauleByName(itemInfo, "model") ;
							
							StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),itemID,outerItemID,itemName,Integer.valueOf(stockCount).intValue()) ;
							
							Date updateTime = Formatter.parseDate(DOMHelper.getSubElementVauleByName(itemInfo, "updateTime"),Formatter.DATE_TIME_FORMAT) ;
//							 if (updateTime.compareTo(modified)<=0)
//					            {
//					            	continue;
//					            }
							m++;
							temp = new Date();
							//������
							methodName="dangdang.item.get";
							//������֤�� --md5;����
							sign = CommHelper.getSign(Params.app_Secret, Params.app_key, methodName, Params.session,temp)  ;
							
							Hashtable<String, String> param1 = new Hashtable<String, String>() ;
							param1.put("sign", sign) ;
							param1.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
							param1.put("app_key",Params.app_key);
							param1.put("method",methodName);
							param1.put("format","xml");
							param1.put("session",Params.session);
							param1.put("sign_method","md5");
							param1.put("it", itemID) ;
							
							String itemdetailresponseText = CommHelper.sendRequest(Params.url, "GET",param1,"") ;
							
							Document itemdetaildoc = DOMHelper.newDocument(itemdetailresponseText, Params.encoding) ;
							Element result = itemdetaildoc.getDocumentElement() ;
						
							Element error = null;
							if(DOMHelper.ElementIsExists(result, "Error"))
								error = (Element)result.getElementsByTagName("Error").item(0) ;
							if(DOMHelper.ElementIsExists(result, "Result"))
								error = (Element)result.getElementsByTagName("Result").item(0) ;
							if(error != null && DOMHelper.ElementIsExists(error, "operCode"))
							{
								String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
								String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
								Log.info("��ȡ������Ʒ��ϸ����ʧ��,����:"+ outerItemID +",������Ϣ��"+operCode+":"+operation) ;
								
								continue;
							}
							
							
		
							Element itemDetail = (Element)result.getElementsByTagName("ItemDetail").item(0);
				
							if(DOMHelper.ElementIsExists(itemDetail, "SpecilaItemInfo"))
							{
								
								NodeList specilaItemInfo =  result.getElementsByTagName("SpecilaItemInfo") ;
								for(int j = 0 ; j < specilaItemInfo.getLength() ; j++)
								{
									try{
										Element skuInfo = (Element) specilaItemInfo.item(j) ;
										
										String quantity = DOMHelper.getSubElementVauleByName(skuInfo, "stockCount") ;
										String sku = DOMHelper.getSubElementVauleByName(skuInfo, "outerItemID") ;
										String subItemID = DOMHelper.getSubElementVauleByName(skuInfo, "subItemID") ;
										Log.info("SKU "+sku+" "+Formatter.format(updateTime,Formatter.DATE_TIME_FORMAT));
										n++;
										StockManager.addStockConfigSku(dao, orgid,itemID,subItemID,sku,Integer.valueOf(quantity).intValue()) ;
									}catch(Exception ex){
										if (conn != null && !conn.getAutoCommit())
											conn.rollback();
										Log.error(jobName, ex.getMessage());
									}
									
									
								}
							}
				
							//����ͬ����������ʱ��
				            if (updateTime.compareTo(modified)>0)
				            {
				            	modified=updateTime;
				            }
						}catch(Exception ex){
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
							Log.error(jobName, ex.getMessage());
							
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
				
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
}