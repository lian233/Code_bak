package com.wofu.business.stock;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
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
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
public class StockManager {
	/**
	 * ���ͬ�����ٿ���¼
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
	//StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "δ����",o.getOrder_sn(), sku, qty,false);
	public static void addSynReduceStore(String modulename,Connection conn,
			String tradecontactid,String status,String tid,String sku,long qty,boolean self) 
		throws Exception	
	{

		if (sku==null || sku.equals("null")) sku="";
		
		//���ϵͳ���ò���Ҫͬ����棬���˳�����
		String sql="select isnull(value,1) from config where name='�Ƿ���Ҫͬ�����'";
		int isneedsyn=Integer.valueOf(SQLHelper.strSelect(conn, sql)).intValue();
		if (isneedsyn==0) return;   
		
		Log.info("���ӿ��ͬ����¼:"+tradecontactid+" "+tid+" "+status+" "+sku);		
		
		if (SynReduceStoreExists(modulename,conn,tradecontactid,status,tid,sku,qty)) return;
		
		if (sku.equals(""))     //���skuΪ�գ����������-1�����ҳ����������sku��ֵΪ�����Ǹ�
		{
			sku="-1";
			sql="select count(*) from ECO_SynReduceStore where tid='"+tid+"' and sku='"+sku+"'";
			while (SQLHelper.intSelect(conn, sql)>0)
			{
				sku=String.valueOf(Integer.valueOf(sku).intValue()-1); //sku��1
				//����������������sku��ͬ����¼
				sql="select count(*) from ECO_SynReduceStore where tid='"+tid+"' and sku='"+sku+"'";
			}
				
		}
	
		
		if (self)    //����ecs_rationconfig���д��ڵĻ������һ�����ͬ����¼�������Լ�����
			sql="insert into ECO_SynReduceStore(tradecontactid,saletradecontactid,status,tid,sku,qty,synflag) "
				+"select distinct d.tradecontactid,'"+tradecontactid+"','"+status+"','"+tid+"','"+sku+"',"+qty+",0 "
				+"from ecs_tradecontactorgcontrast a,ecs_rationconfig b,ecs_rationconfig c,ecs_tradecontactorgcontrast d "
				+"where a.tradecontactid="+tradecontactid +" and a.orgid=b.shoporgid "
				+"and b.rationorgid=c.rationorgid and c.shoporgid=d.orgid";
		else	  ////����ecs_rationconfig���д��ڵĻ������һ�����ͬ����¼���������Լ�����	
			sql="insert into ECO_SynReduceStore(tradecontactid,saletradecontactid,status,tid,sku,qty,synflag) "
				+"select distinct d.tradecontactid,'"+tradecontactid+"','"+status+"','"+tid+"','"+sku+"',"+qty+",0 "
				+"from ecs_tradecontactorgcontrast a,ecs_rationconfig b,ecs_rationconfig c,ecs_tradecontactorgcontrast d "
				+"where a.tradecontactid="+tradecontactid +" and a.orgid=b.shoporgid "
				+"and b.rationorgid=c.rationorgid and c.shoporgid=d.orgid and d.tradecontactid<>"+tradecontactid;
		SQLHelper.executeSQL(conn, sql);
				
	}
	//---���ٱ��ݵ����ݱ�
	public static void bakSynReduceStore(String modulename,Connection conn,String tradecontactid,String tid,String sku) 
		throws Exception
	{
		
		String sql="";
		if (tid.equals(""))
		{
			/*sql="update ECO_SynReduceStore set syntime='"
				+Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)+"',synflag=1 "
				+"where tradecontactid="+tradecontactid+" and sku='"+sku+"'";*/
			sql = new StringBuilder().append("update ECO_SynReduceStore set syntime='")
			.append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT))
			.append("' ,synflag=1 where tradecontactid=").append(tradecontactid)
			.append(" and sku='").append(sku).append("'").toString();
			SQLHelper.executeSQL(conn, sql);
			/*
			sql="insert into ECO_SynReduceStorebak select * from ECO_SynReduceStore "
				+"where tradecontactid='"+tradecontactid+"' and sku='"+sku+"'";
			SQLHelper.executeSQL(conn, sql);
			
			sql="delete from ECO_SynReduceStore "
				+"where tradecontactid='"+tradecontactid+"'  and sku='"+sku+"'";
			SQLHelper.executeSQL(conn, sql);*/
		}
		else
		{
			/*sql="update ECO_SynReduceStore set syntime='"
				+Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)+"',synflag=1 "
				+"where tradecontactid="+tradecontactid+" and tid='"+tid+"' and sku='"+sku+"'";*/
			sql = new StringBuilder().append("update ECO_SynReduceStore set syntime='")
			.append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT))
			.append("',synflag=1 where tradecontactid=")
			.append(tradecontactid).append(" and tid='")
			.append(tid).append("' and sku='").append(sku).append("'").toString();
			SQLHelper.executeSQL(conn, sql);
			/*
			sql="insert into ECO_SynReduceStorebak select * from ECO_SynReduceStore "
				+"where tradecontactid='"+tradecontactid+"' and tid='"+tid+"' and sku='"+sku+"'";
			SQLHelper.executeSQL(conn, sql);
			
			sql="delete from ECO_SynReduceStore "
				+"where tradecontactid='"+tradecontactid+"' and tid='"+tid+"' and sku='"+sku+"'";
			SQLHelper.executeSQL(conn, sql);*/
		}
		
	}
	//--�����ͬ����¼���Ƿ���ڼ�¼
	private static boolean SynReduceStoreExists(String modulename,Connection conn,
			String tradecontactid,String status,String tid,String sku,long qty) 
		throws Exception
	{		
		boolean ret=false;
		

		if (sku.equals("null")) sku="";
		
		String sql="select count(*) cnt from ECO_SynReduceStore with(nolock) "
					+" where saletradecontactid='"+tradecontactid 
					+"' and  sku='"+sku+"' and tid='"+tid+"' and qty="+qty;
		if (SQLHelper.intSelect(conn, sql)!=0)
			ret=true;
	/*	else   //���ټ��ECO_SynReduceStorebak�е�����---�Զ����ECO_SynReduceStore�а���µ�����
		{
			sql="select count(*) cnt from ECO_SynReduceStorebak with(nolock) "
				+" where saletradecontactid='"+tradecontactid+"' " 
				+" and sku='"+sku+"' and tid='"+tid+"' and qty="+qty;
			if (SQLHelper.intSelect(conn, sql)!=0)
				ret=true;
		}*/
		
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
	//--���δ���������������Ƿ���ڼ�¼
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
		/*else  //���ټ�鱸�ݱ�----eco_WaitPayStock������ݻᶨʱɾ��
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
	public static void addSynReduceStore(String modulename,DataCentre dc,
			String tradecontactid,String status,String tid,String sku,long qty,boolean self) 
		throws Exception	
	{
		
		if (sku.equals("null")) sku="";
		
		Log.info("���ӿ��ͬ����¼:"+tradecontactid+" "+tid+" "+status+" "+sku);
		
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
			String tradecontactid,String status,String tid,String sku,long qty) 
		throws Exception
	{		
		boolean ret=false;
			
			/*
			String sql="select count(*) from barcode where custombc='"+sku+"'";
			if (dc.intSelect( sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+sku+"'";
				if (dc.intSelect( sql)==1)
				{
					sql="select customercode from MultiSKURef where refcustomercode='"+sku+"'";
					sku=dc.strSelect( sql);
				}
			}
			*/
			
		if (sku.equals("null")) sku="";
		
		String sql="select count(*) cnt from ECO_SynReduceStore with(nolock) "
					+" where saletradecontactid='"+tradecontactid+"' " 
					+" and  sku='"+sku+"' and tid='"+tid+"' and qty="+qty;
		if (dc.intSelect( sql)!=0)
			ret=true;
		else
		{
			sql="select count(*) cnt from ECO_SynReduceStorebak with(nolock) "
				+" where saletradecontactid='"+tradecontactid
				+"' and sku='"+sku+"' and tid='"+tid+"' and qty="+qty;
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
			throw new JException("�ж��Ƿ���Ҫд������¼����!"+jsqle.getMessage());
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
			throw new JException("ȡ���ÿ�����!"+sqle.getMessage());
		}
		return ret;
		
	}

	public static int getTradeContactUseableStock(final Connection conn,final int tradecontactid,final String sku) throws Exception
	{
		int qty=0;
		Cache<Object,Object> useableInventory =JobManager.useableInventory;
		
		if(useableInventory!=null){
			
			float plansynrateTemp=0.0f;
			final int issynplanstock= (Integer)useableInventory.get("�Ƿ�ͬ�������ƻ����",new Callable<Integer>(){

				public Integer call() throws Exception {
					return SQLHelper.intSelect(conn, "select isnull(value,0) from config where name='�Ƿ�ͬ�������ƻ����'");
				}
				
			});
			if(issynplanstock==1){
				plansynrateTemp=(Integer)useableInventory.get("�����ƻ����ͬ��������",new Callable<Double>(){
					public Double call() throws Exception {
						String rateTemp =SQLHelper.strSelect(conn, "select isnull(value,'0.00') from config where name='�����ƻ����ͬ��������'");
						return Double.valueOf("".equals(rateTemp)?"0.00":rateTemp).doubleValue();
					
					}
					
				});
			}
			final float plansynrate=plansynrateTemp;
			qty = (Integer)useableInventory.get(sku,new Callable<Integer>(){

				@Override
				public Integer call() throws Exception {
					int qty=0;
					String sql="select barcodeid from barcode with(nolock) where custombc='"+sku+"'";
					String barcodeid=SQLHelper.strSelect(conn, sql);
					if (barcodeid.equals(""))
					{
						Log.warn("�Ҳ���SKU��"+sku+"����Ӧ������!");
						return 0;
					}
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
						//�洢���̿�淵��ֵ
						Log.info("sku: "+sku+"�洢���̿�淵��ֵ: "+qtyTemp);
						if(qtyTemp>1)
						qtyTemp=Double.valueOf(Math.floor(qtyTemp*synrate)).intValue();
						if (issynplanstock==1)
						{	
							 //����û���չ����������ƻ��������sku������
							sql="select isnull(sum(qty-FactQty),0) from planreceipt a,planreceiptitem b " 
								+"where a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and a.FinishFlag=0 and b.barcodeid='"+barcodeid+"'";
							float temp=SQLHelper.intSelect(conn, sql);
							//Log.info(barcodeid+"�����ƻ���: "+temp);
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
							
							/*//ԭ���������ƻ�ͬ��
							sql="select isnull(sum(qty),0) from planreceipt a,planreceiptitem b " 
								+"where a.sheetid not in(select plansheetid from receipt where plansheetid is not null) "
								+"and a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and b.barcodeid='"+barcodeid+"'";
							qtyTemp+=Double.valueOf(Math.floor(SQLHelper.intSelect(conn, sql)*plansynrate)).intValue();
							
						}*/
						qty+=qtyTemp;
					}
					
					sql="select isnull(sum(qty),0) from eco_WaitPayStock where sku='"+sku+"'";
					
					int waitpaystock=Integer.valueOf(SQLHelper.strSelect(conn, sql)).intValue();
					
					qty=qty-waitpaystock;
					
					Log.info("sku�����տ����Ϊ: "+qty);		
					return qty;
				}
				
			});
			return qty;
		}else{
			String sql="select barcodeid from barcode with(nolock) where custombc='"+sku+"'";
			String barcodeid=SQLHelper.strSelect(conn, sql);
			if (barcodeid.equals(""))
			{
				Log.warn("�Ҳ���SKU��"+sku+"����Ӧ������!");
				return 0;
			}
			int issynplanstock = SQLHelper.intSelect(conn, "select isnull(value,0) from config where name='�Ƿ�ͬ�������ƻ����'");
			float plansynrate=0.0f;
			if(issynplanstock==0){
				String rateTemp =SQLHelper.strSelect(conn, "select isnull(value,'0.00') from config where name='�����ƻ����ͬ��������'");
				plansynrate= "".equals(rateTemp)?0.0f:Float.valueOf(rateTemp).floatValue();
			}
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
				//�洢���̿�淵��ֵ
				Log.info("sku: "+sku+"�洢���̿�淵��ֵ: "+qtyTemp);
				if(qtyTemp>1)
				qtyTemp=Double.valueOf(Math.floor(qtyTemp*synrate)).intValue();
				if (issynplanstock==1)
				{	
					 //����û���չ����������ƻ��������sku������
					sql="select isnull(sum(qty-FactQty),0) from planreceipt a,planreceiptitem b " 
						+"where a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and a.FinishFlag=0 and b.barcodeid='"+barcodeid+"'";
					float temp=SQLHelper.intSelect(conn, sql);
					//Log.info(barcodeid+"�����ƻ���: "+temp);
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
					
					/*//ԭ���������ƻ�ͬ��
					sql="select isnull(sum(qty),0) from planreceipt a,planreceiptitem b " 
						+"where a.sheetid not in(select plansheetid from receipt where plansheetid is not null) "
						+"and a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and b.barcodeid='"+barcodeid+"'";
					qtyTemp+=Double.valueOf(Math.floor(SQLHelper.intSelect(conn, sql)*plansynrate)).intValue();
					
				}*/
				qty+=qtyTemp;
			}
			
			sql="select isnull(sum(qty),0) from eco_WaitPayStock where sku='"+sku+"'";
			
			int waitpaystock=Integer.valueOf(SQLHelper.strSelect(conn, sql)).intValue();
			
			qty=qty-waitpaystock;
			
			Log.info("sku�����տ����Ϊ: "+qty);		
			return qty;
		}
		
	}
	
	
	public static int getTradeContactUseableStock(Connection conn,int tradecontactid,String sku,boolean isJingXiao) throws Exception
	{
		int qty=0;
		
		String sql="select barcodeid from barcode with(nolock) where custombc='"+sku+"'";
		String barcodeid=SQLHelper.strSelect(conn, sql);
		if (barcodeid.equals(""))
		{
			Log.warn("�Ҳ���SKU��"+sku+"����Ӧ������!");
			return 0;
		}

		sql="select isnull(value,0) from config where name='�Ƿ�ͬ�������ƻ����'";
		int issynplanstock=SQLHelper.intSelect(conn, sql);
		
		sql="select isnull(value,'0.00') from config where name='�����ƻ����ͬ��������'";
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
			//�洢���̿�淵��ֵ
			Log.info("sku: "+sku+"�洢���̿�淵��ֵ: "+qtyTemp);
			if(qtyTemp>1)
			qtyTemp=Double.valueOf(Math.floor(qtyTemp*synrate)).intValue();
			if (issynplanstock==1)
			{	
				 //����û���չ����������ƻ��������sku������
				sql="select isnull(sum(qty-FactQty),0) from planreceipt a,planreceiptitem b " 
					+"where a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and a.FinishFlag=0 and b.barcodeid='"+barcodeid+"'";
				float temp=SQLHelper.intSelect(conn, sql);
				//Log.info(barcodeid+"�����ƻ���: "+temp);
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
				
				/*//ԭ���������ƻ�ͬ��
				sql="select isnull(sum(qty),0) from planreceipt a,planreceiptitem b " 
					+"where a.sheetid not in(select plansheetid from receipt where plansheetid is not null) "
					+"and a.sheetid=b.sheetid and a.shopid='"+dcshopid+"' and a.flag=100 and b.barcodeid='"+barcodeid+"'";
				qtyTemp+=Double.valueOf(Math.floor(SQLHelper.intSelect(conn, sql)*plansynrate)).intValue();
				
			}*/
			qty+=qtyTemp;
		}
		
		//sql="select isnull(sum(qty),0) from eco_WaitPayStock where sku='"+sku+"'";
		/**
		 *�������ÿ�治�����ռ�õ�
		int waitpaystock=Integer.valueOf(SQLHelper.strSelect(conn, sql)).intValue();
		
		qty=qty-waitpaystock;
		**/
		
		Log.info("sku�����տ����Ϊ: "+qty);		
		return qty;
		
	}
	
	//�����ֲ�ͬ��[{'�ֿ�id':'����'},{'�ֿ�2id'����������}]
	public static int getTradeContactJxUseableStock(Connection conn,String orgcode,float synrate,String sku) throws Exception
	{
		int qty=0;

		String sql="select barcodeid from barcode with(nolock) where custombc='"+sku+"'";
		String barcodeid=SQLHelper.strSelect(conn, sql);
		if (barcodeid.equals(""))
		{
			Log.warn("�Ҳ���SKU��"+sku+"����Ӧ������!");
			return 0;
		}

		sql="select isnull(value,0) from config where name='�Ƿ�ͬ�������ƻ����'";
		int issynplanstock=SQLHelper.intSelect(conn, sql);

		sql="select isnull(value,'0.00') from config where name='�����ƻ����ͬ��������'";
		double plansynrate=Double.valueOf(SQLHelper.strSelect(conn, sql)).doubleValue();

		qty=getUseableStock("",conn,orgcode,barcodeid,"0");
		//�洢���̿�淵��ֵ
		Log.info("sku: "+sku+"�洢���̿�淵��ֵ: "+qty);
		if(qty>1)
			qty=Double.valueOf(Math.floor(qty*synrate)).intValue();
		if (issynplanstock==1)
		{	
			//����û���չ����������ƻ��������sku������
			sql="select isnull(sum(qty-FactQty),0) from planreceipt a,planreceiptitem b " 
				+"where a.sheetid=b.sheetid and a.shopid='"+orgcode+"' and a.flag=100 and a.FinishFlag=0 and b.barcodeid='"+barcodeid+"'";
			float temp=SQLHelper.intSelect(conn, sql);
			//Log.info(barcodeid+"�����ƻ���: "+temp);
			if(temp>0){
				qty+=Double.valueOf(Math.floor(temp*plansynrate)).intValue();
			}


		}
		Log.info("sku�����տ����Ϊ: "+qty);		
		return qty;
		
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
	//�����޸�ʱ���������Ƿ�Ҫ�޸���ϸ��ļ�¼
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
