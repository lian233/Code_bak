package com.wofu.intf.fedex;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebParam;
import javax.xml.ws.Holder;

import org.example.servicefororder.CancelOrder;
import org.example.servicefororder.ErrorType;
import org.example.servicefororder.HeaderRequest;
import org.example.servicefororder.OrderInfo;
import org.example.servicefororder.ProductDeatilType;
import org.example.servicefororder.ServiceForOrder;
import org.example.servicefororder.ServiceForOrder_Service;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class AsyncCancelOrderInfo extends Thread {
	private static String jobname = "同步取消订单作业";
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {
			Connection conn = null;
			try {
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				Holder<String> ask = new Holder<String>();
				Holder<String> message = new Holder<String>();
				Holder<List<ErrorType>> error = new Holder<List<ErrorType>>();
				Holder<String> orderCode = new Holder<String>();
				Holder<List<ErrorType>> referenceNo = new Holder<List<ErrorType>>();
				HeaderRequest request = new HeaderRequest();
				request.setAppKey(Params.Key);
				request.setAppToken(Params.Token);
				request.setCustomerCode(Params.customercode);
				ServiceForOrder_Service order_service  = new ServiceForOrder_Service();
				ServiceForOrder info = order_service.getServiceForOrderSOAP();
				//取得要处理的数据的单号  这个单号对应barcodetranlist的sheetid
				Vector infsheetlist=FedexUtil.getInfDownNote(conn,"9903");
				//每一个单号发送一次请求
				String province="";
				String city="";
				String district="";
				String address="";
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					Hashtable ht = (Hashtable)it.next();
					String sheetid=ht.get("OperData").toString();
					Integer serialid = (Integer)ht.get("SerialID");
					Log.info("sheetid: "+sheetid);
					//skuCategory 产品分类   这个还没有表数据关联的
					String sql = "select CustomPurSheetID as orderCode from outstock0 a where a.sheetid='"+sheetid+"'";
					boolean isSuccess = false;
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					CancelOrder cancelorder = new CancelOrder();
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						cancelorder.getMapData(htsku);
						info.updateOrderStatus(request, cancelorder.getOrderStatus(),2, ask, message, error);
					}
					
					if("1".equals(ask.value)){
						isSuccess=true;
						conn.setAutoCommit(false);
						FedexUtil.bakcUpDownNote(conn,serialid);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info("取消订单成功,订单号: "+cancelorder.getOrderCode());
					}else{
						isSuccess=false;
						Log.info("取消订单成功失败: "+message.value);
						List<ErrorType>  type = (List<ErrorType>)error.value;
						List<org.example.servicefororder.ErrorType> errorType = (List<org.example.servicefororder.ErrorType>)error.value;
						for(Iterator itt = errorType.iterator();itt.hasNext();){
							ErrorType typetemp =(org.example.servicefororder.ErrorType)itt.next();
							Log.info("取消订单错误信息: "+typetemp.getErrorMessage());
							}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					if (conn != null && !conn.getAutoCommit()){
						conn.rollback();
						conn.setAutoCommit(true);
					}
						
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				e.printStackTrace();
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
}
