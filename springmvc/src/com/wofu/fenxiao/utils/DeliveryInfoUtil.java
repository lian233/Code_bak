package com.wofu.fenxiao.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.misc.BASE64Encoder;
import com.best.ebill.client.EbillClient;
import com.best.ebill.client.impl.DefaultEbillClient;
import com.best.ebill.client.request.ebill.EbillDetail;
import com.best.ebill.client.request.ebill.EbillRequest;
import com.best.ebill.client.response.ebill.EbillResponse;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.PackageItem;
import com.taobao.api.domain.TradeOrderInfo;
import com.taobao.api.domain.WaybillAddress;
import com.taobao.api.domain.WaybillApplyNewInfo;
import com.taobao.api.domain.WaybillApplyNewRequest;
import com.taobao.api.request.WlbWaybillIGetRequest;
import com.taobao.api.response.WlbWaybillIGetResponse;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.DecDelivery;
import com.wofu.fenxiao.domain.DecOrderItem;
import com.wofu.fenxiao.domain.DecShop;
public class DeliveryInfoUtil {
	private static final Logger logger = LoggerFactory.getLogger(LoggerNames.LOGISTICS_COMPONENT);
	//取圆通快递单号
	public static HashMap<String,Object> getYtoDeliveryInfo(HashMap map,DecCustomer customer,List<DecOrderItem> item)throws Exception{
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		String sendertele = "".equals(map.get("tele").toString())?map.get("phone").toString():map.get("tele").toString();
		String city =map.get("city")+","+map.get("district");
		logger.info("city: "+city);
		StringBuilder bizData=new StringBuilder();
		bizData.append("<RequestOrder>");
		bizData.append("<clientID>"+map.get("clientid").toString()+"</clientID>");
		bizData.append("<logisticProviderID>YTO</logisticProviderID>");
		bizData.append("<customerId>").append(map.get("clientid").toString()).append("</customerId>");
		bizData.append("<txLogisticID>"+map.get("sheetid").toString().trim()+"</txLogisticID>");//物流订单号
		bizData.append("<orderType>1</orderType>");
		bizData.append("<serviceType>1</serviceType>");
		bizData.append("<sender>");
		bizData.append("<name>"+customer.getLinkMan()+"</name>");
		bizData.append("<mobile>"+customer.getMobile()+"</mobile>");
		bizData.append("<prov>"+customer.getState()+"</prov>");
		bizData.append("<city>"+customer.getCity()+"</city>");
		bizData.append("<address>"+customer.getAddress()+"</address>");
		bizData.append("</sender>");
		bizData.append("<receiver>");
		bizData.append("<name>"+map.get("linkman")+"</name>");
		if(map.get("zipcode").toString().length()>6)
			bizData.append("<postCode>"+map.get("zipcode").toString().substring(0,6)+"</postCode>");
		else
			bizData.append("<postCode>"+map.get("zipcode")+"</postCode>");
		bizData.append("<mobile>"+sendertele.replaceAll(" ","/")+"</mobile>");
		bizData.append("<prov>"+map.get("state")+"</prov>");
		bizData.append("<city>"+city+"</city>");
		bizData.append("<address>"+map.get("city")+"</address>");
		bizData.append("</receiver>");
		bizData.append("<items>");
		for (int i=0;i<item.size();i++)
		{
			DecOrderItem t= item.get(i);
			bizData.append("<item>");
			String name=t.getTitle().trim();
			//xml特殊字符转义
			int qty=t.getPurQty();
			bizData.append("<itemName><![CDATA["+name.replaceAll(" ","")+"]]></itemName>");
			bizData.append("<number>"+qty+"</number>");
			bizData.append("</item>");
		}
		bizData.append("</items>");
		bizData.append("</RequestOrder>");
		String sendData = bizData.toString();
		String sign=ytoMakeSign(sendData,map.get("partnerkey").toString());
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("logistics_interface",sendData);
		params.put("data_digest", sign);
		params.put("clientId", map.get("clientid").toString());
		String result=HttpUtil.sendRequest(map.get("url").toString(), params, null);
		System.out.println("result: "+result);
		Document doc = DOMHelper.newDocument(result);
		Element info = doc.getDocumentElement();
		if("true".equals(DOMHelper.getSubElementVauleByName(info, "success"))){
			Element orderMessage = DOMHelper.getSubElementsByName(info, "orderMessage")[0];
			String outsid = DOMHelper.getSubElementVauleByName(orderMessage, "mailNo");
			String zoneCode = DOMHelper.getSubElementVauleByName(orderMessage, "bigPen");
			logger.info("取圆通快递单号成功,单据编码: "+map.get("sheetid").toString()+",快递单号: "+outsid);
			returnMap.put("deliverysheetid", outsid);
			returnMap.put("zoneCode", zoneCode);
			logger.info("zonecode: "+zoneCode);
			returnMap.put("errcode",0);
			//请求目的地编码
			HashMap<String,String> destMap = new HashMap<String,String>();
			destMap.put("method", "yto.BaseData.TransferInfo");
			destMap.put("v", map.get("v").toString());
			destMap.put("user_id", map.get("UserId").toString());
			destMap.put("app_key", map.get("appkey").toString());
			destMap.put("format", "json");
			destMap.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			CenterCode code = new CenterCode();
			code.setCity(map.get("city").toString());
			code.setDistrict(map.get("district").toString());
			code.setProvince(map.get("state").toString());
			//Log.info("code: "+code.toJSONObject());
			JSONArray array = new JSONArray();
			array.add(JSONObject.fromObject(code));
			destMap.put("param", array.toString());
			sign = DeliveryInfoUtil.makeSignT(destMap, map.get("SecretKey").toString());
			destMap.put("sign", sign);
			try{
				String destCoderesult =HttpUtil.sendRequest(map.get("queryurl").toString(),destMap,null);
				logger.info("destCoderesult: "+destCoderesult);
				JSONObject obj= JSONObject.fromObject(destCoderesult);
				returnMap.put("destCode", obj.getString("TransferCenterCode"));
				logger.info("取目的地信息: "+destCoderesult);
			}catch(Exception ed){
				returnMap.put("destCode", "");
			}
	}else{
		returnMap.put("errcode",1);
		String error= DOMHelper.getSubElementVauleByName(info, "reason");
		System.out.println("error: "+error);
		returnMap.put("errmsg",error.length()>80?error.substring(0,80):error);
	}
		return returnMap;
	}
		
