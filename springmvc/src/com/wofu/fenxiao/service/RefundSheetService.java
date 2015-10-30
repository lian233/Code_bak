package com.wofu.fenxiao.service;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.RefundSheet;

import java.util.HashMap;
import java.util.List;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.pulgins.PageView;


public interface RefundSheetService extends BaseService<RefundSheet> {


	public String ifRefundToCustomerRetNote(HashMap<String,Object> map) throws Exception;	
	
	public String tlCancelRefund(HashMap<String,Object> map) throws Exception;	
	
}
