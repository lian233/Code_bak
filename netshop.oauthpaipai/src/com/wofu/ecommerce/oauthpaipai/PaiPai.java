package com.wofu.ecommerce.oauthpaipai;
import java.util.Date;
import java.util.Properties;
import com.wofu.ecommerce.oauthpaipai.Params;
import com.wofu.ecommerce.oauthpaipai.Version;
import com.wofu.ecommerce.oauthpaipai.getOrders;
import com.wofu.common.service.Service;

public class PaiPai extends Service {
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


	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "���Ķ�������ϵͳ [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(Properties prop) throws Exception {
		// TODO Auto-generated method stub
		Params.init(prop);
		this.setParams(prop);

	}

	@Override
	public void process() {
		// TODO Auto-generated method stub

	}
	


	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		getOrders getorders = new getOrders();
		getorders.start();
		
		OrderDelivery orderdelivery=new OrderDelivery();
		orderdelivery.start();
		if(Params.isgenorder){
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		}
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
		
		UpdateStock updatestock = new UpdateStock();
		updatestock.start();
		
		getItems getitems = new getItems();
		getitems.start();
		
		
	}

}
