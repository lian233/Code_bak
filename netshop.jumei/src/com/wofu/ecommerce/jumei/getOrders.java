package com.wofu.ecommerce.jumei;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class getOrders extends Thread {

	private static String jobname = "��ȡ������Ʒ������ҵ";
	
	private static long daymillis=5*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;

	public getOrders() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.jumei.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				getOrderList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jumei.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮������ж���
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		int pageno=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		Date modifiedTemp = modified;
		for(int k=0;k<10;)
		{
			try
			{
				/**while(true)
				{**/
					
					Map<String, String> orderlistparams = new HashMap<String, String>();
					String method="Order/GetOrder";
					//start_date - 1, end_date + 1  �����ӿڻ����������㷨������������������
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+2000L);
					Log.info(startdate.getTime()+"");
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis-1000L);
					Log.info(enddate.toLocaleString());
			       
					
					Map<String, String> paramMap = new HashMap<String, String>();
			        //ϵͳ����������
			        paramMap.put("client_id", Params.clientid);
			        paramMap.put("client_key", Params.clientkey);
			        paramMap.put("start_date", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        paramMap.put("end_date", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        paramMap.put("status", "2,7");
			        //paramMap.put("page", String.valueOf(pageno));
			        //paramMap.put("page_size", "50");
			       
			        String sign=JuMeiUtils.getSign(paramMap, Params.signkey, Params.encoding);
			        
			        paramMap.put("sign", sign);
			        
			        String responseData=CommHelper.sendRequest(Params.url+method, paramMap, "", Params.encoding);
			        Log.info("���ض�������:��"+responseData);
			        
					JSONObject responseresult=new JSONObject(responseData);
					
					int errorCount=responseresult.getInt("error");
					
					if (errorCount>0)
					{
						String errdesc=responseresult.getString("message");
						
						k=10;
						throw new JException(errdesc);
						
					}
					
										
					
					int i=1;
			
					JSONArray orderlist=responseresult.getJSONObject("result").getJSONArray("response");
								
					if (orderlist.length()==0)
					{	/**		
						if (i==1)		
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
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						k=10;
						break;
						**/
						if(i==1){
							try{
								//ÿ10����ȡһ�ζ��������û��ȡ�������Ļ�������ʱ��ȵ�ǰʱ���Сʱ����ҪС�Ļ�
								if(Formatter.parseDate((PublicUtils.getConfig(conn,lasttimeconfvalue,"").substring(0,16)+":00"),Formatter.DATE_TIME_FORMAT)
										.compareTo(Formatter.parseDate((Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00"),Formatter.DATE_TIME_FORMAT))<0){
									String value= Formatter.format(new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis),Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
									if(Formatter.parseDate(value, Formatter.DATE_TIME_FORMAT).compareTo(new Date())<=0){
										System.out.println("dd: "+value);
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);
										System.out.println("finish");
									}
									
									else{
										System.out.println("dd1");
										value= Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);
									}
										
								}else{
									System.out.println("dd2");
									String value= Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
									PublicUtils.setConfig(conn, lasttimeconfvalue, value);
								}
							}catch(Exception e){
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
									
					int invalidCount = 0;
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
											
						Order o=new Order();
					
						o.setObjValue(o, order);
					
						JSONObject receiverinfojsobj=order.getJSONObject("receiver_infos");
					
						o.setObjValue(o.getReceiver_info(),receiverinfojsobj);
						if(new Date(o.getTimestamp()*1000).compareTo(modifiedTemp)>0) invalidCount++;
										
						Log.info(o.getOrder_id()+" "+o.getStatus()+" "+Formatter.format(new Date(o.getTimestamp()*1000),Formatter.DATE_TIME_FORMAT));
						/*
						 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 *2��ɾ���ȴ���Ҹ���ʱ��������� 
						 */		
						String sku;
						String sql="";
						if (o.getStatus()== 2 ||o.getStatus()== 7)
						{	
							
							if (!OrderManager.isCheck("��������Ʒ����", conn, o.getOrder_id()))
							{
								if (!OrderManager.TidLastModifyIntfExists("��������Ʒ����", conn, o.getOrder_id(),new Date(o.getTimestamp()*1000)))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getProduct_infos().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getUpc_code();
										
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_id(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, String.valueOf(o.getStatus()),String.valueOf(o.getOrder_id()), sku, -item.getQuantity(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						
						//����ͬ����������ʱ��
		                if ((new Date(o.getTimestamp()*1000)).compareTo(modified)>0)
		                {
		                	modified=new Date(o.getTimestamp()*1000);
		                }
					}
						
					/**	
						
					//�ж��Ƿ�����һҳ
					if (pageno>=pageCount) break;
					
					pageno++;
					
					i=i+1;
					
				}**/
				//û����Ч����
				if(invalidCount==0){
					try{
						//ÿ10����ȡһ�ζ��������û��ȡ�������Ļ�������ʱ��ȵ�ǰʱ���Сʱ����ҪС�Ļ�
						if(Formatter.parseDate((PublicUtils.getConfig(conn,lasttimeconfvalue,"").substring(0,16)+":00"),Formatter.DATE_TIME_FORMAT)
								.compareTo(Formatter.parseDate((Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00"),Formatter.DATE_TIME_FORMAT))<0){
							String value= Formatter.format(new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis),Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
							if(Formatter.parseDate(value, Formatter.DATE_TIME_FORMAT).compareTo(new Date())<=0){
								System.out.println("dd: "+value);
								PublicUtils.setConfig(conn, lasttimeconfvalue, value);
								System.out.println("finish");
							}
							
							else{
								System.out.println("dd1");
								value= Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
								PublicUtils.setConfig(conn, lasttimeconfvalue, value);
							}
								
						}else{
							System.out.println("dd2");
							String value= Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
							PublicUtils.setConfig(conn, lasttimeconfvalue, value);
						}
					}catch(Exception e){
						Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
					}
					k=10;
					break;
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
