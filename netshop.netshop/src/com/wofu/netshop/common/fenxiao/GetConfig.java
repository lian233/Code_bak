package com.wofu.netshop.common.fenxiao;
/**
 * 加载店配置数据
 */
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class GetConfig {
	private static Hashtable getConfig(String config ,Connection conn)
	throws Exception
	{
		int shopid=0;
		String[] configs = config.split(";");
		StringBuilder sqlTemp  =new StringBuilder("select ");
		for(String e:configs){
			if(e.indexOf("shopid")==0){
				shopid=Integer.parseInt(e.substring(e.indexOf("=")+1));
				sqlTemp.append("a.id").append(",");
			}
				
			else
			sqlTemp.append("a.").append(e).append(",");
		}
		sqlTemp.append(" b.url from decshop a ,channel b where a.channelid=b.id and a.id=").append(shopid);
		Log.info(sqlTemp.toString());
		Hashtable ht = SQLHelper.oneRowSelect(conn, sqlTemp.toString());
		for(Iterator it = ht.keySet().iterator();it.hasNext();){
			String name = (String)it.next();
			Object value= ht.get(name);
			//Log.info(name+" "+value);
			if(value.getClass()==Integer.class)
				ht.put(name, String.valueOf(value));
		}
		return ht;
		
	}
	//初始化Params参数
	public static void taobaoinit(String config,Connection conn,com.wofu.netshop.taobao.fenxiao.Params param)throws Exception{
		//初始化参数
		Properties pro = new Properties();
		pro.putAll(GetConfig.getConfig(config, conn));
		param.init(pro);
		countTaobaoActiveJob(param);
	}
	
	//初始化Params参数
	public static void jingdonginit(String config,Connection conn,com.wofu.netshop.jingdong.fenxiao.Params params)throws Exception{
		//初始化参数
		Properties pro = new Properties();
		pro.putAll(GetConfig.getConfig(config, conn));
		params.init(pro);
		counJingDongActiveJob(params);
	}
	
	//初始化Params参数
	public static void mogujieinit(String config,Connection conn,com.wofu.netshop.mogujie.fenxiao.Params params)throws Exception{
		//初始化参数
		Properties pro = new Properties();
		pro.putAll(GetConfig.getConfig(config, conn));
		params.init(pro);
		counMogujieActiveJob(params);
	}
	
	//初始化Params参数 当当
	public static void dangdang(String config,Connection conn,com.wofu.netshop.dangdang.fenxiao.Params params)throws Exception{
		//初始化参数
		Properties pro = new Properties();
		pro.putAll(GetConfig.getConfig(config, conn));
		params.init(pro);
		counDangdangActiveJob(params);
	}
	
	//初始化Params参数
	public static void meilishuoinit(String config,Connection conn,com.wofu.netshop.meilishuo.fenxiao.Params params)throws Exception{
		//初始化参数
		Properties pro = new Properties();
		pro.putAll(GetConfig.getConfig(config, conn));
		params.init(pro);
		counMeiLiShuoActiveJob(params);
	}
	
	private static int countActiveJob(Connection conn,int shopid) throws Exception {
		String sql ="select (case when isNeedDelivery=1 then 1 else 0 end) +(case when isGenCustomerOrder=1 then 1 else 0 end)+ " +
				"(case when isnull(isgenCustomerRet,0)=1 then 1 else 0 end)+(case when isnull(isUpdateStock,0)=1 then 1 else 0 end)"
		+" from decshop  where id="+shopid;
		return SQLHelper.intSelect(conn, sql);

	}
	
	
	
	//查询淘宝开启的任务
	private static void countTaobaoActiveJob(com.wofu.netshop.taobao.fenxiao.Params param) throws Exception{
		int count=0;
		if(param.isGenCustomerOrder) count++;
		if(param.isgenCustomerRet) count++;
		if(param.isNeedDelivery) count++;
		if(param.isUpdateStock) count++;
		param.jobCount=count;
		
			
		
	}
	//查询京东开启的任务
	private static void counJingDongActiveJob(com.wofu.netshop.jingdong.fenxiao.Params params) throws Exception{
		int count=0;
		if(params.isgetOrder)
			count++;
		if(params.isGenCustomerOrder)
			count++;
		if(params.isgenCustomerRet)
			count++;
		if(params.isNeedDelivery)
			count++;
		params.jobCount=count;
	}
	
	//查询蘑菇街开启的任务
	private static void counMogujieActiveJob(com.wofu.netshop.mogujie.fenxiao.Params params) throws Exception{
		int count=0;
		if(params.isgetOrder)
			count++;
		if(params.isGenCustomerOrder)
			count++;
		if(params.isgenCustomerRet)
			count++;
		if(params.isNeedDelivery)
			count++;
		params.jobCount=count;
	}
	
	//查询美丽说开启的任务
	private static void counMeiLiShuoActiveJob(com.wofu.netshop.meilishuo.fenxiao.Params params) throws Exception{
		int count=0;
		if(params.isgetOrder)
			count++;
		if(params.isGenCustomerOrder)
			count++;
		if(params.isgenCustomerRet)
			count++;
		if(params.isNeedDelivery)
			count++;
		params.jobCount=count;
	}
	
	//查询当当开启的任务
	private static void counDangdangActiveJob(com.wofu.netshop.dangdang.fenxiao.Params params) throws Exception{
		int count=0;
		if(params.isgetOrder)
			count++;
//		if(com.wofu.netshop.dangdang.fenxiao.Params.isGenCustomerOrder)
//			count++;
//		if(com.wofu.netshop.dangdang.fenxiao.Params.isgenCustomerRet)
//			count++;
//		if(com.wofu.netshop.dangdang.fenxiao.Params.isNeedDelivery)
//			count++;
		params.jobCount=count;
	}
	
	public static void alibabainit(String config, Connection connection,
			com.wofu.netshop.alibaba.fenxiao.Params param) throws Exception {
		//初始化参数
		Properties pro = new Properties();
		pro.putAll(GetConfig.getConfig(config, connection));
		param.init(pro);
		counAlibabaActiveJob(param);
		
	}
	
	private static void counAlibabaActiveJob(
			com.wofu.netshop.alibaba.fenxiao.Params param) {
		int count=0;
		if(param.isgetOrder)
			count++;
		if(param.isGenCustomerOrder)
			count++;
		if(param.isgenCustomerRet)
			count++;
		if(param.isNeedDelivery)
			count++;
		param.jobCount=count;
		
	}
	
	
	
	
}
