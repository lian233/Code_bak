package com.wofu.intf.sf;
import java.util.List;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sf.integration.warehouse.service.GetoutsideToLscService;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.intf.IntfUtils;
public class CheckOrderExecuter extends Executer {
	private String company="";
	private String checkword="";
	private String warehouse="";
	private String interfacesystem="";
	private static String jobName="定时检查顺风物流出库单";

	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		company=prop.getProperty("customerCode");  
		checkword=prop.getProperty("checkword");  
		warehouse=prop.getProperty("warehouse");  
		interfacesystem=prop.getProperty("interfacesystem");  
		try
		{
			updateJobFlag(1);
			checkOrder();
			UpdateTimerJob();
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		} catch (Exception e) {
			try {
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"回滚事务失败");
				Log.error(jobName, e1.getMessage());
			}
			
			try{
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			}catch(Exception ex){
				Log.error(jobName,"更新任务信息失败");
				Log.error(jobName, ex.getMessage());
			}
			Log.error(jobName,"错误信息:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
		} finally {
			
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"更新处理标志失败");
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}
		
		
		
	}
	
	private void checkOrder() throws Exception
	{
		String sql="select sheetid from customerdelive0 order by delivedate";
		List sheetlist=this.getDao().oneListSelect(sql);
	
		for (int k=0;k<sheetlist.size();k++)
		{
			
			for(int n=0;n<10;)
			{
				try {
					
					String sheetid=(String) sheetlist.get(k);
	
										
					StringBuffer bizData = new StringBuffer();
					bizData.append("<wmsSailOrderStatusQueryRequest>")
					.append("<checkword>").append(checkword).append("</checkword>")
					.append("<company>").append(company).append("</company>")
					.append("<orderid>").append(sheetid).append("</orderid>")
					.append("</wmsSailOrderStatusQueryRequest>");
	
					Log.info("bizData:　"+bizData.toString());
					
					String result=GetoutsideToLscService.getoutsideToLscServices(bizData.toString());
					Log.info("result: "+result);
					
					//返回结果还原成document
					Document outStockStatuseledoc = DOMHelper.newDocument(result, "gbk");
					Element outStockStatusele = outStockStatuseledoc.getDocumentElement();
					
					String flag=DOMHelper.getSubElementVauleByName(outStockStatusele, "result");
					
					if (flag.equalsIgnoreCase("2"))
					{
						String errorMsg=DOMHelper.getSubElementVauleByName(outStockStatusele, "remark");
						Log.warn("检查出库单","获取出库单信息失败,出库单号:"+sheetid+",错误信息:"+errorMsg);
						
					
					}
					else
					{
						
						Element order = DOMHelper.getSubElementsByName(outStockStatusele, "order")[0];
						Element steps = DOMHelper.getSubElementsByName(outStockStatusele, "steps")[0];
						String orderCode=DOMHelper.getSubElementVauleByName(order, "orderid");
						Element[] step = DOMHelper.getSubElementsByName(steps, "step");
						for(Element e:step){
							String status = DOMHelper.getSubElementVauleByName(e, "status");
							String remark = DOMHelper.getSubElementVauleByName(e, "remark");
							status = status.substring(0,status.indexOf(":"));
							if (status.equalsIgnoreCase("900"))		//已发货
							{
								//取得订单明细String checkword,String company,String warehouse,String orderid
								String orderDetail = getOrderDetail(checkword,company,warehouse,orderCode);
								Document doc = DOMHelper.newDocument(orderDetail, "gbk");
								Element saleorderstatusele = doc.getDocumentElement();
								String result1 = DOMHelper.getSubElementVauleByName(saleorderstatusele, "result");
								if("2".equals(result1)) continue;
								Element header = DOMHelper.getSubElementsByName(saleorderstatusele, "header")[0];
								sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=100";
								if (this.getDao().intSelect(sql)>0) break;			
								
								sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=100";
								if (this.getDao().intSelect(sql)>0) break;
								
								sql="select count(*) from customerdelive0 where sheetid='"+orderCode+"'";
								if (this.getDao().intSelect(sql)==0) break;
								
								String logisticsProviderCode=DOMHelper.getSubElementVauleByName(header, "carrier");

								String shippingOrderNo=DOMHelper.getSubElementVauleByName(header, "waybill_no");
								Log.info("test---");
								float weight=0.00f;
								if (DOMHelper.ElementIsExists(saleorderstatusele, "containerList")){
									Element containerList = DOMHelper.getSubElementsByName(saleorderstatusele, "containerList")[0];
									NodeList items = containerList.getElementsByTagName("item");
									Log.info("item's length: "+items.getLength());
									for(int i=0;i<items.getLength();i++){
										Element item = (Element)items.item(i);
										if(item.getChildNodes().getLength()==1) continue;
										if(!DOMHelper.ElementIsExists(item, "item")) weight+=Float.parseFloat(DOMHelper.getSubElementVauleByName(item, "weight"));
									}
									if(weight>10000) weight=10000;
								}
								Log.info("weight: "+weight);
								sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
								String owner=this.getDao().strSelect(sql);
								ECSDao dao = (ECSDao)this.getDao();
								dao.setTransation(false);
								
								sql="declare @Err int ; declare @NewSheetID char(16); "
									+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
								String commsheetid=dao.strSelect(sql);
													
								
								
								sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
									+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
									+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
									+"linktele,linkman,delivery,deliverysheetid,zipcode,detailid,weigh)"
									+"select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+owner+"',"
									+"outshopid,inshopid,purday,2209,100,'sf',getdate(),'接口','sf',getdate(),"
									+"notes,address,linktele,linkman,'"+logisticsProviderCode+"','"+shippingOrderNo+"',"
									+"zipcode,detailid,"+weight+" from customerdelive0 "
									+" where sheetid='"+orderCode+"'";
								
								dao.execute(sql);
								getDeliveryDetail(commsheetid,saleorderstatusele,dao);
								IntfUtils.upNote(dao.getConnection(),owner, commsheetid, 2209, interfacesystem, sfUtil.getShopID(dao.getConnection(),company,warehouse));
								dao.commit();
								dao.setTransation(true);
								dao=null;
							}
							else if (status.equalsIgnoreCase("10013") || status.equalsIgnoreCase("10012"))		//取消、关闭
							{
								String orderDetail = getOrderDetail(checkword,company,warehouse,orderCode);
								Document doc = DOMHelper.newDocument(orderDetail, "gbk");
								Element saleorderstatusele = doc.getDocumentElement();
								String result1 = DOMHelper.getSubElementVauleByName(saleorderstatusele, "result");
								if("2".equals(result1)) continue;
								Element header = DOMHelper.getSubElementsByName(saleorderstatusele, "header")[0];
								sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
								if (this.getDao().intSelect(sql)>0) break;
								
								sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
								if (this.getDao().intSelect(sql)>0) break;
								
								sql="select count(*) from customerdelive0 where sheetid='"+orderCode+"'";
								if (this.getDao().intSelect(sql)==0) break;
								
								sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
								String owner=this.getDao().strSelect(sql);
								ECSDao dao = (ECSDao)this.getDao();
								
								dao.setTransation(false);
								
								sql="declare @Err int ; declare @NewSheetID char(16); "
									+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
								String commsheetid=dao.strSelect(sql);
													
								sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
									+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
									+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
									+"linktele,linkman,zipcode,detailid)"
									+"select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+owner+"',"
									+"outshopid,inshopid,purday,2209,97,'sf',getdate(),'接口','sf',getdate(),"
									+"notes,address,linktele,linkman,zipcode,detailid from customerdelive0 "
									+" where sheetid='"+orderCode+"'";
								dao.execute(sql);
								getDeliveryDetail(commsheetid,saleorderstatusele,dao);
								IntfUtils.upNote(dao.getConnection(), owner, commsheetid, 2209,interfacesystem,
										sfUtil.getShopID(dao.getConnection(),company,warehouse));
								
								dao.commit();
								dao.setTransation(true);
								dao=null;
							}
								sfUtil.updateMsg(this.getDao().getConnection(), orderCode, 2209, 1);
							//记录操作日志
							Log.info("best logistics","取发货单状态成功,单号:"+orderCode+" 状态:"+status+"备注: "+remark);
						}
						
					}
					n=10;
				} catch (Exception e) {
					if (++n >= 10)
						continue;
					if (this.getConnection() != null && !this.getConnection().getAutoCommit())
						this.getConnection().rollback();
					
					if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
						this.getExtconnection().rollback();
					Log.warn("远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));				
					
					Thread.sleep(10000L);
					
				}
			}
		}
	}
	//创建发货单明细
	
	private void getDeliveryDetail(String commsheetid,Element salesorderele,DataCentre dao) throws Exception
	{
		Element produectsele=(Element) salesorderele.getElementsByTagName("detailList").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("item");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			if(produectele.getChildNodes().getLength()==1) continue;
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "item"); 
			String normalQuantity=DOMHelper.getSubElementVauleByName(produectele, "quantity");
					
			String sql = new StringBuilder().append("insert into wms_outstockitem0(sheetid,customermid,")
				.append("barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) ")
				.append("select '").append(commsheetid).append("',goodsid,barcodeid,1,0,")
				.append(normalQuantity).append(",").append(normalQuantity)
				.append(",pknum,pkname,pkspec from barcode where ")
				.append("custombc='").append(skuCode).append("'").toString();
				
			
			dao.execute(sql);
		}
	}
	
	//取得订单明细
	private String getOrderDetail(String checkword,String company,String warehouse,String orderid) throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("<wmsSailOrderQueryRequest>")
		  .append("<checkword>").append(checkword).append("</checkword>")
		  .append("<company>").append(company).append("</company>")
		  .append("<warehouse>").append(warehouse).append("</warehouse>")
		  .append("<orderid>").append(orderid).append("</orderid>")
		  .append("</wmsSailOrderQueryRequest>");
		return GetoutsideToLscService.getoutsideToLscServices(sb.toString());
		
	}

	
}
