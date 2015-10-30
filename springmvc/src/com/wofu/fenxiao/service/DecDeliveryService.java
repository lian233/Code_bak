package com.wofu.fenxiao.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.DecDelivery;
import com.wofu.fenxiao.pulgins.PageView;

public interface DecDeliveryService extends BaseService<DecDelivery> {

	//查询快递数据
	public List<DecDelivery> queryDelivery(DecDelivery d) throws Exception;
	
	//取得快递列表
	//输入{code,name}
	public List<HashMap> qryDeliveryList(HashMap<String,Object>  map)throws Exception;
	
	
	
	//增加客户快递单数量
	//{ customerID, deliveryID ,qty}
	public String addCustomerDeliveryNum(HashMap<String,Object>  map)throws Exception;
	
	//查询客户快递单剩余数量
	//{customerId, customerName , deliverycode}
	public List<HashMap> qryCustomerDeliveryNum(HashMap<String,Object>  map)throws Exception;
	
	//查询快递单记录流水
	//{customerID, deliveryID, beginTime,endTime}
	public List<HashMap> qryCustomerDeliveryNumBook(HashMap<String,Object>  map)throws Exception;
		
	//取得快递分组
	public List<HashMap> qryDeliveryGroupList(HashMap<String,String>  map)throws Exception;
	
	//选择快递
	public int chooseDecDelivery(String state , String city , String district , String address , int shopID , int payMode , int customerID)throws Exception;
	
	//设置快递区域
	public void setDecDeliveryZone(int deliveryGroupID , ArrayList list) throws Exception; 

	//取得快递编码（大头笔）
	public String getDecDeliveryAddressID(String deliveryID , String state , 
			String city, String district) throws Exception ;	
	
	//回收快递单号
	public String returnDelivery(int deliveryID , String deliverySheetID ) throws Exception; 
	
}
