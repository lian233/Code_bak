package com.wofu.ecommerce.amazon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.FeedSubmissionInfo;
import com.amazonaws.mws.model.GetReportListRequest;
import com.amazonaws.mws.model.GetReportListResponse;
import com.amazonaws.mws.model.GetReportListResult;
import com.amazonaws.mws.model.GetReportRequest;
import com.amazonaws.mws.model.GetReportResponse;
import com.amazonaws.mws.model.IdList;
import com.amazonaws.mws.model.ReportInfo;
import com.amazonaws.mws.model.ReportRequestInfo;
import com.amazonaws.mws.model.RequestReportRequest;
import com.amazonaws.mws.model.RequestReportResponse;
import com.amazonaws.mws.model.RequestReportResult;
import com.amazonaws.mws.model.ResponseMetadata;
import com.amazonaws.mws.model.SubmitFeedRequest;
import com.amazonaws.mws.model.SubmitFeedResponse;
import com.amazonaws.mws.model.SubmitFeedResult;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class StockUtils {

	public static String getSkuReportRequestID(String serviceurl,String accesskeyid,
			String secretaccesskey,String applicationname,String applicationversion,
			String sellerid,String marketplaceid) throws Exception 
	{				
		String reportrequestid="";
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceurl);
		MarketplaceWebService service = new MarketplaceWebServiceClient(
                accesskeyid, secretaccesskey, applicationname, applicationversion, config);
		final IdList marketplaces = new IdList(Arrays.asList(
				marketplaceid));
	        
		RequestReportRequest request = new RequestReportRequest()
			        .withMerchant(sellerid)
			        .withReportType("_GET_FLAT_FILE_OPEN_LISTINGS_DATA_")
			        .withMarketplaceIdList(marketplaces);
		RequestReportResponse response = service.requestReport(request);
		if (response.isSetRequestReportResult()) {
			 RequestReportResult  requestReportResult = response.getRequestReportResult();
			 if (requestReportResult.isSetReportRequestInfo()) {
				 ReportRequestInfo  reportRequestInfo = requestReportResult.getReportRequestInfo();
                 if (reportRequestInfo.isSetReportRequestId()) {
                	 
                	 reportrequestid=reportRequestInfo.getReportRequestId();
                 }
			}
		}
		return reportrequestid;
	}
	
	public static String getSkuReportID(String serviceurl,String accesskeyid,
			String secretaccesskey,String applicationname,String applicationversion,
			String sellerid,String marketplaceid,String reportRequestID)
	{
		String reportid="";
		try
		{
			MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
			config.setServiceURL(serviceurl);
			MarketplaceWebService service = new MarketplaceWebServiceClient(
	                accesskeyid, secretaccesskey, applicationname, applicationversion, config);
			final IdList reportquestidlist = new IdList(Arrays.asList(
		        		reportRequestID));
		        
			GetReportListRequest request = new GetReportListRequest()
				        .withMerchant(sellerid)
				        .withReportRequestIdList(reportquestidlist);
			GetReportListResponse response = service.getReportList(request);
			if (response.isSetGetReportListResult()) {
				GetReportListResult  getReportListResult = response.getGetReportListResult();
				 if (getReportListResult.isSetReportInfoList()) {
					 List<ReportInfo>  reportInfoList = getReportListResult.getReportInfoList();
	                 if (reportInfoList.size()>0) {
	                	 
	                	 reportid=reportInfoList.get(0).getReportId();
	                 }
				}
			}
		}catch(Exception e)
		{
			Log.info("报告未生成,请继续等待!");
		}
		return reportid;
	}

	public static InputStream getSkuReport(String serviceurl,String accesskeyid,
			String secretaccesskey,String applicationname,String applicationversion,
			String sellerid,String marketplaceid,String reportid) throws Exception {
		
		FileInputStream fis=null;
			
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceurl);
		MarketplaceWebService service = new MarketplaceWebServiceClient(
				accesskeyid, secretaccesskey, applicationname,
				applicationversion, config);
		GetReportRequest request = new GetReportRequest()
				.withMerchant(sellerid).withReportId(reportid);
		OutputStream report = new FileOutputStream("skuReport.plt");
		request.setReportOutputStream(report);

		GetReportResponse response = service.getReport(request);
		
		fis=new FileInputStream(new File("skuReport.plt"));
		
		return fis;	
	}
	
	public static void updateStock(DataCentre dao,int orgid,String serviceurl,String accesskeyid,
			String secretaccesskey,String applicationname,String applicationversion,
			String sellerid,String marketplaceid,List<Map> inventoryitems) throws Exception {
		
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceurl);

		MarketplaceWebService service = new MarketplaceWebServiceClient(
				accesskeyid, secretaccesskey,
				applicationname, applicationversion, config);

		SubmitFeedRequest request = new SubmitFeedRequest();
		request.setMerchant(sellerid);
		final IdList marketplaces = new IdList(Arrays
				.asList(marketplaceid));

		request.setMarketplaceIdList(marketplaces);
		request.setFeedType("_POST_INVENTORY_AVAILABILITY_DATA_"); // 更新库存
		String xml = getInventoryXML(sellerid,inventoryitems);
		FileOutputStream fos = new FileOutputStream("inventorytmp.xml");
		fos.write(xml.getBytes("UTF-8"));
		request.setFeedContent(new FileInputStream("inventorytmp.xml"));

		request.setContentMD5(AmazonUtil
				.computeContentMD5HeaderValue((FileInputStream) request
						.getFeedContent()));
		try {
			SubmitFeedResponse response = service.submitFeed(request);
			if (response.isSetSubmitFeedResult()) {

				SubmitFeedResult submitFeedResult = response
						.getSubmitFeedResult();
				if (submitFeedResult.isSetFeedSubmissionInfo()) {

					FeedSubmissionInfo feedSubmissionInfo = submitFeedResult
							.getFeedSubmissionInfo();
					if (feedSubmissionInfo.isSetFeedSubmissionId()) {

						Log.info("更新库存请求号:"+feedSubmissionInfo.getFeedSubmissionId());
					}
					
				}
			}
			Log.info("更新库存成功");
			updateStockConfig(dao,orgid,inventoryitems);
			
		} catch (MarketplaceWebServiceException e) {
			Log.info("更新库存失败:错误信息:"+e.getMessage());
			
			updateStockConfig(dao,orgid,inventoryitems,e.getMessage());
		}
	}
	

	private static void updateStockConfig(DataCentre dao,int orgid, List<Map> inventoryitems) throws Exception
	{
		for (int i=0;i<inventoryitems.size();i++)
		{
			Hashtable skuinfo=(Hashtable) inventoryitems.get(i);
			
			String sku=skuinfo.get("sku").toString();
			String qty=skuinfo.get("qty").toString();
			
			String sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount="+qty+" where orgid="+orgid+" and sku='"+sku+"'";
	
			dao.execute(sql);
			
			sql="select stockcount from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"'";
			int orgistockcount=dao.intSelect(sql);
			
			sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount-"+orgistockcount+"+"+qty+" where orgid="+orgid
				+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
			dao.execute(sql);
			
			Log.info("更新库存成功,SKU:"+sku+",新库存:"+qty);
	
		}
		
	}
	
	private static void updateStockConfig(DataCentre dao,int orgid,List<Map> inventoryitems,String errmsg) throws Exception
	{
		for (int i=0;i<inventoryitems.size();i++)
		{
			Hashtable skuinfo=(Hashtable) inventoryitems.get(i);
			
			String sku=skuinfo.get("sku").toString();
			String qty=skuinfo.get("qty").toString();
			
			String sql="update ecs_stockconfigsku set errflag=1,errmsg='"+errmsg+"' where orgid="+orgid+" and sku='"+sku+"'";
	
			dao.execute(sql);
			
			
			sql="update ecs_stockconfig set errflag=1,errmsg='"+errmsg+"' where orgid="+orgid
				+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
			dao.execute(sql);
		}
	}
	
	private static String getInventoryXML(String sellerid,List<Map> inventoryitems) {
		StringBuffer requestbuffer = new StringBuffer();
		requestbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		requestbuffer
				.append("<AmazonEnvelope xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		requestbuffer.append("<Header>");
		requestbuffer.append("<DocumentVersion>1.01</DocumentVersion>");
		requestbuffer.append("<MerchantIdentifier>" + sellerid
				+ "</MerchantIdentifier>");
		requestbuffer.append("</Header>");
		requestbuffer.append("<MessageType>Inventory</MessageType>");
		for(int i=0;i<inventoryitems.size();i++)
		{
			Map skuinfo=(Map) inventoryitems.get(i);
			String sku=skuinfo.get("sku").toString();
			String qty=skuinfo.get("qty").toString();
			
			requestbuffer.append("<Message>");
			requestbuffer.append("<MessageID>1</MessageID>");
			requestbuffer.append("<OperationType>Update</OperationType>");
			requestbuffer.append("<Inventory>");
			requestbuffer.append("<SKU>"+sku+"</SKU>");
			requestbuffer.append("<Quantity>"+qty+"</Quantity>");
			requestbuffer.append("</Inventory>");
			requestbuffer.append("</Message>");
		}
		requestbuffer.append("</AmazonEnvelope>");

		return requestbuffer.toString();
	}
	
	public static String getSkuStockCount(List skulist,String sku)
	{
		String qty="";
		for(int i=0;i<skulist.size();i++)
		{
			Map skuinfo=(Map) skulist.get(i);
			String inventorysku=skuinfo.get("sku").toString();
			String inventoryqty=skuinfo.get("qty").toString();
			
			if (inventorysku.equals(sku)){
				qty=inventoryqty;
				break;
			}
		}
		return qty;
	}
	
	
}
