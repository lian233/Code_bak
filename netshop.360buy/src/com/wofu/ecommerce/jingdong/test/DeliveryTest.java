package com.wofu.ecommerce.jingdong.test;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.delivery.EtmsWaybillSendRequest;
import com.jd.open.api.sdk.request.delivery.EtmsWaybillcodeGetRequest;
import com.jd.open.api.sdk.request.order.OrderPrintDataGetRequest;
import com.jd.open.api.sdk.response.delivery.EtmsWaybillSendResponse;
import com.jd.open.api.sdk.response.delivery.EtmsWaybillcodeGetResponse;
import com.jd.open.api.sdk.response.order.OrderPrintDataGetResponse;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jingdong.Params;
public class DeliveryTest {

	/**
	 * @param args
	 * 
	 * 
	 */
	//京东迪士尼童鞋专卖店
	/*private static String jBDCustomerCode="020K1181";
	private static String SERVER_URL="http://gw.api.360buy.com/routerjson";
	private static String token="7c0df7d9-61b4-4cd6-8d62-58cb298b2f49";
	private static String appKey="8B4F40F12A5AF2A956DB6B2FE05FEB90";
	private static String appSecret="d06bc1f3b9dd49eab029fa6c2731b8c5";*/
	
	//*京东hellokitty童鞋专卖店
	/*private static String jBDCustomerCode="020K1179";
	private static String SERVER_URL="http://gw.api.360buy.com/routerjson";
	private static String token="e2a22be4-15b1-4782-8e50-4b20053bff10";
	private static String appKey="2AC7B8D76AA4A6F9A95C6919212E3A15";
	private static String appSecret="11476a7291d34df1954a386e08dc375a";*/
	
	//*京东京东瞬足童鞋旗舰店
	/*private static String jBDCustomerCode="020K1180";
	private static String SERVER_URL="http://gw.api.360buy.com/routerjson";
	private static String token="9345c10e-a067-449b-92d6-31a03e90ae3d";
	private static String appKey="9775829784001DEFE182981506CB1779";
	private static String appSecret="1f389a33200c4a6f934b97319d5285ae";*/
	
	//*贝贝怡
	private static String jBDCustomerCode="021K6162";
	private static String SERVER_URL="http://gw.api.360buy.com/routerjson";
	private static String token="e351b3be-fb02-4ce3-b6fc-7fe7fc93e4d3";
	private static String appKey="28585580C280B3A93EF4BCAF3630FDEA";
	private static String appSecret="9bb7808f7dcd405e8a9002f7dbb7b9c5";
	
	
	public static void main(String[] args) {
		try{
			delivery("1609484518","吕娜","陕西 西安市 雁塔区 陕西西安市雁塔区大雁塔街道西安龙首北路东段261号","13991212462");Thread.sleep(1000L);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		

	}
	
	
	public static void delivery(String orderId,String receiverName,String receiverAddress,String receiverMobile){
		String postNo;
		try {
			postNo = getJDPostNo(jBDCustomerCode,SERVER_URL,token,appKey,appSecret);
			//Log.info("--本次发货的运单号为: "+postNo);
			waybillSend(postNo,jBDCustomerCode,orderId,SERVER_URL,
					token,appKey,appSecret,receiverName,receiverAddress,receiverMobile);
			
			Log.info("update outstock0 set deliverysheetid = '"+postNo
					+"' where custompursheetid='"+orderId+"'");
			 //StockUtils.SOPOrderDelivery("sss", orderId, "2087", "000114245784", SERVER_URL, token, appKey, appSecret) ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private static String getJDPostNo(String JBDCustomerCode,String SERVER_URL,String token,String appKey,String appSecret) throws Exception
	{
		JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		EtmsWaybillcodeGetRequest  request = new EtmsWaybillcodeGetRequest ();
		request.setPreNum("1");
		request.setCustomerCode(JBDCustomerCode);
		EtmsWaybillcodeGetResponse response = client.execute(request);

		//状态码
		String code = response.getResultInfo().getDeliveryIdList().get(0);
		
		return code;
	}
	
	public static Boolean waybillSend(String deliveryId ,String customerCode,String orderId,
			String SERVER_URL,String accessToken,String appKey,String appSecret,String receiverName,String receiverAddress,String receiverMobile){
		Boolean isSuccess=false;
		double collectionMoney=0.00f;
		try{
			collectionMoney=getCollectionMoney(orderId,SERVER_URL,accessToken,appKey,appSecret);
			Log.info("--应付金额为:　"+collectionMoney);
			//Hashtable receiverInfo = getReceiverInfo(thrOrderId,conn);
			JdClient client=new DefaultJdClient(SERVER_URL,accessToken,appKey,appSecret);
			EtmsWaybillSendRequest request=new EtmsWaybillSendRequest();
			request.setDeliveryId(deliveryId);
			request.setSalePlat("0010001");
			request.setCustomerCode(customerCode);
			request.setCollectionValue(1);
			request.setCollectionMoney(collectionMoney);
			request.setOrderId(orderId);
			request.setThrOrderId(orderId);
			request.setSenderName("021K6162");
			request.setSenderAddress("杭州");
			request.setSenderMobile(Params.phone);
			request.setReceiveName(receiverName);
			request.setReceiveAddress(receiverAddress);
			request.setReceiveMobile(receiverMobile);
			request.setPackageCount (1);
			request.setWeight(1.0);
			request.setVloumn(1000.0);
			EtmsWaybillSendResponse response=client.execute(request);
			isSuccess =  response.getResultInfo().getCode().equalsIgnoreCase("100");
			Log.info("--订单号: "+orderId+",快递单号: "+deliveryId+",返回信息: "+response.getMsg());
			Log.info("--"+response.getResultInfo().getCode());
		}catch(Exception ex){
			Log.error("--向京东物流系统提交运单信息出错,订单号:　"+orderId+",错误信息: ", ex.getMessage());
		}
		return isSuccess;
	}
	
	
	public static double getCollectionMoney(String orderId,String SERVER_URL,String token,String appKey,String appSecret){
		double collectionMoney=0.00f;
		try{
			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			OrderPrintDataGetRequest request = new OrderPrintDataGetRequest();
			request.setOrderId(orderId);
			OrderPrintDataGetResponse response = client.execute(request);
			if(response.getCode().equals("0")){
				collectionMoney=Double.valueOf(response.getApiOrderPrintData().getShouldPay());
			}
			Log.info("--订单号: "+orderId+" ,获取货到付款应付金额返回信息: "+response.getMsg());
		}catch(Exception ex){
			Log.error("--取货到付款应收金额出错!", ex.getMessage());
		}
		return collectionMoney;
		
	}

}
