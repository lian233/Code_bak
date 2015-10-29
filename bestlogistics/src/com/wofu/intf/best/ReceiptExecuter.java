package com.wofu.intf.best;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
/**
 * �ֿ�����󣬷������Żش���ϵͳ
 * @author Administrator
 *com.wofu.intf.best.ReceiptExecuter
 */
public class ReceiptExecuter extends Executer {
	private static String jobName="��ʱ�������������������";
	private Boolean isBarcodeId=false;
	private String vertifycode="";
	private boolean warehouseMulti=false;
	
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		isBarcodeId=Boolean.parseBoolean(prop.getProperty("isBarcodeId"));
		vertifycode=prop.getProperty("vertifycode");
		warehouseMulti=Boolean.parseBoolean(prop.getProperty("warehouseMulti","false"));

		try {			 
			updateJobFlag(1);
		
			String sql="select top 5000 msgid,partnerid,servicetype,bizdata ,receivedate,interfaceSystem "
				+"from ecs_bestlogisticsinterface with(nolock) where flag=0 and servicetype<>'UpdateInventoryChange' order by receivedate";
			Vector vtintf=this.getDao().multiRowSelect(sql);
			Log.info(jobName+"������Ҫ�������������Ϊ:��"+vtintf.size());
			for (int i=0;i<vtintf.size();i++)
			{
				Hashtable htintf=(Hashtable) vtintf.get(i);
				Object msgid1=htintf.get("msgid");
				String msgid=msgid1!=null?msgid1.toString():"";
				Object partnerid1=htintf.get("partnerid");
				String partnerid=partnerid1!=null?partnerid1.toString():"";
				Object servicetype1=htintf.get("servicetype");
				String servicetype=servicetype1!=null?servicetype1.toString():"";
				Object bizdata1=htintf.get("bizdata");
				String bizdata=bizdata1!=null?bizdata1.toString():"";
				Object receivedate1=htintf.get("receivedate");
				String receivedate=receivedate1!=null?receivedate1.toString():"";
				Object interfaceSystem1=htintf.get("interfaceSystem");
				String interfaceSystem=interfaceSystem1!=null?interfaceSystem1.toString():"";
				ECSDao dao = (ECSDao)this.getDao();
				String processorClassName = Processors.getProcessor(servicetype);
				BizProcessor processor = (BizProcessor) Class.forName(processorClassName).newInstance();
				processor.setBizData(bizdata);
				processor.setConnection(dao.getConnection());
				processor.setInterfaceSystem(interfaceSystem);
				processor.setIsBarcodeId(isBarcodeId);
				processor.setVertifycode(vertifycode);
				processor.setWarehouseMulti(warehouseMulti);
				dao.setTransation(false);
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
				

				
				sql="update ecs_bestlogisticsinterface set flag=1,processdate=getdate() where msgid='"+msgid+"'";
				dao.execute(sql);
				
				dao.commit();
				dao.setTransation(true);
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
