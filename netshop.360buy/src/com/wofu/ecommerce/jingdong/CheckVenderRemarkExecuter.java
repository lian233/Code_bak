package com.wofu.ecommerce.jingdong;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.domain.order.OrderResult;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.request.order.OrderSearchRequest;
import com.jd.open.api.sdk.request.order.OrderVenderRemarkQueryByOrderIdRequest;
import com.jd.open.api.sdk.response.order.OrderVenderRemarkQueryByOrderIdResponse;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
public class CheckVenderRemarkExecuter extends Executer {
	
	private String SERVER_URL = "" ;
	
	private String token = "" ;
	
	private String appKey = "" ;
	
	private String appSecret = "" ;
	
	private String tradecontactid = "" ;
	
	private String username = "" ;
	
	private static String jobName="��ʱ��龩��δ�󶩵����̼ұ�ע";
	
	public void run()  {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		SERVER_URL=prop.getProperty("SERVER_URL") ;
		token=prop.getProperty("token") ;
		appKey=prop.getProperty("appKey") ;
		appSecret=prop.getProperty("appSecret") ;
		tradecontactid=prop.getProperty("tradecontactid") ;
		username=prop.getProperty("username") ;

		try 
		{			 
			updateJobFlag(1);
			
			checkVenderRemarks() ;
			
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				//updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
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
				Log.error(jobName,"���´����־ʧ��");
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
	
	private void checkVenderRemarks() throws Exception
	{

			try
			{
				//���customerorder0����flag=0�Ķ�����δ��ˣ��ľ����������̼ұ�ע  д��refnote�ֶ�
				Log.info(username+",��ʱ��龩���������̼ұ�ע��ʼ");
				String sql = new StringBuilder().append("select sheetid,refsheetid from customerorder0 where tradecontactid=")
					.append(tradecontactid).append(" and flag=0 and refnote=''").toString();
				Vector result= this.getDao().multiRowSelect(sql);
				Log.info(jobName+",����Ҫ����Ķ�����Ϊ:��"+result.size());
				JdClient client=new DefaultJdClient(SERVER_URL,token,appKey,appSecret); 
				OrderVenderRemarkQueryByOrderIdRequest request=new OrderVenderRemarkQueryByOrderIdRequest();
				String message="";
				ArrayList list= new ArrayList();
				for(int i=0;i<result.size();i++){
					try{
						message="";
						String tid=(String)((Hashtable)result.get(i)).get("refsheetid");
						request.setOrderId(Long.parseLong(tid));
						OrderVenderRemarkQueryByOrderIdResponse response=client.execute(request);
						Log.info(response.getVenderRemarkQueryResult().getApiJosResult().toString());
						if(response.getVenderRemarkQueryResult().getApiJosResult().getSuccess()){
							message = response.getVenderRemarkQueryResult().getVenderRemark().getRemark();
							sql = new StringBuilder().append("update customerorder0 set RefNote='")
							.append(message).append("' where sheetid='").append((String)((Hashtable)result.get(i)).get("sheetid"))
							.append("'").toString();
							list.add(sql);
							Log.info("tid: "+tid+", VenderRemark: "+message);
						}
						
						
						
					}catch(Exception ex){
						Log.error(jobName, ex.getMessage());
					}
					
					
				}
				if(list.size()>0)
				SQLHelper.executeBatch(this.getDao().getConnection(), list);
				Log.info(username+",��ʱ��龩���������̼ұ�ע���");
	}catch(Exception ex){
		Log.error(jobName,ex.getMessage());
		}
	}
		
}
