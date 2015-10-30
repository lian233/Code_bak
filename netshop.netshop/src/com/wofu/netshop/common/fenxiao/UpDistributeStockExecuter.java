package com.wofu.netshop.common.fenxiao;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import com.wofu.base.job.Executer;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 更新分销的库存  分销作为一个独立的机构 按比例同步总库存
 * @author Administrator
 *
 */
public class UpDistributeStockExecuter extends Executer{
	private String jobName ="同步分销商品库存";
	private int tradecontactid;
	@Override
	public void run() {
		try{
			Properties pro = StringUtil.getIniProperties(getExecuteobj().getParams());
			tradecontactid = Integer.parseInt(pro.getProperty("tradecontactid","0"));
			updateJobFlag(1);
			String sql ="select a.orgcode,b.synstockrate from ecs_org a inner join ecs_rationconfig b"
				+" on a.orgid=b.rationorgid inner join ecs_tradecontactorgcontrast c on b.shoporgid=c.orgid and "
				+"c.tradecontactid="+tradecontactid;
			Vector result = this.getDao().multiRowSelect(sql);
			String dcList ="",synrateList="";
			for(Iterator it = result.iterator();it.hasNext();){
				Hashtable ht = (Hashtable)it.next();
				dcList+=","+ht.get("orgcode").toString();
				synrateList=ht.get("synstockrate").toString();//多个仓库只有一个同步比例
			}
			sql = "exec IF_SetDistributeInventoryBC '"+dcList+"',null,"+synrateList+",''";
			this.getDao().execute(sql);
			UpdateTimerJob();
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		}catch(Exception e){
			Log.error(jobName,e.getMessage());
			try {
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			try {
				updateJobFlag(0);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(this.getConnection()!=null){
				try {
					this.getConnection().close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		// TODO Auto-generated method stub
	}
	
}
