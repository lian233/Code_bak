package com.wofu.ecommerce.imagespace;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.util.FileUtil;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.ImageUtil;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;

public class ImageManage extends BusinessObject {

	public void getImageSpaceDir() throws Exception {
		StringBuffer strbuf = new StringBuffer();

		String imageSpaceRootPath = this.getRequest().getServletContext()
				.getContext("/material").getRealPath("");
		
		File materialRoot = new File(imageSpaceRootPath);
		File[] dirs = materialRoot.listFiles(new DirFilter());
		if (dirs.length > 0) {
			strbuf.append("[");
			for (int i = 0; i < dirs.length; i++) {
				strbuf.append("{");
				strbuf.append("name:'" + dirs[i].getName() + "',id:'" + i
						+ "',leaf:false");
				strbuf.append(getSubDir(dirs[i].getPath()));
				strbuf.append("},");

			}
			strbuf.deleteCharAt(strbuf.length() - 1);
			strbuf.append("]");
		} else
			strbuf.append("0");
		this.OutputStr(strbuf.toString());
	}

	private String getSubDir(String parentDirStr) {
		StringBuffer strbuf = new StringBuffer();
		File parentDir = new File(parentDirStr);
		File[] dirs = parentDir.listFiles(new DirFilter());
		if (dirs.length > 0) {
			strbuf.append(",children:[");
			for (int i = 0; i < dirs.length; i++) {
				strbuf.append("{");
				strbuf.append("name:'" + dirs[i].getName() + "',id:'" + i
						+ "',leaf:false");
				strbuf.append(getSubDir(dirs[i].getPath()));
				strbuf.append("},");

			}
			strbuf.deleteCharAt(strbuf.length() - 1);
			strbuf.append("]");
		}
		return strbuf.toString();
	}

	private class DirFilter implements FileFilter {
		public boolean accept(File dir) {

			if (dir.isDirectory())
				return true;
			else
				return false;
		}
	}

	private class LastModifiedFileComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			File file1 = (File) o1;
			File file2 = (File) o2;
			long result = file1.lastModified() - file2.lastModified();
			if (result < 0) {
				return -1;
			} else if (result > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private class ImageFileNameFilter implements FilenameFilter {
		public boolean accept(File dir, String filename) {

			if ((filename.endsWith(".jpg") || filename.endsWith(".gif")
					|| filename.endsWith(".bmp") || filename.endsWith(".png") || filename
					.endsWith("jpeg"))
					&& !filename.startsWith("small-"))
				return true;
			else
				return false;
		}
	}

	private class ImageFileFilter implements FileFilter {
		public boolean accept(File imagefile) {

			if ((imagefile.getName().toLowerCase().endsWith(".jpg")
					|| imagefile.getName().toLowerCase().endsWith(".gif")
					|| imagefile.getName().toLowerCase().endsWith(".bmp")
					|| imagefile.getName().toLowerCase().endsWith(".png") || imagefile
					.getName().toLowerCase().endsWith(".jpeg"))
					&& !imagefile.getName().startsWith("small-"))
				return true;
			else
				return false;
		}
	}

	public void getDirImageFiles() throws Exception {
		String reqdata = this.getReqData();

		Properties prop = StringUtil.getIniProperties(reqdata);
		String parentdirname = prop.getProperty("parentdirname");
		

		String imageSpaceRootPath = this.getRequest().getServletContext()
				.getContext("/material").getRealPath("");

		String rootdir=this.getRequest().getRequestURL().toString().replaceAll("/TinyWebServer", "") + "/material/";
		
		String remoteDir = rootdir+ encodeDir(parentdirname) ;
		Vector vt = new Vector();

		File ImageFilePath = new File(imageSpaceRootPath + "/" + parentdirname);
		File[] files = ImageFilePath.listFiles(new ImageFileFilter());
		Arrays.sort(files, new LastModifiedFileComparator());
		for (int i = 0; i < files.length; i++) {
			String srcfilename = files[i].getName();

			Hashtable ht = new Hashtable();

			ht.put("name", srcfilename);
			ht.put("url", remoteDir + encodeURL(srcfilename));
			ht.put("smallurl", remoteDir + encodeURL("small-" + srcfilename));
			//ht.put("url", remoteDir + srcfilename);
			//ht.put("smallurl", remoteDir + "small-" + srcfilename);
			ht.put("modified", Formatter.format(new Date(files[i]
					.lastModified()), Formatter.DATE_TIME_FORMAT));

			vt.add(ht);
		}

		this.OutputStr(this.toJSONArray(vt));
	}
	
	private String encodeDir(String dir) throws Exception
	{
		StringBuffer encodedir=new StringBuffer();
		
		String[] dirs=dir.split("/");
		for (int i=0;i<dirs.length;i++)
		{
			encodedir.append(encodeURL(dirs[i]));
			encodedir.append("/");			
		}
		return encodedir.toString();
	}
	private String encodeURL(String s) throws Exception
	{
		return java.net.URLEncoder.encode(s, "UTF-8");
		
	}
	
	public void deleteFile() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop = StringUtil.getIniProperties(reqdata);
		String picurl = java.net.URLDecoder.decode(prop.getProperty("picurl"),"UTF-8");
		
		
		String realfilename=this.getRequest().getServletContext().getContext("/material").getRealPath(picurl.split("material/")[1]);
		

		File file=new File(realfilename);
		
		
		String smallfilename = file.getParent()+"/"+"small-"+file.getName();
		

		File smallfile=new File(smallfilename);
		smallfile.delete();
		file.delete();

	}
	
