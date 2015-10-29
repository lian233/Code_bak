/*
 * ����������
 * */
package com.wofu.intf.chinapay;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class DtcTools {

	public static SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//���ɱ���ͷ
	public static String createHead(String messageType , String actionType) throws Exception{	
		
		StringBuffer bizData=new StringBuffer();
		//bizData.append("<DTC_Message>");
		bizData.append("<MessageHead>");
		bizData.append(CreateItem("MessageType" , messageType , null));
		bizData.append(CreateItem("MessageId" , CreateMessageID(messageType) , null));
		bizData.append(CreateItem("ActionType" , actionType , null));
		
		Calendar cd = Calendar.getInstance();
		bizData.append(CreateItem("MessageTime" , datetimeformat.format(cd.getTime()) , null));
		bizData.append(CreateItem("SenderId" , Params.SenderId , null));
		bizData.append(CreateItem("ReceiverId" , Params.ReceiverId , null));
		bizData.append(CreateItem("UserNo" , Params.UserNo , null));
		bizData.append(CreateItem("Password" , MD5Util.getMD5Code(Params.Password.getBytes()) , null));
		
		bizData.append("</MessageHead>");
		//bizData.append("<MessageBody>");
		
		return bizData.toString();
		
	}
	
	//���ɱ�����
	public static void createBody(StringBuilder bizData)throws Exception {					
		//StringBuffer bizData=new StringBuffer();
		bizData.insert(0, "<DTCFlow>");
		bizData.insert(0, "<MessageBody>");
		//bizData.append(body);
		bizData.append("</DTCFlow>");
		bizData.append("</MessageBody>");
		
		return ;
		
	}
	
	//��ͷβ
	public static void AddHeadRear(StringBuilder bizData) throws Exception{					
		//StringBuffer bizData=new StringBuffer();
		bizData.insert(0, "<DTC_Message>");
		bizData.insert(0,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bizData.append("</DTC_Message>");
		
		return ;
		
	}
	
	//ȡ�ýӿ�����
	public static Vector getInfDownNote(Connection conn,String sheettype) throws Exception
	{		
		String sql="select SerialID, OperData , OperType, Owner  from Inf_DownNote where Flag = 0 and SheetType= "+sheettype;					
		Vector  sheetlist=SQLHelper.multiRowSelect(conn, sql);
		return sheetlist;
	}
	
	//ȡ�õ�λ
	public static String GetUnitCode(Connection conn, String name)  throws Exception
	{
		String sql="select * from HSUnitCode where Name = '" + name +"'";
		Hashtable dt=SQLHelper.oneRowSelect(conn, sql);
		if (dt.size() <= 0){
			return "011";
		}
		else{
			return dt.get("Code").toString() ;
		}
				
		
	}
	
	
	//
	
	//�����ݼ���ȡ�����ݣ�������ϸ����
	//Data ���ݼ������Ϊ�գ���ֻ��������
	//Filed ���ݼ����ֶ�
	//Item  �����ɵ����ݣ���Filed��Ӧ
	public static String CreateItem(String item  , String field  , Hashtable dt ){	
		if (dt == null){
			return  "<"+item+">" + field + "</"+item+">";
		}
		else{//ȡ���ݼ�����
			//return CreateItem(data , dt.get(field).toString() , null);
			return  "<"+item+">" + dt.get(field).toString().trim() + "</"+item+">";
		}
			
	}
	
	//������ϢID
	public static String CreateMessageID(String sheetType){
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat t =new SimpleDateFormat("yyyyMMddHHmmssS"); 
		return sheetType + t.format(cd.getTime()); 
	}
	
	//���ݽӿڱ�
	public static void backUpInf(Connection conn,int SerialID,String result) throws Exception{
		String sql ="update Inf_DownNote set HandleTime = getdate() ,Flag=100 , result = '"+result+"'  where SerialID = " +  SerialID;

		//Log.error("|||1||sql 1:", sql + "|||||" );
		SQLHelper.executeSQL(conn, sql);
		sql = "insert into inf_DownNotebak select * from Inf_DownNote where SerialID="+SerialID;
		//Log.error("||2|||sql 2:", sql + "|||||" );
		SQLHelper.executeSQL(conn, sql);
		sql = "delete Inf_DownNote where SerialID="+SerialID;
		//Log.error("||3|||sql 3:", sql + "|||||" );
		SQLHelper.executeSQL(conn, sql);
		
	}
	
	//ȡ�ó��ⵥ�ܽ��
	public static String GetOrderTotalAmount(Connection conn , String sheetID) throws Exception {
		String sql="select convert(dec(12,2),sum(Price*NotifyQty)) Amount from OutStockNoteItem where SheetID = '" + sheetID +"'";
		Hashtable dt=SQLHelper.oneRowSelect(conn, sql);
		if (dt.size() <= 0){
			return "0";
		}
		else{
			return dt.get("Amount").toString() ;
		}
		
	}
	
	//ȡ�õ�����ľ���
	public static String GetWeigh(Connection conn , String sheetID  , int outType ) throws Exception {
		
		String t = "OutStockNoteItem";
		if (outType == 0){
			t = "OutStockItem0";
		}
		else if (outType == 1){
			t = "OutStockItem";
		}
		
		String sql="select convert(dec(12,3),isnull(sum(isnull(Weigh,0)*NotifyQty)/1000.000,0)) Weigh from " + t 
			+ " a , Barcode b where a.BarcodeID = b.BarcodeID and a.MID = b.MID and a.SheetID= '"+sheetID+"' ";
		Hashtable dt=SQLHelper.oneRowSelect(conn, sql);
		if (dt.size() <= 0){
			return "0";
		}
		else{
			return dt.get("Weigh").toString()  ;
		}
		
	}
	
	
	//����˰��
	public static String CalTax(Connection conn , String postTaxNo , String amount) throws Exception {
		String sql = "select " + amount + " * TaxRate/100.00 Tax from PostTariff where Code='"+postTaxNo+"'";
		
		Hashtable dt=SQLHelper.oneRowSelect(conn, sql);
		if (dt.size() <= 0){
			//return "0";
			throw new Exception("�Ҳ�������˰�š�"+postTaxNo+"��");
		}
		else{
			return dt.get("Tax").toString()  ;
		}
		
	}
	
	
	
}
