package com.wofu.ecommerce.taobao;

import com.wofu.common.tools.conv.MD5Util;

/**
 * socket������
 * @author Administrator
 *
 */
public class SocketUtil {
	//����content
	public static String encryContent(String str){
		return MD5Util.getMD5Code((str+"wolf").getBytes());
	}
	
	
	
}
