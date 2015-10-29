package com.wofu.intf.chinapay;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
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

import com.wofu.common.json.JSONObject;
import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class Payaction extends HttpServlet {
	/**
	 * 异步生成签名数据，并返回到页面上，最后完成支付
	 */
	private static final long serialVersionUID = 1L;
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
		//Log.info(request.getMethod());
		Connection conn =null;
		String callback ="";//跨域ajax调用的回调函数名
		PayDataResponse payDataResponse = new PayDataResponse();
		try{
			String result=""; //=new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8")).readLine();
			Map<String,String[]> map = request.getParameterMap();
			for(Iterator it = map.keySet().iterator();it.hasNext();){
				String name = (String)it.next();
				String values = map.get(name)[0];
				if("data".equals(name))
					result=URLDecoder.decode(values.replaceAll("-","%"),"utf-8");
				if("callback".equals(name))
					callback=values;
			}
			//Log.info("result: "+result);
			PayData paydata = new PayData();
			paydata.setObjValue(paydata, new JSONObject(result));
			Log.info(paydata.getCuryId());
			String sql = "update ecs_order_info set paymen='"+paydata.getExtParam6()+"',paymencertNo='"+paydata.getExtParam7()+"'"
				+" where order_sn='"+paydata.getExtParam5()+"'";
			conn = PoolHelper.getInstance().getConnection("fire");
			SQLHelper.executeSQL(conn, sql);
			paydata.setExtParam6(CommHelper.UnicodeString(paydata.getExtParam6()));
			Log.info(paydata.getExtParam6());
			String sign = CommHelper.sign(paydata);
			Log.info("sign: "+sign);
			payDataResponse.setCheckvalue(sign);
			payDataResponse.setFlag("1");//成功的标志
			payDataResponse.setUsername(paydata.getExtParam6());
			payDataResponse.setCertNo(paydata.getExtParam7());
		}catch(Exception e){
			e.printStackTrace();
			Log.error("银联支付人信息写入失败", e.getMessage());
			payDataResponse.setCheckvalue("生成签名错误");
			payDataResponse.setFlag("0");//失败的标志
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
		response.setContentType("text/html;charset=utf-8");
		try {
			Log.info(callback+"("+payDataResponse.toJSONObject()+")");
			response.getWriter().write(callback+"("+payDataResponse.toJSONObject()+")");
			Log.info("接收成功 ...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.error("生成签名", "返回签名数据错误"+e.getMessage());
		}finally{
			response.getWriter().flush();
			response.getWriter().close();
			payDataResponse=null;
		}
		
		
		
		
	}
	


}
