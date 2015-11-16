package com.wofu.base.dbmanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.wofu.base.util.BusinessClass;
import com.wofu.base.util.DataRelation;
import com.wofu.common.pool.TinyConnection;
import com.wofu.common.tools.sql.BatchCopy;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.common.tools.sql.SQLHelper;

public final class ECSDao extends DataCentre {
	private static Integer lock=1;
	private Connection conn = null;

	public ECSDao(Connection connection) {
		this.conn = connection;
	}

	public Connection getConnection() {
		return this.conn;
	}
	

	public int IDGenerator(String tablename, String keyname) throws Exception {
		synchronized(lock){
			int ret = 0;
			String sql = "select count(*) from ecs_idlist with(nolock) where tablename='"
					+ tablename + "' and keyname='" + keyname + "'";
			if (intSelect(sql) == 0) {
				sql = "insert into ecs_idlist(tablename,keyname,keyid) "
						+ "values('" + tablename + "','" + keyname + "',1)";
				execute(sql);

				ret = 1;
			} else {
				sql = "select keyid from ecs_idlist with(nolock) where tablename='"
						+ tablename + "' and keyname='" + keyname + "'";
				ret = intSelect(sql) + 1;

				sql = "select max(" + keyname + ") from " + tablename
						+ " with(nolock)";
				int maxkeyid = intSelect(sql);
				if (maxkeyid >= ret) {
					ret = maxkeyid + 1;
					sql = "update ecs_idlist set keyid=" + String.valueOf(ret)
							+ " where tablename='" + tablename + "' and keyname='"
							+ keyname + "'";
					execute(sql);
				} else {
					sql = "update ecs_idlist set keyid=keyid+1 where tablename='"
							+ tablename + "' and keyname='" + keyname + "'";
					execute(sql);
				}
			}
			return ret;
		}
		
	}
	public int IDGeneratorMysql(String tablename, String keyname) throws Exception {
		synchronized(lock){
			int ret = 0;
			String sql = "select count(*) from ecs_idlist where tablename='"
					+ tablename + "' and keyname='" + keyname + "'";
			if (intSelect(sql) == 0) {
				sql = "insert into ecs_idlist(tablename,keyname,keyid) "
						+ "values('" + tablename + "','" + keyname + "',1)";
				execute(sql);

				ret = 1;
			} else {
				sql = "select keyid from ecs_idlist  where tablename='"
						+ tablename + "' and keyname='" + keyname + "'";
				ret = intSelect(sql) + 1;

				sql = "select max(" + keyname + ") from " + tablename;
				int maxkeyid = intSelect(sql);
				if (maxkeyid >= ret) {
					ret = maxkeyid + 1;
					sql = "update ecs_idlist set keyid=" + String.valueOf(ret)
							+ " where tablename='" + tablename + "' and keyname='"
							+ keyname + "'";
					execute(sql);
				} else {
					sql = "update ecs_idlist set keyid=keyid+1 where tablename='"
							+ tablename + "' and keyname='" + keyname + "'";
					execute(sql);
				}
			}
			return ret;
		}
		
	}
	

	public int IDGenerator(BusinessClass businessclass, String keyname)
			throws Exception {
		String tablename = businessclass.getClass().getSimpleName();
		int ret = IDGenerator(tablename, keyname);
		return ret;
	}

	public String BusiCodeGenerator(int merchantid, String tablename,
			String fieldname) throws Exception {
		String busicode = "";

		String sql = "select mcode from ecs_merchant where id=" + merchantid;
		String mcode = this.strSelect(sql);

		String datestr = Formatter.format(new Date(), "yyMMdd");

		sql = "select isnull(max(" + fieldname + "),'') from " + tablename
				+ " " + "where " + fieldname + " like '"
				+ mcode.concat(datestr) + "%'";
		String maxcode = this.strSelect(sql).trim();

		if (maxcode.equals("")) {
			busicode = mcode.concat(datestr) + "0001";

		} else {

			int maxserialid = Integer.valueOf(maxcode.substring(12)).intValue();
			maxserialid = maxserialid + 1; // 最大序号加1

			busicode = mcode.concat(datestr).concat(
					StringUtil.replicate("0", 4 - String.valueOf(maxserialid)
							.length())).concat(String.valueOf(maxserialid));
		}

		return busicode;
	}

