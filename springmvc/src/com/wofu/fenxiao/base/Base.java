package com.wofu.fenxiao.base;

import java.util.List;

public interface Base<T> {
	/**
	 * 返回所有数据
	 * @param t
	 * @return
	 */
	public List<T> queryAll(T t)throws Exception;
	public void delete(int id) throws Exception;
	public void update(T t) throws Exception;
	public T getById(int id)throws Exception;
	public void add(T t) throws Exception;
}
