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

public  class PickProductHelper implements com.vip.osp.sdk.base.BeanSerializer<PickProduct>
{
	
	public static final PickProductHelper OBJ = new PickProductHelper();
	
	public static PickProductHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(PickProduct struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("stock".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setStock(value);
				}
				
				
				
				
				
				if ("barcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBarcode(value);
				}
				
				
				
				
				
				if ("art_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setArt_no(value);
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
				
				
				
				
				
				iprot.readFieldEnd();
			}
			
			iprot.readStructEnd();
			validate(struct);
		}
		else{
			
			throw new OspException();
		}
		
		
	}
	
	
	public void write(PickProduct struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getStock() != null) {
			
			oprot.writeFieldBegin("stock");
			oprot.writeI32(struct.getStock()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBarcode() != null) {
			
			oprot.writeFieldBegin("barcode");
			oprot.writeString(struct.getBarcode());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getArt_no() != null) {
			
			oprot.writeFieldBegin("art_no");
			oprot.writeString(struct.getArt_no());
			
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
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(PickProduct bean) throws OspException {
		
		
	}
	
	
}