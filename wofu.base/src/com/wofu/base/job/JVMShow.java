package com.wofu.base.job;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.wofu.common.pool.PoolManager;
import com.wofu.common.tools.util.log.Log;
import com.google.common.cache.Cache;
/**
 * 虚拟机监控
 * @author Administrator
 *
 */
public class JVMShow implements Runnable{
	private InputStream ps;
	public JVMShow(InputStream ps){
		this.ps=ps;
	}
	@Override
	public void run() {
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(ps));
			String inputStr="";
			while(true){
				try{
					inputStr = br.readLine().trim();
					if("show jvm".equals(inputStr)){
						System.out.println("当前虚拟机可用内存为: "+Runtime.getRuntime().freeMemory()/(1024*1024)+"M");
						System.out.println("当前虚拟机总内存为: "+Runtime.getRuntime().totalMemory()/(1024*1024)+"M");
						System.out.println("当前线程所在线程组活动线程数为: "+Thread.currentThread().getThreadGroup().activeCount());
						System.out.println("当前线程所在线程组信息为: "+Thread.currentThread().getThreadGroup().toString());
					}
					else if("show pool".equals(inputStr)){
						PoolManager.getInstance().listPools(System.out);
					}
					else if("show cache".equals(inputStr)){
						Cache<Object,Object> useableInventory = JobManager.useableInventory;
						System.out.println("缓存总数为: "+useableInventory.size());
						ConcurrentMap<Object ,Object> currentMap = useableInventory.asMap();
						for(Iterator it = currentMap.keySet().iterator();it.hasNext();){
							String key = (String)it.next();
							Integer value = (Integer)currentMap.get(key);
							System.out.println("sku: "+key+",可用库存: "+value);
						}
						//System.out.println("缓存命中率为: "+useableInventory.);
					}
				}catch(Throwable th){
					Log.error("监控线程出错", th.getMessage());
					continue;
				}
				
			}
		}catch(Throwable th){
			Log.error("监控线程出错", th.getMessage());
		}
		
	}

}
