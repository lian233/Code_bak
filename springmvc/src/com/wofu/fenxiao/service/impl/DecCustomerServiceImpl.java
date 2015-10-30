package com.wofu.fenxiao.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.mapping.DecCustomerMapper;
import com.wofu.fenxiao.mapping.DecDeliveryMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecCustomerService;
@Service("decCustomerService")//springmvc注解，这里会自动生成这个类的对象，由spring管理


public class DecCustomerServiceImpl  implements DecCustomerService{
	@Autowired
	private DecCustomerMapper decCustomerMapper;
	@Autowired
	private DecDeliveryMapper decDeliveryMapper;
	
	@Override
	//取得客户列表
	public List<HashMap> qryCustomerList(HashMap<String,String>  map)throws Exception{
		return decCustomerMapper.qryCustomerList(map);
	}

	@Override
	//取得客户列表
	public List<HashMap> qryDCustomerList(HashMap<String,String>  map)throws Exception{
		return decCustomerMapper.qryDCustomerList(map);
	}
	
	//查询客户资料
	@Override
	public List<DecCustomer> qryCustomer(HashMap<String,Object>  map)throws Exception{
		return decCustomerMapper.qryCustomer(map);
	}
	
	//取得客户分组
	public List<HashMap> qryCustomerGroupList(HashMap<String,Object>  map)throws Exception{
		return decCustomerMapper.qryCustomerGroupList(map);
	}

	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void add(DecCustomer t) throws Exception {
		decCustomerMapper.add(t);
		
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void delete(int id) throws Exception {
		decCustomerMapper.delete(id);
		
	}


	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void update(DecCustomer t) throws Exception {
		decCustomerMapper.update(t);
		
	}

	//生成客户编码
	@Override
	public String MakeCustomerCode() throws Exception {
		String ret = "";
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		decCustomerMapper.tlMakeCustomerCode(map);
		if ((Integer)map.get("err")==0){
			ret = (String)map.get("Code");			
		}
		else{
			throw new Exception("生成客户编码"); 						
		}		
		
		return ret;
	}

	@Override
	public PageView query(PageView pageView, DecCustomer t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DecCustomer getById(int id) throws Exception {
		// TODO Auto-generated method stub
		return decCustomerMapper.getById(id);
	}
	
	@Override
	public DecCustomer getByDId(int id) throws Exception {
		// TODO Auto-generated method stub
		return decCustomerMapper.getByDId(id);
	}

	@Override
	public List<DecCustomer> queryAll(DecCustomer t) throws Exception {
		// TODO Auto-generated method stub
		return decCustomerMapper.queryAll(t);
	}

	
	


	

}
