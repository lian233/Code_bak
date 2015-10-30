package com.wofu.business.ftpclient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.wofu.common.tools.conv.AesUtil;
import com.wofu.common.tools.util.log.Log;
/**
 * ftp工具类
 * @author bolinli
 *
 */
public class FTPClientUtil {
	private FTPClient client=null;
	public void connectToServer(String url,int port,String username,String password,String encoding)throws Exception{
		client  = new FTPClient();
		client.setControlEncoding(encoding);
		try{
			client.connect(url, port);
			int replayCode = client.getReplyCode();
			if(!FTPReply.isPositiveCompletion(replayCode)){
				throw new Exception("ftp连接异常 ,connect refuse!");
			}
			if(!client.login(username, password)){
				throw new Exception("ftp用户名或密码错误");
			}
			//client.enterLocalActiveMode();//主动ftp
			client.enterLocalPassiveMode();//被动ftp
			System.out.println("登录ftp成功");
		}catch(Exception e){
			if(client.isConnected())
				try {
					client.disconnect();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			throw e;
		}
		
		
	}
	//关闭ftp连接
	public void disConnection() throws Exception{
		try{
			if(client.isConnected()) 
				client.disconnect();
			System.out.println("退出ftp成功");
		}catch(Exception e){
			Log.error("关闭ftp连接失败", e.getMessage());
			throw e;
		}
		
	}
	//把ftp上面的文件都列出来
	public void listFileNames(String url,int port ,String username,String password,String encoding,String path) throws Exception{
		connectToServer(url,port,username,password,encoding);
		if(path!=null)
		client.changeWorkingDirectory(path);
		listFileName();
		disConnection();
		
	}
	
	private void listFileName() throws Exception{
		FTPFile[] files = client.listFiles();
		System.out.println("文件目录总数为: "+files.length);
		for(FTPFile e:files){
			if(e.isDirectory()){//
				Log.info("子目录: "+e.getName());
				if(".".equals(e.getName()) || "..".equals(e.getName())) continue;//当前目录
				client.changeWorkingDirectory(e.getName());
				listFileName();
				client.changeToParentDirectory();
			}else
				Log.info("文件: "+e.getName());
		}
	}
	/**
	 * 下载指定目录下的指定文件  
	 * @param path   目录，当它为空是，默认是根目录
	 * @param fileName  下载文件名  当它为空时，下载目录下面的所有文件
	 * @param isEnctypt          是否要加密文件流
	 * @param charEncoding       加密时的编码
	 * @param keyWord            加密密钥
	 * @param isDelete            是否删除下载过的文件
	 * @throws Exception
	 */
	public void downLoadFiles(String url,int port ,String username,String password,String encoding,
			String path,String fileName,boolean isEnctypt,String charEncoding,String keyWord,boolean isDelete)throws Exception{
		connectToServer(url,port,username,password,encoding);
		if(path!=null)
		client.changeWorkingDirectory(path);
		if(fileName!=null)
			downLoadFile(fileName,isEnctypt,charEncoding,keyWord,isDelete);
		else{
			FTPFile[] files = client.listFiles();
			for(FTPFile e:files){
				if(e.isFile())
					downLoadFile(e.getName(),isEnctypt,charEncoding,keyWord,isDelete);
			}
		}
		disConnection();
	}
	/**
	 * 下载当前工作目录下的一个文件
	 * @param fileName  下载的文件名
	 * @param fileName  下载的文件名
	 * @throws Exception
	 */
	public void downLoadFile(String fileName,boolean isEnctypt,String charEncoding,String keyWord,boolean isDelete)throws Exception{
		InputStream is = client.retrieveFileStream(fileName);
		if(isEnctypt)
			//解密输入流
			is = AesUtil.decrypt(is,charEncoding,keyWord);
		FileOutputStream fos = new FileOutputStream(fileName);
		try{
			copy(is,fos);
		}finally{
			is.close();
			fos.close();
			client.completePendingCommand();
			if(isDelete) client.deleteFile(fileName);
		}
		Log.info("下载文件成功: "+fileName);
		
	}
	
	private void copy(InputStream is ,OutputStream os) throws Exception{
		byte[] bytes = new byte[1024];
		int len=0;
		while((len=is.read(bytes))!=-1){
			os.write(bytes,0,len);
		}
	}
	/**
	 * 上传文件到ftp的指定目录
	 * @param url
	 * @param port
	 * @param username
	 * @param password
	 * @param encoding
	 * @param path               上传目录
	 * @param fileName           要上传的文件
	 * @param isEnctypt          是否要加密文件流
	 * @param charEncoding       加密时的编码
	 * @param keyWord            加密密钥
	 * @throws Exception
	 */
	public void upLoadFiles(String url,int port ,String username,String password,String encoding,
			String path,String fileName,boolean isEnctypt,String charEncoding,String keyWord)throws Exception{
		connectToServer(url,port,username,password,encoding);
		if(path!=null)
		client.changeWorkingDirectory(path);
		if(fileName!=null)
			upLoadFile(fileName,isEnctypt,charEncoding,keyWord);
		else{
			File file = new File("./");//上传当前目录下的所有文件
			File[] files = file.listFiles();
			for(File f:files){
				if(f.isFile())
					upLoadFile(f.getName(),isEnctypt,charEncoding,keyWord);
			}
			}
		disConnection();
		}
	
	//上传指定文件到指定目录
	private void upLoadFile(String fileName,boolean isEnctypt,String encoding,String keyWord) throws Exception{
		OutputStream os = client.storeFileStream(fileName);
		InputStream is = new FileInputStream(fileName);
		if(isEnctypt)//加密文件流
			is = AesUtil.encrypt(is, encoding, keyWord);
		try{
			copy(is,os);
		}finally{
			if(os!=null) os.close();
			if(is!=null) is.close();
			client.completePendingCommand();
		}
		Log.info("上传文件成功: "+fileName);
		
		
	}
	
	public void main(String[] args) throws Exception{
		 String username="HYGY";
		String password="34qYw67V";
		 int port=2312;
		 String url="210.21.48.7";
		 String keyWord="MYgGnQE2+DAS973vd1DFHg==";
		//new FTPClientUtil().listFileNames(url, port, username, password, "utf-8", null);
		//new FTPClientUtil().downLoadFiles(url, port, username, password, "utf-8", null, null);//listFileNames(url, port, username, password, "utf-8", null);
		//new FTPClientUtil().upLoadFiles(url, port, username, password, "utf-8", "UPLOAD", "880020201507211532022765.xml");//(url, port, username, password, "utf-8", null, null);//listFileNames(url, port, username, password, "utf-8", null);
		//new FTPClientUtil().upLoadFiles(url, port, username, password, "utf-8", "UPLOAD", "880020201507211719322781.xml", true, "utf-8", keyWord);
		//new FTPClientUtil().upLoadFiles(url, port, username, password, "utf-8", "UPLOAD", "880020201507221114066984.xml", true, "utf-8", keyWord);
		new FTPClientUtil().downLoadFiles(url, port, username, password, "utf-8", "DOWNLOAD", null, true, "utf-8", keyWord,true);
	}
	
	
		

}
