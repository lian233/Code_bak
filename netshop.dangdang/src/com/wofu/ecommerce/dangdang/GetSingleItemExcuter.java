package com.wofu.ecommerce.dangdang;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.dangdang.util.CommHelper;

public class GetSingleItemExcuter extends Executer {
	private String itemId="";
	private String app_key="";
	private String session="";
	private String app_Secret="";
	private String orgId="";
	private String url="";
	private String tradecontactid="";
	private final String jobName = "获取当当指定的商品";
	public void run(){
		
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		itemId = prop.getProperty("itemId") ;
		app_key = prop.getProperty("app_key") ;
		session = prop.getProperty("session") ;
		app_Secret = prop.getProperty("app_Secret") ;
		orgId = prop.getProperty("orgId") ;
		url = prop.getProperty("url") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		
		try{
			//检查未入订单
			updateJobFlag(1);
			Date temp = new Date();
			String methodName="dangdang.item.get";
			//生成验证码 --md5;加密
			String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp)  ;
			Hashtable<String, String> param1 = new Hashtable<String, String>() ;
			param1.put("sign", sign) ;
			param1.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
			param1.put("app_key",app_key);
			param1.put("method",methodName);
			param1.put("format","xml");
			param1.put("session",session);
			param1.put("sign_method","md5");
			param1.put("it", itemId) ;
			
			String itemdetailresponseText = CommHelper.sendRequest(url, "GET",param1,"") ;
			//Log.info("itemdetailresponseText: "+itemdetailresponseText);
			
			Document itemdetaildoc = DOMHelper.newDocument(itemdetailresponseText, "GBK") ;
			Element result = itemdetaildoc.getDocumentElement() ;
		
			Element error = null;
			if(DOMHelper.ElementIsExists(result, "Error"))
				error = (Element)result.getElementsByTagName("Error").item(0) ;
			if(DOMHelper.ElementIsExists(result, "Result"))
				error = (Element)result.getElementsByTagName("Result").item(0) ;
			if(error != null && DOMHelper.ElementIsExists(error, "operCode"))
			{
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				Log.info("获取当当商品详细资料失败,商品ID:"+ itemId +",错误信息："+operCode+":"+operation) ;
				
			}
			
			

			Element itemDetail = (Element)result.getElementsByTagName("ItemDetail").item(0);
			String itemCode = DOMHelper.getSubElementVauleByName(itemDetail, "model");
			String title = DOMHelper.getSubElementVauleByName(itemDetail, "itemName");
			String itemId = DOMHelper.getSubElementVauleByName(itemDetail, "itemID");
			int qty= Integer.parseInt(DOMHelper.getSubElementVauleByName(itemDetail, "stockCount"));
			Log.info("取到当当商品,货号: "+itemCode);
			StockManager.stockConfig(this.getDao(), Integer.parseInt(orgId), Integer.parseInt(tradecontactid), itemId, itemCode, title, qty);
			if(DOMHelper.ElementIsExists(itemDetail, "SpecilaItemInfo"))
			{
				
				NodeList specilaItemInfo =  result.getElementsByTagName("SpecilaItemInfo") ;
				//Log.info("specilaItemInfo's size: "+specilaItemInfo.getLength());
				for(int j = 0 ; j < specilaItemInfo.getLength() ; j++)
				{
					try{
						Element skuInfo = (Element) specilaItemInfo.item(j) ;
						
						String quantity = DOMHelper.getSubElementVauleByName(skuInfo, "stockCount") ;
						String sku = DOMHelper.getSubElementVauleByName(skuInfo, "outerItemID") ;
						String subItemID = DOMHelper.getSubElementVauleByName(skuInfo, "subItemID") ;
						StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgId),itemId,subItemID,sku,Integer.valueOf(quantity).intValue()) ;
					}catch(Exception ex){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
							
						Log.error(jobName, ex.getMessage());
					}
					
					
				}
			}
			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		}catch(Exception ex){
			try{
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(ex));
			}catch(Exception e){
				Log.error(jobName, ex.getMessage());
			}
			
			Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(ex));
		}finally {
			try
			{
				if(!this.getConnection().getAutoCommit()) this.getConnection().setAutoCommit(true);
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"更新处理标志失败");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}
		
		
	}
}
