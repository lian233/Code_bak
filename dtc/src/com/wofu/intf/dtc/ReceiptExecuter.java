package com.wofu.intf.dtc;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

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
public class ReceiptExecuter extends Executer {
	private static String jobName="��ʱ����羳���̷�������";
	public void run() {
		try {
			updateJobFlag(1);
			String sql="select top 5000 msgid,servicetype,bizdata  "
				+"from dtcinterfaceinfo with(nolock) where flag=0 order by receivedate";
			Vector vtintf=this.getDao().multiRowSelect(sql);
			Log.info(jobName+"������Ҫ�������������Ϊ:��"+vtintf.size());
			for (int i=0;i<vtintf.size();i++)
			{
				
				Hashtable htintf=(Hashtable) vtintf.get(i);
				System.out.println("���ű�ʾ"+htintf.get("msgid"));
				Object msgid1=htintf.get("msgid");
				String msgid=msgid1!=null?msgid1.toString():"";
				Object servicetype1=htintf.get("servicetype");
				String servicetype=(servicetype1!=null)?servicetype1.toString():"";
				Object bizdata1=htintf.get("bizdata");
				String bizdata=(bizdata1!=null)?bizdata1.toString():"";
				ECSDao dao = (ECSDao)this.getDao();
				if(servicetype.equals("")||bizdata.equals("")){
					continue;
				}
				String processorClassName = Processors.getProcessor(servicetype);
				DtcProcess processor = (DtcProcess) Class.forName(processorClassName).newInstance();
				processor.setBizdata(bizdata);
				processor.setExtConnId(this.getExecuteobj().getDsid());
				processor.setConn(dao.getConnection());
				try{
					processor.process();
				}catch(Exception ex){
					Log.error(jobName+"�������ݳ���: ", "msgid: "+msgid );
					Log.error("������ϸ��Ϣ:��", ex.getMessage());
					if (this.getConnection() != null && !this.getConnection().getAutoCommit())
						this.getConnection().rollback();
					
					if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
						this.getExtconnection().rollback();
					continue;
					
				}
				
				sql="update dtcinterfaceinfo set flag=1,processdate=getdate() where msgid='"+msgid+"'";
				dao.execute(sql);
				dao=null;
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
