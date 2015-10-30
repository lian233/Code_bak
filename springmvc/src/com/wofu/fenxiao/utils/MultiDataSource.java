package com.wofu.fenxiao.utils;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
/**
 * 多数据源datasource
 * @author Administrator
 *
 */
public class MultiDataSource extends AbstractRoutingDataSource{

	@Override
	protected Object determineCurrentLookupKey() {
		// TODO Auto-generated method stub
		return DataSourceHolder.getDataSource();
	}
	

	
	

}
