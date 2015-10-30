/**
 * ems��ݴ򵥣����ؿ�ݵ���
 */
package com.wofu.ecommerce.taobao;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.util.PublicUtils;
public class CheckEmsExpressNoExecuter extends Executer {

	private String sysAccount="";   //��ͻ��ʺ�
	private String password="";    //��ͻ�����
	private String sender="";    //��ͻ�����
	private String senderTel="";    //��ͻ�����
	private String senderAddress="";    //��ͻ�����
	private String url="";    //��ͻ�����
	private String url1="";    //��ͻ�����
	private String senderPost="";    //��˾�ʱ�
	private String senderComp="";    //��˾����
	
	private static String jobName="ȡems��ݵ���";

	@Override
	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		sysAccount=prop.getProperty("sysAccount");
		password=prop.getProperty("password");
		sender=prop.getProperty("sender");
		senderTel=prop.getProperty("senderTel");
		senderAddress=prop.getProperty("senderAddress");
		url=prop.getProperty("url");
		url1=prop.getProperty("url1");
		senderPost=prop.getProperty("senderPost");
		senderComp=prop.getProperty("senderComp");
		
		try {			 

			updateJobFlag(1);
			getEmsStanderExpressId();
			getEmsExpressId();
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
				Log.error(jobName, e1.getMessage());
			}
			
