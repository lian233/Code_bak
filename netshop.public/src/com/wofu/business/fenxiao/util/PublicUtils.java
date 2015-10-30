package com.wofu.business.fenxiao.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.RemoteHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
public class PublicUtils {

	public static String getConfig(Connection conn,String name,int shopid) 
		throws JException
	{
		String result="";
		String sql="";
		try
		{			
			sql="select "+name +" from decshop where id="+shopid;
			result =SQLHelper.strSelect(conn, sql);
			Log.info("result: "+result);
			if (result.equals("") || (result==null))
			{
				sql="update decshop set "+name+"=getdate() wherd id="+shopid;
				SQLHelper.executeSQL(conn, sql);
				
				result =Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT);
			}
			if (result.equals("") || (result==null))
				throw new JException("未配置["+name+"]");
		}catch(Exception jsqle)
		{
			throw new JException("取配置【"+name+"】出错!"+sql);
		}
		return result;
	}
	
	public static void setConfig(Connection conn,String name,int shopid,String value) 
		throws JException
	{
		String sql="";
		try
		{
			sql="update decshop set "+name+"='"+value+"' where id="+shopid;
			SQLHelper.executeSQL(conn, sql);
		}catch(SQLException jsqle)
		{
			throw new JException("更新配置【"+name+"】出错!"+sql);
		}
	}
	
	//从爱查快递http://api.ickd.cn获取配送数据
	public static String[] getDeliveryResult(String companycode,String deliverycode) throws Exception
	{
		
		String key="79F706765C92A00C88EE8711DA7D63D0";
		String[] companys={"ems","shunfeng","pingyou","shentong","yuantong",
				"huitong","zhaijisong","yunda","zhongtong","quanfeng",
				"xinbang","ups","rufeng"};
		
		boolean isfit=false;
		for(int i=0;i<companys.length;i++)
		{
			if (companys[i].equals(companycode)) isfit=true;
		}
		
		if (!isfit)
			throw new JException("不支持的快递公司:【"+companycode+"】");
					
		//URL请求地址 http://api.ickd.cn/?com=[]&nu=[]&id=[]&type=[]
		StringBuffer req = new StringBuffer() ;
		req.append("http://api.ickd.cn/") ;
		req.append("?com=").append(companycode) ;
		req.append("&nu=").append(deliverycode) ;
		req.append("&id=").append(key) ;
		req.append("&type=xml") ;
		req.append("&ord=asc") ;
		
		String res = RemoteHelper.sendRequest(req.toString(), "","GBK") ;

		//检查是否正常返回
		if("".equals(res) || res == null || res.indexOf("<?xml version=\"1.0\" encoding=\"GBK\" ?>") < 0)
			throw new JException("获取快递信息超时,快递公司:"+companycode+",快递单号:"+deliverycode+",返回值:"+res) ;

		Document doc = DOMHelper.newDocument(res, "GBK") ;
		Element response = doc.getDocumentElement() ;
		String errCode = DOMHelper.getSubElementVauleByName(response, "errCode") ;
		String message = DOMHelper.getSubElementVauleByName(response, "message") ;
			
		if(!"0".equals(errCode))
			throw new JException("获取失败,快递公司:"+companycode+",快递单号:"+deliverycode+",错误信息:"+message) ;

			
		Element data  = (Element) response.getElementsByTagName("data").item(0) ;
		NodeList itemList = data.getElementsByTagName("item") ;
		
		//取最后一条记录,判断状态
		Element item = (Element) itemList.item(itemList.getLength()-1) ;
		String context = DOMHelper.getSubElementVauleByName(item, "context");
			
		String status="";
		
		if(context.indexOf("退回") >=0
			||context.indexOf("未妥投")>=0
			||context.indexOf("失败") >=0
			||context.indexOf("不成功") >=0) 
				status ="-1";
		else if(context.indexOf("签收")>=0 
				|| context.indexOf(" 妥投") >=0)
				status ="0";
		else if(context.indexOf("退") > -1 
				&& context.indexOf("签收") > -1)
				status ="-1";
		else
			status ="-1";
		
		StringBuffer record=new StringBuffer();
		
		//跟踪记录
		for(int i=0;i<itemList.getLength();i++)
		{
			item = (Element) itemList.item(i) ;
			String time = DOMHelper.getSubElementVauleByName(item, "time");
			String note = DOMHelper.getSubElementVauleByName(item, "context");
			
			//格式化时间
			time = formatTime(time) ;
			
			record.append(time+" "+note+StringUtil.NEW_LINE);			
		}
		return new String[]{status,record.toString()};
		
	}
	//匹配时间格式yyyy-MM-dd HH:mm:ss
	private static String formatTime(String time)
	{
		String regex = "\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}" ;
	
		Pattern pattern = Pattern.compile(regex) ;
		Matcher matcher = pattern.matcher(time) ;
		if(matcher.find())
			return matcher.group() ;
		else
			return time + ":00" ;
	
	}
	
	//通用的http get请求
	public static String sendGetRequst(Map<String,Object> map,String url) throws Exception{
		HttpClient client = new HttpClient();
		StringBuilder uri = new StringBuilder(url);
		StringBuilder result= new StringBuilder();
		BufferedReader reader=null;
		for(Entry<String,Object> entry:map.entrySet()){
			uri.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		GetMethod method = new GetMethod(uri.substring(0, uri.length()-1));
		try {
			int status = client.executeMethod(method);
			reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
			for(String line = reader.readLine();line !=null;){
				result.append(line);
				line = reader.readLine();
			}
		} catch (Exception e) {
			throw new Exception("http请求出错了,错误信息:　"+e.getMessage());
		}
		return result.toString();
	}
	
	//改变config.xml的内容
	public static void changeConfig(File f,String name,String value)throws Exception{
		Document doc = DOMHelper.newDocument(f,"gbk","conf");
		Element ele = doc.getDocumentElement();
		Element loads = DOMHelper.getSubElementsByName(ele, "loaders")[0];
		Element load  = DOMHelper.getSubElementsByName(loads, "loader")[0];
		Element[] params = DOMHelper.getSubElementsByName(load, "param");
		for(Element e:params){
			String attributeName = e.getAttribute("name");
			if("url".equals(attributeName)){
				DOMHelper.modifyAttribute(e, "value", value);
			}
		}
		DOMHelper.saveDocument(doc, f, "gbk", true);
		
	}
}
