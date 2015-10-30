
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.LogisticsService;
import com.taobao.api.domain.PackageItem;
import com.taobao.api.domain.PrintCheckInfo;
import com.taobao.api.domain.TradeOrderInfo;
import com.taobao.api.domain.WaybillAddress;
import com.taobao.api.domain.WaybillApplyCancelRequest;
import com.taobao.api.domain.WaybillApplyFullUpdateRequest;
import com.taobao.api.domain.WaybillApplyNewInfo;
import com.taobao.api.domain.WaybillApplyNewRequest;
import com.taobao.api.domain.WaybillApplyPrintCheckRequest;
import com.taobao.api.domain.WaybillApplyRequest;
import com.taobao.api.domain.WaybillDetailQueryRequest;
import com.taobao.api.domain.WaybillProductType;
import com.taobao.api.domain.WaybillProductTypeRequest;
import com.taobao.api.domain.WaybillServiceType;
import com.taobao.api.request.WlbWaybillICancelRequest;
import com.taobao.api.request.WlbWaybillIFullupdateRequest;
import com.taobao.api.request.WlbWaybillIGetRequest;
import com.taobao.api.request.WlbWaybillIPrintRequest;
import com.taobao.api.request.WlbWaybillIProductRequest;
import com.taobao.api.request.WlbWaybillIQuerydetailRequest;
import com.taobao.api.request.WlbWaybillISearchRequest;
import com.taobao.api.response.WlbWaybillICancelResponse;
import com.taobao.api.response.WlbWaybillIFullupdateResponse;
import com.taobao.api.response.WlbWaybillIGetResponse;
import com.taobao.api.response.WlbWaybillIPrintResponse;
import com.taobao.api.response.WlbWaybillIProductResponse;
import com.taobao.api.response.WlbWaybillIQuerydetailResponse;
import com.taobao.api.response.WlbWaybillISearchResponse;

/**
 * ��������
 * @author Administrator
 *
 */
public class Main {

	/**
	 * @param args
	 * "cp_code": "POSTB",
    "shipping_address": {
        "address_detail": "��һ��·969��",
        "area": "�ຼ��",
        "city": "������",
        "province": "�㽭ʡ",
        "town": "��ǰ�ֵ�"
    }, 
	
	 */
	private static String url="http://gw.api.tbsandbox.com/router/rest";
	private static String appkey="1021520535";
	private static String secret="sandbox7fd8ac852ea02a740277f1289";
	private static String sessionKey="6100e17f8680b4ed20716437b1162e49b5d13c0a0ba9e052054718218";
	private static String jsonValue="{\"service_value4_json\":\"{\"value\":\"100.00\",\"currency\":\"CNY\",\"payment_type\":\"CASH\"},\"service_code\":\"SVC-COD\"}";
	public static void main(String[] args) throws Exception {
		
		getWayBill();//��ȡ�����浥
		cancel();//ȡ�������浥
		search();
		searchDetail();
		getServiceType();
		modifiedWayBill();
		printInfo();
	}
	
	//��ȡ����浥
	/**
	 * ֧��������ȡ��ÿ������ȡ 10 ���浥��  
	 */
	private static void getWayBill() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIGetRequest req=new WlbWaybillIGetRequest();
		WaybillApplyNewRequest waybill_apply_new_request = new WaybillApplyNewRequest();
		WaybillAddress address = new WaybillAddress();//��ݵ�ַ��Ϣ
		address.setAddressDetail("��һ��·969��");
		address.setAddressFormat("json");
		address.setArea("�ຼ��");//��
		//address.setAreaCode(areaCode)
		address.setCity("������");
		address.setProvince("�㽭ʡ");
		TradeOrderInfo tradeOrderInfo = new TradeOrderInfo();
		tradeOrderInfo.setConsigneeName("����");//
		tradeOrderInfo.setConsigneePhone("13712454578");
		tradeOrderInfo.setConsigneeAddress(address);
		tradeOrderInfo.setOrderChannelsType("TB");
		//���׶����б�
		List<String> tradeOrderList = new ArrayList<String>();
		tradeOrderList.add("193066509162111");
		tradeOrderInfo.setTradeOrderList(tradeOrderList);
		//������Ʒ�б�  �����������Ʒ����
		PackageItem item = new PackageItem();
		item.setCount(1L);
		item.setItemName("ɳ�����-�ǰͿ���100������˱�һ��");
		List<PackageItem> packageList = new ArrayList<PackageItem>();
		packageList.add(item);
		tradeOrderInfo.setPackageItems(packageList);
		tradeOrderInfo.setProductType("STANDARD_EXPRESS");//��ݷ����Ʒ���ͱ���
		tradeOrderInfo.setRealUserId(89346737L);//�浥ʹ����id
		
