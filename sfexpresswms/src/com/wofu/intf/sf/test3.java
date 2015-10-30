package com.wofu.intf.sf;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.config.Resource;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StreamUtil;

public class test3 {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		String s=" ";
		
		System.out.println(s.matches("(\\d{3}-)?\\d{8}|(\\d{4}-)(\\d{7})"));
		
	}

}
