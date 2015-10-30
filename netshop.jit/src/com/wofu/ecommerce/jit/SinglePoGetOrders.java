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
public class SinglePoGetOrders extends Thread {
	private static String jobname = "获取单个po唯品会JIT订单作业";
	private boolean is_importing=false;
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.jit.Params.dbname);
				getOrderList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jit.Params.waittime * 1000000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一个po的订单
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					String po_no = Params.po_no;//p.getPo_no();
					List<SimplePick> pickList  = OrderUtils.createPick(po_no, Params.vendor_id);
					if(pickList!=null ){
						Log.info("-----检测到新的拣货单了.....");
						for(Iterator iter = pickList.iterator();iter.hasNext();){
							SimplePick pick = (SimplePick)iter.next();
							//pick.setPick_no("PICK-2100010971-111");
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
							if(!OrderManager.isCheck("检查JIT订单",conn,po_no+"-"+pick.getPick_no())){//o.getPo_no()+"_"+pick_no
								if(!OrderManager.TidLastModifyIntfExists("检查JIT订单", conn, po_no+"_"+pick.getPick_no(), Formatter.parseDate(detail.getSell_st_time(), Formatter.DATE_TIME_FORMAT))){
									OrderUtils.createInterOrder(conn, detail,pick.getPick_no(), Params.tradecontactid, Params.username);
									Iterator pro = detail.getPick_product_lists().getRelationData().iterator();
									while(pro.hasNext()){
										PickProduct  product =(PickProduct) pro.next();
										Log.info("条码码: "+product.getBarcode()+",产品名称: "+product.getProduct_name()+",库存: "+product.getStock());
										//(jobname, conn, Params.tradecontactid, "未发货",o.getOrder_sn(), sku, qty,false)
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "等待发货", po_no+"_"+pick.getPick_no(), product.getBarcode(), product.getStock(), false);
									}
								}

							}
						}
					}
					break;
				}
				//执行成功后不再循环
				k=10;
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
