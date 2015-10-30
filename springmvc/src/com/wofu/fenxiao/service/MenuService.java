package com.wofu.fenxiao.service;
import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.domain.Menu;
import com.wofu.fenxiao.pulgins.PageView;

public interface MenuService extends BaseService<Menu>{

	//取得帐号的菜单
	public List<HashMap> queryLoginMenu(int loginId) throws Exception;

}
