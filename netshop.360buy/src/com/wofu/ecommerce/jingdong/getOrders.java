package com.wofu.ecommerce.jingdong;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.domain.after.ReturnGoods;
import com.jd.open.api.sdk.domain.after.ReturnInfo;
import com.jd.open.api.sdk.domain.after.ReturnItem;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.domain.order.OrderResult;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.domain.ware.Sku;
import com.jd.open.api.sdk.request.Field;
import com.jd.open.api.sdk.request.after.AfterSearchRequest;
import com.jd.open.api.sdk.request.order.OrderSearchRequest;
import com.jd.open.api.sdk.response.after.AfterSearchResponse;
import com.jd.open.api.sdk.response.order.OrderSearchResponse;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class getOrders extends Thread {
	private static String jobName = "��ȡ����������ҵ��";
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	private static String refundlasttimeconfvalue=Params.username+"ȡ�˻���������ʱ��";
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private static long daymillis=24*60*60*1000L;
	private String lasttime;
	private String refundlasttime;
	private boolean is_importing=false;

	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			is_importing = true;
			try {
				Jingdong.setCurrentDate_getOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				refundlasttime=PublicUtils.getConfig(connection,refundlasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				Log.error("���ӳ������,getOrders������Ϊ"+connection.getMetaData().toString(),"");
				getOrderIdList(connection);
				//��ȡ�����˻�����  ����ͣ
				getRefund(connection) ;
			} catch (Throwable e) {
				try {
					e.printStackTrace() ;
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jingdong.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	//����ʱ�䡢״̬��ȡ����
	public void getOrderIdList(Connection conn) throws Throwable
	{
		Log.info("��ʼ��ȡ�����Ķ���!");
		int pageIndex = 1 ;
		boolean hasNextPage = true ;		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<5;)
		{
			
			try
			{
		
				DefaultJdClient client = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
				OrderSearchRequest request = new OrderSearchRequest();
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				
				request.setStartDate(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
				request.setEndDate(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
				request.setOrderState("WAIT_SELLER_STOCK_OUT,TRADE_CANCELED,LOCKED");
				request.setOptionalFields("order_id,modified,order_state");
				request.setPageSize("20");
				while(hasNextPage)
				{
					request.setPage(String.valueOf(pageIndex));
					OrderSearchResponse response = client.execute(request);
					
					if(!"0".equals(response.getCode()))
					{
						Log.error(jobName,"��ȡ���������б�ʧ��,������Ϣ:"+response.getCode()+","+response.getZhDesc()) ;
						hasNextPage = false ;
						break ;
					}
					OrderResult result = response.getOrderInfoResult() ;
					
					List<OrderSearchInfo> orderSerachInfoList = result.getOrderInfoList() ;
					Log.info("���ζ�����: "+orderSerachInfoList.size());
					if (orderSerachInfoList==null || orderSerachInfoList.size()<=0)
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
						break;
					}
					//�г���Ʒ���Ż�
					for(int i = 0 ; i < orderSerachInfoList.size() ; i++)
					{
						
						try{
							OrderSearchInfo info = orderSerachInfoList.get(i) ;
							/*if(info.getCouponDetailList()!=null){
								for(int j=0;i<info.getCouponDetailList().size();j++){
									Log.info("test");
									CouponDetail detail = info.getCouponDetailList().get(j);
									Log.info("�Ż�����: "+detail.getCouponType());
									Log.info("�Żݽ��: "+detail.getCouponPrice());
									if(detail.getCouponType().equals("52")) Log.info("����: "+info.getOrderId()+",��Ʒ���Żݽ��:��"+detail.getCouponPrice());
								}
							}*/
							Log.info("�����š�"+info.getOrderId()+"��,����޸�ʱ�䡾"+info.getModified()+"��,״̬��"+info.getOrderState()+"��") ;
							
							OrderInfo order=OrderUtils.getFullTrade(info.getOrderId(), Params.SERVER_URL, Params.token, Params.appKey, Params.appSecret);
							//Log.info("����id: "+order.getOrderId()+", Ӧ�����:��"+order.getOrderPayment());
							//�ȴ�����
							if(order.getOrderState().equalsIgnoreCase("WAIT_SELLER_STOCK_OUT"))
							{
								
								
								if (!OrderManager.isCheck("����Ա�����", conn, order.getOrderId()))
								{
									if (!OrderManager.TidLastModifyIntfExists("����Ա�����", conn, order.getOrderId(),Formatter.parseDate(order.getModified(),Formatter.DATE_TIME_FORMAT)))
									{
											
										
										//�������� ��������ɹ������ٿ��--ŷ�����
										OrderUtils.createInterOrder(conn, Params.SERVER_URL,Params.appKey,Params.appSecret,Params.token,
												order,Params.tradecontactid, Params.username,Params.JBDCustomerCode,Params.isLBP,Params.isNeedGetDeliverysheetid);
										
										//����������
										List<ItemInfo> itemList = order.getItemInfoList() ;
										for(int j = 0 ; j < itemList.size() ; j ++)
										{
											String sku = itemList.get(j).getOuterSkuId() ;
											long qty=Integer.valueOf(itemList.get(j).getItemTotal());
											StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, order.getOrderState(),order.getOrderId(), sku, -qty,false);
										}
									}
								}
									
							
							}
							//������ȡ��
							else if(order.getOrderState().equalsIgnoreCase("LOCKED")|| order.getOrderState().equalsIgnoreCase("TRADE_CANCELED"))
							{
								//ȡ������
								String sql="declare @ret int; execute  @ret = IF_CancelCustomerOrder '" + order.getOrderId() + "';select @ret ret;";
								//ŷ�����
								int resultCode =SQLHelper.intSelect(conn, sql) ;
								//ȡ������ʧ��
								if(resultCode == 0)
								{
									Log.info("����δ���-ȡ���ɹ�,����:"+order.getOrderId()+"");
								}else if(resultCode == 1)
								{
									Log.info("���������-�ص�,����:"+order.getOrderId()+"");
								}else if(resultCode ==2)
								{
									Log.info("�����Ѿ�����-ȡ��ʧ��,����:"+order.getOrderId()+"");
								}else if(resultCode ==3)
								{
									Log.info("���������ڻ���ȡ��-ȡ��ʧ��,����:"+order.getOrderId()+"");
								}
								else
								{
									Log.info("ȡ��ʧ��,����:"+order.getOrderId()+"");
								}
							}
							
							//�������Ʒ����޸�ʱ���������ʱ�䣬���ʱ��Ϊ�´�ȡ��Ʒ��ʼʱ��
							if(Formatter.parseDate(order.getModified(), Formatter.DATE_TIME_FORMAT).compareTo(modified) > 0)
							{
								modified = Formatter.parseDate(order.getModified(), Formatter.DATE_TIME_FORMAT) ;
							}
						}catch(Exception ex){
							if(conn!=null && !conn.getAutoCommit()){
								conn.rollback();
							}
							Log.error(jobName, ex.getMessage());
						}
					}
					//�ж��Ƿ�����һҳ
					if(orderSerachInfoList == null || orderSerachInfoList.size() == 0)
						hasNextPage = false ;
					else
						pageIndex ++ ;
				}
				
				//���ȡ����������ȡ������ʱ��С�ڵ�ǰ�죬������������Ϊ������ĵڶ������
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
			} catch (Throwable e) {
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
		
	}
	

	//��ȡ�˻���Ϣ V2
	public void getRefund(Connection conn) throws Throwable
	{
		boolean hasNextPage = true ;
		int pageIndex = 1 ;
		Date modified=Formatter.parseDate(refundlasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<5;)
		{
			try 
			{

				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				
				DefaultJdClient client = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
				AfterSearchRequest request = new AfterSearchRequest();
				String selectFields = "return_id,vender_id,send_type,receive_state,linkman,phone,return_address,consignee,consignor,send_time,receive_time,modifid_time,return_item_list" ;
				Field time_type = new Field("time_type", "MODIFIEDTIME");
				Field start_time = new Field("start_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
				Field end_time = new Field("end_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
				Field receive_state = new Field("receive_state", "WAITING");
				List<Field> queryFields = new ArrayList<Field>();
				queryFields.add(time_type);
				queryFields.add(start_time);
				queryFields.add(end_time);
				queryFields.add(receive_state);
				
				request.setQueryFields(queryFields);
				request.setSelectFields(selectFields) ;
				request.setPageSize("10");
				
	
				while(hasNextPage)
				{
					request.setPage(String.valueOf(pageIndex));
					AfterSearchResponse response = client.execute(request);
	
					
					//�ж��Ƿ���������
					if(!"0".equals(response.getCode()))
					{
						Log.info("���λ�ȡ�����˻���ʧ��,������Ϣ:"+response.getCode()+ "," + response.getZhDesc()) ;
						hasNextPage = false ;
						break ;
					}
					
					ReturnGoods returnGoods = response.getReturnGoods() ; ;
					List<ReturnInfo> returnInfoList = returnGoods.getReturnInfos() ;
					
					if (returnInfoList==null || returnInfoList.size()<=0)
					{				
						if (pageIndex==1)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,refundlasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,refundlasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, refundlasttimeconfvalue, value);			    
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
					
					for(int i = 0 ; i < returnInfoList.size() ; i++)
					{
						try{
							ReturnInfo returnInfo = returnInfoList.get(i) ;
							List<ReturnItem> itemList = returnInfo.getReturnItemList() ;
							for(int j = 0 ; j < itemList.size() ; j++)
							{
								try{
									ReturnItem item = itemList.get(j) ;
									Sku sku= StockUtils.getSkuInfoBySkuId(jobName, item.getSkuId(), Params.SERVER_URL, Params.token, Params.appKey, Params.appSecret) ;
									String sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+Params.tradecontactid;
						            String inshopid=SQLHelper.strSelect(conn, sql);
						            conn.setAutoCommit(false);		
									sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
									String sheetid=SQLHelper.strSelect(conn, sql);
									if (sheetid.trim().equals(""))
										throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
			
									sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , "
										+ "Created , Modified , OrderStatus , Status , GoodStatus , "
					                    + " HasGoodReturn ,RefundFee , Payment , Reason,Description ,"
					                    + " Title , Price , Num , GoodReturnTime , Sid , "
					                    + " TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ," 
					                    + " Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo,skuid)"
					                    + " values('" + sheetid + "' , '" + item.getOrderId() + "' , '" + returnInfo.getReturnId() + "' , '' , '' ,"
					                    + "'" + returnInfo.getSendTime() + "','" + item.getModifidTime() + "','" + returnInfo.getReceiveTime()+ "','','',"
					                    + "'1','0','0','" + item.getReturnType() + "','" + item.getReturnReason() + "',"
					                    + "'" + item.getSkuName() + "','" + item.getPrice() + "','','" + item.getModifidTime() + "','',"
					                    + "'0','','','" + sku.getOuterId() + "',''," 
					                    + "'" + returnInfo.getReturnAddress() + "','','" + inshopid + "','" + returnInfo.getReturnId() + "','" + returnInfo.getConsignor() + "','','','"+ item.getSkuId() +"')" ;
									SQLHelper.executeSQL(conn,sql);
											
									//���뵽֪ͨ��
						            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
						                + sheetid +"',2 , '"+Params.tradecontactid+"' , 'yongjun' , getdate() , null) ";				
									SQLHelper.executeSQL(conn, sql);
									
									Log.info(jobName,"�ӿڵ���:"+sheetid+" �˻�������:"+returnInfo.getReturnId()+",��ϸ���ţ�"+item.getOrderId()+",ʱ��:"+returnInfo.getModifidTime());
									
									conn.commit();
									conn.setAutoCommit(true);
								}catch(Throwable ex){
									if(conn!=null && !conn.getAutoCommit()){
										conn.rollback();
									}
									Log.error(jobName, ex.getMessage());
								}
								
							}
							
							//�������Ʒ����޸�ʱ���������ʱ�䣬���ʱ��Ϊ�´�ȡ��Ʒ��ʼʱ��
							if(Formatter.parseDate(returnInfo.getModifidTime(), Formatter.DATE_TIME_FORMAT).compareTo(modified) < 0)
							{
								modified = Formatter.parseDate(returnInfo.getModifidTime(), Formatter.DATE_TIME_FORMAT) ;
							}
						}catch(Throwable ex){
							if(conn!=null && !conn.getAutoCommit()){
								conn.rollback();
							}
							Log.error(jobName, ex.getMessage());
						}
						
					}
					
					//�ж��Ƿ�����һҳ
					if(returnInfoList.size() > 0)
						pageIndex ++ ;
					else
						hasNextPage = false ;
				}
				
				//���ȡ����������ȡ������ʱ��С�ڵ�ǰ�죬������������Ϊ������ĵڶ������
				if (modified.compareTo(Formatter.parseDate(refundlasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, refundlasttimeconfvalue, value);
	            	}catch(Throwable je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				
				//ִ�гɹ�����ѭ��
				break;
			} catch (Throwable e) 
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	
	
}