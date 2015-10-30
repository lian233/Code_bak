package com.wofu.fenxiao.service;
import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.DistributorPrice;
import java.util.HashMap;
import java.util.List;
import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.pulgins.PageView;


public interface DistributorPriceService extends BaseService<DistributorPrice> {

	//设置客户状态
	public void setDistributorEnable(int customerID , int enable)throws Exception;
	
	//设置客户状态
	public void setDistributorsEnable(int[] customers , int enable)throws Exception;
	
	//保存分销商
	public int saveDistributor(int customerID , int parentID , 
		String name , String state , String city , String district , String address , 
		String linkMan , String linkTele , String mobileNo , String note)throws Exception;
	

	//增加分销商价格
	public void addDistributorPrice(DistributorPrice t,String editor)throws Exception;
	
	//修改分销商价格
	public void updateDistributorPrice(DistributorPrice t,String editor)throws Exception;
	
	//删除分销商价格
	public void delDistributorPrice(int id,String editor)throws Exception;
	
	
}
