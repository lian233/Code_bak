package com.wofu.ecommerce.ordermanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;


import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.request.order.OrderGetRequest;
import com.jd.open.api.sdk.response.order.OrderGetResponse;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.PurchaseOrder;
import com.taobao.api.domain.SubPurchaseOrder;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.FenxiaoOrdersGetRequest;
import com.taobao.api.response.FenxiaoOrdersGetResponse;
import com.wofu.base.util.BusinessObject;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.amazon.AmazonUtil;
import com.wofu.ecommerce.dangdang.OrderItem;
import com.wofu.ecommerce.lefeng.LefengUtil;
import com.wofu.ecommerce.taobao.OrderUtils;
import com.wofu.ecommerce.taobao.Params;


public class ECS_OrderTrack extends BusinessObject {
	
	public void getOutShop() throws Exception
	{
		String sql="select id as outshopid,name as outshopname from shop where shoptype=21";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getOrderInfo(String orgid,String ordercode) throws Exception
	{

		String sql="select tradecontactid from ecs_tradecontactorgcontrast where orgid="+orgid;
		
		String tradecontactid=this.getDao().strSelect(sql);

		sql="select a.*,b.shortname,c.orgname from ecs_org_params a with(nolock),ecs_platform b with(nolock),ecs_org c with(nolock) "
			+"where a.platformid=b.platformid and a.orgid="+orgid+" and a.orgid=c.orgid";
		Hashtable htparams=this.getDao().oneRowSelect(sql);
		
		String platformname=htparams.get("shortname").toString();
		String orgname=htparams.get("orgname").toString();

		Log.info("获取订单,店铺:"+orgname+",订单号:"+ordercode);
		
		if (platformname.equals("taobao"))
		{
			getTaobaoOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}
		else if (platformname.equals("360buy"))
		{
			getJingdongOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}
		else if (platformname.equals("dangdang"))
		{
			getDangdangOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}
		else if (platformname.equals("paipai"))
		{
			getPaipaiOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}
		else if (platformname.equals("amazon"))
		{
			getAmazonOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}
		else if (platformname.equals("taobaofenxiao"))
		{
			getTaobaoFenXiaoOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}
		else if (platformname.equals("val"))
		{
			getValOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}else if (platformname.equals("yhd"))
		{
			getYhdOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}else if (platformname.equals("lefeng"))
		{
			getLefengOrderInfo(orgname,tradecontactid,htparams,ordercode);
		}else
		{
			throw new JException("暂不支持改平台");
		}
		
	}
	

	private void getTaobaoOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		String url=params.get("url").toString();
		String appkey=params.get("appkey").toString();
		String appsecret=params.get("appsecret").toString();
		String authcode=params.get("token").toString();
		
		Trade td=OrderUtils.getFullTrade(ordercode,url,appkey,appsecret,authcode);
		
		Log.info(td.getTid()+" "+td.getStatus()+" "+Formatter.format(td.getModified(),Formatter.DATE_TIME_FORMAT));
		
		if (td.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
		{
			String sheetid=OrderUtils.createInterOrder(this.getDao().getConnection(),td,tradecontactid,orgname,true);
			
			for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
			{
				Order o=(Order) ito.next();
				String sku=o.getOuterSkuId();
			
				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, String.valueOf(td.getTid()),sku);
				StockManager.addSynReduceStore("订单跟踪", this.getDao().getConnection(), tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, -o.getNum().longValue(),false);
			}
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}else if (td.getStatus().equals("WAIT_BUYER_PAY")) 
		{
			throw new JException("订单还未付款!");
			
		}else if (td.getStatus().equals("TRADE_NO_CREATE_PAY"))
		{
			throw new JException("订单付款失败,请通知客人重新付款!");
		}else
		{
			throw new JException("订单已关闭或已退款!");
		}
	}
	
	
	private void getTaobaoFenXiaoOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		String url=params.get("url").toString();
		String appkey=params.get("appkey").toString();
		String appsecret=params.get("appsecret").toString();
		String authcode=params.get("token").toString();
		
		TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
		FenxiaoOrdersGetRequest req=new FenxiaoOrdersGetRequest();	
		req.setPurchaseOrderId(Long.valueOf(ordercode));
		FenxiaoOrdersGetResponse response =(FenxiaoOrdersGetResponse) client.execute(req , authcode);
		
		PurchaseOrder po=(PurchaseOrder) response.getPurchaseOrders().get(0);
		
		Log.info(po.getId()+" "+po.getStatus()+" "+Formatter.format(po.getModified(),Formatter.DATE_TIME_FORMAT));
		
