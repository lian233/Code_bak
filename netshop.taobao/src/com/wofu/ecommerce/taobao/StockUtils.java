package com.wofu.ecommerce.taobao;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.FenxiaoProductUpdateRequest;
import com.taobao.api.request.ItemQuantityUpdateRequest;
import com.taobao.api.response.FenxiaoProductUpdateResponse;
import com.taobao.api.response.ItemQuantityUpdateResponse;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
public class StockUtils {
		
	/*
	 * 过程说明：淘宝更新库存    api免费
	 * 参数说明：
	 * 		   
	 ×
	 */
	public static void updateSkuStock(DataCentre dao,String url,String appkey,
			String appsecret,String authcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,
			int qty,int type) 
		
	{
		TaobaoClient client=null;
		ItemQuantityUpdateRequest updatereq=null;
		try {			
			client=new DefaultTaobaoClient(url,appkey, appsecret);
			updatereq=new ItemQuantityUpdateRequest();
			updatereq.setNumIid(Long.valueOf(stockconfigsku.getItemid()));
			updatereq.setOuterId(stockconfigsku.getSku());	
			updatereq.setSkuId(Long.valueOf(stockconfigsku.getSkuid()));
			updatereq.setQuantity(Long.valueOf(qty));
			updatereq.setType(Long.valueOf(type));
			ItemQuantityUpdateResponse response = client.execute(updatereq,authcode);
			
			while (response!=null && !response.isSuccess())
			{	
				String errorMsg = response.getSubMsg();
				Log.info( "更新淘宝库存失败,SKU【"+stockconfigsku.getSku()+"】,错误信息: "+response.getSubMsg());	
				if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
					Log.info("更新频率过快，线程休眠5秒");
					Thread.sleep(5000L);
					response = client.execute(updatereq,authcode);
					continue;
					}else if(errorMsg.indexOf("商品id对应的商品不存在")!=-1){
						try{
							dao.deleteByKeys(stockconfigsku, new StringBuilder().append("orgid,").append("itemid,").append("sku").toString());
							Log.info("删除数据库中不存在的商品成功,商品sku："+stockconfigsku.getSku());
							break;
						}catch(Exception e){
							e.printStackTrace();
							Log.info("删除数据库中不存在的商品失败,商品sku："+stockconfigsku.getSku());
						}
				
					}else if(errorMsg.indexOf("商品id对应的商品已经被删除")!=-1){
						try{
							dao.deleteByKeys(stockconfigsku, new StringBuilder().append("orgid,").append("itemid,").append("sku").toString());
							Log.info("删除数据库中不存在的商品成功,商品sku："+stockconfigsku.getSku());
							break;
						}catch(Exception e){
							e.printStackTrace();
							Log.info("删除数据库中不存在的商品失败,商品sku："+stockconfigsku.getSku());
						}
					}else if(errorMsg.indexOf("没有找到宝贝对应的SKU")!=-1){
						try{
							dao.deleteByKeys(stockconfigsku, new StringBuilder().append("orgid,").append("itemid,").append("skuid").toString());
							Log.info("删除数据库中不存在的商品成功,商品sku："+stockconfigsku.getSku());
							break;
						}catch(Exception e){
							e.printStackTrace();
							Log.info("删除数据库中不存在的商品失败,商品sku："+stockconfigsku.getSku());
						}
					}
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg(response.getSubMsg().replaceAll("\"",""));
					dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(response.getSubMsg().replaceAll("\"",""));
					dao.updateByKeys(stockconfig,"orgid,itemid");
					return;
			}
				if(response.isSuccess()){
					if (type==1)
					{
						Log.info("更新淘宝库存成功,SKU【"+stockconfigsku.getSku()+"】,原库存:"+stockconfigsku.getStockcount()+" 新库存:"+qty);
						stockconfig.setStockcount(stockconfig.getStockcount()-stockconfigsku.getStockcount()+qty);
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						dao.updateByKeys(stockconfig,"orgid,itemid");
						
						stockconfigsku.setStockcount(qty);
						stockconfigsku.setErrflag(0);
						stockconfigsku.setErrmsg("");
						dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
					}
					else
					{
						Log.info("更新淘宝库存成功,SKU【"+stockconfigsku.getSku()+"】,原库存:"+stockconfigsku.getStockcount()+" 调整库存:"+qty);
						stockconfigsku.setStockcount(stockconfigsku.getStockcount()+qty);
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
						stockconfig.setStockcount(stockconfig.getStockcount()+qty);
						stockconfigsku.setErrflag(0);
						stockconfigsku.setErrmsg("");
						dao.updateByKeys(stockconfig,"orgid,itemid");
					}
				}
				
			
		} catch (Exception e) {
			Log.info(e.getMessage());
			//网络连接失败，五秒重试
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("网络连接超时，五秒重试!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("网络连接失败!");
					Log.info("更新淘宝库存失败,SKU【"+stockconfigsku.getSku()+"】,错误信息:"+e.getMessage());
				}
			}else{	
				Log.info("更新淘宝库存失败,SKU【"+stockconfigsku.getSku()+"】,错误信息:"+e.getMessage());
				stockconfigsku.setErrflag(1);
				stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"",""));
				try{
					dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	

					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(e.getMessage().replaceAll("\"",""));
					dao.updateByKeys(stockconfig,"orgid,itemid");
				}catch(Exception ex){
					Log.error("更新sku库存出错,错误信息写入表错误", ex.getMessage());
				}
				
			}

		}
	}
	/**
	 * 更新货号库存，没有sku的  免费api
	 * @param dao
	 * @param url
	 * @param appkey
	 * @param appsecret
	 * @param authcode
	 * @param stockconfig
	 * @param qty
	 * @param type
	 * @throws Exception
	 */
	public static void updateItemStock(DataCentre dao,String url,String appkey,
			String appsecret,String authcode,ECS_StockConfig stockconfig,
			int qty,int type) 
		throws Exception
	{		TaobaoClient client=null;
			ItemQuantityUpdateRequest updatereq=null;
		try {			
			
			client=new DefaultTaobaoClient(url,appkey, appsecret);
			updatereq=new ItemQuantityUpdateRequest();
			updatereq.setNumIid(Long.valueOf(stockconfig.getItemid()));
			updatereq.setQuantity(Long.valueOf(qty));
			updatereq.setType(Long.valueOf(type));
			ItemQuantityUpdateResponse response = client.execute(updatereq,authcode);
			while (!response.isSuccess())
			{
				String errorMsg = response.getSubMsg();
				Log.info( "更新淘宝库存失败,货号【"+stockconfig.getItemcode()+"】,错误信息:"+response.getSubMsg());	
				if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
					Log.info("更新频率过快，线程休眠5秒");
					Thread.sleep(5000L);
					response = client.execute(updatereq,authcode);
					continue;
				}else if(errorMsg.indexOf("商品id对应的商品不存在")!=-1){
					try{
						dao.deleteByKeys(stockconfig, new StringBuilder().append("orgid,").append("itemid").toString());
						Log.info("删除数据库中不存在的商品成功,商品ID："+stockconfig.getItemcode());
						break;
					}catch(Exception e){
						Log.info("删除数据库中不存在的商品失败,商品ID："+stockconfig.getItemcode());
					}
					
				}else if(errorMsg.indexOf("商品id对应的商品已经被删除")!=-1){
					try{
						dao.deleteByKeys(stockconfig, new StringBuilder().append("orgid,").append("itemid").toString());
						Log.info("删除数据库中不存在的商品成功,商品ID："+stockconfig.getItemcode());
						break;
					}catch(Exception e){
						Log.info("删除数据库中不存在的商品失败,商品ID："+stockconfig.getItemcode());
					}
					
				}
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(response.getSubMsg().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");
				return;
			}
				if(response.isSuccess()){
					if (type==1)
					{
						Log.info("更新淘宝库存成功,货号【"+stockconfig.getItemcode()+"】,原库存:"+stockconfig.getStockcount()+" 新库存:"+qty);
						stockconfig.setStockcount(qty);
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						dao.updateByKeys(stockconfig,"orgid,itemid");
					}
					else
					{
						Log.info("更新淘宝库存成功,货号【"+stockconfig.getItemcode()+"】,原库存:"+stockconfig.getStockcount()+" 调整库存:"+qty);
						stockconfig.setStockcount(stockconfig.getStockcount()+qty);
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						dao.updateByKeys(stockconfig,"orgid,itemid");
					}
				}
			
		} catch (Exception e) {
			//网络连接失败，五秒重试
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("网络连接超时，五秒重试!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("网络连接失败!");
					Log.info("更新淘宝库存失败,货号【"+stockconfig.getItemcode()+"】,错误信息:"+e.getMessage());
				}
			}else{
				Log.info("更新淘宝库存失败,货号【"+stockconfig.getItemcode()+"】,错误信息:"+e.getMessage());
				
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(e.getMessage().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");
			}	
				
		}
	}
	/**
	 * 更新分销产品库存 免费
	 * @param dao
	 * @param url
	 * @param appkey
	 * @param appsecret
	 * @param authcode
	 * @param stockconfig
	 * @param qty
	 * @throws Exception
	 */
	public static void updateDistributionItemStock(DataCentre dao,String url,
			String appkey,String appsecret,String authcode,ECS_StockConfig stockconfig,int qty) 
	throws Exception
	{		TaobaoClient client=null;
			FenxiaoProductUpdateRequest updatereq=null;
		try {			
			client=new DefaultTaobaoClient(url,appkey, appsecret);
			
			updatereq=new FenxiaoProductUpdateRequest();
			updatereq.setPid(Long.valueOf(stockconfig.getItemid()));
			updatereq.setOuterId(stockconfig.getItemcode());
			updatereq.setQuantity(Long.valueOf(qty));
			FenxiaoProductUpdateResponse response = client.execute(updatereq,authcode);
			while (response.getPid()==null)
			{	String errorMsg = response.getSubMsg();
				Log.info("更新分销库存失败,货号:"+stockconfig.getItemcode()+" 错误信息:"+errorMsg);	
				if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
					Log.info("更新频率过快，线程休眠5秒");
					Thread.sleep(5000L);
					response = client.execute(updatereq,authcode);
					continue;
				}else if(errorMsg.indexOf("产品ID不合法，不能为空，对应的产品必须属于登陆用户并且未被删除")!=-1){
					dao.deleteByKeys(stockconfig, new StringBuilder().append("orgid,").append("itemid").toString());
					Log.info("删除数据库中不存在的商品成功,货号："+stockconfig.getItemcode());
					break;
				}
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(response.getSubMsg().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");	
				break;
			}
				if(response.getPid()!=null){
					Log.info("更新分销库存成功,货号:"+stockconfig.getItemcode()+" 原库存:"+stockconfig.getStockcount()+" 新库存:"+qty);
					stockconfig.setStockcount(qty);
					stockconfig.setErrflag(0);
					stockconfig.setErrmsg("");
					dao.updateByKeys(stockconfig,"orgid,itemid");
				}
				
		
		} catch (Exception e) {
			//网络连接失败，五秒重试
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("网络连接超时，五秒重试!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("网络连接失败!");
					Log.info("取分销库存失败,货号:"+stockconfig.getItemcode()+" 错误信息:" + e.getMessage());
				}
			}else{
				Log.info("取分销库存失败,货号:"+stockconfig.getItemcode()+" 错误信息:" + e.getMessage());
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(e.getMessage().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");	
			}
			
		}
	}
	//更新分销sku库存  api免费
	public static void updateDistributionSkuStock(DataCentre dao,String url,
			String appkey,String appsecret,String authcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty) 
	throws Exception
	{
		TaobaoClient client=null;
		FenxiaoProductUpdateRequest updatereq=null;
		try {			
			client=new DefaultTaobaoClient(url,appkey, appsecret);
			updatereq=new FenxiaoProductUpdateRequest();
			//商品id
			updatereq.setPid(Long.valueOf(stockconfigsku.getItemid()));
			//淘宝sku编号
			updatereq.setSkuIds(stockconfigsku.getSkuid());
			//库存
			updatereq.setSkuQuantitys(String.valueOf(qty));
			FenxiaoProductUpdateResponse response = client.execute(updatereq,authcode);
			if(response.isSuccess()){
				Log.info("更新分销库存成功,SKU:"+stockconfigsku.getSku()+" 新库存:"+qty);
				stockconfig.setStockcount(qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				dao.updateByKeys(stockconfig,"orgid,itemid");
				stockconfigsku.setStockcount(qty);
				stockconfigsku.setErrflag(0);
				stockconfigsku.setErrmsg("");
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
				return;
			}
			else{
				String errorMsg = response.getSubMsg();
				if(errorMsg!=null){
					Log.info("更新分销库存失败,货号:"+stockconfig.getItemcode()+" 错误信息:"+errorMsg);	
					if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
						Log.info("更新频率过快，线程休眠5秒");
						Thread.sleep(5000L);
						response = client.execute(updatereq,authcode);
						if(response.isSuccess()){
							Log.info("更新分销库存成功,SKU:"+stockconfigsku.getSku()+" 新库存:"+qty);
							stockconfig.setStockcount(qty);
							stockconfig.setErrflag(0);
							stockconfig.setErrmsg("");
							dao.updateByKeys(stockconfig,"orgid,itemid");
							stockconfigsku.setStockcount(qty);
							stockconfigsku.setErrflag(0);
							stockconfigsku.setErrmsg("");
							dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
							return;
						}
					}else if(errorMsg.indexOf("产品ID不合法，不能为空，对应的产品必须属于登陆用户并且未被删除")!=-1){
						dao.deleteByKeys(stockconfigsku, new StringBuilder().append("orgid,").append("itemid,").append("sku").toString());
						Log.info("删除数据库中不存在的商品成功,商品sku："+stockconfigsku.getSku());
					}
				}
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(response.getSubMsg().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");	
				
				stockconfigsku.setErrflag(1);
				stockconfigsku.setErrmsg(response.getSubMsg().replaceAll("\"",""));
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	
			}
		} catch (Exception e) {
			e.printStackTrace();
			//网络连接失败，五秒重试
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("网络连接超时，五秒重试!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("网络连接失败!");
					Log.info("取分销库存失败,SKU:"+stockconfigsku.getSku()+" 错误信息:" + e.getMessage());
				}
			}else{
				Log.info("取分销库存失败,SKU:"+stockconfigsku.getSku()+" 错误信息:" + e.getMessage());
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(e.getMessage().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");
				stockconfigsku.setErrflag(1);
				stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"",""));
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	
			}
			throw e;
			
		}
	}
	/**
	 * 全部sku的库存总数为0时，把该产品下架  api免费
	 */
	public static void updateDistributionProStatus(String url,
			String appkey,String appsecret,String authcode,ECS_StockConfig stockconfig) 
	throws Exception{
		Long itemid=Long.valueOf(stockconfig.getItemid());
		try{
			TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret);
			FenxiaoProductUpdateRequest req=new FenxiaoProductUpdateRequest();
			req.setPid(itemid);
			req.setStatus("down");
			FenxiaoProductUpdateResponse response = client.execute(req,authcode);
			if(response.getPid()==null){
				Log.info("0库存产品下架失败,商品id:"+itemid+"错误码: "+response.getSubMsg());
			}else{
				Log.info("0库存产品下架成功,商品id:"+itemid);
			}
		}catch(Exception ex){
			Log.info("0库存产品下架失败,商品id:"+itemid);
		}
		
	}

}
