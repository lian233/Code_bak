package com.wofu.ecommerce.jit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jit.utils.Utils;


public class StockUtils {
	

	public static void batchUpdateStock(DataCentre dao,int orgid,String url,String app_key,
			String token,String app_secret,String format,String outerstocklist,
			String ver,int updatetype) throws Exception
	{

		try
		{
			Map<String, String> updatestockparams = new HashMap<String, String>();
	        //系统级参数设置
			updatestockparams.put("appKey", app_key);
			updatestockparams.put("sessionKey", token);
			updatestockparams.put("format", format);
			updatestockparams.put("method", "yhd.products.stock.update");
			updatestockparams.put("ver", ver);
			updatestockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
	       
	        updatestockparams.put("updateType", String.valueOf(updatetype));
	        updatestockparams.put("outerStockList", outerstocklist);
	        
	        String responseOrderListData = Utils.sendByPost(updatestockparams, Params.app_secret, Params.url,"");
			Log.info("更新库存返回数据 ："+responseOrderListData);
			JSONObject responseupdatestock=new JSONObject(responseOrderListData);
			
			JSONObject response=responseupdatestock.getJSONObject("response");
			
			int errorcount=response.getInt("errorCount");
			
			
			if (errorcount>0)
			{
				JSONArray errlist=response.getJSONObject("errInfoList").getJSONArray("errDetailInfo");
				
				String errMsg="";
				for(int i=0;i<errlist.length();i++)
				{
					JSONObject errinfo=errlist.getJSONObject(i);
					
					String errorDes=errinfo.getString("errorDes");
					String errorCode=errinfo.getString("errorCode");
					
					if (errorCode.equals("yhd.visit.error.min_pre_visit_over"))
					{
						Log.info("接口访问太频繁,暂停更新,请稍候......");
						Thread.sleep(60000L);
						batchUpdateStock(dao,orgid,url,app_key,token,
								app_secret,format,outerstocklist,ver,updatetype);
					}

					
					errMsg=errMsg+errorDes;//+":"+pkInfo;
				}
				
				updateStockConfig(dao,orgid,outerstocklist,errMsg);
				
				Log.warn("库存更新失败,错误信息:"+errMsg);
			}
			else
			{
	
				int updateCount=response.getInt("updateCount");
				
				Log.info("更新一号店成功,更新总SKU数:["+updateCount+"]");
				
				updateStockConfig(dao,orgid,outerstocklist,updatetype);
			}
		} catch (Exception e) {
			Log.info("更新一号店库存失败,错误信息:"+e.getMessage());
			updateStockConfig(dao,orgid,outerstocklist,e.getMessage());
		}		
	}
	
	private static void updateStockConfig(DataCentre dao,int orgid,String outerstocklist,int updatetype) throws Exception
	{

		Object[] outerstockarr=StringUtil.split(outerstocklist, ",").toArray();
		for (int i=0;i<outerstockarr.length;i++)
		{

			String outerstock=String.valueOf(outerstockarr[i]);
			
			Object[] stockinfo=(Object[]) StringUtil.split(outerstock,":").toArray();
			
	
			
			String sku=String.valueOf(stockinfo[0]);
			String warehouseid=String.valueOf(stockinfo[1]);
			int qty=Integer.valueOf(String.valueOf(stockinfo[2])).intValue();
			
			String sql="";
			if (updatetype==1)
			{
				sql="select stockcount from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"'";
				int orgistockcount=dao.intSelect(sql);
				
				sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount-"+orgistockcount+"+"+qty
					+" where orgid="+orgid+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
				dao.execute(sql);
				
				sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount="+qty+" where orgid="+orgid+" and sku='"+sku+"'";
				dao.execute(sql);
				
				Log.info("更新一号店成功,SKU:"+sku+",新库存:"+qty);
			}
			else
			{
				sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=stockcount+"+qty+" where orgid="+orgid+" and sku='"+sku+"'";
				dao.execute(sql);
				
				sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount+"+qty
					+" where orgid="+orgid+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
				dao.execute(sql);
				
				Log.info("更新一号店成功,SKU:"+sku+",调整库存:"+qty);
			}
			
		}
		
	}
	
