package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.domain.DecOrderItem;

public interface DecOrderItemMapper extends BaseMapper<DecOrderItem>{
	
	//查询订单明细数据
	public List<DecOrderItem> qryDecOrderItem(HashMap<String,Object>  map)throws Exception;
	
	//删除明细
	public void delete2(DecOrderItem t)throws Exception;

	//查询单个订单明细数据
	public DecOrderItem getByObj(HashMap<String,Object>  map)throws Exception;
	
}
