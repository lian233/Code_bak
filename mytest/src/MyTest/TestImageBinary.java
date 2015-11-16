package MyTest;

import java.awt.image.BufferedImage;    
import java.io.ByteArrayInputStream;    
import java.io.ByteArrayOutputStream;    
import java.io.File;    
import java.io.IOException;    
import java.io.PrintStream;
   
import javax.imageio.ImageIO;    
   
import sun.misc.BASE64Decoder;    
import sun.misc.BASE64Encoder;    
   
public class TestImageBinary {    
    static BASE64Encoder encoder = new sun.misc.BASE64Encoder();    
    static BASE64Decoder decoder = new sun.misc.BASE64Decoder();    
        
    public static void main(String[] args) {    
         
         
        base64StringToImage(getImageBinary()); 
        
    }    
        
    static String getImageBinary(){    
        File f = new File("d:/test.jpg");           
        BufferedImage bi;    
        try {    
            bi = ImageIO.read(f);    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();    
            ImageIO.write(bi, "jpg", baos);    
            byte[] bytes = baos.toByteArray();    
            System.setOut(new PrintStream("d:/testData/test2.txt")); 
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<bytes.length;i++){
            	sb.append(bytes[i]);
            }
            String test2=sb.toString();
            System.out.println(test2);
            System.setOut(new PrintStream("d:/testData/test3.txt")); 
            System.out.println(sb);
            return encoder.encodeBuffer(bytes).trim();    
        } catch (IOException e) {    
            e.printStackTrace();    
        }    
        return null;    
    }    	
        
    static void base64StringToImage(String base64String){    
        try {    
            byte[] bytes1 = decoder.decodeBuffer(base64String);    
                
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);    
            BufferedImage bi1 =ImageIO.read(bais);    
            File w2 = new File("d://QQ.jpg");//可以是jpg,png,gif格式    
            ImageIO.write(bi1, "jpg", w2);//不管输出什么格式图片，此处不需改动    
            System.out.println("成功");
        } catch (IOException e) {    
            e.printStackTrace();    
        }    
    }    
   
} 