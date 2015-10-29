package com.wofu.intf.chinapay;
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
public class ChinaPayService extends HttpServlet {
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
		Log.info("开始接收 ...");
		request.setCharacterEncoding("UTF-8");
		Connection conn =null;
		String data =null;
		try{
			conn  = PoolHelper.getInstance().getConnection(Params.getInstance().getProperty("dbname"));
			
			String transtype = request.getParameter("transtype");
			String amount = request.getParameter("amount");
			String checkvalue = request.getParameter("checkvalue");
			String transdate = request.getParameter("transdate");
			String status = request.getParameter("status");
			String orderno = request.getParameter("orderno");
			String GateId = request.getParameter("GateId");
			String Priv1 = request.getParameter("Priv1");
			String currencycode = request.getParameter("currencycode");
			String merid = request.getParameter("merid");
			String msgId = UUID.randomUUID().toString()+System.currentTimeMillis();
			Object[] param = {msgId,transtype,amount,checkvalue,transdate,status,orderno,GateId,Priv1,currencycode,merid,new Date(),0};
			Log.info("transtype: "+transtype+",amount: "+amount+",checkvalue: "+checkvalue+",transdate: "+transdate+",status: "+status+",orderno: "+orderno
					+",GateId:　"+GateId+",Priv1: "+Priv1+",currencycode: "+currencycode+",merid: "+merid);
			String sql = "insert into chinapaymentinfo(msgId,transtype,amount,checkvalue,transdate,status,orderno,"
				+"GateId,Priv1,currencycode,merid,receivedate,flag) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLHelper.executePreparedSQL(conn,sql,param);
		}catch(Exception e){
			Log.error("银联支付反馈数据写入失败", e.getMessage());
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Log.info("接收成功 ...");
		
		
	}
	


}
