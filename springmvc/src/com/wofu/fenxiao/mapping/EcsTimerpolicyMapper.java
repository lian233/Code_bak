package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.EcsTimerpolicy;

public interface EcsTimerpolicyMapper extends BaseMapper<EcsTimerpolicy>{
	
	//查询某条记录
	public int qryByParams(@Param("executer") String executer,@Param("params")String params)throws Exception;
	//查询最大记录
	public int qryMaxRecord()throws Exception;
	


	
}
