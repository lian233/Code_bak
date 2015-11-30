package com.wofu.ecommerce.suning;
import java.util.Date;
import java.util.Properties;
import com.wofu.ecommerce.suning.Params;
import com.wofu.common.service.Service;
public class SuNing extends Service {
	
	//����ȡ�����߳��Ƿ���ֹ
	private static Date currentDate_getOrder=null;
	

	public static Date  getCurrentDate_getOrder() {
		return currentDate_getOrder;
	}

	public synchronized static void setCurrentDate_getOrder(Date currentDate_getOrder1) {
		currentDate_getOrder = currentDate_getOrder1;
	}
	
	//����ȡ�˻������߳��Ƿ���ֹ
	private static Date currentDate_getRefundOrder=null;
	

	public static Date  getCurrentDate_getRefundOrder() {
		return currentDate_getRefundOrder;
	}

	public synchronized static void setCurrentDate_getRefundOrder(Date currentDate_RefundgetOrder1) {
		currentDate_getRefundOrder = currentDate_RefundgetOrder1;
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
		return "������������ϵͳ [V " + Version.version + "]";
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
		//----��ȡ�������¶�����
		GetOrders getorders = new GetOrders();
		getorders.start();
		
		//----��ȡ���� �ϼ���Ʒ
		//getItems getitems = new getItems() ;
		//getitems.start() ;
		
		//---��ȡ���� �˻�����
		GetRefundOrders getRefundOrders = new GetRefundOrders() ;
		getRefundOrders.start() ;
		System.out.println("�Ƿ����ɶ���:"+Params.isgenorder);
		//----����ϵͳ�ڲ�����
		if(Params.isgenorder){
			GenCustomerOrder gencustomerorder = new GenCustomerOrder();
			gencustomerorder.start();
		}
		
		//---���¶���������Ϣ
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start() ;
		
		//--���¿��  yykͣ
		
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
		//����ȡ�˻������߳�
		CheckgetRefundOrerThread checkgetRefundOrerThread= new CheckgetRefundOrerThread();
		checkgetRefundOrerThread.start();
		
	}
}

