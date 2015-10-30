package com.wofu.fenxiao.service;
import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.DecCustomer;
import java.util.HashMap;
import java.util.List;
import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.pulgins.PageView;
public interface DecCustomerService extends BaseService<DecCustomer> {

	//取得客户数据列表
	public List<HashMap> qryCustomerList(HashMap<String,String>  map)throws Exception;
	public List<HashMap> qryDCustomerList(HashMap<String,String>  map)throws Exception;

	//查询客户资料
	public List<DecCustomer> qryCustomer(HashMap<String,Object>  map)throws Exception;
	
	public DecCustomer getByDId(int id)throws Exception;
	
	//取得客户分组
	public List<HashMap> qryCustomerGroupList(HashMap<String,Object>  map)throws Exception;

	//生成客户编码
	public String MakeCustomerCode()throws Exception;
	
	
	
}