		if (po.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
		{
			String sheetid=OrderUtils.createDistributionOrder("订单跟踪",this.getDao().getConnection(),po,tradecontactid);
			
			for(Iterator ito=po.getSubPurchaseOrders().iterator();ito.hasNext();)
			{
				SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
				String sku=o.getSkuOuterId();
			
				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, String.valueOf(po.getTcOrderId()),sku);		
				StockManager.addSynReduceStore("订单跟踪", this.getDao().getConnection(), tradecontactid, po.getStatus(),String.valueOf(po.getTcOrderId()), sku, -o.getNum().longValue(),false);
			}
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}else if (po.getStatus().equals("WAIT_BUYER_PAY")) 
		{
			throw new JException("订单还未付款!");
			
		}else if (po.getStatus().equals("TRADE_NO_CREATE_PAY"))
		{
			throw new JException("订单付款失败,请通知客人重新付款!");
		}else
		{
			throw new JException("订单已关闭或已退款!");
		}
	}
	private void getJingdongOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		String url=params.get("url").toString();
		String appkey=params.get("appkey").toString();
		String appsecret=params.get("appsecret").toString();
		String token=params.get("token").toString();
		boolean isLBP=params.get("isLBP").toString().equals("1")?true:false;
		
		OrderInfo order=com.wofu.ecommerce.jingdong.OrderUtils.getFullTrade(ordercode, url, token, appkey, appsecret);
		
		Log.info("订单号【"+order.getOrderId()+"】,最后修改时间【"+order.getModified()+"】,状态【"+order.getOrderState()+"】") ;
		
		if(order.getOrderState().equalsIgnoreCase("WAIT_SELLER_STOCK_OUT"))
		{
			//创建订单 如果创建成功，减少库存
			String sheetid=com.wofu.ecommerce.jingdong.OrderUtils.createInterOrder(this.getDao().getConnection(),url,appkey,appsecret,token,
					order,tradecontactid, orgname,"",isLBP,true);
			
			//减其它店库存
			List<ItemInfo> itemList = order.getItemInfoList() ;
			for(int j = 0 ; j < itemList.size() ; j ++)
			{
				String sku = itemList.get(j).getOuterSkuId() ;
				long qty=Integer.valueOf(itemList.get(j).getItemTotal());
				StockManager.addSynReduceStore("订单跟踪", this.getDao().getConnection(), tradecontactid, order.getOrderState(),order.getOrderId(), sku, -qty,false);
			}
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}else if(order.getOrderState().equalsIgnoreCase("LOCKED")|| order.getOrderState().equalsIgnoreCase("TRADE_CANCELED"))
		{
			throw new JException("订单已锁定或取消!");
		}else
		{
			throw new JException("订单还未付款!");
		}
	}
	
	private void getDangdangOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		String url=params.get("url").toString();
		String appKey=params.get("appkey").toString();
		String appsecret=params.get("appsecret").toString();
		String token=params.get("token").toString();
		
		com.wofu.ecommerce.dangdang.Order o = com.wofu.ecommerce.dangdang.OrderUtils.getOrderByID(url,ordercode,appKey, appsecret,token) ;
		
		Log.info("订单号【"+ o.getOrderID() +"】,状态【"+ com.wofu.ecommerce.dangdang.OrderUtils.getOrderStateByCode(o.getOrderState()) +"】,最后修改时间【"+ Formatter.format(o.getLastModifyTime(),Formatter.DATE_TIME_FORMAT) +"】") ;
		
		if(o.getOrderState().equalsIgnoreCase("101"))
		{
			//创建订单 如果创建成功，减少库存
			String sheetid=com.wofu.ecommerce.dangdang.OrderUtils.createInterOrder(this.getDao().getConnection(), o, tradecontactid,orgname);
			
			
			ArrayList<OrderItem> itemList = o.getOrderItemList() ;
			for(int j= 0 ; j < itemList.size() ; j ++)
			{
				String sku = itemList.get(j).getOuterItemID() ;
				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, o.getOrderID(),sku);
			}
			
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}else if (o.getOrderState().equals("100")) 
		{
			throw new JException("订单还未付款!");
			
		}else
		{
			throw new JException("订单已关闭或已退款!");
		}
	}

	private void getPaipaiOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		
		String url=params.get("url").toString();
		String spid=params.get("appkey").toString();
		String secretkey=params.get("appsecret").toString();
		String token=params.get("token").toString();
		String uid=params.get("uid").toString();
		String encoding=params.get("encoding").toString();
		String username = params.get("shopname").toString();
		boolean is_cod=false;
		com.wofu.ecommerce.oauthpaipai.Order o=com.wofu.ecommerce.oauthpaipai.OrderUtils.getDealDetail(spid,secretkey,token,uid,encoding,ordercode);
		
		Log.info("订单号:"+ordercode+" 订单状态:"+o.getDealState()+" 时间:"+Formatter.format(o.getPayTime(), Formatter.DATE_TIME_FORMAT));
		
		if(o.getDealState().equalsIgnoreCase("DS_WAIT_SELLER_DELIVERY"))
		{
			//创建订单 如果创建成功，减少库存
			String sheetid=com.wofu.ecommerce.oauthpaipai.OrderUtils.createInterOrder(this.getDao().getConnection(), o, tradecontactid,username,is_cod);
			
			
			for(int j=0;j<o.getOrderitems().size();j++)
			{
				com.wofu.ecommerce.oauthpaipai.OrderItem item=(com.wofu.ecommerce.oauthpaipai.OrderItem) o.getOrderitems().get(j);
				String sku=item.getStockLocalCode();
				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, ordercode, sku);												
			}
			
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}else if(o.getDealState().equals("STATE_COD_WAIT_SHIP")){   //货到付款等待发货
			is_cod=true;
			//创建订单 如果创建成功，减少库存
			String sheetid=com.wofu.ecommerce.oauthpaipai.OrderUtils.createInterOrder(this.getDao().getConnection(), o, tradecontactid,username,is_cod);
			
			
			for(int j=0;j<o.getOrderitems().size();j++)
			{
				com.wofu.ecommerce.oauthpaipai.OrderItem item=(com.wofu.ecommerce.oauthpaipai.OrderItem) o.getOrderitems().get(j);
				String sku=item.getStockLocalCode();
				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, ordercode, sku);												
			}
			
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
		}else if (o.getDealState().equals("DS_WAIT_BUYER_PAY")) 
		{
			throw new JException("订单还未付款!");
		}
		else if (o.getDealState().equals("DS_REFUND_WAIT_BUYER_DELIVERY")
					||o.getDealState().equals("DS_REFUND_WAIT_SELLER_RECEIVE")
					||o.getDealState().equals("DS_REFUND_WAIT_SELLER_AGREE")
					||o.getDealState().equals("DS_REFUND_OK")||o.getDealState().equals("DS_REFUND_ALL_OK"))
		{	
			throw new JException("订单已发货!");
			
		}else if (o.getDealState().equals("DS_DEAL_CANCELLED")||o.getDealState().equals("DS_CLOSED"))
		{
			throw new JException("订单已关闭或已退款!");
		}
		else
			throw new JException("订单已完成!");
	}

	private void getAmazonOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		
		String serviceurl=params.get("url").toString();
		String accesskeyid=params.get("accesskeyid").toString();
		String secretaccesskey=params.get("secretaccesskey").toString();
		String applicationname=params.get("applicationname").toString();
		String applicationversion=params.get("applicationversion").toString();
		String sellerid=params.get("sellerid").toString();
		String marketplaceid=params.get("marketplaceid").toString();

		com.amazonservices.mws.orders._2013_09_01.model.Order order=com.wofu.ecommerce.amazon.OrderUtils.getOrderByID(serviceurl,accesskeyid,secretaccesskey,
				applicationname,applicationversion,ordercode);
		
		List<com.amazonservices.mws.orders._2013_09_01.model.OrderItem> orderitems=com.wofu.ecommerce.amazon.OrderUtils.getOrderItemList(serviceurl,accesskeyid,secretaccesskey,
				applicationname,applicationversion,sellerid,order.getAmazonOrderId());
		
		Log.info(order.getAmazonOrderId()+" "+order.getOrderStatus()+" "+Formatter.format(AmazonUtil.convertToDate(order.getLastUpdateDate()),Formatter.DATE_TIME_FORMAT));
		
		if (order.getOrderStatus().equals("Unshipped"))
		{
			//创建订单 如果创建成功，减少库存
			String sheetid=com.wofu.ecommerce.amazon.OrderUtils.createInterOrder("订单跟踪",this.getDao().getConnection(),order,orderitems,tradecontactid);
			
			for (com.amazonservices.mws.orders._2013_09_01.model.OrderItem orderitem : orderitems) 
			{								
				String sku=orderitem.getSellerSKU();
			
				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, order.getAmazonOrderId(),sku);
				StockManager.addSynReduceStore("订单跟踪", this.getDao().getConnection(), tradecontactid, order.getOrderStatus(),order.getAmazonOrderId(), 
						sku, -Integer.valueOf(orderitem.getQuantityOrdered()).longValue(),false);
			}
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}else if (order.getOrderStatus().equals("Pending") )
		{
			throw new JException("订单还未付款!");
		}
		else if (order.getOrderStatus().equals("Shipped"))
		{	
			throw new JException("订单已发货!");
			
		}else if (order.getOrderStatus().equals("Canceled"))
		{
			throw new JException("订单已关闭或已退款!");
		}
		else
			throw new JException("订单已完成!");
	}
	
	private void getValOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		
		String supplierid = params.get("uname").toString() ;
		String passWord = params.get("pwd").toString() ;
		String URI = params.get("url").toString() ;

		String swsSupplierID = params.get("swsSupplierID").toString() ;
		String strkey = params.get("decryptkey").toString();

		String striv = params.get("decryptRandomCode").toString() ;
		String wsurl = params.get("webserviceurl").toString() ;

		com.wofu.ecommerce.vjia.Order o=com.wofu.ecommerce.vjia.OrderUtils.getOrderByID(wsurl,URI,swsSupplierID,strkey,
				striv,supplierid,passWord,ordercode);
				
		Log.info("订单号:"+o.getOrderid()+" 订单状态:"+o.getOrderstatus()+",时间:"+Formatter.format(o.getOrderdistributetime(),Formatter.DATE_TIME_FORMAT));
		
		  if (o.getOrderstatus().equalsIgnoreCase("NEW") || o.getOrderstatus().equalsIgnoreCase("CONFIRMED")|| o.getOrderstatus().equalsIgnoreCase("12"))
		{
			//创建订单 如果创建成功，减少库存
			String sheetid=com.wofu.ecommerce.vjia.OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,orgname);
			
			for(int j=0;j<o.getOrderitems().size();j++)
			{
				String sku = o.getOrderitems().get(j).getBarcode() ;
				long qty=Integer.valueOf(o.getOrderitems().get(j).getQty());

				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, o.getOrderid(), sku);										
										
			}
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}
		else if (o.getOrderstatus().equalsIgnoreCase("6") || o.getOrderstatus().equalsIgnoreCase("SENDED"))
		{	
			throw new JException("订单已发货!");
			
		}else if ( o.getOrderstatus().equalsIgnoreCase("CANCELED") ||o.getOrderstatus().equalsIgnoreCase("-1"))
		{
			throw new JException("订单已关闭或已退款!");
		}
		else
			throw new JException("订单已完成!");
	}
	
	private void getYhdOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		
		String url = params.get("url").toString() ;
		String appkey = params.get("appkey").toString() ;
		String appsecret = params.get("appsecret").toString() ;
		String token = params.get("token").toString();

		String erpver="1.0";		
		String format="json";		
		String ver="1.0";

		com.wofu.ecommerce.yhd.Order o=com.wofu.ecommerce.yhd.OrderUtils.getOrderByID(ordercode, appkey,token, format,ver);
				
		Log.info(o.getOrderCode()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateTime(),Formatter.DATE_TIME_FORMAT));
		
		if (o.getOrderStatus().equals("ORDER_PAYED") 
				|| o.getOrderStatus().equals("ORDER_TRUNED_TO_DO")
				|| o.getOrderStatus().equals("ORDER_CAN_OUT_OF_WH"))
		{
			//创建订单 如果创建成功，减少库存
			String sheetid=com.wofu.ecommerce.yhd.OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,orgname);
			
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				com.wofu.ecommerce.yhd.OrderItem item=(com.wofu.ecommerce.yhd.OrderItem) ito.next();
				String sku=item.getOuterId();
				
				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, o.getOrderCode(),sku);
				StockManager.addSynReduceStore("订单跟踪", this.getDao().getConnection(), tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
			}
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}
		else if (o.getOrderStatus().equals("ORDER_WAIT_PAY"))
		{	
			throw new JException("订单未付款!");
			
		}else if (o.getOrderStatus().equals("ORDER_CANCEL"))
		{
			throw new JException("订单已取消!");
		}
		else
			throw new JException("订单已完成!");
	}
	
	private void getLefengOrderInfo(String orgname,String tradecontactid,Map params,String ordercode) throws Exception
	{
		
		String shopid=params.get("gshopid").toString();
		String url=params.get("url").toString();
		String secretkey=params.get("secretaccesskey").toString();
		String encoding=params.get("encoding").toString();

		com.wofu.ecommerce.lefeng.Order o=com.wofu.ecommerce.lefeng.OrderUtils.getOrderByID(url,shopid,secretkey,encoding,ordercode);
				
		Log.info(o.getOrderCode()+" "+LefengUtil.getStatusName(o.getOrderStatus())+" "+o.getCreateTime());
		
		if (o.getOrderStatus()==6 || o.getOrderStatus()==3)
		{
			//创建订单 如果创建成功，减少库存
			String sheetid=com.wofu.ecommerce.lefeng.OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,orgname);
			
			for(Iterator ito=o.getItemList().getRelationData().iterator();ito.hasNext();)
			{
				com.wofu.ecommerce.lefeng.OrderItem item=(com.wofu.ecommerce.lefeng.OrderItem) ito.next();
				String sku=item.getItemCode();
			
				StockManager.deleteWaitPayStock("订单跟踪", this.getDao().getConnection(),tradecontactid, o.getOrderCode(),sku);
				StockManager.addSynReduceStore("订单跟踪", this.getDao().getConnection(), tradecontactid, String.valueOf(o.getOrderStatus()),o.getOrderCode(), sku, -item.getItemQuantity(),false);
			}
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");
				
				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
			
		}
		else if (o.getOrderStatus()==2)
		{	
			throw new JException("订单未付款!");
			
		}else if (o.getOrderStatus()==5)
		{
			throw new JException("订单已取消!");
		}
		else
			throw new JException("订单已完成!");
	}
	/**
	 * @throws Exception
	 */
	public void getOutStockInfo() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String ordercode=prop.getProperty("ordercode");
		String orgid=prop.getProperty("orgid");
		
		Vector vtoutstockinfo=new Vector();
	
		String sql="select count(*) from (select 1 aa from customerorder with(nolock) where refsheetid='"+ordercode+"' "
					+"union select 1 aa from customerorder0 with(nolock)  where refsheetid='"+ordercode+"') a";
		
		
		if (this.getDao().intSelect(sql)==0)
		{
			/*
			Hashtable htorder0=new Hashtable();
		
			htorder0.put("proctime", "");
			htorder0.put("note", "未入系统");
			
			vtoutstockinfo.add(htorder0);
			*/
			
			sql="select count(*) from ns_customerorder where tid='"+ordercode+"' ";
			
			if (this.getDao().intSelect(sql)==0 )	//直接通过API取订单数据下来
			{
				getOrderInfo(orgid,ordercode);
			}
		}
	
		sql="select count(*) from ns_customerorder a with(nolock),it_downnote b with(nolock) "
			+"where a.sheetid=b.sheetid and a.tid='"+ordercode+"' and b.sheettype=1";
		if (this.getDao().intSelect(sql)>0)
		{
			sql="select b.sheetid from ns_customerorder a,it_downnote b "
				+"where a.sheetid=b.sheetid and a.tid='"+ordercode+"' and b.sheettype=1";
			String sheetid=this.getDao().strSelect(sql);
			
			boolean is_success=OrderManager.genCustomerOrder(this.getDao().getConnection(), sheetid);				
			
			
			if (is_success)
			{
				//备份接口数据
				IntfUtils.backupDownNote(this.getDao().getConnection(), "yongjun",sheetid, "1");

				Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
			}
			else
			{
				
				Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
			}
		}

		sql="select count(*) from customerorderreflist where refsheetid='"+ordercode+"'";
		if (this.getDao().intSelect(sql)>0)
		{
			sql="select sheetid from customerorderreflist where refsheetid='"+ordercode+"'";
			
			String sheetid=this.getDao().strSelect(sql);
			
			sql="select refsheetid,convert(char(19),editdate,120) editdate from customerorder0 where sheetid='"+sheetid+"' "
				+" union "
				+"select refsheetid,convert(char(19),editdate,120) editdate from customerorder where sheetid='"+sheetid+"' ";

			Hashtable htreflist=this.getDao().oneRowSelect(sql);
				
			
			Hashtable htorder00=new Hashtable();
			
			htorder00.put("proctime", htreflist.get("editdate").toString());
			htorder00.put("note", "订单已合并至:"+htreflist.get("refsheetid").toString());
			
			vtoutstockinfo.add(htorder00);
		}
		
	
		sql="select count(*) from customerorder0 with(nolock) where refsheetid='"+ordercode+"'";
		
		if (this.getDao().intSelect(sql)>0)
		{
			sql="select convert(char(19),delivedate,120) proctime,'下单' note from customerorder0 where refsheetid='"+ordercode+"'";
			
			vtoutstockinfo.add(this.getDao().oneRowSelect(sql));

		}
		else
		{
			sql="select convert(char(19),delivedate,120) delivedate,"
				+"convert(char(19),checkdate,120) checkdate,flag,outshopid "
				+"from customerorder with(nolock) where refsheetid='"+ordercode+"'";
			
			Hashtable htorder=this.getDao().oneRowSelect(sql);
			
			int orderflag=Integer.valueOf(htorder.get("flag").toString()).intValue();
			
			String outshopid=htorder.get("outshopid").toString();
			
			Hashtable htorder1=new Hashtable();
			
			htorder1.put("proctime", htorder.get("delivedate").toString());
			htorder1.put("note", "下单");
			
			vtoutstockinfo.add(htorder1);
			
			Hashtable htorder2=new Hashtable();
			
			htorder2.put("proctime", htorder.get("checkdate").toString());
			
			if (orderflag==95 || orderflag==98 || orderflag==99)
				htorder2.put("note", "取消");
			else
				htorder2.put("note", "审单");
			
			vtoutstockinfo.add(htorder2);
			
			sql="select * from wmsconfig where dcshopid='"+outshopid+"'";					
			Hashtable htwmsconfig=this.getDao().oneRowSelect(sql);
					
			String wmsdsname=htwmsconfig.get("dsname").toString();
			int isinnerintf=Integer.valueOf(htwmsconfig.get("isinnerintf").toString()).intValue();
			
			if(orderflag!=95  && orderflag!=98 && orderflag!=99)
			{
				try
				{
					if (isinnerintf==1)
					{
						sql="select count(*) from "
							+"(select 1 aa from outstock0 with(nolock)  where custompursheetid='"+ordercode+"' "
							+" union "
							+"select 1 aa from outstock with(nolock) where custompursheetid='"+ordercode+"') a";
						if (this.getExtDao(wmsdsname).intSelect(sql)>0)
						{
			
							sql="select sheetid,flag from outstock0 with(nolock) where custompursheetid='"+ordercode+"' "
							+" union "
							+"select sheetid,flag from outstock with(nolock) where custompursheetid='"+ordercode+"'";
							
							Hashtable htflag=this.getExtDao(wmsdsname).oneRowSelect(sql);
							
							int flag=Integer.valueOf(htflag.get("flag").toString()).intValue();
							
							String sheetid=htflag.get("sheetid").toString();
							
							
							
							if (flag==0 || flag==10 || flag==90 ||flag==92)
							{
								sql="select convert(char(19),recdate,120) from outstock0 with(nolock) where custompursheetid='"+ordercode+"'";
								Hashtable htorder0=new Hashtable();
								
								htorder0.put("proctime", this.getExtDao(wmsdsname).strSelect(sql));
								htorder0.put("note", "仓库接单");
								
								vtoutstockinfo.add(htorder0);
								
								if (flag==0)  //接单
								{
									sql="select count(*) from OutStockBatch0 a,outstockbatchitem0 b "
										+"where a.sheetid=b.sheetid and outsheetid='"+sheetid+"'";
									if (this.getExtDao(wmsdsname).intSelect(sql)>0)
									{
										Hashtable htorderb=new Hashtable();
										
										htorderb.put("proctime", "");
										htorderb.put("note", "订单已分批");
										
										vtoutstockinfo.add(htorderb);
									}
									
								}else if (flag==10) //拣货
								{
									sql="select convert(char(19),editdate,120)  from pickgoods0 with(nolock) where refsheetid='"+sheetid+"'";
									Hashtable htorder10=new Hashtable();
									
									htorder10.put("proctime", this.getExtDao(wmsdsname).strSelect(sql));
									htorder10.put("note", "开始拣货");
									
									vtoutstockinfo.add(htorder10);
									
									//是否已打印
									sql="select isnull(printtimes,0) printtimes from pickgoods0 where refsheetid='"+sheetid+"'";
									
									if (this.getExtDao(wmsdsname).intSelect(sql)==0)
									{
										sql="select count(*) from OutStockBatch0 a,outstockbatchitem0 b "
											+"where a.sheetid=b.sheetid and outsheetid='"+sheetid+"'";
										if (this.getExtDao(wmsdsname).intSelect(sql)>0)
										{
											sql="select isnull(printtimes,0) printtimes  from OutStockBatch0 a,outstockbatchitem0 b "
												+"where a.sheetid=b.sheetid and outsheetid='"+sheetid+"'";
											if (this.getExtDao(wmsdsname).intSelect(sql)>0)
											{
												Hashtable htorderp=new Hashtable();
												
												htorderp.put("proctime", "");
												htorderp.put("note", "拣货单已打印");
												
												vtoutstockinfo.add(htorderp);
											}
											else
											{
												Hashtable htorderp=new Hashtable();
												
												htorderp.put("proctime", "");
												htorderp.put("note", "订单已分批");
												
												vtoutstockinfo.add(htorderp);
											}
										}
										else
										{
											Hashtable htorderp=new Hashtable();
											
											htorderp.put("proctime", "");
											htorderp.put("note", "拣货单未打印");
											
											vtoutstockinfo.add(htorderp);
										}
										
									}else
									{
										
										Hashtable htorderp=new Hashtable();
										
										htorderp.put("proctime", "");
										htorderp.put("note", "拣货单已打印");
										
										vtoutstockinfo.add(htorderp);
									}
									
								}else if (flag==90)  //拣货完成
								{
									
									sql="select convert(char(19),editdate,120) editdate,convert(char(19),checkdate,120) checkdate from pickgoods with(nolock) where refsheetid='"+sheetid+"'";
									
									Hashtable htpick=this.getExtDao(wmsdsname).oneRowSelect(sql); 
									
									Hashtable htorder10=new Hashtable();
									
									htorder10.put("proctime", htpick.get("editdate").toString());
									htorder10.put("note", "开始拣货");
									
									vtoutstockinfo.add(htorder10);
						
									
									Hashtable htorder90=new Hashtable();
									
									htorder90.put("proctime", htpick.get("checkdate").toString());
									htorder90.put("note", "拣货完成");
									
									vtoutstockinfo.add(htorder90);
									
									
									
								}else if (flag==92) //装箱完成
								{
							
									sql="select convert(char(19),editdate,120) editdate,convert(char(19),checkdate,120) checkdate from pickgoods with(nolock) where refsheetid='"+sheetid+"'";
									
									Hashtable htpick=this.getExtDao(wmsdsname).oneRowSelect(sql); 
									
									Hashtable htorder10=new Hashtable();
									
									htorder10.put("proctime", htpick.get("editdate").toString());
									htorder10.put("note", "开始拣货");
									
									vtoutstockinfo.add(htorder10);
									
									Hashtable htorder90=new Hashtable();
									
									htorder90.put("proctime", htpick.get("checkdate").toString());
									htorder90.put("note", "拣货完成");
									
									vtoutstockinfo.add(htorder90);
									
									sql="select convert(char(19),checkdate,120) checkdate from boxup with(nolock) where refsheetid='"+sheetid+"'";
									
									Hashtable htorder92=new Hashtable();
									
									htorder92.put("proctime", this.getExtDao(wmsdsname).strSelect(sql));
									htorder92.put("note", "装箱完成");
									
									vtoutstockinfo.add(htorder92);						
								}
							}else
							{
								sql="select convert(char(19),recdate,120) from outstock with(nolock) where custompursheetid='"+ordercode+"'";
								Hashtable htorder0=new Hashtable();
								
								htorder0.put("proctime", this.getExtDao(wmsdsname).strSelect(sql));
								htorder0.put("note", "仓库接单");
								
								vtoutstockinfo.add(htorder0);
								
								
								sql="select count(*) from pickgoods with(nolock) where refsheetid='"+sheetid+"'";
								
								if (this.getExtDao(wmsdsname).intSelect(sql)>0)
								{
								
									sql="select convert(char(19),editdate,120) editdate,"
										+"convert(char(19),isnull(checkdate,''),120) checkdate from pickgoods with(nolock) "
										+"where refsheetid='"+sheetid+"'";
									
									Hashtable htpick=this.getExtDao(wmsdsname).oneRowSelect(sql); 
									
									Hashtable htorder10=new Hashtable();
									
									htorder10.put("proctime", htpick.get("editdate").toString());
									htorder10.put("note", "开始拣货");
									
									vtoutstockinfo.add(htorder10);
									
									Hashtable htorder90=new Hashtable();
									
									htorder90.put("proctime", htpick.get("checkdate").toString());
									htorder90.put("note", "拣货完成");
									
									vtoutstockinfo.add(htorder90);
								}
									
								sql="select count(*) from boxup with(nolock) where refsheetid='"+sheetid+"'";
								
								if (this.getExtDao(wmsdsname).intSelect(sql)>0)
								{
									sql="select convert(char(19),checkdate,120) from boxup with(nolock) where refsheetid='"+sheetid+"'";
									
									Hashtable htorder92=new Hashtable();
									
									htorder92.put("proctime", this.getExtDao(wmsdsname).strSelect(sql));
									htorder92.put("note", "装箱完成");
									
									vtoutstockinfo.add(htorder92);
								}
								
								if (flag==97 || flag==98 || flag==99)
								{
									sql="select convert(char(19),checkdate,120) from outstock with(nolock) where sheetid='"+sheetid+"'";
									
									Hashtable htorder97=new Hashtable();
									
									htorder97.put("proctime", this.getExtDao(wmsdsname).strSelect(sql));
									htorder97.put("note", "取消");
									
									vtoutstockinfo.add(htorder97);
								}
								else
								{
								
									sql="select convert(char(19),checkdate,120) from outstock with(nolock) where sheetid='"+sheetid+"'";
									
									Hashtable htorder100=new Hashtable();
									
									htorder100.put("proctime", this.getExtDao(wmsdsname).strSelect(sql));
									htorder100.put("note", "出库");
									
									vtoutstockinfo.add(htorder100);
								}
								
							}
						}
						else
						{
							
							Hashtable htorder99=new Hashtable();
							
							htorder99.put("proctime", "");
							htorder99.put("note", "仓库未接收到订单");
							
							vtoutstockinfo.add(htorder99);
						}
					}else
					{
						sql="select count(*) from (select 1 aa from customerdelive a with(nolock) where customersheetid='"+ordercode+"'"
							+"union select 1 aa from customerdelive0 b with(nolock) where customersheetid='"+ordercode+"') a";
						
						if (this.getExtDao(wmsdsname).intSelect(sql)==0)
						{
							Hashtable htorder5=new Hashtable();
							htorder5.put("proctime", "");
							htorder5.put("note", "仓库未收到单");
							
							vtoutstockinfo.add(htorder5);
						}else
						{
						
							sql="select sheetid,refsheetid,flag,convert(char(19),checkdate,120) as checkdate from customerdelive a with(nolock) where customersheetid='"+ordercode+"'"
								+"union select sheetid,refsheetid,flag,convert(char(19),checkdate,120) as checkdate from customerdelive0 b with(nolock) where customersheetid='"+ordercode+"'";
							Hashtable htcd=this.getExtDao(wmsdsname).oneRowSelect(sql);
							
							String outsheetid=htcd.get("sheetid").toString();
							String ordersheetid=htcd.get("refsheetid").toString();
							int sheetflag=Integer.valueOf(htcd.get("flag").toString()).intValue();
							
					
							
							if (sheetflag==100)
							{
								Hashtable htorder100=new Hashtable();
								String checkdate=htcd.get("checkdate").toString();
								
								htorder100.put("proctime", checkdate);
								htorder100.put("note", "出库");
								
								vtoutstockinfo.add(htorder100);
							}else if (sheetflag==95 || sheetflag==98 ||sheetflag==99)
							{
								Hashtable htorder99=new Hashtable();
								String checkdate=htcd.get("checkdate").toString();
								
								htorder99.put("proctime", checkdate);
								htorder99.put("note", "取消");
								
								vtoutstockinfo.add(htorder99);
							}else
							{
								sql="select count(*) from it_infsheetlist0 with(nolock) where sheetid='"+ordersheetid+"' and sheettype=2209";
								if (this.getExtDao(wmsdsname).intSelect(sql)>0)
								{
									Hashtable htorder0=new Hashtable();
									htorder0.put("proctime", "");
									htorder0.put("note", "接口未处理");
									
									vtoutstockinfo.add(htorder0);
								}else{
									sql="select convert(char(19),reqdate,120) as reqdate,convert(char(19),"
										+"isnull(replydate,getdate()),120) as replydate,status from ecs_bestlogisticsmsg with(nolock) "
										+"where infsheetid='"+outsheetid+"' and infsheettype=2209";
									Hashtable htmsg=this.getExtDao(wmsdsname).oneRowSelect(sql);
							
									String reqdate=htmsg.get("reqdate").toString();
									String replydate=htmsg.get("replydate").toString();
									int status=Integer.valueOf(htmsg.get("status").toString()).intValue();
								
									if (status==0)
									{
										Hashtable htorder0=new Hashtable();
										htorder0.put("proctime", reqdate);
										htorder0.put("note", "已请求未反馈");
										
										vtoutstockinfo.add(htorder0);
									}else if(status==-1)
									{
										Hashtable htorder11=new Hashtable();
										htorder11.put("proctime", replydate);
										htorder11.put("note", "拒绝");
										
										vtoutstockinfo.add(htorder11);
									}else if(status==1)
									{
										Hashtable htorder12=new Hashtable();
										htorder12.put("proctime", replydate);
										htorder12.put("note", "接收成功");
										
										vtoutstockinfo.add(htorder12);
									}
								}
							}
						}
					}
				}finally
				{
					this.getExtDao(wmsdsname).freeConnection();				
				}
			}
			
		}

	
		this.OutputStr(this.toJSONArray(vtoutstockinfo));
		
	}

	public void getOrderInfo() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String ordercode=prop.getProperty("ordercode");
		
		String sql="select upper(outshopid) outshopid,convert(char(19),delivedate,120) delivedate,ltrim(rtrim(address)) address,"
			+"ltrim(rtrim(detailid)) detailid,ltrim(rtrim(linktele)) linktele,ltrim(rtrim(linkman)) linkman,"
			+"upper(ltrim(rtrim(delivery))) delivery,isnull(refnote,'') refnote,isnull(invoiceflag,0) invoiceflag,isnull(invoicetitle,'') invoicetitle "
			+"from customerorder0 where refsheetid='"+ordercode+"' "
			+" union "
			+"select upper(outshopid) outshopid,convert(char(19),delivedate,120) delivedate,ltrim(rtrim(address)) address,"
			+"ltrim(rtrim(detailid)) detailid,ltrim(rtrim(linktele)) linktele,ltrim(rtrim(linkman)) linkman,"
			+"upper(ltrim(rtrim(delivery))) delivery,isnull(refnote,'') refnote,isnull(invoiceflag,0) invoiceflag,isnull(invoicetitle,'') invoicetitle "
			+"from customerorder where refsheetid='"+ordercode+"' and flag=100";
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
		
	}
	
	public void cancelOrder() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String ordercode=prop.getProperty("ordercode");
		
		String checker="";
		if (this.getUserInfo().getLogin().indexOf(":")>=0)
			checker=this.getUserInfo().getLogin().split(":")[1];
		else
			checker=this.getUserInfo().getLogin();
		
		//如果还没进系统,提示不能取消
		String sql="select count(*) from (select 1 aa from ns_customerorder where tid='"+ordercode+"' "
		+"union select 1 aa from customerorder where refsheetid='"+ordercode+"' "
		+"union select 1 aa from customerorder0 where refsheetid='"+ordercode+"') a";
		
		if(this.getDao().intSelect(sql)==0)
		{
			//throw new JException("未入系统,不能取消,请稍候再试!");
			//预修改
			sql="select count(*) from ecs_orderpremodify with(nolock) where ordercode='"+ordercode+"'";
			if (this.getDao().intSelect(sql)>0)
			{
				sql="update ecs_orderpremodify set cancelflag=1,updator='"+checker+"',updatetime=getdate() where ordercode='"+ordercode+"'";
				this.getDao().execute(sql);
			}
			else
			{
				sql="insert into ecs_orderpremodify(ordercode,cancelflag,creator,createtime) "
					+"values('"+ordercode+"',1,'"+checker+"',getdate())";
				this.getDao().execute(sql);
			}
		}
		else
		{
		
			sql="select count(*) from customerorderreflist with(nolock) where refsheetid='"+ordercode+"'";
			if (this.getDao().intSelect(sql)>0)
			{
				throw new JException("订单已合并,不能单独取消!");
			}
			
			sql="select outshopid from customerorder0 where refsheetid='"+ordercode+"' "
				+" union " 
				+"select outshopid from customerorder where refsheetid='"+ordercode+"' and flag=100";
			String outshopid=this.getDao().strSelect(sql);
			
	
			//如果还没审核直接取消
			sql="select count(*) from customerorder0 with(nolock) where refsheetid='"+ordercode+"'";
			if (this.getDao().intSelect(sql)>0)
			{
				sql="select sheetid from customerorder0 with(nolock) where refsheetid='"+ordercode+"'";
				String sheetid=this.getDao().strSelect(sql);
				sql="execute Bak_CustomerOrder '"+sheetid+"','"
					+checker+"'; ";
				this.getDao().execute(sql);
				
				sql="delete from customerorderitem0 where sheetid='"+sheetid+"'";
				this.getDao().execute(sql);
				
				sql="delete from customerorder0 where sheetid='"+sheetid+"'";
				this.getDao().execute(sql);
				
			}else
			{
				sql="select flag from customerorder with(nolock) where refsheetid='"+ordercode+"'";
				if (this.getDao().intSelect(sql)<100)
				{
					throw new JException("订单已取消!");
				}
				else
				{
					sql="select * from wmsconfig where dcshopid='"+outshopid+"'";					
					Hashtable htwmsconfig=this.getDao().oneRowSelect(sql);
							
					String wmsdsname=htwmsconfig.get("dsname").toString();
					int isinnerintf=Integer.valueOf(htwmsconfig.get("isinnerintf").toString()).intValue();			
	
					try
					{
						if(isinnerintf==1)
						{
							sql="select count(*) from outstock with(nolock) where custompursheetid='"+ordercode+"'";
							if(this.getExtDao(wmsdsname).intSelect(sql)>0)
							{
								sql="select flag from outstock with(nolock) where custompursheetid='"+ordercode+"'";
								int flag =this.getExtDao(wmsdsname).intSelect(sql);
								if (flag==97 || flag==98 || flag==99)
								{
									throw new JException("订单已取消!");
								}
								else
								{
									throw new JException("订单已出库,不能取消!");
								}
							}
							else
							{
								sql="select count(*) from outstock0 with(nolock) where custompursheetid='"+ordercode+"'";
								if (this.getExtDao(wmsdsname).intSelect(sql)==0)
								{
									//throw new JException("仓库还未接收到订单,请稍后再取消!");
									//预修改
									sql="select count(*) from ecs_orderpremodify with(nolock) where ordercode='"+ordercode+"'";
									if (this.getDao().intSelect(sql)>0)
									{
										sql="update ecs_orderpremodify set cancelflag=1 where ordercode='"+ordercode+"'";
										this.getExtDao(wmsdsname).execute(sql);
									}
									else
									{
										sql="insert into ecs_orderpremodify(ordercode,cancelflag) "
											+"values('"+ordercode+"',1)";
										this.getExtDao(wmsdsname).execute(sql);
									}
								}
								else
								{
									sql="select flag,isnull(holdflag,0) holdflag from outstock0 with(nolock) where custompursheetid='"+ordercode+"'";
									Hashtable htflag=this.getExtDao(wmsdsname).oneRowSelect(sql);
									
									int flag =Integer.valueOf(htflag.get("flag").toString()).intValue();
									int holdflag =Integer.valueOf(htflag.get("holdflag").toString()).intValue();
									
									if (flag==92)
									{
										throw new JException("订单已装箱,不能取消,请联系仓库人员!");
									}
									
									if (holdflag==1)
									{
										throw new JException("订单已取消,不能重复取消!");
									}
									
									sql="select sheetid from outstock0 with(nolock) where custompursheetid='"+ordercode+"'";
									
									String sheetid=this.getExtDao(wmsdsname).strSelect(sql);
									
									sql="execute TL_CancelOutStock '"+sheetid+"','"
									+checker+"'; ";
									this.getExtDao(wmsdsname).execute(sql);
								}
							}
						}
						else
						{
							sql="select sheetid from customerorder with(nolock) where refsheetid='"+ordercode+"'";
							String sheetid=this.getDao().strSelect(sql);
							
							sql="execute TL_CancelSalesOrder '"+sheetid+"'";
							this.getExtDao(wmsdsname).execute(sql);
						}
					}finally
					{
						this.getExtDao(wmsdsname).freeConnection();
					}
					
				}
			}
		}
	}
	
	public void modifyOrder() throws Exception{
		String reqdata = this.getReqData();		
	
		
		Properties prop=StringUtil.getIniProperties(reqdata);
		int orgid=Integer.valueOf(prop.getProperty("orgid")).intValue();
		String ordercode=prop.getProperty("ordercode");
		String fieldname=prop.getProperty("fieldname");
		String fieldvalue=prop.getProperty("fieldvalue");
		
		
		String checker="";
		if (this.getUserInfo().getLogin().indexOf(":")>=0)
			checker=this.getUserInfo().getLogin().split(":")[1];
		else
			checker=this.getUserInfo().getLogin();
		
		//如果还没进系统,提示不能取消
		String sql="select count(*) from (select 1 aa from customerorder where refsheetid='"+ordercode+"' "
		+"union select 1 aa from customerorder0 where refsheetid='"+ordercode+"') a";
		
		if(this.getDao().intSelect(sql)==0)
		{
			//throw new JException("未入系统,不能修改,请稍候再试!");
			
			//预修改
			sql="select count(*) from ecs_orderpremodify with(nolock) where ordercode='"+ordercode+"'";
			if (this.getDao().intSelect(sql)>0)
			{
				sql="update ecs_orderpremodify set "+fieldname+"='"+fieldvalue+"',"
					+"updator='"+checker+"',updatetime=getdate() where ordercode='"+ordercode+"'";
				this.getDao().execute(sql);
			}
			else
			{
				sql="insert into ecs_orderpremodify(ordercode,"+fieldname+",creator,createtime) "
					+"values('"+ordercode+"','"+fieldvalue+"','"+checker+"',getdate())";
				this.getDao().execute(sql);
			}
			
			if (fieldname.equalsIgnoreCase("invoicetitle"))
			{
				sql="update ecs_orderpremodify set invoiceflag=1 where ordercode='"+ordercode+"'";
				this.getDao().execute(sql);
			}
		}
		else
		{
			sql="select count(*) from customerorderreflist with(nolock) where refsheetid='"+ordercode+"'";
			if (this.getDao().intSelect(sql)>0)
			{
				throw new JException("订单已合并,不能修改!");
			}
			
			sql="select outshopid from customerorder0 where refsheetid='"+ordercode+"' "
				+" union "
				+"select outshopid from customerorder where refsheetid='"+ordercode+"' and flag=100";
			String outshopid=this.getDao().strSelect(sql);
		
		
			String tips="";
			if (fieldname.equalsIgnoreCase("linktele")) tips="联系电话修改:";
			else if (fieldname.equalsIgnoreCase("address")) tips="收货地址修改:";
			else if (fieldname.equalsIgnoreCase("linkman")) tips="收货人修改:";
			else if (fieldname.equalsIgnoreCase("delivery")) tips="快递修改:";
			else if (fieldname.equalsIgnoreCase("refnote")) tips="备注修改:";
			else if (fieldname.equalsIgnoreCase("invoicetitle")) tips="发票修改:";
			else if (fieldname.equalsIgnoreCase("outshopid")) tips="出货地修改:";
			
			//如果还没审核直接修改
			sql="select count(*) from customerorder0 with(nolock) where refsheetid='"+ordercode+"'";
			if (this.getDao().intSelect(sql)>0)
			{
				sql="update customerorder0 set "+fieldname+"='"+fieldvalue
					+"',notes=notes+' "+tips+checker+",时间:"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT) +"' where refsheetid='"+ordercode+"'";
				this.getDao().execute(sql);
				
				if (fieldname.equalsIgnoreCase("invoicetitle"))
				{
					sql="update customerorder0 set invoiceflag=1 where refsheetid='"+ordercode+"'";
					this.getDao().execute(sql);
				}
								
			}else
			{
				if (fieldname.equalsIgnoreCase("outshopid"))
				{
					throw new JException("订单已审核,不能修改出货地,须先取消再重入!");
				}
				
				sql="select * from wmsconfig where dcshopid='"+outshopid+"'";					
				Hashtable htwmsconfig=this.getDao().oneRowSelect(sql);
						
				String wmsdsname=htwmsconfig.get("dsname").toString();
				int isinnerintf=Integer.valueOf(htwmsconfig.get("isinnerintf").toString()).intValue();			
		
				try
				{
					if (isinnerintf==1)
					{
						sql="select count(*) from outstock with(nolock) where custompursheetid='"+ordercode+"'";
						if(this.getExtDao(wmsdsname).intSelect(sql)>0)
						{				
							throw new JException("订单已完结,不能修改!");
						}
						else
						{
							sql="select count(*) from outstock0 with(nolock) where custompursheetid='"+ordercode+"'";
							if (this.getExtDao(wmsdsname).intSelect(sql)==0)
							{
								//throw new JException("仓库还未接收到订单,请稍后再修改!");
								
								//预修改
								sql="select count(*) from ecs_orderpremodify with(nolock) where ordercode='"+ordercode+"'";
								if (this.getExtDao(wmsdsname).intSelect(sql)>0)
								{
									sql="update ecs_orderpremodify set "+fieldname+"='"+fieldvalue+"',"
										+"updator='"+checker+"',updatetime=getdate() where ordercode='"+ordercode+"'";
									this.getExtDao(wmsdsname).execute(sql);
								}
								else
								{
									sql="insert into ecs_orderpremodify(ordercode,"+fieldname+",creator,createtime) "
										+"values('"+ordercode+"','"+fieldvalue+"','"+checker+"',getdate())";
									this.getExtDao(wmsdsname).execute(sql);
								}
								
								if (fieldname.equalsIgnoreCase("invoicetitle"))
								{
									sql="update ecs_orderpremodify set invoiceflag=1 where ordercode='"+ordercode+"'";
									this.getExtDao(wmsdsname).execute(sql);
									
									sql="update customerorder set invoiceflag=1 where refsheetid='"+ordercode+"'";
									this.getDao().execute(sql);
								}
								
								sql="update customerorder set "+fieldname+"='"+fieldvalue
									+"',notes=notes+' "+tips+checker+",时间:"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT) +"' where refsheetid='"+ordercode+"'";
								this.getDao().execute(sql);
								
							}
							else
							{
								sql="select sheetid,flag from outstock0 with(nolock) where custompursheetid='"+ordercode+"'";
								
								Hashtable htoutstock0=this.getExtDao(wmsdsname).oneRowSelect(sql);
								
								int flag=Integer.valueOf(htoutstock0.get("flag").toString()).intValue();
								String sheetid=htoutstock0.get("sheetid").toString();
								
								if (flag>=90)
								{
									throw new JException("拣货已完成,不能修改,请联系仓库管理人员!");
								}
								else if(flag==10)
								{
									sql="select isnull(printtimes,0) printtimes from pickgoods0 where refsheetid='"+sheetid+"'";
									
									if (this.getExtDao(wmsdsname).intSelect(sql)==0)
									{
										sql="select count(*) from OutStockBatch0 a,outstockbatchitem0 b "
											+"where a.sheetid=b.sheetid and outsheetid='"+sheetid+"'";
										if (this.getExtDao(wmsdsname).intSelect(sql)>0)
										{
											sql="select isnull(printtimes,0) printtimes  from OutStockBatch0 a,outstockbatchitem0 b "
												+"where a.sheetid=b.sheetid and outsheetid='"+sheetid+"'";
											if (this.getExtDao(wmsdsname).intSelect(sql)>0)
											{
												throw new JException("拣货单已打印,不能修改,请联系仓库管理人员!");
											}
											else
											{
												if (fieldname.equalsIgnoreCase("invoicetitle")||fieldname.equalsIgnoreCase("delivery"))
												{
													throw new JException("拣货单已分批,不能修改快递或发票,请联系仓库管理人员!");
												}
											}
										}
									}
									else
									{
										throw new JException("拣货单已打印,不能修改,请联系仓库管理人员!");
									}
								}else if (flag==0)
								{
									if (fieldname.equalsIgnoreCase("invoicetitle")||fieldname.equalsIgnoreCase("delivery"))
									{
										sql="select count(*)  from OutStockBatch0 a,outstockbatchitem0 b "
											+"where a.sheetid=b.sheetid and outsheetid='"+sheetid+"'";
										if(this.getExtDao(wmsdsname).intSelect(sql)>0)
										{
											throw new JException("拣货单已分批,不能修改快递或发票,请联系仓库管理人员!");
										}
									}
								}
								
								sql="update customerorder set "+fieldname+"='"+fieldvalue
								+"',notes=notes+' "+tips+checker+",时间:"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT) +"' where refsheetid='"+ordercode+"'";
								this.getDao().execute(sql);
								
								if (fieldname.equalsIgnoreCase("linktele")) fieldname="tele";
								
								if (!fieldname.equalsIgnoreCase("refnote"))
								{											
									//修改
									sql="update outstock0 set "+fieldname+"='"+fieldvalue
									+"',note=note+' "+tips+checker+",时间:"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT) +"' where sheetid='"+sheetid+"'";
									this.getExtDao(wmsdsname).execute(sql);
									
									sql="update outstocknote set "+fieldname+"='"+fieldvalue
									+"',note=note+' "+tips+checker+",时间:"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT) +"' where sheetid='"+sheetid+"'";
									this.getExtDao(wmsdsname).execute(sql);
								}
								
								if (fieldname.equalsIgnoreCase("invoicetitle"))
								{
									sql="update customerorder set invoiceflag=1 where refsheetid='"+ordercode+"'";
									this.getDao().execute(sql);
									
									sql="update outstock0 set invoiceflag=1 where sheetid='"+sheetid+"'";
									this.getExtDao(wmsdsname).execute(sql);
									
									sql="update outstocknote set invoiceflag=1 where sheetid='"+sheetid+"'";
									this.getExtDao(wmsdsname).execute(sql);
									
								
								}
														
							}
						}
					}else
					{
						throw new JException("暂时不支持,请联系第三方仓库修改!");
					}
				}finally
				{
					this.getExtDao(wmsdsname).freeConnection();
				}
			}
		}
		
		//更新淘宝备注
		//if (fieldname.equalsIgnoreCase("refnote") && tradecontactid==1)
		//{
			//updateMemo(ordercode,fieldvalue);
		//}
		
		//更新发票备注
		//if (fieldname.equalsIgnoreCase("invoicetitle") && tradecontactid==1)
			//updateInvoice(ordercode,fieldvalue);
		
	}
	
	/*
	private void updateMemo(String ordercode,String memo) throws Exception
	{
		String appkey="12084299";
		String appsecret="719357e3e705802099675d679a52b0ec";
		String authcode="6100d2629fe5982e35411c979127c9fcfe158d013d8e255422229230";	
		String url="http://gw.api.taobao.com/router/rest";
		
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, appsecret,"xml");
		TradeMemoUpdateRequest reqitems=new TradeMemoUpdateRequest();
		reqitems.setTid(Long.valueOf(ordercode));
		reqitems.setMemo(memo);
		reqitems.setFlag(Long.valueOf(1));
		client.execute(reqitems , authcode);
	}
	
	private void updateInvoice(String ordercode,String memo) throws Exception
	{
		String appkey="12084299";
		String appsecret="719357e3e705802099675d679a52b0ec";
		String authcode="6100d2629fe5982e35411c979127c9fcfe158d013d8e255422229230";	
		String url="http://gw.api.taobao.com/router/rest";
		
		/TaobaoClient client=new DefaultTaobaoClient(url, appkey, appsecret,"xml");
		//TradeMemoUpdateRequest reqitems=new TradeMemoUpdateRequest();
		reqitems.setTid(Long.valueOf(ordercode));
		reqitems.setMemo(memo);
		reqitems.setFlag(Long.valueOf(5));
		client.execute(reqitems , authcode);
		
		String checker="";
		if (this.getUserInfo().getLogin().indexOf(":")>=0)
			checker=this.getUserInfo().getLogin().split(":")[1];
		else
			checker=this.getUserInfo().getLogin();
		
		//预修改
		String sql="select count(*) from ecs_orderpremodify with(nolock) where ordercode='"+ordercode+"'";
		if (this.getDao().intSelect(sql)>0)
		{
			sql="update ecs_orderpremodify set invoiceflag=1,invoicetitle='"+memo+"',"
				+"updator='"+checker+"',updatetime=getdate() where ordercode='"+ordercode+"'";
			this.getDao().execute(sql);
		}
		else
		{
			sql="insert into ecs_orderpremodify(ordercode,invoiceflag,invoicetitle,creator,createtime) "
				+"values('"+ordercode+"',1,'"+memo+"','"+checker+"',getdate())";
			this.getDao().execute(sql);
		}
				
	}
	
	*/
	public void mergerOrder() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String sourceordercode=prop.getProperty("sourceordercode");
		String targetordercode=prop.getProperty("targetordercode");
		

		
		String sql="select count(*) from customerorder0 with(nolock) where refsheetid='"+targetordercode+"'";
		
		if (this.getDao().intSelect(sql)==0)
		{
			throw new JException("目标订单未进系统或者已审核,不能合并!");
		}
		
		sql="select outshopid from customerorder0 where refsheetid='"+sourceordercode+"'";
		String outshopid=this.getDao().strSelect(sql);
	
		
		sql="select count(*) from customerorderreflist with(nolock) where refsheetid='"+sourceordercode+"'";
		if (this.getDao().intSelect(sql)>0)
		{
			sql="select sheetid from customerorderreflist with(nolock) where refsheetid='"+sourceordercode+"'";
			
			String sheetid=this.getDao().strSelect(sql);
			
			sql="select refsheetid from customerorder0 with(nolock) where sheetid='"+sheetid+"' "
				+" union "
				+"select refsheetid from customerorder with(nolock) where sheetid='"+sheetid+"' ";
			
			String sourcemergerordercode=this.getDao().strSelect(sql);

			throw new JException("源订单已合并至:"+sourcemergerordercode);		
		}
		
		sql="select count(*) from customerorder0 with(nolock) where refsheetid='"+sourceordercode+"'";
		
		if (this.getDao().intSelect(sql)==0)
		{
			throw new JException("源订单未进系统或者已审核,不能合并!");
		}
		
		sql="select count(*) from customerorderreflist with(nolock) where refsheetid='"+targetordercode+"'";
		if (this.getDao().intSelect(sql)>0)
		{
			sql="select sheetid from customerorderreflist with(nolock) where refsheetid='"+targetordercode+"'";
			
			String sheetid=this.getDao().strSelect(sql);
			
			sql="select refsheetid from customerorder0 with(nolock) where sheetid='"+sheetid+"' "
				+" union "
				+"select refsheetid from customerorder with(nolock) where sheetid='"+sheetid+"' ";
			
			String targetmergerordercode=this.getDao().strSelect(sql);

			throw new JException("目标订单已合并至:"+targetmergerordercode);		
		}
		
		
		sql="select sheetid,outshopid,inshopid,purchaseflag,customid,ltrim(rtrim(linkman)) linkman,ltrim(rtrim(address)) address "
			+"from customerorder0 with(nolock) where refsheetid='"+sourceordercode+"'";
		
		Hashtable htsourceorder=this.getDao().oneRowSelect(sql);
		
		String sourcesheetid=htsourceorder.get("sheetid").toString();
		String sourceoutshopid=htsourceorder.get("outshopid").toString();
		String sourceinshopid=htsourceorder.get("inshopid").toString();
		int sourcepurchaseflag=Integer.valueOf(htsourceorder.get("purchaseflag").toString()).intValue();
		int sourcecustomid=Integer.valueOf(htsourceorder.get("customid").toString()).intValue();
		String sourcelinkman=htsourceorder.get("linkman").toString();
		String sourceaddress=htsourceorder.get("address").toString();
		
		sql="select sheetid,outshopid,inshopid,purchaseflag,customid,ltrim(rtrim(linkman)) linkman,ltrim(rtrim(address)) address "
			+"from customerorder0 with(nolock) where refsheetid='"+targetordercode+"'";
		
		Hashtable httargetorder=this.getDao().oneRowSelect(sql);
		
		String targetsheetid=httargetorder.get("sheetid").toString();
		String targetoutshopid=httargetorder.get("outshopid").toString();
		String targetinshopid=httargetorder.get("inshopid").toString();
		int targetpurchaseflag=Integer.valueOf(httargetorder.get("purchaseflag").toString()).intValue();
		int targetcustomid=Integer.valueOf(httargetorder.get("customid").toString()).intValue();
		String targetlinkman=httargetorder.get("linkman").toString();
		String targetaddress=httargetorder.get("address").toString();
		
		if(!sourceoutshopid.equalsIgnoreCase(targetoutshopid))
		{
			throw new JException("出货地不一致,不允许合并!");
		}
		
		if(!sourceinshopid.equalsIgnoreCase(targetinshopid))
		{
			throw new JException("店铺不一致,不允许合并!");
		}
		
		if(sourcepurchaseflag!=targetpurchaseflag)
		{
			throw new JException("订单类型不一致,不允许合并!");
		}
		
		if(sourcecustomid!=targetcustomid)
		{
			throw new JException("客户不一致,不允许合并!");
		}
		
		if(!sourcelinkman.equalsIgnoreCase(targetlinkman))
		{
			throw new JException("联系人不一致,不允许合并!");
		}
		
		if(!sourceaddress.equalsIgnoreCase(targetaddress))
		{
			throw new JException("收货地址不一致,不允许合并!");
		}
		
		String checker="";
		if (this.getUserInfo().getLogin().indexOf(":")>=0)
			checker=this.getUserInfo().getLogin().split(":")[1];
		else
			checker=this.getUserInfo().getLogin();
		
		sql="execute TL_CustomerOrderOperate '"+targetsheetid+"','"+sourcesheetid+"',null,0,'','"+checker+"'; ";
		this.getDao().execute(sql);
	}
	
}
