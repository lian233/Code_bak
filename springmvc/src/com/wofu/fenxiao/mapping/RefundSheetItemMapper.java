package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.RefundSheetItem;

public interface RefundSheetItemMapper extends BaseMapper<RefundSheetItem>{
	

	//查询退货单数据
	public List<RefundSheetItem> qryRefundSheetItem(HashMap<String,Object>  map)throws Exception;

	
}
