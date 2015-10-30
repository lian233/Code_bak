package com.wofu.fenxiao.service;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.DistributeGoods;

import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.pulgins.PageView;


public interface DistributeGoodsService extends BaseService<DistributeGoods> {

	//查询分销商品
	public List<HashMap> queryDistributeGoods(HashMap<String,Object>  map)throws Exception;

	//查询线条
	public List<HashMap> qryProductLine(HashMap<String,Object>  map)throws Exception;
	
	//取得品牌
	public List<HashMap> qryBrand(HashMap<String,Object>  map)throws Exception;
	
	//删除分销商品
	public void delete(HashMap<String, Object> map)throws Exception;
	
	//查询商品SKU库存
	public List<HashMap> querySkuInventory(HashMap<String,Object>  map)throws Exception;
	
	
	
}
