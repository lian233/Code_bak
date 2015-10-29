package com.wofu.intf.jw;
/**
 * 订单推送反馈信息体
 */
import java.util.Date;
import com.wofu.base.util.BusinessObject;

public class PubSyncDeliveryInfoResponse extends BusinessObject{
	private boolean isSuccess=false; //是否成功
	private String body="";          //消息体
	private String error="";        //错误信息
	private Date ts;                //操作时间
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setIssuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public boolean getIsSuccess() {
		return isSuccess;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Date getTs() {
		return ts;
	}
	public void setTs(Date ts) {
		this.ts = ts;
	}
	
}
