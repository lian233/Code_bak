package com.wofu.ecommerce.coo8;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class GenCustomerOrder extends Thread{
	private static String jobName = "库巴订单生成客户订单作业";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do 
		{
			Connection connection = null;
			is_gening = true;
			try
			{
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				//生成库巴新客户订单
				if(Params.isgenorder)
				doGenCustomerOrder(connection) ;
				//生成库巴退换货单
				if(Params.isgenorderRet)
				dogenReturnCustomerOrder(connection) ;
			}
			catch (Exception e)
			{	try{
					if(connection!=null && !connection.getAutoCommit()) connection.rollback();
				}catch(Exception ex){
					Log.error(jobName, ex.getMessage());
				}
				
				e.printStackTrace() ;
			}
			finally {
				is_gening = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try 
				{
					sleep(1000L);
				} 
				catch (Exception e) 
				{
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		}while (true);
	}
	//生成库巴新单
	private void doGenCustomerOrder(Connection connection)
	{
		try 
		{
			Vector vts=IntfUtils.getDownNotes(connection, String.valueOf(Params.tradecontactid), "1");
			for (int i=0;i<vts.size();i++)
			{	
				try{
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
							Log.error(jobName, "回滚事务失败");
						}
						Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
					}
				}catch(Exception ex){
					if(connection!=null && !connection.getAutoCommit()) connection.rollback();
					Log.error(jobName, ex.getMessage());
				}
				
			}
		}
		catch (Exception e)
		{
			Log.error(jobName, "生成库巴订单失败，错误信息："+e.getMessage()) ;
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
				try{
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
								throw new JException("备份数据失败!"+sql);
							}
							Log.info("生成客户退货成功,库巴单号【"+refsheetid+"】,接口单号【" + sheetid + "】");
						}
						else if (ret==-1)
						{
							Log.info("生成客户退货失败!库巴单号【"+refsheetid+"】,接口单号【" + sheetid + "】");
							throw new JException(sql);
						}
						conn.commit();
						conn.setAutoCommit(true);		
					}
					catch (SQLException e1)
					{		
						Log.info("生成客户退货失败!库巴单号【"+refsheetid+"】,接口单号【" + sheetid + "】");
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
						throw e1;
					}
				}catch(Exception ex){
					if(conn!=null && !conn.getAutoCommit()) conn.rollback();
					Log.error(jobName, ex.getMessage());
				}
							
			}	
		}
		catch (Exception e) 
		{
			Log.error(jobName, "生成库巴退换货单失败，错误信息："+e.getMessage()) ;
		}
	}

	public String toString()
	{
		return jobName + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}