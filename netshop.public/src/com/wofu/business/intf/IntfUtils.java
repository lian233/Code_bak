package com.wofu.business.intf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class IntfUtils {
	
	public static void backupUpNote(Connection conn,String owner,String sheetid,String sheettype) 
		throws Exception
	{
		String sql="insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
			+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
			+ " where owner='"+owner+"' and SheetID = '" + sheetid+ "' and SheetType = "+sheettype;
		SQLHelper.executeSQL(conn, sql);
	
		sql="delete from IT_UpNote where owner='"+owner+"' and SheetID='"+sheetid+"' and sheettype="+sheettype;
		SQLHelper.executeSQL(conn, sql);
		
	}
	
	public static void backupDownNote(Connection conn,String owner,String sheetid,String sheettype)
		throws Exception
	{
		long starttime = System.currentTimeMillis();
		String sql="insert into IT_downNoteBak "
			+ " select Owner,sheetid, sheettype, sender, receiver, notetime, Getdate() "
			+" from IT_downNote  "
			+ " where owner='"+owner+"' and SheetID = '" + sheetid+ "' and SheetType = "+sheettype;
		SQLHelper.executeSQL(conn, sql);
	
		sql="delete from IT_downNote where owner='"+owner+"' and SheetID='"+sheetid+"' and sheettype="+sheettype;
		SQLHelper.executeSQL(conn, sql);
		Log.info("备份it_downnote数据所需要的时间为: "+(System.currentTimeMillis()-starttime));
		
	}
	
	
	public static Vector getUpNotes(Connection conn,String tradecontactid,String sheettype) 
		throws JSQLException
	{
		String sql="select sheetid  from it_upnote "
				+"where receiver='"+tradecontactid+"' and sheettype="+sheettype;
		Vector vts=SQLHelper.multiRowSelect(conn, sql);
		return vts;
	}
	
	public static Vector getDownNotes(Connection conn,String tradecontactid,String sheettype) 
		throws JSQLException
	{
		String sql="select sheetid from it_downnote where sender='"+tradecontactid+"' and sheettype="+sheettype;
		Vector vts=SQLHelper.multiRowSelect(conn, sql);
		return vts;
	}
	
	public static Vector getGlobalDownNotes(Connection conn,String sheettype,String notingetOrder) 
	throws JSQLException
{
	StringBuilder sql;
	String[] temp = notingetOrder.split(",");
	if(temp.length==0)
		sql =new StringBuilder("select sheetid from it_downnote where sheettype="+sheettype+"  order by notetime");
	else{
		sql=new StringBuilder("select sheetid from it_downnote where sheettype="+sheettype+" and sender not in(");
		for(String e:temp){
			sql.append("'").append(e).append("',");
		}
		sql.deleteCharAt(sql.length()-1).append(")");
		Log.info("sql: "+sql.toString());
	}
	
	
	Vector vts=SQLHelper.multiRowSelect(conn, sql.toString());
	return vts;
}
	
	public static void backupIntfSheetList(Connection conn,String sheetid,String interfacesystem,String sheettype) throws Exception
	{
		String sql="update it_infsheetlist0 set executeflag=1,stime=getdate() "
					+"where sheetid='"+sheetid+"' and interfacesystem='"+interfacesystem+"' "
					+"and sheettype="+sheettype;
		SQLHelper.executeSQL(conn, sql);	
		
		sql="insert into it_infsheetlist(SheetID,SheetType,InterfaceSystem,ExecuteFlag,STime) "
			+"select SheetID,SheetType,InterfaceSystem,ExecuteFlag,stime from it_infsheetlist0 "
			+"where sheetid='"+sheetid+"' and interfacesystem='"+interfacesystem+"' "
			+"and sheettype="+sheettype+" and executeflag=1";
		SQLHelper.executeSQL(conn, sql);
		
		sql="delete from it_infsheetlist0 "
			+"where sheetid='"+sheetid+"' and interfacesystem='"+interfacesystem+"' "
			+"and sheettype="+sheettype+" and executeflag=1";
		
		SQLHelper.executeSQL(conn, sql);
	}
	
	public static void upNote(Connection conn,String owner,String sheetid,int sheettype,
			String from,String receiver) throws Exception
	{
		String sql="insert into it_upnote(owner,sheetid,sheettype,sender,receiver,notetime,flag) "
				   +"values('"+owner+"','"+sheetid+"',"+sheettype+",'"+from+"','"+receiver+"',getdate(),0)";
		SQLHelper.executeSQL(conn, sql);
	}
	
	/*
	 * 接口订单
	 */
	public static List getintfsheetlist(Connection conn,String interfacesystem,String sheettype) throws Exception
	{		
		String sql="select sheetid from it_infsheetlist0  "
			+"where sheettype="+sheettype+" and interfacesystem='"+interfacesystem+"' order by stime";
		List sheetlist=SQLHelper.multiRowListSelect(conn, sql);
		return sheetlist;
	}
	
	//备份Inf_DownNote表数据
	public static void backupInfSheetList(Connection conn,int serialID) throws Exception{
		String sql =new StringBuilder("insert into Inf_DownNoteBak(SerialID,SheetType,")
			.append("NoteTime,HandleTime,OperType,OperData,Flag,Owner,result) select SerialID,sheettype,")
			.append("notetime,getdate(),")
			.append("opertype,operdata,100,owner,'success' from Inf_DownNote where serialid=")
			.append(serialID).toString();
		SQLHelper.executeSQL(conn,sql);
		sql = "delete Inf_DownNote where serialid="+serialID;
		SQLHelper.executeSQL(conn,sql);
	}
}
