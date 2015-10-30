package com.wofu.ecommerce.taobao;
/**
 * ¼ì²éÌÔ±¦Â©µ¥
 */
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.common.tools.util.StringUtil;
public class CheckWaitSellerSendGoods extends Executer{

	private String seller_nick;
	public void run() {
		Properties pro = StringUtil.getStringProperties(this.getExecuteobj().getParams());
		seller_nick = pro.getProperty("seller_nick");
		
		
	}
	
}
