package com.wofu.ecommerce.huasheng;

import com.wofu.ecommerce.huasheng.util.Utils;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;

public class StockUtils {
	//更新库存
	public static void batchUpdateStock(String jobname, DataCentre dao,int orgid,String outerstocklist,int updatetype) throws Exception
	{//updatetype:1 set,2 add
		
		String[] skulist = outerstocklist.split(",");
		for(int idx = 0;idx < skulist.length;idx++)
		{
			String[] skuinfo = skulist[idx].split(":");
			String sku = skuinfo[0];
			int qty = Integer.valueOf(skuinfo[1]).intValue();

			Log.info(jobname + " 正在更新的sku:" + sku + ",库存数量:" + qty);
			try
			{
				String ParamData = "type=" + (updatetype == 1 ? "set" : "add") + "&skulist=" + sku + "&qtylist=" + qty;
		        String responseOrderListData = Utils.doRequest("stock", ParamData, true);
				JSONObject responseupdatestock=new JSONObject(responseOrderListData);
				boolean state = responseupdatestock.getBoolean("state");
				String msg = responseupdatestock.getString("msg");

				if (!state)
				{
					updateStockConfig(jobname,dao,orgid,sku,msg);
					Log.warn(jobname + " 库存更新失败,错误信息:"+msg);
				}
				else
				{
					//Log.info(jobname + " 库存更新成功,sku:" + sku);
					updateStockConfig(jobname,dao,orgid,sku,qty,updatetype);
				}
			} catch (Exception e) {
				Log.error(jobname, "更新库存失败,错误信息:"+e.getMessage());
				updateStockConfig(jobname,dao,orgid,sku,e.getMessage());
			}
		}
	}
	
	//成功更新库存后
	private static void updateStockConfig(String jobname, DataCentre dao,int orgid,String sku, int qty,int updatetype) throws Exception
	{
		String sql="";
		if (updatetype==1)	//updatetype:1 set,2 add
		{
			sql="select stockcount from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"'";
			int orgistockcount=dao.intSelect(sql);
			
			sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount-"+orgistockcount+"+"+qty
				+" where orgid="+orgid+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
			dao.execute(sql);
			
			sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount="+qty+" where orgid="+orgid+" and sku='"+sku+"'";
			dao.execute(sql);
			
			Log.info(jobname + " 库存更新成功,SKU:"+sku+",新库存:"+qty);
		}
		else
		{
			sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=stockcount+"+qty+" where orgid="+orgid+" and sku='"+sku+"'";
			dao.execute(sql);
			
			sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount+"+qty
				+" where orgid="+orgid+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
			dao.execute(sql);
			
			Log.info(jobname + " 库存更新成功,SKU:"+sku+",调整库存:"+qty);
		}
	}
	
	//更新库存失败后
	private static void updateStockConfig(String jobname, DataCentre dao,int orgid,String SKU,String errmsg) throws Exception
	{	
		/**
		 * 删除不存在的sku
		 */
		String sql="";
		if(errmsg.indexOf("无对应的产品")!=-1 || errmsg.indexOf("无对应产品")!=-1){
			sql="update ecs_stockconfig set errflag=1,errmsg='指定的产品信息不存在' where orgid="+orgid
			+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+SKU+"')";
			dao.execute(sql);
			
			sql="delete ecs_stockconfigsku where orgid="+orgid+" and sku='"+SKU+"'";
			dao.execute(sql);
			Log.info(jobname + " 删除不存在的sku成功,sku:"+SKU);
		}else{
			//截断错误信息
			if(errmsg.length()>1024)
				errmsg=errmsg.substring(0,1023);
			
			sql="update ecs_stockconfigsku set errflag=1,errmsg='"+errmsg+"' where orgid="+orgid+" and sku='"+SKU+"'";
			dao.execute(sql);
			
			sql="update ecs_stockconfig set errflag=1,errmsg='"+errmsg+"' where orgid="+orgid
				+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+SKU+"')";
			dao.execute(sql);
		}
	}
}
