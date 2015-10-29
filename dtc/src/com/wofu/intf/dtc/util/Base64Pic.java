package com.wofu.intf.dtc.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import com.wofu.common.tools.sql.SQLHelper;

public class Base64Pic {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		InputStream is = new FileInputStream("test.png");
		byte[] result = toByteArray(is);
		//base64编码
		String temp = Base64Code.encodeLines(result);
		System.out.println(temp);
		byte[] pic = Base64Code.decodeLines(temp);
		OutputStream fos =new FileOutputStream("test1.png");
		fos.write(pic);
		fos.flush();
		fos.close();

	}
	//流转字节数组
	private static byte[] toByteArray(InputStream is) throws Exception{
		byte[] temp = new byte[1024];
		int len=0;
		StringBuilder sb = new StringBuilder();
		while((len=is.read(temp))!=-1){
			sb.append(new String(temp,0,len,"iso-8859-1"));
		}
		return sb.toString().getBytes("iso-8859-1");
	}
	
	//
	
	//二进制流转字符串
	private static String streamToString(InputStream is) throws Exception{
		byte[] result = toByteArray(is);
		String temp = Base64Code.encodeLines(result);
		return temp;
		
	}
	
	public static void readFromDbToString()throws Exception{
		Connection conn=null;
		String sql ="select pic from barcodePic";
		try{
			conn = null;//getConnection();
			List lists = SQLHelper.multiRowListSelect(conn, sql);
			FileOutputStream fos = null;
			for(Iterator it = lists.iterator();it.hasNext();){
				ByteArrayInputStream bis = (ByteArrayInputStream)it.next();
				String temp = Base64Code.encodeLines(toByteArray(bis));
				System.out.println("图片base64数据: "+temp);
			}
			System.out.println("转换文件到字符串成功");
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
		
	}
	
	
	

	}
