package com.wofu.fenxiao.pulgins;
import java.util.List;

/**
 * //分页封装函数
 * 
 * @param <T>
 */
public class PageView {
	/**
	 * 分页数据
	 */
	private List records;
	private String id; //排序主键

	/**
	 * 总页数 这个数是计算出来的
	 * 
	 */
	private long pageCount;

	/**
	 * 每页显示几条记录
	 */
	private int psize = 20;

	/**
	 * 默认 当前页 为第一页 这个数是计算出来的
	 */
	private int page = 1;

	/**
	 * 总记录数
	 */
	private long rowCnt;

	/**
	 * 从第几条记录开始
	 */
	private int startPage;

	/**
	 * 规定显示5个页码
	 */
	private int pagecode = 10;

	public PageView() {
	}

	/**
	 * 要获得记录的开始索引　即　开始页码
	 * 
	 * @return
	 */
	public int getFirstResult() {
		return (this.page - 1) * this.psize;
	}

	public int getPagecode() {
		return pagecode;
	}

	public void setPagecode(int pagecode) {
		this.pagecode = pagecode;
	}

	/**
	 * 使用构造函数，，强制必需输入 每页显示数量　和　当前页
	 * 
	 * @param pageSize
	 *            　　每页显示数量
	 * @param pageNow
	 *            　当前页
	 */
	public PageView(int pageSize, int page) {
		this.psize = pageSize;
		this.page = page;
	}

	/**
	 * 使用构造函数，，强制必需输入 当前页
	 * 
	 * @param pageNow
	 *            　当前页
	 */
	public PageView(int page) {
		this.page = page;
		startPage = (this.page - 1) * this.psize;
	}

	/**
	 * 查询结果方法 把　记录数　结果集合　放入到　PageView对象
	 * 
	 * @param rowCount
	 *            总记录数
	 * @param records
	 *            结果集合
	 */

	public void setQueryResult(long rowCount, List records) {
		setRowCnt(rowCount);
		setRecords(records);
	}

	public List getRecords() {
		return records;
	}

	public void setRecords(List records) {
		this.records = records;
	}

	public int getPsize() {
		return psize;
	}

	public void setPsize(int psize) {
		this.psize = psize;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public long getPageCount() {
		return pageCount;
	}


	public long getRowCnt() {
		return rowCnt;
	}

	public void setRowCnt(long rowCnt) {
		this.rowCnt = rowCnt;
		setPageCount(this.rowCnt % this.psize == 0 ? this.rowCnt / this.psize : this.rowCnt / this.psize + 1);
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

}
