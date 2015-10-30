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

public  class RefuseOrReturnProductHelper implements com.vip.osp.sdk.base.BeanSerializer<RefuseOrReturnProduct>
{
	
	public static final RefuseOrReturnProductHelper OBJ = new RefuseOrReturnProductHelper();
	
	public static RefuseOrReturnProductHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(RefuseOrReturnProduct struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
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
	
	
	public void write(RefuseOrReturnProduct struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
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
	
	
	public void validate(RefuseOrReturnProduct bean) throws OspException {
		
		
	}
	
	
}