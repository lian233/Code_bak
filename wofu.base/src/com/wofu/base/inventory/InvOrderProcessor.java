package com.wofu.base.inventory;

import java.util.Hashtable;
import java.util.Vector;

import com.wofu.common.tools.util.Formatter;

public class InvOrderProcessor extends InventoryProcessor {

	
	public void execute() throws Exception {
	
		String sql="select a.outorgid,a.inorgid,a.notes,a.outplaceid,delivedate,"
			+"b.itemid,b.skuid,b.customprice,b.outqty "
			+"from ecs_order a,ecs_orderitem b where a.orderid=b.orderid "
			+"and a.orderid="+this.getBusid();
		Vector vtorder=this.getDao().multiRowSelect(sql);
		for(int i=0;i<vtorder.size();i++)
		{
			Hashtable htorder=(Hashtable) vtorder.get(i);
			
			ECS_AccInventory accinventory=new ECS_AccInventory();
			accinventory.setBusid(this.getBusid());
			accinventory.setBustype(this.getBustype());
			accinventory.setOrgid(Integer.valueOf(htorder.get("outorgid").toString()).intValue());
			accinventory.setItemid(Integer.valueOf(htorder.get("itemid").toString()).intValue());
			accinventory.setSkuid(Integer.valueOf(htorder.get("skuid").toString()).intValue());
			accinventory.setQty(Integer.valueOf(htorder.get("outqty").toString()).intValue());
			accinventory.setDirectflag(-1);
			
			accinventory.setPrice(Double.valueOf(htorder.get("customprice").toString()).doubleValue());
			accinventory.setCost(Double.valueOf(htorder.get("customprice").toString()).doubleValue());
			accinventory.setPlaceid(Integer.valueOf(htorder.get("outplaceid").toString()).intValue());
			accinventory.setBusidate(Formatter.parseDate(htorder.get("delivedate").toString(), Formatter.DATE_TIME_FORMAT));
			
			accinventory.setNotes("网购订单-销售地("+htorder.get("inorgid").toString()+") "+htorder.get("notes").toString());
			
			this.getDao().insert(accinventory);			
		}
		
		InvUtil.inv_Stock(this.getDao(), this.getBusid(), this.getBustype(), 0);
	}

}
