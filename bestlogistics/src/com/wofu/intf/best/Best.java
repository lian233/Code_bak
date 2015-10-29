
package com.wofu.intf.best;

import com.wofu.common.service.Service;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.Properties;

public class Best extends Service
{

	public Best()
	{
	}

	public String description()
	{
		return (new StringBuilder()).append("���������ӿڴ���ϵͳ [V ").append(Version.version).append("]").toString();
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
		//���͵������Ʒ  sheettype=22012
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
				Log.info("�ع�����ʧ��");
			}
			Log.info(Log.getErrorMessage(e));
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				Log.info("�ر����ݿ�����ʧ��");
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
