package com.wofu.fenxiao.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/*
**测试基类
**/

public abstract class BaseTest<T> {
	public AbstractApplicationContext context=null;
	@SuppressWarnings("unchecked")
	@Before
	public void before(){
		//加载ApplicationContent
		context= new ClassPathXmlApplicationContext(new String[]{
				"spring-application.xml","springMvc3-servlet.xml"
		});
		
	}
	
	@Test
	public abstract void test() throws Exception;
	
	@After//释放容器
	public void after(){
		if(context!=null)
			context.close();
	}
	
}
