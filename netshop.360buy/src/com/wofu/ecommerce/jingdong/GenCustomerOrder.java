package com.wofu.ecommerce.jingdong;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.wofu.ecommerce.jingdong.Params;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class GenCustomerOrder extends Thread{
	private static String jobName = "京东订单生成客户订单作业";
	private boolean is_gening=false;
	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;
			is_gening = true;
			try 
			{
				Jingdong.setCurrentDate_genOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.jingdong.Params.dbname);	
				//生成京东新订单
				//Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				doGenCustomerOrders(connection) ;
				//生成京东退货单
				if(Params.isgenorderRet)
				doGenCustomerRefundOrders(connection) ;
			} 
			catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_gening = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jingdong.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	public static void doGenCustomerOrders(Connection connection) throws Throwable
	{
		Log.error("连接池数监测,GenCustomerOrder连接数为"+connection.getMetaData(),"");
		try 
		{
			System.out.println("是否取商城价"+Params.isDistrictMode);
			System.out.println("是否取系统价"+Params.isSystemPrice);
			Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
			Log.info("本次接口生成本地订单任务开始，数量为:　"+vts.size());
			if(vts.size()>0 && 1==Params.tableType){
				//删除已经存在的接口临时表
				String sql = "if not OBJECT_ID('tempdb..#NS_CustomerOrder0') is null drop table #NS_CustomerOrder0";
				SQLHelper.executeSQL(connection, sql);
				sql = "if not OBJECT_ID('tempdb..#NS_Orderitem0') is null drop table #NS_Orderitem0";
				SQLHelper.executeSQL(connection, sql);
				sql = "if not OBJECT_ID('tempdb..#it_downnote0') is null drop table #it_downnote0";
				SQLHelper.executeSQL(connection, sql);
				//建立接口临时表
				sql = "select * into #it_downnote0 from it_downnote where sheettype=1 and sender='"+Params.tradecontactid+"'";
				SQLHelper.executeSQL(connection,sql);
				sql = "select a.* into #ns_customerorder0 from ns_customerorder a(nolock),#it_downnote0 b where a.sheetid=b.sheetid";
				SQLHelper.executeSQL(connection, sql);
				sql = "select a.* into #ns_orderitem0 from ns_orderitem a(nolock),#it_downnote0 b where a.sheetid=b.sheetid";
				SQLHelper.executeSQL(connection, sql);
			}
			for (int i=0;i<vts.size();i++) {
				try{
					Hashtable hts=(Hashtable) vts.get(i);
					String sheetid=hts.get("sheetid").toString();
					
					//开始事务
					connection.setAutoCommit(false);
					boolean is_success;
					//生成客户订单

					
					if(Params.isDistrictMode)
						is_success=OrderManager.genCustomerOrder(connection, sheetid,1,"C",0);
					else if(Params.isSystemPrice) 
						is_success=OrderManager.genCustomerOrder(connection, sheetid,0,0,0,"5",1);
					else
						is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);
					
					if (is_success)
					{
						//备份接口数据
						IntfUtils.backupDownNote(connection, "yongjun",sheetid, "1");
						//提交事务
						connection.commit();
						connection.setAutoCommit(true);
						Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
					}
					else
					{
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Exception e1) {
							Log.error(jobName, "回滚事务失败");
						}
						Log.error(jobName,"生成客户订单失败,接口单号【" + sheetid + "】");
					}
				}catch(Throwable ex){
					if(connection!=null && !connection.getAutoCommit()) connection.rollback();
					Log.error(jobName,ex.getMessage());
				}
				
			}
		
		} catch (Throwable e) 
		{
			if(connection!=null && !connection.getAutoCommit()) connection.rollback();
			Log.error(jobName, "生成客户订单失败,"+e.getMessage()) ;
			//e.printStackTrace() ;
		}
	}

	public static void doGenCustomerRefundOrders(Connection connection) throws Throwable
	{
		try 
		{
			ArrayList<String> slist=new ArrayList<String>();
			String sql="";
			sql="select sheetid from it_downnote with(nolock) where sender='"+Params.tradecontactid+"' and sheettype=2";
			Vector vt=SQLHelper.multiRowSelect(connection, sql);
			Log.info("本次接口生成本地退货订单开始，数量为:　"+vt.size());
			for(int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				slist.add(ht.get("sheetid").toString());
			}
			
			for (Iterator it = slist.iterator(); it.hasNext();) {
				try{
					String sheetid = (String) it.next();
					sql="declare @ret int;  execute  @ret = eco_GetRefund '"+sheetid+"';select @ret ret;";
					
						connection.setAutoCommit(false);
						int ret=SQLHelper.intSelect(connection, sql);
				
						if (ret==0)
						{
							sql = "declare @err int ;exec @Err = IP_DownBak '" + sheetid + "' ,2,'yongjun';select @err;";
							int err=SQLHelper.intSelect(connection, sql);
							if (err==-1)
							{							
								throw new JException("备份数据失败!"+sql);
							}
							Log.info("生成客户退货成功,接口单号【" + sheetid + "】");
						}
						else if (ret==-1)
						{
							Log.info("生成客户退货失败!接口单号【" + sheetid + "】");
							throw new JException(sql);
						}
						connection.commit();
						connection.setAutoCommit(true);		
				}catch(Throwable ex){
					if(connection!=null && !connection.getAutoCommit()) connection.rollback();
					Log.error(jobName, ex.getMessage());
				}
			}	
		
		} catch (Throwable e) 
		{
			if(connection!=null && !connection.getAutoCommit()) connection.rollback();
			Log.error(jobName, e.getMessage());
		}
	}
	
	public String toString()
	{
		return jobName + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
	

}
