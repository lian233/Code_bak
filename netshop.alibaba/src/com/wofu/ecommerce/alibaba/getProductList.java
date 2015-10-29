package com.wofu.ecommerce.alibaba;
/**
 * 获取阿里巴巴中国网站会员所有的产品
 */
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;

public class getProductList extends Thread {
	private static String jobname = "获取阿里巴巴商品列表";
	
	private static String apiName="offer.getPublishOfferList";//"offer.getAllOfferList";
	
	private static String lasttimeconfvalue=Params.username+"取上架商品最新时间";
	
	private static long daymillis=24*60*60*1000L;
	
	private boolean is_importing=false;
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static int interval = 30 ;
	
	private static String type="ALL";  //只支持SALE
	
	private static String returnFields="offerId,offerStatus,subject,amount,gmtModified,productFeatureList";
	
	private static String lasttime;
	private static String access_token=null;
	
	public getProductList(){
		setDaemon(true);
		setName(jobname);
	}
	
	
	public void run() {
		//获取授权令牌的参数
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {		
			Connection conn = null;
			is_importing = true;
			try {		
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("client_id", Params.appkey);
			    params.put("redirect_uri", Params.redirect_uri);
			    params.put("client_secret", Params.secretKey);
			    params.put("refresh_token", Params.refresh_token);
			    String returns=AuthService.refreshToken(Params.host, params);
			    JSONObject access=new JSONObject(returns);
		    	Params.token=access.getString("access_token");
		    	
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttime=PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
	
				getOnSaleProducts(conn);

			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
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
	
	
	private void getOnSaleProducts(Connection conn) throws Exception{
		
		int i=0;
		int j=0;
		int pageIndex=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		ECSDao dao=new ECSDao(conn);

		Log.info("开始取阿里巴巴上架商品");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn, sql);
		for(int k=0;k<10;)
		{
			try
			{
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("type", type) ;
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Log.info("取商品时间为: "+startdate);
				params.put("returnFields", returnFields);
				params.put("timeStamp",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT));
				params.put("page", String.valueOf(pageIndex)) ;
				params.put("pageSize", "25") ;
				params.put("access_token", Params.token);
				params.put("orderBy", "gmt_modified:asc");
				String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,apiName,Params.version,Params.requestmodel,Params.appkey);
			    
				String responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//				Log.info("取商品资料返回结果: "+responseText);
				//返回结果集
				JSONObject jresp=new JSONObject(responseText);
				
				
				JSONObject jres=(JSONObject) jresp.getJSONObject("result");
			
				//返回的商品列表资料
				JSONArray jresult=jres.getJSONArray("toReturn");
				
				while(true)
				{
								
					if (jres.getInt("total")==0)
					{				
						if (i==0)		
						{
							try
							{
								//
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
							}catch(ParseException e)
							{
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
					
					
					
//					if (!jres.getBoolean("success")){
//						Log.error(jobname,"获取阿里巴巴商品列表失败,错误信息:"+jresp + "," + "");
//						break;
//					}
//					else
//					{			
						
						for(int m=0;m<jresult.length();m++){
							//迭代商品
							JSONObject j1=jresult.getJSONObject(m);
							Goods gd=new Goods();
							gd.setObjValue(gd, j1);
							String goodStatus=gd.getOfferStatus();
							Log.info("商品状态: "+goodStatus);
							String gmtModified=CommonUtil.convertToTime(gd.getGmtModified());
							Log.info("商品最后修改时间: "+gmtModified);
								//更新同步订单最新时间
							
			                if (Formatter.parseDate(gmtModified,Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
			                {
			                	modified=Formatter.parseDate(gmtModified,Formatter.DATE_TIME_FORMAT);
			                }
							if(goodStatus.indexOf("delete")!=-1 || goodStatus.indexOf("outdated")!=-1  ) continue;  //商家删除的商品不进系统
							i=i+1;
							//阿里商品编码
							long offerid=gd.getOfferId();
							//商家sku-itemcode
							String itemCode = "";
							JSONArray arr = new JSONArray(gd.getProductFeatureList().toString());
							for(int k1=0; k1<arr.length();k1++){
								String name = arr.getJSONObject(k1).getString("name");
								if("货号".equals(name)) itemCode=(String)arr.getJSONObject(k1).getString("value");
							}
							Log.info("itemCode: "+itemCode);
							
							//根据ID获得单个商品的详细信息和SKU   offer.get
							Hashtable<String, String> params1 = new Hashtable<String, String>() ;
							params1.put("offerId", String.valueOf(gd.getOfferId()));
							params1.put("returnFields", "offerId,offerStatus,subject,amount,type,gmtCreate,gmtModified,skuArray,productFeatureList");
							
							String urlPath1=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
							String response = ApiCallService.callApiTest(Params.url, urlPath1, Params.secretKey, params1);
							
//							Log.info("商品详情: "+response);
							JSONObject res=new JSONObject(response);
							
							
							if(!res.getJSONObject("result").getBoolean("success")){
								Log.info("获取阿里巴巴商品详细资料失败,offerID:"+gd.getOfferId()+"错误信息:"+ res.getString("error_code") + "," + res.getString("error_message"));
								continue;
							}
	
							JSONArray jarray=res.getJSONObject("result").getJSONArray("toReturn");
							Goods oo=new Goods();
							oo.setObjValue(oo,jarray.getJSONObject(0));
							StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),String.valueOf(offerid),itemCode,
									gd.getSubject(),Long.valueOf(gd.getAmountOnSale()).intValue()) ;
							
							//商品SKU
							if(oo.getSkuArray().getRelationData().size()>0){
								for(Iterator s=oo.getSkuArray().getRelationData().iterator();s.hasNext();){
									GoodsSKU ch=(GoodsSKU)s.next();
										Log.info("SKU "+ch.getCargoNumber()+" "+toDateFormat(oo.getGmtModified()));
										StockManager.addStockConfigSku(dao, orgid,String.valueOf(oo.getOfferId()),
												String.valueOf(ch.getSpecId()),ch.getCargoNumber(),Long.valueOf(ch.getCanBookCount()).intValue()) ;
								}
							}
							
							
			                Log.info("modified: "+modified);
						}
						
						//获取总条数
						int total=jres.getInt("total");
						//总页数
						int pageTotal=total%25==0?total/25==0?1:total/25:total/25+1;
						Log.info("当前页:　"+pageIndex);
						Log.info("总页数:　"+pageTotal);
						//判断是否有下一页
						
						if(pageTotal>pageIndex)
							pageIndex ++ ;
						else
						{
							break;
						}
					
				//	}	
				}//while未
				
				Log.info("取阿里巴巴上架总商品数:"+String.valueOf(i)+" 总SKU数:"+String.valueOf(j));
		
				
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
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
		Log.info("本次取商品完毕!");
	}
	public String toDateFormat(String s)throws Exception{
		String m=s.substring(0, 14);
		String date=m.substring(0, 4)+"-"
					+m.substring(4,6)+"-"+
					m.substring(6,8)+" "+
					m.substring(8,10)+":"+
					m.substring(10,12)+":"+
					m.substring(12,14);

		return date;
	}
	
	//根据产品id查询产品详情
	private void getProductDetail(Long offerId){
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("offerId",offerId+"");
			params.put("returnFields", "offerId,offerStatus,subject,amount,type,gmtCreate,gmtModified,skuArray,productFeatureList");
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
			String response =ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//			Log.info("商品详情: "+response);
			JSONObject res=new JSONObject(response);
			JSONObject jo=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0);
		}catch(Exception ex){
			Log.error("查询产品详情出错",ex.getMessage());
		}
		
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
