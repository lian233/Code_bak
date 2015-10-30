package com.wofu.fenxiao.mapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.Login;
import com.wofu.fenxiao.domain.PartMember;



public interface LoginMapper extends BaseMapper<Login>{
	public Login querySingleAccount(String accountName);
	public Login isExist(String accountName);
	/**
	 * 验证用户登陆

	 * @return
	 */
	public Login countAccount(Login account)throws Exception;
	
	public List<Login> queryNoMatch(Map<String, Object> map)throws Exception;
	//两个参数  用Param来指定  在xmL文件中可以直接引用   
	public Login queryByName(@Param("name")String name, @Param("password")String password)throws Exception;
	/**
	 * Public User queryByName(Map paramMap);  这里可以传一个map  xml中也是直接取map的key
	 * <select id=" selectUser" resultMap="BaseResultMap">
   select  *  from user_user_t   where user_name = #{name，jdbcType=VARCHAR} and user_area=#{password,jdbcType=VARCHAR}
</select>
	 */
	//调用存储过程
	public void test(HashMap<String,String>  map)throws Exception;
	//查询用户集合
	public List<Login> queryAll(Login a);
	//调用存储过程  有return参数
	public void testhasReturn(HashMap<String, String> map)throws Exception;
	//返回一个hashmap
	public List<HashMap> returnHashMap(HashMap<String, String> map)throws Exception;
	//把用户加入指定的组
	public void saveRelativity(PartMember partMember) throws Exception;
	
	//取得帐号数据
	public List<HashMap> qryLogin(HashMap<String,Object>  map)throws Exception;
	public List<HashMap> qryDLogin(HashMap<String,Object>  map)throws Exception;
	
	//取得ID值
	public void tlGetNewSerial(HashMap<String, Object> map)throws Exception;

	//取得SheeID值
	public void tlGetNewDecSheetID(HashMap<String, Object> map)throws Exception;

	//取客户配置
	public String tlGetCustomerConfig(HashMap<String, Object> map)throws Exception;
	
	//分页取得帐号数据
	public List<HashMap> qryLoginOnPage(HashMap<String,Object>  map)throws Exception;
	
	//取得查询的帐号数据数量
	public int qryLoginCount(HashMap<String,Object>  map)throws Exception;

	//设置客户配置
	public void tlSetCustomerConfig(HashMap<String, Object> map)throws Exception;
	
}
