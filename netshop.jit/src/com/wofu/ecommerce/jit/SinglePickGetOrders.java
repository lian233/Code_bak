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
public class SinglePickGetOrders extends Thread {
	private static String jobname = "��ȡΨƷ��JIT����������Ŷ�����ҵ";
	private boolean is_importing=false;
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
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
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jit.Params.waittime * 1000000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮������ж���
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
					SimplePick pick = new SimplePick();
					pick.setPick_no(Params.pick_no);
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
					if(!OrderManager.isCheck("���JIT����",conn,po_no+"-"+pick.getPick_no())){//o.getPo_no()+"_"+pick_no
						if(!OrderManager.TidLastModifyIntfExists("���JIT����", conn, po_no+"_"+pick.getPick_no(), Formatter.parseDate(detail.getSell_st_time(), Formatter.DATE_TIME_FORMAT))){
							OrderUtils.createInterOrder(conn, detail,pick.getPick_no(), Params.tradecontactid, Params.username);
							Iterator pro = detail.getPick_product_lists().getRelationData().iterator();
							while(pro.hasNext()){
								PickProduct  product =(PickProduct) pro.next();
								Log.info("������: "+product.getBarcode()+",��Ʒ����: "+product.getProduct_name()+",���: "+product.getStock());
								//(jobname, conn, Params.tradecontactid, "δ����",o.getOrder_sn(), sku, qty,false)
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "�ȴ�����", po_no+"_"+pick.getPick_no(), product.getBarcode(), product.getStock(), false);
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
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
