package com.wofu.ecommerce.taobao;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
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
import com.taobao.api.request.TradeGetRequest;
import com.taobao.api.request.TradeMemoUpdateRequest;
import com.taobao.api.request.WlbOrderJzwithinsConsignRequest;
import com.taobao.api.response.LogisticsConsignResendResponse;
import com.taobao.api.response.LogisticsOfflineSendResponse;
import com.taobao.api.response.TmcMessageProduceResponse;
import com.taobao.api.response.TradeGetResponse;
import com.taobao.api.response.TradeMemoUpdateResponse;
import com.taobao.api.response.WlbOrderJzwithinsConsignResponse;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
public class OrderDelivery extends Thread {

	private static String jobname = "�Ա���������������ҵ";
	//private static DecimalFormat df = new DecimalFormat("0.00");
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {
				if(Params.isRemote)
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname,Params.isRemote,Params.dsid,Params.dsName);
				else
					connection = PoolHelper.getInstance().getConnection(
							com.wofu.ecommerce.taobao.Params.dbname);
				Params.authcode = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				
				System.out.println("����2222222"+Params.authcode);
				delivery(connection);	
				resend(connection);
				//modifiRemark(connection);
				//�ش�����״̬����
				//sendTradeInfo(getTradeInfo(connection),connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
				e.printStackTrace();
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	//�޸Ķ�����ע
	private void modifiRemark(Connection conn) throws Exception{
		String sql = "select sheetid,sender from it_upnote where sheettype=5 and flag=0 and receiver='"+Params.tradecontactid+"'";
		Vector modifiReList = SQLHelper.multiRowSelect(conn, sql);
		if(modifiReList.size()==0) return;
		for(int i=0;i<modifiReList.size();i++){
			try{
				Hashtable item = (Hashtable)modifiReList.get(i);
				String sheetid = item.get("sheetid").toString();
				String sender = item.get("sender").toString();
				String[] remarks = sender.split(":");
				//������
				String tid = remarks[0];
				//��ע
				String remark = remarks[1];
				String tempRemark = getRemark(Long.parseLong(tid));
				if(!"".equals(tempRemark)) remark=tempRemark +"  "+remark;
				TaobaoClient client=new DefaultTaobaoClient(Params.url, Params.appkey, Params.appsecret);
				TradeMemoUpdateRequest req=new TradeMemoUpdateRequest();
				req.setTid(Long.parseLong(tid));
				req.setMemo(remark);
				req.setFlag(3L);//��ɫ
				TradeMemoUpdateResponse response = client.execute(req , Params.authcode);
				String alipayId = String.valueOf(response.getTrade().getAlipayId());
				if(tid.equals(alipayId)){
					try {
						conn.setAutoCommit(false);

						sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
								+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
								+ " where SheetID = '"+ sheetid+ "' and SheetType = 5";
						SQLHelper.executeSQL(conn, sql);

						sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=5";

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
						throw new JSQLException(sql, sqle);
					}
					Log.info(jobname,"��������" + tid + "�� sheetid��"+sheetid+"�� �޸ı�ע�ɹ�");
				}else{
					Log.info(jobname,"��������" + tid + "�� sheetid��"+sheetid+"�� �޸ı�עʧ��");
				}
			}catch(Exception ex){
				Log.error(jobname, ex.getMessage());
			}
			
		}
	
		Log.info(jobname+",�޸Ķ�����ע���");
		
	}
	
	//��ȡԭ���ı�ע
	private String getRemark(long orderId) throws Exception{
		String result ="";
		TaobaoClient client=new DefaultTaobaoClient(Params.url, Params.appkey, Params.appsecret);
		TradeGetRequest req=new TradeGetRequest();
		req.setFields("seller_memo");
		req.setTid(orderId);
		TradeGetResponse response = client.execute(req , Params.authcode);
		if(response!=null){
			result = response.getTrade().getSellerMemo()!=null?response.getTrade().getSellerMemo():"";
		}
		return result;
	}

	/**
	 * @param conn
	 * @throws Exception
	 * api :taobao.logistics.offline.send ���
	 */
	private void delivery(Connection conn)  throws Exception
	{
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+"upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("����Ҫ������Ա���������Ϊ:��"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid")!=null?hto.get("sheetid").toString():"";
			String orderid = hto.get("tid")!=null?hto.get("tid").toString():"";
			String post_company = hto.get("companycode")!=null?hto.get("companycode").toString():"";
			String post_no = hto.get("outsid")!=null?hto.get("outsid").toString():"";		
			//���������˾Ϊ������Դ���
			if (post_company.trim().equals(""))
			{
				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
					+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
					+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
	
				SQLHelper.executeSQL(conn, sql);
				Log.warn(jobname, "��ݹ�˾Ϊ�գ�������:"+orderid+"");
				continue;
			}
			
