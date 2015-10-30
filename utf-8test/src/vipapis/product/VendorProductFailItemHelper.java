package vipapis.product;

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

public  class VendorProductFailItemHelper implements com.vip.osp.sdk.base.BeanSerializer<VendorProductFailItem>
{
	
	public static final VendorProductFailItemHelper OBJ = new VendorProductFailItemHelper();
	
	public static VendorProductFailItemHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(VendorProductFailItem struct, Protocol iprot) throws OspException {
		
		
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
				
				
				
				
				
				if ("msg".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setMsg(value);
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
	
	
	public void write(VendorProductFailItem struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getBarcode() != null) {
			
			oprot.writeFieldBegin("barcode");
			oprot.writeString(struct.getBarcode());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMsg() != null) {
			
			oprot.writeFieldBegin("msg");
			oprot.writeString(struct.getMsg());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(VendorProductFailItem bean) throws OspException {
		
		
	}
	
	
}