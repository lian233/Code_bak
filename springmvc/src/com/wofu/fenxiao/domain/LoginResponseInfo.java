package com.wofu.fenxiao.domain;

import java.util.ArrayList;
import java.util.List;

public class LoginResponseInfo extends BaseResponseInfo{
	List<String> data = new ArrayList<String>();

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}
	
}
