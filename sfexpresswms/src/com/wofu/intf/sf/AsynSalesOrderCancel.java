package com.wofu.intf.sf;
/**
 * 取消发货
 */
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sf.integration.warehouse.service.GetoutsideToLscService;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynSalesOrderCancel extends Thread {
	
	private static String jobname = "同步发货单取消作业";
	private static String serviceType="SyncSalesOrderInfo";
	private static String sheettype="220902";

	public AsynSalesOrderCancel() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {	
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//取得要处理的单据号   对应customerdelive0的refsheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					String sql="select count(*) from customerdelive0 "
						+"where refsheetid='"+sheetid+"'";
					if (SQLHelper.intSelect(conn, sql)==0)
					{
						Log.info("单据不存在或者已处理,接口单号:"+sheetid);
						continue;
					}
					sql="select count(*) from it_infsheetlist where sheetid='"+sheetid+"' and sheettype=2209";
					if (SQLHelper.intSelect(conn, sql)==0) continue; //发货单还没创建，取消通知等待，避免由于通信原因取消通知先于发货通知
					
					try
					{
						conn.setAutoCommit(false);
						StringBuffer bizData=new StringBuffer();
						bizData.append("<wmsCancelSailOrderRequest>")
						.append("<checkword>").append(Params.checkword).append("</checkword>")
						.append("<company>").append(Params.company).append("</company>");
						
						//取customerdelive0取得订单信息
						sql="select sheetid from customerdelive0 "
									+"where refsheetid='"+sheetid+"'";
						Hashtable htorder=SQLHelper.oneRowSelect(conn, sql);
						String outbuzcode=htorder.get("sheetid").toString();
						bizData.append("<orderid>").append(outbuzcode).append("</orderid>")
						.append("</wmsCancelSailOrderRequest>");
						String result=GetoutsideToLscService.getoutsideToLscServices(bizData.toString());
						Log.info("result: "+result);
						
						//返回结果还原成document
						Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
						Element productinforspele = productinforspdoc.getDocumentElement();
						String flag=DOMHelper.getSubElementVauleByName(productinforspele, "result");
						if (flag.equalsIgnoreCase("2")) //失败
						{
							String errorMsg="";
							Element errorsele=(Element) productinforspele.getElementsByTagName("errors").item(0);
							NodeList errorlist=errorsele.getElementsByTagName("error");
							for(int j=0;j<errorlist.getLength();j++)
							{
								Element errorele=(Element) errorlist.item(j);
								String errorcode=DOMHelper.getSubElementVauleByName(errorele, "errorCode");
								String errordesc=DOMHelper.getSubElementVauleByName(errorele, "errorDescription");
								
								errorMsg=errorMsg+"错误代码:"+errorcode+",错误信息:"+errordesc+" ";	
							}
							Log.error(jobname, "同步发货单取消失败,接口单号:"+sheetid+",错误信息："+errorMsg);
							
						}else
						{
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"220902");
							
							sfUtil.recordMsg(conn, CommHelper.getMsgid(),outbuzcode,220902,serviceType);
							
							Log.info(jobname,"同步发货单取消成功,接口单号:"+sheetid);
						}
						conn.commit();
						conn.setAutoCommit(true);
					} catch (Exception e) {
						try {
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
						} catch (Exception e1) {
							Log.error(jobname, "回滚事务失败");
						}
						Log.error("105", jobname, Log.getErrorMessage(e));
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
			
	}
	
}
