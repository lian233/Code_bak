package com.wofu.ecommerce.amazon;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.FeedSubmissionInfo;
import com.amazonaws.mws.model.GetFeedSubmissionListRequest;
import com.amazonaws.mws.model.GetFeedSubmissionListResponse;
import com.amazonaws.mws.model.GetFeedSubmissionListResult;
import com.amazonaws.mws.model.GetFeedSubmissionResultRequest;
import com.amazonaws.mws.model.GetFeedSubmissionResultResponse;
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
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class test1 {

	private static String serviceurl = "https://mws.amazonservices.com.cn/?";

	private static String accesskeyid = "AKIAJHLDF7ZG7J5EABZA";

	private static String secretaccesskey = "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";

	private static String applicationname = "javaAmazonclient";

	private static String applicationversion = "1.0";

	private static String sellerid = "A3Q9OT6U783Z5D";

	private static String marketplaceid = "AAHKV2X7AFYLW";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		
	/*	String s="036007TB2	B00B9DURCI	139.00	9\r\n036007TB3	B00B9DUTKI	139.00	14";
		
		Object[] values=StringUtil.split(s, "\r\n").toArray();

		System.out.println(values.length);
		
		for (int i=0;i<values.length;i++)
		{
			System.out.println(values[i]);
		}*/
		
		//getFeedSubmissionResult("1358773877");

		// updateDeliveryStatus();

		// updateCheckStatus();
		
		//updateInvoiceStatus();
		
		//String reportrequestid=getReportRequestID();
		//System.out.println(reportrequestid);
		//String reportrequestid="1358741817";
		//String reportid=getReportID(reportrequestid);
		//String reportid="195600327";
		//System.out.println(reportid);
		//getReport(reportid);
		
		//updateStock("036007TB2",10);
		
		getFeedSubmissionId();
		
		
	}
	
	private static String getReportRequestID() throws Exception 
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
	
	private static String getReportID(String reportRequestID) throws Exception
	{
		String reportid="";
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
		return reportid;
	}

	private static void getReport(String reportid) throws Exception {
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceurl);
		MarketplaceWebService service = new MarketplaceWebServiceClient(
				accesskeyid, secretaccesskey, applicationname,
				applicationversion, config);
		GetReportRequest request = new GetReportRequest()
				.withMerchant(sellerid).withReportId(reportid);
		OutputStream report = new FileOutputStream("d:/report.xml");
		request.setReportOutputStream(report);

		GetReportResponse response = service.getReport(request);
		System.out.println("GetReport Action Response");
		System.out
				.println("=============================================================================");
		System.out.println();

		System.out.print("    GetReportResponse");
		System.out.println();
		System.out.print("    GetReportResult");
		System.out.println();
		System.out.print("            MD5Checksum");
		System.out.println();
		System.out.print("                "
				+ response.getGetReportResult().getMD5Checksum());
		System.out.println();
		if (response.isSetResponseMetadata()) {
			System.out.print("        ResponseMetadata");
			System.out.println();
			ResponseMetadata responseMetadata = response.getResponseMetadata();
			if (responseMetadata.isSetRequestId()) {
				System.out.print("            RequestId");
				System.out.println();
				System.out.print("                "
						+ responseMetadata.getRequestId());
				System.out.println();
			}
		}
		System.out.println();

		System.out.println("Report");
		System.out
				.println("=============================================================================");
		System.out.println();
		// System.out.println( request.getReportOutputStream().toString() );
		System.out.println();

		System.out.println(response.getResponseHeaderMetadata());
		System.out.println();
	}

	private static void getFeedSubmissionResult(String feedSubmissionId)
			throws Exception {
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceurl);

		MarketplaceWebService service = new MarketplaceWebServiceClient(
				accesskeyid, secretaccesskey, applicationname,
				applicationversion, config);

		GetFeedSubmissionResultRequest request = new GetFeedSubmissionResultRequest();
		request.setMerchant(sellerid);
		request.setFeedSubmissionId(feedSubmissionId);
		OutputStream processingResult = new FileOutputStream(
				"d:/feedSubmissionResult.xml");
		request.setFeedSubmissionResultOutputStream(processingResult);

		GetFeedSubmissionResultResponse response = service
				.getFeedSubmissionResult(request);

		System.out.println("GetFeedSubmissionResult Action Response");
		System.out
				.println("=============================================================================");

		System.out.println("    GetFeedSubmissionResultResponse");

		System.out.println("    GetFeedSubmissionResultResult");

		System.out.println("            MD5Checksum");

		System.out.println("                "
				+ response.getGetFeedSubmissionResultResult().getMD5Checksum());

		if (response.isSetResponseMetadata()) {
			System.out.println("        ResponseMetadata");

			ResponseMetadata responseMetadata = response.getResponseMetadata();
			if (responseMetadata.isSetRequestId()) {
				System.out.println("            RequestId");

				System.out.println("                "
						+ responseMetadata.getRequestId());

			}
		}

		System.out.println("Feed Processing Result");
		System.out
				.println("=============================================================================");

		System.out.println(request.getFeedSubmissionResultOutputStream()
				.toString());
		System.out.println(response.getResponseHeaderMetadata());

	}
	
	private static void updateStock(String sku,int qty) throws Exception {



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
		request.setFeedType("_POST_INVENTORY_AVAILABILITY_DATA_"); // 确认发票
		String xml = getInventoryXML(sku, qty);
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

						Log.info(feedSubmissionInfo.getFeedSubmissionId());
					}
					if (feedSubmissionInfo.isSetFeedType()) {
						Log.info(feedSubmissionInfo.getFeedType());

					}
					if (feedSubmissionInfo.isSetSubmittedDate()) {
						Log.info(feedSubmissionInfo.getSubmittedDate());

					}
					if (feedSubmissionInfo.isSetFeedProcessingStatus()) {
						Log.info(feedSubmissionInfo.getFeedProcessingStatus());

					}
					if (feedSubmissionInfo.isSetStartedProcessingDate()) {
						Log.info(Formatter.format(feedSubmissionInfo
								.getStartedProcessingDate(),
								Formatter.DATE_TIME_FORMAT));

					}
					if (feedSubmissionInfo.isSetCompletedProcessingDate()) {
						Log.info(Formatter.format(feedSubmissionInfo
								.getCompletedProcessingDate(),
								Formatter.DATE_TIME_FORMAT));

					}
				}
			}
			if (response.isSetResponseMetadata()) {
				System.out.print("        ResponseMetadata");

				ResponseMetadata responseMetadata = response
						.getResponseMetadata();
				if (responseMetadata.isSetRequestId()) {
					System.out.print("            RequestId");

					System.out.print("                "
							+ responseMetadata.getRequestId());

				}
			}
			System.out.println(response.getResponseHeaderMetadata());

			Log.info("更新库存成功,SKU:" + sku + ",数量:"+qty);
		} catch (MarketplaceWebServiceException e) {
			throw new JException(e.getMessage() + " sku:" + sku
					+ " 更新状态:4");
		}

	}

	private static String getInventoryXML(String sku, int qty) {
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
		requestbuffer.append("<Message>");
		requestbuffer.append("<MessageID>1</MessageID>");
		requestbuffer.append("<OperationType>Update</OperationType>");
		requestbuffer.append("<Inventory>");
		requestbuffer.append("<SKU>"+sku+"</SKU>");
		requestbuffer.append("<Quantity>"+qty+"</Quantity>");
		requestbuffer.append("</Inventory>");
		requestbuffer.append("</Message>");
		requestbuffer.append("</AmazonEnvelope>");

		return requestbuffer.toString();
	}

	private static void updateInvoiceStatus() throws Exception {

		String amazonorderid = "C03-0511489-4208059";


		Vector items = new Vector();
		Hashtable item = new Hashtable();
		item.put("orderitemid", "69799437714502");
		item.put("num", "1");

		items.add(item);

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
		request.setFeedType("_POST_INVOICE_CONFIRMATION_DATA_"); // 确认发票
		String xml = getInvoiceXML(amazonorderid, items);
		FileOutputStream fos = new FileOutputStream("invoicetemp.xml");
		fos.write(xml.getBytes("UTF-8"));
		request.setFeedContent(new FileInputStream("invoicetemp.xml"));

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

						Log.info(feedSubmissionInfo.getFeedSubmissionId());
					}
					if (feedSubmissionInfo.isSetFeedType()) {
						Log.info(feedSubmissionInfo.getFeedType());

					}
					if (feedSubmissionInfo.isSetSubmittedDate()) {
						Log.info(feedSubmissionInfo.getSubmittedDate());

					}
					if (feedSubmissionInfo.isSetFeedProcessingStatus()) {
						Log.info(feedSubmissionInfo.getFeedProcessingStatus());

					}
					if (feedSubmissionInfo.isSetStartedProcessingDate()) {
						Log.info(Formatter.format(feedSubmissionInfo
								.getStartedProcessingDate(),
								Formatter.DATE_TIME_FORMAT));

					}
					if (feedSubmissionInfo.isSetCompletedProcessingDate()) {
						Log.info(Formatter.format(feedSubmissionInfo
								.getCompletedProcessingDate(),
								Formatter.DATE_TIME_FORMAT));

					}
				}
			}
			if (response.isSetResponseMetadata()) {
				System.out.print("        ResponseMetadata");

				ResponseMetadata responseMetadata = response
						.getResponseMetadata();
				if (responseMetadata.isSetRequestId()) {
					System.out.print("            RequestId");

					System.out.print("                "
							+ responseMetadata.getRequestId());

				}
			}
			System.out.println(response.getResponseHeaderMetadata());

			Log.info("更新发票状态成功,单号:" + amazonorderid + "");
		} catch (MarketplaceWebServiceException e) {
			throw new JException(e.getMessage() + " 单号:" + amazonorderid
					+ " 更新状态:4");
		}

	}

	private static String getInvoiceXML(String amazonorderid, Vector items) {
		StringBuffer requestbuffer = new StringBuffer();
		requestbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		requestbuffer
				.append("<AmazonEnvelope xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		requestbuffer.append("<Header>");
		requestbuffer.append("<DocumentVersion>1.01</DocumentVersion>");
		requestbuffer.append("<MerchantIdentifier>" + sellerid
				+ "</MerchantIdentifier>");
		requestbuffer.append("</Header>");
		requestbuffer.append("<MessageType>InvoiceConfirmation</MessageType>");
		requestbuffer.append("<Message>");
		requestbuffer.append("<MessageID>1</MessageID>");
		requestbuffer.append("<OperationType>Update</OperationType>");
		requestbuffer.append("<InvoiceConfirmation>");
		requestbuffer.append("<AmazonOrderID>" + amazonorderid
				+ "</AmazonOrderID>");
		requestbuffer.append("<InvoiceSentDate>"
				+ Formatter.format(new Date(), Formatter.DATE_FORMAT) + "T"
				+ Formatter.format(new Date(), Formatter.TIME_FORMAT)
				+ "+08:00" + "</InvoiceSentDate>");
		for (int i = 0; i < items.size(); i++) {
			Hashtable item = (Hashtable) items.get(i);

			requestbuffer.append("<Item>");
			requestbuffer.append("<AmazonOrderItemCode>"
					+ item.get("orderitemid") + "</AmazonOrderItemCode>");
			requestbuffer.append("<QuantityConfirmed>" + item.get("num")
					+ "</QuantityConfirmed>");
			requestbuffer.append("</Item>");
		}
		requestbuffer.append("</InvoiceConfirmation>");
		requestbuffer.append("</Message>");
		requestbuffer.append("</AmazonEnvelope>");

		return requestbuffer.toString();
	}

	private static void updateCheckStatus() throws Exception {
		String amazonorderid = "C01-3451413-0698435";
		String merchantorderid = "0113040700000242";

		Vector items = new Vector();
		Hashtable item = new Hashtable();
		item.put("orderitemid", "59186950412104");
		item.put("barcodeid", "1000000023340");

		items.add(item);

		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceurl);

		MarketplaceWebService service = new MarketplaceWebServiceClient(
				accesskeyid, secretaccesskey, applicationname,
				applicationversion, config);

		SubmitFeedRequest request = new SubmitFeedRequest();
		request.setMerchant(sellerid);
		final IdList marketplaces = new IdList(Arrays.asList(marketplaceid));

		request.setMarketplaceIdList(marketplaces);
		request.setFeedType("_POST_ORDER_ACKNOWLEDGEMENT_DATA_"); // 订单确认

		String xml = getCheckXML(amazonorderid, merchantorderid, items);
		FileOutputStream fos = new FileOutputStream("checktemp.xml");
		fos.write(xml.getBytes("UTF-8"));
		request.setFeedContent(new FileInputStream("checktemp.xml"));

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

						Log.info(feedSubmissionInfo.getFeedSubmissionId());
					}
					if (feedSubmissionInfo.isSetFeedType()) {
						Log.info(feedSubmissionInfo.getFeedType());

					}
					if (feedSubmissionInfo.isSetSubmittedDate()) {
						Log.info(feedSubmissionInfo.getSubmittedDate());

					}
					if (feedSubmissionInfo.isSetFeedProcessingStatus()) {
						Log.info(feedSubmissionInfo.getFeedProcessingStatus());

					}
					if (feedSubmissionInfo.isSetStartedProcessingDate()) {
						Log.info(Formatter.format(feedSubmissionInfo
								.getStartedProcessingDate(),
								Formatter.DATE_TIME_FORMAT));

					}
					if (feedSubmissionInfo.isSetCompletedProcessingDate()) {
						Log.info(Formatter.format(feedSubmissionInfo
								.getCompletedProcessingDate(),
								Formatter.DATE_TIME_FORMAT));

					}
				}
			}
			if (response.isSetResponseMetadata()) {
				System.out.print("        ResponseMetadata");

				ResponseMetadata responseMetadata = response
						.getResponseMetadata();
				if (responseMetadata.isSetRequestId()) {
					System.out.print("            RequestId");

					System.out.print("                "
							+ responseMetadata.getRequestId());

				}
			}
			System.out.println(response.getResponseHeaderMetadata());

			Log.info("更新审核状态成功,单号:" + amazonorderid + "");
		} catch (MarketplaceWebServiceException e) {
			throw new JException(e.getMessage() + " 单号:" + amazonorderid
					+ " 更新状态:1");
		}

	}

	private static String getCheckXML(String amazonorderid,
			String merchantorderid, Vector items) {
		StringBuffer requestbuffer = new StringBuffer();
		requestbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		requestbuffer
				.append("<AmazonEnvelope xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		requestbuffer.append("<Header>");
		requestbuffer.append("<DocumentVersion>1.01</DocumentVersion>");
		requestbuffer.append("<MerchantIdentifier>" + sellerid
				+ "</MerchantIdentifier>");
		requestbuffer.append("</Header>");
		requestbuffer.append("<MessageType>OrderAcknowledgement</MessageType>");
		requestbuffer.append("<Message>");
		requestbuffer.append("<MessageID>1</MessageID>");
		requestbuffer.append("<OperationType>Update</OperationType>");
		requestbuffer.append("<OrderAcknowledgement>");
		requestbuffer.append("<AmazonOrderID>" + amazonorderid
				+ "</AmazonOrderID>");
		requestbuffer.append("<MerchantOrderID>" + merchantorderid
				+ "</MerchantOrderID>");
		requestbuffer.append("<StatusCode>Success</StatusCode>");
		/*
		 * for (int i = 0; i < items.size(); i++) { Hashtable item = (Hashtable)
		 * items.get(i);
		 * 
		 * requestbuffer.append("<Item>");
		 * requestbuffer.append("<AmazonOrderItemCode>" +
		 * item.get("orderitemid") + "</AmazonOrderItemCode>");
		 * requestbuffer.append("<MerchantOrderItemID>" + item.get("barcodeid")
		 * + "</MerchantOrderItemID>"); requestbuffer.append("</Item>"); }
		 */
		requestbuffer.append("</OrderAcknowledgement>");
		requestbuffer.append("</Message>");
		requestbuffer.append("</AmazonEnvelope>");

		return requestbuffer.toString();
	}

	private static void updateDeliveryStatus() throws Exception {
		String amazonorderid = "C02-9784055-6487068";
		int merchantfulfillmentid = 537;
		String carriername = "申通";
		String shippertrackingnumbe = "568312149887";

		Vector items = new Vector();
		Hashtable item = new Hashtable();
		item.put("orderitemid", "52526862549584");
		item.put("num", "1");

		items.add(item);

		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceurl);

		MarketplaceWebService service = new MarketplaceWebServiceClient(
				accesskeyid, secretaccesskey, applicationname,
				applicationversion, config);

		SubmitFeedRequest request = new SubmitFeedRequest();
		request.setMerchant(sellerid);
		final IdList marketplaces = new IdList(Arrays.asList(marketplaceid));

		request.setMarketplaceIdList(marketplaces);
		request.setFeedType("_POST_ORDER_FULFILLMENT_DATA_"); // 订单发货
		String xml = getDeliveryXML(amazonorderid, merchantfulfillmentid,
				carriername, shippertrackingnumbe, items);
		FileOutputStream fos = new FileOutputStream("delivery.tmp");
		fos.write(xml.getBytes("UTF-8"));
		request.setFeedContent(new FileInputStream("delivery.tmp"));
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

						Log.info(feedSubmissionInfo.getFeedSubmissionId());
					}
					if (feedSubmissionInfo.isSetFeedType()) {
						Log.info(feedSubmissionInfo.getFeedType());

					}
					if (feedSubmissionInfo.isSetSubmittedDate()) {
						Log.info(feedSubmissionInfo.getSubmittedDate());

					}
					if (feedSubmissionInfo.isSetFeedProcessingStatus()) {
						Log.info(feedSubmissionInfo.getFeedProcessingStatus());

					}
					if (feedSubmissionInfo.isSetStartedProcessingDate()) {
						Log.info(Formatter.format(feedSubmissionInfo
								.getStartedProcessingDate(),
								Formatter.DATE_TIME_FORMAT));

					}
					if (feedSubmissionInfo.isSetCompletedProcessingDate()) {
						Log.info(Formatter.format(feedSubmissionInfo
								.getCompletedProcessingDate(),
								Formatter.DATE_TIME_FORMAT));

					}
				}
			}
			if (response.isSetResponseMetadata()) {
				System.out.print("        ResponseMetadata");

				ResponseMetadata responseMetadata = response
						.getResponseMetadata();
				if (responseMetadata.isSetRequestId()) {
					System.out.print("            RequestId");

					System.out.print("                "
							+ responseMetadata.getRequestId());

				}
			}
			System.out.println(response.getResponseHeaderMetadata());

			Log.info("更新发货状态成功,单号:" + amazonorderid + "");
		} catch (MarketplaceWebServiceException e) {
			throw new JException(e.getMessage() + " 单号:" + amazonorderid
					+ " 更新状态:1");
		}
	}

	private static String getDeliveryXML(String amazonorderid,
			int merchantfulfillmentid, String carriername,
			String shippertrackingnumbe, Vector items) {
		StringBuffer requestbuffer = new StringBuffer();

		requestbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		// requestbuffer.append("<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">");
		requestbuffer
				.append("<AmazonEnvelope xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		requestbuffer.append("<Header>");
		requestbuffer.append("<DocumentVersion>1.01</DocumentVersion>");
		requestbuffer.append("<MerchantIdentifier>" + sellerid
				+ "</MerchantIdentifier>");
		requestbuffer.append("</Header>");
		requestbuffer.append("<MessageType>OrderFulfillment</MessageType>");
		requestbuffer.append("<Message>");
		requestbuffer.append("<MessageID>1</MessageID>");
		requestbuffer.append("<OperationType>Update</OperationType>");
		requestbuffer.append("<OrderFulfillment>");
		requestbuffer.append("<AmazonOrderID>" + amazonorderid
				+ "</AmazonOrderID>");
		// requestbuffer.append("<MerchantFulfillmentID>" +
		// merchantfulfillmentid
		// + "</MerchantFulfillmentID>");
		requestbuffer.append("<FulfillmentDate>"
				+ Formatter.format(new Date(), Formatter.DATE_FORMAT) + "T"
				+ Formatter.format(new Date(), Formatter.TIME_FORMAT)
				+ "+08:00" + "</FulfillmentDate>");
		requestbuffer.append("<FulfillmentData>");
		requestbuffer.append("<CarrierName>" + carriername + "</CarrierName>");
		requestbuffer.append("<ShippingMethod>Standard</ShippingMethod>");
		requestbuffer.append("<ShipperTrackingNumber>" + shippertrackingnumbe
				+ "</ShipperTrackingNumber>");
		requestbuffer.append("</FulfillmentData>");
		for (int i = 0; i < items.size(); i++) {
			Hashtable item = (Hashtable) items.get(i);

			requestbuffer.append("<Item>");
			requestbuffer.append("<AmazonOrderItemCode>"
					+ item.get("orderitemid") + "</AmazonOrderItemCode>");
			requestbuffer
					.append("<Quantity>" + item.get("num") + "</Quantity>");
			requestbuffer.append("</Item>");
		}
		requestbuffer.append("</OrderFulfillment>");
		requestbuffer.append("</Message>");
		requestbuffer.append("</AmazonEnvelope>");

		return requestbuffer.toString();
	}
	
	/**
	 * https://mws.amazonservices.com/
  ?AWSAccessKeyId=0PExampleR2
  &Action=GetFeedSubmissionList
  &FeedSubmissionIdList.Id.1=1058369303&FeedSubmissionIdList.Id.2=1228369302
  &FeedTypeList.Type.1=_POST_PRODUCT_DATA_& FeedTypeList.Type.2=
_POST_PRODUCT_PRICING_DATA_
  &FeedProcessingStatusList.Status.1=_DONE_
  &Marketplace=ATExampleER
  &SellerId=A1ExampleE6
  &Signature=BXExampleo%3D
  &SignatureVersion=2
  &SignatureMethod=HmacSHA256
  &Timestamp=2009-02-04T15%3A51%3A49.015Z
  &Version=2009-01-01  
	 * @return
	 */
	private void getFeedSubmissionListString(String AWSAccessKeyId,String Action,String FeedSubmissionIdList
			,String Marketplace,String SellerId,String Signature,String SignatureMethod,String Timestamp){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("AWSAccessKeyId",AWSAccessKeyId);
		map.put("Action",Action);
		map.put("FeedSubmissionIdList",FeedSubmissionIdList);
		map.put("Marketplace",Marketplace);
		map.put("SellerId",SellerId);
		map.put("Signature",Signature);
		map.put("SignatureVersion","2");
		map.put("SignatureMethod",SignatureMethod);
		map.put("Timestamp",Timestamp);
		map.put("Version","2009-01-01");
		
		
		
		
		
	}
	
	private static void getFeedSubmissionId(){
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		config.setServiceURL(serviceurl);
		

		MarketplaceWebService service = new MarketplaceWebServiceClient(
				accesskeyid, secretaccesskey,
				applicationname, applicationversion, config);

		GetFeedSubmissionListRequest request = new GetFeedSubmissionListRequest();
		request.setMerchant(sellerid);
		final IdList marketplaces = new IdList(Arrays
				.asList("2065344417"));
		request.setFeedSubmissionIdList(marketplaces);
		//request.set
		try {
			GetFeedSubmissionListResponse response = service.getFeedSubmissionList(request);
			if (response.isSetGetFeedSubmissionListResult()) {

				GetFeedSubmissionListResult submitFeedResult = response
						.getGetFeedSubmissionListResult();
				if (submitFeedResult.isSetFeedSubmissionInfoList()) {

					List<FeedSubmissionInfo> feedSubmissionInfo = submitFeedResult
							.getFeedSubmissionInfoList();
					if (feedSubmissionInfo.size()!=0) {
						for(int i=0;i<feedSubmissionInfo.size();i++){
							Log.info("更新发货状态请求号:"+feedSubmissionInfo.get(i).getFeedSubmissionId());
							Log.info(feedSubmissionInfo.get(i).getSubmittedDate());
						}
						
					}
					
				}
			}
			System.out.println(response.getResponseHeaderMetadata().getResponseContext());
		} catch (MarketplaceWebServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	

}
