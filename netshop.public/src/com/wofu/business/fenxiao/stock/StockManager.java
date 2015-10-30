package com.wofu.business.fenxiao.stock;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.Callable;

import com.google.common.cache.Cache;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.job.JobManager;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.ecommerce.stockmanager.DecItem;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

public class StockManager {
	//往分销商品记录表添加记录
	public static void addDecItem(String jobName,DataCentre dao,HashMap map) throws Exception{
		String sql ="select count(*) from DecItem where CustomerID="+map.get("CustomerID")+" and shopid="
		+map.get("shopid")+" and num_iid="+map.get("num_iid")+" and sku_id='"+map.get("sku_id")+"'";
		
		if(dao.intSelect(sql)!=0){
			sql ="select count(*) from DecItem where CustomerID="+map.get("CustomerID")+" and shopid="
			+map.get("shopid")+" and num_iid="+map.get("num_iid")+" and sku_id='"+map.get("sku_id")+"' and modified>='"+Formatter.format((Date)map.get("modified"),Formatter.DATE_TIME_FORMAT)+"'";
			if(dao.intSelect(sql)!=0) return ;
			DecItem item = new DecItem();
			item.getMapData(map);
			dao.updateByKeys(item,"CustomerID,ShopID,num_iid,sku_id");
		}else{
			DecItem item = new DecItem();
			item.getMapData(map);
			item.setAlarmqty(10);
			item.setIsneedsyn(1);
			item.setSynrate(1.0f);
			dao.insert(item);
		}
		
		
	}
	/**
	 * 添加同步减少库存记录
	 * @param modulename
	 * @param conn
	 * @param tradecontactid
	 * @param status
	 * @param tid
	 * @param sku
	 * @param qty
	 * @param self
	 * @throws Exception
	 */
	//StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "未发货",o.getOrder_sn(), sku, qty,false);
	public static void addSynReduceStore(String modulename,Connection conn,
			int saletshopid,int salecustomerid,String status,String tid,String sku,long qty,boolean self) 
		throws Exception	
	{
		String sql=null;
		if (sku==null || sku.equals("null") || "".equals(sku)) return;
		
		Log.info("增加库存同步记录:"+saletshopid+" "+tid+" "+status+" "+sku);		
	
		if (SynReduceStoreExists(modulename,conn,saletshopid,salecustomerid,tid,sku,qty)) return;
		
		if (self)    //给在ecs_rationconfig表中存在的机构添加一条库存同步记录，包括自己在内
			sql=new StringBuilder("insert into des_SynReduceStore(shopid,customerid,saletshopid,")
					.append("salecustomerid,status,tid,sku,qty,CreateTime,flag)")
					.append("select a.id,a.customerid,").append(saletshopid).append(",")
					.append(salecustomerid).append(",'").append(status).append("','")
					.append(tid).append("','").append(sku).append("',").append(qty)
					.append(",getdate(),0 from decshop a where a.isUpdateStock=1").toString();
				
		else	  ////给在ecs_rationconfig表中存在的机构添加一条库存同步记录，不包括自己在内	
			sql=new StringBuilder("insert into des_SynReduceStore(shopid,customerid,saletshopid,")
		.append("salecustomerid,status,tid,sku,qty,CreateTime,flag)")
		.append("select a.id,a.customerid,").append(saletshopid).append(",")
		.append(salecustomerid).append(",'").append(status).append("','")
		.append(tid).append("','").append(sku).append("',").append(qty)
		.append(",getdate(),0 from decshop a where a.id<>").append(saletshopid)
		.append(" and a.isUpdateStock=1").toString();
		SQLHelper.executeSQL(conn, sql);
				
	}
	//---不再备份到备份表
	public static void bakSynReduceStore(String modulename,Connection conn,String tradecontactid,String tid,String sku) 
		throws Exception
	{
		
		String sql="insert into ";
		
		
	}
	//--检查库存同步记录表是否存在记录
	private static boolean SynReduceStoreExists(String modulename,Connection conn,
			int saletshopid,int salecustomerid, String tid,String sku,long qty) 
		throws Exception
	{		
		boolean ret=false;
		

		if (sku.equals("null")) sku="";
		
		String sql="select count(*) cnt from des_SynReduceStore with(nolock) "
					+" where saletshopid="+saletshopid 
					+" and  salecustomerid="+salecustomerid+" and tid='"+tid+"' and sku='"+sku+"'";
		if (SQLHelper.intSelect(conn, sql)!=0)
			ret=true;
		else   //不再检查ECO_SynReduceStorebak中的数据---自动清除ECO_SynReduceStore中半个月的数据
		{
			sql="select count(*) cnt from des_SynReduceStorebak with(nolock) "
				+" where saletshopid="+saletshopid+" " 
				+" and salecustomerid="+salecustomerid+" and tid='"+tid+"' and sku='"+sku+"'";
			if (SQLHelper.intSelect(conn, sql)!=0)
				ret=true;
		}
		
		return ret;
		
	
	}
	
