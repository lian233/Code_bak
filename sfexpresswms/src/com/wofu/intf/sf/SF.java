
package com.wofu.intf.sf;

import com.wofu.common.service.Service;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.Properties;

public class SF extends Service
{

	public SF()
	{
	}

	public String description()
	{
		return (new StringBuilder()).append("顺风物流接口处理系统 [V ").append(Version.version).append("]").toString();
	}

	public void end()
		throws Exception
	{
	}

	public void init(Properties properties)
		throws Exception
	{
		Params.init(properties);
		setParams(properties);
	}

	public void process()
	{
	}

	public void start()
		throws Exception
	{
		AsynProductInfo asynproductinfo = new AsynProductInfo();
		asynproductinfo.start();
		//推送导入的商品  sheettype=22012
		AsynProductInfoDR asynproductinfodr = new AsynProductInfoDR();
		asynproductinfodr.start();
		
		AsynAsnInfo asynasninfo = new AsynAsnInfo();
		asynasninfo.start();
		
		int threadcount=1;
		
		Connection conn = null;
		try {					
			conn = PoolHelper.getInstance().getConnection(Params.dbname);	
			
			String sql="select threadcount from interfacetype where sheettype=2209";
			threadcount=SQLHelper.intSelect(conn, sql);
		} catch (Exception e) {
			try {
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			} catch (Exception e1) {
				Log.info("回滚事务失败");
			}
			Log.info(Log.getErrorMessage(e));
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				Log.info("关闭数据库连接失败");
			}
		}
		
		for(int i=1;i<=threadcount;i++)
		{
			AsynSalesOrderInfo asynsalesorderinfo = new AsynSalesOrderInfo();
			asynsalesorderinfo.setThreadcount(threadcount);
			asynsalesorderinfo.setThreadid(i);
			asynsalesorderinfo.start();
		}
		
		
		AsynSalesOrderCancel asynsalesordercancel = new AsynSalesOrderCancel();
		asynsalesordercancel.start();
		
		AsynRmaInfo asynrmainfo = new AsynRmaInfo();
		asynrmainfo.start();
		
		AsynReturnInfo asynreturninfo = new AsynReturnInfo();
		asynreturninfo.start();
		
		AsynTranferNote transfernote=new AsynTranferNote();
		transfernote.start();
		

		AsynTansferIn transferin=new AsynTansferIn();
		transferin.start();
	}
}
