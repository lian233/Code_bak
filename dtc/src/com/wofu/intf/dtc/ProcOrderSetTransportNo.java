package com.wofu.intf.dtc;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class ProcOrderSetTransportNo extends Thread {

	private static String jobname = "��������Ӧ���˵�����ҵ";		
	private static String messageType="ORDER_SET_TRANSPORT_NO";
	private static String sheetType = "880022";
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");

		do {
			Connection conn = null;
			try {										
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//ȡ�ӿڱ�
				List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
				//----���ɱ���
				//����
				String sheetid=null;
				for(Iterator it=infsheetlist.iterator();it.hasNext();){
					//
					Hashtable t = (Hashtable)it.next();
					Integer SerialID = (Integer)t.get("SerialID");
					String sheetID= t.get("OperData").toString();
					
					String actionType="1";//1������  2���˻� 3��ȡ��
					if (t.get("OperType").toString().equals("99")){//ȡ��
						actionType = "3";
					}
					else if (t.get("OperType").toString().equals("101")){//���
						actionType = "2";
					}
					else{
						actionType = "1";
					}
 
					String sql = "select * from OutStockNote where SheetID = '"+sheetID+"'";
								
					Hashtable dt=SQLHelper.oneRowSelect(conn, sql);
					if (dt.size() <= 0){
						Log.info("���ⵥ�����ڣ�" + sheetID);
						continue;
					}
					
					//��������
					//��ͷ
					StringBuilder bizSheet=new StringBuilder();

					bizSheet.insert(0, "<ORDER_SET_TRANSPORT_NO>");
					
					bizSheet.append(DtcTools.CreateItem("ESHOP_ENT_CODE" , Params.EshopEntCode , null));//������ҵ����
					bizSheet.append(DtcTools.CreateItem("ESHOP_ENT_NAME" , Params.EshopEntName , null));//������ҵ����
					bizSheet.append(DtcTools.CreateItem("ORIGINAL_ORDER_NO" , "CustomPurSheetID" , dt));//ԭʼ�������
					bizSheet.append(DtcTools.CreateItem("TRANSPORT_BILL_NO" , "DeliverySheetID" , dt));//�˵���

					bizSheet.append("</ORDER_SET_TRANSPORT_NO>");
					
					DtcTools.createBody(bizSheet);
					bizSheet.insert(0, DtcTools.createHead(messageType,actionType));
					DtcTools.AddHeadRear(bizSheet);											
					
					Log.info("data : "+bizSheet.toString());

					/* test close*/
					String bizData1 = DtcUtil.filterChar(bizSheet.toString() );
					
					Map requestParams=DtcUtil.makeRequestParams(bizData1);
					conn.setAutoCommit(false);
					String result=CommHelper.sendRequest(Params.url, requestParams, "");
					Log.info("result:��"+result);
					
					if (result.equalsIgnoreCase("false")) //ʧ��
					{	
						
						DtcTools.backUpInf(conn,SerialID,result);
						Log.error(jobname, jobname + "ʧ��,�ӿڵ���:"+SerialID+",������Ϣ��"+result);
						
					}else   //ͬ���ɹ������ݽӿ����ݣ�д��ecs_bestlogisticsmsg��
					{
						DtcTools.backUpInf(conn,SerialID,result);
						Log.error(jobname, jobname + "�ɹ�,�ӿڵ���:"+SerialID);
					}
					
					//num=0;
					//bizData.delete(bizData.indexOf("<products>")+10, bizData.length());
					//Log.info("ɾ���������Ϊ:��"+bizData.toString());
				}					
				conn.commit();
				conn.setAutoCommit(true);
								
			} catch (Exception e) {
				e.printStackTrace();
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
