package com.wofu.fenxiao.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonDateValueProcessor implements JsonValueProcessor{
	
	private String format = "yyyy-MM-dd HH:mm:ss";
	@Override
	public Object processArrayValue(Object arg0, JsonConfig arg1) {
		// TODO Auto-generated method stub
		return process(arg0);
	}

	private Object process(Object arg0) {
		// TODO Auto-generated method stub
		 if(arg0 instanceof Date){   
	            SimpleDateFormat sdf = new SimpleDateFormat(format);   
	            return sdf.format(arg0);   
	        }   
	        return arg0 == null ? "" : arg0.toString(); 
	}

	@Override
	public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {
		// TODO Auto-generated method stub
		return process(arg1);
	}

}
