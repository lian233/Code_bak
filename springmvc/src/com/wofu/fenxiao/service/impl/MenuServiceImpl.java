package com.wofu.fenxiao.service.impl;
/**
 * 帐户服务实现类
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.wofu.fenxiao.domain.Menu;
import com.wofu.fenxiao.mapping.MenuMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.MenuService;



@Transactional//开启事务管理
@Service("menuService")
public class MenuServiceImpl implements MenuService{
	@Autowired//自动注册mapping组件  这里也是接口
	private MenuMapper menuMapper;
	
	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public List<HashMap> queryLoginMenu(int loginId) throws Exception {
		//ArrayList 
		List<HashMap> list= menuMapper.queryLoginMenu(loginId);
		ArrayList ret = new ArrayList();
		for(int i=0;i<list.size();i++){			
			HashMap<String, Object> m = list.get(i);
			
			boolean find = false;
			//找是否存在
			for(int j=0;j<ret.size();j++){
				HashMap<String, Object> f = (HashMap<String, Object>) ret.get(j);
				if (f.get("caption").toString().equals(m.get("mCaption").toString())){
					logger.info("找到原来的对象:"+f.get("caption").toString());
					ArrayList cs = (ArrayList)f.get("childs");
					
					HashMap<String, Object> ci = new HashMap<String, Object> ();
					ci.put("caption", m.get("Caption").toString());
					ci.put("url", m.get("Url").toString());
					ci.put("moduleID", Integer.parseInt(m.get("ModuleID").toString()));
					
					cs.add(ci);	
					find = true;
					break;
				}
			}
			
			if (find){continue;}
			
			HashMap<String, Object> item = new HashMap<String, Object> ();
			
			logger.info("菜单对象：" + m.get("mCaption").toString());
			item.put("caption", m.get("mCaption").toString());
			item.put("moduleID", Integer.parseInt(m.get("MModuleID").toString()));
			if (m.get("Caption").toString().equals("")){
				item.put("url", m.get("Url").toString());								
			}else {
				//增加
				ArrayList childs = new ArrayList();
				HashMap<String, Object> ci = new HashMap<String, Object> ();
				ci.put("caption", m.get("Caption").toString());
				ci.put("url", m.get("Url").toString());
				ci.put("moduleID", Integer.parseInt(m.get("ModuleID").toString()));
				childs.add(ci);
				item.put("childs", childs);
			}		
			
			ret.add(item);
		}
		
		logger.info("菜单对象：" + ret.toString()); 
		return ret;
	}

	@Override
	public PageView query(PageView pageView, Menu t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Menu t) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int id) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Menu getById(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Menu> queryAll(Menu t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Menu t) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	

}	
