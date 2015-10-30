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

public  class DvdReturnHelper implements com.vip.osp.sdk.base.BeanSerializer<DvdReturn>
{
	
	public static final DvdReturnHelper OBJ = new DvdReturnHelper();
	
	public static DvdReturnHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(DvdReturn struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("vendor_id".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setVendor_id(value);
				}
				
				
				
				
				
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
				
				
				
				
				
				if ("return_reason".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setReturn_reason(value);
				}
				
				
				
				
				
				if ("create_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCreate_time(value);
				}
				
				
				
				
				
				if ("back_sn".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBack_sn(value);
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
	
	
	public void write(DvdReturn struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getVendor_id() != null) {
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
		}
		
		
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
		
		
		if(struct.getReturn_reason() != null) {
			
			oprot.writeFieldBegin("return_reason");
			oprot.writeString(struct.getReturn_reason());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCreate_time() != null) {
			
			oprot.writeFieldBegin("create_time");
			oprot.writeString(struct.getCreate_time());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBack_sn() != null) {
			
			oprot.writeFieldBegin("back_sn");
			oprot.writeString(struct.getBack_sn());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(DvdReturn bean) throws OspException {
		
		
	}
	
	
}