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

public  class DvdOrderDetailHelper implements com.vip.osp.sdk.base.BeanSerializer<DvdOrderDetail>
{
	
	public static final DvdOrderDetailHelper OBJ = new DvdOrderDetailHelper();
	
	public static DvdOrderDetailHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(DvdOrderDetail struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("brand_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_name(value);
				}
				
				
				
				
				
				if ("product_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_name(value);
				}
				
				
				
				
				
				if ("size".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSize(value);
				}
				
				
				
				
				
				if ("product_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_no(value);
				}
				
				
				
				
				
				if ("barcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBarcode(value);
				}
				
				
				
				
				
				if ("amount".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setAmount(value);
				}
				
				
				
				
				
				if ("price".equals(schemeField.trim())){
					
					Double value;
					value = iprot.readDouble();
					
					struct.setPrice(value);
				}
				
				
				
				
				
				if ("order_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_id(value);
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
	
	
	public void write(DvdOrderDetail struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getBrand_name() != null) {
			
			oprot.writeFieldBegin("brand_name");
			oprot.writeString(struct.getBrand_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_name() != null) {
			
			oprot.writeFieldBegin("product_name");
			oprot.writeString(struct.getProduct_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSize() != null) {
			
			oprot.writeFieldBegin("size");
			oprot.writeString(struct.getSize());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_no() != null) {
			
			oprot.writeFieldBegin("product_no");
			oprot.writeString(struct.getProduct_no());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBarcode() != null) {
			
			oprot.writeFieldBegin("barcode");
			oprot.writeString(struct.getBarcode());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAmount() != null) {
			
			oprot.writeFieldBegin("amount");
			oprot.writeI32(struct.getAmount()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPrice() != null) {
			
			oprot.writeFieldBegin("price");
			oprot.writeDouble(struct.getPrice());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOrder_id() != null) {
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(DvdOrderDetail bean) throws OspException {
		
		
	}
	
	
}