/**
 * 把eco_rds_item表的数据复制到ecs_stockconfig及eco_stockconfigsku表
 */
package com.wofu.ecommerce.taobao;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class getItemsExecuter extends Executer {

	private String sellernick="";
	private String tradecontactid="";

	private final String jobName="处理淘宝商品资料作业";

	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		sellernick=prop.getProperty("sellernick");	
		tradecontactid=prop.getProperty("tradecontactid");
		
		String jdpresponse="";

		try {
			updateJobFlag(1);
			String sql="delete from eco_rds_item where flag=1 and nick='"+sellernick+"'";
			this.getDao().execute(sql);
			Log.info(sellernick+"-"+jobName+" ,删除已经处理的数据成功!");
			sql="select distinct batchid from eco_rds_item(nolock) where flag=0 and nick='"+sellernick+"' and batchid>0 order by batchid";
			
			List batchlist=this.getDao().oneListSelect(sql);
			System.out.println(sellernick+"的SQL语句 "+sql);
			System.out.println("本次获取的商品数量为"+batchlist.size());
			for(Iterator itbatch=batchlist.iterator();itbatch.hasNext();)
			{
				int batchid=(Integer) itbatch.next();
		
				sql="select jdp_response from eco_rds_item with(nolock) "
					+"where flag=0 and nick='"+sellernick+"' and batchid="+batchid;
				
				List responselist=this.getDao().oneListSelect(sql);
				
				for(Iterator itresponse=responselist.iterator();itresponse.hasNext();)
				{
					Item item=null;
					try{
						jdpresponse=(String) itresponse.next();
						
						item=new Item();
						
						JSONObject jsonobj=new JSONObject(jdpresponse);
						
						JSONObject itemjsobobj=jsonobj.getJSONObject("item_get_response").getJSONObject("item");
					
						item.setObjValue(item, itemjsobobj);
						if(jdpresponse.indexOf("title")==-1){
							ItemUtils.bakItem(jobName, this.getDao().getConnection(), batchid, item.getNum_iid());
							Log.error(jobName, "num_iid: "+item.getNum_iid()+", batchid: "+batchid+",此条数据标题为空，已备份到备份表");
							continue;
						}
						if(!itemjsobobj.isNull("skus")){
							JSONArray skus=itemjsobobj.getJSONObject("skus").getJSONArray("sku");
							item.setFieldValue(item, "skus", skus);
						}
						sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
						int orgid=this.getDao().intSelect(sql);
						
						RDSUtils.processItem(jobName,this.getDao(),item,orgid,Integer.parseInt(tradecontactid),sellernick);		
						
					}catch(Exception ex){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						Log.error(sellernick+"_"+jobName, "num_iid: "+item.getNum_iid());
						Log.error(sellernick+"_"+jobName,ex.getMessage());
					}
					
				}
				//更新处理标志
				try{
					sql = "update eco_rds_item set flag=1 where nick='"+sellernick+"' and batchid="+batchid;
					this.getDao().execute(sql);
				}catch(Exception ex){
					Log.info(jobName+",处理更新标志失败：sellernick: "+sellernick+"batchId: "+batchid);
				}
				

			}
			
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
 