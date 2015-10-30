package com.wofu.intf.tiantu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
public class TianTuService extends HttpServlet {
	private static final String[] resonseSuccess ={"<?xml version=\"1.0\" encoding=\"UTF-8\"?><UpdateSalesOrderStatusRsp><flag>SUCCESS</flag><note></note></UpdateSalesOrderStatusRsp>",
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?><UpdateAsnStatusRsp><flag>SUCCESS</flag><note></note></UpdateAsnStatusRsp>",
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?><UpdateRmaStatusRsp><flag>SUCCESS</flag><note></note></UpdateRmaStatusRsp>",
		};

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		Log.info("��ʼ���� ...");
	
		request.setCharacterEncoding("UTF-8");
		//��ȡ�ύ���Ͳ���		
		String msgid=null ;//request.getParameter("msgId");
		String partnerid=null;//request.getParameter("partnerId");
		String rspbizdata=null;//request.getParameter("bizData");
		String servicetype=null;//request.getParameter("serviceType");
		/**
		Map<String,String[]> map = request.getParameterMap();
		Log.info(map.toString());
		for(Iterator it = map.keySet().iterator();it.hasNext();){
			String name=(String)it.next();
			String value = map.get(name)[0];
			Log.info(name+" "+value);
			if("msgId".equals(name)){
				msgid=value;
			}else if("bizData".equals(name)){
				rspbizdata=value;
			}else if("serviceType".equals(name)){
				servicetype=value;
			}else if("partnerId".equals(name)){
				partnerid=value;
			}
		}
		**/
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
		StringBuffer str1 = new StringBuffer();
		String line=null;
		for(line=reader.readLine();line!=null;){
			str1.append(new URLDecoder().decode(line,"utf-8"));
			line=reader.readLine();
		}
		String result = str1.toString();
		Log.info("result: "+result);
		Properties pro=null;
		try {
			pro = StringUtil.toProperties(result.toCharArray(),'=','&');
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		msgid=pro.getProperty("msgId");
		partnerid=pro.getProperty("partnerId");
		rspbizdata=pro.getProperty("bizData");
		servicetype=pro.getProperty("serviceType");
		StringBuffer bizData=null;
		Connection conn=null;
		StringBuffer replaybizData=null;
		try
		{
			if(rspbizdata.indexOf("<orderStatus>WMS_ACCEPT</orderStatus>")>0){
				Log.info("wms_accept������ʱ������: "+rspbizdata);
				bizData=new StringBuffer();
				bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				bizData.append("<loms>");
				bizData.append("<request>");
				bizData.append("<partnerId>"+partnerid+"</partnerId>");
				bizData.append("<msgId>"+msgid+"</msgId>");
				bizData.append("<msgType>sync</msgType>");
				bizData.append("<serviceType>ResponseNotify</serviceType>");
				bizData.append("<serviceVersion>1.0</serviceVersion>");
				bizData.append("</request>");
				bizData.append("<response>");
				bizData.append("<flag>SUCCESS</flag>");
				bizData.append("<bizData>"+java.net.URLEncoder.encode("UpdateSalesOrderStatus".equals(servicetype)?resonseSuccess[0]:"UpdateAsnStatus".equals(servicetype)?resonseSuccess[1]:resonseSuccess[2],"UTF-8")+"</bizData>");
				bizData.append("</response>");
				bizData.append("</loms>");
				Log.info("���ճɹ� ...");
			}else{
				conn = PoolHelper.getInstance().getConnection(
						Params.getInstance().getProperty("dbname"));
				
				String interfaceSystem=this.getInitParameter("interfacesystem");
							
				String sql="insert into tiantointerfaceinfo(msgid,partnerid,servicetype,bizdata,receivedate,interfaceSystem,flag) "
							+"values(?,?,?,?,?,?,?)";
				
				Object[] sqlv = {msgid,partnerid,servicetype,rspbizdata,new Date(),interfaceSystem,0};
				
				bizData = new StringBuffer();
				bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				bizData.append("<loms>");
				bizData.append("<request>");
				bizData.append("<partnerId>"+partnerid+"</partnerId>");
				bizData.append("<msgId>"+msgid+"</msgId>");
				bizData.append("<msgType>sync</msgType>");
				bizData.append("<serviceType>ResponseNotify</serviceType>");
				bizData.append("<serviceVersion>1.0</serviceVersion>");
				bizData.append("</request>");
				bizData.append("<response>");
				bizData.append("<flag>SUCCESS</flag>");
				bizData.append("<bizData>"+java.net.URLEncoder.encode("UpdateSalesOrderStatus".equals(servicetype)?resonseSuccess[0]:"UpdateAsnStatus".equals(servicetype)?resonseSuccess[1]:resonseSuccess[2],"UTF-8")+"</bizData>");
				bizData.append("</response>");
				bizData.append("</loms>");
				SQLHelper.executePreparedSQL(conn, sql, sqlv);
				Log.info("���ճɹ� ...");
			}
			
		
		}catch(Exception e)
		{
			/**
			 * �����쳣��ʱ������������¼����
			 */
			Log.error("tiantu:", "�����������,������ϢID:["+msgid+"] ������Ϣ:"+e.getMessage());
			bizData=new StringBuffer();
			bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			bizData.append("<"+servicetype+"Rsp>");
			bizData.append("<flag>FAILURE</flag>");
			bizData.append("<note></note>");
			bizData.append("<errors>");
			bizData.append("<error>");
			bizData.append("<errorCode>-1</errorCode>");
			bizData.append("<errorDescription>"+e.getMessage()+"</errorDescription>");
			bizData.append("</error>");
			bizData.append("</errors>");
			bizData.append("</"+servicetype+"Rsp>");
		/**
			bizData = new StringBuffer();
			bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			bizData.append("<loms>");
			bizData.append("<request>");
			bizData.append("<partnerId>"+partnerid+"</partnerId>");
			bizData.append("<msgId>"+msgid+"</msgId>");
			bizData.append("<msgType>sync</msgType>");
			bizData.append("<serviceType>ResponseNotify</serviceType>");
			bizData.append("<serviceVersion>1.0</serviceVersion>");
			bizData.append("</request>");
			bizData.append("<response>");
			bizData.append("<flag>FAILURE</flag>");
			bizData.append("<bizData>"+java.net.URLEncoder.encode(replaybizData.toString(),"UTF-8")+"</bizData>");
			bizData.append("</response>");
			bizData.append("</loms>");
			**/
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
						Log.error("tiantu:","�ع�����ʧ��:"+rollbackexception.getMessage());
					}
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("tiantu:","�����Զ��ύ����ʧ��:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("tiantu:","�����Զ��ύ����ʧ��:"+sqle.getMessage());
			}
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception closeexception) {
				Log.error("tiantu:", "�ر����ݿ�����ʧ��:"+closeexception.getMessage());
			}
			
			Log.info("����ʧ�� ...");
		
		}
		finally {			
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception e) {
				Log.error("tiantu:", "�ر����ݿ�����ʧ��:"+e.getMessage());
			}
		}
		///Log.info(bizData.toString());
		response.setContentType("text/xml; charset=UTF-8");
		response.getOutputStream().write(bizData.toString().getBytes());
		response.getOutputStream().flush();	
		response.getOutputStream().close();	
		msgid=null ;
		partnerid=null;
		rspbizdata=null;
		servicetype=null;
		bizData = null;
		replaybizData=null;
	}
	


}
