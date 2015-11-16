package com.wofu.base.job.timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import com.wofu.base.job.Executer;
import com.wofu.base.job.Params;
import com.wofu.base.job.timer.ECS_TimerPolicy;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class TimerRunner extends Thread {
	private static final String jobname = "定时作业";
	
	private boolean is_running = false;

	private ECS_TimerPolicy job;
	//存放执行错误的线程表id,下次循环把flag改为0	
	private static Vector<Integer> errorVector=new Vector<Integer>(); 
	private String groupname=""; 

	public TimerRunner() {
		setDaemon(true);
		setName("定时作业");
	}

	public TimerRunner(String gn) {
		this.groupname = gn;
		setDaemon(true);
		setName("定时作业");
	}
	
	public static synchronized void modifiedErrVect(int id){
		errorVector.add(id);
	}
	
	public static synchronized void resetErrVect(Connection conn) throws Exception{
		if(errorVector.size()>0){
			StringBuilder sql =new StringBuilder().append("update ecs_timerpolicy set flag=0 where id in(");
			Log.info("上次执行错误线程数: "+errorVector.size());
			for(Integer e:errorVector){
				Log.info(e+"");
				sql.append(e).append(",");
			}
			
				try{
					SQLHelper.executeSQL(conn, sql.deleteCharAt(sql.length()-1).append(")").toString());
				}catch(Exception ex){
					throw new Exception("重置错误记录失败,"
							+ Log.getErrorMessage(ex));
				}
				
				errorVector.removeAllElements();
			
		}
		
		
	}
	

	public void run() {
		Log.info("定时作业", "启动[定时作业]模块");
		long startwaittime;
		int i = -1;
		do {
			Connection connection = null;
			try {	
				connection = PoolHelper.getInstance().getConnection(com.wofu.base.job.Params.dbname);
				resetErrVect(connection);
				Log.info("重置上次执行错误线程成功!");
				resetErrLarEle(connection);
				if (i != Calendar.getInstance().get(6)) {
					try {
						ResetErr(connection);
						i = Calendar.getInstance().get(6);
						Log.info("定时作业", "成功重置错误计数");
						
					} catch (Exception localException1) {
						Log.error("102", "定时作业", Log
								.getErrorMessage(localException1));
					}
	
				}
	
				this.is_running = true;
				ExecuteJob(connection);
				this.is_running = false;
				this.job = null;
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < com.wofu.base.job.Params.waittime * 1000){
				try {
					//System.out.println("sleep 5");
					sleep(1000L);
				} catch (Exception e) {
					Log.warn("定时作业", "系统不支持休眠操作, 作业将严重影响机器性能");
				}
			}
			
		} while(true);
	}

	private void ExecuteJob(Connection conn) {
		Iterator it;
		try {
			it = getAllJob(conn).iterator();
	
			while (true) {
				ECS_TimerPolicy tjob=null;
			
				if (!(it.hasNext()))
					break;
	
				tjob = (ECS_TimerPolicy) it.next();
				this.job = tjob;

				if (tjob.getMaxretry() < tjob.getErrorcount())
				{
					Log.warn("定时作业", "作业[" + tjob.getActivetimes() + "] ["
							+ tjob.getNotes() + "] 已达最大错误次数["
							+ tjob.getErrorcount() + "], 放弃");			
					continue;
				}

				
				String sql="select dsname from ecs_extds where dsid="+tjob.getDsid();
				String dsname=SQLHelper.strSelect(conn, sql);
				
				if (dsname.equals("")) throw new JException("定时作业["+tjob.getNotes()+"]数据源未配置!");
				
				Executer executer=null;
				
				
				try {
					executer=(Executer) Class.forName(tjob.getExecuter().trim()).newInstance();
					executer.setExecuteobj(tjob);
					executer.setDsname(com.wofu.base.job.Params.dbname);
					executer.setExtdsname(dsname);
					executer.start();
				} catch (Exception e) {				
				
					Log.error("定时作业", "启动作业失败 [" + tjob.getActivetimes()
							+ "] [" + tjob.getNotes() + "] \r\n  "
							+ Log.getErrorMessage(e));
				}
			}
		} catch (Exception e) {
			Log.error("201", "定时作业", "处理作业失败,"
					+ Log.getErrorMessage(e));
		}
	}

	private Vector getAllJob(Connection conn) throws Exception {
		String sql = "select id, active, clock, lastActive, "
			+"nextActive, executer, params,notes, activeTimes, ErrorCount, ErrorMessage, "
			+"MaxRetry,Skip, clocktype,dsid,groupname from ECS_TimerPolicy  where active=1 and flag=0 and nextActive<getdate()";
			//+" MaxRetry,Skip, clocktype,dsid from ECS_TimerPolicy  where active=1 "
			//+" and (flag=0 or (flag=1 and datediff(mi,nextactive, getdate())>=30 ))  and nextActive<getdate()";
		if (groupname !=""){
			sql += " and groupname='"+groupname+"'";
		}
		PreparedStatement pst = null;
		ResultSet rs = null;
		Vector vt = new Vector();
		try {
			ECS_TimerPolicy o;
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
		
			while (rs.next()) {
				o = new ECS_TimerPolicy();
				o.setId(rs.getInt("id"));
				o.setActive(rs.getInt("active"));
				o.setClock(rs.getString("clock").trim());	
				o.setParams(rs.getString("params").trim());
				o.setLastactive(new Date(rs.getTimestamp("lastActive")
						.getTime()));
				o.setNextactive(new Date(rs.getTimestamp("nextActive")
						.getTime()));
				o.setExecuter(rs.getString("executer").trim());
				o.setNotes(rs.getString("notes").trim());
				o.setActivetimes(rs.getInt("activeTimes"));
				o.setErrorcount(rs.getInt("errorcount"));
				o.setMaxretry(rs.getInt("MaxRetry"));
				o.setSkip(rs.getInt("Skip"));
				o.setClocktype(rs.getInt("clocktype"));
				o.setDsid(rs.getInt("dsid"));
				o.setGroupname(rs.getString("groupname"));
				vt.add(o);
				
			
			}		
			
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (pst != null)
					pst.close();
			} catch (Exception e) {
			}	
		}
		return vt;
	}


	private void ResetErr(Connection conn) throws Exception {
		
		
		try {
			String sql = "update ECS_TimerPolicy set ErrorCount=0,flag=0";			
			SQLHelper.executeSQL(conn, sql);
			
		} catch (Exception localException) {
			throw new Exception("重置错误记录失败,"
					+ Log.getErrorMessage(localException));
		}
	}
	
	//重置lastactive or nextactive比当前时间大于半个小时 并且错误次数大于11次的job
	private void resetErrLarEle(Connection conn) throws Exception{
		try{
			String sql = "update ECS_TimerPolicy set ErrorCount=0 where ErrorCount>=11 and GETDATE()>DATEADD(mi,15,lastactive)";			
			SQLHelper.executeSQL(conn, sql);
			//处理异常数据
			sql = "update ECS_TimerPolicy set flag=0 where flag=1 and GETDATE()>DATEADD(hh,3,lastactive)";			
			SQLHelper.executeSQL(conn, sql);
			//重置处理时间超过2的
		}catch(Exception ex){
			throw new Exception("重置错误次数达11次，lastactive小于当前时间15分钟的错误记录失败,"
					+ Log.getErrorMessage(ex));
		}
		
	}

	public String toString() {
		return jobname+" "
				+ ((this.is_running) ? "[running]"
						+ ((this.job == null) ? "" : new StringBuffer().append(
								" ").append(this.job.getId()).append("-").append(
								Params.dbname).toString()) : "[waiting]");
	}
}
