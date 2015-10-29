package com.wofu.intf.jw;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class WJService extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		Log.info("JW开始接收 ...");
	
		request.setCharacterEncoding("UTF-8");
		Map<String,String[]> map = request.getParameterMap();
		Connection conn=null;
		String appkey=null;
		String service=null;
		String content=null;
		PubSyncDeliveryInfoResponse pubSyncDeliveryInfoResponse = new PubSyncDeliveryInfoResponse();
		for(Iterator it = map.keySet().iterator();it.hasNext();){
			String name=(String)it.next();
			String value = map.get(name)[0];
			if("appkey".equals(name)){
				appkey=value;
			}else if("service".equals(name)){
				service=value;
			}else if("content".equals(name)){
				content=value;
			}
			//Log.info(name+", "+value);
		
		}
		String msgId = UUID.randomUUID().toString()+System.currentTimeMillis();
		try
		{		conn= PoolHelper.getInstance().getConnection(Params.getInstance().getProperty("dbname"));
				Object[] obj = {msgId,appkey,service,content,new Date(),0};
				String sql ="insert into jwinterfaceinfo(msgid,appkey,servicetype,bizdata,receivedate,flag) values(?,?,?,?,?,?)";
				SQLHelper.executePreparedSQL(conn, sql, obj);
				pubSyncDeliveryInfoResponse.setIssuccess(true);
				pubSyncDeliveryInfoResponse.setTs(new Date());
				Log.info("JW接收成功 ...");
			
		
		}catch(Exception e)
		{
			
			Log.error("jw", e.getMessage());
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
						Log.error("jw","回滚事务失败:"+rollbackexception.getMessage());
					}
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("jw","设置自动提交事务失败:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("jw","设置自动提交事务失败:"+sqle.getMessage());
			}
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception closeexception) {
				Log.error("jw", "关闭数据库连接失败:"+closeexception.getMessage());
			}
			pubSyncDeliveryInfoResponse.setIssuccess(false);
			pubSyncDeliveryInfoResponse.setTs(new Date());
			
			Log.info("接收失败 ...");
		
		}
		finally {			
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception e) {
				Log.error("jw", "关闭数据库连接失败:"+e.getMessage());
			}
		}
		response.setContentType("text/json;charset=utf-8");
			try {
				response.getWriter().write(pubSyncDeliveryInfoResponse.toJSONObject());
			} catch (Exception e) {
				Log.error("jw", "反馈数据失败: "+e.getMessage());
			}
			response.getWriter().flush();
			response.getWriter().close();
			conn=null;
			appkey=null;
			service=null;
			content=null;
		
		
		
	}
	


}
