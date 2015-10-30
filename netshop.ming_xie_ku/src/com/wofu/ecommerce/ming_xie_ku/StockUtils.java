package com.wofu.ecommerce.ming_xie_ku;

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
import com.wofu.ecommerce.ming_xie_ku.Params;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;


public class StockUtils {
	

//	public static void batchUpdateStock(DataCentre dao,int orgid,String VendorSkuId,String Qty) throws Exception
//	{
//		String sku = null;
//		try
//		{
////			Map<String, String> updatestockparams = new HashMap<String, String>();
////	        //ϵͳ����������
////			updatestockparams.put("appKey", app_key);
////			updatestockparams.put("sessionKey", token);
////			updatestockparams.put("format", format);
////			updatestockparams.put("method", "yhd.products.stock.update");
////			updatestockparams.put("ver", ver);
////			updatestockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
////	       
////	        updatestockparams.put("updateType", String.valueOf(updatetype));
////	        updatestockparams.put("outerStockList", outerstocklist);
//	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
//			Date now=new Date();
//			String method="scn.vendor.inventory.incremental.update";
//			//String ver=Params.ver;
//			/***data����***/
//			JSONObject data=new JSONObject();
//			//��Ҫ���ص��ֶΣ�
//			data.put("VendorSkuId", VendorSkuId);   //��������С��λ��ƷΨһ��,skuid�ô�������
//			data.put("Qty", Qty);			   //�������(��0)������-���ӡ�����-����
//			/**sign����***/
//			System.out.println(df.format(now));
//			String sign=Utils.get_sign(data, method, now);
//			/***�ϲ�Ϊ������****/
//			String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
//	        
//	        String responseOrderListData = Utils.sendByPost(Params.url,output_to_server);
//			Log.info("���¿�淵������ ��"+responseOrderListData);
//			JSONObject responseupdatestock=new JSONObject(responseOrderListData);
//			
//			JSONObject response=responseupdatestock;
//			ArrayList<String> sqlerr;
//			String errMsg="";
//			
//			if (response.getBoolean("IsError"))
//			{
//				//JSONArray errlist=response.getJSONObject("errInfoList").getJSONArray("errDetailInfo");
//				sqlerr = new ArrayList<String>();			
//					if(response.getString("ErrMsg").equals("����������ʧ��"))
//					{  //sku������
//						sku=VendorSkuId;
//						String sql = "delete from ecs_stockconfigsku where orgid='"+orgid+"' and sku='"+sku+"'";
//						sqlerr.add(sql);
//					}
//					errMsg=errMsg+response.getString("ErrMsg");//+":"+pkInfo;
//				if(!sqlerr.isEmpty()) SQLHelper.executeBatch(dao.getConnection(), sqlerr);
//				
//				
//				updateStockConfig(dao,orgid,sku,errMsg);
//				
//				Log.warn("������ʧ��,������Ϣ:"+errMsg);
//			}
//			else
//			{
//	
//				int updateCount=response.getInt("updateCount");
//				
//				Log.info("������Ь��ɹ�,������SKU��:["+updateCount+"]");
//				
//				updateStockConfig(dao,orgid,sku,errMsg);
//			}
//		} catch (Exception e) {
//			Log.info("������Ь����ʧ��,������Ϣ:"+e.getMessage());
//			updateStockConfig(dao,orgid,sku,e.getMessage());
//		}		
//	}
	
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
	

	public static boolean updateStock(String url,String appkey,String ver,String format,
			ECS_StockConfigSku ecs_stockconfigsku,int newqty,String app_secret) throws Exception
	{
		boolean flag=false;
		Date now=new Date();
		String method="scn.vendor.inventory.cover.update";
		/***data����***/
		JSONObject data=new JSONObject();
		//��Ҫ���ص��ֶΣ�
		data.put("VendorSkuId",ecs_stockconfigsku.getSku());   //��������С��λ��ƷΨһ��
		data.put("Qty",newqty);   //�������(�Ǹ���)�������������Ϊ��ǰ����ֵ
		/**sign����***/
		String sign=Utils.get_sign(app_secret,appkey,data, method, now,ver,format);
		/***�ϲ�Ϊ������****/
		String output_to_server=Utils.post_data_process(method, data, appkey,now, sign).toString();
    
        String responseOrderListData = Utils.sendByPost(url,output_to_server);

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
//					Log.info("�ӿڷ���̫Ƶ��,��ͣ����,���Ժ�......");
//					Thread.sleep(60000L);
//					//PostClient.sendByPost(url, updatestockparams, secretkey);
//					updateStock(url,merchantid,checkcode,
//							secretkey,erp,erpver,format,ver,
//							warehouseId,sku,qty,newqty,updatetype,app_secret);
//				}
				//String pkInfo=errinfo.getString("pkInfo");
				
				errMsg=errMsg+errorDes;//+":"+pkInfo;		
			Log.warn("������ʧ��,SKU:["+ecs_stockconfigsku.getSku()+"],ԭ����:["+ecs_stockconfigsku.getStockcount()+"],��������:["+newqty+"] ������Ϣ:"+errMsg);
		}
		else
		{
			flag=true;
			Log.info("�����³ɹ�,SKU:["+ecs_stockconfigsku.getSku()+"],ԭ����:["+ecs_stockconfigsku.getStockcount()+"],��������:["+newqty+"]");
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
