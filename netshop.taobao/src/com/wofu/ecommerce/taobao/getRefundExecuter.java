package com.wofu.ecommerce.taobao;
/**
 * �����Ա��˻�����ҵ   ���˻�������eco_rds_fundת�Ƶ��ӿڱ���  û�е���api
 */
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.wofu.common.json.JSONObject;
import com.wofu.common.pool.TinyConnection;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.ECS_TimerPolicy;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.common.pool.TinyConnection;
public class getRefundExecuter extends Executer {

	private String sellernick="";
	private String tradecontactid="";
	private final String jobName="�����Ա��˻�����ҵ";

	@Override
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		sellernick=prop.getProperty("sellernick");	
		tradecontactid=prop.getProperty("tradecontactid");


		try {			 
			updateJobFlag(1);
			//ɾ���Ѿ��������
			String sql="delete from eco_rds_refund where flag=1 and seller_nick='"+sellernick+"' and jdp_modified<DATEADD(dd,-15,getdate())";
			this.getDao().execute(sql);
			Log.info(sellernick+" "+jobName+" ɾ���Ѿ���������ݳɹ�!");
			
			sql="update eco_rds_refund set flag=0 where seller_nick='"+sellernick+"' and flag=-1";
			this.getDao().execute(sql);
			
			sql="select distinct batchid from eco_rds_refund  where flag=0 and seller_nick='"+sellernick+"' and batchid>0  order by batchid";
			//TinyConnection conn=(TinyConnection)this.getConnection();
			//conn.close2();
			List batchlist=this.getDao().oneListSelect(sql);
			for(Iterator itbatch=batchlist.iterator();itbatch.hasNext();)
			{
				int batchid=(Integer) itbatch.next();
				
				sql="select jdp_response from eco_rds_refund with(nolock) "
					+"where flag=0 and seller_nick='"+sellernick+"' and batchid="+batchid;
				
				List responselist=this.getDao().oneListSelect(sql);
				
				for(Iterator itresponse=responselist.iterator();itresponse.hasNext();)
				{
					String jdpresponse=(String) itresponse.next();
					
					Refund r=new Refund();
					
					JSONObject jsonobj=new JSONObject(jdpresponse);
					
					JSONObject refundjsobobj=jsonobj.getJSONObject("refund_get_response").getJSONObject("refund");
					
					r.setObjValue(r, refundjsobobj);
					
					try{
						RDSUtils.processRefund(jobName,this.getDao(),r,tradecontactid,sellernick);
					}catch(Exception ex){
						Log.info(sellernick+" "+jobName+" �����쳣��tid: "+r.getTid()+" ,batchid: "+batchid+" �쳣��Ϣ: "+ex.getMessage());
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						continue;
					}
						
				}
				try{
					sql="update eco_rds_refund set flag=1 "
						+"where seller_nick='"+sellernick+"' and batchid="+batchid;
					this.getDao().execute(sql);
				}catch(Exception ex){
					Log.error(jobName, sellernick+",���ı�־λ����: batchid: "+batchid);
				}
				
			}
			Log.info(sellernick+" "+jobName+" ���ݴ���ɹ�!");
			
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
 