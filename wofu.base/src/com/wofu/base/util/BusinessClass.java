package com.wofu.base.util;

import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.systemmanager.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract interface BusinessClass {
	
	
	public abstract String getUniqueFields1();
	
	public abstract String getUniqueFields2();
	
	public abstract String getUniqueFields3();
	
	public abstract Connection getConnection();
	
	public abstract void setConnection(Connection conn);

	public abstract DataCentre getDao() throws Exception;
	
	public abstract DataCentre getExtDao(String dsname) throws Exception;
	
	public abstract void setDao(DataCentre datacentre);
	
	public abstract void setReqeust(HttpServletRequest req);
	
	public abstract HttpServletRequest getRequest();
	
	public abstract void setResponse(HttpServletResponse res);
	
	public abstract HttpServletResponse getResponse();
	
	public abstract void OutputStr(String str) throws Exception;
	
	public abstract void OutputStream(InputStream in) throws Exception;
	
	
	public abstract String toJSONArray(List lst) throws Exception;
	
	public abstract String toJSONObject(Map mp) throws Exception;
	
	public abstract String toJSONObject() throws Exception;
	
	public abstract String toJSONObject(String fields) throws Exception;
	
	public abstract void getJSONData() throws Exception;
	
	public abstract String getReqData() throws Exception;
	
	public abstract String getJSONTree(String sql,String idfieldname,String namefieldname,
			String parentfieldname,String parentfieldvalue,String wheresql) throws Exception;
	
	public abstract void getMapData(Map mp) throws Exception;	
	
	public abstract UserInfo getUserInfo() throws Exception;
	
	public abstract void copyTo(BusinessClass anotherobj) throws Exception;
	
	public abstract boolean BusiExists(int busid) throws Exception;
	
	public abstract void getDataByID(int busid) throws Exception;
	
	public abstract void getDataByID(int busid,String idfield) throws Exception;
}