	//取汇通快递单号
	public static HashMap<String,Object> getHkDeliveryInfo(HashMap map,DecCustomer customer,List<DecOrderItem> item)throws Exception{
		EbillClient dfc = new DefaultEbillClient(map.get("url").toString(), map.get("clientid").toString(),map.get("partnerkey").toString());
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		String sendertele = "".equals(map.get("tele").toString())?map.get("phone").toString():map.get("tele").toString();
		//String[] address = customer.getAddress().split(" ");
		EbillRequest pr = new EbillRequest();
		pr.setDeliveryConfirm(false);//设置为false，会默认发货确认
		EbillDetail ed  = new EbillDetail();
		String title="";
		ed.setReceiveCity(map.get("city").toString());
		ed.setReceiveCounty(map.get("district").toString());
		ed.setReceiveMan(map.get("linkman").toString());
		ed.setReceiveManPhone(sendertele);
		ed.setReceiveManAddress(map.get("address").toString());
		ed.setReceivePostcode(map.get("zipcode").toString());
		ed.setReceiveProvince(map.get("state").toString());
		ed.setSendCity(customer.getCity());
		ed.setSendCounty(customer.getDistrict());
		ed.setSendMan(customer.getLinkMan());
		ed.setSendManAddress(customer.getAddress());
		ed.setSendManPhone(customer.getMobile());
		ed.setSendProvince(customer.getState());
		for(int i=0;i<item.size();i++){
			DecOrderItem tt = item.get(i);
			title+=tt.getTitle()+";";
			ed.setItemCount((long)(tt.getPurQty()));
		}
		ed.setItemName(title);	
		pr.getEDIPrintDetailList().add(ed);
		EbillResponse efr = dfc.ebill(pr, UUID.randomUUID().toString());
		if("SUCCESS".equalsIgnoreCase(efr.getResult())){
			String outsid =efr.getEDIPrintDetailList().get(0).getMailNo();
			logger.info("取到汇通快递单号: "+outsid+"订单编号为:　"+map.get("sheetid").toString());
			returnMap.put("deliverysheetid", outsid);
			returnMap.put("errcode",0);
		}else{
			returnMap.put("errcode",1);
			returnMap.put("errmsg","");
		    logger.info("取汇通快递单号出错,订单编号: "+map.get("sheetid").toString());
		}
		return returnMap;
}

		
		private static String ytoMakeSign(String xml,String parnterId)
		throws Exception
	{
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		messagedigest.update((xml+parnterId).getBytes("utf-8"));
		String sign = new BASE64Encoder().encode(messagedigest.digest());
        return sign;
	}
		
