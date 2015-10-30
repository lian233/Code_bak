package com.wofu.fenxiao.utils;
/**
 * 数据源存放类
 * @author bolinli
 *
 */
public class DataSourceHolder {
	private static final ThreadLocal<String> dataSource = new ThreadLocal<String>();
	
	public static void setDataSource(String customerType){
		dataSource.set(customerType);
	}
	//获取数据源
	public static String getDataSource(){
		return dataSource.get();
	}
	//清除数据源
	public static void removeDataSource(){
		dataSource.remove();
	}
}