	public String BusiCodeGenerator(int merchantid,
			BusinessClass businessclass, String fieldname) throws Exception {
		String tablename = businessclass.getClass().getSimpleName();
		String ret = BusiCodeGenerator(merchantid, tablename, fieldname);
		return ret;
	}

	public void commit() throws Exception {
		try {
			conn.commit();
		} catch (SQLException e) {

			throw new JException(e);
		}
	}

	public int delete(BusinessClass businessclass) throws Exception {
		int ret = 0;
		String tablename = businessclass.getClass().getSimpleName();
		String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
				+ tablename + "'";
		String keyname = strSelect(sql);
		if (keyname.equals(""))
			throw new JException("table:[" + tablename + "] not key config");
		String keyvalue = "";
		boolean isfindkey = false;
		StringBuffer deletesql = new StringBuffer();
		deletesql.append("delete " + tablename + " where ");
		Field[] fields = businessclass.getClass().getDeclaredFields();
		String getmethodname = "get" + keyname.substring(0, 1).toUpperCase()
				+ keyname.substring(1, keyname.length());
		Method th = null;
		for (int i = 0; i < fields.length; i++) {
			try {
				String fieldname = fields[i].getName();
				// String fieldvalue=fields[i].get(businessclass).toString();
				if (fieldname.equalsIgnoreCase(keyname)) {
					th = businessclass.getClass().getMethod(getmethodname);
					keyvalue = String.valueOf((Integer) th
							.invoke(businessclass));
					isfindkey = true;
					break;
				}

			} catch (Exception e) {
				throw new JException(e);
			}
		}
		if (!isfindkey)
			throw new JException("key:[" + keyname + "] not found");
		if (keyvalue.equals(""))
			throw new JException("key;[" + keyname + "] not assigned value");

		deletesql.append(keyname + "='" + keyvalue + "'");

		ret = execute(deletesql.toString());
		return ret;
	}

	public int delete(BusinessClass businessclass, String wheresql)
			throws Exception {
		String tablename = businessclass.getClass().getSimpleName();
		return delete(tablename, wheresql);
	}

	public int deleteByKeys(BusinessClass businessclass, String keyfields)
			throws Exception {
		int ret = 0;
		String tablename = businessclass.getClass().getSimpleName();
		List<String> keyfieldlist = new ArrayList<String>(StringUtil.split(
				keyfields, ','));
		List<Object> deleteobjs = new ArrayList<Object>();
		Object keyvalue = null;
		boolean isfindkey = false;
		StringBuffer deletesql = new StringBuffer();
		deletesql.append("delete from " + tablename + " where 1=1 ");
		Field[] fields = businessclass.getClass().getDeclaredFields();
		Method th = null;
		for (Iterator it = keyfieldlist.iterator(); it.hasNext();) {
			String keyname = (String) it.next();
			isfindkey = false;
			String getmethodname = "get"
					+ keyname.substring(0, 1).toUpperCase()
					+ keyname.substring(1, keyname.length());
			for (int i = 0; i < fields.length; i++) {
				try {
					String fieldname = fields[i].getName();
					// String
					// fieldvalue=fields[i].get(businessclass).toString();
					if (fieldname.equalsIgnoreCase(keyname)) {
						th = businessclass.getClass().getMethod(getmethodname);
						keyvalue = th.invoke(businessclass);
						isfindkey = true;
						break;
					}

				} catch (Exception e) {
					throw new JException(e);
				}
			}
			if (!isfindkey)
				throw new JException("key:[" + keyname + "] not found");
			if (keyvalue == null)
				throw new JException("key;[" + keyname + "] not assigned value");
			deletesql.append(" and " + keyname + "=?");
			deleteobjs.add(keyvalue);
		}
		ret = SQLHelper.executePreparedSQL(conn, deletesql.toString(),
				deleteobjs.toArray());
		return ret;
	}

	public int delete(String tablename, String wheresql) throws Exception {
		if (wheresql.trim().equals(""))
			throw new JException("where is empty");
		String sql = "delete from " + tablename + " " + wheresql;
		int ret = execute(sql);
		return ret;
	}

