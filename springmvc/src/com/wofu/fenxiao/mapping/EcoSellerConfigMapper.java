package com.wofu.fenxiao.mapping;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.EcoSellerConfig;

public interface EcoSellerConfigMapper extends BaseMapper<EcoSellerConfig>{
	//取得特定用户的下载时间列表
	public EcoSellerConfig qryTimeList(String sellernick)throws Exception;
	//更新用户的某个下载时间
	public void update(EcoSellerConfig sellerconfig) throws Exception;
	
}
