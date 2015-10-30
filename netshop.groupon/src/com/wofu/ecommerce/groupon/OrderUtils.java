package com.wofu.ecommerce.groupon;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import meta.MD5Util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.groupon.domain.model.ws.DisneyRequestBean;
import com.groupon.ws.ObjBodyWriter;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private static long daymillis=2*24*60*60*1000L;
	
	public static Order getOrder(Element orderinfoelement)
	{

		Order o = new Order();
		
		NodeList personnodes = orderinfoelement.getElementsByTagName("person_info");
		
		Element personlement = (Element) personnodes.item(0);
	
		o.setReceiverName(DOMHelper.getSubElementVauleByName(personlement, "receivername"));
		o.setbuyerNick(DOMHelper.getSubElementVauleByName(personlement, "name"));
		o.setEmail(DOMHelper.getSubElementVauleByName(personlement, "email"));	
		o.setMobilePhone(DOMHelper.getSubElementVauleByName(personlement, "mobilephone"));

		o.setRemarks(DOMHelper.getSubElementVauleByName(personlement, "Remarks"));
		o.setSKU(DOMHelper.getSubElementVauleByName(personlement, "typy_SKU"));
		

		o.setProvince(DOMHelper.getSubElementVauleByName(personlement, "address_province"));
		o.setCity(DOMHelper.getSubElementVauleByName(personlement, "address_city"));		
		o.setAddress(DOMHelper.getSubElementVauleByName(personlement, "address_more"));
		o.setZipCode("00000000");

		NodeList ordernodes = orderinfoelement.getElementsByTagName("order");

		Element orderelement = (Element) ordernodes.item(0);

		o.setOrderId(DOMHelper.getSubElementVauleByName(orderelement, "orderId"));
		o.setMoney(DOMHelper.getSubElementVauleByName(orderelement, "order_money"));
		o.setCreatTime(Formatter.format(new Date(Long.valueOf(DOMHelper.getSubElementVauleByName(orderelement, "order_creattime"))),Formatter.DATE_TIME_FORMAT));

		o.setPaymentTime(Formatter.format(new Date(Long.valueOf(DOMHelper.getSubElementVauleByName(orderelement, "order_paymenttime"))),Formatter.DATE_TIME_FORMAT));
		o.setPostage(DOMHelper.getSubElementVauleByName(orderelement, "postage"));
		o.setBuyNum(DOMHelper.getSubElementVauleByName(orderelement, "buyNUM"));
		o.setStatus(DOMHelper.getSubElementVauleByName(orderelement, "order_status"));
		return o;
	}
	/*
	 * ת��һ���������ӿڱ�
	 */
	public static void createInterOrder(Connection conn, String tradecontactid,String username,Order o) throws SQLException,JException
	{
		String sheetid="";
		int haspostFee;
		String sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
		try
		{			
				conn.setAutoCommit(false);			
				sheetid=SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
				
				 //���뵽֪ͨ��
                sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                    + sheetid +"',1 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
				SQLHelper.executeSQL(conn, sql);
				
				if (Float.valueOf(o.getPostage()).compareTo(Float.valueOf("0.0"))>0)
					haspostFee=1;
				else
					haspostFee=0;
				
				 //���뵽���ݱ�
                sql = "insert into ns_customerorder(CustomerOrderId,SheetID,Owner,tid,OrderSheetID,sellernick," 
                    + " created ,payment,status,buyermemo,tradememo,paytime,totalfee,postfee,"
                    + " buyernick,receivername,receiverstate,receivercity,receiverdistrict," 
                    + " receiveraddress,receiverzip,receivermobile,receiverphone,buyeremail,haspostFee," 
                    + " price,num,title,tradefrom,TradeContactID) "
                    + " values(" 
                    + "'" + sheetid + "','" + sheetid + "','yongjun','" + o.getOrderId() + "','','" +username + "'," 
                    + "'" + o.getCreatTime() + "','"  +String.valueOf(Float.valueOf(o.getMoney())*Float.valueOf(o.getBuyNum())+Float.valueOf(o.getPostage())) + "'," 
                    + "'" + o.getStatus() + "','" + o.getRemarks() + "','" + o.getRemarks() + "','" + o.getPaymentTime() + "','"
                    + String.valueOf(Float.valueOf(o.getMoney())*Float.valueOf(o.getBuyNum())) + "','" + o.getPostage() + "','" + o.getbuyerNick()+ "'" 
                    + ",'" + o.getReceiverName() +  "','" + o.getProvince() + "', '" + o.getCity() + "','','" + o.getAddress() + "', " 
                    + "'"  + o.getZipCode() + "','" + o.getMobilePhone() + "','"+o.getMobilePhone()+"'," 
                    + "'" + o.getEmail() + "','" + String.valueOf(haspostFee) + "',"                                        
                    + "'" + o.getMoney() + "','" +o.getBuyNum() + "', '" + username + "','GROUPON','"+tradecontactid+"')";
                SQLHelper.executeSQL(conn, sql);
                
                sql = "insert into ns_orderitem(CustomerOrderId,orderItemId,SheetID,"
                + " sellernick,buyernick,created,outerskuid,totalfee,payment,"
                + " status,owner,num,price,skuid) values( " 
                + "'" + sheetid + "','" + sheetid + "_" + o.getOrderId() + "','" + sheetid + "'," 
                + "'" + username + "', '" + o.getbuyerNick() + "' ,'" + o.getCreatTime() + "', " 
                + "'" + o.getSKU() + "' , '" + String.valueOf(Float.valueOf(o.getMoney())*Float.valueOf(o.getBuyNum())) 
                +"','" +String.valueOf(Float.valueOf(o.getMoney())*Float.valueOf(o.getBuyNum())+Float.valueOf(o.getPostage())) + "' , " 
                + "'" +  o.getStatus() + "','yongjun'," 
                + "" +  o.getBuyNum() + ",'" + o.getMoney()+"','"+o.getSKU()+"')";
                SQLHelper.executeSQL(conn,sql);
                                  
				conn.commit();
				conn.setAutoCommit(true);
				Log.info("���ɶ�����" + o.getOrderId() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");				
		}
		catch (JSQLException e1)
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
			throw new JException("���ɶ�����" + o.getOrderId() + "���ӿ�����ʧ��!"+e1.getMessage());
		}		
	}
	
	/*
	 * ��ȡĳ����Ŀһ��֮������ж���
	 */
	public static void getBusinessOrderList(String modulename,Connection conn,Hashtable htwsinfo,
			String grouponid,Date starttime,Date endtime) throws JException 
	{
		OMFactory soapFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = soapFactory.createOMNamespace(htwsinfo.get("namespace").toString(), "");
		OMElement soapResponse = soapFactory.createOMElement("groupon", omNs);
		DisneyRequestBean requestBean = new DisneyRequestBean();
		String s = (new StringBuilder(String
				.valueOf(System.currentTimeMillis()))).toString();
		requestBean.setRequest_time(s);
		requestBean.setSign(MD5Util.MD5Encode((new StringBuilder(grouponid))
				.append(s).append(htwsinfo.get("key").toString()).toString()));
		requestBean.setGrouponid(grouponid);
		requestBean.setLimit(htwsinfo.get("limit").toString());
		requestBean.setTotal(htwsinfo.get("total").toString());

		requestBean.setStartTime(String.valueOf(starttime.getTime())); //����һ��
		requestBean.setEndTime(String.valueOf(endtime.getTime())); //����һ��
		soapResponse.addChild(ObjBodyWriter.convertBeanToXml(requestBean,
				"request"));
		Options options = new Options();
		options.setTo(new EndpointReference(htwsinfo.get("wsurl").toString()));
		options.setAction("getBusinessProjectList");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			OMElement result = sender.sendReceive(soapResponse);
		
			Document doc = DOMHelper.newDocument(result.toString(),htwsinfo.get("encoding").toString());
			Element urlset = doc.getDocumentElement();
			NodeList orderinfonodes = urlset.getElementsByTagName("order_info");
			if (orderinfonodes.getLength()>0)
			{
				for (int i = 0; i < orderinfonodes.getLength(); i++) {
					
					Element orderinfoelement = (Element) orderinfonodes.item(i);
					Order o=OrderUtils.getOrder(orderinfoelement);
					String orderid=o.getOrderId();
					String status=o.getStatus();
					String sku=o.getSKU();
					long qty=Long.valueOf(o.getBuyNum());
					Date updatetime=Formatter.parseDate(o.getPaymentTime(), Formatter.DATE_TIME_FORMAT); 
							
					/*
					 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
					 *2��ɾ���ȴ���Ҹ���ʱ��������� 
					 */	
					Log.info(o.getOrderId()+" "+o.getStatus()+" "+o.getPaymentTime());
					if (status.equals("1"))
					{
						//��ʹȡ����ʱ����Ҫ�ж϶����Ƿ���ڣ�ÿ���鶩��ʱ���鶩���Ƿ��Ѿ�����
						if (htwsinfo.get("style").toString().equals("0"))
						{
							try
							{
								createInterOrder(conn,htwsinfo.get("tradecontactid").toString(),htwsinfo.get("username").toString(),o);					
								StockManager.deleteWaitPayStock(modulename, conn,htwsinfo.get("tradecontactid").toString(), orderid, sku);			
							} catch(SQLException sqle)
							{
								throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
							}	
						}
						else
						{
							if (!OrderManager.TidExists("����ű�δ�붩��", conn, String.valueOf(o.getOrderId())))
							{
								if (OrderManager.TidIntfExists("����ű�δ�붩��", conn, String.valueOf(o.getOrderId())))
								{
									Log.info("�ӿ��д���,�ͻ������в�����:"+o.getOrderId()+" "+o.getStatus()+" "+o.getPaymentTime());
								}
								else
								{
									Log.info("�ӿ��в�����,�ͻ������в�����:"+o.getOrderId()+" "+o.getStatus()+" "+o.getPaymentTime());
								}
								try
								{
									createInterOrder(conn,htwsinfo.get("tradecontactid").toString(),htwsinfo.get("username").toString(),o);					
									StockManager.deleteWaitPayStock(modulename, conn,htwsinfo.get("tradecontactid").toString(), orderid, sku);			
								} catch(SQLException sqle)
								{
									throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
								}	
							}
						}
					}
					//�ȴ���Ҹ���ʱ��¼�������
					else if (status.equals("0"))
					{
						StockManager.addWaitPayStock(modulename, conn,htwsinfo.get("tradecontactid").toString(), orderid, sku, qty);
						StockManager.addSynReduceStore(modulename, conn, htwsinfo.get("tradecontactid").toString(), status,orderid, sku, -qty,false);	
						//�����Ժ��û��˿�ɹ��������Զ��ر�
						//�ͷſ��,����Ϊ����
					} else if (status.equals("6"))
					{
						StockManager.addSynReduceStore(modulename, conn, htwsinfo.get("tradecontactid").toString(), status,orderid, sku, qty,false);	
						//������ǰ�����һ���������رս���
						//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
					}else if (status.equals("4"))
					{					
						StockManager.deleteWaitPayStock(modulename, conn,htwsinfo.get("tradecontactid").toString(), orderid, sku);	
						StockManager.addSynReduceStore(modulename, conn, htwsinfo.get("tradecontactid").toString(),status,orderid, sku, qty,false);
					}
					  
			        //����ͬ����������ʱ��
	                if (updatetime.compareTo(Formatter.parseDate(PublicUtils.getConfig(conn, htwsinfo.get("lasttimeconfvalue").toString(), ""), Formatter.DATE_TIME_FORMAT))>0)
	                	PublicUtils.setConfig(conn, htwsinfo.get("lasttimeconfvalue").toString(), Formatter.format(updatetime, Formatter.DATE_TIME_FORMAT));
				}
			}else
			{
				try
				{
					//��ö�ʱ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
					if (dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
							compareTo(dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn, htwsinfo.get("lasttimeconfvalue").toString(), ""), Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
					{
						                	
						PublicUtils.setConfig(conn,htwsinfo.get("lasttimeconfvalue").toString(),Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn, htwsinfo.get("lasttimeconfvalue").toString(), ""), Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00");
	                	
					}
				}catch(ParseException e)
				{
					throw new JException("�����õ����ڸ�ʽ!"+e.getMessage());
				}
			}
				
		}catch(JException ja)
		{
			Log.error(modulename, ja.getMessage());
		}
		catch(AxisFault af)
		{
			throw new JException("����Զ�̷������!"+af.getMessage());
		}
		catch(Exception e)
		{
			throw new JException("����XML����!"+e.getMessage());
		}
	}
	

}
