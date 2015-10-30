package com.wofu.fenxiao.action;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecCustomerService;
import com.wofu.fenxiao.mapping.DecCustomerMapper;
import com.wofu.fenxiao.service.DecDeliveryService;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.JsonDateSerializer;
import com.wofu.fenxiao.utils.JsonDateValueProcessor;
import com.wofu.fenxiao.utils.Tools;

@Controller
public class DecCustomerController extends BaseController{
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private DecCustomerService decCustomerService;

	@Autowired  //这里自动生成服务层组件对象
	private DecCustomerMapper decCustomerMapper;
		
	@Autowired  //这里自动生成服务层组件对象
	private DecDeliveryService decDeliveryService;

	@Autowired  
	private LoginService accountService;

	@Autowired  
	private MenuService menuService;
	
	//客户资料页面 初始化数据
	//返回当前登录信息、菜单列表，客户分组列表、快递分组列表数据。
	@RequestMapping(value="iniCustomerData.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniCustomerData(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 300100);
			//String loginStr = "curLogin:"+JSONObject.fromObject(login).toString();
			
			/*
			if (Tools.getCurCustomerID(session)>0){
				return "location.href = \"home.html\";";
			}*/
			
			//取得当前菜单
			List<HashMap> menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ //如果空，测试时取所有菜单 ，实际运行时报错
				try{
					menu = menuService.queryLoginMenu(Integer.parseInt(login.get("ID")));
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
			obj.put("menu", menu);
			
			//取得客户分组
			List<HashMap> customerGroup=null;
			try{
				HashMap<String,Object> p = new HashMap<String,Object>();
				customerGroup = decCustomerService.qryCustomerGroupList(p);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			obj.put("customerGroup", customerGroup);

			//取得快递分组
			List<HashMap> deliveryGroup=null;
			try{
				HashMap<String,String> p = new HashMap<String,String>();
				deliveryGroup = decDeliveryService.qryDeliveryGroupList(p);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			obj.put("deliveryGroup", deliveryGroup);

			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setData("取用户菜单数据出错");
		}
		//取得当前登录信息
		String ret="var allData ="+obj.toString();
		System.out.println(ret);
		return ret;
		
	}		
	
	//问题跟踪管理页面 初始化数据
	//返回当前登录信息、菜单列表
	@RequestMapping(value="iniCustomerServiceData.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniCustomerServiceData(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 300100);
			//String loginStr = "curLogin:"+JSONObject.fromObject(login).toString();
			

			//取得当前菜单
			List<HashMap> menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ //如果空，测试时取所有菜单 ，实际运行时报错
				try{
					menu = menuService.queryLoginMenu(Integer.parseInt(login.get("ID")));
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
			obj.put("menu", menu);
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setData("取用户菜单数据出错");
		}
		//取得当前登录信息
		String ret="var allData ="+obj.toString();
		System.out.println(ret);
		return ret;
		
	}	
	
	//查询客户资料
	//{ groupID ,name ,code,address,linkman}
	@RequestMapping(value="qryCustomer.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryCustomer(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		JsonConfig config=null;
		try{
			config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			if (curCustomerID==0)
			{
				params2.put("id", (Integer)json.get("customerID"));
			}
			else{
				params2.put("id", curCustomerID);
			}			
			params2.put("groupID", (Integer)json.get("groupID"));
			params2.put("name", (String)json.get("name"));
			params2.put("code", (String)json.get("code"));
			params2.put("address", (String)json.get("address"));
			params2.put("linkman", (String)json.get("linkman"));
			
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			logger.info("page: "+view.getPage());
			params2.put("pageview",view);//分页参数
			
			List<DecCustomer> result = decCustomerService.qryCustomer(params2);
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询客户资料出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re,config).toString();
	}
			

	//查询客户列表数据
	// {key}
	//输出 []
	@RequestMapping(value="qryCustomerList.do",method=RequestMethod.POST)
	public @ResponseBody String qryCustomerList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("查询客户【取参数出错】");
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();
		try{
			logger.info("qryCustomerList取得key"+json.get("key"));
			
			HashMap<String,String> params2 = new HashMap<String,String>();
			params2.put("key", (String)json.get("key"));
			//根据系统类型取列表
			HashMap<String,String> curLogin = (HashMap<String,String>)request.getSession().getAttribute("CurLoginSession");
			List<HashMap> result = null;
			if (curLogin.get("SystemType").toString().equals("1")){
				result =decCustomerService.qryDCustomerList(params2);
			}
			else{
				result =decCustomerService.qryCustomerList(params2);
			}
			
			
			re.setErrorCode(0);
			re.setData(result);			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查客户数据出错");
			logger.info("查客户数据出错:"+e.getMessage());
			e.printStackTrace();
		}
		
		return JSONObject.fromObject(re).toString();
	}	
	
	//保存客户数据
	//输入{[ID,Name,Code,GroupID,Address,ZipCode,Email,FaxNo ,Tele,Moblie,LinkMan,GradeID,CreateTime,Creator,ModiTime,ModiID,Status,Note ]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveCustomer.do",method=RequestMethod.POST)
	public @ResponseBody String saveCustomer(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("客户【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray customers = json.getJSONArray("customers");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		try{
			for(int i=0;i<customers.size();i++){
				JSONObject customer = customers.getJSONObject(i);
				
				DecCustomer c = new DecCustomer();
				c.setId(customer.getInt("ID"));
				if (customer.size()>=2){//
					if (customer.containsKey("Name")) {c.setName(customer.getString("Name"));}
					if (customer.containsKey("Code")) {c.setCode(customer.getString("Code"));}
					if (customer.containsKey("GroupID")) {c.setGroupID(customer.getInt("GroupID"));} 
					if (customer.containsKey("DeliveryGroupID")) {c.setDeliveryGroupID(customer.getInt("DeliveryGroupID"));} 
					if (customer.containsKey("Address")) {c.setAddress(customer.getString("Address"));}
					if (customer.containsKey("State")) {c.setState(customer.getString("State"));}
					if (customer.containsKey("City")) {c.setCity(customer.getString("City"));}
					if (customer.containsKey("District")) {c.setDistrict(customer.getString("District"));}
					if (customer.containsKey("ZipCode")) {c.setZipCode(customer.getString("ZipCode"));}
					if (customer.containsKey("Email")) {c.setEmail(customer.getString("Email"));}
					if (customer.containsKey("FaxNo")) {c.setFaxNo(customer.getString("FaxNo"));}
					if (customer.containsKey("Tele")) {c.setTele(customer.getString("Tele"));}
					if (customer.containsKey("Mobile")) {c.setMobile(customer.getString("Mobile"));}
					if (customer.containsKey("LinkMan")) {c.setLinkMan(customer.getString("LinkMan"));}
					if (customer.containsKey("GradeID")) {c.setGradeID(customer.getInt("GradeID"));}
					if (customer.containsKey("Status")) {c.setStatus(customer.getInt("Status"));}
					if (customer.containsKey("Note")) {c.setNote(customer.getString("Note"));}										
					
					/*
					c.setName(customer.containsKey("Name")?customer.getString("Name"):"");					
					c.setCode(customer.containsKey("Code")?customer.getString("Code"):"");
					c.setGroupID(customer.containsKey("GroupID")?customer.getInt("GroupID"):0);
					c.setDeliveryGroupID(customer.containsKey("DeliveryGroupID")?customer.getInt("DeliveryGroupID"):0);
					c.setAddress(customer.containsKey("Address")?customer.getString("Address"):"");
					c.setZipCode(customer.containsKey("ZipCode")?customer.getString("ZipCode"):"");
					c.setEmail(customer.containsKey("Email")?customer.getString("Email"):"");
					c.setFaxNo(customer.containsKey("FaxNo")?customer.getString("FaxNo"):"");
					c.setTele(customer.containsKey("Tele")?customer.getString("Tele"):"");
					c.setMoblie(customer.containsKey("Moblie")?customer.getString("Moblie"):"");
					c.setLinkMan(customer.containsKey("LinkMan")?customer.getString("LinkMan"):"");
					c.setGradeID(customer.containsKey("GradeID")?customer.getInt("GradeID"):0);
	
					c.setStatus(customer.containsKey("Status")?customer.getInt("Status"):1);
					c.setNote(customer.containsKey("Note")?customer.getString("Note"):"");*/
				}
				
				
				if (customer.getInt("ID")==-1){//增加
					c.setCreateTime(new Date());
					c.setModiTime(new Date());
					
					String op = "";
					HashMap<String,String> login = (HashMap<String,String>) request.getSession().getAttribute("CurLoginSession");
					if (null!=login){ 
						op = login.get("Name");
					}
					
					c.setCreator(op);//登录人
					c.setModiID(op);//登录人
					
					c.setCode(decCustomerService.MakeCustomerCode());//生成客户编码
					
					//取得最大的ID
					int newid = accountService.GetNewID(300100);
					idList.add(newid);
					c.setId(newid);
					decCustomerService.add(c);	
					/*
					HashMap<String,Object> map = new HashMap<String,Object> ();					
					map.put("SerialID", 300100);//取客户ID
					accountService.tlGetNewSerial(map);
					logger.info("取得帐号ID值【"+map.get("Value")+"】，返回结果【"+map.get("c")+"】");
					
					if ((Integer)map.get("c")==0){
						c.setId((Integer)map.get("Value"));
						decCustomerService.add(c);			
					}
					else{
						throw new Exception("取新增ID出错"); 						
					}*/
				}else{
					if (customer.size()<=1){//删除 
						decCustomerService.delete(c.getId());
					}
					else{//修改
						c.setModiTime(new Date());
						if (!customer.containsKey("Status")) {c.setStatus(-1);}
						if (!customer.containsKey("GroupID")) {c.setGroupID(-1);}
						if (!customer.containsKey("DeliveryGroupID")) {c.setDeliveryGroupID(-1);}
						if (!customer.containsKey("GradeID")) {c.setGradeID(-1);}						
						
						String op = "";
						HashMap<String,String> login = (HashMap<String,String>) request.getSession().getAttribute("CurLoginSession");
						if (null!=login){ 
							op = login.get("Name");
						}		
						c.setModiID(op);//登录人
						decCustomerService.update(c);
					}
				}				
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存客户数据出错"+e.getMessage());
			logger.info("保存客户数据出错:"+e.getMessage());
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}

	//查询客户问题
	//{ CustomerID, TypeID, Question, Questioner, Answerer, Flag}
	@RequestMapping(value="qryCustomerService.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryCustomerService(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			if (curCustomerID==0)
			{
				params2.put("CustomerID", (Integer)json.get("CustomerID"));
			}
			else{
				params2.put("CustomerID", curCustomerID);
			}			
			params2.put("TypeID", (Integer)json.get("TypeID"));
			params2.put("Question", (String)json.get("Question"));
			params2.put("Questioner", (String)json.get("Questioner"));
			params2.put("Answerer", (String)json.get("Answerer"));
			params2.put("Flag", (Integer)json.get("Flag"));
			
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			logger.info("page: "+view.getPage());
			params2.put("pageview",view);//分页参数
			
			List<HashMap> result = decCustomerMapper.qryCustomerService(params2);
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询客户问题出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}

	//保存客户问题数据
	//输入{ CustomerServices:[ID, TypeID, Question, Questioner, LinkTele, Email, Answer, Answerer, SID, Note]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveCustomerService.do",method=RequestMethod.POST)
	public @ResponseBody String saveCustomerService(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("客户【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray customerServices = json.getJSONArray("CustomerServices");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		try{
			for(int i=0;i<customerServices.size();i++){
				JSONObject customerService = customerServices.getJSONObject(i);
				
				HashMap<String, Object> cs = new HashMap<String,Object>();
				cs.put("ID", customerService.getInt("ID"));
				
				if (customerService.size()>=2){//
					if (customerService.containsKey("TypeID")) {cs.put("TypeID", customerService.getInt("TypeID"));} 
					if (customerService.containsKey("Question")) {cs.put("Question", customerService.getString("Question"));} 
					if (customerService.containsKey("Questioner")) {cs.put("Questioner", customerService.getString("Questioner"));} 
					if (customerService.containsKey("LinkTele")) {cs.put("LinkTele", customerService.getString("LinkTele"));} 
					if (customerService.containsKey("Email")) {cs.put("Email", customerService.getString("Email"));} 
					if (customerService.containsKey("Answer")) {cs.put("Answer", customerService.getString("Answer"));} 
					if (customerService.containsKey("Answerer")) {cs.put("Answerer", customerService.getString("Answerer"));} 
					if (customerService.containsKey("SID")) {cs.put("SID", customerService.getInt("SID"));} 
					if (customerService.containsKey("Flag")) {cs.put("Flag", customerService.getInt("Flag"));} 
					if (customerService.containsKey("Note")) {cs.put("Note", customerService.getString("Note"));} 					
				}
				
				
				if (customerService.getInt("ID")==-1){//增加
					cs.put("CreateTime", new Date());
					cs.put("CustomerID", Tools.getCurCustomerID(request.getSession()));
					cs.put("Flag", 0);
										
					//取得最大的ID
					int newid = accountService.GetNewID(300400);
					idList.add(newid);
					cs.put("ID", newid);
					decCustomerMapper.addCustomerService(cs);	
				}else{
					if (customerService.size()<=1){//删除 
						decCustomerMapper.deleteCustomerService(customerService.getInt("ID"));
					}
					else{//修改
						cs.put("ModiTime", new Date());
						decCustomerMapper.updateCustomerService(cs);
					}
				}				
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存客户问题数据出错"+e.getMessage());
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}	
	
	//查询客户自定义打印内容
	//{ CustomerID}
	@RequestMapping(value="qryCustomerPrintContent.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryCustomerPrintContent(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			
			List<HashMap> result = decCustomerMapper.qryCustomerPrintContent(curCustomerID);
			re.setErrorCode(0);
			re.setData(result);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询自定义打印内容出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}	
	
	//保存客户自定义打印内容
	@RequestMapping(value="saveCustomerPrintContent.do",method=RequestMethod.POST)
	public @ResponseBody String saveCustomerPrintContent(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("客户自定义打印内容【取参数出错】");
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//		
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			if (curCustomerID==0)
			{
				params2.put("CustomerID", (Integer)json.get("CustomerID"));
			}
			else{
				params2.put("CustomerID", curCustomerID);
			}			
			
			params2.put("PrintContent1", (String)json.get("PrintContent1"));
			params2.put("PrintContent2", (String)json.get("PrintContent2"));
			params2.put("PrintContent3", (String)json.get("PrintContent3"));
			params2.put("PrintContent4", (String)json.get("PrintContent4"));
			params2.put("PrintContent5", (String)json.get("PrintContent5"));
			params2.put("PrintContent6", (String)json.get("PrintContent6"));
			params2.put("PrintContent7", (String)json.get("PrintContent7"));
			params2.put("PrintContent8", (String)json.get("PrintContent8"));
			params2.put("PrintContent9", (String)json.get("PrintContent9"));
			params2.put("PrintContent10", (String)json.get("PrintContent10"));
			
			decCustomerMapper.updateCustomerPrintContent(params2);
			
			re.setErrorCode(0);
			re.setData("");
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询自定义打印内容出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();

	}	
		
	
}
