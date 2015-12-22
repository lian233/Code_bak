package com.wofu.ecommerce.stockmanager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.request.ware.WareSkuStockUpdateRequest;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.ItemQuantityUpdateRequest;
import com.taobao.api.response.ItemQuantityUpdateResponse;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.FileUtil;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.ecommerce.util.Util;
public class StockSynByFile extends PageBusinessObject{
	private int serialid;
	private String sku;     //同步sku
	private int stocknum;   //同步数量
	private String log;     //同步结果
	private Date syntime;   //同步时间
	private String synname; //此次同步的名称
	private String shoporgid;//同步店铺
	public StockSynByFile(){
		this.exportQuerySQL="select b.orgname as shoporgid,a.sku,a.stocknum,syntime,synname,log from {searchSQL}" +
				" a,ecs_org b where a.shoporgid=b.orgid";
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public int getStocknum() {
		return stocknum;
	}
	public void setStocknum(int stocknum) {
		this.stocknum = stocknum;
	}
	
	public int getSerialid() {
		return serialid;
	}
	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public Date getSyntime() {
		return syntime;
	}
	public void setSyntime(Date syntime) {
		this.syntime = syntime;
	}
	public String getSynname() {
		return synname;
	}
	public void setSynname(String synname) {
		this.synname = synname;
	}
	public String getShoporgid() {
		return shoporgid;
	}
	public void setShoporgid(String shoporgid) {
		this.shoporgid = shoporgid;
	}
	public void stockImport() throws Exception{
		Log.info("同步库存开始了...");
		String orgid =null;
		InputStream in =null;
		//String line =null;
		try{
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(4096);
			ServletFileUpload upLoad = new ServletFileUpload(factory);
			upLoad.setSizeMax(1000000);
			upLoad.setFileSizeMax(1000000);
			upLoad.setHeaderEncoding("UTF-8");
			this.getRequest().setCharacterEncoding("UTF-8");
			List items = upLoad.parseRequest(this.getRequest());
			for(Iterator it = items.iterator();it.hasNext();){
				FileItem item = (FileItem)it.next();
				if(item.isFormField()){   //普通form字段
					orgid= item.getString("utf-8");
				}else{    //文件字段
					String fileName = item.getName();
					Log.info("fileName: "+fileName);
					Log.info(FileUtil.getExtensionName(fileName));
					if(!"txt".equals(FileUtil.getExtensionName(fileName))){
						this.OutputStr("{\"failure\":\"true\",\"data\":\"文件格式不正确\"}");
						return;
					}
					in = item.getInputStream();
					String sql="select a.*,b.shortname,c.orgname from ecs_org_params a with(nolock),ecs_platform b with(nolock),ecs_org c with(nolock) "
						+"where a.platformid=b.platformid and a.orgid="+orgid+" and a.orgid=c.orgid";
					Hashtable htparams=this.getDao().oneRowSelect(sql);
					String platformname=htparams.get("shortname").toString();
					String orgname=htparams.get("orgname").toString();
					
					sql="select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgid;
					int tradecontactid=this.getDao().intSelect(sql);
					if (platformname.equals("taobao"))
					{
						updateTaobaoStock(orgid,tradecontactid,htparams,in,fileName);
					}
					else if (platformname.equals("360buy"))
					{
						updateJingdongStock(orgid,tradecontactid,htparams,in,fileName);
					}
					else if (platformname.equals("dangdang"))
					{
						updateDangdangStock(orgid,tradecontactid,htparams,in,fileName);
					}
					
				}
				
			}
		}catch(Exception e){
			this.OutputStr("{\"failure\":\"true\",\"data\":\""+e.getMessage()+"\"}");
		}
		
		
	}
	
	
	private void updateDangdangStock(String orgid, int tradecontactid,
			Hashtable htparams, InputStream in,String fileName) throws Exception{
			String url=htparams.get("url").toString();
			String encoding=htparams.get("encoding").toString();
			String session=htparams.get("token").toString();
			String app_key=htparams.get("appkey").toString();
			String app_Secret=htparams.get("appsecret").toString();
			Scanner sc  = new Scanner(in);
			JSONArray arr = new JSONArray();
			boolean isNeedSyn;
			ArrayList<String> sqlBatch = new ArrayList<String>();
			ArrayList<String> insertBatch = new ArrayList<String>();
			while(sc.hasNextLine()){
				isNeedSyn=true;
				String record = sc.nextLine().trim();
				if("".equals(record)) continue;
				String[] item = record.split(":");
				int qty=0;
				if("".equals(item[1])){  //没有数量就用可用库存的比例
					float synRate = Float.parseFloat(item[2]);
					qty = StockManager.getTradeContactUseableStock(this.getConnection(),tradecontactid,item[0]);
					qty = Math.round(qty * synRate);
				}else{
					if(Float.parseFloat(item[1])<1){//设置sku的同步比例
						isNeedSyn=false;
						String sql = "update ecs_stockconfigsku set synrate="+item[1]+" where orgid='"+ orgid+"' and  sku='"+item[0]+"'";
						sqlBatch.add(sql);
						HashMap map = new HashMap();
						map.put("sku", item[0]);
						map.put("stocknum", "");
						map.put("log", "同步比例:"+item[1]);
						arr.put(map);
						
					}else{
						qty = Math.round(Float.parseFloat(item[1]));
						}
						
					}
					if(sqlBatch.size()>=200){
						SQLHelper.executeBatch(this.getConnection(), sqlBatch);
						sqlBatch.clear();
					}
						
					if(isNeedSyn){
						if(qty<0) qty=0;
						String sql ="select 1 from ecs_stockconfigsku where orgid='"+ orgid+"' and  sku='"+item[0]+"'";
						String res = this.getDao().strSelect(sql);
						HashMap map = new HashMap();
						if(!"1".equals(res)){
							map.put("sku", item[0]);
							map.put("stocknum", String.valueOf(qty));
							map.put("log", "同步失败，此sku在erp或当当后台不存在，请核对后再同步");
							map.put("syntime", Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
							map.put("synname", fileName);
							map.put("shoporgid", orgid);
							arr.put(map);
						}else{
							String log = dangdangUtil(url,encoding,app_Secret,app_key,session,item[0] ,qty);
							map.put("sku", item[0]);
							map.put("stocknum", String.valueOf(qty));
							map.put("log", log);
							map.put("syntime", Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
							map.put("synname", fileName);
							map.put("shoporgid", orgid);
							arr.put(map);
						}
						sql = "insert into StockSynByFile(sku,stocknum,log,syntime,synname,shoporgid) values('"+
						map.get("sku")+"','"+map.get("stocknum")+"','"+map.get("log")+"','"+
						map.get("syntime")+"','"+map.get("synname")+"','"+map.get("shoporgid")+"')";
						insertBatch.add(sql);
					}
					if(insertBatch.size()>200){
						SQLHelper.executeBatch(this.getConnection(), insertBatch);
						insertBatch.clear();
					}
		}
		if(sqlBatch.size()>0)
			SQLHelper.executeBatch(this.getConnection(), sqlBatch);
		if(insertBatch.size()>0)
			SQLHelper.executeBatch(this.getConnection(), insertBatch);
		this.OutputStr("{\"success\":\"true\",\"data\":"+arr.toString()+"}");
		//
		
	}
	private void updateJingdongStock(String orgid, int tradecontactid,
			Hashtable htparams, InputStream in,String fileName) throws Exception{
			String SERVER_URL=htparams.get("url").toString();
			String appKey=htparams.get("appkey").toString();
			String appSecret=htparams.get("appsecret").toString();
			String token=htparams.get("token").toString();
			Scanner sc  = new Scanner(in);
			JSONArray arr = new JSONArray();
			boolean isNeedSyn;
			ArrayList<String> sqlBatch = new ArrayList<String>();
			ArrayList<String> insertBatch = new ArrayList<String>();
			while(sc.hasNextLine()){
				isNeedSyn=true;
				String record = sc.nextLine().trim();
				if("".equals(record)) continue;
				String[] item = record.split(":");
				int qty=0;
				if("".equals(item[1])){  //没有数量就用可用库存的比例
					float synRate = Float.parseFloat(item[2]);
					qty = StockManager.getTradeContactUseableStock(this.getConnection(),tradecontactid,item[0]);
					qty = Math.round(qty * synRate);
				}else{
					if(Float.parseFloat(item[1])<1){//设置sku的同步比例
						isNeedSyn=false;
						String sql = "update ecs_stockconfigsku set synrate="+item[1]+" where orgid='"+ orgid+"' and  sku='"+item[0]+"'";
						sqlBatch.add(sql);
						HashMap map = new HashMap();
						map.put("sku", item[0]);
						map.put("stocknum", "");
						map.put("log", "同步比例:"+item[1]);
						arr.put(map);
						
					}else{
						qty = Math.round(Float.parseFloat(item[1]));
						}
						
					}
					if(sqlBatch.size()>=200){
						SQLHelper.executeBatch(this.getConnection(), sqlBatch);
						sqlBatch.clear();
					}
						
					if(isNeedSyn){
						if(qty<0) qty=0;
						String sql ="select skuid,sku from ecs_stockconfigsku where orgid='"+ orgid+"' and  sku='"+item[0]+"'";
						Hashtable<String, String> tb = this.getDao().oneRowSelect(sql);
						HashMap map = new HashMap();
						if(tb.size()==0){
							map.put("sku", item[0]);
							map.put("stocknum", String.valueOf(qty));
							map.put("log", "同步失败，此sku在erp或京东后台不存在，请核对后再同步");
							map.put("syntime", Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
							map.put("synname", fileName);
							map.put("shoporgid", orgid);
							arr.put(map);
						}else{
							String log = jingDongUtil(SERVER_URL,token,appKey,appSecret,qty,(String)tb.get("sku") ,(String)tb.get("skuid"));
							map.put("sku", item[0]);
							map.put("stocknum", String.valueOf(qty));
							map.put("log", log);
							map.put("syntime", Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
							map.put("synname", fileName);
							map.put("shoporgid", orgid);
							arr.put(map);
						}
						sql = "insert into StockSynByFile(sku,stocknum,log,syntime,synname,shoporgid) values('"+
						map.get("sku")+"','"+map.get("stocknum")+"','"+map.get("log")+"','"+
						map.get("syntime")+"','"+map.get("synname")+"','"+map.get("shoporgid")+"')";
						insertBatch.add(sql);
					}
					if(insertBatch.size()>200){
						SQLHelper.executeBatch(this.getConnection(), insertBatch);
						insertBatch.clear();
					}
		}
		if(sqlBatch.size()>0)
			SQLHelper.executeBatch(this.getConnection(), sqlBatch);
		if(insertBatch.size()>0)
			SQLHelper.executeBatch(this.getConnection(), insertBatch);
		this.OutputStr("{\"success\":\"true\",\"data\":"+arr.toString()+"}");
			
		
	}
		
	private void updateTaobaoStock(String orgid,int tradecontactid,Map params,InputStream in,String fileName) throws Exception	{
		String url=params.get("url").toString();
		String appkey=params.get("appkey").toString();
		String appsecret=params.get("appsecret").toString();
		String authcode=params.get("token").toString();
		Scanner sc  = new Scanner(in);
		JSONArray arr = new JSONArray();
		boolean isNeedSyn;
		ArrayList<String> sqlBatch = new ArrayList<String>();
		ArrayList<String> insertBatch = new ArrayList<String>();
		try{
			while(sc.hasNextLine()){
				isNeedSyn=true;
				String record = sc.nextLine().trim();
				if("".equals(record)) continue;
				String[] item = record.split(":");
				int qty=0;
				if("".equals(item[1])){  //没有数量就用可用库存的比例
					float synRate = Float.parseFloat(item[2]);
					qty = StockManager.getTradeContactUseableStock(this.getConnection(),tradecontactid,item[0]);
					qty = Math.round(qty * synRate);
				}else{
					if(Float.parseFloat(item[1])<1  && Float.parseFloat(item[1])!=0){//设置sku的同步比例
						isNeedSyn=false;
						String sql = "update ecs_stockconfigsku set synrate="+item[1]+" where orgid='"+ orgid+"' and  sku='"+item[0]+"'";
						sqlBatch.add(sql);
						HashMap map = new HashMap();
						map.put("sku", item[0]);
						map.put("stocknum", "");
						map.put("log", "同步比例:"+item[1]);
						arr.put(map);
						
					}else{
						qty = Math.round(Float.parseFloat(item[1]));
						}
						
					}
					if(sqlBatch.size()>=200){
						SQLHelper.executeBatch(this.getConnection(), sqlBatch);
						sqlBatch.clear();
					}
						
					if(isNeedSyn){
						if(qty<0) qty=0;
						String sql ="select itemid,skuid,sku from ecs_stockconfigsku where orgid='"+ orgid+"' and  sku='"+item[0]+"'";
						Hashtable<String, String> tb = this.getDao().oneRowSelect(sql);
						HashMap map = new HashMap();
						if(tb.size()==0){
							map.put("sku", item[0]);
							map.put("stocknum", String.valueOf(qty));
							map.put("log", "同步失败，此sku在erp或淘宝后台不存在，请核对后再同步");
							map.put("syntime", Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
							map.put("synname", fileName);
							map.put("shoporgid", orgid);
							arr.put(map);
						}else{
							System.out.println("finish......1");
							String log = taobaoUtil(url,appkey,appsecret,authcode,(String)tb.get("itemid") ,(String)tb.get("skuid"),(String)tb.get("sku"),qty,1);
							map.put("sku", item[0]);
							map.put("stocknum", String.valueOf(qty));
							map.put("log", log);
							System.out.println("finish......2");
							map.put("syntime", Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
							map.put("synname", fileName);
							map.put("shoporgid", orgid);
							System.out.println("finish......3");
							arr.put(map);
						}
						sql = "insert into StockSynByFile(sku,stocknum,log,syntime,synname,shoporgid) values('"+
							map.get("sku")+"','"+map.get("stocknum")+"','"+map.get("log")+"','"+
							map.get("syntime")+"','"+map.get("synname")+"','"+map.get("shoporgid")+"')";
						insertBatch.add(sql);
					}
					if(insertBatch.size()>200){
						SQLHelper.executeBatch(this.getConnection(), insertBatch);
						insertBatch.clear();
					}
						
					
				}
				System.out.println("finish......333");
				if(sqlBatch.size()>0)
					SQLHelper.executeBatch(this.getConnection(), sqlBatch);
				if(insertBatch.size()>0)
					SQLHelper.executeBatch(this.getConnection(), insertBatch);
				System.out.println("finish......");
			this.OutputStr("{\"success\":\"true\",\"data\":"+arr.toString()+"}");
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}

	public static String taobaoUtil(String url,String appkey,
			String appsecret,String authcode,String itemid ,String skuid,String sku,
			int qty,int type) throws Exception 
		
	{
		TaobaoClient client=null;
		ItemQuantityUpdateRequest updatereq=null;
		try {			
			client=new DefaultTaobaoClient(url,appkey, appsecret);
			updatereq=new ItemQuantityUpdateRequest();
			updatereq.setNumIid(Long.valueOf(itemid));
			updatereq.setOuterId(sku);	
			updatereq.setSkuId(Long.valueOf(skuid));
			updatereq.setQuantity(Long.valueOf(qty));
			updatereq.setType(Long.valueOf(type));
			ItemQuantityUpdateResponse response = client.execute(updatereq,authcode);
			
			while (response!=null && !response.isSuccess())
			{	
				String errorMsg = response.getSubMsg();
				if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
					Log.info("更新频率过快，线程休眠5秒");
					Thread.sleep(5000L);
					response = client.execute(updatereq,authcode);
					continue;
					}else{
						return "更新淘宝库存失败,SKU【"+sku+"】,错误信息: "+response.getSubMsg();	
					}
					
			}
			if(response.isSuccess()){
				if (type==1)
					{
						Log.info("更新淘宝库存成功,SKU【"+sku+"】"+" 新库存:"+qty);
						return "更新淘宝库存成功,SKU【"+sku+"】"+" 新库存:"+qty;
						
					}
					
				}
			
		} catch (Exception e) {
			Log.info(e.getMessage());
			//网络连接失败，五秒重试
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("网络连接超时，五秒重试!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("更新淘宝库存失败,SKU【"+sku+"】,错误信息:"+e.getMessage());
					return "更新淘宝库存失败,SKU【"+sku+"】,错误信息:"+e.getMessage();
				}
			}

		}
		//每更新一个停顿一秒时间，以提高正确率
		long currentTime = System.currentTimeMillis();
		while(System.currentTimeMillis()-currentTime<=3000){
			Thread.sleep(1000L);
		}
		return "更新淘宝库存失败,SKU【"+sku+"】,错误信息:未知";
	}
	
	private static String jingDongUtil(String SERVER_URL,String token,String appKey,String appSecret,int qty,String sku,String skuId) throws Exception{
		try{
			DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			WareSkuStockUpdateRequest request = new WareSkuStockUpdateRequest();
			request.setOuterId(sku);
			request.setSkuId(skuId) ;
			request.setQuantity(String.valueOf(qty));
			request.setTradeNo(Util.getTradeNo()) ;
			com.jd.open.api.sdk.response.ware.WareSkuStockUpdateResponse res = client.execute(request);
			Log.info("--京东更新库存返回信息: "+res.getCode());
			if(res.getCode().equals("0"))
			{
				Log.info("更新京东库存成功,SKU【"+ sku +"】,库存:"+ qty);
				return "更新京东库存成功,SKU【"+ sku +"】,库存:"+ qty;
			
			}
			else
			{
				Log.info("更新京东库存失败!SKU【"+sku +"】,错误信息:"+res.getZhDesc());
				return "更新京东库存失败!SKU【"+ sku +"】,错误信息:"+res.getZhDesc();
			
			}
		} catch (Exception e) {
			String errorMessage=e.getMessage();
			Log.info("更新京东库存失败,SKU【"+sku+"】,错误信息:"+errorMessage);
			return "更新京东库存失败,SKU【"+sku+"】,错误信息:"+errorMessage;
		
		}
	}
	
	private static String dangdangUtil(String url,String encoding,String app_Secret,String app_key,String session,String sku,int stocks) throws Exception{
		String resultMsg =null;
		try
		{
			//方法名
			Date temp = new Date();
			String methodName="dangdang.item.stock.update";
			//生成验证码 --md5;加密
			String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
			params.put("app_key",app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",session);
			params.put("sign_method","md5");
			params.put("oit", sku) ;
			params.put("stk", String.valueOf(stocks)) ;
			
			String responseText = CommHelper.sendRequest(url, "GET",params,"") ;
			Document doc = DOMHelper.newDocument(responseText, encoding) ;
			Element result = doc.getDocumentElement() ;
			
			if(DOMHelper.ElementIsExists(result, "Error"))
			{
				Element errorInfo = (Element)result.getElementsByTagName("Error").item(0) ;
				String operCode = DOMHelper.getSubElementVauleByName(errorInfo, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(errorInfo, "operation") ;
				if(!"".equals(operCode))
				{
					resultMsg = "更新当当库存失败,SKU【"+ sku +"】,错误信息："+ operCode +"：" +operation;
					Log.info(resultMsg);
				}
			}
			
			Element resultInfo = (Element)result.getElementsByTagName("Result").item(0) ;
			String operCode = DOMHelper.getSubElementVauleByName(resultInfo, "operCode") ;
			String operation = DOMHelper.getSubElementVauleByName(resultInfo, "operation") ;
		
			if("0".equals(operCode))
			{
				resultMsg = "更新当当库存成功,SKU【"+ sku +"】,新库存:"+ stocks ;
			}
			else if("22".endsWith(operCode))
			{			
				resultMsg ="更新当当库存失败，找不到当当商品资料。SKU【"+ sku +"】,错误信息："+ operCode +"：" +operation;
			}
			else
				resultMsg = "更新当当库存失败,SKU【"+ sku +"】,错误信息："+ operCode +"：" +operation;
			
			
		} catch (Exception e) 
		{
			resultMsg = "更新当当商品库存失败，sku："+sku+"，库存："+stocks+"。错误信息："+e.getMessage() ;
		}
		return resultMsg;
	}
	
	private void toFile(String outputFile,String arr) throws Exception{
		BufferedReader br =null;
		BufferedWriter bw =null;
		try{
			br = new BufferedReader(new StringReader(arr));
			bw = new BufferedWriter(new FileWriter(outputFile));
			char[] temp = new char[1024*2];
			int len =0;
			while((len =br.read(temp))!=-1)
			bw.write(temp,0,len);
		}finally{
			if(br!=null)
				br.close();
			if(bw!=null)
				bw.close();
		}
		
	}
	
	
}
