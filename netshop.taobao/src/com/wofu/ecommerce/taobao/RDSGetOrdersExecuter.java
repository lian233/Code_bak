package com.wofu.ecommerce.taobao;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import com.wofu.common.pool.TinyConnection;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class RDSGetOrdersExecuter extends Executer {
			
	private String sellernick="";
	
	private static String jobName="定时处理RDS数据";
	private boolean isMysql;
	
	//private String step=""; //执行步骤

	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		sellernick=prop.getProperty("sellernick");
		isMysql=Boolean.parseBoolean(prop.getProperty("isMysql","false"));
		
	
		try
		{
			updateJobFlag(1);
			//检查连接
			if (!this.checkExtPool()){
				Log.info("检查外部连接池【"+this.getExtdsname()+"】失败");
				return ;
			}
			//step="更新JOB标志";
			
			
			copy();
	
			//step="更新JOB时间";
			UpdateTimerJob();

			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		} catch (Throwable e) {
			try{
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					
						this.getConnection().rollback();
						this.getConnection().setAutoCommit(true);
				}catch(Exception e1){
						
				try {
					((TinyConnection)this.getConnection()).getPool().releaseOne(this.getConnection());
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					Log.error(jobName+" "+sellernick, "释放本地数据库连接失败: "+e2.getMessage());
				}
				}
					
				try{
					if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit()){
						this.getExtconnection().rollback();
						this.getExtconnection().setAutoCommit(true);
				}
				}catch(Exception e2){
					try {
						((TinyConnection)this.getExtconnection()).getPool().releaseOne(this.getExtconnection());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						Log.error(jobName+" "+sellernick, "释放远程数据库连接失败: "+e1.getMessage());
					}
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

	
	private void copy() throws Throwable
	{
		Log.info("处理-"+sellernick+"-rds数据开始");
		long l = System.currentTimeMillis();
		
		//step="取商家配置";
		String sql="select * from eco_seller_config where sellernick='"+sellernick+"'";
		Hashtable htconfig=this.getDao().oneRowSelect(sql);
		
		if (htconfig.size()<=0){
			Log.error(jobName , "找不到商家配置eco_seller_config["+sellernick+"]");
			return;
		}
		//
		String lastordertime=htconfig.get("lastordertime").toString();
		
		String lastordertimeTemp = Formatter.format(new Date(Formatter.parseDate(lastordertime, Formatter.DATE_TIME_FORMAT).getTime()-30*1000L),Formatter.DATE_TIME_FORMAT);
		String lastrefundtime=htconfig.get("lastrefundtime").toString();
		String lastitemtime=htconfig.get("lastitemtime").toString();
		String lastfxordertime=htconfig.get("lastfxordertime").toString();
		String lastfxrefundtime=htconfig.get("lastfxrefundtime").toString();
		String lasttmreturntime=htconfig.get("lasttmreturntime").toString();
		String lasttmrefundtime=htconfig.get("lasttmrefundtime").toString();
		String lastjxordertime=htconfig.get("lastjxordertime").toString();
		Properties prop=new Properties();
		int tradecount=0;
		int batchid=0;
		//处理订单
		//step="处理订单";
			sql="if object_id( 'tempdb..##"+sellernick+"_tmp_trade') is not null  drop table ##"+sellernick+"_tmp_trade";
			this.getDao().execute(sql);
			
			sql="select top 3000 "+-1+" as batchid,cast(tid as varchar(32)) as tid,status,type,seller_nick,buyer_nick,created,"
				+"modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,jdp_created,"
				+"jdp_modified,0 as flag into ##"+sellernick+"_tmp_trade from sys_info..jdp_tb_trade where seller_nick='"+sellernick+"' and jdp_modified>'"+lastordertimeTemp+"' order by jdp_modified";

			this.getDao().execute(sql);
			
			tradecount=this.getDao().intSelect("select count(*) from ##"+sellernick+"_tmp_trade");
			if (tradecount>0) {
				if(isMysql){
					batchid=this.getExtdao().IDGeneratorMysql("eco_rds_trade", "batchid");
				}else
				batchid=this.getExtdao().IDGenerator("eco_rds_trade", "batchid");
				sql = "update ##"+sellernick+"_tmp_trade set batchid="+-1*batchid;
				this.getDao().execute(sql);
				prop.setProperty("eco_rds_trade", "select * from ##"+sellernick+"_tmp_trade");
				sql="select max(jdp_modified) from ##"+sellernick+"_tmp_trade";	
				String temp = this.getDao().strSelect(sql);
				if(Formatter.parseDate(temp, Formatter.DATE_TIME_FORMAT).compareTo(Formatter.parseDate(lastordertime, Formatter.DATE_TIME_FORMAT))>0){
						lastordertime=temp;
						this.getDao().copyTo(this.getExtdao(), prop);
						sql= "update eco_rds_trade set batchid="+batchid +" where batchid="+-1*batchid;
						this.getExtdao().execute(sql);
						sql="update eco_seller_config set lastordertime='"+lastordertime
						+"' where sellernick='"+sellernick+"'";
						this.getDao().execute(sql);
						Log.info(jobName+","+sellernick+",本次处理的订单数据是:　"+tradecount);
				}
				
			
		}
		
		//step="处理退货单";
			
			sql="if object_id( 'tempdb..##"+sellernick+"_tmp_refund') is not null  drop table ##"+sellernick+"_tmp_refund";
			this.getDao().execute(sql);		
			
			sql="select "+-1+" as batchid,cast(refund_id as varchar(32)) as refund_id,cast(tid as varchar(32)) as tid,cast(oid as varchar(32)) as oid,status,seller_nick,buyer_nick,"
				+"created,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,jdp_created,jdp_modified,0 as flag "
				+"into ##"+sellernick+"_tmp_refund from sys_info..jdp_tb_refund where seller_nick='"+sellernick+"' and  jdp_modified>'"+lastrefundtime+"'";
			this.getDao().execute(sql);
			
			int refundcount=this.getDao().intSelect("select count(*) from ##"+sellernick+"_tmp_refund");
			if (refundcount>0){
				if(isMysql){
					batchid=this.getExtdao().IDGeneratorMysql("eco_rds_refund", "batchid");
				}else
				batchid=this.getExtdao().IDGenerator("eco_rds_refund", "batchid");
				sql = "update ##"+sellernick+"_tmp_refund set batchid="+-1*batchid;
				this.getDao().execute(sql);
				prop.clear();
				prop.setProperty("eco_rds_refund", "select * from ##"+sellernick+"_tmp_refund");
				sql="select max(jdp_modified) from ##"+sellernick+"_tmp_refund";		
				lastrefundtime=this.getDao().strSelect(sql);
				this.getDao().copyTo(this.getExtdao(), prop);
				sql= "update eco_rds_refund set batchid="+batchid +" where batchid="+-1*batchid;
				this.getExtdao().execute(sql);
				sql="update eco_seller_config set lastrefundtime='"+lastrefundtime
				+"' where sellernick='"+sellernick+"'";
				this.getDao().execute(sql);
			Log.info(jobName+","+sellernick+",本次处理的退货订单数据是:　"+refundcount);
			
		}
		
			
		
		//处理商品资料
		//step="处理商品资料";
			sql="if object_id( 'tempdb..##"+sellernick+"_tmp_item') is not null  drop table ##"+sellernick+"_tmp_item";
			this.getDao().execute(sql);
			
			sql="select "+-1+" as batchid,cast(num_iid as varchar(32)) as num_iid,nick,approve_status,has_showcase,cid,"
				+"has_discount,created,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,jdp_delete,jdp_created,"
				+"jdp_modified,0 as flag into ##"+sellernick+"_tmp_item from sys_info..jdp_tb_item where nick='"+sellernick+"' and jdp_modified>'"+lastitemtime+"'";
			this.getDao().execute(sql);
			int itemcount=this.getDao().intSelect("select count(*) from ##"+sellernick+"_tmp_item");
			if (itemcount>0){
				if(isMysql){
					batchid=this.getExtdao().IDGeneratorMysql("eco_rds_item", "batchid");
				}else
				batchid=this.getExtdao().IDGenerator("eco_rds_item", "batchid");
				sql = "update ##"+sellernick+"_tmp_item set batchid="+-1*batchid;
				this.getDao().execute(sql);
				prop.clear();
				prop.setProperty("eco_rds_item", "select * from ##"+sellernick+"_tmp_item");
				sql="select max(jdp_modified) from ##"+sellernick+"_tmp_item";		
				lastitemtime=this.getDao().strSelect(sql);
				this.getDao().copyTo(this.getExtdao(), prop);
				sql= "update eco_rds_item set batchid="+batchid +" where batchid="+-1*batchid;
				this.getExtdao().execute(sql);
				sql="update eco_seller_config set lastitemtime='"+lastitemtime
				+"' where sellernick='"+sellernick+"'";
				this.getDao().execute(sql);
			
			Log.info(jobName+","+sellernick+",本次处理的商品资料数据是:　"+itemcount);
		}
		

		
		//处理分销订单
		//step="处理分销订单";
			sql="if object_id( 'tempdb..##"+sellernick+"_tmp_fx_trade') is not null  drop table ##"+sellernick+"_tmp_fx_trade";
			this.getDao().execute(sql);
			
			sql="select "+-1+" as batchid,cast(fenxiao_id as varchar(32)) as fenxiao_id,status,cast(tc_order_id as varchar(32)) as tc_order_id,supplier_username,"
				+"distributor_username,created,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,jdp_created,"+
				"jdp_modified,0 as flag into ##"+sellernick+"_tmp_fx_trade from sys_info..jdp_fx_trade where supplier_username='"+sellernick+"' and  jdp_modified>'"+lastfxordertime+"'";
			this.getDao().execute(sql);
			int fxtradecount=this.getDao().intSelect("select count(*) from ##"+sellernick+"_tmp_fx_trade");
			if (fxtradecount>0){
				if(isMysql){
					batchid=this.getExtdao().IDGeneratorMysql("eco_rds_fx_trade", "batchid");
				}else
				batchid=this.getExtdao().IDGenerator("eco_rds_fx_trade", "batchid");
				sql = "update ##"+sellernick+"_tmp_fx_trade set batchid="+-1*batchid;
				this.getDao().execute(sql);
				prop.clear();
				prop.setProperty("eco_rds_fx_trade", "select * from ##"+sellernick+"_tmp_fx_trade");
				sql="select max(jdp_modified) from ##"+sellernick+"_tmp_fx_trade";		
				lastfxordertime=this.getDao().strSelect(sql);
				this.getDao().copyTo(this.getExtdao(), prop);
				sql= "update eco_rds_fx_trade set batchid="+batchid +" where batchid="+-1*batchid;
				this.getExtdao().execute(sql);
				sql="update eco_seller_config set lastfxordertime='"+lastfxordertime
				+"' where sellernick='"+sellernick+"'";
				this.getDao().execute(sql);
			
			Log.info(jobName+","+sellernick+",本次处理的分销订单数据是:　"+fxtradecount);
		}
		

		
		//处理分销退单
		//step="处理分销退单";
			sql="if object_id( 'tempdb..##"+sellernick+"_tmp_fx_refund') is not null  drop table ##"+sellernick+"_tmp_fx_refund";
			this.getDao().execute(sql);
			
			sql="select "+-1+" as batchid,cast(sub_order_id as varchar(32)) as sub_order_id,refund_status,supplier_nick,"
				+"distributor_nick,refund_create_time,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,"
				+"jdp_created,jdp_modified,0 as flag into ##"+sellernick+"_tmp_fx_refund from sys_info..jdp_fx_refund where supplier_nick='"+sellernick+"' and jdp_modified>'"+lastfxrefundtime+"'";		
			this.getDao().execute(sql);
			int fxrefundcount=this.getDao().intSelect("select count(*) from ##"+sellernick+"_tmp_fx_refund");
			if (fxrefundcount>0){
				if(isMysql){
					batchid=this.getExtdao().IDGeneratorMysql("eco_rds_fx_refund", "batchid");
				}else
				batchid=this.getExtdao().IDGenerator("eco_rds_fx_refund", "batchid");
				sql = "update ##"+sellernick+"_tmp_fx_refund set batchid="+-1*batchid;
				this.getDao().execute(sql);
				prop.clear();
				prop.setProperty("eco_rds_fx_refund", "select * from ##"+sellernick+"_tmp_fx_refund");
				sql="select max(jdp_modified) from ##"+sellernick+"_tmp_fx_refund";		
				lastfxrefundtime=this.getDao().strSelect(sql);
				this.getDao().copyTo(this.getExtdao(), prop);
				sql= "update eco_rds_fx_refund set batchid="+batchid +" where batchid="+-1*batchid;
				this.getExtdao().execute(sql);
				sql="update eco_seller_config set lastfxrefundtime='"+lastfxrefundtime
				+"' where sellernick='"+sellernick+"'";
				this.getDao().execute(sql);
			
			Log.info(jobName+","+sellernick+",本次处理的分销退单数据是:　"+fxrefundcount);
		}
			
			
			//处理经销订单
			//step="处理经销订单";
				sql="if object_id( 'tempdb..##"+sellernick+"_tmp_jx_trade') is not null  drop table ##"+sellernick+"_tmp_jx_trade";
				this.getDao().execute(sql);
				
				
				sql="select "+-1+" as batchid,cast(dealer_order_id as varchar(32)) as dealer_order_id,order_status,supplier_nick,"
					+"applier_nick,applied_time,modified_time,jdp_hashcode,isnull(jdp_response,'') as jdp_response,"
					+"jdp_created,jdp_modified,0 as flag into ##"+sellernick+"_tmp_jx_trade from sys_info..jdp_jx_trade where supplier_nick='"+sellernick+"' and jdp_modified>'"+lastjxordertime+"'";		
				this.getDao().execute(sql);
				int jxtradecount=this.getDao().intSelect("select count(*) from ##"+sellernick+"_tmp_jx_trade");
				if (jxtradecount>0){
					if(isMysql){
						batchid=this.getExtdao().IDGeneratorMysql("eco_rds_jx_trade", "batchid");
					}else
					batchid=this.getExtdao().IDGenerator("eco_rds_jx_trade", "batchid");
					sql = "update ##"+sellernick+"_tmp_jx_trade set batchid="+-1*batchid;
					this.getDao().execute(sql);
					prop.clear();
					prop.setProperty("eco_rds_jx_trade", "select * from ##"+sellernick+"_tmp_jx_trade");
					sql="select max(jdp_modified) from ##"+sellernick+"_tmp_jx_trade";		
					lastjxordertime=this.getDao().strSelect(sql);
					this.getDao().copyTo(this.getExtdao(), prop);
					sql= "update eco_rds_jx_trade set batchid="+batchid +" where batchid="+-1*batchid;
					this.getExtdao().execute(sql);
					sql="update eco_seller_config set lastjxordertime='"+lastjxordertime
					+"' where sellernick='"+sellernick+"'";
					this.getDao().execute(sql);
				
				Log.info(jobName+","+sellernick+",本次处理的经销订单数据是:　"+jxtradecount);
			}
		
		
		
		//处理天猫退货单
		//step="处理天猫退货单";
			sql="if object_id( 'tempdb..##"+sellernick+"_tmp_tm_return') is not null  drop table ##"+sellernick+"_tmp_tm_return";
			this.getDao().execute(sql);
			
			sql="select "+-1+" as batchid,cast(a.refund_id as varchar(32)) as refund_id,a.refund_phase,a.status,"
				+"a.sid,b.seller_nick,b.buyer_nick,cast(a.tid as varchar(32)) as tid,cast(a.oid as varchar(32)) as oid,a.created,"
				+"a.modified,a.jdp_hashcode,isnull(a.jdp_response,'') as jdp_response,"
				+"a.jdp_created,a.jdp_modified,0 as flag into ##"+sellernick+"_tmp_tm_return from sys_info..jdp_tm_return a,sys_info..jdp_tm_refund b "
				+"where a.tid=b.tid and a.oid=b.oid and b.seller_nick='"+sellernick+"' and a.jdp_modified>'"+lasttmreturntime+"'";
			this.getDao().execute(sql);
			int tmreturncount=this.getDao().intSelect("select count(*) from ##"+sellernick+"_tmp_tm_return");
			if (tmreturncount>0){
				batchid=this.getExtdao().IDGenerator("eco_rds_tm_return", "batchid");
				sql = "update ##"+sellernick+"_tmp_tm_return set batchid="+-1*batchid;
				this.getDao().execute(sql);
				prop.clear();
				prop.setProperty("eco_rds_tm_return", "select * from ##"+sellernick+"_tmp_tm_return");
				sql="select max(jdp_modified) from ##"+sellernick+"_tmp_tm_return";		
				lasttmreturntime=this.getDao().strSelect(sql);
				this.getDao().copyTo(this.getExtdao(), prop);
				sql= "update eco_rds_tm_return set batchid="+batchid +" where batchid="+-1*batchid;
				this.getExtdao().execute(sql);
				sql="update eco_seller_config set lasttmreturntime='"+lasttmreturntime
				+"' where sellernick='"+sellernick+"'";
				this.getDao().execute(sql);
			
			Log.info(jobName+","+sellernick+",本次处理的天猫退货单数据是:　"+tmreturncount);
		}
		

		//处理天猫退款单
		//step="处理天猫退款单";
			sql="if object_id( 'tempdb..##"+sellernick+"_tmp_tm_refund') is not null  drop table ##"+sellernick+"_tmp_tm_refund";
			this.getDao().execute(sql);
			
			sql="select "+-1+" as batchid,cast(refund_id as varchar(32)) as refund_id,refund_phase,status,"
				+"seller_nick,buyer_nick,cast(tid as varchar(32)) as tid,cast(oid as varchar(32)) as oid,created,modified,jdp_hashcode,isnull(jdp_response,'') as jdp_response,"
				+"jdp_created,jdp_modified,0 as flag into ##"+sellernick+"_tmp_tm_refund from sys_info..jdp_tm_refund "
				+"where seller_nick='"+sellernick+"' and jdp_modified>'"+lasttmrefundtime+"'";		
			this.getDao().execute(sql);
			int tmrefundcount=this.getDao().intSelect("select count(*) from ##"+sellernick+"_tmp_tm_refund");
			if (tmrefundcount>0){
				batchid=this.getExtdao().IDGenerator("eco_rds_tm_refund", "batchid");
				sql = "update ##"+sellernick+"_tmp_tm_refund set batchid="+-1*batchid;
				this.getDao().execute(sql);
				prop.clear();
				prop.setProperty("eco_rds_tm_refund", "select * from ##"+sellernick+"_tmp_tm_refund");
				sql="select max(jdp_modified) from ##"+sellernick+"_tmp_tm_refund";		
				lasttmrefundtime=this.getDao().strSelect(sql);
				this.getDao().copyTo(this.getExtdao(), prop);
				sql= "update eco_rds_tm_refund set batchid="+batchid +" where batchid="+-1*batchid;
				this.getExtdao().execute(sql);
				sql="update eco_seller_config set lasttmrefundtime='"+lasttmrefundtime
				+"' where sellernick='"+sellernick+"'";
				this.getDao().execute(sql);
				
				Log.info(jobName+","+sellernick+",本次处理的天猫退款单数据是:　"+tmrefundcount);
				
			}
		
			
			
		//写远程数据库
		

		/*step="处理远程数据";
		prop=new Properties();
		if (tradecount>0) prop.setProperty("eco_rds_trade", "select * from ##"+sellernick+"_tmp_trade");
		if (refundcount>0) prop.setProperty("eco_rds_refund", "select * from ##"+sellernick+"_tmp_refund");
		if (itemcount>0) prop.setProperty("eco_rds_item", "select * from ##"+sellernick+"_tmp_item");
		if (fxtradecount>0) prop.setProperty("eco_rds_fx_trade", "select * from ##"+sellernick+"_tmp_fx_trade");
		if (fxrefundcount>0) prop.setProperty("eco_rds_fx_refund", "select * from ##"+sellernick+"_tmp_fx_refund");
		if (tmreturncount>0) prop.setProperty("eco_rds_tm_return", "select * from ##"+sellernick+"_tmp_tm_return");
		if (tmrefundcount>0) prop.setProperty("eco_rds_tm_refund", "select * from ##"+sellernick+"_tmp_tm_refund");
		
		if (tradecount>0 
				|| refundcount>0 
				|| itemcount>0 
				|| fxtradecount>0 
				|| fxrefundcount>0
				|| tmreturncount>0 
				|| tmrefundcount>0)
			this.getDao().copyTo(this.getExtdao(), prop);
		

		
		if (tradecount>0)
		{
			sql="select max(jdp_modified) from ##"+sellernick+"_tmp_trade";		
			lastordertime=this.getDao().strSelect(sql);
		}
		
		if (refundcount>0)
		{
			sql="select max(jdp_modified) from ##"+sellernick+"_tmp_refund";		
			lastrefundtime=this.getDao().strSelect(sql);
		}
		
		if (itemcount>0)
		{
			sql="select max(jdp_modified) from ##"+sellernick+"_tmp_item";		
			lastitemtime=this.getDao().strSelect(sql);
		}
		
		if (fxtradecount>0)
		{
			sql="select max(jdp_modified) from ##"+sellernick+"_tmp_fx_trade";		
			lastfxordertime=this.getDao().strSelect(sql);
		}
		
		if (fxrefundcount>0)
		{
			sql="select max(jdp_modified) from ##"+sellernick+"_tmp_fx_refund";		
			lastfxrefundtime=this.getDao().strSelect(sql);
		}
		
		if (tmreturncount>0)
		{
			sql="select max(jdp_modified) from ##"+sellernick+"_tmp_tm_return";		
			lasttmreturntime=this.getDao().strSelect(sql);
		}
		
		if (tmrefundcount>0)
		{
			sql="select max(jdp_modified) from ##"+sellernick+"_tmp_tm_refund";		
			lasttmrefundtime=this.getDao().strSelect(sql);
		}
		
		step="更新商家配置时间";
		sql="update eco_seller_config set lastordertime='"+lastordertime
			+"',lastrefundtime='"+lastrefundtime+"',lastitemtime='"+lastitemtime+"',"
			+"lastfxordertime='"+lastfxordertime+"',lastfxrefundtime='"+lastfxrefundtime+"', "
			+"lasttmreturntime='"+lasttmreturntime+"',lasttmrefundtime='"+lasttmrefundtime+"' "
			+"where sellernick='"+sellernick+"'";
		
		this.getDao().execute(sql);
		Log.info(sellernick+", 取rds订单结束时间："+lastordertime);*/
		
		l = System.currentTimeMillis()-l;
		
		Log.info("花费时间(ms): " + l);
			
	}

}
 