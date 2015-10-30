package com.wofu.ecommerce.lefeng;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class LefengUtil {
	
	public static final Map<Integer,String> errList=new HashMap<Integer,String>(){
		{
			put(6500,"��̨���㳬ʱ");
			put(6501,"�����������������");
			put(6502,"ƽ̨�ύ�Ĳ�������");
			put(6503,"ƽ̨�ύ��sign��������");
			put(6504,"ϵͳ�����������");
			put(6600,"ƽ̨�ύ�ĵ���ID����");
			put(7100,"��������Id��ʽ����");
			put(7101,"��������IdΪ��");
			put(7110,"������������ʱ�俪ʼ��ʽ����");
			put(7120,"������������ʱ�������ʽ����");
			put(7130,"������״̬��ʽ����");
			put(7140,"������������ʱ���ʽ����");
			put(7150,"��������ÿҳ���ݸ�ʽ����");
			put(7151,"��������ÿҳ���ݳ���100");
			put(7160,"��������ҳ���ʽ����");
			put(7161,"��������ҳ��Խ��");
			put(7170,"����״̬���Ǵ�����");
			put(7173,"���������̱��Ϊ��");
			put(7174,"������������Ϊ��");
			put(8110,"����������Ʒ��Ŵ���");
			put(8120,"����������Ʒ��ź͵���ID��ƥ��");
			put(8121,"SKU״̬Ϊɾ��״̬");
			put(8130,"����������Ʒ����ڿ����в�����");
			put(8140,"���������·�ʽ��Ϣ����");
			put(8150,"��������޸���������");
			put(8151,"��������޸�����������ȫ���޸�ʱӦΪ��������0");
			put(8152,"��������޸����������������޸�ʱӦΪ����");
		}
	};
	
	public static String filterResponseText(String responsetext)
	{
		String filtertext=responsetext;
		
		//ȥ������������
		if (responsetext.substring(0,1).equals("[")){
			filtertext=responsetext.substring(1, responsetext.length()-1);
		}
		
		return filtertext;
	}
	
	public static String getStatusName(int status)
	{
		String statusname="";
		switch(status)
		{
			case 1: statusname="������";
			case 2:	statusname="��֧��";
			case 3:	statusname="��ȷ��";
			case 4:	statusname="�ѹ���";
			case 5:	statusname="��ȡ��";
			case 6:	statusname="������";
			case 7:	statusname="�ѷ���";
			case 8:	statusname="�����";
			case 9:	statusname="���˻�";
			case 10:statusname="�Ѻϲ�";
			default:statusname="";
		}
		return statusname;
	}

	public static String getSign(Map params,String methodName,String secretKey,String encoding) throws Exception
	{
		String [] paramArr =null;
		Set<String> paramSet = params.keySet();
		
		paramArr= paramSet.toArray(new String[paramSet.size()]);
		//��key�����������
		Arrays.sort(paramArr);
		
        //����Key�Ѳ���ֵƴ��Key�ĺ���
		StringBuilder sb = new StringBuilder();
		if(paramArr != null && paramArr.length >0)
		{
			for (int i = 0; i < paramArr.length; i++)
			{
				sb.append(paramArr[i]+params.get(paramArr[i]));
			}
		}
		//��Key�����꣬���ҰѲ���ֵƴ�������������������ַ���
		String sortedParam =sb.toString();
		String validateString=methodName.concat(sortedParam).concat(secretKey);
		
		String sign = MD5Util.getMD5Code(validateString.getBytes(encoding)) ;
		
		return sign;
	}
	
	public static int getSkuStockCount(String url,String shopid,String secretKey,String encoding,String sku) throws Exception
	{
		String methodApi="querySkuInfoByParams";
		int quantity=0;
		Hashtable<String, String> params = new Hashtable<String, String>() ;	
		params.put("shopId", shopid) ;
		params.put("skus",sku);
	
		String sign=LefengUtil.getSign(params, methodApi, secretKey, encoding);
		
		params.put("sign",sign);

		String reponseText = LefengUtil.filterResponseText(CommHelper.sendRequest(url+methodApi+".htm",params,"",encoding));
		
		
		JSONObject jo = new JSONObject(reponseText);
		int retcode=jo.optInt("result");
		
		if (retcode!=0)
		{
			throw new JException("��ȡ��Ʒ����ʧ��, ������Ϣ:"+LefengUtil.errList.get(retcode));
		}
		
		JSONObject returninfo=jo.optJSONObject("returnInfo");
				
		JSONArray data=returninfo.optJSONArray("data");
		
		if (data.length()==0)
			throw new JException("�Ҳ����ַ���Ʒ����,SKU��"+sku+"��");
		
		for(int i=0;i<data.length();i++)
		{
		
			JSONObject item=data.getJSONObject(i);
		
			if (item.optString("shopSkuId").equals(sku))
				 quantity=Integer.valueOf(item.optString("normalQuantity"));
		}
		
		return quantity;
	}

}
