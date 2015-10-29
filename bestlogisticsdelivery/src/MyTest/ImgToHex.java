package MyTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ImgToHex 
{
	private static String hexStr =  "0123456789ABCDEF";
    public static void main(String[] args) throws Exception
    {	
		InputStream is = new FileInputStream("D:/Hexadecimal.txt");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String str = null;
		StringBuilder sb = new StringBuilder();
		while ((str = br.readLine()) != null)
		{
			sb.append(str);
		}
		String sb2= sb.toString();
		System.out.println("��ʼ");
		sb2 = HexToBinary(sb2);
		System.out.println("����");
    	System.setOut(new PrintStream("d:/test1.txt")); 
    	System.out.println(sb2);
//    	strImg="";
//        System.out.println(strImg);
//        GenerateImage(strImg);  //����ͼƬ
    }
    
    static String HexToBinary(String s){
    	Long bb =Long.parseLong(s, 16);
    	System.out.println("0");
    	return Long.toBinaryString(bb);
    	}
    
	public static String hexString2binaryString(String hexString)
	{
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		
		for (int i = 0; i < hexString.length(); i++)
		{
			System.out.println(hexString.length()-i);
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(hexString
							.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}
	
    //ͼƬת����base64�ַ���
    public static String GetImageStr(String img)
    {//��ͼƬ�ļ�ת��Ϊ�ֽ������ַ��������������Base64���봦��
        byte[] data = img.getBytes();
        data = HexStringToBinary(img);
        //���ֽ�����Base64����
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//����Base64��������ֽ������ַ���
    }
    
	public static byte[] HexStringToBinary(String hexString){
		//hexString�ĳ��ȶ�2ȡ������Ϊbytes�ĳ���
		int len = hexString.length()/2;
		byte[] bytes = new byte[len];
		byte high = 0;//�ֽڸ���λ
		byte low = 0;//�ֽڵ���λ

		for(int i=0;i<len;i++){
			 //������λ�õ���λ
			 high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);
			 low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));
			 bytes[i] = (byte) (high|low);//�ߵ�λ��������
		}
		return bytes;
	}
    
    
    //base64�ַ���ת����ͼƬ
    public static boolean GenerateImage(String imgStr)
    {   //���ֽ������ַ�������Base64���벢����ͼƬ
        if (imgStr == null) //ͼ������Ϊ��
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try 
        {
            //Base64����
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//�����쳣����
                    b[i]+=256;
                }
            }
            //����jpegͼƬ
            String imgFilePath = "d://222.jpg";//�����ɵ�ͼƬ
            OutputStream out = new FileOutputStream(imgFilePath);    
            out.write(b);
            out.flush();
            out.close();
            return true;
        } 
        catch (Exception e) 
        {
            return false;
        }
    }
}

