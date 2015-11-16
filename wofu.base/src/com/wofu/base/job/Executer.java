/*
 * Created on 2006-8-17
 *
 */
package com.wofu.base.job;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.timer.ECS_TimerPolicy;
import com.wofu.common.pool.PoolManager;
import com.wofu.common.service.LoaderManager;
import com.wofu.common.service.ServiceException;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

/**
 * @author penny
 *
 */
public abstract class Executer  extends Thread{
	
	private ECS_TimerPolicy executeobj;
	private Connection connection=null;
	private DataCentre dao=null;
	private String dsname;
	private Connection extconnection=null;
	private DataCentre extdao=null;
	private String extdsname;
	
	protected SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	protected SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
	protected SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * ִ�й���
	 * @author penny
	 */
	

		
	public DataCentre getDao() throws Exception{		
		if (this.dao==null)
		{
			if (this.connection==null){
				this.connection=PoolHelper.getInstance().getConnection(this.dsname);
				this.connection.setAutoCommit(true);
			}
					
			this.dao=new ECSDao(this.connection);
		}else{
			//������ӿ����ԣ������õĻ�����ȡһ��
			dao.checkConnection(dsname);
			connection=dao.getConnection();
		}
		
		return this.dao;
	}




	public ECS_TimerPolicy getExecuteobj() {
		return executeobj;
	}




	public void setExecuteobj(ECS_TimerPolicy executeobj) {
		this.executeobj = executeobj;
	}




