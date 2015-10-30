package com.wofu.ecommerce.vjia;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.wofu.ecommerce.vjia.Params;

/**
 * 
 * @author Administrator
 */

public class CryptoTools {
	// DES���ܵ�˽Կ��������8λ�����ַ���
	private static final byte[] DESkey = Params.strkey.getBytes();// ������Կ

	private static final byte[] DESIV = Params.striv.getBytes();// ��������

	static AlgorithmParameterSpec iv = null;// �����㷨�Ĳ����ӿڣ�IvParameterSpec������һ��ʵ��

	private static Key key = null;

	public CryptoTools() throws Exception {
		DESKeySpec keySpec = new DESKeySpec(DESkey);// ������Կ����
		iv = new IvParameterSpec(DESIV);// ��������
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// �����Կ����
		key = keyFactory.generateSecret(keySpec);// �õ���Կ����

	}

	public String encode(String data) throws Exception {
		Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// �õ����ܶ���Cipher
		enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// ���ù���ģʽΪ����ģʽ��������Կ������
		byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(pasByte);
	}

	public  String decode(String data) throws Exception {
		Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		deCipher.init(Cipher.DECRYPT_MODE, key, iv);
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));
		return new String(pasByte, "UTF-8");
	}

	// ����
	public static void main(String[] args) throws Exception {

		CryptoTools tools = new CryptoTools();
		//System.out.println("����:" + tools.encode(""));
		System.out.println("����:" + tools.decode("gjbBhrCN40EfbOxxHBBk1Q=="));
	}

}
