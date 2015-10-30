package com.wofu.ecommerce.taobao.fenxiao;
/**
 * �����Ա�������ҵ  �Ѷ������ݴ�itf_DecOrder ת�Ƶ��ӿڱ���  û�е���api
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class getOrdersExecuter extends Executer {
	private String sellernick="";
	private boolean waitbuyerpayisin=false;
	private boolean isJzservice=false;  //�ǲ��Ǽ�װ��
	private int shopid;
	private int customerid;
	private boolean isUpdateStock;
	private String jobName="�����Ա�������ҵ";

	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		sellernick=prop.getProperty("sellernick");	
		shopid = Integer.parseInt(prop.getProperty("shopid"));
		customerid = Integer.parseInt(prop.getProperty("customerid","0"));
		shopid=Integer.parseInt(prop.getProperty("shopid"));
		boolean finishisin=true;//Boolean.parseBoolean(prop.getProperty("finishisin","false"));
		isJzservice=Boolean.parseBoolean(prop.getProperty("isJzservice","false"));
		jobName=sellernick+jobName;
		String jdpresponse=null;
		String sql=null;
		String jdp_modified=null;
		List batchlist=null;
		Trade td=null;
		try {			 
			updateJobFlag(1);
			sql = "select isnull(isUpdateStock,0) from decshop where id="+shopid;
			isUpdateStock  = this.getDao().intSelect(sql)==0?false:true;
			//ɾ���Ѿ��������
			/**
			sql="delete from eco_rds_trade where seller_nick='"+sellernick+"' and flag=1  and jdp_modified<dateadd(dd,-15,getdate())";
			this.getDao().execute(sql);
			
			Log.info(jobName+" ɾ���Ѿ���������ݳɹ�!");
			**/
			sql="select distinct batchid from eco_rds_trade where  flag=0 and seller_nick='"+sellernick+"' and batchid>0 order by batchid";
			
			batchlist=this.getDao().oneListSelect(sql);
			Log.info(jobName+","+sellernick+"���δ����������������Ϊ:��"+batchlist.size());
			ArrayList<Long> errMsg=null;
			for(Iterator itbatch=batchlist.iterator();itbatch.hasNext();)
			{
				int batchid=(Integer) itbatch.next();
				sql="select jdp_response,jdp_modified from eco_rds_trade with(nolock) "
					+"where batchid="+batchid;
				
				Vector responselist=this.getDao().multiRowSelect(sql);
				errMsg = new ArrayList<Long>();;
				for(Iterator itresponse=responselist.iterator();itresponse.hasNext();)
				{	
					Hashtable ht =(Hashtable) itresponse.next();
					jdpresponse=ht.get("jdp_response").toString();
					jdp_modified=ht.get("jdp_modified").toString();
					try{
						td=new Trade();
						JSONObject jsonobj=new JSONObject(jdpresponse);
						JSONObject tradejsobobj=jsonobj.getJSONObject("trade_fullinfo_get_response").getJSONObject("trade");
						td.setObjValue(td, tradejsobobj);
						if(jdpresponse.indexOf("orders")==-1){
							OrderUtils.bakOrderItem(jobName, this.getDao().getConnection(), batchid, td.getTid());
							Log.error(jobName, "�������ݲ�������ȱ��'orders'�ֶΣ��ѱ��ݱ��ݱ�");
							continue;
						}
						JSONArray orders=tradejsobobj.getJSONObject("orders").getJSONArray("order");
						td.setFieldValue(td, "orders", orders);
						if(!tradejsobobj.isNull("service_orders"))
							td.setFieldValue(td, "service_orders", tradejsobobj.getJSONObject("service_orders").getJSONArray("service_order"));
						//���ö������޸�ʱ��
						td.setJdp_modified(Formatter.parseDate(jdp_modified, Formatter.DATE_TIME_FORMAT));
						RDSUtils.processOrder(jobName,this.getDao(),td,shopid,sellernick,waitbuyerpayisin,isJzservice,finishisin,customerid,isUpdateStock);
					}catch(Exception ex){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						Log.error(jobName,sellernick+" "+jobName+" �����쳣��tid: "+td.getTid()+" ,batchid: "+batchid+" �쳣��Ϣ: "+sw.toString());
						errMsg.add(td.getTid());
						sw=null;
						continue;
					}
							
				}
				if(errMsg.size()==0){
					sql="update eco_rds_trade set flag=1 "
						+"where batchid="+batchid;
				}
				else{
					StringBuilder sqlTemp = new StringBuilder("update eco_rds_trade set flag=1 where batchid=").append(batchid)
						.append(" and tid not in(");
					for(Iterator it = errMsg.iterator();it.hasNext();){
						sqlTemp.append("'").append(it.next()).append("',");
					}
					sqlTemp.deleteCharAt(sqlTemp.length()-1).append(")");
					sql = sqlTemp.toString();
					Log.info("δ������: "+sql);
				}
				try{
					int success=this.getDao().execute(sql);
					if(success>0){
						Log.info(sellernick+" "+jobName+"���ı�־λ�ɹ���batchid="+batchid);
					}else{
						Log.info(sellernick+" "+jobName+"���ı�־λʧ�ܣ�batchid="+batchid);
					}
				}catch(Exception ex){
					Log.error(jobName, sellernick+",���ı�־λ����, batchid:"+batchid);
				}
			}
			Log.info(sellernick+" :"+jobName+"�������");
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
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.error(jobName,"������Ϣ:"+sw.toString());
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			sw=null;
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
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
			sql=null;
			jdpresponse=null;
			batchlist=null;
			td = null;
		}
		
		

	}

}
 