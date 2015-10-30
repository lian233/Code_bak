package com.wofu.ecommerce.taobao;
/**
 * 
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import com.wofu.common.tools.util.log.Log;
public class GetPublicIp extends Thread{
	private static final String jobName="获取公网ip类";
	@Override
	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		try{
			ServerSocket server = new ServerSocket();
			server.bind(new InetSocketAddress(InetAddress.getByName(Params.serverIp),Params.serverPort));
			Socket socket=null;
			while(true){
				try{
					socket = server.accept();
					new Thread(new GetPublicRunnable(socket, Params.socketContent,Params.dsid,Params.dbname)).start();
					
				}catch(Throwable e1){
					Log.error(jobName, e1.getMessage());
				}finally{
					
				}
			}
		}catch(Exception e){
			Log.error(jobName, e.getMessage());
		}
	}
	
	
	
}