	public static void addWaitPayStock(String modulename,Connection conn,String tradecontactid,String tid,String sku,long qty) 
		throws Exception
	{
		
		if (WaitPayStockExists(modulename,conn,tradecontactid,tid,sku)) return;
	
		String sql="select count(*) from barcode with(nolock) where custombc='"+sku+"'";
		if (SQLHelper.intSelect(conn, sql)>0)
		{
			sql="insert into eco_WaitPayStock(tradecontactid,tid,sku,qty) "
				+"values("+tradecontactid+",'"+tid+"','"+sku+"',"+qty+")";
			SQLHelper.executeSQL(conn, sql);
			
		}
		else
		{
			sql="select count(*) from MultiSKURef with(nolock) where refcustomercode='"+sku+"'";
			if (SQLHelper.intSelect(conn, sql)>0)
			{
				sql="insert into eco_WaitPayStock(tradecontactid,tid,sku,qty) "
					+"select "+tradecontactid+",'"+tid+"',customercode,"+qty+" from MultiSKURef where refcustomercode='"+sku+"'";
				SQLHelper.executeSQL(conn, sql);
			}
		}
	}
	//--检查未付款锁定库存表中是否存在记录
	public static boolean WaitPayStockExists(String modulename,Connection conn,String tradecontactid,String tid,String sku)
		throws Exception
	{
		boolean ret=false;
	
		String sql="select count(*) cnt from eco_WaitPayStock with(nolock) where tradecontactid="+tradecontactid
					+"  and tid='"+tid+"' and (sku='"+sku+"' or  sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"'))";
		if (SQLHelper.intSelect(conn, sql)!=0)
		{
			ret=true;
		}
		/*else  //不再检查备份表----eco_WaitPayStock表的数据会定时删除
		{
		
			sql="select count(*) cnt from eco_WaitPayStockbak with(nolock) where tradecontactid='"+tradecontactid+"' " 
			+"  and tid='"+tid+"' and (sku='"+sku+"' or  sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"'))";
			
			if(SQLHelper.intSelect(conn, sql)!=0) 
				ret=true;
		}*/


		return ret;
	}
	
