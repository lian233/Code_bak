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

public  class ExportProductHelper implements com.vip.osp.sdk.base.BeanSerializer<ExportProduct>
{
	
	public static final ExportProductHelper OBJ = new ExportProductHelper();
	
	public static ExportProductHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(ExportProduct struct, Protocol iprot) throws OspException {
		
		
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
				
				
				
				
				
				if ("po".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPo(value);
				}
				
				
				
				
				
				if ("barcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBarcode(value);
				}
				
				
				
				
				
				if ("product_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_name(value);
				}
				
				
				
				
				
				if ("product_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_no(value);
				}
				
				
				
				
				
				if ("size".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSize(value);
				}
				
				
				
				
				
				if ("brand_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_name(value);
				}
				
				
				
				
				
				if ("amount".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setAmount(value);
				}
				
				
				
				
				
				if ("price".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPrice(value);
				}
				
				
				
				
				
				if ("is_gift".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setIs_gift(value);
				}
				
				
				
				
				
				if ("unit".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setUnit(value);
				}
				
				
				
				
				
				if ("is_vip".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setIs_vip(value);
				}
				
				
				
				
				
				if ("product_pic".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_pic(value);
				}
				
				
				
				
				
				if ("create_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCreate_time(value);
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
	
	
	public void write(ExportProduct struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getOrder_id() != null) {
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPo() != null) {
			
			oprot.writeFieldBegin("po");
			oprot.writeString(struct.getPo());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBarcode() != null) {
			
			oprot.writeFieldBegin("barcode");
			oprot.writeString(struct.getBarcode());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_name() != null) {
			
			oprot.writeFieldBegin("product_name");
			oprot.writeString(struct.getProduct_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_no() != null) {
			
			oprot.writeFieldBegin("product_no");
			oprot.writeString(struct.getProduct_no());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSize() != null) {
			
			oprot.writeFieldBegin("size");
			oprot.writeString(struct.getSize());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_name() != null) {
			
			oprot.writeFieldBegin("brand_name");
			oprot.writeString(struct.getBrand_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAmount() != null) {
			
			oprot.writeFieldBegin("amount");
			oprot.writeI32(struct.getAmount()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPrice() != null) {
			
			oprot.writeFieldBegin("price");
			oprot.writeString(struct.getPrice());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIs_gift() != null) {
			
			oprot.writeFieldBegin("is_gift");
			oprot.writeString(struct.getIs_gift());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getUnit() != null) {
			
			oprot.writeFieldBegin("unit");
			oprot.writeString(struct.getUnit());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIs_vip() != null) {
			
			oprot.writeFieldBegin("is_vip");
			oprot.writeI32(struct.getIs_vip()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_pic() != null) {
			
			oprot.writeFieldBegin("product_pic");
			oprot.writeString(struct.getProduct_pic());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCreate_time() != null) {
			
			oprot.writeFieldBegin("create_time");
			oprot.writeString(struct.getCreate_time());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(ExportProduct bean) throws OspException {
		
		
	}
	
	
}