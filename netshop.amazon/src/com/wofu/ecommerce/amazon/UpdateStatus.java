package com.wofu.ecommerce.amazon;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.IdList;
import com.amazonaws.mws.model.SubmitFeedRequest;
import com.amazonaws.mws.model.SubmitFeedResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.SQLHelper;
public class UpdateStatus extends Thread {

	private static String jobname = "亚马逊订单状态更新作业";

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.amazon.Params.dbname);
				Log.info("同步发货单状态开始");
				doUpdateCheckStatus(connection, Params.tradecontactid);
				doUpdateCancelStatus(connection, Params.tradecontactid);
				doUpdateDeliveryStatus(connection, Params.tradecontactid);
				doUpdateInvoiceStatus(connection, Params.tradecontactid);
				Log.info("同步发货单状态结束");

			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {

				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.amazon.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void doUpdateCheckStatus(Connection conn, String tradecontactid)
			throws Exception {
		Vector vts = IntfUtils.getUpNotes(conn, tradecontactid, "1");
		for (int i = 0; i < vts.size(); i++) {
			Hashtable hts = (Hashtable) vts.get(i);
			String sheetid = hts.get("sheetid").toString();

			String sql = "select tid from ns_delivery with(nolock) where sheetid='"
					+ sheetid + "'";
			String amazonorderid = SQLHelper.strSelect(conn, sql);

			sql = "select sheetid from customerorder where refsheetid='"
					+ amazonorderid + "'";

			String merchantorderid = SQLHelper.strSelect(conn, sql);

			sql = "select top 1 sheetid from ns_customerorder where tid='"
					+ amazonorderid + "' order by modified desc";

			String commsheetid = SQLHelper.strSelect(conn, sql);

			sql = "select a.orderitemid,b.barcodeid from ns_orderitem a,barcode b "
					+ "where a.outerskuid=b.custombc and a.sheetid='"
					+ commsheetid + "'";

			Vector items = SQLHelper.multiRowSelect(conn, sql);

			MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
			config.setServiceURL(Params.serviceurl);

			MarketplaceWebService service = new MarketplaceWebServiceClient(
					Params.accesskeyid, Params.secretaccesskey,
					Params.applicationname, Params.applicationversion, config);

			SubmitFeedRequest request = new SubmitFeedRequest();
			request.setMerchant(Params.sellerid);
			final IdList marketplaces = new IdList(Arrays
					.asList(Params.marketplaceid));

			request.setMarketplaceIdList(marketplaces);
			request.setFeedType("_POST_ORDER_ACKNOWLEDGEMENT_DATA_"); // 订单确认
	
			String xml=getCheckXML(
					amazonorderid, merchantorderid, items);
			FileOutputStream fos=new FileOutputStream("checktemp.xml");
			fos.write(xml.getBytes());
			request.setFeedContent(new FileInputStream("checktemp.xml"));
			
			request.setContentMD5(AmazonUtil.computeContentMD5HeaderValue((FileInputStream) request.getFeedContent()));
			try {
				SubmitFeedResponse response = service.submitFeed(request);

				IntfUtils.backupUpNote(conn, "yongjun", sheetid, "1");

				Log.info("更新审核状态成功,单号:" + amazonorderid + "");
			} catch (Exception e) {
				throw new JException(e.getMessage() + " 单号:" + amazonorderid
						+ " 更新状态:1");
			}
		}
	}

	private String getCheckXML(String amazonorderid, String merchantorderid,
			Vector items) throws Exception{
		StringBuffer requestbuffer = new StringBuffer();
		requestbuffer.append("<?xml version=\"1.0\"?>");
		requestbuffer.append("<AmazonEnvelope xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");	
		requestbuffer.append("<Header>");
		requestbuffer.append("<DocumentVersion>1.01</DocumentVersion>");
		requestbuffer.append("<MerchantIdentifier>"+Params.sellerid
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
		for (int i = 0; i < items.size(); i++) {
			Hashtable item = (Hashtable) items.get(i);

			requestbuffer.append("<Item>");
			requestbuffer.append("<AmazonOrderItemCode>"
					+ item.get("orderitemid") + "</AmazonOrderItemCode>");
			requestbuffer.append("<MerchantOrderItemID>"
					+ item.get("barcodeid") + "</MerchantOrderItemID>");
			requestbuffer.append("</Item>");
		}
		*/
		requestbuffer.append("</OrderAcknowledgement>");
		requestbuffer.append("</Message>");
		requestbuffer.append("</AmazonEnvelope>");

		return requestbuffer.toString();
	}

	private void doUpdateCancelStatus(Connection conn, String tradecontactid)
			throws Exception {
		Vector vts = IntfUtils.getUpNotes(conn, tradecontactid, "2");
		for (int i = 0; i < vts.size(); i++) {
			Hashtable hts = (Hashtable) vts.get(i);
			String sheetid = hts.get("sheetid").toString();

			String sql = "select tid from ns_delivery with(nolock) where sheetid='"
					+ sheetid + "'";
			String amazonorderid = SQLHelper.strSelect(conn, sql);

			sql = "select sheetid from customerorder where refsheetid='"
					+ amazonorderid + "'";

			String merchantorderid = SQLHelper.strSelect(conn, sql);

			sql = "select top 1 sheetid from ns_customerorder where tid='"
					+ amazonorderid + "' order by modified desc";

			String commsheetid = SQLHelper.strSelect(conn, sql);

			sql = "select a.orderitemid,b.barcodeid from ns_orderitem a,barcode b "
					+ "where a.outerskuid=b.custombc and a.sheetid='"
					+ commsheetid + "'";

			Vector items = SQLHelper.multiRowSelect(conn, sql);

			MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
			config.setServiceURL(Params.serviceurl);

			MarketplaceWebService service = new MarketplaceWebServiceClient(
					Params.accesskeyid, Params.secretaccesskey,
					Params.applicationname, Params.applicationversion, config);

			SubmitFeedRequest request = new SubmitFeedRequest();
			request.setMerchant(Params.sellerid);
			final IdList marketplaces = new IdList(Arrays
					.asList(Params.marketplaceid));
		
			request.setMarketplaceIdList(marketplaces);
			request.setFeedType("_POST_FULFILLMENT_ORDER_CANCELLATION_REQUEST_DATA_"); // 订单取消			
			String xml=getCheckXML(
					amazonorderid, merchantorderid, items);
			FileOutputStream fos=new FileOutputStream("canceltemp.xml");
			fos.write(xml.getBytes());
			request.setFeedContent(new FileInputStream("canceltemp.xml"));
			request.setContentMD5(AmazonUtil.computeContentMD5HeaderValue((FileInputStream) request.getFeedContent()));
			
			try {
				SubmitFeedResponse response = service.submitFeed(request);

				IntfUtils.backupUpNote(conn, "yongjun", sheetid, "2");

				Log.info("更新取消状态成功,单号:" + amazonorderid + "");
			} catch (Exception e) {
				throw new Exception(e.getMessage() + " 单号:" + amazonorderid
						+ " 更新状态:2");
			}
		}
	}

	private String getCancelXML(String amazonorderid, String merchantorderid,
			Vector items) throws Exception{
		StringBuffer requestbuffer = new StringBuffer();
		requestbuffer.append("<?xml version=\"1.0\"?>");
		requestbuffer.append("<AmazonEnvelope xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");	
		requestbuffer.append("<Header>");
		requestbuffer.append("<DocumentVersion>1.01</DocumentVersion>");
		requestbuffer.append("<MerchantIdentifier>" + Params.username
				+ "</MerchantIdentifier>");
		requestbuffer.append("</Header>");
		requestbuffer.append("<MessageType>OrderAcknowledgement</MessageType>");
		requestbuffer.append("<Message>");
		requestbuffer.append("<MessageID>1</MessageID>");
		requestbuffer.append("<OperationType>Update</OperationType>");		
		requestbuffer.append("<OrderAcknowledgement>");
		requestbuffer.append("<AmazonOrderID>" + amazonorderid
				+ "</AmazonOrderID>");
	//	requestbuffer.append("<MerchantOrderID>" + merchantorderid
		//		+ "</MerchantOrderID>");
		requestbuffer.append("<StatusCode>Failure</StatusCode>");
		for (int i = 0; i < items.size(); i++) {
			Hashtable item = (Hashtable) items.get(i);

			requestbuffer.append("<Item>");
			requestbuffer.append("<AmazonOrderItemCode>"
					+ item.get("orderitemid") + "</AmazonOrderItemCode>");
			requestbuffer.append("<CancelReason>BuyerCanceled</CancelReason>");
			requestbuffer.append("</Item>");
		}
		requestbuffer.append("</OrderAcknowledgement>");
		requestbuffer.append("</Message>");
		requestbuffer.append("</AmazonEnvelope>");

		return requestbuffer.toString();
	}

	private void doUpdateInvoiceStatus(Connection conn, String tradecontactid)
			throws Exception {
		Vector vts = IntfUtils.getUpNotes(conn, tradecontactid, "5");
		for (int i = 0; i < vts.size(); i++) {
			Hashtable hts = (Hashtable) vts.get(i);
			String sheetid = hts.get("sheetid").toString();

			String sql = "select tid from ns_delivery with(nolock) where sheetid='"
					+ sheetid + "'";
			String amazonorderid = SQLHelper.strSelect(conn, sql);

			sql = "select sheetid from customerorder where refsheetid='"
					+ amazonorderid + "'";

			String merchantorderid = SQLHelper.strSelect(conn, sql);

			sql = "select top 1 sheetid from ns_customerorder where tid='"
					+ amazonorderid + "' order by modified desc";

			String commsheetid = SQLHelper.strSelect(conn, sql);

			sql = "select a.orderitemid,b.barcodeid,a.num from ns_orderitem a,barcode b "
					+ "where a.outerskuid=b.custombc and a.sheetid='"
					+ commsheetid + "'";

			Vector items = SQLHelper.multiRowSelect(conn, sql);

			MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
			config.setServiceURL(Params.serviceurl);

			MarketplaceWebService service = new MarketplaceWebServiceClient(
					Params.accesskeyid, Params.secretaccesskey,
					Params.applicationname, Params.applicationversion, config);

			SubmitFeedRequest request = new SubmitFeedRequest();
			request.setMerchant(Params.sellerid);
			final IdList marketplaces = new IdList(Arrays
					.asList(Params.marketplaceid));

			request.setMarketplaceIdList(marketplaces);
			request
					.setFeedType("_POST_INVOICE_CONFIRMATION_DATA_"); // 确认发票
			String xml=getInvoiceXML(
					amazonorderid, merchantorderid, items);
			FileOutputStream fos=new FileOutputStream("invoicetemp.xml");
			fos.write(xml.getBytes());
			request.setFeedContent(new FileInputStream("invoicetemp.xml"));
			
			request.setContentMD5(AmazonUtil.computeContentMD5HeaderValue((FileInputStream) request.getFeedContent()));
			try {
				SubmitFeedResponse response = service.submitFeed(request);

				IntfUtils.backupUpNote(conn, "yongjun", sheetid, "5");

				Log.info("更新发票状态成功,单号:" + amazonorderid + "");
			} catch (Exception e) {
				throw new Exception(e.getMessage() + " 单号:" + amazonorderid
						+ " 更新状态:4");
			}
		}
	}

	private String getInvoiceXML(String amazonorderid, String merchantorderid,
			Vector items) throws Exception{
		StringBuffer requestbuffer = new StringBuffer();
		requestbuffer.append("<?xml version=\"1.0\"?>");
		requestbuffer.append("<AmazonEnvelope xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		requestbuffer.append("<Header>");
		requestbuffer.append("<DocumentVersion>1.01</DocumentVersion>");
		requestbuffer.append("<MerchantIdentifier>" + Params.sellerid
				+ "</MerchantIdentifier>");
		requestbuffer.append("</Header>");
		requestbuffer.append("<MessageType>InvoiceConfirmation</MessageType>");
		requestbuffer.append("<Message>");
		requestbuffer.append("<MessageID>1</MessageID>");
		requestbuffer.append("<OperationType>Update</OperationType>");		
		requestbuffer.append("<InvoiceConfirmation>");
		requestbuffer.append("<AmazonOrderID>" + amazonorderid
				+ "</AmazonOrderID>");
		requestbuffer.append("<InvoiceSentDate>" + Formatter.format(new Date(), Formatter.DATE_FORMAT)+"T"+Formatter.format(new Date(), Formatter.TIME_FORMAT)+"+08:00"
				+ "</InvoiceSentDate>");
		for (int i = 0; i < items.size(); i++) {
			Hashtable item = (Hashtable) items.get(i);

			requestbuffer.append("<Item>");
			requestbuffer.append("<AmazonOrderItemCode>"
					+ item.get("orderitemid") + "</AmazonOrderItemCode>");
			requestbuffer.append("<QuantityConfirmed>"+item.get("num")+"</QuantityConfirmed>");
			requestbuffer.append("</Item>");
		}
		requestbuffer.append("</InvoiceConfirmation>");
		requestbuffer.append("</Message>");
		requestbuffer.append("</AmazonEnvelope>");

		return requestbuffer.toString();
	}
	/**
	 * 更新发货状态
	 * @param conn
	 * @param tradecontactid
	 * @throws Exception
	 */
	private void doUpdateDeliveryStatus(Connection conn, String tradecontactid)
			throws Exception {
		

		Vector vts = IntfUtils.getUpNotes(conn, tradecontactid, "3");
		System.out.println("要处理的更新数量为:　"+vts.size());
		for (int i = 0; i < vts.size(); i++) {
			Hashtable hts = (Hashtable) vts.get(i);
			String sheetid = hts.get("sheetid").toString();
			

			String sql = "select tid,ltrim(rtrim(b.name)) companyname,ltrim(rtrim(outsid)) outsid from ns_delivery  a,"
					+ "deliveryref b where sheetid='"
					+ sheetid
					+ "' and a.companycode=b.companycode";
			Hashtable htd = SQLHelper.oneRowSelect(conn, sql);
			

			String amazonorderid = htd.get("tid").toString();
			String carriername = htd.get("companyname").toString();
			String shippertrackingnumbe = htd.get("outsid").toString();
		
			//int merchantfulfillmentid = Integer.valueOf(sheetid.substring(12))
			//		.intValue();
			

			sql = "select top 1 sheetid from ns_customerorder where tid='"
					+ amazonorderid + "' order by modified desc";

			String commsheetid = SQLHelper.strSelect(conn, sql);

			sql = "select orderitemid,num from ns_orderitem "
					+ "where sheetid='"
					+ commsheetid + "'";

			Vector items = SQLHelper.multiRowSelect(conn, sql);
		
			MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
			config.setServiceURL(Params.serviceurl);
			
			MarketplaceWebService service = new MarketplaceWebServiceClient(
					Params.accesskeyid, Params.secretaccesskey,
					Params.applicationname, Params.applicationversion, config);

			SubmitFeedRequest request = new SubmitFeedRequest();
			request.setMerchant(Params.sellerid);
			final IdList marketplaces = new IdList(Arrays
					.asList(Params.marketplaceid));

			request.setMarketplaceIdList(marketplaces);
			request.setFeedType("_POST_ORDER_FULFILLMENT_DATA_"); // 订单发货
			String xml=getDeliveryXML(
					amazonorderid, carriername,
					shippertrackingnumbe, items);
			FileOutputStream fos=new FileOutputStream("delivery.tmp");
			fos.write(xml.getBytes("UTF-8"));
			request.setFeedContent(new FileInputStream("delivery.tmp"));
			request.setContentMD5(AmazonUtil.computeContentMD5HeaderValue((FileInputStream) request.getFeedContent()));

			try {
				SubmitFeedResponse response = service.submitFeed(request);
				// -------------异常判断
				Log.info("发送更新发货状态返回数据: "+response.toXML());
				String isSuccess = response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedProcessingStatus();
				Log.info("更新发货状态处理结果: "+isSuccess);
				Log.info("发送更新发货状态问题编号:　"+response.getResponseMetadata().getRequestId());
				IntfUtils.backupUpNote(conn, "yongjun", sheetid, "3");
				Thread.sleep(60*1000L);
				Log.info("更新发货状态成功,单号:" + amazonorderid + "");
			} catch (Exception e) {
				throw new Exception(e.getMessage() + " 单号:" + amazonorderid
						+ " 更新状态:3");
			}
		}
	}
	
	

	private String getDeliveryXML(String amazonorderid,
			String carriername,
			String shippertrackingnumbe, Vector items) throws Exception{
		StringBuffer requestbuffer = new StringBuffer();

		requestbuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		//requestbuffer.append("<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">");
		requestbuffer.append("<AmazonEnvelope xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");		
		requestbuffer.append("<Header>");
		requestbuffer.append("<DocumentVersion>1.01</DocumentVersion>");
		requestbuffer.append("<MerchantIdentifier>"+Params.sellerid
				+ "</MerchantIdentifier>");
		requestbuffer.append("</Header>");
		requestbuffer.append("<MessageType>OrderFulfillment</MessageType>");
		requestbuffer.append("<Message>");
		requestbuffer.append("<MessageID>1</MessageID>");
		requestbuffer.append("<OperationType>Update</OperationType>");		
		requestbuffer.append("<OrderFulfillment>");
		requestbuffer.append("<AmazonOrderID>" + amazonorderid
				+ "</AmazonOrderID>");
		//requestbuffer.append("<MerchantFulfillmentID>" + merchantfulfillmentid
		//		+ "</MerchantFulfillmentID>");
		requestbuffer.append("<FulfillmentDate>" + Formatter.format(new Date(), Formatter.DATE_FORMAT)+"T"+Formatter.format(new Date(), Formatter.TIME_FORMAT)+"+08:00"
				+ "</FulfillmentDate>");
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
	


}
