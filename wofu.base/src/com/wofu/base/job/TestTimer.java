package com.wofu.base.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

public class TestTimer extends TimerTask {

	@Override
	public void run() {
		//System.out.println("test");
		
		try {
			Connection conn= PoolHelper.getInstance().getConnection("shop");
			
			String sql="select orgname from ecs_org ";
			
			List orgs=SQLHelper.multiRowListSelect(conn, sql);
			for(int i=0;i<orgs.size();i++)
			{
				String orgname=(String) orgs.get(i);
				System.out.println(orgname);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
