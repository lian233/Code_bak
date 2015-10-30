package vipapis.delivery;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.EnumSet;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public  class ExportOrderInfoHelper implements com.vip.osp.sdk.base.BeanSerializer<ExportOrderInfo>
{
	
	public static final ExportOrderInfoHelper OBJ = new ExportOrderInfoHelper();
	
	public static ExportOrderInfoHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(ExportOrderInfo struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("order_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_id(value);
				}
				
				
				
				
				
				if ("state".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setState(value);
				}
				
				
				
				
				
				if ("warehouse_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setWarehouse_name(value);
				}
				
				
				
				
				
				if ("ebs_warehouse".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setEbs_warehouse(value);
				}
				
				
				
				
				
				if ("b2c_warehouse".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setB2c_warehouse(value);
				}
				
				
				
				
				
				if ("user_type".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setUser_type(value);
				}
				
				
				
				
				
				if ("user_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setUser_name(value);
				}
				
				
				
				
				
				if ("buyer_id".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setBuyer_id(value);
				}
				
				
				
				
				
				if ("address".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAddress(value);
				}
				
				
				
				
				
				if ("buyer".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBuyer(value);
				}
				
				
				
				
				
				if ("area_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setArea_id(value);
				}
				
				
				
				
				
				if ("postcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPostcode(value);
				}
				
				
				
				
				
				if ("city".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCity(value);
				}
				
				
				
				
				
				if ("province".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProvince(value);
				}
				
				
				
				
				
				if ("country_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCountry_id(value);
				}
				
				
				
				
				
				if ("tel".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTel(value);
				}
				
				
				
				
				
				if ("mobile".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setMobile(value);
				}
				
				
				
				
				
				if ("pay_type".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPay_type(value);
				}
				
				
				
				
				
				if ("pos".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setPos(value);
				}
				
				
				
				
				
				if ("transport_day".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_day(value);
				}
				
				
				
				
				
				if ("remark".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setRemark(value);
				}
				
				
				
				
				
				if ("order_type".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_type(value);
				}
				
				
				
				
				
				if ("vipclub".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setVipclub(value);
				}
				
				
				
				
				
				if ("invoice".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setInvoice(value);
				}
				
				
				
				
				
				if ("goods_money".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setGoods_money(value);
				}
				
				
				
				
				
				if ("money".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setMoney(value);
				}
				
				
				
				
				
				if ("aigo".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAigo(value);
				}
				
				
				
				
				
				if ("favourable_money".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setFavourable_money(value);
				}
				
				
				
				
				
				if ("ex_fav_money".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setEx_fav_money(value);
				}
				
				
				
				
				
				if ("surplus".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSurplus(value);
				}
				
				
				
				
				
				if ("carriage".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarriage(value);
				}
				
				
				
				
				
				if ("transport_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_no(value);
				}
				
				
				
				
				
				if ("carrier_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_code(value);
				}
				
				
				
				
				
				if ("carrier".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier(value);
				}
				
				
				
				
				
				if ("transport_detail".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_detail(value);
				}
				
				
				
				
				
				if ("b2c_transport_code".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setB2c_transport_code(value);
				}
				
				
				
				
				
				if ("transport_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_id(value);
				}
				
				
				
				
				
				if ("transport_type".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_type(value);
				}
				
				
				
				
				
				if ("vendor_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setVendor_code(value);
				}
				
				
				
				
				
				if ("vendor_id".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setVendor_id(value);
				}
				
				
				
				
				
				if ("vendor_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setVendor_name(value);
				}
				
				
				
				
				
				if ("brand_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_name(value);
				}
				
				
				
				
				
				if ("goods_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.ExportProduct> value;
					
					value = new ArrayList<vipapis.delivery.ExportProduct>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.ExportProduct elem0;
							
							elem0 = new vipapis.delivery.ExportProduct();
							vipapis.delivery.ExportProductHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setGoods_list(value);
				}
				
				
				
				
				
				iprot.readFieldEnd();
			}
			
			iprot.readStructEnd();
			validate(struct);
		}
		else{
			
			throw new OspException();
		}
		
		
	}
	
	
	public void write(ExportOrderInfo struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getOrder_id() != null) {
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getState() != null) {
			
			oprot.writeFieldBegin("state");
			oprot.writeString(struct.getState());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getWarehouse_name() != null) {
			
			oprot.writeFieldBegin("warehouse_name");
			oprot.writeString(struct.getWarehouse_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getEbs_warehouse() != null) {
			
			oprot.writeFieldBegin("ebs_warehouse");
			oprot.writeString(struct.getEbs_warehouse());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getB2c_warehouse() != null) {
			
			oprot.writeFieldBegin("b2c_warehouse");
			oprot.writeString(struct.getB2c_warehouse());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getUser_type() != null) {
			
			oprot.writeFieldBegin("user_type");
			oprot.writeI32(struct.getUser_type()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getUser_name() != null) {
			
			oprot.writeFieldBegin("user_name");
			oprot.writeString(struct.getUser_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBuyer_id() != null) {
			
			oprot.writeFieldBegin("buyer_id");
			oprot.writeI32(struct.getBuyer_id()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAddress() != null) {
			
			oprot.writeFieldBegin("address");
			oprot.writeString(struct.getAddress());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBuyer() != null) {
			
			oprot.writeFieldBegin("buyer");
			oprot.writeString(struct.getBuyer());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getArea_id() != null) {
			
			oprot.writeFieldBegin("area_id");
			oprot.writeString(struct.getArea_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPostcode() != null) {
			
			oprot.writeFieldBegin("postcode");
			oprot.writeString(struct.getPostcode());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCity() != null) {
			
			oprot.writeFieldBegin("city");
			oprot.writeString(struct.getCity());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProvince() != null) {
			
			oprot.writeFieldBegin("province");
			oprot.writeString(struct.getProvince());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCountry_id() != null) {
			
			oprot.writeFieldBegin("country_id");
			oprot.writeString(struct.getCountry_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTel() != null) {
			
			oprot.writeFieldBegin("tel");
			oprot.writeString(struct.getTel());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMobile() != null) {
			
			oprot.writeFieldBegin("mobile");
			oprot.writeString(struct.getMobile());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPay_type() != null) {
			
			oprot.writeFieldBegin("pay_type");
			oprot.writeString(struct.getPay_type());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPos() != null) {
			
			oprot.writeFieldBegin("pos");
			oprot.writeI32(struct.getPos()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTransport_day() != null) {
			
			oprot.writeFieldBegin("transport_day");
			oprot.writeString(struct.getTransport_day());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getRemark() != null) {
			
			oprot.writeFieldBegin("remark");
			oprot.writeString(struct.getRemark());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOrder_type() != null) {
			
			oprot.writeFieldBegin("order_type");
			oprot.writeString(struct.getOrder_type());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getVipclub() != null) {
			
			oprot.writeFieldBegin("vipclub");
			oprot.writeString(struct.getVipclub());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getInvoice() != null) {
			
			oprot.writeFieldBegin("invoice");
			oprot.writeString(struct.getInvoice());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getGoods_money() != null) {
			
			oprot.writeFieldBegin("goods_money");
			oprot.writeString(struct.getGoods_money());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMoney() != null) {
			
			oprot.writeFieldBegin("money");
			oprot.writeString(struct.getMoney());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAigo() != null) {
			
			oprot.writeFieldBegin("aigo");
			oprot.writeString(struct.getAigo());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getFavourable_money() != null) {
			
			oprot.writeFieldBegin("favourable_money");
			oprot.writeString(struct.getFavourable_money());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getEx_fav_money() != null) {
			
			oprot.writeFieldBegin("ex_fav_money");
			oprot.writeString(struct.getEx_fav_money());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSurplus() != null) {
			
			oprot.writeFieldBegin("surplus");
			oprot.writeString(struct.getSurplus());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarriage() != null) {
			
			oprot.writeFieldBegin("carriage");
			oprot.writeString(struct.getCarriage());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTransport_no() != null) {
			
			oprot.writeFieldBegin("transport_no");
			oprot.writeString(struct.getTransport_no());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarrier_code() != null) {
			
			oprot.writeFieldBegin("carrier_code");
			oprot.writeString(struct.getCarrier_code());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarrier() != null) {
			
			oprot.writeFieldBegin("carrier");
			oprot.writeString(struct.getCarrier());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTransport_detail() != null) {
			
			oprot.writeFieldBegin("transport_detail");
			oprot.writeString(struct.getTransport_detail());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getB2c_transport_code() != null) {
			
			oprot.writeFieldBegin("b2c_transport_code");
			oprot.writeI32(struct.getB2c_transport_code()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTransport_id() != null) {
			
			oprot.writeFieldBegin("transport_id");
			oprot.writeString(struct.getTransport_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTransport_type() != null) {
			
			oprot.writeFieldBegin("transport_type");
			oprot.writeString(struct.getTransport_type());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getVendor_code() != null) {
			
			oprot.writeFieldBegin("vendor_code");
			oprot.writeString(struct.getVendor_code());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getVendor_id() != null) {
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getVendor_name() != null) {
			
			oprot.writeFieldBegin("vendor_name");
			oprot.writeString(struct.getVendor_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_name() != null) {
			
			oprot.writeFieldBegin("brand_name");
			oprot.writeString(struct.getBrand_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getGoods_list() != null) {
			
			oprot.writeFieldBegin("goods_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.ExportProduct _item0 : struct.getGoods_list()){
				
				
				vipapis.delivery.ExportProductHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(ExportOrderInfo bean) throws OspException {
		
		
	}
	
	
}