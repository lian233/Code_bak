package com.wofu.ecommerce.taobao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import com.wofu.ecommerce.taobao.Processors;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class ReturnBatchDeal extends Thread {
	private static String jobname="处理天猫退款退货";
	
	public void run() {
		
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection conn = null;
			
			try {												
				conn = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				
				String sql="select shopid from ContactShopContrast where tradecontactid="+Params.tradecontactid;
				String shopid=SQLHelper.strSelect(conn, sql);
				
				sql="select sheetid from it_upnote where sheettype=2237 and receiver='"+shopid+"'";
				
				List sheetlist=SQLHelper.multiRowListSelect(conn, sql);
				
				for(Iterator it=sheetlist.iterator();it.hasNext();){
				
					String sheetid=(String) it.next();
					
					sql="select sheetid,bustype,refund_id,refund_phase,refund_version,"
						+"isnull(message,'') message,operator,addressid,refuse_proof from ns_return_batch_operate "
						+"where sheetid='"+sheetid+"'";
				
				
					Hashtable htintf=SQLHelper.oneRowSelect(conn, sql);
					
					String shid=htintf.get("sheetid").toString();
					String bustype=htintf.get("bustype").toString();
					long refundid=Long.valueOf(htintf.get("refund_id").toString());
					String refundphase=htintf.get("refund_phase").toString();
					long refundversion=Long.valueOf(htintf.get("refund_version").toString());
					String message=htintf.get("message").toString();
					String operator=htintf.get("operator").toString();
					long addressid=Long.valueOf(htintf.get("addressid").toString());
					InputStream refuse_proof=(InputStream)htintf.get("refuse_proof");
					
					String processorClassName = Processors.getProcessor(bustype);
					
			
					TMProcessor processor = (TMProcessor) Class.forName(processorClassName).newInstance();
					
					processor.setUrl(Params.url);
					processor.setAppkey(Params.appkey);
					processor.setAppsecret(Params.appsecret);
					processor.setToken(Params.authcode);
					processor.setRefund_id(refundid);
					processor.setRefund_phase(refundphase);
					processor.setRefund_version(refundversion);
					processor.setSeller_logistics_address_id(addressid);
					processor.setMessage(message);
					processor.setRefuse_message(message);
					processor.setRefuse_proof(refuse_proof);
					processor.setOperator(operator);
					
					boolean successflag=processor.process();
					
					if (successflag)
					{
						try {
							conn.setAutoCommit(false);

							sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
									+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
									+ " where SheetID = '"+ sheetid+ "' and SheetType = 2237";
							SQLHelper.executeSQL(conn, sql);

							sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=2237";

							SQLHelper.executeSQL(conn, sql);
							conn.commit();
							conn.setAutoCommit(true);
						} catch (SQLException sqle) {
							if (!conn.getAutoCommit())
								try {
									conn.rollback();
								} catch (Exception e1) {
								}
							try {
								conn.setAutoCommit(true);
							} catch (Exception e1) {
							}
							throw new JSQLException(sql, sqle);
						}
						Log.info("处理天猫退款退货请求成功,单号:"+refundid+",业务类型:"+bustype);
					}
					else
					{
						Log.info("处理天猫退款退货请求失败,单号:"+refundid+",业务类型:"+bustype);
					}
					
				}
				
				
			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	
}	
