package com.wofu.distribution;

import java.util.Properties;

import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 
 * 分销商品
 *
 */
public class distributionGoods extends PageBusinessObject{
	private String barcodeid;
	private String customno;
	private String custombc;
	private String title;
	private String shortname;
	private String brandname;
	private String baseprice;
	private String colorname;
	private String sizename;
	
	
	
	public void search() throws Exception {
		
		String reqdata = this.getReqData();	
		Properties prop=StringUtil.getIniProperties(reqdata);
		String sqlwhere=prop.getProperty("sqlwhere");
		String currpage=prop.getProperty("currpage");
		String pagesize=prop.getProperty("pagesize");
				
		if (prop.containsKey("ordermode"))
			this.orderMode=prop.getProperty("ordermode");

		if (prop.containsKey("searchorderfieldname"))
			this.searchOrderFieldName=prop.getProperty("searchorderfieldname");

		this.setPagesize(Integer.valueOf(pagesize).intValue());
		this.setCurrpage(Integer.valueOf(currpage).intValue());
		
		String sql="select a.barcodeid,a.custombc,b.customno,b.name title,b.shortname,e.name brandname,b.baseprice,c.name sizename,d.name colorname" 
			+" from barcode a with(nolock),goods b with(nolock),size c with(nolock),color d with(nolock),Brand e with(nolock) where" 
			+" a.goodsid=b.goodsid and a.sizeID=c.id and a.colorid=d.id and b.brandid=e.id "+sqlwhere;
		

		
		this.getRequest().getSession().removeAttribute("search_sql_"+this.getModuleid());
		this.getRequest().getSession().setAttribute("search_sql_"+this.getModuleid(),sql);
		
		this.OutputStr(this.toPaginationJSONArray(this.getPaginationData(sql,this.searchOrderFieldName,this.orderMode)));
		
	}
	public String getBarcodeid() {
		return barcodeid;
	}
	public void setBarcodeid(String barcodeid) {
		this.barcodeid = barcodeid;
	}

	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getBrandname() {
		return brandname;
	}
	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}
	public String getBaseprice() {
		return baseprice;
	}
	public void setBaseprice(String baseprice) {
		this.baseprice = baseprice;
	}
	public String getColorname() {
		return colorname;
	}
	public void setColorname(String colorname) {
		this.colorname = colorname;
	}
	public String getSizename() {
		return sizename;
	}
	public void setSizename(String sizename) {
		this.sizename = sizename;
	}
	public String getCustombc() {
		return custombc;
	}
	public void setCustombc(String custombc) {
		this.custombc = custombc;
	}
	public String getCustomno() {
		return customno;
	}
	public void setCustomno(String customno) {
		this.customno = customno;
	}
	
	

}
