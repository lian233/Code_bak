package com.wofu.base.account;

import com.wofu.base.dbmanager.DataCentre;

public abstract class BusinessAccount {
	

	public abstract void execute(DataCentre dc,int busid,int busitype) throws Exception;
	


}
