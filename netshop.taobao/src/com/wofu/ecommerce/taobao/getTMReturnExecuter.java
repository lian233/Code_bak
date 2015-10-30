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

public class getTMReturnExecuter extends Executer {

	private String sellernick="";
	private String tradecontactid="";

	
	private final String jobName="������è�˻�����ҵ";

	@Override
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		sellernick=prop.getProperty("sellernick");	
		tradecontactid=prop.getProperty("tradecontactid");


		try {			 
			
			updateJobFlag(1);
			//ɾ���Ѿ��������
			String sql="delete from eco_rds_tm_return where flag=1";
			this.getDao().execute(sql);
			Log.info("ɾ���Ѿ���������ݳɹ�!");
			
			sql="update eco_rds_tm_return set flag=0 where seller_nick='"+sellernick+"' and flag=-1";
			this.getDao().execute(sql);
			
			sql="select batchid from eco_rds_tm_return with(nolock) where flag=0 and seller_nick='"+sellernick+"' order by batchid";
			
			List batchlist=this.getDao().oneListSelect(sql);
			
			for(Iterator itbatch=batchlist.iterator();itbatch.hasNext();)
			{
				int batchid=(Integer) itbatch.next();
				
				sql="select jdp_response from eco_rds_tm_return with(nolock) "
					+"where flag=0 and seller_nick='"+sellernick+"' and batchid="+batchid;
				
				List responselist=this.getDao().oneListSelect(sql);
				
				for(Iterator itresponse=responselist.iterator();itresponse.hasNext();)
				{
					String jdpresponse=(String) itresponse.next();
					
					NS_ReturnBill returnbill=new NS_ReturnBill();
					
					JSONObject jsonobj=new JSONObject(jdpresponse);
					
					JSONObject returnjsobobj=jsonobj.getJSONObject("tmall_eai_order_refund_good_return_get_response").getJSONObject("return_bill");
					
					returnbill.setObjValue(returnbill, returnjsobobj);
					
					returnbill.setDescription(returnjsobobj.optString("desc"));
					
					JSONArray itemlist=returnjsobobj.getJSONObject("item_list").getJSONArray("refund_item");
					
					returnbill.setFieldValue(returnbill, "item_list", itemlist);
					
					JSONArray tags=returnjsobobj.optJSONObject("tag_list").getJSONArray("tag");  
					
					String taglist="";
					for (int i=0;i<tags.length();i++)
					{
						JSONObject tag=(JSONObject) tags.get(i);
						
						if (tag.opt("tag_key")!=null)
							taglist.concat(tag.optString("tag_key")+"|"+tag.optString("tag_name")+"|"+tag.optString("tag_type"));
						
						if (i!=tags.length()-1) taglist.concat(",");
					}
					returnbill.setTags(taglist);

					RDSUtils.processReturnBill(jobName,this.getDao(),returnbill,tradecontactid,sellernick);	
					
					sql="update eco_rds_tm_return set flag=1 "
						+"where flag=0 and seller_nick='"+sellernick+"' and batchid="+batchid+" and refund_id='"+returnbill.getRefund_id()+"'";
					this.getDao().execute(sql);
				}

				
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
 