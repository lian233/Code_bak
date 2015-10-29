package com.wofu.intf.test;

import com.wofu.business.ftpclient.FTPClientUtil;

public class FtpClient {

	/**
	 * @param args
	 */
	
	private static String username="HYGY";
	private static String password="34qYw67V";
	private static int port=2312;
	private static String url="210.21.48.7";
	private static String keyWord="MYgGnQE2+DAS973vd1DFHg==";
	
	/**
	private static String username="administrator";
	private static String password="WolfNawei2013";
	private static int port=21;
	private static String url="120.26.193.249";
	private static String keyWord="MYgGnQE2+DAS973vd1DFHg==";
	**/
	public static void main(String[] args) throws Exception{
		//new FTPClientUtil().listFileNames(url, port, username, password, "utf-8", null);
		//new FTPClientUtil().downLoadFiles(url, port, username, password, "utf-8", null, null);//listFileNames(url, port, username, password, "utf-8", null);
		//new FTPClientUtil().upLoadFiles(url, port, username, password, "utf-8", "UPLOAD", "880020201507211532022765.xml");//(url, port, username, password, "utf-8", null, null);//listFileNames(url, port, username, password, "utf-8", null);
		//new FTPClientUtil().upLoadFiles(url, port, username, password, "utf-8", "UPLOAD", "880020201507211719322781.xml", true, "utf-8", keyWord);
		//new FTPClientUtil().upLoadFiles(url, port, username, password, "utf-8", "UPLOAD", "880020201507221114066984.xml", true, "utf-8", keyWord);
		new FTPClientUtil().downLoadFiles(url, port, username, password, "utf-8", "DOWNLOAD", null, true, "utf-8", keyWord,true);
	}

}
