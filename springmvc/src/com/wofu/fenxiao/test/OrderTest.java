package com.wofu.fenxiao.test;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.domain.Part;
import com.wofu.fenxiao.service.DecOrderService;
import com.wofu.fenxiao.service.PartService;

/**
 * 用户角色测试用例
 * @author Administrator
 *
 */
public class OrderTest extends BaseTest<DecOrder>{
	private PartService partservice ;
	@Override
	public void test() throws Exception {
		DecOrderService  orderService= (DecOrderService)context.getBean("decOrderService");
		String test="{\"orders\":[115]}";
		JSONArray array = JSONObject.fromObject(test).getJSONArray("orders");
		//orderService.confirmDecOrders(array,null);
		System.out.println("test");
		//partservice.ss(p);
		
		
		
	}
	
	
}
