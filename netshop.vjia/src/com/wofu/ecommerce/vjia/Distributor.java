package com.wofu.ecommerce.vjia;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

public class Distributor {


	//更新vjia配送结果
	public static List updateVjiaDeliveryResult(Connection conn,List orders) throws Exception
	{
		String strkey = "dishiniV" ;
		String striv = "VjiaInfo" ;
		String SupplierId = "dishini" ;
		String url = "http://lmsedi.vjia.com/SupplierAccept.aspx" ;

		ArrayList<Map> resultlist = new ArrayList<Map>() ;

		for(int i = 0 ; i <orders.size() ; i++)
		{
			Hashtable ht = (Hashtable) orders.get(i) ;
			
			Hashtable<String,String> htresult = new Hashtable<String,String>() ;
			
			
			String ordercode = ht.get("ordercode").toString() ;
			int status = Integer.valueOf(ht.get("status").toString()) ;
			
			String orderStatus = "" ;//当当配送结果状态 A3:妥投 A5:拒收
			String rejectReasonCode = "" ;//vjia拒收原因代码 N27:其它
			
			if(status==1)
				orderStatus = "A3" ;
			else if(status==0)
			{
				orderStatus = "A5" ;
				rejectReasonCode = "N27" ;
			}
			else
			{
				htresult.put("ordercode", ordercode) ;
				htresult.put("resultflag", "0") ;
				htresult.put("msg", "未知订单状态:"+status) ;
				resultlist.add(htresult) ;
				Log.error("更新vjia订单配送结果", "更新失败，未知订单状态:"+status) ;
				continue ;
			}
				

			
			String sql = "select top 1 proctime from ecs_deliverynote with(nolock) where ordercode='"+ordercode+"' order by serialid desc" ;
			String distriTime = SQLHelper.strSelect(conn, sql) ;//配送成功时间
				
			sql = "select companyCode,outSid from ns_delivery with(nolock) "
				+"where tid='"+ordercode+"' and sheettype=3" ;
			
			Hashtable htcompany=SQLHelper.oneRowSelect(conn, sql);
	
			String expressCompanyName = getExpressCompanyName(htcompany.get("companyCode").toString()) ;
			String dispatchNo = htcompany.get("outSid").toString() ;
	
			sql = "select count(*) from trandelivery with(nolock) where orideliverysheetid='"+dispatchNo+"'" ;
			if (SQLHelper.intSelect(conn, sql)>0)
			{				
				//查转件表,如果有转件，则用转件表快递信息
				sql = "select delivery as companyCode,deliverysheetid as outSid "
					+"from trandelivery with(nolock) where orideliverysheetid='"+dispatchNo+"'" ;
				Hashtable httrancompany=SQLHelper.oneRowSelect(conn, sql);
				
	
				expressCompanyName =getExpressCompanyName(httrancompany.get("companyCode").toString()) ;
				dispatchNo = httrancompany.get("outSid").toString() ;
			}
					
					
			StringBuffer lcData = new StringBuffer() ;
			lcData.append("<updateinfoforsupplier>") ;
			lcData.append("<ordermessages>") ;
			lcData.append("<ordermessage>") ;
			lcData.append("<formcode>").append(ordercode).append("</formcode>");
			lcData.append("<status>").append(orderStatus).append("</status>") ;
			lcData.append("<rejectreasoncode>").append(rejectReasonCode).append("</rejectreasoncode>") ;
			lcData.append("<operatetime>").append(distriTime).append("</operatetime>") ;
			lcData.append("<expresscompanyname>").append(expressCompanyName).append("</expresscompanyname>") ;
			lcData.append("<dispatchno>").append(dispatchNo).append("</dispatchno>") ;
			lcData.append("</ordermessage>") ;
			lcData.append("</ordermessages>") ;
			lcData.append("</updateinfoforsupplier>") ;
			
			StringBuffer postData = new StringBuffer() ;
			postData.append("lcdata=");
			postData.append(URLEncoder.encode(DesUtil.DesEncode(lcData.toString(),strkey,striv), "UTF-8")) ;
			postData.append("&supplierid=");
			postData.append(SupplierId);
			
			
			String responsexml = CommHelper.getResponseData(url, postData.toString()) ;
			
			responsexml = DesUtil.DesDecode(URLDecoder.decode(responsexml,"UTF-8"), strkey,striv) ;

			Document resultdoc = DOMHelper.newDocument(responsexml);
			Element resultelement=resultdoc.getDocumentElement();
			
			Element result = (Element) resultelement.getElementsByTagName("result").item(0) ;
			
			String resultcode = DOMHelper.getSubElementVauleByName(result, "resultcode").trim() ;
				
			if(resultcode.equalsIgnoreCase("TRUE"))
			{
				htresult.put("ordercode", ordercode) ;
				htresult.put("resultflag", "1") ;
				htresult.put("msg", "") ;
				
				Log.info("更新vjia订单配送结果成功,状态:"+status + ",订单号:"+ordercode) ;
			}				
			else
			{
				htresult.put("ordercode", ordercode) ;
				htresult.put("resultflag", "0") ;
				htresult.put("msg", getReasonByErroeCode(resultcode)) ;
				
				Log.warn("更新配送结果失败,订单号:"+ordercode+",错误信息:"+getReasonByErroeCode(resultcode)) ;
			}

			resultlist.add(htresult);
		}
	
		
		return resultlist ;
	}
	
	//vjia返回错误信息
	private static String getReasonByErroeCode(String errorCode)
	{
		String errMsg="";
		
		if (errorCode.equalsIgnoreCase("B0"))
		{
			errMsg="非法的订单号";
		} else if (errorCode.equalsIgnoreCase("B2"))
		{
			errMsg="不能进行操作，当前状态：配送中";
		} else if (errorCode.equalsIgnoreCase("B3"))
		{
			errMsg="不能进行操作，当前状态：妥投";
		}else if (errorCode.equalsIgnoreCase("B5"))
		{
			errMsg="不能进行操作，当前状态：拒收";
		}else if (errorCode.equalsIgnoreCase("B6"))
		{
			errMsg="不能进行操作，当前状态：退换货入库";
		}else if (errorCode.equalsIgnoreCase("B7"))
		{
			errMsg="不能进行操作，当前状态：拒收入库";
		}else if (errorCode.equalsIgnoreCase("S1"))
		{
			errMsg="非法的XML格式";
		}else if (errorCode.equalsIgnoreCase("S2"))
		{
			errMsg="非法的数字签名";
		}else if (errorCode.equalsIgnoreCase("S3"))
		{
			errMsg="非法的SP";
		}else if (errorCode.equalsIgnoreCase("S5"))
		{
			errMsg="非法的内容";
		}else if (errorCode.equalsIgnoreCase("S6"))
		{
			errMsg="服务器处理错误";
		}else if (errorCode.equalsIgnoreCase("S7"))
		{
			errMsg="字符串长度超出规定长度";
		}
	
		return errMsg ;

	}
	
	private static String getExpressCompanyName(String companycode)
	{
		String companyname="";
		String companys = "EMS:EMS;HTKY:汇通快运;POST:中国邮政平邮;SF:顺丰速运;STO:申通E物流;YTO:圆通速递" ;
		String company[] = companys.split(";") ;
		for(int i = 0 ; i < company.length ; i++)
		{
			String s[] = company[i].split(":") ;
			if (s[0].equalsIgnoreCase(companycode))
			{
				companyname=s[1];
				break;
			}
		}
		return companyname;
	}
}
