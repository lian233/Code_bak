package com.wofu.ecommerce.jiaju.test;

import java.util.HashMap;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jiaju.utils.CommHelper;

public class testGetOrderList {

	public static void main(String[] args) throws Exception {
		/*http://zf.www.jiaju.com/openapi/?service=get_orders_to_send&sign=c764cd660faa58a29704fccf0c656724&type=MD5&doc=xml&partner_id=201307180002
		
		ǩ������
����ǩ����Ҫʹ�ÿͻ���˽����Կ�����ύ���ݰ�����Ȼ����(a-z����)��Ȼ�����һ�������querystring,���querystring�к���
���ı��룬��ʹ��urlencode����ת�塣 ������������������ʹ��˽����Կ���м��ܵ�PHPʵ��:

		*/
		String URL = "http://www.jiaju.com/openapi/";
		String service = "get_orders_to_send";
		String type = "MD5";
		String partner_id = "102452";	//102452
		String Partner_pwd = "OTU4MTZhMjg5NGMwZGVmNmMxMmRjNzMyNDFkNDE5NjkxMDI0NTI=";
		String doc = "json";
		
		HashMap<String, String> Data = new HashMap<String, String>();
		Data.put("service", service);
		Data.put("type", type);
		Data.put("partner_id", partner_id);
		Data.put("doc", doc);
		
		try
		{
			//��ȡ���ݿ�conn
			//�����ݿ��ȡ������Ϣ
			
			
			//����
			String sortStr = CommHelper.sortKey(Data);
			Log.info(sortStr);
			//��������ǩ��
			String Signed = CommHelper.makeSign(sortStr, Partner_pwd);
			Log.info(Signed);
			//��������
			String Result = CommHelper.sendByPost(URL, Signed);
			//Log.info(Result);
			System.out.println(Result);
			
			//�������ص�Json
			
			//����������Ϣ�ж�:
			//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
			
			//*��ȡ���ඩ����Ϣ
			
			//OrderManager.isCheck(
			
			//��鶩���Ƿ����
			//¼�붩����Ϣ�����ݿ�
			//OrderManager.
		}
		catch(Exception err)
		{
			Log.error("�����������",err.getMessage());
		}
	}

}
