package MyTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class imgto2
{
	public static void main(String[] args)
	{
		String path = "D:/test.jpg";
		File file = new File(path);
		FileInputStream fis;
		try
		{
			fis = new FileInputStream(file);
			byte[] b;
			b = new byte[fis.available()];
			StringBuilder str = new StringBuilder();// ��������String
			fis.read(b);
			for (byte bs : b)
			{
				str.append(Integer.toBinaryString(bs));// ת��Ϊ������
			}
			System.out.println("���");
	        System.setOut(new PrintStream("d:/kengdie.txt")); 
			System.out.println(str);
			File apple = new File("D:/test2.bmp");// ���ֽ������ͼƬд����һ���ط�
			FileOutputStream fos = new FileOutputStream(apple);
			fos.write(b);
			fos.flush();
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}