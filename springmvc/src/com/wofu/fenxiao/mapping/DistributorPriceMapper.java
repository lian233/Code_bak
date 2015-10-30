package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.DistributorPrice;


public interface DistributorPriceMapper extends BaseMapper<DistributorPrice>{
	
	//取得渠道数据
	public void setDistributorEnable(HashMap<String,Object>  map)throws Exception;

	//查询多级分销商资料
	public List<HashMap> qrySubDistributor(HashMap<String,Object>  map)throws Exception;
	
	// 保存分销商资料
	public void tlSaveDistributor(HashMap<String, Object> map)throws Exception;
	
	//查询分销价格 
	public List<HashMap> qryDistributorPrice(HashMap<String, Object> map)throws Exception;

	//查询分销价格历史 
	public List<HashMap> qryDistributorPriceLog(HashMap<String, Object> map)throws Exception;
	
	public List<HashMap> qryAllDistributorPrice(HashMap<String, Object> map)throws Exception;
	
	//增加价格历史
	public void addDistributorPriceLog(HashMap<String,Object>  map)throws Exception;
	
}
