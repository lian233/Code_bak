package com.wofu.ecommerce.jit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.wofu.common.tools.util.Formatter;
public class Job {
	private Date nextactive;//下次执行时间
	private String startTimeStr=null;
	private String middleTimeStr=null;
	private String endTimeStr=null;
	private static Long fourteen ;
	private static Long fiveteen ;
	private static Long sixteen ;
	private static Long seventteen ;
	private static Long eighteen ;
	private static SimpleDateFormat timeFormat=new SimpleDateFormat(Formatter.TIME_FORMAT);
	static {
		try {
			fourteen = timeFormat.parse("14:00:00").getTime();
			fiveteen = timeFormat.parse("15:00:00").getTime();
			sixteen = timeFormat.parse("16:00:00").getTime();
			seventteen = timeFormat.parse("17:00:00").getTime();
			eighteen = timeFormat.parse("18:00:00").getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private int clock;
	public Job(int clock,String startTime,String middleTime,String endTime){
		this.clock = clock;
		this.startTimeStr = startTime;
		this.middleTimeStr = middleTime;
		this.endTimeStr = endTime;
		long currentTime=0;
		long startTimeMill=0;
		long millleTimeMill = 0;
		try{
			currentTime = timeFormat.parse(Formatter.format(new Date(), Formatter.TIME_FORMAT)).getTime();
			startTimeMill = timeFormat.parse(startTimeStr).getTime();
			millleTimeMill = timeFormat.parse(middleTime).getTime();
		}catch(Exception e){}
		
		
		if(currentTime<startTimeMill){//设置为开始时间
			String nextactiveStr = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" "+startTime;
			try {
				nextactive= Formatter.parseDate(nextactiveStr, Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {
				
			}
		}
		else if(currentTime<millleTimeMill){//13点
			String nextactiveStr = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" "+middleTime;
			try {
				nextactive= Formatter.parseDate(nextactiveStr, Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {
				
			}
		}
		else if(currentTime<fourteen){//14点
			String nextactiveStr = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" 14:00:00";
			try {
				nextactive= Formatter.parseDate(nextactiveStr, Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {
				
			}
		}
		else if(currentTime<fiveteen){//15
			String nextactiveStr = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" 15:00:00";
			try {
				nextactive= Formatter.parseDate(nextactiveStr, Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {
				
			}
		}
		else if(currentTime<sixteen){//16
			String nextactiveStr = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" 16:00:00";
			try {
				nextactive= Formatter.parseDate(nextactiveStr, Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {
				
			}
		}else if(currentTime<seventteen){//17
			String nextactiveStr = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" 17:00:00";
			try {
				nextactive= Formatter.parseDate(nextactiveStr, Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {
				
			}
		}else if(currentTime<eighteen){//18
			String nextactiveStr = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" 18:00:00";
			try {
				nextactive= Formatter.parseDate(nextactiveStr, Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {
				
			}
		}else{
			Calendar current = Calendar.getInstance();
			current.add(Calendar.DATE, 1);
			try {
				nextactive=Formatter.parseDate(Formatter.format(current, Formatter.DATE_FORMAT)+" "+startTimeStr,
						Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {}
		}
		
			
		
	}
	
	public boolean canExecute(){
		return System.currentTimeMillis()-nextactive.getTime()>=0;
	}
	
	public void next(){
		String endTimetemp = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" "+endTimeStr;
		String middleTimetemp = Formatter.format(new Date(), Formatter.DATE_FORMAT)+" "+middleTimeStr;
		Date endTime=null;
		Date middleTime =null;
		try {
			endTime = Formatter.parseDate(endTimetemp, Formatter.DATE_TIME_FORMAT);
			middleTime = Formatter.parseDate(middleTimetemp, Formatter.DATE_TIME_FORMAT);
		} catch (ParseException e) {
			
		}
		Calendar current = Calendar.getInstance();
		if(System.currentTimeMillis()-middleTime.getTime()<0){//早上只执行一次  9点
			current.set(Calendar.HOUR_OF_DAY, 13);
			current.set(Calendar.MINUTE	, 0);
			current.set(Calendar.SECOND	, 0);
			current.set(Calendar.MILLISECOND, 0);
			nextactive=new Date(current.getTimeInMillis());
		}
		else if(System.currentTimeMillis()-endTime.getTime()>=0){//更改为后一天的starttime
			current.add(Calendar.DATE, 1);
			try {
				nextactive=Formatter.parseDate(Formatter.format(current, Formatter.DATE_FORMAT)+" "+startTimeStr,
						Formatter.DATE_TIME_FORMAT);
			} catch (ParseException e) {}
		}else{//添加一个clock
			current.add(Calendar.HOUR_OF_DAY, clock);
			current.set(Calendar.MINUTE	, 0);
			current.set(Calendar.SECOND	, 0);
			current.set(Calendar.MILLISECOND, 0);
			nextactive=new Date(current.getTimeInMillis());
		}
	}
	
}
