package com.wofu.ecommerce.yz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.ecommerce.yz.utils.Utils;


public class StockUtils {
	

	public static void updateStock(DataCentre dao,ECS_StockConfig ecs_stockconfig,String url,String app_id,
			String AppSecret,String format,ECS_StockConfigSku ecs_StockConfigSku,
			String ver,int qty,boolean isLast) throws Exception
	{
		try
		{
			Map<String, String> updatestockparams = new HashMap<String, String>();
	        //ϵͳ����������
			updatestockparams.put("app_id", app_id);
			updatestockparams.put("format", format);
			updatestockparams.put("method", "kdt.item.sku.update");
			updatestockparams.put("sign_method", "MD5");
			updatestockparams.put("v", ver);
			updatestockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			updatestockparams.put("num_iid", ecs_stockconfig.getItemid());
			updatestockparams.put("sku_id", ecs_StockConfigSku.getSkuid());
			updatestockparams.put("outer_id", ecs_StockConfigSku.getSku());
			updatestockparams.put("quantity", String.valueOf(qty));
	        String responseOrderListData = Utils.sendByPost(updatestockparams, AppSecret, url);
			Log.info("���¿�淵������ ��"+responseOrderListData);
			JSONObject responseupdatestock=new JSONObject(responseOrderListData);
			if(responseupdatestock.isNull("code")){//���³ɹ�
				ecs_StockConfigSku.setErrflag(0);
				ecs_StockConfigSku.setErrmsg("");
				ecs_StockConfigSku.setStockcount(qty);
				dao.updateByKeys(ecs_StockConfigSku, "orgid,itemid,sku");
				Log.info("���¿��ɹ���sku��"+ecs_StockConfigSku.getSku());
			}else{
				String errmstg = responseupdatestock.getString("msg");
				ecs_StockConfigSku.setErrflag(1);
				ecs_StockConfigSku.setErrmsg(errmstg);
				dao.updateByKeys(ecs_StockConfigSku, "orgid,itemid,sku");
				Log.info("���¿��ʧ�ܣ�sku��"+ecs_StockConfigSku.getSku()+"������Ϣ: "+errmstg);
			}
			if(isLast){
				String sql = "select sum(stockcount) from ecs_stockconfigsku where orgid='" +
				ecs_stockconfig.getOrgid()+"' and itemid='"+ecs_stockconfig.getItemid()+"' and errflag=0";
				int totalCount = dao.intSelect(sql);
				ecs_stockconfig.setStockcount(totalCount);
				dao.updateByKeys(ecs_stockconfig,"orgid,itemid");
			}
		} catch (Exception e) {
			Log.info("�������޿��ʧ��,������Ϣ:"+e.getMessage());
		}		
	}
	
	private static void updateStockConfig(DataCentre dao,ECS_StockConfig ecs_stockconfig,JSONObject outerstocklist) throws Exception
	{
		JSONArray array = outerstocklist.names();
		String sql =null;
		String errmsg = null;
		List errlist = new ArrayList<String>();
		StringBuilder errcount = new StringBuilder();
		for(int i=0;i<array.length();i++){
			String obj = array.getString(i);
			JSONObject item = outerstocklist.getJSONObject(obj);
			if(item.isNull("code")){
				sql = new StringBuilder("update ecs_stockconfigsku set errmsg='").append(errmsg)
				.append("' where orgid='").append(ecs_stockconfig.getOrgid()).append("' and skuid='")
				.append(obj).append("' ").toString();
				Log.info("���¿��ɹ����ⲿ���룺"+obj);
				
			}else{
				errmsg= item.getString("msg");
				errcount.append(errmsg);
				sql = new StringBuilder("update ecs_stockconfigsku set errmsg='").append(errmsg)
				.append("' where orgid='").append(ecs_stockconfig.getOrgid()).append("' and skuid='")
				.append(obj).append("' ").toString();
				Log.info("���¿�����,�ⲿ����: "+obj+"������Ϣ: "+errmsg);
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
        
    
        String responseOrderListData = Utils.sendByPost(updatestockparams, app_secret, url);
		
		
		

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
