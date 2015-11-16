package com.wofu.base.common;

import com.wofu.base.account.BusinessAccount;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.util.JException;

public class BusinessControlUtil {
	

	public static void control(DataCentre dc,int busid,int busitype) throws Exception
	{
		String sql="select * from ecs_busitypeconfig where busitype="+busitype;
		ECS_BusiTypeConfig busitypeconfig=new ECS_BusiTypeConfig();
		busitypeconfig.getMapData(dc.oneRowSelect(sql));
		
		if (!busitypeconfig.getAccclass().equals(""))
		{
			BusinessAccount businessaccount=(BusinessAccount) Class.forName(busitypeconfig.getAccclass()).newInstance();
			businessaccount.execute(dc,busid,busitype);
		}
	}
	
	

}
