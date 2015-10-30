/**
 * ˳�綩�����ػش�
 */
package com.wofu.ecommerce.sf.webServiceClient;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.sf.webServiceClient.GetExpressOrderId;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.intf.IntfUtils;
public class SendWeightSFOrderExecuter extends Executer {

	private String account="";   //�ʺ�
	private String password="";    //����
	private static String jobName="˳�綩�����ػش�";
	@Override
	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		account=prop.getProperty("account");
		password=prop.getProperty("password");
		try {			 
			updateJobFlag(1);
			cancelSFOrder();
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
	
	//˳��Ķ������ػش�
	private void cancelSFOrder() throws Exception {
		
		String sql = "select a.operdata,a.serialID,cast(round(b.value*1.0/1000,3) "  //���������ȡ��λС��
			+"as numeric(6,3)) weight,b.deliverysheetid from Inf_DownNote a,weigh b  where a.operdata=b.sheetid and a.sheettype='360200' and b.flag=1";
		
		Vector result = this.getDao().multiRowSelect(sql);
		if(result.size()==0) return;
		StringBuffer sendInfo = new StringBuffer();
		sendInfo.append("<Request service='OrderConfirmService' lang='zh-CN'>")
		.append("<Head>")
		.append(account).append(",").append(password)
		.append("</Head><Body>")
		.append("<OrderConfirm orderid='");
		for(int i=0;i<result.size();i++){
			Hashtable order = (Hashtable)result.get(i);
			String sheetid = order.get("operdata").toString();
			String serialid = order.get("serialID").toString();
			String weight = order.get("weight").toString();
			String deliverysheetid = order.get("deliverysheetid").toString();
			sendInfo.append(sheetid).append("' ")
			.append("mailno='")
			.append(deliverysheetid)
			.append("' ")
			.append("dealtype='1'>")
			.append("<OrderConfirmOption weight='")
			.append(weight)
			.append("'/>")
			.append("</OrderConfirm></Body></Request>");
			Log.info("����xml:��"+sendInfo.toString());
			String result1 = GetExpressOrderId.getExpressId(sendInfo.toString());
			Log.info("˳���ݳ��ػش����Ϊ:��"+result1);
			Document document = DOMHelper.newDocument(result1,"gbk");
			Element ele = document.getDocumentElement();
			String isSuccess = DOMHelper.getSubElementVauleByName(ele, "Head");
			Log.info("isSuccess: "+isSuccess);
			if("ok".equalsIgnoreCase(isSuccess)){
				Element response = DOMHelper.getSubElementsByName(ele,"Body",false)[0];
				Element OrderResponse = DOMHelper.getSubElementsByName(response,"OrderConfirmResponse",false)[0];
				String orderId= OrderResponse.getAttribute("orderid");
				Log.info("orderId: "+orderId);
				if(orderId.equals(sheetid)){
					IntfUtils.backupInfSheetList(this.getDao().getConnection(),Integer.parseInt(serialid));
				}
				Log.info(jobName,"���������ش��ɹ�,���ݺ�: "+sheetid);
				
			}else{
				Element error = DOMHelper.getSubElementsByName(ele, "ERROR", false)[0];
				Log.error(jobName, "�������: "+error.getAttribute("code")+" ������Ϣ: "+DOMHelper.getSubElementVauleByName(ele, "ERROR"));
				if("8019".equals(error.getAttribute("code")) || "8024".equals(error.getAttribute("code"))){ //�����Ѿ�ȡ��
					IntfUtils.backupInfSheetList(this.getDao().getConnection(),Integer.parseInt(serialid));
					Log.info(jobName,"������ȷ�ϻ�������,���: "+sheetid);
				}
			}
			//Log.info("sendInfo: "+sendInfo.toString());
			sendInfo.delete(sendInfo.indexOf("<OrderConfirm orderid=")+23, sendInfo.length());
			//Log.info("sendInfo: "+sendInfo.toString());
		}
		
		sendInfo=null;
	}
		
}
