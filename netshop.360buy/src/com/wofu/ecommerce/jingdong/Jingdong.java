package com.wofu.ecommerce.jingdong;
import java.util.Date;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.ecommerce.jingdong.Params;
public class Jingdong extends Service {
	//����ȡ�����߳��Ƿ���ֹ
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//��������ڲ������߳��Ƿ���ֹ
	private static Date currentDate_genOrder=null;
	

	public static Date  getCurrentDate_genOrder() {
		return currentDate_genOrder;
	}

	public synchronized static void setCurrentDate_genOrder(Date currentDate_genOrder1) {
		currentDate_genOrder = currentDate_genOrder1;
	}
	//����ȡ�����߳��Ƿ���ֹ
	private static Date currentDate_DevOrder=null;
	

	public static Date  getCurrentDate_DevOrder() {
		return currentDate_DevOrder;
	}

	public synchronized static void setCurrentDate_DevOrder(Date currentDate_DevOrder1) {
		currentDate_DevOrder = currentDate_DevOrder1;
	}
	
	//����ȡȡ��Ʒ�߳��Ƿ���ֹ
	private static Date currentDate_getItem=null;
	

	public static Date  getCurrentDate_getItem() {
		return currentDate_getItem;
	}

	public synchronized static void setCurrentDate_getItem(Date currentDate_getItem1) {
		currentDate_getItem = currentDate_getItem1;
	}
	
	//�����¿���߳��Ƿ���ֹ
	private static Date currentDate_updatStock=null;
	

	public static Date  getCurrentDate_updatStock() {
		return currentDate_updatStock;
	}

	public synchronized static void setCurrentDate_updatStock(Date currentDate_updatStock1) {
		currentDate_updatStock = currentDate_updatStock1;
	}

	public String description() {
		return "360buy��������ϵͳ [V " + Version.version + "]";
	}

	public void end() throws Exception {

	}

	public void init(Properties prop) throws Exception {
		Params.init(prop);
		this.setParams(prop);

	}

	public void process() {

	}

	public void start() throws Exception {
		
		//��ȡ�������¶�����ȡ���Ķ������˻�����
		getOrders getorders = new getOrders();
		
		getorders.start();
		
		
		//��ȡ���������޸ĵ���Ʒ V2
		getItems getitems = new getItems() ;
		getitems.start() ;
		
		//����ϵͳ�ڲ�����
		System.out.println("���ɶ�����ҵ�Ƿ���"+Params.isgenorder);
		if(Params.isgenorder){
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		}
		

		
		//���¶���������Ϣ V2  ����ͣ
		OrderDelivery orderdelivery = new OrderDelivery() ;
		
		orderdelivery.start() ;
		
		
		//���¿�� V2
		
//		UpdateStock updatestock=new UpdateStock();
//		updatestock.start();
		
		
		//��ⷢ���߳�
		CheckDeliveryOrerThread checkDeliveryOrerThread = new CheckDeliveryOrerThread();
		checkDeliveryOrerThread.start();
		
		//��������ڲ���ʱ����
		if(Params.isgenorder){
		CheckGenOrerThread checkGenOrerThread= new CheckGenOrerThread();
		checkGenOrerThread.start();
		}
		//����ȡ�����߳�
		CheckThread checkThread= new CheckThread();
		checkThread.start();
		//���ȡ��Ʒ�߳�
		CheckGetItemThread checkGetItemThread= new CheckGetItemThread();
		checkGetItemThread.start();
		// �����¿���߳�
		CheckUpdateStockThread checkUpdateStockThread = new CheckUpdateStockThread();
		checkUpdateStockThread.start();
		
		
	}
}

