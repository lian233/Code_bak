package com.wofu.base.inventory;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.util.Formatter;

public class InvUtil {
	
	public static void inv_Stock(DataCentre dao,int busid,int bustype,int booktype) throws Exception
	{
		
		String sql="select * from ecs_accinventory where busid="+busid +" and bustype="+bustype;
		
		Vector vtinventorybookbc=dao.multiRowSelect(sql);
		
		for(int i=0;i<vtinventorybookbc.size();i++)
		{
			Hashtable htinventorybookbc=(Hashtable) vtinventorybookbc.get(i);
			
			ECS_AccInventory accinventory=new ECS_AccInventory();
			
			accinventory.getMapData(htinventorybookbc);
			
			ECS_InventoryBookBC inventorybookbc=new ECS_InventoryBookBC();
				
			inventorybookbc.setOrgid(accinventory.getOrgid());
			inventorybookbc.setBusid(busid);
			inventorybookbc.setBustype(bustype);
			inventorybookbc.setDirectflag(accinventory.getDirectflag());
			inventorybookbc.setQty(accinventory.getQty());
			inventorybookbc.setPricevalue(accinventory.getPrice()*accinventory.getQty());
			inventorybookbc.setCostvalue(accinventory.getCost()*accinventory.getQty());
			inventorybookbc.setSkuid(accinventory.getSkuid());
			inventorybookbc.setPlaceid(accinventory.getPlaceid());
			inventorybookbc.setNotes(accinventory.getNotes());
			inventorybookbc.setBusidate(accinventory.getBusidate());
			
			sql="select rjdate from ecs_rjproc where orgid="+accinventory.getOrgid()+" and processor='RJInitProcessor'";
			Date rjdate=Formatter.parseDate(dao.strSelect(sql), Formatter.DATE_TIME_FORMAT);
			
			inventorybookbc.setSdate(rjdate);
			inventorybookbc.setStime(new Date());
			
			invbc_Account(dao,inventorybookbc,booktype);
			
		}
		
		sql="select busid,bustype,orgid,directflag,itemid,sum(qty) qty,"
			+"avg(price) price,avg(cost) cost,notes,placeid,busidate "
			+"from ecs_accinventory where busid="+busid +" and bustype="+bustype
			+" group by busid,bustype,orgid,directflag,itemid,notes,placeid,busidate";
		
		Vector vtinventorybook=dao.multiRowSelect(sql);
		

		for(int i=0;i<vtinventorybook.size();i++)
		{
			Hashtable htinventorybook=(Hashtable) vtinventorybook.get(i);
			
			ECS_AccInventory accinventory=new ECS_AccInventory();
			
			accinventory.getMapData(htinventorybook);
			
			ECS_InventoryBook inventorybook=new ECS_InventoryBook();
				
			inventorybook.setOrgid(accinventory.getOrgid());
			inventorybook.setBusid(busid);
			inventorybook.setBustype(bustype);
			inventorybook.setDirectflag(accinventory.getDirectflag());
			inventorybook.setQty(accinventory.getQty());
			inventorybook.setPricevalue(accinventory.getPrice()*accinventory.getQty());
			inventorybook.setCostvalue(accinventory.getCost()*accinventory.getQty());
			inventorybook.setItemid(accinventory.getItemid());
			inventorybook.setPlaceid(accinventory.getPlaceid());
			inventorybook.setNotes(accinventory.getNotes());
			inventorybook.setBusidate(accinventory.getBusidate());
			
			sql="select rjdate from ecs_rjproc where orgid="+accinventory.getOrgid()+" and processor='RJInitProcessor'";
			Date rjdate=Formatter.parseDate(dao.strSelect(sql), Formatter.DATE_TIME_FORMAT);
			
			inventorybook.setSdate(rjdate);
			inventorybook.setStime(new Date());
			
			inv_Account(dao,inventorybook,booktype);
			
		}
		
		sql="delete from ecs_accinventory where busid="+busid +" and bustype="+bustype;
		dao.execute(sql);
	}
	
