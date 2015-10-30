/**
 * 顺风订单称重回传
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

	private String account="";   //帐号
	private String password="";    //密码
	private static String jobName="顺风订单称重回传";
	@Override
	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		account=prop.getProperty("account");
		password=prop.getProperty("password");
		try {			 
			updateJobFlag(1);
			cancelSFOrder();
			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
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
		} finally {
			
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"更新处理标志失败");
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
			}
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}
		
	}
	
	//顺风的订单称重回传
	private void cancelSFOrder() throws Exception {
		
		String sql = "select a.operdata,a.serialID,cast(round(b.value*1.0/1000,3) "  //四舍五入后取三位小数
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
			Log.info("发送xml:　"+sendInfo.toString());
			String result1 = GetExpressOrderId.getExpressId(sendInfo.toString());
			Log.info("顺风快递称重回传结果为:　"+result1);
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
				Log.info(jobName,"订单重量回传成功,单据号: "+sheetid);
				
			}else{
				Element error = DOMHelper.getSubElementsByName(ele, "ERROR", false)[0];
				Log.error(jobName, "错误代码: "+error.getAttribute("code")+" 错误信息: "+DOMHelper.getSubElementVauleByName(ele, "ERROR"));
				if("8019".equals(error.getAttribute("code")) || "8024".equals(error.getAttribute("code"))){ //订单已经取消
					IntfUtils.backupInfSheetList(this.getDao().getConnection(),Integer.parseInt(serialid));
					Log.info(jobName,"订单已确认或已消单,编号: "+sheetid);
				}
			}
			//Log.info("sendInfo: "+sendInfo.toString());
			sendInfo.delete(sendInfo.indexOf("<OrderConfirm orderid=")+23, sendInfo.length());
			//Log.info("sendInfo: "+sendInfo.toString());
		}
		
		sendInfo=null;
	}
		
}
