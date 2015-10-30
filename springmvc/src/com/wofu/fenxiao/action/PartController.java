package com.wofu.fenxiao.action;
import java.util.HashMap;
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
import com.wofu.fenxiao.domain.Part;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.service.PartService;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.Tools;

@Controller
public class PartController extends BaseController{
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private PartService partService;
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private MenuService menuService;
	
	//查询角色资料
	@RequestMapping(value="qryPart.do",method=RequestMethod.POST)
	@ResponseBody
	public String queryPart(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsonResult re = new JsonResult();//全部用这种对象输出
		try{
			Part part =new Part();
			try{
				part.setName(json.getString("name"));
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			List<Part> partResult =partService.queryPart(part);
			for(Part e:partResult){
				logger.info(e.getName());
			}
			re.setErrorCode(0);
			re.setData(partResult);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("取角色资料出错");
		}
		return JSONObject.fromObject(re).toString();
	}
	
	
	//查询帐号角色
	// 输入{loginID}
	//输出  {LoginID ,PartID}
	@RequestMapping(value="qryPartMember.do",method=RequestMethod.POST)
	public @ResponseBody String qryPartMember(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("查询帐号角色【取参数出错】");
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();
		try{
			logger.info("帐号角色取得loginID"+json.get("loginID"));
			
			List<HashMap> result =partService.queryPartMember((Integer)json.get("loginID"));
						
			re.setErrorCode(0);
			re.setData(result);			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("取帐号角色出错");
			logger.info("取帐号角色");
			e.printStackTrace();
		}
		
		//return result.toString(); //不知这样返回对不对  
		//这样能返回json字符串吗，应该要用JSONObject.fromOBject();方法吧
		return JSONObject.fromObject(re).toString();
	}
	
	//取得当前的登录数据
	public HashMap<String,String> getCurLogin(HttpSession session){
		//取得Session数据
		HashMap<String,String> ret = (HashMap) session.getAttribute("CurLoginSession") ;
		if (null==ret){ //如果空，测试时置默认值 ，实际运行时报错
			ret = new HashMap();
			ret.put("ID", "1");
			ret.put("Name", "system");
			ret.put("CName", "系统管理员");
			ret.put("CustomerID", "0");
		}
		
		return ret ;
	}

	//取得当前的菜单
	public List<HashMap> getCurMenu(HttpServletRequest request,HttpServletResponse response){
		//取得Session数据
		List<HashMap> ret = (List<HashMap>) request.getSession().getAttribute("CurMenu") ;
		if (null==ret){ //如果空，测试时取所有菜单 ，实际运行时报错
			try{
				ret = menuService.queryLoginMenu(1);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		
		return ret ;
	}
	
	//帐号及权限管理页面 初始化数据
	//返回当前登录信息、菜单列表，角色列表数据。
	@RequestMapping(value="iniLoginPartData.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniLoginPartData(HttpServletRequest request,HttpServletResponse response){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HttpSession session = request.getSession();
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
				/*login = new HashMap();
				login.put("ID", "1");
				login.put("Name", "system");
				login.put("CName", "系统管理员");
				login.put("CustomerID", "0");*/
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 100300);
			//obj.put("requestURL", Tools.getRequestURL(request));
			
			logger.info("remote address  "+request.getRemoteAddr());  
			logger.info("remote host  "+request.getRemoteHost());  
			logger.info("remote port "+request.getRemotePort());  
			logger.info("remote user  "+request.getRemoteUser());  	
			logger.info("url2:  "+request.getRequestURI());
			logger.info("url3:  "+request.getLocalAddr()+":"+request.getLocalPort()+request.getRequestURI());
			obj.put("requestURL", request.getLocalAddr()+":"+request.getLocalPort()+request.getRequestURI());
			
			
			
			//String loginStr = "curLogin:"+JSONObject.fromObject(login).toString();
			
			
			//取得当前菜单
			List<HashMap> menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ //如果空，测试时取所有菜单 ，实际运行时报错
				try{
					menu = menuService.queryLoginMenu(Integer.parseInt(login.get("ID")));
				} catch (Exception e) {
					logger.info("取菜单出错："+e.getMessage());
					e.printStackTrace();
				}			
			}else{
				logger.info("从session取得菜单："+Integer.toString(menu.size()));
			}
			//String menuStr = "menu:"+JSONArray.fromObject(menu).toString();
			obj.put("menu", menu);
			logger.info("put menu:" + Integer.toString(menu.size()));
			
			//取得所有角色
			String partStr = "";	
			List<Part> part=null;
			try{
				Part p = new Part();
				p.setName("");
				part =partService.queryPart(p);
				//partStr = "part:"+part.toString();
				
				if (Tools.getCurCustomerID(session) !=0){
					part.remove(0);
				}
				
				partStr = "part:"+JSONArray.fromObject(part).toString();
				logger.info("取得角色："+partStr);
			} catch (Exception e) {
				logger.info("取角色出错："+e.getMessage());
				e.printStackTrace();
			}
			
			//String ret = "var allData = {"+loginStr+"," + menuStr + ","+partStr+"}";
			obj.put("part", part);

			//System.out.println(obj.toString());
			re.setErrorCode(0);
			//re.setData("var allData ="+obj.toString());
		}catch(Exception e){
			re.setErrorCode(1);
			re.setData("取用户菜单数据出错");
			logger.info("取用户菜单数据出错："+e.getMessage());
		}
		//取得当前登录信息
		String ret="var allData ="+obj.toString();
		System.out.println(ret);
		//return JSONObject.fromObject(re).toString();
		return ret;
		
	}	
		
	//设置帐号角色
	//输入  { loginID:1, PartID:[1,2,3]}
	@RequestMapping(value="modifyPartMember.do",method=RequestMethod.POST)
	public @ResponseBody String modifyPartMember(HttpServletRequest request,HttpServletResponse response){
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int loginId = (Integer)json.get("loginID");
		//int[] parts = (Integer[])json.get("PartID"); //取数组怎么写？
		//JSONArray parts = json.getJSONObject("PartID");////取数组怎么写？
		//直接getJSONArray();   这里不要用我们的json包的类，要用
		/**
		 *  import net.sf.json.JSONArray;
			import net.sf.json.JSONObject;
		 */
		
		JSONArray parts = json.getJSONArray("PartID");
		JsonResult re = new JsonResult();
		try {
			//删除
			partService.deletePartMember(loginId);
			logger.info("删除角色权限成功");
			
			//增加
			for(int i=0;i<parts.size();i++){
				//JSONObject part = parts.getJSONObject(i);
				HashMap<String,String> map = new HashMap<String,String> ();
				map.put("loginId", String.valueOf(loginId));//
				//map.put("partId", Integer.toString(parts[i]));//不知道你传回来的part数据结构是怎么样的
				map.put("partId", String.valueOf(parts.getInt(i)));
				//map.put("partId", String.valueOf(part[i]));
				partService.addPartMember(map);  //你没有写service接口的实现类吗，这个也要写的
			}
			re.setErrorCode(0);
		} catch (Exception e) { // 不知这样能不能捕捉到异常？            可以
			re.setErrorCode(1)	;
			re.setMsg("修改角色权限失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
		return JSONObject.fromObject(re).toString();
	}
	
}
