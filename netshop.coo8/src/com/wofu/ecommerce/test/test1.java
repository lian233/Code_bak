package com.wofu.ecommerce.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Date;

import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.items.ItemQuantityUpdateRequest;
import com.coo8.api.request.items.ItemsGetOnsaleRequest;
import com.coo8.api.request.order.OrderGetRequest;
import com.coo8.api.request.order.OrdersGetRequest;
import com.coo8.api.request.proudct.ProductGetRequest;
import com.coo8.api.request.proudct.ProductsGetRequest;
import com.coo8.api.response.items.ItemQuantityUpdateResponse;
import com.coo8.api.response.items.ItemsGetOnsaleResponse;
import com.coo8.api.response.order.OrderGetResponse;
import com.coo8.api.response.order.OrdersGetResponse;
import com.coo8.api.response.product.ProducstGetResponse;
import com.coo8.api.response.product.ProductGetResponse;
import com.coo8.open.order.Order;
import com.coo8.open.product.GoodsPop;
import com.coo8.open.product.ProductPop;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.ecommerce.coo8.Params;

public class test1  {
	public static long daymillis=24*60*60*1000L;
	public static void main(String[] args)throws Exception {
		//获取多个订单
		OrdersGetResponse orders=getOrderList();
		System.out.println("total:"+orders.getTotalResult());
		System.out.println("msg:"+orders.getMsg());
		System.out.println("body:"+orders.getBody());
//		for(int i=0;i<orders.getOrders().size();i++){
//			Order order=orders.getOrders().get(i);
//			System.out.println(String.valueOf(order.getStatus()));
//		}
		System.out.println(Double.valueOf(Math.ceil((21)/20.0)).intValue());
		
		
		
		
//		//获取商品列表
//		ProducstGetResponse psger=getProductList();
//		System.out.println(psger.getBody());
//		System.out.println(psger.getTotalResult());
//		for(ProductPop pp:psger.getProductPop()){
//			
//			System.out.println("productid:"+pp.getProduct_no()+"\n"+"productname:"+pp.getProduct_name());
//			System.out.println("brandid:"+pp.getBrand()+"\n"+"productarea:"+pp.getPro_area_desc());
//			System.out.println("descUrl:"+pp.getGoods_desc()+"\n"+"updater:"+pp.getModify_user());
//			System.out.println("goods:"+pp.getGoodsList().get(0).getGoods_no()+" modify:"+pp.getModify_time());
//			System.out.println("ss:"+pp.getModify_user());
//		}
//		
//		ItemsGetOnsaleResponse itmep=getitemlist();
//		System.out.println(itmep.getBody());
//		System.out.println(itmep.getTotalResult());
//		for(int m=0;m<itmep.getProductPop().size();m++){
//			ProductPop pp=itmep.getProductPop().get(m);
//			String produntid=pp.getProduct_no();
//			System.out.println(produntid);
//			for(GoodsPop gp:pp.getGoodsList()){
//				System.out.println("sku"+gp.getSku());
//			};
//			
//		}
//		int total=itmep.getTotalResult();
//		//总页数
//		int pageTotal=Double.valueOf(Math.ceil(total/5)).intValue();
//		System.out.println(total+":"+pageTotal);
		
		
		
//		ItemQuantityUpdateResponse itemq=updateStock();
//		System.out.println(itemq.getBody());
//		JSONObject obj=new JSONObject(itemq.getBody());
//		System.out.println(obj.getJSONObject("item_quantity_update_response").getString("success"));
		
		//获取单个商品
//		ProductGetResponse re=getProductById("");
//		System.out.println(re.getGoods().size());
//		for(GoodsPop gp:re.getGoods()){
//			System.out.println("itemid:"+gp.getSku()+"  "+"outid:"+gp.getGoods_no()+"  goodname:"+gp.getGoods_name()+"  originalPrice:"+gp.getSell_price());
//			System.out.println("color:"+gp.getColor()+"  status:"+gp.getStatus()+"  updater:"+gp.getModify_user()+"  updatertime:"+gp.getModify_time());
//			System.out.println("quantity:"+gp.getShow_quantity()+"  pics:"+gp.getImagesList().get(0).getImg_id()+gp.getImagesList().get(0).getPath()+"  index:"+gp.getImagesList().get(0).getOrder_no());
//			System.out.println();
//		
//		}
//		
		//获取单个订单
//		OrderGetResponse rep=getOneOrder();
//		Order order=rep.getOrder();
//		System.out.println(order.getStatus());
//		System.out.println(order.getConsignee().getInvoiceTitle());
//		System.out.println(order.getConsignee().getInvoice());
//		System.out.println(order.getOrderChangeTime());
		
	}
	public static OrdersGetResponse getOrderList() throws Exception{
		Coo8Client coo8=new DefaultCoo8Client(Params.url,Params.appKey,Params.secretKey);
		OrdersGetRequest orderrequest=new OrdersGetRequest();
		orderrequest.setPageNo(1);
		orderrequest.setPageSize(10);
		//orderrequest.setStatus("RCP");
//		String lasttimeconfvalue=Params.username+"取订单最新时间";
//		String lasttime=PublicUtils.getConfig(getConnection(),lasttimeconfvalue,"2013-10-15 00:00:00");
		Date startdate=Formatter.parseDate("2013-11-17 00:00:00", Formatter.DATE_TIME_FORMAT);
		Date enddate=Formatter.parseDate("2013-11-18 00:00:00", Formatter.DATE_TIME_FORMAT);
		orderrequest.setStartDate(startdate);
		orderrequest.setEndDate(enddate);
		OrdersGetResponse response=coo8.execute(orderrequest);
		return response;
	}
	public static ProducstGetResponse getProductList()throws Exception{
		Coo8Client cc = new DefaultCoo8Client(Params.url, Params.appKey, Params.secretKey);
		ProductsGetRequest request=new ProductsGetRequest();
		request.setFields("productId,productName,items,catalogId,brandId,productarea,provinceName," +
				"munit,weight,descUrl,gift,phaseAdver,startPhaseTime,endPhaseTime,volume,updater,templateId," +
				"pros,brandName,description,item.outId,item.itemId,item.goodsName,item.originalPrice,item.color," +
				"item.status,item.updater,item.updateTime,item.version,item.brandId,item.catalogId,item.quantity," +
				"item.detail,pic.imgId,pic.imgUrl,item.pic.index");				//返回字段
		request.setPageNo(1);				//第几页
		request.setPageSize(1);				//每页多少个
		//request.setStartModified(new Date());
		
		//String lasttimeconfvalue=Params.username+"取订单最新时间";
		//String lasttime=PublicUtils.getConfig(getConnection(),lasttimeconfvalue,"2013-10-11 00:00:00");
		//Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		//Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
		//request.setStartModified(startdate);
		//request.setEndModified(enddate);
		ProducstGetResponse response=cc.execute(request);
		return response;
	}
	public static ProductGetResponse getProductById(String id)throws Exception{
		Coo8Client coo8=new DefaultCoo8Client(Params.url,Params.appKey,Params.secretKey);
		ProductGetRequest request=new ProductGetRequest();
		request.setProductId("A0003950233");
		ProductGetResponse response=coo8.execute(request);

		return response;
	}
	public static OrderGetResponse getOneOrder()throws Exception{
		Coo8Client cc = new DefaultCoo8Client(Params.url, Params.appKey, Params.secretKey);
		OrderGetRequest og = new OrderGetRequest();
		og.setOrderId("1596252776");
		OrderGetResponse or =cc.execute(og);
		return or;
	}
	public static ItemQuantityUpdateResponse updateStock()throws Exception{
		Coo8Client cc = new DefaultCoo8Client(Params.url, Params.appKey, Params.secretKey);
		ItemQuantityUpdateRequest request=new ItemQuantityUpdateRequest();
		request.setItemId("BB95001000");
		request.setProductId("A0003835305");
		request.setQuantity("10");
		ItemQuantityUpdateResponse response=cc.execute(request);
		return response;
	}
	
	
	
	
	
