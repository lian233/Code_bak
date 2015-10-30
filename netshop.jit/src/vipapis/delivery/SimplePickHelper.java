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

public  class SimplePickHelper implements com.vip.osp.sdk.base.BeanSerializer<SimplePick>
{
	
	public static final SimplePickHelper OBJ = new SimplePickHelper();
	
	public static SimplePickHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(SimplePick struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("pick_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPick_no(value);
				}
				
				
				
				
				
				if ("pick_type".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPick_type(value);
				}
				
				
				
				
				
				if ("warehouse".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setWarehouse(value);
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
	
	
	public void write(SimplePick struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("pick_no");
		oprot.writeString(struct.getPick_no());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("pick_type");
		oprot.writeString(struct.getPick_type());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("warehouse");
		oprot.writeString(struct.getWarehouse());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(SimplePick bean) throws OspException {
		
		
	}
	
	
}