		waybill_apply_new_request.setShippingAddress(address);
		//waybill_apply_new_request.setCpId(123L);
		//waybill_apply_new_request.setSellerId(89346737L);
		waybill_apply_new_request.setCpCode("POSTB");//CP ��ݹ�˾����
		waybill_apply_new_request.setRealUserId(89346737L);
		waybill_apply_new_request.setAppKey(appkey);
		waybill_apply_new_request.setTradeOrderInfoCols(tradeOrderInfo);//��Ӧ���ݽṹʾ��JSON
		req.setWaybillApplyNewRequest(waybill_apply_new_request);
		WlbWaybillIGetResponse response = client.execute(req , sessionKey);
		System.out.println(req.getWaybillApplyNewRequest());
		System.out.println(response.getBody());
		List<WaybillApplyNewInfo> waybillApplyNewInfo =response.getWaybillApplyNewCols();
		Iterator it = waybillApplyNewInfo.iterator();
		for(;it.hasNext();){
			WaybillApplyNewInfo temp = (WaybillApplyNewInfo)it.next();
			System.out.println("��ͷ����Ϣ: "+temp.getShortAddress()+"�浥��: "+temp.getWaybillCode()+"Ŀ�ĵر���: "+temp.getPackageCenterCode());
		}
		
	}
	
	private static void getWayBill1() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIGetRequest req=new WlbWaybillIGetRequest();
		WaybillApplyNewRequest waybill_apply_new_request = new WaybillApplyNewRequest();
		WaybillAddress address = new WaybillAddress();//��ݵ�ַ��Ϣ
		address.setAddressDetail("��һ��·969��");
		address.setAddressFormat("json");
		address.setArea("�ຼ��");//��
		//address.setAreaCode(areaCode)
		address.setCity("������");
		address.setProvince("�㽭ʡ");
		TradeOrderInfo tradeOrderInfo = new TradeOrderInfo();
		tradeOrderInfo.setConsigneeName("����");//
		tradeOrderInfo.setConsigneePhone("13712454578");
		tradeOrderInfo.setConsigneeAddress(address);
		tradeOrderInfo.setOrderChannelsType("TB");
		//���׶����б�
		List<String> tradeOrderList = new ArrayList<String>();
		tradeOrderList.add("193066509162111");
		tradeOrderInfo.setTradeOrderList(tradeOrderList);
		//������Ʒ�б�  �����������Ʒ����
		PackageItem item = new PackageItem();
		item.setCount(1L);
		item.setItemName("ɳ�����-�ǰͿ���100������˱�һ��");
		List<PackageItem> packageList = new ArrayList<PackageItem>();
		packageList.add(item);
		tradeOrderInfo.setPackageItems(packageList);
		tradeOrderInfo.setProductType("STANDARD_EXPRESS");//��ݷ����Ʒ���ͱ���
		tradeOrderInfo.setRealUserId(89346737L);//�浥ʹ����id
		
		waybill_apply_new_request.setShippingAddress(address);
		//waybill_apply_new_request.setCpId(123L);
		//waybill_apply_new_request.setSellerId(89346737L);
		waybill_apply_new_request.setCpCode("POSTB");//CP ��ݹ�˾����
		waybill_apply_new_request.setRealUserId(89346737L);
		waybill_apply_new_request.setAppKey(appkey);
		waybill_apply_new_request.setTradeOrderInfoCols(tradeOrderInfo);//��Ӧ���ݽṹʾ��JSON
		req.setWaybillApplyNewRequest(waybill_apply_new_request);
		WlbWaybillIGetResponse response = client.execute(req , sessionKey);
		System.out.println(req.getWaybillApplyNewRequest());
		System.out.println(response.getBody());
		List<WaybillApplyNewInfo> waybillApplyNewInfo =response.getWaybillApplyNewCols();
		Iterator it = waybillApplyNewInfo.iterator();
		for(;it.hasNext();){
			WaybillApplyNewInfo temp = (WaybillApplyNewInfo)it.next();
			System.out.println("��ͷ����Ϣ: "+temp.getShortAddress()+"�浥��: "+temp.getWaybillCode()+"Ŀ�ĵر���: "+temp.getPackageCenterCode());
		}
		
	}
	
	//��ѯ�浥���񶩹����浥ʹ�����v1.0    ��ѯ���������������̵ķ�����ַ   ���������ַһ��Ҫ���ȡ�����浥�ķ�����ַһ��   ֱӪ�Ĳ��ò�
	//ֱӪ�Ϳ�ݵķ�����ַ�����Լ���д
	private static void search() throws ApiException{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillISearchRequest req=new WlbWaybillISearchRequest();
		WaybillApplyRequest waybill_apply_request = new WaybillApplyRequest();
		//waybill_apply_request.setSeller_id(123);
		waybill_apply_request.setCpCode("POSTB");
		//waybill_apply_request.setShipping_address(��Ӧ���ݽṹʾ��JSON);
		//waybill_apply_request.setApp_key(appkey);
		req.setWaybillApplyRequest(waybill_apply_request);
		WlbWaybillISearchResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
	}
	//ȡ�������浥
	private static void cancel() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillICancelRequest req = new WlbWaybillICancelRequest();
		WaybillApplyCancelRequest waybillApplyCancelRequest = new WaybillApplyCancelRequest();
		waybillApplyCancelRequest.setCpCode("POSTB");
		waybillApplyCancelRequest.setRealUserId(123L);
		//���׶����б�
		List<String> tradeOrderList = new ArrayList<String>();
		tradeOrderList.add("193066509162107");
		waybillApplyCancelRequest.setTradeOrderList(tradeOrderList);
		
		waybillApplyCancelRequest.setWaybillCode("9976608158001");
		req.setWaybillApplyCancelRequest(waybillApplyCancelRequest);
		WlbWaybillICancelResponse resp  = client.execute(req,sessionKey);
		System.out.println(resp.getBody());
	}
	
	//����ȡ��ǰ����   ����Ѿ����գ����ȡ��ʧ��
	private static void searchDetail() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIQuerydetailRequest req=new WlbWaybillIQuerydetailRequest();
		WaybillDetailQueryRequest waybill_detail_query_request = new WaybillDetailQueryRequest();
		List<String> bills = new ArrayList<String>();
		bills.add("9976608158050");
		waybill_detail_query_request.setWaybillCodes(bills);
		waybill_detail_query_request.setSellerId(123L);
		waybill_detail_query_request.setCpCode("POSTB");
		List<String> tradeLists = new ArrayList<String>();
		tradeLists.add("193066509162107");
		waybill_detail_query_request.setTradeOrderList(tradeLists);
		req.setWaybillDetailQueryRequest(waybill_detail_query_request);
		WlbWaybillIQuerydetailResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
	}
	
	//�޸Ŀ���浥��Ϣ
	private static void modifiedWayBill() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIFullupdateRequest req=new WlbWaybillIFullupdateRequest();
		WaybillApplyFullUpdateRequest waybill_apply_full_update_request = new WaybillApplyFullUpdateRequest();
		waybill_apply_full_update_request.setSendPhone("12324352324");
		waybill_apply_full_update_request.setConsigneeName("����");
		//waybill_apply_full_update_request.setItemName("�·�");
		waybill_apply_full_update_request.setWeight(123L);
		waybill_apply_full_update_request.setSendName("����");
		List<String> orderLists = new ArrayList<String>();
		orderLists.add("809834543");
		waybill_apply_full_update_request.setTradeOrderList(orderLists);
		waybill_apply_full_update_request.setOrderType(123L);
		waybill_apply_full_update_request.setCpCode("POSTB");
		waybill_apply_full_update_request.setWaybillCode("12321323");
		waybill_apply_full_update_request.setProductType("Standard-Express");
		waybill_apply_full_update_request.setCpId(2323L);
		waybill_apply_full_update_request.setOrderChannelsType("TB");//������Դ
		waybill_apply_full_update_request.setSellerId(123232L);
		waybill_apply_full_update_request.setRealUserId(1233232L);
		waybill_apply_full_update_request.setVolume(123L);
		//������Ʒ�б�  �����������Ʒ����
		PackageItem item = new PackageItem();
		item.setCount(1L);
		item.setItemName("ɳ�����-�ǰͿ���100������˱�һ��");
		List<PackageItem> packageList = new ArrayList<PackageItem>();
		packageList.add(item);
		waybill_apply_full_update_request.setPackageItems(packageList);
		//�� �� �� ��
		List<LogisticsService> services = new ArrayList<LogisticsService>();
		LogisticsService logisticsService = new LogisticsService();
		logisticsService.setServiceCode("POSTB");
		logisticsService.setServiceName("��ͨ���");
		logisticsService.setServiceValue4Json(jsonValue);
		services.add(logisticsService);
		//services.add(arg0)
		waybill_apply_full_update_request.setLogisticsServiceList(services);
		
		waybill_apply_full_update_request.setConsigneePhone("1232112322");
		//�����˵�ַ��Ϣ
		WaybillAddress address = new WaybillAddress();//��ݵ�ַ��Ϣ
		address.setAddressDetail("��һ��·969��");
		address.setAddressFormat("json");
		address.setArea("�ຼ��");//��
		//address.setAreaCode(areaCode)
		address.setCity("������");
		address.setProvince("�㽭ʡ");
		waybill_apply_full_update_request.setShippingAddress(address);
		
		//�ռ��˵�ַ 
		waybill_apply_full_update_request.setConsigneeAddress(address);
		//waybill_apply_full_update_request.setPackage_id("E12321321-1234567");
		req.setWaybillApplyFullUpdateRequest(waybill_apply_full_update_request);
		WlbWaybillIFullupdateResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
	}
	
	//�����̲�Ʒ���ͽӿ�  
	private static void getServiceType() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIProductRequest req=new WlbWaybillIProductRequest();
		WaybillProductTypeRequest waybillProductTypeRequest = new WaybillProductTypeRequest();
		waybillProductTypeRequest.setCpCode("POSTB");
		req.setWaybillProductTypeRequest(waybillProductTypeRequest);
		WlbWaybillIProductResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
		//response.get
	}
	
	//taobao.wlb.waybill.i.print
	//����������֤  һ�ο�����֤10�������������һ��ʧ�ܣ���ȫ����ͨ��
	//�᷵�ش�ӡ����
	//�����������ӡ����һ�δ�ӡ����notice_messageΪnull��������ظ���ӡ��������ʾ��Ϣ��
	private static void printInfo() throws Exception{
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIPrintRequest req=new WlbWaybillIPrintRequest();
		WaybillApplyPrintCheckRequest waybill_apply_print_check_request = new WaybillApplyPrintCheckRequest();
		waybill_apply_print_check_request.setSellerId(2054718218L);
		//�浥������Ϣ����  һ�ο���ȷ�϶������
		List<PrintCheckInfo> infos = new ArrayList<PrintCheckInfo>();
		PrintCheckInfo printCheckInfo = new PrintCheckInfo();
		WaybillAddress address = new WaybillAddress();//������ַ��Ϣ
		address.setAddressDetail("��һ��·969��");
		address.setAddressFormat("json");
		address.setArea("�ຼ��");//��
		//address.setAreaCode(areaCode)
		address.setCity("������");
		address.setProvince("�㽭ʡ");
		printCheckInfo.setConsigneeAddress(address);//�ջ���ַ
		printCheckInfo.setConsigneeName("����");//�ջ���
		printCheckInfo.setConsigneePhone("13712454578");
		WaybillAddress shippingAddress = new WaybillAddress();//��ݵ�ַ��Ϣ
		shippingAddress.setAddressDetail("��һ��·969��");
		shippingAddress.setAddressFormat("json");
		shippingAddress.setArea("�ຼ��");//��
		//address.setAreaCode(areaCode)
		shippingAddress.setCity("������");
		shippingAddress.setProvince("�㽭ʡ");
		printCheckInfo.setShippingAddress(shippingAddress);
		printCheckInfo.setWaybillCode("9976608290002");//��ݵ��š�
		printCheckInfo.setRealUserId(89346737L);//�ش�����
		infos.add(printCheckInfo);
		waybill_apply_print_check_request.setPrintCheckInfoCols(infos);
		waybill_apply_print_check_request.setCpCode("POSTB");//��ݹ�˾����
		req.setWaybillApplyPrintCheckRequest(waybill_apply_print_check_request);
		System.out.println(req.getWaybillApplyPrintCheckRequest());
		WlbWaybillIPrintResponse response = client.execute(req , sessionKey);
		System.out.println(response.getBody());
		if(response.isSuccess()){
			System.out.println("���Ե��ô�ӡ�����д�ӡ......");
		}else{
			System.out.println("��ϢУ��ʧ�ܣ�������Ϣ: "+response.getSubMsg());
		}
	}

}
