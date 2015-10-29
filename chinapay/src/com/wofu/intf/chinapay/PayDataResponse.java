package com.wofu.intf.chinapay;

import com.wofu.base.util.BusinessObject;

public class PayDataResponse extends BusinessObject{
	private String flag;
	private String checkvalue;
	private String username;
	private String certNo;
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getCheckvalue() {
		return checkvalue;
	}
	public void setCheckvalue(String checkvalue) {
		this.checkvalue = checkvalue;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	
	
	
	
	
	
	/**
	 * MerId+OrdId+TransAmt+CuryId+TransDate+TransType+Version+PageRetUrl+BgRetUrl+GateId+Priv1+ BusiType+
	 */
	

}
