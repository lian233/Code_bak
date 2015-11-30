package com.wofu.intf.yjn;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class NewAsynProductInfo extends Thread {
	private static String jobname = "同步商品资料作业";
	private static String messageType="SKU_INFO";
	private static String sheetType = "881101";
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {		
			Connection conn = null;
			try {		
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//取接口表
				List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
				//----生成报文
				//报文头
				StringBuilder bizData=new StringBuilder();
				String sheetid=null;
				String actionType="1";
				for(Iterator it=infsheetlist.iterator();it.hasNext();){
					//
					Hashtable t = (Hashtable)it.next();
					Integer SerialID = (Integer)t.get("SerialID");
					sheetid= t.get("OperData").toString();
					
					Log.info("OperType : "+t.get("OperType").toString());
					
					if (t.get("OperType").toString().equals("99")){//暂停
						actionType = "3";
					}
					else if (t.get("OperType").toString().equals("101")){//变更
						actionType = "2";
					}
					else{
						actionType = "1";
					}
					
					Log.info("actionType1 : "+actionType);
					
					String sql = "select Action,CustomBC,g.Name, g.ShortName, Spec,HSCode,UnitName,g.Price BasePrice,PostTaxNo,Weigh/1000 Weigh ,NetWeigh/1000 NetWeigh,"
							+ " DetailURL,b.BarcodeID , c.Name ColorName , s.Name SizeName , c.ColorID ColorID , s.SizeID SizeID,isnull(UnitNum,1) UnitNum , "
								+" case when p.Unit = '' then UnitName else p.Unit end Unit "
							+ " from BarcodeTranList a , Barcode b , Merchandise g , Size s , Color c , PostTariff p  "
							+ " where a.BarcodeID = b.BarcodeID and b.MID = g.MID "
							+ " and g.MeasureType = s.MeasureTypeID and b.SizeID = s.SizeID and b.ColorID = c.ColorID "
							+ " and g.PostTaxNo = p.Code "
							+ " and SheetID = '"+sheetid+"' ";
					
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
										
					for (int i=0;i<vtsku.size();i++){	
						Hashtable htsku=(Hashtable) vtsku.get(i);
						bizData.append("<"+messageType+">");
						bizData.append(DtcTools.CreateItem("ESHOP_ENT_CODE" , Params.EshopEntCode , null));
						bizData.append(DtcTools.CreateItem("ESHOP_ENT_NAME" , Params.EshopEntName , null));
						bizData.append(DtcTools.CreateItem("EXTERNAL_NO" , "BarcodeID" , htsku));
						bizData.append(DtcTools.CreateItem("SKU" , "CustomBC" , htsku));
						bizData.append(DtcTools.CreateItem("GOODS_NAME" , "ShortName" , htsku));
						bizData.append(DtcTools.CreateItem("GOODS_SPEC" , "Spec" , htsku));//规格
						bizData.append(DtcTools.CreateItem("DECLARE_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("UnitName").toString()) , null));
						bizData.append(DtcTools.CreateItem("POST_TAX_NO" , "PostTaxNo" , htsku));
						//bizData.append(DtcTools.CreateItem("LEGAL_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("UnitName").toString()) , null));
						bizData.append(DtcTools.CreateItem("LEGAL_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("Unit").toString()) , null));

						//bizData.append(DtcTools.CreateItem("CONV_LEGAL_UNIT_NUM" , "1" , null));
						bizData.append(DtcTools.CreateItem("CONV_LEGAL_UNIT_NUM" , "UnitNum" , htsku));
						//bizData.append("<LEGAL_UNIT />");
						//bizData.append("<CONV_LEGAL_UNIT_NUM>1<CONV_LEGAL_UNIT_NUM />");
						//bizData.append(DtcTools.CreateItem("CONV_LEGAL_UNIT_NUM" , "" , htsku));
						bizData.append(DtcTools.CreateItem("HS_CODE" , "HSCode" , htsku));
						bizData.append(DtcTools.CreateItem("IN_AREA_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("UnitName").toString()) , null));
						bizData.append(DtcTools.CreateItem("CONV_IN_AREA_UNIT_NUM" , "1" , null));
						//电商相关接口设计-20150309新增参数
						bizData.append(DtcTools.CreateItem("IS_EXPERIMENT_GOODS" , "0" , null));//是否试点商品   0：否1：是
						bizData.append(DtcTools.CreateItem("IS_CNCA_POR_DOC" , "0" , null));//是否存在食药监局、国家认监委备案附件0：否1：是
						bizData.append(DtcTools.CreateItem("IS_ORIGIN_PLACE_CERT" , "0" , null));//是否存在食药监局、国家认监委备案附件0：否1：是
						bizData.append(DtcTools.CreateItem("IS_TEST_REPORT" , "1" , null));//是否存在境外官方及第三方机构的检测报告0：否1：是
						bizData.append(DtcTools.CreateItem("IS_LEGAL_TICKET" , "1" , null));//是否存在合法采购证明（国外进货发票或小票）0：否1：是
						bizData.append(DtcTools.CreateItem("IS_MARK_EXCHANGE" , "1" , null));//是否存外文标签的中文翻译件0：否1：是
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
					Log.info("result:　"+result);
					
					
					if (result.equalsIgnoreCase("false")) //失败
					{	
						
						DtcTools.backUpInf(conn,SerialID,result);
						Log.error(jobname, "同步商品资料失败,接口单号:"+SerialID+",错误信息："+result);
						
					}else   //同步成功，备份接口数据，写入ecs_bestlogisticsmsg表
					{
						DtcTools.backUpInf(conn,SerialID,result);
						Log.error(jobname, "同步商品资料成功,接口单号:"+SerialID);
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
