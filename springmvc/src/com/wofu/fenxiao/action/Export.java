package com.wofu.fenxiao.action;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.service.DecCustomerService;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.POIUtils;
@Controller
public class Export {
	Logger logger = Logger.getLogger(Export.class);
	@Value("#{configProperties[server_port]}")
	private int port;
	@Autowired
	DecCustomerService deccustomerService;
	@RequestMapping(value="exportexcel.do")
	public @ResponseBody String exportExcel(HttpServletRequest request,HttpServletResponse response){
		JSONObject json=null;
		JsonResult result = new JsonResult();
		try{
			json= Common.getRequestJsonObject(request.getInputStream(), "utf-8");
			//excel第一列数据
			String header = json.getString("header");
			List<DecCustomer> customers = deccustomerService.qryCustomer(null);
			POIUtils.exportToExcelT(response,"客户列表",customers,header);
			result.setErrorCode(0);
		}catch(Exception e){
			logger.info("导出文件出错"+e.getMessage());
			result.setErrorCode(1);
		}
		return JSONObject.fromObject(result).toString();
		
	}
	
	
	@RequestMapping(value="exportexceljxl.do")
	public @ResponseBody String  exportToExcelJxl(HttpServletRequest request,HttpServletResponse response){
		JsonResult result = new JsonResult();
		String tempxlsfile = request.getRealPath(
				"/temp/" + "客户列表" + ".xls");
		System.out.println("tempfile: "+tempxlsfile);
		String test="";
		JSONObject json=null;
		try{
			json= Common.getRequestJsonObject(request.getInputStream(), "utf-8");
			//excel第一列数据
			String header = json.getString("header");
			System.out.println("header: "+header);
			List<DecCustomer> customers = deccustomerService.qryCustomer(null);
			test = POIUtils.exportToExcelJxl(tempxlsfile,response,request,"客户列表",customers,header,port);
			System.out.println("path: "+test);
			result.setData(test);
			result.setErrorCode(0);
		}catch(Exception e){
			logger.info("导出文件出错"+e.getMessage());
			result.setErrorCode(1);
			result.setMsg("导出文件出错了");
		}
		return JSONObject.fromObject(result).toString();
		
	}
	//导出map数据到excel
	@RequestMapping(value="exportfromMap.do",method=RequestMethod.POST)
	public @ResponseBody String exportFromMap(HttpServletResponse response,HttpServletRequest request){
		String tempfile = request.getRealPath("/temp/"+"客户信息"+".xls");
		JsonResult result = new JsonResult();
		System.out.println("tempfile: "+tempfile);
		try {
			List<HashMap> lists =    deccustomerService.qryCustomerList(null);
			String headers ="Name,Code";
			String url = POIUtils.exportToExcelHeadJxlMap(tempfile, response, request, "客户信息", lists, headers,"",port);
			result.setErrorCode(0);
			result.setData(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setErrorCode(1);
		}
		return JSONObject.fromObject(result).toString();
	}
}
