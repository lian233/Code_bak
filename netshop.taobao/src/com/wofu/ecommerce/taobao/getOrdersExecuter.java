package com.wofu.ecommerce.taobao;
/**
 * 处理淘宝订单作业  把订单数据从eco_rds_trade 转移到接口表中  没有调用api
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class getOrdersExecuter extends Executer {

	private String sellernick="";
	private String tradecontactid="";
	private boolean waitbuyerpayisin=false;
	private boolean finishisin=false;
	private boolean isJzservice=false;  //是不是家装店
	
	private String jobName="处理淘宝订单作业";

	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		sellernick=prop.getProperty("sellernick");	
		tradecontactid=prop.getProperty("tradecontactid");
		boolean isc=Boolean.parseBoolean(prop.getProperty("isc"));
		boolean finishisin=Boolean.parseBoolean(prop.getProperty("finishisin","false"));
		isJzservice=Boolean.parseBoolean(prop.getProperty("isJzservice","false"));
		jobName=sellernick+jobName;
		String jdpresponse=null;
		String sql=null;
		List batchlist=null;
		Trade td=null;

		try {			 
			updateJobFlag(1);
			
			sql="select isnull(value,0) from config where name='等待付款订单是否进系统'";
			if (this.getDao().strSelect(sql).equals("1"))
				waitbuyerpayisin=true;
			//删除已经处理过的
			sql="delete from eco_rds_trade where seller_nick='"+sellernick+"' and flag=1  and jdp_modified<dateadd(dd,-15,getdate())";
			this.getDao().execute(sql);
			Log.info(jobName+" 删除已经处理的数据成功!");
			sql="select distinct batchid from eco_rds_trade where  flag=0 and seller_nick='"+sellernick+"' and batchid>0 order by batchid";
			
			batchlist=this.getDao().oneListSelect(sql);
			Log.info(jobName+","+sellernick+"本次处理的数据批次条数为:　"+batchlist.size());
			ArrayList<Long> errMsg=null;
			for(Iterator itbatch=batchlist.iterator();itbatch.hasNext();)
			{
				int batchid=(Integer) itbatch.next();
				sql="select jdp_response from eco_rds_trade with(nolock) "
					+"where batchid="+batchid;
				
				List responselist=this.getDao().oneListSelect(sql);
				errMsg = new ArrayList<Long>();;
				for(Iterator itresponse=responselist.iterator();itresponse.hasNext();)
				{	
					jdpresponse=(String) itresponse.next();
					try{
						td=new Trade();
						JSONObject jsonobj=new JSONObject(jdpresponse);
						JSONObject tradejsobobj=jsonobj.getJSONObject("trade_fullinfo_get_response").getJSONObject("trade");
						td.setObjValue(td, tradejsobobj);
						if(jdpresponse.indexOf("orders")==-1){
							OrderUtils.bakOrderItem(jobName, this.getDao().getConnection(), batchid, td.getTid());
							Log.error(jobName, "订单数据不完整，缺少'orders'字段，已备份表备份表");
							continue;
						}
						JSONArray orders=tradejsobobj.getJSONObject("orders").getJSONArray("order");
						td.setFieldValue(td, "orders", orders);
						if(!tradejsobobj.isNull("service_orders"))
							td.setFieldValue(td, "service_orders", tradejsobobj.getJSONObject("service_orders").getJSONArray("service_order"));
						RDSUtils.processOrder(jobName,this.getDao(),td,tradecontactid,sellernick,waitbuyerpayisin,isc,isJzservice,finishisin);
					}catch(Exception ex){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						Log.error(jobName,sellernick+" "+jobName+" 发生异常，tid: "+td.getTid()+" ,batchid: "+batchid+" 异常信息: "+sw.toString());
						errMsg.add(td.getTid());
						sw=null;
						continue;
					}
							
				}
				if(errMsg.size()==0){
					sql="update eco_rds_trade set flag=1 "
						+"where batchid="+batchid;
				}
				else{
					StringBuilder sqlTemp = new StringBuilder("update eco_rds_trade set flag=1 where batchid=").append(batchid)
						.append(" and tid not in(");
					for(Iterator it = errMsg.iterator();it.hasNext();){
						sqlTemp.append("'").append(it.next()).append("',");
					}
					sqlTemp.deleteCharAt(sqlTemp.length()-1).append(")");
					sql = sqlTemp.toString();
					Log.info("未处理订单: "+sql);
				}
				try{
					int success=this.getDao().execute(sql);
					if(success>0){
						Log.info(sellernick+" "+jobName+"更改标志位成功，batchid="+batchid);
					}else{
						Log.info(sellernick+" "+jobName+"更改标志位失败，batchid="+batchid);
					}
				}catch(Exception ex){
					Log.error(jobName, sellernick+",更改标志位错误, batchid:"+batchid);
				}
			}
			Log.info(sellernick+" :"+jobName+"处理完毕");
			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit()){
					this.getConnection().rollback();
					this.getConnection().setAutoCommit(true);
				}
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit()){
					this.getExtconnection().rollback();
					this.getExtconnection().setAutoCommit(true);
				}
				
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
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.error(jobName,"错误信息:"+sw.toString());
			
			Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			sw=null;
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
			sql=null;
			jdpresponse=null;
			batchlist=null;
			td = null;
		}
		
		

	}

}
 