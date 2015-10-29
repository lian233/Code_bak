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
		System.out.println("开始");
		sb2 = HexToBinary(sb2);
		System.out.println("结束");
    	System.setOut(new PrintStream("d:/test1.txt")); 
    	System.out.println(sb2);
//    	strImg="";
//        System.out.println(strImg);
//        GenerateImage(strImg);  //生成图片
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
	
    //图片转化成base64字符串
    public static String GetImageStr(String img)
    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = img.getBytes();
        data = HexStringToBinary(img);
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }
    
	public static byte[] HexStringToBinary(String hexString){
		//hexString的长度对2取整，作为bytes的长度
		int len = hexString.length()/2;
		byte[] bytes = new byte[len];
		byte high = 0;//字节高四位
		byte low = 0;//字节低四位

		for(int i=0;i<len;i++){
			 //右移四位得到高位
			 high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);
			 low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));
			 bytes[i] = (byte) (high|low);//高地位做或运算
		}
		return bytes;
	}
    
    
    //base64字符串转化成图片
    public static boolean GenerateImage(String imgStr)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try 
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "d://222.jpg";//新生成的图片
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

