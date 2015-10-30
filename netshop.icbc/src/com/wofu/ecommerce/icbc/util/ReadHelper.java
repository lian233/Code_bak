package com.wofu.ecommerce.icbc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadHelper {

    /**
     * 读取jar内资源文�?
     * @param loader
     * @param resource
     * @return
     * @throws IOException
     */
	public static InputStream getResourceAsStream(ClassLoader loader,
			String resource) throws IOException {
		InputStream in = null;
		if (loader != null)
			in = loader.getResourceAsStream(resource);
		if (in == null)
			in = ClassLoader.getSystemResourceAsStream(resource);
		if (in == null)
			throw new IOException("Could not find resource " + resource);
		return in;
	}
    
    /**
     * 获得系统下配置文�?
     * @param req_data_file
     * @return
     */
    public static Properties haveConfigPath(String config_path) throws Exception{
    	Properties properties=null;
//		InputStream is=getResourceAsStream(DemoGeneral.class.getClassLoader(), config_path);
    	InputStream is=new FileInputStream(config_path);
		properties=new Properties();
		properties.load(is);
    	return properties;
    }
    
    /**
     * 获得系统下配置文�?
     * @param req_data_file
     * @return
     */
    public static Properties haveConfigPath(InputStream is) throws Exception{
    	Properties properties=null;
		properties=new Properties();
		properties.load(is);
    	return properties;
    }
    
    /**
     * 获得某目录的上N级目�?
     * @param path 目录
     * @param increment 增量
     * @return
     */
    public static String haveUpDir(String path,int increment){
    	File file=new File(path);
    	if(file.isFile()){
    		file=file.getParentFile();
    	}
    	while(increment!=0){
    		file=file.getParentFile();
    		increment--;
    	}
    	return file.getPath();
    }

	/**
	 * 读取文件内容
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String readFile(String path,String charset) throws Exception{
		File file=new File(path);
		InputStream is=new FileInputStream(file);
		byte[] contentBytes=new byte[is.available()];
		try {
			is.read(contentBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null){
				is.close();
			}
		}
		return new String(contentBytes,charset);
	}

	/**
	 * 读取文件内容
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String readResource(String path,String charset) throws Exception{
		InputStream is=getResourceAsStream(null, path);
		byte[] contentBytes=new byte[is.available()];
		try {
			is.read(contentBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null){
				is.close();
			}
		}
		return new String(contentBytes,charset);
	}
    
}
