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
	
	private String vendor_type="";//��Ӧ������   COMMON����ͨ 3pl��3PL

	private String driver_tel="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="JIT��������������ҵ";
	private static HashMap<String,Integer> warehouse = new HashMap<String,Integer>();
	static {
		warehouse.put("ΨƷ����ݲ�",1);  //ΨƷ����ݲ� ΨƷ�ᱱ���� ΨƷ��ɶ��� ΨƷ���人�� --ΨƷ���Ϻ��� ΨƷ�ᱱ����
		warehouse.put("ΨƷ���Ϻ���",2);
		warehouse.put("ΨƷ��ɶ���",3);
		warehouse.put("ΨƷ�ᱱ����",4);
		warehouse.put("ΨƷ���人��",5);
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
			
			Log.info(jobName,"δ��ִ��....");
			
			UpdateTimerJob();
			
			//Log.info(jobName, "ִ����ҵ�ɹ� ["
			//		+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
			//		+ "] �´δ���ʱ��: "
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
				Log.error(jobName,"�ع�����ʧ��");
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"���´����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
	
	
	}

	
	/*
	 * ��ȡһ��֮������ж���
	 */
	private void orderDelivery() throws Exception
	{		
		for(int k=0;k<10;)
		{
			try
			{	//sheettype=881104 ΪjitҪ����Ķ���
				String sql ="select operdata from inf_downnote where sheettype = 881104";
				List<String> orderList = this.getDao().oneListSelect(sql);
				for(Iterator it =orderList.iterator();it.hasNext();){
					String sheetid = (String)it.next();
					sql ="select note,address,delivery from outstock where sheetid='"+sheetid+"'";
					Hashtable list = this.getDao().oneRowSelect(sql);
					//��ע��ʽ  po��� �ͻ������ �����Լ��������Ϣ
					String notes = list.get("note").toString();
					String address = list.get("address").toString().substring(0,6);
					String[] infos = notes.split(" ");
					String delivery = list.get("delivery").toString();
					//��һ�����������ֵ�
					CreateDeliveryResponse deliveyresponse = createDelivery(infos[0],infos[1],address,delivery);
					if(deliveyresponse!=null){
						Log.info("Storage_no: "+deliveyresponse.getStorage_no()+"Delivery_id: "+deliveyresponse.getDelivery_id());
						//��������ϸ��Ϣ���뵽���ֵ���  ����storage_no
						String storage_no =importDeliveryDetail(infos[0],deliveyresponse,sheetid);
						if(storage_no.equals(deliveyresponse.getStorage_no())){
							Log.info("result:��"+storage_no);
							//ȷ�ϳ��ⵥ
							String deliver_id = confirmDelivery(deliveyresponse.getStorage_no(),infos[0]);
							Log.info("String deliver_id:��"+deliver_id);
							if(infos[1].equals(deliver_id)){//������ϸ�ɹ������ݽӿ�����
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
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				StringWriter sw =new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				Log.info(sw.toString());
				Log.warn(jobName+", Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	//������һ�����������ֵ�
	//delivery_no  ��������Զ����
	private CreateDeliveryResponse createDelivery(String po_no,String delivery_no,
			String warehouseName,String delivery) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2�����õ��ò���������
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(app_key);
		instance.setAppSecret(app_secret);
		instance.setAppURL(url);
		Warehouse warehouser = Warehouse.findByValue(warehouse.get(warehouseName));
		return client.createDelivery(vendor_id, po_no, delivery_no, 
				warehouser, null, Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT), 
				null, delivery, null, null, driver_tel, null, null, null);
	}
	//�����ڶ�������������ϸ��Ϣ���뵽���ֵ���
	private String importDeliveryDetail(String po_no,CreateDeliveryResponse deliveyresponse
			,String sheetid) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2�����õ��ò���������
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(app_key);
		instance.setAppSecret(app_secret);
		instance.setAppURL(url);
		return client.importDeliveryDetail(vendor_id, po_no, deliveyresponse.getStorage_no(), deliveyresponse.getDelivery_id(), getDeliveryList(sheetid));
	}
	
	//��������ȷ�ϳ��ֵ�
	private String confirmDelivery(String storage_no,String po_no) throws Exception{
		JitDeliveryServiceClient client = new JitDeliveryServiceClient();
		//2�����õ��ò���������
		InvocationContext instance = InvocationContext.Factory.getInstance();
		instance.setAppKey(app_key);
		instance.setAppSecret(app_secret);
		instance.setAppURL(url);
		return client.confirmDelivery(vendor_id, storage_no, po_no);
	}
	
	//��ȡ������Ʒ��ϸ
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
			delivery.setPick_no("PICK-2000011855-15");//������ʱ�����
			delivery.setVendor_type(VendorType.findByValue(vendor_type.equals("COMMON")?1:2));
			deliveryList.add(delivery);
		}
		Log.info(deliveryList.toString());
		return deliveryList;
		
	}
	

}
