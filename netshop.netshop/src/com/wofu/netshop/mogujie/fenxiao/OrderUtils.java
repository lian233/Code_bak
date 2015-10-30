package com.wofu.netshop.mogujie.fenxiao;
import java.sql.Connection;
import java.util.Date;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class OrderUtils {
	/*
	 * ת��һ���������ӿڱ�
	 */
	public static void createInterOrder(Connection conn,
			Order o, String username,int shopid,int status) throws Exception {
		try {

			
			String paymode="".equals(o.getPayment_tid())?"1":"2";
			Log.info("payMode: "+paymode);
			
			int invoiceflag=0;
			
			String invoicetitle="";

			conn.setAutoCommit(false);

			int sheetid;
			String sql="declare @Value int;exec TL_GetNewSerial_new 100001,@value output;select @value;";
			sheetid=SQLHelper.intSelect(conn, sql);
			if (sheetid==0)
				throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");

			// ���뵽֪ͨ��
			sql = new StringBuilder().append("insert into inf_downnote(sheettype,notetime,opertype,operdata,flag,owner)")
			.append("values(1,getdate(),100,'")
			.append(sheetid).append("',0,'')").toString();
			SQLHelper.executeSQL(conn,sql);
			float totalPrice =0.0f;
			for (int i=0;i<o.getOrders().getRelationData().size();i++) {
				OrderItem item = (OrderItem) o.getOrders().getRelationData().get(i);
				sql="declare @Value integer;exec TL_GetNewSerial_new '100002',@value output;select @value;";
				int subid = SQLHelper.intSelect(conn, sql);
				sql = new StringBuilder("insert into itf_DecOrderItem(ID , ParentID  , skuid , itemmealname , title , ")
				.append(" sellernick , buyernick , type , created , refundstatus , ")
				.append(" outeriid , outerskuid , totalfee , payment , discountfee , ")
				.append(" adjustfee , status , timeoutactiontime  ,")
				.append(" iid , skuPropertiesName , num , price , ")
				.append(" picPath , oid , snapShotUrl , snapShot ,buyerRate ,sellerRate,")
				.append("  sellertype , refundId , isoversold,modified,numiid,cid,DistributePrice) values( ")
				.append(subid).append(",").append(sheetid).append(",'")
				.append(item.getSku_id()).append("','").append(item.getTitle())
				.append("','").append(item.getTitle())
				.append("','").append(username)
				.append("','").append(username)
				.append("','")
				.append("','").append(Formatter.format(o.getCreated(),Formatter.DATE_TIME_FORMAT))
				//0û���˿10����Ѿ������˿�ȴ�����ͬ�⡣20�����Ѿ�ͬ���˿�ȴ�����˻���30����Ѿ��˻����ȴ�����ȷ���ջ���40���Ҿܾ��˿90�˿�رա�100�˿�ɹ���
				.append("',0")
				.append(",'")
				.append("','").append(item.getSku_bn())
				.append("','").append(item.getTotal_order_fee()/100)
				.append("','").append(item.getTotal_order_fee()/100)
				.append("','")
				.append("','")
				.append("','")
				.append("','")//timeoutactiontime   ������ʱ����ʱ����ʱΪ��
				.append("','")//iidҲΪ��
				.append("','").append(item.getSku_properties())
				.append("',").append(item.getItems_num())
				.append(",'").append(item.getSale_price()/100)
				.append("','").append(item.getImage())//��ƷͼƬ
				.append("','").append(item.getOid())//��ϸ��Ψһ��ʶ  ��ƽ̨������Ի���ɫ������֮��ģ���ϸ��Ϣ��䣬�����oid�����
				.append("','")
				.append("','")
				.append("',0")
				.append(",0")
				.append(",'")//sellertypeΪ��
				.append("',0")//refundid=0
				.append(",'")
				.append("','")//modifiedΪ��
				.append("','")
				.append("','")
				.append("',0.0)").toString();//DistributePrice��ʱΪ��
        		SQLHelper.executeSQL(conn, sql) ;
				totalPrice+= item.getSale_price()*item.getItems_num();
			}
			sql =new StringBuilder().append("insert into itf_DecOrder")
			.append("(ID , shopid , tid , sellernick  , type , ")
			.append(" CreateTime , created , buyermessage , shippingtype , payment , ")
			.append(" discountfee , adjustfee , status , buyermemo , sellermemo , ")
			.append(" tradememo , paytime , endtime , modified ,buyerobtainpointfee , ")
			.append(" pointfee , realpointfee , totalfee , postfee , buyeralipayno , ")
			.append(" buyernick , receivername , receiverstate , receivercity , receiverdistrict , ")
			.append(" receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , ")
			.append(" buyeremail , haspostFee , receivedpayment , codstatus,delivery,")
			.append(" alipayNo , buyerflag , sellerflag,brandsaleflag,dealRateState,")
			.append("InvoiceFlag,invoicetitle,Prepay,")
			.append(" sellerrate , buyerrate , promotion , tradefrom , alipayurl , ")
			.append(" PromotionDetails,paymode)")//56
			.append(" values(")
			.append(sheetid).append(",").append(shopid).append(",'").append(o.getTid())
			.append("','").append(username).append("','")
			.append("','").append(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append(Formatter.format(o.getCreated()!=null?o.getCreated():new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append(o.getBuyer_memo()).append("','")
			.append("','").append(o.getTotal_trade_fee()/100).append("','")
			.append("").append("','0.0','").append(status)//20������
			.append("','").append(o.getBuyer_memo()).append("','").append(o.getTrade_memo()).append("','")
			.append("','").append(Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append("','").append(Formatter.format(o.getLastmodify(), Formatter.DATE_TIME_FORMAT))
			.append("','','','','")
			.append(totalPrice/100).append("','")
			.append(0).append("','")
			.append("','").append(o.getBuyer_uname())
			.append("','").append(o.getReceiver_name())
			.append("','").append(o.getReceiver_state())
			.append("','").append(o.getReceiver_city())
			.append("','").append(o.getReceiver_district())
			.append("','").append(o.getReceiver_address())
			.append("','")
			.append("','").append( o.getReceiver_mobile())
			.append("','").append(o.getReceiver_phone())
			.append("','").append(Formatter.format(o.getLastmodify(), Formatter.DATE_TIME_FORMAT))
			.append("','")
			.append("','")
			.append("','").append(o.getTotal_trade_fee()/100)
			.append("','0','")
			//.append(delivery)  �������
			.append("','")//cod״̬�Ϳ����������
			.append("','")
			.append("','")
			.append("','','',")//dealRateState��������״̬��ʱΪ��
			.append("0,'','','")//InvoiceFlag=0   invoicetitle="";  Prepay=''
			.append("','")
			.append("','")
			.append("','").append("MOGUJIE")
			.append("','")
			.append("','")
			.append("','").append(1).append("')").toString();
			SQLHelper.executeSQL(conn, sql) ;
			conn.commit();
			conn.setAutoCommit(true);

			Log.info("���ɶ�����" + o.getTid() + "���ӿ����ݳɹ����ӿڵ��š�"+ sheetid + "��");

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
			throw new JException("���ɶ�����" + o.getTid() + "���ӿ�����ʧ��,������Ϣ��"+ e1.getMessage());
		}
	}
	
	/**
	public static void getRefund(Connection conn,String tradecontactid,Order o)	throws Exception {

			
		
			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
					+ tradecontactid;
			String inshopid = SQLHelper.strSelect(conn, sql);

			conn.setAutoCommit(false);
			
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();

				sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
				String sheetid = SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");
	
				// ���뵽֪ͨ��
				sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
						+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "insert into ns_Refund(SheetID , tid,RefundID , Oid , AlipayNo , "
						+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
						+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
						+ "Price , Num , GoodReturnTime , Sid , "
						+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName , "
						+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
				
				Object[] sqlv = {
						sheetid,
						o.getOrderCode(),
						String.valueOf(o.getOrderId())+String.valueOf(item.getId()),
						String.valueOf(item.getId()),
						o.getEndUserId(),
						o.getEndUserId(),
						o.getOrderCreateTime(),
						o.getUpdateTime(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						1,
						item.getOrderItemAmount(),
						item.getOrderItemAmount(),
						o.getDeliveryRemark(),
						o.getDeliveryRemark(),
						item.getProductCName(),
						item.getOrderItemPrice(),
						item.getOrderItemNum(),
						item.getProcessFinishDate(),
						"",
						item.getOrderItemAmount(),
						item.getOuterId(),
						item.getOuterId(),
						"",
						"",
						o.getGoodReceiverProvince() + " " + o.getGoodReceiverCity() + " "
								+ o.getGoodReceiverCounty()+ " "
								+ o.getGoodReceiverAddress(), inshopid,
						o.getOrderCode(), o.getGoodReceiverName(),
						o.getGoodReceiverPhone() + " " + o.getGoodReceiverMoblie(),
						o.getEndUserId() };
	
	
				SQLHelper.executePreparedSQL(conn, sql, sqlv);
				
			}

	

			Log.info("�����˻����ɹ�,������:"+ o.getOrderCode()+ " ����״̬��"+o.getOrderStatus()				
					+ " ��������ʱ��:"+Formatter.format(o.getOrderCreateTime(),Formatter.DATE_TIME_FORMAT));

			conn.commit();
			conn.setAutoCommit(true);

	}
	**/
	/**
	public static void createRefund(Connection conn,RefundDetail r,
			int tradecontactid,String app_key,String token,String format,String ver) throws Exception 
	{
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="+ tradecontactid;
		String inshopid = SQLHelper.strSelect(conn, sql);

		
		
		Map<String, String> orderparams = new HashMap<String, String>();
        //ϵͳ����������
		orderparams.put("appKey", app_key);
		orderparams.put("sessionKey", token);
		orderparams.put("format", format);
		orderparams.put("method", "yhd.order.detail.get");
		orderparams.put("ver", ver);
		orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
        
		orderparams.put("orderCode", r.getOrderCode());
     
        
		String responseOrderData = Utils.sendByPost(orderparams,Params.app_secret,Params.url);
        
		//Log.info("�˻�����: "+responseOrderData);
		JSONObject responseorder=new JSONObject(responseOrderData);
		
		int errorOrderCount=responseorder.getJSONObject("response").getInt("errorCount");
		
		if (errorOrderCount>0)
		{
			String errdesc="";
			JSONArray errlist=responseorder.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
			for(int n=0;n<errlist.length();n++)
			{
				JSONObject errinfo=errlist.getJSONObject(n);
				
				errdesc=errdesc+" "+errinfo.getString("errorDes"); 
									
			}
			
			throw new JException(errdesc);						
		}
		
		
		JSONObject orderdetail=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderDetail");
		
		
		Order o=new Order();
		o.setObjValue(o, orderdetail);
						
		
		JSONArray orderItemList=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderItemList").getJSONArray("orderItem");
		
		o.setFieldValue(o, "orderItemList", orderItemList);
		
		conn.setAutoCommit(false);
		
		for (Iterator it=r.getRefundItemList().getRelationData().iterator();it.hasNext();)
		{

			RefundItem item=(RefundItem) it.next();
			
			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			String sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");

			// ���뵽֪ͨ��
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_Refund(SheetID ,RefundID , Oid  , "
					+ "BuyerNick , Created , Modified ,  Status , GoodStatus , "
					+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
					+ "Price , Num , GoodReturnTime  , "
					+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName ,sid, "
					+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			String outerskuid="";
			for (Iterator itorder=o.getOrderItemList().getRelationData().iterator();itorder.hasNext();)
			{
				OrderItem orderitem=(OrderItem) itorder.next();
				if (orderitem.getId()==item.getOrderItemId())
					outerskuid=orderitem.getOuterId();
			}
						

			Object[] sqlv = {
					sheetid,
					String.valueOf(r.getRefundCode()),
					String.valueOf(item.getOrderItemId()),
					r.getReceiverName(),
					r.getApplyDate(),
					r.getApplyDate(),
					String.valueOf(r.getRefundStatus()),
					String.valueOf(r.getRefundStatus()),
					1,
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					r.getReasonMsg(),
					r.getRefundProblem(),
					item.getProductCname(),
					item.getOrderItemPrice(),
					item.getProductRefundNum(),
					r.getSendBackDate(),
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					outerskuid,
					outerskuid,
					r.getExpressName(),
					r.getExpressNbr(),
					r.getSendBackAddress(),
					r.getReceiverAddress(), inshopid,
					r.getOrderCode(), r.getReceiverName(),
					r.getReceiverPhone()};
		

			SQLHelper.executePreparedSQL(conn, sql, sqlv);


			Log.info( "�ӿڵ���:"+ sheetid	+ " ������:"	+ r.getOrderCode()+ " ״̬��"+ r.getRefundStatus()+ "�˻�����ʱ��:"
					+ Formatter.format(r.getApplyDate(),
							Formatter.DATE_TIME_FORMAT));
		}
		
		conn.commit();
		conn.setAutoCommit(true);

	}**/
	
	
}
