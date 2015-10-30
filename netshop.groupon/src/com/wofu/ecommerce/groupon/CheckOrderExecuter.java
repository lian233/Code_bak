package com.wofu.ecommerce.groupon;

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

	private String key="";

	private String categoryid="";

	private String tradecontactid="";

	private String dbname="";
	
	private String username="";
	
	private String lasttimeconfvalue="";
	
	private String namespace="";
	
	private int limit=0;
	
	private int total=40;
	
	private long daymillis=24*60*60*1000L;
	
	private int checkdays=7;
	

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		TimerJob job=(TimerJob) this.getExecuteobj();
		Properties prop=StringUtil.getStringProperties(job.getParams());
		
		wsurl=prop.getProperty("wsurl");
		encoding=prop.getProperty("encoding");
		key=prop.getProperty("key");
		categoryid=prop.getProperty("categoryid");
		tradecontactid=prop.getProperty("tradecontactid");
		dbname=prop.getProperty("dbname");
		username=prop.getProperty("username");
		limit=Integer.valueOf(prop.getProperty("limit"));
		total=Integer.valueOf(prop.getProperty("total"));
		checkdays=Integer.valueOf(prop.getProperty("checkdays"));
		lasttimeconfvalue=Params.username+"取订单最新时间";
		

		Hashtable<String,String> htwsinfo=new Hashtable<String,String>();
		
		htwsinfo.put("tradecontactid", tradecontactid);
		htwsinfo.put("key", key);
		htwsinfo.put("wsurl", wsurl);
		htwsinfo.put("username", username);
		htwsinfo.put("lasttimeconfvalue", lasttimeconfvalue);
		htwsinfo.put("namespace", namespace);
		htwsinfo.put("limit", String.valueOf(limit));
		htwsinfo.put("total", String.valueOf(total));
		htwsinfo.put("style", "1");
		htwsinfo.put("encoding", encoding);
		htwsinfo.put("categoryid", categoryid);
		
		Connection conn=null;
		try {		
			conn= PoolHelper.getInstance().getConnection(dbname);
			List plist = ProjectUtils.getBusinessProjectInfo("定时检查未入订单",htwsinfo);
			
			
			for (Iterator it = plist.iterator(); it.hasNext();) {
			
				String grouponid = (String) it.next();

				Date starttime=Formatter.parseDate("2011-11-11 00:00:00", Formatter.DATE_TIME_FORMAT);
				//Date starttime=new Date(endtime.getTime()-checkdays*daymillis);
				Date endtime=Formatter.parseDate("2011-11-11 23:59:59", Formatter.DATE_TIME_FORMAT);
				OrderUtils.getBusinessOrderList("定时检查未入订单",conn,htwsinfo,grouponid,starttime,endtime);				
			}	
			
		} catch (Exception e) {
			try {
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			} catch (Exception e1) {
				throw new JException("回滚事务失败");
			}
			throw new JException("同步团宝库存"+Log.getErrorMessage(e));
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
