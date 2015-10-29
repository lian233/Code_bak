package com.wofu.intf.chinapay;
import java.util.Date;
import chinapay.PrivateKey;
import chinapay.SecureLink;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class TradeProcessor extends ChinaPaymentProcess{

	@Override
	public void process() throws Exception {
		if(!verify()){
			throw new Exception("����ǩ����֤ʧ��,msgid: "+this.getMsgId());
		}
		if("1001".equals(this.getStatus())){//���׳ɹ� �޸����ݿ�֧��״̬
			String sql ="update ecs_order_info set pay_status=2,order_status=1,pay_time="+(new Date().getTime()-8*60*60)/1000L+ " where chinapay_orderno='"+this.getOrderno()+"'";
			SQLHelper.executeSQL(this.getExtConn(), sql);
			this.setNotes("����֧���ɹ�");
			Log.info("������: "+this.getOrderno()+" ֧���ɹ�");
		}else{
			String sql ="update chinapaymentinfo set notes='����֧��ʧ��' where msgid='"+this.getMsgId()+"'";
			SQLHelper.executeSQL(this.getConn(), sql);
			this.setNotes("����֧��ʧ��");
			Log.info("������: "+this.getOrderno()+" ֧��ʧ��");
		}
		
		
	}

	@Override
	protected boolean verify() throws Exception {
		if(!buildKey())
			return false;
		SecureLink secureLink = new SecureLink(this.getPrivateKey());
		return secureLink.verifyTransResponse(this.getMerid(),this.getOrderno(),this.getAmount(),this.getCurrencycode(),
				this.getTransdate(),this.getTranstype(),this.getStatus(),this.getCheckvalue());
	}
	
	//����Կ����
	private boolean buildKey()throws Exception{
		String keyPath = System.getProperty("user.dir")+"\\PgPubk.key";
		//���ع�Կ����
		PrivateKey key = new PrivateKey();
		this.setPrivateKey(key);
		boolean isSuccessBuild = key.buildKey("999999999999999",0,keyPath);
		if(!isSuccessBuild){
			Log.info("���ع�Կʧ��,�����˳�");
			return false;
		}
		return true;
	}

}
