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

public  class DvdOrderHelper implements com.vip.osp.sdk.base.BeanSerializer<DvdOrder>
{
	
	public static final DvdOrderHelper OBJ = new DvdOrderHelper();
	
	public static DvdOrderHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(DvdOrder struct, Protocol iprot) throws OspException {
		
		
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
				
				
				
				
				
				if ("status".equals(schemeField.trim())){
					
					vipapis.common.OrderStatus value;
					
					value = null;
					String name = iprot.readString();
					vipapis.common.OrderStatus[] values = vipapis.common.OrderStatus.values(); 
					for(vipapis.common.OrderStatus v : values){
						
						if(v.name().equals(name)){
							
							value = v;
							break;
						}
						
					}
					
					
					struct.setStatus(value);
				}
				
				
				
				
				
				if ("buyer".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBuyer(value);
				}
				
				
				
				
				
				if ("address".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAddress(value);
				}
				
				
				
				
				
				if ("mobile".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setMobile(value);
				}
				
				
				
				
				
				if ("tel".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTel(value);
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
				
				
				
				
				
				if ("invoice".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setInvoice(value);
				}
				
				
				
				
				
				if ("carriage".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarriage(value);
				}
				
				
				
				
				
				if ("remark".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setRemark(value);
				}
				
				
				
				
				
				if ("transport_day".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_day(value);
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
				
				
				
				
				
				if ("ex_fav_money".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setEx_fav_money(value);
				}
				
				
				
				
				
				if ("favourable_money".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setFavourable_money(value);
				}
				
				
				
				
				
				if ("product_money".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_money(value);
				}
				
				
				
				
				
				if ("add_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAdd_time(value);
				}
				
				
				
				
				
				if ("po_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPo_id(value);
				}
				
				
				
				
				
				if ("county".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCounty(value);
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
	
	
	public void write(DvdOrder struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getOrder_id() != null) {
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getStatus() != null) {
			
			oprot.writeFieldBegin("status");
			oprot.writeString(struct.getStatus().name());  
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBuyer() != null) {
			
			oprot.writeFieldBegin("buyer");
			oprot.writeString(struct.getBuyer());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAddress() != null) {
			
			oprot.writeFieldBegin("address");
			oprot.writeString(struct.getAddress());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMobile() != null) {
			
			oprot.writeFieldBegin("mobile");
			oprot.writeString(struct.getMobile());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTel() != null) {
			
			oprot.writeFieldBegin("tel");
			oprot.writeString(struct.getTel());
			
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
		
		
		if(struct.getInvoice() != null) {
			
			oprot.writeFieldBegin("invoice");
			oprot.writeString(struct.getInvoice());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarriage() != null) {
			
			oprot.writeFieldBegin("carriage");
			oprot.writeString(struct.getCarriage());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getRemark() != null) {
			
			oprot.writeFieldBegin("remark");
			oprot.writeString(struct.getRemark());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTransport_day() != null) {
			
			oprot.writeFieldBegin("transport_day");
			oprot.writeString(struct.getTransport_day());
			
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
		
		
		if(struct.getEx_fav_money() != null) {
			
			oprot.writeFieldBegin("ex_fav_money");
			oprot.writeString(struct.getEx_fav_money());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getFavourable_money() != null) {
			
			oprot.writeFieldBegin("favourable_money");
			oprot.writeString(struct.getFavourable_money());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_money() != null) {
			
			oprot.writeFieldBegin("product_money");
			oprot.writeString(struct.getProduct_money());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAdd_time() != null) {
			
			oprot.writeFieldBegin("add_time");
			oprot.writeString(struct.getAdd_time());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPo_id() != null) {
			
			oprot.writeFieldBegin("po_id");
			oprot.writeString(struct.getPo_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCounty() != null) {
			
			oprot.writeFieldBegin("county");
			oprot.writeString(struct.getCounty());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(DvdOrder bean) throws OspException {
		
		
	}
	
	
}