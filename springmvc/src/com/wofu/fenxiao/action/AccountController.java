package com.wofu.fenxiao.action;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.Md5Tool;
import com.wofu.fenxiao.utils.POIUtils;



 
@Controller
public class AccountController extends BaseController{
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private LoginService accountService;
	
	@RequestMapping("list")//请求名 现在全部用.do结尾的请求名
	public String list(Model model,  String pageNow) {
		System.out.println("hello word!");
		return "";
	}
	//查找特定用户   
	@RequestMapping(value="/finduser",method=RequestMethod.POST)
	public @ResponseBody Login findUser(@PathVariable int userid) throws Exception{
		
		Login account =accountService.getById(userid);
		System.out.println(account.toString());
		return account;
	}
	

	//调用存储过程    
	@RequestMapping(value="callable.do")
	public @ResponseBody String testcallable(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String,String> map = new HashMap<String,String> ();
		System.out.println(json.get("name"));
		map.put("name", (String)json.get("name"));//调用时传入的参数
		accountService.callable(map);//调用业务
		System.out.println(map.get("back"));//这个是存储过程的output参数  在xml文件中指定
		return "";
	}
	
	//调用有return参数的存储过程
	@RequestMapping(value="callable2.do")
	public @ResponseBody String testcallable2(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String,String> map = new HashMap<String,String> ();
		System.out.println(json.get("name"));
		map.put("name", (String)json.get("name"));//调用时传入的参数
		accountService.callablehasReturn(map);//有return返回类型的
		System.out.println("output返回值： "+map.get("back"));//这个是存储过程的output参数  在xml文件中指定
		System.out.println("return返回值： "+map.get("c"));//这个是存储过程的output参数  在xml文件中指定
		return "";
	}
	
	
	
	//返回一个hashmap  返回多行数据
	@RequestMapping(value="returnHashMap.do")
	public @ResponseBody String returnHashMap(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String,String> params2 = new HashMap<String,String>();
		List<HashMap> map = accountService.returnHashMap(params2);
		System.out.println(map.size());
		return map.toString();
	}
	
	
	//查找特定用户
	@RequestMapping(value="queryall.do",method=RequestMethod.POST)
	public @ResponseBody String findUser(@RequestBody Login account) throws Exception{
		System.out.println(account.getName());
		List<Login> account1 =accountService.queryAll(account);
		for(Login e:account1){
			System.out.println(e.getName());
		}
		return "";
	}
	/**
	 * @param model
	 * 存放返回界面的model
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("query")
	public PageView query(Login account,String pageNow,String pagesize) throws Exception {
		PageView pageView = accountService.query(getPageView(pageNow,pagesize), account);
		return pageView;
	}
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletResponse response,Login account) throws Exception {
		 List<Login> acs =accountService.queryAll(account);
		POIUtils.exportToExcel(response, "账号报表", acs, Login.class, "账号", acs.size());
	}
	/**
	 * 添加用户
	 * 
	 * @param model
	 * @param videoType
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("adduser.do")
	@ResponseBody
	public String add(@RequestBody Login account) {
		JsonResult result =new JsonResult();
		try {
			//account.setPassword(Md5Tool.getMd5(account.getPassword()));
			accountService.add(account);
			result.setErrorCode(0);
		} catch (Exception e) {
			result.setErrorCode(1);
			result.setMsg(e.getMessage());
		}
		return JSONObject.fromObject(result).toString();
	}

	
	/**
	 * 跑到新增界面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("addUI")
	public String addUI() {
		return "";//Common.BACKGROUND_PATH+"/account/add";
	}
	
	/**
	 * 跑到新增界面
	 * 
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("editUI")
	public String editUI(Model model,int accountId) throws Exception {
		Login account = accountService.getById(accountId);
		model.addAttribute("account", account);
		return "";// Common.BACKGROUND_PATH+"/account/edit";
	}
	/**

	 */
	@RequestMapping("isExist")
	@ResponseBody
	public boolean isExist(String name){
		Login account = accountService.isExist(name);
		if(account == null){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 删除
	 * 
	 * @param model
	 * @param videoTypeId
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("deleteById")
	public Map<String, Object> deleteById(Model model, String ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id[] = ids.split(",");
			for (String string : id) {
				if(!Common.isEmpty(string)){
				accountService.delete(Integer.parseInt(string) );
				}
			}
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
	/**
	 * 删除
	 * 
	 * @param model
	 * @param videoTypeId
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("updateState")
	public Map<String, Object> updateState(Model model, String ids,int status) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id[] = ids.split(",");
			for (String string : id) {
				if(!Common.isEmpty(string)){
					Login account = new Login();
					account.setId(Integer.parseInt(string));
					account.setStatus(status);
					accountService.update(account);
				}
			}
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
	/**
	 * 更新类型
	 * 
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("update")
	public Map<String, Object> update(Model model, Login account) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
	
			account.setPassword(Md5Tool.getMd5(account.getPassword()));
			accountService.update(account);
			map.put("flag", "true");
		} catch (Exception e) {
			map.put("flag", "false");
		}
		return map;
	}
	public LoginService getAccountService() {
		return accountService;
	}
	public void setAccountService(LoginService accountService) {
		this.accountService = accountService;
	}
	
}