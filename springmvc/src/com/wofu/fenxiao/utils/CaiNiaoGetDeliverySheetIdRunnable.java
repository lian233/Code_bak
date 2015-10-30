package com.wofu.fenxiao.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.PackageItem;
import com.taobao.api.domain.TradeOrderInfo;
import com.taobao.api.domain.WaybillAddress;
import com.taobao.api.domain.WaybillApplyNewInfo;
import com.taobao.api.domain.WaybillApplyNewRequest;
import com.taobao.api.request.WlbWaybillIGetRequest;
import com.taobao.api.response.WlbWaybillIGetResponse;
import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.DecOrderItem;

/**
 * 菜鸟物流取快递单号线程类
 * @author Administrator
 *
 */
public class CaiNiaoGetDeliverySheetIdRunnable implements Runnable{
	CountDownLatch latch = null;
	List<HashMap> mapList =null;
	HashMap<String,String> params =null;
	DecCustomer customer = null;
	ConcurrentHashMap<String,Object> currentMap=null;
	public CaiNiaoGetDeliverySheetIdRunnable(CountDownLatch latch,
			List<HashMap> mapList,HashMap<String,String> params,DecCustomer customer,
			ConcurrentHashMap<String,Object> currentMap){
		this.latch= latch;
		this.mapList = mapList;
		this.params = params;
		this.customer=customer;
		this.currentMap=currentMap;
	}
	@Override
	public void run() {
		try{
			TaobaoClient client=new DefaultTaobaoClient(params.get("url").toString(), params.get("app_key").toString(), params.get("session").toString());
			WlbWaybillIGetRequest req=new WlbWaybillIGetRequest();
			WaybillApplyNewRequest waybill_apply_new_request = new WaybillApplyNewRequest();
			List<DecOrderItem> item =null; 
			List<TradeOrderInfo> orderLists = new ArrayList<TradeOrderInfo>();
			//发货人信息
			WaybillAddress senderAddress = new WaybillAddress();
			senderAddress.setAddressDetail(customer.getAddress());
			senderAddress.setProvince(customer.getState());
			for(Iterator it = mapList.iterator();it.hasNext();){
				HashMap map = (HashMap)it.next();
				if(map.get("items").getClass()==List.class){
					item = (List<DecOrderItem>)map.get("items");
				}else{
					DecOrderItem tt = (DecOrderItem)map.get("items");
					item = new ArrayList<DecOrderItem>();
					item.add(tt);
				}
				String sendertele = "".equals(map.get("tele").toString())?map.get("phone").toString():map.get("tele").toString();
				WaybillAddress address = new WaybillAddress();//快递地址信息
				address.setAddressDetail(map.get("address").toString());
				address.setAddressFormat("json");
				address.setArea(map.get("district").toString());//区
				//address.setAreaCode(areaCode)
				address.setCity(map.get("city").toString());
				address.setProvince(map.get("state").toString());
				TradeOrderInfo tradeOrderInfo = new TradeOrderInfo();
				tradeOrderInfo.setConsigneeName(map.get("linkman").toString());//
				tradeOrderInfo.setConsigneePhone(sendertele);
				tradeOrderInfo.setConsigneeAddress(address);
				tradeOrderInfo.setOrderChannelsType(OrderSource.findByValue((Integer)map.get("channelid")).name());//订单业源类型
				//交易订单列表
				List<String> tradeOrderList = new ArrayList<String>();
				tradeOrderList.add(map.get("sheetid").toString());
				tradeOrderInfo.setTradeOrderList(tradeOrderList);
				//
				//订单商品列表  包裹里面的商品类型
				List<PackageItem> packageList = new ArrayList<PackageItem>();
				for(Iterator t =item.iterator();t.hasNext(); ){
					DecOrderItem decOrderItem = (DecOrderItem)t.next();
					PackageItem itemTemp = new PackageItem();
					itemTemp.setCount(Long.valueOf(decOrderItem.getPurQty()));
					itemTemp.setItemName(decOrderItem.getTitle());
					packageList.add(itemTemp);
				}
				tradeOrderInfo.setPackageItems(packageList);
				tradeOrderInfo.setProductType("STANDARD_EXPRESS");//快递服务产品类型编码
				tradeOrderInfo.setRealUserId(Long.valueOf(params.get("user_id").toString()));//面单使用者id
				orderLists.add(tradeOrderInfo);
			}
			//发货人信息
			waybill_apply_new_request.setShippingAddress(senderAddress);
			//批量获取的只能是同一个快递公司
			waybill_apply_new_request.setCpCode(params.get("deliveryname").toString());//CP 快递公司编码
			waybill_apply_new_request.setRealUserId(Long.valueOf(params.get("user_id")));
			waybill_apply_new_request.setAppKey(params.get("app_key"));
			waybill_apply_new_request.setTradeOrderInfoCols(orderLists);//对应数据结构示例JSON
			req.setWaybillApplyNewRequest(waybill_apply_new_request);
			WlbWaybillIGetResponse response = client.execute(req , params.get("token"));
			System.out.println(req.getWaybillApplyNewRequest());
			System.out.println(response.getBody());
			HashMap<String,Object> resultMap = null;
			if(response.isSuccess()){
				List<WaybillApplyNewInfo> waybillApplyNewInfo =response.getWaybillApplyNewCols();
				for(Iterator it = waybillApplyNewInfo.iterator();it.hasNext();){
					WaybillApplyNewInfo temp = (WaybillApplyNewInfo)it.next();
					resultMap = new HashMap<String,Object>();
					resultMap.put("deliverysheetid",temp.getWaybillCode());
					resultMap.put("errcode",0);
					String sheetid = temp.getTradeOrderInfo().getTradeOrderList().get(0);
					Object[] o = getId(sheetid,mapList);
					String id = String.valueOf(o[0]);
					resultMap.put("deliveryid", o[1].toString());
					resultMap.put("state", o[2].toString());
					resultMap.put("city", o[3].toString());
					resultMap.put("district", o[4].toString());
					resultMap.put("address", o[5].toString());
					resultMap.put("linkman", o[6].toString());
					resultMap.put("tele", o[7].toString());
					currentMap.put(id,resultMap);
					
				}
				//System.out.println("大头笔信息: "+temp.getShortAddress()+"面单号: "+temp.getWaybillCode()+"目的地编码: "+temp.getPackageCenterCode());
				//returnMap.put("errcode", 0);
				//returnMap.put("deliverysheetid", temp.getWaybillCode());
			}else{
				resultMap = new HashMap<String,Object>();
				resultMap.put("errcode", 1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(latch!=null) latch.countDown();
		}
		
	}
	//查找decorder0订单表对应的id
	private Object[] getId(String sheetid,List<HashMap> mapList) throws Exception{
		Object[] result=new Object[8];
		for(Iterator it  = mapList.iterator();it.hasNext();){
			HashMap temp = (HashMap)it.next();
			String sheetidTemp = temp.get("sheetid").toString();
			if(sheetid.equals(sheetidTemp)){
				result[0] =(Integer)temp.get("id");
				result[1] =temp.get("deliveryid").toString();
				result[2] =temp.get("state").toString();
				result[3] =temp.get("city").toString();
				result[4] =temp.get("district").toString();
				result[5] =temp.get("address").toString();
				result[6] =temp.get("linkman").toString();
				result[7] =temp.get("tele").toString();
				/**
				 * param.put("State", map.get("state"));
					param.put("City", map.get("city"));
					param.put("District", map.get("district"));
					param.put("Address", map.get("address"));
					param.put("LinkMan", map.get("linkman"));							
					param.put("Mobile", map.get("tele"));
				 */
				break;
			}
		}
		return result;
	}

}
