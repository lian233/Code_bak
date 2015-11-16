package com.wofu.base.dbmanager;


import com.wofu.base.util.BusinessObject;


public class ECS_ExtDS extends BusinessObject {
	
	private int dsid;
	private String dsname;
	private String dsip;
	private String dbdriver;
	private int dbport;
	private String dbname;
	private String url;
	private String dbuser;
	private String dbpassword;
	private String encoding;
	private String loadclass;
	private int maxsize;
	private int encryptflag;
	private int enable;
	private String notes;
	
	private void doInsert() throws Exception
	{
		
	}
	private void doUpdate() throws Exception
	{
		
	}
	private void doDelete() throws Exception
	{
		
	}
	public void doTransaction(String action) throws Exception
	{
		
	}
	public String getDbdriver() {
		return dbdriver;
	}
	public void setDbdriver(String dbdriver) {
		this.dbdriver = dbdriver;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getDbpassword() {
		return dbpassword;
	}
	public void setDbpassword(String dbpassword) {
		this.dbpassword = dbpassword;
	}
	public int getDbport() {
		return dbport;
	}
	public void setDbport(int dbport) {
		this.dbport = dbport;
	}
	public String getDbuser() {
		return dbuser;
	}
	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}
	public int getDsid() {
		return dsid;
	}
	public void setDsid(int dsid) {
		this.dsid = dsid;
	}
	public String getDsip() {
		return dsip;
	}
	public void setDsip(String dsip) {
		this.dsip = dsip;
	}
	public String getDsname() {
		return dsname;
	}
	public void setDsname(String dsname) {
		this.dsname = dsname;
	}
	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public int getEncryptflag() {
		return encryptflag;
	}
	public void setEncryptflag(int encryptflag) {
		this.encryptflag = encryptflag;
	}
	public String getLoadclass() {
		return loadclass;
	}
	public void setLoadclass(String loadclass) {
		this.loadclass = loadclass;
	}
	public int getMaxsize() {
		return maxsize;
	}
	public void setMaxsize(int maxsize) {
		this.maxsize = maxsize;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
