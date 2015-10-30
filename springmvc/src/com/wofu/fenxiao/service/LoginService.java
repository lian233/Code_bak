package com.wofu.fenxiao.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.domain.PartMember;
import com.wofu.fenxiao.pulgins.PageView;

//帐户服务接口类
public interface LoginService extends BaseService<Login>{
	public Login querySingleAccount(String accountName);
	public Login isExist(String accountName);
	/**
	 * 验证用户登陆

	 * @return
	 */
	public Login countAccount(Login account);
	public Login queryByName(String name,String pasword) throws Exception;
	
	/**
	 * @param account
	 * @param pageView
	 * @return
	 */
	public PageView queryNoMatch(Login account,PageView pageView)throws Exception;
	public List<Login> queryAll(Login account) throws Exception;
	//调用存储过程，这个是没有return参数的
	public void callable(HashMap<String,String>  map) throws Exception;
	//用户加权限
	public void saveRelativity(PartMember partMember) throws Exception;
	
	//调用存储过程，这个是有return参数的
	public void callablehasReturn(HashMap<String,String>  map)throws Exception;
	
	
	//返回多行数据list<map>
	public List<HashMap> returnHashMap(HashMap<String,String>  map)throws Exception;
	
	//取得帐号的角色
	public List<HashMap> qryLogin(HashMap<String,Object>  map)throws Exception;
	public List<HashMap> qryDLogin(HashMap<String,Object>  map)throws Exception;
	
	//取得ID值
	public void tlGetNewSerial(HashMap<String, Object> map)throws Exception;

	//取得SheetID值
	public void tlGetNewDecSheetID(HashMap<String, Object> map)throws Exception;
	
	//取得新的ID
	public int GetNewID(int serialID) throws Exception;
	
	//取得新的单号
	public String GetNewSheetID(int sheetType) throws Exception;

	//取客户配置
	public String GetCustomerConfig(String name , String defaultValue , int customerID , int subSystemID) throws Exception;
	
	//分页取得帐号数据
	public List<HashMap> qryLoginOnPage(HashMap<String,Object>  map)throws Exception;
	
	//取得查询的帐号数据数量
	public int qryLoginCount(HashMap<String,Object>  map)throws Exception;
	
	//设置SKU替换值
	public void SetCustomerConfig(String name, String setValue, int customerID, int subSystemID)throws Exception;
	
	
	
	
}
