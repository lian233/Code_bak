package com.wofu.fenxiao.service;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.domain.DecOrderItem;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import com.wofu.fenxiao.base.BaseService;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.utils.Tools;


public interface DecOrderService extends BaseService<DecOrder> {

	//查询订单数据
	public List<DecOrder> qryDecOrder(HashMap<String,Object>  map)throws Exception;
	
	public List<DecOrder> qryDecOrderList(HashMap<String,Object>  map)throws Exception;
	
	//订单合并
	public void mergeDecOrder(HttpSession session)throws Exception;
	
	//审核订单
	//public void CheckOrder(HttpSession session , int id , int flag) throws Exception;
	public JSONArray CheckOrder(HttpSession session , JSONArray ids );

	//取消订单
	public void BakOrder(HttpSession session , int id , int flag) throws Exception;

	//取消订单
	public String StopDecOrder(HttpSession session , int id) throws Exception;

	//审核订单
	public String CheckDecOrder(HttpSession session , int id) throws Exception;
	
	//修改订单状态
	public void modifyDecOrderFlag(HttpSession session , int id , int flag) throws Exception;
	
	//查询订单明细
	public List<DecOrderItem> qryDecOrderItem(HashMap<String,Object>  map)throws Exception;

	//增加订单明细
	public void addItem(DecOrderItem t) throws Exception;
	
	//删除订单明细
	public void deleteItem(int id) throws Exception;

	//删除订单明细
	public void deleteItem(DecOrderItem t) throws Exception;

	//删除订单
	public void delete(DecOrder t) throws Exception;
	
	//更新订单明细
	public void updateItem(DecOrderItem t) throws Exception;
	//批量确认订单
	public JSONArray confirmDecOrders(JSONArray ids, HttpSession session
			,String cainiao_app_key,String cainiao_app_secret,String cainiao_token,String cainiao_user_id,String cainiao_url)throws Exception;
	
	//输出订单打印信息
	public JSONArray getOrderPrintInfo(HttpSession session, List<DecOrder> orderList , JSONArray fields)throws Exception;

	//设置订单的明细信息
	public void setDecKeyPicNote(String sheetID , int customerID)throws Exception;

	//更新打印次数
	public void updatePrintTimes(HashMap<String,Object> map)throws Exception;
	
	//取得订单明细
	public DecOrderItem getItemById(int id) throws Exception;

	//取得单个订单明细
	public DecOrderItem getItemById(int id , String bak , String front) throws Exception;

	//取得单个订单
	public DecOrder getById(int id , String bak , String front) throws Exception;
	
	//快递路由查询、
	public JSONArray qryDeliveryInfo(String companyCode,JSONArray outsids)throws Exception;
	
	//查询订单明细统计数据
	public List<HashMap<String, Object>> qryStaDecOrderSku(HashMap<String, Object> map)throws Exception;

	//查询订单明细统计数据
	public List<HashMap> qryStaDecOrderSkuList(HashMap<String, Object> map)throws Exception;
	
}
