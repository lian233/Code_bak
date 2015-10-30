package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.RefundSheet;

public interface RefundSheetMapper extends BaseMapper<RefundSheet>{
	

	//查询退货单数据
	public List<RefundSheet> queryRefundSheet(HashMap<String,Object>  map)throws Exception;

	public void updateRefundSheetSta(HashMap<String,Object>  map)throws Exception;
	
	//生成退货审批单
	public void ifRefundToCustomerRetNote(HashMap<String, Object> map) throws Exception;
	
	//查询退货单统计数据
	public List<HashMap> qryStaRefund(HashMap<String,Object>  map)throws Exception;
	
	//取消分销退货单
	public void tlCancelRefund(HashMap<String, Object> map) throws Exception;
	
}
