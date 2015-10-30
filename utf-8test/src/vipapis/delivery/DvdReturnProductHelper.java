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

public  class DvdReturnProductHelper implements com.vip.osp.sdk.base.BeanSerializer<DvdReturnProduct>
{
	
	public static final DvdReturnProductHelper OBJ = new DvdReturnProductHelper();
	
	public static DvdReturnProductHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(DvdReturnProduct struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("product_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_name(value);
				}
				
				
				
				
				
				if ("order_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_id(value);
				}
				
				
				
				
				
				if ("po_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPo_no(value);
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
				
				
				
				
				
				iprot.readFieldEnd();
			}
			
			iprot.readStructEnd();
			validate(struct);
		}
		else{
			
			throw new OspException();
		}
		
		
	}
	
	
	public void write(DvdReturnProduct struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getProduct_name() != null) {
			
			oprot.writeFieldBegin("product_name");
			oprot.writeString(struct.getProduct_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOrder_id() != null) {
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPo_no() != null) {
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
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
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(DvdReturnProduct bean) throws OspException {
		
		
	}
	
	
}