package com.wofu.ecommerce.taobao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;

import com.taobao.api.domain.Trade;
import com.taobao.api.request.LogisticsOfflineSendRequest;

import com.taobao.api.response.LogisticsOfflineSendResponse;

import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

import com.wofu.common.tools.util.log.Log;


public class getGoodsNum extends Thread {

	private static String jobname = "ȡ������Ʒ����������ҵ";
	

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;

			try {		
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
			
				String sql="select tid from ConsumeCustomer where goodsnum =0 and tradecontactid="+Params.tradecontactid;
				Log.info(sql);
				List tlist=SQLHelper.multiRowListSelect(connection, sql);
				for(Iterator it=tlist.iterator();it.hasNext();)
				{
					String tid=(String) it.next();
					Log.info(tid);
					Log.info(Params.url+" "+Params.appkey+" "+Params.appsecret);
					Trade td=OrderUtils.getFullTrade(tid,Params.url,Params.appkey,Params.appsecret,Params.authcode);
					sql="update ConsumeCustomer set goodsnum="+td.getOrders().size()+" " +
							"where tradecontactid="+Params.tradecontactid +" and tid='"+tid+"'";
					SQLHelper.executeSQL(connection, sql);
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
		
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	}
