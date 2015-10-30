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

public  class CarrierHelper implements com.vip.osp.sdk.base.BeanSerializer<Carrier>
{
	
	public static final CarrierHelper OBJ = new CarrierHelper();
	
	public static CarrierHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Carrier struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("tms_carrier_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTms_carrier_id(value);
				}
				
				
				
				
				
				if ("carrier_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_name(value);
				}
				
				
				
				
				
				if ("carrier_shortname".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_shortname(value);
				}
				
				
				
				
				
				if ("carrier_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_code(value);
				}
				
				
				
				
				
				if ("carrier_isvalid".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setCarrier_isvalid(value);
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
	
	
	public void write(Carrier struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getTms_carrier_id() != null) {
			
			oprot.writeFieldBegin("tms_carrier_id");
			oprot.writeString(struct.getTms_carrier_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarrier_name() != null) {
			
			oprot.writeFieldBegin("carrier_name");
			oprot.writeString(struct.getCarrier_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarrier_shortname() != null) {
			
			oprot.writeFieldBegin("carrier_shortname");
			oprot.writeString(struct.getCarrier_shortname());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarrier_code() != null) {
			
			oprot.writeFieldBegin("carrier_code");
			oprot.writeString(struct.getCarrier_code());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarrier_isvalid() != null) {
			
			oprot.writeFieldBegin("carrier_isvalid");
			oprot.writeI32(struct.getCarrier_isvalid()); 
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Carrier bean) throws OspException {
		
		
	}
	
	
}