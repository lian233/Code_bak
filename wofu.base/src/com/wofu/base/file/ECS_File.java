package com.wofu.base.file;

import com.wofu.base.util.BusinessObject;

public class ECS_File extends BusinessObject {

	private int fileid;
	private String name;
	private String path;
	private String smallpath;
	private String filetype;
	private int filesize;
	private String note;
	public int getFileid() {
		return fileid;
	}
	public void setFileid(int fileid) {
		this.fileid = fileid;
	}
	public int getFilesize() {
		return filesize;
	}
	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getSmallpath() {
		return smallpath;
	}
	public void setSmallpath(String smallpath) {
		this.smallpath = smallpath;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
}
