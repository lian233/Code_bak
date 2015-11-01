package com.wofu.intf.huasheng;
//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
//import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
//import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.huasheng.Order;
import com.wofu.ecommerce.huasheng.OrderItem;
import com.wofu.ecommerce.huasheng.OrderUtils;
//import com.wofu.ecommerce.huasheng.util.*;


@SuppressWarnings("serial")
public class HuashengService extends HttpServlet {
	private String jobname = "����API���ն���������ҵ";
	//������Ϣ
	String errResult = "{\"code\":-5,\"msg\":\"Internal server error!\"}";	//�ڲ�����������
	
	//������get��������
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		JSONObject responseData = new JSONObject();
		String result = errResult;
		try
		{
			responseData.put("code", -1);
			responseData.put("msg", "Please use Post to submit data!");	//��ʹ��POST�ύ����
			result = responseData.toString();
		}
		catch(Exception e){ result = errResult; }
		//���ؽ�����Է�
		Log.info("��Ӧ:" + result);	//Log.info("��Ӧ:" + result);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
		
//		response.getOutputStream().write(result.getBytes());
//		response.getOutputStream().flush();	
//		response.getOutputStream().close();	
		//doPost(request,response);
	}
	
	//�������Ͷ�������
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		Connection conn=null;
		String returnResult = errResult;
		try
		{
			Log.info(jobname + "   ��ʼ�������� ...");
			//���ص�json��Ϣ
			JSONObject responseData = new JSONObject();
			//���ñ���
			request.setCharacterEncoding("UTF-8");
			//��ȡ���ݿ�����
			conn = PoolHelper.getInstance().getConnection(Params.getInstance().getProperty("dbname"));
			//��ȡ���ò���
			String vcode = this.getInitParameter("vcode");	//��֤��
			String tradecontactid = this.getInitParameter("tradecontactid");		//����id
			String username = new String(this.getInitParameter("username").getBytes("ISO-8859-1"), "GBK");		//��������
			
			//��ȡ����Ĳ�����Ϣ
			String Par_service = request.getParameter("service");
			String Par_vcode = request.getParameter("vcode");
			String Par_order = request.getParameter("order");
			
			//����ύ�����Ĳ���
//			Map<String,String[]> map = request.getParameterMap();
//			for(Iterator it = map.keySet().iterator();it.hasNext();){
//				String name=(String)it.next();
//				String value = map.get(name)[0];
//				Log.info("name:" + name + " value:" + value);
//			}
			
			//��鴫�����
			if(Par_service == null)
			{
				responseData.put("code", -2);
				responseData.put("msg", "Necessary parameters: 'service' does not exist!");
				returnResult = responseData.toString();
				Log.info("��Ҫ����serviceΪ��!");
				throw new Exception("normal");
			}
			if(Par_vcode == null)
			{
				responseData.put("code", -2);
				responseData.put("msg", "Necessary parameters: 'vcode' does not exist!");
				returnResult = responseData.toString();
				Log.info("��Ҫ����vcodeΪ��!");
				throw new Exception("normal");
			}
			if(Par_order == null)
			{
				responseData.put("code", -2);
				responseData.put("msg", "Necessary parameters: 'order' does not exist!");
				returnResult = responseData.toString();
				Log.info("��Ҫ����OrderΪ��!");
				throw new Exception("normal");
			}
			
			//����ύ������order����
			Log.info("order[]:" + request.getParameter("order"));
			
			//��֤vcode
			if(vcode == null) throw new Exception("��֤��δ����!");
			if(vcode.equals("")) throw new Exception("��֤��δ����!");
			if(!vcode.equals(Par_vcode))
			{
				responseData.put("code", -3);
				responseData.put("msg", "Verification code is incorrect!");
				returnResult = responseData.toString();
				Log.info("�������֤�벻ƥ��,�޷�����!");
				throw new Exception("normal");
			}
			
			//��֤service
			if(!Par_service.toLowerCase().equals("order"))
			{
				responseData.put("code", -4);
				responseData.put("msg", "Service name is incorrect!");
				returnResult = responseData.toString();
				Log.info("����ķ������Ʋ�ƥ��,�޷�����!");
				throw new Exception("normal");
			}
			
			//��ȡ�����б�
			JSONArray errOrderList = new JSONArray();
			try {
				JSONArray OrderList = new JSONArray(Par_order);
				Log.info("������Ķ�����:" + OrderList.length());
				for(int idxOrder = 0;idxOrder < OrderList.length();idxOrder++)
				{
					//��ȡ��ǰ����
					JSONObject orderJson = OrderList.getJSONObject(idxOrder);
					Order o = new Order();
					o.setObjValue(o, orderJson);
					String orderid = orderJson.getString("order_id");
					Date mtime =  Formatter.parseDate(orderJson.getString("mtime"),Formatter.DATE_TIME_FORMAT);	//�޸�ʱ��
					
					
					Log.info(String.format("���ڴ�����:%s   ����״̬:%s   ���ʽ:%s   ����״̬:%s",
							orderid,
							OrderUtils.getOrderStateByCode(o.getStatus()),
							OrderUtils.getPayWayByCode(o.getPay_id()),
							OrderUtils.getDeliverStatusByCode(o.getPay_status())
							));

					//���Դ�����
					try {
						int Deliver_status = o.getDeliver_status() == null ? 0 : Integer.valueOf(o.getDeliver_status());
						int Order_Status  = o.getStatus() == null ? -1 : Integer.valueOf(o.getStatus());
						int Pay_id = o.getPay_id() == null ? -1 : Integer.valueOf(o.getPay_id());
						
						//��鶩��״̬
						//ֻȡδ����, �Ѹ���������֧�� �� δ�����һ�������  �Ķ���
						if(Deliver_status == 0 && ((Order_Status == Pay_id && Order_Status == 1) || (Order_Status == Pay_id && Order_Status == 0)))
						{
							if (!OrderManager.isCheck(jobname, conn, orderid) && !OrderManager.TidLastModifyIntfExists(jobname, conn, orderid, mtime))
							{
								Log.info("��������["+orderid+"]�Ľӿڶ���");
								try
								{
									//���ɽӿڶ���
									OrderUtils.createInterOrder(conn, o, tradecontactid, username);
									for(Iterator ito=o.getDetail().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getSku();
										long qty= (long)item.getNum();
										
										//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��
										//StockManager.deleteWaitPayStock(jobname, conn,tradecontactid, orderid, sku);
										
										//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
										StockManager.addSynReduceStore(jobname, conn, tradecontactid, o.getStatus(), o.getOrder_id(), sku, qty, false);
									}
								} catch(SQLException sqle)
								{
									throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
								}
							}
							else
								Log.info("����:" +orderid+ "�Ѿ����������ݿ���");
						}
						else
						{
							Log.info("��ǰ����:" + orderid + "״̬�����ϴ���Ҫ��,���Դ���!");
						}
					} catch (Exception e) {
						try {
							JSONObject err = new JSONObject();
							err.put("orderid", orderid);
							err.put("errmsg", e.getMessage());
							errOrderList.put(err);
						} catch (Exception err) { throw new Exception("д����󶩵��б����!"); }
					}
				}
			} catch (Exception e) {
				if(!e.getMessage().equals("д����󶩵��б����!"))
				{
					e.printStackTrace();
					responseData.put("code", -7);
					responseData.put("msg", "Order list data is not standardized!");	//�����б����ݲ��淶
					returnResult = responseData.toString();
					Log.info("����Ķ����б����ݲ��淶,�޷�����!");
					throw new Exception("normal");
				}
				else
					throw e;
			}
			if(errOrderList.length() == 0)
			{
				responseData.put("code", 0);
				responseData.put("msg", "Successfully received!");
				returnResult = responseData.toString();
				Log.info(jobname + "   �������ݳɹ�!");
			}
			else
			{
				responseData.put("code", -6);
				responseData.put("msg", "Some orders received failed!");
				responseData.put("fail_orders", errOrderList);
				returnResult = responseData.toString();
				Log.info(jobname + "   �������ݲ��ֳɹ�!");
			}
		}
		catch(Exception e)
		{
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
						Log.error(jobname, "�ع�����ʧ��:"+rollbackexception.getMessage());
					}
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error(jobname, "�����Զ��ύ����ʧ��:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error(jobname, "�����Զ��ύ����ʧ��:"+sqle.getMessage());
			}
			try {
				if (conn != null)
				{
					conn.close();
					conn = null;
				}
			} catch (Exception closeexception) {
				Log.error(jobname, "�ر����ݿ�����ʧ��:"+closeexception.getMessage());
			}
			if(!e.getMessage().equals("normal"))
			{
				returnResult = errResult;
				Log.error(jobname, "�����ڲ�����:" + e.getMessage());
			}
			Log.info(jobname + "   ��������ʧ��!");
		}
		finally {			
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception e) {
				Log.error(jobname, "�ر����ݿ�����ʧ��:"+e.getMessage());
			}
		}
		
		//���ؽ�����Է�
		Log.info("��Ӧ:" + returnResult);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(returnResult);
		out.close();
	}
}
