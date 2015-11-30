package com.wofu.ecommerce.taobao;
import java.util.Properties;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;

public class RDSGetSingleOrderExecuter extends Executer {

	private String sellernick="";
	
	private static String jobName="定时处理RDS单个订单数据";
	
	private String step=""; //执行步骤
	private String tableName; //要复制的表名
	private String tid; //要复制的订单号


	@Override
	public void run() {
		
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		sellernick=prop.getProperty("sellernick");
		tableName=prop.getProperty("tableName");
		tid=prop.getProperty("tid");
		//检查连接
		if (!this.checkExtPool()){
			System.out.println("检查外部连接池【"+this.getExtdsname()+"】失败");
			return ;
		}
	
		try
		{
			try{
				
				this.getExtdao().getConnection();
	
			}catch(Exception ex){
				Log.error(jobName,"处理商家: "+sellernick+"作业时,"+"远程数据库连接失败");
				return ; //连接不到远程，则退出
			}
			
			step="更新JOB标志";
			updateJobFlag(1);
			
			copy();
	
			step="更新JOB时间";
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

	
	private void copy() throws Exception
	{
		Log.info("处理-"+sellernick+"-rds单个订单数据开始");
		long l = System.currentTimeMillis();
		
		String sql="";
		int batchid=0;
		int tradecount=0;
		int refundcount=0;
		int itemcount=0;
		int fxtradecount=0;
		int fxrefundcount=0;
		int tmreturncount=0;
		int tmrefundcount=0;
		//处理订单
		step="处理订单";
		if(tableName.equals("eco_rds_trade")){
			sql="if object_id( 'tempdb..#tmp_single_trade') is not null  drop table #tmp_single_trade";
			this.getDao().execute(sql);		
			
			batchid=this.getExtdao().IDGenerator("eco_rds_trade", "batchid");
			
			sql="select "+batchid+" as batchid,cast(tid as varchar(32)) as tid,status,type,seller_nick,buyer_nick,created,"
				+"modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,jdp_created,"
				+"jdp_modified,0 as flag into #tmp_single_trade from sys_info..jdp_tb_trade where seller_nick='"+sellernick+"' and tid='"+tid+"'";

			this.getDao().execute(sql);
			
			tradecount=this.getDao().intSelect("select count(*) from #tmp_single_trade");
		}else if(tableName.equals("eco_rds_refund")){
			//处理退货单
			step="处理退货单";
			sql="if object_id( 'tempdb..#tmp_single_refund') is not null  drop table #tmp_single_refund";
			this.getDao().execute(sql);		

			batchid=this.getExtdao().IDGenerator("eco_rds_refund", "batchid");
			
			sql="select "+batchid+" as batchid,cast(refund_id as varchar(32)) as refund_id,cast(tid as varchar(32)) as tid,cast(oid as varchar(32)) as oid,status,seller_nick,buyer_nick,"
				+"created,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,jdp_created,jdp_modified,0 as flag "
				+"into #tmp_single_refund from sys_info..jdp_tb_refund where seller_nick='"+sellernick+"' and tid='"+tid+"'";
			this.getDao().execute(sql);
			
			refundcount=this.getDao().intSelect("select count(*) from #tmp_single_refund");
		}else if(tableName.equals("eco_rds_item")){
			//处理商品资料
			step="处理商品资料";
			sql="if object_id( 'tempdb..#tmp_single_item') is not null  drop table #tmp_single_item";
			this.getDao().execute(sql);		
			
			batchid=this.getExtdao().IDGenerator("eco_rds_item", "batchid");
			sql="select "+batchid+" as batchid,cast(num_iid as varchar(32)) as num_iid,nick,approve_status,has_showcase,cid,"
				+"has_discount,created,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,jdp_delete,jdp_created,"
				+"jdp_modified,0 as flag into #tmp_single_item from sys_info..jdp_tb_item where nick='"+sellernick+"' and tid='"+tid+"'";
			this.getDao().execute(sql);
			itemcount=this.getDao().intSelect("select count(*) from #tmp_single_item");
		}else if(tableName.equals("eco_rds_fx_trade")){
			//处理分销订单
			step="处理分销订单";
			sql="if object_id( 'tempdb..#tmp_fx_single_trade') is not null  drop table #tmp_fx_single_trade";
			this.getDao().execute(sql);		
			batchid=this.getExtdao().IDGenerator("eco_rds_fx_trade", "batchid");
			sql="select "+batchid+" as batchid,cast(fenxiao_id as varchar(32)) as fenxiao_id,status,cast(tc_order_id as varchar(32)) as tc_order_id,supplier_username,"
				+"distributor_username,created,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,jdp_created,"+
				"jdp_modified,0 as flag into #tmp_fx_single_trade from sys_info..jdp_fx_trade where supplier_username='"+sellernick+"' and fenxiao_id in ("+tid+")";
			this.getDao().execute(sql);
			fxtradecount=this.getDao().intSelect("select count(*) from #tmp_fx_single_trade");
			System.out.println("订单数据量大小"+fxtradecount);
		}else if(tableName.equals("eco_rds_fx_refund")){
			//处理分销退单
			step="处理分销退单";
			sql="if object_id( 'tempdb..#tmp_fx_single_refund') is not null  drop table #tmp_fx_single_refund";
			this.getDao().execute(sql);		
			batchid=this.getExtdao().IDGenerator("eco_rds_fx_refund", "batchid");
			sql="select "+batchid+" as batchid,cast(sub_order_id as varchar(32)) as sub_order_id,refund_status,supplier_nick,"
				+"distributor_nick,refund_create_time,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,"
				+"jdp_created,jdp_modified,0 as flag into #tmp_fx_single_refund from sys_info..jdp_fx_refund where supplier_nick='"+sellernick+"' and tid='"+tid+"'";		
			this.getDao().execute(sql);
			fxrefundcount=this.getDao().intSelect("select count(*) from #tmp_fx_single_refund");
		}else if(tableName.equals("eco_rds_tm_return")){
			//处理天猫退货单
			step="处理天猫退货单";
			sql="if object_id( 'tempdb..#tmp_tm_single_return') is not null  drop table #tmp_tm_single_return";
			this.getDao().execute(sql);		
			batchid=this.getExtdao().IDGenerator("eco_rds_tm_return", "batchid");
			sql="select "+batchid+" as batchid,cast(a.refund_id as varchar(32)) as refund_id,a.refund_phase,a.status,"
				+"a.sid,b.seller_nick,b.buyer_nick,cast(a.tid as varchar(32)) as tid,cast(a.oid as varchar(32)) as oid,a.created,"
				+"a.modified,a.jdp_hashcode,isnull(a.jdp_response,'') as jdp_response,"
				+"a.jdp_created,a.jdp_modified,0 as flag into #tmp_tm_single_return from sys_info..jdp_tm_return a,sys_info..jdp_tm_refund b "
				+"where a.tid=b.tid and a.oid=b.oid and b.seller_nick='"+sellernick+"' and tid='"+tid+"'";
			this.getDao().execute(sql);
			tmreturncount=this.getDao().intSelect("select count(*) from #tmp_tm_single_return");
		}else if(tableName.equals("eco_rds_tm_refund")){
			//处理天猫退款单
			step="处理天猫退款单";
			sql="if object_id( 'tempdb..#tmp_tm_single_refund') is not null  drop table #tmp_tm_single_refund";
			this.getDao().execute(sql);		
			batchid=this.getExtdao().IDGenerator("eco_rds_tm_refund", "batchid");
			sql="select "+batchid+" as batchid,cast(refund_id as varchar(32)) as refund_id,refund_phase,status,"
				+"seller_nick,buyer_nick,cast(tid as varchar(32)) as tid,cast(oid as varchar(32)) as oid,created,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,"
				+"jdp_created,jdp_modified,0 as flag into #tmp_tm_single_refund from sys_info..jdp_tm_refund "
				+"where seller_nick='"+sellernick+"' and tid='"+tid+"'";		
			this.getDao().execute(sql);
			tmrefundcount=this.getDao().intSelect("select count(*) from #tmp_tm_single_refund");
		}
		//写远程数据库
		step="处理远程数据";
		Properties prop=new Properties();
		if (tradecount>0) prop.setProperty("eco_rds_trade", "select * from #tmp_single_trade");
		if (refundcount>0) prop.setProperty("eco_rds_refund", "select * from #tmp_single_refund");
		if (itemcount>0) prop.setProperty("eco_rds_item", "select * from #tmp_single_item");
		if (fxtradecount>0) prop.setProperty("eco_rds_fx_trade", "select * from #tmp_fx_single_trade");
		if (fxrefundcount>0) prop.setProperty("eco_rds_fx_refund", "select * from #tmp_fx_single_refund");
		if (tmreturncount>0) prop.setProperty("eco_rds_tm_return", "select * from #tmp_tm_single_return");
		if (tmrefundcount>0) prop.setProperty("eco_rds_tm_refund", "select * from #tmp_tm_single_refund");
		
		if (tradecount>0 
				|| refundcount>0 
				|| itemcount>0 
				|| fxtradecount>0 
				|| fxrefundcount>0
				|| tmreturncount>0 
				|| tmrefundcount>0)
			this.getDao().copyTo(this.getExtdao(), prop);

		Log.info("复制数据完成,订单号: " + tid);
			
	}
	

}
 