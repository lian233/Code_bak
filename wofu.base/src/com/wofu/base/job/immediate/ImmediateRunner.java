package com.wofu.base.job.immediate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

//Referenced classes of package com.little.job.A:
//			B

public class ImmediateRunner extends Thread
{

	private static String jobname = "即时作业";
	private boolean is_running;
	private ImmediateJob job;

	public ImmediateRunner()
	{
		setDaemon(true);
		setName(jobname);
	}

	public void run()
	{
		Log.info(jobname, "启动[" + jobname + "]模块");
		long l = 0L;
		int i = -1;
		do
		{
			Connection connection = null;
			is_running = true;
			try
			{
				connection = PoolHelper.getInstance().getConnection(com.wofu.base.job.Params.dbname);
				if (i != Calendar.getInstance().get(6))
					try
					{
						UpdateImmediateJobErrMsg(connection);
						i = Calendar.getInstance().get(6);
						Log.info(jobname, "成功重置错误计数");
					}
					catch (Exception exception)
					{
						Log.error("102", jobname, Log.getErrorMessage(exception));
					}
					ExcuteAllImmediateJob(connection);
			}
			catch (Exception exception1)
			{
				try
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				}
				catch (Exception exception3)
				{
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(exception1));
			}
			finally
			{
				is_running = false;
				job = null;
				try
				{
					if (connection != null)
						connection.close();
				}
				catch (Exception exception5)
				{
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long l1 = System.currentTimeMillis();
			while (System.currentTimeMillis() - l1 < (long)(com.wofu.base.job.Params.waittime * 1000)) 
				try
				{
					sleep(1000L);
				}
				catch (Exception e)
				{
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void ExcuteAllImmediateJob(Connection connection)
		throws Exception
	{
		Statement statement = null;
		ResultSet resultset = null;
		String s="";
		//String s = "select sheetid,sheettype,ExecuteProc,TransFlag,orderFlag from executesheet0 where executeflag=0 and ErrorCount<" + com.wofu.job.Params.trytimes + " order by orderFlag";
		try
		{
			statement = connection.createStatement();
			resultset = statement.executeQuery(s);
			Connection conn = null;
			try
			{
				while (resultset.next()) 
				{
					if (conn == null)
						conn = PoolHelper.getInstance().getConnection(com.wofu.base.job.Params.dbname);
					ImmediateJob ijob = new ImmediateJob();
					job = ijob;
					ijob.setSheetID(resultset.getString("sheetid"));
					ijob.setSheetType(resultset.getInt("sheettype"));
					ijob.setExecuteProc(resultset.getString("ExecuteProc"));
					ijob.setTransFlag(resultset.getInt("TransFlag") == 1);
					ijob.setOrderFlag(resultset.getInt("orderFlag"));
					try
					{
						ExcuteImmediateJob(conn, ijob);
					}
					catch (Exception exception)
					{
						UpdateImmediateJob(conn, ijob, Log.getErrorMessage(exception));
						Log.error(jobname, "执行作业失败, SheetID=" +ijob.getSheetID() + ";SheetType=" + ijob.getSheetType() + Log.getErrorMessage(exception));
					}
				}
			}
			finally
			{
				if (conn != null)
					try
					{
						conn.close();
					}
					catch (Exception exception2) { }
			}
		}
		catch (Throwable throwable)
		{
			throw new Exception("执行作业失败, " + Log.getErrorMessage(throwable));
		}
		finally
		{
			try
			{
				if (resultset != null)
					resultset.close();
			}
			catch (Throwable throwable1)
			{
				Log.error(jobname, "关闭结果集失败" + Log.getErrorMessage(throwable1));
			}
			try
			{
				if (statement != null)
					statement.close();
			}
			catch (Throwable throwable2)
			{
				Log.error(jobname, "关闭数据库实例失败" + Log.getErrorMessage(throwable2));
			}
		}
	}

	private void ExcuteImmediateJob(Connection conn, ImmediateJob jb)
		throws Exception
	{
		try
		{
			if (jb.getTransFlag())
			{
				conn.setAutoCommit(false);
				SQLHelper.executeProc(conn, job.getExecuteProc());
				UpdateImmediateJobErrMsg(conn, jb);
				UpdateImmediateJob(conn, jb);
				conn.commit();
				conn.setAutoCommit(true);
			} else
			{
				SQLHelper.executeProc(conn, jb.getExecuteProc());
			}
			Log.info(jobname, "执行作业成功, SheetID=" + jb.getSheetID() + ";SheetType=" +jb.getSheetType());
		}
		catch (Exception e1)
		{
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
	}

	private void UpdateImmediateJobErrMsg(Connection connection, ImmediateJob jb)
		throws Exception
	{
		String s = "update executesheet0 set executeflag=1,ErrorMessage='',ErrorCount=0  where sheetid='" + jb.getSheetID() + "' and sheettype=" + jb.getSheetType() + " and orderFlag=" + jb.getOrderFlag();
		try
		{
			SQLHelper.executeSQL(connection, s);
		}
		catch (Exception exception)
		{
			throw new Exception("更新执行结果失败, " + Log.getErrorMessage(exception));
		}
	}

	private void UpdateImmediateJob(Connection connection, ImmediateJob jb)
		throws Exception
	{
		try
		{
			ArrayList arraylist = new ArrayList();
			String s = "insert into executesheet select * from executesheet0 where sheetid='" + jb.getSheetID()+ "' and sheettype=" + jb.getSheetType() + " and orderFlag=" + jb.getOrderFlag();
			arraylist.add(s);
			s = "delete from executesheet0 where sheetid='" + jb.getSheetID() + "' and sheettype=" + jb.getSheetType() + " and orderFlag=" + jb.getOrderFlag();
			arraylist.add(s);
			SQLHelper.executeBatch(connection, arraylist);
		}
		catch (Exception exception)
		{
			throw new Exception("备份请求数据失败, " + Log.getErrorMessage(exception));
		}
	}

	private void UpdateImmediateJob(Connection conn, ImmediateJob jb, String errmsg)
		throws Exception
	{
		byte abyte0[] = errmsg.getBytes();
		if (abyte0.length > 255)
			errmsg = new String(abyte0, 0, 255);
		String s = "update executesheet0 set ErrorCount=ErrorCount + 1,ErrorMessage= ?  where sheetid='" +jb.getSheetID() + "' and sheettype=" + jb.getSheetType() + " and orderFlag=" + jb.getOrderFlag();
		PreparedStatement pst = null;
		try
		{
			pst = conn.prepareStatement(s);
			pst.setString(1, errmsg);
			pst.executeUpdate();
		}
		catch (Exception exception)
		{
			throw new Exception("更新执行结果失败, " + Log.getErrorMessage(exception));
		}
		finally
		{
			try
			{
				if (pst != null)
					pst.close();
			}
			catch (Exception e) { }
		}
	}

	private void UpdateImmediateJobErrMsg(Connection conn)
		throws Exception
	{
		String s = "update executesheet0 set ErrorCount=0 where executeflag=0";
		try
		{
			SQLHelper.executeSQL(conn, s);
		}
		catch (Exception exception)
		{
			throw new Exception("重置错误记录失败," + Log.getErrorMessage(exception));
		}
	}

	public String toString()
	{
		return jobname + " " + (is_running ? "[running]" + (job != null ? " " + job.getExecuteProc() + "-" + job.getSheetID() : "") : "[waiting]");
	}

}