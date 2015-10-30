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

public  class VendorProductSkuKeyHelper implements com.vip.osp.sdk.base.BeanSerializer<VendorProductSkuKey>
{
	
	public static final VendorProductSkuKeyHelper OBJ = new VendorProductSkuKeyHelper();
	
	public static VendorProductSkuKeyHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(VendorProductSkuKey struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("vendor_id".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setVendor_id(value);
				}
				
				
				
				
				
				if ("barcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBarcode(value);
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
	
	
	public void write(VendorProductSkuKey struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("vendor_id");
		oprot.writeI32(struct.getVendor_id()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("barcode");
		oprot.writeString(struct.getBarcode());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(VendorProductSkuKey bean) throws OspException {
		
		
	}
	
	
}