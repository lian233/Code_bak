package com.wofu.ecommerce.groupon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

import meta.MD5Util;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.groupon.ws.ObjBodyWriter;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class ProcessOrderStatus extends Thread {

	private static String jobname = "�����ű������˿���ҵ";
	
	private boolean is_importing=false;
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.groupon.Params.dbname);
				ProcessUnCheckedOrder(connection);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.groupon.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	/*
	 * �����˿� ��Ч���� �˻�ʧ�� �˻��ɹ���δ��˶�����ֱ��ȡ��
	 */
	private void ProcessUnCheckedOrder(Connection conn) throws SQLException
	{
		String sql="";
		try
		{

			sql="select top "+Params.requesttotal+" refsheetid from customerorder0 where tradecontactid='"+Params.tradecontactid+"'";
			Vector<String> vt=SQLHelper.multiRowSelect(conn, sql);	
	
			Vector vtos = getOrderStatus(vt);
			for (int j = 0; j < vtos.size(); j++) {
				Hashtable ht = (Hashtable) vtos.get(j);

				// 6=�˿�ɹ�
				String orderid = ht.get("orderid").toString();
				String status = ht.get("status").toString();
				String updatetime = ht.get("update_time").toString();

				if (status.equals("6")) {
					sql = "select notes from TradeContractStatus where TradeContactID="
							+ String.valueOf(Params.tradecontactid)
							+ " and status='"
							+ status + "'";
					String exnotes = SQLHelper.strSelect(conn, sql);

					conn.setAutoCommit(false);

					sql = "update customerorder0 set flag=97,notes=notes+'"
							+ exnotes + ",ȡ������ʱ�䡾" + updatetime
							+ "��' where refsheetid='" + orderid + "'";
					SQLHelper.executeSQL(conn, sql);

					sql = "select a.refsheetid,a.inshopid,b.PurQty,c.custombc "
							+ "from customerorder0 a,customerorderitem0 b,barcode c"
							+ "where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid "
							+ "a.refsheetid='" + orderid + "'";
					Vector vtgoods = SQLHelper.multiRowSelect(conn, sql);
					for (int i = 0; i < vtgoods.size(); i++) {
						
						Hashtable htgoods = (Hashtable) vtgoods.get(i);
						
						String inshopid = htgoods.get("inshopid").toString();
						String qty = htgoods.get("purqty").toString();
						String sku = htgoods.get("custombc").toString();
						
						sql = "declare @err int; execute @Err=eco_AddSynReduceStore '"
								+ orderid+ "','"+ inshopid+ "','"+ sku+ "'," + qty + "; select @err";
						int ret = SQLHelper.intSelect(conn, sql);
						if (ret == -1) {
							throw new JSQLException(sql, "��¼����ͬ��������ݳ���");
						}

					}

					sql = "select sheetid from customerorder0 where refsheetid='"
							+ orderid + "'";
					String sheetid = SQLHelper.strSelect(conn, sql);

					sql = "declare @err int; execute @Err=TL_SheetTransfer '"
							+ sheetid
							+ "',1,'CustomerOrder0,CustomerOrderItem0','CustomerOrder,CustomerOrderItem'; select @err";

					int ret = SQLHelper.intSelect(conn, sql);
					if (ret == -1) {
						throw new JSQLException(sql, "����ת����ʽ��ʧ��");
					}

					conn.commit();
					conn.setAutoCommit(true);
					Log.info("δ��˿ͻ�����ȡ��,�ͻ������š�" + sheetid + "��,�ű����š�" + orderid
							+ "��");
				}
			}
				
			
		}catch(JSQLException je)
		{
			if (!conn.getAutoCommit())
				try
				{
					conn.rollback();
				}
				catch (Exception e1) { }
			try
			{
				conn.setAutoCommit(true);
			}
			catch (Exception e1) { }
			Log.error(jobname, je.getMessage());
			throw je;			
		}
		
	}	
	/*
	 * �����˿� ��Ч���� �˻�ʧ�� �˻��ɹ�������˶���δ�������
	 */
	/*
	private void ProcessCheckedOrder(Connection conn) throws SQLException
	{
		String sql="";
		ArrayList<String> list=new ArrayList<String>();
		try
		{
			sql="select TradeContactID from TradeContacts where TradeContacts='"+Params.tradecontact+"'";					
			int tradecontactid=SQLHelper.intSelect(conn, sql);
			
			sql="select top "+Params.requesttotal+" sheetid,refsheetid,customid,linkman from customerorder "
			+" where inshopid='"+Params.shopid+"' and sheetid not in(select refsheetid from customerdelive where inshopid='"+Params.shopid+"')"
			+" and tradefrom='"+Params.tradecontact+"'";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);	
			
			Hashtable<String,String> hts=new Hashtable<String,String>();
			Hashtable<String,String> htc=new Hashtable<String,String>();
			Hashtable<String,String> htl=new Hashtable<String,String>();
			for(int i=0;i<vt.size();i++)
			{		
				Hashtable hto = (Hashtable) vt.get(i);
				list.add(hto.get("refsheetid").toString());
				hts.put(hto.get("refsheetid").toString(), hto.get("sheetid").toString());
				htc.put(hto.get("refsheetid").toString(), hto.get("customid").toString());
				htl.put(hto.get("refsheetid").toString(), hto.get("linkman").toString());

				Hashtable ht = getOrderStatus(list);
				for (Iterator it = ht.keySet().iterator(); it.hasNext();) {
					// 2=�����˻� 4=��Ч���� 5=�˿�ʧ�� 6=�˿�ɹ�
					String orderid = (String) it.next();
					String s=ht.get(orderid).toString();
					String status=s.substring(0, s.indexOf("-")-1);
					String updatetime=s.substring(s.indexOf("-")+1, s.length()-1);
					if (status.equals("2")|| status.equals("4")|| status.equals("5")|| status.equals("6")) {
						
						conn.setAutoCommit(false);

						// д����˶���ȡ���쳣��ʾ
						sql = "insert into OrderExceptionTips(sheetid,refsheetid,Customerid,customername,tradecontactid,tradeContact_status,tipsid,updatetime) "
								+ "values('"+ hts.get(orderid).toString()+ "','"+ orderid+ "','"+ htc.get(orderid).toString()+ "','"+ htl.get(orderid).toString()
								+ "','"	+ String.valueOf(tradecontactid)+ "','"	+ ht.get(orderid).toString() + "',2,'"+updatetime+"')";
						SQLHelper.executeSQL(conn, sql);

						conn.commit();
						conn.setAutoCommit(true);
						Log.info("����˿ͻ�����ȡ��,�ͻ������š�" + hts.get(orderid).toString() + "��,�ű����š�"+orderid+"��");
					}
				}
				
			}
			
		}catch(JSQLException je)
		{
			if (!conn.getAutoCommit())
				try
				{
					conn.rollback();
				}
				catch (Exception e1) { }
			try
			{
				conn.setAutoCommit(true);
			}
			catch (Exception e1) { }
			Log.error(jobname, je.getMessage());
			throw je;
		}
	}
	/*
	 * �����˿� ��Ч���� �˻�ʧ�� �˻��ɹ�������˶����Ѽ��δ��������
	 */
	/*
	private void ProcessCheckedUnPickingOrder(Connection conn) throws SQLException
	{
		String sql="";
		ArrayList<String> list=new ArrayList<String>();
		try
		{
			sql="select TradeContactID from TradeContacts where TradeContacts='"+Params.tradecontact+"'";					
			int tradecontactid=SQLHelper.intSelect(conn, sql);
			
			sql="select top "+Params.requesttotal+" sheetid,refsheetid,customid,linkman from customerorder "
			+" where inshopid='"+Params.shopid+"' and sheetid in(select refsheetid from customerdelive where inshopid='"+Params.shopid+"' and flag=5)"
			+" and tradefrom='"+Params.tradecontact+"'";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);	
			
			Hashtable<String,String> hts=new Hashtable<String,String>();
			Hashtable<String,String> htc=new Hashtable<String,String>();
			Hashtable<String,String> htl=new Hashtable<String,String>();
			for(int i=0;i<vt.size();i++)
			{		
				Hashtable hto = (Hashtable) vt.get(i);
				list.add(hto.get("refsheetid").toString());
				hts.put(hto.get("refsheetid").toString(), hto.get("sheetid").toString());
				htc.put(hto.get("refsheetid").toString(), hto.get("customid").toString());
				htl.put(hto.get("refsheetid").toString(), hto.get("linkman").toString());

				Hashtable ht = getOrderStatus(list);
				for (Iterator it = ht.keySet().iterator(); it.hasNext();) {
					// 2=�����˻� 4=��Ч���� 5=�˿�ʧ�� 6=�˿�ɹ�
					String orderid = (String) it.next();
					String s=ht.get(orderid).toString();
					String status=s.substring(0, s.indexOf("-")-1);
					String updatetime=s.substring(s.indexOf("-")+1, s.length()-1);
					if (status.equals("2")|| status.equals("4")|| status.equals("5")|| status.equals("6")) {
						
						conn.setAutoCommit(false);

						// д����˶���ȡ���쳣��ʾ
						sql = "insert into OrderExceptionTips(sheetid,refsheetid,Customerid,customername,tradecontactid,tradeContact_status,tipsid,updatetime) "
								+ "values('"+ hts.get(orderid).toString()+ "','"+ orderid+ "','"+ htc.get(orderid).toString()+ "','"+ htl.get(orderid).toString()
								+ "','"	+ String.valueOf(tradecontactid)+ "','"	+ ht.get(orderid).toString() + "',3,'"+updatetime+"')";
						SQLHelper.executeSQL(conn, sql);

						conn.commit();
						conn.setAutoCommit(true);
						Log.info("����˿ͻ�����ȡ��,�ͻ������š�" + hts.get(orderid).toString() + "��,�ű����š�"+orderid+"��");
					}
				}
				
			}
			
		}catch(JSQLException je)
		{
			if (!conn.getAutoCommit())
				try
				{
					conn.rollback();
				}
				catch (Exception e1) { }
			try
			{
				conn.setAutoCommit(true);
			}
			catch (Exception e1) { }
			Log.error(jobname, je.getMessage());
			throw je;
		}
	}
	*/
	private Vector getOrderStatus(Vector<String> vtlist)
	{
		Vector<Hashtable> vtos=new Vector<Hashtable>();
		StringBuffer buffer = new StringBuffer();					
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buffer.append("<request>");
		for (int i=0;i<vtlist.size();i++) {
			String orderid=vtlist.get(i);
			buffer.append((new StringBuilder("<order_id>")).append(orderid).append("</order_id>").toString());
		}
		buffer.append((new StringBuilder("<request_time>")).append(System.currentTimeMillis()).append("</request_time>").toString());
		buffer.append((new StringBuilder("<sign>")).append(MD5Util.MD5Encode((new StringBuilder(String.valueOf(System.currentTimeMillis()))).append(Params.key).toString())).append("</sign>").toString());
		buffer.append("</request>");
		OMElement requestSoapMessage = ObjBodyWriter.toOMElement(buffer.toString(), "UTF-8");
		Options options = new Options();
		options.setTo(new EndpointReference(Params.wsurl));
		options.setAction("getBusinessProjectUserstatus");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try
		{
			sender = new ServiceClient();
			sender.setOptions(options);					
			OMElement result = sender.sendReceive(requestSoapMessage);
			Document doc = DOMHelper.newDocument(result.toString(), Params.encoding);
			Element urlset = doc.getDocumentElement();
			NodeList orderinfonodes = urlset.getElementsByTagName("order_info");
			if (orderinfonodes.getLength() != 1)
				throw new JException("���ض�����Ϣ������"+result.toString());
						
			Element orderinfoelement = (Element) orderinfonodes.item(0);
			NodeList ordernodes = orderinfoelement
					.getElementsByTagName("order");
			for (int i = 0; i < ordernodes.getLength(); i++) {

				Element orderelement = (Element) ordernodes.item(i);
				Hashtable<String,String> ht=new Hashtable<String,String>();
				ht.put("orderid", orderelement.getElementsByTagName("order_id").item(0)
						.getChildNodes().item(0).getNodeValue());
				ht.put("status", orderelement.getElementsByTagName("status").item(0).getChildNodes()
						.item(0).getNodeValue());
				ht.put("update_time", Formatter.format(new Date(Long.valueOf(orderelement.getElementsByTagName(
				"update_time").item(0).getChildNodes().item(0)
				.getNodeValue())),Formatter.DATE_TIME_FORMAT));		
				vtos.add(ht);
			}
		
		} catch (JException ja) {
			Log.error(jobname, "ȡ����״̬ʧ��!"+ja.getMessage());
		} catch (AxisFault af) {
			Log.error(jobname, "����Զ�̷������!" + af.getMessage());
		} catch (Exception e) {
			Log.error(jobname, "����XML����!" + e.getMessage());
		}
		return vtos;
	}
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
