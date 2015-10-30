package com.wofu.ecommerce.groupon;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import meta.MD5Util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.groupon.domain.model.ws.DisneyRequestBean;
import com.groupon.ws.ObjBodyWriter;
import com.wofu.ecommerce.groupon.Params;
import com.wofu.ecommerce.groupon.StockUtils;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class UpdateStock extends Thread{
	
	private static String jobname = "更新团宝库存作业";
	
	private boolean is_updating=false;
	

	public UpdateStock() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			String tid="";
			String sku="";
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.groupon.Params.dbname);
				Vector vtgoodsinfo=getGoodsInfo(connection);
				
				for(int i=0;i<vtgoodsinfo.size();i++)
				{
					Hashtable<String,String> skuinfo=(Hashtable<String,String>) vtgoodsinfo.get(i);
					tid=skuinfo.get("tid").toString();
					sku=skuinfo.get("sku").toString();										
					skuinfo.put("addflag", "1");
					
					Hashtable<String,String> htwsinfo=new Hashtable<String,String>();
					htwsinfo.put("key", Params.key);
					htwsinfo.put("namespace", Params.namespace);
					htwsinfo.put("categoryid", Params.categoryid);
					htwsinfo.put("wsurl", Params.wsurl);
					htwsinfo.put("encoding", Params.encoding);
					
					StockUtils.updateStock(jobname, htwsinfo, tid, "", skuinfo);
					
					StockManager.bakSynReduceStore(jobname,connection,Params.tradecontactid,tid,sku);
				}
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				if (e.getMessage().indexOf("SKU不存在")>=0)
				{
					try
					{
						StockManager.bakSynReduceStore(jobname,connection,Params.tradecontactid,tid,sku);
					}
					catch(JException je)
					{
						Log.error("105", jobname, Log.getErrorMessage(je));
					}
				}
				
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_updating = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.groupon.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	/*
	 * 返回SKU和库存数量 SKU qty
	 */
	private Vector getGoodsInfo(Connection conn)
	{
		Vector vtinfo=null;
		try
		{			
			String sql="select tid,sku,b.customno,ltrim(rtrim(s.shortname)) sizecode,"
						+"ltrim(rtrim(cl.shortname)) colorcode,qty "
						+" from ECO_SynReduceStore a,goods b,barcode c,size s,color cl "
						+"where a.sku=c.custombc and b.goodsid=c.goodsid and c.sizeid=s.id and c.colorid=cl.id "
						+" and b.MeasureType=s.MeasureTypeid and a.tradecontactid='"+Params.tradecontactid+"' and synflag=0";
			vtinfo=SQLHelper.multiRowSelect(conn, sql);
		}
		catch(JSQLException e)
		{
			Log.error(jobname, "取商品库存更改信息出错:"+e.getMessage());
		}
		return vtinfo;
	}
	
	public String toString()
	{
		return jobname + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
