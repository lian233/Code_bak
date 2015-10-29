package com.wofu.intf.jw;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.wofu.business.intf.IntfUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynProductInfo extends Thread {
	
	private static String jobname = "同步商品资料作业";
	private static String service="subItemAddOrUpdate";
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				List infsheetlist=JwUtil.getintfsheetlist(conn,900001,100);
				Item item=null;
				skuList sku=null;
				String bizData1=null;
				for(Iterator it = infsheetlist.iterator();it.hasNext();){
					Hashtable ht = (Hashtable)it.next();
					Integer serialID = (Integer)ht.get("SerialID");
					String operData = (String)ht.get("OperData");
					String sql ="select a.custombc goodsId,a.custombc skuId,a.custombc barcode,convert(varchar(100),"+
					"a.lastModiTime,20) listTime ,ltrim(rtrim(a.pkspec)) skuSpecId,ltrim(rtrim(b.name)) title,b.price "+
					"from barcode a with(nolock),merchandise b with(nolock) where a.mid=b.mid and a.barcodeid='"+operData+"'";
					Hashtable re = SQLHelper.oneRowSelect(conn, sql);
					ArrayList itemList = new ArrayList();
					item =new Item();
					item.getMapData(re);
					sku = new skuList();
					sku.getMapData(re);
					item.getSkuList().getRelationData().add(sku);
					itemList.add(item);
					bizData1 = "{\"itemList\":"+item.toJSONArray(itemList)+"}";
					String sign=JwUtil.makeSign(bizData1);
					Map requestParams=JwUtil.makeRequestParams(bizData1, service, 
							Params.appkey,Params.format, sign);
					String result=CommHelper.sendRequest(Params.url, requestParams, "");
					Log.info("resutl: "+result);
					result = result.replaceAll("\\\\", "").replaceAll("\"\\[", "\\[").replaceAll("\\]\"", "\\]");
					JSONObject items = new JSONObject(result);
					ArrayList<String> sqlList = new ArrayList<String>();
					if(items.getBoolean("isSuccess")){
						JSONArray itemarr = items.getJSONArray("body");
						for(int i=0;i<itemarr.length();i++){
							JSONObject itemTemp = itemarr.getJSONObject(i);
							String outerSkuId =itemTemp.getString("outerId"); 
							String skuId =itemTemp.getString("skuId"); 
							sqlList.add(new StringBuilder().append("update barcode set outerSkuId='")
									.append(outerSkuId).append("' where custombc='").append(skuId).append("'").toString());
						}
						conn.setAutoCommit(false);
						if(sqlList.size()>0)
						SQLHelper.executeBatch(conn, sqlList);
						JwUtil.backUpIntsheetData(conn,serialID);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info("同步商品成功,barcodeid: "+operData);
					}else{
						String errMsg = items.getString("body");
						Log.error("同步商品资料出错",errMsg);
						if(errMsg.indexOf("关系已存在")!=0){
							conn.setAutoCommit(false);
							JwUtil.backUpIntsheetData(conn,serialID);
							conn.commit();
							conn.setAutoCommit(true);
						}
					}
					
				}
				
			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
}
