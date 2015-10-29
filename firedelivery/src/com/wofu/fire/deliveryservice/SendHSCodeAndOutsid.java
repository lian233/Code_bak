package com.wofu.fire.deliveryservice;
/**
 * 把海关条码跟快递单号  海关审核通过的时候发送数据到火力
 */
import java.sql.Connection;
import net.sf.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
public class SendHSCodeAndOutsid extends Thread {
	private static String jobname = "发送海关条码跟快递单号";
	private static String serviceType="SyncAsnInfo";
	private static String sheettype="22278";
	public SendHSCodeAndOutsid() {
		setDaemon(true);
		setName(jobname);
	}
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection conn = null;
			try {					
				//conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//取得要处理的单号  对应planreceipt,planreceiptitem 的sheetid
				/**
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					
					
					String result=CommHelper.sendRequest(Params.url, requestParams, "");
					//Log.info("result:　"+result);
					
					result=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
					/*
					Document productinfodoc = DOMHelper.newDocument(result, Params.encoding);
					Element productinfoele = productinfodoc.getDocumentElement();	
	
					Element responsesele=(Element) productinfoele.getElementsByTagName("response").item(0);
					
					String bizDataRsp=DOMHelper.getSubElementVauleByName(responsesele, "bizData");
					*/
					/**
					Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
					Element productinforspele = productinforspdoc.getDocumentElement();
					
					String flag=DOMHelper.getSubElementVauleByName(productinforspele, "flag");
					
					
					if (flag.equalsIgnoreCase("FAILURE")) //失败
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
						
						Log.error(jobname, "同步补货单失败,接口单号:"+sheetid+",错误信息："+errorMsg);
						
					}else  //成功后备份接口表数据，更新ecs_bestlogisticsmsg表的数据
					{
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2227");
						
						crossborderUtil.recordMsg(conn, msgid,sheetid,2227,serviceType);
						
						Log.info(jobname,"同步补货单成功,接口单号:"+sheetid);
					}
					
					conn.commit();
					conn.setAutoCommit(true);
				}**/
				HscodeInfo hscodeinfo = new HscodeInfo();
				hscodeinfo.setCustoms_barcode("wserlk");
				hscodeinfo.setDelivery("YTO");
				hscodeinfo.setDelivery_id("54654654");
				hscodeinfo.setOrder_id("DEPOT15063000001");
				String test=JSONObject.fromObject(hscodeinfo).toString();
				Log.info("test: "+test);
				String result =CommHelper.sendRequestT(Params.url,test);
				Log.info("result: "+result);
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
