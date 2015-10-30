package com.wofu.fenxiao.action;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.service.DistributorPriceService;
import com.wofu.fenxiao.mapping.DistributorPriceMapper;
import com.wofu.fenxiao.mapping.DecCustomerMapper;

import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.Md5Tool;
import com.wofu.fenxiao.utils.POIUtils;
import javax.servlet.http.HttpSession;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.utils.Tools;
import com.wofu.fenxiao.domain.DistributorPrice;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import java.util.Iterator;
 
@Controller
public class DistributorPriceController extends BaseController{
	@Value("#{configProperties[server_port]}")
	private int port;
	
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private LoginService accountService;
	
	@Autowired  
	private DistributorPriceService distributorPriceService;

	@Autowired  
	private DistributorPriceMapper distributorPriceMapper;

	@Autowired  
	private DecCustomerMapper decCustomerMapper;
	
	@Autowired 
	private MenuService menuService;
	

	@RequestMapping(value="iniDistributorPrice.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniShopData(HttpSession session){
		JsonResult re = new JsonResult();//全部用这种对象输出
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ 
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			obj.put("moduleID", 300500);
			//取得当前菜单
			List<HashMap> menu =(List<HashMap>) session.getAttribute("CurMenu");
			if (null==menu){ 
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
	
	
	//设置客户状态   
	@RequestMapping(value="setDistributorEnable.do")
	public @ResponseBody String setDistributorEnable(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();
		try{
			JSONArray distributors = json.getJSONArray("distributors");
			Object[] ids= distributors.toArray();
			//for(int i=0;i<distributors.size();i++){
			for(int i=0;i< ids.length;i++){				
				int id = Integer.parseInt(ids[i].toString()) ; //c.getInt("CustomerID");
				int enable = json.getInt("Enable");
				distributorPriceService.setDistributorEnable(id, enable);								
				//distributorPriceMapper.setDistributorEnable(map);
			}
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("设置客户状态出错"+e.getMessage());
			e.printStackTrace();
		}
		
		return JSONObject.fromObject(re).toString();
	}
	
	//查询多级分销商资料
	//{ customerID,Name}
	@RequestMapping(value="qrySubDistributor.do",method=RequestMethod.POST)
	@ResponseBody
	public String qrySubDistributor(HttpServletRequest request ,HttpServletResponse response) throws Exception{
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
			params2.put("CustomerID", curCustomerID);	
			
			List<HashMap> result = distributorPriceMapper.qrySubDistributor(params2);				
			re.setErrorCode(0);
			re.setData(result);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询分销商资料出错"+e.getMessage());
			logger.info("查询分销商资料出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
			
	//保存分销商数据
	//输入saveDistributor{distributors:[CustomerID,Name, State, City, District, Address, LinkMan ,LinkTele, MobileNo ,Note]}
	//id=-1增加， 其它修改。
	//输出 []
	@RequestMapping(value="saveDistributor.do",method=RequestMethod.POST)
	public @ResponseBody String saveDistributor(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("分销商【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray distributors = json.getJSONArray("distributors");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		
		try{
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			for(int i=0;i<distributors.size();i++){
				JSONObject distributor = distributors.getJSONObject(i);
												
				if (!distributor.containsKey("CustomerID")){
					throw new Exception("CustomerID数据不存在");
				} 
				
				int customerID = distributor.getInt("CustomerID");
				int parentID = Tools.getCurCustomerID(request.getSession());
				String name = "";
				if (distributor.containsKey("Name")){
					name = distributor.getString("Name");
				}
				if (name.equals("")){
					throw new Exception("名称没输入");
				}
				
				String state = "null";
				if (distributor.containsKey("State")){state = distributor.getString("State");}
				
				String city = "null";
				if (distributor.containsKey("City")){city = distributor.getString("City");}
				
				String district = "null";
				if (distributor.containsKey("District")){district = distributor.getString("District");}
				
				String address = "null";
				if (distributor.containsKey("Address")){address = distributor.getString("Address");}
				
				String linkMan = "null";
				if (distributor.containsKey("LinkMan")){linkMan = distributor.getString("LinkMan");}
				
				String linkTele = "null";
				if (distributor.containsKey("LinkTele")){linkTele = distributor.getString("LinkTele");}
				
				String mobileNo = "null";
				if (distributor.containsKey("MobileNo")){mobileNo = distributor.getString("MobileNo");}
				
				String note = "null";
				if (distributor.containsKey("Note")){note = distributor.getString("Note");}
				
				int newid = distributorPriceService.saveDistributor(customerID,parentID,name, state , city , district,
					address , linkMan , linkTele , mobileNo ,note);		
				
				if (newid>=1){//新增
					idList.add(newid);
				}
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存分销商数据出错: "+e.getMessage());
			logger.info("保存分销商数据出错");
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}	
	
	//查询分销价格
	//qryDistributorPrice.do{ CustomerID, ParentID , GoodsLevel , GoodsKey , SetType }
	@RequestMapping(value="qryDistributorPrice.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDistributorPrice(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
						
			params2.put("CustomerID", (Integer)json.get("CustomerID"));	
			params2.put("ParentID", (Integer)json.get("ParentID"));
			params2.put("GoodsLevel", (Integer)json.get("GoodsLevel"));
			params2.put("SetType", (Integer)json.get("SetType"));			
			params2.put("GoodsKey", (String)json.get("GoodsKey"));
						
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
			
			List<HashMap> result =distributorPriceMapper.qryDistributorPrice(params2);			
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询分销价格出错");
			logger.info("查询分销价格出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
			
	//查询分销价格历史
	//qryDistributorPriceLog.do{ CustomerID, ParentID , GoodsLevel , GoodsKey , SetType }
	@RequestMapping(value="qryDistributorPriceLog.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryDistributorPriceLog(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
						
			params2.put("CustomerID", (Integer)json.get("CustomerID"));	
			params2.put("ParentID", (Integer)json.get("ParentID"));
			params2.put("GoodsLevel", (Integer)json.get("GoodsLevel"));
			params2.put("SetType", (Integer)json.get("SetType"));			
			params2.put("GoodsKey", (String)json.get("GoodsKey"));
						
			PageView view = Tools.getPageView("LogID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
			
			List<HashMap> result =distributorPriceMapper.qryDistributorPriceLog(params2);
			
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);
			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询分销价格历史出错");
			logger.info("查询分销价格历史出错:"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//保存分销价格
	//输入saveDistributorPrice{ distributorPrice:[ ID,CustomerID, ParentID, GoodsLevel, GoodsKey, SetType, Value]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveDistributorPrice.do",method=RequestMethod.POST)
	public @ResponseBody String saveDistributorPrice(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("分销价格【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray distributorPrices = json.getJSONArray("distributorPrices");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		String editor = Tools.getCurLoginCName(request.getSession());
		
		try{
			int curCustomerID = Tools.getCurCustomerID(request.getSession());
			for(int i=0;i<distributorPrices.size();i++){
				JSONObject distributorPrice = distributorPrices.getJSONObject(i);
												
				if (!distributorPrice.containsKey("ID")){
					throw new Exception("ID数据不存在");
				} 
				
				DistributorPrice c = new DistributorPrice();
				int id = distributorPrice.getInt("ID");
				c.setID(id);				
				if (distributorPrice.size()>=2){//
					
					c.setParentID(curCustomerID);
					if (distributorPrice.containsKey("CustomerID")) {c.setCustomerID(distributorPrice.getInt("CustomerID"));}
					if (distributorPrice.containsKey("GoodsLevel")) {c.setGoodsLevel(distributorPrice.getInt("GoodsLevel"));}
					if (distributorPrice.containsKey("SetType")) {c.setSetType(distributorPrice.getInt("SetType"));}
					if (distributorPrice.containsKey("GoodsKey")) {c.setGoodsKey(distributorPrice.getString("GoodsKey"));}
					if (distributorPrice.containsKey("Value")) {c.setValue(distributorPrice.getDouble("Value"));}
													
				}
								
				if (id==-1){//增加
					//检查数据
					if (!distributorPrice.containsKey("GoodsLevel")) {throw new Exception("没有输入商品层级");}
					if (!distributorPrice.containsKey("SetType")) {throw new Exception("没有输入设置类型");}
					if (!distributorPrice.containsKey("Value")) {throw new Exception("没有输入设置值");}
					if (!distributorPrice.containsKey("GoodsKey")) {throw new Exception("没有输入关键字");}
					
					if (!distributorPrice.containsKey("CustomerID")) {c.setCustomerID(-1);}
					
					
					int newid = accountService.GetNewID(300600);
					idList.add(newid);
					c.setID(newid);
					
					//distributorPriceMapper.add(c);
					distributorPriceService.addDistributorPrice(c, editor);
				}else{
					if (distributorPrice.size()<=1){//删除 
						//distributorPriceMapper.delete(id);
						distributorPriceService.delDistributorPrice(id, editor);
					}
					else{//修改
						distributorPriceMapper.update(c);
						distributorPriceService.updateDistributorPrice(c, editor);
					}
				}				
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存分销价格数据出错: "+e.getMessage());
			logger.info("保存分销价格数据出错");
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}
	
	
	//导入分销价格
	@RequestMapping(value="importDistributorPrice.do")
	public @ResponseBody String importDistributorPrice(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JsonResult result = new JsonResult();
		JSONArray resultList = new JSONArray(); //导入的结果
		String editor = Tools.getCurLoginCName(request.getSession());
		int curCustomerID = Tools.getCurCustomerID(request.getSession());

		
		//获取文件
		MultipartHttpServletRequest req = (MultipartHttpServletRequest)request;
		MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();
		HashMap<String,Object> iresult = new HashMap<String,Object>();//导入结果
		Iterator it = map.keySet().iterator();
		String name = ""; 
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
						Tools.bakFile(request , file, "导入分销价格_"+Tools.getCurCustomerName(request.getSession()));
						
						//----取得基本数据
						//分销商列表
						HashMap<String,String>  cmap = new HashMap<String,String>(); 
						List<HashMap> customerList = decCustomerMapper.qryDCustomerList(cmap);
						
												
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
								//检查层级
								String temp = Tools.getListByName(rowData , headList ,"商品层级").trim();
								int goodsLevel = -1;
								if (temp.equals("品牌")){
									goodsLevel = 1;
								}
								else if (temp.equals("品类")){
									goodsLevel = 2;
								}
								else if (temp.equals("货号")){
									goodsLevel = 3;
								}
								else if (temp.equals("SKU")){
									goodsLevel = 4;
								}
								
								if (goodsLevel == -1 ){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "商品层级【"+temp+"】不支持");
									resultList.add(iresult);
									continue;																											
								}
								
								//检查设置类型
								temp = Tools.getListByName(rowData , headList ,"设置类型").trim();
								int setType = -1;
								if (temp.equals("折扣")){
									setType = 0;
								}
								else if (temp.equals("一口价")){
									setType = 1;
								}
								
								if (setType == -1 ){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "设置类型【"+temp+"】不支持");
									resultList.add(iresult);
									continue;																											
								}
								
								//取分销商
								name = Tools.getListByName(rowData , headList ,"分销商");
								temp = Tools.getListValue(customerList , "Name", name , "ID");								
								if (temp == null){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "找不到分销商资料【"+name+"】");
									resultList.add(iresult);
									continue;																		
								}
								int customerID = Integer.parseInt(temp);
								
								HashMap<String, Object> pp = new HashMap<String,Object>();//查询的参数
								int parentID = curCustomerID;
								if (customerID==parentID){//当前分销商，设定总的价格
									customerID=-1;
									pp.put("ParentID", parentID);
								}
								else{
									//检查分销商关系
									temp = Tools.getListValue(customerList , "Name", name , "ParentID");								
									if (!temp.equals(Integer.toString(curCustomerID))){
										errCount = errCount + 1;
										iresult.put("errorCode", 1);
										iresult.put("msg", "分销商【"+name+"】与当前分销商非上下级关系");
										resultList.add(iresult);
										continue;																											
									}		
									pp.put("CustomerID", customerID);
								}
																
								//查价格
								String goodsKey = Tools.getListByName(rowData , headList ,"商品关键字").trim();								
								if (goodsKey.equals("")){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "【"+name+"】商品关键字【"+goodsKey+"】为空");
									resultList.add(iresult);
									continue;																											
									
								}
								
								temp = Tools.getListByName(rowData , headList ,"设置值").trim();
								double value = 0 ;
								try{
									value = Double.parseDouble(temp) ;									
								}
								catch(Exception e){
									errCount = errCount + 1;
									iresult.put("errorCode", 1);
									iresult.put("msg", "【"+name+"】设置值【"+temp+"】错误");
									resultList.add(iresult);									
								}
								
								pp.put("GoodsLevel", goodsLevel);
								pp.put("GoodsKey", goodsKey);
								pp.put("SetType", setType);
								pp.put("Value", value);
								
								List<HashMap> dp =distributorPriceMapper.qryDistributorPrice(pp);
								DistributorPrice c = new DistributorPrice();
								c.setCustomerID(customerID);
								c.setParentID(parentID);
								c.setGoodsKey(goodsKey);
								c.setGoodsLevel(goodsLevel);
								c.setGoodsLevel(setType);
								c.setValue(value);
								
								int id = -1;
								if (dp.size()>=1){//修改
									HashMap o = dp.get(0);
									id = Integer.parseInt(o.get("ID").toString()) ;
									c.setID(id);
									distributorPriceMapper.update(c);
								}
								else{
									id = accountService.GetNewID(300600);
									c.setID(id);
									distributorPriceMapper.add(c);	
								}								
								
								succCount=succCount+1;
							}
						}
					}catch(Exception e){
						errCount = errCount + 1;						
						logger.info("导入分销价格【"+name+"】出错: "+e.getMessage());
						iresult.put("errorCode", 100);
						iresult.put("msg", "导入分销价格【"+name+"】失败:"+e.getMessage());						
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
	
	//导出分销价格
	@RequestMapping(value="exportDistributorPrice.do")
	public @ResponseBody String  exportDistributorPrice(HttpServletRequest request,HttpServletResponse response){
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
			params2.put("CustomerID", Tools.getCurCustomerID(request.getSession()));			
			List<HashMap>  price = distributorPriceMapper.qryAllDistributorPrice(params2);
			
			//导出
			String header = "分销商,商品层级,商品关键字,设置类型,设置值";
			String fields = "Name,GoodsLevel,GoodsKey,SetType,Value";
			String tempfile = request.getRealPath("/temp/"+"分销价格"+".xls");
			ret= POIUtils.exportToExcelHeadJxlMap(tempfile,response,request,"分销价格",price,fields,header,port);
			
			re.setErrorCode(0);
			re.setData(ret);
			logger.info("链接："+ret);
			

		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询分销价格出错");
			logger.info("查询分销价格出错："+ breakPoint +" "+ e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}		
	
	//导出分销商
	@RequestMapping(value="exportDistributor.do")
	public @ResponseBody String  exportDistributor(HttpServletRequest request,HttpServletResponse response){
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
			params2.put("CustomerID", Tools.getCurCustomerID(request.getSession()));			
			List<HashMap>  customer = distributorPriceMapper.qrySubDistributor(params2);
			
			//导出
						
			String header = "上级分销商,分销商,省,市,区,地址,联系人,联系电话,手机,备注,状态";
			String fields = "ParentName,Name,State,City,District,Address,LinkMan,LinkTele,MobileNo,Note,Enable";
			String tempfile = request.getRealPath("/temp/"+"分销商"+".xls");
			ret= POIUtils.exportToExcelHeadJxlMap(tempfile,response,request,"分销商",customer,fields,header,port);
			
			re.setErrorCode(0);
			re.setData(ret);
			logger.info("链接："+ret);
			

		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询分销商出错");
			logger.info("查询分销商出错："+ breakPoint +" "+ e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}			
				
}