package com.wofu.intf.jw;
/**
 * �������ͷ�����Ϣ��
 */
import java.util.Date;
import com.wofu.base.util.BusinessObject;

public class PubSyncDeliveryInfoResponse extends BusinessObject{
	private boolean isSuccess=false; //�Ƿ�ɹ�
	private String body="";          //��Ϣ��
	private String error="";        //������Ϣ
	private Date ts;                //����ʱ��
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
