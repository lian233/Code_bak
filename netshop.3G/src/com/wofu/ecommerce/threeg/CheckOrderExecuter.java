package com.wofu.ecommerce.threeg;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerJob;
import com.wofu.business.util.PublicUtils;

public class CheckOrderExecuter extends Executer {
	private String wsurl="";

	private String encoding="";

	private String agentid="";

	private String CustomerPrivateKeyPath="";

	private String tradecontactid="";

	private String dbname="";
	
	private String GGMallPublicKeyPath="";
	
	private int checkdays;
		
	private String username="";
	
	private static String cmdcode="1002";
	
	private static long daymillis=24*60*60*1000L;
	
	private Date nextactive=null;
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		TimerJob job=(TimerJob) this.getExecuteobj();
		Properties prop=StringUtil.getStringProperties(job.getParams());
		
		String workdir=System.getProperty("user.dir");
		workdir=StringUtil.replace(workdir,"\\", "/");
		
		wsurl=prop.getProperty("wsurl");
		encoding=prop.getProperty("encoding");
		CustomerPrivateKeyPath=workdir+"/"+prop.getProperty("CustomerPrivateKeyPath");
		GGMallPublicKeyPath=workdir+"/"+prop.getProperty("GGMallPublicKeyPath");		
		agentid=prop.getProperty("agentid");
		tradecontactid=prop.getProperty("tradecontactid");
		dbname=prop.getProperty("dbname");
		username=prop.getProperty("username");	
		nextactive=job.getNextactive();
		
		checkdays=Integer.valueOf(prop.getProperty("checkdays"));
		String lasttimeconfvalue=username+"取订单最新时间";
				
		Hashtable<String,String> htwsinfo=new Hashtable<String,String>();
		
		htwsinfo.put("cmd",cmdcode);
		htwsinfo.put("wsurl",wsurl);
		htwsinfo.put("CustomerPrivateKeyPath",CustomerPrivateKeyPath);
		htwsinfo.put("GGMallPublicKeyPath",GGMallPublicKeyPath);
		htwsinfo.put("encoding",encoding);
		htwsinfo.put("username",username);
		htwsinfo.put("agentid",agentid);
		htwsinfo.put("style","1");
		htwsinfo.put("tradecontactid",tradecontactid);
		htwsinfo.put("lasttimeconfvalue",lasttimeconfvalue);
		
		Connection conn=null;
		try {		
			conn= PoolHelper.getInstance().getConnection(dbname);
			
			
			Long starttime=this.nextactive.getTime()-checkdays*daymillis;
			Long endtime=this.nextactive.getTime()-1000L;
			
			OrderUtils.getOrderList("检查3G未入订单", conn, htwsinfo, "1", new Date(starttime), new Date(endtime));
			OrderUtils.getOrderList("检查3G失败订单", conn, htwsinfo, "7", new Date(starttime), new Date(endtime));
			
		} catch (Exception e) {
			try {
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			} catch (Exception e1) {
				throw new JException("回滚事务失败");
			}
			throw new JException("检查3G未入订单"+Log.getErrorMessage(e));
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				throw new JException("关闭数据库连接失败");
			}
		}
	}
		
}
