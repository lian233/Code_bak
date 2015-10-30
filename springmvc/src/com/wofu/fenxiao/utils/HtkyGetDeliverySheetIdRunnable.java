package com.wofu.fenxiao.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.best.ebill.client.EbillClient;
import com.best.ebill.client.impl.DefaultEbillClient;
import com.best.ebill.client.request.ebill.EbillDetail;
import com.best.ebill.client.request.ebill.EbillRequest;
import com.best.ebill.client.response.ebill.EbillResponse;
import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.DecOrderItem;

public class HtkyGetDeliverySheetIdRunnable implements Runnable{
	CountDownLatch latch =null;
	HashMap map = null;
	DecCustomer customer = null;
	ConcurrentHashMap<String,Object> currentMap=null;
	public HtkyGetDeliverySheetIdRunnable(CountDownLatch latch,HashMap map,DecCustomer customer
			,ConcurrentHashMap<String,Object> currentMap){
		this.map = map;
		this.latch=latch;
		this.customer=customer;
		this.currentMap= currentMap;
	}

	@Override
	public void run() {
		List<DecOrderItem> item =null; 
		HashMap<String,Object> returnMap=null;
		try{
			EbillClient dfc = new DefaultEbillClient(map.get("url").toString(), map.get("clientid").toString(),map.get("partnerkey").toString());
			returnMap = new HashMap<String,Object>();
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
			if(map.get("items").getClass()==List.class){
				item = (List<DecOrderItem>)map.get("items");
				
			}else{
				DecOrderItem tt = (DecOrderItem)map.get("items");
				item = new ArrayList<DecOrderItem>();
				item.add(tt);
				
			}
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
				System.out.println("取到汇通快递单号: "+outsid+"订单编号为:　"+map.get("sheetid").toString());
				returnMap.put("deliverysheetid", outsid);
				returnMap.put("errcode",0);
			}else{
				returnMap.put("errcode",1);
				returnMap.put("errmsg","");
				System.out.println("取汇通快递单号出错,订单编号: "+map.get("sheetid").toString());
			}
		}catch(Exception e){
			returnMap.put("errcode",1);
			returnMap.put("errmsg",e.getMessage());
		}finally{
			currentMap.put(String.valueOf(map.get("id")), returnMap);
			latch.countDown();
		}
		
		
	}

}
