package com.wofu.fenxiao.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.utils.Common;
import com.wofu.fenxiao.utils.POIUtils;
@Controller
public class UpLoadController {
	Logger logger = Logger.getLogger(this.getClass());
	//普通方式上传文件   字节方式  并接收其它的表单参数
	@RequestMapping(value="upload2.do",method=RequestMethod.POST)
	@ResponseBody//requestParam("xx")这个xx要跟表单的input文件选择框的name属性一样
	public String upLoad2(HttpServletRequest request,HttpServletResponse response){
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
		Iterator it = map.keySet().iterator();
		for(;it.hasNext();){
			String filename = (String)it.next();
			//获取文件流
			List<MultipartFile> files =(List<MultipartFile>) map.get(filename);
			for(int j=0;j<files.size();j++){
				MultipartFile file = files.get(j);
				if(!file.isEmpty()){
					logger.info("fileName: "+file.getOriginalFilename());
					long prev = System.currentTimeMillis();
					try{
						InputStream is = file.getInputStream();
						if(is.getClass()==ByteArrayInputStream.class){
							ByteArrayInputStream bis =(ByteArrayInputStream)is;
							FileOutputStream fos = new FileOutputStream(new File("e:/"+new Date().getTime()+file.getOriginalFilename()));
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
							FileOutputStream fos = new FileOutputStream(new File("e:/"+new Date().getTime()+file.getOriginalFilename()));
							byte[] bytes = new byte[1024*2];
							int len=0;
							while((len=in.read(bytes))!=-1){
								fos.write(bytes);
							}
							fos.flush();
							fos.close();
							in.close();
						}
						System.out.println("一共用了: "+(System.currentTimeMillis() - prev)); 
						result.setErrorCode(0);
						result.setMsg("上传文件成功");
					}catch(Exception e){
						logger.info("上传文件出错: "+e.getMessage());
						result.setErrorCode(1);
						result.setMsg("上传文件失败");
					}
				}
			}
			
		}
		
		return JSONObject.fromObject(result).toString();
		
	}
	
	
	//普通方式上传excel文件   字节方式  并接收其它的表单参数
	@RequestMapping(value="readexcel.do",method=RequestMethod.POST)
	@ResponseBody//requestParam("xx")这个xx要跟表单的input文件选择框的name属性一样
	public String upLoad3(HttpServletRequest request,HttpServletResponse response){
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
		Iterator it = map.keySet().iterator();
		for(;it.hasNext();){
			String filename = (String)it.next();
			//获取文件流
			List<MultipartFile> files =(List<MultipartFile>) map.get(filename);
			for(int j=0;j<files.size();j++){
				MultipartFile file = files.get(j);
				if(!file.isEmpty()){
					logger.info("fileName: "+file.getOriginalFilename());
					try{
						//解析excel文件
						POIUtils.getExcelData(file, file.getOriginalFilename(), 0);
					}catch(Exception e){
						logger.info("上传文件出错: "+e.getMessage());
						result.setErrorCode(1);
						result.setMsg("上传文件失败");
					}
				}
			}
			
		}
		
		return JSONObject.fromObject(result).toString();
		
	}
	
	
	
	
}
