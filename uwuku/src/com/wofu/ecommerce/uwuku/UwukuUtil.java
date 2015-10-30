package com.wofu.ecommerce.uwuku;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

public class UwukuUtil {

	public static String makeSign(String clientid,String requesttype,String startdate,String enddate,String appsecret) throws Exception
	{
		String signstr=clientid+requesttype+startdate+enddate+appsecret;	
		String sign=MD5Util.getMD5Code(signstr.getBytes("UTF-8"));
		return sign;
	}
	
	
}