	private static void updateStockConfig(DataCentre dao,int orgid,String outerstocklist,String errmsg) throws Exception
	{	
		/**
		 * 删除不存在的sku
		 */
		String sql="";
		if(errmsg.indexOf("指定的产品信息不存在或产品类型错误")!=-1){
			ArrayList<String> skus = deleteDb(errmsg);
			for(int i=0;i<skus.size();i++){
				String sku=skus.get(i);
				sql="delete ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"'";
				dao.execute(sql);
				Log.info("删除不存在的sku成功,sku:"+sku);
				if(i==0){
					sql="update ecs_stockconfig set errflag=1,errmsg='指定的产品信息不存在或产品类型错误' where orgid="+orgid
					+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
					dao.execute(sql);
				}
				
			}
			
		}else{
			Object[] outerstockarr=StringUtil.split(outerstocklist, ",").toArray();
			for (int i=0;i<outerstockarr.length;i++)
			{

				String outerstock=String.valueOf(outerstockarr[i]);
				
				Object[] stockinfo=StringUtil.split(outerstock,":").toArray();
				
				String sku=String.valueOf(stockinfo[0]);
				//截断错误信息
				if(errmsg.length()>1024)
					errmsg=errmsg.substring(0,1023);
				sql="update ecs_stockconfigsku set errflag=1,errmsg='"+errmsg+"' where orgid="+orgid+" and sku='"+sku+"'";
				dao.execute(sql);
				
				sql="update ecs_stockconfig set errflag=1,errmsg='"+errmsg+"' where orgid="+orgid
					+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
				dao.execute(sql);
			}
		}
	
		
	}
	

	public static boolean updateStock(String url,String merchantid,String checkcode,
			String secretkey,String erp,String erpver,String format,String ver,
			long warehouseId,String sku,int qty,int newqty,int updatetype,String app_secret) throws Exception
	{
		boolean flag=false;
		Map<String, String> updatestockparams = new HashMap<String, String>();
        //系统级参数设置
		updatestockparams.put("checkCode", checkcode);
		updatestockparams.put("merchantId", merchantid);
		updatestockparams.put("erp", erp);
        updatestockparams.put("erpVer", erpver);
        updatestockparams.put("format", format);
        updatestockparams.put("method", "yhd.products.stock.update");
        updatestockparams.put("ver", ver);
       

        updatestockparams.put("updateType", String.valueOf(updatetype));
        updatestockparams.put("outerStockList", sku+":"+warehouseId+":"+newqty);
        
    
        String responseOrderListData = Utils.sendByPost(updatestockparams, Params.app_secret, Params.url,"");
		
		
		

		JSONObject responseupdatestock=new JSONObject(responseOrderListData);
		
		JSONObject response=responseupdatestock.getJSONObject("response");
		
		int errorcount=response.getInt("errorCount");
		
		
		if (errorcount>0)
		{
			flag=false;
			JSONArray errlist=response.getJSONObject("errInfoList").getJSONArray("errDetailInfo");
			
			String errMsg="";
			for(int i=0;i<errlist.length();i++)
			{
				JSONObject errinfo=errlist.getJSONObject(i);
				
				String errorDes=errinfo.getString("errorDes");
				String errorCode=errinfo.getString("errorCode");
				
				if (errorCode.equals("yhd.visit.error.min_pre_visit_over"))
				{
					Log.info("接口访问太频繁,暂停更新,请稍候......");
					Thread.sleep(60000L);
					//PostClient.sendByPost(url, updatestockparams, secretkey);
					updateStock(url,merchantid,checkcode,
							secretkey,erp,erpver,format,ver,
							warehouseId,sku,qty,newqty,updatetype,app_secret);
				}
				//String pkInfo=errinfo.getString("pkInfo");
				
				errMsg=errMsg+errorDes;//+":"+pkInfo;
			}
			
			
			Log.warn("库存更新失败,SKU:["+sku+"],原数量:["+qty+"],更新数量:["+newqty+"] 错误信息:"+errMsg);
		}
		else
		{
			flag=true;
			Log.info("库存更新成功,SKU:["+sku+"],原数量:["+qty+"],更新数量:["+newqty+"]");
		}
		
		return flag;
		
	}
	
	/**
	 * 正则表达式提取不存在的sku
	 */
	public static ArrayList<String> deleteDb(String str){
	
		 
		//(BB81105100)指定的产品信息不存在或产品类型错误(BB81103100)指定的产品信息不存在或产品类型错误
		Pattern p = Pattern.compile("(BB\\d+)");
		Matcher m = p.matcher(str);
		ArrayList<String> skus = new ArrayList<String>();
		while(m.find()){
			String sku=m.group();
			skus.add(sku);
		}
		return skus;
	}
}
