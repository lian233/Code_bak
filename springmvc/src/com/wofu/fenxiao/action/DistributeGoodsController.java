package com.wofu.fenxiao.action;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Date;
import java.util.ArrayList;
import com.wofu.fenxiao.utils.POIUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import com.wofu.fenxiao.domain.DistributeGoods;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.mapping.DistributeGoodsMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DistributeGoodsService;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.Tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;

@Controller
public class DistributeGoodsController extends BaseController{
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private DistributeGoodsService distributeGoodsService;
	
	@Autowired 
	private MenuService menuService;
	
	@Autowired 
	private LoginService accountService;
	
	@Autowired
	private DistributeGoodsMapper distributeGoodsMapper;
	
	
	
	
	//店铺资料页面 初始化数据
	//返回当前登录信息、菜单列表，客户分组列表、快递分组列表数据。
	@RequestMapping(value="iniDistributeGoodsData.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniDistributeGoodsData(HttpSession session){
		JsonResult re = new JsonResult();
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			
			
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
			
			//取得品牌
			List<HashMap> brand=null;
			try{
				HashMap<String,Object> p = new HashMap<String,Object>();
				
				brand = distributeGoodsService.qryBrand(p);
			} catch (Exception e) {
				throw new Exception("取品牌出错："+ e.getMessage());
			}			
			obj.put("brand", brand);

			//取得线条
			List<HashMap> productLine=null;
			try{
				HashMap<String,Object> p = new HashMap<String,Object>();
				
				productLine = distributeGoodsService.qryProductLine(p);
			} catch (Exception e) {
				throw new Exception("取线条出错："+ e.getMessage());
			}			
			obj.put("productLine", productLine);

			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setData("取基本数据出错：" + e.getMessage());
		}
		
