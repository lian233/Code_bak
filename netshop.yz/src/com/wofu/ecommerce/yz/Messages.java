package com.wofu.ecommerce.yz;

import com.wofu.base.util.BusinessObject;

public class Messages extends BusinessObject{
	private String title;//���Եı���
	private String content;//���Ե�����
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
