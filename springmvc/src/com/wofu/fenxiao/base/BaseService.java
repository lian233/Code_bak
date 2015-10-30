package com.wofu.fenxiao.base;

import com.wofu.fenxiao.pulgins.PageView;






/**
 * 所有服务接口都要继承这个

 * @param <T>
 */
public interface BaseService<T> extends Base<T> {
	/**
	 * 返回分页后的数据
	 * @param pageView
	 * @param t
	 * @return
	 * @throws Exception 
	 */
	public PageView query(PageView pageView,T t) throws Exception;
}
