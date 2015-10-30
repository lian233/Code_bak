package com.wofu.ecommerce.vjia;
import java.util.Iterator;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
public class SoapServiceClient {

	private SoapHeader soapheader = null;

	private SoapBody soapbody = null;

	private String url;
	
	public SoapServiceClient()
	{
		
	}
	public SoapServiceClient(String url,SoapHeader header,SoapBody body)
	{
		this.setSoapbody(soapbody);
		this.setSoapheader(soapheader);
		this.setUrl(url);
	}
	public SoapBody getSoapbody() {
		return soapbody;
	}

	public void setSoapbody(SoapBody soapbody) {
		this.soapbody = soapbody;
	}

	public SoapHeader getSoapheader() {
		return soapheader;
	}

	public void setSoapheader(SoapHeader soapheader) {
		this.soapheader = soapheader;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String request() throws Exception {
		SOAPConnection connection=null;
		
		try
		{
			SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
			connection = soapConnFactory.createConnection();
	
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage message = messageFactory.createMessage();
	
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			SOAPBody body = envelope.getBody();
	
			SOAPElement bodyElement = body.addChildElement(envelope.createName(
					this.soapbody.getRequestname(), "", this.soapbody.getUri()));
			for (Iterator it=this.soapbody.getBodyParams().keySet().iterator();it.hasNext();)
			{
				String paramname=(String) it.next();
				String paramvlaue=this.soapbody.getBodyParams().get(paramname).toString();
				bodyElement.addChildElement(paramname).addTextNode(paramvlaue);
			}
	
			SOAPHeader header = envelope.getHeader();
			SOAPElement headerelement = header.addChildElement(envelope.createName(
					this.soapheader.getHeadername(), "", this.soapheader.getUri()));
			for (Iterator it=this.soapheader.getHeader().keySet().iterator();it.hasNext();)
			{

				String headername=(String) it.next();
				String headervalue=this.soapheader.getHeader().get(headername).toString();
				
				headerelement.addChildElement(headername).addTextNode(headervalue);
			}
			
			message.saveChanges();
			SOAPMessage responsemessage = connection.call(message,this.url);
		 
	
			SOAPElement soapbody = responsemessage.getSOAPBody();
	
			SOAPElement responseelement = (SOAPElement) soapbody
					.getChildElements().next();
			String result = responseelement.getChildNodes().item(0)
					.getTextContent();
			return result;
		}
		finally
		{
			connection.close();
		}
	}

}
