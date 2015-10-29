package com.wofu.ecommerce.threeg.test;

import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class CreateDSAkey { 

	public static void main(String[] args) {
		try { 
			CreateDSAKeys();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static void CreateDSAKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
		kpg.initialize(1024, new SecureRandom());
		KeyPair keys = kpg.generateKeyPair();
		PrivateKey privateKey = keys.getPrivate();
		PublicKey publicKey = keys.getPublic(); 
		SaveKey("G:/ecommerce/3G/private.dsa.java.key", privateKey);
		SaveKey("G:/ecommerce/3G/public.dsa.java.key", publicKey);
	}

	static void SaveKey(String filename, Key key) {
		try {
			if (null == key) {
				throw new Exception("key is null.");
			}
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(key.getEncoded());
			fos.close();
		} catch (Exception e) {
			System.err.println("SaveEncodedKey: Exception " + e.toString());
		}
	}
}
