package MyTest;

import java.util.UUID;

import com.wofu.common.tools.util.log.Log;

public class d extends Thread 
{
	public static void main(String[] args)
	{
		long startwaittime = System.currentTimeMillis();
		//��ʼһ��ѭ����Ϊ�˵ȴ�ʱ��һ��ʱ���ٽ�����һ��������
		while (System.currentTimeMillis() - startwaittime < (long) (30 * 1000))		
			try {
				System.out.println("��ǰʱ��"+System.currentTimeMillis()+"��ʼʱ��"+ startwaittime);
				sleep(1000L);
			} catch (Exception e) {
				Log.warn( "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
			}
	}

	
}