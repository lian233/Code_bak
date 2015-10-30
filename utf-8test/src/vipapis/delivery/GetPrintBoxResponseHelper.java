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

public  class GetPrintBoxResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetPrintBoxResponse>
{
	
	public static final GetPrintBoxResponseHelper OBJ = new GetPrintBoxResponseHelper();
	
	public static GetPrintBoxResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetPrintBoxResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("template".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTemplate(value);
				}
				
				
				
				
				
				if ("total".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setTotal(value);
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
	
	
	public void write(GetPrintBoxResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getTemplate() != null) {
			
			oprot.writeFieldBegin("template");
			oprot.writeString(struct.getTemplate());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTotal() != null) {
			
			oprot.writeFieldBegin("total");
			oprot.writeI32(struct.getTotal()); 
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(GetPrintBoxResponse bean) throws OspException {
		
		
	}
	
	
}