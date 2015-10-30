package com.wofu.ecommerce.rke;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke.utils.Utils;
public class OrderDelivery extends Thread {

	private static String jobname = "麦斯卡经销订单发货处理作业";
	
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);

				delivery(connection);	
				
				//modifiRemark(connection);

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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void delivery(Connection conn)  throws Exception
	{
		
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+"upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("本次要处理的订单发货条数为: "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderid = hto.get("tid").toString();
			String post_company = hto.get("companycode").toString();
			String post_no = hto.get("outsid").toString();		
			
			//如果物流公司为空则忽略处理
			if (post_company.trim().equals(""))
			{
				Log.warn(jobname, "快递公司为空！订单号:"+orderid+"");
				continue;
			}
			
			String postcompanyid=getCompanyID(post_company);
			
			if (postcompanyid.equals(""))
			{
				//如果物流公司为空则忽略处理
				if (post_company.trim().equals(""))
				{
					Log.warn(jobname, "快递公司未配置！快递公司："+post_company+" 订单号:"+orderid+"");
					continue;
				}
			}
			/*
			 * http://www.mesuca.com/shop/index.php?
				act=api& 
				op=deliver& 
				service=deliver& 
				order_id=123& 
				express_company=223& 
				express_id=23242& 
				vcode=h9iX0TiKwmxtX0xrfVwKrJ& 
				page=1& 
				page_size=1

			 */
			StringBuffer optBuffer =new StringBuffer();
			optBuffer.append("http://www.mesuca.com/shop/index.php?");
			optBuffer.append("act=api&");
			optBuffer.append("op=deliver&");
			optBuffer.append("service=deliver&");
			optBuffer.append("order_id="+orderid+"&");
			optBuffer.append("express_company="+post_company+"&");
			optBuffer.append("express_id="+post_no+"&");
			optBuffer.append("vcode=h9iX0TiKwmxtX0xrfVwKrJ&");
			optBuffer.append("page=1&");
			optBuffer.append("page_size=1");
			
			String responseOrderListData = Utils.sendbyget(optBuffer.toString());
			Log.info("responseOrderListData: "+responseOrderListData);
			
			JSONObject resultObject = new JSONObject(responseOrderListData);
			
			if (!resultObject.getString("msg").equals("获取订单信息成功"))
			{
				String errdesc=resultObject.getString("msg");
				
				Log.warn("订单发货失败,订单号:["+orderid+"],快递公司:["+post_company+"],快递单号:["+post_no+"] 错误信息:"+errdesc);
				
			}
				
			
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
				throw new JSQLException(sql, sqle);
			}
			Log.info(jobname,"处理订单【" + orderid + "】发货成功,快递公司【"+ post_company + "】,快递单号【" + post_no + "】");

		}
	}
	
	private String getCompanyID(String companycode) throws Exception
	{
		String companyid="";
		Object[] cys=StringUtil.split(Params.company, ";").toArray();
		for(int i=0;i<cys.length;i++)
		{
			String cy=(String) cys[i];
			
			Object[] cs=StringUtil.split(cy, ":").toArray();
			
			String ccode=(String) cs[0];
			String cid=(String) cs[1];
			
			if(ccode.equals(companycode))
			{
				companyid=cid;
				break;
			}
		}
		
		return companyid;
	}
	
//	private String getDeliveryDetail(Connection conn,String orderId) throws Exception{
//		String sql = "select PurQty product_number,OuterSkuID product_sn from customerorderitem a,CustomerOrder b  where a.sheetid=b.sheetid and b.refsheetid='"+orderId+"'";
//		Vector result = SQLHelper.multiRowSelect(conn, sql);
//		JSONArray itemArr = new JSONArray();
//		for(Iterator it = result.iterator();it.hasNext();){
//			Hashtable ht = (Hashtable)it.next();
//			itemArr.put(ht);
//			
//		}
//		String re =new JSONObject().put("goods", itemArr).toString();
//		Log.info("itemInfo: "+re);
//		return re;
//	}
	
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
