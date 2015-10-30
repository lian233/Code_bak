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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.domain.DecOrderItem;
import com.wofu.fenxiao.domain.RefundSheet;
import com.wofu.fenxiao.domain.RefundSheetItem;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.mapping.RefundSheetMapper;
import com.wofu.fenxiao.mapping.RefundSheetItemMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.RefundSheetService;
import com.wofu.fenxiao.service.MenuService;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.Tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;

@Controller
public class RefundController extends BaseController{
	
	@Value("#{configProperties[server_port]}")
	private int port;
	
	//日志对象
	Logger logger = Logger.getLogger(this.getClass());
	//服务层接口组件
	@Autowired  //这里自动生成服务层组件对象
	private RefundSheetService refundSheetsService;
	
	@Autowired 
	private MenuService menuService;
	
	@Autowired 
	private LoginService accountService;
	
	@Autowired
	private RefundSheetMapper refundSheetMapper;
	
	@Autowired
	private RefundSheetItemMapper refundSheetItemMapper;

	
	
	@Autowired 
	private LoginService loginService;	
	
	
	
	//店铺资料页面 初始化数据
	//返回当前登录信息、菜单列表，客户分组列表、快递分组列表数据。
	@RequestMapping(value="iniRefundSheet.do",method=RequestMethod.GET)
	@ResponseBody 
	public String iniRefundSheet(HttpSession session){
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

			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setData("取基本数据出错：" + e.getMessage());
		}
		
