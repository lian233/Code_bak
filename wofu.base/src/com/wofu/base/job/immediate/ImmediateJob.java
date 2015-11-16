package com.wofu.base.job.immediate;

public class ImmediateJob
{

	private String sheetid;
	private int sheettype;
	private String executeproc;
	private boolean transflag;
	private int orderflag;

	public ImmediateJob()
	{
	}

	public void setSheetID(String sheetid)	
	{
		this.sheetid = sheetid;
	}
	
	public String getSheetID()
	{
		return sheetid;
	}

	public String getExecuteProc()
	{
		return executeproc;
	}

	public void setExecuteProc(String executeproc)
	{
		this.executeproc=executeproc;
	}

	public void setSheetType(int sheettype)
	{
		this.sheettype = sheettype;
	}
	
	public int getSheetType() {
	
		return sheettype;
	}

	public void setTransFlag(boolean flag)
	{
		transflag = flag;
	}
	
	public boolean getTransFlag()
	{
		return transflag;
	}
	
	public void setOrderFlag(int orderflag)
	{
		this.orderflag=orderflag;
	}
	public int getOrderFlag()
	{
		return orderflag;
	}

}