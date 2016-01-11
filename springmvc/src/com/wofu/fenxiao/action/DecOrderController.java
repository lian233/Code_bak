package com.wofu.fenxiao.action;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.DecDelivery;
import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.domain.DecOrderItem;
import com.wofu.fenxiao.domain.DistributeGoods;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.mapping.DistributeGoodsMapper;
import com.wofu.fenxiao.mapping.DecOrderMapper;

import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecCustomerService;
import com.wofu.fenxiao.service.DecOrderService;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.service.DecShopService;
import com.wofu.fenxiao.service.DecDeliveryService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.JsonDateValueProcessor;
import com.wofu.fenxiao.utils.POIUtils;
import com.wofu.fenxiao.utils.Tools;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.wofu.fenxiao.service.DistributeGoodsService;


 
@Controller
public class DecOrderController extends BaseController{
	@Value("#{configProperties[server_port]}")
	private int port;
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private DecOrderService decOrderService;
	
	@Autowired  //这里自动生成服务层组件对象
	private MenuService menuService;

	@Autowired  //这里自动生成服务层组件对象
	private DecShopService decShopService;

	@Autowired  //这里自动生成服务层组件对象
	private DecDeliveryService decDeliveryService;
	
	@Autowired  //这里自动生成服务层组件对象
	private LoginService loginService;	
		
	@Autowired  //这里自动生成服务层组件对象
	private DecCustomerService decCustomerService;
			
	
	@Autowired  //这里自动生成服务层组件对象
	private DistributeGoodsService distributeGoodsService;
	
	@Autowired
	private DistributeGoodsMapper distributeGoodsMapper;
	
	@Autowired
	private DecOrderMapper decOrderMapper;
	//菜鸟物流参数
	@Value("#{configProperties[app_key]}")
	private String app_key;
	@Value("#{configProperties[app_secret]}")
	private String app_secret;
	@Value("#{configProperties[cainiao_token]}")
	private String cainiao_token;
	@Value("#{configProperties[cainiao_user_id]}")
	private String cainiao_user_id;
	@Value("#{configProperties[cainiao_url]}")
	private String cainiao_url;
	
