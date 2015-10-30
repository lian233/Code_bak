package com.wofu.fenxiao.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wofu.fenxiao.domain.DistributorPrice;
import com.wofu.fenxiao.mapping.DistributorPriceMapper;
import com.wofu.fenxiao.mapping.DecCustomerMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DistributorPriceService;

@Service("distributorPriceService")//springmvc注解，这里会自动生成这个类的对象，由spring管理


public class DistributorPriceServiceImpl  implements DistributorPriceService{
	@Autowired
	private DistributorPriceMapper distributorPriceMapper;

	@Autowired
	private DecCustomerMapper decCustomerMapper;
	
	@Override
	//设置客户状态
	public void setDistributorEnable(int customerID , int enable)throws Exception{
		HashMap<String,Object> map =  new HashMap<String,Object>();	
		map.put("CustomerID", customerID);
		map.put("Enable", enable);
		distributorPriceMapper.setDistributorEnable(map);
		
	}
	
	@Override
	//设置客户状态
	public void setDistributorsEnable(int[] customers , int enable)throws Exception{
		for(int i = 0 ; i<customers.length ;i++){
			HashMap<String,Object> map =  new HashMap<String,Object>();	
			map.put("CustomerID", customers[i]);
			map.put("Enable", enable);
			distributorPriceMapper.setDistributorEnable(map);
		}				
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	//保存分销商
	public int saveDistributor(int customerID , int parentID , 
		String name , String state , String city , String district , String address , 
		String linkMan , String linkTele , String mobileNo , String note)throws Exception{
		HashMap<String,Object> map =  new HashMap<String,Object>();	
		
		//检查是否存在
		HashMap<String,String> p = new HashMap<String,String>();
		p.put("name", name);
		List<HashMap> r = decCustomerMapper.qryDCustomerList(p);
		
		if (r.size()>=1){
			if (customerID < 0){
				throw new Exception ("分销商【"+name+"】已存在");
			}
			else{
				HashMap h = r.get(0);
				int id = Integer.parseInt(h.get("ID").toString());
				if (id != customerID){
					throw new Exception ("分销商【"+name+"】已存在");
				}				
			}
		}
		
		map.put("CustomerID", customerID);
		map.put("ParentID", parentID);
		map.put("Name", name);
		map.put("State", state);
		map.put("City", city);
		map.put("District", district);
		map.put("Address", address);
		map.put("LinkMan", linkMan);
		map.put("LinkTele", linkTele);
		map.put("MobileNo", mobileNo);
		map.put("Note", note);
				
		distributorPriceMapper.tlSaveDistributor(map);
		return Integer.parseInt(map.get("NewID").toString()) ;
		
	}
	
	//增加分销商价格
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override	
	public void addDistributorPrice(DistributorPrice t,String editor)throws Exception{
		distributorPriceMapper.add(t);
		
		HashMap<String,Object>  map = new HashMap<String,Object>();
		map.put("Operator", editor);
		map.put("OperType", 1);
		map.put("ID", t.getID());
		distributorPriceMapper.addDistributorPriceLog(map);
	}
	
	//修改分销商价格
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void updateDistributorPrice(DistributorPrice t,String editor)throws Exception{
		distributorPriceMapper.update(t);

		HashMap<String,Object>  map = new HashMap<String,Object>();
		map.put("Operator", editor);
		map.put("OperType", 2);
		map.put("ID", t.getID());
		distributorPriceMapper.addDistributorPriceLog(map);		
	}
	
	//删除分销商价格
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void delDistributorPrice(int id,String editor)throws Exception{
		HashMap<String,Object>  map = new HashMap<String,Object>();
		map.put("Operator", editor);
		map.put("OperType", 3);
		map.put("ID", id);
		distributorPriceMapper.addDistributorPriceLog(map);
		
		distributorPriceMapper.delete(id);
	}

	
	@Override
	public PageView query(PageView pageView, DistributorPrice t)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(DistributorPrice t) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int id) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DistributorPrice getById(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DistributorPrice> queryAll(DistributorPrice t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(DistributorPrice t) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	


	

}
