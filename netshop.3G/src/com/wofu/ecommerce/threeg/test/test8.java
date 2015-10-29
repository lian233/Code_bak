package com.wofu.ecommerce.threeg.test;

import com.wofu.ecommerce.threeg.util.CommonHelper;
import com.wofu.ecommerce.threeg.util.Utility;

public class test8 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		String s=CommonHelper.SendRequest("http://ecrm.taobao.com/shopbonusapply/buyer_apply_result.htm?success=false&rc=RC_SHOPBONUS_PERSON_COUNT_EXCEED", "");
		System.out.println(s);
		

	}
	
	

}
