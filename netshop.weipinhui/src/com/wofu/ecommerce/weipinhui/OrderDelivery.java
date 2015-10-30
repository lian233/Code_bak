package com.wofu.ecommerce.weipinhui;

import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.util.CommHelper;
/**
 * 唯品会发货流程
 * 1:订单导出
 * 2:发货
 * @author Administrator
 *
 */
public class OrderDelivery extends Thread {

	private static String jobname = "唯品会订单发货处理作业";
	private static String tradecontactid=Params.tradecontactid ;
	private boolean is_exporting = false;
	
	//执行操作
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//符合或超过指定的启动时间
				Connection connection = null;
				is_exporting = true;
				try {		
					connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.weipinhui.Params.dbname);
					//记录当前执行时间(用于判断是否线程假死)
					WeipinHui.setCurrentDate_orderDelivery(new Date());
					//查询发货单信息后订单发货
					doDelivery(connection,getDeliveryOrders(connection,3));	
					//修改物流信息
					editDeliveryInfo(connection,getDeliveryOrders(connection,4));	
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
				Log.info(jobname + "下次执行等待时间:" + Params.waittime + "秒");
				long startwaittime = System.currentTimeMillis();
				while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.weipinhui.Params.waittime * 1000))
					try {
						sleep(1000L);
					} catch (Exception e) {
						Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
					}
				//更新一次配置参数(从数据库中读取)
				Params.UpdateSettingFromDB(null);
			}
			else
			{//等待启动
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
			}
		} while (true);
	}
	
	///////////////////////////////////////////////////////////
	/**
	 * 查询发货单信息(在表it_upnote  sheettype=3,ns_delivery,deliveryref表中查询出来要发货的订单)
	 * @param conn 数据库连接
	 * @param sheettype 发货类型，3为发货，4为修改物流信息
	 * @return 发货单信息表
	 */
	private Vector<Hashtable> getDeliveryOrders(Connection conn,int sheettype)
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			sql = "select a.notetime,a.sheetid,b.tid, b.companycode,b.outsid,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
				+ "where a.sheettype = " + sheettype + " and a.sheetid=b.sheetid and a.receiver='"
				+ tradecontactid + "' and b.companycode=c.companycode";
			
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int k=0; k<vt.size();k++)
			{	
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(k);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString().trim().replaceAll("[?]", ""));
				ht.put("express_code", hto.get("companycode").toString().trim());
				ht.put("transport_no", hto.get("outsid").toString().trim());     //快递单号
				ht.put("sheettype", String.valueOf(hto.get("sheettype")));     //发货类型，3为发货，4为修改物流信息
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobname, "查询发货单信息出错:"+sqle.getMessage());
		}
		catch(Exception e)
		{
			Log.error(jobname, "查询发货单信息出错:"+e.getMessage());
			//e.printStackTrace() ;
		}
		return vtorders;
	}
	
	/**
	 * 订单发货(IT_UpNote -> IT_UpNoteBak)
	 * @param conn 数据库连接
	 * @param vdeliveryorder 发货单信息表
	 * @throws Exception
	 */
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		String sql = "" ;
		Log.info("待发货总数为:　"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			//当前发货的信息
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();		//系统内容部订单号
			String orderID = hto.get("orderid").toString();		//订单号
			String postCompany = hto.get("express_code").toString();		//快递公司编号
			String postNo = hto.get("transport_no").toString();		//快递单号
			String sheetType = hto.get("sheettype").toString();		//发货类型，3为发货，4为修改物流信息
			try 
			{
				//更新发货状态
				boolean	success = delivery(jobname, conn, hto) ;
				Log.info("发货成功状态："+success);
				if(success)
				{
					conn.setAutoCommit(false);
	
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
	
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
					SQLHelper.executeSQL(conn, sql);
					
					conn.commit();
					conn.setAutoCommit(true);	
				}
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				Log.info("【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			}
		}
	}
	
	/**
	 * 查询所有唯品会承运商名称
	 * @param WEIPINHUI_DeliveryCompanyCode 唯品会承运商Code
	 * @return 承运商名称
	 */
	private static String GetDeliveryCompanyName(String WEIPINHUI_DeliveryCompanyCode)
	{
		String returnname = "";
		if(!WEIPINHUI_DeliveryCompanyCode.equals(""))
		{
			//当还没初始化快递公司列表的时候初始化
			if(Params.DeliveryCompanyJsonData.equals(""))
			{
				Log.info("开始初始化承运商列表数据...");
				JSONArray carriersList = new JSONArray();
				int pageIndex = 1;
				boolean hasNextPage = true;
				while(hasNextPage)
				{
					try {
						//填充参数
						JSONObject jsonobj = new JSONObject();
						jsonobj.put("page", pageIndex);
						jsonobj.put("limit", 100);
						jsonobj.put("vendor_id", Params.vendor_id);
						//发送请求
						String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getCarrierList", jsonobj.toString());
						//判断请求结果
						String returnCode = new JSONObject(responseText).getString("returnCode");
						if(!returnCode.equals("0"))
							break;
						//页数分析
						int orderNum= new JSONObject(responseText).getJSONObject("result").getInt("total");
						int pageTotal=0;
						if(orderNum!=0){
							pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
						}
						//获取数据
						JSONArray carriers = new JSONObject(responseText).getJSONObject("result").getJSONArray("carriers");
						//查找快递公司名称
						for(int i=0;i<carriers.length();i++)
						{
							JSONObject carrier = carriers.getJSONObject(i);
							carriersList.put(carrier);
						}
						//判断是否有下一页
						if(pageIndex >= pageTotal)
							hasNextPage = false ;
						else
							pageIndex ++ ;
					} catch (JSONException e) {
						Log.error(jobname, "获取承运商列表出错!");
						returnname = "";
					}
				}
				
				if(carriersList.length() > 0)
				{
					Params.DeliveryCompanyJsonData = carriersList.toString();
					Log.info("初始化承运商列表数据完毕!");
				}
				else
					Log.info("初始化承运商列表数据失败!");
			}
			//已经有承运商数据则开始从内存中查询
			if(!Params.DeliveryCompanyJsonData.equals(""))
			{
				try {
					JSONArray carriersList = new JSONArray(Params.DeliveryCompanyJsonData);
					for(int i=0;i<carriersList.length();i++)
					{
						JSONObject carrier = carriersList.getJSONObject(i);
						String carrierCode = carrier.getString("carrier_code");
						String carrierName = carrier.getString("carrier_name");
						if(WEIPINHUI_DeliveryCompanyCode.equals(carrierCode))
						{
							//Log.info("找到快递公司名:" + carrierName + ",快递公司编号:" + carrierCode);
							returnname = carrierName;
							break;
						}
					}
				} catch (JSONException e) {
					Log.error(jobname, "查询承运商名称出错!");
					returnname = "";
				}
			}
		}
		return returnname;
	}
	
	/**
	 * 自发货订单--更新发货状态
	 * @param jobname
	 * @param conn
	 * @param hto 当前发货的信息
	 * @return 是否成功
	 * @throws Exception
	 */
	private static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto) throws Exception
	{
		boolean flag = false ;
		//订单号
		String orderCode = hto.get("orderid").toString();
		//快递公司
		String CompanyCode = hto.get("express_code").toString();
		String postCompanyCode = Params.htPostCompany.get(hto.get("express_code").toString());	//从快递代码 转为 唯品会编码(如:SF -> 1800000604)
		String postCompanyName = GetDeliveryCompanyName(postCompanyCode);		//查询唯品会快递编号的对应快递公司名(1800000604 -> 顺丰快递)
		if(postCompanyName.equals(""))
		{
			Log.warn("查询不到指定的唯品会承运公司Code:" + postCompanyCode + "的对应承运公司名称!");
			return false;
		}
		
		//运单号
		String postNo = hto.get("transport_no").toString();
		//导出订单
		if(!exportOrder(orderCode)){
			Log.error("唯品会订单导出错误,订单号: ", orderCode);
			return false;
		}
		
		JSONObject jsonobj = new JSONObject();
		JSONObject Ship = new JSONObject(); 
		//准备要发出的数据
		Ship.put("order_id", orderCode);
		Ship.put("carrier_code", postCompanyCode);
		Ship.put("carrier_name", postCompanyName);
		
		//查询客户订单表
		String sql = "select count(*) from customerorder where flag=100 and refsheetid='"+orderCode+"'";
		//String sql = "select count(*) from customerorder0 where flag=100 and refsheetid='"+orderCode+"'";	//测试用.
		int counterA = SQLHelper.intSelect(conn, sql);
		if(counterA==0){	//在客户订单表上查询不到该订单怎去合单表上查询
			//查询合单表
			sql = "select count(1) from CustomerOrderRefList where refsheetid='"+orderCode+"'";
			int counterB = SQLHelper.intSelect(conn, sql);
			if(counterB==0){
				Log.info("订单号: 【"+orderCode+"】,状态异常，不作发货处理");
				return true;
			}else{    //合单发货
				Ship.put("package_type", 1);
				Ship.put("packages", getPackages(orderCode,conn,postNo));
			}
		}
		else if(counterA==1){	//没有拆分订单
			Ship.put("package_type", 1);
			Ship.put("packages", getPackages(orderCode,conn,postNo));
		}else{  //拆分订单  customerorder表  同一个订单号有有2个以上的记录
			sql = "select refsheetid,deliverysheetid from customerorder where flag=100 and refsheetid='"+orderCode+"'";		//取出customerorder同一个订单号的不同快递单号
			Vector customorders = SQLHelper.multiRowSelect(conn, sql);
			sql = "select tid,outsid from ns_delivery where tid='"+orderCode+"'";		//查询ns_delivery同一个订单号的多个快递单号
			Vector deliverys = SQLHelper.multiRowSelect(conn, sql);
			boolean isFold = false;
			if(customorders.size() != deliverys.size())		//两个记录数必须一样
				isFold=false;
			else{
				//customerorder表
				for(int i=0;i<customorders.size();i++){	
					isFold = false;
					Hashtable t = (Hashtable)customorders.get(i);
					String refsheetid = t.get("refsheetid").toString();		//订单号
					String deliverysheetid = t.get("deliverysheetid").toString();		//运单号
					//ns_delivery表
					for(int j=0;j<deliverys.size();j++){
						Hashtable d = (Hashtable)deliverys.get(i);
						String tid = d.get("tid").toString();	//订单号
						String outsid = d.get("outsid").toString();		//运单号
						if(refsheetid.equals(tid) && deliverysheetid.equals(outsid)){
							isFold=true;
							break;
						}
					}
				}
			}
			if(isFold)	{
				Ship.put("package_type", 2);
				Ship.put("packages", getPackagesCf(conn ,customorders));
			}else{
				Log.info("订单号: 【"+orderCode+"】  拆单分货还没有全部发货，下一次再发货！");
				return false;
			}
		}
		jsonobj.put("vendor_id",Params.vendor_id);
		jsonobj.put("ship_list", new JSONArray().put(Ship));
		//发送请求
		try 
		{	
			Log.info("发送发货请求 ...");
			String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "ship", jsonobj.toString());
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(responseText);
			//判断请求结果
			String returnCode = new JSONObject(responseText).getString("returnCode");
			if(!returnCode.equals("0"))
				return false;
			int fail_num= responseObj.getJSONObject("result").getInt("fail_num");
			int successNum= responseObj.getJSONObject("result").getInt("success_num");
			//发货成功的订单
			if(successNum>0){
				flag=true;
				Log.info("发货成功，唯品会单号【" + orderCode + "】，快递公司【" + postCompanyName + "】，快递单号【" + postNo + "】") ;
			}
			else if(fail_num > 0)
				throw new Exception("接口返回发货失败");
		} catch (Exception e) {
			Log.info("发货失败，唯品会单号【" + orderCode + "】，快递公司【" + postCompanyName + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			flag=false ;
		}
		return flag ;
	}
	
	/**
	 * 供应商根据订单号码修改订单导出状态
	 * @param order_sn 订单号
	 * @return 导出状态
	 * @throws Exception
	 */
	private static Boolean exportOrder(String order_id) throws Exception{
		//定义成功状态
		Boolean isExport=false;
		try {
			//填充参数
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("order_id", order_id);
			jsonobj.put("vendor_id", Params.vendor_id);
			//发送请求
			String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "exportOrderById", jsonobj.toString());
			//Log.info("导出订单返回结果: "+responseText);
			if(new JSONObject(responseText).getJSONObject("result").getInt("success_num")==1)
				isExport=true;
		} catch (JSONException e) {
			isExport=false;
		}
		return isExport;
	}
	
	/**
	 * 获取指定订单的包裹信息(没有拆分订单的包裹)
	 * @param order_sn 订单号
	 * @param conn 数据库连接
	 * @param transport_no 运单号
	 * @return JSONArray
	 * @throws Exception
	 */
	private static JSONArray getPackages(String order_sn,Connection conn,String transport_no) throws Exception{
		String sql = "select distinct a.outerskuid,a.num from ns_orderitem a,ns_customerorder b  where a.sheetid=b.sheetid and b.tid='"+order_sn+"'";
		Vector result= SQLHelper.multiRowSelect(conn, sql);
		
		JSONArray packages = new JSONArray();
		JSONObject Package = new JSONObject();
		JSONArray package_product_list = new JSONArray();
		
		/////Package/////
		//package_product_list(arr)
		for(int i=0;i<result.size();i++){
			//PackageProduct(obj)
			JSONObject PackageProduct = new JSONObject();
			Hashtable temp= (Hashtable)result.get(i);
			PackageProduct.put("barcode", (String)temp.get("outerskuid"));
			PackageProduct.put("amount", Integer.parseInt(temp.get("num").toString()));
			package_product_list.put(PackageProduct);
		}
		//transport_no
		Package.put("package_product_list",package_product_list);
		Package.put("transport_no",transport_no);
		
		/////packages/////
		packages.put(Package);
		return packages;
	}
	
	/**
	 * 获取指定订单的包裹信息(拆分订单)
	 * @param conn
	 * @param ve customorders
	 * @return
	 * @throws Exception
	 */
	private static JSONArray getPackagesCf(Connection conn,Vector<Hashtable> ve) throws Exception{
		JSONArray packages = new JSONArray();
		for(int i=0;i<ve.size();i++){
			Hashtable table = (Hashtable)ve.get(i);
			String order_sn=table.get("refsheetid").toString();		//订单号
			String transport_no=table.get("deliverysheetid").toString();		//快递单号
			String sql = "select a.outerskuid,a.PurQty from customerorderitem a,customerorder b  where a.sheetid=b.sheetid and b.refsheetid='"+order_sn+"' and b.deliverysheetid='"+transport_no+"'";
			Vector result= SQLHelper.multiRowSelect(conn, sql);
			JSONObject local= new JSONObject();
			JSONArray package_product_list = new JSONArray();
			for(int j=0;j<result.size();j++){
				Hashtable temp= (Hashtable)result.get(j);
				String outerskuid=temp.get("outerskuid").toString();
				if("".equals(outerskuid)) {
					continue;
				}
				JSONObject obj = new JSONObject();
				obj.put("barcode", outerskuid);
				obj.put("amount", Integer.parseInt(temp.get("PurQty").toString()));
				package_product_list.put(obj);
			}
			local.put("package_product_list",package_product_list);
			local.put("transport_no",transport_no);
			packages.put(local);
		}
		Log.info("折单分货数据："+packages.toString());
		return packages;
		
	}
	
	///////////////////////////////////////////////////////////////
	
	/**
	 * 修改物流信息
	 * @return
	 * @throws Exception
	 */
	private static void editDeliveryInfo(Connection conn,Vector<Hashtable> hto) throws Exception
	{
		String sql = "" ;
		Log.info("修改物流发货信息总数为:"+hto.size());
		for (int i = 0; i < hto.size(); i++) 
		{
			Hashtable tab = (Hashtable) hto.get(i);
			String sheetid = tab.get("sheetid").toString();
			String orderID = tab.get("orderid").toString();
			String postCompany = tab.get("express_code").toString();
			String postNo = tab.get("transport_no").toString();
			try{
				boolean	success = editDeliveryInfoOne(tab,conn) ;
				Log.info("修改物流发货信息状态：" + success + " orderID:" + orderID + " sheetid:" + sheetid);
				if(success)
				{
					conn.setAutoCommit(false);
	
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
					SQLHelper.executeSQL(conn, sql);
	
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";
					SQLHelper.executeSQL(conn, sql);
					
					conn.commit();
					conn.setAutoCommit(true);	
				}
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				//e.printStackTrace() ;
				Log.info("修改物流信息失败，唯品会单号【" + orderID + "】，快递公司【" + postCompany + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			}
		}
	}
	
	/**
	 * 修改单个的物流信息
	 * @param jobname
	 * @param hto
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private static boolean editDeliveryInfoOne(Hashtable hto,Connection conn) throws Exception
	{
		boolean flag = false ;
		//订单号
		String orderCode = hto.get("orderid").toString();
		//快递公司
		String CompanyCode = hto.get("express_code").toString();
		String postCompanyCode = Params.htPostCompany.get(hto.get("express_code").toString());	//从快递代码 转为 唯品会编码(如:SF -> 1800000604)
		String postCompanyName = GetDeliveryCompanyName(postCompanyCode);		//查询唯品会快递编号的对应快递公司名(1800000604 -> 顺丰快递)
		if(postCompanyName.equals(""))
		{
			Log.warn("查询不到指定的唯品会承运公司Code:" + postCompanyCode + "的对应承运公司名称!");
			return false;
		}

		String sql ="select count(*) from customerorder where flag=100 and refsheetid='"+orderCode+"'";
		if(SQLHelper.intSelect(conn, sql)>1) {
			sql = "select count(1) from it_upnote a,ns_delivery b where a.sheetid=b.sheetid and b.tid='"+orderCode+"'";
			int count = SQLHelper.intSelect(conn, sql);
			if(count==1){   //已经发货
				Log.info("订单号:【"+orderCode+"】 作了拆单发货，此修改物流信息操作忽略!");
				return true;   //拆单的不用修改物流信息
			}else{         //没有发货
				Log.info("订单号:【"+orderCode+"】 作了拆单发货，此修改物流信息操作忽略!");
				return false;   //拆单的不用修改物流信息
			}
			
		}
		JSONObject jsonobj = new JSONObject();
		JSONObject Ship = new JSONObject(); 
		//订单号
		Ship.put("order_id", orderCode);
		//快递公司
		Ship.put("carrier_code", postCompanyCode);
		Ship.put("carrier_name", postCompanyName);
		//运单号
		String postNo = hto.get("transport_no").toString();
		//包裹
		Ship.put("package_type", 1);
		Ship.put("packages", getPackages(orderCode,conn,postNo));
		//发货单和供应商
		jsonobj.put("vendor_id",Params.vendor_id);
		jsonobj.put("ship_list", new JSONArray().put(Ship));
		//发送请求
		try 
		{	
			Log.info("发送修改订单的配送信息请求 ...");
			String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "editShipInfo", jsonobj.toString());
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(responseText);
			//判断请求结果
			String returnCode = new JSONObject(responseText).getString("returnCode");
			if(!returnCode.equals("0"))
				return false;
			int fail_num= responseObj.getJSONObject("result").getInt("fail_num");
			int successNum= responseObj.getJSONObject("result").getInt("success_num");
			if(successNum>0){
				flag=true;
				Log.info("订单修改物流信息成功，唯品会单号【" + orderCode + "】，快递公司【" + postCompanyName + "】，快递单号【" + postNo + "】") ;
			}
			else if(fail_num > 0)
				throw new Exception("接口返回修改物流信息失败");
			
		} catch (Exception e) {
			Log.warn("订单修改物流信息失败，唯品会单号【" + orderCode + "】，快递公司【" + postCompanyName + "】，快递单号【" + postNo + "】。错误信息：" + e.getMessage()) ;
			flag=false ;
		}
		return flag ;
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
