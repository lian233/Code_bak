package com.wofu.intf.huasheng.test;

import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.huasheng.Params;
import com.wofu.ecommerce.huasheng.util.Utils;

public class testcode {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//��������
		
		//Params.apiurl = "http://seryhappy.imwork.net/wscenter/UpdateServlet";
		
//		String orderCode = "20015102917280397650";
//		String postCompanyCode = "˳��";
//		String postNo = "102777468986";
//		
//		try {
//			System.out.println("���ͷ������� ...");
//			
//			String ParamData = "order_id=" + orderCode + "&express_company=" + postCompanyCode + "&express_id=" + postNo;
//
//			String result = Utils.doRequest("deliver", ParamData, false);
//			
//			if(result.equals(""))
//			{
//				System.out.println("�޷��ؽ��!");
//				return;
//			}
//			
//			JSONObject resultJson = new JSONObject(result);
//			if(resultJson.getBoolean("state"))
//			{
//				System.out.println("���������ɹ������š�" + orderCode + "������ݹ�˾��" + postCompanyCode + "������ݵ��š�" + postNo + "��") ;
//			}
//			else
//			{
//				System.out.println("��������ʧ�ܣ����š�" + orderCode + "������ݹ�˾��" + postCompanyCode + "������ݵ��š�" + postNo + "����������Ϣ��" + resultJson.getString("msg")) ;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		//ͬ��������
//		String ParamData = "type=set&skulist=6933102210975&qtylist=100";
//		Utils.doRequest("stock", ParamData, true);
	}

}
