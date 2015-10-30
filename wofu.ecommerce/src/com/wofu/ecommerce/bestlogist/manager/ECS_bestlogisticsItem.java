package com.wofu.ecommerce.bestlogist.manager;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.util.BusinessObject;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.intf.best.BestUtil;
import com.wofu.intf.best.CommHelper;
public class ECS_bestlogisticsItem extends BusinessObject{
	private String sku;
	private int normalQty;
	private int defQty;
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public int getNormalQty() {
		return normalQty;
	}
	public void setNormalQty(int normalQty) {
		this.normalQty = normalQty;
	}
	public int getDefQty() {
		return defQty;
	}
	public void setDefQty(int defQty) {
		this.defQty = defQty;
	}
	
	//查找特定的sku-批量
	public void find() throws Exception{
		StringBuilder bizData=null;
		String note =null;
		try{
			String reqData = this.getReqData();
			Properties per = StringUtil.getIniProperties(reqData);
			String skus = per.getProperty("sku","");
			if("".equals(skus)) new Exception("请输入商品的sku编码");
			String sql = "select url,appkey,appsecret,token,refreshtoken,gshopid,supplierkey,uid,webserviceurl,uname from ecs_org_params where orgid=300";
			Hashtable ht = this.getDao().oneRowSelect(sql);
			if(ht==null) throw new Exception("未配置百世仓库参数");
			String url = ht.get("url").toString();
			String partnerid = ht.get("appkey").toString();
			String partnerkey = ht.get("appsecret").toString();
			String serviceversion = ht.get("token").toString();
			String msgtype = ht.get("refreshtoken").toString();
			String callbackurl = ht.get("gshopid").toString();
			String customerCode = ht.get("supplierkey").toString();
			String warehouseCode = ht.get("uid").toString();
			String interfacesystem = ht.get("uname").toString();
			String[] items = skus.split(",");
			bizData = new StringBuilder();
			bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
				.append("<GetProductInventory>")
				.append("<customerCode>").append(customerCode)
				.append("</customerCode>")
				.append("<warehouseCode>")
				.append(warehouseCode)
				.append("</warehouseCode>")
				.append("<products>");
			for(String e:items){
				bizData.append("<product>").append(e).append("</product>");
			}
			bizData.append("</products></GetProductInventory>");
			String msgId= UUID.randomUUID().toString();
			String sign=BestUtil.makeSign(BestUtil.makeSignParams(bizData.toString(), "GetProductInventory",msgtype,
					partnerid,partnerkey,serviceversion,callbackurl,msgId));
			
			Map requestParams=BestUtil.makeRequestParams(bizData.toString(), "GetProductInventory", 
					msgId, msgtype, sign,callbackurl,
					serviceversion,partnerid);

			String result=CommHelper.sendRequest(url, requestParams, "");
			String rspBizData=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
			rspBizData = BestUtil.filterChar(rspBizData);
			Log.info("result:　"+rspBizData);
			Document doc = DOMHelper.newDocument(rspBizData,"GBK");
			Element ele = doc.getDocumentElement();
			String flag = DOMHelper.getSubElementVauleByName(ele, "flag");
			if("FAILURE".equalsIgnoreCase(flag)){
				note = DOMHelper.getSubElementVauleByName(ele,"note");
				Element errs = DOMHelper.getSubElementsByName(ele, "errors")[0];
				Element err = DOMHelper.getSubElementsByName(errs, "error")[0];
				String errmsg = DOMHelper.getSubElementVauleByName(err, "errorDescription");
				throw new Exception(note +" "+errmsg);
			}
		}catch(Exception e){
			Log.info(e.getMessage());
			throw e;
		}finally{
			bizData=null;
			note =null;
		}
		
	}
	
	
}
