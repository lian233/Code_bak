package com.wofu.base.dbmanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.util.BusinessClass;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.util.JException;

public abstract class DataCentre {
	
	  public abstract void setTransation(boolean autoCommit) throws Exception;
	  
	  public abstract void commit() throws Exception;
	  
	  public abstract void rollback() throws Exception;
	  
	  public abstract void freeConnection() throws Exception;
	  
	  public abstract int execute(String sql) throws JSQLException;
	  public abstract void executeBatch(Collection collection) throws JSQLException;
	  
	  public abstract void executePreparedSQL(String sql,Object[] objs) throws JSQLException;

	  public abstract int delete(BusinessClass businessclass) throws Exception;
	  
	  public abstract int delete(BusinessClass businessclass,String wheresql) throws Exception;
	  
	  public abstract int deleteByKeys(BusinessClass businessclass,String keyfields) throws Exception;
	  
	  public abstract int delete(String tablename ,String wheresql) throws Exception;
	  
	  public abstract int IDGenerator(String tablename, String keyname) throws Exception;
	  public abstract int IDGeneratorMysql(String tablename, String keyname) throws Exception;
	  
	  public abstract int IDGenerator(BusinessClass businessclass, String keyname) throws Exception;
	  
	  public abstract String BusiCodeGenerator(int merchantid,String tablename, String fieldname) throws Exception;
	  
	  public abstract String BusiCodeGenerator(int merchantid,BusinessClass businessclass, String fieldname) throws Exception;
	  
	  public abstract int update(BusinessClass businessclass) throws Exception;
	  
	  public abstract int update(BusinessClass businessclass,String updatefields) throws Exception;
	  
	  public abstract int update(BusinessClass businessclass,String updatefields,String keyfield) throws Exception;
	  
	  public abstract int updateByKeys(BusinessClass businessclass,String keysfields) throws Exception;
	  	 
	  public abstract int update(String tablename,String updatefields,String fieldvalues,String wheresql) throws Exception;
	  
	  public abstract int insert(BusinessClass businessclass) throws Exception;
	  public abstract int insert(BusinessClass businessclass,String key) throws Exception;
	  
	  public abstract Vector multiRowSelect(String sql) throws Exception;
	  
	  public abstract List oneListSelect(String sql) throws Exception;
	  
	  public abstract Hashtable oneRowSelect(String sql) throws Exception;
	  
	  public abstract int intSelect(String sql)  throws Exception;
	  
	  public abstract Vector getSQLMeta(String sql)  throws Exception;
	  
	  public abstract Vector getTableMeta(String sql)  throws Exception;

	  public abstract String strSelect(String sql) throws Exception;	  
	  
	  public abstract ResultSet getResultSet(String sql) throws Exception;
	  
	  public abstract BusinessClass getObjByID(String classname,String keyid) throws Exception;
	  
	  public abstract boolean exists(BusinessClass businessclass) throws Exception;
	  
	  public abstract Connection getConnection();
	  public abstract void setConnection(Connection con) throws Exception;
	  
	  public abstract int[] copyTo(DataCentre targetdc,Properties sqls) throws Exception;
	  //检测数据库连接可用性
	  public abstract void checkConnection(String nsName) throws Exception;
	  public abstract int countByKeys(BusinessClass businessclass, String keyfields)throws Exception;
	  
	  
}
