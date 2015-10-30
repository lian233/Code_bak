package com.wofu.ecommerce.bestlogist.manager;
import java.sql.Connection;
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
import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.intf.best.BestUtil;
import com.wofu.intf.best.CommHelper;
public class ECS_bestlogistics extends BusinessObject{
	private String orderid;
	private String status;
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	//重新推送已经审核的订单
	public void resendOrderInfo() throws Exception{
		String reqDate = this.getReqData();
		Properties p = StringUtil.getIniProperties(reqDate);
		String sheettype = p.getProperty("sheettype","");
		String sheetid = p.getProperty("sheetid","");
		if("".equals(sheetid)) throw new Exception("单据号不能为空");
		String shoporgid= p.getProperty("shoporgid","");
		if("2209".equals(sheettype)){
			sendCustomerOrderStatus(sheetid,shoporgid);
		}else if("2210".equals(sheettype)){
			sendAsnStatus(sheetid,shoporgid);
		}else if("2222".equals(sheettype)){
			sendRmaStatus(sheetid,shoporgid);
		}
		else{throw new Exception("未知单据类型!");}
	}
	
	//重新取消订单
	public void reCancelOrderInfo() throws Exception{
		String reqDate = this.getReqData();
		Properties p = StringUtil.getIniProperties(reqDate);
		String sheettype = p.getProperty("sheettype","");
		String sheetid = p.getProperty("sheetid","");
		if("".equals(sheetid)) throw new Exception("单据号不能为空");
		String shoporgid= p.getProperty("shoporgid","");
		if("2209".equals(sheettype)){
			reCencelOrder(sheetid,shoporgid);
		}else if("2210".equals(sheettype)){
			new Exception("暂时不能取消来货计划单");
		}else if("2222".equals(sheettype)){
			new Exception("暂时不能取消销售退货单");
		}
		else{throw new Exception("未知单据类型!");}
	}
	
	//重新推送销售退货单
	private void sendRmaStatus(String sheetid, String shoporgid) throws Exception{
		throw new Exception("暂时不能重新推送销售退货单");
		
	}
	//重新推送来货计划单
	private void sendAsnStatus(String sheetid, String shoporgid) throws Exception{
		throw new Exception("暂时不能重新推送来货计划单");
		
	}
	
