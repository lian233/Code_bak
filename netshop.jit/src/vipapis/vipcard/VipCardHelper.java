package vipapis.vipcard;

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

public  class VipCardHelper implements com.vip.osp.sdk.base.BeanSerializer<VipCard>
{
	
	public static final VipCardHelper OBJ = new VipCardHelper();
	
	public static VipCardHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(VipCard struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("card_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCard_code(value);
				}
				
				
				
				
				
				if ("money".equals(schemeField.trim())){
					
					double value;
					value = iprot.readDouble();
					
					struct.setMoney(value);
				}
				
				
				
				
				
				if ("state".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setState(value);
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
	
	
	public void write(VipCard struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("card_code");
		oprot.writeString(struct.getCard_code());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("money");
		oprot.writeDouble(struct.getMoney());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("state");
		oprot.writeI32(struct.getState()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(VipCard bean) throws OspException {
		
		
	}
	
	
}