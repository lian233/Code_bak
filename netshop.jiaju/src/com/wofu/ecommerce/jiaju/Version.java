package com.wofu.ecommerce.jiaju;

import java.util.Properties;
import com.wofu.common.tools.config.Resource;

//�ҾӾͶ�������ϵͳ�汾�Ż�ȡ
public class Version
{

	public static String version;

	public Version()
	{
	}

	static 
	{//��ȡ�汾����Ϣ
		Properties properties = Resource.load("com.wofu.ecommerce.suning.Version");
		version = properties.getProperty("version.number") + "." + properties.getProperty("build.number");
	}
}