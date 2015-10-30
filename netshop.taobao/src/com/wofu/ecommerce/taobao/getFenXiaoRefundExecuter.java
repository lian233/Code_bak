package com.wofu.ecommerce.taobao;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;

public class getFenXiaoRefundExecuter extends Executer {

	private String sellernick="";
	private String tradecontactid="";

	
	private final String jobName="��������˻�����ҵ";

	@Override
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		sellernick=prop.getProperty("sellernick");	
		tradecontactid=prop.getProperty("tradecontactid");
		
		long sub_order_id=0;
		int batchid=0;
		
/*			
		//���json
		Log.info("�������˻�����ҵ��ʼ");
		Vector responselistc = null;
		try{
			String csql="select cast(sub_order_id as varchar(32)) as sub_order_id , batchid ,jdp_response "
				+"from eco_rds_fx_refund where flag=0 and sub_order_id= 3286534677356";

			responselistc=this.getDao().multiRowSelect(csql);

		}
		catch(Exception ex)
		{}
		

		if (null==responselistc){
			System.out.println("responselistcΪ��");
		}
		else {
			String msg = "";
			for(Iterator itresponse=responselistc.iterator();itresponse.hasNext();)
			{
				Hashtable jdpresponse=(Hashtable) itresponse.next();
				
				String jdpresponsestr=jdpresponse.get("jdp_response").toString();
				sub_order_id=Long.valueOf(jdpresponse.get("sub_order_id").toString()).longValue();
				batchid=Integer.valueOf(jdpresponse.get("batchid").toString()).intValue();
				
				try{
					msg="1" ;
					RefundDetail r=new RefundDetail();
					msg="2" ;
					JSONObject jsonobj=new JSONObject(jdpresponsestr);		
					msg="3" ;
					JSONObject refunddetailjsobobj=jsonobj.getJSONObject("fenxiao_refund_get_response").getJSONObject("refund_detail");
					msg="4" ;
					r.setObjValue(r, refunddetailjsobobj);
					msg="5" ;
					System.out.println("json�ɹ���sub_order_id��["+ Long.toString(sub_order_id)  
							+"]batchid:["+Long.toString(batchid)+"]msg:["+msg+"]");
				}
				catch (Exception e) {
					System.out.println("jsonʧ�ܣ�sub_order_id��["+ Long.toString(sub_order_id)  
							+"]batchid:["+Long.toString(batchid)+"]msg:["+msg+"], " + e.getMessage());
					
					try{
						String psql="insert into eco_rds_fx_refund_bad select * from eco_rds_fx_refund where flag=0 and sub_order_id="
							+Long.toString(sub_order_id) + " and batchid = " + Long.toString(batchid);
						this.getDao().execute(psql);
	
						psql="delete from eco_rds_fx_refund where flag=0 and sub_order_id="
							+Long.toString(sub_order_id) + " and batchid = " + Long.toString(batchid);
						this.getDao().execute(psql);
					}
					catch(Exception ex)
					{
						System.out.println("����json����ʧ�ܣ�" + ex.getMessage());
					}
				}
				
			}	
		}
		
		//return ;
*/		
		
		String breakpoint="";
		try {			 
			Log.info("��������˻�����ҵ��ʼ");
			updateJobFlag(1);
			
			//ɾ���Ѿ��������
			String sql="delete from eco_rds_fx_refund where flag=1 and supplier_nick='"
				+sellernick+"' and modified<dateadd(dd,-30,getdate())";
			this.getDao().execute(sql);
			Log.info("ɾ���Ѿ���������ݳɹ�!");
			
			sql="update eco_rds_fx_refund set flag=0 where supplier_nick='"+sellernick+"' and flag=-1";
			this.getDao().execute(sql);
			
			sql="select distinct top 1000 batchid from eco_rds_fx_refund with(nolock) where flag=0 and supplier_nick='"
				+sellernick+"' and batchid>0 order by batchid";
			
			List batchlist=this.getDao().oneListSelect(sql);
			
			for(Iterator itbatch=batchlist.iterator();itbatch.hasNext();)
			{
				batchid=(Integer) itbatch.next();
				//Log.info("�����˻�����ҵ������id:��"+batchid);
				sql="select cast(sub_order_id as varchar(32)) as sub_order_id,jdp_response from eco_rds_fx_refund with(nolock) "
					+"where flag=0 and supplier_nick='"+sellernick+"' and batchid="+batchid;
				
				Vector responselist=this.getDao().multiRowSelect(sql);
				
				for(Iterator itresponse=responselist.iterator();itresponse.hasNext();)
				{
					Hashtable jdpresponse=(Hashtable) itresponse.next();
					
					String jdpresponsestr=jdpresponse.get("jdp_response").toString();
					//long sub_order_id=Long.valueOf(jdpresponse.get("sub_order_id").toString()).longValue();
					sub_order_id=Long.valueOf(jdpresponse.get("sub_order_id").toString()).longValue();
					
					RefundDetail r=new RefundDetail();
					
					JSONObject jsonobj=new JSONObject(jdpresponsestr);
					
					breakpoint ="1";
					JSONObject refunddetailjsobobj=jsonobj.getJSONObject("fenxiao_refund_get_response").getJSONObject("refund_detail");
					breakpoint ="2";
					
					r.setObjValue(r, refunddetailjsobobj);
					breakpoint ="3";
					r.setSub_order_id(sub_order_id);
					breakpoint ="4";
					RDSUtils.processFenXiaoRefund(jobName,this.getDao(),r,tradecontactid,sellernick);	
					breakpoint ="5";
		
					sql="update eco_rds_fx_refund set flag=1 "
						+"where flag=0 and supplier_nick='"+sellernick+"' and batchid="+batchid+" and sub_order_id='"+r.getSub_order_id()+"'";
					this.getDao().execute(sql);
				}

			}
			Log.info("���������Ա��˻����ɹ�!");
			
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
 