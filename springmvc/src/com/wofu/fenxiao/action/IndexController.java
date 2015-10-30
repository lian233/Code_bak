package com.wofu.fenxiao.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class IndexController {
	
	@RequestMapping(value="")
	public String index(){
		System.out.println("login");
		return "login";
	}
}
