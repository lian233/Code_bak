package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.ecommerce.qqbuy.Params;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class UpdateStock extends Thread {

	private static String jobname = "更新QQ网购库存作业";

	private static String tradecontactid = Params.tradecontactid;

	private static int count = 10;

	private static int updateCount = 10;//更新sku临时表时间间隔，单位：分钟

	private static String accessToken = Params.accessToken ;

	private static String appOAuthID = Params.appOAuthID ;

	private static String cooperatorId = Params.cooperatorId ;

	private static String secretOAuthKey = Params.secretOAuthKey ;

	private static String uin = Params.uin ;

	private static String encoding = Params.encoding ;
	
	private boolean is_updating = false;
	
	private static long monthMillis = 30*24*60*60*1000L ;

	public UpdateStock() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_updating = true;
			try {
				updateSkuInfo() ;
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.qqbuy.Params.dbname);
				doUpdateStock(connection,getGoodsInfo(jobname, connection));
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
				e.printStackTrace() ;
			} finally {
				is_updating = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.qqbuy.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	public String toString() {
		return jobname + " " + (is_updating ? "[updating]" : "[waiting]");
	}
	//更新QQ网购商品库存
	public static void doUpdateStock(Connection conn,Vector vtinfo) throws JSQLException, JException , Exception
	{
		if(vtinfo.size() <= 0)
			return ;
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid='"+tradecontactid+"'";
		int orgid=SQLHelper.intSelect(conn, sql);
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("accessToken", accessToken) ;
		params.put("appOAuthID", appOAuthID) ;
		params.put("cooperatorId", cooperatorId) ;
		params.put("secretOAuthKey", secretOAuthKey) ;
		params.put("uin", String.valueOf(uin)) ;
		params.put("encoding", encoding) ;
		for(int i = 0 ; i < vtinfo.size() ; i++)
		{
			Hashtable htinfo=(Hashtable) vtinfo.get(i);
			String tid=htinfo.get("tid").toString();
			String sku=htinfo.get("sku").toString();
			int qty = Integer.parseInt(htinfo.get("qty").toString()) ;
			if(qty==0)
			{
				StockUtils.backupSynReduceStoreNote(jobname, conn, Params.tradecontactid, sku, tid) ;
				Log.info(jobname,"更新QQ网购库存成功,订单号:"+tid+",SKU【"+ sku +"】,库存调整:"+ qty);
				continue ;
			}
			
			StockUtils.updateStock(jobname, conn,tradecontactid,orgid,tid, sku, qty, params) ;
		}
	}
	
	
	//更新商品资料临时表
	public static void updateSkuInfo()
	{
		if (count >= updateCount) 
		{
			Date eDate = new Date(System.currentTimeMillis()) ;
			Date sDate = new Date(System.currentTimeMillis() - 6*monthMillis) ;
			
			String startTime = Formatter.format(sDate, Formatter.DATE_TIME_FORMAT) ;
			String endTime = Formatter.format(eDate, Formatter.DATE_TIME_FORMAT) ;
			String pageSize = "50";
			
			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put("accessToken", accessToken);
			params.put("appOAuthID", appOAuthID);
			params.put("cooperatorId", cooperatorId);
			params.put("secretOAuthKey", secretOAuthKey);
			params.put("uin", String.valueOf(uin));
			params.put("encoding", encoding);
			params.put("startTime", startTime);
			params.put("endTime", endTime);
			params.put("pageSize", pageSize);

			StockUtils.setAllSkuInfo(jobname, params);
			Log.info("更新sku临时表成功") ;
			count = 0 ;
		}
		else
			count ++ ;
	}
	
	/*返回SKU和库存数量
	 * SKU qty
	 */
	public static Vector getGoodsInfo(String jobname,Connection conn)
	{
		Vector vtinfo=null;
		try
		{			
			String sql="select tid,sku,qty from ECO_SynReduceStore with(nolock)"
				+"where tradecontactid='"+Params.tradecontactid+"' "
				+"and synflag=0 and sku is not null and sku<>'' order by sku desc";
			vtinfo=SQLHelper.multiRowSelect(conn, sql);
			//合并相同sku修改数量
			vtinfo = StockUtils.sumSynQty2(vtinfo) ;

		}
		catch(JSQLException e)
		{
			Log.error(jobname, "取商品库存更改信息出错:"+e.getMessage());
		}
		catch(Exception e)
		{
			Log.error(jobname, "合并相同sku库存数量出错:"+e.getMessage());
			e.printStackTrace() ;
		}
		return vtinfo;
	}
}