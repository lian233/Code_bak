package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.domain.DecOrderItem;

public interface DecOrderMapper extends BaseMapper<DecOrder>{
	
	//查询订单数据
	public List<DecOrder> qryDecOrder(HashMap<String,Object>  map)throws Exception;

	//根据ID列表查询订单数据
	public List<DecOrder> qryDecOrderList(HashMap<String,Object>  map)throws Exception;
	
	//确认订单
	//输入：调用的参数
	public void confirmDecOrder(HashMap<String, Object> map)throws Exception;

	//订单发货
	//输入：订单列表
	public void sendDecOrder(HashMap<String, Object> map)throws Exception;
	
	//订单合并
	public void mergeDecOrder(HashMap<String, Object> map)throws Exception;
	
	//审核订单
	public void stDecOrder(HashMap<String, Object> map) throws Exception;

	//取消订单
	public void tlBakDecOrder(HashMap<String, Object> map) throws Exception;

	//分销取消订单
	public void tlStopDecOrder(HashMap<String, Object> map) throws Exception;

	//审核订单
	public void tlCheckDecOrder(HashMap<String, Object> map) throws Exception;
	
	//修改订单状态
	public void tlModifyDecOrderFlag(HashMap<String, Object> map) throws Exception;
	
	//合并订单
	public void tlMergeDecOrderAuto(HashMap<String, Object> map) throws Exception;
	//取得要确认的订单的flag , deliveryid ,deliverysheetid
	public List<HashMap> getDeliveryOrder(@Param("ids") Object[] ids,@Param("tableprefix") String tableprefix);
	//确定订单时更新快递信息
	public void updateDeliveryInfo(HashMap<String, Object> returnMap);
	//调用存储过程统计快递
	public void CustomerDecDeliveryBook(HashMap param);
	
	//删除
	public void delete2(DecOrder t)throws Exception;
	
	//设置订单的明细信息
	public void tlSetDecKeyPicNote(HashMap<String, Object> map) throws Exception;
	
	//更新打印次数
	public void updatePrintTimes(HashMap<String, Object> map)throws Exception;

	//修改快递
	public void modifyDelivey(HashMap<String, Object> map)throws Exception;
	
	//更新发货状态
	public void updateOutFlag(HashMap<String, Object> map)throws Exception;
	
	//查询单个订单数据
	public DecOrder getByObj(HashMap<String,Object>  map)throws Exception;
	//取得要同步发货状态的订单信息
	public List<HashMap<String, Object>> getSendOrders(@Param("ids")Object[] ids,
			@Param("tableprefix") String tableprefix)throws Exception;
		
	//查询订单明细统计数据
	public List<HashMap<String, Object>> qryStaDecOrderSku(HashMap<String, Object> map)throws Exception;
		
	//查询订单明细统计数据
	public List<HashMap> qryStaDecOrderSkuList(HashMap<String, Object> map)throws Exception;
	

	//查询订单统计数据
	public List<HashMap> qryStaDecOrder(HashMap<String, Object> map)throws Exception;

	//商品销售统计
	public List<HashMap> qryGoodsSta(HashMap<String, Object> map)throws Exception;

	//日销售统计
	public List<HashMap> qryDaysSta(HashMap<String, Object> map)throws Exception;
	
	//客户的基本统计数据
	public List<HashMap> stGetCustomerBaseSta(HashMap<String,Object>  map)throws Exception;
	
	
}
