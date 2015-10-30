package com.wofu.ecommerce.yz;

import com.wofu.base.util.BusinessObject;

public class Messages extends BusinessObject{
	private String title;//留言的标题
	private String content;//留言的内容
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
