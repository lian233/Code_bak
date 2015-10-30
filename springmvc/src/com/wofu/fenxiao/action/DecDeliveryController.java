package com.wofu.fenxiao.action;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.wofu.fenxiao.domain.DecDelivery;
import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.domain.DistributeGoods;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecDeliveryService;
import com.wofu.fenxiao.mapping.DecDeliveryMapper;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.POIUtils;
import com.wofu.fenxiao.utils.Tools;

@Controller
public class DecDeliveryController extends BaseController{
	
	@Value("#{configProperties[server_port]}")
	private int port;
	
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private DecDeliveryService decDeliveryService;
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private MenuService menuService;

	@Autowired  //这里自动生成服务层组件对象
	private DecDeliveryMapper decDeliveryMapper;
	
	
	
	//查询客户快递单剩余数量
	//{customerId, customerName , deliverycode}
	@RequestMapping(value="qryCustomerDeliveryNum.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryCustomerDeliveryNum(HttpServletRequest request ,HttpServletResponse response) throws Exception{
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
				params2.put("customerId", (Integer)json.get("customerId"));
			}
			else{
				params2.put("customerId", curCustomerID);
			}
			
			//params2.put("customerId", (Integer)json.get("customerId"));
			params2.put("customerName", (String)json.get("customerName"));
			params2.put("deliverycode", (String)json.get("deliverycode"));
			
			PageView view = Tools.getPageView("CustomerID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
			
			
			List<HashMap> result = decDeliveryService.qryCustomerDeliveryNum(params2);
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询客户快递单剩余数量出错");
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//查询快递单记录流水
	//{customerID, deliveryID, beginTime,endTime}
	@RequestMapping(value="qryCustomerDeliveryNumBook.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryCustomerDeliveryNumBook(HttpServletRequest request ,HttpServletResponse response) throws Exception{
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
				if (json.get("customerID") != null ) {params2.put("customerID", (Integer)json.get("customerID"));}
			}
			else{
				params2.put("customerID", curCustomerID);
			}
			
			//params2.put("customerID", (Integer)json.get("customerID"));
			if (json.get("deliveryID") != null ) {params2.put("deliveryID", (Integer)json.get("deliveryID"));}
			params2.put("beginTime", (String)json.get("beginTime"));
			params2.put("endTime", (String)json.get("endTime"));
			params2.put("sheetType", (Integer)json.get("sheetType"));
			params2.put("routeFlag", (Integer)json.get("routeFlag"));
			params2.put("canReturn", (Integer)json.get("canReturn"));
			params2.put("outDays", (Integer)json.get("outDays"));
			
			params2.put("deliverySheetID", (String)json.get("deliverySheetID"));
			params2.put("address", (String)json.get("address"));
			params2.put("linkMan", (String)json.get("linkMan"));
			
			//logger.info((String)json.get("beginTime") +" " + (String)json.get("endTime"));
			
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数			
			
			List<HashMap> result = decDeliveryService.qryCustomerDeliveryNumBook(params2);
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询快递单记录流水出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
		
	//增加客户快递单数量
	//addCustomerDeliveryNum
	//输入  { customerID, deliveryID ,qty}
	@RequestMapping(value="addCustomerDeliveryNum.do",method=RequestMethod.POST)
	public @ResponseBody String addCustomerDeliveryNum(HttpServletRequest request,HttpServletResponse response){
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonResult re = new JsonResult();
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();
			map.put("ID", 0); 
			map.put("SheetType", 0);
			map.put("CustomerID", (Integer)json.get("customerID"));//
			map.put("DeliveryID", (Integer)json.get("deliveryID"));//
			map.put("Qty", (Integer)json.get("qty"));//
			map.put("Note", "");//Tools.getCurLoginCName(request.getSession()));
			
			String ret=decDeliveryService.addCustomerDeliveryNum(map);
			if (ret.equals("")){
				re.setErrorCode(0);
			}else{
				re.setErrorCode(10);
				re.setMsg(ret);
			}
			
			
		} catch (Exception e) {
			re.setErrorCode(1);
			re.setMsg("增加客户快递单数量出错:"+e.getMessage());			
			e.printStackTrace();
		}
		
		return JSONObject.fromObject(re).toString();
	}
	
