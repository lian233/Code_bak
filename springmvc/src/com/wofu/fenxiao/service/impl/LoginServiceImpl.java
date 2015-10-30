package com.wofu.fenxiao.service.impl;
/**
 * 帐户服务实现类
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.domain.PartMember;
import com.wofu.fenxiao.mapping.LoginMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.LoginService;
@Transactional//开启事务管理
@Service("loginService")
public class LoginServiceImpl implements LoginService{
	@Autowired//自动注册mapping组件  这里也是接口
	private LoginMapper loginService;

	public PageView query(PageView pageView, Login account)throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("paging", pageView);
		map.put("t", account);
		List<Login> list = loginService.query(map);
		pageView.setRecords(list);
		return pageView;
	}
	
	
	public PageView queryNoMatch(Login account,PageView pageView)throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("paging", pageView);
		map.put("t", account);
		List<Login> list = loginService.queryNoMatch(map);
		pageView.setRecords(list);
		return pageView;
	}
	
	public List<Login> queryAll(Login t)throws Exception {
		// TODO Auto-generated method stub
		return loginService.queryAll(t);
	}

	public void delete(int id) throws Exception {
		loginService.delete(id);
	}

	public void update(Login t) throws Exception {
		loginService.update(t);
	}

	public Login getById(int id)throws Exception {
		return loginService.getById(id);
	}
	
	

	
	public void add(Login t) throws Exception {
		loginService.add(t);
	}

	
	public Login querySingleAccount(String accountName) {
		return loginService.querySingleAccount(accountName);
	}

	
	public Login isExist(String accountName) {
		return loginService.isExist(accountName);
	}


	@Override
	public Login countAccount(Login account) {
		// TODO Auto-generated method stub
		return null;
	}



	//要据条件查询
	@Override
	public Login queryByName(String name, String password)throws Exception {
		return loginService.queryByName(name,password);
	}

	//调用存储过程
	
	public void callable(HashMap<String,String> map) throws Exception{
		// TODO Auto-generated method stub
		loginService.test(map);
	}

	//调用有return返回值 的存储过程
	@Override
	public void callablehasReturn(HashMap<String, String> map) throws Exception{
		loginService.testhasReturn(map);
		
	}

	//返回一个hashmap数据结构
	@Override
	public List<HashMap> returnHashMap(HashMap<String, String> map)throws Exception {
		return loginService.returnHashMap(map);
		
	}

	//把用户加入指定的角色中
	@Override
	public void saveRelativity(PartMember partMember) throws Exception {
		loginService.saveRelativity(partMember);
		
	}
	
	//取得帐号的角色
	@Override
	public List<HashMap> qryLogin(HashMap<String,Object>  map)throws Exception{
		return loginService.qryLogin(map);
	}

	@Override
	public List<HashMap> qryDLogin(HashMap<String,Object>  map)throws Exception{
		return loginService.qryDLogin(map);
	}

	@Override
	public void tlGetNewSerial(HashMap<String, Object> map) throws Exception {
		loginService.tlGetNewSerial(map);
		
	}
	
	//取得新的ID数据
	@Override
	public int GetNewID(int serialID) throws Exception{
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();
			map.put("SerialID", serialID);//
			tlGetNewSerial(map);
			
			if ((Integer)map.get("c")==0){
				return (Integer)map.get("Value");			
			}
			else{
				throw new Exception("取新增ID出错"); 						
			}
		}catch(Exception e){
			throw new Exception("取新增ID出错:"+e.getMessage()); 
		}
		
	}


	@Override
	public List<HashMap> qryLoginOnPage(HashMap<String, Object> map) throws Exception {
		return loginService.qryLoginOnPage(map);
	}


	@Override
	public int qryLoginCount(HashMap<String, Object> map)throws Exception {
		return loginService.qryLoginCount(map);
	}


	@Override
	public String GetNewSheetID(int sheetType) throws Exception {
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();
			map.put("SheetType", sheetType);//
			tlGetNewDecSheetID(map);
			
			if ((Integer)map.get("err")==0){
				return (String)map.get("SheetID");			
			}
			else{
				throw new Exception("取新单号出错"); 						
			}
		}catch(Exception e){
			throw new Exception("取新单号出错:"+e.getMessage()); 
		}	}


	@Override
	public void tlGetNewDecSheetID(HashMap<String, Object> map) throws Exception {
		loginService.tlGetNewDecSheetID(map);
		
	}


	@Override
	public String GetCustomerConfig(String name, String defaultValue, int customerID, int subSystemID) throws Exception {
		HashMap<String,Object> map = new HashMap<String,Object> ();
		map.put("Name", name);
		map.put("DefaultValue", defaultValue);
		map.put("CustomerID", customerID);
		map.put("SubSystemID", subSystemID);
		
		return loginService.tlGetCustomerConfig(map);
				
	}	
	
	//设置SKU替换值
	@Override
	public void SetCustomerConfig(String name, String setValue, int customerID, int subSystemID)throws Exception{
		HashMap<String,Object> map = new HashMap<String,Object> ();
		map.put("Name", name);
		map.put("SetValue", setValue);
		map.put("CustomerID", customerID);
		map.put("SubSystemID", subSystemID);
		
		loginService.tlSetCustomerConfig(map);
		
	}
	
	

}	
