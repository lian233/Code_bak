package com.wofu.ecommerce.wqb;

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
import com.wofu.ecommerce.wqb.utils.Utils;


public class StockUtils {
	

	public static void UpdateSkuStock(DataCentre dao,int orgid,String url,String app_key,
			String app_secret,String format,String sku,int qty) throws Exception
	{

		try
		{
			Map<String, String> updatestockparams = new HashMap<String, String>();
	        //ϵͳ����������
			updatestockparams.put("user", app_key);
			updatestockparams.put("appKey", app_key);
			updatestockparams.put("format", format);
			updatestockparams.put("method", "IOpenAPI.UpdateProductSkuNum");
	        updatestockparams.put("proSkuNo", sku);
	        updatestockparams.put("proNum", String.valueOf(qty));
	        
	        String responseOrderListData = Utils.sendByPost(updatestockparams,app_secret, "IOpenAPI.UpdateProductSkuNum", url);
			Log.info("���¿�淵������ ��"+responseOrderListData);
			JSONObject responseupdatestock=new JSONObject(responseOrderListData);
			
			
			
			if (!"101".equals(responseupdatestock.getString("Code")))
			{
				
				Log.warn("������ʧ��,sku: "+sku+", ������Ϣ:"+responseupdatestock.getString("Message"));
			}
			else
			{
				Log.info("�����������ɹ�,sku: "+sku+" �������Ϊ: "+qty);
				
			}
		} catch (Exception e) {
			Log.info("�������������ʧ��,������Ϣ:"+e.getMessage());
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
				
				Log.info("�����������ɹ�,SKU:"+sku+",�¿��:"+qty);
			}
			else
			{
				sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=stockcount+"+qty+" where orgid="+orgid+" and sku='"+sku+"'";
				dao.execute(sql);
				
				sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount+"+qty
					+" where orgid="+orgid+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
				dao.execute(sql);
				
				Log.info("�����������ɹ�,SKU:"+sku+",�������:"+qty);
			}
			
		}
		
	}
	
	private static void updateStockConfig(DataCentre dao,int orgid,String outerstocklist,String errmsg) throws Exception
	{	
		/**
		 * ɾ�������ڵ�sku
		 */
		String sql="";
		if(errmsg.indexOf("ָ���Ĳ�Ʒ��Ϣ�����ڻ��Ʒ���ʹ���")!=-1){
			ArrayList<String> skus = deleteDb(errmsg);
			for(int i=0;i<skus.size();i++){
				String sku=skus.get(i);
				sql="delete ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"'";
				dao.execute(sql);
				Log.info("ɾ�������ڵ�sku�ɹ�,sku:"+sku);
				if(i==0){
					sql="update ecs_stockconfig set errflag=1,errmsg='ָ���Ĳ�Ʒ��Ϣ�����ڻ��Ʒ���ʹ���' where orgid="+orgid
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
				//�ضϴ�����Ϣ
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
        //ϵͳ����������
		updatestockparams.put("checkCode", checkcode);
		updatestockparams.put("merchantId", merchantid);
		updatestockparams.put("erp", erp);
        updatestockparams.put("erpVer", erpver);
        updatestockparams.put("format", format);
        updatestockparams.put("method", "yhd.products.stock.update");
        updatestockparams.put("ver", ver);
       

        updatestockparams.put("updateType", String.valueOf(updatetype));
        updatestockparams.put("outerStockList", sku+":"+warehouseId+":"+newqty);
        
    
        String responseOrderListData = Utils.sendByPost(updatestockparams, app_secret,"", url);
		
		
		

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
					Log.info("�ӿڷ���̫Ƶ��,��ͣ����,���Ժ�......");
					Thread.sleep(60000L);
					//PostClient.sendByPost(url, updatestockparams, secretkey);
					updateStock(url,merchantid,checkcode,
							secretkey,erp,erpver,format,ver,
							warehouseId,sku,qty,newqty,updatetype,app_secret);
				}
				//String pkInfo=errinfo.getString("pkInfo");
				
				errMsg=errMsg+errorDes;//+":"+pkInfo;
			}
			
			
			Log.warn("������ʧ��,SKU:["+sku+"],ԭ����:["+qty+"],��������:["+newqty+"] ������Ϣ:"+errMsg);
		}
		else
		{
			flag=true;
			Log.info("�����³ɹ�,SKU:["+sku+"],ԭ����:["+qty+"],��������:["+newqty+"]");
		}
		
		return flag;
		
	}
	
	/**
	 * ������ʽ��ȡ�����ڵ�sku
	 */
	public static ArrayList<String> deleteDb(String str){
	
		 
		//(BB81105100)ָ���Ĳ�Ʒ��Ϣ�����ڻ��Ʒ���ʹ���(BB81103100)ָ���Ĳ�Ʒ��Ϣ�����ڻ��Ʒ���ʹ���
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
