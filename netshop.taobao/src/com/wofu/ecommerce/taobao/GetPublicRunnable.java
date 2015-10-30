package com.wofu.ecommerce.taobao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class GetPublicRunnable implements Runnable{
	public GetPublicRunnable(Socket socket,String socketContent,String dsid,String dbname){
		this.socket=socket;
		this.socketContent= socketContent;
		this.dsid=dsid;
		this.dbname=dbname;
	}
	private Socket socket=null;
	private String socketContent;
	private String dsid;
	private String dbname;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Connection conn=null;
		InputStream is=null;
		String sql=null;
		String url =null;
		BufferedReader br=null;
		boolean isNeedUpdateIp=true;
			try{
				conn = PoolHelper.getInstance().getConnection(dbname);
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
				String content = br.readLine().trim();
				//Log.info("content: "+content);
				//Log.info("contents:　"+content);
				String[] clients = socketContent.split(";");
				for(String e:clients){
					if(SocketUtil.encryContent(e).equals(content)){
						if(e.indexOf("noupdateip")>0)
						{
							isNeedUpdateIp=false;
							e = e.substring(0,e.indexOf("noupdateip"));
							//Log.info("content: "+e);
						}
						if(isNeedUpdateIp){
							String ipaddress = socket.getRemoteSocketAddress().toString();
							String address = ipaddress.substring(1,ipaddress.indexOf(":"));
							String dsidTemp = getDsid(e,dsid);
							//Log.info("新ipadderss: "+address);
							sql ="select url from ecs_extds where dsid="+dsidTemp;
							url = SQLHelper.strSelect(conn, sql);
							url = url.replaceAll("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}", address);
							sql = new StringBuilder().append("update ecs_extds set enable=1,dsip='")
								.append(address).append("' ,url='").append(url)
								.append("' where dsid=").append(dsidTemp).toString();
							Log.info("sql: "+sql);
							SQLHelper.executeSQL(conn, sql);
							Log.info("更新客户端,dsid: "+dsidTemp+" 的ip成功!");
						}else{
							String dsidTemp = getDsid(e,dsid);
							sql = new StringBuilder().append("update ecs_extds set enable=1")
								.append(" where dsid=").append(dsidTemp).toString();
							Log.info("sql: "+sql);
							SQLHelper.executeSQL(conn, sql);
							Log.info("更新客户端,dsid: "+dsidTemp+" 的enable属性成功!");
						}
						
						
					}
				}
			}catch(Throwable e1){
				Log.error("更新extds表工作线程出错", e1.getMessage());
			}finally{
				if(is!=null){
					try{
						is.close();
						is=null;
					}catch(Exception yyy){
						Log.error("关闭输入流失败", yyy.getMessage());
					}
					
				}
				if(br!=null){
					try{
						br.close();
						br=null;
					}catch(Exception eee){
						Log.error("关闭输入流失败", eee.getMessage());
					}
					
				}
				if(socket!=null){
					try{
						socket.close();
						socket=null;
					}catch(Exception qq){
						Log.error("关闭socket失败", qq.getMessage());
					}
					
				}
				if(conn!=null){
					try{
						conn.close();
						conn=null;
					}catch(Exception w){
						Log.error("关闭数据库连接失败", w.getMessage());
					}
					
				}
				sql=null;
				url=null;
			}
	}
	
	private String getDsid(String name,String dsid) throws Exception{
		String[] dsids = dsid.split(";");
		for(String e:dsids){
			String[] info = e.split(":");
			if(name.equals(info[0]))
				return info[1];
		}
		return "";
	}
	
}
