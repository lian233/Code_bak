package com.wofu.netshop.alibaba.fenxiao;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.alibaba.fenxiao.api.ApiCallService;
import com.wofu.netshop.alibaba.fenxiao.util.CommonUtil;
public class OrderUtils {
	private static String TradeFields = "";
	private static String RefundFields = "";
	public static void setOrderItemSKU(Connection conn,Order o,String orgId) throws Exception
	{		
		for(int j=0;j<o.getOrderEntries().getRelationData().size();j++)
		{
			OrderItem item=(OrderItem) o.getOrderEntries().getRelationData().get(j);
			String sku="";
			String sqls="select count(*) from ecs_StockConfigsku where itemid='"+item.getSourceId()+"' and skuid='"+item.getSpecId()+"'";
			if (SQLHelper.intSelect(conn, sqls)==0)
			{
				sku="";
				
			}
			
			sqls="select sku from ecs_StockConfigsku where itemid='"+item.getSourceId()+"' and skuid='"+item.getSpecId()+"'";
			sku=SQLHelper.strSelect(conn, sqls);
			item.setSku(sku);
			o.getOrderEntries().getRelationData().set(j, item);
		}
	}
	
	public static void setOrderItemCode(Connection conn,Order o,String orgId) throws Exception
	{		
		for(int j=0;j<o.getOrderEntries().getRelationData().size();j++)
		{
			OrderItem item=(OrderItem) o.getOrderEntries().getRelationData().get(j);
			String sku="";
			String sqls="select itemcode from ecs_StockConfig where itemid='"+item.getSourceId()+"' and orgid='"+orgId+"'";
			if ("".equals(SQLHelper.strSelect(conn, sqls)))
			{
				sqls ="select sku from ecs_stockconfigsku where itemid='"+item.getSourceId()+"' and orgid="+orgId;
				sku=SQLHelper.strSelect(conn, sqls);
				item.setSku(sku);
				o.getOrderEntries().getRelationData().set(j, item);
				
				
			}else{
				sqls="select itemcode from ecs_StockConfig where itemid='"+item.getSourceId()+"' and orgid='"+orgId+"'";
				sku=SQLHelper.strSelect(conn, sqls);
				item.setSku(sku);
				o.getOrderEntries().getRelationData().set(j, item);
			}
			
			
		}
	}
	/**
	 * ����Ͱ� ת��һ���������ӿڱ�
	 * public static String getBuyerId(Long orderId,String token,String appkey,String appSec
			,String namespace,int version,String requestmodel,String url ){
	 */
	public static int createInterOrder(Connection conn,
			Order o, String tradecontactid,String username,String token,String appkey,String appSec
			,String namespace,int version,String requestmodel,String url,int shopid,int orderStstus)
			throws Exception {
		try {
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

			String addresses[] = o.getToArea().replaceAll("'", "").split(" ");
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
			.append(sheetid).append(",").append(shopid).append(",'").append(o.getId())
			.append("','").append(username).append("','")
			.append("','").append(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append(Formatter.format(o.getGmtCreate()!=null?o.getGmtCreate():new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append("','")
			.append("','").append(o.getSumPayment()/100).append("','")
			.append("").append("','0.0','").append(orderStstus)//20������
			.append("','").append("").append("','").append("").append("','")
			.append("','").append(Formatter.format(o.getGmtCreate(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append("','").append(Formatter.format(o.getGmtModified(), Formatter.DATE_TIME_FORMAT))
			.append("','','','','")
			.append((o.getSumPayment()-o.getCarriage())/100 ).append("','")
			.append(o.getCarriage()/100).append("','")
			.append("','").append(getBuyerId(o.getId(),token,appkey,appSec,namespace,version,requestmodel,url))
			.append("','").append(o.getToFullName())
			.append("','").append(addresses[0])
			.append("','").append(addresses[1])
			.append("','").append(addresses[2])
			.append("','").append(addresses[3])
			.append("','")
			.append("','").append(o.getToMobile()!=null?o.getToMobile():"")
			.append("','").append(o.getToPhone()!=null?o.getToPhone():"")
			.append("','").append(Formatter.format(o.getGmtModified(), Formatter.DATE_TIME_FORMAT))
			.append("','")
			.append("','")
			.append("','").append(o.getCarriage()/100)
			.append("','0','")
			//.append(delivery)  �������
			.append("','")//cod״̬�Ϳ����������
			.append("','")
			.append("','")
			.append("','','',")//dealRateState��������״̬��ʱΪ��
			.append("0,'','','")//InvoiceFlag=0   invoicetitle="";  Prepay=''
			.append("','")
			.append("','")
			.append("','").append("ALIBABA")
			.append("','")
			.append("','")
			.append("','").append(1).append("')").toString();
			SQLHelper.executeSQL(conn, sql) ;
			
			//ѭ�������Ʒ����
			for(int i=0; i< o.getOrderEntries().getRelationData().size();i++){
				OrderItem item = (OrderItem)o.getOrderEntries().getRelationData().get(i);
				sql="declare @Value integer;exec TL_GetNewSerial_new '100002',@value output;select @value;";
				int subid = SQLHelper.intSelect(conn, sql);
				JSONArray pic=null;
				if(item.getProductPic()!=null)
				pic=new JSONArray(item.getProductPic());
				String skuinfo="";
				if(item.getSpecInfo()!=null){
					JSONArray sku=new JSONArray(item.getSpecInfo());
					
					for(int j=0;j<sku.length();j++){
						skuinfo=skuinfo+sku.getJSONObject(j).getString("specName")+":"+sku.getJSONObject(j).getString("specValue")+";";
					}
				} 
				
				sql = new StringBuilder("insert into itf_DecOrderItem(ID , ParentID  , skuid , itemmealname , title , ")
				.append(" sellernick , buyernick , type , created , refundstatus , ")
				.append(" outeriid , outerskuid , totalfee , payment , discountfee , ")
				.append(" adjustfee , status , timeoutactiontime  ,")
				.append(" iid , skuPropertiesName , num , price , ")
				.append(" picPath , oid , snapShotUrl , snapShot ,buyerRate ,sellerRate,")
				.append("  sellertype , refundId , isoversold,modified,numiid,cid,DistributePrice) values( ")
				.append(subid).append(",").append(sheetid).append(",'")
				.append(item.getSpecId()).append("','").append(item.getProductName())
				.append("','").append(item.getProductName())
				.append("','").append(username)
				.append("','").append(username)
				.append("','")
				.append("','").append(Formatter.format(o.getGmtModified(),Formatter.DATE_TIME_FORMAT))
				//0û���˿10����Ѿ������˿�ȴ�����ͬ�⡣20�����Ѿ�ͬ���˿�ȴ�����˻���30����Ѿ��˻����ȴ�����ȷ���ջ���40���Ҿܾ��˿90�˿�رա�100�˿�ɹ���
				.append("',0")
				.append(",'")
				.append("','").append(item.getSpecId())
				.append("','").append((item.getPrice()*item.getQuantity()+item.getEntryDiscount())/100)
				.append("','").append(((item.getPrice()*item.getQuantity()+item.getEntryDiscount()+o.getCarriage())/100))
				.append("','")
				.append("','")
				.append("','")
				.append("','")//timeoutactiontime   ������ʱ����ʱ����ʱΪ��
				.append("','")//iidҲΪ��
				.append("','").append(skuinfo)
				.append("',").append(item.getQuantity())
				.append(",'").append(item.getPrice()/100)
				.append("','").append(item.getProductPic().substring(2,item.getProductPic().length()-2))//��ƷͼƬ
				.append("','").append(item.getSpecId())//��ϸ��Ψһ��ʶ  ��ƽ̨������Ի���ɫ������֮��ģ���ϸ��Ϣ��䣬�����oid�����
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
			}
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getId() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");
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
			throw new JException("���ɶ�����" + o.getId() + "���ӿ�����ʧ��,������Ϣ��"
					+ e1.getMessage());
		}
	}

	//�˻�
	public static void getRefund(String modulename, Connection conn,
			String tradecontactid, Order o) throws Exception {

//		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
//				+ tradecontactid;
//		String inshopid = SQLHelper.strSelect(conn, sql);
//
//		conn.setAutoCommit(false);
//
//		sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
//		String sheetid = SQLHelper.strSelect(conn, sql);
//		if (sheetid.trim().equals(""))
//			throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");
//
//		// ���뵽֪ͨ��
//		sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
//				+ "values('yongjun','"
//				+ sheetid
//				+ "',2 , '"
//				+ tradecontactid
//				+ "' , 'yongjun' , getdate() , null) ";
//		SQLHelper.executeSQL(conn, sql);
//
//		sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , "
//				+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
//				+ " HasGoodReturn ,RefundFee , Payment ,  Title ,"
//				+ "Price , Num ,"
//				+ " TotalFee ,  OuterIid , OuterSkuId  , "
//				+ " ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
//				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//
//		Object[] sqlv = {
//				sheetid,
//				o.getId(),
//				o.getId(),
//				o.getAli_trade_no(),
//				o.getBuyer_nick(),
//				Formatter.format(o.getCreat_time(), Formatter.DATE_TIME_FORMAT),
//				Formatter.format(o.getModified(), Formatter.DATE_TIME_FORMAT),
//				o.getStatus(),
//				o.getStatus(),
//				o.getStatus(),
//				1,
//				o.getPayment(),
//				o.getPayment(),
//				o.getTitle(),
//				Double.valueOf(o.getPrice()),
//				o.getNum(),
//				o.getTotal_fee(),
//				o.getOuter_id(),
//				o.getSku_outer_id(),
//				o.getReceiver_state() + " " + o.getReceiver_city() + " "
//						+ o.getReceiver_district() + " "
//						+ o.getReceiver_address(), inshopid, o.getTid(),
//				o.getReceiver_name(),
//				o.getReceiver_mobile() + " " + o.getReceiver_phone(),
//				o.getAli_trade_no() };
//
//		SQLHelper.executePreparedSQL(conn, sql, sqlv);
//
//		Log.info(modulename, "�ӿڵ���:"
//				+ sheetid
//				+ " ������:"
//				+ o.getTid()
//				+ " ����״̬��"
//				+ o.getStatus()
//				+ " ��������ʱ��:"
//				+ Formatter.format(o.getCreat_time(),
//						Formatter.DATE_TIME_FORMAT));
//
//		conn.commit();
//		conn.setAutoCommit(true);

	}
	
	/**
	 *���ݶ���idȡ���������--���ns_customerorder��buynick�ֶ�
	 * @param orderId
	 * @param token
	 * @param appkey
	 * @param appSec
	 * @return
	 */
	public static String getBuyerId(Long orderId,String token,String appkey,String appSec
			,String namespace,int version,String requestmodel,String url ){
		String result="";
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("id", orderId+"") ;
			String urlPath=CommonUtil.buildInvokeUrlPath(namespace,"trade.order.detail.get",version,requestmodel,appkey);
			params.put("access_token", token);
			String responseText = ApiCallService.callApiTest(url, urlPath, appSec, params);
			Log.info("ȡ�������鷵������Ϊ: "+responseText);
			JSONObject res=new JSONObject(responseText);
			result =  res.getJSONObject("orderModel").getString("buyerLoginId");
			Log.info("������:��"+result);
		}catch(Exception ex){
			Log.error("���ݶ���idȡ���id����", ex.getMessage());
		}
		return result;
		
		
	}

}
