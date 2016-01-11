package com.wofu.fenxiao.mapping;

import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseMapper;
import com.wofu.fenxiao.domain.DataPackage;

//数据包管理
public interface DataPackageMapper extends BaseMapper<DataPackage>{

	//查询数据包
	public List<HashMap> qryDataPackage(HashMap<String, Object> map)throws Exception;
	
	//查询当前identity的值
	public Integer qryIdentity()throws Exception;
	
	//查询指定的文件是否已经存在于数据库中
	public Integer qryFileExisting(String strFileName)throws Exception;
	
	//获取指定id的数据包信息
	public DataPackage getById(Integer packageID)throws Exception;
}
