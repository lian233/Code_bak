package com.wofu.ecommerce.taobao;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import com.wofu.common.tools.util.log.Log;

public class SockClient extends Thread{
	private static final String jobName = "��ȡ����ip�ͻ���";
	@Override
	public void run() {
		Log.info(jobName,"������"+jobName+"ģ�顿");
		PrintStream ps=null;
		Socket socket=null;
		long currentTime;
		while(true){
			try{
				socket = new Socket(Params.serverIp,Params.serverPort);
				ps = new PrintStream(socket.getOutputStream());
				ps.print(SocketUtil.encryContent(Params.socketContent));
				ps.flush();
				ps.close();
				socket.close();
				Log.info("������������������!");
			}catch(Exception e){
				Log.error(jobName, e.getMessage());
			}finally{
				if(ps!=null) ps.close();
				if(socket!=null)
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					currentTime = System.currentTimeMillis();
					while(System.currentTimeMillis()-currentTime<Params.SocketwaitMinute*60*1000L){
						try{
							Thread.sleep(1000L);
						}catch(Exception e){
							Log.error(jobName, e.getMessage());
						}
						
					}
			}
		}
	}

	
}
