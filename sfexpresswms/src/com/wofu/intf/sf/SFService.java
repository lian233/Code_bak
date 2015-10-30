package com.wofu.intf.sf;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class SFService extends HttpServlet {

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		//Log.info("远程服务器地址:　"+request.getRemoteAddr());
		
		/*BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuffer str1 = new StringBuffer();
		String line=null;
		for(line=reader.readLine();line!=null;){
			str1.append(new URLDecoder().decode(line,"utf-8"));
			line=reader.readLine();
		}
		Log.info("sponser:"+str1.toString());*/
		
		Log.info("开始接收 ...");
		
		request.setCharacterEncoding("UTF-8");
		
		//读取提交类型参数		
		String logistics_interface = "";//request.getParameter("msgId");
		String warehouseCode = "";//request.getParameter("partnerId");
		String servicetype = "";//request.getParameter("serviceType");
		Pattern par = Pattern.compile("<([a-zA-Z]{1,})>\n  <header>");
		Map<String,String[]> map = request.getParameterMap();
		for(Iterator it = map.keySet().iterator();it.hasNext();){
			String name=(String)it.next();
			String value = map.get(name)[0];
			if("logistics_interface".equals(name)){
				logistics_interface=value;
			}else if("warehouseCode".equals(name)){
				warehouseCode=value;
			}
		}
		Matcher m = par.matcher(logistics_interface);
		if(m.find()){
			servicetype = m.group(1);
		}
		Log.info("servicetype: "+servicetype);
		Log.info("logistics_interface="+logistics_interface);
		Log.info("warehouseCode="+warehouseCode);
		StringBuffer replaybizData=new StringBuffer();
		StringBuffer bizData=new StringBuffer();
		Connection conn=null;
		try
		{			
			
			conn = PoolHelper.getInstance().getConnection(
					Params.getInstance().getProperty("dbname"));
			String msgid = CommHelper.getMsgid();
			conn.setAutoCommit(false);
			String partnerid = this.getInitParameter("company");
			
			String interfaceSystem=this.getInitParameter("interfacesystem");
			Log.info("partnerid: "+partnerid);
			Log.info("interfaceSystem: "+interfaceSystem);			
			String sql="insert into ecs_bestlogisticsinterface(msgid,partnerid,servicetype,bizdata,receivedate,interfaceSystem,flag) "
						+"values(?,?,?,?,?,?,?)";
			
			Object[] sqlv = {msgid,partnerid,servicetype,"warehouseCode="+warehouseCode+";"+logistics_interface,new Date(),interfaceSystem,0};
			
			SQLHelper.executePreparedSQL(conn, sql, sqlv);
			replaybizData.append("<Response>");
			replaybizData.append("<success>true</success>");
			replaybizData.append("<reason></reason><Response>");		
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("接收成功 ...");
		
		}catch(Exception e)
		{
			Log.error("sf logistics", e.getMessage());
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuffer str1 = new StringBuffer();
			String line=null;
			for(line=reader.readLine();line!=null;){
				str1.append(new URLDecoder().decode(line,"utf-8"));
				line=reader.readLine();
			}
			
			Log.error("sf logistics", "处理请求出错,错误信息:"+str1.toString());
			
			replaybizData=new StringBuffer();
			
			replaybizData.append("<Response>");
			replaybizData.append("<success>false</success>");
			replaybizData.append("<reason>其它</reason><Response>");
			try
			{
				if (!conn.getAutoCommit())
				{
					try
					{
						conn.rollback();
					}
					catch (Exception rollbackexception) 
					{ 
						Log.error("sf logistics","回滚事务失败:"+rollbackexception.getMessage());
					}
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("sf logistics","设置自动提交事务失败:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("sf logistics","设置自动提交事务失败:"+sqle.getMessage());
			}
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception closeexception) {
				Log.error("sf logistics", "关闭数据库连接失败:"+closeexception.getMessage());
			}
			
			Log.info("接收失败 ...");
		
		}
		finally {			
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception e) {
				Log.error("sf logistics", "关闭数据库连接失败:"+e.getMessage());
			}
		}
		
	
		///Log.info(bizData.toString());
		response.setContentType("text/xml; charset=UTF-8");
		response.getOutputStream().write(replaybizData.toString().getBytes());
		response.getOutputStream().flush();	
		response.getOutputStream().close();	
	}
	


}
