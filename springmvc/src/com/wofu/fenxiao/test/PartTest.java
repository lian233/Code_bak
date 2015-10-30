package com.wofu.fenxiao.test;

import com.wofu.fenxiao.domain.Part;
import com.wofu.fenxiao.service.PartService;

/**
 * 用户角色测试用例
 * @author Administrator
 *
 */
public class PartTest extends BaseTest<Part>{
	private PartService partservice ;
	@Override
	public void test() throws Exception {
		partservice = (PartService)context.getBean("partService");
		Part p = new Part();
		p.setId(55);
		p.setName("管理员");
		p.setNote("系统管理员");
		p.setStatus(0);
		partservice.ss(p);
		
		
		
	}
	
	
}
