package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.DecDelivery;
import com.wofu.fenxiao.domain.DistributeGoods;

public interface DistributeGoodsMapper extends BaseMapper<DistributeGoods>{
	
	//查询分销商品
	public List<HashMap> queryDistributeGoods(HashMap<String,Object>  map)throws Exception;

	//客户查询分销商品
	public List<HashMap> queryCustomerDistributeGoods(HashMap<String,Object>  map)throws Exception;
	
	//查询线条
	public List<HashMap> qryProductLine(HashMap<String,Object>  map)throws Exception;
	
	//取得品牌
	public List<HashMap> qryBrand(HashMap<String,Object>  map)throws Exception;

	//查商品资料
	public List<HashMap> queryGoods(HashMap<String,Object>  map)throws Exception;
	
	//取分销商品资料
	public DistributeGoods getById(HashMap<String, Object> map);
	
	//取客户等级
	public  List<HashMap> qryGrade(HashMap<String, Object> map);
	
	//删除分销商品
	public void delete(HashMap<String, Object> map)throws Exception;
	
	//查询商品SKU库存
	public List<HashMap> querySkuInventory(HashMap<String,Object>  map)throws Exception;
	
	//查询分销客户分销产品线
	public List<HashMap> queryCustomerProductGrade(HashMap<String,Object>  map)throws Exception;

	//客户加入分销 
	public void addCustomerProductGrade(HashMap<String, Object> map)throws Exception;
	
	//客户退出分销 
	public void removeCustomerProductGrade(HashMap<String, Object> map)throws Exception;

	//修改分销商品图片链接 
	public void updateImaUrl(HashMap<String, Object> map)throws Exception;
	
	//修改分销商品状态
	public void updateStatus(HashMap<String, Object> map)throws Exception;
	
	
}