			//���������˾Ϊ������Դ���
			if (post_no.trim().equals(""))
			{
				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
					+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
					+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
	
				SQLHelper.executeSQL(conn, sql);
				Log.warn(jobname, "��ݵ���Ϊ�գ�������:"+orderid+"");
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
				
				Log.warn(jobname, "�����š�"+orderid+"����ȫ������!");
				continue;
			}
			if(Params.isJZ){
				sql ="select top 1 lbpdc from ns_customerorder where tid='"+orderid+"'";
				String isJz= SQLHelper.strSelect(conn, sql);
				if("zj".equals(isJz)){//��װ����
					jzSend(post_no,orderid,post_company,conn,sheetid);
					Log.info("��װ������ʱ������: ������: "+orderid);
				}else
					normalSend(post_no,orderid,post_company,conn,sheetid);
			}else
			normalSend(post_no,orderid,post_company,conn,sheetid);
			

			
		}
	}
	
	private void normalSend(String post_no,String orderid,String post_company,Connection conn,String sheetid){
		try {
			String sql;
			TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret);
			LogisticsOfflineSendRequest req=new LogisticsOfflineSendRequest();	
			req.setOutSid(post_no);
			//��long��������Ϸ�  С��1000��Ҳ���Ϸ�
			if(new BigInteger(orderid).compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE)))>0 || new BigInteger(orderid).compareTo(new BigInteger(String.valueOf("1000")))<0){
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
				Log.info(jobname,"��������" + orderid + "��,�����Ų��Ϸ���");
				return;
			}
			Long tid  = TranTid(orderid);
			req.setTid(TranTid(orderid));
			req.setCompanyCode(post_company);
			LogisticsOfflineSendResponse rsp = client.execute(req, Params.authcode);
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
				Log.info(jobname,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
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
					Log.info("����: "+orderid+", sheetid: "+sheetid+",״̬�쳣,�ѱ��ݵ����ݱ�");
				}
				else if(rsp.getSubCode().equals("isv.logistics-offline-service-error:AT0011") || rsp.getSubCode().equals("isv.logistics-offline-service-error:AT0112")
						 || rsp.getSubCode().equals("isv.logistics-offline-service-error:B60")){//B60���ᶩ��
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info("����: "+orderid+", sheetid: "+sheetid+",״̬�쳣,�ѱ��ݵ����ݱ�");
				}
				else if(rsp.getSubCode().equals("isv.logistics-offline-service-error:P38")){
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info("����: "+orderid+", sheetid: "+sheetid+",״̬�쳣,,��У��δͨ��.�ѱ��ݵ����ݱ�");
				}
				else if(rsp.getSubCode().equals("isv.logistics-offline-service-error:S01")){
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info("����: "+orderid+", sheetid: "+sheetid+",״̬�쳣,�ѱ��ݵ����ݱ�");
				}
				else if(rsp.getSubCode().equals("CD06")){//�˿�ɹ�
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info("����: "+orderid+", sheetid: "+sheetid+",�˿�ɹ�,�����ٷ���");
				}
				else if(rsp.getSubCode().equals("CD07")){//�˿�ɹ�
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info("����: "+orderid+", sheetid: "+sheetid+",��������״̬��Ϊ�½�״̬,���跢������");
				}
				else if(rsp.getSubCode().equals("CONSIGN_FUZZY_QUERY_ORDER_ERROR")){//�����Ų�����
					//�Ѿ�ͬ������״̬���˻��ɹ���
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info("����: "+orderid+", sheetid: "+sheetid+",�����Ų�����,�ѱ���");
				}
				else if(rsp.getSubCode().indexOf("ORDER_NOT_FOUND_ERROR")!=-1){
                	sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.error(jobname, "������:��"+orderid+", �޷��ҵ���");
                }
				else if(rsp.getSubCode().indexOf("B06")!=-1){//����������
                	sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.error(jobname, "������:��"+orderid+", �޷��ҵ���");
                }
				if (rsp.getSubMsg().indexOf("�����ظ�����")>=0|| rsp.getSubMsg().indexOf("�������Ͳ�ƥ��")>=0)
				{
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(jobname,"������" + orderid + "�������ظ�����,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
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
					Log.info(jobname,"û��Ȩ�޷���,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
				} 
				else if((rsp.getSubMsg().indexOf("�Ӷ�����û�о�ȷƥ��")>=0)) 
				{
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					Log.info(jobname,"����������,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
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
						Log.info(jobname,"��������" + orderid + "��,�Ӷ�����"+subtids+"�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
					}
					
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					
					Log.info(jobname,"�������������ڻ򶩵��Ѿ�����,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
				} else if (rsp.getSubMsg().indexOf("�˵��Ų����Ϲ�����Ѿ���ʹ��") >=0 )
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
					
                    Log.info(jobname,"�˵��Ų����Ϲ�����Ѿ���ʹ��,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
                }
				else{
					System.out.println("...1122.....");
					Log.info(jobname,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��"+"������Ϣ:"+rsp.getSubMsg()+rsp.getMsg());
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
		System.out.println("...3344.....");
		Log.error(jobname,"��������" + orderid + "������ʧ��,��ݹ�˾��"	+ post_company + "��,��ݵ��š�" + post_no	+ "��,������Ϣ:" + e.getMessage());
	}
	catch(Exception e)
	{	e.printStackTrace();
		Log.error(jobname, "ͬ������״̬������, ������Ϣ: "+e.getMessage());
	}
	}
	
	//��װ��������
	private void jzSend(String post_no,String orderid,String post_company,Connection conn,String sheetid){
		try {
			conn.setAutoCommit(false);

			String sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
					+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
					+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
			SQLHelper.executeSQL(conn, sql);

			sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

			SQLHelper.executeSQL(conn, sql);
			conn.commit();
			conn.setAutoCommit(true);
			/**
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
					Log.info(jobname,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
					Log.info("��װ��������: "+arr.get(i).toString());
					break;
				}
				else
				{		
					Log.info("��װ��������ʧ��,������Ϣ: ", rsp.getResultInfo());
					continue;
						
				}
			}
			
			
		
	
	} catch (ApiException e) {

		Log.error(jobname,"��������" + orderid + "������ʧ��,��ݹ�˾��"	+ post_company + "��,��ݵ��š�" + post_no	+ "��,������Ϣ:" + e.getMessage());
	}**/}
	catch(Exception e)
	{
		Log.error(jobname, "ͬ������״̬������, ������Ϣ: "+e.getMessage());
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
				//��long��������Ϸ�  С��1000��Ҳ���Ϸ�
				if(new BigInteger(orderid).compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE)))>0 || new BigInteger(orderid).compareTo(new BigInteger(String.valueOf("1000")))<0){
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
					Log.info(jobname,"��������" + orderid + "��,�����Ų��Ϸ���");
					return;
				}
				//���������˾Ϊ������Դ���
				if (post_company.trim().equals(""))
				{
					Log.warn(jobname, "��ݹ�˾Ϊ�գ�������:"+orderid+"");
					continue;
				}
				
				//���������˾Ϊ������Դ���
				if (post_no.trim().equals(""))
				{
					Log.warn(jobname, "��ݵ���Ϊ�գ�������:"+orderid+"");
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
					
					Log.warn(jobname, "�����š�"+orderid+"����ȫ������!");
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
							Log.info(jobname,"��������" + orderid + "�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
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
								Log.info(jobname,"������" + orderid + "�������ظ�����,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
							}else if (rsp.getSubMsg().indexOf("����δ����")>=0)
							{
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(jobname,"������" + orderid + "������δ���������Ѿ��˻�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
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
								Log.info(jobname,"û��Ȩ�޷���,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
								
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
										Log.info(jobname,"��������" + orderid + "��,�Ӷ�����"+subtids+"�������ɹ�,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
									}
								}
								
								sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
								Log.info(jobname,"�������������ڻ򶩵��Ѿ�����,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
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

		                        Log.info(jobname,"�˵��Ų����Ϲ�����Ѿ���ʹ��,������" + orderid + "��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��");
		                    }else if(rsp.getSubMsg().indexOf("�ö�����֧���޸�") >=0){  
		                    	sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
		                    	Log.info(jobname,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��"+"������Ϣ:"+rsp.getSubMsg()+rsp.getMsg());
		                    }
		                    else if(rsp.getSubMsg().indexOf("�Ӷ�����û�о�ȷƥ��") >=0){  
		                    	sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
								SQLHelper.executeSQL(conn, sql);

								sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";

								SQLHelper.executeSQL(conn, sql);
		                    	Log.info(jobname,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��"+"������Ϣ:"+rsp.getSubMsg()+rsp.getMsg());
		                    }
							else
								Log.info(jobname,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ post_company + "��,��ݵ��š�" + post_no + "��"+"������Ϣ:"+rsp.getSubMsg()+rsp.getMsg());
						}
			}catch(Exception ex){
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.info("�޸ķ�����Ϣ��ҵ����������: "+orderid+",������Ϣ: "+ex.getMessage());
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
                	Log.info(jobname,"�����Ա�����������Ϣ,sheetid:��" + sheetid + "���ɹ�");
				}else{
					Log.info(jobname,"�����Ա�����������Ϣ,sheetid:��" + sheetid + "��ʧ��");
				}
			}catch(Exception e){
				Log.error("ͬ������������Ϣ���� ", e.getMessage());
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
			Log.error("���͵��������������ݳ���,tid: '"+tid+",status: '"+status, e.getMessage());
		}
		return isSuccess;
	}
	
	
	private List<String> getTradeInfo(Connection conn) throws Exception{
		List<String> sheetids = new ArrayList<String>();
		String sql = "select sheetid from it_upnote where sheettype=6 and receiver='"+Params.tradecontactid+"'";
		sheetids = SQLHelper.multiRowListSelect(conn, sql);
		return sheetids;
	}
	
	
    private long TranTid(String tid) throws Exception
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
    
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
