package com.wofu.intf.test;

public class WordCut {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "重庆 重庆 渝北区  重庆市渝北区长安锦绣城11栋26-5";
		String[] words = str.split("\\s+");
		for(String word : words){
		    System.out.println(word);
		}
		System.out.println(words[0]);
		System.out.println(words[1]);
		System.out.println(words[2]);
		System.out.println(words[3]);
		System.out.println("测试");
		if(words[4]!=null){
			System.out.println("空啊");
		}

	}

}
