package com.wofu.ecommerce.vjia;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {

	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private static long daymillis=24*60*60*1000L;

	
	//�����ӿڶ���
	public static String createInterOrder(Connection conn,Order o,String tradecontactid,String username) throws JException, SQLException
	{
		//���������PayFee = totalfee - discountfee + postfee + PayFee - Prepay
		
		String sheetid="";
		try {
			conn.setAutoCommit(false);
			
			Log.info("payment: '"+(o.getTotalprice()+o.getTransferprice())+"'");
			String sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			
			sheetid=SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
			
			 //���뵽���ݱ�
            sql = "insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , " 
            	+ " type , created , buyermessage , shippingtype , payment , "  //11
				+ " discountfee , adjustfee , status , buyermemo , sellermemo , "
				+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
				+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
				+ " buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , "
				+ " receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , "
				+ " buyeremail , commissionfee , availableconfirmfee , haspostFee , receivedpayment , "
				+ " codfee , codstatus , timeoutactiontime , delivery , deliverySheetID , "
				+ " alipayNo , buyerflag , sellerflag,price , num , title , snapshoturl , snapshot , "
				+ " sellerrate,buyerrate,dealRateState,numiid,promotion,tradefrom,alipayurl,PromotionDetails,TradeContactID,paymode,InvoiceFlag,Prepay) values(" 
                + "'" + sheetid + "','" + sheetid + "','yongjun','" + o.getOrderid() + "','','" + username + "'," //6   //
                + "'','" + Formatter.format(o.getOrderdistributetime(), Formatter.DATE_TIME_FORMAT) + "','" + o.getReceivetime() + "','','" + (o.getTotalprice()+o.getTransferprice()) + "',"
                + "'0.0','0.0','"+o.getOrderstatus()+"','" + o.getComment() + "',''," 
                + "'','" + Formatter.format(o.getOrderdistributetime(), Formatter.DATE_TIME_FORMAT) + "','','" + Formatter.format(o.getOrderdistributetime(), Formatter.DATE_TIME_FORMAT) + "','',"
                + "'','','" + o.getTotalprice() + "','" + o.getTransferprice() + "','',"
                + "'" + o.getUsername().replace("'", "''") + "','','" + o.getUsername().replace("'", "''") + "','','','',"
                + "'" + o.getAddress() + "','" + o.getPostalcode() + "','" + o.getUserphone() + "','" + o.getUsertel() + "',''," 
            	+ "'','','','','',"
            	+ "'','','','','',"
            	+ "'','0','','','','','','',"
            	+ "'','','','','','vjia','','','" + tradecontactid+ "','" + o.getPayMode() + "','" + o.getNeedinvoice() +"','"+o.getPaidprice()+"')";
            SQLHelper.executeSQL(conn, sql);
        	OrderItem oi ;
        	for(int i = 0 ; i < o.getOrderitems().size() ; i++)
        	{
        		oi = (OrderItem) o.getOrderitems().get(i) ;
        		sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , "   //5
                    + " title , sellernick , buyernick , type , created , " 
                    + " refundstatus , outeriid , outerskuid , totalfee , payment , "   //15
                    + " discountfee , adjustfee , status , timeoutactiontime , owner , "   //20
                    + " iid , skuPropertiesName , num , price , picPath , " 
                    + " oid , snapShotUrl , snapShot ,modified) values( " 
                    + "'" + sheetid + "','"  + sheetid+"-"+o.getOrderid() + String.valueOf(i+1) + "','" + sheetid + "','" + oi.getSku() + "','" + oi.getProductname() +"',"  //5
                    + "'" + oi.getProductname() + "','','" + o.getUsername().replace("'", "''") + "','','" + Formatter.format(o.getOrderdistributetime(),Formatter.DATE_TIME_FORMAT) + "',"  //10
                    + "'','','" + oi.getBarcode() + "','" + oi.getAmount() + "','" + oi.getAmount() + "',"  //15
                    + "'','','"+o.getOrderstatus()+"','','yongjun',"
                    + "'','','" + oi.getQty() + "','" + oi.getPrice() + "','',"
                    + "'','','','" + Formatter.format(o.getOrderdistributetime(),Formatter.DATE_TIME_FORMAT) + "')";

        		SQLHelper.executeSQL(conn, sql) ;
        		if("1".equals(o.getNeedinvoice()))
        		{
        			//���ӷ�Ʊ��ϸ
        			int qty = Integer.parseInt(oi.getQty()) ;
        			float unitPrice = Float.parseFloat(oi.getPrice()) ;
        			String itemName = getInvoiceDetail("��ȡ��Ʒ�������", conn, oi.getBarcode()) ;
        			String unitName = getGoodsUnitName("��ȡ��Ʒ��λ", conn, oi.getBarcode()) ;
        			
        			if(!"".equals(itemName) && itemName != null)
        			{
        				//��������Ʒ��ͬ�۸񣬷�ֹ��Ʊ��ϸ��������¼�ظ�
        				sql = "select count(*) from ns_invoiceItem with(nolock) where sheetid='"+ sheetid +"' and tid='"+ o.getOrderid() +"' and name='"+ itemName +"'" ;
            			if(SQLHelper.intSelect(conn, sql) > 0)
            				itemName = itemName + String.valueOf(i) ;
        				
        				sql = "insert into ns_InvoiceItem(SheetID,tid,InvoiceTitle,Name,Unit," 
    	    				+ "Qty,Price,Amount,Note) values(" 
    	    				+ "'" + sheetid + "','"+ o.getOrderid() + "','" + o.getInvoiceTitle() + "','" + itemName + "','"+ unitName +"'," 
    	    				+ "'" + oi.getQty() + "','" + unitPrice + "','" + (qty*unitPrice) + "','')" ;
        				SQLHelper.executeSQL(conn, sql);
        			}
        			else
        			{
        				//ȡ����������Ʒ��Ϣ,���ӷ�Ʊ��Ϣ
        				sql = "select customerCode,PriceRatio from multiskuref with(nolock) where refcustomercode='"+ oi.getBarcode() +"'" ;
        				Vector multisku = SQLHelper.multiRowSelect(conn, sql) ;
        				for(int j = 0 ; j < multisku.size() ; j++)
        				{
        					Hashtable skuinfo = (Hashtable) multisku.get(j) ;
        					String sku = skuinfo.get("customerCode").toString() ;
        					float priceRatio = Float.parseFloat(String.valueOf(skuinfo.get("PriceRatio"))) ;
        					itemName = getInvoiceDetail("��ȡ��Ʒ�������", conn, sku) ;
                			unitName = getGoodsUnitName("��ȡ��Ʒ��λ", conn, sku) ;
                			
                			//��������Ʒ��ͬ�۸񣬷�ֹ��Ʊ��ϸ��������¼�ظ�
            				sql = "select count(*) from ns_invoiceItem with(nolock) where sheetid='"+ sheetid +"' and tid='"+ o.getOrderid() +"' and name='"+ itemName +"'" ;
                			if(SQLHelper.intSelect(conn, sql) > 0)
                				itemName = itemName + String.valueOf(i) ;
                			
                			sql = "insert into ns_InvoiceItem(SheetID,tid,InvoiceTitle,Name,Unit," 
        	    				+ "Qty,Price,Amount,Note) values(" 
        	    				+ "'" + sheetid + "','"+ o.getOrderid() + "','" + o.getInvoiceTitle() + "','" + itemName + "','"+ unitName +"'," 
        	    				+ "'" + oi.getQty() + "','" + (unitPrice*priceRatio) + "','" + (qty*unitPrice)*priceRatio + "','')" ;
                			SQLHelper.executeSQL(conn, sql);
        				}
        			}
        		}
        	}
        	
        	 //���뵽֪ͨ��
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				

            SQLHelper.executeSQL(conn, sql);
			
        	conn.commit();
			conn.setAutoCommit(true);

			Log.info("���ɶ�����" + o.getOrderid() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");            				
		
			
		}
		catch (Exception e)
		{
			if (!conn.getAutoCommit())
				try
				{
					conn.rollback();
				}
				catch (Exception e1) { }
			try
			{
				conn.setAutoCommit(true);
			}
			catch (Exception e2) { }
		}
		
		return sheetid ;
	}

/*	//������Ʊ
	public static void getInvoiceInfoByOrderID(Connection conn, String sheetID, String orderID,Hashtable<String, String> params) throws Exception
	{	
		String passWord = params.get("passWord") ;
		String userName = params.get("userName") ;
		String URI = params.get("URI") ;
		String swsSupplierID = params.get("swsSupplierID") ;
		String wsurl = params.get("wsurl") ;
		String strkey = params.get("strkey") ;
		String striv = params.get("striv") ;
		
		String desOrderID = DesUtil.DesEncode(orderID, strkey, striv) ;
		SoapHeader soapHeader = new SoapHeader() ;
		soapHeader.setUname(userName) ;
		soapHeader.setPassword(passWord) ;
		soapHeader.setUri(URI) ;
		
		SoapBody soapBody = new SoapBody() ;
		soapBody.setRequestname("GetInvoiceInfo") ;
		soapBody.setUri(URI) ;
		
		Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
		bodyParams.put("swsSupplierID", swsSupplierID);
		bodyParams.put("DECformCode", desOrderID) ;
		
		soapBody.setBodyParams(bodyParams) ;
		
		SoapServiceClient client = new SoapServiceClient() ;
		client.setUrl(wsurl+"/GetInvoiceInfoService.asmx") ;
		client.setSoapheader(soapHeader) ;
		client.setSoapbody(soapBody) ;
		
		String result = client.request() ;
		
		Document resultdoc=DOMHelper.newDocument(result);
	    Element resultelement=resultdoc.getDocumentElement();
	    Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
	    String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
	    if(!"0".equals(resultcode))
		   {
			   Log.info("��ȡ��Ʊ��Ϣʧ�ܣ����š�" + orderID + "��" + DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim()) ;
			   return ;
		   }
	    try
	    {
	    	Element[] allinvoice = DOMHelper.getSubElementsByName(resultdetail, "invoice") ;
	    	//ȡ��ÿ�ŷ�Ʊ
	    	for (int i=0;i<allinvoice.length;i++)
		    {
	    		//��Ʊ̧ͷ
	    		String invoiceTitle = DOMHelper.getSubElementVauleByName(allinvoice[i], "invoicetitle");
	    		//ȡ�ö�����ϸ	
	    		Element[] invoiceDetail = DOMHelper.getSubElementsByName(allinvoice[i], "invoicedetail") ;
	    		for(int j = 0 ; j < invoiceDetail.length ; j++)
	    		{
	    			Element invoice=(Element) invoiceDetail[j] ;	    
			    	String name = DOMHelper.getSubElementVauleByName(invoice, "name");
			    	String unit = DOMHelper.getSubElementVauleByName(invoice, "unit");
			    	String qty = DOMHelper.getSubElementVauleByName(invoice, "qty") ;
			    	String unitprice = DOMHelper.getSubElementVauleByName(invoice, "unitprice") ;
			    	String price = DOMHelper.getSubElementVauleByName(invoice, "price") ;
			    	
			    	if("".equals(qty))
			    		qty="0" ;
			    	if("".equals(unitprice))
			    		unitprice="0" ;
			    	if("".equals(price))
			    		price="0" ;	
			    	String  sql = "insert into ns_InvoiceItem(SheetID,tid,InvoiceTitle,Name,Unit," 
	    				+ "Qty,Price,Amount,Note) values(" 
	    				+ "'" + sheetID + "','"+ orderID + "','" + invoiceTitle + "','" + name + "','" + unit + "'," 
	    				+ "'" + qty + "','" + unitprice + "','" + price + "','')" ;
			    	//System.out.println("sql = " + sql) ;
			    	SQLHelper.executeSQL(conn, sql);
	    		}
		    	
		    }

		} catch (Exception e) {
			// TODO: handle exception
			Log.info("���ɶ�����Ʊ��" + orderID + "���ӿ�����ʧ�ܣ��ӿڵ��š�" + sheetID + "��" + e.getMessage());  
			e.printStackTrace() ;
		}
		
		
	}*/
	
	public static String getInvoiceTitle(String jobname,String orderID,String URI,String wsurl,String supplierid,
			String passWord,String swsSupplierID,String strkey,String striv)
	{
	
		String title = "" ;
		try
		{
			String desOrderID = DesUtil.DesEncode(orderID, strkey, striv) ;
			SoapHeader soapHeader = new SoapHeader() ;
			soapHeader.setUname(supplierid) ;
			soapHeader.setPassword(passWord) ;
			soapHeader.setUri(URI) ;
			
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("GetInvoiceInfo") ;
			soapBody.setUri(URI) ;
			
			Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
			bodyParams.put("swsSupplierID", swsSupplierID);
			bodyParams.put("DECformCode", desOrderID) ;
			
			soapBody.setBodyParams(bodyParams) ;
			
			SoapServiceClient client = new SoapServiceClient() ;
			client.setUrl(wsurl+"/GetInvoiceInfoService.asmx") ;
			client.setSoapheader(soapHeader) ;
			client.setSoapbody(soapBody) ;
			
			String result = client.request() ;

			Document resultdoc=DOMHelper.newDocument(result);
		    Element resultelement=resultdoc.getDocumentElement();
		    Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
		    String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
		    if(!"0".equals(resultcode))
			{
			   Log.info("��ȡ��Ʊ��Ϣʧ�ܣ����š�" + orderID + "��" + DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim()) ;
			   return "";
			}
		    Element[] allinvoice = DOMHelper.getSubElementsByName(resultdetail, "invoice") ;
		    //��Ʊ̧ͷ
    		title = DOMHelper.getSubElementVauleByName(allinvoice[0], "invoicetitle");
		}
		catch (Exception e) 
		{
			Log.error(jobname, "��ȡ��Ʊ̨ͷʧ��,������Ϣ��"+e.getMessage()) ;
			title = "" ;
		}
		return title ;
	}

	public static String getErrorInfoByResultCode(String errorCode)
	{
		Hashtable<String, String> error = new Hashtable<String, String>() ;
		error.put("-1", "�����֤���� ") ;
		error.put("0", "ȫ���ɹ�") ;
		error.put("1", "��Ӧ��ID����ȷ") ;
		error.put("2", "�ǹ�Ӧ�̷���������ϵ") ;
		error.put("3", "ȫ��ʧ��") ;
		error.put("4", "����ʧ��") ;
		error.put("5", "û�пɴ���Ķ���") ;
		error.put("6", "��Ӧ�̲��������") ;
		
		try {
			return error.get(errorCode) ;
		} catch (Exception e) {
			// TODO: handle exception
			return null ;
		}
	}
	 
	/**
	 * ��ȡvjia�˻�����
	 * @param jobname
	 * @param conn
	 * @param tradecontactid
	 * @param lasttimeconfvalue
	 * @param timeInterval ʱ����(����)
	 * @throws Exception
	 */
	
	public static void getReturnOrders(String jobname, Connection conn, String tradecontactid,String lasttimeconfvalue,int timeInterval,Hashtable<String, String> params) throws Exception
	{
		String passWord = params.get("passWord") ;
		String userName = params.get("userName") ;
		String URI = params.get("URI") ;
		String swsSupplierID = params.get("swsSupplierID") ;
		String wsurl = params.get("wsurl") ;
		String strkey = params.get("strkey") ;
		String striv = params.get("striv") ;
		int pageSize = Integer.parseInt(params.get("pageSize")) ;
		
	   Date currentDate = new Date() ;
	   Date configTime = new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()) ;
	   Date startTime=new Date(configTime.getTime()+1000L);
	   Date endTime=new Date(configTime.getTime()+daymillis);
	   
	   String sTime = Formatter.format(startTime, Formatter.DATE_TIME_FORMAT);
	   String eTime = Formatter.format(endTime, Formatter.DATE_TIME_FORMAT) ;
	   
	   boolean getOrdersSuccess = true ;
	   boolean hasNextPage = true ;
	   int pageIndex = 1 ;
	   ArrayList<String> sqlList = new ArrayList<String>() ;
	   
       SoapHeader soapheader=new SoapHeader();
	   soapheader.setPassword(passWord);
	   soapheader.setUname(userName);
	   soapheader.setUri(URI);
	   
	   SoapBody soapbody=new SoapBody();
	   soapbody.setRequestname("GetReturnFormInfoBysupplierIDAndTime");
	   soapbody.setUri(URI);
	   
	   while(hasNextPage)
	   {
			try 
			{
			   //Log.info("pageIndex="+String.valueOf(pageIndex)) ;
			   Hashtable<String,String> bodyparams=new Hashtable<String,String>();
			   bodyparams.put("swsSupplierID", swsSupplierID);
			   bodyparams.put("page", String.valueOf(pageIndex)) ;
			   bodyparams.put("pageSize", String.valueOf(pageSize)) ;
			   bodyparams.put("startTime", sTime) ;
			   bodyparams.put("endTime", eTime) ;
			   soapbody.setBodyParams(bodyparams);
			   
			   SoapServiceClient client=new SoapServiceClient();
			   client.setUrl(wsurl+"/GetReTurnFormInfoService.asmx");
			   client.setSoapbody(soapbody);
			   client.setSoapheader(soapheader);
			   
			   String result=client.request();
			   
			  
			   
			   Document resultdoc=DOMHelper.newDocument(result);
			   Element resultelement=resultdoc.getDocumentElement();
			   Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
			   String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
			   String resultmessage = DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim() ;
	
			   if("-1234699".indexOf(resultcode) >= 0)
			   {
				   Log.error(jobname, "��ȡvjia�˻�����ʧ�ܣ�������룺"+resultcode + "��������Ϣ��" + resultmessage) ;
				   hasNextPage = false ;
				   getOrdersSuccess = false ;
				   break ;
			   }
			   else if("5".equals(resultcode))
			   {
				   hasNextPage = false ;
				   getOrdersSuccess = true ;
				   //Log.info("vjiaȡ�˻�����"+ resultmessage) ;û�пɴ���Ķ���
				   break ;
			   }
			   String sql = "" ;
			   //���ڶ���
			   Element[] orders = DOMHelper.getSubElementsByName(resultdetail, "order") ;
			   for(int i = 0 ; i < orders.length ; i++)
			   {
				   Element order = orders[i] ;
				   //ԭ������
				   String orderid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderid").trim(), strkey, striv) ;
				   //������
				   Element[] accepts = DOMHelper.getSubElementsByName(order, "accept") ;
				   for(int j = 0 ; j < accepts.length ; j++)
				   {
					   Element accept = accepts[j] ;
					   String acceptid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(accept, "acceptid").trim(), strkey, striv) ;
					   System.out.println("acceptid="+acceptid) ;
					   //������ϸ
					   Element[] acceptdetails = DOMHelper.getSubElementsByName(accept, "acceptdetail") ;
					   for(int k = 0 ; k < acceptdetails.length ; k ++)
					   {
						   Element acceptdetail = acceptdetails[k] ;
						   String direction = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(acceptdetail, "direction").trim(), strkey, striv) ;
						   String productcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(acceptdetail, "productcode").trim(), strkey, striv) ;
						   String barcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(acceptdetail, "barcode").trim(), strkey, striv) ;
						   String productname = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(acceptdetail, "productname").trim(), strkey, striv) ;
						   String amount = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(acceptdetail, "amount").trim(), strkey, striv) ;
						   String sellprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(acceptdetail, "sellprice").trim(), strkey, striv) ;
						   
						   sql="select shopid from ContactShopContrast where tradecontactid="+tradecontactid;
				           String inshopid=SQLHelper.strSelect(conn, sql);
				           //conn.setAutoCommit(false);
							sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
							String sheetid=SQLHelper.strSelect(conn, sql);
							
							if (sheetid.trim().equals(""))
								throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
	
							 //���뵽֪ͨ��
				            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
				                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
							//SQLHelper.executeSQL(conn, sql);
				            sqlList.add(sql) ;
				            
							sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , "
								+ "Created , Modified , OrderStatus , Status , GoodStatus , "
			                    + " HasGoodReturn ,RefundFee , Payment , Reason,Description ,"
			                    + " Title , Price , Num , GoodReturnTime , Sid , "
			                    + " TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ," 
			                    + " Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
			                    + " values('" + sheetid + "' , '" + orderid + "' , '" + acceptid + "' , '' , '' ,"
			                    + "'',getdate(),'','',''," 
			                    + "'','" + String.valueOf(Integer.parseInt(amount)*Float.parseFloat(sellprice)) + "','" + String.valueOf(Integer.parseInt(amount)*Float.parseFloat(sellprice)) + "','','" + direction + "',"
			                    + "'" + productname + "','" + sellprice + "','" + amount + "','','" + direction + "',"
			                    + "'" + String.valueOf(Integer.parseInt(amount)*Float.parseFloat(sellprice)) + "','','" + barcode + "','" + productcode + "','',"
			                    + "'','','" + inshopid + "','" + acceptid + "','','','')" ;
							//SQLHelper.executeSQL(conn,sql);
							sqlList.add(sql) ;
							
							//conn.commit();
							//conn.setAutoCommit(true);
							Log.info(jobname,"�ӿڵ���:"+sheetid+" ������:"+orderid);
					   }
				   }
				   //ȡ����Ʊ��Ϣ(�ȿͻ��յ����ٸ��ͻ��ķ�Ʊ�������Ʊ�Ͱ���һͬ�ĳ�����ȡ���˹���)
				   sql = "select count(*) from customerOrder with(nolock) where refsheetid='"+ orderid +"' and flag='100' and invoiceflag='1'" ;
				   if(SQLHelper.intSelect(conn, sql) > 0)
				   {
					   sql = "update customerOrder set invoiceflag='0',TradeNote=TradeNote+'�˻�����Ҫ��Ʊ' where refsheetid='"+ orderid +"' and flag='100'" ;
					   SQLHelper.executeSQL(conn, sql) ;
				   }
			   }
			   String allpagenum = DOMHelper.getSubElementVauleByName(resultdetail, "allpagenum").trim() ;
			   if(pageIndex < Integer.parseInt(allpagenum))
			   {
				   //��һҳ
				   pageIndex ++ ;
				   System.out.println("��һҳ��"+pageIndex) ;
			   }
			   else
			   {
				   hasNextPage = false ;
			   }
			   getOrdersSuccess = true ;
			} 
			catch (Exception e) 
			{
				Log.error(jobname, "ȡvjia�˻���������������Ϣ��"+e.getMessage()) ;
				hasNextPage = false ;
				getOrdersSuccess = false ;
				break;
			}
	   }
	   //���û��ȡ���˻�����
	   if(!getOrdersSuccess)
	   {
		   if((currentDate.getTime() - configTime.getTime()) > timeInterval*60*1000L)
		   {
			   PublicUtils.setConfig(conn,lasttimeconfvalue,Formatter.format((new Date(configTime.getTime()+daymillis)),Formatter.DATE_TIME_FORMAT));
		   }
		   return ;
	   }
		   
	   //�����˻�����
	   try 
	   {
		   conn.setAutoCommit(false) ;
		   for(int i = 0 ; i < sqlList.size() ; i++)
		   {
			   SQLHelper.executeSQL(conn, sqlList.get(i)) ;
		   }
		   //���ȡ��ʱ����ϴ�ȡ����ʱ��������趨��ʱ����������ȡ����ʱ��
		   if((currentDate.getTime() - configTime.getTime()) > timeInterval*60*1000L)
		   {
			   PublicUtils.setConfig(conn,lasttimeconfvalue,Formatter.format((new Date(configTime.getTime()+timeInterval*60*1000L)),Formatter.DATE_TIME_FORMAT));
		   }
		   else if(sqlList.size() >0)
		   {
			   PublicUtils.setConfig(conn,lasttimeconfvalue,Formatter.format((new Date(currentDate.getTime())),Formatter.DATE_TIME_FORMAT));
		   }
		   conn.commit() ;
		   conn.setAutoCommit(true) ;
	   } 
	   catch (Exception e)
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
			Log.error(jobname, "��ȡvjia�˻�������ʧ�ܣ�������Ϣ��"+ e.getMessage()) ;
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
			//System.out.println(sql) ;
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
	

	public static Order getOrderByID(String wsurl,String uri,String swsSupplierID,String strkey,
			String striv,String supplierid,String passWord,String orderID) throws Exception
	{
	
			Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
			bodyParams.put("swsSupplierID", swsSupplierID) ;
			bodyParams.put("DESFormCode", DesUtil.DesEncode(orderID, strkey, striv)) ;
			
			SoapHeader soapHeader = new SoapHeader() ;
			soapHeader.setUname(supplierid) ;
			soapHeader.setPassword(passWord) ;
			soapHeader.setUri(uri) ;
			
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("GetFormCodeInfo") ;
			soapBody.setUri(uri) ;
			soapBody.setBodyParams(bodyParams) ;
			
			SoapServiceClient client = new SoapServiceClient() ;
			client.setUrl(wsurl+"/GetOrderService.asmx") ;
			client.setSoapheader(soapHeader) ;
			client.setSoapbody(soapBody) ;
			
			String result = client.request() ;
			
			Document resultdoc=DOMHelper.newDocument(result);
			Element resultelement=resultdoc.getDocumentElement();
			Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
			Element order =(Element) resultdetail.getElementsByTagName("order").item(0) ;
			
			Order o  = new Order() ;
			
			String orderid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderid"), strkey, striv).trim() ;
				
		   String orderdistributetime = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderdistributetime"), strkey, striv) ;
		   String username = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "username"), strkey, striv) ;
		   String usertel = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "usertel"), strkey, striv) ;
		   String userphone = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "userphone"), strkey, striv) ;
		   String areaid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "areaid"), strkey, striv) ;
		   String postalcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "postalcode"), strkey, striv) ;
		   String address = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "address"), strkey,striv) ;
		   String needinvoice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "needinvoice"), strkey, striv).trim() ;
		   String receivetime = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "receivetime"), strkey, striv) ;
		   String totalprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "totalprice"), strkey, striv) ;
		   String transferprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "transferprice"), strkey, striv) ;
		   String paidprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "paidprice"), strkey, striv) ;
		   String unpaidprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "unpaidprice"), strkey, striv).trim() ;
		   String comment = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "comment"), strkey, striv) ;
		   String orderstatus = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderstatus"), strkey, striv) ;
		      

		   //�����ͻ�ʱ�䣬ȥ�����ź��������
		   if(receivetime != null && receivetime.indexOf("��") > -1)
		   {
			   receivetime = receivetime.substring(0, receivetime.indexOf("��")) ;
		   }
		   
		   //����ģʽ 1������֧��	2�ǻ�������
		   if("0.00".equals(unpaidprice) || Float.parseFloat(unpaidprice) == 0)
			   o.setPayMode("1") ;
		   else
			   o.setPayMode("2") ;
		   //�Ƿ��跢Ʊ��1��Ҫ��0����Ҫ,needinvoice ����ֵ
		   if("True".equalsIgnoreCase(needinvoice))
		   {
			   o.setNeedinvoice("1") ;
			   String invoiceTitle =OrderUtils.getInvoiceTitle("ȡ��������", orderid,uri,wsurl,supplierid,passWord,swsSupplierID,strkey,striv);
			   //�����Ʊ̨ͷΪ�����ˡ���������ϵ����Ϊ��Ʊ̨ͷ
			   if("����".equals(invoiceTitle))
				   o.setInvoiceTitle(username.replace("'", "''")) ;
			   else
				   o.setInvoiceTitle(invoiceTitle) ;
		   }
		   else
			   o.setNeedinvoice("0") ;
		   
		   //�����ַ��Ϣ����ʡ�����У��������ÿո����
		   address = address.replace("��ʡ��", " ").replace("���У�", " ").replace("������", " ") ;
 
			   o.setOrderid(orderid) ;
			   if (!orderdistributetime.equals(""))
			   o.setOrderdistributetime(Formatter.parseDate(orderdistributetime,Formatter.DATE_TIME_FORMAT)) ;
		   else				   
			   o.setOrderdistributetime(new Date()) ;
		   o.setUsername(username) ;
		   o.setUsertel(usertel) ;
		   o.setUserphone(userphone) ;
		   o.setAreaid(areaid) ;
		   o.setPostalcode(postalcode) ;
		   o.setAddress(address) ;
		   o.setReceivetime(receivetime) ;
		   o.setTotalprice(Float.parseFloat(totalprice)) ;
		   o.setTransferprice(Float.parseFloat(transferprice)) ;
		   o.setPaidprice(Float.parseFloat(paidprice)) ;
		   o.setUnpaidprice(Float.parseFloat(unpaidprice)) ;
		   o.setComment(comment) ;	   
		   o.setOrderstatus(orderstatus);
			   
		
		   //ȡ�ö�����ϸ
		   Element[] orderDetail = DOMHelper.getSubElementsByName(order, "orderdetail") ;

		   for(int j = 0 ; j < orderDetail.length ; j++)
		   {
			   Element detail=(Element) orderDetail[j] ; 
			   String barcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "barcode"), strkey, striv) ;
			   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "sku"), strkey, striv) ;
			   String productname = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "productname"), strkey, striv) ;
			   String size = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "size"), strkey, striv) ;
			   String qty = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "qty"), strkey, striv) ;
			   String price = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "price"), strkey, striv) ;
			   String amount = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "amount"), strkey, striv) ;
			   
			   if("".equals(qty) || qty==null)
				   qty = "0" ;
			   if("".equals(price) || price==null)
				   qty = "0" ;
			   if("".equals(amount) || amount==null)
				   amount = "0" ;
			   
			   OrderItem oi = new OrderItem() ;
			   oi.setBarcode(barcode) ;
			   oi.setSku(sku) ;
			   oi.setProductname(productname) ;
			   oi.setSize(size) ;
			   oi.setQty(qty) ;
			   oi.setPrice(price) ;
			   oi.setAmount(amount) ;
			   
			   o.addOrderitems(oi) ;
		   }
			
		return o;
	}
	
}
