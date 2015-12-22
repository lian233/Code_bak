package com.wofu.intf.dtc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class PaymentsService  extends HttpServlet{

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
{
		String orderNo="";
		String tradeNo="";
		String notifyTime="";
		String resultCode="";
		String sign="";
		String message="";
		String resultMessage="";
		String outOrderNo="";
		String version="";
		String availableBalance="";
		String protocol="";
		String success="";
		String service="";
		String signType="";
		String partnerId="";
		String status="";
		Connection conn=null;
		Log.info("��ʼ���� ...");
		request.setCharacterEncoding("UTF-8");
		//��ȡ�ύ���Ͳ���	
		Map<String,String[]> map = request.getParameterMap();

		orderNo=request.getParameter("orderNo");
		tradeNo=request.getParameter("tradeNo");
		notifyTime=request.getParameter("notifyTime");
		resultCode=request.getParameter("resultCode");
		sign=request.getParameter("sign");
		message=request.getParameter("memo");
		resultMessage=request.getParameter("resultMessage");
		outOrderNo=request.getParameter("outOrderNo");
		version=request.getParameter("version");
		availableBalance=request.getParameter("availableBalance");
		protocol=request.getParameter("protocol");
		success=request.getParameter("success");
		service=request.getParameter("service");
		signType=request.getParameter("signType");
		partnerId=request.getParameter("partnerId");
		status=request.getParameter("status");
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		try {
			conn = PoolHelper.getInstance().getConnection(
					Params.getInstance().getProperty("dbname"));
			String sql="insert into paymentinterface(orderNo,tradeNo,notifyTime,resultCode,sign,message,resultMessage,outOrderNo," +
					"version,availableBalance,protocol,success,service,signType,partnerId,status,createtime,flag) "
				+"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	        Object[] sqlv ={orderNo,tradeNo,notifyTime,resultCode,sign,message,resultMessage,outOrderNo,version,availableBalance,
	        		protocol,success,service,signType,partnerId,status,time,0};
	        SQLHelper.executePreparedSQL(conn, sql, sqlv);
	        Log.info("���ճɹ�");
	        conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.setContentType("text/plain; charset=UTF-8");
		response.getOutputStream().write("success".getBytes());
		response.getOutputStream().flush();	
		response.getOutputStream().close();	
}
}
