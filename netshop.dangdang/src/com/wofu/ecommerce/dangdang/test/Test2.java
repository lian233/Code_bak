package com.wofu.ecommerce.dangdang.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.wofu.ecommerce.dangdang.DeliveryOrderInfo;
import com.wofu.ecommerce.dangdang.DeliveryOrderInfoItem;
import com.wofu.ecommerce.dangdang.OperateInfo;
import com.wofu.ecommerce.dangdang.Order;
import com.wofu.ecommerce.dangdang.OrderItem;
import com.wofu.ecommerce.dangdang.OrderUtils;
import com.wofu.ecommerce.dangdang.Params;
import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.conv.Coded;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.common.tools.util.Formatter;

public class Test2 {

	private static String gShopID="8117";
	private static 	String key="nawain2012";
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	//private static String url = "http://switch.dangdang.com/v2" ;
	private static String url = "http://api.dangdang.com/v2" ;
	private static String encoding= "GBK";
	
	public static void main(String[] args) throws Exception {

		//getItemDetail();
		
		getSku();
		
	}
	
	public static void getItemDetail() throws Exception
	{
			String itemID="1033639206";
			String gbkValuesStr = Coded.getEncode(gShopID, encoding)
								  .concat(Coded.getEncode(itemID, encoding))
								  .concat(Coded.getEncode(key, encoding)) ;
			String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(url).append("/getItemDetail.php");
			
			Hashtable params=new Hashtable();
			params.put("gShopID", gShopID) ;
			params.put("it", itemID) ;
			params.put("validateString", validateString) ;
			

			String responseText = CommHelper.sendRequest(sb.toString(), "GET", params ,"") ;
			System.out.println(responseText) ;
			
			
	}
	
	public static void getSku() throws Exception
	{
			String sku="N42512A13";
			String gbkValuesStr = Coded.getEncode(gShopID, encoding)
								  .concat(Coded.getEncode(sku, encoding))
								  .concat(Coded.getEncode(key, encoding)) ;
			String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(url).append("/getItemID.php");
			
			Hashtable params=new Hashtable();
			params.put("gShopID", gShopID) ;
			params.put("oit", sku) ;
			params.put("validateString", validateString) ;
			

			String responseText = CommHelper.sendRequest(sb.toString(), "GET", params ,"") ;
			System.out.println(responseText) ;
			
			
	}
	
}
