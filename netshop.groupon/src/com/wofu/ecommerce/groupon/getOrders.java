package com.wofu.ecommerce.groupon;

import java.sql.Connection;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;


import com.wofu.ecommerce.groupon.Params;
import com.wofu.business.util.PublicUtils;

import com.wofu.common.tools.sql.PoolHelper;

import com.wofu.common.tools.util.Formatter;

import com.wofu.common.tools.util.log.Log;


public class getOrders extends Thread {

	private static String jobname = "��ȡ�ű�������ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private boolean is_importing=false;

	

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
						com.wofu.ecommerce.groupon.Params.dbname);					
				

				Hashtable<String,String> htwsinfo=new Hashtable<String,String>();
				
				htwsinfo.put("tradecontactid", Params.tradecontactid);
				htwsinfo.put("key", Params.key);
				htwsinfo.put("wsurl", Params.wsurl);
				htwsinfo.put("username", Params.username);
				htwsinfo.put("lasttimeconfvalue", lasttimeconfvalue);
				htwsinfo.put("namespace", Params.namespace);
				htwsinfo.put("limit", String.valueOf(Params.limit));
				htwsinfo.put("total", String.valueOf(Params.total));
				htwsinfo.put("style", "0");
				htwsinfo.put("encoding", Params.encoding);
				htwsinfo.put("categoryid", Params.categoryid);
				
				
				List plist = ProjectUtils.getBusinessProjectInfo(jobname,htwsinfo);
	
			
				for (Iterator it = plist.iterator(); it.hasNext();) {
				
					String grouponid = (String) it.next();

					Date starttime=new Date(Formatter.parseDate(PublicUtils.getConfig(connection, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date endtime=new Date(starttime.getTime()+daymillis);
					OrderUtils.getBusinessOrderList(jobname,connection,htwsinfo,grouponid,starttime,endtime);				
				}				
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.groupon.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
