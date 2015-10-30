package com.wofu.ecommerce.amazon;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.StreamUtil;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class AmazonUtil {

	public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		XMLGregorianCalendar gc = null;
		try {
			gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return gc;
	}

	public static Date convertToDate(XMLGregorianCalendar cal) throws Exception {
		GregorianCalendar ca = cal.toGregorianCalendar();
		return ca.getTime();
	}

	public static String computeContentMD5HeaderValue(FileInputStream is)
			throws IOException, NoSuchAlgorithmException {

		DigestInputStream dis = new DigestInputStream(is, MessageDigest
				.getInstance("MD5"));

		byte[] buffer = new byte[8192];
		while (dis.read(buffer) > 0);

		String md5Content = new String(org.apache.commons.codec.binary.Base64
				.encodeBase64(dis.getMessageDigest().digest()));

		is.getChannel().position(0);

		return md5Content;
	}
	
	public static List<Map<String,String>> getSkuInfo(String serviceurl,String accesskeyid,
			String secretaccesskey,String applicationname,String applicationversion,
			String sellerid,String marketplaceid) throws Exception
	{
		
		Vector<Map<String,String>> skulist=new Vector<Map<String,String>>();
		
		String reportrequestid=StockUtils.getSkuReportRequestID(serviceurl,accesskeyid,
				secretaccesskey,applicationname,applicationversion,
				sellerid,marketplaceid);
		
		Log.info("ȡ��Ʒ�б�������ID:"+reportrequestid);
		
		String reportid="";
		
		while (reportid.equals(""))
		{
			reportid=StockUtils.getSkuReportID(serviceurl,accesskeyid,
				secretaccesskey,applicationname,applicationversion,
				sellerid,marketplaceid,reportrequestid);
			
			if (reportid.equals(""))
			{
				Log.info("��Ʒ����δ����,�����ĵȴ�һ����....");
				Thread.sleep(60000L);  //����δ����,�ȴ�һ����
			}
		}
		
		Log.info("ȡ��Ʒ�б���ID:"+reportid);
		
		InputStream ism=StockUtils.getSkuReport(serviceurl,accesskeyid,
				secretaccesskey,applicationname,applicationversion,
				sellerid,marketplaceid,reportid);
		
		
		//FileInputStream ism=new FileInputStream(new File("skuReport.plt"));
		
		String skustr=StreamUtil.InputStreamToStr(ism, "GBK");
		
		Object[] values=StringUtil.split(skustr, "\r\n").toArray();
		
		for (int i=1;i<values.length;i++)  //ȥ����һ��
		{
			String linestr=(String) values[i];  //ÿ����ϢΪ SKU,ASIN,�۸�,����
			
			Object[] skuinfo=StringUtil.split(linestr, "	").toArray();
		
			String sku=(String) skuinfo[0];
			String qty=(String) skuinfo[3];
			
			Hashtable<String,String> htsku=new Hashtable<String,String>();
			
			htsku.put("sku", sku);
			htsku.put("qty", qty);
			
			skulist.add(htsku);
		}
		
		return skulist;
	}
}
