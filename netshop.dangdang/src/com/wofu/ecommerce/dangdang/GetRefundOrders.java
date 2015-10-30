package com.wofu.ecommerce.dangdang;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.dangdang.util.CommHelper;
/**
 * 
 * 获取当当退换货单作业
 *
 */
public class GetRefundOrders extends Thread {

	private static String jobname = "获取当当退换货单作业";

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.dangdang.Params.dbname);	
				getRefund(connection) ;
				
			} catch (Exception e) {
				try {
					e.printStackTrace() ;
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {

				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000 * Params.timeInterval))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	

	public void getRefund(Connection conn) throws Exception
	{
		String sql = "" ;
		String resultText = "" ;
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		String status = "1" ;//处理状态 1:待处理 2:已处理 已在当当后台审核同意或不同意
		Log.info("获取当当退货开始!");
		for(int k=0;k<10;)
		{
			try 
			{	
				while(hasNextPage)
				{
					Date temp = new Date();
					//方法名
					String methodName="dangdang.orders.exchange.return.list.get";
					//生成验证码 --md5;加密
					String sign =  CommHelper.getSign(Params.app_Secret, Params.app_key, methodName, Params.session,temp);
					
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
					params.put("app_key",Params.app_key);
					params.put("method",methodName);
					params.put("format","xml");
					params.put("session",Params.session);
					params.put("sign_method","md5");
					params.put("p", String.valueOf(pageIndex)) ;
					params.put("status", status) ;
					
					resultText = CommHelper.sendRequest(Params.url, "GET", params, "") ;
					Document doc = DOMHelper.newDocument(resultText, Params.encoding) ;
					Element resultElement = doc.getDocumentElement() ;
					//判断返回是否正确
					if(DOMHelper.ElementIsExists(resultElement, "Error"))
					{
						Element error = (Element) resultElement.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error(jobname, "获取当当退换货订单失败,错误信息:"+operCode+operation) ;
							return ;
						}
					} 
					
					Element totalInfo = (Element) resultElement.getElementsByTagName("totalInfo").item(0) ;
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal") ;
					
					Element ordersList = (Element)resultElement.getElementsByTagName("OrdersList").item(0) ;
					NodeList orderInfo = ordersList.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i < orderInfo.getLength() ; i++)
					{
						ReturnOrder o = new ReturnOrder() ;
						Element order = (Element) orderInfo.item(i) ;
						String orderID = DOMHelper.getSubElementVauleByName(order, "orderID") ;
						String returnExchangeStatus = DOMHelper.getSubElementVauleByName(order, "returnExchangeStatus") ;
						String returnExchangeCode = DOMHelper.getSubElementVauleByName(order, "returnExchangeCode") ;
						String orderMoney = DOMHelper.getSubElementVauleByName(order, "orderMoney").trim() ;
						String orderTime = DOMHelper.getSubElementVauleByName(order, "orderTime") ;
						String orderStatus = DOMHelper.getSubElementVauleByName(order, "orderStatus") ;
						String returnExchangeOrdersApprStatus = DOMHelper.getSubElementVauleByName(order, "returnExchangeOrdersApprStatus") ;
						
						//退换货申请审核状态，只生成待处理申请通过的	0:审核不通过 1:审核通过 2:待审核
						if(!"1".equals(returnExchangeOrdersApprStatus))
							continue ;
						
						if("".equals(orderMoney) || orderMoney == null)
							continue ;
							//orderMoney = "0.0" ;
						o.setOrderID(orderID) ;
						o.setReturnExchangeStatus(returnExchangeStatus) ;
						o.setReturnExchangeCode(returnExchangeCode) ;
						o.setOrderMoney(Float.parseFloat(orderMoney)) ;
						o.setOrderTime(Formatter.parseDate(orderTime, Formatter.DATE_TIME_FORMAT)) ;
						o.setOrderStatus(orderStatus) ;
						o.setReturnExchangeOrdersApprStatus(returnExchangeOrdersApprStatus) ;
						
						Element itemsList = (Element) order.getElementsByTagName("itemsList").item(0) ;
						NodeList itemInfo = itemsList.getElementsByTagName("ItemInfo") ;
						ArrayList<ReturnOrderItem> itemList = new ArrayList<ReturnOrderItem>() ;
						for(int j = 0 ; j < itemInfo.getLength() ; j ++)
						{
							ReturnOrderItem orderitem = new ReturnOrderItem() ;
							
							Element item = (Element) itemInfo.item(j) ;
							String itemID = DOMHelper.getSubElementVauleByName(item, "itemID") ;
							String itemName = DOMHelper.getSubElementVauleByName(item, "itemName") ;
							String itemSubhead = DOMHelper.getSubElementVauleByName(item, "itemSubhead") ;
							String unitPrice = DOMHelper.getSubElementVauleByName(item, "unitPrice").trim() ;
							String orderCount = DOMHelper.getSubElementVauleByName(item, "orderCount") ;
							String outerItemID = DOMHelper.getSubElementVauleByName(item, "outerItemID") ;
							
							if("".equals(unitPrice) || unitPrice == null)
								unitPrice = "0.0" ;
	
							if("".equals(orderCount) || orderCount == null)
								orderCount = "0" ;
							
							orderitem.setItemID(itemID) ;
							orderitem.setItemName(itemName) ;
							orderitem.setItemSubhead(itemSubhead) ;
							orderitem.setUnitPrice(Float.parseFloat(unitPrice)) ;
							orderitem.setOrderCount(Integer.parseInt(orderCount)) ;
							orderitem.setOuterItemID(outerItemID) ;
							
							itemList.add(orderitem) ;
						}
						o.setItemList(itemList) ;
						//获取原订单信息，取联系人，地址
						Order customOrder = OrderUtils.getOrderByID(Params.url,orderID,Params.session,Params.app_key,Params.app_Secret) ;
						
						OrderUtils.createRefundOrder("生成当当退换货接口订单", conn, Params.tradecontactid, o,customOrder) ;
					}
					if(pageIndex < Integer.parseInt(pageTotal))
						pageIndex ++ ;
					else
					{
						hasNextPage = false ;
						break ;
					}
				}
				break;
				
			}catch (Exception e) 
			{
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("获取当当退货结束!");
	}
}