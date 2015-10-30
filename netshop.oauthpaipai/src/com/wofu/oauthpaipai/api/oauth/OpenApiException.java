package com.wofu.oauthpaipai.api.oauth;


public class OpenApiException extends Exception {
	
	private static final long serialVersionUID = 8243127099991355146L;
	private int code;
	
	/**
	 * 必填参数为空。
	 */
	public final static int PARAMETER_EMPTY = 2001;
	
	/**
	 * 必填参数无效。
	 */
	public final static int PARAMETER_INVALID = 2002;
	
	/**
	 * 服务器响应数据无效。
	 */
	public final static int RESPONSE_DATA_INVALID = 2003;
	
	/**
	 * 生成签名失败。
	 */
	public final static int MAKE_SIGNATURE_ERROR = 2004;

	/**
	 * 网络错误。
	 */
	public final static int NETWORK_ERROR = 3000;
	
	
	public OpenApiException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public OpenApiException(int code, Exception ex) {
		super(ex);
		this.code = code;
	}
	
	public int getErrorCode() {
		return code;
	}



}
