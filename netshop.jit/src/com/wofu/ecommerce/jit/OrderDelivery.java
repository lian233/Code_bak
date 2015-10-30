package com.wofu.ecommerce.jit;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import vipapis.common.VendorType;
import vipapis.common.Warehouse;
import vipapis.delivery.CreateDeliveryRequest;
import vipapis.delivery.CreateDeliveryResponse;
import vipapis.delivery.Delivery;
import vipapis.delivery.SimplePick;
import vipapis.delivery.JitDeliveryServiceHelper.JitDeliveryServiceClient;
import vipapis.delivery.JitDeliveryServiceHelper.importDeliveryDetail_args;
import vipapis.delivery.JitDeliveryServiceHelper.importDeliveryDetail_argsHelper;

import com.vip.osp.sdk.buffer.MemoryBuffer;
import com.vip.osp.sdk.context.InvocationContext;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.JSONProtocol;
import com.vip.osp.sdk.protocol.Protocol;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jit.utils.Utils;
public class OrderDelivery extends Thread {
	private static HashMap<String,Integer> warehouse = new HashMap<String,Integer>();
	static {
		warehouse.put("VIP_NH",1);  //唯品会广州仓 唯品会北京仓 唯品会成都仓 唯品会武汉仓 --唯品会上海仓
		warehouse.put("VIP_SH",2);
		warehouse.put("VIP_CD",3);
		warehouse.put("VIP_BJ",4);
		warehouse.put("VIP_HZ",5);
	}
	

