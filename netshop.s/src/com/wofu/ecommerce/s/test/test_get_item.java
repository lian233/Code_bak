package com.wofu.ecommerce.s.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.s.Params;
import com.wofu.ecommerce.s.UTF8_transformer;
import com.wofu.ecommerce.s.utils.Utils;

public class test_get_item 
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
		String method="scn.vendor.item.full.get";
		String ver=Params.ver;
		
		JSONObject data=new JSONObject();
		data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
		data.put("VendorItemId","1");  //��������ƷID
//		data.put("ProductNo", "111111");  //����
//		data.put("ItemName", String.valueOf(null)); //��Ʒ����
//		data.put("BrandName", String.valueOf(null)); //Ʒ������
//		data.put("ItemStatus", String.valueOf(null));  //��Ʒ״̬(1-���� 2-�¼�)
//		data.put("StartShowDate", String.valueOf(null)); //�ϼܿ�ʼʱ��
//		data.put("EndShowDate", String.valueOf(null));    //�ϼܽ���ʱ��
//		data.put("StartDownDate", String.valueOf(null));  //�¼ܿ�ʼʱ��
//		data.put("EndDownDate", String.valueOf(null));    //�¼ܽ���ʱ��
//		data.put("PageNo", String.valueOf(null));    //ҳ��
//		data.put("PageSize", String.valueOf(null));  //ÿҳ������Ĭ��40�����100
		String sign=Utils.get_sign(data, method, now);
		String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
		String response=Utils.sendByPost(Params.url, output_to_server);
		JSONObject result=new JSONObject(response);
		System.out.println("��¼����:"+result.getInt("TotalResults"));
		JSONArray item_list=result.getJSONArray("Result");
		for(int i=0;i<item_list.length();i++)
		{
			System.out.println("��������ƷID:"+item_list.getJSONObject(i).getString("VendorItemId"));
			System.out.println("����:"+item_list.getJSONObject(i).getString("ProductNo"));
			System.out.println("��Ʒ����:"+item_list.getJSONObject(i).getString("ItemName"));
			System.out.println("Ʒ������:"+item_list.getJSONObject(i).getString("BrandName"));
			System.out.println("���Ƽ�:"+item_list.getJSONObject(i).getString("TagPrice"));
			System.out.println("��ɫ:"+item_list.getJSONObject(i).getString("ColorName"));
			System.out.println("��Ʒ״̬:"+item_list.getJSONObject(i).getInt("ItemStatus"));
			System.out.println("�ϼ�ʱ��:"+item_list.getJSONObject(i).getString("ShowDate"));
			System.out.println("�¼�ʱ��:"+item_list.getJSONObject(i).getString("DownDate"));
			System.out.println("����ʱ��:"+item_list.getJSONObject(i).getString("UpdateDate"));
			JSONArray Skus=item_list.getJSONObject(i).getJSONArray("Skus");
			System.out.println("{");
			for(int j=0;j<Skus.length();j++)
			{
				System.out.println("	������SKU ID:"+Skus.getJSONObject(j).getString("VendorSkuId"));
				System.out.println("	����:"+Skus.getJSONObject(j).getString("SizeName"));
				System.out.println("	����ʱ��:"+Skus.getJSONObject(j).getString("UpdateDate"));
			}
			System.out.println("}");
			System.out.println();
		}
		if(result.get("ErrCode").equals(null)) System.out.println("�������:��");
		else 								   System.out.println("�������:"+result.get("ErrCode"));
		//System.out.println(result.getJSONArray("Result"));
	}

}
