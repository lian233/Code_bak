package com.wofu.netshop.common;
/**
 * 加载店配置数据
 */
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.taobao.Params;

public class GetConfig {
	private static Hashtable getConfig(String config ,long orgid,Connection conn)
	throws Exception
	{
		String[] configs = config.split(";");
		StringBuilder sqlTemp  =new StringBuilder("select ");
		for(String e:configs)	{
			sqlTemp.append(e).append(",");
		}
		sqlTemp.deleteCharAt(sqlTemp.length()-1);
		sqlTemp.append(" from ecs_org_params where orgid=").append(orgid);
		//Log.info(sqlTemp.toString());
		Hashtable ht = SQLHelper.oneRowSelect(conn, sqlTemp.toString());
		for(Iterator it = ht.keySet().iterator();it.hasNext();){
			String name = (String)it.next();
			Object value= ht.get(name);
			if(value.getClass()==Integer.class)
				ht.put(name, String.valueOf(value));
		}
		return ht;
		
	}
	//初始化Params参数
	public static void init(String config,Connection conn,long orgid)throws Exception{
		//初始化参数
		Properties pro = new Properties();
		pro.putAll(GetConfig.getConfig(config, orgid, conn));
		countTaobaoActiveJob();
		Params.init(pro);
	}
	//查询开启的任务
	private static int countActiveJob(Connection conn,long orgid) throws Exception {
		String sql ="select (case when isNeedDelivery=1 then 1 else 0 end) +(case when isGenCustomerOrder=1 then 1 else 0 end)+ " +
				"(case when isnull(isgenCustomerRet,0)=1 then 1 else 0 end)+(case when isnull(isUpdateStock,0)=1 then 1 else 0 end)"
		+" from ecs_org_params  where orgid="+orgid;
		return SQLHelper.intSelect(conn, sql);

	}
	
	
	private static void countTaobaoActiveJob() throws Exception{
		int count=0;
		if(Params.isGenCustomerOrder)
			count++;
		if(Params.isgenCustomerRet)
			count++;
		if(Params.isNeedDelivery)
			count++;
		if(Params.isUpdateStock)
			count++;
		Params.jobCount=count;
			
		
	}
	

	
	
}
