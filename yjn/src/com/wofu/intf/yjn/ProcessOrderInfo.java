package com.wofu.intf.yjn;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class ProcessOrderInfo extends Thread {

	private static String jobname = "�����׽��϶�����ҵ";		
	private static String messageType="ORDER_INFO";
	private static String sheetType = "880020";
	private static DecimalFormat sf= new DecimalFormat("0.00");  //������λС������������
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");

		do {
			Connection conn = null;
			try {										
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//ȡ�ӿڱ�
				List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
				//����
				for(Iterator it=infsheetlist.iterator();it.hasNext();){
					//
					Hashtable t = (Hashtable)it.next();
					Integer SerialID = (Integer)t.get("SerialID");
					//String CustomsNo = (String)t.get("Owner");
					String sheetID = t.get("OperData").toString();
					String actionType="I";//1������  2���˻� 3��ȡ��
					if (t.get("OperType").toString().equals("99")){//ȡ��
						actionType = "3";
					}
					else if (t.get("OperType").toString().equals("101")){//���
						actionType = "2";
					}
					else{
						actionType = "I";
					}
					
					String sql = "select * from OutStockNote where SheetID = '"+sheetID+"'";
								
					Hashtable dtOut=SQLHelper.oneRowSelect(conn, sql);
					if (dtOut.size() <= 0){
						Log.info("���ⵥ�����ڣ�" + sheetID);
						continue;
					}
						
					
					//��ϸ
					StringBuilder bizItem = new StringBuilder();

					sql = "select CustomBC, CustomsListNO, NotifyQty, a.Price, Weigh/1000 Weigh, NetWeigh/1000 NetWeigh, a.Notes, "
						+ " b.ColorID , b.SizeID, s.Name SizeName , c.Name ColorName , UnitName, a.Price * NotifyQty TotalPrice, "
						//+ " IsNull(C.Name, '') + ' ' + IsNull(S.Name, '') Spec , PostTaxNo, Country" 
						+ " IsNull(B.HSCode, '') HSCode, m.Spec , PostTaxNo, Country"
					  + " from OutStockNoteItem a , Barcode b , Merchandise m , Size s , Color c "
					  + " where a.BarcodeID = b.BarcodeID and a.MID= b.MID and b.MID = m.MID and a.MID = m.MID "
					  + "  and M.MeasureType = s.MeasureTypeID and b.SizeID *= s.SizeID and b.ColorID *= c.ColorID "
					  + " and SheetID = '" + sheetID + "' ";
					
					Vector vtItem = SQLHelper.multiRowSelect(conn, sql);
					BigDecimal totalTaxFee = new BigDecimal(0) ;
					String taxFee = "";
					String strCountryCode = "";
					for (int j=0; j<vtItem.size(); j++) {
						Hashtable htItem=(Hashtable) vtItem.get(j);
						
						bizItem.append("<ORDER_DETAIL>");
						bizItem.append(DtcTools.CreateItem("SKU" , "CustomBC" , htItem));//��Ʒ���� SKU
						if ("".equals(strCountryCode)){
							strCountryCode = htItem.get("Country").toString();
						}
						//bizItem.append(DtcTools.CreateItem("GOODS_SPEC" , spec, null));//����ͺ�
						bizItem.append(DtcTools.CreateItem("GOODS_SPEC" , "Spec" , htItem));//����ͺ�
						bizItem.append(DtcTools.CreateItem("CURRENCY_CODE" , "142" , null));//CURRENCY_CODE 142 �����
						bizItem.append(DtcTools.CreateItem("PRICE" , "Price" , htItem));//��Ʒ����
						bizItem.append(DtcTools.CreateItem("QTY" , "NotifyQty" , htItem));//��Ʒ����
						bizItem.append(DtcTools.CreateItem("GOODS_FEE" , "TotalPrice" , htItem));//��Ʒ�ܼ�
						taxFee = sf.format(Double.parseDouble(DtcTools.CalTax(conn ,htItem.get("PostTaxNo").toString() , 
								htItem.get("TotalPrice").toString() )));
						bizItem.append(DtcTools.CreateItem("TAX_FEE" , 	 taxFee, null));//˰����
						
						bizItem.append("</ORDER_DETAIL>");
						totalTaxFee = totalTaxFee.add(new BigDecimal(Double.parseDouble(taxFee)));
					}
					if (bizItem.length() <= 0){//û����ϸ
						Log.info("�Ҳ�����ϸ��" + sql);
						continue;
					}
					//��������
					//��ͷ
					StringBuilder bizSheet = new StringBuilder();
					bizSheet.append(DtcTools.CreateItem("CUSTOMS_CODE", Params.CustomsCode , null));//�걨���ش���
					bizSheet.append(DtcTools.CreateItem("BIZ_TYPE_CODE", Params.biz_type_code, null));//ҵ������  ֱ�����ڣ�I10,������˰���ڣ�I20
					bizSheet.append(DtcTools.CreateItem("ORIGINAL_ORDER_NO" , "CustomPurSheetID" , dtOut));//ԭʼ�������
					bizSheet.append(DtcTools.CreateItem("ESHOP_ENT_CODE" , Params.EshopEntCode , null));//������ҵ����
					bizSheet.append(DtcTools.CreateItem("ESHOP_ENT_NAME" , Params.EshopEntName , null));//������ҵ����
					bizSheet.append(DtcTools.CreateItem("DESP_ARRI_COUNTRY_CODE" , strCountryCode , null));//���˹�  116	�ձ�
					bizSheet.append(DtcTools.CreateItem("SHIP_TOOL_CODE" , "Y" , null));//���䷽ʽ
					bizSheet.append(DtcTools.CreateItem("CHECK_TYPE" , "R" , null));//��֤����R:�ջ��� P:֧����
					bizSheet.append(DtcTools.CreateItem("RECEIVER_ID_NO" , "CertNo" , dtOut));//�ջ������֤����
					bizSheet.append(DtcTools.CreateItem("RECEIVER_NAME" , "CertName" , dtOut));//�ջ�������
					bizSheet.append(DtcTools.CreateItem("RECEIVER_ADDRESS" , "Address" , dtOut));//�ջ��˵�ַ
					bizSheet.append(DtcTools.CreateItem("RECEIVER_TEL" , "Tele" , dtOut));//�ջ��˵绰
					//Double fee = DtcTools.GetOrderTotalAmount(conn , sheetID);					
					bizSheet.append(DtcTools.CreateItem("GOODS_FEE" , DtcTools.GetOrderTotalAmount(conn , sheetID) , null));	//�����ܶ�
					//bizSheet.append(DtcTools.CreateItem("TAX_FEE" , "Tax" , dtOut));//˰���ܶ�
					Log.info("totalTaxFee: "+totalTaxFee);
					if(totalTaxFee.compareTo(new BigDecimal(50))>0)
						bizSheet.append(DtcTools.CreateItem("TAX_FEE" , sf.format(totalTaxFee.doubleValue()), null));//˰���ܶ�
					else{
						bizSheet.append(DtcTools.CreateItem("TAX_FEE", "0.0000" , null));//˰���ܶ�
					}

					bizSheet.append(DtcTools.CreateItem("GROSS_WEIGHT" , DtcTools.GetWeigh(conn , sheetID , 2), null));//ë��
					
					//bizData.append("<PROXY_ENT_CODE />");
					//bizData.append("<PROXY_ENT_NAME />");
					bizSheet.append(DtcTools.CreateItem("SORTLINE_ID" , Params.SortLineID , null));//�ּ���ID SORTLINE01�������̲�ո�  SORTLINE02�������������� SORTLINE03�������̲ˮ��"
					//����
					bizSheet.insert(0, "<ORDER_HEAD>");
					bizSheet.append(bizItem.toString());
					bizSheet.append("</ORDER_HEAD>");
					
					DtcTools.createBody(bizSheet);
					bizSheet.insert(0, DtcTools.createHead(messageType,actionType));
					DtcTools.AddHeadRear(bizSheet);											
					
					Log.info("data : "+bizSheet.toString());
					
					
					/* test close*/
					String bizData1 = DtcUtil.filterChar(bizSheet.toString() );
					
					Map requestParams=DtcUtil.makeRequestParams(bizData1);
//					conn.setAutoCommit(false);
//					String result=CommHelper.sendRequest(Params.url, requestParams, "");
//					Log.info("result:��"+result);
//					
//					if (result.equalsIgnoreCase("false")) //ʧ��
//					{	
//						Log.error(jobname, jobname + "ʧ��,�ӿڵ���:"+SerialID+",������Ϣ��"+result);
//					}else   //ͬ���ɹ������ݽӿ����ݣ�д��ecs_bestlogisticsmsg��
//					{	
//						Log.error(jobname, jobname + "�ɹ�,�ӿڵ���:"+SerialID);
//					}
//					
//					DtcTools.backUpInf(conn,SerialID,result);
					
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
