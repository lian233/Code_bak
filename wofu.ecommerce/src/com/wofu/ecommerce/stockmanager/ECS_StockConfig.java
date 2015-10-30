package com.wofu.ecommerce.stockmanager;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.ware.Sku;
import com.jd.open.api.sdk.domain.ware.Ware;
import com.jd.open.api.sdk.request.ware.WareGetRequest;
import com.jd.open.api.sdk.response.ware.WareGetResponse;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.FenxiaoProduct;
import com.taobao.api.domain.FenxiaoSku;
import com.taobao.api.request.FenxiaoProductsGetRequest;
import com.taobao.api.response.FenxiaoProductsGetResponse;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.ecommerce.oauthpaipai.Params;
import com.wofu.ecommerce.vjia.SoapBody;
import com.wofu.ecommerce.vjia.SoapHeader;
import com.wofu.ecommerce.vjia.SoapServiceClient;
import com.wofu.ecommerce.yhd.utils.Utils;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;

public class ECS_StockConfig extends PageBusinessObject {
	private int serialid;
	private int orgid;
	private String itemid;
	private String title;
	private String itemcode;
	private int alarmqty;
	private int alarmstyle;
	private int isneedsyn;
	private double addstockqty;
	private int stockcount;
	private int errflag;
	private String errmsg;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	

	private DataRelation ecsstockconfigofecsstockconfigs =new DataRelation("ecsstockconfigofecsstockconfig","com.wofu.ecommerce.stockmanager.ECS_StockConfig");

	public ECS_StockConfig()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="itemid";
		
