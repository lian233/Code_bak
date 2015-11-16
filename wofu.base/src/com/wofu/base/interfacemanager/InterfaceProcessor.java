package com.wofu.base.interfacemanager;

import java.lang.reflect.Method;
import java.sql.Connection;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.util.BusinessClass;
import com.wofu.common.tools.sql.PoolHelper;

public abstract class InterfaceProcessor {
	
	private String busid;
	private int bustype;
	private int	orgid;
	private String vertifycode;
	private DataCentre dao;
	private int extinterfaceid;
	private DataCentre extdao;
	private Connection extconnection;
	private int noteid;
	private boolean isExists=false;
	private int merchantid;

	public int getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}

	public abstract void execute() throws Exception;
	
	public DataCentre getExtDao() throws Exception
	{
		if (this.extdao==null)
		{
			if (this.extconnection==null)
			{
				String sql="select dsname from ecs_extds where dsid="+this.extinterfaceid;
				String dsname=this.getDao().strSelect(sql);
				this.extconnection=PoolHelper.getInstance().getConnection(dsname);
			}
			this.extdao=new ECSDao(this.extconnection);
		}
		
		return this.extdao;
	}
	
	public int IDGenerator(BusinessClass downobj,String downkeyfields,
			String downkeycompfields,BusinessClass inobj,String inkeyfield) throws Exception
	{
		int idvalue=0;
		String intablename=inobj.getClass().getSimpleName();
		
		String sql="select count(*) from "+intablename+" where merchantid="+String.valueOf(this.merchantid);
		
		String vsql="select "+inkeyfield+" from "+intablename+" where merchantid="+String.valueOf(this.merchantid);
		
		String[] keyfields=downkeyfields.split(",");
		String[] compfields=downkeycompfields.split(",");
		
		for (int i=0;i<keyfields.length;i++)
		{
			String downgetmethodname = "get"
				+ keyfields[i].substring(0, 1).toUpperCase()
				+ keyfields[i].substring(1, keyfields[i].length());
			
			Method th = downobj.getClass().getMethod(downgetmethodname);
			String downkeyvalue =String.valueOf(th.invoke(downobj));
		
			sql=sql+" and "+compfields[i]+"='"+downkeyvalue+"'";
			vsql=vsql+" and "+compfields[i]+"='"+downkeyvalue+"'";
				
		}
		
		
		if (this.getDao().intSelect(sql)>0)
		{
			this.isExists=true;			
			idvalue=this.getDao().intSelect(vsql);
		}
		else
		{
			this.isExists=false;
			idvalue=this.getDao().IDGenerator(inobj, inkeyfield);
		}
		return idvalue;
	}
	
	public int getIDByCustomID(String tablename,String idfield,
			String customfields,String customvalues) throws Exception
	{
		
		String sql="select "+idfield+" from "+tablename+" where merchantid="+this.merchantid;
		String[] fields=customfields.split(",");
		String[] values=customvalues.split(",");
		
		for (int i=0;i<fields.length;i++)
		{
			String fieldname=fields[i];
			String fieldvalue=values[i];
			sql=sql+" and "+fieldname+"='"+fieldvalue+"'" ;
		}
		
		return this.getDao().intSelect(sql);
	}
	
	public void Import(BusinessClass obj) throws Exception
	{
		if (this.isExists)
			this.getDao().update(obj);
		else
			this.getDao().insert(obj);
		
		this.isExists=false;  //导入一条记录后立即将isExists设置为false
	}

	public String getBusid() {
		return busid;
	}

	public void setBusid(String busid) {
		this.busid = busid;
	}

	public DataCentre getDao() {
		return dao;
	}

	public void setDao(DataCentre dao) {
		this.dao = dao;
	}

	public int getExtinterfaceid() {
		return extinterfaceid;
	}

	public void setExtinterfaceid(int extinterfaceid) {
		this.extinterfaceid = extinterfaceid;
	}


	public int getNoteid() {
		return noteid;
	}

	public void setNoteid(int noteid) {
		this.noteid = noteid;
	}

	public int getOrgid() {
		return orgid;
	}

	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}

	public int getBustype() {
		return bustype;
	}

	public void setBustype(int bustype) {
		this.bustype = bustype;
	}

	public String getVertifycode() {
		return vertifycode;
	}

	public void setVertifycode(String vertifycode) {
		this.vertifycode = vertifycode;
	}

	public void setExtDao(DataCentre extdao) {
		this.extdao = extdao;
	}

	
	

}