	public static Connection getConnection() throws Exception
	{

		String driver="com.microsoft.jdbc.sqlserver.SQLServerDriver";
		String url="jdbc:microsoft:sqlserver://172.20.11.116:1433;DatabaseName=ErpDKBMConnect";
		String user="sa";
		String password="sa";
		
		if (driver != null && !driver.equals("")) {
			DriverManager.registerDriver(
				(Driver) Class.forName(driver).newInstance());
		}
		if (user != null) {
			return DriverManager.getConnection(url, user, password);
		} else {
			return DriverManager.getConnection(url);
		}
			
	}
	public static ItemsGetOnsaleResponse getitemlist()throws Exception{
		Coo8Client cc = new DefaultCoo8Client(Params.url, Params.appKey, Params.secretKey);
		ItemsGetOnsaleRequest request=new ItemsGetOnsaleRequest();
		request.setFields("productId,productName,items,catalogId,brandId,productarea,provinceName," +
				"munit,weight,descUrl,gift,phaseAdver,startPhaseTime,endPhaseTime,volume,updater,templateId," +
				"pros,brandName,description,item.outId,item.itemId,item.goodsName,item.originalPrice,item.color," +
				"item.status,item.updater,item.updateTime,item.version,item.brandId,item.catalogId,item.quantity," +
				"item.detail,item.pic.imgId,item.pic.imgUrl,item.pic.index");
		request.setPageNo(1);
		//String lasttimeconfvalue=Params.username+"取订单最新时间";
		//String lasttime=PublicUtils.getConfig(getConnection(),lasttimeconfvalue,"2013-10-11 00:00:00");
		//Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		//Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
		request.setPageSize(10);
		//request.setStartModified(startdate);
	//	request.setEndModified(enddate);
		ItemsGetOnsaleResponse response=cc.execute(request);
		return response;
	}
	
}