	private static String jobname = "JIT订单发货处理作业";
	
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);

				delivery(connection);		//合并订单出库
				deliverySingle(connection);		//没有合并的订单出库

			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	//合并订单出库
	private void delivery(Connection conn)  throws Exception
	{
		//查找合单记录发货
		String sql = "select distinct c.SheetID  from ns_delivery a with(nolock) ,it_upnote b with(nolock),CustomerOrderRefList c with(nolock)"
			+" where a.sheetID=b.SheetID and A.tid=c.RefSheetID and a.tradecontactid="+Params.tradecontactid;
		
		List<String> deliveryList = SQLHelper.multiRowListSelect(conn, sql);
		Log.info("本次要处理的合并订单发货条数为: "+deliveryList.size());
		for (int i = 0; i < deliveryList.size(); i++) {
			//合单后的订单号
			String sheetid = deliveryList.get(i).toString();
			
			sql ="select top 1 companycode,tid from ns_delivery with(nolock) where tid in(select refsheetid "
				+"from customerorderreflist with(nolock) where sheetid='"+sheetid+"')";
			Hashtable ht = SQLHelper.oneRowSelect(conn, sql);
			String deliveryCompanyCode = ht.get("companycode").toString();
			String tid = ht.get("tid").toString();
			String[] poinfo = tid.split("_");
			String po_no = poinfo[0];
			String pick_no = poinfo[1];
			String delivery_no = Params.companyname+System.currentTimeMillis();			//送货单编号??
			//订单所在仓库
			sql = "select receiveraddress from ns_customerorder with(nolock) where tid='"+tid+"'";
			String address = SQLHelper.strSelect(conn, sql);
			//发货第一步：创建出仓单
			CreateDeliveryResponse deliveryresponse = createDelivery(poinfo[0],delivery_no,address,deliveryCompanyCode);
			if(deliveryresponse!=null){
				Log.info("Storage_no: "+deliveryresponse.getStorage_no()+"Delivery_id: "+deliveryresponse.getDelivery_id());
				//发货第二步：将出仓明细信息导入到出仓单中
				String storage_no =importDeliveryDetailT(po_no,deliveryresponse,sheetid,conn,false);
				Log.info("result:　"+storage_no);
				//第三步：确认出库单
				if(storage_no.equals(deliveryresponse.getStorage_no())){
					String deliver_id = confirmDelivery(storage_no,po_no);
					Log.info("String deliver_id:　"+deliver_id);
					if(delivery_no.equals(deliver_id)){
						//出库成功后，备份it_upnote表数据
						backupData(sheetid,conn);
						}
					}
				
			}
		}
	}
	//备份合单表的所有子订单发货记录
	private void backupData(String sheetid,Connection conn) throws Exception{
		String sql ="select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"'";
		List<String> orderList = SQLHelper.multiRowListSelect(conn, sql);
		for(int i=0;i<orderList.size();i++){
			String tid = orderList.get(i);
			try{
				conn.setAutoCommit(false);
				sql ="insert into it_upnotebak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
					+"select onwer,sheetid,sheettype,sender,receiver,notetime,getdate(),1 from it_upnote with(nolock) where sheetid in"
					+"select sheetid from ns_delivery where tid='"+tid+"'";
				SQLHelper.executeSQL(conn, sql);
				sql = "delete it_upnote from it_upnote a  with(nolock),ns_devivery b with(nolock) where a.sheetid=b.sheetid and b.tid='"+tid+"'";
				SQLHelper.executeSQL(conn,sql);
				conn.commit();
				Log.info(jobname,"处理订单【" + tid + "】发货成功");
			}catch(Exception e){
				if(conn!=null && !conn.getAutoCommit())
					conn.rollback();
			}
			
		}
		
	}
	
	//备份合单表的所有子订单发货记录
	private void backupDataSingle(String tid,Connection conn) throws Exception{
			try{
				conn.setAutoCommit(false);
				String sql ="insert into it_upnotebak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
					+"select onwer,sheetid,sheettype,sender,receiver,notetime,getdate(),1 from it_upnote with(nolock) where sheetid in"
					+"select sheetid from ns_delivery where tid='"+tid+"'";
				SQLHelper.executeSQL(conn, sql);
				sql = "delete it_upnote from it_upnote a  with(nolock),ns_devivery b with(nolock) where a.sheetid=b.sheetid and b.tid='"+tid+"'";
				SQLHelper.executeSQL(conn,sql);
				conn.commit();
				Log.info(jobname,"处理订单【" + tid + "】发货成功");
			}catch(Exception e){
				if(conn!=null && !conn.getAutoCommit())
					conn.rollback();
			}
			
		
	}

	//没有合并的订单出库
	private void deliverySingle(Connection conn)  throws Exception
	{
		//查找合单记录发货
		String sql = "select a.tid,a.companycode from ns_delivery a with(nolock) ,it_upnote b with(nolock) where a.sheetID=b.SheetID and  "
			+"a.tradecontactid="+Params.tradecontactid+" and a.tid not in(select refsheetid from CustomerOrderRefList with(nolock))";
		
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("本次要处理的订单发货条数为: "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			Hashtable ht = (Hashtable)vdeliveryorder.get(i);
			String tid = ht.get("tid").toString();
			String deliveryCompanyCode = ht.get("companycode").toString();
			
			String[] poinfo = tid.split("_");
			String po_no = poinfo[0];
			String pick_no = poinfo[1];
			String delivery_no = Params.companyname+System.currentTimeMillis();
			//订单所在仓库
			sql = "select receiveraddress from ns_customerorder with(nolock) where tid='"+tid+"'";
			String address = SQLHelper.strSelect(conn, sql);
			Log.info("订单所在仓库:" + address);
			//发货第一步：创建出仓单
			CreateDeliveryResponse deliveryresponse = createDelivery(poinfo[0],delivery_no,address,deliveryCompanyCode);
			if(deliveryresponse!=null){
				Log.info("Storage_no: "+deliveryresponse.getStorage_no()+"Delivery_id: "+deliveryresponse.getDelivery_id());
				//发货第二步：将出仓明细信息导入到出仓单中
				String storage_no =importDeliveryDetailT(po_no,deliveryresponse,tid,conn,true);
				Log.info("result:　"+storage_no);
				//第三步：确认出库单
				if(storage_no.equals(deliveryresponse.getStorage_no())){
					String deliver_id = confirmDelivery(storage_no,po_no);
					Log.info("String deliver_id:　"+deliver_id);
					if(delivery_no.equals(deliver_id)){
						//出库成功后，备份it_upnote表数据
						backupDataSingle(tid,conn);
						}
					}
				
			}
		}
	}
	
	// (未调用过)
	private String getCompanyID(String companycode) throws Exception
	{
		String companyid="";
		Object[] cys=StringUtil.split(Params.company, ";").toArray();
		for(int i=0;i<cys.length;i++)
		{
			String cy=(String) cys[i];
			
			Object[] cs=StringUtil.split(cy, ":").toArray();
			
			String ccode=(String) cs[0];
			String cid=(String) cs[1];
			
			if(ccode.equals(companycode))
			{
				companyid=cid;
				break;
			}
		}
		
		return companyid;
	}
	//发货第一步：创建出仓单
	//delivery_no  这个参数自定义的
	private CreateDeliveryResponse createDelivery(String po_no,String delivery_no,
			String warehouseName,String delivery) throws Exception{
		
		try
		{
			
			
			JitDeliveryServiceClient client = new JitDeliveryServiceClient();
			//2、设置调用参数，必须
			InvocationContext instance = InvocationContext.Factory.getInstance();
			instance.setAppKey(Params.app_key);
			instance.setAppSecret(Params.app_secret);
			instance.setAppURL(Params.url);
			Warehouse warehouser = Warehouse.findByValue(warehouse.get(warehouseName));
			
			Log.info("发送请求....");
			
			//return client.createDelivery(Integer.parseInt(Params.vendor_id), po_no, delivery_no, 
			//		warehouser, null, Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT), 
			//		null, delivery, null, null, Params.driver_tel, null, null, null);
		
		}
		catch(Exception e){ Log.info(e.getMessage());}
		return null;
	}
	
	//发货第二步：将出仓明细信息导入到出仓单中 (未调用过)
	private String importDeliveryDetail(String po_no,CreateDeliveryResponse deliveyresponse
			,String sheetid,Connection conn) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2、设置调用参数，必须
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(Params.app_key);
		instance.setAppSecret(Params.app_secret);
		instance.setAppURL(Params.url);
		return client.importDeliveryDetail(Integer.parseInt(Params.vendor_id), po_no, deliveyresponse.getStorage_no(), deliveyresponse.getDelivery_id(), getDeliveryList(conn,sheetid));
	}
	
	//第三步：确认出仓单
	private String confirmDelivery(String storage_no,String po_no) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2、设置调用参数，必须
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(Params.app_key);
		instance.setAppSecret(Params.app_secret);
		instance.setAppURL(Params.url);
		return client.confirmDelivery(Integer.parseInt(Params.vendor_id), storage_no, po_no);
	}
	
	
