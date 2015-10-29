package com.wofu.intf.chinapay;
import java.util.Hashtable;
import java.util.Vector;
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
	private static String jobName="��ʱ��������֧����������";
	public void run() {

		try {			 
			updateJobFlag(1);
			String sql="select top 5000 msgid,transtype,merid,orderno,status,amount,transdate,currencycode,	GateId,"
				+" checkvalue,Priv1 from chinapaymentinfo with(nolock) where flag=0 order by receivedate";
			Vector vtintf=this.getDao().multiRowSelect(sql);
			Log.info(jobName+"������Ҫ�������������Ϊ:��"+vtintf.size());
			for (int i=0;i<vtintf.size();i++)
			{
				Hashtable<String,String> htintf=(Hashtable) vtintf.get(i);
				Object msgid1=htintf.get("msgid");
				String msgid=msgid1!=null?msgid1.toString():"";
				Object transtype1=htintf.get("transtype");
				String transtype=transtype1!=null?transtype1.toString():"";
				ECSDao dao = (ECSDao)this.getDao();
				ECSDao extDao = (ECSDao)this.getExtdao();
				String processorClassName = Processors.getProcessor(transtype);
				ChinaPaymentProcess processor = (ChinaPaymentProcess) Class.forName(processorClassName).newInstance();
				processor.setAmount(htintf.get("amount"));
				processor.setTranstype(transtype);
				processor.setCheckvalue(htintf.get("checkvalue"));
				processor.setCurrencycode(htintf.get("currencycode"));
				processor.setGateId(htintf.get("GateId"));
				processor.setMerid(htintf.get("merid"));//merid
				processor.setOrderno(htintf.get("orderno"));
				processor.setPriv1(htintf.get("Priv1"));
				processor.setStatus(htintf.get("status"));
				processor.setMsgId(msgid);
				processor.setTransdate(htintf.get("transdate"));
				dao.setTransation(false);
				extDao.setTransation(false);
				processor.setConn(dao.getConnection());
				processor.setExtConn(extDao.getConnection());
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
				
				sql="update chinapaymentinfo set flag=1,processdate=getdate(), notes='"+processor.getNotes()+"' where msgid='"+msgid+"'";
				dao.execute(sql);
				dao.commit();
				extDao.commit();
				dao.setTransation(true);
				extDao.setTransation(true);
				
				
			}
			
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
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
