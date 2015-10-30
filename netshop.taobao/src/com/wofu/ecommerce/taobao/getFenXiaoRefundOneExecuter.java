package com.wofu.ecommerce.taobao;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;

public class getFenXiaoRefundOneExecuter extends Executer {
	private String sellernick="";
	private String tradecontactid="";
	private final String jobName="处理-1分销退货单作业";

	@Override
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		sellernick=prop.getProperty("sellernick");	
		tradecontactid=prop.getProperty("tradecontactid");
		Integer batchid;
		long sub_order_id;
		try {			 
			Log.info(jobName+"开始");
			updateJobFlag(1);
			String sql = "select distinct batchid from eco_rds_fx_refund where flag=-1 and supplier_nick='"+sellernick+"'";
			List result = this.getDao().oneListSelect(sql);
			for(Iterator it = result.iterator();it.hasNext();){
				batchid = (Integer)it.next();
				sql = "select cast(sub_order_id as varchar(32)) as sub_order_id,jdp_response from eco_rds_fx_refund with(nolock) "
					+ "where flag=-1 and supplier_nick='"+sellernick+"' and batchid="+batchid;
				Vector ve = this.getDao().multiRowSelect(sql);
				for(Iterator i= ve.iterator();i.hasNext();){
					Hashtable jdpresponse =(Hashtable) i.next();
					String jdpresponsestr = jdpresponse.get("jdp_response").toString();
					sub_order_id = Long.parseLong(jdpresponse.get("sub_order_id").toString());
					RefundDetail r = new RefundDetail();
					JSONObject jsonobj = new JSONObject(jdpresponsestr);
					JSONObject refunddetailjsobobj = jsonobj.getJSONObject(
							"fenxiao_refund_get_response").getJSONObject(
							"refund_detail");
					r.setObjValue(r, refunddetailjsobobj);
					r.setSub_order_id(sub_order_id);
					RDSUtils.processFenXiaoRefundOne(jobName, this.getDao(), r,
							tradecontactid, sellernick,refunddetailjsobobj.getLong("purchase_order_id"));

					sql = "update eco_rds_fx_refund set flag=1 "
							+ "where batchid=" + batchid + " and sub_order_id='"+sub_order_id+"'";
					this.getDao().execute(sql);
				}
				
			}
			Log.info("处理淘宝-1标志退货单成功!");
			
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
				if (this.getConnection() != null){
					this.getConnection().setAutoCommit(true);
					this.getConnection().close();
				}
					
				if (this.getExtconnection() != null){
					this.getExtconnection().setAutoCommit(true);
					this.getExtconnection().close();
				}
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}

		
	
	}

}
 