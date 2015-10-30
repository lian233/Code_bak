package com.wofu.ecommerce.s;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.s.Params;
import com.wofu.ecommerce.s.utils.Utils;


public class StockUtils {
	

	public static void batchUpdateStock(DataCentre dao,int orgid,String VendorSkuId,String Qty) throws Exception
	{
		String sku = null;
		try
		{
//			Map<String, String> updatestockparams = new HashMap<String, String>();
//	        //系统级参数设置
//			updatestockparams.put("appKey", app_key);
//			updatestockparams.put("sessionKey", token);
//			updatestockparams.put("format", format);
//			updatestockparams.put("method", "yhd.products.stock.update");
//			updatestockparams.put("ver", ver);
//			updatestockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//	       
//	        updatestockparams.put("updateType", String.valueOf(updatetype));
//	        updatestockparams.put("outerStockList", outerstocklist);
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			Date now=new Date();
			String method="scn.vendor.inventory.incremental.update";
			//String ver=Params.ver;
			/***data部分***/
			JSONObject data=new JSONObject();
			//需要返回的字段：
			data.put("VendorSkuId", VendorSkuId);   //供货商最小单位商品唯一码,skuid该从哪里来
			data.put("Qty", Qty);			   //库存数量(非0)，正数-增加。负数-减少
			/**sign部分***/
			System.out.println(df.format(now));
			String sign=Utils.get_sign(data, method, now);
			/***合并为输出语句****/
			String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
	        
	        String responseOrderListData = Utils.sendByPost(Params.url,output_to_server);
			Log.info("更新库存返回数据 ："+responseOrderListData);
			JSONObject responseupdatestock=new JSONObject(responseOrderListData);
			
			JSONObject response=responseupdatestock;
			ArrayList<String> sqlerr;
			String errMsg="";
			
			if (response.getBoolean("IsError"))
			{
				//JSONArray errlist=response.getJSONObject("errInfoList").getJSONArray("errDetailInfo");
				sqlerr = new ArrayList<String>();			
					if(response.getString("ErrMsg").equals("增量库存更新失败"))
					{  //sku不存在
						sku=VendorSkuId;
						String sql = "delete from ecs_stockconfigsku where orgid='"+orgid+"' and sku='"+sku+"'";
						sqlerr.add(sql);
					}
					errMsg=errMsg+response.getString("ErrMsg");//+":"+pkInfo;
				if(!sqlerr.isEmpty()) SQLHelper.executeBatch(dao.getConnection(), sqlerr);
				
				
				updateStockConfig(dao,orgid,sku,errMsg);
				
				Log.warn("库存更新失败,错误信息:"+errMsg);
			}
			else
			{
	
				int updateCount=response.getInt("updateCount");
				
				Log.info("更新名鞋库成功,更新总SKU数:["+updateCount+"]");
				
				updateStockConfig(dao,orgid,sku,errMsg);
			}
		} catch (Exception e) {
			Log.info("更新名鞋库库存失败,错误信息:"+e.getMessage());
			updateStockConfig(dao,orgid,sku,e.getMessage());
		}		
	}
	
	private static void updateStockConfig(DataCentre dao,int orgid,String outerstocklist,int updatetype) throws Exception
	{

		Object[] outerstockarr=StringUtil.split(outerstocklist, ",").toArray();
		for (int i=0;i<outerstockarr.length;i++)
		{

			String outerstock=String.valueOf(outerstockarr[i]);
			
			Object[] stockinfo=StringUtil.split(outerstock,":").toArray();
			
	
			
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
				
				Log.info("更新名鞋库成功,SKU:"+sku+",新库存:"+qty);
			}
			else
			{
				sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=stockcount+"+qty+" where orgid="+orgid+" and sku='"+sku+"'";
				dao.execute(sql);
				
				sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount+"+qty
					+" where orgid="+orgid+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
				dao.execute(sql);
				
				Log.info("更新名鞋库成功,SKU:"+sku+",调整库存:"+qty);
			}
			
		}
		
	}
	/**updateStockConfig(DataCentre dao,int orgid,String skuid,String errmsg) **/
	private static void updateStockConfig(DataCentre dao,int orgid,String skuid,String errmsg) throws Exception
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
				String sku=skuid;
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
	

	public static boolean updateStock(String url,String merchantid,String checkcode,
			String secretkey,String erp,String erpver,String format,String ver,
			long warehouseId,String sku,int qty,int newqty,int updatetype,String app_secret) throws Exception
	{
		boolean flag=false;
//		Map<String, String> updatestockparams = new HashMap<String, String>();
//        //系统级参数设置
//		updatestockparams.put("checkCode", checkcode);
//		updatestockparams.put("merchantId", merchantid);
//		updatestockparams.put("erp", erp);
//        updatestockparams.put("erpVer", erpver);
//        updatestockparams.put("format", format);
//        updatestockparams.put("method", "yhd.products.stock.update");
//        updatestockparams.put("ver", ver);
//       
//
//        updatestockparams.put("updateType", String.valueOf(updatetype));
//        updatestockparams.put("outerStockList", sku+":"+warehouseId+":"+newqty);
	       //系统级参数设置
		UTF8_transformer utf8_transformer=new UTF8_transformer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		Date now=new Date();
		String method="scn.vendor.inventory.incremental.update";
		/***data部分***/
		JSONObject data=new JSONObject();
		//需要返回的字段：
		data.put("VendorSkuId",sku);   //供货商最小单位商品唯一码
		data.put("Qty",newqty);   //库存数量(非0)，正数-增加。负数-减少	
		/**sign部分***/
		System.out.println(df.format(now));
		String sign=Utils.get_sign(data, method, now);
		/***合并为输出语句****/
		String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
    
        String responseOrderListData = Utils.sendByPost(Params.url,output_to_server);

		JSONObject responseupdatestock=new JSONObject(responseOrderListData);
		
		JSONObject response=responseupdatestock;
		
//		int errorcount=response.getInt("errorCount");
		
		
		if (response.getBoolean("IsError"))
		{
			flag=false;
//			JSONArray errlist=response.getJSONObject("errInfoList").getJSONArray("errDetailInfo");
//			
			String errMsg="";
				String errorDes=response.getString("ErrMsg");
				String errorCode=response.getString("ErrCode");
				
//				if (errorCode.equals("yhd.visit.error.min_pre_visit_over"))
//				{
//					Log.info("接口访问太频繁,暂停更新,请稍候......");
//					Thread.sleep(60000L);
//					//PostClient.sendByPost(url, updatestockparams, secretkey);
//					updateStock(url,merchantid,checkcode,
//							secretkey,erp,erpver,format,ver,
//							warehouseId,sku,qty,newqty,updatetype,app_secret);
//				}
				//String pkInfo=errinfo.getString("pkInfo");
				
				errMsg=errMsg+errorDes;//+":"+pkInfo;		
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
