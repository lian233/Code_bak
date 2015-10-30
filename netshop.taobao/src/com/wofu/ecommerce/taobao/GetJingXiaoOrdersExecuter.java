package com.wofu.ecommerce.taobao;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.common.tools.util.Formatter;
public class GetJingXiaoOrdersExecuter extends Executer {
	private String sellernick="";
	private String tradecontactid="";
	private final String jobName="�����Ա�����������ҵ";
	private final long daymili=24*60*60*1000L;
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		sellernick=prop.getProperty("sellernick");	
		tradecontactid=prop.getProperty("tradecontactid");
		
		String jdpresponse="";

		try {			 
			updateJobFlag(1);

			//ɾ���Ѿ�15��ǰ�������
			String sql="delete from eco_rds_jx_trade where flag=1 and supplier_nick='"+sellernick+"' and jdp_modified<dateadd(dd,-15,getdate())";
			this.getDao().execute(sql);
			Log.info(jobName+"-"+sellernick+"-ɾ���Ѿ���������ݳɹ�!");
			
			sql="select distinct batchid from eco_rds_jx_trade with(nolock) where flag=0 and supplier_nick='"+sellernick+"' and batchid>0 order by batchid";
			
			List batchlist=this.getDao().oneListSelect(sql);
			Log.info(jobName+"-"+sellernick+"����Ҫ�������������Ϊ: "+batchlist.size());
			for(Iterator itbatch=batchlist.iterator();itbatch.hasNext();)
			{
				int batchid=(Integer) itbatch.next();
		
				sql="select jdp_response from eco_rds_jx_trade with(nolock) "
					+"where flag=0 and supplier_nick='"+sellernick+"' and batchid="+batchid;
				
				List responselist=this.getDao().oneListSelect(sql);
				
				for(Iterator itresponse=responselist.iterator();itresponse.hasNext();)
				{
					Dealer_order dealer_order=null;
					try{
						jdpresponse=(String) itresponse.next();
						dealer_order=new Dealer_order();
						JSONObject jsonobj=new JSONObject(jdpresponse);
						JSONObject purchaseorderjsonobj=jsonobj.getJSONObject("fenxiao_dealer_requisitionorder_query_response").getJSONObject("dealer_orders").getJSONArray("dealer_order").getJSONObject(0);
						if(!purchaseorderjsonobj.isNull("receiver")){
							JSONObject receiverjsonobj=purchaseorderjsonobj.getJSONObject("receiver");

							if (!receiverjsonobj.isNull("address"))
								dealer_order.getReceiverinfo().setAddress(receiverjsonobj.getString("address").replaceAll("'", ""));
							if (!receiverjsonobj.isNull("state"))
								dealer_order.getReceiverinfo().setState(receiverjsonobj.getString("state"));
							if (!receiverjsonobj.isNull("city"))
								dealer_order.getReceiverinfo().setCity(receiverjsonobj.getString("city").replaceAll("'", ""));

							dealer_order.setObjValue(dealer_order, purchaseorderjsonobj);
							if (!receiverjsonobj.isNull("name"))
								dealer_order.getReceiverinfo().setName(receiverjsonobj.getString("name").replaceAll("'", ""));
			
							if (!receiverjsonobj.isNull("district"))
								dealer_order.getReceiverinfo().setDistrict(receiverjsonobj.getString("district").replaceAll("'", ""));	

							if (!receiverjsonobj.isNull("phone"))
								dealer_order.getReceiverinfo().setPhone(receiverjsonobj.getString("phone"));


							if (!receiverjsonobj.isNull("mobile_phone"))						
								dealer_order.getReceiverinfo().setMobile_phone(receiverjsonobj.getString("mobile_phone"));
							if (!receiverjsonobj.isNull("zip"))	
								dealer_order.getReceiverinfo().setZip(receiverjsonobj.getString("zip"));
						}
						if(!purchaseorderjsonobj.isNull("dealer_order_details")){
							JSONArray subjxorders=purchaseorderjsonobj.getJSONObject("dealer_order_details").getJSONArray("dealer_order_detail");
							
							dealer_order.setFieldValue(dealer_order, "dealer_order_details", subjxorders);
						}
						RDSUtils.processJingXiaoOrder(jobName,this.getDao(),dealer_order,tradecontactid,sellernick);		
						
					}catch(Exception ex){
						Log.error(jobName,sellernick+" "+jobName+" �����쳣������id: "+dealer_order.getDealer_order_id()+" ,batchid: "+batchid+" �쳣��Ϣ: "+ex.getMessage());
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						continue;
					}
					
				}
				sql="update eco_rds_jx_trade set flag=1 "
					+"where flag=0 and supplier_nick='"+sellernick+"' and batchid="+batchid;
				this.getDao().execute(sql);
				
						
			}
			
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
			
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
				Log.error(jobName, e1.getMessage());
			}
			
			try{
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			}catch(Exception ex){
				Log.error(jobName,"����������Ϣʧ��");
				Log.error(jobName, ex.getMessage());
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"���´����־ʧ��");
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
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}

	}

}
 