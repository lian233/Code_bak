package com.wofu.ecommerce.jit;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vipapis.delivery.GetPoListResponseT;
import vipapis.delivery.PickDetailT;
import vipapis.delivery.PickProduct;
import vipapis.delivery.PurchaseOrder;
import vipapis.delivery.SimplePick;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
public class GetExpireOrders extends Thread {
	private static String jobname = "获取唯品会JIT已经过期Po订单作业";
	private Job job;
	public GetExpireOrders(){
		if(job==null) job = new Job(1,"09:00:00","13:00:00","18:00:00");
	}
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			try {
//				if(job.canExecute()){
					connection = PoolHelper.getInstance().getConnection(
							com.wofu.ecommerce.jit.Params.dbname);
					getOrderList(connection);
					
					Thread.sleep(15000L);
//					job.next();
//				}
				
			} catch (Exception e) {
				try {
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
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (true);
	}

	
	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList(Connection conn) throws Exception
	{	
		Log.info(jobname+",开始!");
		long pageno=1L;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Map<String,String> param = new HashMap<String,String>();
					param.put("app_key",Params.app_key);
					param.put("app_secret",Params.app_secret);
					param.put("version",Params.ver);
					param.put("service",Params.service);
					param.put("url",Params.url);
					param.put("format",Params.format);
					param.put("vendor_id",Params.vendor_id);
					GetPoListResponseT response = OrderUtils.getExPirePoList(param);
					List<PurchaseOrder> orderList = response.getPurchase_order_list().getRelationData();
					Iterator it = orderList.iterator();
					while(it.hasNext()){
						PurchaseOrder  p = (PurchaseOrder)it.next();
						Log.info("po: "+p.getPo_no()+",结束时间: "+p.getSell_et_time());
						//po编码
						String po_no = p.getPo_no();
						//创建拣货单
						List<SimplePick> pickList  = OrderUtils.createPick(po_no, Params.vendor_id);
						if(pickList!=null ){
							for(Iterator iter = pickList.iterator();iter.hasNext();){
								System.out.println("-----检测到新的拣货单了.....");
								SimplePick pick = (SimplePick)iter.next();
								//获取拣货单明细
								Map<String,String> params =new HashMap<String,String>(); 
								params.put("app_key",Params.app_key);
								params.put("app_secret",Params.app_secret);
								params.put("version",Params.ver);
								params.put("service",Params.service);
								params.put("url",Params.url);
								params.put("format",Params.format);
								params.put("pick_no",pick.getPick_no());//
								params.put("po_no",po_no);
								params.put("vendor_id",String.valueOf(Params.vendor_id));
								PickDetailT detail = OrderUtils.getPickDetail(params);
								if(!OrderManager.isCheck("检查JIT订单",conn,po_no+"-"+pick.getPick_no())){
									if(!OrderManager.TidLastModifyIntfExists("检查JIT订单", conn, po_no+"_"+pick.getPick_no(), Formatter.parseDate(detail.getSell_st_time(), Formatter.DATE_TIME_FORMAT))){
										OrderUtils.createInterOrder(conn, detail,pick.getPick_no(), Params.tradecontactid, Params.username);
										Iterator pro = detail.getPick_product_lists().getRelationData().iterator();
										while(pro.hasNext()){
											PickProduct  product =(PickProduct) pro.next();
											Log.info("条码码: "+product.getBarcode()+",产品名称: "+product.getProduct_name()+",库存: "+product.getStock());
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "等待发货", po_no+"_"+pick.getPick_no(), product.getBarcode(), product.getStock(), false);
										}
									}
									
								}
							}
							
						}
				}
					break;
				}
				k=10;
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
		Log.info(jobname+",结束!");
	}
	
	
	public String toString()
	{
		return jobname;
	}
}
