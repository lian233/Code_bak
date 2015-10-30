package com.wofu.netshop.taobao;
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
 * �Ա������߳���
 * @author Administrator
 *
 */
public class DeliveryRunnable implements Runnable{
	private String jobName="�Ա�������ҵ";
	private CountDownLatch watch;
	private String username="";
	public DeliveryRunnable(CountDownLatch watch,String username){
		this.watch=watch;
		this.username=username;
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
					Log.error(username,"�ر����ݿ��������  "+e1.getMessage(),null);
				}
				Log.info(username,"�����̴߳���: "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"�ر����ݿ����ӳ���: "+e.getMessage(),null);
				}
				watch.countDown();
		}
		
	}
	
	private void delivery(Connection conn)  throws Exception
	{
		
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+"upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info(username," ����Ҫ������Ա���������Ϊ:��"+vdeliveryorder.size(),null);
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid")!=null?hto.get("sheetid").toString():"";
			String orderid = hto.get("tid")!=null?hto.get("tid").toString():"";
			String post_company = hto.get("companycode")!=null?hto.get("companycode").toString():"";
			String post_no = hto.get("outsid")!=null?hto.get("outsid").toString():"";		
			//���������˾Ϊ������Դ���
			if (post_company.trim().equals(""))
			{
				Log.warn(username,jobName+ " ��ݹ�˾Ϊ�գ�������:"+orderid,null);
				continue;
			}
			
			//���������˾Ϊ������Դ���
			if (post_no.trim().equals(""))
			{
				Log.warn(username, "��ݵ���Ϊ�գ�������:"+orderid,null);
				continue;
			}
			
			if (!StringUtil.isNumeric(orderid))
			{
				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
					+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
					+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
	
				SQLHelper.executeSQL(conn, sql);
				
				Log.warn(username,jobName+",�����š�"+orderid+"����ȫ������!",null);
				continue;
			}
			if("1".equals(Params.isJZ)){
				sql ="select top 1 lbpdc from ns_customerorder where tid='"+orderid+"'";
				String isJz= SQLHelper.strSelect(conn, sql);
				if("zj".equals(isJz)){//��װ����
					//jzSend(post_no,orderid,post_company,conn,sheetid);
					Log.info(username,"��װ������ʱ������: ������: "+orderid,null);
				}else
					normalSend(post_no,orderid,post_company,conn,sheetid);
			}else
			normalSend(post_no,orderid,post_company,conn,sheetid);
			

			
		}
		
	}
	
	private void normalSend(String post_no,String orderid,String post_company,Connection conn,String sheetid){
		try {
			
			TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret);
			LogisticsOfflineSendRequest req=new LogisticsOfflineSendRequest();	
			req.setOutSid(post_no);
			req.setTid(TranTid(orderid));
			req.setCompanyCode(post_company);
			LogisticsOfflineSendResponse rsp = client.execute(req, Params.authcode);
			String sql;
			
			if (rsp.isSuccess())
			{			
				try {
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
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
				Log.info(username,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
			}
			else
			{	
				if(rsp.getSubCode().equals("isv.logistics-offline-service-error:B04")){
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"����: "+orderid+", sheetid: "+sheetid+",״̬�쳣,�ѱ��ݵ����ݱ�",null);
				}
				if(rsp.getSubCode().equals("isv.logistics-offline-service-error:P38")){
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"����: "+orderid+", sheetid: "+sheetid+",״̬�쳣,,��У��δͨ��.�ѱ��ݵ����ݱ�",null);
				}
				if(rsp.getSubCode().equals("isv.logistics-offline-service-error:S01")){
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"����: "+orderid+", sheetid: "+sheetid+",״̬�쳣,�ѱ��ݵ����ݱ�",null);
				}
				if(rsp.getSubCode().indexOf("ORDER_NOT_FOUND_ERROR")!=-1){
                	sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.error(username, jobName+",������:��"+orderid+", �޷��ҵ���",null);
                }
				if (rsp.getSubMsg().indexOf("�����ظ�����")>=0|| rsp.getSubMsg().indexOf("�������Ͳ�ƥ��")>=0)
				{
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"������" + orderid + "�������ظ�����,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
				}
				
				else if((rsp.getSubMsg().indexOf("û��Ȩ�޽��з���")>=0)||rsp.getSubMsg().indexOf("û��Ȩ�޷���")>=0
						||rsp.getSubMsg().indexOf("��ǰ����״̬��֧���޸�")>=0) 
				{
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(username,"û��Ȩ�޷���,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
				} else if (rsp.getSubMsg().indexOf("��������������") >=0 || rsp.getSubMsg().indexOf("�����Ѿ�����") >=0
						 || rsp.getSubMsg().indexOf("��ǰ�����Ķ����ǲ𵥶���") >=0)
				{
					TaobaoClient subclient=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret);
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
					

					
					LogisticsOfflineSendResponse subrsp = client.execute(subreq, Params.authcode);
					
		
					
					if (subrsp.isSuccess())
					{		
						Log.info(jobName,"��������" + orderid + "��,�Ӷ�����"+subtids+"�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
					}
					
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					
					Log.info(username,"�������������ڻ򶩵��Ѿ�����,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
				} else if (rsp.getSubMsg().indexOf("�˵��Ų����Ϲ�����Ѿ���ʹ��") >=0)
                {
					//��˾�ڲ����ᷢ������
					if ((post_no.toUpperCase().indexOf("1111111")>=0) ||
						(post_no.toUpperCase().indexOf("YJ")>=0))
					{
						sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
						SQLHelper.executeSQL(conn, sql);

						sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

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
                        	cc=post_company;   //�Ѵ�����˵��Ŷ�Ӧ�Ķ������޸�һ�£��ú����޸ĺ�Ŀ����Ϣ����ȷд��sheettype=3
                        	orderid+="�˵��Ų����Ϲ�����Ѿ���ʹ��";
                        	memo+="�˵��Ų����Ϲ�����Ѿ���ʹ��";
                        }
                        	

                        sql = "update ns_delivery set companycode='" + cc + "',tid='"+orderid+"',memo='"+memo+"' where SheetID = '" + sheetid + "'";
                        SQLHelper.executeSQL(conn, sql);
					}
					
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					
                    Log.info(jobName,"�˵��Ų����Ϲ�����Ѿ���ʹ��,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
                }
				else{
					Log.info(username,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��"+"������Ϣ:"+rsp.getSubMsg()+rsp.getMsg(),null);
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

		Log.error(username,"��������" + orderid + "������ʧ��,��ݹ�˾��"	+ post_company + "��,��ݵ��š�" + post_no	+ "��,������Ϣ:" + e.getMessage(),null);
	}
	catch(Exception e)
	{
		Log.error(username, "ͬ������״̬������, ������Ϣ: "+e.getMessage(),null);
	}
	}
	
	//��װ��������
	private void jzSend(String post_no,String orderid,String post_company,Connection conn,String sheetid){
		try {
			
			TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret);
			WlbOrderJzwithinsConsignRequest req=new WlbOrderJzwithinsConsignRequest();
			req.setTid(TranTid(orderid));
			JSONObject obj = new JSONObject();
			obj.put("mail_no ", post_no);
			obj.put("zy_company ", post_company);
			req.setTmsPartner(obj.toString());
			JSONArray arr = new JSONArray(Params.jzParams);
			WlbOrderJzwithinsConsignResponse rsp;
			for(int i=0;i<arr.length();i++){
				req.setInsPartner(arr.get(i).toString());
				rsp = client.execute(req, Params.authcode);
				String sql;
				if (rsp.isSuccess())
				{			

					try {
						conn.setAutoCommit(false);

						sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
								+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
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
					Log.info(username,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
					Log.info(username,"��װ��������: "+arr.get(i).toString(),null);
					break;
				}
				else
				{		
					Log.info(username,"��װ��������ʧ��,������Ϣ: "+rsp.getResultInfo(),null);
					continue;
						
				}
			}
			
			
		
	
	} catch (ApiException e) {

		Log.error(username,"��������" + orderid + "������ʧ��,��ݹ�˾��"	+ post_company + "��,��ݵ��š�" + post_no	+ "��,������Ϣ:" + e.getMessage(),null);
	}
	catch(Exception e)
	{
		Log.error(username, "ͬ������״̬������, ������Ϣ: "+e.getMessage(),null);
	}
	}
	
	
	/**
	 * 
	 * �޸�������˾���˵���   �Ѿ��������Ķ������ܽ��д˲���
	 * api : taobao.logistics.consign.resend ���
	 * 
	 */
	private void resend(Connection conn)  throws Exception
	{
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+" upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=4 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			String orderid="";
			try{
				Hashtable hto = (Hashtable) vdeliveryorder.get(i);
				String sheetid = hto.get("sheetid")!=null?hto.get("sheetid").toString():"";
				orderid = hto.get("tid")!=null?hto.get("tid").toString():"";
				String post_company = hto.get("companycode")!=null?hto.get("companycode").toString().trim():"";
				String post_no = hto.get("outsid").toString()!=null?hto.get("outsid").toString().trim():"";	
				
				//���������˾Ϊ������Դ���
				if (post_company.trim().equals(""))
				{
					Log.warn(username,jobName + " ��ݹ�˾Ϊ�գ�������:"+orderid+"");
					continue;
				}
				
				//���������˾Ϊ������Դ���
				if (post_no.trim().equals(""))
				{
					Log.warn(username,jobName + "��ݵ���Ϊ�գ�������:"+orderid+"");
					continue;
				}
				
				if (!StringUtil.isNumeric(orderid))
				{
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";
		
					SQLHelper.executeSQL(conn, sql);
					
					Log.warn(username,jobName + " �����š�"+orderid+"����ȫ������!");
					continue;
				}
				
						TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret);
						LogisticsConsignResendRequest req=new LogisticsConsignResendRequest();	
						req.setOutSid(post_no);
						req.setTid(TranTid(orderid));					
						req.setCompanyCode(post_company);
						LogisticsConsignResendResponse rsp = client.execute(req, Params.authcode);
						
						
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
							Log.info(username,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
						}
						else
						{		
							
							if (rsp.getSubMsg().indexOf("�����ظ�����")>=0|| rsp.getSubMsg().indexOf("�������Ͳ�ƥ��")>=0)
							{
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(username,"������" + orderid + "�������ظ�����,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
							}else if (rsp.getSubMsg().indexOf("����δ����")>=0)
							{
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(username,"������" + orderid + "������δ���������Ѿ��˻�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
							}
							else if((rsp.getSubMsg().indexOf("û��Ȩ�޽��з���")>=0)||rsp.getSubMsg().indexOf("û��Ȩ�޷���")>=0 
									||rsp.getSubMsg().indexOf("��ǰ����״̬��֧���޸�")>=0 ) 
							{
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(username,"û��Ȩ�޷���,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
								
							}
							else if (rsp.getSubMsg().indexOf("��������������") >=0 || rsp.getSubMsg().indexOf("�����Ѿ�����") >=0
									 || rsp.getSubMsg().indexOf("��ǰ�����Ķ����ǲ𵥶���") >=0)
							{
								TaobaoClient subclient=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret);
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
									
									LogisticsOfflineSendResponse subrsp = client.execute(subreq, Params.authcode);
									if (subrsp.isSuccess())
									{		
										Log.info(username,"��������" + orderid + "��,�Ӷ�����"+subtids+"�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
									}
								}
								
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(jobName,"�������������ڻ򶩵��Ѿ�����,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
							} else if (rsp.getSubMsg().indexOf("�˵��Ų����Ϲ�����Ѿ���ʹ��") >=0)
		                    {
								//��˾�ڲ����ᷢ������
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

		                        Log.info(username,"�˵��Ų����Ϲ�����Ѿ���ʹ��,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��",null);
		                    }else if(rsp.getSubMsg().indexOf("�ö�����֧���޸�") >=0){  
		                    	sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
		                    	Log.info(jobName,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��"+"������Ϣ:"+rsp.getSubMsg()+rsp.getMsg());
		                    }
							else
								Log.info(username,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��"+"������Ϣ:"+rsp.getSubMsg()+rsp.getMsg(),null);
						}
			}catch(Exception ex){
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.info(username,"�޸ķ�����Ϣ��ҵ����������: "+orderid+",������Ϣ: "+ex.getMessage(),null);
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
                	Log.info(username,"�����Ա�����������Ϣ,sheetid:��" + sheetid + "���ɹ�",null);
				}else{
					Log.info(username,"�����Ա�����������Ϣ,sheetid:��" + sheetid + "��ʧ��",null);
				}
			}catch(Exception e){
				Log.error(username,"ͬ������������Ϣ����  "+ e.getMessage(),null);
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
			TaobaoClient client =new DefaultTaobaoClient(Params.url,Params.appkey,Params.appsecret);
			TmcMessageProduceRequest req = new TmcMessageProduceRequest();
			req.setTopic("taobao_jds_TradeTrace");
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("action_time", actionTime);
			map.put("operator", operator);
			map.put("remark", remark);
			map.put("seller_nick", Params.sellernick);
			map.put("status", status);
			map.put("tid", tid);
			String contents = new JSONWriter().write(map);
			//Log.info("contents: " +contents);
			req.setContent(contents);
			TmcMessageProduceResponse rep = client.execute(req, Params.authcode);
			//Log.info("req: "+rep.getBody());
			if(rep.getIsSuccess()){
				isSuccess=true;
				//Log.info("���͵��������������ݳɹ�,������: "+tid+"״̬:��"+status);
			}
		}catch(Exception e){
			Log.error(username,"���͵��������������ݳ���,tid: '"+tid+",status: '"+status+" "+e.getMessage(),null);
		}
		return isSuccess;
	}
	
	
	private List<String> getTradeInfo(Connection conn) throws Exception{
		List<String> sheetids = new ArrayList<String>();
		String sql = "select sheetid from it_upnote where sheettype=6 and receiver='"+Params.tradecontactid+"'";
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
