//package MyTest;
//
//import com.jd.open.api.sdk.DefaultJdClient;
//import com.jd.open.api.sdk.JdClient;
//import com.jd.open.api.sdk.domain.order.OrderInfo;
//import com.jd.open.api.sdk.request.delivery.EtmsWaybillSendRequest;
//import com.jd.open.api.sdk.request.delivery.EtmsWaybillcodeGetRequest;
//import com.jd.open.api.sdk.request.order.OrderPrintDataGetRequest;
//import com.jd.open.api.sdk.response.delivery.EtmsWaybillSendResponse;
//import com.jd.open.api.sdk.response.delivery.EtmsWaybillcodeGetResponse;
//import com.jd.open.api.sdk.response.order.OrderPrintDataGetResponse;
//import com.wofu.common.tools.util.log.Log;
//import com.wofu.ecommerce.jingdong.Params;
//
//public class GreedDragonExpress {
//
//	static String JBDCustomerCode = null;
//	static String SERVER_URL = null;
//	static String token = null;
//	static String appKey = null;
//	static String appSecret = null;
//	static String accessToken=null;
//	/**
//	 * @param args
//	 * @throws Exception 
//	 */
//	public static void main(String[] args) throws Exception {
//		// TODO Auto-generated method stub
//		String deliverysheetid=getJDPostNo(JBDCustomerCode,SERVER_URL,token,appKey,appSecret);
//		waybillSend(deliverysheetid,JBDCustomerCode,SERVER_URL,token,appKey,appSecret,o);
//	}
//
//	//最后一步
//	public static Boolean waybillSend(String deliveryId ,String customerCode,
//			String SERVER_URL,String accessToken,String appKey,String appSecret){
//		Boolean isSuccess=false;
//		double collectionMoney=0.00f;
//		try{
//			collectionMoney=getCollectionMoney(o.getOrderId(),SERVER_URL,accessToken,appKey,appSecret);//获取货到付款金额
//			//Log.info("应付金额为:　"+collectionMoney);
//			//Hashtable receiverInfo = getReceiverInfo(thrOrderId,conn);
//			JdClient client=new DefaultJdClient(SERVER_URL,accessToken,appKey,appSecret);
//			EtmsWaybillSendRequest request=new EtmsWaybillSendRequest();
//			request.setDeliveryId(deliveryId);
//			request.setSalePlat("0010001");
//			request.setCustomerCode(customerCode);
//			request.setCollectionValue(1);
//			request.setCollectionMoney(collectionMoney);
//			request.setOrderId(o.getOrderId());
//			request.setThrOrderId(o.getOrderId());
//			request.setSenderName(Params.username);
//			request.setSenderAddress(Params.address);
//			request.setSenderMobile(Params.phone);
//			request.setReceiveName(o.getConsigneeInfo().getFullname());
//			request.setReceiveAddress(o.getConsigneeInfo().getFullAddress());
//			request.setReceiveMobile(o.getConsigneeInfo().getMobile());
//			request.setPackageCount (1);
//			request.setWeight(1.0);
//			request.setVloumn(1000.0);
//			EtmsWaybillSendResponse response=client.execute(request);
//			isSuccess =  response.getResultInfo().getCode().equalsIgnoreCase("100");
//			Log.info("订单号: "+o.getOrderId()+",快递单号: "+deliveryId+",返回信息: "+response.getMsg());
//			Log.info(response.getResultInfo().getCode());
//		}catch(Exception ex){
//			Log.error("向京东物流系统提交运单信息出错,订单号:　"+o.getOrderId()+",错误信息: ", ex.getMessage());
//		}
//		return isSuccess;
//	}
//	//获得单号
//	private static String getJDPostNo(String JBDCustomerCode,String SERVER_URL,String token,String appKey,String appSecret) throws Exception
//	{
//		String result="";
//		while(result.equals("") || result==null){
//			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
//			EtmsWaybillcodeGetRequest  request = new EtmsWaybillcodeGetRequest ();
//			request.setPreNum("1");
//			request.setCustomerCode(JBDCustomerCode);
//			EtmsWaybillcodeGetResponse response = client.execute(request);
//
//			//状态码
//			result = response.getResultInfo().getDeliveryIdList().get(0);
//		}
//		
//		
//		return result;
//	}
//	//取货到付款金额
//	public static double getCollectionMoney(String orderId,String SERVER_URL,String token,String appKey,String appSecret) throws Exception{
//		double collectionMoney=0.00f;
//		try{
//			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
//			OrderPrintDataGetRequest request = new OrderPrintDataGetRequest();
//			request.setOrderId(orderId);
//			OrderPrintDataGetResponse response = client.execute(request);
//			if(response.getCode().equals("0")){
//				collectionMoney=Double.valueOf(response.getApiOrderPrintData().getShouldPay());
//			}
//			Log.info("订单号: "+orderId+" ,获取货到付款应付金额返回信息: "+response.getMsg());
//		}catch(Exception ex){
//			Log.error("取货到付款应收金额出错!", ex.getMessage());
//		}
//		return collectionMoney;
//		
//	}
//}