		this.exportQuerySQL="select b.orgname as orgid,a.itemid,a.itemcode,a.title,"
			+"a.alarmqty,case when a.alarmstyle=1 then '调整库存为零' when a.alarmstyle=2 then '邮件通知' else '短信通知' end as alarmstyle,"
			+"case when a.isneedsyn=0 then '否' else '是' end as isneedsyn,addstockqty,stockcount,"
			+"case when a.errflag=0 then '否' else '是' end as errflag,a.errmsg,a.creator,a.createtime,a.updator,a.updatetime "
			+"from {searchSQL} a,ecs_org b "
			+"where a.orgid=b.orgid ";
	}
	
	public void update() throws Exception
	{
		try{
			this.getJSONData();
			for (int i=0;i<this.getEcsstockconfigofecsstockconfigs().getRelationData().size();i++)
			{
				ECS_StockConfig stockconfig=(ECS_StockConfig) this.getEcsstockconfigofecsstockconfigs().getRelationData().get(i);
				

				stockconfig.updator=this.getUserInfo().getLogin();
				stockconfig.updatetime=new Date();
				this.getDao().updateByKeys(stockconfig, "orgid,itemid");
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		
	}
	
	public void getSkus() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		
		String orgid=prop.getProperty("orgid");
		String itemid=prop.getProperty("itemid");
		
		String sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+itemid+"' order by sku";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	//下载特定的商品
	public void getSpecificItem()throws Exception{
		try{
			String reqData = this.getReqData();
			Properties prop = StringUtil.getIniProperties(reqData);
			String orgid = prop.getProperty("orgid");
			String sql = "select a.shortname from ecs_platform a with(nolock) ,ecs_org_params b with(nolock) where a.platformid=b.platformid and b.orgid="+orgid;
			String platform = this.getDao().strSelect(sql);
			String itemids = prop.getProperty("itemids");
			if("360buy".equalsIgnoreCase(platform)){
				getJingDongItem(itemids,orgid);
			}else if("dangdang".equalsIgnoreCase(platform)){
				getDangDangItem(itemids,orgid);
			}else if("taobaofenxiao".equalsIgnoreCase(platform)){
				getTaobaoFenxiaoItem(itemids,orgid);
			}else if("val".equalsIgnoreCase(platform)){
				getValItem(itemids,orgid);
			}else if("suning".equalsIgnoreCase(platform)){
				getSuNingItem(itemids,orgid);
			}else if("yhd".equalsIgnoreCase(platform)){
				getYhdItem(itemids,orgid);
			}else if("mogujie".equalsIgnoreCase(platform)){
				getMgjItem(itemids,orgid);
			}else if("paipai".equalsIgnoreCase(platform)){
				getPaiPaiItem(itemids,orgid);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	//下载paipai商品
	private void getPaiPaiItem(String itemids, String orgid2) throws Exception {
		String sql = "select url, token,uid,sellerid,secretaccesskey,encoding from ecs_org_params with(nolock) where orgid="+orgid2;
		Hashtable params = this.getDao().oneRowSelect(sql);
		String url = params.get("url").toString();
		String secretkey = params.get("secretaccesskey").toString();
		String token = params.get("token").toString();
		String uid = params.get("uid").toString();
		String spid = params.get("sellerid").toString();
		String encoding = params.get("encoding").toString();
		sql= "select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgid2;
		String tradecontactid = this.getDao().strSelect(sql);
		PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid,secretkey, token, Long.valueOf(uid));
		
		sdk.setCharset(encoding);
		
		HashMap<String, Object> param = sdk.getParams("/item/getItem.xhtml");
		param.put("needDetailInfo", "1");
		param.put("sellerUin", uid);
		param.put("orderType", "0");
		param.put("format", "json");
		param.put("pureData", "1");
		if(itemids.indexOf(",")>0){
			StringBuilder sqlwhere = new StringBuilder("select * from ecs_stockconfig with(nolock) where orgid=")
				.append(orgid2).append(" and itemId in(");
			String[] items = itemids.split(",");
			for(String e:items){
				param.put("itemCode", e);
				String result = sdk.invoke();
				System.out.println("result: "+result);
				Document doc = DOMHelper.newDocument(result);
				Element ele = doc.getDocumentElement();
				String errorCode = DOMHelper.getSubElementVauleByName(ele, "errorCode");
				if("0".equals(errorCode)){
					String itemId = DOMHelper.getSubElementVauleByName(ele, "itemCode");
					String itemcode = DOMHelper.getSubElementVauleByName(ele, "itemLocalCode");
					String itemName = DOMHelper.getSubElementVauleByName(ele, "itemName");
					int qty = Integer.parseInt(DOMHelper.getSubElementVauleByName(ele, "stockCount"));
					StockManager.stockConfig(this.getDao(), Integer.parseInt(orgid2), Integer.parseInt(tradecontactid), itemId, itemcode, itemName, qty);
					Element stockList = DOMHelper.getSubElementsByName(ele, "stockList")[0];
					Element[] skus = DOMHelper.getSubElementsByName(stockList, "stock");
					for(Element el:skus){
						String skuid= DOMHelper.getSubElementVauleByName(el, "stockId");
						String sku= DOMHelper.getSubElementVauleByName(el, "stockLocalCode");
						int skuQty  = Integer.parseInt(DOMHelper.getSubElementVauleByName(el, "stockCount"));
						//DataCentre dao,int orgid,String itemid,String skuid,String sku,int qty
						StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgid2), itemId, skuid, sku, skuQty);
					}
					sqlwhere.append("'").append(e).append("',");
				}
				
				
			}
			this.setCurrpage(1);
			this.setPagesize(20);
			sqlwhere.delete(sqlwhere.length()-1,sqlwhere.length()).append(")");
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sqlwhere.toString(),this.searchOrderFieldName,this.orderMode)));
			
			
		}else{
			param.put("itemCode", itemids);
			//Log.info("itemCode: "+params.get("itemCode"));
			String result = sdk.invoke();
			System.out.println("result: "+result);
			Document doc = DOMHelper.newDocument(result);
			Element ele = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(ele, "errorCode");
			if("0".equals(errorCode)){
				String itemId = DOMHelper.getSubElementVauleByName(ele, "itemCode");
				String itemcode = DOMHelper.getSubElementVauleByName(ele, "itemLocalCode");
				String itemName = DOMHelper.getSubElementVauleByName(ele, "itemName");
				int qty = Integer.parseInt(DOMHelper.getSubElementVauleByName(ele, "stockCount"));
				StockManager.stockConfig(this.getDao(), Integer.parseInt(orgid2), Integer.parseInt(tradecontactid), itemId, itemcode, itemName, qty);
				Element stockList = DOMHelper.getSubElementsByName(ele, "stockList")[0];
				Element[] skus = DOMHelper.getSubElementsByName(stockList, "stock");
				for(Element el:skus){
					String skuid= DOMHelper.getSubElementVauleByName(el, "skuId");
					String sku= DOMHelper.getSubElementVauleByName(el, "stockLocalCode");
					int skuQty  = Integer.parseInt(DOMHelper.getSubElementVauleByName(el, "stockCount"));
					//DataCentre dao,int orgid,String itemid,String skuid,String sku,int qty
					StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgid2), itemId, skuid, sku, skuQty);
				}
				sql = new StringBuilder("select * from ecs_stockconfig with(nolock) where orgid=")
				.append(orgid2).append(" and itemId ='").append(itemids).append("' ").toString();
				this.setCurrpage(1);
				this.setPagesize(20);
				this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
			}
			
			
		}
		
		
	}

	private void getMgjItem(String itemids, String orgid2) throws Exception {
		String sql = "select url,appkey,appsecret, token from ecs_org_params with(nolock) where orgid="+orgid2;
		Hashtable params = this.getDao().oneRowSelect(sql);
		String url = params.get("url").toString();
		String appkey = params.get("appkey").toString();
		String appsecret = params.get("appsecret").toString();
		String token = params.get("token").toString();
		sql= "select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgid2;
		String tradecontactid = this.getDao().strSelect(sql);
		if(itemids.indexOf(",")>0){
			StringBuilder sqlwhere = new StringBuilder("select * from ecs_stockconfig with(nolock) where orgid=")
				.append(orgid2).append(" and itemId in(");
			String[] items = itemids.split(",");
			Map<String, String> productparams = new HashMap<String, String>();
	        //系统级参数设置
			productparams.put("app_key", appkey);
			productparams.put("access_token", token);
			productparams.put("method", "youdian.item.getItemInfo");
	        JSONObject responseproduct;
	        int totalCount;
	        JSONArray productlist;
			for(String e:items){
				sqlwhere.append("'").append(e).append("',");
				productparams.put("itemId", e);
			        
			    String responseProductData = com.wofu.ecommerce.mgj.utils.Utils.sendByPost(productparams, appsecret, url);
			    Log.info("取商品返回数据:　"+responseProductData);
			    responseproduct=new JSONObject(responseProductData);
			    if(responseproduct.getJSONObject("status").getInt("code")==10001){
			    	JSONObject obj= responseproduct.getJSONObject("result").getJSONObject("data");
			    	long productId=obj.optLong("item_id");
					String productCode=obj.optString("item_code");
					String productCname=obj.optString("item_name");
					int itemstock = obj.getInt("item_stock");
					Log.info("货号:"+productCode+",产品名称:"+productCname);
					
					StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,itemstock) ;
					JSONArray childseriallist = obj.getJSONArray("item_skus");
					for(int m=0;m<childseriallist.length();m++)
					{
						JSONObject childserial=childseriallist.optJSONObject(m);
						String sku=childserial.optString("sku_code");
						long skuid=childserial.optLong("sku_id");
						
						int quantity=childserial.optInt("sku_stock");
						
						StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid),sku,quantity) ;
					}
					
			    }	
			}
			this.setCurrpage(1);
			this.setPagesize(20);
			sqlwhere.delete(sqlwhere.length()-1,sqlwhere.length()).append(")");
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sqlwhere.toString(),this.searchOrderFieldName,this.orderMode)));
		}else {
			sql = "select * from ecs_stockconfig with(nolock) where orgid="+orgid2+ "and itemId='"+itemids+"'";
			Map<String, String> productparams = new HashMap<String, String>();
	        //系统级参数设置
			productparams.put("app_key", appkey);
			productparams.put("access_token", token);
			productparams.put("method", "youdian.item.getItemInfo");
	        productparams.put("itemId", itemids);
	        
	        String responseProductData = Utils.sendByPost(productparams, appsecret, url);
	       // Log.info("取商品返回数据:　"+responseProductData);
			JSONObject responseproduct=new JSONObject(responseProductData);
			
			if(responseproduct.getJSONObject("status").getInt("code")==10001){
		    	JSONObject obj= responseproduct.getJSONObject("result").getJSONObject("data");
		    	long productId=obj.optLong("item_id");
				String productCode=obj.optString("item_code");
				String productCname=obj.optString("item_name");
				int itemstock = obj.getInt("item_stock");
				Log.info("货号:"+productCode+",产品名称:"+productCname);
				
				StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,itemstock) ;
				JSONArray childseriallist = obj.getJSONArray("item_skus");
				for(int m=0;m<childseriallist.length();m++)
				{
					JSONObject childserial=childseriallist.optJSONObject(m);
					String sku=childserial.optString("sku_code");
					long skuid=childserial.optLong("sku_id");
					
					int quantity=childserial.optInt("sku_stock");
					
					StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid),sku,quantity) ;
				}
				
		    }
			this.setCurrpage(1);
			this.setPagesize(20);
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
		}
		
	}

	private void getYhdItem(String itemids, String orgId) throws Exception{
		String sql = "select url,appkey,appsecret, token from ecs_org_params with(nolock) where orgid="+orgId;
		Hashtable params = this.getDao().oneRowSelect(sql);
		String url = params.get("url").toString();
		String appkey = params.get("appkey").toString();
		String appsecret = params.get("appsecret").toString();
		String token = params.get("token").toString();
		sql= "select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgId;
		String tradecontactid = this.getDao().strSelect(sql);
		if(itemids.indexOf(",")>0){
			StringBuilder sqlwhere = new StringBuilder("select * from ecs_stockconfig with(nolock) where orgid=")
				.append(orgId).append(" and itemCode in(");
			String[] items = itemids.split(",");
			Map<String, String> productparams = new HashMap<String, String>();
	        //系统级参数设置
			productparams.put("appKey", appkey);
			productparams.put("sessionKey", token);
			productparams.put("format", "json");
			productparams.put("method", "yhd.serial.products.search");
			productparams.put("ver", "1.0");
			productparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
	        productparams.put("method", "yhd.serial.products.search");
	       
	        productparams.put("canShow", "1");
	        productparams.put("canSale", "1");
	        productparams.put("curPage", String.valueOf(1));
	        productparams.put("pageRows", "20");
	        productparams.put("verifyFlg", "2");
	        JSONObject responseproduct;
	        int totalCount;
	        JSONArray productlist;
			for(String e:items){
				sqlwhere.append("'").append(e).append("',");
				productparams.put("productCodeList", e);
			        
			    String responseProductData = Utils.sendByPost(productparams, appsecret, url);
			        //Log.info("取商品返回数据:　"+responseProductData);
			    responseproduct=new JSONObject(responseProductData);
					
				totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					
				productlist=responseproduct.getJSONObject("response").getJSONObject("serialProductList").getJSONArray("serialProduct");
					
					long productId;
					String productCode;
					String productCname;
					Map<String, String> stockparams = new HashMap<String, String>();
			        //系统级参数设置
					stockparams.put("appKey", appkey);
					stockparams.put("sessionKey", token);
					stockparams.put("format", "json");
					stockparams.put("method", "yhd.serial.product.get");
					stockparams.put("ver", "1.0");
					stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					JSONObject product;
					for(int i=0;i<productlist.length();i++)
					{
						product=productlist.getJSONObject(i);
					
						productId=product.optLong("productId");
						productCode=product.optString("productCode");
						productCname=product.optString("productCname");
						
						Log.info("货号:"+productCode+",产品名称:"+productCname);
						
						StockManager.stockConfig(this.getDao(), Integer.parseInt(orgId),Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						
						stockparams.put("productId", String.valueOf(productId));
			        
						String responseData = Utils.sendByPost(stockparams, appsecret, url);
						//Log.info("取商品详细返回数据:　"+responseData);
						
						JSONObject responsestock=new JSONObject(responseData);
						
						JSONArray childseriallist=responsestock.getJSONObject("response").getJSONObject("serialChildProdList").getJSONArray("serialChildProd");
						JSONObject childserial;
						JSONArray stocklist;
						String sku;
						long skuid;
						
						for(int m=0;m<childseriallist.length();m++)
						{
							childserial=childseriallist.optJSONObject(m);
							
							sku=childserial.optString("outerId");
							skuid=childserial.optLong("productId");
							
							stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							
							JSONObject stock;
							int quantity;
							long warehouseId;
							for (int j=0;j<stocklist.length();j++)
							{
								stock=stocklist.optJSONObject(j);
								
								quantity=stock.optInt("vs");
								warehouseId=stock.optLong("warehouseId");
								
								StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgId),String.valueOf(productId),String.valueOf(skuid)+"-"+String.valueOf(warehouseId),sku,quantity) ;
								
							}
						}
					}
			}
			this.setCurrpage(1);
			this.setPagesize(20);
			sqlwhere.delete(sqlwhere.length()-1,sqlwhere.length()).append(")");
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sqlwhere.toString(),this.searchOrderFieldName,this.orderMode)));
		}else {
			sql = "select * from ecs_stockconfig with(nolock) where orgid="+orgId+ "and itemCode='"+itemids+"'";
			Map<String, String> productparams = new HashMap<String, String>();
	        //系统级参数设置
			productparams.put("appKey", appkey);
			productparams.put("sessionKey", token);
			productparams.put("format", "json");
			productparams.put("method", "yhd.serial.products.search");
			productparams.put("ver", "1.0");
			productparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
	        productparams.put("method", "yhd.serial.products.search");
	       
	        productparams.put("canShow", "1");
	        productparams.put("canSale", "1");
	        productparams.put("curPage", String.valueOf(1));
	        productparams.put("pageRows", "20");
	        productparams.put("verifyFlg", "2");
	        productparams.put("productCodeList", itemids);
	        
	        String responseProductData = Utils.sendByPost(productparams, appsecret, url);
	       // Log.info("取商品返回数据:　"+responseProductData);
			JSONObject responseproduct=new JSONObject(responseProductData);
			
			int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
			
			JSONArray productlist=responseproduct.getJSONObject("response").getJSONObject("serialProductList").getJSONArray("serialProduct");
			
			
			
			for(int i=0;i<productlist.length();i++)
			{
				JSONObject product=productlist.getJSONObject(i);
			
				long productId=product.optLong("productId");
				String productCode=product.optString("productCode");
				String productCname=product.optString("productCname");
				
				Log.info("货号:"+productCode+",产品名称:"+productCname);
				
				StockManager.stockConfig(this.getDao(), Integer.parseInt(orgId),Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
				
				
				Map<String, String> stockparams = new HashMap<String, String>();
		        //系统级参数设置
				stockparams.put("appKey", appkey);
				stockparams.put("sessionKey", token);
				stockparams.put("format", "json");
				stockparams.put("method", "yhd.serial.product.get");
				stockparams.put("ver", "1.0");
				stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				stockparams.put("productId", String.valueOf(productId));
	        
				String responseData = Utils.sendByPost(stockparams, appsecret, url);
				// Log.info("取商品详细返回数据:　"+responseData);
				
				JSONObject responsestock=new JSONObject(responseData);
				
				JSONArray childseriallist=responsestock.getJSONObject("response").getJSONObject("serialChildProdList").getJSONArray("serialChildProd");
				
				for(int m=0;m<childseriallist.length();m++)
				{
					JSONObject childserial=childseriallist.optJSONObject(m);
					
					String sku=childserial.optString("outerId");
					long skuid=childserial.optLong("productId");
					
					
					JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
					
					for (int j=0;j<stocklist.length();j++)
					{
						JSONObject stock=stocklist.optJSONObject(j);
						
						int quantity=stock.optInt("vs");
						long warehouseId=stock.optLong("warehouseId");
						
						StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgId),String.valueOf(productId),String.valueOf(skuid)+"-"+String.valueOf(warehouseId),sku,quantity) ;
						
					}
				}
			}
			this.setCurrpage(1);
			this.setPagesize(20);
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
		}
		
		
	}

	private void getSuNingItem(String itemids, String orgid2) throws Exception{
		String sql = "select url,appkey,appsecret from ecs_org_params with(nolock) where orgid="+orgid2;
		Hashtable params = this.getDao().oneRowSelect(sql);
		String url = params.get("url").toString();
		String appkey = params.get("appkey").toString();
		String appsecret = params.get("appsecret").toString();
		sql= "select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgid2;
		String tradecontactid = this.getDao().strSelect(sql);
		if(itemids.indexOf(",")>0){
			StringBuilder sqlwhere = new StringBuilder("select * from ecs_stockconfig with(nolock) where orgid=")
			.append(orgid2).append(" and itemid in(");
			String[] items = itemids.split(",");
			String apiMethod="suning.custom.item.get";
		    String ReqParams ="";
		    HashMap<String,String> reqMap = new HashMap<String,String>();
		    HashMap<String,Object> map = new HashMap<String,Object>();
		    map.put("appSecret", appsecret);
		    map.put("appMethod", apiMethod);
		    map.put("format", "json");
		    map.put("versionNo", "1.2");
		    map.put("appRequestTime", com.wofu.ecommerce.suning.util.CommHelper.getNowTime());
		    map.put("appKey", appkey);
		    for(String e:items){
		    	sqlwhere.append("'").append(e).append("',");
				reqMap.put("productCode", e);  //只取在售商品
				ReqParams  = com.wofu.ecommerce.suning.util.CommHelper.getJsonStr(reqMap, "item");
			    map.put("resparams", ReqParams);
			    String responseText = com.wofu.ecommerce.suning.util.CommHelper.doRequest(map,url);
				//Log.info("返回数据: "+responseText);
				//把返回的数据转成json对象
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				if(responseText.indexOf("sn_error")!=-1){   //发生错误
					String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
					if("biz.handler.data-get:no-result".equals(operCode)){ //没有结果
					Log.info("没有可用的商品！");
					throw new Exception("找不到特定的商品,id: "+e);
				}else{
					if(!"".equals(operCode))
					{
						Log.error("苏宁获取商品作业", "获取商品作业失败,operCode:"+operCode);
						throw new Exception("下载苏宁特定商品出错了,错误码: "+operCode);
					}
					return;
					}
				}
				
				//商品集合
				JSONObject itemInfo= responseObj.getJSONObject("sn_body").getJSONObject("item");
						//苏宁商品编号
						String itemID = itemInfo.getString("productCode");
						//商品标题 
						String itemName = itemInfo.getString("productName");
						//货号
						String outerItemID =itemInfo.getString("itemCode");
						if("".equals(itemID)){  //商品编码为空，跳过
							break;
						}
						//商品库存
						String stockCount="";
						if(itemInfo.toString().indexOf("childItem") == -1){  //没有子商品的情况
							stockCount=com.wofu.ecommerce.suning.StockUtils.getInventoryByproductCode(itemID,appkey,appsecret,"json",url);
							StockManager.stockConfig(this.getDao(), Integer.parseInt(orgid2),Integer.valueOf(tradecontactid),itemID,outerItemID,itemName,Integer.valueOf(stockCount).intValue()) ;
						}else{     //有子商品情况下写stockconfigsku表
							JSONArray chileItem = itemInfo.getJSONArray("childItem");
							int totalCount=0;
								for(int j = 0 ; j < chileItem.length() ; j++)
								{	
									try{
										JSONObject item = chileItem.getJSONObject(j) ;
										//sku
										String sku = item.getString("itemCode");
										//外部sku
										String subItemID = item.getString("productCode");
										if("".equals(subItemID)){  //商品编码为空，跳过
											Log.info("子商品编码为空,主商品编码为:　"+itemID);
											break;
										}
										Log.info("产品编号: "+subItemID);
										//库存   String produceCode,String app_key,String app_Secret,String format,String url
										stockCount=com.wofu.ecommerce.suning.StockUtils.getInventoryByproductCode(subItemID,appkey,appsecret,"json",url);
										totalCount+=Integer.parseInt(stockCount);
										Log.info("获取到新的SKU: "+sku);
										StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgid2),itemID,subItemID,sku,Integer.valueOf(stockCount).intValue()) ;
									}catch(Exception ex){
										Log.warn("苏宁取商品写入sku信息出错,错误信息: "+ex.getMessage());
										continue;
									}
									
								}
								StockManager.stockConfig(this.getDao(), Integer.parseInt(orgid2),Integer.valueOf(tradecontactid),itemID,outerItemID,itemName,totalCount) ;
							}
			}
		    this.setCurrpage(1);
			this.setPagesize(20);
			sqlwhere.delete(sqlwhere.length()-1,sqlwhere.length()).append(")");
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sqlwhere.toString(),this.searchOrderFieldName,this.orderMode)));
		}else{
			sql = "select * from ecs_stockconfig with(nolock) where orgid="+orgid2 +" and itemid='"+itemids+"'";
			String apiMethod="suning.custom.item.get";
		    String ReqParams ="";
		    HashMap<String,String> reqMap = new HashMap<String,String>();
		    HashMap<String,Object> map = new HashMap<String,Object>();
		    map.put("appSecret", appsecret);
		    map.put("appMethod", apiMethod);
		    map.put("format", "json");
		    map.put("versionNo", "1.2");
		    map.put("appRequestTime", com.wofu.ecommerce.suning.util.CommHelper.getNowTime());
		    map.put("appKey", appkey);
				reqMap.put("productCode", itemids);  //只取在售商品
				ReqParams  = com.wofu.ecommerce.suning.util.CommHelper.getJsonStr(reqMap, "item");
			    map.put("resparams", ReqParams);
			    String responseText = com.wofu.ecommerce.suning.util.CommHelper.doRequest(map,url);
				//把返回的数据转成json对象
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				if(responseText.indexOf("sn_error")!=-1){   //发生错误
					String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
					if("biz.handler.data-get:no-result".equals(operCode)){ //没有结果
					Log.info("没有可用的商品！");
					throw new Exception("找不到特定的商品,id: "+itemids);
				}else{
					if(!"".equals(operCode))
					{
						Log.error("苏宁获取商品作业", "获取商品作业失败,operCode:"+operCode);
						throw new Exception("下载苏宁特定商品出错了,错误码: "+operCode);
					}
					return;
					}
				}
				
				//商品集合
				JSONObject itemInfo= responseObj.getJSONObject("sn_body").getJSONObject("item");
						//苏宁商品编号
						String itemID = itemInfo.getString("productCode");
						//商品标题 
						String itemName = itemInfo.getString("productName");
						//货号
						String outerItemID =itemInfo.getString("itemCode");
						if("".equals(itemID)){  //商品编码为空，跳过
							return;
						}
						//商品库存
						String stockCount="";
						if(itemInfo.toString().indexOf("childItem") == -1){  //没有子商品的情况
							stockCount=com.wofu.ecommerce.suning.StockUtils.getInventoryByproductCode(itemID,appkey,appsecret,"json",url);
							StockManager.stockConfig(this.getDao(), Integer.parseInt(orgid2),Integer.valueOf(tradecontactid),itemID,outerItemID,itemName,Integer.valueOf(stockCount).intValue()) ;
						}else{     //有子商品情况下写stockconfigsku表
							JSONArray chileItem = itemInfo.getJSONArray("childItem");
							int totalCount=0;
								for(int j = 0 ; j < chileItem.length() ; j++)
								{	
									try{
										JSONObject item = chileItem.getJSONObject(j) ;
										//sku
										String sku = item.getString("itemCode");
										//外部sku
										String subItemID = item.getString("productCode");
										if("".equals(subItemID)){  //商品编码为空，跳过
											Log.info("子商品编码为空,主商品编码为:　"+itemID);
											break;
										}
										Log.info("产品编号: "+subItemID);
										stockCount=com.wofu.ecommerce.suning.StockUtils.getInventoryByproductCode(subItemID,appkey,appsecret,"json",url);
										totalCount+=Integer.parseInt(stockCount);
										Log.info("获取到新的SKU: "+sku);
										StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgid2),itemID,subItemID,sku,Integer.valueOf(stockCount).intValue()) ;
									}catch(Exception ex){
										Log.warn("苏宁取商品写入sku信息出错,错误信息: "+ex.getMessage());
										continue;
									}
									
								}
								StockManager.stockConfig(this.getDao(), Integer.parseInt(orgid2),Integer.valueOf(tradecontactid),itemID,outerItemID,itemName,totalCount) ;
							}
						this.setCurrpage(1);
						this.setPagesize(20);
						this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
		}
		
	}

	private void getValItem(String itemids, String orgid2) throws Exception{
		String sql = "select uname,pwd,url,swssupplierid,pageSize,decryptkey,decryptRandomCode,webserviceurl from ecs_org_params with(nolock) where orgid="+orgid2;
		Hashtable params = this.getDao().oneRowSelect(sql);
		String supplierid = params.get("uname").toString();
		String suppliersign = params.get("pwd").toString();
		String uri = params.get("url").toString();
		String swssupplierid = params.get("swssupplierid").toString();
		String pageSize = params.get("pageSize").toString();
		String strkey = params.get("decryptkey").toString();
		String striv = params.get("decryptRandomCode").toString();
		String wsurl = params.get("webserviceurl").toString();
		sql= "select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgid2;
		String tradecontactid = this.getDao().strSelect(sql);
		if(itemids.indexOf(",")>0){
			StringBuilder sqlwhere = new StringBuilder("select * from ecs_stockconfig with(nolock) where orgid=")
			.append(orgid2).append(" and itemid in(");
			Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
			SoapHeader soapHeader = new SoapHeader() ;
			
			soapHeader.setUname(supplierid) ;
			soapHeader.setPassword(suppliersign) ;
			soapHeader.setUri(uri) ;
			//取总页面，再从后面开始取起
			bodyParams.put("page", String.valueOf(1));
			bodyParams.put("swsSupplierID", swssupplierid) ;
			bodyParams.put("pageSize", pageSize) ;
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("GetProductInfoByBarcode") ;
			soapBody.setUri(uri) ;
			SoapServiceClient client = new SoapServiceClient() ;
			client.setUrl(wsurl + "/GetProductInfoService.asmx") ;
			client.setSoapheader(soapHeader) ;
			String[] barcode = itemids.split(",");
			String itemIdTemp="";
			for(String e:barcode){
				
				bodyParams.put("barCode", DesUtil.DesEncode(e, strkey, striv)) ;
				soapBody.setBodyParams(bodyParams) ;
				client.setSoapbody(soapBody) ;
				String result = client.request() ;
				//Log.info("商品信息: "+ result);
				Document resultdoc=DOMHelper.newDocument(result);
			    Element resultelement=resultdoc.getDocumentElement();
			    String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
			    if("-1234699".indexOf(resultcode) >= 0 || "57".indexOf(resultcode) >= 0 ||"7".indexOf(resultcode) >= 0)
			    {
				   throw new Exception("获取商品信息失败，错误代码："+ resultcode+"，错误信息："+DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim()) ;
				   
				}
			    Element product=(Element) resultelement.getElementsByTagName("product").item(0);
					   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "barcode"), strkey, striv) ;
					   String skuid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "sku"), strkey, striv) ;
					   String itemid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "productcode"), strkey, striv) ;
					   String itemcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "developid"), strkey, striv) ;
					   String productname = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "productname"), strkey, striv) ;
					   String qty =DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "fororder"), strkey, striv) ;
					   if(!itemid.equals(itemIdTemp)){
						  itemIdTemp= itemid;
						  sqlwhere.append("'").append(itemIdTemp).append("',");
					   }
					   
					   if (qty==null || qty.equals("")) qty="0";
					   
					   Log.info("货号："+itemcode+", SKU "+sku);

					   StockManager.stockConfig(this.getDao(), Integer.valueOf(orgid2),Integer.valueOf(tradecontactid),itemid,itemcode,productname,0);						
												
					   StockManager.addStockConfigSku(this.getDao(), Integer.valueOf(orgid2),itemid,skuid,sku,Integer.valueOf(qty));
			}
			this.setCurrpage(1);
			this.setPagesize(20);
			sqlwhere.delete(sqlwhere.length()-1,sqlwhere.length()).append(")");
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sqlwhere.toString(),this.searchOrderFieldName,this.orderMode)));
			
		}else{
			Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
			SoapHeader soapHeader = new SoapHeader() ;
			
			soapHeader.setUname(supplierid) ;
			soapHeader.setPassword(suppliersign) ;
			soapHeader.setUri(uri) ;
			//取总页面，再从后面开始取起
			bodyParams.put("page", String.valueOf(1));
			bodyParams.put("swsSupplierID", swssupplierid);
			bodyParams.put("pageSize", pageSize) ;
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("GetProductInfoByBarcode") ;
			soapBody.setUri(uri) ;
			SoapServiceClient client = new SoapServiceClient() ;
			client.setUrl(wsurl + "/GetProductInfoService.asmx") ;
			client.setSoapheader(soapHeader) ;
				Log.info("barcode: "+itemids);
				bodyParams.put("barCode", DesUtil.DesEncode(itemids, strkey, striv)) ;
				soapBody.setBodyParams(bodyParams) ;
				client.setSoapbody(soapBody) ;
				String result = client.request() ;
				//Log.info("商品信息: "+ result);
				Document resultdoc=DOMHelper.newDocument(result);
			    Element resultelement=resultdoc.getDocumentElement();
			    String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
			    if("-1234699".indexOf(resultcode) >= 0 || "57".indexOf(resultcode) >= 0 ||"7".indexOf(resultcode) >= 0)
			    {
			    	 throw new Exception("获取商品信息失败，错误代码："+ resultcode+"，错误信息："+DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim()) ;
				}
			    Element product=(Element) resultelement.getElementsByTagName("product").item(0);
					   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "barcode"), strkey, striv) ;
					   String skuid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "sku"), strkey, striv) ;
					   String itemid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "productcode"), strkey, striv) ;
					   String itemcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "developid"), strkey, striv) ;
					   String productname = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "productname"), strkey, striv) ;
					   String qty =DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "fororder"), strkey, striv) ;
					   sql = "select * from ecs_stockconfig with(nolock) where orgid="+orgid2 +" and itemid='"+itemid+"'";
					   if (qty==null || qty.equals("")) qty="0";
					   
					   Log.info("货号："+itemcode+", SKU "+sku);

					   StockManager.stockConfig(this.getDao(), Integer.valueOf(orgid2),Integer.valueOf(tradecontactid),itemid,itemcode,productname,0);						
												
					   StockManager.addStockConfigSku(this.getDao(), Integer.valueOf(orgid2),itemid,skuid,sku,Integer.valueOf(qty));
					   this.setCurrpage(1);
					   this.setPagesize(20);
					   this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
			
		}
		
	}

	private void getTaobaoFenxiaoItem(String itemids, String orgid2) throws Exception{
		String sql = "select url,appkey,appsecret,token from ecs_org_params with(nolock) where orgid="+orgid2;
		Hashtable params = this.getDao().oneRowSelect(sql);
		String url = params.get("url").toString();
		String appkey = params.get("appkey").toString();
		String appsecret = params.get("appsecret").toString();
		String authcode = params.get("token").toString();
		sql= "select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgid2;
		String tradecontactid = this.getDao().strSelect(sql);
		FenxiaoProductsGetResponse response=null;
		if(itemids.indexOf(",")>0){
			StringBuilder sqlwhere = new StringBuilder("select * from ecs_stockconfig with(nolock) where orgid=")
				.append(orgid2).append(" and itemid in(");
			String[] items = itemids.split(",");
			TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
			FenxiaoProductsGetRequest req=new FenxiaoProductsGetRequest();
			req.setFields("skus");
			for(String e:items){
				sqlwhere.append("'").append(e).append("',");
				req.setPids(e);
				response = client.execute(req , authcode);
				if (response.getProducts()==null || response.getProducts().size()<=0)
				{		
					throw new Exception("特定的商品ID不存在,id:"+e);
				}
				
				for(Iterator it=response.getProducts().iterator();it.hasNext();)
				{
					FenxiaoProduct product=(FenxiaoProduct) it.next();

					StockManager.stockConfig(this.getDao(), Integer.valueOf(orgid2),Integer.valueOf(tradecontactid),String.valueOf(product.getPid()),
							product.getOuterId(),product.getName(),product.getQuantity().intValue()) ;
					if (product.getSkus()!=null)						
					{
						for(Iterator itsku=product.getSkus().iterator();itsku.hasNext();)
						{
							FenxiaoSku sku=(FenxiaoSku) itsku.next();
														
							Log.info("SKU "+sku.getOuterId()+" "+Formatter.format(product.getModified(),Formatter.DATE_TIME_FORMAT));

							StockManager.addStockConfigSku(this.getDao(), Integer.valueOf(orgid2),String.valueOf(product.getPid()),
									String.valueOf(sku.getId()),sku.getOuterId(),sku.getQuantity().intValue()) ;
						}
					}
				}
			}
			this.setCurrpage(1);
			this.setPagesize(20);
			sqlwhere.delete(sqlwhere.length()-1,sqlwhere.length()).append(")");
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sqlwhere.toString(),this.searchOrderFieldName,this.orderMode)));
		}else{
			sql = "select * from ecs_stockconfig with(nolock) where orgid="+orgid2 +" and itemid='"+itemids+"'";
			TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
			FenxiaoProductsGetRequest req=new FenxiaoProductsGetRequest();
			req.setFields("skus");
			req.setPids(itemids);
			response = client.execute(req , authcode);
				if (response.getProducts()==null || response.getProducts().size()<=0)
				{		
					throw new Exception("特定的商品ID不存在,id:"+itemids);
				}
				
				for(Iterator it=response.getProducts().iterator();it.hasNext();)
				{
					FenxiaoProduct product=(FenxiaoProduct) it.next();

					StockManager.stockConfig(this.getDao(), Integer.valueOf(orgid2),Integer.valueOf(tradecontactid),String.valueOf(product.getPid()),
							product.getOuterId(),product.getName(),product.getQuantity().intValue()) ;
					if (product.getSkus()!=null)						
					{
						for(Iterator itsku=product.getSkus().iterator();itsku.hasNext();)
						{
							FenxiaoSku sku=(FenxiaoSku) itsku.next();
														
							Log.info("SKU "+sku.getOuterId()+" "+Formatter.format(product.getModified(),Formatter.DATE_TIME_FORMAT));

							StockManager.addStockConfigSku(this.getDao(), Integer.valueOf(orgid2),String.valueOf(product.getPid()),
									String.valueOf(sku.getId()),sku.getOuterId(),sku.getQuantity().intValue()) ;
						}
					}
				}
				this.setCurrpage(1);
				this.setPagesize(20);
				this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
		}
		
	}

	private void getJingDongItem(String items,String orgid) throws Exception{
		try{
			String sql = "select url,appkey,appsecret,token from ecs_org_params with(nolock) where orgid="+orgid;
			Hashtable params = this.getDao().oneRowSelect(sql);
			String SERVER_URL = params.get("url").toString();
			String appKey = params.get("appkey").toString();
			String appSecret = params.get("appsecret").toString();
			String token = params.get("token").toString();
			sql= "select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgid;
			String tradecontactid = this.getDao().strSelect(sql);
			
			//
			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);

			WareGetRequest  wareGetRequest= new WareGetRequest();
			if(items.indexOf(",")>0){
				String[] wareIds = items.split(",");
				StringBuilder sqlwhere = new StringBuilder("select * from ecs_stockconfig with(nolock) where  orgid="+orgid+" and itemid in(");
				for(String e:wareIds){
							sqlwhere.append("'").append(e).append("',");
							wareGetRequest.setWareId(e);

							wareGetRequest.setFields("");

							WareGetResponse response= client.execute(wareGetRequest);
					
						if(!response.getCode().equals("0")){   //获取商品出错了
							throw new Exception("特定的商品ID不存在,id:"+e+"错误信息: "+response.getMsg());
						}
						Ware result = response.getWare() ;
						StockManager.stockConfig(this.getDao(), Integer.parseInt(orgid),Integer.valueOf(tradecontactid),String.valueOf(result.getWareId()),
								result.getItemNum(),result.getTitle(),Long.valueOf(result.getStockNum()).intValue()) ;
						if (result.getSkus()!=null && result.getSkus().size()!=0) 						
						{
							for(Iterator it=result.getSkus().iterator();it.hasNext();)
							{
								try{
									Sku skuinfo=(Sku) it.next();						
									
									Log.info("SKU "+skuinfo.getOuterId());
									
									StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgid),String.valueOf(result.getWareId()),
											String.valueOf(skuinfo.getSkuId()),skuinfo.getOuterId(),Long.valueOf(skuinfo.getStockNum()).intValue()) ;
								}catch(Exception ex){
									if(this.getDao()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
									Log.error("获取商品出错", ex.getMessage());
								}
							}
						}
				}
				this.setCurrpage(1);
				this.setPagesize(20);
				sqlwhere.delete(sqlwhere.length()-1,sqlwhere.length()).append(")");
				this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sqlwhere.toString(),this.searchOrderFieldName,this.orderMode)));
				
			}else{
				sql = "select * from ecs_stockconfig with(nolock) where orgid="+orgid +" and itemid='"+items+"'";
				wareGetRequest.setWareId(items);
				wareGetRequest.setFields("");
				WareGetResponse response= client.execute(wareGetRequest);
			if(!response.getCode().equals("0")){   //获取商品出错了
				throw new Exception("特定的商品ID不存在,id:"+items+"错误信息: "+response.getMsg());
			}
			Ware result = response.getWare();
			StockManager.stockConfig(this.getDao(), Integer.parseInt(orgid),Integer.valueOf(tradecontactid),String.valueOf(result.getWareId()),
					result.getItemNum(),result.getTitle(),Long.valueOf(result.getStockNum()).intValue()) ;
			if (result.getSkus()!=null && result.getSkus().size()!=0) 						
			{
				for(Iterator it=result.getSkus().iterator();it.hasNext();)
				{
					try{
						Sku skuinfo=(Sku) it.next();						
						
						Log.info("SKU "+skuinfo.getOuterId());
						
						StockManager.addStockConfigSku(this.getDao(), Integer.parseInt(orgid),String.valueOf(result.getWareId()),
								String.valueOf(skuinfo.getSkuId()),skuinfo.getOuterId(),Long.valueOf(skuinfo.getStockNum()).intValue()) ;
					}catch(Exception ex){
						if(this.getDao()!=null && !this.getDao().getConnection().getAutoCommit()) this.getDao().rollback();
						Log.error("获取商品出错", ex.getMessage());
					}
				}
			}
		    this.setCurrpage(1);
			this.setPagesize(20);
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	private void getDangDangItem(String items,String orgId) throws Exception{
		String sql = "select url,appkey,appsecret,token from ecs_org_params with(nolock) where orgid="+orgId;
		Hashtable params = this.getDao().oneRowSelect(sql);
		String url = params.get("url").toString();
		String app_key = params.get("appkey").toString();
		String app_Secret = params.get("appsecret").toString();
		String session = params.get("token").toString();
		sql= "select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgId;
		String tradecontactid = this.getDao().strSelect(sql);
		
		if(items.indexOf(",")>0){
			StringBuilder sqlwhere = new StringBuilder("select * from ecs_stockconfig with(nolock) where  orgid="+orgId+" and itemid in(");
			String[] item = items.split(",");
			String methodName="dangdang.item.get";
			//生成验证码 --md5;加密
			Date temp = new Date();
			String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp)  ;
			Hashtable<String, String> param1 = new Hashtable<String, String>() ;
			param1.put("sign", sign) ;
			param1.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
			param1.put("app_key",app_key);
			param1.put("method",methodName);
			param1.put("format","xml");
			param1.put("session",session);
			param1.put("sign_method","md5");
			for(String e:item){
				sqlwhere.append("'").append(e).append("',");
				param1.put("it", e) ;
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
					Log.info("获取当当商品详细资料失败,商品ID:"+ items +",错误信息："+operCode+":"+operation) ;
					throw new Exception("获取当当商品详细资料失败,商品ID:"+ items +",错误信息："+operCode+":"+operation);
					
				}
				Element itemDetail = (Element)result.getElementsByTagName("ItemDetail").item(0);
				String itemCode = DOMHelper.getSubElementVauleByName(itemDetail, "model");
				String title = DOMHelper.getSubElementVauleByName(itemDetail, "itemName");
				String itemId = DOMHelper.getSubElementVauleByName(itemDetail, "itemID");
				String stockCount= DOMHelper.getSubElementVauleByName(itemDetail, "stockCount");
				int  qty= Integer.parseInt("".equals(stockCount)?"0":stockCount);
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
								
							Log.error("取当当商品出错", ex.getMessage());
						}
						
						
					}
				}
			}
			this.setCurrpage(1);
			this.setPagesize(20);
			sqlwhere.delete(sqlwhere.length()-1,sqlwhere.length()).append(")");
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sqlwhere.toString(),this.searchOrderFieldName,this.orderMode)));
		}else{
			sql = "select * from ecs_stockconfig with(nolock) where orgid="+orgId+" and itemid='"+items+"'";
			String methodName="dangdang.item.get";
			Date temp = new Date();
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
			param1.put("it", items) ;
			
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
				Log.info("获取当当商品详细资料失败,商品ID:"+ items +",错误信息："+operCode+":"+operation) ;
				throw new Exception("获取当当商品详细资料失败,商品ID:"+ items +",错误信息："+operCode+":"+operation);
				
			}
			Element itemDetail = (Element)result.getElementsByTagName("ItemDetail").item(0);
			String itemCode = DOMHelper.getSubElementVauleByName(itemDetail, "model");
			String title = DOMHelper.getSubElementVauleByName(itemDetail, "itemName");
			String itemId = DOMHelper.getSubElementVauleByName(itemDetail, "itemID");
			String stockCount = DOMHelper.getSubElementVauleByName(itemDetail, "stockCount");
			int qty= Integer.parseInt("".equals(stockCount)?"0":stockCount);
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
							
						Log.error("取当当商品出错", ex.getMessage());
					}
					
					
				}
			}
			this.setCurrpage(1);
			this.setPagesize(20);
			this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
		}
		
	}
	
	
	public int getSerialid() {
		return serialid;
	}

	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public int getOrgid() {
		return orgid;
	}

	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getItemcode() {
		return itemcode;
	}

	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}

	public int getAlarmqty() {
		return alarmqty;
	}

	public void setAlarmqty(int alarmqty) {
		this.alarmqty = alarmqty;
	}

	public int getAlarmstyle() {
		return alarmstyle;
	}

	public void setAlarmstyle(int alarmstyle) {
		this.alarmstyle = alarmstyle;
	}

	public int getIsneedsyn() {
		return isneedsyn;
	}

	public void setIsneedsyn(int isneedsyn) {
		this.isneedsyn = isneedsyn;
	}

	public double getAddstockqty() {
		return addstockqty;
	}

	public void setAddstockqty(double addstockqty) {
		this.addstockqty = addstockqty;
	}

	public int getStockcount() {
		return stockcount;
	}

	public void setStockcount(int stockcount) {
		this.stockcount = stockcount;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	

	

	public DataRelation getEcsstockconfigofecsstockconfigs() {
		return ecsstockconfigofecsstockconfigs;
	}
	public void setEcsstockconfigofecsstockconfigs(
			DataRelation ecsstockconfigofecsstockconfigs) {
		this.ecsstockconfigofecsstockconfigs = ecsstockconfigofecsstockconfigs;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getUpdator() {
		return updator;
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public int getErrflag() {
		return errflag;
	}

	public void setErrflag(int errflag) {
		this.errflag = errflag;
	}


}
