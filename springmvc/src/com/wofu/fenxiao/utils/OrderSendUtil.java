package com.wofu.fenxiao.utils;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import net.sf.json.JSONObject;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.order.OrderLbpOutstorageRequest;
import com.jd.open.api.sdk.request.order.OrderSopOutstorageRequest;
import com.jd.open.api.sdk.request.order.OrderSopWaybillUpdateRequest;
import com.jd.open.api.sdk.response.order.OrderLbpOutstorageResponse;
import com.jd.open.api.sdk.response.order.OrderSopOutstorageResponse;
import com.jd.open.api.sdk.response.order.OrderSopWaybillUpdateResponse;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.LogisticsConsignResendRequest;
import com.taobao.api.request.LogisticsOfflineSendRequest;
import com.taobao.api.response.LogisticsConsignResendResponse;
import com.taobao.api.response.LogisticsOfflineSendResponse;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.ecommerce.alibaba.Params;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
/** * 同步发货状态帮助类
 * @author bolinli
 *
 */
public class OrderSendUtil {
	//同步淘宝发货状态
	public static JSONObject taobao(HashMap<String ,Object> params)throws Exception{
		JSONObject obj = new JSONObject();
		obj.put("ID",(Integer)params.get("id"));
		TaobaoClient client=new DefaultTaobaoClient(params.get("url").toString(),params.get("appkey").toString(), params.get("appsecret").toString());
		LogisticsOfflineSendRequest req=new LogisticsOfflineSendRequest();	
		req.setOutSid(params.get("outsid").toString().trim());
		req.setTid(TranTid(params.get("tid").toString().trim()));
		req.setCompanyCode(params.get("companycode").toString());
		LogisticsOfflineSendResponse rsp = client.execute(req, params.get("token").toString());
		if (!rsp.isSuccess())
		{	
			String errmsg ="";
			if(rsp.getSubCode()!=null){
				if(rsp.getSubCode().equals("isv.logistics-offline-service-error:B04") || rsp.getSubCode().equals("isv.logistics-offline-service-error:S01")){
					errmsg="订单状态异常,请手工发货";
				}
				else if(rsp.getSubCode().equals("isv.logistics-offline-service-error:P38")){
					
					errmsg="订单状态异常,拆单校验未通过,请手工发货";
				}
				else if(rsp.getSubCode().indexOf("ORDER_NOT_FOUND_ERROR")!=-1){
					errmsg="订单无法找到";
					System.out.println("ddd");
	            }
				if(rsp.getSubMsg()!=null){
					if (rsp.getSubMsg().indexOf("不能重复发货")>=0|| rsp.getSubMsg().indexOf("发货类型不匹配")>=0)
					{
						errmsg="订单重复发货";
					}
					
					else if((rsp.getSubMsg().indexOf("没有权限进行发货")>=0)||rsp.getSubMsg().indexOf("没有权限发货")>=0
							||rsp.getSubMsg().indexOf("当前订单状态不支持修改")>=0) 
					{
						errmsg="订单没有权限发货";
					} else if (rsp.getSubMsg().indexOf("物流订单不存在") >=0 || rsp.getSubMsg().indexOf("订单已经被拆单") >=0
							 || rsp.getSubMsg().indexOf("当前操作的订单是拆单订单") >=0)
					{
						errmsg="物流订单不存在或订单已经被拆单";
					} else if (rsp.getSubMsg().indexOf("运单号不符合规则或已经被使用") >=0)
		            {
						errmsg="运单号不符合规则或已经被使用";
		            }
					else{
						errmsg="订单发货失败,错误信息: "+rsp.getSubMsg()+rsp.getMsg();
					}
				}
				
			}else{
				errmsg=rsp.getMsg();
			}
			obj.put("errorCode", 1);
			obj.put("msg", errmsg);
		}else{
			obj.put("errorCode", 0);
		}
		return obj;
	}
	