//////////////////////////////////////////////////////
	
	//tid格式  po_pickno
	
	//获取合单出库商品明细
	private static List<Delivery> getDeliveryList(Connection conn,String sheetid) throws Exception{
		List<Delivery> deliveryList = new ArrayList<Delivery>();
		String sql ="select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"'";		//合单后的订单号  查询到 原来的订单号
		List<String> orderList = SQLHelper.multiRowListSelect(conn, sql);
		for(int i=0;i<orderList.size();i++){
			String tid = orderList.get(i);
			
			//sql ="select outerskuid,'"+(i+1)+"' box_no,'"+tid.split("_")[1]+"' pick_no,"
			//	+"a.Num amount from ns_orderitem a with(nolock),ns_customerorder b with(nolock) where a.sheetid=b.sheetid and  b.tid='"+tid+"'";
			
			sql = "select c.CustomerSheetID tid, o.SheetID, '"+tid.split("_")[1]+"' pick_no, b.CustomBC, o.PackCode, o.Qty" + 
				  "FROM CustomerDelive c,OutPackageItem o,Barcode b where c.SheetID = o.SheetID and o.BarcodeID = b.BarcodeID and c.flag=10" +
				  "and c.CustomerSheetID = '" + tid + "'";
			Log.info("getDeliveryList() sql: "+sql);
			
			Vector<Hashtable<String,String>> itemList = SQLHelper.multiRowSelect(conn,sql);
			for(Iterator it =itemList.iterator();it.hasNext();){
				Hashtable item = (Hashtable)it.next();
				Delivery delivery = new Delivery();
				delivery.setAmount((Integer)item.get("Qty"));		//装箱数Qty
				delivery.setBarcode(item.get("CustomBC").toString());	//商品条码BarCodeID  -> 查询后得到的suk(CustomBC)
				delivery.setBox_no(item.get("PackCode").toString());		//箱码   PackCode
				delivery.setPick_no(item.get("pick_no").toString());	//拣货单号
				delivery.setVendor_type(VendorType.findByValue(Params.VendorType.equals("COMMON")?1:2));	//供应商类型： 只可传：COMMON或3PL
				deliveryList.add(delivery);
			}
			
		}
		Log.info(deliveryList.toString());
		return deliveryList;
		
	}
	
	//获取没有合单出库商品明细
	private static List<Delivery> getDeliveryListSingle(Connection conn,String tid) throws Exception{
		List<Delivery> deliveryList = new ArrayList<Delivery>();
		
			String sql = "select c.CustomerSheetID tid, o.SheetID, '"+tid.split("_")[1]+"' pick_no, b.CustomBC, o.PackCode, o.Qty" + 
			  "FROM CustomerDelive c,OutPackageItem o,Barcode b where c.SheetID = o.SheetID and o.BarcodeID = b.BarcodeID and c.flag=10" +
			  "and c.CustomerSheetID = '" + tid + "'";
			Log.info("getDeliveryListSingle() sql: "+sql);
			
			Vector<Hashtable<String,String>> itemList = SQLHelper.multiRowSelect(conn,sql);
			for(Iterator it =itemList.iterator();it.hasNext();){
				Hashtable item = (Hashtable)it.next();
				Delivery delivery = new Delivery();
				delivery.setAmount((Integer)item.get("amount"));
				delivery.setBarcode(item.get("outerskuid").toString());
				delivery.setBox_no(item.get("box_no").toString());
				delivery.setPick_no(item.get("pick_no").toString());//这里暂时定这个
				delivery.setVendor_type(VendorType.findByValue(Params.VendorType.equals("COMMON")?1:2));
				deliveryList.add(delivery);
			}
			
		Log.info(deliveryList.toString());
		return deliveryList;
		
	}
	
