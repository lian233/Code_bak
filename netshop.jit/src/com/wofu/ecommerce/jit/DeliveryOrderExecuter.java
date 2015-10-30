package com.wofu.ecommerce.jit;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import vipapis.common.VendorType;
import vipapis.common.Warehouse;
import vipapis.delivery.CreateDeliveryResponse;
import vipapis.delivery.Delivery;
import vipapis.delivery.JitDeliveryServiceHelper.JitDeliveryServiceClient;
import com.vip.osp.sdk.context.InvocationContext;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.job.Executer;
public class DeliveryOrderExecuter extends Executer {

	private String url="";

	private int vendor_id ;

	private String app_key  = "";
	
	private String app_secret="";
	
	private String vendor_type="";//供应商类型   COMMON：普通 3pl：3PL

	private String driver_tel="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="JIT订单发货处理作业";
	private static HashMap<String,Integer> warehouse = new HashMap<String,Integer>();
	static {
		warehouse.put("唯品会广州仓",1);  //唯品会广州仓 唯品会北京仓 唯品会成都仓 唯品会武汉仓 --唯品会上海仓 唯品会北京仓
		warehouse.put("唯品会上海仓",2);
		warehouse.put("唯品会成都仓",3);
		warehouse.put("唯品会北京仓",4);
		warehouse.put("唯品会武汉仓",5);
	}

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		vendor_type=prop.getProperty("vendor_type");
		driver_tel=prop.getProperty("driver_tel");

		vendor_id=Integer.parseInt(prop.getProperty("vendor_id"));
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");

		try {		
			
			updateJobFlag(1);
	
			//orderDelivery();
			
			Log.info(jobName,"未能执行....");
			
			UpdateTimerJob();
			
			//Log.info(jobName, "执行作业成功 ["
			//		+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
			//		+ "] 下次处理时间: "
			//		+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"回滚事务失败");
			}
			Log.error(jobName,"错误信息:"+Log.getErrorMessage(e));
			
			
			Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"更新处理标志失败");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}
		
	
	
	}

	
	/*
	 * 获取一天之类的所有订单
	 */
	private void orderDelivery() throws Exception
	{		
		for(int k=0;k<10;)
		{
			try
			{	//sheettype=881104 为jit要出库的订单
				String sql ="select operdata from inf_downnote where sheettype = 881104";
				List<String> orderList = this.getDao().oneListSelect(sql);
				for(Iterator it =orderList.iterator();it.hasNext();){
					String sheetid = (String)it.next();
					sql ="select note,address,delivery from outstock where sheetid='"+sheetid+"'";
					Hashtable list = this.getDao().oneRowSelect(sql);
					//备注格式  po编号 送货单编号 你们自己定义的信息
					String notes = list.get("note").toString();
					String address = list.get("address").toString().substring(0,6);
					String[] infos = notes.split(" ");
					String delivery = list.get("delivery").toString();
					//第一步：创建出仓单
					CreateDeliveryResponse deliveyresponse = createDelivery(infos[0],infos[1],address,delivery);
					if(deliveyresponse!=null){
						Log.info("Storage_no: "+deliveyresponse.getStorage_no()+"Delivery_id: "+deliveyresponse.getDelivery_id());
						//将出仓明细信息导入到出仓单中  返回storage_no
						String storage_no =importDeliveryDetail(infos[0],deliveyresponse,sheetid);
						if(storage_no.equals(deliveyresponse.getStorage_no())){
							Log.info("result:　"+storage_no);
							//确认出库单
							String deliver_id = confirmDelivery(deliveyresponse.getStorage_no(),infos[0]);
							Log.info("String deliver_id:　"+deliver_id);
							if(infos[1].equals(deliver_id)){//导入明细成功，备份接口数据
								DataCentre dao=null;
								try{
									dao = this.getDao();
									dao.setTransation(false);
									sql = "insert into inf_downnotebak(SheetType,NoteTime,HandleTime,OperType,OperData," +
											"Flag) select 881104, NoteTime,getdate(),OperType,OperData,100 from inf_downnote"
									+ " where SheetType=881104 and operdata='"+sheetid+"'";
									dao.execute(sql);
									sql = "delete inf_downnote where SheetType=881104 and operdata='"+sheetid+"'";
									dao.execute(sql);
									dao.commit();
								}catch(Exception e){
									if(dao!=null)
									dao.rollback();
								}finally{
									dao.setTransation(true);
									dao=null;
								}
							}
						}
					}
					
					
				}
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				StringWriter sw =new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				Log.info(sw.toString());
				Log.warn(jobName+", 远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	//发货第一步：创建出仓单
	//delivery_no  这个参数自定义的
	private CreateDeliveryResponse createDelivery(String po_no,String delivery_no,
			String warehouseName,String delivery) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2、设置调用参数，必须
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(app_key);
		instance.setAppSecret(app_secret);
		instance.setAppURL(url);
		Warehouse warehouser = Warehouse.findByValue(warehouse.get(warehouseName));
		return client.createDelivery(vendor_id, po_no, delivery_no, 
				warehouser, null, Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT), 
				null, delivery, null, null, driver_tel, null, null, null);
	}
	//发货第二步：将出仓明细信息导入到出仓单中
	private String importDeliveryDetail(String po_no,CreateDeliveryResponse deliveyresponse
			,String sheetid) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2、设置调用参数，必须
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(app_key);
		instance.setAppSecret(app_secret);
		instance.setAppURL(url);
		return client.importDeliveryDetail(vendor_id, po_no, deliveyresponse.getStorage_no(), deliveyresponse.getDelivery_id(), getDeliveryList(sheetid));
	}
	
	//第三步：确认出仓单
	private String confirmDelivery(String storage_no,String po_no) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2、设置调用参数，必须
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(app_key);
		instance.setAppSecret(app_secret);
		instance.setAppURL(url);
		return client.confirmDelivery(vendor_id, storage_no, po_no);
	}
	
	//获取出库商品明细
	private List<Delivery> getDeliveryList(String sheetid) throws Exception{
		List<Delivery> deliveryList = new ArrayList<Delivery>();
		String sql ="select refsheetid from customerorderreflist where sheetid='"+sheetid+"'";
		sql ="select CustomBC barcode,boxid box_no,"
			+"cast(round(outqty,2) as int) amount from BoxUpItem a,Barcode b, boxup c where "+
			"c.refSheetID='"+sheetid+"' and a.sheetid=c.sheetid and a.BarCodeID=b.BarcodeID";
		Vector<Hashtable<String,String>> itemList = this.getDao().multiRowSelect(sql);
		for(Iterator it =itemList.iterator();it.hasNext();){
			Hashtable item = (Hashtable)it.next();
			Delivery delivery = new Delivery();
			delivery.setAmount((Integer)item.get("amount"));
			delivery.setBarcode(item.get("barcode").toString());
			delivery.setBox_no(item.get("box_no").toString());
			delivery.setPick_no("PICK-2000011855-15");//这里暂时定这个
			delivery.setVendor_type(VendorType.findByValue(vendor_type.equals("COMMON")?1:2));
			deliveryList.add(delivery);
		}
		Log.info(deliveryList.toString());
		return deliveryList;
		
	}
	

}
