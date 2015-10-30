package com.wofu.ecommerce.jumei;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.wofu.common.tools.conv.MD5Util;

public class JuMeiUtils {
	public static String getSign(Map params,String signkey,String encoding) throws Exception
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
		String validateString=signkey.concat(sortedParam).concat(signkey);
		
		String sign = MD5Util.getMD5Code(validateString.getBytes(encoding)).toUpperCase() ;
		
		return sign;
	}
}
