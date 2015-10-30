package com.wofu.intf.sf;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
/**
 * 仓库出货后，发货单号回传到系统
 * @author Administrator
 *com.wofu.intf.best.ReceiptExecuter
 */
public class ReceiptExecuter extends Executer {
	private static String jobName="定时处理顺风物流出库数据";
	private static String customerCode;
	private static String vertifycode;
	public void run() {
		Properties pro = StringUtil.getStringProperties(this.getExecuteobj().getParams());
		customerCode= pro.getProperty("customerCode");
		vertifycode= pro.getProperty("vertifycode");
		try {			 
			updateJobFlag(1);
			String sql="select msgid,servicetype,bizdata ,receivedate,interfaceSystem "
				+"from ecs_bestlogisticsinterface with(nolock) where flag=0 order by receivedate";
			Vector vtintf=this.getDao().multiRowSelect(sql);
			Log.info(jobName+"，本次要处理的数据条数为:　"+vtintf.size());
			for (int i=0;i<vtintf.size();i++)
			{
				Hashtable htintf=(Hashtable) vtintf.get(i);
				Object msgid1=htintf.get("msgid");
				String msgid=msgid1!=null?msgid1.toString():"";
				Object servicetype1=htintf.get("servicetype");
				String servicetype=servicetype1!=null?servicetype1.toString():"";
				Object bizdata1=htintf.get("bizdata");
				String bizdata=bizdata1!=null?bizdata1.toString():"";
				Object receivedate1=htintf.get("receivedate");
				String receivedate=receivedate1!=null?receivedate1.toString():"";
				//商家wms系统
				Object interfaceSystem1=htintf.get("interfaceSystem");
				String interfaceSystem=interfaceSystem1!=null?interfaceSystem1.toString():"";
				ECSDao dao = (ECSDao)this.getDao();
				dao.setTransation(false);
				String processorClassName = Processors.getProcessor(servicetype);
				BizProcessor processor = (BizProcessor) Class.forName(processorClassName).newInstance();
				processor.setBizData(bizdata);
				processor.setCustomerCode(customerCode);
				processor.setVertifycode(vertifycode);
				processor.setConnection(dao.getConnection());
				processor.setInterfaceSystem(interfaceSystem);
				try{
					processor.process();
				}catch(Exception ex){
					Log.error(jobName+"处理数据出错: ", "msgid: "+msgid );
					Log.error("错误详细信息:　", ex.getMessage());
					if (this.getConnection() != null && !this.getConnection().getAutoCommit())
						this.getConnection().rollback();
					
					if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
						this.getExtconnection().rollback();
					continue;
					
				}
				
				sql="update ecs_bestlogisticsinterface set flag=1,processdate=getdate() where msgid='"+msgid+"'";
				dao.execute(sql);
				
				dao.commit();
				dao.setTransation(true);
				dao=null;
			}
			
			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
			
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"回滚事务失败");
				Log.error(jobName, e1.getMessage());
			}
			
			try{
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			}catch(Exception ex){
				Log.error(jobName,"更新任务信息失败");
				Log.error(jobName, ex.getMessage());
			}
			Log.error(jobName,"错误信息:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
		} finally {
			
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"更新处理标志失败");
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}
		
		
		
	}
	
	
	
}