		String ret="var allData ="+obj.toString();
		return ret;
		
	}		
	
	//查询分销商品
	//queryDistributeGoods.do{brandID(int), productLineID(int),dept,customNo,name,Status(int), isDistribute (int)}
	@RequestMapping(value="queryDistributeGoods.do",method=RequestMethod.POST)
	@ResponseBody
	public String queryDistributeGoods(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("BrandID", (Integer)json.get("BrandID"));
			params2.put("ProductLineID", (Integer)json.get("ProductLineID"));
			params2.put("Dept", (String)json.get("Dept"));
			params2.put("CustomNo", (String)json.get("CustomNo"));
			params2.put("GoodsName", (String)json.get("GoodsName"));
			params2.put("Status", (Integer)json.get("Status"));
			params2.put("IsDistribute", (Integer)json.get("IsDistribute"));
			String dc=accountService.GetCustomerConfig("分销仓库列表", "", 0, 0);
			String temp = accountService.GetCustomerConfig("同步库存比例", "", 0, 0);
			try{
				Float.parseFloat(temp);
			}
			catch(Exception ee){
				temp ="100";
			}
			
			dc = "dbo.TL_GetGoodsStockSum('"+dc+"',GoodsID) * " + temp + "/100";
			params2.put("useQty", dc);
			int customerID = Tools.getCurCustomerID(request.getSession());
			
			PageView view = Tools.getPageView("GoodsID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
						
			List<HashMap> result = null;
			
			if (customerID <= 0){
				result = distributeGoodsService.queryDistributeGoods(params2);
			}
			else{
				params2.put("CustomerID", customerID);
				result = distributeGoodsMapper.queryCustomerDistributeGoods(params2);
			}
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询分销商品资料出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}
			
	//增加分销商品
	//addDistributeGoods.do{goods:[ productLineID, goodsID]}
	@RequestMapping(value="addDistributeGoods.do",method=RequestMethod.POST)
	@ResponseBody
	public String addDistributeGoods(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JsonResult re = new JsonResult();//
		try{
			JSONArray goods = json.getJSONArray("goods");
			if (goods==null){
				throw new Exception("找不到需增加的商品列表"); 
			}
			
			for(int i=0;i<goods.size();i++){
				JSONObject good = goods.getJSONObject(i);
				DistributeGoods g = new DistributeGoods();
								
				g.setGoodsID(good.getInt("GoodsID"));
				g.setProductLineID(good.getInt("ProductLineID"));
				g.setStatus(1);
				distributeGoodsService.add(g);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("增加分销商品资料出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}
				
	//保存分销商品
	//saveDistributeGoods.do{goods:[ ProductLineID, GoodsID, Title, GoodsUrl,Note]}
	@RequestMapping(value="saveDistributeGoods.do",method=RequestMethod.POST)
	@ResponseBody
	public String saveDistributeGoods(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JsonResult re = new JsonResult();//
		try{
			JSONArray goods = json.getJSONArray("goods");
			if (goods==null){
				throw new Exception("找不到需保存的商品列表"); 
			}
			
			for(int i=0;i<goods.size();i++){
				JSONObject good = goods.getJSONObject(i);
				DistributeGoods g = new DistributeGoods();
								
				g.setGoodsID(good.getInt("GoodsID"));
				g.setProductLineID(good.getInt("ProductLineID"));
				g.setTitle(good.getString("Title"));
				g.setGoodsUrl(good.getString("GoodsUrl"));
				g.setBasePrice(good.getDouble("BasePrice"));
				g.setPrice(good.getDouble("Price"));
				g.setNote(good.getString("Note"));
				distributeGoodsService.update(g);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存分销商品资料出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}
		
	//设置分销商品状态
	//setDistributeGoodsStatus.do{goods:[ GoodsID],Status:int}
	@RequestMapping(value="setDistributeGoodsStatus.do",method=RequestMethod.POST)
	@ResponseBody
	public String setDistributeGoodsStatus(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JsonResult re = new JsonResult();//
		try{
			JSONArray goods = json.getJSONArray("goods");			
			if (goods==null){
				throw new Exception("找不到需保存的商品列表"); 
			}
			
			int status = json.getInt("Status");
			
			for(int i=0;i<goods.size();i++){								
				JSONObject good = goods.getJSONObject(i);
				
				HashMap<String, Object> params2 = new HashMap<String,Object>();
				params2.put("GoodsID", good.getInt("GoodsID"));
				params2.put("ProductLineID", good.getInt("ProductLineID"));
				params2.put("Status", status);
				
				distributeGoodsMapper.updateStatus(params2);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("设置分销商品资料状态出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}	
	
	//删除分销商品
	//deleteDistributeGoods.do{goods:[ GoodsID],Status:int}
	@RequestMapping(value="deleteDistributeGoods.do",method=RequestMethod.POST)
	@ResponseBody
	public String deleteDistributeGoods(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JsonResult re = new JsonResult();//
		try{
			JSONArray goods = json.getJSONArray("goods");			
			if (goods==null){
				throw new Exception("找不到需删除的商品列表"); 
			}
			
			for(int i=0;i<goods.size();i++){								
				JSONObject good = goods.getJSONObject(i);
				
				HashMap<String, Object> params2 = new HashMap<String,Object>();
				params2.put("GoodsID", good.getInt("GoodsID"));
				params2.put("ProductLineID", good.getInt("ProductLineID"));
				
				distributeGoodsMapper.delete(params2);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("删除分销商品资料出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}		

	//设置分销商品图片
	//setDistributeGoodsImg.do{GoodsID,file}
	@RequestMapping(value="setDistributeGoodsImg.do")
	public @ResponseBody String setDistributeGoodsImg(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JsonResult result = new JsonResult();
		
		//获取普通表单数据
		Map<String,String[]> paramemap = request.getParameterMap();		
		String goodsID = paramemap.get("GoodsID")[0]; 
		HashMap<String, Object> params2 = new HashMap<String,Object>();
		params2.put("GoodsID", goodsID);
		distributeGoodsMapper.updateImaUrl(params2);		
		
		
		//获取文件
		MultipartHttpServletRequest req = (MultipartHttpServletRequest)request;
		MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();
		HashMap<String,Object> iresult = new HashMap<String,Object>();//导入结果
		Iterator it = map.keySet().iterator();
		for(;it.hasNext();){
			String filename = (String)it.next();
			//获取文件流
			List<MultipartFile> files =(List<MultipartFile>) map.get(filename);
			for(int j=0;j<files.size();j++){
				MultipartFile file = files.get(j);
				
				try{
					InputStream is = file.getInputStream();
					String dirName = request.getRealPath("goodsImages/"+goodsID+"/");
					File d = new File(dirName);
					d.mkdirs();
										
					File saveFile = new File(dirName+"/main.jpg");
					
					if(is.getClass()==ByteArrayInputStream.class){
						ByteArrayInputStream bis =(ByteArrayInputStream)is;										
						FileOutputStream fos = new FileOutputStream(saveFile);
						byte[] bytes = new byte[1024*2];
						int len=0;
						while((len=bis.read(bytes))!=-1){
							fos.write(bytes);
						}
						fos.flush();
						fos.close();
						bis.close();
					}else if(is.getClass()==FileInputStream.class){
						FileInputStream in =(FileInputStream) file.getInputStream();
						FileOutputStream fos = new FileOutputStream(saveFile);
						byte[] bytes = new byte[1024*2];
						int len=0;
						while((len=in.read(bytes))!=-1){
							fos.write(bytes);
						}
						fos.flush();
						fos.close();
						in.close();
					}					
					
					result.setErrorCode(0);
				}catch(Exception e){
					result.setErrorCode(0);
					result.setMsg("保存文件出错:"+e.getMessage());
				}				
			}			
		}
		return JSONObject.fromObject(result).toString();
		
	}
	
	//查询SKU及库存
	//querySkuInventory.do{GoodsID}
	@RequestMapping(value="querySkuInventory.do",method=RequestMethod.POST)
	@ResponseBody
	public String querySkuInventory(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("GoodsID", (Integer)json.get("GoodsID"));
			//取仓库
			String dc = accountService.GetCustomerConfig("分销仓库列表", "", 0, 0);
			dc= "'"+dc.replace(",", "','")+"'";
			String temp = accountService.GetCustomerConfig("同步库存比例", "", 0, 0);
			try{
				Float.parseFloat(temp);
			}
			catch(Exception ee){
				temp ="100";
			}
			
			params2.put("dc", dc);
			params2.put("rate", temp+"/100");
			//params2.put("dc", "'571N0L'");
			//params2.put("useQty", 0);
			
						
			List<HashMap> result = distributeGoodsService.querySkuInventory(params2);
			re.setErrorCode(0);
			re.setData(result);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询分销商品资料出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}


	//分销客户级别登录信息、菜单列表，客户分组列表、快递分组列表数据。
	@RequestMapping(value="iniCustomerProductGrade.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniCustomerProductGrade(HttpSession session){
		JsonResult re = new JsonResult();
		JSONObject obj = new JSONObject();
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			if (null==login){ //如果空，测试时置默认值 ，实际运行时报错
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login);
			
			
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
			
			//取得线条
			List<HashMap> productLine=null;
			try{
				HashMap<String,Object> p = new HashMap<String,Object>();
				
				productLine = distributeGoodsService.qryProductLine(p);
			} catch (Exception e) {
				throw new Exception("取线条出错："+ e.getMessage());
			}			
			obj.put("productLine", productLine);

			//取得等级
			List<HashMap> grade=null;
			try{
				HashMap<String,Object> p = new HashMap<String,Object>();
				
				grade = distributeGoodsMapper.qryGrade(p);
			} catch (Exception e) {
				throw new Exception("取等级出错："+ e.getMessage());
			}			
			obj.put("grade", grade);
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setData("取基本数据出错：" + e.getMessage());
		}
		
		String ret="var allData ="+obj.toString();
		return ret;
		
	}			

	
	//查询分销客户分销产品线
	//qryCustomerProductGrade.do{brandID(int), productLineID(int),dept,customNo,name,Status(int), isDistribute (int)}
	@RequestMapping(value="qryCustomerProductGrade.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryCustomerProductGrade(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			int customerID = Tools.getCurCustomerID(request.getSession());
			
			if (customerID>0){
				params2.put("customerID", customerID);
			}
			params2.put("customerName", (String)json.get("customerName"));
			params2.put("productLineID", (Integer)json.get("productLineID"));
			params2.put("gradeID", (Integer)json.get("gradeID"));			
			params2.put("IsDistribute", (Integer)json.get("IsDistribute"));

			PageView view = Tools.getPageView("CustomerID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
						
			List<HashMap> result = distributeGoodsMapper.queryCustomerProductGrade(params2);
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询分销客户分销产品线出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}

	//客户加入分销
	//addCustomerProductGrade.do{Customers:[ID], ProductLineID, GradeID }
	@RequestMapping(value="addCustomerProductGrade.do",method=RequestMethod.POST)
	@ResponseBody
	public String addCustomerProductGrade(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JsonResult re = new JsonResult();//
		try{
			JSONArray customers = json.getJSONArray("Customers");			
			if (customers==null){
				throw new Exception("没有客户列表"); 
			}
			
			int ProductLineID = json.getInt("ProductLineID");
			int GradeID = json.getInt("GradeID");
			
			for(int i=0;i<customers.size();i++){								
				int customerID = customers.getInt(i);
				//删除再增加
				HashMap<String, Object> dp = new HashMap<String,Object>();
				dp.put("CustomerID", customerID);
				dp.put("ProductLineID", ProductLineID);
				distributeGoodsMapper.removeCustomerProductGrade(dp);
				
				//增加
				dp.put("GradeID", GradeID);
				distributeGoodsMapper.addCustomerProductGrade(dp);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("客户加入分销出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}	
	
	//取消客户分销
	//delCustomerProductGrade.do{[{CustomerID, ProductLineID}] }
	@RequestMapping(value="delCustomerProductGrade.do",method=RequestMethod.POST)
	@ResponseBody
	public String delCustomerProductGrade(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JsonResult re = new JsonResult();//
		try{
			JSONArray customers = json.getJSONArray("Customers");			
			if (customers==null){
				throw new Exception("没有客户列表"); 
			}
						
			for(int i=0;i<customers.size();i++){								
				JSONObject customer = customers.getJSONObject(i);
				
				//删除
				HashMap<String, Object> dp = new HashMap<String,Object>();
				dp.put("CustomerID", Integer.parseInt(customer.get("CustomerID").toString()));
				dp.put("ProductLineID", Integer.parseInt(customer.get("ProductLineID").toString()));
				distributeGoodsMapper.removeCustomerProductGrade(dp);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			e.printStackTrace();
			re.setErrorCode(1);
			re.setMsg("取消客户分销出错：" + e.getMessage());			
		}
		return JSONObject.fromObject(re).toString();
	}	
	
	//导入分销商品
	@RequestMapping(value="importDistributeGoods.do")
	public @ResponseBody String importDistributeGoods(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JsonResult result = new JsonResult();
		
		//获取普通表单数据
		/*
		Map<String,String[]> paramemap = request.getParameterMap();
		for(Iterator it = paramemap.keySet().iterator();it.hasNext();){
			String paramsName = (String)it.next();
			logger.info(paramsName+" "+paramemap.get(paramsName)[0]);
		}*/
		
		//取得产品线
		List<HashMap> productLine=null;
		try{
			HashMap<String,Object> p = new HashMap<String,Object>();
			
			productLine = distributeGoodsService.qryProductLine(p);
		} catch (Exception e) {
			throw new Exception("取线条出错："+ e.getMessage());
		}			
		
		
		//获取文件
		logger.info("准备获取文件");
		MultipartHttpServletRequest req = (MultipartHttpServletRequest)request;
		logger.info("获取文件完成");
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
						Tools.bakFile(request , file, "导入分销商品_");
						

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
								
								String line = Tools.getListByName(rowData , headList ,"产品线");								
								if ((line.equals(""))) {
									throw new Exception("没有产品线数据");								
								}
								
								//取产品线
								String temp = Tools.getListValue(productLine , "Name", line , "ID");
								if (temp == null){
									iresult.put("errorCode", 1);
									iresult.put("msg", "找不到产品线【"+temp+"】");
									resultList.add(result);
									continue;																		
								}								
								int lineid = Integer.parseInt(temp);
								
								String customno = Tools.getListByName(rowData , headList ,"货号");
								if ((customno.equals(""))) {
									throw new Exception("没有货号数据");																		
								}
															
								//取商品资料									
								HashMap<String, Object> fgoodsparams = new HashMap<String,Object>();
								fgoodsparams.put("CustomNo", customno);
								List<HashMap> goods=distributeGoodsMapper.queryGoods(fgoodsparams);
								if (goods.size()<=0){
									iresult.put("errorCode", 1);
									iresult.put("msg", "找不到商品资料【"+customno+"】");
									resultList.add(result);
									continue;																											
								}
								
								
								int goodsID = 0 ;
								try{
									goodsID = Integer.parseInt(goods.get(0).get("GoodsID").toString());
								}catch(Exception e){
									iresult.put("errorCode", 10);
									iresult.put("msg", "取商品资料【"+customno+"】异常"+e.getMessage());
									resultList.add(result);
									continue;																																				
								}
								
								//检查是否存在
								HashMap<String, Object> findparams = new HashMap<String,Object>();	
								findparams.put("GoodsID", goodsID);
								findparams.put("ProductLineID",lineid);
								DistributeGoods dg= distributeGoodsMapper.getById(findparams);
								boolean newFlag = false;
								if (dg == null){
									logger.info("分销商品不存在");
									newFlag = true;
									//增加
									dg = new DistributeGoods();									
									dg.setGoodsID(goodsID);
									dg.setProductLineID(lineid);
									dg.setStatus(1);	
								}else{
									logger.info("分销商品存在");
								}
								
								dg.setTitle(Tools.getListByName(rowData , headList ,"标题"));
								dg.setImaUrl(Tools.getListByName(rowData , headList ,"本地图片链接"));
								dg.setGoodsUrl(Tools.getListByName(rowData , headList ,"网站链接"));
								temp = Tools.getListByName(rowData , headList ,"网络牌价");
								if (temp!=""){
									try{
										dg.setBasePrice(Double.parseDouble(temp));
									}catch(Exception e){
										iresult.put("errorCode", 12);
										iresult.put("msg", "取商品资料【"+customno+"】网络牌价【"+temp+"】异常");
										resultList.add(result);
										continue;																																															
									}
								}
								
								temp = Tools.getListByName(rowData , headList ,"售价");
								if (temp!=""){
									try{
										dg.setPrice(Double.parseDouble(temp));
									}catch(Exception e){
										iresult.put("errorCode", 12);
										iresult.put("msg", "取商品资料【"+customno+"】售价【"+temp+"】异常");
										resultList.add(result);
										continue;																																															
									}
								}								
								
								dg.setNote(Tools.getListByName(rowData , headList ,"备注"));
								if (newFlag){
									distributeGoodsService.add(dg);
								}else{
									distributeGoodsService.update(dg);
								}
							}
						}//end for 
						result.setErrorCode(0);
						result.setData(resultList);
					}catch(Exception e){
						logger.info("导入文件出错: "+e.getMessage());
						result.setErrorCode(1);
						result.setMsg("导入文件失败："+e.getMessage());
					}
					
				}
			}
		}
		return JSONObject.fromObject(result).toString();
	}	
		
		
}
