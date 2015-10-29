package com.wofu.intf.dtc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class DtcService extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final BASE64Decoder base64decoder = new BASE64Decoder();
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
		Connection conn =null;
		String data =null;
		try{
			conn  = PoolHelper.getInstance().getConnection(Params.getInstance().getProperty("dbname"));
			data = request.getParameter("data");
			String MessageType = "";
			String MessageId =UUID.randomUUID().toString()+System.currentTimeMillis();
			String ReceiverId = "";
			data = URLDecoder.decode(new String(base64decoder.decodeBuffer(data),"utf-8"),"utf-8");
			data = data.replaceAll("\n[' ']{1,}","");
			Log.info("data: "+data);
			Pattern par = Pattern.compile("<MessageType>(.{1,})</MessageType><MessageId>.{1,}</MessageId><MessageTime>.{1,}</MessageTime><SenderId>.{1,}</SenderId><SenderAddress /><ReceiverId>(.{1,})</ReceiverId>");
			Matcher  m = par.matcher(data);
			if(m.find()){//��������
				MessageType = m.group(1);
				ReceiverId =m.group(2);
			}else{//��Ʒ����
				par = Pattern.compile("<MessageType>(.{1,})</MessageType><MessageId>.{1,}</MessageId><MessageTime>.{1,}</MessageTime><SenderId>.{1,}</SenderId><ReceiverId>(.{1,})</ReceiverId>");
				m = par.matcher(data);
				if(m.find()){//��������
					MessageType = m.group(1);
					ReceiverId =m.group(2);
				}
			}
			
			String sql = "insert into dtcinterfaceinfo(msgid,ReceiverId,servicetype,bizdata,receivedate) values(?,?,?,?,?) ";
			Object[] obj = {MessageId,ReceiverId,MessageType,data.substring(data.indexOf("<MessageBody>"),data.indexOf("</MessageBody>")+14),new Date()};
			SQLHelper.executePreparedSQL(conn, sql, obj);
		}catch(Exception e){
			Log.error("�羳���̷�������д��ʧ��", e.getMessage());
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			data=null;
		}
		Log.info("���ճɹ� ...");
		
		
		/**
		//��ȡ�ύ���Ͳ���		
		String msgid=null ;//request.getParameter("msgId");
		String partnerid=null;//request.getParameter("partnerId");
		String rspbizdata=null;//request.getParameter("bizData");
		String servicetype=null;//request.getParameter("serviceType");
		Map<String,String[]> map = request.getParameterMap();
		for(Iterator it = map.keySet().iterator();it.hasNext();){
			String name=(String)it.next();
			String value = map.get(name)[0];
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
							
				String sql="insert into ecs_bestlogisticsinterface(msgid,partnerid,servicetype,bizdata,receivedate,interfaceSystem,flag) "
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
		/**
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuffer str1 = new StringBuffer();
			String line=null;
			for(line=reader.readLine();line!=null;){
				str1.append(new URLDecoder().decode(line,"utf-8"));
				line=reader.readLine();
			}
			Log.info("���󷵻�����Ϊ:"+str1.toString());
			Log.error("best logistics", "�����������,������ϢID:["+msgid+"] ������Ϣ:"+e.getMessage());
			Log.error("data: ",rspbizdata);
			String str="-1\n"+e.getMessage();
			
			replaybizData=new StringBuffer();
			
			replaybizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			replaybizData.append("<"+servicetype+"Rsp>");
			replaybizData.append("<flag>FAILURE</flag>");
			replaybizData.append("<note></note>");
			replaybizData.append("<errors>");
			replaybizData.append("<error>");
			replaybizData.append("<errorCode>-1</errorCode>");
			replaybizData.append("<errorDescription>"+e.getMessage()+"</errorDescription>");
			replaybizData.append("</error>");
			replaybizData.append("</errors>");
			replaybizData.append("</"+servicetype+"Rsp>");
		
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
						Log.error("best logistics","�ع�����ʧ��:"+rollbackexception.getMessage());
					}
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("best logistics","�����Զ��ύ����ʧ��:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("best logistics","�����Զ��ύ����ʧ��:"+sqle.getMessage());
			}
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception closeexception) {
				Log.error("best logistics", "�ر����ݿ�����ʧ��:"+closeexception.getMessage());
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
				Log.error("best logistics", "�ر����ݿ�����ʧ��:"+e.getMessage());
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
		**/
		
		
	}
	


}
