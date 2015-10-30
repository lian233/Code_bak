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
			put(6500,"后台计算超时");
			put(6501,"服务器出现运算故障");
			put(6502,"平台提交的参数错误");
			put(6503,"平台提交的sign参数错误");
			put(6504,"系统出现运算错误");
			put(6600,"平台提交的店铺ID错误");
			put(7100,"参数订单Id格式错误");
			put(7101,"参数订单Id为空");
			put(7110,"参数订单创建时间开始格式错误");
			put(7120,"参数订单创建时间结束格式错误");
			put(7130,"参数订状态格式错误");
			put(7140,"参数订单发货时间格式错误");
			put(7150,"参数订单每页数据格式错误");
			put(7151,"参数订单每页数据超过100");
			put(7160,"参数订单页码格式错误");
			put(7161,"参数订单页码越界");
			put(7170,"订单状态不是待发货");
			put(7173,"参数物流商编号为空");
			put(7174,"参数物流单号为空");
			put(8110,"参数店铺商品编号错误");
			put(8120,"参数店铺商品编号和店铺ID不匹配");
			put(8121,"SKU状态为删除状态");
			put(8130,"参数店铺商品编号在库存表中不存在");
			put(8140,"参数库存更新方式信息错误");
			put(8150,"参数库存修改数量错误");
			put(8151,"参数库存修改数量错误，在全量修改时应为正整数或0");
			put(8152,"参数库存修改数量错误，在增量修改时应为整数");
		}
	};
	
	public static String filterResponseText(String responsetext)
	{
		String filtertext=responsetext;
		
		//去掉左右中括号
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
			case 1: statusname="待处理";
			case 2:	statusname="待支付";
			case 3:	statusname="已确认";
			case 4:	statusname="已挂起";
			case 5:	statusname="已取消";
			case 6:	statusname="待发货";
			case 7:	statusname="已发货";
			case 8:	statusname="已完成";
			case 9:	statusname="已退货";
			case 10:statusname="已合并";
			default:statusname="";
		}
		return statusname;
	}

	public static String getSign(Map params,String methodName,String secretKey,String encoding) throws Exception
	{
		String [] paramArr =null;
		Set<String> paramSet = params.keySet();
		
		paramArr= paramSet.toArray(new String[paramSet.size()]);
		//对key数组进行排序
		Arrays.sort(paramArr);
		
        //根据Key把参数值拼到Key的后面
		StringBuilder sb = new StringBuilder();
		if(paramArr != null && paramArr.length >0)
		{
			for (int i = 0; i < paramArr.length; i++)
			{
				sb.append(paramArr[i]+params.get(paramArr[i]));
			}
		}
		//按Key排序完，并且把参数值拼到参数后。连接起来的字符串
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
			throw new JException("获取商品资料失败, 错误信息:"+LefengUtil.errList.get(retcode));
		}
		
		JSONObject returninfo=jo.optJSONObject("returnInfo");
				
		JSONArray data=returninfo.optJSONArray("data");
		
		if (data.length()==0)
			throw new JException("找不到乐峰商品资料,SKU【"+sku+"】");
		
		for(int i=0;i<data.length();i++)
		{
		
			JSONObject item=data.getJSONObject(i);
		
			if (item.optString("shopSkuId").equals(sku))
				 quantity=Integer.valueOf(item.optString("normalQuantity"));
		}
		
		return quantity;
	}

}
