package com.wofu.netshop.taobao.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
/**
 * 接口客户退货单生成客户退货单线程类
 * @author Administrator
 *
 */
public class GenCustomerRetRunnable implements Runnable{
	private String jobName="淘宝接口退货订单生成客户退货订单作业";
	private CountDownLatch watch;
	private Params param;
	public GenCustomerRetRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {
		// TODO Auto-generated method stub
		Connection connection = null;
		try {
			connection = PoolHelper.getInstance().getConnection("shop");	
			dogenCustomerRet(connection,getSheetList(connection));			
		} catch (Exception e) {
			try {
				if (connection != null && !connection.getAutoCommit())
					connection.rollback();
			} catch (Exception e1) {
				Log.error(param.username,jobName+ " 回滚事务失败",null);
			}
			Log.error(param.username, jobName+" "+Log.getErrorMessage(e));
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				Log.error(param.username,jobName+" 关闭数据库连接失败");
			}
			watch.countDown();
		}
		
	}
	
	private void dogenCustomerRet(Connection conn,List slist) throws SQLException, JException
	{
		Log.info(param.username,"本次共要处理的淘宝退货单总数为: "+slist.size(),null);
		for (Iterator it = slist.iterator(); it.hasNext();) {
			String sheetid = (String) it.next();
			String sql="select tid from ns_refund with(nolock) where sheetid='"+sheetid+"'";
			String refsheetid=SQLHelper.strSelect(conn, sql);
			sql="declare @ret int;  execute  @ret = eco_GetRefund '"+sheetid+"';select @ret ret;";
			try
			{			
				conn.setAutoCommit(false);
				int ret=SQLHelper.intSelect(conn, sql);
				if (ret==0)
				{
					sql = "declare @err int ;exec @Err = IP_DownBak '" + sheetid + "' ,2,'yongjun';select @err;";
					int err=SQLHelper.intSelect(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
				}
					
			}
			catch (Exception e1)
			{		
				Log.info(param.username,"生成客户退货失败!淘宝单号【"+refsheetid+"】,接口单号【" + sheetid + "】",null);
				Log.info(param.username,"详细错误信息："+e1.getMessage(),null);
				if (!conn.getAutoCommit())
					try
					{
						conn.rollback();
					}
					catch (Exception e2) { }
				try
				{
					conn.setAutoCommit(true);
				}
				catch (Exception e3) {
					
				}
				//throw e1;
			}			
		}
		Log.info(param.username,jobName+" 执行完毕！",null);
	}
	
	private List<String> getSheetList(Connection conn)
	{
		ArrayList<String> sheetlist=new ArrayList<String>();
		String sql="";
		try
		{
			sql="select top 500 sheetid from it_downnote with(nolock) where sender='"+param.tradecontactid+"' and sheettype=2";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0;i<vt.size();i++)
			{				
				Hashtable ht=(Hashtable) vt.get(i);
				sheetlist.add(ht.get("sheetid").toString());
			}
		
		}catch(SQLException e)
		{
			Log.error(param.username,jobName+" 取通知单号出错!",null);
		}
		return sheetlist;
	}
	
	

}
