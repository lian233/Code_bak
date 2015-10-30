package com.wofu.ecommerce.taobao;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import com.wofu.common.tools.util.log.Log;

public class SockClient extends Thread{
	private static final String jobName = "获取公网ip客户端";
	@Override
	public void run() {
		Log.info(jobName,"启动【"+jobName+"模块】");
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
				Log.info("向服务器发送请求完成!");
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
