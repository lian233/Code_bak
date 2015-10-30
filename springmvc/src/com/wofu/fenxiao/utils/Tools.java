/*
 * 工具类
 * */
package com.wofu.fenxiao.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.multipart.MultipartFile;

import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.pulgins.PageView;

public class Tools {

	/* 取得客户前缀
	 * 
	 * */
	public static String getFrontStr(HttpSession session) throws Exception{
		if (null == session){
			throw new Exception("找不到Session数据");
		} 
		
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			return login.get("Front");
		
		} catch (Exception e) {
			return "g1_";
			//throw new Exception("用户登录信息不存在");
		}
	}
	
	/* 取得客户ID
	 * 
	 * */
	public static int getCurCustomerID(HttpSession session) throws Exception{
		if (null == session){
			throw new Exception("找不到Session数据");
		} 
		
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			return Integer.parseInt(login.get("CustomerID")) ;
		
		} catch (Exception e) {
			return 0;
			//throw new Exception("用户登录信息不存在");
		}
	}	

	/* 取得系统类型
	 * 
	 * */
	public static String getSystemType(HttpSession session) throws Exception{
		if (null == session){
			throw new Exception("找不到Session数据");
		} 
		
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			return login.get("SystemType") ;
		
		} catch (Exception e) {
			return "0";
			//throw new Exception("用户登录信息不存在");
		}
	}	
	
	/* 取得客户名
	 * 
	 * */
	public static String getCurCustomerName(HttpSession session) throws Exception{
		if (null == session){
			throw new Exception("找不到Session数据");
		} 
		
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			return login.get("CustomerName") ;
		
		} catch (Exception e) {
			return "";
			//throw new Exception("用户登录信息不存在");
		}
	}	
	
	/* 根据对照表取得索引，从列表中取得数据
	 * */
	public static String getListByName(List<String> list , HashMap<String,Integer> head , String key ){
		Integer i = head.get(key);
		if (i==null){
			return "";
		}
		else{
			if (list.size() <= i){
				return "";
			}
			else{
				return list.get(i);
			}
		}
	}
		
	/* 从列表中取得关键字对应的ID值
	 * */
	public static String getListValue(List<HashMap> list , String findField , String findValue , String keyField ){
		for(int i=0;i<list.size();i++){
			HashMap map = list.get(i);
			String v = String.valueOf(map.get(findField)).trim();
			if (v.equals(findValue.trim())){
				return String.valueOf(map.get(keyField));
			}			
		}
		
		return null;
	}
	

	/* 取得登录人名
	 * 
	 * */
	public static String getCurLoginCName(HttpSession session) throws Exception{
		if (null == session){
			throw new Exception("找不到Session数据");
		} 
		
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			return login.get("CName") ;
		
		} catch (Exception e) {
			return "test";
			//throw new Exception("用户登录信息不存在");
		}
	}	
	
	//取得JSONArray对象的值
	public static String getArrayStr(JSONObject o , String name) throws Exception{
		return o.containsKey(name)?o.getString("Name"):"";
	}
	
	/* 取得登录ID
	 * 
	 * */
	public static int getCurLoginID(HttpSession session) throws Exception{
		if (null == session){
			throw new Exception("找不到Session数据");
		} 
		
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
			return Integer.parseInt(login.get("ID")) ;
		
		} catch (Exception e) {
			//return "test";
			throw new Exception("用户登录信息不存在");
		}
	}		
	
	/* 取得登录信息
	 * 
	 * */
	public static HashMap<String,String> getLoginSession(HttpSession session) throws Exception{
		if (null == session){
			throw new Exception("找不到Session数据");
		} 
		
		try{
			HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLogin");
			return login;
		
		} catch (Exception e) {
			throw new Exception("用户登录信息不存在");
		}
	}
	
	/* 取得菜单信息
	 * 
	 * */
	public static List<HashMap> getMenuSession(HttpSession session) throws Exception{
		if (null == session){
			throw new Exception("找不到Session数据");
		} 
		
		try{
			List<HashMap> menu =(List<HashMap>) session.getAttribute("CurMenu");
			return menu;
		
		} catch (Exception e) {
			throw new Exception("菜单信息不存在");
		}
	}	
	
	/* 取得分页行数
	 * 
	 * */	
	public static int getPageSize(HttpServletRequest request){
		int ret = 100;
		if (null != request){
			try{
			ret = (Integer)request.getAttribute("psize");
			}
			catch(Exception e){}
		}

		return ret;		
	}
	
	/* 生成分页对象
	 * */
	public static PageView getPageView(String IdField , int pageNow , int pageSize){
		pageNow++;
		PageView view = new PageView();
		view.setId(IdField);
		view.setPage(pageNow);
		if(pageSize>0){ 
			view.setPsize(pageSize);
		}
		
		return view;
	}
	
	/* 取得打印格式
	 * */
	public static String getPrintFormat(int customerID,String name)  throws Exception{
				
		String dirName = "e:/PrintFormat"+"\\"+Integer.toString(customerID)+"\\";
		String fileName = dirName+ name +".js";
		
		//读取文件
		File file = new File(fileName);
		if (!file.exists()){
			dirName = "e:/PrintFormat"+"\\0\\";
			fileName = dirName+ name +".js";
			file = new File(fileName);
			
			
			if (!file.exists()){
				String def= "{\"name\":\""+name+"\", \"img\":\"./deliveryImg/"+name+".jpg\",\"fields\":[]}";
				return def;
			}			
		}
		
		Reader in = new InputStreamReader(new FileInputStream(file),"utf-8");		
		char[] cbuf = new char[1024];
		int len=0;
        
		String str = ""; 
		StringWriter data = new StringWriter();
		while((len=in.read(cbuf))!=-1){
			data.write(cbuf, 0, len);
		}

		in.close();
		
		return data.toString();
		
	}
	
	/*记录当前的列表
	 * */
	public static void setOrderList(HttpSession session , List<DecOrder> list){
		session.setAttribute("curOrder", list);
	}
	
	/* 取得打印属性
	 * */
	public static String getPrintProp(){
		//String js = "{\"fieldGroup\":[{\"name\":\"Sender\",\"text\":\"发货人信息\",\"fields\":[\"ShopName\",\"CustomerName\",\"CustomerLinkMan\",\"CustomerTele\",\"CustomerMobile\",\"CustomerAddress\",\"CustomerNote\",\"PrintTime\",\"ShopLinkMan\",\"ShopTele\"],\"fieldsText\":[\"店铺\",\"发货人公司\",\"发货人联系人\",\"发货人电话\",\"发货人手机\",\"发货人地址\",\"发货人备注\",\"打印时间\",\"店铺联系人\",\"店铺电话\"]},"
			//+"{\"name\":\"Sender2\",\"text\":\"发货人信息2\",\"fields\":[\"ShopName2\",\"CustomerName2\",\"CustomerLinkMan2\",\"CustomerTele2\",\"CustomerMobile2\",\"CustomerAddress2\",\"CustomerNote2\",\"PrintTime2\",\"ShopLinkMan2\",\"ShopTele2\"],\"fieldsText\":[\"店铺2\",\"发货人公司2\",\"发货人联系人2\",\"发货人电话2\",\"发货人手机2\",\"发货人地址2\",\"发货人备注2\",\"打印时间2\",\"店铺联系人2\",\"店铺电话2\"]},"
		String js = "{\"fieldGroup\":[{\"name\":\"Sender\",\"text\":\"发货人信息\",\"fields\":[\"ShopName\",\"CustomerName\",\"CustomerAddress\",\"CustomerNote\",\"PrintTime\",\"ShopLinkMan\",\"ShopTele\"],\"fieldsText\":[\"店铺\",\"发货人公司\",\"发货人地址\",\"发货人备注\",\"打印时间\",\"店铺联系人\",\"店铺电话\"]},"
			+"{\"name\":\"Sender2\",\"text\":\"发货人信息2\",\"fields\":[\"ShopName2\",\"CustomerName2\",\"CustomerAddress2\",\"CustomerNote2\",\"PrintTime2\",\"ShopLinkMan2\",\"ShopTele2\"],\"fieldsText\":[\"店铺2\",\"发货人公司2\",\"发货人地址2\",\"发货人备注2\",\"打印时间2\",\"店铺联系人2\",\"店铺电话2\"]},"
			+"{\"name\":\"Receiver\",\"text\":\"收货人信息\",\"fields\":[\"LinkMan\",\"Mobile\",\"Phone\",\"MobilePhone\",\"State\",\"City\",\"District\",\"Address\",\"DetailAddress\",\"BuyerNick\"],\"fieldsText\":[\"收货人姓名\",\"收货人手机\",\"收货人电话\",\"收货人手机电话\",\"收货人省份\",\"收货人市\",\"收货人区\",\"收货人地址\",\"收货人详细地址\",\"买家昵称\"]},"
			+"{\"name\":\"Receiver2\",\"text\":\"收货人信息2\",\"fields\":[\"LinkMan2\",\"Mobile2\",\"Phone2\",\"MobilePhone2\",\"State2\",\"City2\",\"District2\",\"Address2\",\"DetailAddress2\",\"BuyerNick2\"],\"fieldsText\":[\"收货人姓名2\",\"收货人手机2\",\"收货人电话2\",\"收货人手机电话2\",\"收货人省份2\",\"收货人市2\",\"收货人区2\",\"收货人地址2\",\"收货人详细地址2\",\"买家昵称2\"]},"
			+"{\"name\":\"Order\",\"text\":\"订单信息\",\"fields\":[\"RefSheetID\",\"ItemContent\",\"TotalQty\",\"Note\",\"PayTime\"],\"fieldsText\":[\"订单号\",\"商品信息\",\"商品总数量\",\"备注\",\"付款时间\"]},"
			+"{\"name\":\"Order2\",\"text\":\"订单信息2\",\"fields\":[\"RefSheetID2\",\"ItemContent2\",\"TotalQty2\",\"Note2\"],\"fieldsText\":[\"订单号2\",\"商品信息2\",\"商品总数量2\",\"备注2\"]},"
			+"{\"name\":\"Delivery\",\"text\":\"快递信息\",\"fields\":[\"ZoneCode\",\"AddressID\",\"DeliveryName\",\"DeliveryCode\",\"DeliverySheetID\",\"DeliveryNote\"],\"fieldsText\":[\"目的地编码\",\"大头笔\",\"快递名称\",\"快递编码\",\"快递单号\",\"快递备注\"]},"
			+"{\"name\":\"Delivery2\",\"text\":\"快递信息2\",\"fields\":[\"ZoneCode2\",\"AddressID2\",\"DeliveryName2\",\"DeliveryCode2\",\"DeliverySheetID2\",\"DeliveryNote2\"],\"fieldsText\":[\"目的地编码2\",\"大头笔2\",\"快递名称2\",\"快递编码2\",\"快递单号2\",\"快递备注2\"]},"
			+"{\"name\":\"Text\",\"text\":\"文本信息\",\"fields\":[\"ReceiverText\",\"SendText\",\"ReceiverText2\",\"SendText2\"],\"fieldsText\":[\"收件人\",\"寄件人\",\"收件人2\",\"寄件人2\"]},"
			+"{\"name\":\"Text\",\"text\":\"自定义信息\",\"fields\":[\"PrintContent1\",\"PrintContent2\",\"PrintContent3\",\"PrintContent4\",\"PrintContent5\",\"PrintContent6\",\"PrintContent7\",\"PrintContent8\",\"PrintContent9\",\"PrintContent10\"],\"fieldsText\":[\"自定义内容1\",\"自定义内容2\",\"自定义内容3\",\"自定义内容4\",\"自定义内容5\",\"自定义内容6\",\"自定义内容7\",\"自定义内容8\",\"自定义内容9\",\"自定义内容10\"]},"
			+"{\"name\":\"Barcode\",\"text\":\"条码信息\",\"fields\":[\"RefSheetID_\",\"DeliverySheetID_\",\"RefSheetID2_\",\"DeliverySheetID2_\"],\"fieldsText\":[\"订单号条码\",\"快递单号条码\",\"订单号条码2\",\"快递单号条码2\"]}"
			+"]}";
		
		return js;
	}
	
	/*取得客户端调用URL
	 * */
	public static String getRequestURL(HttpServletRequest request) { 
		if (request == null) { 
			return ""; 
		} 
		String url = ""; 
		url = request.getContextPath(); 
		url = url + request.getServletPath(); 

		/*
		java.util.Enumeration names = request.getParameterNames(); 
		int i = 0; 
		if (!"".equals(request.getQueryString()) || request.getQueryString() != null) { 
			url = url + "?" + request.getQueryString(); 
		} 

		if (names != null) { 
		while (names.hasMoreElements()) { 
			String name = (String) names.nextElement(); 
			if (i == 0) { 
				url = url + "?"; 
			} else { 
				url = url + "&"; 
			} 
			i++; 
	
			String value = request.getParameter(name); 
			if (value == null) { 
				value = ""; 
			} 
	
			url = url + name + "=" + value; 
			try { 
			// java.net.URLEncoder.encode(url, "ISO-8859"); 
			} catch (Exception e) { 
				e.printStackTrace(); 
			} 
		} 
		} 
		try { 
		// String enUrl = java.net.URLEncoder.encode(url, "utf-8"); 
		} catch (Exception ex) { 
		ex.printStackTrace(); 
		} */

		return url; 
	}  
	
	/* 保存导入的文件备份
	 * 格式：文件名_客户_时间
	 * */	
	public static void bakFile(HttpServletRequest request,MultipartFile file , String head) throws Exception{
		if(!file.isEmpty()){
			String dir = "e:/BackupFile";
			//检查目录是否存在
			
			
			long prev = System.currentTimeMillis();
			try{
				InputStream is = file.getInputStream();
				//String dirName = request.getRealPath("BackupFile/")+"\\"+Integer.toString(Tools.getCurCustomerID(request.getSession()))+"\\";				
				String dirName = "e:/BackupFile"+"\\"+Integer.toString(Tools.getCurCustomerID(request.getSession()))+"\\";
				File d = new File(dirName);
				d.mkdir();//创建文件夹
				
				File saveFile = new File(dirName+head.trim()+(new Date().getTime())+file.getOriginalFilename());
				
				
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
			}catch(Exception e){
				 throw new Exception("保存文件出错:"+e.getMessage());
			}
		}		
	}
	
	/* 检查是否登录
	 * 
	 * */
	public static void checkLogin(HttpSession session,HttpServletResponse response) throws Exception{		
		System.out.println("检查登录");
		if (session==null){
			System.out.println("session为空");
		}
		HashMap<String,String> login = (HashMap<String,String>)session.getAttribute("CurLoginSession");
		
		if (null==login){ 
			System.out.println("跳转");
			response.sendRedirect("default.html");
		}
	}
	
}