	/**
	 * 
	 * 修改物流公司和运单号   已经发过货的订单才能进行此操作
	 * 
	 * 
	 */
	public static  HashMap<String ,Object> resend(HashMap<String ,String> params)  throws Exception
	{
		HashMap<String,Object> result = new HashMap<String,Object>();
		TaobaoClient client=new DefaultTaobaoClient(params.get("url"),params.get("appkey"), params.get("appsecret"));
		LogisticsConsignResendRequest req=new LogisticsConsignResendRequest();	
		req.setOutSid(params.get("outsid"));
		req.setTid(TranTid(params.get("tid")));					
		req.setCompanyCode(params.get("companycode"));
		LogisticsConsignResendResponse rsp = client.execute(req, params.get("authcode"));
		if (rsp.isSuccess())
		{			
			result.put("result",true);
		}
		else
		{		
			if (rsp.getSubMsg().indexOf("不能重复发货")>=0|| rsp.getSubMsg().indexOf("发货类型不匹配")>=0)
			{
				result.put("result",false);
				result.put("errmsg","订单重复发货");
			}else if (rsp.getSubMsg().indexOf("订单未发货")>=0)
			{
				result.put("result",false);
				result.put("errmsg","订单可能未发货，或已经退货");
			}
			else if((rsp.getSubMsg().indexOf("没有权限进行发货")>=0)||rsp.getSubMsg().indexOf("没有权限发货")>=0 
					||rsp.getSubMsg().indexOf("当前订单状态不支持修改")>=0 ) 
			{
				result.put("result",false);
				result.put("errmsg","订单没有权限发货");

			}
			else if (rsp.getSubMsg().indexOf("物流订单不存在") >=0 || rsp.getSubMsg().indexOf("订单已经被拆单") >=0
					|| rsp.getSubMsg().indexOf("当前操作的订单是拆单订单") >=0)
			{
				result.put("result",false);
				result.put("errmsg","订单不存在或订单已经被拆");
			} else if (rsp.getSubMsg().indexOf("运单号不符合规则或已经被使用") >=0)
			{
				result.put("result",false);
				result.put("errmsg","运单号不符合规则或已经被使用");
			}else if(rsp.getSubMsg().indexOf("该订单不支持修改") >=0){  
				
				result.put("result",false);
				result.put("errmsg","订单不能修改物流信息");
			}
			else{
				
			}
				result.put("result",false);
				result.put("errmsg","订单转件失败: "+rsp.getSubMsg()+rsp.getMsg());
		}
		return result;
			
	}
	//同步京东发货状态
	public static JSONObject jingDong(HashMap<String ,Object> params)throws Exception{
		int isLbp = (Integer)params.get("isLbp")!=null?(Integer)params.get("isLbp"):0;
		if(isLbp==1){                                //isLbp=1代表lbp店铺
			return lbpOrderDelivery(params);
		}else{
			return soPOrderDelivery(params);
		}
	}
	
	//LBP发货
	private static JSONObject lbpOrderDelivery(HashMap<String ,Object> params) throws Exception
	{
			JSONObject result = new JSONObject();
			result.put("ID",(Integer)params.get("id"));
			JdClient client = new DefaultJdClient(params.get("url").toString(),params.get("token").toString(),
				params.get("appkey").toString(),params.get("appsecret").toString());
			OrderLbpOutstorageRequest request = new OrderLbpOutstorageRequest();
			request.setOrderId(params.get("tid").toString());
			request.setLogisticsId(params.get("companycode").toString()) ;
			request.setPackageNum("1");
			request.setWaybill(params.get("outsid").toString());
			request.setTradeNo(new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date()));
			OrderLbpOutstorageResponse response = client.execute(request);
			//状态码
			String code = response.getCode() ;
			
