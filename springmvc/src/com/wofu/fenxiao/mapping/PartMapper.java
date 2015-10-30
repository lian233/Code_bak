package com.wofu.fenxiao.mapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.Part;

public interface PartMapper  extends BaseService<Part>{

	//查询角色数据
	public List<Part> queryPart(Part p) throws Exception;
	
	//取得帐号的角色
	public List<HashMap> queryPartMember(int loginId) throws Exception;
	
	//删除帐号所有的角色
	public void deletePartMember(int loginId) throws Exception;
	
	//增加帐号角色，map中定义了loginId 和 partId
	public void addPartMember(HashMap<String,String>  map)throws Exception;
	
	//删除所有的角色
	public void deleteAll();
}
