package com.wofu.ecommerce.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
	public static String getTradeNo()
	{
		return sdf.format(new Date()) ;
	}
}
