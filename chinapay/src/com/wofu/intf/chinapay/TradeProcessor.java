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
			throw new Exception("数字签名验证失败,msgid: "+this.getMsgId());
		}
		if("1001".equals(this.getStatus())){//交易成功 修改数据库支付状态
			String sql ="update ecs_order_info set pay_status=2,order_status=1,pay_time="+(new Date().getTime()-8*60*60)/1000L+ " where chinapay_orderno='"+this.getOrderno()+"'";
			SQLHelper.executeSQL(this.getExtConn(), sql);
			this.setNotes("订单支付成功");
			Log.info("订单号: "+this.getOrderno()+" 支付成功");
		}else{
			String sql ="update chinapaymentinfo set notes='订单支付失败' where msgid='"+this.getMsgId()+"'";
			SQLHelper.executeSQL(this.getConn(), sql);
			this.setNotes("订单支付失败");
			Log.info("订单号: "+this.getOrderno()+" 支付失败");
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
	
	//加载钥对象
	private boolean buildKey()throws Exception{
		String keyPath = System.getProperty("user.dir")+"\\PgPubk.key";
		//加载公钥对象
		PrivateKey key = new PrivateKey();
		this.setPrivateKey(key);
		boolean isSuccessBuild = key.buildKey("999999999999999",0,keyPath);
		if(!isSuccessBuild){
			Log.info("加载公钥失败,程序退出");
			return false;
		}
		return true;
	}

}
