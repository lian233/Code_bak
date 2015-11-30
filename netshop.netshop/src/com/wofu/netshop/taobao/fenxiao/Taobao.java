 package com.wofu.netshop.taobao.fenxiao;
import java.util.concurrent.CountDownLatch;
import com.wofu.base.job.Executer;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.common.fenxiao.GetConfig;
public class Taobao extends Executer{
	private String config;//参数列表
	private String jobName="分销淘宝店总任务";
	public void run(){
		CountDownLatch watch=null;
		Params param = new Params();
		try {
			updateJobFlag(1);
			config = getExecuteobj().getParams();
			//加载店配置参数
			GetConfig.taobaoinit(config,this.getDao().getConnection(),param);
			jobName = param.username+jobName;
			 watch= new CountDownLatch(param.jobCount);
			 //Log.info("count: "+param.jobCount);
			if(param.isNeedDelivery){  //发货
				new Thread(new DeliveryRunnable(watch,param)).start();
			}
			System.out.println("是否生成订单"+param.isGenCustomerOrder);
			if(param.isGenCustomerOrder){//生成客户订单
				new Thread(new GenCustomerOrderRunnable(watch,param)).start();
			}
			if(param.isgenCustomerRet){//生成客户退货订单
				new Thread(new GenCustomerRetRunnable(watch,param)).start();
			}if(param.isUpdateStock){//更新库存
				new Thread(new UpdateStockRunnable(watch,param)).start();
			}
			
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
		}finally{
			try {
				watch.await();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Log.error(jobName, "主线程等待错误: "+e1.getMessage());
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
				Log.error(jobName,"关闭数据库连接失败");
			}
			
			Log.info(jobName, "执行作业完成 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		}
	}
}
