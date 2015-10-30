package com.wofu.fenxiao.interceptor;
/**
 * 登录拦截器
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
public class LoginInterceptor extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		System.out.println(request.getLocalAddr()+":"+request.getLocalPort()+request.getRequestURI());
		// TODO Auto-generated method stub
		//检查sessin用的用户信息，没有的话，重定向到登录 页面
		if(request.getSession().getAttribute("CurLoginSession")==null){
			String requestURI = request.getRequestURI();
			if(requestURI.indexOf("login.do")>0)//登录页面不拦截
				return true;
			else{
				response.setContentType("application/json;charset=utf-8");
				//response.getOutputStream().write("var allData={\"errorCode\":-100,\"msg\":\"not login\"}".getBytes());
				response.getOutputStream().write("window.location.href=\"default.html\";".getBytes());
				response.getOutputStream().flush();
				return false;
			}
			
		}else{
			return true;
		}
		
	}
	
}
