package com.wofu.base.systemmanager;

import java.util.Date;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.inventory.InventoryProcessor;

public class SystemControl {

	public static void busiControl(DataCentre dao,int busid,int bustype) throws Exception
	{
		String sql="select count(*) from ecs_busproclist where busid="+busid+" and bustype="+bustype;
		if (dao.intSelect(sql)>0) return;
		
		ECS_BusProcList busproclist=new ECS_BusProcList();
		busproclist.setBusid(busid);
		busproclist.setBustype(bustype);
		busproclist.setExecuteflag(0);
		busproclist.setStime(new Date());
		
		dao.insert(busproclist);
		
		ECS_BusTypeConfig bustypeconfig=new ECS_BusTypeConfig();
		bustypeconfig.setDao(dao);
		bustypeconfig.getDataByID(bustype);
		
		if (!bustypeconfig.getInvprocessor().equals(""))
		{
			InventoryProcessor invprocessor=(InventoryProcessor) Class.forName(bustypeconfig.getInvprocessor()).newInstance();
			invprocessor.setDao(dao);
			invprocessor.setBusid(busid);
			invprocessor.setBustype(bustype);
			invprocessor.execute();
		}
		
		busproclist.setExecuteflag(1);
		dao.updateByKeys(busproclist, "busid,bustype");
	}
}
