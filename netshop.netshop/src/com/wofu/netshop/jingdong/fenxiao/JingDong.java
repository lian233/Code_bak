 package com.wofu.netshop.jingdong.fenxiao;
import java.util.concurrent.CountDownLatch;
import com.wofu.base.job.Executer;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.common.fenxiao.GetConfig;
import com.wofu.netshop.jingdong.fenxiao.GetOrdersRunnable;
import com.wofu.netshop.jingdong.fenxiao.Params;
public class JingDong extends Executer{
	private String config;//�����б�
	private String jobName="����������������";
	public void run(){
		CountDownLatch watch=null;
		Params param = new Params();
		try {
			updateJobFlag(1);
			config = getExecuteobj().getParams();
			//���ص����ò���
			GetConfig.jingdonginit(config,this.getDao().getConnection(),param);
			jobName = param.username.trim()+jobName;
			 watch= new CountDownLatch(param.jobCount);
			if(param.isNeedDelivery){  //����
				new Thread(new DeliveryRunnable(watch,param)).start();
			}
			if(param.isGenCustomerOrder){//���ɿͻ�����
				new Thread(new GenCustomerOrderRunnable(watch,param)).start();
			}
//			if(param.isgenCustomerRet){//���ɿͻ��˻�����
//				new Thread(new GenCustomerRetRunnable(watch,param)).start();
//			}
			if(param.isgetOrder){//���ض���
				new Thread(new GetOrdersRunnable(watch,param)).start();
			}
//			if(param.isUpdateStock){//�������¿��
//				new Thread(new UpdateStockRunnable(watch,param)).start();
//			}
			
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
		}finally{
			try {
				watch.await();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Log.error(jobName, "���̵߳ȴ�����: "+e1.getMessage());
			}
			try {
				UpdateTimerJob();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				updateJobFlag(0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.error(jobName, e.getMessage());
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
			
			Log.info(jobName, "ִ����ҵ��� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		}
	}
}
