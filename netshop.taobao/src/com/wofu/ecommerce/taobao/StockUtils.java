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
	 * ����˵�����Ա����¿��    api���
	 * ����˵����
	 * 		   
	 ��
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
				Log.info( "�����Ա����ʧ��,SKU��"+stockconfigsku.getSku()+"��,������Ϣ: "+response.getSubMsg());	
				if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
					Log.info("����Ƶ�ʹ��죬�߳�����5��");
					Thread.sleep(5000L);
					response = client.execute(updatereq,authcode);
					continue;
					}else if(errorMsg.indexOf("��Ʒid��Ӧ����Ʒ������")!=-1){
						try{
							dao.deleteByKeys(stockconfigsku, new StringBuilder().append("orgid,").append("itemid,").append("sku").toString());
							Log.info("ɾ�����ݿ��в����ڵ���Ʒ�ɹ�,��Ʒsku��"+stockconfigsku.getSku());
							break;
						}catch(Exception e){
							e.printStackTrace();
							Log.info("ɾ�����ݿ��в����ڵ���Ʒʧ��,��Ʒsku��"+stockconfigsku.getSku());
						}
				
					}else if(errorMsg.indexOf("��Ʒid��Ӧ����Ʒ�Ѿ���ɾ��")!=-1){
						try{
							dao.deleteByKeys(stockconfigsku, new StringBuilder().append("orgid,").append("itemid,").append("sku").toString());
							Log.info("ɾ�����ݿ��в����ڵ���Ʒ�ɹ�,��Ʒsku��"+stockconfigsku.getSku());
							break;
						}catch(Exception e){
							e.printStackTrace();
							Log.info("ɾ�����ݿ��в����ڵ���Ʒʧ��,��Ʒsku��"+stockconfigsku.getSku());
						}
					}else if(errorMsg.indexOf("û���ҵ�������Ӧ��SKU")!=-1){
						try{
							dao.deleteByKeys(stockconfigsku, new StringBuilder().append("orgid,").append("itemid,").append("skuid").toString());
							Log.info("ɾ�����ݿ��в����ڵ���Ʒ�ɹ�,��Ʒsku��"+stockconfigsku.getSku());
							break;
						}catch(Exception e){
							e.printStackTrace();
							Log.info("ɾ�����ݿ��в����ڵ���Ʒʧ��,��Ʒsku��"+stockconfigsku.getSku());
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
						Log.info("�����Ա����ɹ�,SKU��"+stockconfigsku.getSku()+"��,ԭ���:"+stockconfigsku.getStockcount()+" �¿��:"+qty);
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
						Log.info("�����Ա����ɹ�,SKU��"+stockconfigsku.getSku()+"��,ԭ���:"+stockconfigsku.getStockcount()+" �������:"+qty);
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
			//��������ʧ�ܣ���������
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("�������ӳ�ʱ����������!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("��������ʧ��!");
					Log.info("�����Ա����ʧ��,SKU��"+stockconfigsku.getSku()+"��,������Ϣ:"+e.getMessage());
				}
			}else{	
				Log.info("�����Ա����ʧ��,SKU��"+stockconfigsku.getSku()+"��,������Ϣ:"+e.getMessage());
				stockconfigsku.setErrflag(1);
				stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"",""));
				try{
					dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	

					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(e.getMessage().replaceAll("\"",""));
					dao.updateByKeys(stockconfig,"orgid,itemid");
				}catch(Exception ex){
					Log.error("����sku������,������Ϣд������", ex.getMessage());
				}
				
			}

		}
	}
	/**
	 * ���»��ſ�棬û��sku��  ���api
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
				Log.info( "�����Ա����ʧ��,���š�"+stockconfig.getItemcode()+"��,������Ϣ:"+response.getSubMsg());	
				if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
					Log.info("����Ƶ�ʹ��죬�߳�����5��");
					Thread.sleep(5000L);
					response = client.execute(updatereq,authcode);
					continue;
				}else if(errorMsg.indexOf("��Ʒid��Ӧ����Ʒ������")!=-1){
					try{
						dao.deleteByKeys(stockconfig, new StringBuilder().append("orgid,").append("itemid").toString());
						Log.info("ɾ�����ݿ��в����ڵ���Ʒ�ɹ�,��ƷID��"+stockconfig.getItemcode());
						break;
					}catch(Exception e){
						Log.info("ɾ�����ݿ��в����ڵ���Ʒʧ��,��ƷID��"+stockconfig.getItemcode());
					}
					
				}else if(errorMsg.indexOf("��Ʒid��Ӧ����Ʒ�Ѿ���ɾ��")!=-1){
					try{
						dao.deleteByKeys(stockconfig, new StringBuilder().append("orgid,").append("itemid").toString());
						Log.info("ɾ�����ݿ��в����ڵ���Ʒ�ɹ�,��ƷID��"+stockconfig.getItemcode());
						break;
					}catch(Exception e){
						Log.info("ɾ�����ݿ��в����ڵ���Ʒʧ��,��ƷID��"+stockconfig.getItemcode());
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
						Log.info("�����Ա����ɹ�,���š�"+stockconfig.getItemcode()+"��,ԭ���:"+stockconfig.getStockcount()+" �¿��:"+qty);
						stockconfig.setStockcount(qty);
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						dao.updateByKeys(stockconfig,"orgid,itemid");
					}
					else
					{
						Log.info("�����Ա����ɹ�,���š�"+stockconfig.getItemcode()+"��,ԭ���:"+stockconfig.getStockcount()+" �������:"+qty);
						stockconfig.setStockcount(stockconfig.getStockcount()+qty);
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						dao.updateByKeys(stockconfig,"orgid,itemid");
					}
				}
			
		} catch (Exception e) {
			//��������ʧ�ܣ���������
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("�������ӳ�ʱ����������!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("��������ʧ��!");
					Log.info("�����Ա����ʧ��,���š�"+stockconfig.getItemcode()+"��,������Ϣ:"+e.getMessage());
				}
			}else{
				Log.info("�����Ա����ʧ��,���š�"+stockconfig.getItemcode()+"��,������Ϣ:"+e.getMessage());
				
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(e.getMessage().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");
			}	
				
		}
	}
	/**
	 * ���·�����Ʒ��� ���
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
				Log.info("���·������ʧ��,����:"+stockconfig.getItemcode()+" ������Ϣ:"+errorMsg);	
				if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
					Log.info("����Ƶ�ʹ��죬�߳�����5��");
					Thread.sleep(5000L);
					response = client.execute(updatereq,authcode);
					continue;
				}else if(errorMsg.indexOf("��ƷID���Ϸ�������Ϊ�գ���Ӧ�Ĳ�Ʒ�������ڵ�½�û�����δ��ɾ��")!=-1){
					dao.deleteByKeys(stockconfig, new StringBuilder().append("orgid,").append("itemid").toString());
					Log.info("ɾ�����ݿ��в����ڵ���Ʒ�ɹ�,���ţ�"+stockconfig.getItemcode());
					break;
				}
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(response.getSubMsg().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");	
				break;
			}
				if(response.getPid()!=null){
					Log.info("���·������ɹ�,����:"+stockconfig.getItemcode()+" ԭ���:"+stockconfig.getStockcount()+" �¿��:"+qty);
					stockconfig.setStockcount(qty);
					stockconfig.setErrflag(0);
					stockconfig.setErrmsg("");
					dao.updateByKeys(stockconfig,"orgid,itemid");
				}
				
		
		} catch (Exception e) {
			//��������ʧ�ܣ���������
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("�������ӳ�ʱ����������!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("��������ʧ��!");
					Log.info("ȡ�������ʧ��,����:"+stockconfig.getItemcode()+" ������Ϣ:" + e.getMessage());
				}
			}else{
				Log.info("ȡ�������ʧ��,����:"+stockconfig.getItemcode()+" ������Ϣ:" + e.getMessage());
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(e.getMessage().replaceAll("\"",""));
				dao.updateByKeys(stockconfig,"orgid,itemid");	
			}
			
		}
	}
	//���·���sku���  api���
	public static void updateDistributionSkuStock(DataCentre dao,String url,
			String appkey,String appsecret,String authcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty) 
	throws Exception
	{
		TaobaoClient client=null;
		FenxiaoProductUpdateRequest updatereq=null;
		try {			
			client=new DefaultTaobaoClient(url,appkey, appsecret);
			updatereq=new FenxiaoProductUpdateRequest();
			//��Ʒid
			updatereq.setPid(Long.valueOf(stockconfigsku.getItemid()));
			//�Ա�sku���
			updatereq.setSkuIds(stockconfigsku.getSkuid());
			//���
			updatereq.setSkuQuantitys(String.valueOf(qty));
			FenxiaoProductUpdateResponse response = client.execute(updatereq,authcode);
			if(response.isSuccess()){
				Log.info("���·������ɹ�,SKU:"+stockconfigsku.getSku()+" �¿��:"+qty);
				stockconfig.setStockcount(qty);
				stockconfig.setErrflag(1);
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
					Log.info("���·������ʧ��,����:"+stockconfig.getItemcode()+" ������Ϣ:"+errorMsg);	
					if(errorMsg.indexOf("This ban will last for 1 more seconds")!=-1){
						Log.info("����Ƶ�ʹ��죬�߳�����5��");
						Thread.sleep(5000L);
						response = client.execute(updatereq,authcode);
						if(response.isSuccess()){
							Log.info("���·������ɹ�,SKU:"+stockconfigsku.getSku()+" �¿��:"+qty);
							stockconfig.setStockcount(qty);
							stockconfig.setErrflag(0);
							stockconfig.setErrmsg("1");
							dao.updateByKeys(stockconfig,"orgid,itemid");
							stockconfigsku.setStockcount(qty);
							stockconfigsku.setErrflag(0);
							stockconfigsku.setErrmsg("");
							dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
							return;
						}
					}else if(errorMsg.indexOf("��ƷID���Ϸ�������Ϊ�գ���Ӧ�Ĳ�Ʒ�������ڵ�½�û�����δ��ɾ��")!=-1){
						dao.deleteByKeys(stockconfigsku, new StringBuilder().append("orgid,").append("itemid,").append("sku").toString());
						Log.info("ɾ�����ݿ��в����ڵ���Ʒ�ɹ�,��Ʒsku��"+stockconfigsku.getSku());
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
			//��������ʧ�ܣ���������
			if(e.getMessage().indexOf("java.net.SocketTimeoutException")!=-1){
				try{
					Log.info("�������ӳ�ʱ����������!");
					Thread.sleep(5000L);
					client.execute(updatereq,authcode);
				}catch(Exception ex){
					Log.info("��������ʧ��!");
					Log.info("ȡ�������ʧ��,SKU:"+stockconfigsku.getSku()+" ������Ϣ:" + e.getMessage());
				}
			}else{
				Log.info("ȡ�������ʧ��,SKU:"+stockconfigsku.getSku()+" ������Ϣ:" + e.getMessage());
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
	 * ȫ��sku�Ŀ������Ϊ0ʱ���Ѹò�Ʒ�¼�  api���
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
				Log.info("0����Ʒ�¼�ʧ��,��Ʒid:"+itemid+"������: "+response.getSubMsg());
			}else{
				Log.info("0����Ʒ�¼ܳɹ�,��Ʒid:"+itemid);
			}
		}catch(Exception ex){
			Log.info("0����Ʒ�¼�ʧ��,��Ʒid:"+itemid);
		}
		
	}

}
