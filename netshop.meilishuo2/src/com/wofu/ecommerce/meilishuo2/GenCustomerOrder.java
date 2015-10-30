package com.wofu.ecommerce.meilishuo2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class GenCustomerOrder extends Thread
{
	private static String jobName = "美丽说订单生成客户订单作业";

	private boolean is_gening = false;

	// 生成美丽说新订单
	public static void doGenCustomerOrders(Connection connection)
	{
		try
		{
			Log.info("美丽说订单生成客户订单作业开始");
			Vector vts = IntfUtils.getDownNotes(connection,Params.tradecontactid, "1");
			for (int i = 0; i < vts.size(); i++)
			{
				try
				{
					Hashtable hts=(Hashtable) vts.get(i);
					String sheetid=hts.get("sheetid").toString();
					
					//开始事务
					connection.setAutoCommit(false);
					//生成客户订单
					boolean is_success=OrderManager.genCustomerOrder(connection, sheetid);
					
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
				} catch (Exception e)
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
					Log.error(jobName,e.getMessage());
				}
			}
		} catch (Exception e)
		{
			Log.error(jobName, "生成客户订单失败,"+e.getMessage()) ;
			e.printStackTrace() ;
		}
		Log.info("本次美丽说订单生成客户订单作业处理完毕！");
	}
	//生成美丽说退货单
	public static void doGenCustomerRefundOrders(Connection connection)
	{
		try 
		{	
			Log.info("美丽说退货订单生成客户退货订单作业开始");
			ArrayList<String> slist=new ArrayList<String>();
			String sql="";
			sql="select sheetid from it_downnote with(nolock) where sender='"+Params.tradecontactid+"' and sheettype=2";
			Vector vt=SQLHelper.multiRowSelect(connection, sql);
			for(int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				slist.add(ht.get("sheetid").toString());
			}
			
			for (Iterator it = slist.iterator(); it.hasNext();) {
				try{
					String sheetid = (String) it.next();
					sql="declare @ret int;  execute  @ret = eco_GetRefund '"+sheetid+"';select @ret ret;";
					try
					{
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
							
					}
					catch (SQLException e1)
					{		
						Log.info("生成客户退货失败!接口单号【" + sheetid + "】，错误信息："+e1.getMessage());
						if (!connection.getAutoCommit())
							try
							{
								connection.rollback();
							}
							catch (Exception e2) { }
						try
						{
							connection.setAutoCommit(true);
						}
						catch (Exception e3) { }
						throw e1;
					}
				}catch(Exception ex){
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
					Log.error(jobName,ex.getMessage());
				}
							
			}	
		
			
		} catch (Exception e) 
		{
			e.printStackTrace() ;
		}
		Log.info("美丽说退货订单生成客户退货订单作业完毕！");
	}
	
	public String toString()
	{
		return jobName + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}