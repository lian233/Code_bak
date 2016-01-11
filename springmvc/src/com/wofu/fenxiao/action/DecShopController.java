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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wofu.common.tools.util.Formatter;
import com.wofu.fenxiao.domain.DecShop;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.mapping.DistributeGoodsMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecShopService;
import com.wofu.fenxiao.mapping.DecShopMapper;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.JsonDateValueProcessor;
import com.wofu.fenxiao.utils.Tools;
import com.wofu.fenxiao.utils.POIUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.util.MultiValueMap;
import java.util.Iterator;


@Controller
public class DecShopController extends BaseController{
	@Value("#{configProperties[server_port]}")
	private int port;
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private DecShopService decShopService;
	
	@Autowired 
	private MenuService menuService;
	
	@Autowired
	private DecShopMapper decShopMapper;
	
	
	@Autowired 
	private LoginService accountService;
	//@Value("#{configProperties['userPageSize']}")
	//读取配置文件的属性
	@Value("#{configProperties[extds_id]}")
	private int extdsid;
	@Value("#{configProperties[rds_name]}")
	private String rds_name;
	//淘宝参数
	@Value("#{configProperties[app_key]}")
	private String app_key;
	@Value("#{configProperties[app_secret]}")
	private String app_secret;
	//京东参数
	@Value("#{configProperties[app_key_360]}")
	private String app_key_360;
	@Value("#{configProperties[app_secret_360]}")
	private String app_secret_360;
	//蘑菇街参数
	@Value("#{configProperties[mogujie_app_key]}")
	private String mogujie_app_key;
	@Value("#{configProperties[mogujie_app_secret]}")
	private String mogujie_app_secret;
	//美丽说参数
	@Value("#{configProperties[meilisuo_app_key]}")
	private String meilisuo_app_key;
	@Value("#{configProperties[meilisuo_app_secret]}")
	private String meilisuo_app_secret;
	//阿里巴巴参数
	@Value("#{configProperties[alibaba_app_key]}")
	private String alibaba_app_key;
	@Value("#{configProperties[alibaba_app_secret]}")
	private String alibaba_app_secret;
	
