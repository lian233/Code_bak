package com.wofu.ecommerce.taobao;

import java.util.Properties;

import com.wofu.common.tools.config.Resource;

public class Processors {
	
	private static String DEFINE_FILE = Processors.class.getName();
	
	public static String getProcessor(String key) {
		Properties prop = Resource.load(DEFINE_FILE);
		return prop.getProperty(key.trim(), key);
	}

}
