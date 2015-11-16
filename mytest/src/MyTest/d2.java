package MyTest;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.wofu.common.tools.util.Formatter;

public class d2 extends Thread 
{
	public static void main(String[] args) throws Exception
	{
		
		String lasttime="2008-08-08 12:10:12";
		Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+3000000);
		String  bb =Formatter.format(startdate, Formatter.DATE_TIME_FORMAT);
		System.out.println(bb);
		
	}
	
}  