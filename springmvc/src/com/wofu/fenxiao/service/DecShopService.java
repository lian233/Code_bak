package com.wofu.fenxiao.service;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.DecShop;

import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.pulgins.PageView;


public interface DecShopService extends BaseService<DecShop> {


	//取得渠道数据列表
	public List<HashMap> qryChannelList(HashMap<String,String>  map)throws Exception;

	//查询店铺资料
	public List<HashMap> qryShop(HashMap<String,Object>  map)throws Exception;
	public List<HashMap> qryDShop(HashMap<String,Object>  map)throws Exception;
	
	//取得店铺列表
	//输入{customerID,name}
	public List<HashMap> qryShopList(HashMap<String,Object>  map)throws Exception;
	//添加商铺资料
	public void addshop(DecShop t,String rds_name,int extdsid) throws Exception;
	//取得客户的店铺列表
	public List<HashMap> qryCustomerShopList(int customerID)throws Exception;
	
	//生成店铺编码
	public String MakeShopCode(int customerID)throws Exception;
	//
	//获取网站token
	public String getToken(int shopid, int channelid, String appkey, String app_secret, String getTokenLink, int extdsid, String rds_name) throws Exception;
	//更新网店参数
	public void updateshop(DecShop c,String rds_name,int extdsid)throws Exception;
	
	@Override
	public void add(DecShop t) throws Exception;
	
}
