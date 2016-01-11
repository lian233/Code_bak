package com.wofu.fenxiao.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.IOException;
import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Service;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wofu.fenxiao.domain.DataPackage;
//import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.domain.JsonResult;
import com.wofu.fenxiao.mapping.DataPackageMapper;
import com.wofu.fenxiao.service.DistributeGoodsService;
import com.wofu.fenxiao.service.LoginService;
import com.wofu.fenxiao.service.MenuService;

import com.wofu.fenxiao.utils.Common;
//import com.wofu.fenxiao.utils.Md5Tool;
//import com.wofu.fenxiao.utils.POIUtils;
import com.wofu.fenxiao.utils.Tools;
import com.wofu.fenxiao.pulgins.PageView;

//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
//import org.springframework.web.multipart.commons.CommonsMultipartFile;

//数据包管理
@Controller
public class DataPackageController extends BaseController {
	//数据包存放目录
	String DataPackageDirPath = "./datapackage/";
	
	// 获取参数

	// 日志对象
	Logger logger = Logger.getLogger(this.getClass());

	// 服务层接口组件,这里自动生成服务层组件对象
	@Autowired
	// 账户服务
	private LoginService accountService;

	@Autowired
	// 菜单服务
	private MenuService menuService;

	@Autowired
	// 分销商品服务
	private DistributeGoodsService distributeGoodsService;

	@Autowired
	// 数据包map
	private DataPackageMapper dataPackageMapper;