	public int execute(String sql) throws JSQLException {
		int ret;
		try {
			ret = SQLHelper.executeSQL(conn, sql);
		} catch (SQLException e) {
			throw new JSQLException(sql, e);
		}
		return ret;
	}

	public boolean exists(BusinessClass businessclass) throws JException {
		boolean ret = false;
		Method th = null;
		String tablename = businessclass.getClass().getSimpleName();
		String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
			+ tablename + "'";
		try {
			String keyname = strSelect(sql);
			String getmethodname = "get"
				+ keyname.substring(0, 1).toUpperCase()
				+ keyname.substring(1, keyname.length());
			th = businessclass.getClass().getMethod(getmethodname);
			String keyvalue = (String) th.invoke(businessclass);

			sql = "select count(*) from " + tablename + " with(nolock) where "
			+ keyname + "='" + keyvalue + "'";
			ret = intSelect(sql) > 0;
		} catch (Exception e) {
			throw new JException(e);
		}
		return ret;

	}

	public int countByKeys(BusinessClass businessclass, String keyfields)
	throws Exception {
		int ret = 0;
		String tablename = businessclass.getClass().getSimpleName();
		List<String> keyfieldlist = new ArrayList<String>(StringUtil.split(
				keyfields, ','));
		Object keyvalue = null;
		boolean isfindkey = false;
		StringBuffer countsql = new StringBuffer();
		countsql.append("select count(*) from  " + tablename + " with(nolock) where 1=1 ");
		Field[] fields = businessclass.getClass().getDeclaredFields();
		Method th = null;
		for (Iterator it = keyfieldlist.iterator(); it.hasNext();) {
			String keyname = (String) it.next();
			isfindkey = false;
			String getmethodname = "get"
				+ keyname.substring(0, 1).toUpperCase()
				+ keyname.substring(1, keyname.length());
			for (int i = 0; i < fields.length; i++) {
				try {
					String fieldname = fields[i].getName();
					// String
					// fieldvalue=fields[i].get(businessclass).toString();
					if (fieldname.equalsIgnoreCase(keyname)) {
						th = businessclass.getClass().getMethod(getmethodname);
						keyvalue = th.invoke(businessclass);
						isfindkey = true;
						break;
						
					}
					

				} catch (Exception e) {
					throw new JException(e);
				}
			}
			if (!isfindkey)
				throw new JException("key:[" + keyname + "] not found");
			if (keyvalue == null)
				throw new JException("key;[" + keyname + "] not assigned value");
			if(keyvalue.getClass()==java.util.Date.class)
				countsql.append(" and " + keyname + "='").append(Formatter.format(keyvalue, Formatter.DATE_TIME_FORMAT)).append("' ");
			else if(keyvalue.getClass()==java.lang.Integer.class)
				countsql.append(" and " + keyname + "=").append((Integer)keyvalue);
			else
				countsql.append(" and " + keyname + "='").append(keyvalue).append("' ");
		}
		ret = SQLHelper.executeCount(conn, countsql.toString());
		return ret;
	}

