package com.wofu.ecommerce.jingdong.test;

import org.junit.Before;

/**
 * ���Ի���
 * @author Administrator
 *
 */
public abstract class TestManager {
	protected String app_key="";
	protected String app_secret="";
	protected String token="";
	protected String url="";
	
	//����ǰ��ɵ�����
	@Before
	abstract void init();
	

}
