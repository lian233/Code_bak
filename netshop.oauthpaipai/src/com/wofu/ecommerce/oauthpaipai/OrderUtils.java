package com.wofu.ecommerce.oauthpaipai;


import java.sql.Connection;
import java.sql.SQLException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;


public class OrderUtils {


	
	
	
	public static Vector getSaleGoods(Element itemlist)
	{
		Vector<Hashtable> vtsku=new Vector<Hashtable>();
		NodeList itemnodes = itemlist.getElementsByTagName("itemListNew");
		
		NodeList iteminfonodes = ((Element)itemnodes.item(0)).getElementsByTagName("itemInfo");
		
		for(int j=0;j<iteminfonodes.getLength();j++)
		{				
			Element orderitemelement = (Element) iteminfonodes.item(j);
			Hashtable<String,String> htsku=new Hashtable<String,String>();
			htsku.put("sku", DOMHelper.getSubElementVauleByName(orderitemelement,"stockLocalCode"));
			htsku.put("qty", DOMHelper.getSubElementVauleByName(orderitemelement,"itemDealCount"));
			vtsku.add(htsku);
		}
		return vtsku;
	}
	
	public static Order getDealDetail(String spid,String secretkey,String token,String uid,String encoding,String dealcode)
		throws Exception
	{
		Order o=new Order();
			
	
		PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
		
		sdk.setCharset(encoding);
		
		HashMap<String, Object> params = sdk.getParams("/deal/getDealDetail.xhtml");
		// 填充URL请求参数

		params.put("sellerUin", uid);
		params.put("dealCode", dealcode);
		params.put("listItem", "1");	
		

			
		String result = sdk.invoke();
		
		Log.info("订单详情："+result);	
		Document doc = DOMHelper.newDocument(result.toString(), encoding);
		Element dealinfoelement = doc.getDocumentElement();
		
		//Element dealinfoelement = (Element) urlset.getElementsByTagName("getDealDetail").item(0);
			
		o.setDealCode(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode"));
		o.setDealdesc(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealDesc"));

		
		o.setDealDetailLink(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealDetailLink"));
		o.setBuyerRemark(DOMHelper.getSubElementVauleByName(dealinfoelement, "buyerRemark"));
		Log.info("买家留言: "+o.getBuyerRemark());
		o.setDealPayType(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealPayType"));
		
		o.setDealPayTypeDesc(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealPayTypeDesc"));
		o.setDealRateState(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealRateState"));
		o.setDealRateStateDesc(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealRateStateDesc"));
		o.setHasInvoice(Integer.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "hasInvoice")));
		

		o.setInvoiceTitle(DOMHelper.getSubElementVauleByName(dealinfoelement, "invoiceContent"));
		o.setInvoiceContent(DOMHelper.getSubElementVauleByName(dealinfoelement, "invoiceTitle"));
		o.setTenpayCode(DOMHelper.getSubElementVauleByName(dealinfoelement, "tenpayCode"));
		o.setWuliuId(DOMHelper.getSubElementVauleByName(dealinfoelement, "wuliuId"));
	
		o.setReceiverAddress(DOMHelper.getSubElementVauleByName(dealinfoelement, "receiverAddress"));
		o.setReceiverMobile(DOMHelper.getSubElementVauleByName(dealinfoelement, "receiverMobile"));
		o.setReceiverName(DOMHelper.getSubElementVauleByName(dealinfoelement, "receiverName"));
		o.setReceiverPhone(DOMHelper.getSubElementVauleByName(dealinfoelement, "receiverPhone"));
		o.setReceiverPostcode(DOMHelper.getSubElementVauleByName(dealinfoelement, "receiverPostcode"));
		o.setBuyerName(DOMHelper.getSubElementVauleByName(dealinfoelement, "buyerName").replaceAll("'", "''"));
		o.setBuyerUin(DOMHelper.getSubElementVauleByName(dealinfoelement, "buyerUin"));
		o.setFreight(Double.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "freight")));
		o.setTransportType(DOMHelper.getSubElementVauleByName(dealinfoelement, "transportType"));

		o.setTransportTypeDesc(DOMHelper.getSubElementVauleByName(dealinfoelement, "transportTypeDesc"));
		o.setTotalCash(Double.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "totalCash")));
		o.setDealPayFeeTotal(Double.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealPayFeeTotal")));
		o.setCouponFee(Double.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "couponFee")));
		o.setComboInfo(DOMHelper.getSubElementVauleByName(dealinfoelement, "comboInfo"));
		o.setDealState(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState"));
		o.setDealStateDesc(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealStateDesc"));
		o.setCreateTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "createTime"),Formatter.DATE_TIME_FORMAT));
		o.setDealEndTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealEndTime"),Formatter.DATE_TIME_FORMAT));
		o.setLastUpdateTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "lastUpdateTime"),Formatter.DATE_TIME_FORMAT));
		o.setPayTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "payTime"),Formatter.DATE_TIME_FORMAT));
		o.setPayReturnTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "payReturnTime"),Formatter.DATE_TIME_FORMAT));
		o.setRecvfeeReturnTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "recvfeeReturnTime"),Formatter.DATE_TIME_FORMAT));
		o.setRecvfeeTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "recvfeeTime"),Formatter.DATE_TIME_FORMAT));
		o.setSellerConsignmentTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "sellerConsignmentTime"),Formatter.DATE_TIME_FORMAT));
		o.setDealNoteType(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealNoteType"));
		o.setDealNote(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealNote"));	
		Log.info("订单的备注内容： "+o.getDealNote());
		o.setDealFlag(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealFlag"));

		
		o.setAvailableAction(DOMHelper.getSubElementVauleByName(dealinfoelement, "availableAction"));		
		o.setDealType(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealType"));
		o.setDealTypeDesc(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealTypeDesc"));			
		o.setWhoPayShippingfee(Integer.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "whoPayShippingfee")));
		o.setSellerRecvRefund(Double.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "sellerRecvRefund")));
		o.setBuyerRecvRefund(Double.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "buyerRecvRefund")));				
		o.setDealPayFeePoint(Double.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealPayFeePoint")));
		o.setDealPayFeeTicket(Double.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "dealPayFeeTicket")));
		String shoppingfeecalc=DOMHelper.getSubElementVauleByName(dealinfoelement, "shippingfeeCalc");
		if (shoppingfeecalc.equals("")) shoppingfeecalc="0";
		o.setShippingfeeCalc(Double.valueOf(shoppingfeecalc));
		o.setSellerCrm(DOMHelper.getSubElementVauleByName(dealinfoelement, "sellerCrm"));

		o.setSellerName(DOMHelper.getSubElementVauleByName(dealinfoelement, "sellerName"));
		o.setSellerUin(Long.valueOf(DOMHelper.getSubElementVauleByName(dealinfoelement, "sellerUin")));
		o.setExpectArrivalTime(Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "expectArrivalTime"),Formatter.DATE_TIME_FORMAT));
	
		o.setWuliuCompany(DOMHelper.getSubElementVauleByName(dealinfoelement, "wuliuCompany"));
		o.setWuliuCode(DOMHelper.getSubElementVauleByName(dealinfoelement, "wuliuCode"));
		o.setWuliuDesc(DOMHelper.getSubElementVauleByName(dealinfoelement, "wuliuDesc"));
	
		NodeList itemnodes = dealinfoelement.getElementsByTagName("itemList");
		
		NodeList iteminfonodes = ((Element)itemnodes.item(0)).getElementsByTagName("itemInfo");
		
		double totalfee=0.00;
		double adjustfee=0.00;
		
		for(int j=0;j<iteminfonodes.getLength();j++)
		{				
			Element orderitemelement = (Element) iteminfonodes.item(j);
			
			OrderItem oi=new OrderItem();					
			oi.setDealSubCode(DOMHelper.getSubElementVauleByName(orderitemelement,"dealSubCode"));
			oi.setItemName(DOMHelper.getSubElementVauleByName(orderitemelement,"itemName"));
			oi.setItemCode(DOMHelper.getSubElementVauleByName(orderitemelement,"itemCode"));
			oi.setItemCodeHistory(DOMHelper.getSubElementVauleByName(orderitemelement,"itemCodeHistory"));
			oi.setItemLocalCode(DOMHelper.getSubElementVauleByName(orderitemelement,"itemLocalCode"));
			oi.setStockLocalCode(DOMHelper.getSubElementVauleByName(orderitemelement,"stockLocalCode"));
			oi.setStockAttr(DOMHelper.getSubElementVauleByName(orderitemelement,"stockAttr"));
		
			oi.setItemDetailLink(DOMHelper.getSubElementVauleByName(orderitemelement,"itemDetailLink"));
			oi.setItemPic80(DOMHelper.getSubElementVauleByName(orderitemelement,"itemPic80"));
			oi.setItemRetailPrice(Double.valueOf(DOMHelper.getSubElementVauleByName(orderitemelement,"itemRetailPrice")));
			oi.setItemDealPrice(Double.valueOf(DOMHelper.getSubElementVauleByName(orderitemelement,"itemDealPrice")));
			oi.setItemAdjustPrice(Double.valueOf(DOMHelper.getSubElementVauleByName(orderitemelement,"itemAdjustPrice")));
			oi.setItemDiscountFee(Double.valueOf(DOMHelper.getSubElementVauleByName(orderitemelement,"itemDiscountFee")));
			oi.setItemDealCount(Integer.parseInt(DOMHelper.getSubElementVauleByName(orderitemelement,"itemDealCount")));
			oi.setAccount(DOMHelper.getSubElementVauleByName(orderitemelement,"account"));
			
			oi.setRefundState(DOMHelper.getSubElementVauleByName(orderitemelement,"refundState"));
			oi.setRefundStateDesc(DOMHelper.getSubElementVauleByName(orderitemelement,"refundStateDesc"));
			o.addOrderItem(oi);
			
			totalfee=totalfee+oi.getItemDealCount()*oi.getItemDealPrice();
			adjustfee=adjustfee+oi.getItemAdjustPrice();					
		}	
		o.setTotalfee(totalfee);
		o.setAdjustfee(adjustfee);
	
		return o;
	}
	
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn, Order o,String tradecontactid,String username,boolean is_cod) 
		throws JException,SQLException
	{
		String sheetid="";
		String sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
		try
		{			
				conn.setAutoCommit(false);			
				sheetid=SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql,"取接口单号出错!");
				//2为货到付款  1为正常下单
				int payMode=is_cod?2:1;
				//货到付款的总金额要写到这里，里面包括了服务费
				String payfee=is_cod?String.valueOf(o.getDealPayFeeTotal()/100):"";
				//货到付款时间定为当前时间
				String paytime=is_cod?Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT):Formatter.format(o.getCreateTime(),Formatter.DATE_TIME_FORMAT);
				 //加入到通知表
                sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                    + sheetid +"',1 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
				SQLHelper.executeSQL(conn, sql);
								
				 //加入到单据表
                sql =  "insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , " 
                	+ " type , created , buyermessage , shippingtype , payment , "
					+ " discountfee , adjustfee , status , buyermemo , sellermemo , "
					+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
					+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
					+ " buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , "
					+ " buyeremail , commissionfee , availableconfirmfee , haspostFee , receivedpayment , "
					+ " codfee , codstatus , timeoutactiontime , delivery , deliverySheetID , "
					+ " alipayNo , buyerflag , sellerflag,price , num , title , snapshoturl , snapshot , "
					+ " sellerrate,buyerrate,dealRateState,numiid,promotion,tradefrom,alipayurl,PromotionDetails,TradeContactID,payMode,payfee) values(" 
                    + "'" + sheetid + "','" + sheetid + "','yongjun','" + o.getDealCode() + "','','" +Params.username + "',''," 
                    + "'" + Formatter.format(o.getCreateTime(),Formatter.DATE_TIME_FORMAT) + "','"  +o.getBuyerRemark() + "'," 
                    + "'" + o.getTransportType() + "','" + o.getDealPayFeeTotal()/100 + "','" + o.getCouponFee()/100 + "','"+o.getAdjustfee()/100+"','"
                    + o.getDealState()+ "','" + o.getBuyerRemark() + "','"+o.getDealNote()+"'" 
                    + ",'','" + paytime + "', '" 
                    + Formatter.format(o.getDealEndTime(),Formatter.DATE_TIME_FORMAT) + "','"+Formatter.format(o.getLastUpdateTime(),Formatter.DATE_TIME_FORMAT)+"','', " 
                    + "'','','"+o.getTotalfee()/100+"','"+o.getFreight()/100+"','"+o.getTenpayCode()+"','"+o.getBuyerUin()+"','"+o.getBuyerUin()+"',"
                    + "'" + o.getReceiverName() + "','','','','"+o.getReceiverAddress()+"','"+o.getReceiverPostcode()+"',"                                        
                    + "'" + o.getReceiverMobile() + "','" +o.getReceiverPhone() + "', '" + Formatter.format(o.getSellerConsignmentTime(),Formatter.DATE_TIME_FORMAT) + "','','','',"
                    +"'','','','','','','','"+o.getTenpayCode()+"','','"+getConvertSellerflag(o.getDealNoteType())+"','','','"+username+"','"+o.getDealDetailLink()+"','',"
                    +"'','','"+o.getDealRateState()+"','','"+o.getComboInfo()+"','paipai','"+o.getDealDetailLink()+"','"+o.getComboInfo()+"','"+tradecontactid+"',"+payMode+",'"+payfee+"')";
                Log.info(o.getDealNote());
                //Log.info("sql: "+sql);
                SQLHelper.executeSQL(conn, sql);
                
                for(int i=0; i<o.getOrderitems().size();i++)
                {
                	OrderItem oi=(OrderItem) o.getOrderitems().get(i);
                	
                	//整体折扣按金额比例分配
                	double wholediscountfee=(oi.getItemDealPrice()*oi.getItemDealCount()+oi.getItemAdjustPrice())*o.getCouponFee()/o.getTotalfee();
                	
	                sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , " 
                    + " title , sellernick , buyernick , type , created , " 
                    + " refundstatus , outeriid , outerskuid , totalfee , payment , " 
                    + " discountfee , adjustfee , status , timeoutactiontime , owner , " 
                    + " iid , skuPropertiesName , num , price , picPath , " 
                    + " oid , snapShotUrl , snapShot ,modified) values( " 
	                + "'" + sheetid + "','" + sheetid+"-"+oi.getDealSubCode() + "','" + sheetid + "'," 
	                + "'" + oi.getItemCode() + "','','" + oi.getItemName() + "','" + o.getSellerName() + "','"+o.getBuyerName()+"'," 
	                + "'' , '" + Formatter.format(o.getCreateTime(),Formatter.DATE_TIME_FORMAT)+"','"+oi.getRefundState()+"','"+oi.getItemLocalCode()+"','"+oi.getStockLocalCode()+"'," 
	                + "'"+String.valueOf((oi.getItemDealPrice()*oi.getItemDealCount())/100)+"',"
	                + "'"+String.valueOf((oi.getItemDealPrice()*oi.getItemDealCount())/100+oi.getItemAdjustPrice()/100+oi.getItemDiscountFee()/100+wholediscountfee/100)+"',"
	                + "'" + String.valueOf(oi.getItemDiscountFee()/100)+ "','"+String.valueOf(oi.getItemAdjustPrice()/100)+"','"+o.getDealState()+"','','yongjun'," 
	                +"'','"+oi.getStockAttr()+"',"+oi.getItemDealCount()+",'"+oi.getItemDealPrice()/100+"','"+oi.getItemPic80()+"',"
	                + "'" +  oi.getDealSubCode() + "','" + oi.getItemPic80()+"','"+oi.getItemDetailLink()+"','"+Formatter.format(o.getLastUpdateTime(),Formatter.DATE_TIME_FORMAT)+"')";
	                SQLHelper.executeSQL(conn,sql);
                }
                
               
				conn.commit();
				conn.setAutoCommit(true);
				Log.info("生成订单【" + o.getDealCode() + "】接口数据成功，接口单号【" + sheetid + "】");
				
				return sheetid;
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
			throw new JException("生成订单【" + o.getDealCode() + "】接口数据失败!"+e.getMessage());
		}	
	}
	
	private static int getConvertSellerflag(String notetype)
	{
		if (notetype.equalsIgnoreCase("RED")) 	
				return 1;
		else if (notetype.equalsIgnoreCase("YELLOW")) 			
				return 2;
		else if (notetype.equalsIgnoreCase("GREEN")) 	
				return 3;
		else if (notetype.equalsIgnoreCase("BLUE")) 
				return 4;
		else if (notetype.equalsIgnoreCase("PINK")) 	
				return 5;
		else 	
				return 0;	
	}
	
}
