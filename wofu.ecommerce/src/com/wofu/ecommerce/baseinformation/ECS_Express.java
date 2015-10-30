package com.wofu.ecommerce.baseinformation;

import com.wofu.base.util.BusinessObject;

public class ECS_Express extends BusinessObject {
	
	
	public void getUseExpress() throws Exception
	{
		String sql="select rtrim(ltrim(companycode)) companycode,rtrim(ltrim(name)) companyname "
			+"from ecs_express with(nolock) where status=1";					
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	

	public void getExpress() throws Exception
	{
		String sql="select rtrim(ltrim(companycode)) companycode,rtrim(ltrim(name)) companyname "
			+"from ecs_express with(nolock) ";					
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}


}
