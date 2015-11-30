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
				List infsheetlist=DtcTools.getInfDownItemNote(conn,sheetType);
				//----生成报文
				//报文头
				StringBuilder bizData=new StringBuilder();
				String sheetid=null;
				String actionType="1";
				for(Iterator it=infsheetlist.iterator();it.hasNext();){
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
					String sql ="select  Action,CustomBC,g.Country,g.MID,g.ProducerName,g.SupplierName,g.RegFlag,g.Name, g.ShortName, Spec,b.HSCode,UnitName,g.Price BasePrice,isnull(g.CustomsType,2) CustomsType,PostTaxNo,Weigh/1000 Weigh ,NetWeigh/1000 NetWeigh,"
							 +" DetailURL,b.BarcodeID , c.Name ColorName , s.Name SizeName , c.ColorID ColorID , s.SizeID SizeID,isnull(UnitNum,1) UnitNum ," 
							+	 " case when p.Unit = '' then UnitName else p.Unit end Unit ,isnull(e.isexperimental_unit,0) isexperimental_unit,g.Origin,g.DeptID "
							 +" from BarcodeTranList a  inner join Barcode b on  a.BarcodeID = b.BarcodeID and sheetid='"+sheetid+" ' inner join Merchandise g  on  "
						+"b.MID = g.MID inner join  Size s on g.MeasureType = s.MeasureTypeID  and b.SizeID "
						+"= s.SizeID inner join Color c  on b.ColorID = c.ColorID inner join  PostTariff p on g.PostTaxNo = "
						+"p.Code left outer join experimental_unit e  on b.hscode=e.hscode ";
					
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					
					
					
					for (int i=0;i<vtsku.size();i++){
						System.out.println("要同步的商品资料总数为:"+vtsku.size());
						Hashtable htsku=(Hashtable) vtsku.get(i);
						//查询图片
						sql ="select MarkExchange from GoodsImage where goodsid='"+htsku.get("MID").toString()+"'";
						String MarkExchange=SQLHelper.strSelect(conn, sql);
						sql ="select OriginPlaceCert from GoodsImage where goodsid='"+htsku.get("MID").toString()+"'";
						String OriginPlaceCert=SQLHelper.strSelect(conn, sql);
						//查询国家代码
						sql ="select code from country_code where country='"+htsku.get("Origin").toString()+"'";
						String originCode=SQLHelper.strSelect(conn, sql);
						if(originCode.equalsIgnoreCase(""))
						{
							System.out.println(sheetid+" 找不到对应的国家代码  "+htsku.get("Origin").toString());
							continue;
						}
						bizData.append("<"+messageType+">");
						bizData.append(DtcTools.CreateItem("ESHOP_ENT_CODE" , Params.EshopEntCode , null));
						bizData.append(DtcTools.CreateItem("ESHOP_ENT_NAME" , Params.EshopEntName , null));
						bizData.append(DtcTools.CreateItem("EXTERNAL_NO" , "BarcodeID" , htsku));
			     		bizData.append(DtcTools.CreateItem("SKU" , "CustomBC" , htsku));
						bizData.append(DtcTools.CreateItem("GOODS_NAME" , "ShortName" , htsku));
						bizData.append(DtcTools.CreateItem("GOODS_SPEC" , "Spec" , htsku));//规格
						bizData.append(DtcTools.CreateItem("DECLARE_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("UnitName").toString()) , null));
						bizData.append(DtcTools.CreateItem("POST_TAX_NO" , "PostTaxNo" , htsku));
						bizData.append(DtcTools.CreateItem("LEGAL_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("Unit").toString()) , null));
						bizData.append(DtcTools.CreateItem("CONV_LEGAL_UNIT_NUM" , "UnitNum" , htsku));
						if((Integer)htsku.get("CustomsType")==2){
							bizData.append(DtcTools.CreateItem("MARK_EXCHANGE" , MarkExchange , null));//外文标签的中文翻译件
								if(!(OriginPlaceCert.length()>300)){
									bizData.append(DtcTools.CreateItem("ORIGIN_PLACE_CERT" ,  OriginPlaceCert , null));//原产地证书
								}
							bizData.append(DtcTools.CreateItem("HS_CODE" , "HSCode" , htsku));//只有保税需要写hscode
							bizData.append(DtcTools.CreateItem("CONV_IN_AREA_UNIT_NUM" , "1" , null));//只有保税需要写入区计量单位折算数量  固定为1
							bizData.append(DtcTools.CreateItem("PRODUCER_NAME" , "ProducerName" , htsku));//只有保税需要 生产企业名称
							bizData.append(DtcTools.CreateItem("SUPPLIER_NAME" , "SupplierName" , htsku));//只有保税需要 供应商名称
							if(String.valueOf(htsku.get("DeptID")).indexOf("4")!=-1)
								bizData.append(DtcTools.CreateItem("IS_CNCA_POR" , "1" , null));//只有保税需要国外生产企业是否在中国注册备案（食药监局、国家认监委）奶粉产品才要
							else
								bizData.append(DtcTools.CreateItem("IS_CNCA_POR" , "0" , null));//只有保税需要国外生产企业是否在中国注册备案（食药监局、国家认监委）
							
						}
						else if((Integer)htsku.get("CustomsType")==1 && (Integer)htsku.get("isexperimental_unit")==1)//直邮并且是试点商品
							bizData.append(DtcTools.CreateItem("HS_CODE" , "HSCode" , htsku));
						else{
							bizData.append(DtcTools.CreateItem("IS_CNCA_POR" , "否" , null));
						}
						bizData.append(DtcTools.CreateItem("IN_AREA_UNIT" , DtcTools.GetUnitCode(conn,htsku.get("UnitName").toString()) , null));
						bizData.append(DtcTools.CreateItem("IS_EXPERIMENT_GOODS" , String.valueOf(htsku.get("isexperimental_unit")) , null));//是否是试点商品    0否  1是
//						bizData.append(DtcTools.CreateItem("ORIGIN_COUNTRY_CODE" , "Country" , htsku));//原产国代码  之后再改
						bizData.append(DtcTools.CreateItem("ORIGIN_COUNTRY_CODE" , originCode , null));//原产国代码  之后再改
						bizData.append(DtcTools.CreateItem("CHECK_ORG_CODE" , (Integer)htsku.get("CustomsType")==2?"500400":"500600" , null));//施检机构的代码  保税:500400  直邮：500600  2:保税  1：直邮
						bizData.append(DtcTools.CreateItem("IS_CNCA_POR_DOC" , "0" , null));//是否存在食药监局、国家认监委备案附件0：否1：是//
						bizData.append(DtcTools.CreateItem("IS_ORIGIN_PLACE_CERT" , "0" , null));//是否存在原产地证书0：否1：是//
						bizData.append(DtcTools.CreateItem("IS_TEST_REPORT" , "0" , null));//是否存在境外官方及第三方机构的检测报告0：否1：是//
						bizData.append(DtcTools.CreateItem("IS_LEGAL_TICKET" , "0" , null));//是否存在合法采购证明（国外进货发票或小票）0：否1：是//
						bizData.append(DtcTools.CreateItem("IS_MARK_EXCHANGE" , (Integer)htsku.get("CustomsType")==1?"0":"1" , null));//是否存外文标签的中文翻译件0：否1：是//
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
				sleep(30000);
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
