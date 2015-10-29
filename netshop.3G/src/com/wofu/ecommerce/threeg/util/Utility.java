package com.wofu.ecommerce.threeg.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import com.wofu.common.tools.util.StringUtil;

import javax.swing.JOptionPane;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;



public class Utility {
	
	static String ALGORITHM = "DSA";
	
    public static String Sign(String sourceTxt, String privatekeyPath) throws Exception
    {
    	String signcode="";
    	try {
    	      // Retrieve the Private Key
    	      PrivateKey privateKey = LoadPrivateKey(privatekeyPath);

    	      // Create the signer object
    	      Signature signer = Signature.getInstance(ALGORITHM);
    	      signer.initSign(privateKey, new SecureRandom());
    	    
    	      // Save the binary of the String which we will sign
    	      byte[] message = sourceTxt.getBytes("GBK");

    	      // Insert the message into the signer object
    	      signer.update(message);
    	      byte[] signature = signer.sign();

    	      // mos: message filestream
    	      // sos: signature filestream
    	      //FileOutputStream mos = new FileOutputStream("dsa.java.msg");
    	      //mos.write(message);

    	      //FileOutputStream sos = new FileOutputStream("dsa.java.sig");
    	      //sos.write(signature);
    	      
    	      signcode=bytesToHexString(signature);
    	      
    	      //StringBuffer sb=new StringBuffer(signcode);
    	      //Log.info(signcode);
    	     // sb.insert(40, "0214").insert(0, "302C0214");
    	      //signcode=sb.toString().toUpperCase();
    	      //"302C0214"+signcode.substring(0, 40)+"0214"

	    } catch (Exception e) {
	      throw new Exception("SignDSAMessage: " + e.toString());
	    }
    	return signcode.toUpperCase();
    }
        
    
    /*
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。   
     * @param src byte[] data   
     * @return hex string   
     */      
    public static String bytesToHexString(byte[] src){   
       return new String(Hex.encodeHex(src)); 
    }   
    
    /**  
     * Convert hex string to byte[]  
     * @param hexString the hex string  
     * @return byte[]  
     * @throws DecoderException 
     */  
    public static byte[] hexStringToBytes(String hexString) throws DecoderException {   
        if (hexString == null || hexString.equals("")) {   
            return null;   
        }   
        
        return Hex.decodeHex(hexString.toCharArray());   
    }   
    
   

    
    private static PrivateKey LoadPrivateKey(String filename) throws Exception 
    {

        PrivateKey key = null;

        try {

          FileInputStream fis = new FileInputStream(filename);
          byte[] b = new byte[fis.available()];
          fis.read(b);
          fis.close();

          PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b);

          KeyFactory factory = KeyFactory.getInstance("DSA");
          key = factory.generatePrivate(spec);

        } catch (Exception e) {
          throw new Exception("LoadPrivateKey: " + e.toString());
        }

        return key;
      }
    
    public static boolean ValifyDigest(String source, String digest, String publicKeyPath) throws Exception
    {
    	boolean result=true;
		try {
		      
		      // Load the public
		      PublicKey publicKey = LoadPublicKey(publicKeyPath);
	
		      // Load the message from file
		      //FileInputStream mis = new FileInputStream("dsa.java.msg");
		      //byte[] message = new byte[mis.available()];
		      //mis.read(message); mis.close();
	
		      // Display the resurrected string
		     // JOptionPane.showMessageDialog(null,
		       //   new String(message, 0, message.length, "UTF-8"));       
	
		      // Load the signature of the message from file
		      //FileInputStream sis = new FileInputStream("dsa.java.sig");
		     // byte[] signature = new byte[sis.available()];
		     // sis.read(signature); sis.close();
		      //Log.info(source);
		      //Log.info(digest);
		      // Initialize Signature Object
		      Signature verifier = Signature.getInstance(ALGORITHM);
		      verifier.initVerify(publicKey);
	
		      // Load the message into the Verifier Object
		      verifier.update(source.getBytes("GBK"));
	
		      // Verify the Signature on the Message
		      result = verifier.verify(hexStringToBytes(digest));
		      
		      /*
		      StringBuilder sb = new StringBuilder();
		      if( result )
		      {
		        sb.append("Message Verified:\n");
		        sb.append(new String(message, 0, message.length, "UTF-8"));
		      }
		      else
		      {
		        sb.append("Message Not Verified");
		      }
		      
		      JOptionPane.showMessageDialog(null, sb.toString());
			*/
		    } catch (Exception e) {
		      throw new Exception("VerifyDSAMessage: " + e.toString());
		    }
    	return result;
    }
    
    private static PublicKey LoadPublicKey(String filename) throws Exception 
    {

        PublicKey key = null;

        try {

          FileInputStream fis = new FileInputStream(filename);
          byte[] b = new byte[fis.available()];
          fis.read(b);
          fis.close();

          X509EncodedKeySpec spec = new X509EncodedKeySpec(b);

          KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
          key = factory.generatePublic(spec);

        } catch (Exception e) {
          throw new Exception("LoadPublicKey: " + e.toString());
        }

        return key;
      }
}
