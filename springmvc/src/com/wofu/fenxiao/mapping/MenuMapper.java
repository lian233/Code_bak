package com.wofu.fenxiao.mapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.Menu;


public interface MenuMapper extends BaseService<Menu> {

	//取得帐号的菜单
	public List<HashMap> queryLoginMenu(int loginId) throws Exception;

}
