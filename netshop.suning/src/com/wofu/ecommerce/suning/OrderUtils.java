package com.wofu.ecommerce.suning;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.suning.util.CommHelper;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","�˻�","����",""} ;

	/**
	 * ��ȡ����������ϸ��Ϣ
	 * @param orderCode	������
	 * @return
	 */
	public static Order getOrderByCode(String url,String orderCode,String appKey,String appSecret,String format)
	{
			
		Order o=null;
		try 
		{	for(int i=0;i<5;i++){
			//������
			String apimethod="suning.custom.order.get";
			HashMap<String,String> reqMap = new HashMap<String,String>();
	        reqMap.put("orderCode", orderCode);
	        String ReqParams = CommHelper.getJsonStr(reqMap, "orderGet");
	        HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("appSecret", appSecret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appRequestTime", CommHelper.getNowTime());
	        map.put("appKey", appKey);
	        map.put("resparams", ReqParams);
	        //��������
			String responseText = CommHelper.doRequest(map,url);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText).getJSONObject("responseContent");
			if(responseText.indexOf("sn_error")!=-1){
				//������� 
				JSONObject errorObj= responseObj.getJSONObject("sn_error");
				//�жϷ����Ƿ���ȷ
				if(errorObj!=null)
				{
					String operCode = errorObj.getString("error_code") ;
					if(!"".equals(operCode))
					{
						Log.error("������ȡ����", "��ȡ����ʧ��,������:"+ orderCode +",operCode:"+operCode);
						return null ;
					}
				}
			}
			
			JSONObject orderObj = responseObj.getJSONObject("sn_body").getJSONObject("orderGet");
			JSONArray items = orderObj.getJSONArray("orderDetail");
			o = new Order() ;
			o.setObjValue(o,orderObj);
			o.setFieldValue(o, "orderItemList", items);
			//������Ʒ��ĳЩ����
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();
				//��Ʒsku
				String itemCode= OrderUtils.getItemCodeByProduceCode(item.getProductCode(),appKey,appSecret,format)[0];
				item.setItemCode(itemCode);
				//��ƷͼƬ����
				String itemImg= OrderUtils.getItemCodeByProduceCode(item.getProductCode(),appKey,appSecret,format)[1];
				item.setPicPath(itemImg);
			}
			break;
		}
		
			
	} catch (Exception e) {
			Log.error("��ȡ��������", "��ȡ����ʧ�ܣ����ţ�"+orderCode+"��������Ϣ��"+e.getMessage()) ;
			o = null ;
		}
		return o ;
	}

	/**
	 * �����ӿڶ���
	 * @param conn
	 * @param o
	 * @param tradeContactID
	 * @param username
	 * @return
	 * @throws SQLException 
	 */
	public static String createInterOrder(Connection conn,Order o,String tradeContactID,String username) throws Exception
	{		
		try 
		{
			conn.setAutoCommit(false);		
			String sheetid="";
			String sql="declare @Err int ; declare @NewSheetID char(16); "+
				"execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
				
			sheetid=SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
			Hashtable cityInfo = getCityByCode(conn,o.getProvinceCode(),o.getCityCode(),o.getDistrictCode());
			
			//������ϸ
			float totalPrice = 0.00f ;//�ܽ��
			float sellerDiscount = 0.0f ;//�̼����Żݽ��
			//ʵ���ܽ��
			float totalItemPayment=0.0f;
			//Ӧ���ܽ��
			float totalfee=0.0f;
			//���ʷ�
			float totalPostfee=0.0f;
			//float paymentPercent = 1-(sellerDiscount/totalPrice) ;
			//float countDiscountFee = 0f ;
			//float countPayment = 0f ;
			//��Ʊ���
			//float invoicePercent = 1 - ((discountfee+giftCardMoney) / totalPrice) ;
			//float countInvoicePayment = 0f ;
			int j=0;
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				
				//��Ʒ���Żݽ��
				float discountfee=item.getCoupontotalMoney()+item.getVouchertotalMoney();
				//Ӧ�����
				float itemTotalFee = item.getSaleNum() * item.getUnitPrice()-discountfee ;
				//ʵ�����
				float itemPayment = item.getPayAmount() ;
				//float invoicePayment = 0f ;
				//����iidΪ�����̼ұ���
				sql = new StringBuilder().append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
                    .append(" title , sellernick , buyernick , type , created , ") 
                    .append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")
                    .append(" discountfee , adjustfee , status , timeoutactiontime , owner , ")
                    .append(" iid , skuPropertiesName , num , price , picPath , " )
                    .append(" buyerRate,modified) values( '")
                    .append(sheetid).append("','").append(sheetid).append("-").append(o.getOrderCode()).append(String.valueOf(++j))
                    .append("','").append(sheetid).append("','").append(item.getItemCode()).append("','','")
                    .append(item.getProductName()).append("','").append(username).append("','")
                    .append(o.getUserName()).append("','','")
                    .append(Formatter.format(o.getOrderSaleTime(), Formatter.DATE_TIME_FORMAT)).append("','','").append(item.getItemCode()).append("','")
                    .append(item.getItemCode()).append("','").append(itemTotalFee).append("','").append(itemPayment).append("','")
                    .append(discountfee).append("','','").append(getOrderStateByCode(o.getOrderLineStatus())).append("','','yongjun','").append(item.getProductCode()).append("','','")
                    .append((int)item.getSaleNum()).append("','").append(item.getUnitPrice()).append("','").append(item.getPicPath())
                    .append("',").append(Integer.parseInt(o.getEvaluationMark())).append(",'").append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();
				//Log.info("ns_orderitem��SQL��䣺"+sql);    
        		SQLHelper.executeSQL(conn, sql) ;
        		sellerDiscount+=discountfee;
        		totalItemPayment+=itemPayment;
        		totalfee+=item.getUnitPrice()*item.getSaleNum();
        		totalPostfee+=item.getTransportFee();
			}
			//Log.info("ns_orderitemд����ϣ���ns_customerorder��");
			//���뵽���ݱ�
			System.out.println("��ָ�����");
			System.out.println("1"+sellerDiscount);
			System.out.println("12"+getOrderStateByCode(o.getOrderLineStatus()));
			System.out.println("13"+totalfee);
			System.out.println("14"+totalPostfee);
			System.out.println("15"+o.getCustomerName());
			System.out.println("16"+cityInfo.get("provinceName"));
			System.out.println("17"+cityInfo.get("cityname"));
			System.out.println("18"+cityInfo.get("districtname"));
			System.out.println("19"+o.getCustomerAddress().replaceAll("'", ""));
			System.out.println("10"+o.getMobNum());
			System.out.println("11"+o.getEvaluationMark());
			System.out.println("12"+tradeContactID);
			System.out.println("13"+o.getBuyerOrdRemark().replaceAll("'","''"));
			System.out.println("14"+o.getSellerOrdRemark());
			sql =  new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append(" type , created , buyermessage , shippingtype , payment , ")
				.append(" discountfee , adjustfee , status ,paytime,totalfee , postfee , buyeralipayno , ")
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receivermobile , dealRateState, ")
				.append(" tradefrom,TradeContactID,modified,buyermemo,sellermemo) values('")
				.append(sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrderCode()).append("','','").append(username)
				.append("','','").append(Formatter.format(o.getOrderSaleTime(),Formatter.DATE_TIME_FORMAT)).append("','','','").append(totalItemPayment).append("','").append(sellerDiscount).append("','','")
				.append(getOrderStateByCode(o.getOrderLineStatus())).append("','").append(Formatter.format(o.getOrderSaleTime(),Formatter.DATE_TIME_FORMAT)).append("','").append(totalfee).append("','").append(totalPostfee).append("','','").append(o.getUserName()).append("','','")
				.append(o.getCustomerName()).append("','").append(cityInfo.get("provinceName")).append("','").append(cityInfo.get("cityname")).append("','").append(cityInfo.get("districtname"))
				.append("','").append(o.getCustomerAddress().replaceAll("'", "")).append("','").append(o.getMobNum()).append("','").append(o.getEvaluationMark()).append("',")
                .append("'SUNING','").append(tradeContactID).append("','").append(Formatter.format(o.getOrderSaleTime(), Formatter.DATE_TIME_FORMAT)).append("','").append(o.getBuyerOrdRemark().replaceAll("'","''")).append("','").append(o.getSellerOrdRemark()).append("')").toString();
			Log.info("ns_customerorder��SQL��䣺"+sql);

			SQLHelper.executeSQL(conn, sql);
			//���뵽֪ͨ��
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrderCode() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");

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
			throw new JException("���ɶ�����" + o.getOrderCode() + "���ӿ�����ʧ��!"
					+ e1.getMessage());
		}
	}

	


	
	//��ȡ��Ʊ��ϸ��Ϣ
	public static String getInvoiceDetail(String jobname,Connection conn,String sku)
	{
		String detail = "" ;
		try 
		{
			String sql = "select a.customBC+c.name from barcode as a with(nolock),goods as b with(nolock),dept as c with(nolock) " +
			"where a.goodsid=b.goodsid and b.deptid=c.id and a.customBC='"+ sku +"'" ;
			detail = SQLHelper.strSelect(conn, sql) ;
		}
		catch (Exception e) 
		{
			Log.error(jobname, "��ȡ��Ʒ�������ʧ��,������Ϣ:"+e.getMessage()) ;
			detail = "" ;
			e.printStackTrace() ;
		}
		
		return detail ;
	}

	//��ȡ��Ʊ��λ
	public static String getGoodsUnitName(String jobname,Connection conn,String sku)
	{
		String unitName = "" ;
		if("".equals(sku) || sku == null)
			return unitName ;
		try 
		{
			String sql ="select unitname from goods as a with(nolock),barcode as b with(nolock) where a.goodsid=b.goodsid and b.customBC='"+ sku +"'" ;
			unitName = SQLHelper.strSelect(conn, sql) ;
		} catch (Exception e) {
			Log.error(jobname, "��ȡ��Ʒ��λʧ��,������Ϣ:"+e.getMessage()+",sku:"+sku) ;
		}
		return unitName ;
	}
	
	//�����˻��ӿ�����
	public static void createRefundOrder(String jobname,Connection conn,String tradecontactid,
			Order order,String url,String appKey,String appsecret,String format)
	{
		ReturnOrder o = getReturnOrder(order,url,appKey,appsecret,format);
		String sql = "" ;
		float refundFee = 0f ;
		//�������
		String BuyerNick=order.getCustomerName();
		//֧�������ҵĽ��
		float Payment=o.getDealMoney()-o.getReturnMoney();
		try 
		{
			ArrayList<ReturnOrderItem> itemList = o.getItemList() ;
			
			sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+tradecontactid;
            String inshopid = SQLHelper.strSelect(conn, sql);
			for(int i=0 ; i<itemList.size() ; i++)
			{
				try 
				{
					ReturnOrderItem item = itemList.get(i) ;
		            conn.setAutoCommit(false);
		            
		            sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
					//Log.info(sql) ;
		            String sheetid = SQLHelper.strSelect(conn, sql);
					if (sheetid.trim().equals(""))
						throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
					
					refundFee = item.getUnitPrice() * item.getOrderCount() ;
					
					sql=new StringBuilder().append("insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , ")
						.append("Created , Modified , OrderStatus , Status , GoodStatus , ")
	                    .append(" HasGoodReturn ,RefundFee , Payment , Reason,Description ,")
	                    .append(" Title , Price , Num , GoodReturnTime , Sid , ")
	                    .append(" TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ,") 
	                    .append(" Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)")
	                    .append(" values('").append(sheetid ).append("','").append(sheetid).append("','','','").append(BuyerNick).append("','")
	                    .append( Formatter.format(o.getApplyTime(), Formatter.DATE_TIME_FORMAT)).append("','").append(Formatter.format(o.getApplyTime(), Formatter.DATE_TIME_FORMAT)).append("','").append(o.getStatusDesc()).append("','',''")
	                    .append("'1','").append(o.getReturnMoney()).append("','").append(Payment).append("','").append(o.getReason()).append("','','")
	                    .append(item.getItemName()).append("','").append(item.getUnitPrice()).append("','").append(item.getOrderCount()).append("','").append(Formatter.format(o.getApplyTime(), Formatter.DATE_TIME_FORMAT)).append("','").append(o.getMailNo())
	                    .append("','").append(o.getDealMoney()).append("','").append(item.getItemID()).append("','','").append(item.getOuterItemID()).append("','")
	                    .append("'','").append(order.getCustomerAddress().replaceAll("'",",'','")).append(inshopid).append("','").append(order.getOrderCode()).append("','").append(order.getCustomerName()).append("','").append(order.getMobNum()).append("','')").toString();

					Log.info("�˻���sql: "+sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					//���뵽֪ͨ��     �˻���־Ϊ2
		            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
		                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					//Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					Log.info(jobname,"�ӿڵ���:"+sheetid+" �˻�������:"+o.getOrderCode()+"����������ʱ��:"+Formatter.format(o.getApplyTime(),Formatter.DATE_TIME_FORMAT));
					conn.commit();
					conn.setAutoCommit(true);
				}
				catch (SQLException e1)
				{			
					if (!conn.getAutoCommit())
						try
						{
							conn.rollback();
						}
						catch (Exception e2) { }
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception e3) { }
					throw new JSQLException("�����˻���" + o.getOrderCode() + "���ӿ�����ʧ��!"+e1.getMessage());
				}
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "���ɽӿ��˻���ʧ��,������:"+o.getOrderCode() + ",�˻�������:"+",������Ϣ:"+e.getMessage()) ;
			e.printStackTrace() ;
		}
	}
	//�˻���������
	private static String getRefundDesc(String index)
	{
		try 
		{
			return refundDesc[Integer.parseInt(index)] ;
		} catch (Exception e) {
			return index ;
		}
	}

	
	//���ض���״̬
	public static String getOrderStateByCode(String orderStateCode)
	{
		if("10".equals(orderStateCode))
			return "�ȴ�����" ;
		else if("20".equals(orderStateCode))
			return "�ѷ���" ;
		else if("21".equals(orderStateCode))
			return "���ַ���" ;
		else if("30".equals(orderStateCode))
			return "���׳ɹ�" ;
		else if("40".equals(orderStateCode))
			return "���׹ر�" ;
		else
			return "δ֪�Ķ���״̬" ;
	}
	/**
	 * ���ݳ��д����ȡ����
	 * @return
	 */
	public static Hashtable getCityByCode(Connection conn,String provinceCode,String citycode,String districtcode){
		String sql = new StringBuilder().append("select provinceName,cityname,districtname from sn_citycode where provinceCode='").append(provinceCode)
			.append("' and citycode='").append(citycode).append("' and districtcode='").append(districtcode).append("'").toString();
		try {
			return SQLHelper.oneRowSelect(conn, sql);
		} catch (JSQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * ����������Ʒ�����ȡ�̼�sku,��ƷͼƬ����
	 * @return
	 */
	public static String[] getItemCodeByProduceCode(String productCode,String appKey,String appSec,String format) throws Exception{
		String[] itemInfo= new String[2];
		try{
			for(int k=0;k<5;k++){
				//������
				String apiMethod="suning.custom.item.get";
				HashMap<String,String> reqMap = new HashMap<String,String>();
				reqMap.put("productCode", productCode);
			    String ReqParams = CommHelper.getJsonStr(reqMap, "item");
			    HashMap<String,Object> map = new HashMap<String,Object>();
			    map.put("appSecret", appSec);
			    map.put("appMethod", apiMethod);
			    map.put("format", format);
			    map.put("versionNo", "v1.2");
			    map.put("appRequestTime", CommHelper.getNowTime());
			    map.put("appKey", appKey);
			    map.put("resparams", ReqParams);
			     //��������
				String responseText = CommHelper.doRequest(map,Params.url);
				//Log.info("��ȡ��Ʒsku�����ȷ��ؽ��:��"+responseText);
				//{"sn_responseContent":{"sn_head":{},"sn_error":{"error_code":"biz.handler.data-get:no-result"}}}
				JSONObject response = new JSONObject(responseText).getJSONObject("sn_responseContent");
				if(!response.isNull("sn_error")){
					itemInfo[0]="";
					itemInfo[1]="";
					return itemInfo;
				}
				JSONObject item= response.getJSONObject("sn_body").getJSONObject("item");
				//û������Ʒ�����
				String productCodeTemp=item.getString("productCode");
				if(productCodeTemp.equals(productCode)){
					itemInfo[0]=item.getString("itemCode");
					itemInfo[1]=item.getString("img1Url");
				}else{
					JSONArray childitems=item.getJSONArray("childItem");
					for(int i=0;i<childitems.length();i++){
						JSONObject chItem=childitems.getJSONObject(i);
						if(productCode.equals(chItem.getString("productCode"))){
							itemInfo[0]=chItem.getString("itemCode");
							itemInfo[1]=chItem.getString("img1Url");
						}
					}
				}
				break;
			}
			return itemInfo;
			
		}catch(Exception ex){
			Log.info("��ȡ������Ʒ�������,��Ʒ����: "+productCode);
			return null;
		}
		
		
	}
	
	/**
	 * ���ݶ�����ȡ���˻���Ϣ
	 * orderCode  ԭ������
	 * ReturnOrder �˻���������
	 */
	public static ReturnOrder getReturnOrder(Order o,String url,String appKey,String appsecret,String format){
		ReturnOrder ro=null;
		try{
			for(int k=0;k<5;k++){
				//������
				String apimethod="suning.custom.singlerejected.get";
				HashMap<String,String> reqMap = new HashMap<String,String>();
		        reqMap.put("orderCode", o.getOrderCode());
		        String ReqParams = CommHelper.getJsonStr(reqMap, "singleGetRejected");
		        HashMap<String,Object> map = new HashMap<String,Object>();
		        map.put("appSecret", appsecret);
		        map.put("appMethod", apimethod);
		        map.put("format", format);
		        map.put("versionNo", "v1.2");
		        map.put("appRequestTime", CommHelper.getNowTime());
		        map.put("appKey", appKey);
		        map.put("resparams", ReqParams);
		        //��������
		        Log.info("---test---");
				String responseText = CommHelper.doRequest(map,url);
				Log.info("�˻�����Ϣ: "+responseText);
				JSONObject responseObj = new JSONObject(responseText);
				 Log.info("---test33---");
				JSONArray returnItems = responseObj.getJSONObject("sn_responseContent").getJSONObject("sn_body").getJSONArray("singleGetRejected");
				Log.info("---test1---");
				JSONObject returnObj = returnItems.getJSONObject(0);
				ro = new ReturnOrder();
				ro.setObjValue(ro, returnObj);
				Log.info("---test2---");
				ArrayList<ReturnOrderItem> returnOrderItems = new ArrayList<ReturnOrderItem>();
				Log.info("---test3---");
				for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
				{
					OrderItem oi=(OrderItem) ito.next();
					ReturnOrderItem rotm = new ReturnOrderItem();
					rotm.setItemID(oi.getProductCode());
					rotm.setItemName(oi.getProductName());
					rotm.setItemSubhead(oi.getProductName());
					rotm.setOrderCount((int)oi.getSaleNum());
					rotm.setOuterItemID(oi.getItemCode());
					rotm.setUnitPrice(oi.getUnitPrice());
					returnOrderItems.add(rotm);
				}
				ro.setItemList(returnOrderItems);
				
			}
			return ro;
		}catch(Exception ex){
			Log.error("��ȡ�˻�����Ϣʧ��", ex.getMessage());
			return null;
		}
		
	}
	
	/**
	 * ��ȡ������˾����
	 * @param code
	 * @return
	 */
	public static String getExpressInfo(Connection conn,String code){
		String sql = new StringBuilder().append("select name from expressInfo where code='")
		.append(code).append("'").toString();
		try {
			return SQLHelper.strSelect(conn, sql);
		} catch (JSQLException e) {
			Log.info("��ѯ������˾�����Ӧ��������˾����!");
			return "";
		}
	}
}