	public void setDao(DataCentre datacentre) {
		this.dao=datacentre;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getDsname() {
		return dsname;
	}

	public void setDsname(String dsname) {
		this.dsname = dsname;
	}

	public Connection getExtconnection() {
		return extconnection;
	}

	public void setExtconnection(Connection extconnection) {
		this.extconnection = extconnection;
	}

	public DataCentre getExtdao() throws Exception {
		if (this.extdao==null)
		{
			if (this.extconnection==null)
					this.extconnection=PoolHelper.getInstance().getConnection(this.extdsname);
			this.extdao=new ECSDao(this.extconnection);
		}else{
			//������ӿ����ԣ������õĻ�����ȡһ��
			extdao.checkConnection(extdsname);
			extconnection=extdao.getConnection();
		}
		//������ӿ����ԣ������õĻ�����ȡһ��
		
		return this.extdao;
	}
	
	/**
	 * ����������Ƿ���ڣ���������ڣ�����
	 * */
	public boolean checkExtPool(){
		boolean check = PoolManager.getInstance().checkPool(this.extdsname);
		if(check){  //��������Ƿ���Ч
			Connection conn=null;
			try {
				conn = PoolHelper.getInstance().getConnection(this.extdsname);
			} catch (SQLException e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				Log.error("��ȡ���ݿ����ӳ���", sw.toString());
				check=false;
				sw=null;
				try {
					PoolManager.getInstance().close(this.extdsname);
					
				} catch (SQLException e1) {
					Log.error("�ر���Ч���ӳ�ʧ��", e1.getMessage());
				}
			}finally{
				if(conn!=null){
					try {
						conn.close();
					} catch (SQLException e) {
						Log.error("�ر����ݿ�����ʧ��", e.getMessage());
					}
				}
				
			}
		}
		if (!check){//��������ڣ����ؽ�						
			Connection conn = null;
			String sql=null;
			try{
				try {
					conn = PoolHelper.getInstance().getConnection(
							com.wofu.common.service.Params.getInstance().getProperty("dbname"));
					
					sql="select * from ecs_extds with(nolock) where enable=1 and dsid= " + this.executeobj.getDsid();
					PreparedStatement pst = conn.prepareStatement(sql);
					ResultSet rs = pst.executeQuery();
					
					if (rs.next()){
						Properties prop = new Properties();
						
						prop.setProperty("driver", rs.getString("dbdriver"));
	
						prop.setProperty("url", rs.getString("url"));
						prop.setProperty("user", rs.getString("dbuser"));
						
						prop.setProperty("password", rs.getString("dbpassword"));
						prop.setProperty("maxsize", rs.getString("maxsize"));
						
						if (rs.getInt("encryptflag")==1)
							prop.setProperty("encrypt", "true");
						else
							prop.setProperty("encrypt", "false");
						
						prop.setProperty("encoding", rs.getString("encoding"));
						
						LoaderManager.getInstance().createPool(rs.getString("loadclass") , this.extdsname , false , prop);
						
						PoolManager.getInstance().listPools(System.out);
						
						System.out.println("�����ⲿ���ӳ�");
						check=true;
					}	
					else{
						Log.error("�������ӳ�","�Ҳ������ӳأ�"+sql);
					}
						
				}
				catch (Exception ex)
				{
					Log.error("�������ӳ�",ex.getMessage());
					/**
					sql ="update ecs_extds set  enable=0 where dsid= " + this.executeobj.getDsid();
					try {
						this.getDao().execute(sql);
					} catch (Exception e) {
						e.printStackTrace();
						Log.error("�ر��ⲿ���ӳ�enbable���Գ���", e.getMessage());
					}**/
					check=false;
				}
			}
			finally {			
				try {
					if (conn != null){
						conn.close();
					}	
				} catch (Exception e) {
					Log.error("������ӳ�", "�ر����ݿ�����ʧ��:"+e.getMessage());
				}
			}
			
		}
		
		return check;		
	}
	

	public void setExtdao(DataCentre extdao) {
		this.extdao = extdao;
	}

	public String getExtdsname() {
		return extdsname;
	}

	public void setExtdsname(String extdsname) {
		this.extdsname = extdsname;
	}
	
	protected void updateJobFlag(int flag)  throws Exception 
	{
	
		try
		{
			this.executeobj.setFlag(flag);
			DataCentre dao=this.getDao();
			dao.setTransation(true);
			dao.update(this.executeobj,"flag");
		} catch (Exception e) {
			throw new Exception("����ִ�б�־ʧ��, "
					+ Log.getErrorMessage(e));
		}
	}

	protected void UpdateTimerJob() throws Exception {

		try {
			String str2;
			Calendar cd = Calendar.getInstance();
			
			if (this.executeobj.getClocktype() == 1) {
	
				cd.setTime(this.executeobj.getNextactive());

				cd.add(Calendar.DAY_OF_MONTH, 1);
				str2 = this.dateformat.format(cd.getTime()) + " "
						+ this.executeobj.getClock();
			
				cd.setTime(this.datetimeformat.parse(str2));
			}
			if (this.executeobj.getClocktype() == 2) {
				cd.setTime(this.executeobj.getNextactive());
				do {
					cd.add(Calendar.DAY_OF_MONTH, 1);
					str2 = this.dateformat.format(cd.getTime()) + " "
							+ this.executeobj.getClock();
					cd.setTime(this.datetimeformat.parse(str2));
				} while (System.currentTimeMillis() > cd.getTimeInMillis());
			} else if (this.executeobj.getClocktype() == 0) {
				
				long addmsec = this.timeformat.parse(this.executeobj.getClock()).getTime()
						- this.timeformat.parse("00:00:00").getTime();
				
				cd.add(Calendar.MILLISECOND, (int) addmsec);
			}

			this.executeobj.setActivetimes(this.executeobj.getActivetimes()+1);
			this.executeobj.setErrorcount(0);
			this.executeobj.setErrormessage("");
			this.executeobj.setNextactive(new Date(cd.getTimeInMillis()));
			this.executeobj.setLastactive(new Date(System.currentTimeMillis()));
			DataCentre dao=this.getDao();
			dao.setTransation(true);
			dao.update(this.executeobj,"activetimes,errorcount,errormessage,nextactive,lastactive");
			
		} catch (Exception localException) {
			throw new Exception("����ִ�н��ʧ��, "
					+ Log.getErrorMessage(localException));
		}
	}

	protected void UpdateTimerJob(String errmsg) throws Exception {
		byte[] arrayOfByte = errmsg.getBytes();
		if (arrayOfByte.length > 255)
			errmsg = new String(arrayOfByte, 0, 255);

		try {
			
			this.executeobj.setActivetimes(this.executeobj.getActivetimes()+1);
			this.executeobj.setErrorcount(this.executeobj.getErrorcount()+1);
			this.executeobj.setErrormessage(errmsg);
			this.executeobj.setLastactive(new Date(System.currentTimeMillis()));
			DataCentre dao=this.getDao();
			dao.setTransation(true);
			dao.update(this.executeobj,"activetimes,errorcount,errormessage,lastactive");
			
		} catch (Exception localException) {
			throw new Exception("����ִ�н��ʧ��, "
					+ Log.getErrorMessage(localException));
		}
	}
}