		//汇通签名
		public static String makeSign(Map params,String key) throws Exception{
			StringBuilder sb = new StringBuilder();
			sb.append((String)params.get("mailNo")).append(key);
			System.out.println("sb: "+sb.toString());
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sb.toString().getBytes("utf-8"));
			byte[] b = md.digest();
			return (new sun.misc.BASE64Encoder()).encode(b);
		}
		
		//圆通目的地编码签名
		private static String makeSignT(Map params, String s1) throws NoSuchAlgorithmException {
			TreeMap<String,String> map = new TreeMap<String,String>();
			map.putAll(params);
			StringBuilder sb= new StringBuilder(s1);
			for(Iterator it=map.keySet().iterator();it.hasNext();){
				String name=(String)it.next();
				if("param".equals(name)) continue;
				sb.append(name).append((String)map.get(name));
			}
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(sb.toString().getBytes());
			return bytes2Hex(md5.digest()); 
		}
		
		private static String bytes2Hex(byte[] byteArray) {
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (byteArray[i] >= 0 && byteArray[i] < 16) {
					strBuf.append("0");
				}
				strBuf.append(Integer.toHexString(byteArray[i] & 0xFF).toUpperCase());
			}
			return strBuf.toString();
		}
		
	//查询汇通路由信息
	public static String getHKDeliveryRouteInfo(String partnerid,String outsid,String partnerkey,String url) throws Exception{
		HashMap<String ,String> request = new HashMap<String ,String>();
		request.put("parternID", partnerid);
		request.put("mailNo", outsid);
		request.put("serviceType", "RequestQuery");
		request.put("format","json");
		return HttpUtil.sendRequest(url,request,partnerkey);
	}
	
	//查询圆通快递信息
	public static String getYTODeliveryRouteInfo(DecDelivery delivery,String data)throws Exception{
		HashMap<String,String> params = new HashMap<String ,String>();
		params.put("method", "yto.Marketing.WaybillTrace");
		params.put("v", delivery.getV());
		params.put("user_id", delivery.getUserId());
		params.put("app_key", delivery.getAppkey());
		params.put("format", "json");
		params.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
		params.put("param", data);
		String sign =makeSignT(params,delivery.getSecretKey());
		params.put("sign", sign);
		return HttpUtil.sendRequest(delivery.getQueryurl(),params,null);
	}
	
	//hashMap转jsonarray
	public static JSONArray hashmaptojsonarr(Map map) throws Exception{
		JSONArray arr = new JSONArray();
		for(Iterator it= map.keySet().iterator();it.hasNext();){
			JSONObject obj = (JSONObject)map.get((String)it.next());
			arr.add(obj);
		}
		return arr;
	}

	//获取菜鸟物流电子单面
	public static HashMap<String, Object> getCAINIAODeliveryInfo(DecShop shop, HashMap map,
			DecCustomer customer, List<DecOrderItem> item) throws Exception{
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		String sendertele = "".equals(map.get("tele").toString())?map.get("phone").toString():map.get("tele").toString();
		TaobaoClient client=new DefaultTaobaoClient(map.get("url").toString(), shop.getAppKey(), shop.getSession());
		WlbWaybillIGetRequest req=new WlbWaybillIGetRequest();
		WaybillApplyNewRequest waybill_apply_new_request = new WaybillApplyNewRequest();
		WaybillAddress address = new WaybillAddress();//快递地址信息
		address.setAddressDetail(map.get("address").toString());
		address.setAddressFormat("json");
		address.setArea(map.get("district").toString());//区
		//address.setAreaCode(areaCode)
		address.setCity(map.get("city").toString());
		address.setProvince(map.get("state").toString());
		List<TradeOrderInfo> tradeinfos = new ArrayList<TradeOrderInfo>();
		TradeOrderInfo tradeOrderInfo = new TradeOrderInfo();
		tradeinfos.add(tradeOrderInfo);
		tradeOrderInfo.setConsigneeName(map.get("linkman").toString());//
		tradeOrderInfo.setConsigneePhone(sendertele);
		tradeOrderInfo.setConsigneeAddress(address);
		/**
		 * "OTHERS","TB","TM","JD","PP","YX","YHD","DD","EBAY
      ","AMAZON","QQ","SN","GM","WPH","JM","LF","MGJ","J
      S","PX","YT","VANCL","YL","YG","1688"
		 */
		tradeOrderInfo.setOrderChannelsType(OrderSource.findByValue((Integer)map.get("channelid")).name());//订单业源类型
		//交易订单列表
		List<String> tradeOrderList = new ArrayList<String>();
		tradeOrderList.add(map.get("sheetid").toString());
		tradeOrderInfo.setTradeOrderList(tradeOrderList);
		//订单商品列表  包裹里面的商品类型
		List<PackageItem> packageList = new ArrayList<PackageItem>();
		for(Iterator it =item.iterator();it.hasNext(); ){
			DecOrderItem decOrderItem = (DecOrderItem)it.next();
			PackageItem itemTemp = new PackageItem();
			itemTemp.setCount(Long.valueOf(decOrderItem.getPurQty()));
			itemTemp.setItemName(decOrderItem.getTitle());
			packageList.add(itemTemp);
		}
		tradeOrderInfo.setPackageItems(packageList);
		tradeOrderInfo.setProductType("STANDARD_EXPRESS");//快递服务产品类型编码
		tradeOrderInfo.setRealUserId(Long.valueOf(shop.getUser_id()));//面单使用者id
		waybill_apply_new_request.setShippingAddress(address);
		waybill_apply_new_request.setCpCode(map.get("deliveryname").toString());//CP 快递公司编码
		waybill_apply_new_request.setRealUserId(Long.valueOf(shop.getUser_id()));
		waybill_apply_new_request.setAppKey(shop.getAppKey());
		waybill_apply_new_request.setTradeOrderInfoCols(tradeinfos);//对应数据结构示例JSON
		req.setWaybillApplyNewRequest(waybill_apply_new_request);
		WlbWaybillIGetResponse response = client.execute(req , shop.getToken());
		System.out.println(req.getWaybillApplyNewRequest());
		System.out.println(response.getBody());
		if(response.isSuccess()){
			List<WaybillApplyNewInfo> waybillApplyNewInfo =response.getWaybillApplyNewCols();
			WaybillApplyNewInfo temp = waybillApplyNewInfo.get(0);
			System.out.println("大头笔信息: "+temp.getShortAddress()+"面单号: "+temp.getWaybillCode()+"目的地编码: "+temp.getPackageCenterCode());
			returnMap.put("errcode", 0);
			returnMap.put("deliverysheetid", temp.getWaybillCode());
		}else{
			returnMap.put("errcode", 1);
		}
		
		return returnMap;
	}
		
}