	// 初始化请求(ok)
	@RequestMapping(value = "iniDataPackage.do", method = RequestMethod.GET)
	@ResponseBody
	public String iniDataPackage(HttpSession session) {
		// 全部用这种对象输出
		JsonResult re = new JsonResult();
		// re的data部分
		JSONObject obj = new JSONObject();
		try {
			// 获取当前登录信息
			HashMap<String, String> login = (HashMap<String, String>) session.getAttribute("CurLoginSession");
			if (null == login) { // 没登陆则返回登录界面
				return "location.href = \"default.html\";";
			}
			obj.put("curLogin", login); // 写入登录信息
			// 写入菜单
			obj.put("moduleID", 300700);
			// 取得当前菜单
			List<HashMap> menu = (List<HashMap>) session.getAttribute("CurMenu");
			if (null == menu) {
				try {
					menu = menuService.queryLoginMenu(Integer.parseInt(login
							.get("ID")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			obj.put("menu", menu);
			// 写入产品线
			List<HashMap> productLine = null;
			try {
				HashMap<String, Object> p = new HashMap<String, Object>();

				productLine = distributeGoodsService.qryProductLine(p);
			} catch (Exception e) {
				throw new Exception("取产品线出错：" + e.getMessage());
			}
			obj.put("productLine", productLine);

			re.setErrorCode(0);
		} catch (Exception e) {
			re.setErrorCode(1);
			re.setData("取用户菜单数据出错");
		}
		// 取得当前登录信息
		String ret = "var allData =" + obj.toString();
		System.out.println(ret);
		return ret;
	}

	// 查询数据包(ok)
	@RequestMapping(value = "qryDataPackageList.do", method = RequestMethod.POST)
	@ResponseBody
	public String qryDataPackageList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		// 获取请求数据
		JSONObject json = null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),
					"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 准备要返回的json数据
		JsonResult re = new JsonResult(); // 使用规定格式
		try {
			HashMap<String, Object> params = new HashMap<String, Object>(); // 存放查询参数
			if (json.containsKey("StartDateTime"))
				params.put("StartDateTime", (String) json.get("StartDateTime") + ":00"); // 开始时间
			if (json.containsKey("EndDateTime"))
				params.put("EndDateTime", (String) json.get("EndDateTime") + ":59"); // 结束时间
			params.put("Title", (String) json.get("Title")); // 数据包标题
			params.put("ProductLineID", (Integer) json.get("ProductLineID")); // 产品线
			params.put("DataTypeID", (Integer) json.get("DataTypeID")); // 数据包类型
			
			// 分页(spring来实现)
			PageView view = Tools.getPageView("ID",json.get("pn") != null ? (Integer) json.get("pn") : 0, json.get("pageSize") != null ? (Integer) json.get("pageSize") : 0);
			params.put("pageview", view);

			// 执行查询
			List<HashMap> result = dataPackageMapper.qryDataPackage(params);

			re.setErrorCode(0); // 错误代码0
			re.setData(result); // 把查询结果写入到data里面
			view.setPage(view.getPage() - 1);
			re.setPageInfo(view); // 分页信息

		} catch (Exception e) {
			re.setErrorCode(1);
			re.setMsg("查询数据包资料出错");
			logger.info("查询数据包资料出错:" + e.getMessage());
			e.printStackTrace();
		}
		return JSONObject.fromObject(re).toString();
	}

	// 修改&删除数据包(ok)
	@RequestMapping(value = "editDataPackage.do", method = RequestMethod.POST)
	public @ResponseBody
	String editDataPackage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = null;
		try {
			json = Common.getRequestJsonObject(request.getInputStream(),
					"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONArray dataPackages = json.getJSONArray("DataPackages");
		JsonResult re = new JsonResult();

		try {
			for (int i = 0; i < dataPackages.size(); i++) {
				JSONObject dataPackage = dataPackages.getJSONObject(i);

				if (!dataPackage.containsKey("ID")) {
					re.setErrorCode(1);
					throw new Exception("ID数据不存在");
				}

				DataPackage dp = new DataPackage();
				int id = dataPackage.getInt("ID");
				dp.setID(id);

				// 只允许修改和删除(增加与上传同时进行)
				if (id > 0) {
					if (dataPackage.size() <= 1) {// 删除
						try {
							//删除服务器上的文件
							DataPackage tmpdp = dataPackageMapper.getById(id);		//获取当前数据包信息
							if(tmpdp!=null)
							{
								String dirName = request.getRealPath(DataPackageDirPath);	//获取目录在服务器的绝对路径
								String fileName = tmpdp.getFileName();		//获取文件名
								File delfile = new File(dirName + "\\" + fileName);
								if(delfile.exists())
								{
									delfile.delete();
								}
							}
							//删除记录
							dataPackageMapper.delete(id);
							
							logger.info("删除数据包成功! 数据包ID:" + id);
						} catch (Exception e) {
							re.setErrorCode(2);
							throw new Exception("删除数据包出错!");
						}
					} else {// 修改
						try {
							if (dataPackage.containsKey("Title")) {
								dp.setTitle(dataPackage.getString("Title"));
							}
							if (dataPackage.containsKey("Note")) {
								dp.setNote(dataPackage.getString("Note"));
							}
							if (dataPackage.containsKey("ProductLineID")) {
								dp.setProductLineID(dataPackage.getInt("ProductLineID"));
							}
							if (dataPackage.containsKey("DataType")) {
								dp.setDataType(dataPackage.getInt("DataType"));
							}
							dataPackageMapper.update(dp);
							logger.info("修改数据包成功! 数据包ID:" + id);
						} catch (Exception e) {
							re.setErrorCode(3);
							throw new Exception("修改数据包信息出错!");
						}
					}
				}
				else
				{
					re.setErrorCode(1);
					throw new Exception("ID数据不正确");
				}
			}
			re.setErrorCode(0);
		} catch (Exception e) {
			if(re.getErrorCode() == 0)
				re.setErrorCode(-1);
			re.setMsg(e.getMessage());
			logger.info("保存或删除数据包出错" + e.getMessage());
			e.printStackTrace();
		}
		return JSONObject.fromObject(re).toString();
	}

	//上传数据包(ok)
	//@SuppressWarnings("deprecation")
	@RequestMapping(value="uploadDataPackage.do")
	public @ResponseBody String uploadDataPackage(HttpServletRequest request,HttpServletResponse response) throws Exception{
		//返回的数据
		JsonResult result = new JsonResult();
		
		//获取普通表单数据(数据包信息)
		Map<String,String[]> packageInfo = request.getParameterMap();
		DataPackage dp = new DataPackage();
		//标题
		if(packageInfo.containsKey("packageTitle"))
			dp.setTitle(packageInfo.get("packageTitle")[0]);
		else
		{
			result.setErrorCode(6);
			throw new Exception("传入信息缺少!");
		}
		//备注
		if(packageInfo.containsKey("packageNote"))
			dp.setNote(packageInfo.get("packageNote")[0]);
		//产品线
		if(packageInfo.containsKey("packageProductLineID"))
			dp.setProductLineID(Integer.parseInt(packageInfo.get("packageProductLineID")[0]));
		//类型
		if(packageInfo.containsKey("packageDataTypeID"))
			dp.setDataType(Integer.parseInt(packageInfo.get("packageDataTypeID")[0]));
		else
		{
			result.setErrorCode(6);
			throw new Exception("传入信息缺少!");
		}
		//上传时间  String UploadTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());		
		dp.setUploadTime(new Date());
		//操作员
		dp.setOperator(Tools.getCurLoginCName(request.getSession()));
		
		//获取文件
		try{
			MultipartHttpServletRequest req = (MultipartHttpServletRequest)request;
			//获取文件表单
			MultipartFile file = req.getFile("packageFile");
			//文件为空
			if(file == null)
			{
				result.setErrorCode(1);
				throw new Exception("请选择要上传文件!");
			}
			//检查文件大小(约等于30mb)
			if(file.getSize() > 32000000)
			{
				result.setErrorCode(2);
				throw new Exception("文件大小不能超过30M!");
			}
			//检查文件格式
			String FileName = file.getOriginalFilename();		//文件名
			String[] tmp = FileName.split("[.]");
			String FileFormat = tmp[tmp.length - 1];	//扩展名
			if(FileFormat.trim().equals("") || (!FileFormat.toLowerCase().equals("rar") && !FileFormat.toLowerCase().equals("zip")))
			{
				result.setErrorCode(3);
				throw new Exception("当前文件格式不支持,只支持rar,zip格式的数据包文件!");
			}
			//检查目录
			String dirName = request.getRealPath(DataPackageDirPath);	//获取目录在服务器的绝对路径
			File d = new File(dirName);
			if(!d.exists())
				d.mkdirs();
			//检查文件是否有同名文件&检查数据库中是否存在同样的文件
			File saveFile = new File(dirName + "\\" + FileName);	//文件在服务器的绝对路径
			if(saveFile.exists() || dataPackageMapper.qryFileExisting(FileName) > 0)
			{
				result.setErrorCode(4);
				throw new Exception("当前文件名:\"" + FileName + "\"已经存在于服务器中,不能重复上传,若要替换请先删除原来的文件!");
			}
			//保存文件
			InputStream is = file.getInputStream();
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
			//录入信息到数据库
			try
			{
				dp.setFileName(FileName);
				dataPackageMapper.add(dp);		//录入数据包信息
				result.setData(dataPackageMapper.qryIdentity());	//返回数据包的唯一ID
				logger.info("成功上传数据包!服务器路径为:" + dirName + "\\" + FileName);
				result.setErrorCode(0);
			}
			catch(Exception err)
			{
				saveFile.delete();		//删除刚刚上传的文件
				result.setErrorCode(5);
				throw new Exception("录入数据包信息时出错,请检查数据库连接和sql语句!");
			}
		}catch(Exception e){
			if(result.getErrorCode() == 0)
			{
				result.setErrorCode(-1);
				e.printStackTrace();
			}
			result.setMsg("上传数据包出错:"+e.getMessage());
			logger.info(result.getMsg());
		}
		return JSONObject.fromObject(result).toString();
	}
}