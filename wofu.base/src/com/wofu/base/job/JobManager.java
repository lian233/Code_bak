package com.wofu.base.job;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wofu.base.dbmanager.ECS_ExtDS;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.common.pool.PoolManager;
import com.wofu.common.service.Loader;
import com.wofu.common.service.Service;
import com.wofu.common.service.ServiceException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class JobManager extends Service
{
  //存放sku->可用库存对应关系	
  public static Cache<Object,Object> useableInventory=null;
  public String description()
  {
	return "";
    //return "作业管理系统 [V " + Version.version + "]";
  }

  public static void main(String[] paramArrayOfString) {
    JobManager localJobManager = new JobManager();
    try {
      localJobManager.init(null);
    } catch (Exception e) {
      System.exit(-1);
    }
    while (true) {
      try {
        Thread.sleep(30000L);
      } catch (Exception e) {
      }
      System.gc();
    }
  }

  public void init(Properties prop)
    throws Exception
  {
    Params.init(prop);
    this.setParams(prop);
    
    loadExtDS();
    if(Params.isNeedCache)
    	createCacheOject();
  }

  public void start()
    throws Exception
  {
    TimerRunner runner=new TimerRunner(Params.groupname);
    runner.start();
    new Thread(new JVMShow(System.in)).start();
    
  }

  public void process()
  {
  }

  public void end()
    throws Exception
  {
  }
  
  private void loadExtDS() throws Exception
	{
		Connection conn = null;
		try {
			conn = PoolHelper.getInstance().getConnection(
					com.wofu.common.service.Params.getInstance().getProperty("dbname"));
	
			
			//String groupname=Params.groupname;
			//System.out.println(groupname);
			String sql="select * from ecs_extds with(nolock) where enable=1 ";
			if (Params.groupname !=""){
				sql = sql + " and groupname='"+Params.groupname+"'";	
			}			
			
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for (int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				ECS_ExtDS extds=new ECS_ExtDS();				
				extds.setConnection(conn);
				extds.getMapData(ht);
	
				Properties prop = new Properties();
				
				prop.setProperty("driver", extds.getDbdriver());

				prop.setProperty("url", extds.getUrl());
				prop.setProperty("user", extds.getDbuser());
				
				prop.setProperty("password", extds.getDbpassword());
				prop.setProperty("maxsize", String.valueOf(extds.getMaxsize()));
				
				if (extds.getEncryptflag()==1)
					prop.setProperty("encrypt", "true");
				else
					prop.setProperty("encrypt", "false");
				prop.setProperty("encoding", extds.getEncoding());
	
				Loader loader = null;
				try
				{
					loader = (Loader)Class.forName(extds.getLoadclass()).newInstance();
					loader.setName(extds.getDsname());
					loader.setKey(false);
					loader.load(prop);		
				}
				catch (Exception exception)
				{
					if (loader != null && !loader.isKey())
					{
						Log.error("初始化", "装载器[" + loader.getName() + "] 初始化错误, 已被停止." + exception.getMessage());
						extds.setEnable(0);
						extds.getDao().update(extds);
						Log.error("连接池enable属性已经更改","dsnam: "+extds.getDsname());
						continue;
					}
					if (exception instanceof ServiceException)
						throw (ServiceException)exception;
					else
						throw new ServiceException(exception);
				}
				if (loader.getParams().get("encrypt").equals("true"))
					extds.setEncryptflag(1);
				else
					extds.setEncryptflag(0);
				extds.setDbpassword(loader.getParams().getProperty("password"));
				
				extds.getDao().update(extds);
			}
		}
		 finally {			
			try {
				if (conn != null){
					conn.close();
					System.out.println("关闭数据库连接成功");
					PoolManager.getInstance().listPools(System.out);
				}	
			} catch (Exception e) {
				Log.error("WebServer", "关闭数据库连接失败:"+e.getMessage());
			}
		}
	}
  
  //建立缓存对象
  private void createCacheOject(){
	  useableInventory= CacheBuilder.newBuilder()
	  .concurrencyLevel(8)//最多八个线程同时写
	  .expireAfterWrite(6, TimeUnit.HOURS)//6小时后缓存过期
	  .initialCapacity(10000)//初始容量  10000
	  .maximumSize(30000)//最大容量  30000
	  .recordStats().build();//记录统计信息
	  
  }
}