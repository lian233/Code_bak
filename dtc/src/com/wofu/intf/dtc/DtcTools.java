/*
 * 公共函数类
 * */
package com.wofu.intf.dtc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import sun.misc.BASE64Encoder;

import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.intf.dtc.util.Base64Code;

public class DtcTools {
	private static String hexStr =  "0123456789ABCDEF";
	public static SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//生成报文头
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
	

	
//	将十六进制转化为字节数组字符串，并对其进行Base64编码处理
//    public static String GetImageBase64(String img) throws Exception
//    {  	
//        byte[] data = null;
//        //进行十六进制转化字节数组的动作
//        System.out.println("16进制转2进制");
//        String  imgBate = hexString2binaryString(img);
//        System.setOut(new PrintStream("d:/2jinzhi.txt")); 
//        System.out.println(imgBate);
//        System.exit(0);
//        return  imgBate;//返回Base64编码过的字节数组字符串
//    }
    
    
	//将十六进制转化为字节数组字符串，并对其进行Base64编码处理
//    public static String GetImageBase64(String img) throws Exception
//    {  	
//        byte[] data = null;
//        //进行十六进制转化字节数组的动作
//        StringBuilder str = new StringBuilder();
//        data = HexStringToBinary(img);
//		for (byte bs : data)
//		{
//			str.append(Integer.toBinaryString(bs));// 转换为二进制
//		}
//        System.setOut(new PrintStream("d:/16byte.txt")); 
//        System.out.println(str.toString());
//		File apple = new File("D:/tupian.bmp");// 把字节数组的图片写到另一个地方
//		FileOutputStream fos = new FileOutputStream(apple);
//		fos.write(data);
//		fos.flush();
//		fos.close();
//        System.exit(0);
//        return  str.toString();//返回Base64编码过的字节数组字符串
//    }
    
	
	public static String GetImageBase64(String img) throws Exception{
		String path = "D:/test.jpg";
		System.out.println("测试");
		File file = new File(path);
		FileInputStream fis;
			fis = new FileInputStream(file);
			byte[] b;
			b = new byte[fis.available()];
			StringBuilder str = new StringBuilder();// 不建议用String
			fis.read(b);
			for (byte bs : b)
			{
				str.append(Integer.toBinaryString(bs));// 转换为二进制
			}
		return str.toString();
	}
	
	
    //16进制转2进制
	public static String hexString2binaryString(String hexString)
	{
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		StringBuilder bString = new StringBuilder();
		String tmp="";
		
		for (int i = 0; i < hexString.length(); i++)
		{
			System.out.println(hexString.length()-i);
			tmp = "0000"+ Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			bString = bString.append(tmp.substring(tmp.length() - 4));
		}
		return bString.toString();
	}
	
	public static byte[] HexStringToBinary(String hexString){
		//hexString的长度对2取整，作为bytes的长度
		int len = hexString.length()/2;
		byte[] bytes = new byte[len];
		byte high = 0;//字节高四位
		byte low = 0;//字节低四位

		for(int i=0;i<len;i++){
			 //右移四位得到高位
			 high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);
			 low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));
			 bytes[i] = (byte) (high|low);//高地位做或运算
		}
		return bytes;
	}
    
	//生成报文体
	public static void createBody(StringBuilder bizData)throws Exception {					
		//StringBuffer bizData=new StringBuffer();
		bizData.insert(0, "<DTCFlow>");
		bizData.insert(0, "<MessageBody>");
		//bizData.append(body);
		bizData.append("</DTCFlow>");
		bizData.append("</MessageBody>");
		
		return ;
		
	}
	
	//加头尾
	public static void AddHeadRear(StringBuilder bizData) throws Exception{					
		//StringBuffer bizData=new StringBuffer();
		bizData.insert(0, "<DTC_Message>");
		bizData.insert(0,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bizData.append("</DTC_Message>");
		
		return ;
		
	}
	
	//取得接口数据
	public static Vector getInfDownNote(Connection conn,String sheettype) throws Exception
	{		
		String sql="select SerialID, OperData , OperType, Owner  from Inf_DownNote where Flag = 0 and SheetType= "+sheettype; //注销为了测试
//		String sql="select top 10 SerialID, OperData , OperType, Owner  from Inf_DownNotebak where  SheetType= "+sheettype;
		Vector  sheetlist=SQLHelper.multiRowSelect(conn, sql);
		return sheetlist;
	}
	
	//取得单位
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
	
	//从数据集中取出数据，生成明细数据
	//Data 数据集，如果为空，则只生成数据
	//Filed 数据集的字段
	//Item  需生成的数据，与Filed对应
//	public static String CreateItem(String item  , String field  , Hashtable dt ) throws Exception{	
//		if (dt == null){
//			return  "<"+item+"><![CDATA[" + field + "]]></"+item+">";
//		}
//		else{//取数据集数据
//			//return CreateItem(data , dt.get(field).toString() , null);
//			return  "<"+item+"><![CDATA[" + dt.get(field).toString().trim() + "]]></"+item+">";
//		}
//			
//	}
	
	public static String CreateItem(String item  , String field  , Hashtable dt ) throws Exception{	
		if (dt == null){
			return  "<"+item+">" + field + "</"+item+">";
		}
		else{//取数据集数据
			//return CreateItem(data , dt.get(field).toString() , null);
			return  "<"+item+">" + dt.get(field).toString().trim() + "</"+item+">";
		}
			
	}
	
	//生成消息ID
	public static String CreateMessageID(String sheetType){
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat t =new SimpleDateFormat("yyyyMMddHHmmssS"); 
		return sheetType + t.format(cd.getTime()); 
	}
	
	//备份接口表
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
	
	//取得出库单总金额
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
	
	//取得单据里的净重
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
	
	
	//计算税费
	public static String CalTax(Connection conn , String postTaxNo , String amount) throws Exception {
		String sql = "select " + amount + " * TaxRate/100.00 Tax from PostTariff where Code='"+postTaxNo+"'";
		
		Hashtable dt=SQLHelper.oneRowSelect(conn, sql);
		if (dt.size() <= 0){
			//return "0";
			throw new Exception("找不到行邮税号【"+postTaxNo+"】");
		}
		else{
			return dt.get("Tax").toString()  ;
		}
		
	}
	
	public static String getCompnayName(String companycode)
	{
		String companyName="";
		
	
		String com[] = Params.company.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			if(s[0].equals(companycode))
			{
				companyName=s[1];
				break;
			}
		}
		
		return companyName;
		
	}
	
	public static String filterCharT(String xml) throws Exception{
		StringBuilder sb = new StringBuilder();
		char[] arr = xml.toCharArray();
		for(char e:arr){
			if((int)e<=0x1f){//不可见字符
			}else if((int)e==0xdde2){//不可见字符
				Log.info("e:"+Integer.toHexString((int)(e)));
			}else if((int)e==38)//&
				sb.append("&amp;");
			else if((int)e==0x3c)//<
				sb.append("&lt;");
			else if((int)e==0x3e)//>
				sb.append("&gt;");
			else if((int)e==0x27)//'
				sb.append("&apos;");
			else if((int)e==0x22)//"
				sb.append("&quot;");
				else{
				sb.append(e);
			}
			
		}
		return sb.toString();
	}
	
	
	
}
