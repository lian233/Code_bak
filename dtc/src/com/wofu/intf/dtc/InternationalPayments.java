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
 * ��Demoչʾ����������׼�������<br>
 * ���Ҫ����demoӦ����ʵ�ʳ��������Խ�main�����л���web�����������???<br>
 * 
 * @author lucas
 *
 */
public class InternationalPayments  extends Thread{
	private static DecimalFormat sf= new DecimalFormat("0.00");
	private static String sheetType = "880021";
	/**
	 * �ַ�����???
	 */
	private static final String CHARSET = "UTF-8";
	
	/**
	 *openapi�������??? <br>
	 * ��������Ϊ��{@link http://openapi.yijifu.net/gateway.html} <br>
	 * ��������Ϊ��{@link http://openapi.yiji.com/gateway.html}
	 */
	private static final String OPENAPI_ADDRESS = "http://openapi.yiji.com/gateway.html";

	/**
	 * �������ID�����׼��������̻�ʱ�ṩ���̻�ID
	 */
	private static final String PARTNER_ID = "20150915020007879887";
	
	/**
	 * ��ȫУ���룬���׼��������̻�ʱ������̼Ұ�ȫ�룬�����Ʊ���ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

	 */
	private static final String SERCURITY_KEY = "36d7e38660ad477513c03fe506e4e297";
	
	/**
	 *����Э�飬����ʹ��httpPost 
	 */
	private static final String HTTP_POST_PROTOCOL = "httpPost";
	
	/**
	 *ǩ�����ͣ�֧�֣�<br>
	 * SHA-256 <br>
	 * MD5
	 */
	private static final String SIGN_TYPE = "MD5";
	
	/**
	 * ����URL��������Ϊ��ת����ʱͬ��֪ͨ���õ�ַ???
	 * �������???����???
	 */
	private static final String RETURN_URL = "http://115.28.13.161:30005/yijifu.html";
	
	/**
	 * �첽֪ͨURL��������Ϊ�첽����ʱ�Ὣ???�ս���������õ�???
	 * �������???����???
	 */
	private static final String NOTIFY_URL = "http://115.28.13.161:30005/yijifu.html";


	/**
	 * Demo����ڣ���ʵ��ʹ��ʱ�������web����Ӧ�������������Ĵ������(ʹ��spring-mvc���)???
	 * <pre>
	 * 	@RequestMapping(value = "query", method = RequestMethod.POST)
		public String query(DataForm dataForm, HttpServletRequest request,
		HttpServletResponse response, ModelMap modelMap) {
			//ҵ����
		}
	 * </pre>
	 * 
	 * @param args
	 */
	private static String jobName = "�ϴ�����֧������ҵ";
	
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				//����֧����
				sendPayment(connection);
				
			} catch (Throwable e) {
				try {
					e.printStackTrace() ;
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime  * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	



	
	public void sendPayment(Connection conn) throws Throwable{
		//���� 
		try {
			
		conn = PoolHelper.getInstance().getConnection(Params.dbname);
				//ȡ�ӿڱ�
		
		List infsheetlist=DtcTools.getInfDownNote(conn,sheetType);
		if(infsheetlist.size()>0){
		System.out.println("����Ҫ�ϴ���֧������Ϊ"+infsheetlist.size());
		}
		System.out.println("�������"+Params.EshopEntCode);
		//��Ʒ
		for(Iterator it=infsheetlist.iterator();it.hasNext();){
			//
			Hashtable t = (Hashtable)it.next();
			String SerialID = t.get("SerialID").toString();
			String sheetID= t.get("OperData").toString();

			
			String sql = "select * from OutStockNote where sheetID = '"+ sheetID+"'";
			Vector  vt=SQLHelper.multiRowSelect(conn, sql);
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
			String taxFee = sf.format(Double.parseDouble(totalPrice));//˰��
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
			// ��������
			//�ⲿ�ֲ�������һ���̻������ǹ̶���
			params.put(ApiConstants.ORDER_NO,ORDER_NO);
			params.put(ApiConstants.PARTNER_ID, PARTNER_ID);
			params.put(ApiConstants.PROTOCOL, HTTP_POST_PROTOCOL);
			params.put(ApiConstants.SIGN_TYPE, SIGN_TYPE);
			params.put(ApiConstants.NOTIFY_URL, NOTIFY_URL);
			params.put(ApiConstants.RETURN_URL, RETURN_URL);
			
			// ҵ�����
			//ÿ�������ҵ������ǲ�ͬ��
			//������ο��ĵ����������
			params.put(ApiConstants.SERVICE, "paymentBillV2Order");//����֧�����ϴ�ҵ��
			params.put("eshopEntName", Params.EshopEntName);	//����ƽ̨�ĺ��ر������� ������ҵ����
			params.put("outOrderNo", outOrderNo);	//������� ����ƽ̨��ԭʼ����������ƽ̨��ʶ���磺YJF2015084545
			params.put("customsCode", "5100");	//���뺣�ش���4600��֣�ݹ���  5100�����ݺ��� 
			params.put("payerName", CertName);		//֧��������
			params.put("payerDocType", CertType);	//֧����֤������ 01:�������֤
			params.put("taxAmount", taxFee);		//˰����  
			params.put("freightCurrency", "142");//�������ֱ�� 
			params.put("goodsAmount", PayFee);	//������	
			params.put("eshopEntCode", Params.EshopEntCode);	//������ҵ���� 
			params.put("payerId", CertNo);		//֧����֤������
			params.put("freightAmount", PostFee);	//�������  
			params.put("taxCurrency", "142");	//˰����ֱ�� 142�����
			params.put("goodsCurrency", "142");	//������ֱ��  142����� 
			
			HttpClient client = new DefaultHttpClient();
			
			HttpPost post = new HttpPost(OPENAPI_ADDRESS);
		
			
			String sign = DigestUtil.digest(params, SERCURITY_KEY, DigestALGEnum.MD5,
					"UTF-8");
			
			String requestString = buildPairs(params,sign);
			Log.info("�������:"+requestString);
			post.setEntity(new StringEntity(requestString, ContentType.create("application/x-www-form-urlencoded",CHARSET)));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String body = EntityUtils.toString(entity,CHARSET);
			System.out.println("���ص�����"+body);
			JSONObject  returnData=new JSONObject(body);
//			System.out.println("���ص�����2"+returnData.toString());
			String result = returnData.optString("success");
			String resultDetails = returnData.optString("resultMessage");
			int SerialID_INT=Integer.parseInt(SerialID);
			
			sql="insert into corresponding(Id,SerialID,OperData,CustomPurSheetID,SendTime) "
			+"values(?,?,?,?,?)";
	        Object[] sqlv ={ORDER_NO,SerialID,sheetID,"fire"+outOrderNo,time()};
	        SQLHelper.executePreparedSQL(conn, sql, sqlv);
	        
			if(result.equalsIgnoreCase("true")){
				
				DtcTools.backUpInf(conn,SerialID_INT,result);
				System.out.println("�ɹ�  ��������:"+result);
			}
			if(result.equalsIgnoreCase("false")){
				DtcTools.backUpInf(conn,SerialID_INT,result);
				System.out.println("ʧ��  ��������:"+resultDetails);
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
	 * ������񲢷�����???
	 * @param parameters
	 * @return
	 */


	/**
	 * ��������װ��HttpEntity???Ҫ����ʽ
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
				Log.error(e+"","��֧�ֵ��ַ�???");
			}
		}
		resultStringBuilder.append("sign=")
		.append(sign);
		String resultString = resultStringBuilder.toString();
		return resultString;
	}

}
