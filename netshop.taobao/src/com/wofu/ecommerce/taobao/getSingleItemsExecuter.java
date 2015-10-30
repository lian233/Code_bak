/**
 * ��eco_rds_item������ݸ��Ƶ�ecs_stockconfig��eco_stockconfigsku��
 */
package com.wofu.ecommerce.taobao;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class getSingleItemsExecuter extends Executer {

	private String sellernick="";
	private String tradecontactid="";
	private String batchid="";
	private String num_iid="";

	private final String jobName="�����Ա�ָ����Ʒ������ҵ";

	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		tradecontactid=prop.getProperty("tradecontactid");
		batchid=prop.getProperty("batchid");
		num_iid=prop.getProperty("num_iid");
		sellernick=prop.getProperty("sellernick");
		

		try {
			updateJobFlag(1);
			String sql="select jdp_response from eco_rds_item(nolock) where flag=0 and batchid='"+batchid+"' and num_iid='"+num_iid+"'";
			
			String jdpresponse=this.getDao().strSelect(sql);

					Item item=null;
					try{
						
						item=new Item();
						
						JSONObject jsonobj=new JSONObject(jdpresponse);
						
						JSONObject itemjsobobj=jsonobj.getJSONObject("item_get_response").getJSONObject("item");
					
						item.setObjValue(item, itemjsobobj);
						if(jdpresponse.indexOf("title")==-1){
							Log.error(jobName, "num_iid: "+item.getNum_iid()+", batchid: "+batchid+",�������ݱ���Ϊ�գ��ѱ��ݵ����ݱ�");
							
						}else{
							if(!itemjsobobj.isNull("skus")){
								JSONArray skus=itemjsobobj.getJSONObject("skus").getJSONArray("sku");
								item.setFieldValue(item, "skus", skus);
							}
							sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
							int orgid=this.getDao().intSelect(sql);
							
							RDSUtils.processItem(jobName,this.getDao(),item,orgid,Integer.parseInt(tradecontactid),sellernick);	
						}
							
					}catch(Exception ex){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						Log.error(sellernick+"_"+jobName, "num_iid: "+item.getNum_iid());
						Log.error(sellernick+"_"+jobName,ex.getMessage());
					}
					
				//���´����־
				try{
					sql = "update eco_rds_item set flag=1 where and batchid='"+batchid+"' and num_iid='"+num_iid+"'";
					this.getDao().execute(sql);
				}catch(Exception ex){
					Log.info(jobName+",������±�־ʧ�ܣ�sellernick: "+sellernick+"batchId: "+batchid+" num_iid:"+num_iid);
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
 