	//页面 初始化数据
	//返回当前登录信息、菜单列表，快递列表数据。
	@RequestMapping(value="iniCustomerDeliveryData.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniCustomerDeliveryData(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 200400);
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
			
			//取得快递列表
			List<DecDelivery> delivery=null;
			try{
				DecDelivery d = new DecDelivery();
				d.setName("");
				delivery =decDeliveryService.queryDelivery(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			obj.put("delivery", delivery);
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
	
	//取得打印格式字段
	@RequestMapping(value="iniPrintDeliveryDesign.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniPrintDeliveryDesign(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		
		
		HashMap<String,String> login = null; 
		List<HashMap> menu = null;
		try{
			login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				login = new HashMap();
				login.put("ID", "1");
				login.put("Name", "system");
				login.put("CName", "系统管理员");
				login.put("CustomerID", "0");
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 400400);
						
			//取得当前菜单
			menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ //如果空，测试时取所有菜单 ，实际运行时报错
				try{
					menu = menuService.queryLoginMenu(1);
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
			obj.put("menu", menu);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//取得快递列表
		List<HashMap> delivery=null;
		try{
			HashMap<String,Object> p = new HashMap<String,Object>();
			delivery = decDeliveryService.qryDeliveryList(p);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		obj.put("delivery", delivery);
		
		logger.info("快递列表：" + delivery.toString());		
		
		//打印格式字段
		//List<HashMap> menu = new List<HashMap>();
		//JSONArray fieldGroup = new JSONArray();
		//HashMap<String,Object> Receiver = new HashMap<String,Object>();
		/*String js = "{\"fieldGroup\":[{\"name\":\"Sender\",\"fields\":[\"ShopName\",\"CustomerName\",\"CustomerLinkMan\",\"CustomerTele\",\"CustomerMobile\",\"CustomerAddress\",\"CustomerNote\"]},"
			+"{\"name\":\"Receiver\",\"fields\":[\"LinkMan\",\"Mobile\",\"Phone\",\"MobilePhone\",\"State\",\"City\",\"District\",\"Address\",\"DetailAddress\",\"BuyerNick\"]},"
			+"{\"name\":\"Order\",\"fields\":[\"RefSheetID\",\"ItemContent\",\"TotalQty\",\"Note\"]},"
			+"{\"name\":\"Delivery\",\"fields\":[\"AddressID\",\"DeliveryName\",\"DeliveryCode\",\"DeliverySheetID\",\"DeliveryNote\"]}"
			+"]}";*/

		
		String js = Tools.getPrintProp(); /*"{\"fieldGroup\":[{\"name\":\"Sender\",\"text\":\"发货人信息\",\"fields\":[\"ShopName\",\"CustomerName\",\"CustomerLinkMan\",\"CustomerTele\",\"CustomerMobile\",\"CustomerAddress\",\"CustomerNote\"],\"fieldsText\":[\"店铺\",\"发货人公司\",\"发货人联系人\",\"发货人电话\",\"发货人手机\",\"发货人地址\",\"发货人备注\"]},"
			+"{\"name\":\"Receiver\",\"text\":\"收货人信息\",\"fields\":[\"LinkMan\",\"Mobile\",\"Phone\",\"MobilePhone\",\"State\",\"City\",\"District\",\"Address\",\"DetailAddress\",\"BuyerNick\"],\"fieldsText\":[\"收货人姓名\",\"收货人手机\",\"收货人电话\",\"收货人手机电话\",\"收货人省份\",\"收货人市\",\"收货人区\",\"收货人地址\",\"收货人详细地址\",\"买家昵称\"]},"
			+"{\"name\":\"Order\",\"text\":\"订单信息\",\"fields\":[\"RefSheetID\",\"ItemContent\",\"TotalQty\",\"Note\"],\"fieldsText\":[\"订单号\",\"商品信息\",\"商品总数量\",\"备注\"]},"
			+"{\"name\":\"Delivery\",\"text\":\"快递信息\",\"fields\":[\"ZoneCode\",\"AddressID\",\"DeliveryName\",\"DeliveryCode\",\"DeliverySheetID\",\"DeliveryNote\"],\"fieldsText\":[\"目的地编码\",\"大头笔\",\"快递名称\",\"快递编码\",\"快递单号\",\"快递备注\"]}"
			+"]}";
			//+"],\"delivery\":"+delivery.toString()
			//+",\"menu\":"+menu.toString()+",\"login\":"+login.toString()+"}";
			//*/
		
		//String js = "{[{}]}";
		try{
			JSONObject json=JSONObject.fromObject(js);			
			//obj.put("fieldGroup", json);
		} catch(Exception e){
			logger.info("打印格式字段转化JSON出错");
			e.printStackTrace();
		}
		//Receiver.put("name", "Receiver");
		//Receiver.put("name", "Receiver");
		//JSONArray ReceiverGroup = new JSONArray();
		//fieldGroup.add(json);		
		
		String os = obj.toString();
		os=os.substring(0,os.length()-1);
		String ret="var allData ="+os+ ","+js.substring(1,js.length()) ; //obj.toString();
				
		return ret;

	}

/*
	//取得打印格式字段
	@RequestMapping(value="iniPrintDeliveryDesign.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniPrintDeliveryDesign(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		
		
		HashMap<String,String> login = null; 
		List<HashMap> menu = null;
		try{
			login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				login = new HashMap();
				login.put("ID", "1");
				login.put("Name", "system");
				login.put("CName", "系统管理员");
				login.put("CustomerID", "0");
			}
			obj.put("curLogin", login);
						
			//取得当前菜单
			menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ //如果空，测试时取所有菜单 ，实际运行时报错
				try{
					menu = menuService.queryLoginMenu(1);
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
			obj.put("menu", menu);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//取得快递列表
		List<HashMap> delivery=null;
		try{
			HashMap<String,Object> p = new HashMap<String,Object>();
			delivery = decDeliveryService.qryDeliveryList(p);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		obj.put("delivery", delivery);
		
		logger.info("快递列表：" + delivery.toString());		
		
		//打印格式字段
		//List<HashMap> menu = new List<HashMap>();
		//JSONArray fieldGroup = new JSONArray();
		//HashMap<String,Object> Receiver = new HashMap<String,Object>();
		/*String js = "{\"fieldGroup\":[{\"name\":\"Sender\",\"fields\":[\"ShopName\",\"CustomerName\",\"CustomerLinkMan\",\"CustomerTele\",\"CustomerMobile\",\"CustomerAddress\",\"CustomerNote\"]},"
			+"{\"name\":\"Receiver\",\"fields\":[\"LinkMan\",\"Mobile\",\"Phone\",\"MobilePhone\",\"State\",\"City\",\"District\",\"Address\",\"DetailAddress\",\"BuyerNick\"]},"
			+"{\"name\":\"Order\",\"fields\":[\"RefSheetID\",\"ItemContent\",\"TotalQty\",\"Note\"]},"
			+"{\"name\":\"Delivery\",\"fields\":[\"AddressID\",\"DeliveryName\",\"DeliveryCode\",\"DeliverySheetID\",\"DeliveryNote\"]}"
			+"]}";*/

		/*
		String js = "{\"fieldGroup\":[{\"name\":\"Sender\",\"text\":\"发货人信息\",\"fields\":[\"ShopName\",\"CustomerName\",\"CustomerLinkMan\",\"CustomerTele\",\"CustomerMobile\",\"CustomerAddress\",\"CustomerNote\"],\"fieldsText\":[\"店铺\",\"发货人公司\",\"发货人联系人\",\"发货人电话\",\"发货人手机\",\"发货人地址\",\"发货人备注\"]},"
			+"{\"name\":\"Receiver\",\"text\":\"收货人信息\",\"fields\":[\"LinkMan\",\"Mobile\",\"Phone\",\"MobilePhone\",\"State\",\"City\",\"District\",\"Address\",\"DetailAddress\",\"BuyerNick\"],\"fieldsText\":[\"收货人姓名\",\"收货人手机\",\"收货人电话\",\"收货人手机电话\",\"收货人省份\",\"收货人市\",\"收货人区\",\"收货人地址\",\"收货人详细地址\",\"买家昵称\"]},"
			+"{\"name\":\"Order\",\"text\":\"订单信息\",\"fields\":[\"RefSheetID\",\"ItemContent\",\"TotalQty\",\"Note\"],\"fieldsText\":[\"订单号\",\"商品信息\",\"商品总数量\",\"备注\"]},"
			+"{\"name\":\"Delivery\",\"text\":\"快递信息\",\"fields\":[\"AddressID\",\"DeliveryName\",\"DeliveryCode\",\"DeliverySheetID\",\"DeliveryNote\"],\"fieldsText\":[\"大头笔\",\"快递名称\",\"快递编码\",\"快递单号\",\"快递备注\"]}"
			+"]}";
			//+"],\"delivery\":"+delivery.toString()
			//+",\"menu\":"+menu.toString()+",\"login\":"+login.toString()+"}";
			//*/
		/*
		//String js = "{[{}]}";
		try{
			JSONObject json=JSONObject.fromObject(js);			
			//obj.put("fieldGroup", json);
		} catch(Exception e){
			logger.info("打印格式字段转化JSON出错");
			e.printStackTrace();
		}
		//Receiver.put("name", "Receiver");
		//Receiver.put("name", "Receiver");
		//JSONArray ReceiverGroup = new JSONArray();
		//fieldGroup.add(json);		
		
		String os = obj.toString();
		os=os.substring(0,os.length()-1);
		String ret="var allData ="+os+ ","+js.substring(1,js.length()) ; //obj.toString();
				
		return ret;

	}
 * */	
	

	
	//取得打印属性
	@RequestMapping(value="getFieldGroup.do",method=RequestMethod.POST)
	@ResponseBody 
	public String getFieldGroup(HttpSession session){		
		String js = Tools.getPrintProp(); /*"{\"fieldGroup\":[{\"name\":\"Sender\",\"text\":\"发货人信息\",\"fields\":[\"ShopName\",\"CustomerName\",\"CustomerLinkMan\",\"CustomerTele\",\"CustomerMobile\",\"CustomerAddress\",\"CustomerNote\"],\"fieldsText\":[\"店铺\",\"发货人公司\",\"发货人联系人\",\"发货人电话\",\"发货人手机\",\"发货人地址\",\"发货人备注\"]},"
			+"{\"name\":\"Receiver\",\"text\":\"收货人信息\",\"fields\":[\"LinkMan\",\"Mobile\",\"Phone\",\"MobilePhone\",\"State\",\"City\",\"District\",\"Address\",\"DetailAddress\",\"BuyerNick\"],\"fieldsText\":[\"收货人姓名\",\"收货人手机\",\"收货人电话\",\"收货人手机电话\",\"收货人省份\",\"收货人市\",\"收货人区\",\"收货人地址\",\"收货人详细地址\",\"买家昵称\"]},"
			+"{\"name\":\"Order\",\"text\":\"订单信息\",\"fields\":[\"RefSheetID\",\"ItemContent\",\"TotalQty\",\"Note\"],\"fieldsText\":[\"订单号\",\"商品信息\",\"商品总数量\",\"备注\"]},"
			+"{\"name\":\"Delivery\",\"text\":\"快递信息\",\"fields\":[\"ZoneCode\",\"AddressID\",\"DeliveryName\",\"DeliveryCode\",\"DeliverySheetID\",\"DeliveryNote\"],\"fieldsText\":[\"目的地编码\",\"大头笔\",\"快递名称\",\"快递编码\",\"快递单号\",\"快递备注\"]}"
			+"]}";*/
		
		return js;

	}
	
	//初始化打印设计器
	@RequestMapping(value="iniPrintDelivery.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniPrintDelivery(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		
		
		HashMap<String,String> login = null; 
		List<HashMap> menu = null;
		try{
			login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				login = new HashMap();
				login.put("ID", "1");
				login.put("Name", "system");
				login.put("CName", "系统管理员");
				login.put("CustomerID", "0");
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 400400);
						
			//取得当前菜单
			menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ //如果空，测试时取所有菜单 ，实际运行时报错
				try{
					menu = menuService.queryLoginMenu(1);
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
			obj.put("menu", menu);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//取得快递列表
		List<HashMap> delivery=null;
		try{
			HashMap<String,Object> p = new HashMap<String,Object>();
			delivery = decDeliveryService.qryDeliveryList(p);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		obj.put("delivery", delivery);
		
		String os = obj.toString();
		os=os.substring(0,os.length()-1);
		String ret=obj.toString();
				
		return ret;

	}
	
	//获取快递打印格式及打印属性
	@RequestMapping(value="getPrintDeliveryProp.do",method=RequestMethod.POST)
	public @ResponseBody String getPrintDeliveryProp(HttpServletRequest request,HttpServletResponse response) throws Exception{		
		JSONObject json=null;
		JsonResult re = new JsonResult();
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		String name = json.getString("name");		
		int customerID = Tools.getCurCustomerID(request.getSession());
		String data = Tools.getPrintFormat(customerID, name);
		String js = Tools.getPrintProp(); /*"{\"fieldGroup\":[{\"name\":\"Sender\",\"text\":\"发货人信息\",\"fields\":[\"ShopName\",\"CustomerName\",\"CustomerLinkMan\",\"CustomerTele\",\"CustomerMobile\",\"CustomerAddress\",\"CustomerNote\"],\"fieldsText\":[\"店铺\",\"发货人公司\",\"发货人联系人\",\"发货人电话\",\"发货人手机\",\"发货人地址\",\"发货人备注\"]},"
			+"{\"name\":\"Receiver\",\"text\":\"收货人信息\",\"fields\":[\"LinkMan\",\"Mobile\",\"Phone\",\"MobilePhone\",\"State\",\"City\",\"District\",\"Address\",\"DetailAddress\",\"BuyerNick\"],\"fieldsText\":[\"收货人姓名\",\"收货人手机\",\"收货人电话\",\"收货人手机电话\",\"收货人省份\",\"收货人市\",\"收货人区\",\"收货人地址\",\"收货人详细地址\",\"买家昵称\"]},"
			+"{\"name\":\"Order\",\"text\":\"订单信息\",\"fields\":[\"RefSheetID\",\"ItemContent\",\"TotalQty\",\"Note\"],\"fieldsText\":[\"订单号\",\"商品信息\",\"商品总数量\",\"备注\"]},"
			+"{\"name\":\"Delivery\",\"text\":\"快递信息\",\"fields\":[\"ZoneCode\",\"AddressID\",\"DeliveryName\",\"DeliveryCode\",\"DeliverySheetID\",\"DeliveryNote\"],\"fieldsText\":[\"目的地编码\",\"大头笔\",\"快递名称\",\"快递编码\",\"快递单号\",\"快递备注\"]}"
			+"]}";*/
		
		js = js.substring(0,js.length()-1) + ",\"prop\":"+data+"}";
		logger.info("输出 ："+js);
		re.setData(js);
		
		return JSONObject.fromObject(re).toString();
	}
	
	//获取快递打印格式
	@RequestMapping(value="getPrintDelivery.do",method=RequestMethod.POST)
	public @ResponseBody String getPrintDelivery(HttpServletRequest request,HttpServletResponse response) throws Exception{		
		JSONObject json=null;
		JsonResult re = new JsonResult();
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		String name = json.getString("name");		
		int customerID = Tools.getCurCustomerID(request.getSession());
		
		re.setData(Tools.getPrintFormat(customerID, name));
		
		return JSONObject.fromObject(re).toString();
	}
	
	//保存快递格式
	@RequestMapping(value="savePrintDelivery.do",method=RequestMethod.POST)
	public @ResponseBody String savePrintDelivery(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JsonResult result = new JsonResult();

		InputStreamReader input = new InputStreamReader(request.getInputStream(), "utf-8");
		BufferedReader bufReader = new BufferedReader(input);
		String line = "";
		StringBuilder contentBuf = new StringBuilder();
		while ((line = bufReader.readLine()) != null) {
			contentBuf.append(line);
		}
		//System.out.println(contentBuf.toString());
		
		String name = "";
		try{
			JSONObject json=JSONObject.fromObject(contentBuf.toString());
			name = json.getString("name");
		} catch(Exception e){
			result.setErrorCode(1);
			result.setMsg("取打印格式数据出错：" + e.getMessage());						
			//throw new Exception("取打印格式数据出错");
		}		
		
		
		
		//String fileName = request.getRealPath("./")+"js/"+ name+"_"+Tools.getCurCustomerID(request.getSession()) +".js";
		//String fileName = request.getRealPath("js/")+"\\"+ name+"_"+Tools.getCurCustomerID(request.getSession()) +".js";
		//String dirName = request.getRealPath("js/")+"\\"+Integer.toString(Tools.getCurCustomerID(request.getSession()))+"\\";
		String dirName = "e:/PrintFormat"+"\\"+Integer.toString(Tools.getCurCustomerID(request.getSession()))+"\\";
		String fileName = dirName+ name +".js";
		logger.info("保存的文件名：" + fileName);
		logger.info("内容：" + contentBuf.toString());
				
		try{			
			File f = new File(fileName);
			File d = new File(dirName);
			//if (!f.isDirectory()){
			d.mkdir();//创建文件夹
			//}

			
			FileOutputStream saveFile = new FileOutputStream(f);
			saveFile.write(contentBuf.toString().getBytes("utf-8"));
			saveFile.flush();
			saveFile.close();
			result.setErrorCode(0);
		}catch(Exception e){
			logger.info("保存文件出错：" + e.getMessage());
			result.setErrorCode(1);
			result.setMsg("保存文件出错：" + e.getMessage());			
		}
		
		
		return JSONObject.fromObject(result).toString();
		
	}	 
	
	//导入快递方案
	@RequestMapping(value="importDecDeliveryZone.do")
	public @ResponseBody String importDecDeliveryZone(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JsonResult result = new JsonResult();
		
		//获取普通表单数据
		Map<String,String[]> paramemap = request.getParameterMap();
		for(Iterator it = paramemap.keySet().iterator();it.hasNext();){
			String paramsName = (String)it.next();
			logger.info(paramsName+" "+paramemap.get(paramsName)[0]);
		}
		
		
		String group = paramemap.get("group")[0]; //request.getAttribute("group").toString();
		logger.info("取得group："+group);
		//取得快递分组
		List<HashMap> deliveryGroup=null;
		try{
			HashMap<String,String> p = new HashMap<String,String>();
			p.put("name", group);
			deliveryGroup = decDeliveryService.qryDeliveryGroupList(p);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		if (deliveryGroup.size()<=0){
			result.setErrorCode(1);
			result.setMsg("找不到快递套餐【"+group+"】");
			return JSONObject.fromObject(result).toString();
		}
		
		int deliveryGroupID = Integer.parseInt((deliveryGroup.get(0).get("ID").toString()));
		
		
		//获取文件
		MultipartHttpServletRequest req = (MultipartHttpServletRequest)request;
		MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();
		HashMap<String,Object> iresult = new HashMap<String,Object>();//导入结果
		Iterator it = map.keySet().iterator();
		for(;it.hasNext();){
			String filename = (String)it.next();
			//获取文件流
			List<MultipartFile> files =(List<MultipartFile>) map.get(filename);
			JSONArray resultList = new JSONArray(); //导入的结果
			for(int j=0;j<files.size();j++){
				MultipartFile file = files.get(j);
				if(!file.isEmpty()){
					try{
						//保存文件
						Tools.bakFile(request , file, "导入快递方案_");
						
						//快递列表
						List<HashMap> deliveryList = decDeliveryService.qryDeliveryList(new HashMap<String, Object> () );
						
							

						//解析excel文件
						HashMap<String,Integer> headList = new HashMap<String,Integer>();
						List<List<String>> data = POIUtils.getExcelData(file, file.getOriginalFilename(), 0);						
						//List<HashMap> list = new List<HashMap>();
						ArrayList list = new ArrayList();
						
						for(int row=0;row<data.size();row++){
							List<String> rowData = data.get(row);
							
							if (row==0){//第一行为列头，记录位置
								for(int col=0;col<rowData.size();col++){
									headList.put(rowData.get(col), col);
								}
							}
							else{//数据导入	
								HashMap<String, Object> params = new HashMap<String,Object>();
								
								String state = Tools.getListByName(rowData , headList ,"省");								
								if ((state.equals(""))) {
									continue;																		
								}
								String city = Tools.getListByName(rowData , headList ,"市");
								String district = Tools.getListByName(rowData , headList ,"区");
								String delivery = Tools.getListByName(rowData , headList ,"快递");
								
								//取快递 
								String temp = Tools.getListValue(deliveryList , "Name", delivery , "ID");
								if (temp==null){
									temp = Tools.getListValue(deliveryList , "Code", delivery , "ID");
								}
								
								if (temp == null){
									iresult.put("errorCode", 1);
									iresult.put("msg", "找不到快递【"+temp+"】");
									resultList.add(result);
									continue;																		
								}
								
								int deliveryID = Integer.parseInt(temp);
								params.put("deliveryGroupID", deliveryGroupID);
								params.put("deliveryID", deliveryID);
								params.put("state", state);
								params.put("city", city);
								params.put("district", district);
								params.put("status", 1);
								//deliveryGroupID
								list.add(params);								
							}
						}//end for 
						decDeliveryService.setDecDeliveryZone(deliveryGroupID,list);
						result.setErrorCode(0);
						result.setData(resultList);
					}catch(Exception e){
						logger.info("导入文件出错: "+e.getMessage());
						result.setErrorCode(1);
						result.setMsg("导入文件失败");
					}
					
				}
			}
		}
		return JSONObject.fromObject(result).toString();
	}

	//页面 初始化数据
	//返回当前登录信息、菜单列表，快递列表数据。
	@RequestMapping(value="iniDecDeliveryZone.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniDecDeliveryZone(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 200400);
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
			
			//取得快递列表
			List<DecDelivery> delivery=null;
			try{
				DecDelivery d = new DecDelivery();
				d.setName("");
				delivery =decDeliveryService.queryDelivery(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//取得快递分组
			List<HashMap> deliveryGroup=null;
			try{
				HashMap<String,String> p = new HashMap<String,String>();
				p.put("name", "");
				deliveryGroup = decDeliveryService.qryDeliveryGroupList(p);
			} catch (Exception e) {
				e.printStackTrace();
			}					
			
			obj.put("delivery", delivery);
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

	//查询快递区域
	//{ DeliveryGroupID (int), DeliveryID (int), State, City, District,Status(int)}
	@RequestMapping(value="queryDecDeliveryZone.do",method=RequestMethod.POST)
	@ResponseBody
	public String queryDecDeliveryZone(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("DeliveryGroupID", (Integer)json.get("DeliveryGroupID"));
			params2.put("DeliveryID", (Integer)json.get("DeliveryID"));
			params2.put("State", (String)json.get("State"));
			params2.put("City", (String)json.get("City"));
			params2.put("District", (String)json.get("District"));
			params2.put("Status", (Integer)json.get("Status"));
			
			PageView view = Tools.getPageView("State", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
			
			
			List<HashMap> result = decDeliveryMapper.queryDecDeliveryZone(params2);
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询快递区域出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
	
	
	//设置快递区域状态
	//setDecDeliveryZone.do.do{ DecDeliveryZones:[ DeliveryGroupID,DeliveryID,State,City,District],Status:int}
	@RequestMapping(value="setDecDeliveryZone.do",method=RequestMethod.POST)
	@ResponseBody
	public String setDecDeliveryZone(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		JsonResult re = new JsonResult();//
		try{
			JSONArray dd = json.getJSONArray("DecDeliveryZones");			
			if (dd==null){
				throw new Exception("找不到需设置的列表"); 
			}
			
			int status = json.getInt("Status");
			
			for(int i=0;i<dd.size();i++){								
				JSONObject d = dd.getJSONObject(i);
				HashMap<String, Object> params2 = new HashMap<String,Object>();
				
				params2.put("DeliveryGroupID", d.getInt("DeliveryGroupID"));
				params2.put("DeliveryID", d.getInt("DeliveryID"));
				params2.put("State", d.getString("State"));
				params2.put("City", d.getString("City"));
				params2.put("District", d.getString("District"));
				params2.put("Status", status);
				
				params2.put("DeliveryGroupID", d.getInt("DeliveryGroupID"));
								
				decDeliveryMapper.updateDecDeliveryZoneStatus(params2);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("设置快递区域状态出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}	
	
	//修改快递区域快递
	//modifyDecDeliveryZoneDelivery.do{ DecDeliveryZones:[ DeliveryGroupID,DeliveryID,State,City,District],DeliveryID:int}
	@RequestMapping(value="modifyDecDeliveryZoneDelivery.do",method=RequestMethod.POST)
	@ResponseBody
	public String modifyDecDeliveryZoneDelivery(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		JsonResult re = new JsonResult();//
		try{
			JSONArray dd = json.getJSONArray("DecDeliveryZones");			
			if (dd==null){
				throw new Exception("找不到需设置的列表"); 
			}
			
			int deliveryID = json.getInt("DeliveryID");
			
			for(int i=0;i<dd.size();i++){								
				JSONObject d = dd.getJSONObject(i);
				HashMap<String, Object> params2 = new HashMap<String,Object>();
				
				params2.put("DeliveryGroupID", d.getInt("DeliveryGroupID"));
				//params2.put("DeliveryID", d.getInt("DeliveryID"));
				params2.put("State", d.getString("State"));
				params2.put("City", d.getString("City"));
				params2.put("District", d.getString("District"));
				params2.put("DeliveryID", deliveryID);
				
				decDeliveryMapper.updateDecDeliveryZoneDelivery(params2);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("设置快递区域状态出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}	
	
	//导出快递区域
	@RequestMapping(value="exportDecDeliveryZone.do")
	public @ResponseBody String  exportDecDeliveryZone(HttpServletRequest request,HttpServletResponse response){
		JsonResult result = new JsonResult();
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		String ret ;
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("DeliveryGroupID", (Integer)json.get("DeliveryGroupID"));
			params2.put("DeliveryID", (Integer)json.get("DeliveryID"));
			params2.put("State", (String)json.get("State"));
			params2.put("City", (String)json.get("City"));
			params2.put("District", (String)json.get("District"));
			params2.put("Status", (Integer)json.get("Status"));
			List<HashMap> data = decDeliveryMapper.queryDecDeliveryZone(params2);
			
			//输出
			String header = "快递套餐,快递,省份,城市,区,接收范围,不接收范围,部分接收范围,接收范围扩展,状态";
			String fields = "DeliveryGroupName,DeliveryName,State,City,District,ReceivedZone,NoReceivedZone,PartReceivedZone,NoReceivedZoneEx,Status";
			String tempfile = request.getRealPath("/temp/"+"快递区域"+".xls");
			ret= POIUtils.exportToExcelHeadJxlMap(tempfile,response,request,"快递区域",data,fields,header,port);
			
			result.setErrorCode(0);			
			result.setData(ret);
		}catch(Exception e){
			logger.info("导出快递区域文件出错"+e.getMessage());
			result.setErrorCode(1);
			result.setMsg("导出快递区域文件出错"+e.getMessage());
		}
		
		logger.info("导出返回："+JSONObject.fromObject(result).toString());
		return JSONObject.fromObject(result).toString();
	}	

	//查询快递使用统计
	//{customerName , deliveryID,beginTime,endTime}
	@RequestMapping(value="qryDeliverySta.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDeliverySta(HttpServletRequest request ,HttpServletResponse response) throws Exception{
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
			
			params2.put("DeliveryID", (Integer)json.get("DeliveryID"));
			params2.put("BeginTime", (String)json.get("BeginTime"));
			params2.put("EndTime", (String)json.get("EndTime")+" 23:59:59");
			
			if (params2.get("DeliveryID")==null){
				params2.put("DeliveryID", "null");
			}

			if (params2.get("CustomerID")==null){
				params2.put("CustomerID", "null");
			}
			
			//PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
			//		json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			//params2.put("pageview",view);//分页参数
			
			logger.info("准备执行");
			List<HashMap> result = decDeliveryMapper.stCustomerDeliveryNumSta(params2);
			logger.info("执行完成");
			re.setErrorCode(0);
			re.setData(result);
			//view.setPage(view.getPage()-1);
			//re.setPageInfo(view);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询快递使用统计出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
		
	//查询快递跟单汇总
	//{ BeginTime, EndTime }
	@RequestMapping(value="qryDeliveryTraceSta.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDeliveryTraceSta(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			
			params2.put("BeginTime", (String)json.get("BeginTime"));
			params2.put("EndTime", (String)json.get("EndTime")+" 23:59:59");
			
			//PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
			//		json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			//params2.put("pageview",view);//分页参数
			
			List<HashMap> result = decDeliveryMapper.qryDeliveryTraceSta(params2);
			re.setErrorCode(0);
			re.setData(result);
			//view.setPage(view.getPage()-1);
			//re.setPageInfo(view);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询快递跟单汇总出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//查询快递跟单
	//{ BeginTime, EndTime, DeliveryID, DeliverySheetID,Station,Address,LinkMan,IsFinish,IsOverTime}
	@RequestMapping(value="qryDeliveryTrace.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDeliveryTrace(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			
			params2.put("BeginTime", (String)json.get("BeginTime"));
			params2.put("EndTime", (String)json.get("EndTime")+" 23:59:59");
			params2.put("DeliveryID", (Integer)json.get("DeliveryID"));
			params2.put("DeliverySheetID", (String)json.get("DeliverySheetID"));
			params2.put("Position", (String)json.get("Position"));
			params2.put("Address", (String)json.get("Address"));
			params2.put("LinkMan", (String)json.get("LinkMan"));
			params2.put("IsFinish", (Integer)json.get("IsFinish"));
			params2.put("IsOverTime", (Integer)json.get("IsOverTime"));
			
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
			
			List<HashMap> result = decDeliveryMapper.qryDeliveryTrace(params2);
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询快递跟单出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//导入快递回收
	@RequestMapping(value="importReturnDelivery.do")
	public @ResponseBody String importReturnDelivery(HttpServletRequest request,HttpServletResponse response) throws Exception{
		System.out.println("进入importReturnDelivery");
		JsonResult result = new JsonResult();
		
		//获取普通表单数据
		Map<String,String[]> paramemap = request.getParameterMap();
		for(Iterator it = paramemap.keySet().iterator();it.hasNext();){
			String paramsName = (String)it.next();
			logger.info(paramsName+" "+paramemap.get(paramsName)[0]);
		}
		
		
		//获取文件
		MultipartHttpServletRequest req = (MultipartHttpServletRequest)request;
		MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();
		HashMap<String,Object> iresult = new HashMap<String,Object>();//导入结果
		Iterator it = map.keySet().iterator();
		JSONArray resultList = new JSONArray(); //导入的结果
		for(;it.hasNext();){
			String filename = (String)it.next();
			//获取文件流
			List<MultipartFile> files =(List<MultipartFile>) map.get(filename);			
			for(int j=0;j<files.size();j++){
				MultipartFile file = files.get(j);
				if(!file.isEmpty()){
					try{
						//保存文件
						Tools.bakFile(request , file, "导入快递回收_");
						
						//快递列表
						List<HashMap> deliveryList = decDeliveryService.qryDeliveryList(new HashMap<String, Object> () );
						
						
						//解析excel文件
						HashMap<String,Integer> headList = new HashMap<String,Integer>();
						List<List<String>> data = POIUtils.getExcelData(file, file.getOriginalFilename(), 0);						
						//List<HashMap> list = new List<HashMap>();
						ArrayList list = new ArrayList();
						
						for(int row=0;row<data.size();row++){
							List<String> rowData = data.get(row);
							
							if (row==0){//第一行为列头，记录位置
								for(int col=0;col<rowData.size();col++){
									headList.put(rowData.get(col), col);
								}
							}
							else{//数据导入	
								HashMap<String, Object> params = new HashMap<String,Object>();
								
								String deliverySheetID = Tools.getListByName(rowData , headList ,"快递单号");
								if ((deliverySheetID.equals(""))) {
									iresult.put("errorCode", 3);
									iresult.put("msg", "快递单号为空");
									resultList.add(iresult);
									continue;																		
								}
								
								//取快递
								String delivery = Tools.getListByName(rowData , headList ,"快递");								
								if ((delivery.equals(""))) {
									iresult.put("errorCode", 2);
									iresult.put("msg", "快递为空");
									resultList.add(iresult);
									continue;																		
								}
								
								String temp = Tools.getListValue(deliveryList , "ID", delivery , "ID");
								if (temp==null){
									temp = Tools.getListValue(deliveryList , "Code", delivery , "ID");
								}
								
								if (temp == null){
									iresult.put("errorCode", 1);
									iresult.put("msg", "找不到快递【"+temp+"】");
									resultList.add(iresult);
									continue;																		
								}
								
								int deliveryID = Integer.parseInt(temp);
								
								try{
									System.out.println("回收：" +  deliverySheetID);
									String ret = decDeliveryService.returnDelivery(deliveryID, deliverySheetID);
									if (ret!=""){
										iresult.put("errorCode", 3);
										iresult.put("msg", ret);	
										resultList.add(iresult);
									}
								}catch(Exception ex){
									System.out.println("回收失败：" +  ex.getMessage());
									iresult.put("errorCode", 2);
									iresult.put("msg", ex.getMessage());	
									resultList.add(iresult);
								}
								
							}
						}//end for 
						result.setErrorCode(0);
						result.setData(resultList);
					}catch(Exception e){
						logger.info("导入文件出错: "+e.getMessage());
						result.setErrorCode(100);
						result.setMsg("导入文件失败");
					}
					
				}
			}
		}
		return JSONObject.fromObject(result).toString();
	}	

	//导出快递流水
	@RequestMapping(value="exportCustomerDeliveryNumBook.do")
	public @ResponseBody String  exportCustomerDeliveryNumBook(HttpServletRequest request,HttpServletResponse response){
		JsonResult result = new JsonResult();
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		String ret ;
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();

			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			if (curCustomerID==0)
			{
				if (json.get("customerID") != null ) {params2.put("customerID", (Integer)json.get("customerID"));}
			}
			else{
				params2.put("customerID", curCustomerID);
			}
			
			//params2.put("customerID", (Integer)json.get("customerID"));
			if (json.get("deliveryID") != null ) {params2.put("deliveryID", (Integer)json.get("deliveryID"));}
			params2.put("beginTime", (String)json.get("beginTime"));
			params2.put("endTime", (String)json.get("endTime"));
			params2.put("sheetType", (Integer)json.get("sheetType"));
			params2.put("routeFlag", (Integer)json.get("routeFlag"));
			params2.put("canReturn", (Integer)json.get("canReturn"));
			params2.put("outDays", (Integer)json.get("outDays"));
			
			params2.put("deliverySheetID", (String)json.get("deliverySheetID"));
			params2.put("address", (String)json.get("address"));
			params2.put("linkMan", (String)json.get("linkMan"));
						
			List<HashMap> data = decDeliveryService.qryCustomerDeliveryNumBook(params2);
			
			//输出
			String header = "ID,客户,快递,客户名称,快递名称,快递编码,数量,剩余数量,时间,备注,快递单号,路由标志,路由开始时间,路由结束时间,省,市,区,地址,联系人,手机";
			String fields = "ID,CustomerID,DeliveryID,CustomerName,DeliveryName,DeliveryCode,Qty,CloseQty,STime,Note,DeliverySheetID,RouteFlag,BegingRouteTime,EndRouteTime,State,City,District,Address,LinkMan,Mobile";
			String tempfile = request.getRealPath("/temp/"+"快递流水"+".xls");
			ret= POIUtils.exportToExcelHeadJxlMap(tempfile,response,request,"快递流水",data,fields,header,port);
			
			result.setErrorCode(0);			
			result.setData(ret);
		}catch(Exception e){
			logger.info("导出快递流水文件出错"+e.getMessage());
			result.setErrorCode(1);
			result.setMsg("导出快递流水文件出错"+e.getMessage());
		}
		
		logger.info("导出返回："+JSONObject.fromObject(result).toString());
		return JSONObject.fromObject(result).toString();
	}	
	
}
