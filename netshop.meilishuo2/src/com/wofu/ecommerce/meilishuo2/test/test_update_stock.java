//package com.wofu.ecommerce.meilishuo2.test;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.Date;
//
//import com.wofu.common.tools.util.Formatter;
//import com.wofu.ecommerce.meilishuo2.Params;
//import com.wofu.ecommerce.meilishuo2.util.Utils;
//
//public class test_update_stock
//{
//	public static void main(String args[])
//	{
//		//��װ��
//		Date now=new Date();
//		StringBuffer out_to_server=new StringBuffer();
//		out_to_server.append("http://api.open.meilishuo.com/router/rest?");
//		out_to_server.append("app_key=");
//		out_to_server.append(Params.appKey);
//		out_to_server.append("&");
//		out_to_server.append("fields=");
//		out_to_server.append("&");
//		out_to_server.append("format=");
//		out_to_server.append("&");
//		out_to_server.append("method=");
//		out_to_server.append("meilishuo.item.quantity.update"); //��������Ҫ�ģ��Ժ��װ��һ����ķ���
//		out_to_server.append("&");
//		out_to_server.append("session=");
//		out_to_server.append("5efba92b4ad1a1018056f1d654aa3fe2"); //sessionҲ����access_tokenһ�����ͻ��ʱ������ҲҪ�����ݿ�����ļ��ȵط���̬��ȡ
//		out_to_server.append("&");
//		out_to_server.append("sign_method=md5");
//		out_to_server.append("&");
//		out_to_server.append("timestamp=");
//		try
//		{
//			out_to_server.append(URLEncoder.encode(Formatter.format(now,
//					Formatter.DATE_TIME_FORMAT), "UTF-8"));
//		} catch (UnsupportedEncodingException e)
//		{
//			e.printStackTrace();
//		}
//		out_to_server.append("&");
//		out_to_server.append("v=1.0");
//		out_to_server.append("&");
//		out_to_server.append("twitter_id=");
//		out_to_server.append("3546196425");
//		out_to_server.append("&");
//		out_to_server.append("1st=");
//		out_to_server.append("&");
//		out_to_server.append("2rd=");
//		out_to_server.append("&");
//		out_to_server.append("goods_code=");
//		out_to_server.append("&");
//		out_to_server.append("sku_id=");
//		out_to_server.append("118383318");
//		out_to_server.append("&");
//		out_to_server.append("modify_type=");
//		out_to_server.append("set");
//		out_to_server.append("&");
//		out_to_server.append("modify_value=");
//		out_to_server.append(666);
//		out_to_server.append("&");
//		out_to_server.append("sign=");
//		out_to_server.append(Utils.get_sign("meilishuo.item.quantity.update", "5efba92b4ad1a1018056f1d654aa3fe2", now));
//		//�������󲢻�ȡ���
//		String responseText=Utils.http_get(out_to_server.toString());
//		System.out.println(out_to_server.toString());
//		System.out.println(responseText);
//	}
//}
