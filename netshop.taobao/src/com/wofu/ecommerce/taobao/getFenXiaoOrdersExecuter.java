package com.wofu.ecommerce.taobao;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class getFenXiaoOrdersExecuter extends Executer {
	private String sellernick="";
	private String tradecontactid="";
	private final String jobName="处理淘宝分销订单作业";
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		sellernick=prop.getProperty("sellernick");	
		tradecontactid=prop.getProperty("tradecontactid");
		
		String jdpresponse="";

		try {			 
			updateJobFlag(1);

			//删除已经处理过的
			
			String sql="delete from eco_rds_fx_trade where supplier_username='"+sellernick+"' and flag=1 and modified<dateadd(dd,-15,getdate())";
			this.getDao().execute(sql);
			Log.info(jobName+"-"+sellernick+"-删除已经处理的数据成功!");
			
			sql="select distinct batchid from eco_rds_fx_trade with(nolock) where flag=0 and supplier_username='"+sellernick+"' and batchid>0 order by batchid";
			
			List batchlist=this.getDao().oneListSelect(sql);
			Log.info(jobName+"-"+sellernick+"本次要处理的数据条数为: "+batchlist.size());
			for(Iterator itbatch=batchlist.iterator();itbatch.hasNext();)
			{
				int batchid=(Integer) itbatch.next();
		
				sql="select jdp_response from eco_rds_fx_trade with(nolock) "
					+"where flag=0 and supplier_username='"+sellernick+"' and batchid="+batchid;
				
				List responselist=this.getDao().oneListSelect(sql);
				
				for(Iterator itresponse=responselist.iterator();itresponse.hasNext();)

				{
					PurchaseOrder purchaseorder=null;
					try{
						jdpresponse=(String) itresponse.next();
						purchaseorder=new PurchaseOrder();
						JSONObject jsonobj=new JSONObject(jdpresponse);
						JSONObject purchaseorderjsonobj=jsonobj.getJSONObject("fenxiao_orders_get_response").getJSONObject("purchase_orders").getJSONArray("purchase_order").getJSONObject(0);
						if(!purchaseorderjsonobj.isNull("receiver")){
							JSONObject receiverjsonobj=purchaseorderjsonobj.getJSONObject("receiver");

							if (!receiverjsonobj.isNull("address"))
							purchaseorder.getReceiverinfo().setAddress(receiverjsonobj.getString("address").replaceAll("'", ""));
							if (!receiverjsonobj.isNull("state"))
							purchaseorder.getReceiverinfo().setState(receiverjsonobj.getString("state"));
							if (!receiverjsonobj.isNull("city"))
							purchaseorder.getReceiverinfo().setCity(receiverjsonobj.getString("city").replaceAll("'", ""));

							purchaseorder.setObjValue(purchaseorder, purchaseorderjsonobj);

							if (!receiverjsonobj.isNull("name"))
								purchaseorder.getReceiverinfo().setName(receiverjsonobj.getString("name").replaceAll("'", ""));
			
							if (!receiverjsonobj.isNull("district"))
								purchaseorder.getReceiverinfo().setDistrict(receiverjsonobj.getString("district").replaceAll("'", ""));	

							if (!receiverjsonobj.isNull("phone"))
								purchaseorder.getReceiverinfo().setPhone(receiverjsonobj.getString("phone"));
							if (!receiverjsonobj.isNull("address"))
							purchaseorder.getReceiverinfo().setAddress(receiverjsonobj.getString("address").replaceAll("'", ""));
							if (!receiverjsonobj.isNull("state"))
							purchaseorder.getReceiverinfo().setState(receiverjsonobj.getString("state"));
							if (!receiverjsonobj.isNull("city"))
							purchaseorder.getReceiverinfo().setCity(receiverjsonobj.getString("city").replaceAll("'", ""));

							if (!receiverjsonobj.isNull("mobile_phone"))						
								purchaseorder.getReceiverinfo().setMobile_phone(receiverjsonobj.getString("mobile_phone"));
							if (!receiverjsonobj.isNull("zip"))	
								purchaseorder.getReceiverinfo().setZip(receiverjsonobj.getString("zip"));
						}
						if(!purchaseorderjsonobj.isNull("sub_purchase_orders")){
							JSONArray suborders=purchaseorderjsonobj.getJSONObject("sub_purchase_orders").getJSONArray("sub_purchase_order");
							
							purchaseorder.setFieldValue(purchaseorder, "sub_purchase_orders", suborders);
						}
						RDSUtils.processFenXiaoOrder(jobName,this.getDao(),purchaseorder,tradecontactid,sellernick);		
						sql="update eco_rds_fx_trade set flag=1 "
							+"where flag=0 and supplier_username='"+sellernick+"' and batchid="+batchid+" and fenxiao_id='"+purchaseorder.getFenxiao_id()+"'";
						this.getDao().execute(sql);
					}catch(Exception ex){
						Log.error(jobName,sellernick+" "+jobName+" 发生异常，分销id: "+purchaseorder.getFenxiao_id()+" ,batchid: "+batchid+" 异常信息: "+ex.getMessage());
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						continue;
					}
					
				}
						
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
 