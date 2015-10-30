package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.Channel;
import com.wofu.fenxiao.domain.DecShop;

public interface DecShopMapper extends BaseMapper<DecShop>{
	
	//取得渠道数据列表
	public List<HashMap> qryChannelList(HashMap<String,String>  map)throws Exception;

	//查询店铺资料
	public List<HashMap> qryShop(HashMap<String,Object>  map)throws Exception;
	
	public List<HashMap> qryDShop(HashMap<String,Object>  map)throws Exception;
	
	//取得店铺列表
	public List<HashMap> qryShopList(HashMap<String,Object>  map)throws Exception;
	
	//生成店铺编码
	public void tlMakeShopCode(HashMap<String, Object> map)throws Exception;
	//
	//获取特定的channelcode
	public Channel getChannelById(int channelid);
	//更新token
	public void updateToken(@Param("token") String token,@Param("refreshtoken") String refreshtoken,@Param("id") int id,@Param("user_id") String user_id);
	//取得商品资料
	public List<HashMap> qryDecItem(HashMap<String,Object>  map)throws Exception;
	
	
	//更新商品资料
	public void updateDecItem(HashMap<String, Object> map)throws Exception;
	
}