	//再次取消百世订单
	private void reCencelOrder(String sheetid,String shoporgid) throws Exception{
		String  sql = "select count(*) from customerorder where sheetid='"+sheetid+"' and flag=99";
		if(this.getDao().intSelect(sql)==1) throw new Exception("订单已经取消,请查询订单状态");
		sql= "select count(*) from customerorder where sheetid='"+sheetid+"' and flag=100";
		if(this.getDao().intSelect(sql)!=1) throw new Exception("订单未审核或不存在，请重新审核");
		DataCentre dao =null;
		//"   select url,appkey,appsecret,token,refreshtoken,gshopid,supplierkey,uid,webserviceurl from ecs_org_params with(nolock) where orgid=300";
		sql = "select url,appkey,appsecret,token,refreshtoken,gshopid,supplierkey,uid,webserviceurl,uname from ecs_org_params where orgid=300";
		Hashtable ht = this.getDao().oneRowSelect(sql);
		if(ht.size()==0) throw new Exception("未配置百世仓库参数");
		String url = ht.get("url").toString();
		String partnerid = ht.get("appkey").toString();
		String partnerkey = ht.get("appsecret").toString();
		String serviceversion = ht.get("token").toString();
		String msgtype = ht.get("refreshtoken").toString();
		String callbackurl = ht.get("gshopid").toString();
		String customerCode = ht.get("supplierkey").toString();
		String warehouseCode = ht.get("uid").toString();
		String interfacesystem = ht.get("uname").toString();
		boolean isExitst=true;
		if(!"".equals(shoporgid)) {
			Hashtable result = getDSName(this.getDao().getConnection(),shoporgid);
			String dsname= (String)result.get("dsname");
			warehouseCode= (String)result.get("warehouseCode");
			customerCode= (String)result.get("customercode");
			dao = this.getExtDao(dsname);
		}else{
			dao = this.getExtDao(BestUtil.getDSName(this.getDao().getConnection(), customerCode, warehouseCode));
		}
		sql = "select sheetid from customerdelive0 where refsheetid='"+sheetid+"'";
		String sheetidTemp = dao.strSelect(sql);
		if("".equals(sheetidTemp)){
			throw new Exception("此订单已经处理，请查询订单状态！");
		}
		String result = generOderString(ht,sheetidTemp);
		if("".equals(result)) throw new Exception("请求超时，请稍后再试！");
		String rspBizData=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
		Log.info("rspBizData: "+rspBizData);
		rspBizData = BestUtil.filterChar(rspBizData);
		Document outStockStatusDoc = DOMHelper.newDocument(rspBizData, "GBK");
		Element outStockStatusele = outStockStatusDoc.getDocumentElement();	
		String flag=DOMHelper.getSubElementVauleByName(outStockStatusele, "flag");
		if("success".equalsIgnoreCase(flag)) {
			Element saleorderele=(Element) outStockStatusele.getElementsByTagName("salesOrder").item(0);
			
			String orderCode=DOMHelper.getSubElementVauleByName(saleorderele, "orderCode");
			String orderStatus=DOMHelper.getSubElementVauleByName(saleorderele, "orderStatus");
			if("CANCELED".equals(status))
			throw new Exception("订单已经取消,请查询订单状态");
			else{
				sql = "select count(*) from it_infsheetlist where sheettype=220902 and sheetid='"+sheetid+"'";
				if(dao.intSelect(sql)==1){
					reSend(dao,sheetid,220902);
				}else{
					sql ="select count(*) from it_infsheetlist0 where sheettype=220902 and sheetid='"+sheetid+"'";
					if(0==dao.intSelect(sql))
						send(dao,sheetid,220902);
				}
				throw new Exception("订单取消完成，请稍后查询订单状态");
			}
			
		}else{//没有推送成功的订单，如果要取消，直接写入接口表，让系统取消
			sql = "select sheetid from customerdelive0 where refsheetid='"+sheetid+"'";
			String refsheetid= dao.strSelect(sql);
			if("".equals(refsheetid)) throw new Exception("此订单已经处理,请到运营系统再次查询");
			sql = new StringBuilder("select count(1) from (select 1 aa from wms_outstock0 where refsheetid='")
			.append(refsheetid).append("' union select 1 aa from wms_outstock where refsheetid='")
			.append(refsheetid).append("') bb").toString();
			if(dao.intSelect(sql)>0) throw new Exception("百世系统已经有相应的订单数据反馈过来，或已经取消");
			//写入接口表取消订单
			//生成单据号
			sql = "select vertifycode from IT_SystemInfo with(nolock) where SystemName='WMS接口客户'";
			String vertifyCode = dao.strSelect(sql);
			sql = "select value from Config where Name='本店号'";
			String shopid = dao.strSelect(sql);
			dao.setTransation(false);
			try{
				sql = "declare @Err int ;declare @NewSheetID char(16);"
					+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;"
					+"select @NewSheetID;";
				String sheetidtemp = dao.strSelect(sql);
				
				sql = new StringBuilder().append("insert into wms_outstock0(sheetid,refsheetid,pursheetid,")
				.append("custompursheetid,owner,outid,inid,purday,transfertype,flag,notifyOper,notifydate,operator,")
				.append("checker,checkdate,note,address,linktele,linkman,zipcode,detailid) select '")
				.append(sheetidtemp).append("',sheetid,refsheetid,customersheetid,'").append(vertifyCode)
				.append("',outshopid,inshopid,purday,2209,97,'best',getdate(),'接口','best',getdate(),notes,")
				.append("address,linktele,linkman,zipcode,detailid from customerdelive0 where sheetid='")
				.append(refsheetid).append("'").toString();
				dao.execute(sql);
				sql = new StringBuilder().append("insert into wms_outstockitem0(sheetid,customermid,")
				.append("barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) select '")
				.append(sheetidtemp).append("',goodsid,barcodeid,1,0,purqty,outqty,pknum,pkname,pkspec")
				.append(" from customerdeliveitem0 where sheetid='")
				.append(refsheetid).append("'").toString();
				dao.execute(sql);
				sql = new StringBuilder("insert into it_upnote(owner,sheetid,sheettype,sender,receiver,")
				.append("notetime,flag) values('")
				.append(vertifyCode).append("','").append(sheetidtemp)
				.append("',2209,'").append(vertifyCode).append("','")
				.append(shopid).append("',GETDATE(),0)").toString();
				dao.execute(sql);
				dao.commit();
				throw new Exception("订单取消完成，请稍后再查询订单状态");
			}catch(Exception e){
				Log.error("写入wms_oustock0取消订单数据出错", e.getMessage());
				if(!dao.getConnection().getAutoCommit())
					dao.rollback();
				throw new Exception("取消订单出错,请稍后再尝试");
			}
			
			
			
			 
			
			
		}
		
		
	}
	//重新推送客户订单
	private void sendCustomerOrderStatus(String sheetid, String shoporgid) throws Exception{
		String sql= "select count(*) from customerorder with(nolock) where sheetid='"+sheetid+"' and flag=100";
		if(this.getDao().intSelect(sql)!=1) throw new Exception("订单未审核或不存在，请重新审核");
		sql= "select count(*) from customerorder with(nolock) where sheetid='"+sheetid+"' and outflag=1 and flag=100";
		if(this.getDao().intSelect(sql)==1) throw new Exception("订单已经出库，请确认");
		DataCentre dao =null;
		sql = "select url,appkey,appsecret,token,refreshtoken,gshopid,supplierkey,uid,webserviceurl,uname from ecs_org_params with(nolock) where orgid=300";
		Hashtable ht = this.getDao().oneRowSelect(sql);
		if(ht.size()==0) throw new Exception("未配置百世仓库参数");
		String url = ht.get("url").toString();
		String partnerid = ht.get("appkey").toString();
		String partnerkey = ht.get("appsecret").toString();
		String serviceversion = ht.get("token").toString();
		String msgtype = ht.get("refreshtoken").toString();
		String callbackurl = ht.get("gshopid").toString();
		String customerCode = ht.get("supplierkey").toString();
		String warehouseCode = ht.get("uid").toString();
		boolean isExitst=true;
		if(!"".equals(shoporgid)) {
			Hashtable result = getDSName(this.getDao().getConnection(),shoporgid);
			String dsname= (String)result.get("dsname");
			warehouseCode= (String)result.get("warehouseCode");
			customerCode= (String)result.get("customercode");
			dao = this.getExtDao(dsname);
		}else{
			dao = this.getExtDao(BestUtil.getDSName(this.getDao().getConnection(), customerCode, warehouseCode));
		}
		sql = "select sheetid from customerdelive0 with(nolock) where refsheetid='"+sheetid+"'";
		String refsheetid = dao.strSelect(sql);
		if("".equals(refsheetid)){
			throw new Exception("此订单已经处理，请查询订单状态！");
		}
		String result = generOderString(ht,refsheetid);
		//Log.info("result: "+result);
		if("".equals(result)) throw new Exception("请求超时，请稍后再试！");
		String rspBizData=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
		rspBizData = BestUtil.filterChar(rspBizData);
		Document doc = DOMHelper.newDocument(rspBizData, "gbk");
		Element ele = doc.getDocumentElement();
		String flag = DOMHelper.getSubElementVauleByName(ele, "flag");
		if("success".equalsIgnoreCase(flag)) {
			
			throw new Exception("订单已经推送到百世系统,请查询订单状态");
			
		}
		
		sql = "select count(*) from it_infsheetlist with(nolock) where sheettype=2209 and sheetid='"+sheetid+"'";
		if(dao.intSelect(sql)==1){
			reSend(dao,sheetid,2209);
		}else{
			sql ="select count(*) from it_infsheetlist0 with(nolock) where sheettype=2209 and sheetid='"+sheetid+"'";
			if(0==dao.intSelect(sql))
				send(dao,sheetid,2209);
			else{
				throw new Exception("订单已经推送，请过二到三分钟再查询订单状态");
			}
		}
		throw new Exception("订单推送完成，请过二到三分钟再查询订单状态");
		
		
		
	}
	
	public void getOrderStatus() throws Exception{
			String reqDate = this.getReqData();
			Properties p = StringUtil.getIniProperties(reqDate);
			String sheettype = p.getProperty("sheettype","");
			String sheetid = p.getProperty("sheetid","");
			if("".equals(sheetid)) throw new Exception("单据号不能为空");
			String shoporgid= p.getProperty("shoporgid","");
			if("2209".equals(sheettype)){
				getCustomerOrderStatus(sheetid,shoporgid);
			}else if("2210".equals(sheettype)){
				getAsnStatus(sheetid,shoporgid);
			}else if("2222".equals(sheettype)){
				getRmaStatus(sheetid,shoporgid);
			}
			else{throw new Exception("未知单据类型!");}
			
	}
	
