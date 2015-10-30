package com.wofu.intf.sf;
/**
 * ȡ������
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
	
	private static String jobname = "ͬ��������ȡ����ҵ";
	private static String serviceType="SyncSalesOrderInfo";
	private static String sheettype="220902";

	public AsynSalesOrderCancel() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {	
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//ȡ��Ҫ����ĵ��ݺ�   ��Ӧcustomerdelive0��refsheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					String sql="select count(*) from customerdelive0 "
						+"where refsheetid='"+sheetid+"'";
					if (SQLHelper.intSelect(conn, sql)==0)
					{
						Log.info("���ݲ����ڻ����Ѵ���,�ӿڵ���:"+sheetid);
						continue;
					}
					sql="select count(*) from it_infsheetlist where sheetid='"+sheetid+"' and sheettype=2209";
					if (SQLHelper.intSelect(conn, sql)==0) continue; //��������û������ȡ��֪ͨ�ȴ�����������ͨ��ԭ��ȡ��֪ͨ���ڷ���֪ͨ
					
					try
					{
						conn.setAutoCommit(false);
						StringBuffer bizData=new StringBuffer();
						bizData.append("<wmsCancelSailOrderRequest>")
						.append("<checkword>").append(Params.checkword).append("</checkword>")
						.append("<company>").append(Params.company).append("</company>");
						
						//ȡcustomerdelive0ȡ�ö�����Ϣ
						sql="select sheetid from customerdelive0 "
									+"where refsheetid='"+sheetid+"'";
						Hashtable htorder=SQLHelper.oneRowSelect(conn, sql);
						String outbuzcode=htorder.get("sheetid").toString();
						bizData.append("<orderid>").append(outbuzcode).append("</orderid>")
						.append("</wmsCancelSailOrderRequest>");
						String result=GetoutsideToLscService.getoutsideToLscServices(bizData.toString());
						Log.info("result: "+result);
						
						//���ؽ����ԭ��document
						Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
						Element productinforspele = productinforspdoc.getDocumentElement();
						String flag=DOMHelper.getSubElementVauleByName(productinforspele, "result");
						if (flag.equalsIgnoreCase("2")) //ʧ��
						{
							String errorMsg="";
							Element errorsele=(Element) productinforspele.getElementsByTagName("errors").item(0);
							NodeList errorlist=errorsele.getElementsByTagName("error");
							for(int j=0;j<errorlist.getLength();j++)
							{
								Element errorele=(Element) errorlist.item(j);
								String errorcode=DOMHelper.getSubElementVauleByName(errorele, "errorCode");
								String errordesc=DOMHelper.getSubElementVauleByName(errorele, "errorDescription");
								
								errorMsg=errorMsg+"�������:"+errorcode+",������Ϣ:"+errordesc+" ";	
							}
							Log.error(jobname, "ͬ��������ȡ��ʧ��,�ӿڵ���:"+sheetid+",������Ϣ��"+errorMsg);
							
						}else
						{
							IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"220902");
							
							sfUtil.recordMsg(conn, CommHelper.getMsgid(),outbuzcode,220902,serviceType);
							
							Log.info(jobname,"ͬ��������ȡ���ɹ�,�ӿڵ���:"+sheetid);
						}
						conn.commit();
						conn.setAutoCommit(true);
					} catch (Exception e) {
						try {
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
						} catch (Exception e1) {
							Log.error(jobname, "�ع�����ʧ��");
						}
						Log.error("105", jobname, Log.getErrorMessage(e));
					}
				}
			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));			
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
			
	}
	
}
