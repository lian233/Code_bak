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


	//����vjia���ͽ��
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
			
			String orderStatus = "" ;//�������ͽ��״̬ A3:��Ͷ A5:����
			String rejectReasonCode = "" ;//vjia����ԭ����� N27:����
			
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
				htresult.put("msg", "δ֪����״̬:"+status) ;
				resultlist.add(htresult) ;
				Log.error("����vjia�������ͽ��", "����ʧ�ܣ�δ֪����״̬:"+status) ;
				continue ;
			}
				

			
			String sql = "select top 1 proctime from ecs_deliverynote with(nolock) where ordercode='"+ordercode+"' order by serialid desc" ;
			String distriTime = SQLHelper.strSelect(conn, sql) ;//���ͳɹ�ʱ��
				
			sql = "select companyCode,outSid from ns_delivery with(nolock) "
				+"where tid='"+ordercode+"' and sheettype=3" ;
			
			Hashtable htcompany=SQLHelper.oneRowSelect(conn, sql);
	
			String expressCompanyName = getExpressCompanyName(htcompany.get("companyCode").toString()) ;
			String dispatchNo = htcompany.get("outSid").toString() ;
	
			sql = "select count(*) from trandelivery with(nolock) where orideliverysheetid='"+dispatchNo+"'" ;
			if (SQLHelper.intSelect(conn, sql)>0)
			{				
				//��ת����,�����ת��������ת��������Ϣ
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
				
				Log.info("����vjia�������ͽ���ɹ�,״̬:"+status + ",������:"+ordercode) ;
			}				
			else
			{
				htresult.put("ordercode", ordercode) ;
				htresult.put("resultflag", "0") ;
				htresult.put("msg", getReasonByErroeCode(resultcode)) ;
				
				Log.warn("�������ͽ��ʧ��,������:"+ordercode+",������Ϣ:"+getReasonByErroeCode(resultcode)) ;
			}

			resultlist.add(htresult);
		}
	
		
		return resultlist ;
	}
	
	//vjia���ش�����Ϣ
	private static String getReasonByErroeCode(String errorCode)
	{
		String errMsg="";
		
		if (errorCode.equalsIgnoreCase("B0"))
		{
			errMsg="�Ƿ��Ķ�����";
		} else if (errorCode.equalsIgnoreCase("B2"))
		{
			errMsg="���ܽ��в�������ǰ״̬��������";
		} else if (errorCode.equalsIgnoreCase("B3"))
		{
			errMsg="���ܽ��в�������ǰ״̬����Ͷ";
		}else if (errorCode.equalsIgnoreCase("B5"))
		{
			errMsg="���ܽ��в�������ǰ״̬������";
		}else if (errorCode.equalsIgnoreCase("B6"))
		{
			errMsg="���ܽ��в�������ǰ״̬���˻������";
		}else if (errorCode.equalsIgnoreCase("B7"))
		{
			errMsg="���ܽ��в�������ǰ״̬���������";
		}else if (errorCode.equalsIgnoreCase("S1"))
		{
			errMsg="�Ƿ���XML��ʽ";
		}else if (errorCode.equalsIgnoreCase("S2"))
		{
			errMsg="�Ƿ�������ǩ��";
		}else if (errorCode.equalsIgnoreCase("S3"))
		{
			errMsg="�Ƿ���SP";
		}else if (errorCode.equalsIgnoreCase("S5"))
		{
			errMsg="�Ƿ�������";
		}else if (errorCode.equalsIgnoreCase("S6"))
		{
			errMsg="�������������";
		}else if (errorCode.equalsIgnoreCase("S7"))
		{
			errMsg="�ַ������ȳ����涨����";
		}
	
		return errMsg ;

	}
	
	private static String getExpressCompanyName(String companycode)
	{
		String companyname="";
		String companys = "EMS:EMS;HTKY:��ͨ����;POST:�й�����ƽ��;SF:˳������;STO:��ͨE����;YTO:Բͨ�ٵ�" ;
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