	public void addFolder() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop = StringUtil.getIniProperties(reqdata);
		String parentdirname = prop.getProperty("parentdirname");
		String foldername = prop.getProperty("foldername");
		String newfolder = this.getRequest().getServletContext()
		.getContext("/material").getRealPath(parentdirname+"/"+foldername);
		File file=new File(newfolder);
		
		if (!file.exists()) FileUtil.mkdir(file);
		
		this.OutputStr("{id:9999,\"name\":\""+foldername+"\",leaf:false}");
	}
	public void deleteFolder() throws Exception
	{
		String reqdata = this.getReqData();
		Properties prop = StringUtil.getIniProperties(reqdata);
		String foldername = prop.getProperty("foldername");
		String folder = this.getRequest().getServletContext()
		.getContext("/material").getRealPath(foldername);
		File file=new File(folder);
		
		if (file.exists()) FileUtil.deletePath(file);	
	}
	
	public void uploadImage() 
	{

		String currDir ="";
		String msg="";
		int status=-1;
		String result="";
		StringBuffer files=new StringBuffer();
		
		try
		{
			
			DiskFileItemFactory factory = new DiskFileItemFactory();  
			factory.setSizeThreshold(4096);

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(1000000);
			upload.setFileSizeMax(500000);
			upload.setHeaderEncoding("UTF-8");
			
			List items = upload.parseRequest(this.getRequest());
	
			Iterator iter = items.iterator();  
			while (iter.hasNext()) {  
			    FileItem item = (FileItem) iter.next();  
			  
			    if (item.isFormField()) {  
			        //如果是普通表单字段   
			        String name = item.getFieldName();  
			        String value = item.getString("GBK").replaceAll("%incline%", "/");  
			    
			     
			        if (name.equalsIgnoreCase("parentdirname"))
			        	currDir=this.getRequest().getServletContext().getContext("/material").getRealPath(value);
			    } else {  
			        //如果是文件字段   
			    	
		            String filename = item.getName();
		  
		            filename=filename.replace("\\", "/");

		            if (!FileUtil.getExtensionName(filename).equalsIgnoreCase("gif")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("jpg")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("bmp")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("jpeg")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("png"))
		            	throw new JException("文件非图片格式:"+filename);
		            
		            String[] splits=filename.split("/");
		            	
		            String name=splits[splits.length-1];
		  
		            
		            File uploadedFile = new File(currDir+"/"+name);  
		            item.write(uploadedFile);   
		            
		            ImageUtil.makeSmallImage(currDir+"/"+name, currDir+"/"+"small-"+name);
		            
		            files.append("{\"filename\":\""+item.getFieldName()+"\"},");
		            status=0;
			    }  
			}
			status=1;
		}catch(FileUploadException e)
		{
			msg="读取上传文件失败!"+e.getMessage();
		}catch(IOException e)
		{
			msg="生成缩略图失败!"+e.getMessage();
		}catch(Exception e)
		{
	
			msg="上传文件失败!"+e.getMessage();
		}

		
		if (status==1)
			result="{success:true,status:"+status+",\"msg\":\""+msg+"\"";
		else
			result="{success:false,status:"+status+",\"msg\":\""+msg+"\"";
		
		if (files.toString().indexOf(",")>=0)
			result=result.concat(",\"files\":["+files.toString().substring(0, files.toString().length()-1)+"]");  //去掉最后一个逗号
		
		result=result.concat("}");
		
		try
		{
			this.OutputStr(result);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void replaceImage() 
	{

		
		
		String oldpicurl ="";
		String msg="";
		int status=0;
		String result="";
		
		try
		{
			DiskFileItemFactory factory = new DiskFileItemFactory();  
			factory.setSizeThreshold(4096);

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(1000000);
			upload.setFileSizeMax(500000);
			upload.setHeaderEncoding("UTF-8");
			
			List items = upload.parseRequest(this.getRequest());
	
			Iterator iter = items.iterator();  
			while (iter.hasNext()) {  
			    FileItem item = (FileItem) iter.next();  
			  
			    if (item.isFormField()) {  
			        //如果是普通表单字段   
			        String name = item.getFieldName();  
			        String value = java.net.URLDecoder.decode(item.getString("UTF-8"),"UTF-8");  
			    			     
			        if (name.equalsIgnoreCase("oldpicurl"))
			        {
			        	oldpicurl=this.getRequest().getServletContext().getContext("/material").getRealPath(value.split("material/")[1]);
			        	
			        }
			    } else {  
			        //如果是文件字段   
			    	
		            String filename = item.getName();
		  
		            filename=filename.replace("\\", "/");

		            if (!FileUtil.getExtensionName(filename).equalsIgnoreCase("gif")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("jpg")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("bmp")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("jpeg")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("png"))
		            	throw new JException("文件非图片格式:"+filename);
		            
		   
		            File uploadedFile = new File(oldpicurl);  
		       
		            
		            item.write(uploadedFile);   
		            		            
		            
		            ImageUtil.makeSmallImage(oldpicurl, uploadedFile.getParent()+"/"+"small-"+uploadedFile.getName());
		            
			    }  
			}
			status=1;
		}catch(FileUploadException e)
		{
			msg="读取上传文件失败!"+e.getMessage();
		}catch(IOException e)
		{
			msg="生成缩略图失败!"+e.getMessage();
		}catch(Exception e)
		{
			msg="上传文件失败!"+e.getMessage();
		}

		
		if (status==1)
			result="{success:true,status:"+status+",\"msg\":\""+msg+"\"";
		else
			result="{success:false,status:"+status+",\"msg\":\""+msg+"\"";
		
		result=result.concat("}");
	
		
		try
		{
			this.OutputStr(result);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void batchUploadImage() throws Exception 
	{

		
		this.getRequest().setCharacterEncoding("UTF-8");		
		String currDir =this.getRequest().getServletContext().getContext("/material").getRealPath(this.getRequest().getParameter("parentdirname"));;		
		String msg="";		
		try
		{
		
			DiskFileItemFactory factory = new DiskFileItemFactory();  
			factory.setSizeThreshold(4096);
		
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(1000000);
			upload.setFileSizeMax(500000);
			upload.setHeaderEncoding("UTF-8");

			List items = upload.parseRequest(this.getRequest());

			
			Iterator iter = items.iterator();  
			while (iter.hasNext()) {  
			    FileItem item = (FileItem) iter.next();  
			  
			    if (item.isFormField()) {  
			        //如果是普通表单字段   
			        String name = item.getFieldName();  
			        String value = item.getString("GBK").replaceAll("%incline%", "/");  
			        
			        	
			    } else {  
			        //如果是文件字段   
			    	
		            String filename = item.getName();

		            
		            filename=filename.replace("\\", "/");

		            if (!FileUtil.getExtensionName(filename).equalsIgnoreCase("gif")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("jpg")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("bmp")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("jpeg")
		            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("png"))
		            	throw new JException("文件非图片格式:"+filename);
		            
		            String[] splits=filename.split("/");
		            	
		            String name=splits[splits.length-1];
		  
		            
		            File uploadedFile = new File(currDir+"/"+name);  
		            item.write(uploadedFile);   
		     
		            
		            ImageUtil.makeSmallImage(currDir+"/"+name, currDir+"/"+"small-"+name);
		            

		            
		            InputStream is = new FileInputStream(currDir+"/"+name);
		            BufferedImage buff = ImageIO.read(is);
		            
		            int imgwidth=buff.getWidth();
		            int imgheight=buff.getHeight();		         
		  
		            is.close();
		
		            String mimetype= new MimetypesFileTypeMap().getContentType(uploadedFile);
		            
		            this.OutputStr("{\"status\":\"1\",\"name\":\""+filename+"\",\"width\":\""+imgwidth+"\",\"height\":\""+imgheight+"\",\"mime\":\""+mimetype+"\"}");
		    
			    }  
			}
		}catch(FileUploadException e)
		{
			msg="读取上传文件失败!"+e.getMessage();
			this.OutputStr("{\"status\":\"0\",\"error\":\""+msg+"\"}");
		}catch(IOException e)
		{
			msg="生成缩略图失败!"+e.getMessage();
			this.OutputStr("{\"status\":\"0\",\"error\":\""+msg+"\"}");
		}catch(Exception e)
		{			
			msg="上传文件失败!"+e.getMessage();
			this.OutputStr("{\"status\":\"0\",\"error\":\""+msg+"\"}");
		}

	}

}
