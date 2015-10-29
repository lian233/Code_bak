package com.wofu.fire.deliveryservice;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
/**
 * �����˵�������
 * @author Administrator
 *
 */
public class DeliveryService extends HttpServlet {
	/**
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		doPost(request, response);
	}
	**/
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		Log.info("��ʼ���� ...");
		request.setCharacterEncoding("UTF-8");
		//��ȡ�ύ���Ͳ���		
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
		String line=null;
		StringBuilder result =new StringBuilder();
		for(line= br.readLine();line!=null;result.append(line),line=br.readLine());
		Log.info("result:��"+result.toString());
		Connection conn=null;
		Response re = new Response();
		if(result.length()>0 && result.charAt(0)==123){
			try
			{
					conn = PoolHelper.getInstance().getConnection(
							Params.getInstance().getProperty("dbname"));
					UUID uuid = UUID.randomUUID();
					String sql ="insert into cborderinterfaceinfo(msgid,servicetype,bizdata,receivedate,flag) values(?,?,?,?,?)";
					Object[] obj ={uuid.toString(),"loader",result.toString(),new Date(),0};
					SQLHelper.executePreparedSQL(conn, sql, obj);
					re.setCode(0);
					Log.info("�����˵����ݳɹ� ...");
			}catch(Exception e)
			{
				Log.info("����ʧ�� ..."+e.getMessage());
				re.setCode(1);
				re.setMsg("�����˵�����ʧ��");
			}
			finally {			
				try {
					if (conn != null)
					{
						conn.close();
					}
				} catch (Exception e) {
					Log.error("cross Border�羳�������ݴ���", "�ر����ݿ�����ʧ��:"+e.getMessage());
				}
			}
		}else{
			re.setCode(1);
			re.setMsg("�˵����ݱ�����json��ʽ");
		}
		response.setContentType("text/json; charset=UTF-8");
		response.getOutputStream().write(JSONObject.fromObject(re).toString().getBytes("utf-8"));
		response.getOutputStream().flush();	
		response.getOutputStream().close();	
	}
	


}
