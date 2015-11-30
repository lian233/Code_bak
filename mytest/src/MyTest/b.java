package MyTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 十六进制转成图片
 */
public class b
{
	public static void main(String[] args) throws Exception
	{
		InputStream is = new FileInputStream("D:/aaa.txt");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		System.out.println(br);
		String str = null;
		StringBuilder sb = new StringBuilder();
		while ((str = br.readLine()) != null)
		{
			System.out.println(str);
			sb.append(str);
		}
		System.out.println(sb+"测试");
	}

}