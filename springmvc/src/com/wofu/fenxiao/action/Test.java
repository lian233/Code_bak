package com.wofu.fenxiao.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Test {
		@RequestMapping(value="/test")
		public void test(){
			System.out.println("hello word!");
		}
		
		
		
		@RequestMapping(value="/test2")
		public void test2(){
			System.out.println("hello word2!");
		}

}
