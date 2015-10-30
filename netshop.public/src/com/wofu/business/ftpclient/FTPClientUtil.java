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
 * ftp������
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
				throw new Exception("ftp�����쳣 ,connect refuse!");
			}
			if(!client.login(username, password)){
				throw new Exception("ftp�û������������");
			}
			//client.enterLocalActiveMode();//����ftp
			client.enterLocalPassiveMode();//����ftp
			System.out.println("��¼ftp�ɹ�");
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
	//�ر�ftp����
	public void disConnection() throws Exception{
		try{
			if(client.isConnected()) 
				client.disconnect();
			System.out.println("�˳�ftp�ɹ�");
		}catch(Exception e){
			Log.error("�ر�ftp����ʧ��", e.getMessage());
			throw e;
		}
		
	}
	//��ftp������ļ����г���
	public void listFileNames(String url,int port ,String username,String password,String encoding,String path) throws Exception{
		connectToServer(url,port,username,password,encoding);
		if(path!=null)
		client.changeWorkingDirectory(path);
		listFileName();
		disConnection();
		
	}
	
	private void listFileName() throws Exception{
		FTPFile[] files = client.listFiles();
		System.out.println("�ļ�Ŀ¼����Ϊ: "+files.length);
		for(FTPFile e:files){
			if(e.isDirectory()){//
				Log.info("��Ŀ¼: "+e.getName());
				if(".".equals(e.getName()) || "..".equals(e.getName())) continue;//��ǰĿ¼
				client.changeWorkingDirectory(e.getName());
				listFileName();
				client.changeToParentDirectory();
			}else
				Log.info("�ļ�: "+e.getName());
		}
	}
	/**
	 * ����ָ��Ŀ¼�µ�ָ���ļ�  
	 * @param path   Ŀ¼������Ϊ���ǣ�Ĭ���Ǹ�Ŀ¼
	 * @param fileName  �����ļ���  ����Ϊ��ʱ������Ŀ¼����������ļ�
	 * @param isEnctypt          �Ƿ�Ҫ�����ļ���
	 * @param charEncoding       ����ʱ�ı���
	 * @param keyWord            ������Կ
	 * @param isDelete            �Ƿ�ɾ�����ع����ļ�
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
	 * ���ص�ǰ����Ŀ¼�µ�һ���ļ�
	 * @param fileName  ���ص��ļ���
	 * @param fileName  ���ص��ļ���
	 * @throws Exception
	 */
	public void downLoadFile(String fileName,boolean isEnctypt,String charEncoding,String keyWord,boolean isDelete)throws Exception{
		InputStream is = client.retrieveFileStream(fileName);
		if(isEnctypt)
			//����������
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
		Log.info("�����ļ��ɹ�: "+fileName);
		
	}
	
	private void copy(InputStream is ,OutputStream os) throws Exception{
		byte[] bytes = new byte[1024];
		int len=0;
		while((len=is.read(bytes))!=-1){
			os.write(bytes,0,len);
		}
	}
	/**
	 * �ϴ��ļ���ftp��ָ��Ŀ¼
	 * @param url
	 * @param port
	 * @param username
	 * @param password
	 * @param encoding
	 * @param path               �ϴ�Ŀ¼
	 * @param fileName           Ҫ�ϴ����ļ�
	 * @param isEnctypt          �Ƿ�Ҫ�����ļ���
	 * @param charEncoding       ����ʱ�ı���
	 * @param keyWord            ������Կ
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
			File file = new File("./");//�ϴ���ǰĿ¼�µ������ļ�
			File[] files = file.listFiles();
			for(File f:files){
				if(f.isFile())
					upLoadFile(f.getName(),isEnctypt,charEncoding,keyWord);
			}
			}
		disConnection();
		}
	
	//�ϴ�ָ���ļ���ָ��Ŀ¼
	private void upLoadFile(String fileName,boolean isEnctypt,String encoding,String keyWord) throws Exception{
		OutputStream os = client.storeFileStream(fileName);
		InputStream is = new FileInputStream(fileName);
		if(isEnctypt)//�����ļ���
			is = AesUtil.encrypt(is, encoding, keyWord);
		try{
			copy(is,os);
		}finally{
			if(os!=null) os.close();
			if(is!=null) is.close();
			client.completePendingCommand();
		}
		Log.info("�ϴ��ļ��ɹ�: "+fileName);
		
		
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
