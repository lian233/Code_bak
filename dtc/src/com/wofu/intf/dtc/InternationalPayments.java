package com.wofu.intf.dtc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.intf.dtc.ApiConstants;
import com.wofu.intf.dtc.DigestUtil;
import com.wofu.intf.dtc.DigestUtil.DigestALGEnum;
import com.google.common.collect.Maps;
/**
 * 该Demo展示了如何请求易极付服务<br>
 * 如果要将该demo应用于实际场景，可以将main方法切换到web请求处理代码中???<br>
 * 
 * @author lucas
 *
 */
public class InternationalPayments  extends Thread{
	private static DecimalFormat sf= new DecimalFormat("0.00");
	private static String sheetType = "880021";
	/**
	 * 字符编码???
	 */
	private static final String CHARSET = "UTF-8";
	
	/**
	 *openapi的请求地??? <br>
	 * 联调环境为：{@link http://openapi.yijifu.net/gateway.html} <br>
	 * 生产环境为：{@link http://openapi.yiji.com/gateway.html}
	 */
	private static final String OPENAPI_ADDRESS = "http://openapi.yiji.com/gateway.html";

	/**
	 * 合作伙伴ID，在易极付开立商户时提供的商户ID
	 */
	private static final String PARTNER_ID = "20150915020007879887";
	
	/**
	 * 安全校验码，在易极付建立商户时分配的商家安全码，请妥善保存ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

	 */
	private static final String SERCURITY_KEY = "36d7e38660ad477513c03fe506e4e297";
	
	/**
	 *请求协议，建议使用httpPost 
	 */
	private static final String HTTP_POST_PROTOCOL = "httpPost";
	
	/**
	 *签名类型，支持：<br>
	 * SHA-256 <br>
	 * MD5
	 */
	private static final String SIGN_TYPE = "MD5";
	
	/**
	 * 返回URL，当服务为跳转服务时同步通知到该地址???
	 * 具体请见???发文???
	 */
	private static final String RETURN_URL = "http://115.28.13.161:30005/yijifu.html";
	
	/**
	 * 异步通知URL，当服务为异步服务时会将???终结果发送至该地???
	 * 具体请见???发文???
	 */
	private static final String NOTIFY_URL = "http://115.28.13.161:30005/yijifu.html";


