package com.wofu.fenxiao.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wofu.fenxiao.domain.Part;
import com.wofu.fenxiao.mapping.PartMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.PartService;
@Service("partService")//springmvc注解，这里会自动生成这个类的对象，由spring管理
public class PartServiceImpl implements PartService{
	@Autowired
	private PartMapper partMapper;
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})  //注解式事务处理  Exception异常会自动回滚
	@Override
	public void addPartMember(HashMap<String, String> map) throws Exception{
		partMapper.addPartMember(map);
		
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})
	@Override
	public void deletePartMember(int loginId) throws Exception{
		partMapper.deletePartMember(loginId);
		
	}
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Part> queryPart(Part p) throws Exception{
		return partMapper.queryPart(p);
	}
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<HashMap> queryPartMember(int loginId) throws Exception{
		return partMapper.queryPartMember(loginId);
	}
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public PageView query(PageView pageView, Part t) {
		// TODO Auto-generated method stub
		return null;
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})
	@Override
	public void add(Part t) throws Exception {
		partMapper.add(t);
		
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})
	@Override
	public void delete(int id) throws Exception {
		partMapper.delete(id);
		
	}
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Part getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Part> queryAll(Part t) throws Exception{
		return partMapper.queryAll(t);
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})
	@Override
	public void update(Part t) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})
	public void ss(Part t)throws Exception{
		deleteAll();
		/**
		 * 这样是不能回滚的，异常被捕获后，要向外抛出异常后，才能正常回滚
		try{
			int i=1/0;//
		}catch(Exception e){
			e.printStackTrace();
		}
		**/
		int i=1/0;
		add(t);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteAll() throws Exception{
		partMapper.deleteAll();
		
	}

}
