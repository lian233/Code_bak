package com.wofu.ecommerce.taobao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Cooperation;
import com.taobao.api.domain.DealerOrder;
import com.taobao.api.domain.DealerOrderDetail;
import com.taobao.api.domain.Distributor;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.PromotionDetail;
import com.taobao.api.domain.PurchaseOrder;
import com.taobao.api.domain.Refund;
import com.taobao.api.domain.RefundDetail;
import com.taobao.api.domain.SubPurchaseOrder;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.FenxiaoCooperationGetRequest;
import com.taobao.api.request.FenxiaoDistributorsGetRequest;
import com.taobao.api.request.FenxiaoRefundGetRequest;
import com.taobao.api.request.RefundGetRequest;
import com.taobao.api.request.TradeFullinfoGetRequest;
import com.taobao.api.response.FenxiaoCooperationGetResponse;
import com.taobao.api.response.FenxiaoDistributorsGetResponse;
import com.taobao.api.response.FenxiaoRefundGetResponse;
import com.taobao.api.response.RefundGetResponse;
import com.taobao.api.response.TradeFullinfoGetResponse;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.Types;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {

	private static String TradeFields = "seller_nick,buyer_nick,title,type,created,tid,"
			+ "seller_rate,buyer_flag,buyer_rate,status,payment,"
			+ "adjust_fee,post_fee,total_fee,pay_time,end_time,modified,"
			+ "consign_time,buyer_obtain_point_fee,point_fee,real_point_fee,"
			+ "received_payment,commission_fee,buyer_memo,seller_memo,"
			+ "alipay_no,buyer_message,pic_path,num_iid,num,price,buyer_alipay_no,"
			+ "receiver_name,receiver_state,receiver_city,receiver_district,"
			+ "receiver_address,receiver_zip,receiver_mobile,receiver_phone,is_brand_sale,"
			+ "buyer_email,seller_flag,seller_alipay_no,seller_mobile,trade_from,"
			+ "seller_phone,seller_name,seller_email,available_confirm_fee,alipay_url,"
			+ "has_post_fee,timeout_action_time,Snapshot,snapshot_url,cod_fee,cod_status,"
			+ "shipping_type,trade_memo,is_3D,buyer_email,buyer_memo,buyer_flag,promotion,promotion_details,orders";

	private static String RefundFields = "refund_id, alipay_no, tid, oid, buyer_nick, seller_nick,"
			+ "total_fee,order_status,iid, status, created,modified, refund_fee,"
			+ "good_status, has_good_return, payment, reason, desc, num_iid, "
			+ "title, price, num, good_return_time, company_name, sid, address,"
			+ "shipping_type, refund_remind_timeout";
	
	
	public  static void  updateFinishedStatus(Connection conn,String tradecontactid,long tid,Date endtime) throws Exception
	{
		String sql="update customerorder0 set endtime='"+Formatter.format(endtime, Formatter.DATE_TIME_FORMAT)
			+"' where refsheetid='"+tid+"' and tradecontactid="+tradecontactid;
		int rowcnt=SQLHelper.executeSQL(conn, sql);
		
		if (rowcnt==0)
		{
			sql="update customerorder set endtime='"+Formatter.format(endtime, Formatter.DATE_TIME_FORMAT)
				+"' where refsheetid='"+tid+"' and tradecontactid="+tradecontactid;
			rowcnt=SQLHelper.executeSQL(conn, sql);
			
			if (rowcnt==0)
			{
				sql="update customerorder0 set endtime='"+Formatter.format(endtime, Formatter.DATE_TIME_FORMAT)
					+"' where sheetid in(select sheetid from customerorderreflist where refsheetid='"+tid+"') and tradecontactid="+tradecontactid;
				rowcnt=SQLHelper.executeSQL(conn, sql);
				if (rowcnt==0)
				{
					sql="update customerorder set endtime='"+Formatter.format(endtime, Formatter.DATE_TIME_FORMAT)
						+"' where sheetid in(select sheetid from customerorderreflist where refsheetid='"+tid+"') and tradecontactid="+tradecontactid;
					rowcnt=SQLHelper.executeSQL(conn, sql);
				}
			}
		}
	}

	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Trade t, String tradecontactid,String username,boolean isFormal) throws Exception {
		try {

			String sheetid = "";

	
			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");
			
			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid
					+ "',1 , '"
					+ tradecontactid
					+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			int haspostFee = 0;
			if (isFormal && t.getHasPostFee()) {
				haspostFee = 1;
			}
			String promotionDetails = "";

			for (int i = 0; i < t.getPromotionDetails().size(); i++) {
				promotionDetails = promotionDetails
						+ t.getPromotionDetails().get(i).getPromotionDesc()
						+ ";";
			}


			int brandsaleflag = 0;

			if (t.getIsBrandSale())
				brandsaleflag = 1;
			
			int buyerflag=0;
			int sellerflag=0;
			boolean buyerrate=false;
			boolean sellerrate=false;
			
			String sellermemo="";
			String buyermemo="";
			String tradememo="";
			String buyermessage="";
			double payment=0.00;
			double adjustfee=0.00;
			double receivedpayment=0.00;
			String promotion="";
			
			Date paytime=t.getCreated();
			
			String alipayurl="";
			
			if (t.getBuyerMemo()!=null)
				buyermemo=t.getBuyerMemo();
			if (t.getBuyerMessage()!=null)
				buyermessage=t.getBuyerMessage();
			
			if (isFormal)
			{
				if (t.getPayTime()!=null)
					paytime=t.getPayTime();
				
				if (t.getSellerMemo()!=null)
					sellermemo=t.getSellerMemo();
				
				if (t.getTradeMemo()!=null)
					tradememo=t.getTradeMemo();
				
				if (t.getPayment()!=null)
					payment=Double.valueOf(t.getPayment()).doubleValue();
				if (t.getAdjustFee()!=null)
					adjustfee=Double.valueOf(t.getAdjustFee()).doubleValue();
				if (t.getReceivedPayment()!=null)
					receivedpayment=Double.valueOf(t.getReceivedPayment()).doubleValue();
				if (t.getBuyerFlag()!=null)
					buyerflag=t.getBuyerFlag().intValue();
				if (t.getSellerFlag()!=null)
					sellerflag=t.getSellerFlag().intValue();
				if (t.getBuyerRate()!=null)
					buyerrate=t.getBuyerRate();
				if (t.getSellerRate()!=null)
					sellerrate=t.getSellerRate();
				
				if (t.getPromotion()!=null)
					promotion=t.getPromotion();
				if (t.getAlipayUrl()!=null)
					alipayurl=t.getAlipayUrl();
			}
			String receiverdistrict="";
			if (t.getReceiverDistrict()!=null)				
				receiverdistrict=t.getReceiverDistrict();
			
			String buyeremail="";
			if (t.getBuyerEmail()!=null)
				buyeremail=t.getBuyerEmail();
			
			Date consigntime=new Date();
			if (t.getConsignTime()!=null)
				consigntime=t.getConsignTime();
			Date endtime=new Date();
			if (t.getEndTime()!=null)
				endtime=t.getEndTime();
			
			String zipcode="";			
			if (t.getReceiverZip()!=null)
				zipcode=t.getReceiverZip();
			
			String address="";
			if (t.getReceiverAddress()!=null)
				address=StringUtil.replace(t.getReceiverAddress(),"'","");
			
			String shoppingtype="";
			if (t.getShippingType()!=null)
				shoppingtype=t.getShippingType();
			
			String state="";
			if (t.getReceiverState()!=null)
				state=t.getReceiverState();
			
			String city="";
			if (t.getReceiverCity()!=null)
				city=t.getReceiverCity();
			
			String mobile="";
			if (t.getReceiverMobile()!=null)
				mobile=t.getReceiverMobile();
			
			String phone="";
			if (t.getReceiverPhone()!=null)
				phone=t.getReceiverPhone();
			
			String linkman="";
			if (t.getReceiverName()!=null)
				linkman=StringUtil.replace(t.getReceiverName(),"'","");
			
			String buyernick="";
			if (t.getBuyerNick()!=null)
				buyernick=StringUtil.replace(t.getBuyerNick(),"'","");
			
			String tradefrom="";
			if (t.getTradeFrom()!=null)
				tradefrom=t.getTradeFrom();
			
			String buyeralipayno="";
			if (t.getBuyerAlipayNo()!=null)
				buyeralipayno=t.getBuyerAlipayNo();
			
			String sellernick="";
			if (t.getSellerNick()!=null)
				sellernick=t.getSellerNick();
			
			String type="";
			if (t.getType()!=null)
				type=t.getType();

			
			Date modified=new Date();
			if (t.getModified()!=null)
				modified=t.getModified();
			
			Date created=new Date();
			if (t.getCreated()!=null)
				created=t.getCreated();
			
			String totalfee="0.00";
			if (t.getTotalFee()!=null)
				totalfee=t.getTotalFee();
			
			String postfee="0.00";
			if (t.getPostFee()!=null)
				postfee=t.getPostFee();
			
			String status="";
			if (t.getStatus()!=null)
				status=t.getStatus();
			
			long realpointfee=0L;			
			if (t.getRealPointFee()!=null)
				realpointfee=t.getRealPointFee();
			long pointfee=0L;			
			if (t.getPointFee()!=null)
				pointfee=t.getPointFee();
			long buyerobtainpointfee=0L;
			if (t.getBuyerObtainPointFee()!=null)
				buyerobtainpointfee=t.getBuyerObtainPointFee();
			sql="select isnull(value,0) from config where name='淘宝卖家备注红旗是否去掉第一个字符'";
			int isremovefirst=0;
			String isremovefirstTemp=SQLHelper.strSelect(conn, sql);
			if(isremovefirstTemp!=null && !"".equals(isremovefirstTemp))
			isremovefirst=Integer.valueOf(isremovefirstTemp).intValue();
			
			if (isremovefirst==1 && t.getSellerFlag()==1L && !sellermemo.equals(""))
			{
				if (sellermemo.substring(0, 1).matches("[A-Za-z]"))
					sellermemo=sellermemo.substring(1);
			}

			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , "
					+ " type , created , buyermessage , shippingtype , payment , "
					+ " discountfee , adjustfee , status , buyermemo , sellermemo , "
					+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
					+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
					+ " buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , "
					+ " buyeremail , haspostFee , receivedpayment , "
					+ " alipayNo , buyerflag , sellerflag,brandsaleflag,"
					+ " sellerrate , buyerrate , promotion , tradefrom , alipayurl , "
					+ " PromotionDetails,tradeContactid) "
					// +"
					// values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					+ " values("
					/*
					 * Object[]
					 * sqlv={sheetid,sheetid,"yongjun",String.valueOf(tid),t.getSellerNick(),
					 * t.getType(),t.getCreated(),t.getBuyerMessage(),t.getShippingType(),Double.valueOf(t.getPayment()),
					 * Double.valueOf(t.getDiscountFee()),Double.valueOf(t.getAdjustFee()),t.getStatus(),t.getBuyerMemo(),
					 * t.getSellerMemo(),t.getTradeMemo(),t.getPayTime(),t.getEndTime(),t.getModified(),t.getBuyerObtainPointFee(),
					 * t.getPointFee(),t.getRealPointFee(),t.getTotalFee(),t.getPostFee(),t.getBuyerAlipayNo(),t.getBuyerNick(),
					 * t.getReceiverName(),t.getReceiverState(),t.getReceiverCity(),t.getReceiverDistrict(),t.getReceiverAddress(),
					 * t.getReceiverZip(),t.getReceiverMobile(),
					 * t.getReceiverPhone(),t.getConsignTime(),t.getBuyerEmail(),
					 * 0.00,t.getAvailableConfirmFee(),haspostFee,t.getReceivedPayment(),t.getCodFee(),t.getCodStatus(),
					 * t.getTimeoutActionTime(),t.getAlipayNo(),t.getBuyerFlag(),
					 * t.getSellerFlag(),brandsaleflag, t.getPrice(),
					 * t.getNum(),t.getTitle(),t.getSnapshotUrl(),t.getSnapshot(),Types.convertBooleanToShort(t.getSellerRate()),
					 * Types.convertBooleanToShort(t.getBuyerRate())
					 * ,t.getNumIid(),t.getPromotion(),t.getTradeFrom()
					 * ,t.getAlipayUrl(), promotionDetails,tradecontactid};
					 */
					+ "'"
					+ sheetid
					+ "','"
					+ sheetid
					+ "','"+username+"','"
					+ t.getTid()
					+ "','"
					+ sellernick
					+ "', "
					+ "'"
					+ type
					+ "' ,'"
					+ Formatter.format(created,
							Formatter.DATE_TIME_FORMAT)
					+ "','"
					+ buyermessage
					+ "','"
					+ shoppingtype
					+ "','"
					+ payment
					+ "', "
					+ "0.00"
					+ ", '"
					+ adjustfee
					+ "' , '"
					+status
					+ "' , '"
					+ buyermemo
					+ "' ,'"
					+ sellermemo
					+ "' , "
					+ "'"
					+ tradememo
					+ "' , '"
					+ Formatter.format(paytime,
							Formatter.DATE_TIME_FORMAT)
					+ "' , '"
					+ Formatter.format(endtime,
							Formatter.DATE_TIME_FORMAT)
					+ "', '"
					+ Formatter.format(modified,
							Formatter.DATE_TIME_FORMAT)
					+ "' , "
					+ buyerobtainpointfee
					+ " , "
					+ pointfee
					+ " , "
					+ realpointfee
					+ " , '"
					+ totalfee
					+ "' , '"
					+ postfee
					+ "','"
					+ buyeralipayno
					+ "',"
					+ "'"
					+ buyernick.replaceAll("'", "")
					+ "' ,'"
					+ linkman.replaceAll("'", "")
					+ "' , '"
					+ state
					+ "', '"
					+ city
					+ "' , '"
					+ receiverdistrict
					+ "', "
					+ "'"
					+ address.replaceAll("'", " ")
					+ "','"
					+ zipcode
					+ "' , '"
					+ mobile
					+ "' , '"
					+ phone
					+ "' , '"
					+ Formatter.format(consigntime,
							Formatter.DATE_TIME_FORMAT) + "' , " + "'"
					+ buyeremail + "' , " + String.valueOf(haspostFee)
					+ ",'" + receivedpayment + "', " + "'"
					+ t.getAlipayNo() + "' , " + buyerflag + ","
					+ sellerflag + " , " + brandsaleflag + ", " + "'"
					+ Types.convertBooleanToShort(sellerrate) + "' , '"
					+ Types.convertBooleanToShort(buyerrate) + "' , "
					+ "'" +promotion + "', '" + tradefrom
					+ "', '" + alipayurl + "','" + promotionDetails
					+ "'," + tradecontactid + ")";
			

			// SQLHelper.executePreparedSQL(conn,sql,sqlv);
			SQLHelper.executeSQL(conn, sql);
			
			for (Iterator itorder = t.getOrders().iterator(); itorder.hasNext();) {
				Order o = (Order) itorder.next();

				if (o.getTitle().indexOf("秒杀") >= 0
						&& o.getTitle().indexOf("随机") >= 0) {
					sql = "select max(itemid) from ecs_seckill a,ecs_seckillitem b "
							+ "where a.killid=b.killid and a.sellcode='"
							+ o.getOuterIid() + "'";
					int maxitemid = SQLHelper.intSelect(conn, sql);
					double a = Math.random() * maxitemid;
					a = Math.ceil(a);
					int randomitemid = new Double(a).intValue();

					sql = "select b.sku from ecs_seckill a,ecs_seckillitem b "
							+ "where a.killid=b.killid and a.sellcode='"
							+ o.getOuterIid() + "' and b.itemid="
							+ randomitemid;

					String randomsku = SQLHelper.strSelect(conn, sql);

					o.setOuterSkuId(randomsku);
				}
				boolean isoversold=false;
				int cid=0;
				String skuid="";
				String skuprop="";
				String outerskuid="";
				String snapshot="";
				long refundid=0L;
				outerskuid=o.getOuterSkuId();
				if (isFormal)
				{
					isoversold=o.getIsOversold();
					cid=o.getCid().intValue();
					skuid=o.getSkuId();
					skuprop=o.getSkuPropertiesName();
					snapshot=o.getSnapshot();
					refundid=o.getRefundId();
				}


				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , "
						+ " title , sellernick , buyernick , type , created , "
						+ " refundstatus , outeriid , outerskuid , totalfee , payment , "
						+ " discountfee , adjustfee , status  ,"
						+ " skuPropertiesName , num , price , picPath , "
						+ " oid , snapShotUrl , snapShot , buyerRate ,sellerRate ,refundId,"
						+ "  numiid , cid , isoversold) values( "
						+ "'"
						+ sheetid
						+ "','"
						+ sheetid
						+ "_"
						+ o.getOid()
						+ "','"
						+ sheetid
						+ "','"
						+ skuid
						+ "' , '"
						+ o.getItemMealName()
						+ "', "
						+ "'"
						+ o.getTitle()
						+ "' , '"
						+ o.getSellerNick()
						+ "', '"
						+ o.getBuyerNick()
						+ "' , '"
						+ o.getSellerType()
						+ "','"
						+ Formatter.format(t.getCreated(),
								Formatter.DATE_TIME_FORMAT)
						+ "', "
						+ "'"
						+ o.getRefundStatus()
						+ "' , '"
						+ o.getOuterIid()
						+ "' , '"
						+ outerskuid
						+ "' , '"
						+ o.getTotalFee()
						+ "' , '"
						+ o.getPayment()
						+ "' , "
						+ "'"
						+ o.getDiscountFee()
						+ "', '"
						+ o.getAdjustFee()
						+ "' , '"
						+ o.getStatus()
						+ "' , "
						+ " '"
						+ skuprop
						+ "' ,"
						+ o.getNum()
						+ " , '"
						+ o.getPrice()
						+ "' , '"
						+ o.getPicPath()
						+ "' , "
						+ "'"
						+ o.getOid()
						+ "' , '"
						+ o.getSnapshotUrl()
						+ "' , '"
						+ snapshot
						+ "' , '"
						+ Types.convertBooleanToShort(buyerrate)
						+ "' , '"
						+ Types.convertBooleanToShort(sellerrate)
						+ "', "
						+ refundid
						+ ","
						+ o.getNumIid()
						+ ","
						+ cid
						+ ","
						+ Types.convertBooleanToShort(isoversold) + ")";
				SQLHelper.executeSQL(conn, sql);

			
			}
		
			conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成订单【" + t.getTid() + "】接口数据成功，接口单号【"
					+ sheetid + "】");

			return sheetid;

		} catch (JSQLException e1) {
			e1.printStackTrace();
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
				e3.printStackTrace();
			}
			throw new JException("生成订单【" + t.getTid() + "】接口数据失败!"
					+ e1.getMessage());
		}
	}
	/**
	 * 获取分销商信息
	 * taobao.fenxiao.distributors.get  api收费
	 * @param url
	 * @param appkey
	 * @param appsecret
	 * @param token
	 * @param nick
	 * @return
	 * @throws Exception
	 */
	public static  String getDistributorShopName(String url,String appkey,String appsecret,String token,String nick) throws Exception
	{
		String shopname="";
		
		TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
		FenxiaoDistributorsGetRequest req=new FenxiaoDistributorsGetRequest();
		req.setNicks(nick);
		FenxiaoDistributorsGetResponse response = client.execute(req ,token);

		if (DOMHelper.ElementIsExists(DOMHelper.newDocument(response.getBody(), "GBK").getDocumentElement(), "distributors"))
		{
			String shopurl=response.getDistributors().get(0).getShopWebLink();
				
			String html=getOneHtml(shopurl);
			
			if (html.equals("")) return "";
				
			String title= getTitle(html);

			shopname=title;
	
				
		}
		return shopname;
		
	}

	private static  String getTitle(String s) {
		String regex;
		String title = "";
		final List<String> list = new ArrayList<String>();
		regex = "<title>.*?</title>";
		final Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
		final Matcher ma = pa.matcher(s);
		while (ma.find()) {
			list.add(ma.group());
		}
		for (int i = 0; i < list.size(); i++) {
			title = title + list.get(i);
		}
		return outTag(title);
	}

	private static String outTag(final String s) {
		return s.replaceAll("<.*?>", "");
	}

	private static String getOneHtml(String htmlurl) throws IOException {

		if (htmlurl.toLowerCase().indexOf("http")<0)
			htmlurl="http://"+htmlurl;
		

		URL url;
		String temp;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(htmlurl);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "GBK"));// 读取网页全部内容
			while ((temp = in.readLine()) != null) {
				sb.append(temp);
			}
			in.close();
		} catch (MalformedURLException me) {
			System.out.println("你输入的URL格式有问题！请仔细输入");
			me.getMessage();
			throw me;
		}catch(UnknownHostException une)
		{
			System.out.println(une.getMessage());
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		} 
		return sb.toString();
	}
	
	public static void getDistributorByNick(Connection conn,String tradecontactid,String nick) throws Exception
	{

		String sql="select url,appkey,appsecret,token from ecs_org_params a,ecs_tradecontactorgcontrast b where a.orgid=b.orgid and b.tradecontactid="+tradecontactid;
		Hashtable htparams=SQLHelper.oneRowSelect(conn, sql);
		
		String url=htparams.get("url").toString();
		String appkey=htparams.get("appkey").toString();
		String appsecret=htparams.get("appsecret").toString();
		String token=htparams.get("token").toString();
		
		TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
		FenxiaoDistributorsGetRequest req=new FenxiaoDistributorsGetRequest();		
		req.setNicks(nick);
		FenxiaoDistributorsGetResponse response = client.execute(req , token);
		Log.info(response.toString());
		Log.info(response.getBody());
		for(Iterator it=response.getDistributors().iterator();it.hasNext();)
		{
			Distributor distribtor=(Distributor) it.next();
			Log.info(distribtor.getDistributorId()+" "+distribtor.getDistributorName()+" "+Formatter.format(distribtor.getStarts(),Formatter.DATE_TIME_FORMAT));
			
			String shopname=getDistributorShopName(url,appkey,appsecret,token,nick);
			
			shopname=StringUtil.replace(shopname, "'"," ");
			
			
			sql="select count(*) from ecs_distributor with(nolock) where distributorid="+distribtor.getDistributorId();
			if (SQLHelper.intSelect(conn, sql)==0)
			{
				sql="insert into ecs_distributor(distributorid,distributorname,startdate,shopname,manager,creator,operator,updator) "
					+"values("+distribtor.getDistributorId()+",'"+distribtor.getDistributorName()+"','"
					+Formatter.format(distribtor.getStarts(),Formatter.DATE_TIME_FORMAT)+"','"+shopname+"','','system','system','system')";
				SQLHelper.executeSQL(conn, sql);
			}
			else
			{
				sql="update ecs_distributor set distributorname='"+distribtor.getDistributorName()+"',startdate='"+
					Formatter.format(distribtor.getStarts(),Formatter.DATE_TIME_FORMAT)+"',"
					+"updatetime='"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)+"', "
					+"shopname='"+shopname+"' "
					+"where distributorid="+distribtor.getDistributorId();
				SQLHelper.executeSQL(conn, sql);
			}
			
		}
	}

	public static String createDistributionOrder(String modulename,
			Connection conn, PurchaseOrder po,  String tradecontactid)
			throws Exception {
		String sql = "select count(*) from ecs_distributor with(nolock) "
				+ "where distributorname='"
				+ po.getDistributorUsername().trim() + "' and shopname<>''";

		if (SQLHelper.intSelect(conn, sql) == 0) {
			Log.info("不存在分销商:" + po.getDistributorUsername().trim()
					+ ",或者分销商店铺名称为空!");
			getDistributorByNick(conn,tradecontactid,po.getDistributorUsername().trim());
		}

		try {

			String sheetid = "";

			conn.setAutoCommit(false);

			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			System.out.println("sheetid: "+sheetid);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid
					+ "',1 , '"
					+ tradecontactid
					+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			int haspostFee = 0;

			String promotionDetails = "";
			
			
			sql = "select distributorid,shopname from ecs_distributor with(nolock) where distributorname='"
					+ po.getDistributorUsername().trim() + "'";
			Hashtable distributorinfo = SQLHelper.oneRowSelect(conn, sql);

			String distributorid = distributorinfo.get("distributorid")
					.toString();
			String distributorshopname = distributorinfo.get("shopname")
					.toString();
			

			String buyermemo = "";
			if (!po.getMemo().equals(""))
				buyermemo = po.getMemo().substring(
						po.getMemo().indexOf(" :") + 2, po.getMemo().length());
			
			

			String phone=po.getReceiver().getPhone();
			if (phone==null) phone="";

			sql = "insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , "
					+ " type , created , buyermessage , shippingtype , payment , "
					+ " discountfee , adjustfee , status ,  "
					+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
					+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
					+ " buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone  , "
					+ " buyeremail , commissionfee , availableconfirmfee , haspostFee , receivedpayment , "
					+ " buyermemo,sellermemo, "
					+ " snapshoturl, tradefrom ,PromotionDetails,"
					+ " tradeContactid,distributorid,distributetid,distributorshopname) values("
					+ "'"
					+ sheetid
					+ "','"
					+ sheetid
					+ "','yongjun','"
					+ po.getId()
					+ "','', '"
					+ po.getSupplierUsername()
					+ "', "
					+ "'fixed' ,'"
					+ Formatter.format(po.getCreated(),
							Formatter.DATE_TIME_FORMAT)
					+ "','','"
					+ po.getShipping()
					+ "','"
					+ po.getDistributorPayment()
					+ "', "
					+ "'"
					+ String.valueOf(Double.valueOf(po.getTotalFee())
							.doubleValue()
							- Double.valueOf(po.getDistributorPayment())
									.doubleValue())
					+ "', '0.00' , '"
					+ po.getStatus()
					+ "' , "
					+ "'' , '"
					+ Formatter.format(po.getPayTime(),
							Formatter.DATE_TIME_FORMAT)
					+ "' , '"
					+ Formatter.format(po.getModified(),
							Formatter.DATE_TIME_FORMAT)
					+ "', '"
					+ Formatter.format(po.getModified(),
							Formatter.DATE_TIME_FORMAT)
					+ "' , 0 , 0, 0,"
					+ po.getTotalFee()
					+ " , '"
					+ po.getPostFee()
					+ "','"
					+ po.getAlipayNo()
					+ "',"
					+ "'"
					+ po.getReceiver().getName().replaceAll("'", "")
					+ "' ,'"
					+ po.getReceiver().getName().replaceAll("'", "")
					+ "' , '"
					+ po.getReceiver().getState()
					+ "', '"
					+ po.getReceiver().getCity()
					+ "' , '"
					+ po.getReceiver().getDistrict()
					+ "', "
					+ "'"
					+ po.getReceiver().getAddress().replaceAll("'", " ")
					+ "','"
					+ po.getReceiver().getZip()
					+ "' , '"
					+ po.getReceiver().getMobilePhone()
					+ "' , '"
					+ phone
					+ "' ,  "
					+ "'' , '0.00' , '0.00' , "
					+ String.valueOf(haspostFee)
					+ ",'"
					+ po.getDistributorPayment()
					+ "', "
					+ "  '"
					+ buyermemo.replaceAll("'", "")
					+ "', '"
					+ po.getSupplierMemo()
					+ "' ,'"
					+ po.getSnapshotUrl()
					+ "', '"
					+ po.getDistributorUsername()
					+ "', '"
					+ promotionDetails
					+ "',"
					+ tradecontactid
					+ ","
					+ distributorid
					+ ",'"
					+ po.getTcOrderId()
					+ "','"
					+ distributorshopname + "')";

			SQLHelper.executeSQL(conn, sql);

		
			
			for (Iterator itorder = po.getSubPurchaseOrders().iterator(); itorder
					.hasNext();) {
				SubPurchaseOrder o = (SubPurchaseOrder) itorder.next();
				String refundstatus = "";
				if (o.getStatus().equalsIgnoreCase("RADE_REFUNDING")
						|| o.getStatus().equalsIgnoreCase("TRADE_REFUNDED"))
					refundstatus = o.getStatus();
				else
					refundstatus = "NO_REFUND";

				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid  , "
						+ " title , sellernick , buyernick , created , "
						+ " outeriid , outerskuid , totalfee , payment , "
						+ " status  , owner , "
						+ " iid , skuPropertiesName , num , price , picPath , "
						+ " oid , snapShotUrl,refundstatus,"
						+ "  numiid , distributePrice) values( "
						+ "'"
						+ sheetid
						+ "','"
						+ sheetid
						+ "_"
						+ o.getFenxiaoId()
						+ "','"
						+ sheetid
						+ "','"
						+ o.getSkuId()
						+ "' , "
						+ "'"
						+ o.getTitle()
						+ "' , '"
						+ po.getSupplierUsername()
						+ "', '"
						+ po.getDistributorUsername()
						+ "' , '"
						+ Formatter.format(o.getCreated(),
								Formatter.DATE_TIME_FORMAT)
						+ "', "
						+ "'"
						+ o.getItemOuterId()
						+ "' , '"
						+ o.getSkuOuterId()
						+ "' , '"
						+ o.getTotalFee()
						+ "' , '"
						+ o.getDistributorPayment()
						+ "' , "
						+ "'"
						+ o.getStatus()
						+ "','yongjun',"
						+ "'"
						+ o.getItemId()
						+ "' , '"
						+ o.getSkuProperties()
						+ "' ,"
						+ o.getNum()
						+ " , '"
						+ o.getPrice()
						+ "' , '"
						+ o.getSnapshotUrl()
						+ "' , "
						+ "'"
						+ o.getId()
						+ "' , '"
						+ o.getSnapshotUrl()
						+ "' , '"
						+ refundstatus
						+ "',"
						+ o.getItemId()
						+ ","
						+ Double.valueOf(o.getBuyerPayment()).doubleValue()
						/ o.getNum() + ")";
				SQLHelper.executeSQL(conn, sql);
			}
			

			conn.commit();
			conn.setAutoCommit(true);

			Log.info(modulename, "生成订单【" + po.getId() + "】接口数据成功，接口单号【" + sheetid
					+ "】");
			return sheetid;

		} catch (JSQLException e1) {
			e1.printStackTrace();
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			throw new JException("生成订单【" + po.getId() + "】接口数据失败!" + e1.getMessage());
		}
	}

	public static String createDealerOrder(String modulename,
			Connection conn, DealerOrder po,  String tradecontactid)
			throws Exception {
		String sql = "select count(*) from ecs_distributor with(nolock) "
				+ "where distributorname='"
				+ po.getApplierNick().trim() + "' and shopname<>''";

		if (SQLHelper.intSelect(conn, sql) == 0) {
			throw new JException("不存在分销商:" + po.getApplierNick().trim()
					+ ",或者分销商店铺名称为空!");
		}

		try {

			String sheetid = "";

			conn.setAutoCommit(false);

			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid
					+ "',1 , '"
					+ tradecontactid
					+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			int haspostFee = 0;

			String promotionDetails = "";
			
			
			sql = "select distributorid,shopname from ecs_distributor with(nolock) where distributorname='"
					+ po.getApplierNick().trim() + "'";
			Hashtable distributorinfo = SQLHelper.oneRowSelect(conn, sql);

			String distributorid = distributorinfo.get("distributorid")
					.toString();
			String distributorshopname = distributorinfo.get("shopname")
					.toString();
		
			String buyermemo = "";

			

			String phone=po.getReceiver().getPhone();
			if (phone==null) phone="";

			sql = "insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , "
					+ " type , created , buyermessage , shippingtype , payment , "
					+ " discountfee , adjustfee , status ,  "
					+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
					+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
					+ " buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone  , "
					+ " buyeremail , commissionfee , availableconfirmfee , haspostFee , receivedpayment , "
					+ " buyermemo,sellermemo, "
					+ " snapshoturl, tradefrom ,PromotionDetails,"
					+ " tradeContactid,distributorid,distributetid,distributorshopname) values("
					+ "'"
					+ sheetid
					+ "','"
					+ sheetid
					+ "','yongjun','"
					+ po.getDealerOrderId()
					+ "','', '"
					+ po.getSupplierNick()
					+ "', "
					+ "'fixed' ,'"
					+ Formatter.format(po.getAppliedTime(),
							Formatter.DATE_TIME_FORMAT)
					+ "','','"
					+ po.getLogisticsType()
					+ "','"
					+ String.valueOf(Double.valueOf(po.getTotalPrice()).doubleValue()+Double.valueOf(po.getLogisticsFee()).doubleValue())
					+ "', "
					+ "'0.00', '0.00' , '"
					+ po.getOrderStatus()
					+ "' , "
					+ "'' , '"
					+ Formatter.format(po.getPayTime(),
							Formatter.DATE_TIME_FORMAT)
					+ "' , '"
					+ Formatter.format(po.getModifiedTime(),
							Formatter.DATE_TIME_FORMAT)
					+ "', '"
					+ Formatter.format(po.getModifiedTime(),
							Formatter.DATE_TIME_FORMAT)
					+ "' , 0 , 0, 0,"
					+ po.getTotalPrice()
					+ " , '"
					+ po.getLogisticsFee()
					+ "','"
					+ po.getAlipayNo()
					+ "',"
					+ "'"
					+ po.getReceiver().getName().replaceAll("'", "")
					+ "' ,'"
					+ po.getReceiver().getName().replaceAll("'", "")
					+ "' , '"
					+ po.getReceiver().getState()
					+ "', '"
					+ po.getReceiver().getCity()
					+ "' , '"
					+ po.getReceiver().getDistrict()
					+ "', "
					+ "'"
					+ po.getReceiver().getAddress().replaceAll("'", " ")
					+ "','"
					+ po.getReceiver().getZip()
					+ "' , '"
					+ po.getReceiver().getMobilePhone()
					+ "' , '"
					+ phone
					+ "' ,  "
					+ "'' , '0.00' , '0.00' , "
					+ String.valueOf(haspostFee)
					+ ",'"
					+ String.valueOf(Double.valueOf(po.getTotalPrice()).doubleValue()+Double.valueOf(po.getLogisticsFee()).doubleValue())
					+ "', "
					+ "  '"
					+ buyermemo.replaceAll("'", "")
					+ "', '' ,'', '"
					+ po.getApplierNick()
					+ "', '"
					+ promotionDetails
					+ "',"
					+ tradecontactid
					+ ","
					+ distributorid
					+ ",'','"
					+ distributorshopname + "')";

			SQLHelper.executeSQL(conn, sql);

		
			
			for (Iterator itorder = po.getDealerOrderDetails().iterator(); itorder
					.hasNext();) {
				DealerOrderDetail o = (DealerOrderDetail) itorder.next();
				String refundstatus  = "NO_REFUND";

				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid  , "
						+ " title , sellernick , buyernick , created , "
						+ " outeriid , outerskuid , totalfee , payment , "
						+ " status  , owner , "
						+ " iid , skuPropertiesName , num , price , picPath , "
						+ " oid , snapShotUrl,refundstatus,"
						+ "  numiid , distributePrice) values( "
						+ "'"
						+ sheetid
						+ "','"
						+ sheetid
						+ "_"
						+ o.getDealerDetailId()
						+ "','"
						+ sheetid
						+ "','"
						+ o.getSkuId()
						+ "' , "
						+ "'"
						+ o.getProductTitle()
						+ "' , '"
						+ po.getSupplierNick()
						+ "', '"
						+ po.getApplierNick()
						+ "' , '"
						+ Formatter.format(po.getAppliedTime(),
								Formatter.DATE_TIME_FORMAT)
						+ "', "
						+ "'' , '"
						+ o.getSkuNumber()
						+ "' , '"
						+ o.getPriceCount()
						+ "' , '"
						+ o.getPriceCount()
						+ "' , "
						+ "'"
						+ po.getOrderStatus()
						+ "','yongjun',"
						+ "'"
						+ o.getSkuId()
						+ "' , '"
						+ o.getSkuSpec()
						+ "' ,"
						+ o.getQuantity()
						+ " , '"
						+ o.getFinalPrice()
						+ "' , '"
						+ o.getSnapshotUrl()
						+ "' , "
						+ "'"
						+ o.getDealerDetailId()
						+ "' , '"
						+ o.getSnapshotUrl()
						+ "' , '"
						+ refundstatus
						+ "',"
						+ o.getProductId()
						+ ","
						+ o.getFinalPrice() + ")";
				SQLHelper.executeSQL(conn, sql);
			}
			

			conn.commit();
			conn.setAutoCommit(true);

			Log.info(modulename, "生成订单【" + po.getDealerOrderId() + "】接口数据成功，接口单号【" + sheetid
					+ "】");
			return sheetid;

		} catch (JSQLException e1) {
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			throw new JException("生成订单【" + po.getDealerOrderId() + "】接口数据失败!" + e1.getMessage());
		}
	}



	// taobao.trade.fullinfo.get 收费 
	public static Trade getFullTrade(String tid, String url, String appkey,
			String appsecret, String authcode) throws JException {
		Trade t = null;
		try {
			TaobaoClient client = new DefaultTaobaoClient(url, appkey,
					appsecret, "xml");
			TradeFullinfoGetRequest req = new TradeFullinfoGetRequest();
			req.setFields(TradeFields);
			req.setTid(Long.valueOf(tid));
			TradeFullinfoGetResponse rsp = client.execute(req, authcode);
			
			
			//Log.info(rsp.getBody());
			
			
			t = rsp.getTrade();
			
		

			Document doc = DOMHelper.newDocument(rsp.getBody(), "GBK");

			Element urlset = doc.getDocumentElement();
			NodeList tradenodes = urlset.getElementsByTagName("trade");
			Element trade = (Element) tradenodes.item(0);
			String phone = DOMHelper.getSubElementVauleByName(trade,
					"receiver_phone");
			String mobile = DOMHelper.getSubElementVauleByName(trade,
					"receiver_mobile");
			String sellermemo = DOMHelper.getSubElementVauleByName(trade,
					"seller_memo").replaceAll("'", " ");
			String buyermessage = DOMHelper.getSubElementVauleByName(trade,
					"buyer_message").replaceAll("'", " ");
			String buyermemo = DOMHelper.getSubElementVauleByName(trade,
					"buyer_memo").replaceAll("'", " ");

			String receiverdistrict = DOMHelper.getSubElementVauleByName(trade,
					"receiver_district");
			String promotion = DOMHelper.getSubElementVauleByName(trade,
					"promotion");
			String alipayurl = DOMHelper.getSubElementVauleByName(trade,
					"alipay_url");
			String timeoutactiontime = DOMHelper.getSubElementVauleByName(
					trade, "timeout_action_time");
			String buyerflag = DOMHelper.getSubElementVauleByName(trade,
					"buyer_flag");
			String snapshot = DOMHelper.getSubElementVauleByName(trade,
					"snapshot");
			String tradememo = DOMHelper.getSubElementVauleByName(trade,
					"trade_memo").replaceAll("'", " ");
			String consigntime = DOMHelper.getSubElementVauleByName(trade,
					"consign_time");
			String endtime = DOMHelper.getSubElementVauleByName(trade,
					"end_time");

			if (!DOMHelper.ElementIsExists(trade, "promotion_details")) {
				ArrayList<PromotionDetail> pdlist = new ArrayList<PromotionDetail>();
				// PromotionDetail pd=new PromotionDetail();
				// pd.setPromotionName("");
				// pd.setDiscountFee("");
				// pd.setGiftItemName("");

				// pdlist.add(pd);
				t.setPromotionDetails(pdlist);
			} else {
				Element promotiondetails = (Element) trade
						.getElementsByTagName("promotion_details").item(0);
				NodeList pdnodes = promotiondetails
						.getElementsByTagName("promotion_detail");
				for (int i = 0; i < pdnodes.getLength(); i++) {
					Element promotiondetail = (Element) pdnodes.item(i);

					boolean discountfeeflag = true;
					if (!DOMHelper.ElementIsExists(promotiondetail,
							"discount_fee")) {
						discountfeeflag = false;
					}
					String giftitemname = DOMHelper.getSubElementVauleByName(
							promotiondetail, "gift_item_name");
					String id = DOMHelper.getSubElementVauleByName(
							promotiondetail, "id");
					for (int j = 0; j < t.getPromotionDetails().size(); j++) {

						if (t.getPromotionDetails().get(j).getId() == Long
								.valueOf(id).longValue()) {
							t.getPromotionDetails().get(j).setGiftItemName(
									giftitemname);

							if (!discountfeeflag) {
								t.getPromotionDetails().get(j).setDiscountFee(
										"0.00");
							}
						}
					}
				}
			}

			Element orders = (Element) trade.getElementsByTagName("orders")
					.item(0);
			NodeList ordernodes = orders.getElementsByTagName("order");
			for (int n = 0; n < ordernodes.getLength(); n++) {
				Element order = (Element) ordernodes.item(n);
				String outerskuid = DOMHelper.getSubElementVauleByName(order,
						"outer_sku_id");
				String outeriid = DOMHelper.getSubElementVauleByName(order,
						"outer_iid");
				String oid = DOMHelper.getSubElementVauleByName(order, "oid");
				String skuid = DOMHelper.getSubElementVauleByName(order,
						"sku_id");
				String itemmealname = DOMHelper.getSubElementVauleByName(order,
						"item_meal_name");
				String sellernick = DOMHelper.getSubElementVauleByName(order,
						"seller_nick");

				String buyernick = DOMHelper.getSubElementVauleByName(order,
						"buyer_nick");
				String sellertype = DOMHelper.getSubElementVauleByName(order,
						"seller_type");
				String otimeoutactiontime = DOMHelper.getSubElementVauleByName(
						order, "timeout_action_time");
				String skupropertiesname = DOMHelper.getSubElementVauleByName(
						order, "sku_properties_name");
				String refundid = DOMHelper.getSubElementVauleByName(order,
						"refund_id");
				if (refundid.equals(""))
					refundid = "0";

				for (int m = 0; m < t.getOrders().size(); m++) {

					if (t.getOrders().get(m).getOid().compareTo(
							Long.valueOf(oid)) == 0) {
						if (outerskuid.equals(""))
							t.getOrders().get(m).setOuterSkuId(outeriid);
						t.getOrders().get(m).setSkuId(skuid);
						t.getOrders().get(m).setItemMealName(itemmealname);
						t.getOrders().get(m).setSellerNick(sellernick);
						t.getOrders().get(m).setBuyerNick(buyernick);
						t.getOrders().get(m).setSellerType(sellertype);
						t.getOrders().get(m)
								.setRefundId(Long.valueOf(refundid));
						if (!otimeoutactiontime.equals(""))
							t.getOrders().get(m).setTimeoutActionTime(
									Formatter.parseDate(otimeoutactiontime,
											Formatter.DATE_TIME_FORMAT));
						else
							t.setTimeoutActionTime(Formatter.parseDate(
									"1900-01-01 00:00:00",
									Formatter.DATE_TIME_FORMAT));
						t.getOrders().get(m).setSkuPropertiesName(
								skupropertiesname);

						break;
					}
				}
			}

			t.setReceiverPhone(phone);
			t.setReceiverMobile(mobile);
			t.setSellerMemo(sellermemo);
			t.setBuyerMessage(buyermessage);
			t.setBuyerMemo(buyermemo);
			t.setReceiverDistrict(receiverdistrict);
			t.setPromotion(promotion);
			t.setAlipayUrl(alipayurl);
			if (!timeoutactiontime.equals(""))
				t.setTimeoutActionTime(Formatter.parseDate(timeoutactiontime,
						Formatter.DATE_TIME_FORMAT));
			else
				t.setTimeoutActionTime(Formatter.parseDate(
						"1900-01-01 00:00:00", Formatter.DATE_TIME_FORMAT));

			if (buyerflag.equals(""))
				buyerflag = "0";
			t.setBuyerFlag(Long.valueOf(buyerflag));
			t.setSnapshot(snapshot);
			t.setTradeMemo(tradememo);
			if (!consigntime.equals(""))
				t.setConsignTime(Formatter.parseDate(consigntime,
						Formatter.DATE_TIME_FORMAT));
			else
				t.setConsignTime(Formatter.parseDate("1900-01-01 00:00:00",
						Formatter.DATE_TIME_FORMAT));

			if (!endtime.equals(""))
				t.setEndTime(Formatter.parseDate(endtime,
						Formatter.DATE_TIME_FORMAT));
			else
				t.setEndTime(Formatter.parseDate("1900-01-01 00:00:00",
						Formatter.DATE_TIME_FORMAT));

		} catch (ApiException e) {
			throw new JException("调用取订单完整信息远程方法异常!" + e.getMessage());
		} catch (JException je) {
			throw new JException("解析XML出错!" + je.getMessage());
		} catch (ParseException pe) {
			throw new JException("不可用的日期格式!" + pe.getMessage());
		}
		return t;
	}

	// taobao.refund.get 收费  api收费
	public static void getRefund(String modulename, Connection conn,
			String url, String appkey, String appsecret, String authcode,
			String tradecontactid, Trade td, Order o, String tid, long refundid)
			throws Exception
	{
		TaobaoClient client = new DefaultTaobaoClient(url, appkey,
				appsecret, "xml");
		RefundGetRequest req = new RefundGetRequest();
		req.setFields(RefundFields);
		req.setRefundId(refundid);
		RefundGetResponse rsp = client.execute(req, authcode);

		Refund r = rsp.getRefund();

		Document doc = DOMHelper.newDocument(rsp.getBody(), "GBK");

		Element urlset = doc.getDocumentElement();
		Element refundelement = (Element) urlset.getElementsByTagName(
				"refund").item(0);
		String getgoodreturntime = DOMHelper.getSubElementVauleByName(
				refundelement, "good_return_time");

		if (!getgoodreturntime.equals(""))
			r.setGoodReturnTime(Formatter.parseDate(getgoodreturntime,
					Formatter.DATE_TIME_FORMAT));
		else
			r.setGoodReturnTime(Formatter.parseDate("1900-01-01 00:00:00",
					Formatter.DATE_TIME_FORMAT));
		String totalfee = DOMHelper.getSubElementVauleByName(refundelement,
				"total_fee");
		if (totalfee.equals(""))
			r.setTotalFee("0.00");

		String iid = DOMHelper.getSubElementVauleByName(refundelement,
				"iid");
		r.setIid(iid);

		String address = DOMHelper.getSubElementVauleByName(refundelement,
				"address");
		r.setAddress(address);

		String sid = DOMHelper.getSubElementVauleByName(refundelement,
				"sid");
		r.setSid(sid);

		String companyname = DOMHelper.getSubElementVauleByName(
				refundelement, "company_name");
		r.setCompanyName(companyname);

		int hasGoodReturn = 0;
		if (r.getHasGoodReturn()) {
			hasGoodReturn = 1;
		}

		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
				+ tradecontactid;
		String inshopid = SQLHelper.strSelect(conn, sql);

		conn.setAutoCommit(false);

		sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
		String sheetid = SQLHelper.strSelect(conn, sql);
		if (sheetid.trim().equals(""))
			throw new JSQLException(sql, "取接口单号出错!");

		// 加入到通知表
		sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
				+ sheetid
				+ "',2 , '"
				+ tradecontactid
				+ "' , 'yongjun' , getdate() , null) ";
		SQLHelper.executeSQL(conn, sql);

		sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , "
				+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
				+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
				+ "Price , Num , GoodReturnTime , Sid , "
				+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName , "
				+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
				//+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				+"values("
				+"'"
				+sheetid
				+"','"
				+String.valueOf(r.getRefundId())
				+"','"
				+String.valueOf(r.getOid())
				+"','"
				+r.getAlipayNo()
				+"','"
				+r.getBuyerNick()
				+"','"
				+Formatter.format(r.getCreated(),Formatter.DATE_TIME_FORMAT)
				+"','"
				+Formatter.format(r.getModified(),Formatter.DATE_TIME_FORMAT)
				+"','"
				+r.getOrderStatus()
				+"','"
				+r.getStatus()
				+"','"
				+r.getGoodStatus()
				+"',"
				+hasGoodReturn
				+","
				+Double.valueOf(r.getRefundFee())
				+","
				+Double.valueOf(r.getPayment())
				+",'"
				+r.getReason()
				+"','"
				+r.getDesc()
				+"','"
				+o.getTitle()
				+"',"
				+Double.valueOf(r.getPrice())
				+","
				+o.getNum().intValue()
				+",'"
				+Formatter.format(r.getGoodReturnTime(),Formatter.DATE_TIME_FORMAT)
				+"','"
				+r.getSid()
				+"',"
				+Double.valueOf(r.getTotalFee())
				+",'"
				+o.getOuterIid()
				+"','"
				+o.getOuterSkuId()
				+"','"
				+r.getCompanyName()
				+"','"
				+r.getAddress()
				+"','"
				+td.getReceiverState() + " " + td.getReceiverCity() + " "+ td.getReceiverDistrict() + " "+ td.getReceiverAddress()
				+"','"
				+inshopid
				+"','"
				+String.valueOf(r.getTid())
				+"','"
				+ td.getReceiverName()
				+"','"
				+td.getReceiverPhone() + " " + td.getReceiverMobile()
				+"','"
				+td.getBuyerAlipayNo()
				+"')";
//		Object[] sqlv = {
//				sheetid,
//				String.valueOf(r.getRefundId()),
//				String.valueOf(r.getOid()),
//				r.getAlipayNo(),
//				r.getBuyerNick(),
//				r.getCreated(),
//				r.getModified(),
//				r.getOrderStatus(),
//				r.getStatus(),
//				r.getGoodStatus(),
//				hasGoodReturn,
//				Double.valueOf(r.getRefundFee()),
//				Double.valueOf(r.getPayment()),
//				r.getReason(),
//				r.getDesc(),
//				r.getTitle(),
//				Double.valueOf(r.getPrice()),
//				r.getNum().intValue(),
//				r.getGoodReturnTime(),
//				r.getSid(),
//				Double.valueOf(r.getTotalFee()),
//				o.getOuterIid(),
//				o.getOuterSkuId(),
//				r.getCompanyName(),
//				r.getAddress(),
//				td.getReceiverState() + " " + td.getReceiverCity() + " "
//						+ td.getReceiverDistrict() + " "
//						+ td.getReceiverAddress(), inshopid,
//				String.valueOf(r.getTid()), td.getReceiverName(),
//				td.getReceiverPhone() + " " + td.getReceiverMobile(),
//				td.getBuyerAlipayNo() };
//
//
//		SQLHelper.executePreparedSQL(conn, sql, sqlv);
		
		SQLHelper.executeSQL(conn, sql);
		
		Log.info(modulename, "接口单号:"
				+ sheetid
				+ " 订单号:"
				+ td.getTid()
				+ " 订单状态："
				+ td.getStatus()
				+ " 退款状态:"
				+ r.getStatus()
				+ " 订单创建时间:"
				+ Formatter.format(td.getCreated(),
						Formatter.DATE_TIME_FORMAT)
				+ " 退款创建时间:"
				+ Formatter.format(r.getCreated(),
						Formatter.DATE_TIME_FORMAT));

		conn.commit();
		conn.setAutoCommit(true);


	}

	public static void getDistributeRefund(String modulename, Connection conn,
			String url, String appkey, String appsecret, String authcode,
			String tradecontactid, PurchaseOrder po, SubPurchaseOrder o)
			throws Exception {
		
			TaobaoClient client = new DefaultTaobaoClient(url, appkey,
					appsecret, "xml");
			FenxiaoRefundGetRequest req = new FenxiaoRefundGetRequest();
			req.setSubOrderId(o.getFenxiaoId());
			FenxiaoRefundGetResponse rsp = client.execute(req, authcode);

			RefundDetail r = rsp.getRefundDetail();

			int hasGoodReturn=0;
			if (r.getIsReturnGoods()) hasGoodReturn=1;
			
			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
					+ tradecontactid;
			String inshopid = SQLHelper.strSelect(conn, sql);

			conn.setAutoCommit(false);

			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			String sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid
					+ "',2 , '"
					+ tradecontactid
					+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , "
					+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
					+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
					+ "Price , Num  , "
					+ " TotalFee ,  OuterIid , OuterSkuId  , "
					+ " ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
					//+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					+"values("
					+"'"
					+sheetid
					+"','"
					+String.valueOf(o.getFenxiaoId())
					+"','"
					+String.valueOf(o.getFenxiaoId())
					+"','"
					+po.getAlipayNo()
					+"','"
					+r.getDistributorNick()
					+"','"
					+Formatter.format(r.getRefundCreateTime(),Formatter.DATE_TIME_FORMAT)
					+"','"
					+Formatter.format(r.getModified(),Formatter.DATE_TIME_FORMAT)
					+"','"
					+po.getStatus()
					+"','"
					+r.getRefundStatus()
					+"','"
					+r.getRefundStatus()
					+"',"
					+hasGoodReturn
					+","
					+Double.valueOf(r.getRefundFee())
					+","
					+Double.valueOf(r.getPaySupFee())
					+",'"
					+r.getRefundReason()
					+"','"
					+r.getRefundDesc()
					+"','"
					+o.getTitle()
					+"',"
					+Double.valueOf(r.getRefundFee())
					+","
					+o.getNum().intValue()
					+","
					+Double.valueOf(r.getRefundFee())
					+",'"
					+o.getItemOuterId()
					+"','"
					+o.getSkuOuterId()
					+"','"
					+po.getReceiver().getState() + "" + po.getReceiver().getCity() + ""+ po.getReceiver().getDistrict() + ""+ po.getReceiver().getAddress()
					+"','"
					+inshopid
					+"','"
					+String.valueOf(po.getId())
					+"','"
					+po.getReceiver().getName()
					+"','"
					+po.getReceiver().getPhone() + " " + po.getReceiver().getMobilePhone()
					+"','"
					+po.getAlipayNo()
					+"')";

//			Object[] sqlv = {
//					
//					sheetid,
//					String.valueOf(o.getFenxiaoId()),
//					String.valueOf(o.getFenxiaoId()),
//					po.getAlipayNo(),
//					r.getDistributorNick(),
//					Formatter.format(r.getRefundCreateTime(),Formatter.DATE_TIME_FORMAT),
//					Formatter.format(r.getModified(),Formatter.DATE_TIME_FORMAT),
//					po.getStatus(),
//					r.getRefundStatus(),
//					r.getRefundStatus(),
//					hasGoodReturn,
//					Double.valueOf(r.getRefundFee()),
//					Double.valueOf(r.getPaySupFee()),
//					r.getRefundReason(),
//					r.getRefundDesc(),
//					o.getTitle(),
//					Double.valueOf(r.getRefundFee()),
//					o.getNum().intValue(),									
//					Double.valueOf(r.getRefundFee()),
//					o.getItemOuterId(),
//					o.getSkuOuterId(),					
//					po.getReceiver().getState() + " " + po.getReceiver().getCity() + " "
//							+ po.getReceiver().getDistrict() + " "
//							+ po.getReceiver().getAddress(), inshopid,
//					String.valueOf(po.getId()), po.getReceiver().getName(),
//					po.getReceiver().getPhone() + " " + po.getReceiver().getMobilePhone(),
//					po.getAlipayNo() };
//
			//Log.info("失败的SQL"+sql);
			//SQLHelper.executePreparedSQL(conn, sql, sqlv);
			SQLHelper.executeSQL(conn, sql);
			
			Log.info(modulename, "接口单号:"
					+ sheetid
					+ " 订单号:"
					+ po.getId()
					+ " 订单状态："
					+ po.getStatus()
					+ " 退款状态:"
					+ r.getRefundStatus()
					+ " 订单创建时间:"
					+ Formatter.format(po.getCreated(),
							Formatter.DATE_TIME_FORMAT)
					+ " 退货创建时间:"
					+ Formatter.format(r.getRefundCreateTime(),
							Formatter.DATE_TIME_FORMAT));

			conn.commit();
			conn.setAutoCommit(true);

		
	}
	
	
	public static void bakOrderItem(String jobName,Connection conn,int batchid,long tid){
		try{
			conn.setAutoCommit(false);
			String sql=new StringBuffer().append("insert into eco_rds_tradebak select * from eco_rds_trade where batchid=").append(batchid)
					.append(" and tid='").append(tid).append("'").toString();
			SQLHelper.executeSQL(conn, sql);
			sql = new StringBuffer().append("delete from eco_rds_trade where tid='").append(tid).append("' and batchid=").append(batchid).toString();
			SQLHelper.executeSQL(conn, sql);
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception ex){
			Log.error(jobName, ex.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.error(jobName, "设置自动提交事务失败");
			}
		}
		
	}

}
