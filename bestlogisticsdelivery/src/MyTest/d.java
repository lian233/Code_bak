package MyTest;

import java.util.UUID;

import com.wofu.common.tools.util.log.Log;

public class d extends Thread 
{
	public static void main(String[] args)
	{
		long startwaittime = System.currentTimeMillis();
		//开始一次循环，为了等待时间一段时间再进行下一步动作。
		while (System.currentTimeMillis() - startwaittime < (long) (30 * 1000))		
			try {
				System.out.println("当前时间"+System.currentTimeMillis()+"开始时间"+ startwaittime);
				sleep(1000L);
			} catch (Exception e) {
				Log.warn( "系统不支持休眠操作, 作业将严重影响机器性能");
			}
	}

	
}