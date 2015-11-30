package com.wofu.intf.yjn;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
/**
 * 
 * @author Administrator
 *
 */
public class PaymentExecuter extends Executer {
	private static String jobName="��ʱ����֧������������";
	public void run() {
		Properties pro = StringUtil.getStringProperties(this.getExecuteobj().getParams());
		String connection =pro.getProperty("conn");
		try {
			updateJobFlag(1);
			String sql="select top 10 orderNo,status,message from paymentinterface with(nolock) where flag=0 order by createtime";
			Vector vtintf=this.getDao().multiRowSelect(sql);
			Log.info(jobName+"������Ҫ�������������Ϊ:��"+vtintf.size());
			
			for (int i=0;i<vtintf.size();i++)
			{
				
				Hashtable htintf=(Hashtable) vtintf.get(i);
				Object status1=htintf.get("status");//״̬
				String status=status1!=null?status1.toString():"";
				Object orderNo1=htintf.get("orderNo");//���
				String orderNo=orderNo1!=null?orderNo1.toString():"";
				Object message1=htintf.get("message");//��Ϣ��ϸ
				String message=message1!=null?message1.toString():"";
				
				sql ="SELECT SerialID FROM "+connection+"..corresponding where id='"+orderNo+"'";
				String SerialID = this.getDao().strSelect(sql);
				if(SerialID.equals("")){
					continue;
				}
				sql="update "+connection+"..Inf_DownNotebak set  flag ='100',result='"+status+"' where SerialID='"+SerialID+"'";
				this.getDao().execute(sql);
				sql="update paymentinterface set  flag ='1' where orderNo='"+orderNo+"'";
				this.getDao().execute(sql);
				this.getDao().commit();
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
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
		
		
	}
	
	
	
}
