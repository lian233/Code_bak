package com.wofu.fenxiao.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wofu.fenxiao.domain.DecDelivery;
import com.wofu.fenxiao.mapping.DecDeliveryMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecDeliveryService;

@Service("deliveryService")//springmvc注解，这里会自动生成这个类的对象，由spring管理

public class DecDeliveryServiceImpl implements DecDeliveryService{
	@Autowired
	private DecDeliveryMapper deliveryMapper;
	
	Logger logger = Logger.getLogger(this.getClass());
	
	//查询快递数据
	@Override
	public List<DecDelivery> queryDelivery(DecDelivery d) throws Exception{
		return deliveryMapper.queryDelivery(d);
	}
	
	//取得快递列表
	//输入{code,name}
	@Override
	public List<HashMap> qryDeliveryList(HashMap<String,Object>  map)throws Exception{
		if (map==null){
			map = new HashMap<String,Object>();
		}
		return deliveryMapper.qryDeliveryList(map);
	}
	
	//增加客户快递单数量
	//{ customerID, deliveryID ,qty}
	@Override
	public String addCustomerDeliveryNum(HashMap<String,Object>  map)throws Exception{
		deliveryMapper.addCustomerDeliveryNum(map);
		return map.get("OutMsg").toString();
	}
	
	//查询客户快递单剩余数量
	//{customerId, customerName , deliverycode}
	@Override
	public List<HashMap> qryCustomerDeliveryNum(HashMap<String,Object>  map)throws Exception{
		return deliveryMapper.qryCustomerDeliveryNum(map) ;
	}
	
	//查询快递单记录流水
	//{customerID, deliveryID, beginTime,endTime}
	@Override
	public List<HashMap> qryCustomerDeliveryNumBook(HashMap<String,Object>  map)throws Exception{
		return deliveryMapper.qryCustomerDeliveryNumBook(map);
	}
	
	//取得快递分组
	@Override
	public List<HashMap> qryDeliveryGroupList(HashMap<String,String>  map)throws Exception{
		return deliveryMapper.qryDeliveryGroupList(map);
	}


	@Override
	public PageView query(PageView pageView, DecDelivery t) throws Exception {
		// TODO Auto-generated method stub
		return deliveryMapper.query(pageView, t);
	}

	@Override
	public void add(DecDelivery t) throws Exception {
		// TODO Auto-generated method stub
		deliveryMapper.add(t);
	}

	@Override
	public void delete(int id) throws Exception {
		// TODO Auto-generated method stub
		deliveryMapper.delete(id);
		
	}

	@Override
	public DecDelivery getById(int id) throws Exception {
		// TODO Auto-generated method stub
		return deliveryMapper.getById(id);
	}

	@Override
	public List<DecDelivery> queryAll(DecDelivery t) throws Exception {
		// TODO Auto-generated method stub
		return deliveryMapper.queryAll(t);
	}

	@Override
	public void update(DecDelivery t) throws Exception {
		// TODO Auto-generated method stub
		deliveryMapper.update(t);
	}

	@Override
	public int chooseDecDelivery(String state, String city, String district,
			String address, int shopID, int payMode, int customerID) throws Exception {		
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("InState", state);
		map.put("InCity", city);
		map.put("InDistrict", district);
		map.put("InAddress", address);
		map.put("InShopID", shopID);
		map.put("PayMode", payMode);
		map.put("CustomerID", customerID);
		map.put("InState", state);
		
		deliveryMapper.tlChooseDecDelivery(map);
		return (Integer)map.get("OutDeliveryID") ;
		
	}

	//取得快递编码（大头笔）
	@Override
	public String getDecDeliveryAddressID(String deliveryID , String state , 
			String city, String district) throws Exception {		
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("DeliveryID", deliveryID);
		map.put("State", state);
		map.put("City", city);
		map.put("District", district);
		
		deliveryMapper.tlGetDecDeliveryAddressID(map);
		return map.get("AddressID").toString() ;
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void setDecDeliveryZone(int deliveryGroupID, ArrayList list) throws Exception {
		deliveryMapper.deleteDecDeliveryZone(deliveryGroupID);
		
		for(int i=0 ; i < list.size() ; i++){	
			HashMap<String, Object> params = (HashMap<String, Object>) list.get(i);
			logger.info("取得数据："+params.get("state"));
			deliveryMapper.addDecDeliveryZone(params);
		}
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public String returnDelivery(int deliveryID, String deliverySheetID) throws Exception {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("DeliveryID", deliveryID);
		map.put("DeliverySheetID", deliverySheetID);
		
		deliveryMapper.tlReturnDelivery(map);
		return map.get("Msg").toString();
		
	}
	
	

}