	public static void deleteWaitPayStock(String modulename,Connection conn,String tradecontactid,String tid,String sku) 
		throws Exception
	{

		String sql="select count(*) from eco_WaitPayStock with(nolock) "
			+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
		if (SQLHelper.intSelect(conn, sql)>0)
		{
			sql="select count(*) from eco_WaitPayStockbak with(nolock) "
				+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
			if(SQLHelper.intSelect(conn, sql)==0){
				sql="insert into eco_waitpaystockbak select *,getdate() from eco_WaitPayStock "
					+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
				SQLHelper.executeSQL(conn, sql);
			}
			sql="delete from  eco_WaitPayStock where tradecontactid="+tradecontactid
				+" and tid='"+tid+"' and sku='"+sku+"'";						
			SQLHelper.executeSQL(conn, sql);
		
		}
		else
		{
			sql="select count(*) cnt from eco_WaitPayStock with(nolock) where tradecontactid="+tradecontactid
				+" and tid='"+tid+"' and sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"')";
			if (SQLHelper.intSelect(conn, sql)>0)
			{
				sql="select count(*) cnt from eco_WaitPayStockbak with(nolock) where tradecontactid="+tradecontactid
				+" and tid='"+tid+"' and sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"')";
				if(SQLHelper.intSelect(conn, sql)==0){
					sql="insert into eco_waitpaystockbak select *,getdate() from eco_WaitPayStock "
						+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' "
						+"and sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"')";
					SQLHelper.executeSQL(conn, sql);
				}
				
				sql="delete from  eco_WaitPayStock where tradecontactid="+tradecontactid
					+" and tid='"+tid+"' and sku in(select customercode from MultiSKURef "
					+"where refcustomercode='"+sku+"')";						
				SQLHelper.executeSQL(conn, sql);
			}
		}
	
	}
	/**
	public static void addSynReduceStore(String modulename,DataCentre dc,
			String tradecontactid,String status,String tid,String sku,long qty,boolean self) 
		throws Exception	
	{
		
		if (sku.equals("null")) sku="";
		
		Log.info("增加库存同步记录:"+tradecontactid+" "+tid+" "+status+" "+sku);
		//
		//private static boolean SynReduceStoreExists(String modulename,DataCentre dc,
				//int saleshopid,int salecustomerid,String tid,String sku,long qty) 
		//
		if (SynReduceStoreExists(modulename,dc,tradecontactid,status,tid,sku,qty)) return;

			
		String sql="";
		
		if (self)
			sql="insert into ECO_SynReduceStore(tradecontactid,saletradecontactid,status,tid,sku,qty,synflag) "
				+"select distinct d.tradecontactid,'"+tradecontactid+"','"+status+"','"+tid+"','"+sku+"',"+qty+",0 "
				+"from ecs_tradecontactorgcontrast a,ecs_rationconfig b,ecs_rationconfig c,ecs_tradecontactorgcontrast d "
				+"where a.tradecontactid="+tradecontactid +" and a.orgid=b.shoporgid "
				+"and b.rationorgid=c.rationorgid and c.shoporgid=d.orgid";
		else		
			sql="insert into ECO_SynReduceStore(tradecontactid,saletradecontactid,status,tid,sku,qty,synflag) "
				+"select distinct d.tradecontactid,'"+tradecontactid+"','"+status+"','"+tid+"','"+sku+"',"+qty+",0 "
				+"from ecs_tradecontactorgcontrast a,ecs_rationconfig b,ecs_rationconfig c,ecs_tradecontactorgcontrast d "
				+"where a.tradecontactid="+tradecontactid +" and a.orgid=b.shoporgid "
				+"and b.rationorgid=c.rationorgid and c.shoporgid=d.orgid and d.tradecontactid<>"+tradecontactid;
		dc.execute( sql);

	}
	**/
	
	public static void bakSynReduceStore(String modulename,DataCentre dc,String tradecontactid,String tid,String sku) 
		throws Exception
	{
		
		if (sku.equals("null")) sku="";
		
		String sql="update ECO_SynReduceStore set syntime='"
			+Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)+"',synflag=1 "
			+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
		dc.execute( sql);
		
		sql="insert into ECO_SynReduceStorebak select * from ECO_SynReduceStore "
			+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
		dc.execute( sql);
		
