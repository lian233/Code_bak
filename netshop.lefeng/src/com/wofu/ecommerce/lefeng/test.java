package com.wofu.ecommerce.lefeng;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.wofu.common.tools.util.Formatter;


public class test {

	private static String shopid="1000013";
	private static String url="http://119.254.76.42:8080/suning/cron_shop/";
	
	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws Exception {

		String methodApi="sellerSearchDealList";
		
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("shopId", shopid) ;
		params.put("createTimeStart","2013-06-16") ;
		params.put("createTimeEnd", "2013-06-17");
		params.put("pageNo", "1") ;
		params.put("pageSize", "40") ;
		

		String sign=LefengUtil.getSign(params, methodApi, "&^gK&9Bf9&nw", "UTF-8");
		
		//params.put("sign", sign);
		
		List requestparams=new ArrayList();
		requestparams.add("shopId="+shopid);
		requestparams.add("sign="+sign);
		requestparams.add("createTimeStart=2013-06-16");
		requestparams.add("createTimeEnd=2013-06-17");
		requestparams.add("pageSize=40");
		requestparams.add("pageNo=1");
		
		String responseText = LefengUtil.filterResponseText(CommHelper.sendRequest(url+methodApi+".jsp",params,"","UTF-8"));
		
		System.out.println(responseText);
	}

}
