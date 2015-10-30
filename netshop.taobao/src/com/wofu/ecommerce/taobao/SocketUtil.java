package com.wofu.ecommerce.taobao;

import com.wofu.common.tools.conv.MD5Util;

/**
 * socket工具类
 * @author Administrator
 *
 */
public class SocketUtil {
	//加密content
	public static String encryContent(String str){
		return MD5Util.getMD5Code((str+"wolf").getBytes());
	}
	
	
	
}