		String ret="var allData ="+obj.toString();
		return ret;
		
	}		
	
	//查询退货单
	//qryRefundSheet{flag,delivery, deliverySheetID,timeType, beginTime,endTime}
	@RequestMapping(value="qryRefundSheet.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryRefundSheet(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			params2.put("flag", (Integer)json.get("flag"));
			if (json.containsKey("flag")){
				if (json.getInt("flag")<97){params2.put("bak", "0");}
				else {params2.put("bak", "");}
			}
			
			params2.put("delivery", (String)json.get("delivery"));
			params2.put("deliverySheetID", (String)json.get("deliverySheetID"));
			params2.put("range", (Integer)json.get("range"));

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
			
			int customerID = Tools.getCurCustomerID(request.getSession());
			if (customerID >0){
				params2.put("customerID", customerID);
			}
			
			
			PageView view = Tools.getPageView("ID", json.get("pn")!=null?(Integer)json.get("pn"):0, 
					json.get("pageSize")!=null?(Integer)json.get("pageSize"):0);
			params2.put("pageview",view);//分页参数
								
			logger.info("准备查退货单");
			List<RefundSheet> result = refundSheetMapper.queryRefundSheet(params2);	
			logger.info("查退货单OK");
			re.setErrorCode(0);
			re.setData(result);
			view.setPage(view.getPage()-1);
			re.setPageInfo(view);			
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询退货单出错：" + e.getMessage());	
			e.printStackTrace();
		}
		return JSONObject.fromObject(re).toString();
	}
	
	//查询单个退货明细数据
	@RequestMapping(value="qryRefundSheetItem.do",method=RequestMethod.POST)
	@ResponseBody
	public String qryRefundSheetItem(HttpServletRequest request ,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JsonResult re = new JsonResult();//
		try{
			HashMap<String, Object> params2 = new HashMap<String,Object>();
			
			if (json.containsKey("flag")){
				if (json.getInt("flag")<97){params2.put("bak", "0");}
				else {params2.put("bak", "");}
			}
			params2.put("sheetID", (String)json.get("sheetID"));			
			
			List<RefundSheetItem> result = refundSheetItemMapper.qryRefundSheetItem(params2) ;
			re.setErrorCode(0);
			re.setData(result);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询退货单明细出错"+e.getMessage());
		}
		return JSONObject.fromObject(re).toString();
	}	
	
	
	//保存退货单数据
	//输入saveRefundSheet.do{RefundSheets:[ ID, Delivery, DeliverySheetID, Note]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveRefundSheet.do",method=RequestMethod.POST)
	public @ResponseBody String saveRefundSheet(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("退货单【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray refunds = json.getJSONArray("RefundSheets");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		try{
			for(int i=0;i<refunds.size();i++){
				JSONObject refund = refunds.getJSONObject(i);
				
				RefundSheet r = new RefundSheet();
				r.setID(refund.getInt("ID"));
				if (refund.size()>=2){//
					if (refund.containsKey("Delivery")) {r.setDelivery(refund.getString("Delivery"));}
					if (refund.containsKey("DeliverySheetID")) {r.setDeliverySheetID(refund.getString("DeliverySheetID"));}
					if (refund.containsKey("Note")) {r.setNote(refund.getString("Note"));}
					
				}
								
				if (refund.getInt("ID")==-1){//增加
					//取得最大的ID
					r.setFlag(0);
					r.setCustomerID(Tools.getCurCustomerID(request.getSession()));
					r.setEditTime(new Date());
					r.setEditor(Tools.getCurLoginCName(request.getSession()));//登录人
					int newid = loginService.GetNewID(400501);
					r.setID(newid);
					
					//取得单号
					String sheetid = loginService.GetNewSheetID(400500);
					r.setSheetID(sheetid);
					
					
					refundSheetMapper.add(r);
					
					HashMap<String,Object> idmap = new HashMap<String,Object>();
					idmap.put("ID", newid);
					idmap.put("SheetID", sheetid);
					idList.add(idmap);	
										
				}else{
					if (refund.size()<=1){//删除 
						//decOrderService.delete(c.getId());
						refundSheetMapper.delete(refund.getInt("ID"));
					}
					else{//修改						
						r.setEditTime(new Date());
						r.setEditor(Tools.getCurLoginCName(request.getSession()));//登录人						
						refundSheetMapper.update(r);
					}
				}				
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存退货单数据出错："+e.getMessage());
			logger.info("保存退货单数据出错："+e.getMessage());
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}	
	
	
	//保存退货单明细数据
	//输入{ RefundSheetItems:[ID, OuterSkuID, Title, NotifyQty, NotifyPrice,Note]}
	//id=-1增加， 只有ID删除，其它修改。
	//输出 []
	@RequestMapping(value="saveRefundSheetItem.do",method=RequestMethod.POST)
	public @ResponseBody String saveRefundSheetItem(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject json=null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			logger.info("退货单明细【取参数出错】");
			e.printStackTrace();
		}
		
		JSONArray items = json.getJSONArray("RefundSheetItems");
		JsonResult re = new JsonResult();
		JSONArray idList = new JSONArray();
		try{
			for(int i=0;i<items.size();i++){
				JSONObject item = items.getJSONObject(i);
				
				RefundSheetItem r = new RefundSheetItem();
				r.setID(item.getInt("ID"));
				if (item.size()>=2){//
					if (item.containsKey("SheetID")) {r.setSheetID(item.getString("SheetID"));}
					if (item.containsKey("OuterSkuID")) {r.setOuterSkuID(item.getString("OuterSkuID"));}
					if (item.containsKey("NotifyPrice")) {r.setNotifyPrice(item.getDouble("NotifyPrice"));}
					if (item.containsKey("NotifyQty")) {r.setNotifyQty(item.getInt("NotifyQty"));}
					if (item.containsKey("Note")) {r.setNote(item.getString("Note"));}
										
				}
								
				int customerID = Tools.getCurCustomerID(request.getSession()); 
				String sheetID = r.getSheetID();
				if (item.getInt("ID")==-1){//增加
					if (!item.containsKey("SheetID")){
						throw new Exception("没有输入订单编号"); 
					}
					//取得最大的ID
					int newid = loginService.GetNewID(400502); 
					r.setID(newid);	
					refundSheetItemMapper.add(r);	
					idList.add(newid);
				}else{
					if (item.size()<=1){//删除 
						//DecOrderItem oi = decOrderService.getItemById(c.getId() , "0" ,  c.getFront());
						//if (oi!=null){sheetID = oi.getSheetID();}						
						//decOrderService.deleteItem(c.getId());
						refundSheetItemMapper.delete(item.getInt("ID"));						
					}
					else{//修改
						refundSheetItemMapper.update(r);
					}
				}
				
				//更新单头信息
				HashMap<String, Object> uparam = new HashMap<String,Object>();
				uparam.put("SheetID", sheetID);
				refundSheetMapper.updateRefundSheetSta(uparam);
			}
			
			re.setErrorCode(0);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("保存退货单明细数据出错："+e.getMessage());
			e.printStackTrace();
		}
		
		re.setData(idList);
		return JSONObject.fromObject(re).toString();
	}
		
	//审核退货单    
	@RequestMapping(value="checkRefundSheet.do")
	public @ResponseBody String checkRefundSheet(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray checkResult =new JSONArray() ;
		//
		
		JsonResult re = new JsonResult();
		try {
			//取得退货单列表
			JSONArray ids = json.getJSONArray("RefundSheets");
			
			
			for(int i=0;i<ids.size();i++){
				JSONObject r =  new JSONObject();
				try{
					int id = Integer.parseInt(ids.get(i).toString());
					HashMap<String, Object> param = new HashMap<String,Object>();					 
					param.put("ID", id);		
					param.put("Editor", Tools.getCurLoginCName(request.getSession()));					
					String msg = refundSheetsService.ifRefundToCustomerRetNote(param);
					
					//String msg = param.get("Msg").toString();	
					logger.info("msg:"+msg );
					r.put("ID", id);
					if (msg.equals("")){
						r.put("errorCode", 0);						
					}else{
						r.put("errorCode", 1);
						r.put("msg",msg);
					}					
					
				}catch (Exception e) { 
					r.put("errorCode", -1);
					r.put("msg",e.getMessage());
				}
				checkResult.add(r);
			}
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(1)	;
			re.setMsg("审核退货单失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
				
		re.setData(checkResult);
		return JSONObject.fromObject(re).toString();
	}			
	
	//取消退货单    
	@RequestMapping(value="cancelRefundSheet.do")
	public @ResponseBody String cancelRefundSheet(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		JSONObject json=null;
		try {//这里直接把请求参数转成json对象
			json = Common.getRequestJsonObject(request.getInputStream(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray checkResult =new JSONArray() ;
		//
		
		JsonResult re = new JsonResult();
		try {
			//取得退货单列表
			JSONArray ids = json.getJSONArray("RefundSheets");
			
			
			for(int i=0;i<ids.size();i++){
				JSONObject r =  new JSONObject();
				try{
					int id = Integer.parseInt(ids.get(i).toString());
					HashMap<String, Object> param = new HashMap<String,Object>();					 
					param.put("ID", id);		
					param.put("Checker", Tools.getCurLoginCName(request.getSession()));					
					String msg = refundSheetsService.tlCancelRefund(param);
					
					r.put("ID", id);
					if (msg.equals("")){
						r.put("errorCode", 0);						
					}else{
						r.put("errorCode", 1);
						r.put("msg",msg);
					}					
					
				}catch (Exception e) { 
					r.put("errorCode", -1);
					r.put("msg",e.getMessage());
				}
				checkResult.add(r);
			}
			re.setErrorCode(0);
		} catch (Exception e) { 
			re.setErrorCode(1)	;
			re.setMsg("取消退货单失败【"+e.getMessage() +"】");	
			e.printStackTrace();
		}
				
		re.setData(checkResult);
		return JSONObject.fromObject(re).toString();
	}			

	//导出退货单
	//exportRefundSheet{flag,delivery, deliverySheetID,timeType, beginTime,endTime}
	@RequestMapping(value="exportRefundSheet.do",method=RequestMethod.POST)
	@ResponseBody
	public String exportRefundSheet(HttpServletRequest request ,HttpServletResponse response) throws Exception{
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
			params2.put("flag", (Integer)json.get("flag"));
			if (json.containsKey("flag")){
				if (json.getInt("flag")<97){params2.put("bak", "0");}
				else {params2.put("bak", "");}
			}
			
			params2.put("delivery", (String)json.get("delivery"));
			params2.put("deliverySheetID", (String)json.get("deliverySheetID"));
			params2.put("range", (Integer)json.get("range"));

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
			
			int customerID = Tools.getCurCustomerID(request.getSession());
			if (customerID >0){
				params2.put("customerID", customerID);
			}
			
			List<HashMap> refunds = refundSheetMapper.qryStaRefund(params2);
			//logger.info( JSONObject.fromObject(refunds).toString());
			//导出
			String header = "单号,快递公司,快递单号,商品总数量,商品总金额,实退总数量,实退总金额,备注,SKU,通知数,通知退货价,实退数,实际退货价,明细备注";
			String fields = "SheetID,Delivery,DeliverySheetID,TotalQty,TotalAmount,TotalRefundQty,TotalRefundAmount,Note,OuterSkuID,NotifyQty,NotifyPrice,FactQty,FactPrice,ItemNote";
			String tempfile = request.getRealPath("/temp/"+"退货单数据"+".xls");
			
			HashMap h = refunds.get(0);
			logger.info("金额"+h.get("TotalAmount").toString()) ;
			logger.info("单价"+h.get("NotifyPrice").toString()) ;
			
			ret= POIUtils.exportToExcelHeadJxlMap(tempfile,response,request,"退货单数据",refunds,fields,header,port);
			
			
			re.setErrorCode(0);
			re.setData(ret);
		}catch(Exception e){
			re.setErrorCode(1);
			re.setMsg("查询退货单出错：" + e.getMessage());	
			e.printStackTrace();
		}
		return JSONObject.fromObject(re).toString();
	}
		
}
