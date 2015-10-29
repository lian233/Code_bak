/*
 * ����������
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
	

	
//	��ʮ������ת��Ϊ�ֽ������ַ��������������Base64���봦��
//    public static String GetImageBase64(String img) throws Exception
//    {  	
//        byte[] data = null;
//        //����ʮ������ת���ֽ�����Ķ���
//        System.out.println("16����ת2����");
//        String  imgBate = hexString2binaryString(img);
//        System.setOut(new PrintStream("d:/2jinzhi.txt")); 
//        System.out.println(imgBate);
//        System.exit(0);
//        return  imgBate;//����Base64��������ֽ������ַ���
//    }
    
    
	//��ʮ������ת��Ϊ�ֽ������ַ��������������Base64���봦��
//    public static String GetImageBase64(String img) throws Exception
//    {  	
//        byte[] data = null;
//        //����ʮ������ת���ֽ�����Ķ���
//        StringBuilder str = new StringBuilder();
//        data = HexStringToBinary(img);
//		for (byte bs : data)
//		{
//			str.append(Integer.toBinaryString(bs));// ת��Ϊ������
//		}
//        System.setOut(new PrintStream("d:/16byte.txt")); 
//        System.out.println(str.toString());
//		File apple = new File("D:/tupian.bmp");// ���ֽ������ͼƬд����һ���ط�
//		FileOutputStream fos = new FileOutputStream(apple);
//		fos.write(data);
//		fos.flush();
//		fos.close();
//        System.exit(0);
//        return  str.toString();//����Base64��������ֽ������ַ���
//    }
    
	
	public static String GetImageBase64(String img) throws Exception{
		String path = "D:/test.jpg";
		System.out.println("����");
		File file = new File(path);
		FileInputStream fis;
			fis = new FileInputStream(file);
			byte[] b;
			b = new byte[fis.available()];
			StringBuilder str = new StringBuilder();// ��������String
			fis.read(b);
			for (byte bs : b)
			{
				str.append(Integer.toBinaryString(bs));// ת��Ϊ������
			}
		return str.toString();
	}
	
	
    //16����ת2����
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
		//hexString�ĳ��ȶ�2ȡ������Ϊbytes�ĳ���
		int len = hexString.length()/2;
		byte[] bytes = new byte[len];
		byte high = 0;//�ֽڸ���λ
		byte low = 0;//�ֽڵ���λ

		for(int i=0;i<len;i++){
			 //������λ�õ���λ
			 high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);
			 low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));
			 bytes[i] = (byte) (high|low);//�ߵ�λ��������
		}
		return bytes;
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
		String sql="select SerialID, OperData , OperType, Owner  from Inf_DownNote where Flag = 0 and SheetType= "+sheettype; //ע��Ϊ�˲���
//		String sql="select top 10 SerialID, OperData , OperType, Owner  from Inf_DownNotebak where  SheetType= "+sheettype;
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
//	public static String CreateItem(String item  , String field  , Hashtable dt ) throws Exception{	
//		if (dt == null){
//			return  "<"+item+"><![CDATA[" + field + "]]></"+item+">";
//		}
//		else{//ȡ���ݼ�����
//			//return CreateItem(data , dt.get(field).toString() , null);
//			return  "<"+item+"><![CDATA[" + dt.get(field).toString().trim() + "]]></"+item+">";
//		}
//			
//	}
	
	public static String CreateItem(String item  , String field  , Hashtable dt ) throws Exception{	
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
			if((int)e<=0x1f){//���ɼ��ַ�
			}else if((int)e==0xdde2){//���ɼ��ַ�
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