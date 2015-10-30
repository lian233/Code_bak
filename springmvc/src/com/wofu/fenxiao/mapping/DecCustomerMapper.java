package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.DecCustomer;

public interface DecCustomerMapper extends BaseMapper<DecCustomer>{
	
	//取得客户数据列表
	public List<HashMap> qryCustomerList(HashMap<String,String>  map)throws Exception;
	
	public List<HashMap> qryDCustomerList(HashMap<String,String>  map)throws Exception;
	
	public DecCustomer getByDId(int id)throws Exception;

	//查询客户资料
	public List<DecCustomer> qryCustomer(HashMap<String,Object>  map)throws Exception;
	
	//取得客户分组
	public List<HashMap> qryCustomerGroupList(HashMap<String,Object>  map)throws Exception;
	
	//生成客户编码
	public void tlMakeCustomerCode(HashMap<String, Object> map)throws Exception;

	//查询问题
	public List<HashMap> qryCustomerService(HashMap<String,Object>  map)throws Exception;

	//增加客户问题
	public void addCustomerService(HashMap<String, Object> map)throws Exception;

	//修改客户问题
	public void updateCustomerService(HashMap<String, Object> map)throws Exception;
	
	//删除客户问题
	public void deleteCustomerService(int id)throws Exception;

	//修改自定义打印内容
	public void updateCustomerPrintContent(HashMap<String, Object> map)throws Exception;

	//查询自定义打印内容
	public List<HashMap> qryCustomerPrintContent(int customerID)throws Exception;
	
}
