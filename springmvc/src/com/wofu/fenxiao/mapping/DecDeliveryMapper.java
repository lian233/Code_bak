package com.wofu.fenxiao.mapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.DecDelivery;

public interface DecDeliveryMapper  extends BaseService<DecDelivery>{

	//查询快递数据
	public List<DecDelivery> queryDelivery(DecDelivery d) throws Exception;
	
	//取得快递列表
	//输入{code,name}
	public List<HashMap> qryDeliveryList(HashMap<String,Object>  map)throws Exception;
	
	//增加客户快递单数量
	//{ customerID, deliveryID ,qty}
	public void addCustomerDeliveryNum(HashMap<String,Object>  map)throws Exception;
	
	//查询客户快递单剩余数量
	//{customerId, customerName , deliverycode}
	public List<HashMap> qryCustomerDeliveryNum(HashMap<String,Object>  map)throws Exception;

	//查询客户快递单剩余数量
	//{customerID, deliveryID}
	public List<HashMap> queryCustomerDeliveryNum(HashMap<String,Object>  map)throws Exception;
	
	//查询快递单记录流水
	//{customerID, deliveryID, beginTime,endTime}
	public List<HashMap> qryCustomerDeliveryNumBook(HashMap<String,Object>  map)throws Exception;
	
	//取得快递分组
	public List<HashMap> qryDeliveryGroupList(HashMap<String,String>  map)throws Exception;
	
	//选择快递
	public void tlChooseDecDelivery(HashMap<String,Object>  map)throws Exception;
	
	//增加快递区域
	public void addDecDeliveryZone(HashMap<String,Object>  map)throws Exception;
	
	//删除某个分组的快递区域
	public void deleteDecDeliveryZone(int deliveryGroupID)throws Exception;
	
	
	//取得快递编码（大头笔）
	public void tlGetDecDeliveryAddressID(HashMap<String, Object> map) throws Exception;
	//取快递公司信息
	public DecDelivery getByCode(String companyCode);

	//查询快递区域数据
	//{ DeliveryGroupID (int), DeliveryID (int), State, City, District,Status(int)}
	public List<HashMap> queryDecDeliveryZone(HashMap<String,Object>  map)throws Exception;
	
	//修改快递区域资料
	public void updateDecDeliveryZoneStatus(HashMap<String,Object>  map)throws Exception;

	//修改修改快递区域快递
	public void updateDecDeliveryZoneDelivery(HashMap<String,Object>  map)throws Exception;
	
	//快递使用统计
	public List<HashMap> stCustomerDeliveryNumSta(HashMap<String,Object>  map)throws Exception;
	
	//查询快递跟单汇总
	public List<HashMap> qryDeliveryTraceSta(HashMap<String,Object>  map)throws Exception;

	//查询快递跟单
	public List<HashMap> qryDeliveryTrace(HashMap<String,Object>  map)throws Exception;

	//快递单号回收
	public void tlReturnDelivery(HashMap<String,Object>  map)throws Exception;
	
}
