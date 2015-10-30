//package com.wofu.ecommerce.ming_xie_ku;
//
//import java.sql.Connection;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import com.wofu.business.util.PublicUtils;
//import com.wofu.common.json.JSONObject;
//import com.wofu.common.tools.conv.MD5Util;
//import com.wofu.common.tools.sql.PoolHelper;
//import com.wofu.common.tools.util.Formatter;
//import com.wofu.common.tools.util.log.Log;
//import com.wofu.ecommerce.ming_xie_ku.utils.Utils;
//
//public class GetEasyOrders extends Thread
//{
//	private static String jobName = "��ȡ��Ь�ⶩ����ҵ";
//	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";  //Parmas���Ǵ������ط����ƹ����ģ��Ѿ������޸�
//	private static long daymillis=24*60*60*1000L;
//	private String lasttime;
//	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
//	@Override
//	public void run() 
//	{
//		Log.info(jobName, "����[" + jobName + "]ģ��");
//		do 
//		{
//			Connection connection = null;
//			try 
//			{
//				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.ming_xie_ku.Params.dbname); //���ݿ�����ʱ����
//				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//				//getOrderList(connection);  //��ʱû����
//			} catch (Exception e)
//			{
//				try 
//				{
//					if (connection != null && !connection.getAutoCommit())
//						connection.rollback();
//				} catch (Exception e1) 
//				{
//					Log.error(jobName, "�ع�����ʧ��");
//				}
//				Log.error("105", jobName, Log.getErrorMessage(e));
//			}finally
//			{
//				try
//				{
//					if (connection != null) connection.close();
//				} catch (Exception e)
//				{
//					Log.error(jobName, "�ر����ݿ�����ʧ��");
//				}
//			}
//			System.gc();
//		} while (true);
////		super.run();
//	}
//	
//	//��ȡ��Ь���¶���
////	public void getOrderList(Connection conn) throws Throwable
////	{
////		while(true)
////		{
////			Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
////			Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
////			Map<String, String> orderlistparams = new HashMap<String, String>();
////		}
////	}
//	
//	
//	
//	/*****�����̻�ȡ������Ҫ��Ϣ*****/
//	public void getOrderShortList(Connection conn) throws Throwable
//	{
//		UTF8_transformer utf8_transformer=new UTF8_transformer();
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
//		Date now=new Date();
//		//Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
//		//Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
//		String method="scn.vendor.order.brief.get";
//		String ver=Params.ver;
//
//		/***data����***/
//		JSONObject data=new JSONObject();
//		//��Ҫ���ص��ֶΣ�
//		data.put("Fields",Params.Fields);	
//		/**���¶����Ǳ����**/
//		data.put("StartUpdateDate", Params.StartUpdateDate); //�������¿�ʼʱ��
//		data.put("EndUpdateDate", Params.EndUpdateDate);   //�������½���ʱ��
//		data.put("StartSubmitDate", Params.StartSubmitDate); //�����ύ��ʼʱ��
//		data.put("EndSubmitDate", Params.EndSubmitDate);   //�����ύ����ʱ��
//		data.put("SellerId", Params.SellerId);        //������ID(�ⲿ�̼Ҹ�ֵ����)
//		data.put("SellerOrderNo", Params.SellerOrderNo);   //�����̶����� 	
//		data.put("VendorOrderNo", Params.VendorOrderNo);   //�����̶�����
//		data.put("OrderStatus", Params.OrderStatus);     //����״̬(1-δ���� 2-��ȷ�� 3-�ѷ��� 4-������)
//		data.put("PageNo", Params.PageNo);          //ҳ��
//		data.put("PageSize", Params.PageSize);        //ÿҳ������Ĭ��40�����100
//		/**sign����***/
//		String sign=Params.app_Secret
//		+"app_key"+Params.app_key
//		+"data"+data.toString()
//		+"format"+Params.format
//		+"method"+method
//		+"timestamp"+df.format(now)
//		+"v"+ver;
//		sign=MD5Util.getMD5Code(sign.getBytes());
//		/***�ϲ�Ϊ������****/
//		String output_to_server=
//			"data="+utf8_transformer.getUTF8String(data.toString())+"&"+
//			"method="+utf8_transformer.getUTF8String(method)+"&"+
//			"v="+Params.ver+"&"+
//			"app_key="+utf8_transformer.getUTF8String(Params.app_key)+"&"+
//			"format=json"+"&"+
//			"timestamp="+utf8_transformer.getUTF8String(df.format(now))+"&"+
//			"sign="+sign.toUpperCase();		
//		String responseOrderListData=Utils.sendByPost(Params.url, output_to_server);
//		JSONObject responseResult=new JSONObject(responseOrderListData);
//		JSONObject Result=responseResult.getJSONArray("Result").getJSONObject(0);
//		for(int i=0;i<Result.getInt("total_results");i++)
//		{
//			
//		}
//	}
//	
//	
//}