	public static void invbc_Account(DataCentre dao,ECS_InventoryBookBC invbookbc,int booktype) throws Exception
	{
		int closeqty=0;
		double closecostvalue=0.0000;
		ECS_InventoryBC invbc=new ECS_InventoryBC();
		String sql="select count(*) from ecs_inventorybc "+
		" where orgid="+invbookbc.getOrgid()+
		" and skuid="+invbookbc.getSkuid()+
		" and placeid="+invbookbc.getPlaceid();
		if (dao.intSelect(sql)==0)
		{
			invbc.setOrgid(invbookbc.getOrgid());
			invbc.setSkuid(invbookbc.getSkuid());
			invbc.setPlaceid(invbookbc.getPlaceid());
			invbc.setQty(0);
			invbc.setBadqty(0);
			invbc.setLockbadqty(0);
			invbc.setLockqty(0);
			invbc.setAvgcostvalue(0.0000);
			invbc.setLastdayqty(0);
			invbc.setLastmpnthqty(0);
			
			dao.insert(invbc);
			
			closeqty=0;
			closecostvalue=0.0000;
		}
		else
		{
			sql="select *from ecs_inventorybc "+
			" where orgid="+invbookbc.getOrgid()+
			" and skuid="+invbookbc.getSkuid()+
			" and placeid="+invbookbc.getPlaceid();
			
			invbc.getMapData(dao.oneRowSelect(sql));
			
			closeqty=invbc.getQty();
			closecostvalue=invbc.getAvgcostvalue();
			
		}
	
		
		closeqty=closeqty+invbookbc.getDirectflag()*invbookbc.getQty();
		closecostvalue=closecostvalue+invbookbc.getDirectflag()*invbookbc.getCostvalue();
		
		
		invbc.setQty(closeqty);
		invbc.setAvgcostvalue(closecostvalue);
		
		dao.updateByKeys(invbc, "orgid,skuid,placeid");
		
		
		if (invbookbc.getQty()!=0 || invbookbc.getCostvalue()!=0.0000)
		{
			invbookbc.setCloseqty(closeqty);
			invbookbc.setClosecostvalue(closecostvalue);
			invbookbc.setSerialid(dao.IDGenerator(invbookbc, "serialid"));
			
			dao.insert(invbookbc);		
		}		
	}

	public static void inv_Account(DataCentre dao,ECS_InventoryBook invbook,int booktype) throws Exception
	{
		int closeqty=0;
		double closecostvalue=0.0000;
		ECS_Inventory inv=new ECS_Inventory();
		String sql="select count(*) from ecs_inventory "+
		" where orgid="+invbook.getOrgid()+
		" and itemid="+invbook.getItemid()+
		" and placeid="+invbook.getPlaceid();
		if (dao.intSelect(sql)==0)
		{
			inv.setOrgid(invbook.getOrgid());
			inv.setItemid(invbook.getItemid());
			inv.setPlaceid(invbook.getPlaceid());
			inv.setQty(0);
			inv.setBadqty(0);
			inv.setLockbadqty(0);
			inv.setLockqty(0);
			inv.setAvgcostvalue(0.0000);
			inv.setLastdayqty(0);
			inv.setLastmpnthqty(0);
			
			dao.insert(inv);
			
			closeqty=0;
			closecostvalue=0.0000;
		}
		else
		{
			sql="select *from ecs_inventory "+
			" where orgid="+invbook.getOrgid()+
			" and itemid="+invbook.getItemid()+
			" and placeid="+invbook.getPlaceid();
			
			inv.getMapData(dao.oneRowSelect(sql));
			
			closeqty=inv.getQty();
			closecostvalue=inv.getAvgcostvalue();
			
		}
		
		closeqty=closeqty+invbook.getDirectflag()*invbook.getQty();
		closecostvalue=closecostvalue+invbook.getDirectflag()*invbook.getCostvalue();
		
		
		inv.setQty(closeqty);
		inv.setAvgcostvalue(closecostvalue);
		
		dao.updateByKeys(inv,  "orgid,itemid,placeid");
		
		if (invbook.getQty()!=0 || invbook.getCostvalue()!=0.0000)
		{
			invbook.setCloseqty(closeqty);
			invbook.setClosecostvalue(closecostvalue);
			invbook.setSerialid(dao.IDGenerator(invbook, "serialid"));
			
			dao.insert(invbook);		
		}		
	}

}
