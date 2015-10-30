package com.wofu.fenxiao.domain;

import java.util.Date;

/**
 * rds淘宝下载订单配置表
 * @author Administrator
 *
 */
public class EcoSellerConfig {	
	private String sellernick;
	private Date lastordertime;
	private Date lastrefundtime;
	private Date lastitemtime;
	private Date lastfxordertime;
	private Date lastfxrefundtime;
	private Date lasttmreturntime;
	private Date lasttmrefundtime;
	private Date lastjxordertime;
	public String getSellernick() {
		return sellernick;
	}
	public void setSellernick(String sellernick) {
		this.sellernick = sellernick;
	}
	public Date getLastordertime() {
		return lastordertime;
	}
	public void setLastordertime(Date lastordertime) {
		this.lastordertime = lastordertime;
	}
	public Date getLastrefundtime() {
		return lastrefundtime;
	}
	public void setLastrefundtime(Date lastrefundtime) {
		this.lastrefundtime = lastrefundtime;
	}
	public Date getLastitemtime() {
		return lastitemtime;
	}
	public void setLastitemtime(Date lastitemtime) {
		this.lastitemtime = lastitemtime;
	}
	public Date getLastfxordertime() {
		return lastfxordertime;
	}
	public void setLastfxordertime(Date lastfxordertime) {
		this.lastfxordertime = lastfxordertime;
	}
	public Date getLastfxrefundtime() {
		return lastfxrefundtime;
	}
	public void setLastfxrefundtime(Date lastfxrefundtime) {
		this.lastfxrefundtime = lastfxrefundtime;
	}
	public Date getLasttmreturntime() {
		return lasttmreturntime;
	}
	public void setLasttmreturntime(Date lasttmreturntime) {
		this.lasttmreturntime = lasttmreturntime;
	}
	public Date getLasttmrefundtime() {
		return lasttmrefundtime;
	}
	public void setLasttmrefundtime(Date lasttmrefundtime) {
		this.lasttmrefundtime = lasttmrefundtime;
	}
	public Date getLastjxordertime() {
		return lastjxordertime;
	}
	public void setLastjxordertime(Date lastjxordertime) {
		this.lastjxordertime = lastjxordertime;
	}
	
}
