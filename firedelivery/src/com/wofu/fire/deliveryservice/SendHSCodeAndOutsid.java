package com.wofu.fire.deliveryservice;
/**
 * �Ѻ����������ݵ���  �������ͨ����ʱ�������ݵ�����
 */
import java.sql.Connection;
import net.sf.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
public class SendHSCodeAndOutsid extends Thread {
	private static String jobname = "���ͺ����������ݵ���";
	private static String serviceType="SyncAsnInfo";
	private static String sheettype="22278";
	public SendHSCodeAndOutsid() {
		setDaemon(true);
		setName(jobname);
	}
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection conn = null;
			try {					
				//conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//ȡ��Ҫ����ĵ���  ��Ӧplanreceipt,planreceiptitem ��sheetid
				/**
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					
					
					String result=CommHelper.sendRequest(Params.url, requestParams, "");
					//Log.info("result:��"+result);
					
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
					
					
					if (flag.equalsIgnoreCase("FAILURE")) //ʧ��
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
						
						Log.error(jobname, "ͬ��������ʧ��,�ӿڵ���:"+sheetid+",������Ϣ��"+errorMsg);
						
					}else  //�ɹ��󱸷ݽӿڱ����ݣ�����ecs_bestlogisticsmsg�������
					{
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2227");
						
						crossborderUtil.recordMsg(conn, msgid,sheetid,2227,serviceType);
						
						Log.info(jobname,"ͬ���������ɹ�,�ӿڵ���:"+sheetid);
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
