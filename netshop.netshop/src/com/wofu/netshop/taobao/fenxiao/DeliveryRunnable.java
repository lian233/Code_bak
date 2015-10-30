package com.wofu.netshop.taobao.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.internal.util.StringUtils;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.request.LogisticsConsignResendRequest;
import com.taobao.api.request.LogisticsOfflineSendRequest;
import com.taobao.api.request.TmcMessageProduceRequest;
import com.taobao.api.request.WlbOrderJzwithinsConsignRequest;
import com.taobao.api.response.LogisticsConsignResendResponse;
import com.taobao.api.response.LogisticsOfflineSendResponse;
import com.taobao.api.response.TmcMessageProduceResponse;
import com.taobao.api.response.WlbOrderJzwithinsConsignResponse;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 淘宝发货线程类
 * @author Administrator
 *
 */
public class DeliveryRunnable implements Runnable{
	private String jobName="淘宝分销发货作业";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	public DeliveryRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param = param;
	}
	public void run() {
		// TODO Auto-generated method stub
		Connection conn=null;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			delivery(conn);
		}catch(Exception e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(username,"关闭数据库事务出错  "+e1.getMessage(),null);
				}
				Log.info(username,"发货线程错误: "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"关闭数据库连接出错: "+e.getMessage(),null);
				}
				watch.countDown();
		}
		
	}
	
	private void delivery(Connection conn)  throws Exception
	{
		
		
		String sql = "select a.id,a.tid,a.companycode,a.outsid from itf_delivery a,Inf_UpNote b "
			+"where a.id=b.OperData and a.sheettype=3 and a.shopid="+param.shopid;
		Log.info(sql);
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info(username," 本次要处理的淘宝发货条数为:　"+vdeliveryorder.size(),null);
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			int sheetid = (Integer)(hto.get("id"));
			String orderid = hto.get("tid")!=null?hto.get("tid").toString().trim():"";
			String post_company = hto.get("companycode")!=null?hto.get("companycode").toString():"";
			String post_no = hto.get("outsid")!=null?hto.get("outsid").toString().trim():"";		
			//如果物流公司为空则忽略处理
			if (post_company.trim().equals(""))
			{
				Log.warn(username,jobName+ " 快递公司为空！订单号:"+orderid,null);
				continue;
			}
			
			//如果物流公司为空则忽略处理
			if (post_no.trim().equals(""))
			{
				Log.warn(username, "快递单号为空！订单号:"+orderid,null);
				continue;
			}
			
			if (!StringUtil.isNumeric(orderid))
			{
				sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
					+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
					+ " where operdata = "+ sheetid+ " and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";
	
				SQLHelper.executeSQL(conn, sql);
				
				Log.warn(username,jobName+",订单号【"+orderid+"】不全是数字!",null);
				continue;
			}
			
			normalSend(post_no,orderid,post_company,conn,sheetid);
			

			
		}
		
	}
	
	private void normalSend(String post_no,String orderid,String post_company,Connection conn,int sheetid){
		try {
			Log.info(param.url+" "+param.appkey+" "+param.appsecret+" "+param.authcode);
			TaobaoClient client=new DefaultTaobaoClient(param.url,param.appkey, param.appsecret);
			LogisticsOfflineSendRequest req=new LogisticsOfflineSendRequest();	
			req.setOutSid(post_no);
			req.setTid(TranTid(orderid));
			req.setCompanyCode(post_company);
			LogisticsOfflineSendResponse rsp = client.execute(req, param.authcode);
			Log.info(rsp.toString());
			String sql;
			
			if (rsp.isSuccess())
			{			
				try {
					conn.setAutoCommit(false);

					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
				} catch (SQLException sqle) {
					if (!conn.getAutoCommit())
						try {
							conn.rollback();
						} catch (Exception e1) {
						}
					try {
						conn.setAutoCommit(true);
					} catch (Exception e1) {
					}
					//throw new JSQLException(sql, sqle);
				}
				Log.info(username,"处理订单【" + orderid + "】发货成功,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
			}
			else
			{	
				if(rsp.getSubCode().equals("isv.logistics-offline-service-error:B04")){
					//已经同步发货状态或退货成功了
					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"订单: "+orderid+", sheetid: "+sheetid+",状态异常,已备份到备份表",null);
				}
				if(rsp.getSubCode().equals("isv.logistics-offline-service-error:P38")){
					//已经同步发货状态或退货成功了
					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"订单: "+orderid+", sheetid: "+sheetid+",状态异常,,拆单校验未通过.已备份到备份表",null);
				}
				if(rsp.getSubCode().equals("isv.logistics-offline-service-error:S01")){
					//已经同步发货状态或退货成功了
					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"订单: "+orderid+", sheetid: "+sheetid+",状态异常,已备份到备份表",null);
				}
				if(rsp.getSubCode().indexOf("ORDER_NOT_FOUND_ERROR")!=-1){
					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.error(username, jobName+",订单号:　"+orderid+", 无法找到！",null);
                }
				if (rsp.getSubMsg().indexOf("不能重复发货")>=0|| rsp.getSubMsg().indexOf("发货类型不匹配")>=0)
				{
					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"订单【" + orderid + "】不能重复发货,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
				}
				
				else if((rsp.getSubMsg().indexOf("没有权限进行发货")>=0)||rsp.getSubMsg().indexOf("没有权限发货")>=0
						||rsp.getSubMsg().indexOf("当前订单状态不支持修改")>=0) 
				{
					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"没有权限发货,订单【" + orderid + "】,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
				} else if (rsp.getSubMsg().indexOf("物流订单不存在") >=0 || rsp.getSubMsg().indexOf("订单已经被拆单") >=0
						 || rsp.getSubMsg().indexOf("当前操作的订单是拆单订单") >=0)
				{
					Log.info(username, "子订单发货,订单号: "+orderid, null);
					TaobaoClient subclient=new DefaultTaobaoClient(param.url,param.appkey, param.appsecret);
					LogisticsOfflineSendRequest subreq=new LogisticsOfflineSendRequest();	
					subreq.setOutSid(post_no);
					subreq.setTid(TranTid(orderid));
					subreq.setCompanyCode(post_company);
					subreq.setIsSplit(1L);
					
					String subtids="";
					
					sql="select oid from customerorderitem a with(nolock),customerorder b with(nolock)," 
							+"customerdelive c with(nolock) where a.sheetid=b.sheetid and a.sheetid=c.refsheetid "
							+"and c.customersheetid='"+orderid+"' and c.delivery='"+post_company+"' "
							+"and c.deliverysheetid='"+post_no+"'";
					List sublist=SQLHelper.multiRowListSelect(conn, sql);
					for(Iterator it=sublist.iterator();it.hasNext();)
					{
						String oid=(String) it.next();
						subtids=oid+","+subtids;
					}
					subtids=(subtids!=null&&subtids!=""?subtids.substring(0, subtids.length()-1):"");
					
					subreq.setSubTid(subtids);
					

					
					LogisticsOfflineSendResponse subrsp = client.execute(subreq, param.authcode);
					
		
					
					if (subrsp.isSuccess())
					{		
						Log.info(jobName,"处理订单【" + orderid + "】,子订单【"+subtids+"】发货成功,快递公司【"+ post_company + "】,快递单号【" + post_no + "】");
					}
					
					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					
					Log.info(username,"物流订单不存在或订单已经被拆单,订单【" + orderid + "】,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
				} else if (rsp.getSubMsg().indexOf("运单号不符合规则或已经被使用") >=0)
                {
					//公司内部自提发货处理
					if ((post_no.toUpperCase().indexOf("1111111")>=0) ||
						(post_no.toUpperCase().indexOf("YJ")>=0))
					{
						sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
							+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
							+ " where operdata = "+ sheetid+ " and SheetType = 3";
						SQLHelper.executeSQL(conn, sql);
			
						sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

						SQLHelper.executeSQL(conn, sql);
						
					}
					else
					{
						String cc="";
						String memo="";
                        if (post_no.toUpperCase().indexOf("EH") == 0)
                        {
                            cc = "EMS";
                        }
                        else if (post_no.toUpperCase().indexOf("101") == 0)
                        {
                            cc = "SF";
                        }
                        else if (post_no.toUpperCase().indexOf("368") == 0)
                        {
                            cc = "STO";
                        }
                        else if (post_no.toUpperCase().indexOf("W") == 0)
                        {
                            cc = "YTO";
                        }
                        else{
                        	cc=post_company;   //把错误的运单号对应的订单号修改一下，让后面修改后的快递信息能正确写入sheettype=3
                        	orderid+="运单号不符合规则或已经被使用";
                        	memo+="运单号不符合规则或已经被使用";
                        }
                        	

                        sql = "update ns_delivery set companycode='" + cc + "',tid='"+orderid+"',memo='"+memo+"' where SheetID = '" + sheetid + "'";
                        SQLHelper.executeSQL(conn, sql);
					}
					
					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					
                    Log.info(jobName,"运单号不符合规则或已经被使用,订单【" + orderid + "】,快递公司【"+ post_company + "】,快递单号【" + post_no + "】");
                }
				else{
					Log.info(username,"处理订单【" + orderid + "】发货失败,快递公司【"+ post_company + "】,快递单号【" + post_no + "】"+"错误信息:"+rsp.getSubMsg()+rsp.getMsg(),null);
					String errmsg = rsp.getSubMsg();
					Pattern par = Pattern.compile("This ban will last for (\\d{1,9}) more seconds");
					Matcher m = par.matcher(errmsg);
					if(m.find()){
						long delayTime = Long.parseLong(m.group(1))*1000L;
						long now = System.currentTimeMillis();
						while(System.currentTimeMillis()<now+delayTime){
							Thread.sleep(1000L);
						}
					}
					
					
				}
					
			}
		
	
	} catch (ApiException e) {

		Log.error(username,"处理订单【" + orderid + "】发货失败,快递公司【"	+ post_company + "】,快递单号【" + post_no	+ "】,错误信息:" + e.getMessage(),null);
	}
	catch(Exception e)
	{
		Log.error(username, "同步发货状态出错了, 错误信息: "+e.getMessage(),null);
	}
	}
	
	//家装订单发货
	private void jzSend(String post_no,String orderid,String post_company,Connection conn,String sheetid){
		try {
			
			TaobaoClient client=new DefaultTaobaoClient(param.url,param.appkey, param.appsecret);
			WlbOrderJzwithinsConsignRequest req=new WlbOrderJzwithinsConsignRequest();
			req.setTid(TranTid(orderid));
			JSONObject obj = new JSONObject();
			obj.put("mail_no ", post_no);
			obj.put("zy_company ", post_company);
			req.setTmsPartner(obj.toString());
			JSONArray arr = new JSONArray(param.jzParams);
			WlbOrderJzwithinsConsignResponse rsp;
			for(int i=0;i<arr.length();i++){
				req.setInsPartner(arr.get(i).toString());
				rsp = client.execute(req, param.authcode);
				String sql;
				if (rsp.isSuccess())
				{			

					try {
						conn.setAutoCommit(false);

						sql = "insert into IT_UpNoteBak(SerialID,Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
								+ " select SerialID,Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
								+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
						SQLHelper.executeSQL(conn, sql);

						sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

						SQLHelper.executeSQL(conn, sql);
						conn.commit();
						conn.setAutoCommit(true);
					} catch (SQLException sqle) {
						if (!conn.getAutoCommit())
							try {
								conn.rollback();
							} catch (Exception e1) {
							}
						try {
							conn.setAutoCommit(true);
						} catch (Exception e1) {
						}
						//throw new JSQLException(sql, sqle);
					}
					Log.info(username,"处理订单【" + orderid + "】发货成功,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
					Log.info(username,"家装发货参数: "+arr.get(i).toString(),null);
					break;
				}
				else
				{		
					Log.info(username,"家装订单发货失败,错误信息: "+rsp.getResultInfo(),null);
					continue;
						
				}
			}
			
			
		
	
	} catch (ApiException e) {

		Log.error(username,"处理订单【" + orderid + "】发货失败,快递公司【"	+ post_company + "】,快递单号【" + post_no	+ "】,错误信息:" + e.getMessage(),null);
	}
	catch(Exception e)
	{
		Log.error(username, "同步发货状态出错了, 错误信息: "+e.getMessage(),null);
	}
	}
	
	
	/**
	 * 
	 * 修改物流公司和运单号   已经发过货的订单才能进行此操作
	 * api : taobao.logistics.consign.resend 免费
	 * 
	 */
	private void resend(Connection conn)  throws Exception
	{
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+" upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=4 and a.sheetid=b.sheetid and a.receiver='"
			+ param.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			String orderid="";
			try{
				Hashtable hto = (Hashtable) vdeliveryorder.get(i);
				String sheetid = hto.get("sheetid")!=null?hto.get("sheetid").toString():"";
				orderid = hto.get("tid")!=null?hto.get("tid").toString():"";
				String post_company = hto.get("companycode")!=null?hto.get("companycode").toString().trim():"";
				String post_no = hto.get("outsid").toString()!=null?hto.get("outsid").toString().trim():"";	
				
				//如果物流公司为空则忽略处理
				if (post_company.trim().equals(""))
				{
					Log.warn(username,jobName + " 快递公司为空！订单号:"+orderid+"");
					continue;
				}
				
				//如果物流公司为空则忽略处理
				if (post_no.trim().equals(""))
				{
					Log.warn(username,jobName + "快递单号为空！订单号:"+orderid+"");
					continue;
				}
				
				if (!StringUtil.isNumeric(orderid))
				{
					sql = "insert into IT_UpNoteBak(SerialID,Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";
		
					SQLHelper.executeSQL(conn, sql);
					
					Log.warn(username,jobName + " 订单号【"+orderid+"】不全是数字!");
					continue;
				}
				
						TaobaoClient client=new DefaultTaobaoClient(param.url,param.appkey, param.appsecret);
						LogisticsConsignResendRequest req=new LogisticsConsignResendRequest();	
						req.setOutSid(post_no);
						req.setTid(TranTid(orderid));					
						req.setCompanyCode(post_company);
						LogisticsConsignResendResponse rsp = client.execute(req, param.authcode);
						
						
						if (rsp.isSuccess())
						{			

							try {
								conn.setAutoCommit(false);

								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
										+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
										+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								conn.commit();
								conn.setAutoCommit(true);
							} catch (Exception sqle) {
								if (!conn.getAutoCommit())
									try {
										conn.rollback();
									} catch (Exception e1) {
									}
								try {
									conn.setAutoCommit(true);
								} catch (Exception e1) {
								}
								//throw new JSQLException(sql, sqle);
							}
							Log.info(username,"处理订单【" + orderid + "】发货成功,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
						}
						else
						{		
							
							if (rsp.getSubMsg().indexOf("不能重复发货")>=0|| rsp.getSubMsg().indexOf("发货类型不匹配")>=0)
							{
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(username,"订单【" + orderid + "】不能重复发货,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
							}else if (rsp.getSubMsg().indexOf("订单未发货")>=0)
							{
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(username,"订单【" + orderid + "】可能未发货，或已经退货,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
							}
							else if((rsp.getSubMsg().indexOf("没有权限进行发货")>=0)||rsp.getSubMsg().indexOf("没有权限发货")>=0 
									||rsp.getSubMsg().indexOf("当前订单状态不支持修改")>=0 ) 
							{
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(username,"没有权限发货,订单【" + orderid + "】,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
								
							}
							else if (rsp.getSubMsg().indexOf("物流订单不存在") >=0 || rsp.getSubMsg().indexOf("订单已经被拆单") >=0
									 || rsp.getSubMsg().indexOf("当前操作的订单是拆单订单") >=0)
							{
								TaobaoClient subclient=new DefaultTaobaoClient(param.url,param.appkey, param.appsecret);
								LogisticsOfflineSendRequest subreq=new LogisticsOfflineSendRequest();	
								subreq.setOutSid(post_no);
								subreq.setTid(TranTid(orderid));
								subreq.setCompanyCode(post_company);
								subreq.setIsSplit(1L);
								String subtids="";
								
								sql="select oid from customerorderitem a with(nolock),customerorder b with(nolock)," 
										+"customerdelive c with(nolock) where a.sheetid=b.sheetid and a.sheetid=c.refsheetid "
										+"and c.customersheetid='"+orderid+"' and c.delivery='"+post_company+"' "
										+"and c.deliverysheetid='"+post_no+"'";
								List sublist=SQLHelper.multiRowListSelect(conn, sql);
								if(sublist.size()>0){
									for(Iterator it=sublist.iterator();it.hasNext();)
									{
										String oid=(String) it.next();
										subtids=oid+","+subtids;
									}
									
									subtids=subtids.substring(0, subtids.length()-1);
									
									subreq.setSubTid(subtids);
									
									LogisticsOfflineSendResponse subrsp = client.execute(subreq, param.authcode);
									if (subrsp.isSuccess())
									{		
										Log.info(username,"处理订单【" + orderid + "】,子订单【"+subtids+"】发货成功,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
									}
								}
								
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(jobName,"物流订单不存在或订单已经被拆,订单【" + orderid + "】,快递公司【"+ post_company + "】,快递单号【" + post_no + "】");
							} else if (rsp.getSubMsg().indexOf("运单号不符合规则或已经被使用") >=0)
		                    {
								//公司内部自提发货处理
								if ((post_no.toUpperCase().indexOf("1111111")>=0) ||
									(post_no.toUpperCase().indexOf("YJ")>=0))
								{
									sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
										+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
										+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
									SQLHelper.executeSQL(conn, sql);

									sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

									SQLHelper.executeSQL(conn, sql);
								}
								else
								{
									String cc="";
			                        if (post_no.toUpperCase().indexOf("EH") == 0)
			                        {
			                            cc = "EMS";
			                        }
			                        else if (post_no.toUpperCase().indexOf("101") == 0)
			                        {
			                            cc = "SF";
			                        }
			                        else if (post_no.toUpperCase().indexOf("368") == 0)
			                        {
			                            cc = "STO";
			                        }
			                        else if (post_no.toUpperCase().indexOf("W") == 0)
			                        {
			                            cc = "YTO";
			                        }
			                        else
			                        	cc=post_company;
		
			                        sql = "update ns_delivery set companycode='" + cc + "' where SheetID = '" + sheetid + "'";
			                        SQLHelper.executeSQL(conn, sql);
								}

		                        Log.info(username,"运单号不符合规则或已经被使用,订单【" + orderid + "】,快递公司【"+ post_company + "】,快递单号【" + post_no + "】",null);
		                    }else if(rsp.getSubMsg().indexOf("该订单不支持修改") >=0){  
		                    	sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
		                    	Log.info(jobName,"处理订单【" + orderid + "】发货失败,快递公司【"+ post_company + "】,快递单号【" + post_no + "】"+"错误信息:"+rsp.getSubMsg()+rsp.getMsg());
		                    }
							else
								Log.info(username,"处理订单【" + orderid + "】发货失败,快递公司【"+ post_company + "】,快递单号【" + post_no + "】"+"错误信息:"+rsp.getSubMsg()+rsp.getMsg(),null);
						}
			}catch(Exception ex){
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.info(username,"修改发货信息作业出错，订单号: "+orderid+",错误信息: "+ex.getMessage(),null);
			}
			
		}
	}
	
	private void sendTradeInfo(List sheetids,Connection conn) throws Exception{
		Vector<Hashtable> tradeInfo = new Vector();
		String sql="";
		boolean isSuccess = false;
		for(int i=0;i<sheetids.size();i++){
			try{
				String sheetid = (String)sheetids.get(i);
				sql = "select tid,actiontime,operator,status,remark from ns_tradetrace where sheetid='"+sheetid+"'";
				tradeInfo=SQLHelper.multiRowSelect(conn, sql);
				for(Iterator it = tradeInfo.iterator();it.hasNext();){
					Hashtable tab = (Hashtable)it.next();
					isSuccess = sendSingleInfo(tab);
				}
				if(isSuccess){
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 6";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=6";

					SQLHelper.executeSQL(conn, sql);
                	Log.info(username,"处理淘宝订单跟踪信息,sheetid:【" + sheetid + "】成功",null);
				}else{
					Log.info(username,"处理淘宝订单跟踪信息,sheetid:【" + sheetid + "】失败",null);
				}
			}catch(Exception e){
				Log.error(username,"同步订单跟踪信息出错  "+ e.getMessage(),null);
				continue;
			}
		}
	}
	
	private boolean sendSingleInfo(Hashtable tab) throws Exception{
		boolean isSuccess=false;
		String tidString = tab.get("tid").toString().trim();
		if(!StringUtils.isNumeric(tidString)) return true;
		Long tid = Long.parseLong(tidString);
		String actionTime = tab.get("actiontime").toString().trim().substring(0,19);
		String operator = tab.get("operator").toString().trim();
		String status = tab.get("status").toString().trim();
		String remark = tab.get("remark").toString().trim();
		try{
			TaobaoClient client =new DefaultTaobaoClient(param.url,param.appkey,param.appsecret);
			TmcMessageProduceRequest req = new TmcMessageProduceRequest();
			req.setTopic("taobao_jds_TradeTrace");
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("action_time", actionTime);
			map.put("operator", operator);
			map.put("remark", remark);
			map.put("seller_nick", param.sellernick);
			map.put("status", status);
			map.put("tid", tid);
			String contents = new JSONWriter().write(map);
			//Log.info("contents: " +contents);
			req.setContent(contents);
			TmcMessageProduceResponse rep = client.execute(req, param.authcode);
			//Log.info("req: "+rep.getBody());
			if(rep.getIsSuccess()){
				isSuccess=true;
				//Log.info("发送单条订单跟踪数据成功,订单号: "+tid+"状态:　"+status);
			}
		}catch(Exception e){
			Log.error(username,"发送单条订单跟踪数据出错,tid: '"+tid+",status: '"+status+" "+e.getMessage(),null);
		}
		return isSuccess;
	}
	
	
	private List<String> getTradeInfo(Connection conn) throws Exception{
		List<String> sheetids = new ArrayList<String>();
		String sql = "select sheetid from it_upnote where sheettype=6 and receiver='"+param.tradecontactid+"'";
		sheetids = SQLHelper.multiRowListSelect(conn, sql);
		return sheetids;
	}
	
	 private long TranTid(String tid)
	    {
	        tid = tid.trim();
	        tid = tid.toLowerCase();
	        String t = "";
	        for (int i=0; i<tid.length();i++)
	        {
	        	char c=tid.charAt(i);
	            if ((c >= 48) && (c <= 57))
	            {
	                t = t + c;
	            }
	        }

	        long id = 0;
	        if (t != "")
	        {
	            id = Long.parseLong(t);
	        }
	        return id;
	    }

}
