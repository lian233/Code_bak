package com.wofu.fenxiao.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wofu.fenxiao.domain.RefundSheet;
import com.wofu.fenxiao.mapping.RefundSheetMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.RefundSheetService;
import com.wofu.fenxiao.utils.Tools;
@Service("RefundSheetService")//springmvc注解，这里会自动生成这个类的对象，由spring管理


public class RefundSheetServiceImpl  implements RefundSheetService{
	@Autowired
	private RefundSheetMapper refundSheetMapper;
	Logger logger = Logger.getLogger(DecOrderServiceImpl.class);

	@Override
	public PageView query(PageView pageView, RefundSheet t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(RefundSheet t) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int id) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RefundSheet getById(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RefundSheet> queryAll(RefundSheet t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(RefundSheet t) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public String ifRefundToCustomerRetNote(HashMap<String,Object> map) throws Exception {
		String ret = "";
		try{
			logger.info("调用准备：");
			refundSheetMapper.ifRefundToCustomerRetNote(map);
			ret = map.get("Msg").toString();
			logger.info("调用返回："+ret);
			
		} catch (Exception e) { 
			logger.info("失败："+ e.getMessage());
			throw new Exception("审核退货单失败：" + e.getMessage());
		}
		
		return ret;
		
	}	
		
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public String tlCancelRefund(HashMap<String,Object> map) throws Exception {
		String ret = "";
		try{
			refundSheetMapper.tlCancelRefund(map);
			ret = map.get("Msg").toString();
			logger.info("调用返回："+ret);
			
		} catch (Exception e) { 
			logger.info("失败："+ e.getMessage());
			throw new Exception("取消退货单失败：" + e.getMessage());
		}
		
		return ret;
		
	}	
		

}
