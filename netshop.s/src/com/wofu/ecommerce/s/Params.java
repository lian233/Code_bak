package com.wofu.ecommerce.s;

import java.util.Hashtable;
import java.util.Properties;

public class Params {
	public static String OrdExpress = null;
	public static String dbname = "";   
	public static String url = "http://gxtest.s.cn/api/fx";   //����API��ַ������Ҫ��
	public static String tradecontactid = "10";
	public static String encoding = "GBK";
	public static int total = 10;
	public static int waittime = 10;
	public static String sendMode = "";
	/*���ַ������ǲ������֪����ʲô�õ�*/
	public static String orderState = "";
	public static String username = "";
	public static String address = "";
	public static String zipcode = "";
	public static String phone = "";
	public static String mobile = "";
	public static String company = "" ;
	public static String companyTel = "" ;
	public static String codDeliveryBeginTime="" ;
	public static String codDeliveryEndTime="" ;
	public static int timeInterval = 30 ;
	public static Hashtable<String, String> htComCode = new Hashtable<String, String>() ;
	public static Hashtable<String, String> htComTel = new Hashtable<String, String>() ;

	
	public static String session="";
	public static String app_key = "gytest";  //��ȷ��//��Ь��Ҳ�õ�
	public static String app_Secret = "865";  //��ȷ��//��Ь��Ҳ�õ�
	public static int isDelay;
	public static int tableType;
	/***��Ь��API���в���****/
	public static String ver="2.0";
	public static String format="json";
	public static String Fields="seller_id, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair";
	public static String StartUpdateDate="2015-04-13 09:34:18";
	public static String EndUpdateDate="2015-04-13 09:34:18";
	public static String StartSubmitDate="2015-04-13 09:34:18";
	public static String EndSubmitDate="2015-04-13 09:34:18";
	public static String SellerId="scn";
	public static String SellerOrderNo="20150413002";
	public static String VendorOrderNo="100000004046";
	public static String OrderStatus="1";
	public static String PageNo;
	public static String PageSize;
		/**����**/
	public static String ShippingFee="0";
	public static String VendorMemo="��";
	public static String ExpressCompanyId="˳��";
	public static String ExpressNo="966808137833";
	public static String SkuQtyPair="TestSkuId001:3";
	
	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "");
		url=properties.getProperty("url","http://gxtest.s.cn/api/fx");
		tradecontactid=properties.getProperty("tradecontactid","10");
		encoding=properties.getProperty("encoding","GBK");
		session=properties.getProperty("session","");
		app_key=properties.getProperty("app_key","gytest"); //��Ь��Ҳ�õ�
		app_Secret=properties.getProperty("app_Secret","865");//��Ь��Ҳ�õ�
		total=Integer.parseInt(properties.getProperty("total","10"));
		waittime=Integer.parseInt(properties.getProperty("waittime","10"));
		sendMode=properties.getProperty("sendMode","9999");
		orderState=properties.getProperty("orderState","101");
		username=properties.getProperty("username","");
		address=properties.getProperty("address","");
		zipcode=properties.getProperty("zipcode","");
		phone=properties.getProperty("phone","");
		mobile=properties.getProperty("mobile","");
		company=properties.getProperty("company","");
		companyTel=properties.getProperty("company","EMS:11183;HTKY:021-62963636;POST:�й�����ƽ��;SF:4008111111;STO:400-889-5543;YTO:021-6977888/999");
		codDeliveryBeginTime=properties.getProperty("codDeliveryBeginTime","6:00:00");
		codDeliveryEndTime=properties.getProperty("codDeliveryEndTime","11:00:00");
		timeInterval=Integer.parseInt(properties.getProperty("timeInterval","30"));
		isDelay = Integer.valueOf(properties.getProperty("isDelay", "0"));
		tableType = Integer.valueOf(properties.getProperty("tableType", "0"));
		/***��Ь��API���в���****/
		format=properties.getProperty("format","json");
		ver=properties.getProperty("ver","2.0");
		Fields=properties.getProperty("Fields","seller_id, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express");
//		StartUpdateDate=properties.getProperty("StartUpdateDate","2015-04-13 09:34:18");
//		EndUpdateDate=properties.getProperty("EndUpdateDate","2015-04-13 09:34:18");
//		StartSubmitDate=properties.getProperty("StartSubmitDate","2015-04-13 09:34:18");
//		EndSubmitDate=properties.getProperty("EndSubmitDate","2015-04-13 09:34:18");
//		SellerId=properties.getProperty("SellerId","scn");
//		SellerOrderNo=properties.getProperty("SellerOrderNo","20150413002");
//		VendorOrderNo=properties.getProperty("VendorOrderNo","100000004048");
		
		StartUpdateDate=properties.getProperty("StartUpdateDate",null);
		EndUpdateDate=properties.getProperty("EndUpdateDate",null);
		StartSubmitDate=properties.getProperty("StartSubmitDate",null);
		EndSubmitDate=properties.getProperty("EndSubmitDate",null);
		SellerId=properties.getProperty("SellerId",null);
		SellerOrderNo=properties.getProperty("SellerOrderNo",null);
		VendorOrderNo=properties.getProperty("VendorOrderNo",null);
		
		OrderStatus=properties.getProperty("OrderStatus","1");
		PageNo=properties.getProperty("PageNo","1");
		PageSize=properties.getProperty("PageSize","50");
		ShippingFee=properties.getProperty("ShippingFee","0");
		VendorMemo=properties.getProperty("VendorMemo","�ޱ�ע");
		ExpressCompanyId=properties.getProperty("company",null); //!!!
		ExpressNo=properties.getProperty("VendorMemo",null);
		SkuQtyPair=properties.getProperty("SkuQtyPair",null);
		OrdExpress=properties.getProperty("OrdExpress",null);
		//��ȡ��Ӧ�Ŀ�ݹ�˾����
		String com[] = company.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			htComCode.put(s[0], s[1]) ;
		}
		//��ȡ��ݹ�˾�绰
		String comTel[] = companyTel.split(";") ;
		for(int j = 0 ; j < comTel.length ; j++)
		{
			String s[] = comTel[j].split(":") ;
			htComTel.put(s[0], s[1]) ;
		}
	}
}
