package com.wofu.ecommerce.baseinformation;

import com.wofu.base.util.BusinessObject;

public class TradeContacts extends BusinessObject {
	
	
	public void getTradeContacts() throws Exception
	{
		String sql="select tradecontactid,tradecontacts from tradecontacts with(nolock)";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void doTransaction(String action) throws Exception {
		
		if (action.equalsIgnoreCase("gettradecontacts"))
			getTradeContacts();
	}

}
