package com.wofu.netshop.jingdong;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

/**
 * 京东发货线程类
 * @author bolinli
 *
 */
public class DeliveryRunnable implements Runnable{
	private String jobName="京东发货作业";
	private CountDownLatch watch;
	private String username="";
	public DeliveryRunnable(CountDownLatch watch,String username){
		this.watch=watch;
		this.username=username;
	}
	public void run() {
		Connection conn=null;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			delivery(conn,getDeliveryOrders(conn));
		}catch(Exception e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(username,"关闭数据库事务出错  "+e1.getMessage(),null);
				}
				Log.info(username,"发货线程错误: "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"关闭数据库连接出错: "+e.getMessage(),null);
				}
				watch.countDown();
		}
		
	}
	
	
	private void delivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception{
		Log.info("本次发货数量 为："+vdeliveryorder.size());
		String sql = "" ;
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("post_company").toString().toUpperCase();
			String companycode = hto.get("companycode").toString().toUpperCase();
			String postNo = hto.get("post_no").toString();
			String sheetType = String.valueOf(hto.get("sheetType"));
			try 
			{
				boolean success = false ;
				
				if (companycode.equalsIgnoreCase("JDKD"))
				{
					if(!Params.jdkdNeedDelivery)
						success = true ;
					else
						success = StockUtils.SOPOrderDelivery(jobName, orderID, postCompany, postNo, Params.SERVER_URL, Params.token, Params.appKey, Params.appSecret) ;
				}
				else
				{
				
					//发货
					if("3".equals(sheetType))
					{
						if (Params.isLBP){
							success = StockUtils.LBPOrderDelivery(jobName, orderID, postCompany, postNo, Params.SERVER_URL, Params.token, Params.appKey, Params.appSecret) ;
						}
							
						else{
									success = StockUtils.SOPOrderDelivery(jobName, orderID, postCompany, postNo, Params.SERVER_URL, Params.token, Params.appKey, Params.appSecret) ;
							}
							
							
					}
					//转件
					else if("4".equals(sheetType))
						success = StockUtils.SOPModifyExpressInfo(jobName, orderID, postCompany, postNo, Params.SERVER_URL, Params.token, Params.appKey, Params.appSecret) ;
					else
					{
						Log.error(jobName, "未知单据类型:"+sheetType) ;
						continue ;
					}
				}
				
				if(success)
				{
					conn.setAutoCommit(false);
					


					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = "+sheetType;
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype="+sheetType;

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);	
				}
			}
			catch (Throwable e) 
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.info("更新发货信息失败，京东单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误代码：" + e.getMessage()) ;
			}
			
		}
		
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)
	{	

		
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			
			sql = "select a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock) "
				+ "where (a.sheettype=3 or a.sheettype=4) and a.sheetid=b.sheetid and a.receiver='"
				+ Params.tradecontactid + "' and b.companycode=c.companycode";

			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				
				
				Hashtable<String,String> ht=new Hashtable<String,String>();
			
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				
				
				
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString().trim());
				String companyid=getCompnayID(hto.get("companycode").toString().trim());
				
				if (companyid.equals("")) 
				{
					Log.info("未配置物流公司代号:"+hto.get("companycode").toString());
					continue;
				}
				
				ht.put("post_company", companyid);
				ht.put("companycode", hto.get("companycode").toString().trim());
				
				String postno=hto.get("outsid").toString().trim();
				if(postno.indexOf("-")!=-1){
					postno=postno.substring(0,postno.indexOf("-"));
				}
				ht.put("post_no", postno);
				ht.put("sheetType", String.valueOf(hto.get("sheettype"))) ;
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobName, "查询发货单信息出错:"+sqle.getMessage());
		}
		catch(Throwable e)
		{
			e.printStackTrace() ;
		}
		return vtorders;
	}
	
	private String getCompnayID(String companycode)
	{
		String companyid="";
		
	
		String com[] = Params.companycode.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			if(s[0].equals(companycode))
			{
				companyid=s[1];
				break;
			}
		}
		
		return companyid;
		
	}

}
