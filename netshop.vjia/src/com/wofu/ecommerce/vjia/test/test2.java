package com.wofu.ecommerce.vjia.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.ecommerce.vjia.CryptoTools;
import com.wofu.ecommerce.vjia.Params;
import com.wofu.ecommerce.vjia.SoapBody;
import com.wofu.ecommerce.vjia.SoapHeader;
import com.wofu.ecommerce.vjia.SoapServiceClient;

public class test2 {

	private static final String strkey = "beibeiyi";
	private static final String striv = "VjiaInfo";
	private static final String passWord = "28HIZHCEwmbWk5q5+I4rGfIPe5oi6eKi";
	private static final String userName = "KGsDb8/aa5p2+D0yfmTPXw==";
	private static final String swsSupplierID = "beibeiyi";
	private static final String wsurl = "http://sws2.vjia.com/swsms";
	private static final String URI = "http://swsms.vjia.org/";
	

	public static void main(String[] args) throws Exception {
		//getOrderList();
		//getGoodsList();
		te();
		
		///getBarcdoeListByOrderID("223072543209");
		//getBarcdoeListByOrderID("223122836805");
		//getGoodByCode();
	}
	
	private static void getGoodsList() throws Exception {
		File file= new File("c:\\rr.txt");
	    FileOutputStream os = new FileOutputStream(file);
	    PrintStream ps = new PrintStream(os);
		for(int i=1;i<=143;i++){
			SoapHeader soapHeader = new SoapHeader() ;
			
			soapHeader.setUname(userName) ;
			soapHeader.setPassword(passWord) ;
			soapHeader.setUri(URI) ;
			
			Hashtable<String, String> bodyParams = new Hashtable<String, String>();
			bodyParams.put("swsSupplierID", swsSupplierID) ;
			bodyParams.put("page", String.valueOf(i));
			bodyParams.put("pageSize", "20");
			bodyParams.put("status", "all");
			bodyParams.put("isNew", "all");
			bodyParams.put("startTime", "2014-01-08 19:42:49");
			bodyParams.put("endTime", "2014-02-08 19:25:49");

			
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("GetProductInfo") ;
			soapBody.setUri(URI) ;
			
			soapBody.setBodyParams(bodyParams);

			SoapServiceClient client = new SoapServiceClient();
			client.setUrl(wsurl + "/GetProductInfoService.asmx");
			client.setSoapbody(soapBody);
			client.setSoapheader(soapHeader);

			String result = client.request();

			System.out.println(result);
			
			Document resultdoc=DOMHelper.newDocument(result);
		    Element resultelement=resultdoc.getDocumentElement();
		    
		    
		    Element productList=(Element) resultelement.getElementsByTagName("productlist").item(0);
		    NodeList products = productList.getElementsByTagName("product");
		    
		    for (int n=0;n<products.getLength();n++)
		   {
			   Element product = (Element) products.item(n);
			   
			   String barcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "barcode"), strkey, striv) ;
			   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "sku"), strkey, striv) ;
			   String productcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "productcode"), strkey, striv) ;
			   String developid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "developid"), strkey, striv) ;
			   String productname = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "productname"), strkey, striv) ;
			   String color = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "color"), strkey, striv) ;
			   String size = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "size"), strkey, striv) ;
			   String fororder = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "fororder"), strkey, striv) ;
			   
			   String onsale = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "onsale"), strkey, striv) ;
			       ps.print(barcode+":"+sku+":"+productcode+":"+developid+":"+productname+":"+fororder+":"+size+"\n");
				  // System.out.println(barcode);
				  // System.out.println(sku);
				  // System.out.println(productcode);
				   //System.out.println(developid);
				   //System.out.println(productname);

				   //System.out.println(fororder);
				   //System.out.println(size);
			   
			  

		   }
		}
		ps.close();
		
		
	}

	private static void getOrderList() throws Exception {

		SoapHeader soapheader = new SoapHeader();
		soapheader.setPassword(passWord);
		soapheader.setUname(userName);
		soapheader.setUri(URI);

		SoapBody soapbody = new SoapBody();
		soapbody.setRequestname("GetOrderByTime");
		soapbody.setUri(URI);

		Hashtable<String, String> bodyparams = new Hashtable<String, String>();
		bodyparams.put("swsSupplierID", swsSupplierID);
		bodyparams.put("page", "1");
		bodyparams.put("pageSize", "10");
		bodyparams.put("startTime", "2013-10-25 00:00:00");
		bodyparams.put("endTime", "2013-11-07 00:00:00");		
		bodyparams.put("status", "ALL");
		bodyparams.put("sort", "0");
		bodyparams.put("orderCode", "");
		bodyparams.put("addressee", "");
		bodyparams.put("phone", "");
		
		soapbody.setBodyParams(bodyparams);

		SoapServiceClient client = new SoapServiceClient();
		client.setUrl(wsurl + "/GetOrderService.asmx");
		client.setSoapbody(soapbody);
		client.setSoapheader(soapheader);

		String result = client.request();

		
	   Document resultdoc=DOMHelper.newDocument(result);
	   Element resultelement=resultdoc.getDocumentElement();
	   
	   Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
	   NodeList orderList = resultdetail.getElementsByTagName("order") ;
	   
	   for (int i=0;i<orderList.getLength();i++)
	   {

		   Element order=(Element) orderList.item(i);
		   String orderid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderid"), strkey, striv).trim() ;
		  // String orderdistributetime = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderdistributetime"), strkey, striv) ;
		  //String username = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "username"), strkey, striv) ;
		   //String usertel = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "usertel"), strkey, striv) ;
		  // String userphone = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "userphone"), strkey, striv) ;
		   
		   String orderstatus = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderstatus"), strkey, striv) ;
		   
		   System.out.println(orderid+" "+orderstatus);
	   }
	}
	
	
	private static void getBarcdoeListByOrderID(String orderID) throws Exception
	{
	
			Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
			bodyParams.put("swsSupplierID", swsSupplierID) ;
			bodyParams.put("DESFormCode", DesUtil.DesEncode(orderID, strkey, striv)) ;
			
			SoapHeader soapHeader = new SoapHeader() ;
			soapHeader.setUname(userName) ;
			soapHeader.setPassword(passWord) ;
			soapHeader.setUri(URI) ;
			
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("GetFormCodeInfo") ;
			soapBody.setUri(URI) ;
			soapBody.setBodyParams(bodyParams) ;
			
			SoapServiceClient client = new SoapServiceClient() ;
			client.setUrl(Params.wsurl+"/GetOrderService.asmx") ;
			client.setSoapheader(soapHeader) ;
			client.setSoapbody(soapBody) ;
			
			String result = client.request() ;
			System.out.println(new CryptoTools().decode("8ldBsYB5R8g="));
			
			/*Document resultdoc=DOMHelper.newDocument(result);
			Element resultelement=resultdoc.getDocumentElement();
			Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
			Element order =(Element) resultdetail.getElementsByTagName("order").item(0) ;
			
			System.out.println(DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderstatus"), strkey, striv));
			System.out.println(DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderdistributetime"), strkey, striv));
			System.out.println(DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "username"), strkey, striv));
			*/
		
	}
	
	
	private static void getGoodByCode() throws Exception {
		SoapHeader soapheader = new SoapHeader();
		soapheader.setPassword(passWord);
		soapheader.setUname(userName);
		soapheader.setUri(URI);

		SoapBody soapbody = new SoapBody();
		soapbody.setRequestname("GetProductInfoByBarcode");
		soapbody.setUri(URI);

		Hashtable<String, String> bodyparams = new Hashtable<String, String>();
		bodyparams.put("barCode", "10337044");
		bodyparams.put("swsSupplierID", swsSupplierID);
		bodyparams.put("pageSize", "10");
		bodyparams.put("page", "1");
		
		
		soapbody.setBodyParams(bodyparams);

		SoapServiceClient client = new SoapServiceClient();
		client.setUrl(wsurl + "/GetProductInfoService.asmx");
		client.setSoapbody(soapbody);
		client.setSoapheader(soapheader);

		String result = client.request();
		System.out.println(result);
	}
	
	public static void te(){
		File file= new File("c:\\rr2.txt");
	    FileOutputStream os=null;
		try {
			os = new FileOutputStream(file);
			PrintStream ps = new PrintStream(os);
			String result="<result><resultcode>0</resultcode><resultmessage>全部成功</resultmessage><swssupplierid>beibeiyi</swssupplierid><resultdetail><allinfonum>19</allinfonum><allpagenum>2</allpagenum><nowpage>2</nowpage><productlist><product><barcode>D6OhXH3SfQbWxodNO4VP6Q==</barcode><sku>Pj+2i7zIkeKY9gwmp0t/8g==</sku><productcode>tDgq1Em9iII=</productcode><developid>/8BcBA2jqiQ=</developid><productname>jJs9/AgMWOutIfrf2xw3yafEnOqQUY4TPtNOcm5kjbrDnt1Hip27jTP2uIOqeOu43+/q+5aIKJK4D3c8aNQBCUeq+fu1yBlx9cFJidLJChAfoHCnyNFZtw==</productname><color>FoeE9rd0dPs=</color><size>pNB+2GuDlRNweobxsBncWRa2hRGWI4nF</size><fororder>FWLBlYuoGeY=</fororder><onsale>zD4VI1TQLC0=</onsale></product><product><barcode>D6OhXH3SfQbAk8ZKcOUjJw==</barcode><sku>D+EIQzhqozVOAv8KEQQ7sA==</sku><productcode>kcYzrsfWQ/c=</productcode><developid>/8BcBA2jqiQ=</developid><productname>jJs9/AgMWOutIfrf2xw3yafEnOqQUY4TPtNOcm5kjbrDnt1Hip27jTP2uIOqeOu43+/q+5aIKJK4D3c8aNQBCUeq+fu1yBlx9cFJidLJChAeAdC97iHMEA==</productname><color>P7J3jwbYhvA=</color><size>7z0THBB5SQD/LJGzTKS2F3d9U8Gu4lkD</size><fororder>FWLBlYuoGeY=</fororder><onsale>zD4VI1TQLC0=</onsale></product><product><barcode>D6OhXH3SfQZDJ4BKbVgzeA==</barcode><sku>oRu4INLn30OGgn3WkJQQGQ==</sku><productcode>kcYzrsfWQ/c=</productcode><developid>/8BcBA2jqiQ=</developid><productname>jJs9/AgMWOutIfrf2xw3yafEnOqQUY4TPtNOcm5kjbrDnt1Hip27jTP2uIOqeOu43+/q+5aIKJK4D3c8aNQBCUeq+fu1yBlx9cFJidLJChAeAdC97iHMEA==</productname><color>P7J3jwbYhvA=</color><size>pNB+2GuDlRNweobxsBncWRa2hRGWI4nF</size><fororder>FWLBlYuoGeY=</fororder><onsale>zD4VI1TQLC0=</onsale></product><product><barcode>D6OhXH3SfQZ4zB8teNuS8Q==</barcode><sku>g6k12r2OkUgyp/WXgOAXOg==</sku><productcode>jmM3tG5W7+A=</productcode><developid>/8BcBA2jqiQ=</developid><productname>jJs9/AgMWOutIfrf2xw3yafEnOqQUY4TPtNOcm5kjbrDnt1Hip27jTP2uIOqeOu43+/q+5aIKJK4D3c8aNQBCUeq+fu1yBlx9cFJidLJChB/W4s81lto7A==</productname><color>x+GuFkb/oJY=</color><size>7z0THBB5SQD/LJGzTKS2F3d9U8Gu4lkD</size><fororder>FWLBlYuoGeY=</fororder><onsale>zD4VI1TQLC0=</onsale></product><product><barcode>D6OhXH3SfQaeyx85briDFQ==</barcode><sku>6m16SPcjLOqO+3JlsMzpoA==</sku><productcode>jmM3tG5W7+A=</productcode><developid>/8BcBA2jqiQ=</developid><productname>jJs9/AgMWOutIfrf2xw3yafEnOqQUY4TPtNOcm5kjbrDnt1Hip27jTP2uIOqeOu43+/q+5aIKJK4D3c8aNQBCUeq+fu1yBlx9cFJidLJChB/W4s81lto7A==</productname><color>x+GuFkb/oJY=</color><size>pNB+2GuDlRNweobxsBncWRa2hRGWI4nF</size><fororder>FWLBlYuoGeY=</fororder><onsale>zD4VI1TQLC0=</onsale></product><product><barcode>4ZMPlglYtMStv8e+ieeFww==</barcode><sku>5hFXtsr1UKJyCwTOItD53w==</sku><productcode>3W1/jjjXefc=</productcode><developid>FSYoC+OrvBs=</developid><productname>jJs9/AgMWOutIfrf2xw3yZ5a/lNVCzktLPpEUz6800Fmqh2Hs4D0ZDCrFVioWqjqls6IhbNXyl4D8LE/JdT+xv0GZTGgNSkjOhJxx556b6NmzSSYETZW5owDHQFx1bMC4TzZvktEmWKmtWMLPMXovSnvlhHxew6o</productname><color>k1d8ZZQw22vY8PY2hwElpFuh2/jdsEWuAhtmVoWAYEU=</color><size>zU9FJ2MU2Eo=</size><fororder>oXRc/Gpp/Mk=</fororder><onsale>N+/0VPJVQ9o=</onsale></product><product><barcode>3OyETlqZotGg3nwSNZAyhg==</barcode><sku>b/1MA74p4CVaZ5qoZYVEyA==</sku><productcode>qcsYlr5iYWY=</productcode><developid>5fFJgtoi30E=</developid><productname>jJs9/AgMWOv4AJNUKDYNvw6ulBZb4IV95rcpNLcaW+iy9mi2UjbOOD93bF2MQNYcBXeCBHTyn2sM4EEpazYZSA==</productname><color>7h3B5QGBMjk0nqmPJQGQ7g==</color><size>zU9FJ2MU2Eo=</size><fororder>XW8UPZG5QJI=</fororder><onsale>zD4VI1TQLC0=</onsale></product><product><barcode>+3WJAtMSz2TzILqajY/Crw==</barcode><sku>0eK8BgYpxklywQb10tdLkA==</sku><productcode>NG6EEUT+jiM=</productcode><developid>5fFJgtoi30E=</developid><productname>jJs9/AgMWOv4AJNUKDYNvw6ulBZb4IV95rcpNLcaW+iy9mi2UjbOOD93bF2MQNYcBXeCBHTyn2v7tLT8pT3W2g==</productname><color>7h3B5QGBMjkSANw/Uxm3Fw==</color><size>zU9FJ2MU2Eo=</size><fororder>XW8UPZG5QJI=</fororder><onsale>zD4VI1TQLC0=</onsale></product><product><barcode>h9YxW3D0inqq1F7U2Ol0fg==</barcode><sku>q+5fz7PN2c4DcB4dww1xeg==</sku><productcode>58k48ELsk/k=</productcode><developid>5fFJgtoi30E=</developid><productname>jJs9/AgMWOv4AJNUKDYNvw6ulBZb4IV95rcpNLcaW+iy9mi2UjbOOD93bF2MQNYcycBNFaExjWY7ecR/kPezCg==</productname><color>k1d8ZZQw22t/ES/v5bcd2w==</color><size>zU9FJ2MU2Eo=</size><fororder>2iRBrXZpxho=</fororder><onsale>zD4VI1TQLC0=</onsale></product></productlist></resultdetail></result>";
			Document resultdoc=null;
			
			resultdoc = DOMHelper.newDocument(result);
			
		    Element resultelement=resultdoc.getDocumentElement();
		    
		    
		    Element productList=(Element) resultelement.getElementsByTagName("productlist").item(0);
		    NodeList products = productList.getElementsByTagName("product");
		    
		    for (int n=0;n<products.getLength();n++)
		   {
			   Element product = (Element) products.item(n);
			   
			   String barcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "barcode"), strkey, striv) ;
			   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "sku"), strkey, striv) ;
			   String productcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "productcode"), strkey, striv) ;
			   String developid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "developid"), strkey, striv) ;
			   String productname = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "productname"), strkey, striv) ;
			   String color = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "color"), strkey, striv) ;
			   String size = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "size"), strkey, striv) ;
			   String fororder = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "fororder"), strkey, striv) ;
			   
			   String onsale = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "onsale"), strkey, striv) ;
			       ps.print(barcode+":"+sku+":"+productcode+":"+developid+":"+productname+":"+fororder+":"+size+"\n");
				  // System.out.println(barcode);
				  // System.out.println(sku);
				  // System.out.println(productcode);
				   //System.out.println(developid);
				   //System.out.println(productname);

				   //System.out.println(fororder);
				   //System.out.println(size);
			   
			  

		   }
		    ps.close();
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	    
	
	
	
	

}
