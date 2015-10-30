package com.wofu.ecommerce.oauthpaipai;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;
public class OrderDelivery extends Thread {

	private static String jobName = "拍拍订单发货处理作业";
	
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {
				//改变静态时间
				PaiPai.setCurrentDate_DevOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.oauthpaipai.Params.dbname);
			
				doDelivery(connection,getDeliveryOrders(connection));		
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.oauthpaipai.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Throwable{
	

		Log.info("发货作业开始");
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			try{
				Hashtable hto = (Hashtable) vdeliveryorder.get(i);
				String sheetid = hto.get("sheetid").toString();
				String orderid = hto.get("orderid").toString();
				String post_company = hto.get("post_company").toString();
				String post_no = hto.get("post_no").toString();
				
				//检查订单状态
				String sql = "";
				
				if (!doVerifyStatus(orderid))
				{
					Log.warn(jobName,"订单【"+orderid+"】状态不正确,请检查并手工处理该订单!");
					
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
					
					continue;
				}
				
				PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(Params.spid, Params.secretkey, Params.token, Long.valueOf(Params.uid));
				
				sdk.setCharset(Params.encoding);
				
				HashMap<String, Object> params = sdk.getParams("/deal/sellerConsignDealItem.xhtml");
		
				params.put("sellerUin", Params.uid);
				params.put("dealCode", orderid);
				params.put("logisticsName", post_company);
				params.put("logisticsCode", post_no);
				params.put("arriveDays", hto.get("arrivedays").toString());

				
				String result = sdk.invoke();;	

				Document doc = DOMHelper.newDocument(result.toString(),Params.encoding);
				Element urlset = doc.getDocumentElement();
				String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
				String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage");

				
				if (errorcode.equals("0")) {						
					
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);						

					Log.info("处理订单【" + orderid + "】发货成功,快递公司【"+ post_company + "】,快递单号【" + post_no + "】");
				} else {
					Log.error(jobName,"处理订单【" + orderid + "】发货失败,快递公司【"+ post_company + "】,快递单号【" + post_no+ "】,错误信息:" + errormessage);
				}
			}catch(Throwable ex){
				try{
					if(conn!=null && !conn.getAutoCommit())
						conn.rollback();
				}catch(Throwable e){
					Log.error(jobName, e.getMessage());
				}
				
				Log.error(jobName, ex.getMessage());
				
			}
			

		}
		Log.info("发货作业执行完毕");
	}
	
	//标志订单发货状态前检查订单是否为等待卖家发货，频繁调用发货接口失败会被处罚
	private boolean doVerifyStatus(String orderid) throws Throwable
	{
		boolean status_flag=true;
			
		PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(Params.spid, Params.secretkey, Params.token, Long.valueOf(Params.uid));
		
		sdk.setCharset(Params.encoding);
		
		HashMap<String, Object> params = sdk.getParams("/deal/getDealDetail.xhtml");
	
		params.put("sellerUin", Params.uid);			
		params.put("dealCode", orderid);
			
		
		String result = sdk.invoke();
		//Log.info("result: "+ result);
		Document doc = DOMHelper.newDocument(result.toString(), Params.encoding);
		Element urlset = doc.getDocumentElement();
		//在线付款跟货到付款是两种不同的状态
		if (!DOMHelper.getSubElementVauleByName(urlset,"dealState").equalsIgnoreCase("DS_WAIT_SELLER_DELIVERY"))
		{
			status_flag=false;
		}
		else
		{
			status_flag=true;				
		}
		
		return status_flag;
	}

	
	private Vector<Hashtable> getDeliveryOrders(Connection conn) throws Throwable
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays from it_upnote a , ns_delivery b,deliveryref c "
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ Params.tradecontactid + "' and b.companycode=c.companycode";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString());
				ht.put("post_company", hto.get("companycode").toString());
				ht.put("post_no", hto.get("outsid").toString());
				ht.put("arrivedays",hto.get("defaultarrivedays"));
				vtorders.add(ht);
			}
		}
		catch(Throwable sqle)
		{
			Log.error(jobName, "查询发货单信息出错:"+sqle.getMessage());
		}		
		return vtorders;
	}
	
	
	public String toString()
	{
		return jobName + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
