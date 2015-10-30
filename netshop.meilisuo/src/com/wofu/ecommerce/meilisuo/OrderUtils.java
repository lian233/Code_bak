package com.wofu.ecommerce.meilisuo;
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
import com.wofu.ecommerce.meilisuo.util.CommHelper;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","�˻�","����",""} ;

	/**
	 * ��ȡ����������ϸ��Ϣ
	 * @param orderCode	������
	 * @return
	 */
	

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
			
			//������ϸ
			float totalPrice = 0.00f ;//�ܽ��
			float sellerDiscount = 0.0f ;//�̼����Żݽ��
			//ʵ���ܽ��
			float totalItemPayment=0.0f;
			//Ӧ���ܽ��
			float totalfee=0.0f;
			//���ʷ�
			float totalPostfee=0.0f;
			int j=0;
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				String itemPayment = String.valueOf(item.getPrice()*item.getAmount());
				sql = new StringBuilder().append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
                    .append(" title , sellernick , buyernick , type , created , ") 
                    .append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")
                    .append(" discountfee , adjustfee , status , timeoutactiontime , owner , ")
                    .append(" skuPropertiesName , num , price , picPath , " )
                    .append("modified) values( '")
                    .append(sheetid).append("','").append(sheetid).append("-").append(o.getOrder_id()).append(String.valueOf(++j))
                    .append("','").append(sheetid).append("','").append(item.getSku()).append("','','")
                    .append(item.getGoods_title()).append("','").append(username).append("','")
                    .append(o.getNickname()).append("','','")
                    .append(Formatter.format(o.getCtime(), Formatter.DATE_TIME_FORMAT)).append("','','").append(item.getGoods_no()).append("','")
                    .append(item.getSku()).append("','").append(itemPayment).append("','").append(itemPayment).append("','")
                    .append("','','").append(o.getStatus_text()).append("','','yongjun',").append("'','")
                    .append((int)item.getAmount()).append("','").append(item.getPrice()).append("','").append(item.getGoods_img())
                    .append("','").append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();
				//Log.info("ns_orderitem��SQL��䣺"+sql);    
        		SQLHelper.executeSQL(conn, sql) ;

			}
			//Log.info("ns_orderitemд����ϣ���ns_customerorder��");
			//���뵽���ݱ�
			sql =  new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append(" type , created , buyermessage , shippingtype , payment , ")  //11
				.append(" discountfee , adjustfee , status ,paytime,totalfee , postfee , buyeralipayno , ")//18
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receivermobile , ")
				.append(" tradefrom,TradeContactID,modified) values('")
				.append(sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrder_id()).append("','','").append(username)  //6
				.append("','','").append(Formatter.format(o.getCtime(),Formatter.DATE_TIME_FORMAT)).append("','','','").append(o.getTotal_price()).append("','").append(sellerDiscount).append("','','")  //13
				.append(new String(o.getStatus_text().getBytes(),"GBK")).append("','").append(Formatter.format(o.getCtime(),Formatter.DATE_TIME_FORMAT)).append("','").append(o.getTotal_price()).append("','").append(o.getExpress_price()).append("','','").append(o.getBuyer_nickname()).append("','','")
				.append(o.getNickname()).append("','").append(o.getProvince()).append("','").append(o.getCity()).append("','").append(o.getDistrict())
				.append("','").append(o.getStreet().replaceAll("'", "")).append("','").append(o.getPhone()).append("',")
                .append("'MEILISUO','").append(tradeContactID).append("','").append(Formatter.format(o.getCtime(), Formatter.DATE_TIME_FORMAT)).append("')").toString();
			//Log.info("ns_customerorder��SQL��䣺"+sql);

			SQLHelper.executeSQL(conn, sql);
			//���뵽֪ͨ��
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrder_id() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");

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
			throw new JException("���ɶ�����" + o.getOrder_id() + "���ӿ�����ʧ��!"
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
	/*public static void createRefundOrder(String jobname,Connection conn,String tradecontactid,
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
	                    .append("'','").append(order.getCustomerAddress().replaceAll("'",",'','")).append(inshopid).append("','").append(order.getOrderCode()).append("'").append(order.getCustomerName()).append("','").append(order.getMobNum()).append("','')").toString();

					Log.info("�˻���sql: "+sql) ;
					System.out.println("--");
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
	}*/
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
				String responseText = CommHelper.doRequest(map,Params.url,"get");
				//Log.info("��ȡ��Ʒsku�����ȷ��ؽ��:��"+responseText);
				JSONObject item= new JSONObject(responseText).getJSONObject("sn_responseContent").getJSONObject("sn_body").getJSONObject("item");
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
	
	//������ɫ�����ţ������ѯsku
	public static String getItemCodeByColSizeCustom(Connection conn,String goods_no,
			String color, String size) {
		String result="";
		String sql = new StringBuilder().append("select custombc from v_barcodeAll where colorname='")
			.append(color).append("' and customno='").append(goods_no)
			.append("' and sizename='").append(size).append("'").toString();
		try{
			Log.info("��sku:��"+sql);
			result = SQLHelper.strSelect(conn, sql);
		}catch(Exception ex){
			Log.error("��ѯ��Ʒsku����", ex.getMessage());
		}
		return result;
	}
}