	public void freeConnection() throws JException {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			throw new JException(e);
		}
	}

	public BusinessClass getObjByID(String classname, String keyid)
			throws JException {
		BusinessClass businessclass = null;
		try {
			businessclass = (BusinessClass) Class.forName(classname)
					.newInstance();
			String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
					+ classname + "'";
			String keyname = strSelect(sql);
			if (keyname.equals(""))
				throw new JException("table:[" + classname + "] not key config");
			sql = "select * from " + classname + " with(nolock) where "
					+ keyname + "='" + keyid + "'";
			Hashtable ht = oneRowSelect(sql);
			businessclass.getMapData(ht);
		} catch (Exception e) {
			throw new JException(e);
		}
		return businessclass;
	}

	public ResultSet getResultSet(String sql) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			throw new JSQLException(sql, e);
		}
		SQLHelper.close(null, stmt, null);
		return rs;
	}

	public int insert(BusinessClass businessclass) throws Exception {
		int ret = 0;
		String tablename = businessclass.getClass().getSimpleName();
		StringBuffer insertsql = new StringBuffer();
		StringBuffer valuessql = new StringBuffer();
		insertsql.append("insert into " + tablename + "(");
		valuessql.append(" values(");
		List<Object> valueobj = new ArrayList<Object>();
		Field[] fields = businessclass.getClass().getDeclaredFields();
		Method th = null;
		for (int i = 0; i < fields.length; i++) {
			try {
				if (fields[i].getType() != DataRelation.class) {
					String fieldname = fields[i].getName();
					if (fields[i].getType() == DataRelation.class)
						continue;

					String getmethodname = "get"
							+ fieldname.substring(0, 1).toUpperCase()
							+ fieldname.substring(1, fieldname.length());
					th = businessclass.getClass().getMethod(getmethodname);
					Object vobj = th.invoke(businessclass);

					if (fields[i].getType() == Boolean.class) {
						if (((Boolean) vobj).booleanValue())
							vobj = Integer.valueOf(1);
						else
							vobj = Integer.valueOf(0);
					}
					valueobj.add(vobj);
					insertsql.append(fieldname).append(",");
					valuessql.append("?,");

				}
			} catch (Exception e) {
				throw new JException(e);
			}
		}
		insertsql.deleteCharAt(insertsql.length() - 1);
		valuessql.deleteCharAt(valuessql.length() - 1);
		insertsql.append(")");
		valuessql.append(")");
		String sql = insertsql.toString() + valuessql.toString();
		try {
			ret = SQLHelper.executePreparedSQL(conn, sql, valueobj.toArray());
		} catch (SQLException e) {
			throw new JSQLException(sql, e);
		}
		return ret;
	}
	
	
	public int insert(BusinessClass businessclass,String primarykey) throws Exception {
		int ret = 0;
		String tablename = businessclass.getClass().getSimpleName();
		StringBuffer insertsql = new StringBuffer();
		StringBuffer valuessql = new StringBuffer();
		insertsql.append("insert into " + tablename + "(");
		valuessql.append(" values(");
		List<Object> valueobj = new ArrayList<Object>();
		Field[] fields = businessclass.getClass().getDeclaredFields();
		Method th = null;
		for (int i = 0; i < fields.length; i++) {
			try {
				if (fields[i].getType() != DataRelation.class) {
					String fieldname = fields[i].getName();
					if (fields[i].getType() == DataRelation.class)
						continue;
					if(primarykey.equals(fieldname)) continue;

					String getmethodname = "get"
							+ fieldname.substring(0, 1).toUpperCase()
							+ fieldname.substring(1, fieldname.length());
					th = businessclass.getClass().getMethod(getmethodname);
					Object vobj = th.invoke(businessclass);

					if (fields[i].getType() == Boolean.class) {
						if (((Boolean) vobj).booleanValue())
							vobj = Integer.valueOf(1);
						else
							vobj = Integer.valueOf(0);
					}

					valueobj.add(vobj);
					insertsql.append(fieldname).append(",");
					valuessql.append("?,");

				}
			} catch (Exception e) {
				throw new JException(e);
			}
		}
		insertsql.deleteCharAt(insertsql.length() - 1);
		valuessql.deleteCharAt(valuessql.length() - 1);
		insertsql.append(")");
		valuessql.append(")");
		String sql = insertsql.toString() + valuessql.toString();
		try {
			ret = SQLHelper.executePreparedSQL(conn, sql, valueobj.toArray());
		} catch (SQLException e) {
			throw new JSQLException(sql, e);
		}
		return ret;
	}

	public int intSelect(String sql) throws Exception {
		return SQLHelper.intSelect(conn, sql);
	}

	public Vector multiRowSelect(String sql) throws Exception {
		return SQLHelper.multiRowSelect(conn, sql);
	}

	public List oneListSelect(String sql) throws Exception {
		return SQLHelper.multiRowListSelect(conn, sql);
	}

	public Hashtable oneRowSelect(String sql) throws Exception {
		return SQLHelper.oneRowSelect(conn, sql);
	}

	public void rollback() throws JException {
		try {
			conn.rollback();
		} catch (SQLException e) {
			throw new JException(e);
		}

	}

	public void setTransation(boolean autoCommit) throws Exception {
		try {
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			throw new JException(e);
		}
	}

	public String strSelect(String sql) throws Exception {
		return SQLHelper.strSelect(conn, sql);
	}

	public int updateByKeys(BusinessClass businessclass, String keyfields)
			throws JException, SQLException {
		int ret = 0;
		String tablename = businessclass.getClass().getSimpleName();
		List<String> keyfieldlist = new ArrayList<String>(StringUtil.split(
				keyfields, ','));
		boolean isfindkey = false;
		Object keyvalue = null;
		StringBuffer updatesql = new StringBuffer();
		StringBuffer wheresql = new StringBuffer();
		updatesql.append("update " + tablename + " set ");
		wheresql.append(" where 1=1 ");
		List<Object> updateobjs = new ArrayList<Object>();
		List<Object> keyobjs = new ArrayList<Object>();
		Field[] fields = businessclass.getClass().getDeclaredFields();
		boolean fieldiskey = false;
		String keyname = "";
		Method th = null;
		for (int i = 0; i < fields.length; i++) {
			fieldiskey = false;
			try {
				String fieldname = fields[i].getName();
				if (fields[i].getType() == DataRelation.class)
					continue;
				
				String getmethodname = "get"
						+ fieldname.substring(0, 1).toUpperCase()
						+ fieldname.substring(1, fieldname.length());
				th = businessclass.getClass().getMethod(getmethodname);
				Object fieldvalue = th.invoke(businessclass);
				for (Iterator it = keyfieldlist.iterator(); it.hasNext();) {
					keyname = (String) it.next();
					if (fieldname.equalsIgnoreCase(keyname))
						fieldiskey = true;
				}
				if (fieldiskey) {
					isfindkey = true;
					keyvalue = fieldvalue;
					if (keyvalue == null)
						throw new JException("key;[" + keyname
								+ "] not assigned value");
					wheresql.append("and " + fieldname + "=? ");
					keyobjs.add(keyvalue);
				} else {
					if (fields[i].getType() == Boolean.class) {
						if (((Boolean) fieldvalue).booleanValue())
							fieldvalue = Integer.valueOf(1);
						else
							fieldvalue = Integer.valueOf(0);
					}
					updateobjs.add(fieldvalue);
				}
				
				if(!fieldiskey)
					updatesql.append(fieldname + "=?,");
			} catch (Exception e) {
				throw new JException(e);
			}
		}
		if (!isfindkey)
			throw new JException("key:[" + keyname + "] not found");

		updateobjs.addAll(keyobjs);
		updatesql.deleteCharAt(updatesql.length() - 1);
		
		updatesql.append(wheresql.toString());

		
		ret = SQLHelper.executePreparedSQL(conn, updatesql.toString(),
				updateobjs.toArray());
		return ret;
	}

	public int update(BusinessClass businessclass) throws Exception {
		int ret = 0;
		String tablename = businessclass.getClass().getSimpleName();
		String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
				+ tablename + "'";
		String keyname = strSelect(sql);
		if (keyname.equals(""))
			throw new JException("table:[" + tablename + "] not key config");
		boolean isfindkey = false;
		Object keyvalue = null;
		StringBuffer updatesql = new StringBuffer();
		updatesql.append("update " + tablename + " set ");
		List<Object> updateobjs = new ArrayList<Object>();
		Field[] fields = businessclass.getClass().getDeclaredFields();
		Method th = null;
		for (int i = 0; i < fields.length; i++) {
			try {
				String fieldname = fields[i].getName();
				if (fields[i].getType() == DataRelation.class)
					continue;

				String getmethodname = "get"
						+ fieldname.substring(0, 1).toUpperCase()
						+ fieldname.substring(1, fieldname.length());
				th = businessclass.getClass().getMethod(getmethodname);
				Object fieldvalue = th.invoke(businessclass);

				if (fieldname.equalsIgnoreCase(keyname)) {
					isfindkey = true;
					keyvalue = fieldvalue;
				} else {
					if (fields[i].getType() == Boolean.class) {
						if (((Boolean) fieldvalue).booleanValue())
							fieldvalue = Integer.valueOf(1);
						else
							fieldvalue = Integer.valueOf(0);
					}
					updatesql.append(fieldname + "=?,");
					updateobjs.add(fieldvalue);
				}

			} catch (Exception e) {
				throw new JException(e);
			}
		}
		if (!isfindkey)
			throw new JException("key:[" + keyname + "] not found");
		if (keyvalue == null)
			throw new JException("key;[" + keyname + "] not assigned value");
		updateobjs.add(keyvalue);
		updatesql.deleteCharAt(updatesql.length() - 1);
		updatesql.append(" where " + keyname + "=?");
		ret = SQLHelper.executePreparedSQL(conn, updatesql.toString(),
				updateobjs.toArray());
		return ret;
	}

	public int update(BusinessClass businessclass, String updatefields)
			throws Exception {
		int ret = 0;
		List<String> updatefieldlist = new ArrayList<String>(StringUtil.split(
				updatefields, ','));
		String tablename = businessclass.getClass().getSimpleName();
		String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
				+ tablename + "'";
		String keyname = strSelect(sql);
		if (keyname.equals(""))
			throw new JException("table:[" + tablename + "] not key config");
		Object keyvalue = null;
		boolean isfindkey = false;
		boolean isfindupdatefield = false;
		StringBuffer updatesql = new StringBuffer();
		updatesql.append("update " + tablename + " set ");
		List<Object> updateobjs = new ArrayList<Object>();
		Field[] fields = businessclass.getClass().getDeclaredFields();
		Method th = null;
		for (int i = 0; i < fields.length; i++) {
			try {
				String fieldname = fields[i].getName();
				String getmethodname = "get"
						+ fieldname.substring(0, 1).toUpperCase()
						+ fieldname.substring(1, fieldname.length());
				th = businessclass.getClass().getMethod(getmethodname);
				Object fieldvalue = th.invoke(businessclass);

				if (fieldname.equalsIgnoreCase(keyname)) {
					isfindkey = true;
					keyvalue = fieldvalue;
				}

				if (updatefieldlist.indexOf(fieldname) >= 0) {
					isfindupdatefield = true;
					updateobjs.add(fieldvalue);
					updatesql.append(fieldname + "=?,");
				}

			} catch (Exception e) {
				throw new JException(e);
			}
		}
		if (!isfindkey)
			throw new JException("key:[" + keyname + "] not found");
		if (keyvalue == null)
			throw new JException("key;[" + keyname + "] not assigned value");
		if (!isfindupdatefield)
			throw new JException("object " + tablename
					+ " not found update fields");
		updateobjs.add(keyvalue);
		updatesql.deleteCharAt(updatesql.length() - 1);
		updatesql.append(" where " + keyname + "=?");

		ret = SQLHelper.executePreparedSQL(conn, updatesql.toString(),
				updateobjs.toArray());

		return ret;
	}
	
	public int update(BusinessClass businessclass,String updatefields,String keyfield) throws Exception
	{
		int ret = 0;
		List<String> updatefieldlist = new ArrayList<String>(StringUtil.split(
				updatefields, ','));
		String tablename = businessclass.getClass().getSimpleName();
		if (keyfield.equals(""))
			throw new JException("key field is empty!");
		Object keyvalue = null;
		boolean isfindkey = false;
		boolean isfindupdatefield = false;
		StringBuffer updatesql = new StringBuffer();
		updatesql.append("update " + tablename + " set ");
		List<Object> updateobjs = new ArrayList<Object>();
		Field[] fields = businessclass.getClass().getDeclaredFields();
		Method th = null;
		for (int i = 0; i < fields.length; i++) {
			try {
				String fieldname = fields[i].getName();
				String getmethodname = "get"
						+ fieldname.substring(0, 1).toUpperCase()
						+ fieldname.substring(1, fieldname.length());
				th = businessclass.getClass().getMethod(getmethodname);
				Object fieldvalue = th.invoke(businessclass);

				if (fieldname.equalsIgnoreCase(keyfield)) {
					isfindkey = true;
					keyvalue = fieldvalue;
				}

				if (updatefieldlist.indexOf(fieldname) >= 0) {
					isfindupdatefield = true;
					updateobjs.add(fieldvalue);
					updatesql.append(fieldname + "=?,");
				}

			} catch (Exception e) {
				throw new JException(e);
			}
		}
		if (!isfindkey)
			throw new JException("key:[" + keyfield + "] not found");
		if (keyvalue == null)
			throw new JException("key;[" + keyfield + "] not assigned value");
		if (!isfindupdatefield)
			throw new JException("object " + tablename
					+ " not found update fields");
		updateobjs.add(keyvalue);
		updatesql.deleteCharAt(updatesql.length() - 1);
		updatesql.append(" where " + keyfield + "=?");

		ret = SQLHelper.executePreparedSQL(conn, updatesql.toString(),
				updateobjs.toArray());

		return ret;
	}

	public int update(String tablename, String updatefields,
			String fieldvalues, String wheresql) throws Exception {
		int ret = 0;

		if (wheresql.trim().equals(""))
			throw new JException("where sql is empty!");

		List updatefieldlist = new ArrayList(StringUtil
				.split(updatefields, ','));
		List fieldvaluelist = new ArrayList(StringUtil.split(fieldvalues, ','));
		if (updatefieldlist.size() != fieldvaluelist.size())
			throw new JException("update fields count not equal field values!");

		Statement stmt = null;
		ResultSet rs = null;
		boolean isfindupdatefield = false;
		StringBuffer updatesql = new StringBuffer();
		updatesql.append("update " + tablename + " set ");
		String sql = "select * from " + tablename + " with(nolock)";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				// 未根据不同列的数据类型使用不同的取值方法
				String colname = rsmd.getColumnName(i + 1);
				if (updatefieldlist.indexOf(colname) >= 0) {
					isfindupdatefield = true;
					updatesql.append(colname
							+ "='"
							+ fieldvaluelist.get(
									updatefieldlist.indexOf(colname))
									.toString() + "',");
				}

			}
		} catch (Exception sqle) {
			throw new JSQLException(sql, sqle);
		} finally {
			SQLHelper.close(null, stmt, rs);
		}
		if (!isfindupdatefield)
			throw new JException("object " + tablename
					+ " not found update fields");
		updatesql.deleteCharAt(updatesql.length() - 1);
		updatesql.append(" " + wheresql);
		ret = execute(updatesql.toString());
		return ret;
	}

	public void executePreparedSQL(String sql, Object[] objs)
	throws JSQLException {
		try {
			SQLHelper.executePreparedSQL(conn, sql, objs);
		} catch (SQLException e) {
			throw new JSQLException(sql, e);
		}
	}

	public void executeBatch(Collection collection)
	throws JSQLException {
		try {
			SQLHelper.executeBatch(conn, collection);
		} catch (SQLException e) {
			throw new JSQLException(e);
		}
	}

	public int[] copyTo(DataCentre targetdc, Properties sqls) throws Exception {
		long l = System.currentTimeMillis();

		int[] total = BatchCopy.execute(this.conn,targetdc.getConnection(),sqls);
		
		l = System.currentTimeMillis()-l;
		
		//Log.info("转换数据成功");
		
		Object[] tns = sqls.keySet().toArray();
		
		for(int i=0;i<total.length;i++){
			Log.info("  " + tns[i].toString() + ": " + total[i]);
		}
		//Log.info("花费时间(ms): " + l);
		
		return total;
	}
	
	public  Vector getSQLMeta(String sql)  throws Exception
	{
		return SQLHelper.getSQLMeta(this.conn, sql);
	}
	  
	public Vector getTableMeta(String tablename)  throws Exception
	{
		return SQLHelper.getTableMeta(this.conn, tablename);
	}

	@Override
	public void checkConnection(String nsName) throws Exception{
		Boolean isAvailable=false;
		TinyConnection con=(TinyConnection)conn;
		if(con==null) {
			setConnection(PoolHelper.getInstance().getConnection(nsName));
			return;
		}
		try {
			conn.setAutoCommit(false);
			conn.rollback();
			conn.setAutoCommit(true);
			if (!conn.isClosed()) {
				isAvailable=true;
			}
		}catch(Exception ex){
			isAvailable=false;
			try {
				con.getPool().releaseOne(conn);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.error("关闭无用连接失败", e.getMessage());
					}
				}
		
		//Log.info("连接可用性: "+isAvailable);
		if(!isAvailable) {
			try {
				con.getPool().releaseOne(conn);
				setConnection(PoolHelper.getInstance().getConnection(nsName));
			} catch (SQLException e) {
				//e.printStackTrace();
				Log.error("从"+nsName+"取连接取数据库连接出错",e.getMessage());
				try {
					throw new Exception("从"+nsName+"取连接取数据库连接出错");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		con=null;
	}

	@Override
	public void setConnection(Connection con) throws Exception{
		conn=con;
		conn.setAutoCommit(true);
	}

}
