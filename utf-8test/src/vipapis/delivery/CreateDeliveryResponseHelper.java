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

public  class CreateDeliveryResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<CreateDeliveryResponse>
{
	
	public static final CreateDeliveryResponseHelper OBJ = new CreateDeliveryResponseHelper();
	
	public static CreateDeliveryResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(CreateDeliveryResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("delivery_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setDelivery_id(value);
				}
				
				
				
				
				
				if ("storage_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setStorage_no(value);
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
	
	
	public void write(CreateDeliveryResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getDelivery_id() != null) {
			
			oprot.writeFieldBegin("delivery_id");
			oprot.writeString(struct.getDelivery_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getStorage_no() != null) {
			
			oprot.writeFieldBegin("storage_no");
			oprot.writeString(struct.getStorage_no());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(CreateDeliveryResponse bean) throws OspException {
		
		
	}
	
	
}