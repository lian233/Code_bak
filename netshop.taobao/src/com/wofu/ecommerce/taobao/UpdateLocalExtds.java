package com.wofu.ecommerce.taobao;
/**
 * 
 */
import java.sql.Connection;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class UpdateLocalExtds extends Thread{
	private static final String jobName="更新本地extds表标志";
	@Override
	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		try{
			Connection conn=null;
			String sql="";
			String url="";
			while(true){
				try{
					conn = PoolHelper.getInstance().getConnection(Params.dbname);
					sql = new StringBuilder().append("update ecs_extds set enable=1 where dsid=")
							.append(Params.localdsid).toString();
						SQLHelper.executeSQL(conn, sql);
						Log.info(jobName,"执行成功");
					long currentTime= System.currentTimeMillis();
					while(currentTime+Params.waittime*4*1000L>System.currentTimeMillis()){
						Thread.sleep(1000L);
					}
				}catch(Exception e1){
					Log.error(jobName, e1.getMessage());
				}finally{
					if(conn!=null){
						conn.close();
					}
				}
			}
		}catch(Exception e){
			Log.error(jobName, e.getMessage());
		}
	}
	
}