	//查询销售退货单据状态
	private void getRmaStatus(String sheetid,String shoporgid) throws Exception{
		String note ="";
		DataCentre dao =null;
		try{
			String sql = "select url,appkey,appsecret,token,refreshtoken,gshopid,supplierkey,uid,webserviceurl,uname from ecs_org_params with(nolock) where orgid=300";
			Hashtable ht = this.getDao().oneRowSelect(sql);
			if(ht.size()==0) throw new Exception("未配置百世仓库参数");
			String url = ht.get("url").toString();
			String partnerid = ht.get("appkey").toString();
			String partnerkey = ht.get("appsecret").toString();
			String serviceversion = ht.get("token").toString();
			String msgtype = ht.get("refreshtoken").toString();
			String callbackurl = ht.get("gshopid").toString();
			String customerCode = ht.get("supplierkey").toString();
			String warehouseCode = ht.get("uid").toString();
			String interfacesystem = ht.get("uname").toString();
			boolean isExitst=true;
			if(!"".equals(shoporgid)) {
				Hashtable result = getDSName(this.getDao().getConnection(),shoporgid);
				String dsname= (String)result.get("dsname");
				warehouseCode= (String)result.get("warehouseCode");
				customerCode= (String)result.get("customercode");
				dao = this.getExtDao(dsname);
			}else{
				dao = this.getExtDao(BestUtil.getDSName(this.getDao().getConnection(), customerCode, warehouseCode));
			}
			sql = "select sheetid from customerretrcv0 with(nolock) where refsheetid='"+sheetid+"'";
			String rmaCode = dao.strSelect(sql);
			Log.info("sheetid:"+rmaCode);
			if("".equals(rmaCode)) {
				sql = "select sheetid from customerretrcv with(nolock) where refsheetid='"+sheetid+"'";
				rmaCode = dao.strSelect(sql);
				if("".equals(rmaCode)) {
					isExitst=false;
				}else{
					isExitst=true;
				}
			}
			
			if(!isExitst)
			throw new Exception("此销售退货单不存在！");
			StringBuilder bizData = new StringBuilder();
			bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			bizData.append("<GetRmaStatus>");
			bizData.append("<customerCode>"+customerCode+"</customerCode>");
			bizData.append("<warehouseCode>"+warehouseCode+"</warehouseCode>");
			bizData.append("<rmaCode>"+rmaCode+"</rmaCode>");
			bizData.append("</GetRmaStatus>");
			String msgId= UUID.randomUUID().toString();
			String sign=BestUtil.makeSign(BestUtil.makeSignParams(bizData.toString(), "GetRmaStatus",msgtype,
					partnerid,partnerkey,serviceversion,callbackurl,msgId));
			
			Map requestParams=BestUtil.makeRequestParams(bizData.toString(), "GetRmaStatus", 
					msgId, msgtype, sign,callbackurl,
					serviceversion,partnerid);

			String result=CommHelper.sendRequest(url, requestParams, "");
			if("".equals(result)) throw new Exception("请求超时，请稍后再试！");
			String rspBizData=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
			rspBizData = BestUtil.filterChar(rspBizData);
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
			Element ras = DOMHelper.getSubElementsByName(ele, "rma")[0];
			String rmaStatus = DOMHelper.getSubElementVauleByName(ras, "rmaStatus");
			if (rmaStatus.equalsIgnoreCase("FULFILLED"))		//收货完成
			{
				boolean isReceive = false;
				sql="select count(*) from wms_instock0 where refsheetid='"+rmaCode+"' and transfertype=2222 and flag=100";
				if (dao.intSelect(sql)>0) isReceive = true;
				
				sql="select count(*) from wms_instock where refsheetid='"+rmaCode+"' and transfertype=2222 and flag=100";
				if (dao.intSelect(sql)>0) isReceive = true;
				if(!isReceive){
					sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
					String owner=dao.strSelect(sql);
					
					sql="select refsheetid,outshopid,inshopid from CustomerRetrcv0 where sheetid='"+rmaCode+"'";
					Hashtable htplan=dao.oneRowSelect(sql);
					
					String refsheetid=htplan.get("refsheetid").toString();
					String outshopid=htplan.get("outshopid").toString();
					String inshopid=htplan.get("inshopid").toString();
					
					dao.setTransation(false);
					sql="declare @Err int ; declare @NewSheetID char(16); "
						+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
					String commsheetid=dao.strSelect(sql);
										
					sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,"
						+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
						+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
						+"values('"+commsheetid+"','"+rmaCode+"','"+refsheetid+"','"+rmaCode+"','"+owner+"',"
						+"'"+outshopid+"','"+inshopid+"',30,2222,100,getdate(),'best',getdate(),'接口',getdate(),'best',getdate(),"
						+"'')";
				
					dao.execute(sql);
					
					getrmsInStockDetail(commsheetid,ras,false,dao);
						
					IntfUtils.upNote(dao.getConnection(), owner, commsheetid, 2222, interfacesystem, inshopid);
					dao.commit();
					dao.setTransation(true);
				}
			
			}
			else if (rmaStatus.equalsIgnoreCase("CANCELED") || rmaStatus.equalsIgnoreCase("CLOSED"))		//取消
			{
				boolean isReceive = false;
				sql="select count(*) from wms_instock0 where refsheetid='"+rmaCode+"' and transfertype=2222 and flag=97";
				if (dao.intSelect(sql)>0) isReceive = true;
				
				sql="select count(*) from wms_instock where refsheetid='"+rmaCode+"' and transfertype=2222 and flag=97";
				if (dao.intSelect(sql)>0) isReceive = true;
				if(!isReceive){
					sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
					String owner=dao.strSelect(sql);
					
					sql="select refsheetid,outshopid,inshopid from CustomerRetrcv0 where sheetid='"+rmaCode+"'";
					Hashtable htplan=dao.oneRowSelect(sql);
					
					String refsheetid=htplan.get("refsheetid").toString();
					String outshopid=htplan.get("outshopid").toString();
					String inshopid=htplan.get("inshopid").toString();
					
					dao.setTransation(false);
					sql="declare @Err int ; declare @NewSheetID char(16); "
						+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
					String commsheetid=dao.strSelect(sql);
					
					sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,"
						+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
						+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
						+"values('"+commsheetid+"','"+rmaCode+"','"+refsheetid+"','"+rmaCode+"','"+owner+"',"
						+"'"+outshopid+"','"+inshopid+"',30,2222,100,getdate(),'best',getdate(),'接口',getdate(),'best',getdate(),"
						+"'')";
				
					dao.execute(sql);
					
					getrmsInStockDetail(commsheetid,ras,false,dao);
						
					IntfUtils.upNote(dao.getConnection(), owner, commsheetid, 2222, interfacesystem, inshopid);
					dao.commit();
					dao.setTransation(true);
				}
			
			}
			Log.info("best logistics","接收销售退货单状态成功,单号:"+sheetid+" 状态:"+rmaStatus);
			this.OutputStr(new StringBuilder().append("[{\"sheetid\":\"").append(sheetid)
					.append("\",\"status\":\"").append(rmaStatus).append("\",\"remark\":\"")
					.append(returnRmaOrderStatus(rmaStatus)).append("\",\"logisticsProviderCode\":\"")
					.append(rmaCode).append("\"}]").toString());
		}catch(Exception e){
			if(dao!=null && !dao.getConnection().getAutoCommit()){
				dao.rollback();
			}
			dao.setTransation(true);
			throw e;
		}finally{
			if(dao!=null && dao.getConnection()!=null){
				dao.getConnection().close();
			}
		}
		
	}
	//查询入库单状态
	private void getAsnStatus(String asnCode,String shoporgid) throws Exception{
		String note = "";
		DataCentre dao =null;
		try{
			String sql = "select url,appkey,appsecret,token,refreshtoken,gshopid,supplierkey,uid,webserviceurl,uname from ecs_org_params with(nolock) where orgid=300";
			Hashtable ht = this.getDao().oneRowSelect(sql);
			if(ht.size()==0) throw new Exception("未配置百世仓库参数");
			String url = ht.get("url").toString();
			String partnerid = ht.get("appkey").toString();
			String partnerkey = ht.get("appsecret").toString();
			String serviceversion = ht.get("token").toString();
			String msgtype = ht.get("refreshtoken").toString();
			String callbackurl = ht.get("gshopid").toString();
			String customerCode = ht.get("supplierkey").toString();
			String warehouseCode = ht.get("uid").toString();
			String interfacesystem = ht.get("webserviceurl").toString();
			if(!"".equals(shoporgid)) {
				/**
				Hashtable result = getDSName(this.getDao().getConnection(),shoporgid);
				String dsname= (String)result.get("dsname");
				warehouseCode= (String)result.get("warehouseCode");
				customerCode= (String)result.get("customercode");
				dao = this.getExtDao(dsname);
				**/
				Hashtable result = getDSName(this.getDao().getConnection(),shoporgid);
				String dsname= (String)result.get("dsname");
				ht.put("uid",(String)result.get("warehouseCode"));
				warehouseCode = (String)result.get("warehouseCode");
				ht.put("supplierkey",(String)result.get("customercode"));
				customerCode = result.get("customercode").toString();
				partnerid =  (String)result.get("partnerid");
				partnerkey =  (String)result.get("partnerkey");
				dao = this.getExtDao(dsname);
			}else{
				dao = this.getExtDao(BestUtil.getDSName(this.getDao().getConnection(), customerCode, warehouseCode));
			}
			sql = "select 1 from planreceipt with(nolock) where sheetid='"+asnCode+"'";
			if(!"1".equals(dao.strSelect(sql))) throw new Exception("此来货计划单不存在！");
			StringBuilder bizData = new StringBuilder();
			bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			bizData.append("<GetAsnStatus>");
			bizData.append("<customerCode>"+customerCode+"</customerCode>");
			bizData.append("<warehouseCode>"+warehouseCode+"</warehouseCode>");
			bizData.append("<asnCode>"+asnCode+"</asnCode>");
			bizData.append("</GetAsnStatus>");
			String msgId= UUID.randomUUID().toString();
			String sign=BestUtil.makeSign(BestUtil.makeSignParams(bizData.toString(), "GetAsnStatus",msgtype,
					partnerid,partnerkey,serviceversion,callbackurl,msgId));
			
			Map requestParams=BestUtil.makeRequestParams(bizData.toString(), "GetAsnStatus", 
					msgId, msgtype, sign,callbackurl,
					serviceversion,partnerid);

			String result=CommHelper.sendRequest(url, requestParams, "");
			if("".equals(result)) throw new Exception("请求超时，请稍后再试！");
			String rspBizData=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
			rspBizData = BestUtil.filterChar(rspBizData);
			//Log.info("rspBizData: "+rspBizData);
			Document doc = DOMHelper.newDocument(rspBizData, "gbk");
			Element ele = doc.getDocumentElement();
			String flag = DOMHelper.getSubElementVauleByName(ele, "flag");
			String notes = DOMHelper.getSubElementVauleByName(ele, "note");
			if(!"success".equalsIgnoreCase(flag)) {
				note = DOMHelper.getSubElementVauleByName(ele,"note");
				Element errs = DOMHelper.getSubElementsByName(ele, "errors")[0];
				Element err = DOMHelper.getSubElementsByName(errs, "error")[0];
				String errmsg = DOMHelper.getSubElementVauleByName(err, "errorDescription");
				throw new Exception(notes+" "+errmsg);
				
			}
			Element asn = DOMHelper.getSubElementsByName(ele, "asn")[0];
			String asnStatus = DOMHelper.getSubElementVauleByName(asn, "asnStatus");
			if (asnStatus.equalsIgnoreCase("FULFILLED"))		//收货完成  百世已经收货完成了  这些商品信息会首先写到dc库的wms_instock0 flag=100,wms_instockitem0,it_upnote表
			{                                                   //it_upnote的sheetid为wms_instock0的sheetid
				boolean isReceive=false;
				notes = DOMHelper.getSubElementVauleByName(asn, "note");
				sql="select count(*) from wms_instock0 where refsheetid='"+asnCode+"' and transfertype=2314 and flag=100";
				if (dao.intSelect(sql)>0) isReceive=true;
				
				sql="select count(*) from wms_instock where refsheetid='"+asnCode+"' and transfertype=2314 and flag=100";
				if (dao.intSelect(sql)>0) isReceive=true;
				if(!isReceive){
					sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
					String owner=dao.strSelect(sql);
					
					sql="select shopid,venderid from planreceipt where sheetid='"+asnCode+"'";
					Hashtable htplan=dao.oneRowSelect(sql);
					
					String venderid=htplan.get("venderid").toString();
					String shopid=htplan.get("shopid").toString();
					dao.setTransation(false);
					sql="declare @Err int ; declare @NewSheetID char(16); "
						+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
					String commsheetid=dao.strSelect(sql);
										
					sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,plansheetid,"
						+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
						+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
						+"values('"+commsheetid+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+owner+"',"
						+"'"+venderid+"','"+shopid+"',30,2314,100,getdate(),'best',getdate(),'接口',getdate(),'best',getdate(),"
						+"'')";
				
					dao.execute(sql);
					
					getInStockDetail(commsheetid,asn,false,dao);
						
					IntfUtils.upNote(dao.getConnection(), owner, commsheetid, 2314, interfacesystem, shopid);
					dao.commit();
					dao.setTransation(true);
				}
				
			}
			else if (asnStatus.equalsIgnoreCase("CANCELED") || asnStatus.equalsIgnoreCase("CLOSED"))		//取消  flag=97
			{
				boolean isReceive = false;
				notes = DOMHelper.getSubElementVauleByName(asn, "note");
				sql="select count(*) from wms_instock0 where refsheetid='"+asnCode+"' and transfertype=2314 and flag=97";
				if (dao.intSelect(sql)>0) isReceive = true;
				
				sql="select count(*) from wms_instock where refsheetid='"+asnCode+"' and transfertype=2314 and flag=97";
				if (dao.intSelect(sql)>0) isReceive = true;
				if(!isReceive){
					sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
					String owner=dao.strSelect(sql);
					
					dao.setTransation(false);
					sql="select shopid,venderid from planreceipt where sheetid='"+asnCode+"'";
					Hashtable htplan=dao.oneRowSelect(sql);
					
					String venderid=htplan.get("venderid").toString();
					String shopid=htplan.get("shopid").toString();
					
					sql="declare @Err int ; declare @NewSheetID char(16); "
						+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
					String commsheetid=dao.strSelect(sql);
										
					sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,plansheetid,"
						+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
						+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
						+"values('"+commsheetid+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+owner+"',"
						+"'"+venderid+"','"+shopid+"',30,2314,97,getdate(),'best',getdate(),'接口',getdate(),'best',getdate(),"
						+"'')";
				
					dao.execute(sql);
					
					sql="insert into wms_instockitem0(sheetid,customermid,"
						+"barcodeid,badflag,NotifyPrice,price,notifyqty,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) "
						+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,qty,qty,"
						+",0,0,0,pknum,pkname,pkspec,17.00 "
						+"from planreceiptitem where sheetid='"+asnCode+"' ";
					
					IntfUtils.upNote(dao.getConnection(), owner, commsheetid, 2314, interfacesystem, shopid);
					dao.commit();
					dao.setTransation(true);
				}
				
			}
			Log.info("best logistics","接收补货单状态成功,单号:"+asnCode+" 状态:"+asnStatus);
			this.OutputStr(new StringBuilder().append("[{\"sheetid\":\"").append(asnCode)
					.append("\",\"status\":\"").append(asnStatus).append("\",\"remark\":\"")
					.append(returnAsnOrderStatus(asnStatus)).append("\",\"logisticsProviderCode\":\"")
					.append(note).append("\"}]").toString());
		}catch(Exception e){
			if(dao!=null && !dao.getConnection().getAutoCommit()){
				dao.rollback();
			}
			dao.setTransation(true);
			throw e;
		}finally{
			if(dao!=null && dao.getConnection()!=null){
				dao.getConnection().close();
			}
		}
		
		
	}
	//入库单明细
	private void getInStockDetail(String commsheetid, Element updateAsnStatusele, boolean b,
			DataCentre dao) throws Exception{
		Element produectsele=(Element) updateAsnStatusele.getElementsByTagName("products").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("product");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "skuCode"); 
			int normalQuantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(produectele, "normalQuantity")).intValue();
			int defectiveQuantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(produectele, "defectiveQuantity")).intValue();

			String sql = new StringBuilder().append("insert into wms_instockitem0(sheetid,customermid,")
			.append("barcodeid,badflag,NotifyPrice,price,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) ")
			.append(" select '").append(commsheetid)
			.append("',goodsid,barcodeid,1,0.00,0.00,").append(normalQuantity)
			.append(",").append(defectiveQuantity).append(",0,0,pknum,pkname,pkspec,17.00 ")
			.append("from barcode where ").append(b?"barcodeid='":"custombc='")
			.append(skuCode).append("'").toString();
			
			dao.execute(sql);
		}
		
	}
	//查询出库单状态
	private void getCustomerOrderStatus(String refsheetid,String shoporgid)throws Exception{
		DataCentre dao =null;
		try{
			String shippingOrderNo="";
			String logisticsProviderCode="";
			String sql = "select url,appkey,appsecret,token,refreshtoken,gshopid,supplierkey,uid,webserviceurl from ecs_org_params with(nolock) where orgid=300";
			Hashtable ht = this.getDao().oneRowSelect(sql);
			if(ht.size()==0) {
				throw new Exception("未配置百世仓库参数");
			}
			//Log.info("shoporgid: "+shoporgid);
			String customerCode = ht.get("supplierkey").toString();
			String warehouseCode = ht.get("uid").toString();
			String interfacesystem = ht.get("webserviceurl").toString();
			if(!"".equals(shoporgid)) {
				Hashtable result = getDSName(this.getDao().getConnection(),shoporgid);
				String dsname= (String)result.get("dsname");
				ht.put("uid",(String)result.get("warehouseCode"));
				warehouseCode = (String)result.get("warehouseCode");
				ht.put("supplierkey",(String)result.get("customercode"));
				customerCode = result.get("customercode").toString();
				ht.put("appkey", (String)result.get("partnerid"));
				ht.put("appsecret", (String)result.get("partnerkey"));
				dao = this.getExtDao(dsname);
			}else{
				dao = this.getExtDao(BestUtil.getDSName(this.getDao().getConnection(), customerCode, warehouseCode));
			}
			sql = "select sheetid from customerdelive0 with(nolock) where refsheetid='"+refsheetid+"'";
			String sheetid = dao.strSelect(sql);
			if("".equals(sheetid)) {
				sql = "select sheetid from customerdelive with(nolock) where refsheetid='"+refsheetid+"'";
				sheetid = dao.strSelect(sql);
				if("".equals(sheetid))
				throw new Exception("此订单不存在!");
			}
			String result = generOderString(ht,sheetid);
			//Log.info("result: "+result);
			if("".equals(result)) throw new Exception("请求超时，请稍后再试！");
			String rspBizData=result.substring(result.indexOf("<bizData>")+9,result.indexOf("</bizData>"));
			rspBizData = BestUtil.filterChar(rspBizData);
			//Log.info("result:　"+rspBizData);
			Document outStockStatusDoc = DOMHelper.newDocument(rspBizData, "GBK");
			
			Element outStockStatusele = outStockStatusDoc.getDocumentElement();	
			
			String flag=DOMHelper.getSubElementVauleByName(outStockStatusele, "flag");
			
			if (flag.equalsIgnoreCase("FAILURE"))
			{
				String errorMsg="";
				Element errorsele=(Element) outStockStatusele.getElementsByTagName("errors").item(0);
				NodeList errorlist=errorsele.getElementsByTagName("error");
				for(int j=0;j<errorlist.getLength();j++)
				{
					Element errorele=(Element) errorlist.item(j);
					String errorcode=DOMHelper.getSubElementVauleByName(errorele, "errorCode");
					String errordesc=DOMHelper.getSubElementVauleByName(errorele, "errorDescription");
					
					errorMsg=errorMsg+"错误代码:"+errorcode+",错误信息:"+errordesc+" ";	
				}
				
				throw new Exception("获取出库单信息失败,出库单号:"+sheetid+",错误信息:"+errorMsg);
			
			}
			else
			{
				
				Element saleorderele=(Element) outStockStatusele.getElementsByTagName("salesOrder").item(0);
				
				String orderCode=DOMHelper.getSubElementVauleByName(saleorderele, "orderCode");
				String orderStatus=DOMHelper.getSubElementVauleByName(saleorderele, "orderStatus");

				
				
				if (orderStatus.equalsIgnoreCase("DELIVERED"))		//已发货
				{
					logisticsProviderCode=DOMHelper.getSubElementVauleByName(saleorderele, "logisticsProviderCode");
					shippingOrderNo=DOMHelper.getSubElementVauleByName(saleorderele, "shippingOrderNo");
					boolean isReceive=false;
					sql = new StringBuilder().append("select COUNT(*) from (select 1 aa from wms_outstock0 where refsheetid='")
					.append(orderCode).append("' and transfertype=2209 and flag=100 union select 1 aa from wms_outstock where refsheetid='")
					.append(orderCode).append("' and transfertype=2209 and flag=100) a").toString();
					if (dao.intSelect(sql)>0) isReceive=true;
					
					if(!isReceive){
						double weight=Double.valueOf(DOMHelper.getSubElementVauleByName(saleorderele, "weight")).doubleValue()*1000;
						
						sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
						String owner=dao.strSelect(sql);
						
						dao.setTransation(false);			
						sql="declare @Err int ; declare @NewSheetID char(16); "
							+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
						String commsheetid=dao.strSelect(sql);
											
						sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
							+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
							+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
							+"linktele,linkman,delivery,deliverysheetid,zipcode,detailid,weigh)"
							+"select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+owner+"',"
							+"outshopid,inshopid,purday,2209,100,'best',getdate(),'接口','best',getdate(),"
							+"notes,address,linktele,linkman,'"+logisticsProviderCode+"','"+shippingOrderNo+"',"
							+"zipcode,detailid,"+weight+" from customerdelive0 "
							+" where sheetid='"+orderCode+"'";
						
						dao.execute(sql);
					
						getDeliveryDetail(commsheetid,saleorderele,false,dao);
					
						IntfUtils.upNote(dao.getConnection(),owner, commsheetid, 2209, interfacesystem, BestUtil.getShopID(dao.getConnection(),customerCode,warehouseCode));
					
						dao.commit();
						dao.setTransation(true);
					}
					
				}
				else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//取消、关闭
				{
					boolean isReceive=false;
					sql = new StringBuilder().append("select COUNT(*) from (select 1 aa from wms_outstock0 where refsheetid='")
					.append(orderCode).append("' and transfertype=2209 and flag=97 union select 1 aa from wms_outstock where refsheetid='")
					.append(orderCode).append("' and transfertype=2209 and flag=97) a").toString();
					if (dao.intSelect(sql)>0) isReceive=true;
					
					if(!isReceive){
						
						sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+interfacesystem+"'";
						String owner=dao.strSelect(sql);
						dao.setTransation(false);
						
						sql="declare @Err int ; declare @NewSheetID char(16); "
							+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
						String commsheetid=dao.strSelect(sql);
											
						sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
							+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
							+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
							+"linktele,linkman,zipcode,detailid)"
							+"select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+owner+"',"
							+"outshopid,inshopid,purday,2209,97,'best',getdate(),'接口','best',getdate(),"
							+"notes,address,linktele,linkman,zipcode,detailid from customerdelive0 "
							+" where sheetid='"+orderCode+"'";
						dao.execute(sql);
						
						getCancelDeliveryDetail(commsheetid,orderCode,dao);
						
						processPartRefund(orderCode,dao);
						
						IntfUtils.upNote(dao.getConnection(), owner, commsheetid, 2209,interfacesystem,
								BestUtil.getShopID(dao.getConnection(),customerCode,warehouseCode));
						
						dao.commit();
						dao.setTransation(true);
					}
					
				}
				
				Log.info("best logistics","取发货单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
				if("DELIVERED".equals(orderStatus)){
					this.OutputStr(new StringBuilder().append("[{\"sheetid\":\"").append(refsheetid).append("\",\"status\":\"")
							.append(orderStatus).append("\",\"remark\":\"").append(returnOrderStatus(orderStatus))
							.append("\",\"deliverysheetid\":\"").append(shippingOrderNo).append("\",\"logisticsProviderCode\":\"")
							.append(logisticsProviderCode).append("\"}]").toString());
				}else
				this.OutputStr(new StringBuilder().append("[{\"sheetid\":\"").append(refsheetid).append("\",\"status\":\"")
						.append(orderStatus).append("\",\"remark\":\"").append(returnOrderStatus(orderStatus)).append("\"}]").toString());
				
			}
		}catch(Exception e){
			e.printStackTrace();
			if(dao!=null && !dao.getConnection().getAutoCommit()){
				dao.rollback();
			}
			dao.setTransation(true);
			Log.info("exception: "+e.getMessage());
			throw e;
		}finally{
			if(dao!=null && dao.getConnection()!=null){
				dao.getConnection().close();
			}
		}
	}
	
	private Hashtable<String,String> getDSName(Connection connection, String dcshopid) throws JSQLException {
		String sql = "select warehouseCode,dsname,customercode,partnerid,partnerkey from ecs_bestlogisticswarehousecontrast where dcshopid='"+dcshopid+"'";
		return SQLHelper.oneRowSelect(connection, sql);
	}
	private String returnOrderStatus(String s){
		String remark ="";
		if("DELIVERED".equalsIgnoreCase(s)) remark="百世WMS已发货";
		else if("WMS_CANCELED".equalsIgnoreCase(s)) remark="百世WMS订单已取消";
		else if("WMS_ACCEPT".equalsIgnoreCase(s)) remark="百世WMS订单或出库单已接单";
		else if("WMS_PRINT".equalsIgnoreCase(s)) remark="百世WMS已打印";
		else if("WMS_PICKUP".equalsIgnoreCase(s)) remark="百世WMS已拣货";
		else if("WMS_CHECK".equalsIgnoreCase(s)) remark="百世WMS已验货";
		else if("WMS_PACKAGE".equalsIgnoreCase(s)) remark="百世WMS已打包";
		else if("WMS_WEIGHT".equalsIgnoreCase(s)) remark="WMS已称重";
		else if("WMS_ACCEPT".equalsIgnoreCase(s)) remark="百世WMS订单或出库单已接单";
		else if("WMS_REJECT".equalsIgnoreCase(s)) remark="百世WMS接单失败";
		return remark;
	}
	
	private String returnAsnOrderStatus(String s){
		String remark ="";
		if("FULFILLED".equalsIgnoreCase(s)) remark="百世WMS收货完成";
		else if("INPROCESS".equalsIgnoreCase(s)) remark="百世WMS处理中";
		else if("NEW".equalsIgnoreCase(s)) remark="百世WMS未开始处理";
		else if("CLOSED".equalsIgnoreCase(s)) remark="百世WMS关闭";
		else if("CANCELED".equalsIgnoreCase(s)) remark="百世WMS取消成功";
		return remark;
	}
	
	private String returnRmaOrderStatus(String s){
		String remark ="";
		if("FULFILLED".equalsIgnoreCase(s)) remark="百世WMS收货完成";
		else if("INPROCESS".equalsIgnoreCase(s)) remark="百世WMS处理中";
		else if("NEW".equalsIgnoreCase(s)) remark="百世WMS未开始处理";
		else if("CLOSED".equalsIgnoreCase(s)) remark="百世WMS关闭";
		else if("CANCELED".equalsIgnoreCase(s)) remark="百世WMS取消成功";
		else if("REJECTED".equalsIgnoreCase(s)) remark="百世WMS拒收";
		return remark;
	}
	
	private void getDeliveryDetail(String commsheetid,Element salesorderele,Boolean isBarcodeId,DataCentre dao) throws Exception
	{
		Element produectsele=(Element) salesorderele.getElementsByTagName("products").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("product");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "skuCode"); 
			String normalQuantity=DOMHelper.getSubElementVauleByName(produectele, "normalQuantity");
					
			String sql = new StringBuilder().append("insert into wms_outstockitem0(sheetid,customermid,")
				.append("barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec)")
				.append(" select '").append(commsheetid)
				.append("',goodsid,barcodeid,1,0,")
				.append(normalQuantity).append(",").append(normalQuantity)
				.append(",pknum,pkname,pkspec from barcode where ")
				.append(isBarcodeId?"barcodeid='":"custombc='").append(skuCode).append("'").toString();
				
			
			dao.execute(sql);
		}
	}
	
	private void getCancelDeliveryDetail(String commsheetid,String ordercode,DataCentre dao) throws Exception
	{
		
		String sql="insert into wms_outstockitem0(sheetid,customermid,"
			+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
			+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
			+",purqty,outqty,pknum,pkname,pkspec "
			+"from customerdeliveitem0 where sheetid='"+ordercode+"'";
		
		dao.execute(sql);
	}
	
	
	//重入部分退货的其他商品   先查到订单的tid,再根据tid查询退货接口表的数据是否有相应的数据，如果有，查询这个订单是否已经被合单，分别处理
	private void processPartRefund(String orderCode,DataCentre dao) throws Exception
	{	
		//查找退货订单的客户订单号  tid
		String sql="select refsheetid,customersheetid from customerdelive0 with(nolock) where sheetid='"+orderCode+"'";
		
		Hashtable result =dao.oneRowSelect(sql);
		String tid = result.get("customersheetid").toString();
		
		sql="select count(*) from ns_refund with(nolock) where tid='"+tid+"'";
		//查找这个订单在退货接口表的记录
		if(this.getDao().intSelect(sql)>0)   //如果有退货
		{
			String sheetid=result.get("refsheetid").toString();
			/**
			sql="select count(*) from customerorderreflist with(nolock)  where refsheetid='"+tid+"'";
			
			String sheetid="";
			
			if(SQLHelper.intSelect(this.getConnection(), sql)>0)  //如果被合并掉
			{
				sql="select sheetid from customerorderreflist with(nolock)  where refsheetid='"+tid+"'";
				
				sheetid=SQLHelper.strSelect(this.getConnection(), sql);

			}
			else
			{	
				sql="select sheetid from customerorder with(nolock) where refsheetid='"+tid+"'";
			
				sheetid=SQLHelper.strSelect(this.getConnection(), sql);

			}
			**/
			
			//查询这个订单退货的商品明细  如果有相应的退货明细商品，则生成相应的customerorder0 customerorderitem0表数据，即新的订单
			sql="select count(*) from customerorderitem with(nolock) "
				+"where sheetid='"+sheetid+"' "
				+" and oid not in(select oid from ns_refund with(nolock) "
				+ "where tid='"+tid+"' or tid in(select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"'))   and paypresentid is null";
			
			if (this.getDao().intSelect(sql)>0) //如果存在部分退货，重新生成其他商品的订单
			{
				sql="select outshopid from customerorder with(nolock)  where sheetid='"+sheetid+"'";
				//查找订单对应的仓库
				String outshopid=this.getDao().strSelect(sql);
				
				//生成新的单据编号
				sql="declare @Err int ; declare @newsheetid char(16); "
					+"execute  @Err = TL_GetNewMSheetID 2209, '"+outshopid+"' , '020V01' , @newsheetid output;select @newsheetid;";			
				String newsheetid=this.getDao().strSelect(sql);
				
				
				
				sql="if object_id('tempdb..#tmp_order') is not null  drop table #tmp_order;";
				
				this.getDao().execute(sql);
				//把customerorder表的这条记录写入表临时表
				sql="select * into #tmp_order from customerorder where sheetid='"+sheetid+"'";
				this.getDao().execute(sql);
				
				
				sql="update #tmp_order set sheetid='"+newsheetid+"',flag=0,notes=notes+'部分退货重入'";
				this.getDao().execute(sql);
				
				sql="insert into customerorder0 select * from #tmp_order";
				this.getDao().execute(sql);
				
				
				sql="if object_id('tempdb..#tmp_orderitem') is not null  drop table #tmp_orderitem;";
				
				sql="select * into #tmp_orderitem from customerorderitem with(nolock) "
					+"where sheetid='"+sheetid+"' "
					+" and oid not in(select oid from ns_refund with(nolock) "
					+ "where tid='"+tid+"' or tid in(select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"')) and paypresentid is null";
				this.getDao().execute(sql);
				
				
				sql="update #tmp_orderitem set sheetid='"+newsheetid+"'";
				this.getDao().execute(sql);
				
				sql="insert into customerorderitem0 select * from #tmp_orderitem";
				this.getDao().execute(sql);
				
				sql="if object_id('tempdb..#tmp_order') is not null  drop table #tmp_order;";
				this.getDao().execute(sql);
				sql="if object_id('tempdb..#tmp_orderitem') is not null  drop table #tmp_orderitem;";
				this.getDao().execute(sql);
				
			}
			
			
		}
		
	}
	

	private void getrmsInStockDetail(String commsheetid,Element asnele,Boolean IsBarcodeId,DataCentre dao) throws Exception
	{
		Element produectsele=(Element) asnele.getElementsByTagName("products").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("product");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "skuCode"); 
			int normalQuantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(produectele, "normalQuantity")).intValue();
			int defectiveQuantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(produectele, "defectiveQuantity")).intValue();
			String sql = new StringBuilder().append("insert into wms_instockitem0(sheetid,customermid,barcodeid,")
			.append("badflag,NotifyPrice,price,NotifyPQty,inqty,InPQty,InBadQty,pknum,pkname,pkspec,Taxrate) ")
			.append(" select '").append(commsheetid).append("',goodsid,barcodeid,1,0.00,0.00,0,")
			.append(normalQuantity)
			.append(",0,").append(defectiveQuantity).append(",pknum,pkname,pkspec,17.00 ")
			.append("from barcode where ").append(IsBarcodeId?"barcodeid='":"custombc='")
			.append(skuCode).append("'").toString();
			dao.execute(sql);
		}
	}
	//查询百世所有的仓库
	public void getBestDcshop() throws Exception{
		String sql ="select a.name,b.dcshopid from shop a,ecs_bestlogisticswarehousecontrast b where a.id=b.dcshopid";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	//重新推送订单
	private void reSend(DataCentre dao, String sheetid, int sheettype) throws Exception{
		dao.setTransation(false);
		String sql = "insert into it_infsheetlist0 select * from it_infsheetlist where sheetid='"
			+sheetid+"' and sheettype="+sheettype;
		dao.execute(sql);
		sql = "delete it_infsheetlist where sheetid='"+sheetid+ "' and sheettype="+sheettype;
		dao.execute(sql);
		dao.commit();
		dao.setTransation(true);
		
	}
	//写入接口数据
	private void send(DataCentre dao ,String sheetid,int sheettype) throws Exception {
		String sql ="insert into it_infsheetlist0 select "+sheetid+","+sheettype+",vertifycode,0,getdate() from "
		+"IT_SystemInfo with(nolock) where SystemName='WMS接口客户'";
		dao.execute(sql);
	}
	/**
	 *查询百世销售订单状态
	 *String url = ht.get("url").toString();
			String partnerid = ht.get("appkey").toString();
			String partnerkey = ht.get("appsecret").toString();
			String serviceversion = ht.get("token").toString();
			String msgtype = ht.get("refreshtoken").toString();
			String callbackurl = ht.get("gshopid").toString();
			String customerCode = ht.get("supplierkey").toString();
			String warehouseCode = ht.get("uid").toString();
			String interfacesystem = ht.get("webserviceurl").toString();
			String sign=BestUtil.makeSign(BestUtil.makeSignParams(bizData.toString(), "GetRmaStatus",msgtype,
					partnerid,partnerkey,serviceversion,callbackurl,msgId));
			
			Map requestParams=BestUtil.makeRequestParams(bizData.toString(), "GetRmaStatus", 
					msgId, msgtype, sign,callbackurl,
					serviceversion,partnerid);
	 */
	private String generOderString(Hashtable ht,String sheetid) throws Exception{
		StringBuilder bizData = new StringBuilder();
		bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bizData.append("<GetSalesOrderStatus>");
		bizData.append("<customerCode>"+ht.get("supplierkey").toString()+"</customerCode>");
		bizData.append("<warehouseCode>"+ht.get("uid").toString()+"</warehouseCode>");
		bizData.append("<orderCode>"+sheetid+"</orderCode>");
		bizData.append("</GetSalesOrderStatus>");
		//Log.info("data: "+bizData.toString());
		String msgId= UUID.randomUUID().toString();
		String sign=BestUtil.makeSign(BestUtil.makeSignParams(bizData.toString(), "GetSalesOrderStatus",ht.get("refreshtoken").toString(),
				 ht.get("appkey").toString(),ht.get("appsecret").toString(),ht.get("token").toString(),ht.get("gshopid").toString(),msgId));
		Map requestParams=BestUtil.makeRequestParams(bizData.toString(), "GetSalesOrderStatus", 
				msgId, ht.get("refreshtoken").toString(), sign,ht.get("gshopid").toString(),
				ht.get("token").toString(),ht.get("appkey").toString());
		String result=CommHelper.sendRequest(ht.get("url").toString(), requestParams, "");
		return result;
	}
	 
	
}
