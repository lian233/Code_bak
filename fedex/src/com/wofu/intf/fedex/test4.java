package com.wofu.intf.fedex;
import java.util.Iterator;
import java.util.List;
import javax.xml.ws.Holder;
import org.example.servicefororder.OrderInfo;
import org.example.servicefororder.ProductDeatilType;
import org.example.servicefororder.ServiceForOrder;
import org.example.servicefororder.ServiceForOrder_Service;
import org.example.serviceforproduct.ErrorType;
import org.example.serviceforproduct.HeaderRequest;
import org.example.serviceforproduct.ProductInfo;
import org.example.serviceforproduct.ServiceForProduct;
import org.example.serviceforproduct.ServiceForProduct_Service;

import com.wofu.common.tools.util.log.Log;
public class test4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		createProduct();
		//createOrder();
	}

	private static void createProduct(){
		ServiceForProduct_Service service  = new ServiceForProduct_Service();
		ServiceForProduct info = service.getServiceForProductSOAP();
		HeaderRequest request = new HeaderRequest();
		request.setAppKey(Params.Key);
		request.setAppToken(Params.Token);
		request.setCustomerCode(Params.customercode);
		ProductInfo productInfo = new ProductInfo();
		productInfo.setApplyEnterpriseCode("02050000");
		//productInfo.setBarcodeDefine("55");
		productInfo.setBarcodeType(0);
		productInfo.setBrand("sdffsd4");
		productInfo.setGoodTaxCode("44");
		productInfo.setGoodTaxCode("4");
		//productInfo.setHeight(4.4f);
		productInfo.setHsCode("9703000090");//必填
		productInfo.setHsGoodsName("end1");
		//productInfo.setLength(3f);
		productInfo.setNetWeight(2f);
		productInfo.setOriginCountry("CN");
		productInfo.setProductDeclaredValue(2f);
		productInfo.setProductLink("33");
		productInfo.setSkuCategory(1);
		productInfo.setSkuEnName("442");
		productInfo.setSkuName("中国货14");//名称
		productInfo.setSkuNo("bbbb1245922");//sku
		productInfo.setSpecificationsAndModels("e2e3e2");
		productInfo.setUOM("007");
		productInfo.setWeight(5f);
		Log.info("ApplyEnterpriseCode: "+productInfo.getApplyEnterpriseCode());
		Log.info("BarcodeDefine: " +productInfo.getBarcodeDefine());
		Log.info("Brand:　"+productInfo.getBrand());
		Log.info("GoodTaxCode: "+productInfo.getGoodTaxCode());
		Log.info("HsCode: "+productInfo.getHsCode());
		Log.info("HsGoodsName: "+productInfo.getHsGoodsName());
		System.out.println("yyy");
		Log.info("OriginCountry: "+productInfo.getOriginCountry());
		Log.info("SkuEnName: " +productInfo.getSkuEnName());
		Log.info("SkuName: "+productInfo.getSkuName());
		System.out.println("sss");
		Log.info("skuNo: "+productInfo.getSkuNo());
		Log.info("SpecificationsAndModels: "+productInfo.getSpecificationsAndModels());
		Log.info("UOM: "+productInfo.getUOM());
		Log.info("BarcodeType: "+productInfo.getBarcodeType()+"");
		Log.info("Height: "+productInfo.getHeight()+"");
		Log.info("Length: "+productInfo.getLength()+"");
		Log.info("netweight: "+productInfo.getNetWeight()+"");
		Log.info("ProductDeclaredValue: "+productInfo.getProductDeclaredValue()+"");
		Log.info("SkuCategory: "+productInfo.getSkuCategory()+"");
		Log.info("Weight: "+productInfo.getWeight()+"");
		Log.info("weight: "+productInfo.getWidth()+"");
		//productInfo.setHeight(1f);
		Holder ask = new Holder();
		Holder message = new Holder();
		Holder error = new Holder();
		System.out.println(message.value);
		System.out.println(ask.value);
		info.createProduct(request, productInfo, ask, message, error);
		System.out.println(message.value);
		System.out.println(ask.value);
		List<ErrorType>  type = (List<ErrorType>)error.value;
		for(Iterator it =type.iterator();it.hasNext(); ){
			ErrorType err =(ErrorType) it.next();
			System.out.println(err.getErrorMessage());
		}
	}
		
		
		
	private static void createOrder(){
		ServiceForOrder_Service service = new ServiceForOrder_Service();
		ServiceForOrder serviceforservice = service.getServiceForOrderSOAP();
		org.example.servicefororder.HeaderRequest request =new org.example.servicefororder.HeaderRequest();
		request.setAppKey(Params.Key);
		request.setAppToken(Params.Token);
		request.setCustomerCode(Params.customercode);
		Holder ask = new Holder();
		Holder message = new Holder();
		Holder error = new Holder();
		Holder orderCode = new Holder();
		Holder referenceNo = new Holder();
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setIdNumber("450421198805151574");
		orderInfo.setIdType("身份证");
		orderInfo.setOabCity("南宁");//收件人城市
		orderInfo.setOabCounty("CN");
		orderInfo.setOabName("黄五");
		orderInfo.setOabPhone("13474854873");
		orderInfo.setOabStateName("广西");
		orderInfo.setOabStreetAddress1("天河区棠德南路");
		orderInfo.setOrderStatus("2");//确认的订单
		orderInfo.setReferenceNo("ttkfdlasf654654");//订单号
		orderInfo.setRemark("测试订单");
		orderInfo.setSmCode("FEDEX");//运输方式
		orderInfo.setOabPostcode("542142");//邮编
		orderInfo.setGrossWt("33");
		ProductDeatilType type1 = new ProductDeatilType();
		type1.setOpQuantity(3);
		type1.setProductSku("bbbb2453");
		type1.getDealPrice().add(3.3f);//产品成交单价
		type1.getTransactionPrice().add(3.3f);//产品交易总价格
		orderInfo.getOrderProduct().add(type1);
		serviceforservice.createOrder(request, orderInfo, ask, message, orderCode, error, referenceNo);
		System.out.println(message.value);
		List<org.example.servicefororder.ErrorType> errorType = (List<org.example.servicefororder.ErrorType>)error.value;
		for(Iterator it = errorType.iterator();it.hasNext();){
			org.example.servicefororder.ErrorType type =(org.example.servicefororder.ErrorType)it.next();
			System.out.println(type.getErrorMessage());
			}
	}

}