	//订单处理页面 初始化数据
	//返回当前登录信息、菜单列表，店铺列表、快递列表数据。
	@RequestMapping(value="iniDecSheetData.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniDecSheetData(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 400100);
			//String loginStr = "curLogin:"+JSONObject.fromObject(login).toString();
			
			
			//取得当前菜单
			List<HashMap> menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ //如果空，测试时取所有菜单 ，实际运行时报错
				try{
					menu = menuService.queryLoginMenu(1);
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
			obj.put("menu", menu);
			
			//取得客户店铺
			List<HashMap> shop=null;
			try{
				HashMap<String,Object> p = new HashMap<String,Object>();
				p.put("customerID", Tools.getCurCustomerID(session));
				shop = decShopService.qryShopList(p);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			obj.put("shop", shop);

			//取得快递
			List<HashMap> delivery=null;
			try{
				HashMap<String,Object> p = new HashMap<String,Object>();
				delivery = decDeliveryService.qryDeliveryList(p);
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
	
	//查询订单数据
	/* {shopID,deliveryID,flag,range,key,
		refsheetid, sellerFlag , buyerNick, linkman, phone,
		mobile,state,address,deliverySheetID,goodsName,
		title,outerSkuID,note, buyerMemo, sellerMemo, 
		buyerMessage, tradeMemo, color,size,itemCount,
		itemQty,postFee,totalAmount,tradeFrom, invoiceFlag ,
		payMode,sheetID}
	*/
	@RequestMapping(value="qryDecOrder.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDecOrder(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		System.out.println("准备检查登录");
		try{
			Tools.checkLogin(request.getSession(), response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("检查登录成功");
		
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int breakPoint = 1; 
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("front", Tools.getFrontStr(request.getSession()));
			if (null==json.get("bak")){
				params2.put("bak", "0");
			}else{
				params2.put("bak", (String)json.get("bak"));
			}
						
			params2.put("customerID", Tools.getCurCustomerID(request.getSession()));
			params2.put("shopID", (Integer)json.get("shopID"));
			//params2.put("deliveryID", (Integer)json.get("deliveryID"));
			if (json.containsKey("deliveryID")){
				logger.info("包括快递条件");
				if (json.get("deliveryID")!=null && !json.get("deliveryID").equals("null")){
					logger.info("快递非空" + json.get("deliveryID").toString());
					params2.put("deliveryID", (Integer)json.get("deliveryID"));
				}
			}
			logger.info("快递条件OK");
			breakPoint = 40; 
			
			params2.put("flag", (Integer)json.get("flag"));
			if (json.containsKey("flag")){
				if (json.getInt("flag")<97){params2.put("bak", "0");}
				else {params2.put("bak", "");}
			}

			if (json.containsKey("timeType")){
				params2.put("timeType", (Integer)json.get("timeType"));				
			}
			if (!params2.containsKey("timeType")){
				params2.put("timeType",1);
			}
			
			if (json.containsKey("beginTime")){
				params2.put("begintime", (String)json.get("beginTime")+":00");//付款时间段
				logger.info("取得开始："+ params2.get("begintime").toString());
			}
			
			if (json.containsKey("endTime")){
				params2.put("endtime", (String)json.get("endTime")+":00");//付款时间段
			}
						
			params2.put("range", (Integer)json.get("range"));			
			params2.put("key", (String)json.get("key"));
			params2.put("refsheetid", (String)json.get("refsheetid"));
			params2.put("sellerFlag", (Integer)json.get("sellerFlag"));
			params2.put("inDays", (Integer)json.get("inDays"));
			 
			params2.put("buyerNick", (String)json.get("buyerNick"));
			params2.put("linkman", (String)json.get("linkman"));
			params2.put("phone", (String)json.get("phone"));
			params2.put("mobile", (String)json.get("mobile"));
			params2.put("state", (String)json.get("state"));
			params2.put("address", (String)json.get("address"));
			params2.put("deliverySheetID", (String)json.get("deliverySheetID"));
			params2.put("goodsName", (String)json.get("goodsName"));
			params2.put("title", (String)json.get("title"));
			params2.put("outerSkuID", (String)json.get("outerSkuID"));
			params2.put("note", (String)json.get("note"));
			params2.put("buyerMemo", (String)json.get("buyerMemo"));
			params2.put("sellerMemo", (String)json.get("sellerMemo"));
			params2.put("buyerMessage", (String)json.get("buyerMessage"));
			params2.put("tradeMemo", (String)json.get("tradeMemo"));
			params2.put("color", (String)json.get("color"));
			params2.put("size", (String)json.get("size"));
			params2.put("itemCount", (Integer)json.get("itemCount"));
			params2.put("itemQty", (Integer)json.get("itemQty"));
			if (json.containsKey("postFee")){
				params2.put("postFee", Double.parseDouble(json.get("postFee").toString()));
			}
			if (json.containsKey("totalAmount")){
				params2.put("totalAmount", Double.parseDouble(json.get("totalAmount").toString()));
			}
			
			params2.put("invoiceFlag", (Integer)json.get("invoiceFlag"));
			params2.put("payMode", (Integer)json.get("payMode"));
			params2.put("tradeFrom", (String)json.get("tradeFrom"));
			params2.put("sheetID", (String)json.get("sheetID"));
			
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
			
			
			List<DecOrder> result = decOrderService.qryDecOrder(params2);						
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
			
			//记录当时的订单列表
			if (json.containsKey("query")){
				if (json.getInt("query")==1){
					params2.remove("pageview");
					List<DecOrder> result2 = decOrderService.qryDecOrder(params2);					
					
					Tools.setOrderList(request.getSession(), result2);
					
					//test
					//List<DecOrder> os = (List<DecOrder>)request.getSession().getAttribute("curOrder");
					//logger.info("取得size"+os.size());
					/*
					List<DecOrder> os = (List<DecOrder>)request.getSession().getAttribute("curOrder");
					if (null==os){
						throw new Exception("找不到订单列表");
					}else{
						logger.info("取得size"+os.size());
						for(Iterator it = os.iterator();it.hasNext();){
							DecOrder d = (DecOrder)it.next();
							logger.info("取得ID"+d.getId());
						}
					}*/
				}
			}
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询订单出错");
			logger.info("查询订单出错："+ breakPoint +" "+ e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
			
	
	@RequestMapping(value="qryDecOrderItem.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDecOrderItem(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("front", Tools.getFrontStr(request.getSession()));
			if (null==json.get("bak")){
				params2.put("bak", "0");
			}else{
				params2.put("bak", (String)json.get("bak"));
			}			
			
			if (json.containsKey("flag")){
				if (json.getInt("key")<97){params2.put("bak", "0");}
				else {params2.put("bak", "");}
			}			
			params2.put("sheetID", (String)json.get("sheetID"));			
			
			List<DecOrderItem> result = decOrderService.qryDecOrderItem(params2);
			re.setErrorCode(0);
			re.setData(result);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询订单明细出错");
			logger.info("查询订单明细出错："+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//保存订单数据
	//输入r{orders:[ID, RefSheetID, ShopID,DeliveryID, Note,BuyerNick,State,City,District,Address,Phone,Mobile,LinkMan, PostFee, PayFee,PayMode,InvoiceFlag,InvoiceTitle,InvoiceID,Note]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveDecOrder.do",method=RequestMethod.POST)
	public @ResponseBody String saveDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("订单【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray orders = json.getJSONArray("orders");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		try{
			for(int i=0;i<orders.size();i++){
				JSONObject order = orders.getJSONObject(i);
				
				DecOrder c = new DecOrder();
				c.setId(order.getInt("ID"));
				if (order.size()>=2){//
					if (order.containsKey("refSheetID")) {c.setRefSheetID(order.getString("refSheetID"));}
					if (order.containsKey("shopID")) {c.setShopID(order.getInt("shopID"));} else{c.setShopID(-1);}
					if (order.containsKey("deliveryID")) {c.setDeliveryID(order.getInt("deliveryID"));} else{c.setDeliveryID(-1);}
					if (order.containsKey("deliverySheetID")) {c.setDeliverySheetID(order.getString("deliverySheetID"));}					
					if (order.containsKey("buyerNick")) {c.setBuyerNick(order.getString("buyerNick"));}
					if (order.containsKey("state")) {c.setState(order.getString("state"));}
					if (order.containsKey("district")) {c.setDistrict(order.getString("district"));}
					if (order.containsKey("city")) {c.setCity(order.getString("city"));}
					if (order.containsKey("address")) {c.setAddress(order.getString("address"));}
					if (order.containsKey("phone")) {c.setPhone(order.getString("phone"));}
					if (order.containsKey("mobile")) {c.setMobile(order.getString("mobile"));}
					if (order.containsKey("linkMan")) {c.setLinkMan(order.getString("linkMan"));}
					if (order.containsKey("postFee")) {c.setPostFee(order.getDouble("postFee"));}
					if (order.containsKey("payFee")) {c.setPayFee(order.getDouble("payFee"));}
					if (order.containsKey("payMode")) {c.setPayMode(order.getInt("payMode"));}
					if (order.containsKey("invoiceFlag")) {c.setInvoiceFlag(order.getInt("invoiceFlag"));}
					if (order.containsKey("invoiceTitle")) {c.setInvoiceTitle(order.getString("invoiceTitle"));}
					if (order.containsKey("invoiceID")) {c.setInvoiceID(order.getString("invoiceID"));}
					if (order.containsKey("note")) {c.setNote(order.getString("note"));}
					
					/*
					c.setRefSheetID(order.getString("RefSheetID"));
					c.setShopID(order.getInt("ShopID"));
					c.setDeliveryID(order.getInt("DeliveryID"));
					c.setBuyerNick(order.getString("BuyerNick"));
					c.setState(order.getString("State"));
					c.setCity(order.getString("City"));
					c.setDistrict(order.getString("District"));
					c.setAddress(order.getString("Address"));
					c.setPhone(order.getString("Phone"));
					c.setMobile(order.getString("Mobile"));
					c.setLinkMan(order.getString("LinkMan"));
					c.setPostFee(order.getDouble("PostFee"));
					c.setPayFee(order.getDouble("PayFee"));
					c.setPayMode(order.getInt("PayMode"));
					c.setInvoiceFlag(order.getInt("InvoiceFlag"));
					c.setInvoiceTitle(order.getString("InvoiceTitle"));
					c.setInvoiceID(order.getString("InvoiceID"));
					c.setNote(order.getString("Note"));	 
					 */					
				}
				
				c.setFront(Tools.getFrontStr(request.getSession()));
				
				
				if (order.getInt("ID")==-1){//增加
					//取得最大的ID
					c.setCustomerID(Tools.getCurCustomerID(request.getSession()));
					c.setCreateTime(new Date());
					c.setEditTime(new Date());
					c.setPayTime(new Date());
					c.setSheetFlag(2);//手工制单
					c.setEditor(Tools.getCurLoginCName(request.getSession()));//登录人
					int newid = loginService.GetNewID(400101);
					c.setId(newid);
					
					//取得单号
					String sheetid = loginService.GetNewSheetID(400100);
					c.setSheetID(sheetid);
					
					decOrderService.add(c);
					
					HashMap<String,Object> idmap = new HashMap<String,Object>();
					idmap.put("ID", newid);
					idmap.put("SheetID", sheetid);
					idList.add(idmap);	
										
				}else{
					if (order.size()<=1){//删除 
						//decOrderService.delete(c.getId());
						decOrderService.delete(c);
					}
					else{//修改
						if (!order.containsKey("deliveryID")) {c.setDeliveryID(-1);}
						if (!order.containsKey("invoiceFlag")) {c.setInvoiceFlag(-1);}
						if (!order.containsKey("weigh")) {c.setWeigh(-1);}
						if (!order.containsKey("mark")) {c.setMark(-1);}
						if (!order.containsKey("printTimes")) {c.setPrintTimes(-1);}
						
						c.setEditTime(new Date());
						c.setEditor(Tools.getCurLoginCName(request.getSession()));//登录人						
						decOrderService.update(c);
					}
				}				
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存订单数据出错："+e.getMessage());
			logger.info("保存订单数据出错："+e.getMessage());
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}
			
	
	//保存订单明细数据
	//输入{orderItems:[ID, SheetID, OuterSkuID, Title, SkuPropertiesName, CustomPrice, PurQty,Note]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveDecOrderItem.do",method=RequestMethod.POST)
	public @ResponseBody String saveDecOrderItem(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("订单明细【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray items = json.getJSONArray("orderItems");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		try{
			for(int i=0;i<items.size();i++){
				JSONObject item = items.getJSONObject(i);
				
				DecOrderItem c = new DecOrderItem();
				c.setId(item.getInt("ID"));
				if (item.size()>=2){//
					if (item.containsKey("SheetID")) {c.setSheetID(item.getString("SheetID"));}
					if (item.containsKey("OuterSkuID")) {c.setOuterSkuID(item.getString("OuterSkuID"));}
					if (item.containsKey("Title")) {c.setTitle(item.getString("Title"));}
					if (item.containsKey("SkuPropertiesName")) {c.setSkuPropertiesName(item.getString("SkuPropertiesName"));}
					if (item.containsKey("CustomPrice")) {c.setCustomPrice(item.getDouble("CustomPrice"));}
					if (item.containsKey("PurQty")) {c.setPurQty(item.getInt("PurQty"));}
					if (item.containsKey("Note")) {c.setNote(item.getString("Note"));}
										
					/*
					c.setSheetID(item.getString("SheetID"));
					c.setOuterSkuID(item.getString("OuterSkuID"));
					c.setTitle(item.getString("Title"));
					c.setSkuPropertiesName(item.getString("SkuPropertiesName"));
					c.setTitle(item.getString("Title"));
					c.setCustomPrice(item.getDouble("CustomPrice"));
					c.setPurQty(item.getInt("PurQty"));
					c.setNote(item.getString("Note"));	*/				
				}
								
				c.setFront(Tools.getFrontStr(request.getSession()));
				int customerID = Tools.getCurCustomerID(request.getSession()); 
				String sheetID = c.getSheetID();
				if (item.getInt("ID")==-1){//增加
					if (!item.containsKey("SheetID")){
						throw new Exception("没有输入订单编号"); 
					}
					//取得最大的ID
					int newid = loginService.GetNewID(400102); 
					c.setId(newid);	
					decOrderService.addItem(c);	
					idList.add(newid);
				}else{
					if (item.size()<=1){//删除 
						DecOrderItem oi = decOrderService.getItemById(c.getId() , "0" ,  c.getFront());
						if (oi!=null){sheetID = oi.getSheetID();}						
						//decOrderService.deleteItem(c.getId());
						decOrderService.deleteItem(c);						
					}
					else{//修改
						DecOrderItem oi = decOrderService.getItemById(c.getId() , "0" ,  c.getFront());	
						if (oi!=null){sheetID = oi.getSheetID();}
						decOrderService.updateItem(c);
					}
				}
				
				//刷新单头信息
				if (sheetID!=null){
					decOrderService.setDecKeyPicNote(sheetID,customerID);
				}
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存订单数据出错："+e.getMessage());
			logger.info("保存订单数据出错");
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}
	
	//确认订单    
	@RequestMapping(value="confirmDecOrder.do")
	public @ResponseBody String confirmDecOrder(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray checkResult =null;//= new JSONArray() ;
		JsonResult re = new JsonResult();
		try {
			//取得订单列表
			JSONArray ids = null;
			if (json.containsKey("orders")){
				ids = json.getJSONArray("orders");
				logger.info("有orders参数："+ids.toString());
			}else{
				//取session
				ids = new JSONArray();
				//test
				List<DecOrder> os1 = (List<DecOrder>)request.getSession().getAttribute("curOrder");
				if (null==os1){
					throw new Exception("找不到订单列表");
				}else{					
					logger.info("取得size"+os1.size());
					for(Iterator it = os1.iterator();it.hasNext();){
						DecOrder d = (DecOrder)it.next();
						ids.add(d.getId());						
					}
				}
			}
			logger.info("取得订单列表数：" + ids.size());
			logger.info("取得订单列表：" + ids.toString());
			
			if (ids.size()<=0){
				throw new Exception("没有订单数量");
			}
			
				try{
					checkResult=decOrderService.confirmDecOrders(ids,session,app_key,
							app_secret,cainiao_token,cainiao_user_id,cainiao_url);
					logger.info("订单确认返回："+ checkResult.toString());
					//result.put("errorCode", 0);
				}catch (Exception e) { 
				}
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(1)	;
			re.setMsg("确认订单失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
		re.setData(checkResult);
		return JSONObject.fromObject(re).toString();
	}	
	
	
	//修改订单快递    
	@RequestMapping(value="modifyDelivey.do")
	public @ResponseBody String modifyDelivey(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray checkResult =null;//= new JSONArray() ;
		//
		
		JsonResult re = new JsonResult();
		try {
			//取得订单列表
			JSONArray ids =  json.getJSONArray("orders");
						
			if (ids.size()<=0){
				throw new Exception("没有订单数量");
			}
			
			//修改快递
			String idss = "";
			for(int i=0;i<ids.size();i++){
				if (idss.equals("")){
					idss=Integer.toString(ids.getInt(i));
				}
				else{
					idss=idss + ","+Integer.toString(ids.getInt(i));
				}
			}
			
			int deliveryID = json.getInt("deliveryID");
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("front", Tools.getFrontStr(request.getSession()));
			params2.put("idList", idss);
			params2.put("deliveryID", deliveryID);
			decOrderMapper.modifyDelivey(params2);
			
			//取快递单号
			int isCreate = json.getInt("isCreate");
			if (isCreate==1){
				try{
					checkResult=decOrderService.confirmDecOrders(ids,session,app_key,
							app_secret,cainiao_token,cainiao_user_id,cainiao_url);
					logger.info("修改快递取单号返回："+ checkResult.toString());
				}catch (Exception e) { 
				}
			}
				
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(1)	;
			re.setMsg("修改快递失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
				
		re.setData(checkResult);
		return JSONObject.fromObject(re).toString();
	}	
		
	
	//订单发货    
	@RequestMapping(value="sendDecOrder.do")
	public @ResponseBody String sendDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray checkResult = new JSONArray() ;
		JsonResult re = new JsonResult();
		//try {
			//取得订单列表
			JSONArray ids = json.getJSONArray("orders");
			//for(int i=0;i<ids.size();i++){
				//HashMap<String,Object>  result = new HashMap<String,Object> ();
			//	try{
				//	result.put("ID", ids.getInt(i));					
					//decOrderService.CheckOrder(request.getSession(), ids.getInt(i), 100);
					checkResult = decOrderService.CheckOrder(request.getSession(), ids);
				//}catch (Exception e) { 
					//result.put("errorCode", 1);
					//result.put("msg", "订单发货【"+Integer.toString(ids.getInt(i))+"】失败:" + e.getMessage());
					//logger.info("订单发货【"+Integer.toString(ids.getInt(i))+"】失败：" + e.getMessage());
				//}
				//checkResult.add(result);				
			//}
			//re.setErrorCode(0);
		//} catch (Exception e) { 
			//re.setErrorCode(1)	;
			//re.setMsg("订单发货失败【"+e.getMessage() +"】");	
			//e.printStackTrace();
		//}
				
		re.setData(checkResult);
		logger.info("发货返回："+JSONObject.fromObject(re).toString());
		return JSONObject.fromObject(re).toString();
	}		
	
	//根据订单列表打印订单    
	//把列表写入到session里
	@RequestMapping(value="printDecOrder.do")
	public @ResponseBody String printDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();
		JSONArray checkResult =null;//= new JSONArray() ;
		try{								
			JSONArray ids = json.getJSONArray("orders");
			
			//先确认，获取数据
			checkResult=decOrderService.confirmDecOrders(ids,request.getSession(),app_key,
					app_secret,cainiao_token,cainiao_user_id,cainiao_url);
			
			String name = "PrintInfo" + (new Date().getTime());
			request.getSession().setAttribute(name, ids);
			
			String localCode = json.getString("LocalCode"); 
			String data= "{\"session\":\""+name+"\",\"printFormat\":"
			+Tools.getPrintFormat(Tools.getCurCustomerID(request.getSession()), localCode)+"}";
			
			String printSort = Integer.toString(json.getInt("printSort")) ; 
			request.getSession().setAttribute("printBillSort", printSort);
			
			re.setErrorCode(0);
			re.setData(data);
		}catch(Exception e){
			re.setErrorCode(1)	;
			re.setMsg("选择打印【"+e.getMessage() +"】");	
			e.printStackTrace();			
		}		
		
		
		return JSONObject.fromObject(re).toString();
	}	
	
	
	//根据订单列表打印订单    
	@RequestMapping(value="getDecOrderPrintInfo.do")
	public @ResponseBody String getDecOrderPrintInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
				
		JSONObject json=null;
		//logger.info("输入参数："+request.get  );
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			Map<String,String[]> paramemap = request.getParameterMap();
			String p = "" ;			
			for(Iterator it = paramemap.keySet().iterator();it.hasNext();){
				String paramsName = (String)it.next();
				logger.info("参数【"+paramsName+"】："+paramemap.get(paramsName)[0]);
				p=paramsName;
			}
						
			//logger.info("取得GET："+p);t
			json = JSONObject.fromObject(p);
		}
		
		JsonResult re = new JsonResult();
		
		try{
			String sessionName = json.getString("session");
			if (sessionName == null){
				throw new Exception("缺少提取的订单信息");
			}
			
			JSONArray fields = json.getJSONArray("fields");
			
			JSONArray ids = (JSONArray)request.getSession().getAttribute(sessionName);
			if (ids==null){
				throw new Exception("订单信息不存在");
			}
						
			String idList = ""; 
			for(int i=0;i<ids.size();i++){
				if (idList==""){
					idList = Integer.toString(ids.getInt(i));
				}
				else{
					idList = idList +","+Integer.toString(ids.getInt(i));
				}
			}
			
			if (idList==""){
				throw new Exception("取不到订单列表");
			}
	
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("front", Tools.getFrontStr(request.getSession()));
			if (null==json.get("bak")){
				params2.put("bak", "0");
			}else{
				params2.put("bak", (String)json.get("bak"));
			}							
			params2.put("idList", idList);
			
		
			if (!json.containsKey("flag")){
				re.setErrorCode(10)	;
				re.setMsg("没有确定状态");	
				return JSONObject.fromObject(re).toString();						
			} 
			
			int flag = json.getInt("flag");
			/*if ((flag<10) || ((flag>=95) && (flag<=99))){
				re.setErrorCode(10)	;
				re.setMsg("订单当前状态不能打印");	
				return JSONObject.fromObject(re).toString();									
			}
			else*/ if (flag >=95){
				params2.put("bak", "");
			}
			else {
				params2.put("bak", "0");
			}
					
			if (flag>=0){
				params2.put("flag", flag);
			}
			
			String pb = request.getSession().getAttribute("printBillSort").toString();
			
			if (pb.equals("0")){//按SKU排序
				params2.put("orderby", " order by KeyNote ");
			}
			
			List<DecOrder> orderList = decOrderService.qryDecOrderList(params2);
			
			if (orderList.size()<=0){
				re.setErrorCode(3)	;
				re.setMsg("找不到订单");	
				return JSONObject.fromObject(re).toString();			
			}
								
			JSONArray d = decOrderService.getOrderPrintInfo(request.getSession(), orderList, fields);
			logger.info("准备返回数量：" + d.size());
			
			re.setData(d);
			re.setErrorCode(0);
			
			//清session
			request.getSession().removeAttribute(sessionName);
		}catch(Exception e){
			re.setErrorCode(1)	;
			re.setMsg("取订单打印内容信息出错【"+e.getMessage() +"】");	
			e.printStackTrace();			
		}
		
		//logger.info(JSONObject.fromObject(re).toString());
		
		return JSONObject.fromObject(re).toString();
	}
	
	/*
	//根据订单列表打印订单    
	@RequestMapping(value="printDecOrder.do")
	public @ResponseBody String printDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();				
		JSONArray ids = json.getJSONArray("orders");
		String idList = ""; 
		for(int i=0;i<ids.size();i++){
			if (idList==""){
				idList = Integer.toString(ids.getInt(i));
			}
			else{
				idList = idList +","+Integer.toString(ids.getInt(i));
			}
		}
		
		if (idList==""){
			re.setErrorCode(10)	;
			re.setMsg("取不到订单列表");	
			return JSONObject.fromObject(re).toString();			
		}

		HashMap<String, Object> params2 = new HashMap<String,Object>();
		params2.put("front", Tools.getFrontStr(request.getSession()));
		if (null==json.get("bak")){
			params2.put("bak", "0");
		}else{
			params2.put("bak", (String)json.get("bak"));
		}							
		params2.put("idList", idList);
		
		if (!json.containsKey("flag")){
			re.setErrorCode(10)	;
			re.setMsg("没有确定状态");	
			return JSONObject.fromObject(re).toString();						
		} 
		
		int flag = json.getInt("flag");
		if ((flag<10) || ((flag>=95) && (flag<=99))){
			re.setErrorCode(10)	;
			re.setMsg("订单当前状态不能打印");	
			return JSONObject.fromObject(re).toString();									
		}
		else if (flag >=100){
			params2.put("bak", "");
		}
		else {
			params2.put("bak", "0");
		}
				
		params2.put("flag", flag);
		params2.put("orderby", " order by KeyNote ");
		List<DecOrder> orderList = decOrderService.qryDecOrderList(params2);
		
		if (orderList.size()<=0){
			re.setErrorCode(3)	;
			re.setMsg("找不到订单");	
			return JSONObject.fromObject(re).toString();			
		}
				
		try{
			re.setData(decOrderService.getOrderPrintInfo(request.getSession(), orderList));
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1)	;
			re.setMsg("取订单打印信息出错【"+e.getMessage() +"】");	
			e.printStackTrace();			
		}
		return JSONObject.fromObject(re).toString();
	}*/	
	
	//记录打印次数    
	@RequestMapping(value="finishPrintDecOrder.do")
	public @ResponseBody String finishPrintDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();				
		JSONArray ids = json.getJSONArray("orders");
		String idList = ""; 
		for(int i=0;i<ids.size();i++){
			if (idList==""){
				idList = Integer.toString(ids.getInt(i));
			}
			else{
				idList = idList +","+Integer.toString(ids.getInt(i));
			}
		}
		
		if (idList==""){
			re.setErrorCode(10)	;
			re.setMsg("取不到订单列表");	
			return JSONObject.fromObject(re).toString();			
		}

		HashMap<String, Object> params2 = new HashMap<String,Object>();
		params2.put("front", Tools.getFrontStr(request.getSession()));
		if (null==json.get("bak")){
			params2.put("bak", "0");
		}else{
			params2.put("bak", (String)json.get("bak"));
		}							
		params2.put("idList", idList);
		
		try{
			decOrderService.updatePrintTimes(params2);
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1)	;
			re.setMsg("完成打印出错【"+e.getMessage() +"】");	
			e.printStackTrace();			
		}
		return JSONObject.fromObject(re).toString();
	}	
	
	/*
	//打印已确认的且未打印过的订单    
	@RequestMapping(value="printConfrimDecOrder.do")
	public @ResponseBody String printConfrimDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();		
		
		if (!json.containsKey("deliveryid")){
			re.setErrorCode(10)	;
			re.setMsg("没有确定快递");	
			return JSONObject.fromObject(re).toString();						
		} 
		
		int deliveryid = json.getInt("deliveryid");
		HashMap<String, Object> params2 = new HashMap<String,Object>();
		params2.put("front", Tools.getFrontStr(request.getSession()));
		params2.put("customerID", Tools.getCurCustomerID(request.getSession()));
		params2.put("bak", "0");
		params2.put("flag", 10);
		params2.put("printTimes", 0);
				
		params2.put("orderby", " order by KeyNote ");
		List<DecOrder> orderList = decOrderService.qryDecOrder(params2);
		
		if (orderList.size()<=0){
			re.setErrorCode(3)	;
			re.setMsg("找不到订单");	
			return JSONObject.fromObject(re).toString();			
		}
				
		try{
			re.setErrorCode(0);
			re.setData(decOrderService.getOrderPrintInfo(request.getSession(), orderList));
		}catch(Exception e){
			re.setErrorCode(1)	;
			re.setMsg("取订单打印信息出错【"+e.getMessage() +"】");	
			e.printStackTrace();			
		}
		return JSONObject.fromObject(re).toString();
	}	*/
	
	//打印已确认的且未打印过的订单   
	//从curOrder中取出来，生成订单列表，记录在新的session里，session为新记的ID
	@RequestMapping(value="printConfrimDecOrder.do")
	public @ResponseBody String printConfrimDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();
		//取session
		JSONArray ids = new JSONArray() ;
		
		try{
			List<DecOrder> os = (List<DecOrder>)request.getSession().getAttribute("curOrder");
			if (null==os){
				throw new Exception("找不到订单列表");
			}else{
				for(int i=0 ; i<os.size() ; i++){
					ids.add(os.get(i).getId());
				}					
			}				
			
			String name = "PrintInfo" + (new Date().getTime());
			request.getSession().setAttribute(name, ids);			
			String localCode = json.getString("LocalCode"); 
			String data= "{\"session\":\""+name+"\",\"printFormat\":"
				+Tools.getPrintFormat(Tools.getCurCustomerID(request.getSession()), localCode)+"}";
			//data:{"session":"PrintInfo1431251516203","printFormat":{}}
			
			re.setErrorCode(0);
			re.setData(data);
		}catch(Exception e){
			re.setErrorCode(1)	;
			re.setMsg("批量打印出错【"+e.getMessage() +"】");	
			e.printStackTrace();			
		}		
		return JSONObject.fromObject(re).toString();
	}		
	
	
	//取消订单  
	@RequestMapping(value="cancelDecOrder.do")
	public @ResponseBody String cancelDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray checkResult = new JSONArray() ;
		JsonResult re = new JsonResult();
		try {
			//取得订单列表
			JSONArray ids = json.getJSONArray("orders");
			for(int i=0;i<ids.size();i++){
				HashMap<String,Object>  result = new HashMap<String,Object> ();
				try{
					result.put("ID", ids.getInt(i));					
					decOrderService.BakOrder(request.getSession(), ids.getInt(i), 97);
					result.put("errorCode", 0);
				}catch (Exception e) { 
					result.put("errorCode", 1);
					result.put("msg", "取消订单【"+Integer.toString(ids.getInt(i))+"】失败");
					logger.info("取消订单【"+Integer.toString(ids.getInt(i))+"】失败：" + e.getMessage());
				}
				checkResult.add(result);				
			}
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(1)	;
			re.setMsg("取消订单失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
				
		re.setData(checkResult);
		return JSONObject.fromObject(re).toString();
	}		
	
	//分销取消订单  
	@RequestMapping(value="stopDecOrder.do")
	public @ResponseBody String stopDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray checkResult = new JSONArray() ;
		JsonResult re = new JsonResult();
		String msg = "";
		try {
			//取得订单列表
			JSONArray ids = json.getJSONArray("orders");
			for(int i=0;i<ids.size();i++){
				HashMap<String,Object>  result = new HashMap<String,Object> ();
				try{
					result.put("ID", ids.getInt(i));					
					//msg = decOrderService.stopDecOrder(request.getSession(),ids.getInt(i));
					msg = decOrderService.StopDecOrder(request.getSession(), ids.getInt(i));
					if (msg.equals("") || msg==null){
						result.put("errorCode", 0);
					}
					else{
						result.put("errorCode", 100);
						result.put("msg", msg);
					}					
				}catch (Exception e) { 
					result.put("errorCode", 1);
					result.put("msg", "取消订单【"+Integer.toString(ids.getInt(i))+"】失败");
					logger.info("取消订单【"+Integer.toString(ids.getInt(i))+"】失败：" + e.getMessage());
				}
				checkResult.add(result);				
			}
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(-1)	;
			re.setMsg("取消订单失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
				
		re.setData(checkResult);
		return JSONObject.fromObject(re).toString();
	}		
	
	//分销审核订单  
	@RequestMapping(value="checkDecOrder.do")
	public @ResponseBody String checkDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray checkResult = new JSONArray() ;
		JsonResult re = new JsonResult();
		String msg = "";
		try {
			//取得订单列表
			JSONArray ids = json.getJSONArray("orders");
			for(int i=0;i<ids.size();i++){
				HashMap<String,Object>  result = new HashMap<String,Object> ();
				try{
					result.put("ID", ids.getInt(i));					
					//msg = decOrderService.checkDecOrder(request.getSession(), ids.getInt(i));
					//msg = decOrderService.checkDecOrder(request.getSession(), ids.getInt(i));
					msg = decOrderService.CheckDecOrder(request.getSession(), ids.getInt(i));
					if (msg.equals("") || msg==null ){
						result.put("errorCode", 0);
					}
					else{
						result.put("errorCode", 100);
						result.put("msg", msg);
					}					
				}catch (Exception e) { 
					result.put("errorCode", 1);
					result.put("msg", "审订订单【"+Integer.toString(ids.getInt(i))+"】失败");
					logger.info("审核订单【"+Integer.toString(ids.getInt(i))+"】失败：" + e.getMessage());
				}
				checkResult.add(result);				
			}
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(-1)	;
			re.setMsg("审核订单失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
				
		re.setData(checkResult);
		return JSONObject.fromObject(re).toString();
	}			
	
	//自动合并订单
	@RequestMapping(value="mergeDecOrder.do")
	public @ResponseBody String mergeDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JsonResult re = new JsonResult();
		try {
			//取得订单列表
			decOrderService.mergeDecOrder(request.getSession()) ;
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(1)	;
			re.setMsg("合并订单失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
				
		return JSONObject.fromObject(re).toString();
	}			

	
	//导入订单
	@RequestMapping(value="importDecOrder.do")
	public @ResponseBody String importDecOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JsonResult result = new JsonResult();
		JSONArray resultList = new JSONArray(); //导入的结果
		String editor = Tools.getCurLoginCName(request.getSession());
		String front = Tools.getFrontStr(request.getSession());
		int customerID = Tools.getCurCustomerID(request.getSession());

		
		//获取文件
		MultipartHttpServletRequest req = (MultipartHttpServletRequest)request;
		MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();
		HashMap<String,Object> iresult = new HashMap<String,Object>();//导入结果
		Iterator it = map.keySet().iterator();
		String refSheetID = ""; 
		int errCount = 0 ;
		int succCount = 0 ;
			
		for(;it.hasNext();){
			String filename = (String)it.next();
			//获取文件流
			List<MultipartFile> files =(List<MultipartFile>) map.get(filename);			
			for(int j=0;j<files.size();j++){
				MultipartFile file = files.get(j);
				if(!file.isEmpty()){
					logger.info("fileName: "+file.getOriginalFilename());
					try{
						//保存文件
						Tools.bakFile(request , file, "导入订单_"+Tools.getCurCustomerName(request.getSession()));
						
						//----取得基本数据
						
						//店铺列表
						List<HashMap> shopList = decShopService.qryCustomerShopList(customerID);
						
						//快递列表
						List<HashMap> deliveryList = decDeliveryService.qryDeliveryList(new HashMap<String, Object> () );
						
						//解析excel文件
						HashMap<String,Integer> headList = new HashMap<String,Integer>();
						List<List<String>> data = POIUtils.getExcelData(file, file.getOriginalFilename(), 0);
						logger.info("导入数据行数："+data.size());
						for(int row=0;row<data.size();row++){
							List<String> rowData = data.get(row);
							
							if (row==0){//第一行为列头，记录位置
								for(int col=0;col<rowData.size();col++){
									headList.put(rowData.get(col), col);
								}
							}
							else{//数据导入								 								
								//----单头								
								//检查订单是否存在								
								refSheetID = Tools.getListByName(rowData , headList ,"订单号");	
								logger.info("订单号："+refSheetID);
								if (refSheetID == ""){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "没有【订单号】数据");
									resultList.add(iresult);
									continue;
								}
								
								HashMap<String, Object> params = new HashMap<String,Object>();
								params.put("front", front);
								params.put("bak", "0");
								params.put("refsheetid", refSheetID);
								params.put("customerID", customerID);
								List<DecOrder> o = decOrderService.qryDecOrder(params) ;
								
								int id = -1;
								String sheetID = "";
								if (o.size()>=1){
									//修改
									id=o.get(0).getId();	
									sheetID = o.get(0).getSheetID();
								}
								
								//取得店铺ID
								String shopName = Tools.getListByName(rowData , headList ,"店铺");
								String temp = Tools.getListValue(shopList , "Name", shopName , "ID");					
								if (temp == null){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "【"+refSheetID+"】找不到店铺【"+temp+"】");
									resultList.add(iresult);
									continue;																		
								}
								int shopID = Integer.parseInt(temp);
								
								DecOrder decOrder = new DecOrder();	
								//支付方式:
								//4	分销
								//3	自动发货
								//2	货到付款
								//1	在线支付
								//0	其他
								decOrder.setPayMode(1);//默认支付方式为在线支付
								
								decOrder.setRefSheetID(refSheetID);
								decOrder.setShopID(shopID);
								
								String state = Tools.getListByName(rowData , headList ,"省");
								String city = Tools.getListByName(rowData , headList ,"市");
								
								if ((state.equals("") || (city.equals("")))) {
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "【"+refSheetID+"】没有省");
									resultList.add(iresult);
									continue;																		
								}
								
								String district = Tools.getListByName(rowData , headList ,"区");
								String address = Tools.getListByName(rowData , headList ,"地址");	
								if (address.equals("")) {
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "【"+refSheetID+"】没有地址");
									resultList.add(iresult);
									continue;																		
								}
								
								//取得快递ID								
								String deliveryName = Tools.getListByName(rowData , headList ,"快递");
								temp = Tools.getListValue(deliveryList , "Name", deliveryName , "ID");
								int deliveryID = 0;
								if (!Tools.getSystemType(request.getSession()).equals("1")){ 
									if (temp == null){
										//分配快递
										try{
											deliveryID = decDeliveryService.chooseDecDelivery(state, city, district, address, shopID, 1, customerID);
										}catch(Exception e){
											throw new Exception("选择快递出错");
										}
										logger.info("分配快递：" + Integer.toString(deliveryID));
									}else {
										deliveryID = Integer.parseInt(temp);
									}
								}
								
								decOrder.setDeliveryID(deliveryID);
								decOrder.setSellerNick(Tools.getListByName(rowData , headList ,"买家昵称"));
								temp = Tools.getListByName(rowData , headList ,"付款时间");
								
								if (temp != ""){
									try{									
										SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										decOrder.setPayTime(formatter.parse(temp));
									}catch(Exception e){
										decOrder.setPayTime(new Date());
									}
								}else{
									decOrder.setPayTime(new Date());
								}
								
								decOrder.setState(state);
								decOrder.setCity(city);
								decOrder.setDistrict(district);
								decOrder.setAddress(address);
								decOrder.setPhone(Tools.getListByName(rowData , headList ,"电话"));
								decOrder.setMobile(Tools.getListByName(rowData , headList ,"手机"));
								if ((decOrder.getMobile().equals("")) || (decOrder.getMobile()==null) 
										||(decOrder.getPhone().equals("")) || (decOrder.getPhone()==null) ){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "【"+refSheetID+"】没有手机或电话");
									resultList.add(iresult);
									continue;																		
								}
								
								decOrder.setLinkMan(Tools.getListByName(rowData , headList ,"收货人"));
								if ((decOrder.getLinkMan().equals("")) || (decOrder.getLinkMan()==null)){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "【"+refSheetID+"】没有收货人");
									resultList.add(iresult);
									continue;																		
								}
								
								if ((decOrder.getSellerNick().equals("")) || (decOrder.getSellerNick()==null)){
									decOrder.setSellerNick(decOrder.getLinkMan());
								} 
								
								decOrder.setNote(Tools.getListByName(rowData , headList ,"备注"));
								decOrder.setCustomerID(customerID);
								
								temp = Tools.getListByName(rowData , headList ,"邮费");
								try{								
									if (temp != ""){
										decOrder.setPostFee(Double.parseDouble(temp));
									}
								}catch(Exception e){
								}		
								decOrder.setEditor(editor);//登录人
								decOrder.setFront(front);
								
								//明细
								String title = Tools.getListByName(rowData , headList ,"标题");
								String skuPropertiesName = Tools.getListByName(rowData , headList ,"属性");
								String outerSkuID = Tools.getListByName(rowData , headList ,"SKU");
							
								int itemId = -1;
								if (id>0) {//修改的订单
									//检查是否存在
									HashMap<String, Object> params2 = new HashMap<String,Object>();
									params2.put("front", front);
									params2.put("bak", "0");
									params2.put("sheetID", sheetID);
									params2.put("title", title);
									params2.put("SkuPropertiesName", skuPropertiesName);
									params2.put("outerSkuID", outerSkuID);									
									List<DecOrderItem> oi = decOrderService.qryDecOrderItem(params2) ;	
									
									if (oi.size()>=1){
										itemId = oi.get(0).getId();	
									}
									 
								}
								
								DecOrderItem decOrderItem = new DecOrderItem();								
								decOrderItem.setOuterSkuID(outerSkuID);
								decOrderItem.setTitle(title);
								decOrderItem.setSkuPropertiesName(skuPropertiesName);

								temp = Tools.getListByName(rowData , headList ,"单价");
								try{			
									decOrderItem.setCustomPrice(Double.parseDouble(temp));
								}catch(Exception e){
									errCount = errCount + 1;
									iresult.put("errorCode", 11);
									iresult.put("msg", "【"+refSheetID+"】单价【"+temp+"】出错");
									resultList.add(iresult);
									continue;											
								}		
								
								temp = Tools.getListByName(rowData , headList ,"数量");
								try{			
									decOrderItem.setPurQty(Integer.parseInt(temp));
								}catch(Exception e){
									errCount = errCount + 1;
									iresult.put("errorCode", 12);
									iresult.put("msg", "【"+refSheetID+"】数量【"+temp+"】出错");
									resultList.add(iresult);
									continue;											
								}		
								
								decOrderItem.setNote(Tools.getListByName(rowData , headList ,"明细备注"));
								decOrderItem.setFront(front);
								
								//----保存
								//保存单头
								if (id ==-1){//增加
									decOrder.setCreateTime(new Date());
									decOrder.setEditTime(new Date());
									decOrder.setSheetFlag(3);
									
									int newid = loginService.GetNewID(400101);
									decOrder.setId(newid);
									
									//取得单号
									sheetID = loginService.GetNewSheetID(400100);
									decOrder.setSheetID(sheetID);	
									decOrderService.add(decOrder);
								}
								else {
									decOrderService.update(decOrder);
								}
								
								//保存明细
								if (itemId ==-1){//增加
									decOrderItem.setSheetID(sheetID);
									decOrderItem.setId(loginService.GetNewID(400102));	
									decOrderItem.setSheetID(sheetID);	
									decOrderService.addItem(decOrderItem);
								}
								else {
									decOrderService.updateItem(decOrderItem);
								}
								
								decOrderService.setDecKeyPicNote(sheetID,customerID);
								
								succCount=succCount+1;
							}
						}
					}catch(Exception e){
						errCount = errCount + 1;						
						logger.info("导入订单【"+refSheetID+"】出错: "+e.getMessage());
						iresult.put("errorCode", 100);
						iresult.put("msg", "导入订单【"+refSheetID+"】失败:"+e.getMessage());
						
					}
				}
			}
			
		}		
		
		String msg = "导入成功记录【"+ Integer.toString(succCount) +"】，导入失败记录【"+Integer.toString(errCount) +"】";
		result.setMsg(msg);
		result.setData(resultList);
		logger.info("导入结果：" + JSONObject.fromObject(result).toString());
		return JSONObject.fromObject(result).toString();
		
	}
	
	//查询订单快递路由
	@RequestMapping(value="qrydeliveryRouteInfo.do",method=RequestMethod.POST)
	public @ResponseBody String qryDeliveryInfo(HttpServletRequest request){
		JSONObject json =null;
		JsonResult result = new JsonResult();
		JSONArray deliveryResult =null;
		try{
			json = Common.getRequestJsonObject(request.getInputStream(), "utf-8");
			String companyCode = json.getString("companyCode");
			JSONArray outsid = json.getJSONArray("orders");
			deliveryResult  = decOrderService.qryDeliveryInfo(companyCode, outsid);
			result.setErrorCode(0);
			result.setData(deliveryResult);
		}catch(Exception e){
			logger.info("查询订单快递路由出错了,错误信息: "+e.getMessage());
			result.setErrorCode(1);
			result.setMsg(e.getMessage());
		}
		
		return JSONObject.fromObject(result).toString();
	}
	
	//修改订单状态  
	@RequestMapping(value="modifyDecOrderFlag.do")
	public @ResponseBody String modifyDecOrderFlag(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		JSONArray checkResult = new JSONArray() ;
		JsonResult re = new JsonResult();
		try {
			//取得订单列表
			JSONArray ids = json.getJSONArray("orders");
			int flag = json.getInt("flag");
			for(int i=0;i<ids.size();i++){
				HashMap<String,Object>  result = new HashMap<String,Object> ();
				try{
					result.put("ID", ids.getInt(i));					
					decOrderService.modifyDecOrderFlag(request.getSession(), ids.getInt(i), flag);
					result.put("errorCode", 0);
				}catch (Exception e) { 
					result.put("errorCode", 1);
					result.put("msg", "取消订单【"+Integer.toString(ids.getInt(i))+"】失败");
					logger.info("取消订单【"+Integer.toString(ids.getInt(i))+"】失败：" + e.getMessage());
				}
				checkResult.add(result);				
			}
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(1)	;
			re.setMsg("取消订单失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
				
		re.setData(checkResult);
		return JSONObject.fromObject(re).toString();
	}		
	
	//导出订单
	/*
	@RequestMapping(value="exportDecOrder.do")
	public @ResponseBody String  exportExcel(HttpServletRequest request,HttpServletResponse response){
		JsonResult result =null;
		try{
			JSONObject json=null;
			result = new JsonResult();
		}
		catch(Exception e){
			logger.info("建立对象出错"+e.getMessage());
		}
			
		try{
			//json= Common.getRequestJsonObject(request.getInputStream(), "utf-8");
			//excel第一列数据
			//加店铺名、快递
			String header = "订单号,主帐号,买家昵称,快递单号,省,市,区,地址,电话,手机,联系人,付款时间,发货时间,明细内容,明细行数,商品总数量,商品总金额,邮费,买家备注,卖家备注,买家留言,交易备注,备注";
			String fields = "RefSheetID,SellerNick,DeliverySheetID,BuyerNick,State,City,District,Address,Phone,Mobile,LinkMan,PayTime,SendTime,ItemContent,ItemCount,TotalQty,TotalAmount,PostFee,BuyerMemo,SellerMemo,BuyerMessage,TradeMemo,Note";
			//List<DecCustomer> customers = deccustomerService.qryCustomer(null);
			
			List<DecOrder> os = (List<DecOrder>)request.getSession().getAttribute("curOrder");
			if (os==null){
				throw new Exception("没有查询的订单数据");
			}
			
			logger.info("订单数据"+ os.size());
			POIUtils.exportToExcelHead(response,"订单",os,fields,header);
			logger.info("订单数据ok");
			result.setErrorCode(0);			
		}catch(Exception e){
			logger.info("导出订单文件出错"+e.getMessage());
			result.setErrorCode(1);
			result.setMsg("导出订单文件出错"+e.getMessage());
		}
		
		return JSONObject.fromObject(result).toString();
	}	*/
	
	//导出订单
	@RequestMapping(value="exportDecOrder.do")
	public @ResponseBody String  exportExcel(HttpServletRequest request,HttpServletResponse response){
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int breakPoint = 1; 
		JsonResult re = new JsonResult();//
		String ret ;
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("front", Tools.getFrontStr(request.getSession()));
			if (null==json.get("bak")){
				params2.put("bak", "0");
			}else{
				params2.put("bak", (String)json.get("bak"));
			}
						
			params2.put("customerID", Tools.getCurCustomerID(request.getSession()));
			params2.put("shopID", (Integer)json.get("shopID"));
			//params2.put("deliveryID", (Integer)json.get("deliveryID"));
			if (json.containsKey("deliveryID")){
				logger.info("包括快递条件");
				if (json.get("deliveryID")!=null && !json.get("deliveryID").equals("null")){
					logger.info("快递非空" + json.get("deliveryID").toString());
					params2.put("deliveryID", (Integer)json.get("deliveryID"));
				}
			}
			logger.info("快递条件OK");
			breakPoint = 40; 
			
			params2.put("flag", (Integer)json.get("flag"));
			if (json.containsKey("flag")){
				if (json.getInt("flag")<97){params2.put("bak", "0");}
				else {params2.put("bak", "");}
			}

			if (json.containsKey("timeType")){
				params2.put("timeType", (Integer)json.get("timeType"));				
			}
			if (!params2.containsKey("timeType")){
				params2.put("timeType",1);
			}
			
			if (json.containsKey("beginTime")){
				params2.put("begintime", (String)json.get("beginTime")+":00");//付款时间段
				logger.info("取得开始："+ params2.get("begintime").toString());
			}
			
			if (json.containsKey("endTime")){
				params2.put("endtime", (String)json.get("endTime")+":00");//付款时间段
			}
						
			params2.put("range", (Integer)json.get("range"));			
			params2.put("key", (String)json.get("key"));
			params2.put("refsheetid", (String)json.get("refsheetid"));
			params2.put("sellerFlag", (Integer)json.get("sellerFlag"));
			params2.put("inDays", (Integer)json.get("inDays"));
			 
			params2.put("buyerNick", (String)json.get("buyerNick"));
			params2.put("linkman", (String)json.get("linkman"));
			params2.put("phone", (String)json.get("phone"));
			params2.put("mobile", (String)json.get("mobile"));
			params2.put("state", (String)json.get("state"));
			params2.put("address", (String)json.get("address"));
			params2.put("deliverySheetID", (String)json.get("deliverySheetID"));
			params2.put("goodsName", (String)json.get("goodsName"));
			params2.put("title", (String)json.get("title"));
			params2.put("outerSkuID", (String)json.get("outerSkuID"));
			params2.put("note", (String)json.get("note"));
			params2.put("buyerMemo", (String)json.get("buyerMemo"));
			params2.put("sellerMemo", (String)json.get("sellerMemo"));
			params2.put("buyerMessage", (String)json.get("buyerMessage"));
			params2.put("tradeMemo", (String)json.get("tradeMemo"));
			params2.put("color", (String)json.get("color"));
			params2.put("size", (String)json.get("size"));
			params2.put("itemCount", (Integer)json.get("itemCount"));
			params2.put("itemQty", (Integer)json.get("itemQty"));
			if (json.containsKey("postFee")){
				params2.put("postFee", Double.parseDouble(json.get("postFee").toString()));
			}
			if (json.containsKey("totalAmount")){
				params2.put("totalAmount", Double.parseDouble(json.get("totalAmount").toString()));
			}
			
			params2.put("invoiceFlag", (Integer)json.get("invoiceFlag"));
			params2.put("payMode", (Integer)json.get("payMode"));
			params2.put("tradeFrom", (String)json.get("tradeFrom"));
			params2.put("sheetID", (String)json.get("sheetID"));
						
			
			//List<DecOrder> result = decOrderService.qryDecOrder(params2);						
			List<HashMap>  orders = decOrderMapper.qryStaDecOrder(params2);
			
			//导出
			String header = "订单号,店铺,快递,快递单号,买家昵称,省,市,区,地址,电话,手机,联系人,付款时间,发货时间,明细内容,明细行数,商品总数量,商品总金额,邮费,买家备注,卖家备注,买家留言,交易备注,备注";
			//String fields = "RefSheetID,ShopName,DeliveryName,DeliverySheetID,BuyerNick,State,City,District,Address,Phone,Mobile,LinkMan,PayTime,SendTime,ItemContent,ItemCount,TotalQty,TotalAmount,PostFee,BuyerMemo,SellerMemo,BuyerMessage,TradeMemo,Note";			
			String fields = "refSheetID,shopName,deliveryName,deliverySheetID,buyerNick,state,city,district,address,phone,mobile,linkMan,payTime,sendTime,itemContent,itemCount,totalQty,totalAmount,postFee,buyerMemo,sellerMemo,buyerMessage,tradeMemo,note";
			String tempfile = request.getRealPath("/temp/"+"订单数据"+".xls");
			ret= POIUtils.exportToExcelHeadJxlMap(tempfile,response,request,"订单数据",orders,fields,header,port);
			
			re.setErrorCode(0);
			re.setData(ret);
			logger.info("链接："+ret);
			

		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询订单出错");
			logger.info("查询订单出错："+ breakPoint +" "+ e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}	
		
	

	
	//导出订单商品统计
	@RequestMapping(value="exportStaDecOrderSku.do")
	public @ResponseBody String  exportStaDecOrderSku(HttpServletRequest request,HttpServletResponse response){
		JsonResult result = new JsonResult();
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		String ret ;
		try{
			//查询
			List<DecOrder> os = (List<DecOrder>)request.getSession().getAttribute("curOrder");
			if (os==null){
				throw new Exception("没有查询的订单数据");
			}
			//取得列表
			String sheetList = ""; 
			for(int i=0;i<os.size();i++){
				DecOrder o = os.get(i);
				if (sheetList==""){
					sheetList = "'"+o.getSheetID()+"'";
				}
				else{
					sheetList = sheetList +","+"'"+o.getSheetID()+"'";
				}
			}
			
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("front", Tools.getFrontStr(request.getSession()));
			if (null==json.get("bak")){
				params2.put("bak", "0");
			}else{
				params2.put("bak", (String)json.get("bak"));
			}
						
			params2.put("sheetList",sheetList);
			//params2.put("customerID", Tools.getCurCustomerID(request.getSession()));			
			List<HashMap>  ordersku = decOrderService.qryStaDecOrderSkuList(params2);
			
			
			//输出
			String header = "商品编码,标题,属性,数量";
			String fields = "OuterSkuID,Title,SkuPropertiesName,Qty";
			//List<DecCustomer> customers = deccustomerService.qryCustomer(null);
			
			
			//POIUtils.exportToExcelHead(response,"订单商品",ordersku,fields,header);
			//public static String exportToExcelJxlMap(String tempxlsfile,HttpServletResponse response,HttpServletRequest request,String fileName,List<HashMap> objs, String headers)
			String tempfile = request.getRealPath("/temp/"+"订单商品"+".xls");
			ret= POIUtils.exportToExcelHeadJxlMap(tempfile,response,request,"订单商品",ordersku,fields,header,port);
			result.setErrorCode(0);			
			result.setData(ret);
			logger.info("导出链接："+ret);
		}catch(Exception e){
			logger.info("导出订单文件出错"+e.getMessage());
			result.setErrorCode(1);
			result.setMsg("导出订单文件出错"+e.getMessage());
		}
		
		logger.info("导出返回："+JSONObject.fromObject(result).toString());
		return JSONObject.fromObject(result).toString();
	}	
	
	

	/*
	//导出订单商品统计
	@RequestMapping(value="exportStaDecOrderSku.do")
	public @ResponseBody String  exportStaDecOrderSku(HttpServletRequest request,HttpServletResponse response){
		JsonResult result =null;
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		try{
			//查询
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("front", Tools.getFrontStr(request.getSession()));
			if (null==json.get("bak")){
				params2.put("bak", "0");
			}else{
				params2.put("bak", (String)json.get("bak"));
			}
						
			params2.put("customerID", Tools.getCurCustomerID(request.getSession()));
			params2.put("shopID", (Integer)json.get("shopID"));
			params2.put("deliveryID", (Integer)json.get("deliveryID"));
			params2.put("flag", (Integer)json.get("flag"));
			if (json.containsKey("flag")){
				if (json.getInt("flag")<97){params2.put("bak", "0");}
				else {params2.put("bak", "");}
			}

			if (json.containsKey("beginTime")){
				params2.put("begintime", (String)json.get("beginTime")+":00");//付款时间段
				logger.info("取得开始："+ params2.get("begintime").toString());
			}
			
			if (json.containsKey("endTime")){
				params2.put("endtime", (String)json.get("endTime")+":00");//付款时间段
			}
						
			params2.put("range", (Integer)json.get("range"));			
			params2.put("key", (String)json.get("key"));
			params2.put("refsheetid", (String)json.get("refsheetid"));
			params2.put("sellerFlag", (Integer)json.get("sellerFlag"));
			params2.put("inDays", (Integer)json.get("inDays"));
			 
			params2.put("buyerNick", (String)json.get("buyerNick"));
			params2.put("linkman", (String)json.get("linkman"));
			params2.put("phone", (String)json.get("phone"));
			params2.put("mobile", (String)json.get("mobile"));
			params2.put("state", (String)json.get("state"));
			params2.put("address", (String)json.get("address"));
			params2.put("deliverySheetID", (String)json.get("deliverySheetID"));
			params2.put("goodsName", (String)json.get("goodsName"));
			params2.put("title", (String)json.get("title"));
			params2.put("outerSkuID", (String)json.get("outerSkuID"));
			params2.put("note", (String)json.get("note"));
			params2.put("buyerMemo", (String)json.get("buyerMemo"));
			params2.put("sellerMemo", (String)json.get("sellerMemo"));
			params2.put("buyerMessage", (String)json.get("buyerMessage"));
			params2.put("tradeMemo", (String)json.get("tradeMemo"));
			params2.put("color", (String)json.get("color"));
			params2.put("size", (String)json.get("size"));
			params2.put("itemCount", (Integer)json.get("itemCount"));
			params2.put("itemQty", (Integer)json.get("itemQty"));
			if (json.containsKey("postFee")){
				params2.put("postFee", Double.parseDouble(json.get("postFee").toString()));
			}
			if (json.containsKey("totalAmount")){
				params2.put("totalAmount", Double.parseDouble(json.get("totalAmount").toString()));
			}
			
			params2.put("invoiceFlag", (Integer)json.get("invoiceFlag"));
			params2.put("payMode", (Integer)json.get("payMode"));
			params2.put("tradeFrom", (String)json.get("tradeFrom"));
			params2.put("sheetID", (String)json.get("sheetID"));
			
			List<HashMap<String, Object>>  ordersku = decOrderService.qryStaDecOrderSku(params2);
			
			
			//输出
			String header = "商品编码,标题,属性,数量";
			String fields = "OuterSkuID,Title,SkuPropertiesName,Qty";
			//List<DecCustomer> customers = deccustomerService.qryCustomer(null);
			
			
			POIUtils.exportToExcelHead(response,"订单商品",ordersku,fields,header);
			result.setErrorCode(0);			
		}catch(Exception e){
			logger.info("导出订单文件出错"+e.getMessage());
			result.setErrorCode(1);
			result.setMsg("导出订单文件出错"+e.getMessage());
		}
		
		return JSONObject.fromObject(result).toString();
	}	
	 * */
	
	//统计 初始化数据
	//返回当前登录信息、菜单列表，店铺列表数据。
	@RequestMapping(value="iniGoodsStaData.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniGoodsStaData(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 400100);
			
			
			//取得当前菜单
			List<HashMap> menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ //如果空，测试时取所有菜单 ，实际运行时报错
				try{
					menu = menuService.queryLoginMenu(1);
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
			obj.put("menu", menu);
			
			//取得客户店铺
			List<HashMap> shop=null;
			try{
				HashMap<String,Object> p = new HashMap<String,Object>();
				p.put("customerID", Tools.getCurCustomerID(session));
				shop = decShopService.qryShopList(p);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			obj.put("shop", shop);

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
	
	//商品销售统计
	//{ BeginTime, EndTime,ShopID,SKU }
	@RequestMapping(value="qryGoodsSta.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryGoodsSta(HttpServletRequest request ,HttpServletResponse response) throws Exception{
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
				params2.put("CustomerID", (Integer)json.get("CustomerID"));
			}
			else{
				params2.put("CustomerID", curCustomerID);
			}			
			
			params2.put("front", Tools.getFrontStr(request.getSession()));
			params2.put("BeginTime", (String)json.get("BeginTime"));
			params2.put("EndTime", (String)json.get("EndTime")+" 23:59:59");			
			params2.put("ShopID", (Integer)json.get("ShopID"));
			params2.put("OuterSKUID", (String)json.get("OuterSKUID"));
			//PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
			//		json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			//logger.info("page: "+view.getPage());
			//params2.put("pageview",view);//分页参数
			
			List<HashMap> result = decOrderMapper.qryGoodsSta(params2);
			re.setErrorCode(0);
			re.setData(result);
			//view.setPage(view.getPage()-1);
			//re.setPageInfo(view);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("商品销售统计出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re,config).toString();
	}	

	//日销售统计
	//{ BeginTime, EndTime,ShopID }
	@RequestMapping(value="qryDaysSta.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDaysSta(HttpServletRequest request ,HttpServletResponse response) throws Exception{
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
				params2.put("CustomerID", (Integer)json.get("CustomerID"));
			}
			else{
				params2.put("CustomerID", curCustomerID);
			}			
			
			params2.put("front", Tools.getFrontStr(request.getSession()));
			params2.put("BeginTime", (String)json.get("BeginTime"));
			params2.put("EndTime", (String)json.get("EndTime")+" 23:59:59");			
			params2.put("ShopID", (Integer)json.get("ShopID"));
			params2.put("OuterSKUID", (String)json.get("OuterSKUID"));
			//PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
			//		json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			//logger.info("page: "+view.getPage());
			//params2.put("pageview",view);//分页参数
			
			List<HashMap> result = decOrderMapper.qryDaysSta(params2);
			re.setErrorCode(0);
			re.setData(result);
			//view.setPage(view.getPage()-1);
			//re.setPageInfo(view);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("日销售统计出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re,config).toString();
	}	
	
	//查询客户基本统计信息
	@RequestMapping(value="getCustomerBaseSta.do",method=RequestMethod.POST)
	@ResponseBody
	public String getCustomerBaseSta(HttpServletRequest request ,HttpServletResponse response) throws Exception{
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
			
			List<HashMap> result = decOrderMapper.stGetCustomerBaseSta(params2);
			re.setErrorCode(0);
			re.setData(result);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询客户基本统计信息出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}	

}