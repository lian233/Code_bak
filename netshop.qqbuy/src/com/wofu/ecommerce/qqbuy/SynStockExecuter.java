package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;
import com.wofu.base.job.timer.TimerJob;
import com.wofu.base.job.Executer;

public class SynStockExecuter extends Executer {
	
	private String appOAuthID = "" ;
	private String secretOAuthKey = "" ;
	private String accessToken = "" ;
	private String uin = "" ;
	private String cooperatorId = "" ;
	private static String uri="/item/modifySKUStock.xhtml";
	private String tradecontactid="";
	private String dbname="";
	private String pageSize = "" ;
	private String jobname = "" ;
	private String encoding = "" ;
	private int stockAlarmQty = 7 ;
	private String startTime = "" ;
	private String endTime = "" ;
	private static long monthMillis = 30 * 24 * 60 * 60 * 1000L ; 

	
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		//TimerJob job=(TimerJob) this.getExecuteobj();
		Properties prop=StringUtil.getStringProperties(job.getParams());

		appOAuthID=prop.getProperty("appOAuthID");
		secretOAuthKey=prop.getProperty("secretOAuthKey");
		accessToken=prop.getProperty("accessToken");
		uin=prop.getProperty("uin");
		cooperatorId=prop.getProperty("cooperatorId");
		tradecontactid=prop.getProperty("tradecontactid");
		dbname=prop.getProperty("dbname");
		pageSize=prop.getProperty("pageSize");
		jobname=prop.getProperty("jobname");
		encoding=prop.getProperty("encoding");
		stockAlarmQty=Integer.parseInt(prop.getProperty("stockAlarmQty"));
		
		startTime=Formatter.format(new Date(new Date().getTime()-10*monthMillis), Formatter.DATE_TIME_FORMAT);
		endTime=Formatter.format((new Date()), Formatter.DATE_TIME_FORMAT);
		
		Log.info("startTime="+startTime) ;
		Log.info("endTime="+endTime) ;
		
		Connection conn=PoolHelper.getInstance().getConnection(dbname);
		Hashtable<String, String> inputParams = new Hashtable<String, String>() ;
		inputParams.put("jobname", "") ;
		inputParams.put("accessToken", accessToken) ;
		inputParams.put("appOAuthID", appOAuthID) ;
		inputParams.put("secretOAuthKey", secretOAuthKey) ;
		inputParams.put("cooperatorId", cooperatorId) ;
		inputParams.put("uin", uin) ;
		inputParams.put("encoding", encoding) ;
		inputParams.put("startTime", startTime) ;
		inputParams.put("endTime", endTime) ;
		inputParams.put("pageSize", pageSize) ;

		
		//获取QQ网购所有上架商品ID
		List<Goods> goodsList = StockUtils.getSkuList(jobname, inputParams) ;
		Log.info(jobname,"获取到"+goodsList.size()+"个QQ网购商品。") ;
		for (int k=0;k<10;)
		{
			int update = 0 ;
			try
			{
				String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid='"+tradecontactid+"'";
				int orgid=SQLHelper.intSelect(conn, sql);
				for(int i = 0 ; i < goodsList.size() ; i++)
				{
					try 
					{
						Goods goods = goodsList.get(i) ;
						SkuInfo skuInfo = goods.getStockList().get(0) ;
						String stockHourseId = skuInfo.getStockhouseId() ;
						int stockCount = skuInfo.getStockCount() ;
						String sku = skuInfo.getStockLocalcode() ;
						//根据sku取得系统库存，更新到QQ网购
						int qty=StockManager.getTradeContactUseableStock(conn,Integer.valueOf(tradecontactid).intValue(),sku);
						
							//新库存=系统可用库存+QQ网购已经付款库存
							int newQty = qty + skuInfo.getStockPayedNum() ;
							if(newQty < stockAlarmQty)
							{
								newQty = 0 ;
								Log.info("商品【"+ sku +"】已经达到境界库存:"+ stockAlarmQty +",同步库存为:"+ newQty +"") ;
							}
							int oldQty = skuInfo.getStockCount() ;
							//QQ网购仓库id
							String stockHouseId = skuInfo.getStockhouseId() ;
							String skuid = goods.getSkuId() ;
							
							PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
							sdk.setCharset(encoding) ;
							HashMap<String, Object> params = sdk.getParams(uri);
							params.put("charset", encoding) ;
							params.put("format", "xml") ;
							params.put("cooperatorId", cooperatorId) ;
							params.put("skuId", skuid) ;
							params.put("stockhouseId", stockHouseId) ;
							params.put("stockCount", String.valueOf(newQty)) ;
							
							String responseText = sdk.invoke() ;
							
							Document doc = DOMHelper.newDocument(responseText, encoding);
							Element resultElement = doc.getDocumentElement();
							String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
							if("0".equals(errorCode))
							{
								update ++ ;
								Log.info("更新QQ网购商品库存成功,sku【"+ sku +"】,原库存:"+ oldQty +",新库存:"+ newQty +",付款库存:"+ skuInfo.getStockPayedNum() +",状态:"+ skuInfo.getStockSaleState()) ;
							}
							else if("3831".equals(errorCode))
							{
								Log.error(jobname, "更新QQ网购商品库存失败,sku【"+ sku +"】,错误信息:"+errorCode+",该商品已报名活动，不能减少库存数量！") ;
							}
							else
							{
								String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
								Log.error(jobname, "更新QQ网购商品库存失败,sku【"+ sku +"】,错误信息:"+errorCode+errorMessage) ;
							}
						
						
						//检查未配置在售商品警戒库存
					/*
						if("STOCK_STATE_SELLING".equalsIgnoreCase(skuInfo.getStockSaleState()))
						{
							sql="select count(*) from ecs_stockconfig with(nolock) where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"'";
							if (SQLHelper.intSelect(conn, sql) > 0)
							{
								sql="update ecs_stockconfig set status='1' where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"'" ;
								SQLHelper.executeSQL(conn, sql) ;
							}
							else
								StockManager.StockConfig(conn, skuInfo.getStockLocalcode(), Integer.parseInt(tradecontactid), 1) ;
						}
						else
						{
							//如果上架产品下架,更新状态
							sql="select count(*) from ecs_stockconfig with(nolock) where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"' and status=1";
							if (SQLHelper.intSelect(conn, sql) > 0)
							{
								sql="update ecs_stockconfig set status='0' where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"' and status='1'" ;
								SQLHelper.executeSQL(conn, sql) ;
							}
						}
						*/
					
					} catch (Exception e) {
						Log.error(jobname, "更新QQ网购商品库存错误:"+e.getMessage()) ;
						e.printStackTrace() ;
					}
				}
				Log.info("更新QQ网购商品库存完成，本次共更新"+ update + "个QQ网购商品库存。");
				//结束循环
				k = 10 ;
			} catch (Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			} finally 
			{
				try 
				{
					if (conn != null)
						conn.close();
				} catch (Exception e) 
				{
					throw new JException("关闭数据库连接失败");
				}
			}
		}
	}
	
}