	//店铺资料页面 初始化数据
	//返回当前登录信息、菜单列表，客户分组列表、快递分组列表数据。
	@RequestMapping(value="iniShopData.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniShopData(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 300200);
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
			
			//取得渠道
			List<HashMap> channel=null;
			try{
				HashMap<String,String> p = new HashMap<String,String>();
				channel = decShopService.qryChannelList(p);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			obj.put("channel", channel);


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
	
	//查询店铺资料
	//{ customerID,Name}
	@RequestMapping(value="qryShop.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryShop(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
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
				params2.put("customerID", (Integer)json.get("customerID"));
			}
			else{
				params2.put("customerID", curCustomerID);
			}
						
			params2.put("name", (String)json.get("name"));
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
						
			//根据系统类型取列表
			HashMap<String,String> curLogin = (HashMap<String,String>)request.getSession().getAttribute("CurLoginSession");			
			List<HashMap> result = null;			
			if (curLogin.get("SystemType").toString().equals("1")){
				result =decShopService.qryDShop(params2);
			}
			else{
				result =decShopService.qryShop(params2);
			}
			
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询店铺资料出错");
			logger.info("查询店铺资料出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re,config).toString();
	}
			

	//查询店铺列表数据
	// {key}
	//输出 []
	@RequestMapping(value="qryShopList.do",method=RequestMethod.POST)
	public @ResponseBody String qryShopList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("查询帐号【取参数出错】");
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();
		try{
			logger.info("qryCustomerList取得key"+json.get("key"));
			HashMap<String,Object> params2 = new HashMap<String,Object>();
			params2.put("key", (String)json.get("key"));
			List<HashMap> result =decShopService.qryShopList(params2);
			
			re.setErrorCode(0);
			re.setData(result);			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查店铺数据出错");
			logger.info("查店铺数据出错");
			e.printStackTrace();
		}
		
		return JSONObject.fromObject(re).toString();
	}	
	
	//保存店铺数据
	//输入{shops:[ID,Name,CustomerID,ChannelID,Code,NetAddr,CanMerge,CanSeparate,SynFlag,Nick,AppKey,Session,Token,GetOrderSpan,Status,Note]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveShop.do",method=RequestMethod.POST)
	public @ResponseBody String saveShop(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("店铺【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray shops = json.getJSONArray("shops");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		
		try{
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			for(int i=0;i<shops.size();i++){
				JSONObject shop = shops.getJSONObject(i);
												
				if (!shop.containsKey("ID")){
					throw new Exception("ID数据不存在");
				} 
				
				DecShop c = new DecShop();
				int id = shop.getInt("ID");
				c.setId(id);				
				if (shop.size()>=2){//
					if (shop.containsKey("Name")) {c.setName(shop.getString("Name"));}
					if (curCustomerID==0){//站点维护的资料
						if (shop.containsKey("CustomerID")) {
							c.setCustomerID(shop.getInt("CustomerID"));
						} 
						else {
							if (id==-1){//增加
								throw new Exception("没输入客户数据");
							}else{
								c.setCustomerID(-1);
							}
						}						
					}else{
						c.setCustomerID(curCustomerID);
					}
										
					if (shop.containsKey("ChannelID")) {c.setChannelID(shop.getInt("ChannelID"));}
					if (shop.containsKey("Code")) {c.setCode(shop.getString("Code"));}
					if (shop.containsKey("NetAddr")) {c.setNetAddr(shop.getString("NetAddr"));}
					if (shop.containsKey("Tele")) {c.setTele(shop.getString("Tele"));}
					if (shop.containsKey("LinkMan")) {c.setLinkMan(shop.getString("LinkMan"));}
					if (shop.containsKey("CanMerge")) {c.setCanMerge(shop.getInt("CanMerge"));}
					if (shop.containsKey("CanSeparate")) {c.setCanSeparate(shop.getInt("CanSeparate"));}
					if (shop.containsKey("SynFlag")) {c.setSynFlag(shop.getInt("SynFlag"));}
					if (shop.containsKey("Nick")) {c.setNick(shop.getString("Nick"));}
					if (shop.containsKey("AppKey")) {c.setAppKey(shop.getString("AppKey"));}
					if (shop.containsKey("Session")) {c.setSession(shop.getString("Session"));}
					if (shop.containsKey("Token")) {c.setToken(shop.getString("Token"));}
					if (shop.containsKey("GetOrderSpan")) {c.setGetOrderSpan(shop.getInt("GetOrderSpan"));}
					
					if (shop.containsKey("Status")) {c.setStatus(shop.getInt("Status"));}
					if (shop.containsKey("Note")) {c.setNote(shop.getString("Note"));}										
				}
				
//				int isUpdateStock = 0;
//				HashMap<String,String> curLogin = (HashMap<String,String>)request.getSession().getAttribute("CurLoginSession");
//				if (curLogin.get("SystemType").toString().equals("1")){
//					isUpdateStock = 1;
//				}
				
				if (id==-1){//增加
					//检查数据
					if (!shop.containsKey("Name")) {throw new Exception("没有输入名称");}
					if (!shop.containsKey("ChannelID")) {throw new Exception("没有输入店铺类型");}
					if (!shop.containsKey("Status")) {c.setStatus(1);}
					if (!shop.containsKey("IsGetOrder")) {c.setIsGetOrder(1);}
					
					if (!shop.containsKey("isNeedDelivery")) c.setIsNeedDelivery(1);
					if (shop.containsKey("isNeedDelivery")) c.setIsNeedDelivery(shop.getInt("isNeedDelivery"));
					if (!shop.containsKey("isUpdateStock")) c.setIsUpdateStock(shop.getInt("SynFlag"));
					if (shop.containsKey("isUpdateStock")) c.setIsUpdateStock(shop.getInt("isUpdateStock"));
					if (!shop.containsKey("isgenCustomerRet")) c.setIsgenCustomerRet(0);
					if (shop.containsKey("isgenCustomerRet")) c.setIsgenCustomerRet(shop.getInt("isgenCustomerRet"));
					
					//取得最大的ID
					int newid = accountService.GetNewID(300200);
					idList.add(newid);
					c.setId(newid);
					c.setCode(decShopService.MakeShopCode(c.getCustomerID()));//生成机构编码
					c.setLastOrderTime(new Date());
					c.setLastRefundTime(new Date());
					c.setLastItemTime(new Date());
					c.setLastTokenTime(new Date());
					//c.setIsUpdateStock(isUpdateStock);
					c.setNick("");
					c.setToken("");
					
					
					int channelid =shop.getInt("ChannelID");
					switch(channelid){
					case 1://淘宝
						c.setAppKey(app_key);
						c.setSession(app_secret);
						break;
					case 2://京东
						c.setAppKey(app_key_360);
						c.setSession(app_secret_360);
						break;
					case 3://蘑菇街
						c.setAppKey(mogujie_app_key);
						c.setSession(mogujie_app_secret);
						break;
					case 4://美丽说
						c.setAppKey(meilisuo_app_key);
						c.setSession(meilisuo_app_secret);
						break;
					case 5://阿里巴巴
						c.setAppKey(alibaba_app_key);
						c.setSession(alibaba_app_secret);
						break;
					}
					decShopService.add(c);			
				}else{
					if (shop.size()<=1){//删除 
						decShopService.delete(c.getId());
					}
					else{//修改
						if (!shop.containsKey("GetOrderSpan")) {c.setGetOrderSpan(-1);}
						if (!shop.containsKey("ChannelID")) {c.setChannelID(-1);}
						if (!shop.containsKey("CanMerge")) {c.setCanMerge(-1);}
						if (!shop.containsKey("CanSeparate")) {c.setCanSeparate(-1);}
						if (!shop.containsKey("SynFlag")) {c.setSynFlag(-1);}
						if (!shop.containsKey("Status")) {c.setStatus(-1);}
						
						if (!shop.containsKey("isNeedDelivery")) c.setIsNeedDelivery(1);
						if (shop.containsKey("isNeedDelivery")) c.setIsNeedDelivery(shop.getInt("isNeedDelivery"));
						
						if (!shop.containsKey("SynFlag")) {c.setSynFlag(-1);}
						if (!shop.containsKey("isUpdateStock")) c.setIsUpdateStock(c.getSynFlag());
						if (shop.containsKey("isUpdateStock")) c.setIsUpdateStock(shop.getInt("isUpdateStock"));
						
						if (!shop.containsKey("isgenCustomerRet")) c.setIsgenCustomerRet(0);
						if (shop.containsKey("isgenCustomerRet")) c.setIsgenCustomerRet(shop.getInt("isgenCustomerRet"));
						
						if (shop.containsKey("lastordertime") && !"".equals(shop.getString("lastordertime"))) {c.setLastOrderTime(Formatter.parseDate(shop.getString("lastordertime"), Formatter.DATE_TIME_FORMAT));}
						int channelid =shop.getInt("ChannelID");
						switch(channelid){
						case 1://淘宝
							c.setAppKey(app_key);
							c.setSession(app_secret);
							break;
						case 2://京东
							c.setAppKey(app_key_360);
							c.setSession(app_secret_360);
							break;
						case 3://蘑菇街
							c.setAppKey(mogujie_app_key);
							c.setSession(mogujie_app_secret);
							break;
						case 4://美丽说
							c.setAppKey(meilisuo_app_key);
							c.setSession(meilisuo_app_secret);
							break;
						case 5://阿里巴巴
							c.setAppKey(alibaba_app_key);
							c.setSession(alibaba_app_secret);
							break;
						}
						decShopService.updateshop(c,rds_name,extdsid);
					}
				}				
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存店铺数据出错: "+e.getMessage());
			logger.info("保存店铺数据出错");
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}
	
	
	//修改网店参数  appkey app_secret  token
	@RequestMapping(value="updateShopParams.do",method=RequestMethod.POST)
	public @ResponseBody String updateShopParams(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("店铺【取参数出错】");
			e.printStackTrace();
		}
		JsonResult re = new JsonResult();
		try{
				if (!json.containsKey("ID")){
					throw new Exception("ID数据不存在");
				} 
				DecShop c = new DecShop();
				int id = json.getInt("ID");
				c.setId(id);
				if(!json.containsKey("Nick"))throw new Exception("店铺主帐号为空");
				else
					c.setNick(json.getString("Nick"));
				if(!json.containsKey("AppKey"))throw new Exception("app_key为空");
				else
					c.setAppKey(json.getString("AppKey"));
				if(!json.containsKey("Token"))throw new Exception("Token为空");
				else
					c.setToken(json.getString("Token"));
				if(!json.containsKey("Session"))throw new Exception("Session为空");
				else
					c.setSession(json.getString("Session"));
				c.setChannelID(json.getInt("channelid"));
				if(json.containsKey("lastordertime") && !"".equals(json.getString("lastordertime")))c.setLastOrderTime(Formatter.parseDate(json.getString("lastordertime"), Formatter.DATE_TIME_FORMAT));
					c.setStatus(-1);	//不修改
					c.setSynFlag(-1);	//...
					c.setIsGetOrder(-1);
					c.setCanMerge(-1);
					c.setCanSeparate(-1);
					c.setGetOrderSpan(-1);
					c.setCustomerID(-1);
					decShopService.updateshop(c,rds_name,extdsid);
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存店铺参数数据出错");
			logger.info("保存店铺数据出错");
			e.printStackTrace();
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//获取淘宝店token  写入rds数据库同步列表
	@RequestMapping(value="getToken.do",method=RequestMethod.POST)
	public @ResponseBody String getToken(HttpServletRequest request){
		System.out.println("exedsid: "+extdsid);
		JSONObject json=null;
		JsonResult  result = new JsonResult();
		try{
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
			int channelid = json.getInt("channelid");
			int id = json.getInt("id");
			String appkey = json.getString("AppKey");
			String app_secret = json.getString("Session");
			String getTokenLink = json.getString("gettoken");
			String token = decShopService.getToken(id,channelid,appkey,app_secret,getTokenLink,extdsid,rds_name);
			result.setErrorCode(0);
			JSONObject obj = new JSONObject();
			obj.put("token", token);
			result.setData(obj);
		}catch(Exception e){
			result.setErrorCode(1);
			result.setMsg("获取token出错,"+e.getMessage());
		}
		return JSONObject.fromObject(result).toString();
	}
		
	
	
	//商品资料页面 初始化数据
	@RequestMapping(value="iniDecItemData.do",method=RequestMethod.GET)
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
			obj.put("moduleID", 300300);
			
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
	
	
	//查询商品数据
	//{ShopID、OuterSkuID、CustomBC、Title、Name、Props、Vender}
	@RequestMapping(value="qryDecItem.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDecItem(HttpServletRequest request ,HttpServletResponse response) throws Exception{	
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
				params2.put("CustomerID", (Integer)json.get("customerID"));
			}
			else{
				params2.put("CustomerID", curCustomerID);
			}
						
			params2.put("ShopID", (Integer)json.get("ShopID"));
			params2.put("OuterSkuID", (String)json.get("OuterSkuID"));
			params2.put("CustomBC", (String)json.get("CustomBC"));
			params2.put("Title", (String)json.get("Title"));
			params2.put("Name", (String)json.get("Name"));
			params2.put("Props", (String)json.get("Props"));
			params2.put("Vender", (String)json.get("Vender"));
			
			PageView view = Tools.getPageView("sku_id", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
			
			List<HashMap> result =decShopMapper.qryDecItem(params2);
								
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询商品资料出错");
			logger.info("查询商品资料出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//保存商品资料数据
	//输入saveDecItem{ DecItems:[ ShopID, sku_id, CustomBC,Name,BasePrice,Cost,Vender,Note]}
	//输出 []
	@RequestMapping(value="saveDecItem.do",method=RequestMethod.POST)
	public @ResponseBody String saveDecItem(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("商品资料【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray items = json.getJSONArray("DecItems");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		
		try{
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			for(int i=0;i<items.size();i++){
				JSONObject item = items.getJSONObject(i);
				
				HashMap<String, Object> params2 = new HashMap<String,Object>();
							
				params2.put("CustomBC", (String)item.get("CustomBC"));
				params2.put("Name", (String)item.get("Name"));
				if (item.get("BasePrice") !=null) {
					System.out.println("BasePrice非空进入");
					System.out.println(item.get("BasePrice"));
					params2.put("BasePrice", item.getDouble("BasePrice"));
				}
				if (item.get("Cost") !=null) {
					params2.put("Cost", item.getDouble("Cost"));
				}
				params2.put("Vender", (String)item.get("Vender"));	
				params2.put("Note", (String)item.get("Note"));
				params2.put("sku_id", (String)item.get("sku_id"));
				params2.put("ShopID", (Integer)item.get("ShopID"));
				
				decShopMapper.updateDecItem(params2);				
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存商品资料出错:"+e.getMessage());
			logger.info("保存商品资料出错");
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}	
			
	//取SKU替换数据
	@RequestMapping(value="getCustomerSKUReplace.do",method=RequestMethod.POST)
	public @ResponseBody String getCustomerSKUReplace(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		JsonResult re = new JsonResult();
		try{			 
			String v=accountService.GetCustomerConfig("SKUReplace" , "" , Tools.getCurCustomerID(request.getSession()) ,0);			
			re.setErrorCode(0);
			re.setData(v);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("取SKU替换值出错："+e.getMessage());
			e.printStackTrace();
		}
		
		return JSONObject.fromObject(re).toString();
	}	
		
	//设置SKU替换数据
	@RequestMapping(value="setCustomerSKUReplace.do",method=RequestMethod.POST)
	public @ResponseBody String setCustomerSKUReplace(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		JsonResult re = new JsonResult();
		try{
			String v = (String)json.get("Value");
			accountService.SetCustomerConfig("SKUReplace" , v , Tools.getCurCustomerID(request.getSession()) ,0);			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("设置SKU替换值出错："+e.getMessage());
			e.printStackTrace();
		}
		
		return JSONObject.fromObject(re).toString();
	}	
	
	
	//导出商品数据
	//{ShopID、OuterSkuID、CustomBC、Title、Name、Props、Vender}
	@RequestMapping(value="exportDecItem.do",method=RequestMethod.POST)
	@ResponseBody
	public String exportDecItem(HttpServletRequest request ,HttpServletResponse response) throws Exception{	
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonResult re = new JsonResult();//
		String ret ;
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			if (curCustomerID==0)
			{
				params2.put("CustomerID", (Integer)json.get("customerID"));
			}
			else{
				params2.put("CustomerID", curCustomerID);
			}
						
			params2.put("ShopID", (Integer)json.get("ShopID"));
			params2.put("OuterSkuID", (String)json.get("OuterSkuID"));
			params2.put("CustomBC", (String)json.get("CustomBC"));
			params2.put("Title", (String)json.get("Title"));
			params2.put("Name", (String)json.get("Name"));
			params2.put("Props", (String)json.get("Props"));
			params2.put("Vender", (String)json.get("Vender"));
			
			List<HashMap> result =decShopMapper.qryDecItem(params2);

			//导出
			String header = "店铺ID,商品内部ID,店铺,标题,SKU,属性,商品编码,商品名称,供应商,基本价格,进货价,备注";
			String fields = "ShopID,sku_id,ShopName,Title,OuterSkuID,props_name,CustomBC,Name,Vender,BasePrice,Cost,Note";
			String tempfile = request.getRealPath("/temp/"+"商品资料数据"+".xls");
			ret= POIUtils.exportToExcelHeadJxlMap(tempfile,response,request,"商品资料数据",result,fields,header,port);
			
			re.setErrorCode(0);
			re.setData(ret);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询商品资料出错:"+e.getMessage());
			logger.info("查询商品资料出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//导入商品数据
	@RequestMapping(value="importDecItem.do")
	public @ResponseBody String importDecItem(HttpServletRequest request,HttpServletResponse response) throws Exception{
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
		int errCount = 0 ;
		int succCount = 0 ;
		String skuid = "";
			
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
						Tools.bakFile(request , file, "导入商品资料_"+Tools.getCurCustomerName(request.getSession()));
						
						//解析excel文件
						HashMap<String,Integer> headList = new HashMap<String,Integer>();
						List<List<String>> data = POIUtils.getExcelData(file, file.getOriginalFilename(), 0);
						for(int row=0;row<data.size();row++){
							List<String> rowData = data.get(row);
							
							if (row==0){//第一行为列头，记录位置
								for(int col=0;col<rowData.size();col++){
									headList.put(rowData.get(col), col);
								}
							}
							else{//数据导入								 								
								String temp = Tools.getListByName(rowData , headList ,"店铺ID");
								int shopID = 0;
								try{
									shopID = Integer.parseInt(temp);
								}
								catch(Exception e){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "店铺ID【"+temp+"】错误");
									resultList.add(iresult);
									continue;																											
								}
								
								skuid = Tools.getListByName(rowData , headList ,"商品内部ID");
								if (skuid.equals("")){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "没有商品内部ID");
									resultList.add(iresult);
									continue;																																				
								}
								
								String customBC = Tools.getListByName(rowData , headList ,"商品编码");
								String name = Tools.getListByName(rowData , headList ,"商品名称");
								String vender = Tools.getListByName(rowData , headList ,"供应商");
								String note = Tools.getListByName(rowData , headList ,"备注");
								
								double basePrice = -999999;
								temp = Tools.getListByName(rowData , headList ,"基本价格");
								if (!temp.trim().equals("")){								
									try{
										basePrice = Double.parseDouble(temp);
									}
									catch(Exception e){
										errCount = errCount + 1;
										iresult.put("errorCode", 1);
										iresult.put("msg", "基本价格【"+temp+"】错误");
										resultList.add(iresult);
										continue;																											
									}
								}
								
								
								double cost = -999999;
								temp = Tools.getListByName(rowData , headList ,"进货价");
								if (!temp.trim().equals("")){	
									try{
										cost = Double.parseDouble(temp);
									}
									catch(Exception e){
										errCount = errCount + 1;
										iresult.put("errorCode", 1);
										iresult.put("msg", "进货价【"+temp+"】错误");
										resultList.add(iresult);
										continue;																											
									}			
								}
								
								HashMap<String, Object> params2 = new HashMap<String,Object>();
								
								params2.put("CustomBC", customBC);
								params2.put("Name", name);
								if (basePrice!=-999999) {params2.put("BasePrice", basePrice);}
								if (cost!=-999999) {params2.put("Cost", cost);}

								params2.put("Vender", vender);	
								params2.put("Note", note);
								params2.put("sku_id", skuid);
								params2.put("ShopID", shopID);
								
								decShopMapper.updateDecItem(params2);												
								
								succCount=succCount+1;
							}
						}
					}catch(Exception e){
						errCount = errCount + 1;						
						iresult.put("errorCode", 100);
						iresult.put("msg", "导入商品资料【"+skuid+"】失败:"+e.getMessage());
						
					}
				}
			}
			
		}		
		
		String msg = "导入成功记录【"+ Integer.toString(succCount) +"】，导入失败记录【"+Integer.toString(errCount) +"】";
		result.setMsg(msg);
		result.setData(resultList);
		return JSONObject.fromObject(result).toString();
		
	}	
	
	
		
	
}
