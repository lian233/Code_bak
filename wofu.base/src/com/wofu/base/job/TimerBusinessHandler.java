package com.wofu.base.job;

import java.util.Hashtable;
import java.util.Map;
import java.util.TimerTask;

public abstract class TimerBusinessHandler extends TimerTask {
	
	private Map params;

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

	public void run() {
		
	}
}