		sql="delete from ECO_SynReduceStore "
			+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
		dc.execute( sql);
		
	}
	
	private static boolean SynReduceStoreExists(String modulename,DataCentre dc,
			int saleshopid,int salecustomerid,String tid,String sku,long qty) 
		throws Exception
	{		
		boolean ret=false;
		
		String sql="select count(*) cnt from des_SynReduceStore with(nolock) "
					+" where saletshopid="+saleshopid
					+" and  salecustomerid="+salecustomerid+" and tid='"+tid+"' and sku='"+sku+"'";
		if (dc.intSelect( sql)!=0)
			ret=true;
		else
		{
			sql="select count(*) cnt from des_SynReduceStorebak with(nolock) "
				+" where saletshopid="+saleshopid
				+" and  salecustomerid="+salecustomerid+" and tid='"+tid+"' and sku='"+sku+"'";
			if (dc.intSelect( sql)!=0)
				ret=true;
		}
		
		
		return ret;
	}
	
	public static void addWaitPayStock(String modulename,DataCentre dc,String tradecontactid,String tid,String sku,long qty) 
		throws Exception
	{
		
		if (WaitPayStockExists(modulename,dc,tradecontactid,tid,sku)) return;
	
		String sql="select count(*) from barcode with(nolock) where custombc='"+sku+"'";
		if (dc.intSelect(sql)>0)
		{
			sql="insert into eco_WaitPayStock(tradecontactid,tid,sku,qty) "
				+"values("+tradecontactid+",'"+tid+"','"+sku+"',"+qty+")";
			dc.execute( sql);
			
		}
		else
		{
			sql="select count(*) from MultiSKURef with(nolock) where refcustomercode='"+sku+"'";
			if (dc.intSelect( sql)>0)
			{
				sql="insert into eco_WaitPayStock(tradecontactid,tid,sku,qty) "
					+"select "+tradecontactid+",'"+tid+"',customercode,"+qty+" from MultiSKURef where refcustomercode='"+sku+"'";
				dc.execute( sql);
			}
		}
			
	
	}
	
	public static boolean WaitPayStockExists(String modulename,DataCentre dc,String tradecontactid,String tid,String sku)
		throws Exception
	{
		boolean ret=false;
		try
		{
			
			String sql="select count(*) cnt from eco_WaitPayStock with(nolock) where tradecontactid="+tradecontactid
						+" and tid='"+tid+"' and (sku='"+sku+"' or  sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"'))";
			if (dc.intSelect( sql)!=0)
			{
				ret=true;
			}
			else
			{
			
				sql="select count(*) cnt from eco_WaitPayStockbak with(nolock) where tradecontactid="+tradecontactid
				+" and tid='"+tid+"' and (sku='"+sku+"' or  sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"'))";
				
				if(dc.intSelect( sql)!=0) 
					ret=true;
			}

		}catch(JSQLException jsqle)
		{
			throw new JException("判断是否需要写锁定记录出错!"+jsqle.getMessage());
		}
		return ret;
	}
	
	public static void deleteWaitPayStock(String modulename,DataCentre dc,String tradecontactid,String tid,String sku) 
		throws Exception
	{
		
		String sql="select count(*) from eco_WaitPayStock with(nolock) "
			+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
		if (dc.intSelect(sql)>0)
		{
			sql="select count(*) from eco_WaitPayStockbak with(nolock) "
				+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
			if(dc.intSelect(sql)==0)
			sql="insert into eco_waitpaystockbak select *,getdate() from eco_WaitPayStock "
				+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";
			dc.execute( sql);
			
			sql="delete from  eco_WaitPayStock where tradecontactid="+tradecontactid
				+" and tid='"+tid+"' and sku='"+sku+"'";						
			dc.execute( sql);
		
		}
		else
		{
			sql="select count(*) cnt from eco_WaitPayStock with(nolock) where tradecontactid="+tradecontactid
				+" and tid='"+tid+"' and sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"')";
			if (dc.intSelect( sql)>0)
			{
				sql="select count(*) cnt from eco_WaitPayStock with(nolock) where tradecontactid="+tradecontactid
				+" and tid='"+tid+"' and sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"')";
				if(dc.intSelect(sql)==0)
				sql="insert into eco_waitpaystockbak select *,getdate() from eco_WaitPayStock "
					+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' "
					+"and sku in(select customercode from MultiSKURef where refcustomercode='"+sku+"')";
				dc.execute( sql);
				
				sql="delete from  eco_WaitPayStock where tradecontactid="+tradecontactid
					+" and tid='"+tid+"' and sku in(select customercode from MultiSKURef "
					+"where refcustomercode='"+sku+"')";						
				dc.execute( sql);
			}
		}
		
	}
	
	public static int getUseableStock(String modulename,Connection conn,String dcshopid,String barcodeid,String placeid) 
		throws JException
	{
		int ret=0;
		try
		{			
			String sql = "declare @qty int; select @qty=dbo.TL_GetUseableStock('"+ dcshopid+ "','"+barcodeid+"',"+placeid+") select @qty";
			ret=SQLHelper.intSelect(conn, sql);
		}catch(SQLException sqle)
		{
			throw new JException("取可用库存出错!"+sqle.getMessage());
		}
		return ret;
		
	}

	public static int getTradeContactUseableStock(Connection conn,String sku) throws Exception
	{
		int qty=0;
		
		String sql="select barcodeid from barcode with(nolock) where custombc='"+sku+"'";
		String barcodeid=SQLHelper.strSelect(conn, sql);
		if (barcodeid.equals(""))
		{
			Log.warn("找不到SKU【"+sku+"】对应的条码!");
			return 0;
		}
		sql ="select UseQty from DistributeInventoryBC where BarcodeID='"+barcodeid+"'";
		qty = SQLHelper.intSelect(conn, sql);
		return qty;
		
	}
	
	public static int getTradeContactUseableStock(Connection conn,int tradecontactid,String sku,boolean isJingXiao) throws Exception
	{
		int qty=0;
		
		String sql="select barcodeid from barcode with(nolock) where custombc='"+sku+"'";
		String barcodeid=SQLHelper.strSelect(conn, sql);
		if (barcodeid.equals(""))
		{
			Log.warn("找不到SKU【"+sku+"】对应的条码!");
			return 0;
		}

		sql="select isnull(value,0) from config where name='是否同步来货计划库存'";
		int issynplanstock=SQLHelper.intSelect(conn, sql);
		
		sql="select isnull(value,'0.00') from config where name='来货计划库存同步库存比例'";
		double plansynrate=Double.valueOf(SQLHelper.strSelect(conn, sql)).doubleValue();
		
		
		sql="select distinct a.orgcode,c.synstockrate from ecs_org a,ecs_tradecontactorgcontrast b,ecs_rationconfig c "
			+"where a.orgid=c.rationorgid and c.shoporgid=b.orgid and b.tradecontactid="+tradecontactid;
		Vector dclist=SQLHelper.multiRowSelect(conn, sql);
			
		for (int i=0;i<dclist.size();i++)
		{
			int qtyTemp=0;
			Hashtable htdc=(Hashtable) dclist.get(i);
			
			String dcshopid=htdc.get("orgcode").toString();
			double synrate=Double.valueOf(htdc.get("synstockrate").toString()).doubleValue();
			qtyTemp=getUseableStock("",conn,dcshopid,barcodeid,"0");
			//存储过程库存返回值
			Log.info("sku: "+sku+"存储过程库存返回值: "+qtyTemp);
			if(qtyTemp>1)
			qtyTemp=Double.valueOf(Math.floor(qtyTemp*synrate)).intValue();
			if (issynplanstock==1)
			{	
				 //处理还没有收过货的来货计划单中这个sku的总数
				sql="select isnull(sum(qty-FactQty),0) from planreceipt a,planreceiptitem b " 
					+"where a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and a.FinishFlag=0 and b.barcodeid='"+barcodeid+"'";
				float temp=SQLHelper.intSelect(conn, sql);
				//Log.info(barcodeid+"来货计划数: "+temp);
				if(temp>0){
					qtyTemp+=Double.valueOf(Math.floor(temp*plansynrate)).intValue();
				}
				
				//************************************
				
				//*********************************************************************:::::::::::::::::
				/**
				sql="select isnull(sum(qty),0) from planreceipt a,planreceiptitem b " 
					+"where a.sheetid not in(select plansheetid from receipt where plansheetid is not null) "
					+"and a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and b.barcodeid='"+barcodeid+"'";
				qtyTemp+=Double.valueOf(Math.floor(SQLHelper.intSelect(conn, sql)*plansynrate)).intValue();
				*/
				
				//******************************************************:::::::::::::::::::::::::::::::::
			}
				
				/*//原来的来货计划同步
				sql="select isnull(sum(qty),0) from planreceipt a,planreceiptitem b " 
					+"where a.sheetid not in(select plansheetid from receipt where plansheetid is not null) "
					+"and a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and b.barcodeid='"+barcodeid+"'";
				qtyTemp+=Double.valueOf(Math.floor(SQLHelper.intSelect(conn, sql)*plansynrate)).intValue();
				
			}*/
			qty+=qtyTemp;
		}
		
		//sql="select isnull(sum(qty),0) from eco_WaitPayStock where sku='"+sku+"'";
		/**
		 *经销可用库存不算电商占用的
		int waitpaystock=Integer.valueOf(SQLHelper.strSelect(conn, sql)).intValue();
		
		qty=qty-waitpaystock;
		**/
		
		Log.info("sku的最终库存数为: "+qty);		
		return qty;
		
	}
	

	
	public static int getFenxiaoTradeContactUseableStock(final Connection conn,final int customerid,int shopid,final String sku) throws Exception
	{
		int qty=0;
		Cache<Object,Object> useableInventory =JobManager.useableInventory;
		final float  synrate ;
		String sql= "select synstockrate from dec_rationconfig where customerid="+customerid+" and shopid="+shopid;
		String temp =SQLHelper.strSelect(conn, sql);
		synrate = Float.valueOf("".equals(temp)?"1":temp).floatValue();
		if(useableInventory!=null){
			qty = (Integer)useableInventory.get(sku,new Callable<Integer>(){

				@Override
				public Integer call() throws Exception {
					int qty=0;
					String sql="select barcodeid from barcode with(nolock) where custombc='"+sku+"'";
					String barcodeid=SQLHelper.strSelect(conn, sql);
					if (barcodeid.equals(""))
					{
						Log.warn("找不到SKU【"+sku+"】对应的条码!");
						return 0;
					}
					sql="select useqty from DistributeInventoryBC where barcodeid='"+barcodeid+"'";
					qty = Double.valueOf(Math.floor(Double.valueOf(SQLHelper.intSelect(conn, sql)*synrate))).intValue();
					Log.info("分销sku的最终库存数为: "+qty);		
					return qty;
				}
				
			});
			return qty;
		}else{
			sql="select barcodeid from barcode with(nolock) where custombc='"+sku+"'";
			String barcodeid=SQLHelper.strSelect(conn, sql);
			if (barcodeid.equals(""))
			{
				Log.warn("找不到SKU【"+sku+"】对应的条码!");
				return 0;
			}
			sql="select useqty from DistributeInventoryBC where barcodeid='"+barcodeid+"'";
			qty = Double.valueOf(Math.floor(Double.valueOf(SQLHelper.intSelect(conn, sql)*synrate))).intValue();
			Log.info("sku的最终库存数为: "+qty);		
			return qty;
		}
		
	}
	


	public static void stockConfig(DataCentre dao,int orgid,int tradecontactid,String itemid,String itemcode,String title,int qty) throws Exception
	{
		if (itemcode ==null) itemcode="";
		
		Log.info("itemid:"+itemid+",itemcode:"+itemcode);
		
		String sql="select count(*) from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		if (dao.intSelect(sql)==0)
		{	
			sql="select defaultalarmqty,defaultalarmstyle from tradecontacts with(nolock)where tradecontactid="+tradecontactid;
			Hashtable ht=dao.oneRowSelect(sql);
			int defaultalarmqty=Integer.valueOf(ht.get("defaultalarmqty").toString());
			int defaultalarmstyle=Integer.valueOf(ht.get("defaultalarmstyle").toString());
			
			ECS_StockConfig stockconfig=new ECS_StockConfig();
			
			stockconfig.setSerialid(dao.IDGenerator(stockconfig, "serialid"));
			stockconfig.setOrgid(orgid);
			stockconfig.setItemid(itemid);
			stockconfig.setItemcode(itemcode);
			stockconfig.setTitle(title);
			stockconfig.setAlarmqty(defaultalarmqty);
			stockconfig.setAlarmstyle(defaultalarmstyle);
			stockconfig.setIsneedsyn(1);
			stockconfig.setAddstockqty(0.00);
			stockconfig.setStockcount(qty);
			stockconfig.setCreator("system");
			stockconfig.setCreatetime(new Date());
			stockconfig.setUpdator("system");
			stockconfig.setUpdatetime(new Date());
			dao.insert(stockconfig);
			
		}
		else
		{	
			sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
			ECS_StockConfig stockconfig=new ECS_StockConfig();
			stockconfig.getMapData(dao.oneRowSelect(sql));
			stockconfig.setItemcode(itemcode);
			stockconfig.setTitle(title);
			stockconfig.setUpdatetime(new Date());
			dao.updateByKeys(stockconfig, "orgid,itemid");
		}
	}
	
	public static void addStockConfigSku(DataCentre dao,int orgid,String itemid,String skuid,String sku,int qty) throws Exception
	{
		if (sku==null) sku="";
		
		Log.info("itemid:"+itemid+",skuid:"+skuid+",sku:"+sku);
		
		String sql="select count(*) from ecs_StockConfigsku with(nolock) where orgid="+orgid+" and itemid='"+itemid+"' and skuid='"+skuid+"'";
		if (dao.intSelect(sql)==0)
		{
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.setOrgid(orgid);
			stockconfigsku.setItemid(itemid);
			stockconfigsku.setSkuid(skuid);
			stockconfigsku.setSku(sku);
			stockconfigsku.setStockcount(qty);
			stockconfigsku.setSynrate(1.00f);
			dao.insert(stockconfigsku);
		}
		else
		{
			sql="select orgid,itemid,skuid,stockcount,synrate from ecs_StockConfigsku with(nolock) where orgid="+orgid+" and itemid='"+itemid+"' and skuid='"+skuid+"'";
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(dao.oneRowSelect(sql));
			stockconfigsku.setSku(sku);
			dao.updateByKeys(stockconfigsku, "orgid,itemid,skuid");
		}
		
	}
	//根据修改时间来决定是否要修改明细表的记录
	public static void addStockConfigSku(DataCentre dao,int orgid,String itemid,String skuid,String sku,int qty,Date modifiedIime) throws Exception
	{
		if (sku==null) sku="";
		
		Log.info("itemid:"+itemid+",skuid:"+skuid+",sku:"+sku);
		
		String sql="select count(*) from ecs_StockConfigsku with(nolock) where orgid="+orgid+" and itemid='"+itemid+"' and skuid='"+skuid+"'";
		if (dao.intSelect(sql)==0)
		{
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.setOrgid(orgid);
			stockconfigsku.setItemid(itemid);
			stockconfigsku.setSkuid(skuid);
			stockconfigsku.setSku(sku);
			stockconfigsku.setStockcount(qty);
			stockconfigsku.setSynrate(1.00f);
			dao.insert(stockconfigsku);
		}
		else
		{
			sql="select orgid,itemid,skuid,synrate from ecs_StockConfigsku with(nolock) where orgid="+orgid+" and itemid='"+itemid+"' and skuid='"+skuid+"'";
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(dao.oneRowSelect(sql));
			stockconfigsku.setSku(sku);
			dao.updateByKeys(stockconfigsku, "orgid,itemid,skuid");
		}
		
	}
	
	
	
	
}
