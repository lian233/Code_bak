package com.wofu.ecommerce.huasheng;
import java.util.Date;
import java.util.Properties;
import com.wofu.common.service.Service;
import com.wofu.ecommerce.huasheng.CheckDeliveryOrderThread;
import com.wofu.ecommerce.huasheng.GenCustomerOrder;
import com.wofu.ecommerce.huasheng.OrderDelivery;
import com.wofu.ecommerce.huasheng.Params;
import com.wofu.ecommerce.huasheng.Version;

public class huasheng extends Service {
	//��ⷢ���߳��Ƿ���ֹ
	private static Date currentDate_orderDelivery=null;
	public static Date getCurrentDate_orderDelivery() { return currentDate_orderDelivery; }
	public synchronized static void setCurrentDate_orderDelivery(Date currentDate_orderDelivery1) {
		currentDate_orderDelivery = currentDate_orderDelivery1;
	}
	
	@Override
	public String description() {
		return "�羳�̳�(����API)��������ϵͳ [V " + Version.version + "]";
	}

	@Override
	public void end() throws Exception {
		
	}

	@Override
	public void init(Properties prop) throws Exception {
		Params.init(prop);
		this.setParams(prop);
	}

	@Override
	public void process() {

	}

	@Override
	public void start() throws Exception {
		//----����ϵͳ�ڲ�����
		GenCustomerOrder gencustomerorder = new GenCustomerOrder();
		gencustomerorder.start();
		
		//---��������
		OrderDelivery orderdelivery = new OrderDelivery() ;
		orderdelivery.start();
		
		//ͬ�����
//		UpdateStock updatestock=new UpdateStock();
//		updatestock.start();
		
		//����߳�
		CheckDeliveryOrderThread checkDeliveryOrderThread= new CheckDeliveryOrderThread();
		checkDeliveryOrderThread.start();
	}
}
