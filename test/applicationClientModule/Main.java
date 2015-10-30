
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.LogisticsService;
import com.taobao.api.domain.PackageItem;
import com.taobao.api.domain.PrintCheckInfo;
import com.taobao.api.domain.TradeOrderInfo;
import com.taobao.api.domain.WaybillAddress;
import com.taobao.api.domain.WaybillApplyCancelRequest;
import com.taobao.api.domain.WaybillApplyFullUpdateRequest;
import com.taobao.api.domain.WaybillApplyNewInfo;
import com.taobao.api.domain.WaybillApplyNewRequest;
import com.taobao.api.domain.WaybillApplyPrintCheckRequest;
import com.taobao.api.domain.WaybillApplyRequest;
import com.taobao.api.domain.WaybillDetailQueryRequest;
import com.taobao.api.domain.WaybillProductType;
import com.taobao.api.domain.WaybillProductTypeRequest;
import com.taobao.api.domain.WaybillServiceType;
import com.taobao.api.request.WlbWaybillICancelRequest;
import com.taobao.api.request.WlbWaybillIFullupdateRequest;
import com.taobao.api.request.WlbWaybillIGetRequest;
import com.taobao.api.request.WlbWaybillIPrintRequest;
import com.taobao.api.request.WlbWaybillIProductRequest;
import com.taobao.api.request.WlbWaybillIQuerydetailRequest;
import com.taobao.api.request.WlbWaybillISearchRequest;
import com.taobao.api.response.WlbWaybillICancelResponse;
import com.taobao.api.response.WlbWaybillIFullupdateResponse;
import com.taobao.api.response.WlbWaybillIGetResponse;
import com.taobao.api.response.WlbWaybillIPrintResponse;
import com.taobao.api.response.WlbWaybillIProductResponse;
import com.taobao.api.response.WlbWaybillIQuerydetailResponse;
import com.taobao.api.response.WlbWaybillISearchResponse;

/**
 * 菜鸟物流
 * @author Administrator
 *
 */
public class Main {

	/**
	 * @param args
	 * "cp_code": "POSTB",
    "shipping_address": {
        "address_detail": "文一西路969号",
        "area": "余杭区",
        "city": "杭州市",
        "province": "浙江省",
        "town": "仓前街道"
    }, 
	
	 */
	private static String url="http://gw.api.tbsandbox.com/router/rest";
	private static String appkey="1021520535";
	private static String secret="sandbox7fd8ac852ea02a740277f1289";
	private static String sessionKey="6100e17f8680b4ed20716437b1162e49b5d13c0a0ba9e052054718218";
	private static String jsonValue="{\"service_value4_json\":\"{\"value\":\"100.00\",\"currency\":\"CNY\",\"payment_type\":\"CASH\"},\"service_code\":\"SVC-COD\"}";
	public static void main(String[] args) throws Exception {
		
		getWayBill();//获取电子面单
		cancel();//取消电子面单
		search();
		searchDetail();
		getServiceType();
		modifiedWayBill();
		printInfo();
	}
	
	//获取快递面单
	/**
	 * 支持批量获取，每次最多获取 10 个面单号  
	 */
	private static void getWayBill() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIGetRequest req=new WlbWaybillIGetRequest();
		WaybillApplyNewRequest waybill_apply_new_request = new WaybillApplyNewRequest();
		WaybillAddress address = new WaybillAddress();//快递地址信息
		address.setAddressDetail("文一西路969号");
		address.setAddressFormat("json");
		address.setArea("余杭区");//区
		//address.setAreaCode(areaCode)
		address.setCity("杭州市");
		address.setProvince("浙江省");
		TradeOrderInfo tradeOrderInfo = new TradeOrderInfo();
		tradeOrderInfo.setConsigneeName("张三");//
		tradeOrderInfo.setConsigneePhone("13712454578");
		tradeOrderInfo.setConsigneeAddress(address);
		tradeOrderInfo.setOrderChannelsType("TB");
		//交易订单列表
		List<String> tradeOrderList = new ArrayList<String>();
		tradeOrderList.add("193066509162111");
		tradeOrderInfo.setTradeOrderList(tradeOrderList);
		//订单商品列表  包裹里面的商品类型
		PackageItem item = new PackageItem();
		item.setCount(1L);
		item.setItemName("沙箱测试-星巴克满100赠送马克杯一个");
		List<PackageItem> packageList = new ArrayList<PackageItem>();
		packageList.add(item);
		tradeOrderInfo.setPackageItems(packageList);
		tradeOrderInfo.setProductType("STANDARD_EXPRESS");//快递服务产品类型编码
		tradeOrderInfo.setRealUserId(89346737L);//面单使用者id
		
