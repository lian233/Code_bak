package com.wofu.intf.sf;
/**
 * �������ƻ������͵�����ϵͳ
 */
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sf.integration.warehouse.service.GetoutsideToLscService;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynAsnInfo extends Thread {
	
	private static String jobname = "ͬ����������ҵ";
	private static String serviceType="SyncAsnInfo";
	private static String sheettype="2227";
	public AsynAsnInfo() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				//ȡ��Ҫ����ĵ���  ��Ӧplanreceipt,planreceiptitem ��sheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,sheettype);

				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					Log.info("sheetid: "+sheetid);
					conn.setAutoCommit(false);
					
					StringBuffer bizData=new StringBuffer();
					bizData.append("<wmsPurchaseOrderRequest>")
					.append("<checkword>").append(Params.checkword).append("</checkword>")
					.append("<header>")
					.append("<company>").append(Params.company).append("</company>")
					.append("<warehouse>").append(Params.warehouse).append("</warehouse>")
					//�ɹ���������
					.append("<erp_order_num>").append(sheetid).append("</erp_order_num>")
					//��ⵥ����  10�ɹ����
					.append("<erp_order_type>�ɹ����</erp_order_type>");
					
					//ȡ�����ƻ�����Ϣ
					String sql="select shopid,flag,purdate,checkdate,note from planreceipt "
								+"where sheetid='"+sheetid+"'";
					
					Hashtable htplan=SQLHelper.oneRowSelect(conn, sql);
					
					//String shopid=htplan.get("shopid").toString();
					//Ԥ���ջ�ʱ��
					String purDate = htplan.get("purdate").toString().substring(0,19);; 
					//����ʱ��2100-00-00 11:11:11
					String order_date = htplan.get("checkdate").toString().substring(0,19);
					Log.info("order-date: "+order_date);
					String note=htplan.get("note").toString();
					int sheetflag=Integer.valueOf(htplan.get("flag").toString()).intValue();
					
					bizData.append("<order_date>"+order_date+"</order_date>")
					.append("<buyer>"+Params.linkman+"</buyer>")
					.append("<buyer_phone>").append(Params.phone).append("</buyer_phone>")
					.append("<scheduled_receipt_date>"+purDate+"</scheduled_receipt_date>")
					.append("<source_id>").append(Params.source_id).append("</source_id>")
					.append("<note_to_receiver>").append(note).append("</note_to_receiver>")
					.append("</header>")
					.append("<detailList>");

					sql= new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
						.append("a.qty,b.colorname,b.sizename from planreceiptitem a,v_barcodeall b where a.sheetid='")
						.append(sheetid).append("' and a.barcodeid=b.barcodeid").toString();
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						
						String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString();
						int qty=Double.valueOf(htsku.get("qty").toString()).intValue();
						String itemnote="��ɫ:"+htsku.get("colorname").toString()+" ����:"+htsku.get("sizename").toString().trim();
						bizData.append("<item>")
						.append("<erp_order_line_num>").append(i+1).append("</erp_order_line_num>")
		
						.append("<item >"+custombc+"</item>")
						.append("<total_qty>"+qty+"</total_qty>")
						.append("<note>"+itemnote+"</note>")				
						.append("</item>");
					}										
					bizData.append("</detailList></wmsPurchaseOrderRequest>");
					Log.info("bizdata: "+bizData.toString());
					String result=GetoutsideToLscService.getoutsideToLscServices(bizData.toString());
					Log.info("result:��"+result);
					
					Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
					Element productinforspele = productinforspdoc.getDocumentElement();
					
					String flag=DOMHelper.getSubElementVauleByName(productinforspele, "result");
					
					if (flag.equalsIgnoreCase("2")) //ʧ��
					{
						 String errorMsg= DOMHelper.getSubElementVauleByName(productinforspele, "remark");
						
						
						Log.error(jobname, "ͬ��������ʧ��,�ӿڵ���:"+sheetid+",������Ϣ��"+errorMsg);
						
					}else  //�ɹ��󱸷ݽӿڱ����ݣ�����ecs_bestlogisticsmsg�������
					{
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2227");
						
						sfUtil.recordMsg(conn, CommHelper.getMsgid(),sheetid,2227,serviceType);
						
						Log.info(jobname,"ͬ���������ɹ�,�ӿڵ���:"+sheetid);
					}
					
					conn.commit();
					conn.setAutoCommit(true);
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
