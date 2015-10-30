package com.wofu.ecommerce.uwuku;


import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.Coded;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;

import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class getOrders extends Thread {

	private static String jobname = "��ȡ�ſ���������ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static String requesttype="order";
	private static String requestmethod="youwuku.order.get";
	private static String fields="tid,status,num_iid,title,num,price,discount_fee,"
		+"discount_type,adjust_fee,post_fee,payment,total_fee,sku_id,properties_name,"
		+"modified,pic_path,buyer_id,seller_nick,buyer_nick,buyer_rate,seller_rate,creat_time,"
		+"pay_time,seller_id,ali_trade_no,outer_id,sku_outer_id,receiver_name,receiver_phone,"
		+"receiver_mobile,receiver_state,receiver_city,receiver_district,receiver_address,receiver_zip";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public getOrders() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				
			
				getOrderList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮������ж���
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		while(hasNextPage)
		{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("uid", Params.clientid) ;
			Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
			Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
			params.put("start_time",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT)) ;
			params.put("end_time", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));
			params.put("page", String.valueOf(pageIndex)) ;
			params.put("page_no", "40") ;
			
	
			String sign=UwukuUtil.makeSign(Params.clientid, requesttype, 
					params.get("start_time").toString(), 
					params.get("end_time").toString(),Params.appsecret);
			
			params.put("sign", sign);
			params.put("fields", fields);
			
			params.put("method", requestmethod);
			params.put("request_type", requesttype);
			params.put("platform", Params.platform);
			params.put("sign_type", Params.signtype);
			params.put("format", Params.format);
			params.put("v", Params.version);
			
			String responseText = CommHelper.sendRequest(Params.url+requesttype,params);
			
			//System.out.println(responseText);
			

			JSONObject jo = new JSONObject(responseText);
			
			int retcode=jo.optInt("code");
			
			if (retcode!=0)
			{
				hasNextPage = false ;
				Log.warn(jobname,"ȡ����ʧ��,������Ϣ:"+jo.optString("error"));
				break ;
			}
			
			int pageTotal=Double.valueOf(Math.ceil(jo.optInt("count")/40)).intValue(); //��ҳ��
			pageIndex=jo.optInt("page");//��ǰҳ��
			
			JSONArray dealList=jo.optJSONArray("order_info");
			
			if (dealList.length()==0)
			{
				hasNextPage = false ;
				//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
				if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
						compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
				{
					try
                	{
						String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
						PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
                	}catch(JException je)
                	{
                		Log.error(jobname, je.getMessage());
                	}
				}
				Log.info(jobname,"��������Ҫ����Ķ���!");
				break ;
			}
			
			for(int i=0;i<dealList.length();i++)
			{
				JSONObject deal=dealList.getJSONObject(i);
				
				Order o=new Order();
				o.setObjValue(o, deal);
		
				Log.info(o.getTid()+" "+o.getStatus()+" "+o.getCreat_time());
				
				/*
				 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
				 *2��ɾ���ȴ���Ҹ���ʱ��������� 
				 */		
				String sku;
				String sql="";
				if (o.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
				{	
					
					if (!OrderManager.isCheck("�������ⶩ��", conn, o.getTid()))
					{
						if (!OrderManager.TidLastModifyIntfExists("�������ⶩ��", conn, o.getTid(),o.getCreat_time()))
						{
							OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
											
							StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(),o.getSku_outer_id());
							StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), o.getSku_outer_id(), -o.getNum(),false);
						
						}
					}

					//�ȴ���Ҹ���ʱ��¼�������
				}
				
				
				else if (o.getStatus().equals("WAIT_BUYER_PAY"))
				{						
						
					StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid,  o.getTid(),o.getSku_outer_id(),o.getNum());
					StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), o.getSku_outer_id(), -o.getNum(),false);
					

					//������ǰ�����һ���������رս���
					//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
				}else if (o.getStatus().equals("TRADE_CLOSED_BY_SELLER"))
				{
				
					StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(), o.getSku_outer_id());
					if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, o.getTid(), o.getSku_outer_id()))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
						StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), o.getSku_outer_id(), -o.getNum(),false);
								
		
				}
				else if (o.getStatus().equals("TRADE_FINISHED") || o.getStatus().equals("WAIT_BUYER_CONFIRM_GOODS"))
				{
	
					StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(), o.getSku_outer_id());								

				}
				else if (o.getStatus().equals("WAIT_SELLER_AGREE")  
						|| o.getStatus().equals("SELLER_REFUSE_BUYER")
						|| o.getStatus().equals("WAIT_BUYER_RETURN_GOODS")
						|| o.getStatus().equals("WAIT_SELLER_CONFIRM_GOODS")
						|| o.getStatus().equals("REFUND_SUCCESS"))
				{
					//�����˻�
				
					OrderUtils.getRefund(jobname,conn,Params.tradecontactid,o);
					
				}
				
				
				//����ͬ����������ʱ��
                if (o.getCreat_time().compareTo(modified)>0)
                {
                	modified=o.getCreat_time();
                }
			}
		
			
			//�ж��Ƿ�����һҳ
			if(pageTotal>pageIndex)
				pageIndex ++ ;
			else
				hasNextPage = false ;
				
		}
			
		if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
		{
			try
        	{
        		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
        		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
        	}catch(JException je)
        	{
        		Log.error(jobname,je.getMessage());
        	}
		}
				
		
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
