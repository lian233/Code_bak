package com.wofu.ecommerce.weidian;

import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.ecommerce.weidian.utils.Utils;
import com.wofu.ecommerce.weidian.utils.getToken;


public class StockUtils 
{
	//�ȳ��Կ�����û������������о͸���һ�����ݣ�û�о���������Ʒ֮����ͬ�����
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
				
				Log.info("������Ь��ɹ�,SKU:"+sku+",�¿��:"+qty);
			}
			else
			{
				sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=stockcount+"+qty+" where orgid="+orgid+" and sku='"+sku+"'";
				dao.execute(sql);
				
				sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount+"+qty
					+" where orgid="+orgid+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+sku+"')";
				dao.execute(sql);
				
				Log.info("������Ь��ɹ�,SKU:"+sku+",�������:"+qty);
			}
		}
		
	}
	/**updateStockConfig(DataCentre dao,int orgid,String skuid,String errmsg) **/
	private static void updateStockConfig(DataCentre dao,int orgid,String skuid,String errmsg) throws Exception
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
				String sku=skuid;
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
	

	public static boolean updateStock(String url,
			ECS_StockConfigSku stockconfigsku,int newqty,int updatetype,DataCentre dao) throws Exception
	{
		boolean flag=false;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		Date now=new Date();        
		JSONObject param_Object = new JSONObject();
		JSONObject public_Object = new JSONObject();
		JSONArray skus = new JSONArray();
		JSONObject skuOjb = new JSONObject();
		public_Object.put("method", "vdian.item.update");
		public_Object.put("access_token", getToken.getToken_zy(dao.getConnection())); //д���������ڻ�ȡaccess_token
		public_Object.put("version", "1.0"); 
		public_Object.put("format", "json"); 
		skuOjb.put("id", stockconfigsku.getSkuid());
		skuOjb.put("stock", String.valueOf(newqty));
		skus.put(skuOjb);
		param_Object.put("itemid", stockconfigsku.getItemid());
		param_Object.put("skus", skus);
		String opt_to_sever = Params.url + "?public=" + URLEncoder.encode(public_Object.toString(),"UTF-8") + "&param=" + URLEncoder.encode(param_Object.toString(),"UTF-8") ;
		String responseOrderListData = Utils.sendbyget(opt_to_sever);
		JSONObject responseupdatestock=new JSONObject(responseOrderListData);
		JSONObject response=responseupdatestock;
		if (response.getJSONObject("status").getString("status_reason").equals("success"))
		{

			flag=true;
			Log.info("�����³ɹ�,SKU:["+stockconfigsku.getSku()+"],ԭ����:["+stockconfigsku.getStockcount()+"],��������:["+newqty+"]");
			stockconfigsku.setStockcount(newqty);
			dao.updateByKeys(stockconfigsku, "orgid,itemid,skuid");
		}
		else
		{
			flag=false;
			String errMsg="";
				String errorDes=response.getJSONObject("status").getString("status_reason");
				String errorCode=String.valueOf(response.getJSONObject("status").getInt("status_code"));
				errMsg=errMsg+errorDes + errorCode;//+":"+pkInfo;		
			Log.warn("������ʧ��,SKU:["+stockconfigsku.getSku()+"],ԭ����:["+stockconfigsku.getStockcount()+"],��������:["+newqty+"] ������Ϣ:"+errMsg);
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(errMsg);
			dao.updateByKeys(stockconfigsku, "orgid,itemid,skuid");
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
