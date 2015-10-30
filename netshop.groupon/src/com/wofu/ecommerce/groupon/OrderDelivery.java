package com.wofu.ecommerce.groupon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
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
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;


public class OrderDelivery extends Thread {

	private static String jobname = "团宝订单发货处理作业";

	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.groupon.Params.dbname);
			
					getBusinessProjectOrderDelivery(connection,
							getDeliveryOrders(connection));		
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.groupon.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void getBusinessProjectOrderDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws JSQLException{
		StringBuffer buffer = new StringBuffer();
		String key = Params.key;
		for (int i = 0; i < vdeliveryorder.size(); i++) {	
			Hashtable ht=(Hashtable) vdeliveryorder.get(i);
			String orderid=ht.get("orderid").toString();
			
			buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			buffer.append("<request>");
			
			buffer.append("<order>");
			buffer.append((new StringBuilder("<order_id>")).append(ht.get("orderid").toString())
					.append("</order_id>").toString());
			buffer.append("<post_company>"+ht.get("post_company").toString()+"</post_company>");
			buffer.append("<post_no>"+ht.get("post_no").toString()+"</post_no>");
			buffer.append("</order>");
	
		
			buffer.append((new StringBuilder("<request_time>")).append(
					System.currentTimeMillis()).append("</request_time>").toString());
			buffer.append((new StringBuilder("<sign>")).append(
					MD5Util.MD5Encode((new StringBuilder(String.valueOf(System
							.currentTimeMillis()))).append(key).toString()))
					.append("</sign>").toString());
			buffer.append("</request>");
			OMElement requestSoapMessage = ObjBodyWriter.toOMElement(buffer
					.toString(), "UTF-8");
			Options options = new Options();
			options.setTo(new EndpointReference(Params.wsurl));
			options.setAction("getBusinessProjectOrderDelivery");
			options.setProperty("__CHUNKED__", Boolean.valueOf(false));
			ServiceClient sender = null;
			try {
				sender = new ServiceClient();
				sender.setOptions(options);
				//Log.info(requestSoapMessage.toString());
				OMElement result = sender.sendReceive(requestSoapMessage);
				Document doc = DOMHelper.newDocument(result.toString(), Params.encoding);
				Element urlset = doc.getDocumentElement();
			
				String retcode=DOMHelper.getSubElementVauleByName(urlset, "retcode");
				String errmsg=DOMHelper.getSubElementVauleByName(urlset, "err_msg");
				
				if (retcode.equals("400"))
				{
					Log.warn("处理订单【"+orderid+"】发货失败,发送请求错误!");
				}
				else if (retcode.equals("500"))
				{
					Log.warn("处理订单【"+orderid+"】发货失败,服务器异常,请联系团宝!,错误信息:"+errmsg);
					//Log.info(result.toString());
				}
				else
				{
					NodeList orderinfonodes = urlset.getElementsByTagName("order_info");
					if (orderinfonodes.getLength() != 1)
						throw new JException("返回订单信息不完整" + result.toString());
					
					Element orderinfoelement = (Element) orderinfonodes.item(0);
				
					Element orderelement = (Element) orderinfoelement.getElementsByTagName("order").item(0);
					
					Hashtable htd=SQLHelper.oneRowSelect(conn, "select sheetid,companycode,outsid from ns_delivery where tid='"+orderid+"'");
					String sheetid=htd.get("sheetid").toString();
					String post_company=htd.get("companycode").toString();
					String post_no=htd.get("outsid").toString();
					if (DOMHelper.getSubElementVauleByName(orderelement, "status").equals("100")) {					
						IntfUtils.backupUpNote(conn,"yongjun",sheetid,"3");
						Log.info("处理订单【"+ orderid + "】发货成功,快递公司【"+post_company+"】,快递单号【"+post_no+"】");
					} else {
						IntfUtils.backupUpNote(conn,"yongjun",sheetid,"3");
						Log.info("订单【"+ orderid + "】已发货,不能重复发货,快递公司【"+post_company+"】,快递单号【"+post_no+"】");
					}
		
				}		
			}catch(JException ja)
			{
				Log.error(jobname, ja.getMessage());
			}
			catch(AxisFault af)
			{
				Log.error(jobname, "访问远程服务出错!"+af.getMessage());
			}
			catch(JSQLException jsql)
			{
				Log.error(jobname, "备份数据失败!"+jsql.getMessage());
			}
			catch(Exception e)
			{
				Log.error(jobname, "解析XML出错!"+e.getMessage());
			}	
			buffer.delete(0, buffer.length());
			//Log.info(buffer.toString());			
		}
	}
	

	private Vector<Hashtable> getDeliveryOrders(Connection conn)
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			sql = "select top "+Params.requesttotal+" b.tid, b.companycode,b.outsid from it_upnote a , ns_delivery b "
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ Params.tradecontactid + "'";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable) vt.get(i);
				ht.put("orderid", hto.get("tid").toString());
				ht.put("post_company", hto.get("companycode").toString());
				ht.put("post_no", hto.get("outsid").toString());
				vtorders.add(ht);				
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobname, "查询发货单信息出错:"+sqle.getMessage());
		}		
		return vtorders;
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
