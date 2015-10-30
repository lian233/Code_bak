package com.wofu.ecommerce.rke;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke.utils.Utils;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class GetItems extends Thread {

	private static String jobname = "获取麦斯卡经销商品作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取商品最新时间";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;
	private String columns="goods_id|goods_sn|goods_name|goods_number|last_modify";
	private String itemcolumns="products";

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.rke.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getItemList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.rke.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一天之内未发货的所有商品
	 */
	private void getItemList(Connection conn) throws Exception
	{		
		int pageno=1;
		ECSDao dao = new ECSDao(conn);
		String responseOrderListData="";
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=dao.intSelect(sql);
		Date temp = Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT);
		Document doc;
		Element ele;
		String result;
		int totalPage;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					String method ="search_goods_list";
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					Map<String, String> orderlistparams = new HashMap<String, String>();
					Log.debug(String.valueOf(startdate.getTime()/1000L));
					Log.debug(String.valueOf(enddate.getTime()/1000L));
			        //系统级参数设置
					orderlistparams.put("api_version", Params.ver);
			        orderlistparams.put("act", method);
			        orderlistparams.put("last_modify_st_time", String.valueOf(startdate.getTime()/1000L));
			        orderlistparams.put("last_modify_en_time", String.valueOf(enddate.getTime()/1000L));
			       
			        orderlistparams.put("pages", String.valueOf(pageno));
			        orderlistparams.put("counts", Params.pageSize);
			        orderlistparams.put("columns", columns);
			        Log.debug("page: "+pageno);
					responseOrderListData = Utils.sendByPost(orderlistparams, Params.url);
					Log.debug("responseItemListData: "+responseOrderListData);
					
					doc = DOMHelper.newDocument(responseOrderListData, "GBK");
					ele = doc.getDocumentElement();
					result = DOMHelper.getSubElementVauleByName(ele, "result");
					if (!"success".equals(result))
					{
						String errdesc=DOMHelper.getSubElementVauleByName(ele, "msg");
						Log.error(jobname, "取商品列表失败:"+errdesc);
						k=10;
						break;
					}
					Element info = DOMHelper.getSubElementsByName(ele, "info")[0];
					int totalCount=Integer.parseInt(DOMHelper.getSubElementVauleByName(info, "counts"));
					Log.debug("totalCount: "+totalCount);
					if (totalCount==0)
					{				
						if (pageno==1L)		
						{
							try
							{
								//如一天之内都取不到商品，而且当前天大于配置天，则将取商品最新时间更新为当前天的零点
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
		
					Element[] itemList = DOMHelper.getSubElementsByName(ele,"item");
					String goodId;
					String goodSn;
					String goodName;
					String goodNumber;
					Element item;
					for(int j=0;j<itemList.length;j++)
					{
						Element itemTemp=itemList[j];
						goodId = DOMHelper.getSubElementVauleByName(itemTemp, "goods_id");
						goodSn = DOMHelper.getSubElementVauleByName(itemTemp, "goods_sn");
						goodName = DOMHelper.getSubElementVauleByName(itemTemp, "goods_name");
						goodNumber = DOMHelper.getSubElementVauleByName(itemTemp, "goods_number");
						modified = new Date((Long.parseLong(DOMHelper.getSubElementVauleByName(itemTemp, "last_modify"))*1000L));
						StockManager.stockConfig(dao, orgid,Integer.parseInt(Params.tradecontactid),goodId,goodSn,goodName,Integer.parseInt(goodNumber)) ;
						item = StockUtils.getItemById(goodId,itemcolumns);
						if(null==item) continue;
						Element[] data_info = DOMHelper.getSubElementsByName(item, "data_info");
						Element skuInfo;
						String sku;
						String skuId;
						String qty;
						for(int i=0;i<data_info.length;i++){
							skuInfo = data_info[i] ;
							sku=DOMHelper.getSubElementVauleByName(skuInfo, "sku");
							skuId=DOMHelper.getSubElementVauleByName(skuInfo, "product_id");
							qty="".equals(DOMHelper.getSubElementVauleByName(skuInfo, "product_number"))?"0":DOMHelper.getSubElementVauleByName(skuInfo, "product_number");
							StockManager.addStockConfigSku(dao, orgid,goodId,skuId,sku,Integer.parseInt(qty)) ;
						}
						item=null;
						if(modified.compareTo(temp)>0)  {
							temp =modified;
						}
						
					}
					totalPage = totalCount % Integer.parseInt(Params.pageSize)==0?totalCount / Integer.parseInt(Params.pageSize):totalCount>Integer.parseInt(Params.pageSize)?totalCount/Integer.parseInt(Params.pageSize):1;
					Log.info("totalPage: "+totalPage);
					responseOrderListData=null;
					//判断是否有下一页
					if (pageno>=totalPage) break;
					
					pageno++;
					
				}
				if (temp.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(temp,Formatter.DATE_TIME_FORMAT);
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
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