			if(!"0".equals(code))
			{
				result.put("errorCode", 1);
				result.put("msg", response.getZhDesc());
			}
			return result;
		
	}
	
	//SOP发货
	public static JSONObject soPOrderDelivery(HashMap<String ,Object> params)throws Exception
	{
		JSONObject result = new JSONObject();
		result.put("ID",(Integer)params.get("id"));
		JdClient client = new DefaultJdClient(params.get("url").toString(),params.get("token").toString()
				,params.get("appkey").toString(),params.get("appsecret").toString());
		OrderSopOutstorageRequest request = new OrderSopOutstorageRequest();
		request.setOrderId(params.get("tid").toString());
		request.setLogisticsId(params.get("companycode").toString()) ;
		request.setWaybill(params.get("outsid").toString()) ;
		request.setTradeNo(new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date()));
		OrderSopOutstorageResponse response = client.execute(request);
		//状态码
		String code = response.getCode() ;
		if(!"0".equals(code))
		{
			result.put("errorCode", 1);
			result.put("msg", response.getZhDesc());
		}
		return result;
		
	}
	
	//修改京东快递信息 V2
	public static HashMap<String,Object> SOPModifyExpressInfo(HashMap<String ,String> params) throws Exception
	{
		HashMap<String,Object> result = new HashMap<String,Object>();
		JdClient client = new DefaultJdClient(params.get("url"),params.get("token"),params.get("appkey"),params.get("appsecret"));
		OrderSopWaybillUpdateRequest request = new OrderSopWaybillUpdateRequest();
		request.setOrderId(params.get("tid"));
		request.setLogisticsId(params.get("companycode"));
		request.setWaybill(params.get("outsid"));
		request.setTradeNo(new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date())) ;
		OrderSopWaybillUpdateResponse response=client.execute(request);
		if("0".equals(response.getCode()))
		{
			result.put("result", true);
		}
		else
		{
			result.put("result", false);
			result.put("errmsg", "订单转件失败: "+response.getZhDesc());
		}
		return result;
	}
	
	  private static long TranTid(String tid)
	    {
	        tid = tid.trim();
	        tid = tid.toLowerCase();
	        String t = "";
	        for (int i=0; i<tid.length();i++)
	        {
	        	char c=tid.charAt(i);
	            if ((c >= 48) && (c <= 57))
	            {
	                t = t + c;
	            }
	        }

	        long id = 0;
	        if (t != "")
	        {
	            id = Long.parseLong(t);
	        }
	        return id;
	    }
	  //蘑菇街订单发货状态同步
	  public  static JSONObject mogujie(HashMap<String ,Object> params) throws Exception
		{
		        JSONObject obj = new JSONObject();
		        obj.put("ID",(Integer)params.get("id"));
				HashMap<String,Object> result = new HashMap<String,Object>();
				Map<String, String> param = new HashMap<String, String>();
		        //系统级参数设置
				param.put("app_key", params.get("appkey").toString());
				param.put("access_token", params.get("token").toString());
				param.put("method", "youdian.logistics.send");
				param.put("tid", params.get("tid").toString().trim());
				param.put("company_code",getCompanyID(params.get("companycode").toString(),params.get("delivercompanycodelist").toString()));
				param.put("out_sid", params.get("outsid").toString().trim());
				String responseOrderListData = HttpUtil.sendRequest(params.get("url").toString(), param,null);
				JSONObject responseproduct=JSONObject.fromObject(responseOrderListData).getJSONObject("status");
				int errorCount=responseproduct.getInt("code");
				
				if (errorCount!=10001)
				{
					String errdesc=responseproduct.getString("msg");
					obj.put("errorCode", 1);
					obj.put("msg", errdesc);
				}else{
					obj.put("errorCode", 0);
				}
				return obj;
			
		}
	  
	//蘑菇街取订单信息
	  public  static JSONObject getmogujieorder(HashMap<String ,Object> params) throws Exception{
		        JSONObject obj = new JSONObject();
		        obj.put("ID",(Integer)params.get("id"));
				HashMap<String,Object> result = new HashMap<String,Object>();
				Map<String, String> param = new HashMap<String, String>();
		        //系统级参数设置
				param.put("app_key", params.get("appkey").toString());
				param.put("access_token", params.get("token").toString());
				param.put("method", "youdian.trade.get");
				param.put("tid", params.get("tid").toString().trim());
				System.out.println("参数："+param.get("app_key").toString()+" "+param.get("access_token").toString()+" "+param.get("method").toString() +" "+param.get("tid").toString());
				
				String responseOrderListData = HttpUtil.sendRequest(params.get("url").toString(), param,null);
				System.out.println("返回："+ responseOrderListData);
				JSONObject responseproduct=JSONObject.fromObject(responseOrderListData).getJSONObject("status");
				int errorCount=responseproduct.getInt("code");
				
				if (errorCount!=10001)
				{
					String errdesc=responseproduct.getString("msg");
					obj.put("errorCode", 1);
					obj.put("msg", errdesc);
				}else{
					obj.put("errorCode", 0);
				}
				return obj;
			
	}
	  
	  private static String getCompanyID(String companycode,String company) throws Exception
		{
			String companyid="";
			Object[] cys=StringUtil.split(company, ";").toArray();
			for(int i=0;i<cys.length;i++)
			{
				String cy=(String) cys[i];
				
				Object[] cs=StringUtil.split(cy, ":").toArray();
				
				String ccode=(String) cs[0];
				String cid=(String) cs[1];
				
				if(ccode.equals(companycode))
				{
					companyid=cid;
					break;
				}
			}
			
			return companyid;
		}

	
	//美丽说发货
	public static JSONObject meilisuo(HashMap params) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("ID",(Integer)params.get("id"));
		Map<String, String> param = new HashMap<String, String>();
        //系统级参数设置
		param.put("app_key", params.get("appkey").toString());
		param.put("session", params.get("token").toString());
		param.put("method", "meilishuo.order.deliver");
		param.put("sign_method", "md5");
		param.put("v", "1.0");
		param.put("order_id", params.get("tid").toString().trim());
		param.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
		param.put("express_company",getCompanyID(params.get("companycode").toString(),params.get("delivercompanycodelist").toString()));
		param.put("express_id", params.get("outsid").toString().trim());
		String responseOrderListData = HttpUtil.sendByGetT(param,params.get("url").toString(),true ,params.get("appsecret").toString());
		JSONObject response=JSONObject.fromObject(responseOrderListData);
		if(response.containsKey("error_response")){
			String errdesc=response.getJSONObject("error_response").getString("message");
			obj.put("errorCode", 1);
			obj.put("msg", errdesc);
		}else{
			obj.put("errorCode", 0);
		}
		return obj;
	}
	
	//阿里巴巴发货
	public static JSONObject alibaba(HashMap params) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("ID",(Integer)params.get("id"));
		Map<String, String> param = new HashMap<String, String>();
		
		param.put("client_id", params.get("appkey").toString());
		param.put("redirect_uri", "http://163.com");
		param.put("client_secret", params.get("appsecret").toString());
		param.put("refresh_token", params.get("refresh_token").toString());
	    String returns=AuthService.refreshToken("gw.open.1688.com", params);
	    JSONObject access=JSONObject.fromObject(returns);
    	String token=access.getString("access_token");
        //系统级参数设置
    	//获取订单明细
    	param.clear();
		param.put("orderId", params.get("tid").toString().trim());
		param.put("sellerMemberId", params.get("sellerMemberId").toString());
		param.put("access_token", token);
		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.orderList.get",Params.version,Params.requestmodel,Params.appkey);
		String response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
		//Log.info("result: "+response);
		JSONObject res=JSONObject.fromObject(response);
		net.sf.json.JSONArray orderEntries=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).getJSONArray("orderEntries");
		
		String orderdetailids="";
		for(int j=0; j<orderEntries.size();j++){
			JSONObject o=orderEntries.getJSONObject(j);
			orderdetailids=orderdetailids+o.getLong("id");
			if(j>=0&&j<orderEntries.size()-1){
				orderdetailids=orderdetailids+",";
			}
		}
		param.clear();
		String timenow=Formatter.format(new Date(), Formatter.DATE_TIME_MS_FORMAT);
		param.put("memberId",params.get("sellerMemberId").toString());
		param.put("orderId", params.get("tid").toString().trim());
		param.put("orderEntryIds", orderdetailids);				//订单明细IDs
		param.put("tradeSourceType", "cbu-trade");
		param.put("logisticsCompanyId", getCompanyID(params.get("companycode").toString(),params.get("delivercompanycodelist").toString()));			//物流公司ID
																//其他物流公司名称
		param.put("logisticsBillNo", params.get("outsid").toString().trim());					//运货单号
		param.put("gmtSystemSend", timenow);						//系统发货时间
		param.put("gmtLogisticsCompanySend", timenow);				//卖家发货时间
		param.put("gmtLogisticsCom", timenow);				//卖家发货时间
		param.put("access_token", token);
		urlPath=CommonUtil.buildInvokeUrlPath("cn.alibaba.open","e56.logistics.offline.send",Params.version,Params.requestmodel,Params.appkey);
		response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
		
		res=JSONObject.fromObject(response);
		boolean code = res.getBoolean("success") ;
		if(!code){
			String errdesc=res.getJSONObject("error_response").getString("message");
			obj.put("errorCode", 1);
			obj.put("msg", errdesc);
		}else{
			obj.put("errorCode", 0);
		}
		return obj;
	}
	
}
