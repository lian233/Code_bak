/**
 * ems快递打单，返回快递单号
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

	private String sysAccount="";   //大客户帐号
	private String password="";    //大客户密码
	private String sender="";    //大客户密码
	private String senderTel="";    //大客户密码
	private String senderAddress="";    //大客户密码
	private String url="";    //大客户密码
	private String url1="";    //大客户密码
	private String senderPost="";    //公司邮编
	private String senderComp="";    //公司名称
	
	private static String jobName="取ems快递单号";

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
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"回滚事务失败");
				Log.error(jobName, e1.getMessage());
			}
			
			try{
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			}catch(Exception ex){
				Log.error(jobName,"更新任务信息失败");
				Log.error(jobName, ex.getMessage());
			}
			Log.error(jobName,"错误信息:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
		} finally {
			
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"更新处理标志失败");
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}
		
	
	}
	
	
	// 打印ems快递信息
	private boolean printEmsExpressInfo(String sheetid,String billId) throws Exception {
		HashMap<String,Object> map = new HashMap();
    	StringBuilder sendInfo = new StringBuilder();
    	StringBuilder sql = new StringBuilder();
    	ArrayList<String> arr = new ArrayList();
    	boolean sendResult = false;
			try{
				//拼装中国邮政自助系统所需要的xml文件
		    	sendInfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
		    		.append("<XMLInfo>").append("<sysAccount>")
		    		.append(sysAccount).append("</sysAccount>")  //大客户号   商家的在ems自助系统的帐号
		    		.append("<passWord>").append(MD5Util.getMD5Code((password).getBytes())).append("</passWord>")//大客户密码   商家的在ems自助系统的密码 
		    		.append("<appKey></appKey>")
		    		.append("<printKind>2</printKind>")  //打印类型  1为五联单打印，2为热敏打印
		    		.append("<printDatas><printData>")
		    		.append("<bigAccountDataId>").append(sheetid).append("</bigAccountDataId>")
		    		.append("<billno>").append(billId).append("</billno>")
		    		//寄件人信息
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
		    		//处理联系电话，可能包括了固话
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
		    		//收件人信息
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
		    		sendInfo.append("<cargoType>物品</cargoType>")
		    		.append("<remark>").append(note).append("</remark>")
		    		.append("</printData></printDatas></XMLInfo>");
		    		//Log.info("xml: "+sendInfo.toString());
		    		String xml= new BASE64Encoder().encode(sendInfo.toString().getBytes("utf-8"));
		    		//Log.info("解密数据为:　"+new String(new BASE64Decoder().decodeBuffer(xml),"utf-8"));
		    		//Log.info("base64Data: "+URLEncoder.encode(xml,"utf-8"));
		    		map.put("method", "updatePrintDatas");
		    		map.put("xml", URLEncoder.encode(xml,"utf-8"));
		    		String res = PublicUtils.sendGetRequst(map, url);
		    		Log.info("回传ems邮政快递信息结果: "+new String(new BASE64Decoder().decodeBuffer(res),"utf-8"));
		    		res = new String(new BASE64Decoder().decodeBuffer(res),"utf-8");
		    		Document doc = DOMHelper.newDocument(res,"utf-8");
		    		Element docEle = doc.getDocumentElement();
		    		String success = DOMHelper.getSubElementVauleByName(docEle, "result");
		    		//Log.info("回传快递信息返回数据:　"+success);
		    		if(success.equals("1")) {
		    			sendResult=true;
		    			Log.info("回传ems邮政快递信息成功,快递单号: "+billId,"单据号: "+sheetid);
		    		}
		    		
			}catch(Exception ex){
				Log.error("回传ems邮政快递信息失败,快递单号: "+billId,"单据号: "+sheetid);
				Log.warn("回传ems快递信息失败" + Log.getErrorMessage(ex));
				throw new Exception(ex);
			}
			return sendResult;
			
	}
	
	//处理表emsOrderInfo的数据，把取到运单号写入到outstock0，outstocknote表
	private void getEmsStanderExpressId() throws Exception {
		HashMap<String,Object> map = new HashMap();
		//获取标准快递单号   福建省内
		String sql = "select top 10 sheetid from outstock0  where  delivery='ems'  and flag>=10 and address like '福建%' and ISNULL(DeliverySheetID,'')=''";
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
    	//Log.info("解密数据为:　"+new String(new BASE64Decoder().decodeBuffer(xml),"utf-8"));
    	//Log.info("base64Data: "+URLEncoder.encode(xml,"utf-8"));
    	map.put("method", "getBillNumBySys");
    	map.put("xml", URLEncoder.encode(xml,"utf-8"));
    	String res = PublicUtils.sendGetRequst(map, url1);
    	res = new String(new BASE64Decoder().decodeBuffer(res),"utf-8");
    	Log.info("回传ems邮政快递信息结果:　"+res);
    	Document doc = DOMHelper.newDocument(res, "utf-8");
    	Element ele = doc.getDocumentElement();
    	String success = DOMHelper.getSubElementVauleByName(ele,"result");
    	//Log.info("result: "+success);
    	if(success.equals("1")){  //取运单号成功
    			Element[] eles = DOMHelper.getSubElements(ele.getElementsByTagName("assignIds").item(0));
    			for(int i=0;i<eles.length;i++){
    				String id= DOMHelper.getSubElementVauleByName(eles[i], "billno");
    				Log.info("取得运单号id:　"+id);
    				arr.add(id);
    			}
    	}
    	if(arr.size()>0){
    		for(int j=0;j<arr.size();j++){
    			sqls.add(new StringBuilder().append("update outstock0 set DeliverySheetID='").append(arr.get(j)).append("' where sheetid='")
    					.append(sheetids.get(j)).append("'").toString());
    			sqls.add(new StringBuilder().append("update outstocknote set DeliverySheetID='").append(arr.get(j)).append("' where sheetid='")
    					.append(sheetids.get(j)).append("'").toString());
    			//回传打印信息到EMS自助系统
    			if(printEmsExpressInfo((String)sheetids.get(j),arr.get(j))) {
    				SQLHelper.executeBatch(this.getExtdao().getConnection(), sqls);
    				Log.info("更新ems邮政快递完成,单据编号: "+sheetids.get(j)+"运单号: "+arr.get(j));
    			}
    			Thread.sleep(1000L);
    		}
    	}
    	
			
	}
	
	
	//处理表emsOrderInfo的数据，把取到运单号写入到outstock0，outstocknote表
	private void getEmsExpressId() throws Exception {
		HashMap<String,Object> map = new HashMap();
		//获取经济快递单号   外省
		String sql = "select top 10 sheetid from outstock0  where  delivery='ems'  and flag>=10 and address not like '福建%' and ISNULL(DeliverySheetID,'')=''";
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
    	//Log.info("解密数据为:　"+new String(new BASE64Decoder().decodeBuffer(xml),"utf-8"));
    	//Log.info("base64Data: "+URLEncoder.encode(xml,"utf-8"));
    	map.put("method", "getBillNumBySys");
    	map.put("xml", URLEncoder.encode(xml,"utf-8"));
    	String res = PublicUtils.sendGetRequst(map, url1);
    	res = new String(new BASE64Decoder().decodeBuffer(res),"utf-8");
    	//Log.info("返回数据为:　"+res);
    	Document doc = DOMHelper.newDocument(res, "utf-8");
    	Element ele = doc.getDocumentElement();
    	String success = DOMHelper.getSubElementVauleByName(ele,"result");
    	//Log.info("result: "+success);
    	if(success.equals("1")){  //取运单号成功
    			Element[] eles = DOMHelper.getSubElements(ele.getElementsByTagName("assignIds").item(0));
    			for(int i=0;i<eles.length;i++){
    				String id= DOMHelper.getSubElementVauleByName(eles[i], "billno");
    				Log.info("取得运单号id:　"+id);
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
