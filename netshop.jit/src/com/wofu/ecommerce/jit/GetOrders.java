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
public class GetOrders extends Thread {
	private static String jobname = "��ȡΨƷ��JIT������ҵ";
	private Job job;
	public GetOrders(){
		if(job==null) job = new Job(1,"09:00:00","13:00:00","18:00:00");
	}
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
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
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
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
	 * ��ȡһ��֮������ж���
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		Log.info(jobname+",��ʼ!");
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
					GetPoListResponseT response = OrderUtils.getPoList(param);
					List<PurchaseOrder> orderList = response.getPurchase_order_list().getRelationData();
					Iterator it = orderList.iterator();
					while(it.hasNext()){
						PurchaseOrder  p = (PurchaseOrder)it.next();
						Log.info("po: "+p.getPo_no()+",����ʱ��: "+p.getSell_et_time());
						//po����
						String po_no = p.getPo_no();
						//���������
						List<SimplePick> pickList  = OrderUtils.createPick(po_no, Params.vendor_id);
						if(pickList!=null ){
							for(Iterator iter = pickList.iterator();iter.hasNext();){
								System.out.println("-----��⵽�µļ������.....");
								SimplePick pick = (SimplePick)iter.next();
								//��ȡ�������ϸ
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
								if(!OrderManager.isCheck("���JIT����",conn,po_no+"-"+pick.getPick_no())){
									if(!OrderManager.TidLastModifyIntfExists("���JIT����", conn, po_no+"_"+pick.getPick_no(), Formatter.parseDate(detail.getSell_st_time(), Formatter.DATE_TIME_FORMAT))){
										OrderUtils.createInterOrder(conn, detail,pick.getPick_no(), Params.tradecontactid, Params.username);
										Iterator pro = detail.getPick_product_lists().getRelationData().iterator();
										while(pro.hasNext()){
											PickProduct  product =(PickProduct) pro.next();
											Log.info("������: "+product.getBarcode()+",��Ʒ����: "+product.getProduct_name()+",���: "+product.getStock());
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "�ȴ�����", po_no+"_"+pick.getPick_no(), product.getBarcode(), product.getStock(), false);
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
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
		Log.info(jobname+",����!");
	}
	
	
	public String toString()
	{
		return jobname;
	}
}
