package com.wofu.ecommerce.jingdong.test;

import org.junit.Before;

/**
 * 测试基类
 * @author Administrator
 *
 */
public abstract class TestManager {
	protected String app_key="";
	protected String app_secret="";
	protected String token="";
	protected String url="";
	
	//测试前完成的任务
	@Before
	abstract void init();
	

}
