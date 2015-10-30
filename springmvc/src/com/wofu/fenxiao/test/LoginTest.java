package com.wofu.fenxiao.test;
import java.util.HashMap;
import org.junit.Test;
import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.domain.Part;
import com.wofu.fenxiao.domain.PartMember;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.LoginService;

/**
 * 用户测试用例
 * @author Administrator
 *
 */
public class LoginTest extends BaseTest<Login>{
	private LoginService loginService;
	@Test
	public void test() {
		loginService = (LoginService)context.getBean("loginService");
		Login login = new Login();
		login.setId(0);
		login.setName("管理员");
		login.setCName("管理员");
		login.setCustomerID(0);
		login.setPassword("123456");
		login.setId(4);
		login.setIp("212.23.25.889");
		login.setStatus(0);
		PartMember member = new PartMember();
		member.setLogin(login);
		Part part = new Part();
		part.setId(0);
		part.setName("test");
		member.setPart(part);
		try{//
			//把用户加入角色中
			loginService.saveRelativity(member);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
}
