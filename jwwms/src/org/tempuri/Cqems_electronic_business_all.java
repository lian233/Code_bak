package org.tempuri;

import java.net.URLEncoder;

import sun.misc.BASE64Encoder;

/**
 * EMS接收电商信息含运单
 * @author Administrator
 *
 */
public class Cqems_electronic_business_all {
	
	public static String cqems_electronic_business_all(String xmlstring,String emscode) throws Exception{
		Cqemsbusiness cqemsbusiness = new Cqemsbusiness();
		CqemsbusinessSoap cqemsbusinessSoap =  cqemsbusiness.getCqemsbusinessSoap12();
		return cqemsbusinessSoap.cqemsElectronicBusinessAll(URLEncoder.encode(new BASE64Encoder().encode(xmlstring.getBytes("utf-8")),"utf-8"), emscode);
		
	}
}
