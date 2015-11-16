// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package com.wofu.base.job;

import java.util.Properties;

public class Params
{

	public static String dbname = "shop";
	public static int waittime = 30;
	public static String groupname = "";
	public static boolean isNeedCache=false;// 是否启用本地缓存

	public Params()
	{
	}

	public static void init(Properties properties)
	{
		dbname = properties.getProperty("dbname", "");
		waittime = (new Integer(properties.getProperty("waittime", "30"))).intValue();
		groupname = properties.getProperty("groupname", "");
		if (null==groupname){
			groupname="";
		}
		isNeedCache = Boolean.parseBoolean(properties.getProperty("isNeedCache", "false"));
			
	}

}