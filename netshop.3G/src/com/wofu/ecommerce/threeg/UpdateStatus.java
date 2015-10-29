package com.wofu.ecommerce.threeg;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.ecommerce.threeg.util.CommonHelper;
import com.wofu.ecommerce.threeg.util.Utility;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.SQLHelper;

public class UpdateStatus extends Thread {
	
	private static String jobname = "3G����״̬������ҵ";
	
	private static String updatestatucmethod="OrderStatusCharge.ashx";
	
	private static String cmdcode="1001";


	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
	
			try {		
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.threeg.Params.dbname);
				doUpdateCheckStatus(connection,Params.tradecontactid);
				doUpdateCancelStatus(connection,Params.tradecontactid);
				doUpdateDeliveryStatus(connection,Params.tradecontactid);
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
			
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.threeg.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	private void doUpdateCheckStatus(Connection conn,String tradecontactid) 
		throws Exception
	{
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "1");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			
			String sql="select tid from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
			String tid=SQLHelper.strSelect(conn, sql);
			
			String body=getBody(tid,"","1","","","");
			
			String requestdata=CommonHelper.getXML(Params.CustomerPrivateKeyPath, Params.agentid, cmdcode, body);
			try
			{
				doUpdate(requestdata);
				IntfUtils.backupUpNote(conn, "yongjun",sheetid, "1");
				Log.info("�������״̬�ɹ�,����:"+tid+"");
			}catch(JException je)
			{
				throw new JException(je.getMessage()+" ����:"+tid+" ����״̬:1");
			}
		}	
	}
	
	private void doUpdateCancelStatus(Connection conn,String tradecontactid) throws Exception
	{
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "2");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			
			String sql="select tid,memo from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
			Hashtable htd=SQLHelper.oneRowSelect(conn, sql);
			
			String tid=htd.get("tid").toString();
			String memo=htd.get("memo").toString();
			
			String body=getBody(tid,memo,"3","","","");
			
			String requestdata=CommonHelper.getXML(Params.CustomerPrivateKeyPath, Params.agentid, cmdcode, body);
			try
			{
				doUpdate(requestdata);
				IntfUtils.backupUpNote(conn, "yongjun",sheetid, "2");
				
				//ȡ��������������
				sql="select c.custombc,b.purqty from customerorder a with(nolock),"
						+" customerorderitem b with(nolock),barcode c with(nolock) "
						+" where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid "
						+" and a.refsheetid='"+tid+"'";
				Vector vtc=SQLHelper.multiRowSelect(conn, sql);				
				for(int j=0;j<vtc.size();j++)
				{
					Hashtable htc=(Hashtable) vtc.get(j);
					String sku=htc.get("custombc").toString();
					int qty=Double.valueOf(htc.get("purqty").toString()).intValue();
					StockManager.addSynReduceStore(jobname, conn, tradecontactid, "3",tid, sku, qty,false);
				}
				
				Log.info("����ȡ��״̬�ɹ�,����:"+tid+"");
			}catch(JException je)
			{
				throw new JException(je.getMessage()+" ����:"+tid+" ����״̬:3 ȡ����Ϣ:"+memo);
			}
		}	
	}
	
	private void doUpdateDeliveryStatus(Connection conn,String tradecontactid) throws Exception
	{
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "3");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			
			String sql="select tid,companycode,outsid from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
			Hashtable htd=SQLHelper.oneRowSelect(conn, sql);
			
			String tid=htd.get("tid").toString();
			String companycode=htd.get("companycode").toString();
			String outsid=htd.get("outsid").toString();
			
			String body=getBody(tid,"","2",companycode,outsid,"");
			
			String requestdata=CommonHelper.getXML(Params.CustomerPrivateKeyPath, Params.agentid, cmdcode, body);
			try
			{
				doUpdate(requestdata);
				IntfUtils.backupUpNote(conn, "yongjun",sheetid, "3");
				Log.info("���·���״̬�ɹ�,����:"+tid+"");
			}catch(JException je)
			{
				throw new JException(je.getMessage()+" ����:"+tid+" ����״̬:2 ��ݹ�˾:"+companycode+" ��ݵ���:"+outsid);
			}
		}	
	}
	
	private String getBody(String orderid,String message,String orderstatus,
			String logisticoperator,String logisticid,String remark)
	{
		StringBuffer bodybuffer=new StringBuffer();
		
		bodybuffer.append("<body>");
		bodybuffer.append("<OrderId>").append(orderid).append("</OrderId>");
		bodybuffer.append("<OrderStatus>").append(orderstatus).append("</OrderStatus>");
		bodybuffer.append("<Message>").append(message).append("</Message>");
		bodybuffer.append("<LogisticOperator>").append(logisticoperator).append("</LogisticOperator>");
		bodybuffer.append("<LogisticId>").append(logisticid).append("</LogisticId>");
		bodybuffer.append("<Remark>").append(remark).append("</Remark>");
		bodybuffer.append("</body>");
		return bodybuffer.toString();
	}
	
	private void doUpdate(String reqeustData) 
		throws Exception
	{
		//Log.info(reqeustData);
		String s=CommonHelper.SendRequest(Params.wsurl+updatestatucmethod,reqeustData);
		
		String bodystr=s.substring(s.indexOf("<body>"), s.indexOf("</body>")+7);
		
		//Log.info(s);
		Document doc = DOMHelper.newDocument(s, Params.encoding);
		Element msgelement = doc.getDocumentElement();
		Element bodyElement=(Element) msgelement.getElementsByTagName("body").item(0);
		Element ctrlElement=(Element) msgelement.getElementsByTagName("ctrl").item(0);

		String messagedigest=DOMHelper.getSubElementVauleByName(ctrlElement, "md");
		if (!Utility.ValifyDigest(bodystr,messagedigest,Params.GGMallPublicKeyPath))
		{
			
			throw new JException("ǩ����֤ʧ��!");
		}
		else
		{
			String message=DOMHelper.getSubElementVauleByName(bodyElement, "message");
			if (!message.toUpperCase().equals("Y"))
			{
				throw new JException("����ʧ��");
			}
			
		}
	}

}