			try{
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			}catch(Exception ex){
				Log.error(jobName,"����������Ϣʧ��");
				Log.error(jobName, ex.getMessage());
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
		} finally {
			
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"���´����־ʧ��");
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
	
	}
	
	
	// ��ӡems�����Ϣ
	private boolean printEmsExpressInfo(String sheetid,String billId) throws Exception {
		HashMap<String,Object> map = new HashMap();
    	StringBuilder sendInfo = new StringBuilder();
    	StringBuilder sql = new StringBuilder();
    	ArrayList<String> arr = new ArrayList();
    	boolean sendResult = false;
			try{
				//ƴװ�й���������ϵͳ����Ҫ��xml�ļ�
		    	sendInfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
		    		.append("<XMLInfo>").append("<sysAccount>")
		    		.append(sysAccount).append("</sysAccount>")  //��ͻ���   �̼ҵ���ems����ϵͳ���ʺ�
		    		.append("<passWord>").append(MD5Util.getMD5Code((password).getBytes())).append("</passWord>")//��ͻ�����   �̼ҵ���ems����ϵͳ������ 
		    		.append("<appKey></appKey>")
		    		.append("<printKind>2</printKind>")  //��ӡ����  1Ϊ��������ӡ��2Ϊ������ӡ
		    		.append("<printDatas><printData>")
		    		.append("<bigAccountDataId>").append(sheetid).append("</bigAccountDataId>")
		    		.append("<billno>").append(billId).append("</billno>")
		    		//�ļ�����Ϣ
		    		.append("<scontactor>").append(sender).append("</scontactor>")
		    		.append("<scustMobile>").append(senderTel).append("</scustMobile>")
		    		.append("<scustPost>").append(senderPost).append("</scustPost>")
		    		.append("<scustAddr>").append(senderAddress).append("</scustAddr>");
		    		//.append("<scustComp>").append(senderComp).append("<scustComp>");
		    	sql.append("select sheetid,LinkMan, tele,Address,ZipCode,note from outstock0  where  sheetid='").append(sheetid).append("'");
		    	Hashtable result = this.getExtdao().oneRowSelect(sql.toString());
		    		String LinkMan = result.get("LinkMan").toString();
		    		String tcustMobile = result.get("tele").toString();
		    		String note = result.get("note").toString();
		    		//������ϵ�绰�����ܰ����˹̻�
		    		if(tcustMobile.indexOf(" ")!=-1) {
		    			tcustMobile=tcustMobile.substring(0,tcustMobile.indexOf(" "));
		    		}
		    		
		    		String addresses = result.get("Address").toString();
		    		String ZipCode = result.get("ZipCode").toString();
		    		String[] add = addresses.split(" ");
		    		String province = add[0];
		    		String city = add[1];
		    		String county = add[2];
		    		sql.delete(0, sql.length());
		    		//�ռ�����Ϣ
		    		sendInfo.append("<tcontactor>").append(LinkMan).append("</tcontactor>")
		    		.append("<tcustMobile>").append(tcustMobile).append("</tcustMobile>")
		    		.append("<tcustPost>").append(ZipCode).append("</tcustPost>")
		    		.append("<tcustAddr>").append(addresses).append("</tcustAddr>")
		    		.append("<tcustProvince>").append(province).append("</tcustProvince>")
		    		.append("<tcustCity>").append(city).append("</tcustCity>")
		    		.append("<tcustCounty>").append(county).append("</tcustCounty>")
		    		.append("<weight>").append("0.5000").append("</weight>")
		    		.append("<length>").append("1.0000").append("</length>")
		    		.append("<insure>").append("1.0000").append("</insure>");
		    		Vector orderitem = this.getExtdao().multiRowSelect(new StringBuilder().append("select title from outstockitem0 where sheetid='").append(sheetid).append("'").toString());
		    		String[] cargoDescs=new String[orderitem.size()];
		    		for(int k=0;k<orderitem.size();k++){
		    			Hashtable item =(Hashtable)orderitem.get(k);
		    			String cargoDesc=k==0?"cargoDesc":"cargoDesc"+k;
		    			sendInfo.append("<").append(cargoDesc).append(">")
		    			.append(item.get("title").toString())
		    			.append("</").append(cargoDesc).append(">");
		    		}
		    		sendInfo.append("<cargoType>��Ʒ</cargoType>")
		    		.append("<remark>").append(note).append("</remark>")
		    		.append("</printData></printDatas></XMLInfo>");
		    		//Log.info("xml: "+sendInfo.toString());
		    		String xml= new BASE64Encoder().encode(sendInfo.toString().getBytes("utf-8"));
		    		//Log.info("��������Ϊ:��"+new String(new BASE64Decoder().decodeBuffer(xml),"utf-8"));
		    		//Log.info("base64Data: "+URLEncoder.encode(xml,"utf-8"));
		    		map.put("method", "updatePrintDatas");
		    		map.put("xml", URLEncoder.encode(xml,"utf-8"));
		    		String res = PublicUtils.sendGetRequst(map, url);
		    		Log.info("�ش�ems���������Ϣ���: "+new String(new BASE64Decoder().decodeBuffer(res),"utf-8"));
		    		res = new String(new BASE64Decoder().decodeBuffer(res),"utf-8");
		    		Document doc = DOMHelper.newDocument(res,"utf-8");
		    		Element docEle = doc.getDocumentElement();
		    		String success = DOMHelper.getSubElementVauleByName(docEle, "result");
		    		//Log.info("�ش������Ϣ��������:��"+success);
		    		if(success.equals("1")) {
		    			sendResult=true;
		    			Log.info("�ش�ems���������Ϣ�ɹ�,��ݵ���: "+billId,"���ݺ�: "+sheetid);
		    		}
		    		
			}catch(Exception ex){
				Log.error("�ش�ems���������Ϣʧ��,��ݵ���: "+billId,"���ݺ�: "+sheetid);
				Log.warn("�ش�ems�����Ϣʧ��" + Log.getErrorMessage(ex));
				throw new Exception(ex);
			}
			return sendResult;
			
	}
	
	//�����emsOrderInfo�����ݣ���ȡ���˵���д�뵽outstock0��outstocknote��
	private void getEmsStanderExpressId() throws Exception {
		HashMap<String,Object> map = new HashMap();
		//��ȡ��׼��ݵ���   ����ʡ��
		String sql = "select top 10 sheetid from outstock0  where  delivery='ems'  and flag>=10 and address like '����%' and ISNULL(DeliverySheetID,'')=''";
		List sheetids = this.getExtdao().oneListSelect(sql);
		if(sheetids.size()==0) return;
		StringBuffer sendInfo = new StringBuffer();
		ArrayList<String> arr = new ArrayList();
		ArrayList<String> sqls = new ArrayList();
		sendInfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
		.append("<XMLInfo>")
		.append("<sysAccount>").append(sysAccount).append("</sysAccount>")
		.append("<passWord>").append(MD5Util.getMD5Code((password).getBytes())).append("</passWord>")
		.append("<appKey></appKey>")
		.append("<businessType>1</businessType>")
		.append("<billNoAmount>").append(sheetids.size()).append("</billNoAmount>")
		.append("</XMLInfo>");
		Log.info("xml: "+sendInfo.toString());
    	String xml= new BASE64Encoder().encode(sendInfo.toString().getBytes("utf-8"));
    	//Log.info("��������Ϊ:��"+new String(new BASE64Decoder().decodeBuffer(xml),"utf-8"));
    	//Log.info("base64Data: "+URLEncoder.encode(xml,"utf-8"));
    	map.put("method", "getBillNumBySys");
    	map.put("xml", URLEncoder.encode(xml,"utf-8"));
    	String res = PublicUtils.sendGetRequst(map, url1);
    	res = new String(new BASE64Decoder().decodeBuffer(res),"utf-8");
    	Log.info("�ش�ems���������Ϣ���:��"+res);
    	Document doc = DOMHelper.newDocument(res, "utf-8");
    	Element ele = doc.getDocumentElement();
    	String success = DOMHelper.getSubElementVauleByName(ele,"result");
    	//Log.info("result: "+success);
    	if(success.equals("1")){  //ȡ�˵��ųɹ�
    			Element[] eles = DOMHelper.getSubElements(ele.getElementsByTagName("assignIds").item(0));
    			for(int i=0;i<eles.length;i++){
    				String id= DOMHelper.getSubElementVauleByName(eles[i], "billno");
    				Log.info("ȡ���˵���id:��"+id);
    				arr.add(id);
    			}
    	}
    	if(arr.size()>0){
    		for(int j=0;j<arr.size();j++){
    			sqls.add(new StringBuilder().append("update outstock0 set DeliverySheetID='").append(arr.get(j)).append("' where sheetid='")
    					.append(sheetids.get(j)).append("'").toString());
    			sqls.add(new StringBuilder().append("update outstocknote set DeliverySheetID='").append(arr.get(j)).append("' where sheetid='")
    					.append(sheetids.get(j)).append("'").toString());
    			//�ش���ӡ��Ϣ��EMS����ϵͳ
    			if(printEmsExpressInfo((String)sheetids.get(j),arr.get(j))) {
    				SQLHelper.executeBatch(this.getExtdao().getConnection(), sqls);
    				Log.info("����ems����������,���ݱ��: "+sheetids.get(j)+"�˵���: "+arr.get(j));
    			}
    			Thread.sleep(1000L);
    		}
    	}
    	
			
	}
	
	
	//�����emsOrderInfo�����ݣ���ȡ���˵���д�뵽outstock0��outstocknote��
	private void getEmsExpressId() throws Exception {
		HashMap<String,Object> map = new HashMap();
		//��ȡ���ÿ�ݵ���   ��ʡ
		String sql = "select top 10 sheetid from outstock0  where  delivery='ems'  and flag>=10 and address not like '����%' and ISNULL(DeliverySheetID,'')=''";
		List sheetids = this.getExtdao().oneListSelect(sql);
		if(sheetids.size()==0) return;
		StringBuffer sendInfo = new StringBuffer();
		ArrayList<String> arr = new ArrayList();
		ArrayList<String> sqls = new ArrayList();
		sendInfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
		.append("<XMLInfo>")
		.append("<sysAccount>").append(sysAccount).append("</sysAccount>")
		.append("<passWord>").append(MD5Util.getMD5Code((password).getBytes())).append("</passWord>")
		.append("<appKey></appKey>")
		.append("<businessType>4</businessType>")
		.append("<billNoAmount>").append(sheetids.size()).append("</billNoAmount>")
		.append("</XMLInfo>");
		Log.info("xml: "+sendInfo.toString());
    	String xml= new BASE64Encoder().encode(sendInfo.toString().getBytes("utf-8"));
    	//Log.info("��������Ϊ:��"+new String(new BASE64Decoder().decodeBuffer(xml),"utf-8"));
    	//Log.info("base64Data: "+URLEncoder.encode(xml,"utf-8"));
    	map.put("method", "getBillNumBySys");
    	map.put("xml", URLEncoder.encode(xml,"utf-8"));
    	String res = PublicUtils.sendGetRequst(map, url1);
    	res = new String(new BASE64Decoder().decodeBuffer(res),"utf-8");
    	//Log.info("��������Ϊ:��"+res);
    	Document doc = DOMHelper.newDocument(res, "utf-8");
    	Element ele = doc.getDocumentElement();
    	String success = DOMHelper.getSubElementVauleByName(ele,"result");
    	//Log.info("result: "+success);
    	if(success.equals("1")){  //ȡ�˵��ųɹ�
    			Element[] eles = DOMHelper.getSubElements(ele.getElementsByTagName("assignIds").item(0));
    			for(int i=0;i<eles.length;i++){
    				String id= DOMHelper.getSubElementVauleByName(eles[i], "billno");
    				Log.info("ȡ���˵���id:��"+id);
    				arr.add(id);
    			}
    	}
    	if(arr.size()>0){
    		for(int j=0;j<arr.size();j++){
    			sqls.add(new StringBuilder().append("update outstock0 set DeliverySheetID='").append(arr.get(j)).append("' where sheetid='")
    					.append(sheetids.get(j)).append("'").toString());
    			sqls.add(new StringBuilder().append("update outstocknote set DeliverySheetID='").append(arr.get(j)).append("' where sheetid='")
    					.append(sheetids.get(j)).append("'").toString());
    			if(printEmsExpressInfo((String)sheetids.get(j),arr.get(j))) {
    				SQLHelper.executeBatch(this.getExtdao().getConnection(), sqls);
    				sqls.clear();
    			}
    			Thread.sleep(1000L);
    		}
    	}
			
	}
	

	
		
}
