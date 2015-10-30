package com.wofu.ecommerce.vjia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import com.wofu.ecommerce.vjia.Params;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class GenCustomerOrder extends Thread{
	private static String jobname = "Vjia订单生成客户订单作业";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do 
		{
			Connection connection = null;
			is_gening = true;
			try
			{
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				//生成vjia新客户订单
				if(Params.isgenorder)
				doGenCustomerOrder(connection) ;
				//生成vjia退换货单
				if(Params.isgenorderRet)
				dogenReturnCustomerOrder(connection) ;
			}
			catch (Exception e)
			{
				e.printStackTrace() ;
			}
			finally {
				is_gening = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.vjia.Params.waittime * 1000))
				try 
				{
					sleep(1000L);
				} 
				catch (Exception e) 
				{
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		}while (true);
	}
	//生成vjia新单
	private void doGenCustomerOrder(Connection connection)
	{
		try 
		{
			Vector vts=IntfUtils.getDownNotes(connection, String.valueOf(Params.tradecontactid), "1");
			Log.info("本次生成订单总数为:　"+vts.size());
			for (int i=0;i<vts.size();i++)
			{
				Hashtable hts=(Hashtable) vts.get(i);
				String sheetid=hts.get("sheetid").toString();
				// 开始事务
				connection.setAutoCommit(false);
				// 生成客户订单
				boolean is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);
				if (is_success)
				{
					// 备份接口数据
					IntfUtils.backupDownNote(connection, "yongjun",sheetid, "1");
					// 提交事务
					connection.commit();
					connection.setAutoCommit(true);
					Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
				}
				else
				{
					try
					{
						if (connection != null && !connection.getAutoCommit())
							connection.rollback();
					}
					catch (Exception e1) 
					{
						Log.error(jobname, "回滚事务失败");
					}
					Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
				}
			}
			Log.info("生成订单完毕！");
		}
		catch (Exception e)
		{
			Log.error(jobname, "生成vjia订单失败，错误信息："+e.getMessage()) ;
		}
	}
	//生成退换货单
	private void dogenReturnCustomerOrder(Connection conn) throws SQLException, JException
	{
		try 
		{
			ArrayList<String> slist=new ArrayList<String>();
			String sql="";
			sql="select sheetid from it_downnote with(nolock) where sender='"+Params.tradecontactid+"' and sheettype=2";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				slist.add(ht.get("sheetid").toString());
			}
			
			for (Iterator it = slist.iterator(); it.hasNext();) {
				String sheetid = (String) it.next();
				
				sql="select tid from ns_refund with(nolock) where sheetid='"+sheetid+"'";
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
						if (err==-1)
						{		
							//throw new JException("备份数据失败!"+sql);
							Log.error(jobname, "备份数据失败");
							if(conn!=null && !conn.getAutoCommit()) {
								conn.rollback();
								conn.setAutoCommit(true);
								
							}
							continue;
						}
						Log.info("生成客户退货成功,vjia单号【"+refsheetid+"】,接口单号【" + sheetid + "】");
					}
					else if (ret==-1)
					{
						Log.info("生成客户退货失败!vjia单号【"+refsheetid+"】,接口单号【" + sheetid + "】");
						if(conn!=null && !conn.getAutoCommit()) {
							conn.rollback();
							conn.setAutoCommit(true);
							
						}
						continue;
					}
					conn.commit();
					conn.setAutoCommit(true);		
				}
				catch (SQLException e1)
				{		
					Log.info("生成客户退货失败!vjia单号【"+refsheetid+"】,接口单号【" + sheetid + "】");
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
					catch (Exception e3) { }
					continue;
				}			
			}	
		}
		catch (Exception e) 
		{
			if(conn!=null && !conn.getAutoCommit()) {
				conn.rollback();
				conn.setAutoCommit(true);
				
			}
				
			Log.error(jobname, "生成vjia退换货单失败，错误信息："+e.getMessage()) ;
		}
	}

	public String toString()
	{
		return jobname + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}