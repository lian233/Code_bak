package com.wofu.base.util;

import com.wofu.common.tools.util.StringUtil;

public class JSONTool {
	
	public static String unEscape(String encodeString) {
		if (encodeString.equals("null")) {
			return null;
		} else {
			encodeString = StringUtil.replace(encodeString, "\\t", "\t");
			encodeString = StringUtil.replace(encodeString, "\\r", "\r");
			encodeString = StringUtil.replace(encodeString, "\\n", "\n");
			encodeString = StringUtil.replace(encodeString, "\\f", "\f");
			encodeString = StringUtil.replace(encodeString, "\\b", "\b");
			encodeString = StringUtil.replace(encodeString, "/", "/");
			encodeString = StringUtil.replace(encodeString, "\"", "\"");
			encodeString = StringUtil.replace(encodeString, "\\", "\\");
			return encodeString;
		}
	}

	public static String escape(String sourceString) {
		if (sourceString == null) {
			return "null";
		} else {
			sourceString = StringUtil.replace(sourceString, "\\", "\\\\");
			sourceString = StringUtil.replace(sourceString, "\"", "\\\"");
			sourceString = StringUtil.replace(sourceString, "/", "/");
			sourceString = StringUtil.replace(sourceString, "\b", "\\b");
			sourceString = StringUtil.replace(sourceString, "\f", "\\f");
			sourceString = StringUtil.replace(sourceString, "\n", "\\n");
			sourceString = StringUtil.replace(sourceString, "\r", "\\r");
			sourceString = StringUtil.replace(sourceString, "\t", "\\t");
			return sourceString;
		}
	}

}