	/**
	 * Demo的入口，在实际使用时，如果是web环境应该是类似这样的处理代码(使用spring-mvc框架)???
	 * <pre>
	 * 	@RequestMapping(value = "query", method = RequestMethod.POST)
		public String query(DataForm dataForm, HttpServletRequest request,
		HttpServletResponse response, ModelMap modelMap) {
			//业务处理
		}
	 * </pre>
	 * 
	 * @param args
	 */
	private static String jobName = "上传国际支付单作业";
	
	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;
			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				//发送支付单
				sendPayment(connection);
				
			} catch (Throwable e) {
				try {
					e.printStackTrace() ;
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime  * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	



	
	public void sendPayment(Connection conn) throws Throwable{
		//参数 
		try {
			
		conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//取接口表
		
		List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
		System.out.println("本次要上传的支付单数为"+infsheetlist.size());
		
		//商品
		for(Iterator it=infsheetlist.iterator();it.hasNext();){
			//
			Hashtable t = (Hashtable)it.next();
			String SerialID = t.get("SerialID").toString();
			String sheetID= t.get("OperData").toString();

			
			String sql = "select * from OutStockNote where sheetID = '"+ sheetID+"'";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for (int i=0;i<vt.size();i++){	
			Hashtable ht=(Hashtable) vt.get(i);
			String outOrderNo = ht.get("CustomPurSheetID").toString();
			String CertName = ht.get("CertName").toString();
			String CertNo = ht.get("CertNo").toString();
			String CertType = ht.get("CertType").toString();
			String PayFee = ht.get("PayFee").toString();
			String PostFee = ht.get("PostFee").toString();
		    sql ="select sum(A.price*A.notifyqty*p.taxrate/100.00) from "
				+ " outstocknoteitem a,Merchandise m ,PostTariff p "
				+ " where a.MID = m.MID and m.PostTaxNo = p.code and a.sheetID='"+sheetID+"'";
			String totalPrice = SQLHelper.strSelect(conn, sql);
			if (totalPrice == "") {
				totalPrice = "0";
			}
			//Log.info("totalPrice: "+totalPrice);
			if (Double.parseDouble(totalPrice) <= 50) {
				totalPrice = "0";
			}
			String taxFee = sf.format(Double.parseDouble(totalPrice));//税金
//			System.out.println(outOrderNo);
//			System.out.println(CertName);
//			System.out.println(CertNo);
//			System.out.println(CertType);
//			System.out.println(PayFee);
//			System.out.println(PostFee);
//			System.out.println(totalPrice);
//			System.out.println(SerialID);
			String ORDER_NO = UUID.randomUUID().toString();
			Map<String, String> params = Maps.newHashMap();
			// 基本参数
			//这部分参数对于一个商户而言是固定的
			params.put(ApiConstants.ORDER_NO,ORDER_NO);
			params.put(ApiConstants.PARTNER_ID, PARTNER_ID);
			params.put(ApiConstants.PROTOCOL, HTTP_POST_PROTOCOL);
			params.put(ApiConstants.SIGN_TYPE, SIGN_TYPE);
			params.put(ApiConstants.NOTIFY_URL, NOTIFY_URL);
			params.put(ApiConstants.RETURN_URL, RETURN_URL);
			
			// 业务参数
			//每个服务的业务参数是不同的
			//具体请参考文档的请求参数
			params.put(ApiConstants.SERVICE, "paymentBillV2Order");//国际支付单上传业务
			params.put("eshopEntName", Params.EshopEntName);	//电商平台的海关备案名称 电商企业名称
			params.put("outOrderNo", outOrderNo);	//订单编号 电商平台的原始订单号增加平台标识例如：YJF2015084545
			params.put("customsCode", "5100");	//申请海关代码4600：郑州关区  5100：广州海关 
			params.put("payerName", CertName);		//支付人姓名
			params.put("payerDocType", CertType);	//支付人证件类型 01:居民身份证
			params.put("taxAmount", taxFee);		//税款金额  
			params.put("freightCurrency", "142");//物流币种编号 
			params.put("goodsAmount", PayFee);	//货款金额	
			params.put("eshopEntCode", Params.EshopEntCode);	//电商企业代码 
			params.put("payerId", CertNo);		//支付人证件号码
			params.put("freightAmount", PostFee);	//物流金额  
			params.put("taxCurrency", "142");	//税款币种编号 142人民币
			params.put("goodsCurrency", "142");	//货款币种编号  142人民币 
			
			HttpClient client = new DefaultHttpClient();
			
			HttpPost post = new HttpPost(OPENAPI_ADDRESS);
		
			
			String sign = DigestUtil.digest(params, SERCURITY_KEY, DigestALGEnum.MD5,
					"UTF-8");
			
			String requestString = buildPairs(params,sign);
			Log.info("请求参数:"+requestString);
			post.setEntity(new StringEntity(requestString, ContentType.create("application/x-www-form-urlencoded",CHARSET)));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String body = EntityUtils.toString(entity,CHARSET);
			System.out.println("返回的数据"+body);
			JSONObject  returnData=new JSONObject(body);
//			System.out.println("返回的数据2"+returnData.toString());
			String result = returnData.optString("success");
			String resultDetails = returnData.optString("resultMessage");
			int SerialID_INT=Integer.parseInt(SerialID);
			
			sql="insert into corresponding(Id,SerialID,OperData,CustomPurSheetID,SendTime) "
			+"values(?,?,?,?,?)";
	        Object[] sqlv ={ORDER_NO,SerialID,sheetID,"fire"+outOrderNo,time()};
	        SQLHelper.executePreparedSQL(conn, sql, sqlv);
	        
			if(result.equalsIgnoreCase("true")){
				
				DtcTools.backUpInf(conn,SerialID_INT,result);
				System.out.println("成功  返回内容:"+result);
			}
			if(result.equalsIgnoreCase("false")){
				DtcTools.backUpInf(conn,SerialID_INT,result);
				System.out.println("失败  返回内容:"+resultDetails);
			}
			
		}
		}
		conn.close();
				} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	private Object time() {
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		return time;
	}





	/**
	 * 请求服务并返回信???
	 * @param parameters
	 * @return
	 */


	/**
	 * 将参数组装成HttpEntity???要的形式
	 * @param parameters
	 * @param sign
	 * @return
	 */
	private static String buildPairs(Map<String, String> parameters, String sign) {
		StringBuilder resultStringBuilder = new StringBuilder();
		for (Entry<String, String> e : parameters.entrySet()) {
			try {
				resultStringBuilder.append(e.getKey()).append("=")
						.append(URLEncoder.encode(e.getValue(),CHARSET))
						.append("&");
			} catch (UnsupportedEncodingException e1) {
				Log.error(e+"","不支持的字符???");
			}
		}
		resultStringBuilder.append("sign=")
		.append(sign);
		String resultString = resultStringBuilder.toString();
		return resultString;
	}

}
