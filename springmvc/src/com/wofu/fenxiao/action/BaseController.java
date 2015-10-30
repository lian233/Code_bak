package com.wofu.fenxiao.action;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.utils.Common;



/**
 * 

 */
public class BaseController {
	PageView pageView = null;
	public PageView getPageView(String pageNow,String pagesize) {
		if (Common.isEmpty(pageNow)) {
			pageView = new PageView(1);
		} else {
			pageView = new PageView(Integer.parseInt(pageNow));
		}
		if (Common.isEmpty(pagesize)) {
			pagesize = "10";
		} 
		pageView.setPage(Integer.parseInt(pagesize));
		return pageView;
	}
	
	/**

	 */
	/*@ModelAttribute
	public void init(HttpServletRequest request){
		String path = Common.BACKGROUND_PATH;
		Object ep = request.getSession().getAttribute("basePath");
		if(ep!=null){
			if(!path.endsWith(ep.toString())){
				Common.BACKGROUND_PATH = "/WEB-INF/jsp/background"+ep;
			}
		}
		
	}*/
}