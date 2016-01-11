package com.wofu.fenxiao.domain;

import java.util.Date;

public class DataPackage {
	private Integer ID;		//数据包编号
	private String Title;		//数据包标题
	private Integer DataType;		//数据包类型
	private Integer ProductLineID;		//产品线ID
	private String Note;		//备注
	private Date UploadTime;		//上传时间
	private String Operator;		//上传操作员
	private String FileName;		//文件名(含扩展名)
	
	/**
	 * @return the iD
	 */
	public Integer getID() {
		return ID;
	}
	/**
	 * @param id the iD to set
	 */
	public void setID(Integer id) {
		ID = id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return Title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		Title = title;
	}
	/**
	 * @return the dataType
	 */
	public Integer getDataType() {
		return DataType;
	}
	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(Integer dataType) {
		DataType = dataType;
	}
	/**
	 * @return the productLineID
	 */
	public Integer getProductLineID() {
		return ProductLineID;
	}
	/**
	 * @param productLineID the productLineID to set
	 */
	public void setProductLineID(Integer productLineID) {
		ProductLineID = productLineID;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return Note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		Note = note;
	}
	/**
	 * @return the uploadTime
	 */
	public Date getUploadTime() {
		return UploadTime;
	}
	/**
	 * @param uploadTime the uploadTime to set
	 */
	public void setUploadTime(Date uploadTime) {
		UploadTime = uploadTime;
	}
	/**
	 * @return the operator
	 */
	public String getOperator() {
		return Operator;
	}
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		Operator = operator;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return FileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		FileName = fileName;
	}	
}
