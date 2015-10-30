package com.wofu.ecommerce.s.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.ecommerce.s.Params;
import com.wofu.ecommerce.s.UTF8_transformer;
import com.wofu.ecommerce.s.utils.Utils;

public class test_get_detail_orders 
{
	private static String jobName = "��ȡ��Ь�ⶩ����ҵ";
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";  //Parmas���Ǵ������ط����ƹ����ģ��Ѿ������޸�
	private static long daymillis=24*60*60*1000L;
	private static String lasttime;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public static void main(String[] args)
	{
		try {
			test2();
			System.out.println("end");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void test2() throws JSONException, ParseException
	{
		UTF8_transformer utf8_transformer=new UTF8_transformer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		Date now=new Date();
		//Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		//Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
		String method="scn.vendor.order.full.get";
		String ver=Params.ver;

		/***data����***/
		JSONObject data=new JSONObject();
		//��Ҫ���ص��ֶΣ�
		data.put("Fields",Params.Fields);	
		/**���¶����Ǳ����**/
		data.put("StartUpdateDate", Params.StartUpdateDate); //�������¿�ʼʱ��
		data.put("EndUpdateDate", Params.EndUpdateDate);   //�������½���ʱ��
		data.put("StartSubmitDate", Params.StartSubmitDate); //�����ύ��ʼʱ��
		data.put("EndSubmitDate", Params.EndSubmitDate);   //�����ύ����ʱ��
		data.put("SellerId", Params.SellerId);        //������ID(�ⲿ�̼Ҹ�ֵ����)
		data.put("SellerOrderNo", Params.SellerOrderNo);   //�����̶����� 	
		data.put("VendorOrderNo", Params.VendorOrderNo);   //�����̶�����
		data.put("OrderStatus", Params.OrderStatus);     //����״̬(1-δ���� 2-��ȷ�� 3-�ѷ��� 4-������)
		data.put("PageNo", Params.PageNo);          //ҳ��
		data.put("PageSize", Params.PageSize);        //ÿҳ������Ĭ��40�����100
		/**sign����***/
		String sign=Params.app_Secret
		+"app_key"+Params.app_key
		+"data"+data.toString()
		+"format"+Params.format
		+"method"+method
		+"timestamp"+df.format(now)
		+"v"+ver;
		sign=MD5Util.getMD5Code(sign.getBytes());
		/***�ϲ�Ϊ������****/
		String output_to_server=
			"data="+utf8_transformer.getUTF8String(data.toString())+"&"+
			"method="+utf8_transformer.getUTF8String(method)+"&"+
			"v="+utf8_transformer.getUTF8String(Params.ver)+"&"+
			"app_key="+utf8_transformer.getUTF8String(Params.app_key)+"&"+
			"format="+utf8_transformer.getUTF8String("json")+"&"+
			"timestamp="+utf8_transformer.getUTF8String(df.format(now))+"&"+
			"sign="+utf8_transformer.getUTF8String(sign.toUpperCase());		
		String responseOrderListData=Utils.sendByPost(Params.url, output_to_server);	
		//String responseOrderListData=Utils.sendByPost(Params.url, utf8_transformer.getUTF8String(output_to_server));
		System.out.println(data+"\n");
		System.out.println(output_to_server+"\n");
		System.out.println(responseOrderListData+"\n");
		JSONObject responseResult=new JSONObject(responseOrderListData);
		System.out.println("��¼����:"+responseResult.getInt("TotalResults"));
		if(responseResult.get("ErrCode").equals(null))
			System.out.println("�������:��");
		else
			System.out.println("�������:"+responseResult.get("ErrCode"));
		System.out.println("������Ϣ:"+responseResult.get("ErrMsg"));
		/**�����������**/
		JSONArray orderlist=responseResult.getJSONArray("Result");
		System.out.println("�����������:"+orderlist.toString());
		for(int j=0;j<orderlist.length();j++)
		{
			JSONObject order=orderlist.getJSONObject(j);
			System.out.println("�������"+j+":"+order.toString());
			/**������**/
			System.out.println("�����̶�����:"+order.getString("VendorOrderNo")); //�����̶�����
		}
		/**������ϸ**/
		JSONArray  tempArray1=responseResult.getJSONArray("Result");
		JSONObject tempObject1=tempArray1.getJSONObject(0);
		JSONArray  tempArray2=tempObject1.getJSONArray("OrderDets");
		JSONObject tempObject2=tempArray2.getJSONObject(0);
		System.out.println("������ϸ:"+tempObject2.toString());
		
		JSONArray OrderDets=responseResult.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets");
		for(int j=0;j<OrderDets.length();j++)
		{
			JSONObject object=OrderDets.getJSONObject(j);
			System.out.println("������ϸ��"+object.toString());
		}
		/**��¼����**/
		System.out.println("��¼����:"+responseResult.getInt("TotalResults"));
//		for(int j=0;j<responseResult.getInt("TotalResults");j++)
//		{
//			
//		}
	}

}
