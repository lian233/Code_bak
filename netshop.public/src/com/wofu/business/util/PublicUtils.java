package com.wofu.business.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
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
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.RemoteHelper;
import com.wofu.common.tools.util.StringUtil;
public class PublicUtils {

	public static String getConfig(Connection conn,String name,String defaultvalue) 
		throws JException
	{
		String result="";
		String sql="";
		try
		{			
			sql="select value from config where name='"+name+"'";
			result =SQLHelper.strSelect(conn, sql);
			if (result.equals("") || (result==null))
			{
				sql="insert into config (systemid,name,value,note) values(0,'"+name+"','"+defaultvalue+"','"+name+"')";
				SQLHelper.executeSQL(conn, sql);
				
				result =defaultvalue;
			}
			if (result.equals("") || (result==null))
				throw new JException("δ����["+name+"]");
		}catch(Exception jsqle)
		{
			throw new JException("ȡ���á�"+name+"������!"+sql);
		}
		return result;
	}
	
	public static void setConfig(Connection conn,String name,String value) 
		throws JException
	{
		String sql="";
		try
		{
			sql="update config set value='"+value+"' where name='"+name+"'";
			SQLHelper.executeSQL(conn, sql);
		}catch(SQLException jsqle)
		{
			throw new JException("�������á�"+name+"������!"+sql);
		}
	}
	
	//�Ӱ�����http://api.ickd.cn��ȡ��������
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
			throw new JException("��֧�ֵĿ�ݹ�˾:��"+companycode+"��");
					
		//URL�����ַ http://api.ickd.cn/?com=[]&nu=[]&id=[]&type=[]
		StringBuffer req = new StringBuffer() ;
		req.append("http://api.ickd.cn/") ;
		req.append("?com=").append(companycode) ;
		req.append("&nu=").append(deliverycode) ;
		req.append("&id=").append(key) ;
		req.append("&type=xml") ;
		req.append("&ord=asc") ;
		
		String res = RemoteHelper.sendRequest(req.toString(), "","GBK") ;

		//����Ƿ���������
		if("".equals(res) || res == null || res.indexOf("<?xml version=\"1.0\" encoding=\"GBK\" ?>") < 0)
			throw new JException("��ȡ�����Ϣ��ʱ,��ݹ�˾:"+companycode+",��ݵ���:"+deliverycode+",����ֵ:"+res) ;

		Document doc = DOMHelper.newDocument(res, "GBK") ;
		Element response = doc.getDocumentElement() ;
		String errCode = DOMHelper.getSubElementVauleByName(response, "errCode") ;
		String message = DOMHelper.getSubElementVauleByName(response, "message") ;
			
		if(!"0".equals(errCode))
			throw new JException("��ȡʧ��,��ݹ�˾:"+companycode+",��ݵ���:"+deliverycode+",������Ϣ:"+message) ;

			
		Element data  = (Element) response.getElementsByTagName("data").item(0) ;
		NodeList itemList = data.getElementsByTagName("item") ;
		
		//ȡ���һ����¼,�ж�״̬
		Element item = (Element) itemList.item(itemList.getLength()-1) ;
		String context = DOMHelper.getSubElementVauleByName(item, "context");
			
		String status="";
		
		if(context.indexOf("�˻�") >=0
			||context.indexOf("δ��Ͷ")>=0
			||context.indexOf("ʧ��") >=0
			||context.indexOf("���ɹ�") >=0) 
				status ="-1";
		else if(context.indexOf("ǩ��")>=0 
				|| context.indexOf(" ��Ͷ") >=0)
				status ="0";
		else if(context.indexOf("��") > -1 
				&& context.indexOf("ǩ��") > -1)
				status ="-1";
		else
			status ="-1";
		
		StringBuffer record=new StringBuffer();
		
		//���ټ�¼
		for(int i=0;i<itemList.getLength();i++)
		{
			item = (Element) itemList.item(i) ;
			String time = DOMHelper.getSubElementVauleByName(item, "time");
			String note = DOMHelper.getSubElementVauleByName(item, "context");
			
			//��ʽ��ʱ��
			time = formatTime(time) ;
			
			record.append(time+" "+note+StringUtil.NEW_LINE);			
		}
		return new String[]{status,record.toString()};
		
	}
	//ƥ��ʱ���ʽyyyy-MM-dd HH:mm:ss
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
	
	//ͨ�õ�http get����
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
			throw new Exception("http���������,������Ϣ:��"+e.getMessage());
		}
		return result.toString();
	}
	
	//�ı�config.xml������
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
	//��ȡ���ݿ��б����token
	public static String getToken(Connection conn,int tradecontactid) throws Exception{
		String token ="";
		String sql ="select a.token from ecs_org_params a (nolock),ecs_tradecontactorgcontrast b (nolock) where a.orgid=b.orgid and b.tradecontactid="+tradecontactid;
		token = SQLHelper.strSelect(conn, sql);
		return token;
	}
	
	//��ȡ���ݿ��б����refretoken
	public static String getRefreToken(Connection conn,int tradecontactid) throws Exception{
		String token ="";
		String sql ="select a.refreshtoken from ecs_org_params a (nolock),ecs_tradecontactorgcontrast b (nolock) where a.orgid=b.orgid and b.tradecontactid="+tradecontactid;
		token = SQLHelper.strSelect(conn, sql);
		return token;
	}
}