//////////////////////////////////////////////////////
	
	
	
	
	//发货第二步：将出仓明细信息导入到出仓单中(Single 为 false 时为合单)
	public static String importDeliveryDetailT(String po_no,CreateDeliveryResponse deliveyresponse
			,String sheetid,Connection conn, Boolean Single) throws Exception{
		
		importDeliveryDetail_args args = new importDeliveryDetail_args();
		args.setVendor_id(Integer.parseInt(Params.vendor_id));
		args.setPo_no(po_no);
		args.setStorage_no(deliveyresponse.getStorage_no());
		args.setDelivery_no(deliveyresponse.getDelivery_id());
		
		if(Single)
			args.setDelivery_list(getDeliveryListSingle(conn,sheetid));		//没有合单		传入订单号
		else
			args.setDelivery_list(getDeliveryList(conn,sheetid));		//获取合单出库商品明细	传入合单后的订单号
		
		Protocol oprot = new JSONProtocol(new MemoryBuffer(1024));
		importDeliveryDetail_argsHelper.getInstance().write(args, oprot);
        String request = ((MemoryBuffer)oprot.getTrans_()).getArr_().toString();
        Log.info("request: "+request);
        HashMap<String,String> map = new  HashMap<String,String>();
		map.put("appKey", Params.app_key);
		map.put("format", Params.format);
		map.put("method", "importDeliveryDetail");
		map.put("service", Params.service);
		map.put("timestamp", String.valueOf(System.currentTimeMillis()/1000L));
		map.put("version", Params.ver);
        String result = Utils.sendByPost(map,request,Params.url,Params.app_secret);
		Log.info("DeliveryDetail: "+result);
		// {"returnCode":"-1","returnMessage":"第1行该拣货单商品已全部导入，无可导入信息！"}
		JSONObject obj = new JSONObject(result);
		if("0".equals(obj.getString("returnCode"))){
			
		}else{
			String errMsg = obj.getString("returnMessage");
			Log.info("导入出仓单明细出错: "+errMsg);
		}
		return null;
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
