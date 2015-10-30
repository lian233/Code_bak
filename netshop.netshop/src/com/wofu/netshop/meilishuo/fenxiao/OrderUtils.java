package com.wofu.netshop.meilishuo.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import com.wofu.base.util.DataRelation;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","�˻�","����",""} ;
	/**
	 * �����ӿڶ���
	 * @param conn
	 * @param o
	 * @param tradeContactID
	 * @param username
	 * @return
	 * @throws SQLException 
	 */
	public static void createInterOrder(Connection conn,Order o,int shopid,String username,int orderstatus) throws Exception
	{		
		try 
		{
			conn.setAutoCommit(false);		
			
			int sheetid=0;
			String sql="declare @Value int;exec TL_GetNewSerial_new 100001,@value output;select @value;";
			sheetid=SQLHelper.intSelect(conn, sql);
			if (sheetid==0)
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
				DataRelation pro = item.getProp();
				StringBuilder proties = new StringBuilder();
				for(Iterator it = pro.getRelationData().iterator();it.hasNext();){
					Prop pr = (Prop)it.next();
					proties.append(pr.getName()).append(":").append(pr.getValue()).append(" ");
				}
				String itemPayment = String.valueOf(item.getPrice()*item.getAmount());
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
				.append(item.getSku()).append("','").append(item.getGoods_title())
				.append("','").append(item.getGoods_title())
				.append("','").append(username)
				.append("','").append(username)
				.append("','")
				.append("','").append(Formatter.format(o.getCtime(),Formatter.DATE_TIME_FORMAT))
				//0û���˿10����Ѿ������˿�ȴ�����ͬ�⡣20�����Ѿ�ͬ���˿�ȴ�����˻���30����Ѿ��˻����ȴ�����ȷ���ջ���40���Ҿܾ��˿90�˿�رա�100�˿�ɹ���
				.append("',0")
				.append(",'").append(item.getGoods_no())
				.append("','").append(item.getSku())
				.append("','").append(itemPayment)
				.append("','").append(itemPayment)
				.append("','")
				.append("','")
				.append("','")
				.append("','")//timeoutactiontime   ������ʱ����ʱ����ʱΪ��
				.append("','")//iidҲΪ��
				.append("','").append(proties.toString())//��Ʒ����  �̼ҷ���ʱʶ����Ʒ��
				.append("',").append(item.getAmount())
				.append(",'").append(item.getPrice())
				.append("','").append(item.getGoods_img())//��ƷͼƬ  ��ҳ����ʾ��ƷͼƬ��
				.append("','")
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
			//Log.info("ns_orderitemд����ϣ���ns_customerorder��");
			//���뵽���ݱ�
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
			.append(sheetid).append(",").append(shopid).append(",'").append(o.getOrder_id())
			.append("','").append(username).append("','")
			.append("','").append(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append(Formatter.format(o.getCtime()!=null?o.getCtime():new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append("','")
			.append("','").append(o.getTotal_price()).append("','")
			.append(sellerDiscount).append("','0.0','").append(orderstatus)//20������
			.append("','','','")
			.append("','").append(Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append("','").append(Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT))
			.append("','','','','")
			.append(o.getTotal_price()).append("','")
			.append(o.getExpress_price()).append("','")
			.append("','").append(o.getBuyer_nickname())
			.append("','").append(o.getNickname())
			.append("','").append(o.getProvince())
			.append("','").append(o.getCity())
			.append("','").append(o.getDistrict())
			.append("','").append(o.getStreet().replaceAll("'", ""))
			.append("','")
			.append("','").append( o.getPhone())
			.append("','")
			.append("','")
			.append("','")
			.append("','")
			.append("','").append(o.getTotal_price())
			.append("','0','")
			//.append(delivery)  �������
			.append("','")//cod״̬�Ϳ����������
			.append("','")
			.append("','")
			.append("','','',")//dealRateState��������״̬��ʱΪ��
			.append("0,'','','")//InvoiceFlag=0   invoicetitle="";  Prepay=''
			.append("','")
			.append("','")
			.append("','").append("MEILISHUO")
			.append("','")
			.append("','")
			.append("','").append(1).append("')").toString();
			SQLHelper.executeSQL(conn, sql) ;
			
			// ���뵽֪ͨ��
			sql = new StringBuilder().append("insert into inf_downnote(sheettype,notetime,opertype,operdata,flag,owner)")
			.append("values(1,getdate(),100,'")
			.append(sheetid).append("',0,'')").toString();
			SQLHelper.executeSQL(conn,sql);
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrder_id() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");

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
	public static void createRefundOrder(String jobname,Connection conn,String tradecontactid,
			Order order,String url,String appKey,String appsecret)
	{
		
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
			return null;
		
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
