package com.wofu.fenxiao.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecCustomerService;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.Md5Tool;
import com.wofu.fenxiao.utils.Tools;

@Controller
public class LoginController {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	LoginService accountService;
	@Autowired
	MenuService menuService;
	@Autowired
	DecCustomerService decCustomerService;
	
	//用户登录
	@RequestMapping(value="login.do",method=RequestMethod.POST)
	//responseBody是直接向客户端返回数据
	//public @ResponseBody String login(@RequestBody Login user) throws Exception{
	public @ResponseBody String login(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		//System.out.println(user.getName()+" "+user.getName()+"1"+user.getPassword()+"1");
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		JsonResult re = new JsonResult();
		String name = (String)json.get("name");
		String password = (String)json.get("password");
		logger.info("login...");
		if(name==null || password==null || "".equals(name) || "".equals(password)){
			re.setErrorCode(1)	;
			re.setMsg("用户名或密码不能为空");
		}
		else{
			try{
				Login account = accountService.queryByName(name, null);

				if(account==null){
					re.setErrorCode(1);
					re.setMsg("找不到用户【"+name+"】");
					return JSONObject.fromObject(re).toString();
				}
				
				if (account.getStatus()!=1){
					re.setErrorCode(2);
					re.setMsg("用户状态不对");
					return JSONObject.fromObject(re).toString();					
				}
				
				logger.info("找到用户：" +account.getCName() + " 客户ID：" + Integer.toString(account.getCustomerID())  );
				//检查密码				
				if (! password.equals(account.getPassword()))
				{
					if (! account.getPassword().equals(Md5Tool.getMd5(password)) ){
						logger.info("原：" + account.getPassword() + " 输入密码："+ password);
						re.setErrorCode(1);
						re.setMsg("密码不正确");
						return JSONObject.fromObject(re).toString();				
					}
				}				
				
				//取得帐户
				HashMap<String,String> curLogin = new HashMap<String,String>();
				curLogin.put("ID", Integer.toString(account.getId()));
				curLogin.put("Name",account.getName());
				curLogin.put("CName",account.getCName());
				curLogin.put("CustomerID",Integer.toString(account.getCustomerID()));
				
				curLogin.put("SystemLogo",accountService.GetCustomerConfig("系统LOGO" , "./images/logo.png",1,0));
				curLogin.put("SystemName",accountService.GetCustomerConfig("系统名称" , "E快递",1,0));
				curLogin.put("SystemType",accountService.GetCustomerConfig("系统类型" , "0",1,0));
				curLogin.put("SystemUrl",accountService.GetCustomerConfig("系统URL" , "0",1,0));
				
				//取得客户
				DecCustomer customer = null;
				if (curLogin.get("SystemType").toString().equals("1")){
					customer = decCustomerService.getByDId(account.getCustomerID());
				}
				else{
					customer = decCustomerService.getById(account.getCustomerID());
				}
				
				
				if (null==customer){
					throw new Exception("找不到客户");
				}
				curLogin.put("CustomerName",customer.getName());
				curLogin.put("CustomerTele",customer.getTele());
				curLogin.put("CustomerEmail",customer.getEmail());
				curLogin.put("CustomerLevel", Integer.toString(customer.getLevel()) );
				
				//取得客户分组前缀
				if (curLogin.get("SystemType").toString().equals("1")){
					customer = decCustomerService.getByDId(account.getCustomerID());
					curLogin.put("Front", "");
				}
				else{
					HashMap<String,Object> param = new HashMap<String,Object>();
					param.put("id", customer.getGroupID());
					List<HashMap> group = decCustomerService.qryCustomerGroupList(param);
					
					if (null==group){
						throw new Exception("找不到客户分组");
					}
					
					if (group.size()<=0){
						re.setErrorCode(2);
						re.setMsg("找不到客户分组");
						return JSONObject.fromObject(re).toString();					
					} 
					curLogin.put("Front", (String)group.get(0).get("Front"));
				}
				
				request.getSession().setAttribute("CurLoginSession", curLogin);
				
				logger.info("取得SESSON客户编号"+ Integer.toString(Tools.getCurCustomerID(request.getSession())));
								
				//取得当前菜单
				List<HashMap> menu =menuService.queryLoginMenu(account.getId());
				logger.info("取得菜单："+Integer.toString(menu.size()));
				request.getSession().setAttribute("CurMenu", menu);	
				
				re.setErrorCode(0);
			}catch (Exception e) {
				logger.info("登录失败：" + e.getMessage());
				re.setErrorCode(1);
				re.setMsg("登录失败：" + e.getMessage());
				e.printStackTrace();
				
			}
		}
		return JSONObject.fromObject(re).toString();
	}

	
	//保存帐号数据
	//输入{[ID,Name,CName, CustomerID, Password,Status,Note,operType]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveLogin.do",method=RequestMethod.POST)
	public @ResponseBody String saveLogin(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("查询帐号【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray logins = json.getJSONArray("logins");
		JSONArray idList = new JSONArray();
		JsonResult re = new JsonResult();
				
		try{
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			for(int i=0;i<logins.size();i++){
				JSONObject login = logins.getJSONObject(i);
				logger.info("保存帐号取得字段数【"+login.size()+"】");
				
				if (!login.containsKey("ID")){
					throw new Exception("ID数据不存在");
				} 
				
				Login l = new Login();
				int id = login.getInt("ID");
				l.setId(id);
				if (login.size()>=2){//
					if (login.containsKey("Name")) {l.setName(login.getString("Name"));}
					if (login.containsKey("CName")) {l.setCName(login.getString("CName"));}
					if (login.containsKey("Note")) {l.setNote(login.getString("Note"));}
											
					if (curCustomerID==0){//站点维护的资料
						if (login.containsKey("CustomerID")) {
							l.setCustomerID(login.getInt("CustomerID"));
						} 
						else {
							if (id==-1){//增加
								throw new Exception("没输入客户数据");
							}else{
								l.setCustomerID(-1);
							}
						}						
					}else{
						l.setCustomerID(curCustomerID);
					}
					
					if (login.containsKey("Status")) {l.setStatus(login.getInt("Status"));}
					
					if (login.containsKey("Password")){
						l.setPassword(Md5Tool.getMd5((String)login.get("Password")));
					}
										
				}
				/*
				HashMap<String,String> map = new HashMap<String,String> ();
				map.put("ID", login.getString("ID"));
				map.put("Name", login.getString("Name"));
				map.put("CName", login.getString("CName"));
				map.put("CustomerID", login.getString("CustomerID"));
				map.put("Password", login.getString("Password"));
				map.put("Status", login.getString("Status"));
				map.put("Note", login.getString("Note"));*/
				
				if (id==-1){//增加
					//取得最大的ID
					/*
					HashMap<String,Object> map = new HashMap<String,Object> ();
					map.put("SerialID", 100300);//调用时传入的参数
					
					accountService.tlGetNewSerial(map);
					logger.info("取得帐号ID值【"+map.get("Value")+"】，返回结果【"+map.get("c")+"】");
					
					
					if ((Integer)map.get("c")==0){
						l.setId((Integer)map.get("Value"));
						accountService.add(l);			
					}
					else{
						throw new Exception("取新增ID出错"); 						
					}*/
										
					if (!login.containsKey("Name")) {throw new Exception("没有输入名称");}
					//检查名称
					Login account = accountService.queryByName(l.getName(), null);
					if (account != null){
						throw new Exception("帐号【"+l.getName()+"】已存在");
					}
					
					if (!login.containsKey("Status")) {l.setStatus(1);}
					
					int newid = accountService.GetNewID(100300);
					idList.add(newid);
					l.setId(newid);
					accountService.add(l);								
				}else{
					logger.info("保存帐号取得字段数【"+login.size()+"】");
					if (login.size()<=1){//删除 
						accountService.delete(l.getId());
					}
					else{//修改
						if (!login.containsKey("Status")) {l.setStatus(-1);}
						accountService.update(l);
					}
				}				
			}			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存帐号数据出错：" + e.getMessage());
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}
	
	
	//修改密码
	@RequestMapping(value="modifyPasswd.do",method=RequestMethod.POST)
	public @ResponseBody String modifyPasswd(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("修改密码【取参数出错】");
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();
		int id = Tools.getCurLoginID(request.getSession());
		String password = (String)json.get("password");
		String oriPassword = (String)json.get("oriPassword");
		int isCheck = 1;//检查原密码
		
		
		if (json.has("id") ){//如果有指定ID，则修改指定ID的密码；否则修改登录的密码
			id = (Integer)json.get("id");
		} 
		
		if (json.has("isCheck") ){//是否检查原密码，用来管理员重设密码不检查
			isCheck = (Integer)json.get("isCheck");
		} 
		
		//取用户
		Login l = accountService.getById(id);
		if(l==null){
			re.setErrorCode(1);
			re.setMsg("找不到用户");
			return JSONObject.fromObject(re).toString();
		}		
		
		//检查密码 
		if (isCheck != 0){
			if (oriPassword==null){
				re.setErrorCode(1);
				re.setMsg("没有输入原密码");
				return JSONObject.fromObject(re).toString();				
			}
			if (! l.getPassword().equals(oriPassword) )
			{
				if (! l.getPassword().equals(Md5Tool.getMd5(oriPassword))) {
					re.setErrorCode(1);
					re.setMsg("原密码不正确");
					return JSONObject.fromObject(re).toString();				
				}
			}
		}
		
		//修改密码
		try{			
			l.setId(id);
			l.setPassword(Md5Tool.getMd5(password));
			accountService.update(l);
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("修改密码失败");
			logger.info("保存帐号数据出错");
			e.printStackTrace();
		}
				
		return JSONObject.fromObject(re).toString();
	}
	
	//删除帐号数据
	//{"loginID":[1,2,3]}
	@RequestMapping(value="removeLogin.do",method=RequestMethod.POST)
	public @ResponseBody String removeLogin(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("查询帐号【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray logins = json.getJSONArray("loginID");
		JsonResult re = new JsonResult();
		try{
			for(int i=0;i<logins.size();i++){
				accountService.delete(logins.getInt(i));				
			}			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("删除帐号数据出错");
			logger.info("删除帐号数据出错");
			e.printStackTrace();
		}
	
		return JSONObject.fromObject(re).toString();
	}	
	
	//查询帐号数据
	// { customerID,cName}
	//输出 []
	@RequestMapping(value="qryLogin.do",method=RequestMethod.POST)
	public @ResponseBody String qryLogin(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("查询帐号【取参数出错】");
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();
		try{
			logger.info("帐号角色取得cName"+json.get("cName"));
			
			HashMap<String,Object> params2 = new HashMap<String,Object>();
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			try{
				if (curCustomerID==0)
				{
					params2.put("customerID", (Integer)json.get("customerID"));
				}
				else{
					params2.put("customerID", curCustomerID);
				}
			}catch(Exception e){}
			
			String cName = json.get("cName")!=null?(String)(json.get("cName")):"";
			params2.put("cName", cName);
			
			//分页
			/*
			PageView view = new PageView();
			view.setId("ID");
			int pageNow = json.get("pn")!=null?(Integer)json.get("pn"):1;//默认第一页
			view.setPage(pageNow);
			int pageSize = json.get("pageSize")!=null?(Integer)json.get("pageSize"):0;//默认每页10
			if(pageSize!=0) 
				view.setPsize(pageSize);
			params2.put("pageview",view);//分页参数
			*/
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
			
			
			//根据系统类型取列表
			HashMap<String,String> curLogin = (HashMap<String,String>)request.getSession().getAttribute("CurLoginSession");			
			List<HashMap> result = null;						
			if (curLogin.get("SystemType").toString().equals("1")){
				result =accountService.qryDLogin(params2);
			}
			else{
				result =accountService.qryLogin(params2);
			}
			
			for(Iterator it = result.iterator();it.hasNext();){
				HashMap temp = (HashMap)it.next();
				for(Iterator t = temp.keySet().iterator();t.hasNext();){
					String name=(String)t.next();
					Object value= temp.get(name);
					logger.info(name+" "+value.toString());
				}
			}
			re.setErrorCode(0);
			re.setData(result);	
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查帐号数据出错");
			logger.info("查帐号数据出错");
			e.printStackTrace();
		}
		
		//return result.toString(); //不知这样返回对不对  
		//这样能返回json字符串吗，应该要用JSONObject.fromOBject();方法吧
		logger.info("result:　"+JSONObject.fromObject(re).toString());
		return JSONObject.fromObject(re).toString();
	}
	
	//退出登录
	@RequestMapping(value="loginout.do",method=RequestMethod.GET)
	@ResponseBody 
	public String loginout(HttpSession session){
		session.setAttribute("CurLoginSession", null);
		session.setAttribute("CurMenu", null);
		
		return "location.href = \"default.html\";";
		
	}			
		
	

}