		waybill_apply_new_request.setShippingAddress(address);
		//waybill_apply_new_request.setCpId(123L);
		//waybill_apply_new_request.setSellerId(89346737L);
		waybill_apply_new_request.setCpCode("POSTB");//CP 快递公司编码
		waybill_apply_new_request.setRealUserId(89346737L);
		waybill_apply_new_request.setAppKey(appkey);
		waybill_apply_new_request.setTradeOrderInfoCols(tradeOrderInfo);//对应数据结构示例JSON
		req.setWaybillApplyNewRequest(waybill_apply_new_request);
		WlbWaybillIGetResponse response = client.execute(req , sessionKey);
		System.out.println(req.getWaybillApplyNewRequest());
		System.out.println(response.getBody());
		List<WaybillApplyNewInfo> waybillApplyNewInfo =response.getWaybillApplyNewCols();
		Iterator it = waybillApplyNewInfo.iterator();
		for(;it.hasNext();){
			WaybillApplyNewInfo temp = (WaybillApplyNewInfo)it.next();
			System.out.println("大头笔信息: "+temp.getShortAddress()+"面单号: "+temp.getWaybillCode()+"目的地编码: "+temp.getPackageCenterCode());
		}
		
	}
	
	private static void getWayBill1() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIGetRequest req=new WlbWaybillIGetRequest();
		WaybillApplyNewRequest waybill_apply_new_request = new WaybillApplyNewRequest();
		WaybillAddress address = new WaybillAddress();//快递地址信息
		address.setAddressDetail("文一西路969号");
		address.setAddressFormat("json");
		address.setArea("余杭区");//区
		//address.setAreaCode(areaCode)
		address.setCity("杭州市");
		address.setProvince("浙江省");
		TradeOrderInfo tradeOrderInfo = new TradeOrderInfo();
		tradeOrderInfo.setConsigneeName("张三");//
		tradeOrderInfo.setConsigneePhone("13712454578");
		tradeOrderInfo.setConsigneeAddress(address);
		tradeOrderInfo.setOrderChannelsType("TB");
		//交易订单列表
		List<String> tradeOrderList = new ArrayList<String>();
		tradeOrderList.add("193066509162111");
		tradeOrderInfo.setTradeOrderList(tradeOrderList);
		//订单商品列表  包裹里面的商品类型
		PackageItem item = new PackageItem();
		item.setCount(1L);
		item.setItemName("沙箱测试-星巴克满100赠送马克杯一个");
		List<PackageItem> packageList = new ArrayList<PackageItem>();
		packageList.add(item);
		tradeOrderInfo.setPackageItems(packageList);
		tradeOrderInfo.setProductType("STANDARD_EXPRESS");//快递服务产品类型编码
		tradeOrderInfo.setRealUserId(89346737L);//面单使用者id
		
		waybill_apply_new_request.setShippingAddress(address);
		//waybill_apply_new_request.setCpId(123L);
		//waybill_apply_new_request.setSellerId(89346737L);
		waybill_apply_new_request.setCpCode("POSTB");//CP 快递公司编码
		waybill_apply_new_request.setRealUserId(89346737L);
		waybill_apply_new_request.setAppKey(appkey);
		waybill_apply_new_request.setTradeOrderInfoCols(tradeOrderInfo);//对应数据结构示例JSON
		req.setWaybillApplyNewRequest(waybill_apply_new_request);
		WlbWaybillIGetResponse response = client.execute(req , sessionKey);
		System.out.println(req.getWaybillApplyNewRequest());
		System.out.println(response.getBody());
		List<WaybillApplyNewInfo> waybillApplyNewInfo =response.getWaybillApplyNewCols();
		Iterator it = waybillApplyNewInfo.iterator();
		for(;it.hasNext();){
			WaybillApplyNewInfo temp = (WaybillApplyNewInfo)it.next();
			System.out.println("大头笔信息: "+temp.getShortAddress()+"面单号: "+temp.getWaybillCode()+"目的地编码: "+temp.getPackageCenterCode());
		}
		
	}
	
	//查询面单服务订购及面单使用情况v1.0    查询加盟型物流合作商的发货地址   这个发货地址一定要与获取电子面单的发货地址一致   直营的不用查
	//直营型快递的发货地址可以自己填写
	private static void search() throws ApiException{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillISearchRequest req=new WlbWaybillISearchRequest();
		WaybillApplyRequest waybill_apply_request = new WaybillApplyRequest();
		//waybill_apply_request.setSeller_id(123);
		waybill_apply_request.setCpCode("POSTB");
		//waybill_apply_request.setShipping_address(对应数据结构示例JSON);
		//waybill_apply_request.setApp_key(appkey);
		req.setWaybillApplyRequest(waybill_apply_request);
		WlbWaybillISearchResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
	}
	//取消电子面单
	private static void cancel() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillICancelRequest req = new WlbWaybillICancelRequest();
		WaybillApplyCancelRequest waybillApplyCancelRequest = new WaybillApplyCancelRequest();
		waybillApplyCancelRequest.setCpCode("POSTB");
		waybillApplyCancelRequest.setRealUserId(123L);
		//交易订单列表
		List<String> tradeOrderList = new ArrayList<String>();
		tradeOrderList.add("193066509162107");
		waybillApplyCancelRequest.setTradeOrderList(tradeOrderList);
		
		waybillApplyCancelRequest.setWaybillCode("9976608158001");
		req.setWaybillApplyCancelRequest(waybillApplyCancelRequest);
		WlbWaybillICancelResponse resp  = client.execute(req,sessionKey);
		System.out.println(resp.getBody());
	}
	
	//订单取消前调用   如果已经揽收，则会取消失败
	private static void searchDetail() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIQuerydetailRequest req=new WlbWaybillIQuerydetailRequest();
		WaybillDetailQueryRequest waybill_detail_query_request = new WaybillDetailQueryRequest();
		List<String> bills = new ArrayList<String>();
		bills.add("9976608158050");
		waybill_detail_query_request.setWaybillCodes(bills);
		waybill_detail_query_request.setSellerId(123L);
		waybill_detail_query_request.setCpCode("POSTB");
		List<String> tradeLists = new ArrayList<String>();
		tradeLists.add("193066509162107");
		waybill_detail_query_request.setTradeOrderList(tradeLists);
		req.setWaybillDetailQueryRequest(waybill_detail_query_request);
		WlbWaybillIQuerydetailResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
	}
	
	//修改快递面单信息
	private static void modifiedWayBill() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIFullupdateRequest req=new WlbWaybillIFullupdateRequest();
		WaybillApplyFullUpdateRequest waybill_apply_full_update_request = new WaybillApplyFullUpdateRequest();
		waybill_apply_full_update_request.setSendPhone("12324352324");
		waybill_apply_full_update_request.setConsigneeName("李四");
		//waybill_apply_full_update_request.setItemName("衣服");
		waybill_apply_full_update_request.setWeight(123L);
		waybill_apply_full_update_request.setSendName("王二");
		List<String> orderLists = new ArrayList<String>();
		orderLists.add("809834543");
		waybill_apply_full_update_request.setTradeOrderList(orderLists);
		waybill_apply_full_update_request.setOrderType(123L);
		waybill_apply_full_update_request.setCpCode("POSTB");
		waybill_apply_full_update_request.setWaybillCode("12321323");
		waybill_apply_full_update_request.setProductType("Standard-Express");
		waybill_apply_full_update_request.setCpId(2323L);
		waybill_apply_full_update_request.setOrderChannelsType("TB");//订单来源
		waybill_apply_full_update_request.setSellerId(123232L);
		waybill_apply_full_update_request.setRealUserId(1233232L);
		waybill_apply_full_update_request.setVolume(123L);
		//订单商品列表  包裹里面的商品类型
		PackageItem item = new PackageItem();
		item.setCount(1L);
		item.setItemName("沙箱测试-星巴克满100赠送马克杯一个");
		List<PackageItem> packageList = new ArrayList<PackageItem>();
		packageList.add(item);
		waybill_apply_full_update_request.setPackageItems(packageList);
		//服 务 类 型
		List<LogisticsService> services = new ArrayList<LogisticsService>();
		LogisticsService logisticsService = new LogisticsService();
		logisticsService.setServiceCode("POSTB");
		logisticsService.setServiceName("申通快递");
		logisticsService.setServiceValue4Json(jsonValue);
		services.add(logisticsService);
		//services.add(arg0)
		waybill_apply_full_update_request.setLogisticsServiceList(services);
		
		waybill_apply_full_update_request.setConsigneePhone("1232112322");
		//发货人地址信息
		WaybillAddress address = new WaybillAddress();//快递地址信息
		address.setAddressDetail("文一西路969号");
		address.setAddressFormat("json");
		address.setArea("余杭区");//区
		//address.setAreaCode(areaCode)
		address.setCity("杭州市");
		address.setProvince("浙江省");
		waybill_apply_full_update_request.setShippingAddress(address);
		
		//收件人地址 
		waybill_apply_full_update_request.setConsigneeAddress(address);
		//waybill_apply_full_update_request.setPackage_id("E12321321-1234567");
		req.setWaybillApplyFullUpdateRequest(waybill_apply_full_update_request);
		WlbWaybillIFullupdateResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
	}
	
	//物流商产品类型接口  
	private static void getServiceType() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIProductRequest req=new WlbWaybillIProductRequest();
		WaybillProductTypeRequest waybillProductTypeRequest = new WaybillProductTypeRequest();
		waybillProductTypeRequest.setCpCode("POSTB");
		req.setWaybillProductTypeRequest(waybillProductTypeRequest);
		WlbWaybillIProductResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
		//response.get
	}
	
	//taobao.wlb.waybill.i.print
	//可以批量验证  一次可以验证10个订单，如果有一个失败，则全部不通过
	//会返回打印次数
	//如果是正常打印（第一次打印）则notice_message为null，如果是重复打印，则有提示信息。
	private static void printInfo() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIPrintRequest req=new WlbWaybillIPrintRequest();
		WaybillApplyPrintCheckRequest waybill_apply_print_check_request = new WaybillApplyPrintCheckRequest();
		waybill_apply_print_check_request.setSellerId(2054718218L);
		//面单详情信息集合  一次可以确认多个订单
		List<PrintCheckInfo> infos = new ArrayList<PrintCheckInfo>();
		PrintCheckInfo printCheckInfo = new PrintCheckInfo();
		WaybillAddress address = new WaybillAddress();//发货地址信息
		address.setAddressDetail("文一西路969号");
		address.setAddressFormat("json");
		address.setArea("余杭区");//区
		//address.setAreaCode(areaCode)
		address.setCity("杭州市");
		address.setProvince("浙江省");
		printCheckInfo.setConsigneeAddress(address);//收货地址
		printCheckInfo.setConsigneeName("张三");//收货人
		printCheckInfo.setConsigneePhone("13712454578");
		WaybillAddress shippingAddress = new WaybillAddress();//快递地址信息
		shippingAddress.setAddressDetail("文一西路969号");
		shippingAddress.setAddressFormat("json");
		shippingAddress.setArea("余杭区");//区
		//address.setAreaCode(areaCode)
		shippingAddress.setCity("杭州市");
		shippingAddress.setProvince("浙江省");
		printCheckInfo.setShippingAddress(shippingAddress);
		printCheckInfo.setWaybillCode("9976608290002");//快递单号、
		printCheckInfo.setRealUserId(89346737L);//必传参数
		infos.add(printCheckInfo);
		waybill_apply_print_check_request.setPrintCheckInfoCols(infos);
		waybill_apply_print_check_request.setCpCode("POSTB");//快递公司编码
		req.setWaybillApplyPrintCheckRequest(waybill_apply_print_check_request);
		System.out.println(req.getWaybillApplyPrintCheckRequest());
		WlbWaybillIPrintResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
		if(response.isSuccess()){
			System.out.println("可以调用打印机进行打印......");
		}else{
			System.out.println("信息校验失败，错误信息: "+response.getSubMsg());
		}
	}

}
