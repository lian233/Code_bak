package com.wofu.intf.test;

public class WordCut {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "���� ���� �山��  �������山�����������11��26-5";
		String[] words = str.split("\\s+");
		for(String word : words){
		    System.out.println(word);
		}
		System.out.println(words[0]);
		System.out.println(words[1]);
		System.out.println(words[2]);
		System.out.println(words[3]);
		System.out.println("����");
		if(words[4]!=null){
			System.out.println("�հ�");
		}

	}

}
