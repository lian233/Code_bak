package com.wofu.ecommerce.jit;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vipapis.delivery.GetPickListResponse;
import vipapis.delivery.GetPoListRequest;
import vipapis.delivery.GetPoListResponseT;
import vipapis.delivery.PickDetailRequest;
import vipapis.delivery.PickDetailT;
import vipapis.delivery.PickProduct;
import vipapis.delivery.SimplePick;
import vipapis.delivery.JitDeliveryServiceHelper.JitDeliveryServiceClient;

import com.vip.osp.sdk.context.InvocationContext;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jit.utils.Utils;
public class OrderUtils {
	/*
	 * 转入一个订单到接口表
	 */
	private static HashMap<String,Object> warehouse = new HashMap<String,Object>();
	private static SimpleDateFormat timeFormat= new SimpleDateFormat("HH:mm:ss");
	private static Long nighteen ;
	private static Long thirteen ;
	private static Long fourteen ;
	private static Long fiveteen ;
	private static Long sixteen ;
	private static Long seventteen ;
	private static Long eighteen ;
	static {
		//广州仓
		HashMap<String,String> nh = new HashMap<String,String>();
		nh.put("tel", "0758-8990088");
		nh.put("phone", "0758-8993051");
		nh.put("address", "肇庆市 大旺高新区 亚铝大街以南 北江大道 ");
		nh.put("linkman", "曹淑仪");
		nh.put("name", "广州仓");
		//上海仓
		HashMap<String,String> sh = new HashMap<String,String>();
		sh.put("tel", "0512-36827574");
		sh.put("phone", "0512-36827588");
		sh.put("address", "江苏省昆山市淀湖镇丁家滨路7号");
		sh.put("linkman", "王赛男");
		sh.put("name", "上海仓");
		
		//成都仓
		HashMap<String,String> cd = new HashMap<String,String>();
		cd.put("tel", "028-27985500");
		cd.put("phone", "028-27985501");
		cd.put("address", "简阳市简新大道南路66号法派工业园 ");
		cd.put("linkman", "吴美");
		cd.put("name", "成都仓");
		
		//北京仓
		HashMap<String,String> bj = new HashMap<String,String>();
		bj.put("tel", "022-82209110");
		bj.put("phone", "022-82209113");
		bj.put("address", "天津市武清区崔黄口镇地毯产业园宏达道宏光路20号");
		bj.put("linkman", "常金梦 ");
		bj.put("name", "北京仓 ");
		
		//华中仓
		HashMap<String,String> hz = new HashMap<String,String>();
		hz.put("tel", "13398190963");
		hz.put("phone", "0711-3819647");
		hz.put("address", "湖北省鄂州市葛店开发区人民路唯品会");
		hz.put("linkman", "郑正谱");
		hz.put("name", "武汉仓");
		
		warehouse.put("VIP_NH", nh);
		warehouse.put("VIP_SH", sh);
		warehouse.put("VIP_CD", cd);
		warehouse.put("VIP_BJ", bj);
		warehouse.put("VIP_HZ", hz);
		try {
			nighteen = timeFormat.parse("10:00:00").getTime();
			thirteen = timeFormat.parse("14:00:00").getTime();
			fourteen = timeFormat.parse("15:00:00").getTime();
			fiveteen = timeFormat.parse("16:00:00").getTime();
			sixteen = timeFormat.parse("17:00:00").getTime();
			seventteen = timeFormat.parse("18:00:00").getTime();
			eighteen = timeFormat.parse("19:00:00").getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String createInterOrder(Connection conn,
			PickDetailT o,String pick_no, String tradecontactid,String username) throws Exception {
		String tid=o.getPo_no()+"_"+pick_no;//tid格式  po_pick_no
		String sellermemo =username+" "+Formatter.format(new Date(),Formatter.DATE_FORMAT)+" "
			+((HashMap)warehouse.get(o.getWarehouse())).get("name").toString()+" ";
		if("成都仓".equals(((HashMap)warehouse.get(o.getWarehouse())).get("name").toString())){//成都仓
			sellermemo = sellermemo+"(贝贝怡PO: "+o.getPo_no()+")";
		}else{
			long current = timeFormat.parse(Formatter.format(new Date(), Formatter.TIME_FORMAT)).getTime();
			if(pick_no.indexOf("BHPICK")!=-1){//补货单： 13：00-17：00之间的BH都发第一批   下午17：00-18：00出来的补货单要发第二批
				if(current<nighteen){//第一批第一次
					sellermemo = sellermemo+"(1-1)";
				}else if(current<thirteen){
					sellermemo = sellermemo+"(2-1)";
				}else if(current<fourteen){
					sellermemo = sellermemo+"(3-1)";
				}else if(current<fiveteen){
					sellermemo = sellermemo+"(4-1)";
				}else if(current<sixteen){
					sellermemo = sellermemo+"(5-1)";
				}else if(current<seventteen){
					sellermemo = sellermemo+"(1-2)";
				}else if(current<eighteen){
					sellermemo = sellermemo+"(2-2)";
				}
			}else{
				if(current<nighteen){//第一批第一次
					sellermemo = sellermemo+"(1-1)";
				}else if(current<thirteen){
					sellermemo = sellermemo+"(2-1)";
				}else if(current<fourteen){
					sellermemo = sellermemo+"(1-2)";
				}else if(current<fiveteen){
					sellermemo = sellermemo+"(2-2)";
				}else if(current<sixteen){
					sellermemo = sellermemo+"(3-2)";
				}else if(current<seventteen){
					sellermemo = sellermemo+"(4-2)";
				}else if(current<eighteen){
					sellermemo = sellermemo+"(5-2)";
				}
			}
			sellermemo = sellermemo+"(贝贝怡PO: "+o.getPo_no()+")";
		}
		Date time = "0000-00-00 00:00:00".equals(o.getExport_time())?new Date():Formatter.parseDate(o.getExport_time(), Formatter.DATE_TIME_FORMAT);
		try {
			String sheetid = "";
			conn.setAutoCommit(false);
			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");
			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick ,"
					+ "  created , buyermemo , paytime ,  modified , "
					+" tradefrom,tradeContactid,ordersheetid,receiveraddress,receivername,receivermobile,receiverphone,sellermemo,buyernick) "
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ tid
					+ "','"+ username+ "','"+Formatter.format(time, Formatter.DATE_TIME_FORMAT)
					+ "','','"+Formatter.format(time, Formatter.DATE_TIME_FORMAT)+"',"//拣货单放到地址字段中去
					+"'"+o.getSell_st_time()+"','JIT'," + tradecontactid + ",'"+o.getPo_no()+"','"
					+((HashMap)warehouse.get(o.getWarehouse())).get("address").toString()+ 
					"','"+((HashMap)warehouse.get(o.getWarehouse())).get("name").toString()+"_"+o.getPo_no()+
					"','"+((HashMap)warehouse.get(o.getWarehouse())).get("phone").toString()+
					"','"+((HashMap)warehouse.get(o.getWarehouse())).get("tel").toString()+
					"','"+sellermemo+"','虚拟客户')";
			SQLHelper.executeSQL(conn, sql);
			for (Iterator it = o.getPick_product_lists().getRelationData().iterator();it.hasNext();) {
				PickProduct  product =(PickProduct) it.next();
				sql = "insert into ns_orderitem(CustomerOrderId,orderItemId,SheetID,skuid,itemmealname,"
						+ " title,sellernick,created,outerskuid,num,payment,totalfee,price) values("
						+ "'"+ sheetid+ "','"+ sheetid+ product.getBarcode()+ "','"+ sheetid+ "','0','"+ product.getProduct_name()
						+ "', '"+ product.getProduct_name()+ "' , '"+ username+ "', '"+Formatter.format(time, Formatter.DATE_TIME_FORMAT)
						+ "', '"+ product.getBarcode()+ "' , '"+ 
						+ product.getStock()+ "','"+product.getActual_market_price()*product.getStock()+"','"+product.getActual_market_price()*product.getStock()+"','"+product.getActual_market_price()+"')";
				SQLHelper.executeSQL(conn, sql);		
			}
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成JIT拣货订单【" + tid + "】接口数据成功，接口单号【"+ sheetid + "】");
			return sheetid;
		} catch (JSQLException e1) {
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			throw new JException("生成订单【" + tid + "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	
	public static void getRefund(Connection conn,String tradecontactid,Order o)	throws Exception {

			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
					+ tradecontactid;
			String inshopid = SQLHelper.strSelect(conn, sql);

			conn.setAutoCommit(false);
			
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();

				sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
				String sheetid = SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql, "取接口单号出错!");
	
				// 加入到通知表
				sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
						+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "insert into ns_Refund(SheetID , tid,RefundID , Oid , AlipayNo , "
						+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
						+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
						+ "Price , Num , GoodReturnTime , Sid , "
						+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName , "
						+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
				
				Object[] sqlv = {
						sheetid,
						o.getOrderCode(),
						String.valueOf(o.getOrderId())+String.valueOf(item.getId()),
						String.valueOf(item.getId()),
						o.getEndUserId(),
						o.getEndUserId(),
						o.getOrderCreateTime(),
						o.getUpdateTime(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						1,
						item.getOrderItemAmount(),
						item.getOrderItemAmount(),
						o.getDeliveryRemark(),
						o.getDeliveryRemark(),
						item.getProductCName(),
						item.getOrderItemPrice(),
						item.getOrderItemNum(),
						item.getProcessFinishDate(),
						"",
						item.getOrderItemAmount(),
						item.getOuterId(),
						item.getOuterId(),
						"",
						"",
						o.getGoodReceiverProvince() + " " + o.getGoodReceiverCity() + " "
								+ o.getGoodReceiverCounty()+ " "
								+ o.getGoodReceiverAddress(), inshopid,
						o.getOrderCode(), o.getGoodReceiverName(),
						o.getGoodReceiverPhone() + " " + o.getGoodReceiverMoblie(),
						o.getEndUserId() };
	
				SQLHelper.executePreparedSQL(conn, sql, sqlv);
			}
			Log.info("生成退货单成功,订单号:"+ o.getOrderCode()+ " 订单状态："+o.getOrderStatus()				
					+ " 订单创建时间:"+Formatter.format(o.getOrderCreateTime(),Formatter.DATE_TIME_FORMAT));

			conn.commit();
			conn.setAutoCommit(true);

	}
	
	//每一步：获取po列表  当天以后的
	public static GetPoListResponseT getPoList(Map<String,String> params) throws Exception{
		//{"vendor_id":"2010","et_sell_st_time":"2015-08-12","page":1,"limit":100}
		GetPoListRequest getPoListRequest  = new GetPoListRequest();
		getPoListRequest.setLimit(100);
		getPoListRequest.setPage(1);
		getPoListRequest.setVendor_id(params.get("vendor_id"));//把取po时间提前1分钟
		getPoListRequest.setSt_sell_et_time(Formatter.format(new Date(System.currentTimeMillis()), Formatter.DATE_TIME_FORMAT));
		getPoListRequest.setEt_sell_et_time(Formatter.format(new Date(System.currentTimeMillis()+6*24*60*60*1000L), Formatter.DATE_TIME_FORMAT));
		String request = getPoListRequest.toJSONObject();
		System.out.println("request: "+request);
		HashMap<String,String> map = new  HashMap<String,String>();
		map.put("appKey", params.get("app_key"));
		map.put("format", params.get("format"));
		map.put("method", "getPoList");
		map.put("service", params.get("service"));
		map.put("timestamp", String.valueOf(System.currentTimeMillis()/1000L));
		map.put("version", "1.0.0");
		String result = Utils.sendByPost(map,request,params.get("url"),params.get("app_secret"));
		Log.info("result:　"+result);
		JSONObject obj = new JSONObject(result);
		if("0".equals(obj.getString("returnCode"))){
			JSONObject res = obj.getJSONObject("result");
			GetPoListResponseT response = new GetPoListResponseT();
			response.setFieldValue(response, "purchase_order_list", res.getJSONArray("purchase_order_list"));
			response.setObjValue(response, res);
			return response;
			
		}
		return null;
		
		
	}
	
	//每一步：获取po列表  当天过期的po
	public static GetPoListResponseT getExPirePoList(Map<String,String> params) throws Exception{
		//{"vendor_id":"2010","et_sell_st_time":"2015-08-12","page":1,"limit":100}
		GetPoListRequest getPoListRequest  = new GetPoListRequest();
		getPoListRequest.setLimit(100);
		getPoListRequest.setPage(1);
		getPoListRequest.setVendor_id(params.get("vendor_id"));//把取po时间提前24小时
		getPoListRequest.setSt_sell_et_time(Formatter.format(new Date(System.currentTimeMillis()-24*60*60*1000L), Formatter.DATE_TIME_FORMAT));
		getPoListRequest.setEt_sell_et_time(Formatter.format(new Date(System.currentTimeMillis()-30*1000L), Formatter.DATE_TIME_FORMAT));
		String request = getPoListRequest.toJSONObject();
		System.out.println("request: "+request);
		HashMap<String,String> map = new  HashMap<String,String>();
		map.put("appKey", params.get("app_key"));
		map.put("format", params.get("format"));
		map.put("method", "getPoList");
		map.put("service", params.get("service"));
		map.put("timestamp", String.valueOf(System.currentTimeMillis()/1000L));
		map.put("version", "1.0.0");
		String result = Utils.sendByPost(map,request,params.get("url"),params.get("app_secret"));
		Log.info("result:　"+result);
		JSONObject obj = new JSONObject(result);
		if("0".equals(obj.getString("returnCode"))){
			JSONObject res = obj.getJSONObject("result");
			GetPoListResponseT response = new GetPoListResponseT();
			response.setFieldValue(response, "purchase_order_list", res.getJSONArray("purchase_order_list"));
			response.setObjValue(response, res);
			return response;
			
		}
		return null;
		
		
	}
	//获取指定po下面的拣货单
//	public static GetPickListResponse getPickList(String po_no,String app_key,String app_secret,String vendor_id,String url) throws Exception{
//		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
//		//2、设置调用参数，必须
//		InvocationContext instance = InvocationContext.Factory.getInstance();
//		instance.setAppKey(app_key);
//		instance.setAppSecret(app_secret);
//		instance.setAppURL(url);
//		return client.getPickList(Integer.parseInt(vendor_id),po_no,null,null,null,null,null,null,null,null,null,null,null,1,100);
//	}
	//获取拣货单明细
	public static PickDetailT getPickDetail(Map<String,String> params ) throws Exception{
		int page=1;
		PickDetailT pickDetailt=null;
		while(true){
			PickDetailRequest pickDetail = new PickDetailRequest();
			pickDetail.setPage(page);
			pickDetail.setPick_no(params.get("pick_no"));
			pickDetail.setPo_no(params.get("po_no"));
			pickDetail.setVendor_id(params.get("vendor_id"));
			String request = pickDetail.toJSONObject();
			System.out.println("request: "+request);
			HashMap<String,String> map = new  HashMap<String,String>();
			map.put("appKey", params.get("app_key"));
			map.put("format", params.get("format"));
			map.put("method", "getPickDetail");
			map.put("service", params.get("service"));
			map.put("timestamp", String.valueOf(System.currentTimeMillis()/1000L));
			map.put("version", params.get("version"));
			String result = Utils.sendByPost(map,request,params.get("url"),params.get("app_secret"));
			Log.info("result:　"+result);
			//Log.info("pickDetail: "+result);
			JSONObject obj = new JSONObject(result);
			if("0".equals(obj.getString("returnCode"))){
				int totalNum = obj.getJSONObject("result").getInt("total");
				int totalPage = Double.valueOf(Math.ceil(totalNum/100.0)).intValue();
				System.out.println("总页数: "+totalPage);
				if(page==1){
					pickDetailt = new PickDetailT();
					pickDetailt.setObjValue(pickDetailt, obj.getJSONObject("result"));
					pickDetailt.setFieldValue(pickDetailt, "pick_product_lists", obj.getJSONObject("result").getJSONArray("pick_product_list"));
				}else{
					JSONArray items = obj.getJSONObject("result").getJSONArray("pick_product_list");
					for(int i=0;i<items.length();i++){
						JSONObject temp = items.getJSONObject(i);
						PickProduct product = new PickProduct();
						product.setObjValue(product, temp);
						pickDetailt.getPick_product_lists().getRelationData().add(product);
					}
					
				}
				if(page<totalPage)
					page++;
				else{
					break;
				}
			}
			
		}
		return pickDetailt;
	}
	
	//创建拣货单   一次可能创建了多个拣货单
	public static List<SimplePick> createPick(String po_no,String vendor_id) throws Exception{
		String request =  "{\"po_no\":\""+po_no+"\",\"vendor_id\":"+vendor_id+"}";
		HashMap<String,String> map = new  HashMap<String,String>();
		map.put("appKey", Params.app_key);
		map.put("format", Params.format);
		map.put("method", "createPick");
		map.put("service", Params.service);
		map.put("timestamp", String.valueOf(System.currentTimeMillis()/1000L));
		map.put("version", Params.ver);
		String result = Utils.sendByPost(map,request,Params.url,Params.app_secret);
		Log.info("pickDetail: "+result);
		JSONObject obj = new JSONObject(result);
		if("0".equals(obj.getString("returnCode"))){
			List<SimplePick> lists = new ArrayList<SimplePick>();
			JSONArray arr = obj.getJSONArray("result");
			for(int i=0;i<arr.length();i++){
				SimplePick simplePick = new SimplePick();
				simplePick.setObjValue(simplePick, arr.getJSONObject(i));
				lists.add(simplePick);
			}
			
			return lists;
		}
		return null;
	}
	
	
}
