package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.Channel;
import com.wofu.fenxiao.domain.DecShop;

public interface ChannelMapper extends BaseMapper<Channel>{
	
	//取得渠道数据
	public Channel getChannelById(int channelid)throws Exception;

	
	
}
