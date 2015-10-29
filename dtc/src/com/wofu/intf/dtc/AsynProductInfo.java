package com.wofu.intf.dtc;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynProductInfo extends Thread {
	private static String jobname = "ͬ����Ʒ������ҵ";
	private static String messageType="SKU_INFO";
	private static String sheetType = "881101";
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection conn = null;
			try {		
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//ȡ�ӿڱ�
				List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
				//----���ɱ���
				//����ͷ
				StringBuilder bizData=new StringBuilder();
				String sheetid=null;
				String actionType="1";
				for(Iterator it=infsheetlist.iterator();it.hasNext();){
					Hashtable t = (Hashtable)it.next();
					Integer SerialID = (Integer)t.get("SerialID");
					sheetid= t.get("OperData").toString();
					Log.info("OperType : "+t.get("OperType").toString());
					if (t.get("OperType").toString().equals("99")){//��ͣ
						actionType = "3";
					}
					else if (t.get("OperType").toString().equals("101")){//���
						actionType = "2";
					}
					else{
						actionType = "1";
					}
					Log.info("actionType1 : "+actionType);
					String sql ="select Action,CustomBC,g.Country,g.MID,g.ProducerName,g.SupplierName,g.RegFlag,g.Name, g.ShortName, Spec,b.HSCode,UnitName,g.Price BasePrice,isnull(g.CustomsType,2) CustomsType,PostTaxNo,Weigh/1000 Weigh ,NetWeigh/1000 NetWeigh,"
							 +" DetailURL,b.BarcodeID , c.Name ColorName , s.Name SizeName , c.ColorID ColorID , s.SizeID SizeID,isnull(UnitNum,1) UnitNum ," 
							+	 " case when p.Unit = '' then UnitName else p.Unit end Unit ,isnull(e.isexperimental_unit,0) isexperimental_unit,g.Origin,g.DeptID "
							 +" from BarcodeTranList a  inner join Barcode b on  a.BarcodeID = b.BarcodeID and sheetid='"+sheetid+" ' inner join Merchandise g  on  "
						+"b.MID = g.MID inner join  Size s on g.MeasureType = s.MeasureTypeID  and b.SizeID "
						+"= s.SizeID inner join Color c  on b.ColorID = c.ColorID inner join  PostTariff p on g.PostTaxNo = "
						+"p.Code left outer join experimental_unit e  on b.hscode=e.hscode ";
					
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					
					
					
					for (int i=0;i<vtsku.size();i++){
						System.out.println("Ҫͬ������Ʒ��������Ϊ:"+vtsku.size());
						Hashtable htsku=(Hashtable) vtsku.get(i);
						sql ="select MarkExchange from GoodsImage where goodsid='"+htsku.get("MID").toString()+"'";
						String MarkExchange=SQLHelper.strSelect(conn, sql);
						sql ="select OriginPlaceCert from GoodsImage where goodsid='"+htsku.get("MID").toString()+"'";
						String OriginPlaceCert=SQLHelper.strSelect(conn, sql);
						bizData.append("<"+messageType+">");
						bizData.append(DtcTools.CreateItem("ESHOP_ENT_CODE" , Params.EshopEntCode , null));
						bizData.append(DtcTools.CreateItem("ESHOP_ENT_NAME" , Params.EshopEntName , null));
						bizData.append(DtcTools.CreateItem("EXTERNAL_NO" , "BarcodeID" , htsku));
			     		bizData.append(DtcTools.CreateItem("SKU" , "CustomBC" , htsku));
						bizData.append(DtcTools.CreateItem("GOODS_NAME" , "ShortName" , htsku));
						bizData.append(DtcTools.CreateItem("GOODS_SPEC" , "Spec" , htsku));//���
						bizData.append(DtcTools.CreateItem("DECLARE_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("UnitName").toString()) , null));
						bizData.append(DtcTools.CreateItem("POST_TAX_NO" , "PostTaxNo" , htsku));
						bizData.append(DtcTools.CreateItem("LEGAL_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("Unit").toString()) , null));
						bizData.append(DtcTools.CreateItem("CONV_LEGAL_UNIT_NUM" , "UnitNum" , htsku));
						if((Integer)htsku.get("CustomsType")==2){
							bizData.append(DtcTools.CreateItem("MARK_EXCHANGE" , MarkExchange , null));//���ı�ǩ�����ķ����
								if(!(OriginPlaceCert.length()>300)){
									bizData.append(DtcTools.CreateItem("ORIGIN_PLACE_CERT" ,  OriginPlaceCert , null));//ԭ����֤��
								}
							bizData.append(DtcTools.CreateItem("HS_CODE" , "HSCode" , htsku));//ֻ�б�˰��Ҫдhscode
							bizData.append(DtcTools.CreateItem("CONV_IN_AREA_UNIT_NUM" , "1" , null));//ֻ�б�˰��Ҫд����������λ��������  �̶�Ϊ1
							bizData.append(DtcTools.CreateItem("PRODUCER_NAME" , "ProducerName" , htsku));//ֻ�б�˰��Ҫ ������ҵ����
							bizData.append(DtcTools.CreateItem("SUPPLIER_NAME" , "SupplierName" , htsku));//ֻ�б�˰��Ҫ ��Ӧ������
							if(String.valueOf(htsku.get("DeptID")).indexOf("4")!=-1)
								bizData.append(DtcTools.CreateItem("IS_CNCA_POR" , "1" , null));//ֻ�б�˰��Ҫ����������ҵ�Ƿ����й�ע�ᱸ����ʳҩ��֡������ϼ�ί���̷۲�Ʒ��Ҫ
							else
								bizData.append(DtcTools.CreateItem("IS_CNCA_POR" , "0" , null));//ֻ�б�˰��Ҫ����������ҵ�Ƿ����й�ע�ᱸ����ʳҩ��֡������ϼ�ί��
							
						}
						else if((Integer)htsku.get("CustomsType")==1 && (Integer)htsku.get("isexperimental_unit")==1)//ֱ�ʲ������Ե���Ʒ
							bizData.append(DtcTools.CreateItem("HS_CODE" , "HSCode" , htsku));
						else{
							bizData.append(DtcTools.CreateItem("IS_CNCA_POR" , "��" , null));
						}
						bizData.append(DtcTools.CreateItem("IN_AREA_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("UnitName").toString()) , null));
						bizData.append(DtcTools.CreateItem("IS_EXPERIMENT_GOODS" , String.valueOf(htsku.get("isexperimental_unit")) , null));//�Ƿ����Ե���Ʒ    0��  1��
						bizData.append(DtcTools.CreateItem("ORIGIN_COUNTRY_CODE" , "Country" , htsku));//ԭ��������  ֮���ٸ�
						bizData.append(DtcTools.CreateItem("CHECK_ORG_CODE" , (Integer)htsku.get("CustomsType")==2?"500400":"500600" , null));//ʩ������Ĵ���  ��˰:500400  ֱ�ʣ�500600  2:��˰  1��ֱ��
						bizData.append(DtcTools.CreateItem("IS_CNCA_POR_DOC" , "0" , null));//�Ƿ����ʳҩ��֡������ϼ�ί��������0����1����//
						bizData.append(DtcTools.CreateItem("IS_ORIGIN_PLACE_CERT" , "0" , null));//�Ƿ����ԭ����֤��0����1����//
						bizData.append(DtcTools.CreateItem("IS_TEST_REPORT" , "0" , null));//�Ƿ���ھ���ٷ��������������ļ�ⱨ��0����1����//
						bizData.append(DtcTools.CreateItem("IS_LEGAL_TICKET" , "0" , null));//�Ƿ���ںϷ��ɹ�֤�������������Ʊ��СƱ��0����1����//
						bizData.append(DtcTools.CreateItem("IS_MARK_EXCHANGE" , (Integer)htsku.get("CustomsType")==1?"0":"1" , null));//�Ƿ�����ı�ǩ�����ķ����0����1����//
						bizData.append("</"+messageType+">");
					}
					if (bizData.length() > 0){
						DtcTools.createBody(bizData);
						bizData.insert(0, DtcTools.createHead(messageType,actionType));
						DtcTools.AddHeadRear(bizData);						
					}
					Log.info("data: "+bizData.toString());
					String bizData1 = DtcUtil.filterChar(bizData.toString() );
					Map requestParams=DtcUtil.makeRequestParams(bizData1);
					conn.setAutoCommit(false);
					String result=CommHelper.sendRequest(Params.url, requestParams, "");
					Log.info("result:��"+result);
					
					
					if (result.equalsIgnoreCase("false")) //ʧ��
					{	
						
						DtcTools.backUpInf(conn,SerialID,result);
						Log.error(jobname, "ͬ����Ʒ����ʧ��,�ӿڵ���:"+SerialID+",������Ϣ��"+result);
						
					}else   //ͬ���ɹ������ݽӿ����ݣ�д��ecs_bestlogisticsmsg��
					{
						DtcTools.backUpInf(conn,SerialID,result);
						Log.error(jobname, "ͬ����Ʒ���ϳɹ�,�ӿڵ���:"+SerialID);
					}
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
