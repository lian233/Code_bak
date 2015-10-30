package com.wofu.intf.tiantu;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.intf.IntfUtils;
public class CheckOrderExecuter extends Executer {
	private String partnerid="";

	private String partnerkey="";

	private String url="";

	private String callbackurl="";

	private String encoding="";
	private String owner="";

	private String customercode="";
	
	private String warehousecode="";
	
	private String interfacesystem="";
	
	private String serviceversion="";
	
	private String msgtype="";
	
	private String dbname="";
	
	private static String jobName="定时检查天图物流出库单";
	private Boolean isBarcodeId=false;

	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
	
		partnerid=prop.getProperty("partnerid");
		partnerkey=prop.getProperty("partnerkey");
		url=prop.getProperty("url");
		callbackurl=prop.getProperty("callbackurl");
		warehousecode=prop.getProperty("warehousecode");
		interfacesystem=prop.getProperty("interfacesystem");
		serviceversion=prop.getProperty("serviceversion");
		msgtype=prop.getProperty("msgtype");
		encoding=prop.getProperty("encoding");
		dbname=prop.getProperty("dbname");
		customercode=prop.getProperty("customercode");
		isBarcodeId=Boolean.parseBoolean(prop.getProperty("isBarcodeId"));
		
				 
		try
		{
			updateJobFlag(1);
			String sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
			owner=this.getDao().strSelect(sql);
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
	
					String serviceType = "GetSalesOrderStatus";
					StringBuffer bizData = new StringBuffer();
					bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					bizData.append("<"+serviceType+">");
					bizData.append("<customerCode>"+customercode+"</customerCode>");
					bizData.append("<warehouseCode>"+warehousecode+"</warehouseCode>");
					bizData.append("<orderCode>"+sheetid+"</orderCode>");
					bizData.append("</"+serviceType+">");
	
					
					String msgId=TianTuUtil.makeMsgId(this.getDao().getConnection());
					//Log.info("bizData:　"+bizData.toString());
					List signParams=TianTuUtil.makeSignParams(bizData.toString(), serviceType,msgtype,
						partnerid,partnerkey,serviceversion,callbackurl,msgId);
					String sign=TianTuUtil.makeSign(signParams);
	
			
					Map requestParams=TianTuUtil.makeRequestParams(bizData.toString(), serviceType, 
						msgId, msgtype, sign,callbackurl,
						serviceversion,partnerid);
	
					String result=CommHelper.sendRequestT(url, requestParams, "");
					
					String rspBizData=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
					rspBizData = TianTuUtil.filterChar(rspBizData);
					Document outStockStatusDoc = DOMHelper.newDocument(rspBizData, encoding);
	
					Element outStockStatusele = outStockStatusDoc.getDocumentElement();	
					
					
					String flag=DOMHelper.getSubElementVauleByName(outStockStatusele, "flag");
					
					if (flag.equalsIgnoreCase("FAILURE"))
					{
						String errorMsg="";
						Element errorsele=(Element) outStockStatusele.getElementsByTagName("errors").item(0);
						NodeList errorlist=errorsele.getElementsByTagName("error");
						for(int j=0;j<errorlist.getLength();j++)
						{
							Element errorele=(Element) errorlist.item(j);
							String errorcode=DOMHelper.getSubElementVauleByName(errorele, "errorCode");
							String errordesc=DOMHelper.getSubElementVauleByName(errorele, "errorDescription");
							
							errorMsg=errorMsg+"错误代码:"+errorcode+",错误信息:"+errordesc+" ";	
						}
						
						Log.warn("检查出库单","获取出库单信息失败,出库单号:"+sheetid+",错误信息:"+errorMsg);
						
					
					}
					else
					{
						
					
						Element saleorderele=(Element) outStockStatusele.getElementsByTagName("salesOrder").item(0);
						
						String orderCode=DOMHelper.getSubElementVauleByName(saleorderele, "orderCode");
						String orderStatus=DOMHelper.getSubElementVauleByName(saleorderele, "orderStatus");
	
						
						
						if (orderStatus.equalsIgnoreCase("DELIVERED"))		//已发货
						{
							sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=100";
							if (this.getDao().intSelect(sql)>0) break;			
							
							sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=100";
							if (this.getDao().intSelect(sql)>0) break;
							
							sql="select count(*) from customerdelive0 where sheetid='"+orderCode+"'";
							if (this.getDao().intSelect(sql)==0) break;
							
							
							String logisticsProviderCode=DOMHelper.getSubElementVauleByName(saleorderele, "logisticsProviderCode");
							String shippingOrderNo=DOMHelper.getSubElementVauleByName(saleorderele, "shippingOrderNo");
							double weight=Double.valueOf(DOMHelper.getSubElementVauleByName(saleorderele, "weight")).doubleValue()*1000;
							
							this.getDao().setTransation(false);
							
							//sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
							//String owner=this.getDao().strSelect(sql);
											
							sql="declare @Err int ; declare @NewSheetID char(16); "
								+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
							String commsheetid=this.getDao().strSelect(sql);
												
							
							
							sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
								+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
								+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
								+"linktele,linkman,delivery,deliverysheetid,zipcode,detailid,weigh)"
								+"select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+owner+"',"
								+"outshopid,inshopid,purday,2209,100,'best',getdate(),'接口','best',getdate(),"
								+"notes,address,linktele,linkman,'"+logisticsProviderCode+"','"+shippingOrderNo+"',"
								+"zipcode,detailid,"+weight+" from customerdelive0 "
								+" where sheetid='"+orderCode+"'";
							
							this.getDao().execute(sql);
						
						
							getDeliveryDetail(commsheetid,saleorderele,isBarcodeId);
							
						
							IntfUtils.upNote(this.getDao().getConnection(),owner, commsheetid, 2209, interfacesystem, TianTuUtil.getShopID(this.getDao().getConnection(),customercode,warehousecode));
						
							
							this.getDao().commit();
							this.getDao().setTransation(true);
							
						}
						else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//取消、关闭
						{
							sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
							if (this.getDao().intSelect(sql)>0) break;
							
							sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
							if (this.getDao().intSelect(sql)>0) break;
							
							sql="select count(*) from customerdelive0 where sheetid='"+orderCode+"'";
							if (this.getDao().intSelect(sql)==0) break;
							
							this.getDao().setTransation(false);
							
							//sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
							//String owner=this.getDao().strSelect(sql);
							
							
							sql="declare @Err int ; declare @NewSheetID char(16); "
								+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
							String commsheetid=this.getDao().strSelect(sql);
												
							sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
								+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
								+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
								+"linktele,linkman,zipcode,detailid)"
								+"select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+owner+"',"
								+"outshopid,inshopid,purday,2209,97,'best',getdate(),'接口','best',getdate(),"
								+"notes,address,linktele,linkman,zipcode,detailid from customerdelive0 "
								+" where sheetid='"+orderCode+"'";
							this.getDao().execute(sql);
							
							getDeliveryDetail(commsheetid,saleorderele,isBarcodeId);
							
							
							IntfUtils.upNote(this.getDao().getConnection(), owner, commsheetid, 2209,interfacesystem,
									TianTuUtil.getShopID(this.getDao().getConnection(),customercode,warehousecode));
							
							
							this.getDao().commit();
							this.getDao().setTransation(true);
						}
						/**
						if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
							BestUtil.updateMsg(this.getDao().getConnection(), orderCode, 2209, -1);
						else
							BestUtil.updateMsg(this.getDao().getConnection(), orderCode, 2209, 1);
							**/
						
						//记录操作日志
			
						/**
						Element operatorlogsele=(Element) saleorderele.getElementsByTagName("operatorLogs").item(0);
						NodeList operatorlognodelist=operatorlogsele.getElementsByTagName("operatorLog");
						
						for (int i=0;i<operatorlognodelist.getLength();i++)
						{
							Element operatorele=(Element) operatorlognodelist.item(i);
							
							String operator=DOMHelper.getSubElementVauleByName(operatorele, "operator");
							String operatorTime=DOMHelper.getSubElementVauleByName(operatorele, "operatorTime");
							String operatorStatus=DOMHelper.getSubElementVauleByName(operatorele, "operatorStatus");
					
							String orderType="NORMAL";
							
							if (operatorStatus.equalsIgnoreCase("ACCEPTED")) operatorStatus="WMS_ACCEPT";
							if (operatorStatus.equalsIgnoreCase("PRINTED")) operatorStatus="WMS_PRINT";
							if (operatorStatus.equalsIgnoreCase("PICKUPED")) operatorStatus="WMS_PICK";
							if (operatorStatus.equalsIgnoreCase("CHECKED")) operatorStatus="WMS_CHECK";
							if (operatorStatus.equalsIgnoreCase("PACKAGED")) operatorStatus="WMS_PACKAGE";
							if (operatorStatus.equalsIgnoreCase("WEIGHTED")) operatorStatus="WMS_WEIGH";
							
							sql="select count(*) from ecs_bestlogisticsinfo with(nolock) where ordercode='"+orderCode+"' "
								+"and orderstatus='"+operatorStatus+"' and orderType='"+orderType+"'";
							if (this.getDao().intSelect(sql)==0)
							{
								sql="insert into ecs_bestlogisticsinfo(ordercode,orderstatus,ordertype,operator,operatortime) "
									+"values('"+orderCode+"','"+orderStatus+"','"+orderType+"','"+operator+"','"+operatorTime+"')";
								this.getDao().execute(sql);
							}
							
							
						}
						**/
						
						Log.info("best logistics","取发货单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
						
					}
					n=10;
				} catch (Exception e) {
					if (++n >= 10){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						break;
					}
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
	
	private void getDeliveryDetail(String commsheetid,Element salesorderele,Boolean isBarcodeId) throws Exception
	{
		Element produectsele=(Element) salesorderele.getElementsByTagName("products").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("product");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "skuCode"); 
			String normalQuantity=DOMHelper.getSubElementVauleByName(produectele, "normalQuantity");
					
			/* sql="insert into wms_outstockitem0(sheetid,customermid,"
				+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
				+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
				+","+normalQuantity+","+normalQuantity+",pknum,pkname,pkspec "
				+"from barcode where custombc='"+skuCode+"'";*/
			
			String sql = new StringBuilder().append("insert into wms_outstockitem0(sheetid,customermid,")
				.append("barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec)")
				.append(" select '").append(commsheetid)
				.append("',goodsid,barcodeid,1,0,")
				.append(normalQuantity).append(",").append(normalQuantity)
				.append(",pknum,pkname,pkspec from barcode where ")
				.append(isBarcodeId?"barcodeid='":"custombc='").append(skuCode).append("'").toString();
				
			
			this.getDao().execute(sql);
		}
	}

	
}
