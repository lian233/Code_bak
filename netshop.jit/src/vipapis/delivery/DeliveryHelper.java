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

public  class DeliveryHelper implements com.vip.osp.sdk.base.BeanSerializer<Delivery>
{
	
	public static final DeliveryHelper OBJ = new DeliveryHelper();
	
	public static DeliveryHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Delivery struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("vendor_type".equals(schemeField.trim())){
					
					vipapis.common.VendorType value;
					
					value = null;
					String name = iprot.readString();
					vipapis.common.VendorType[] values = vipapis.common.VendorType.values(); 
					for(vipapis.common.VendorType v : values){
						
						if(v.name().equals(name)){
							
							value = v;
							break;
						}
						
					}
					
					
					struct.setVendor_type(value);
				}
				
				
				
				
				
				if ("barcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBarcode(value);
				}
				
				
				
				
				
				if ("box_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBox_no(value);
				}
				
				
				
				
				
				if ("pick_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPick_no(value);
				}
				
				
				
				
				
				if ("amount".equals(schemeField.trim())){
					
					int value;
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
	
	
	public void write(Delivery struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("vendor_type");
		oprot.writeString(struct.getVendor_type().name());  
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("barcode");
		oprot.writeString(struct.getBarcode());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("box_no");
		oprot.writeString(struct.getBox_no());
		
		oprot.writeFieldEnd();
		
		if(struct.getPick_no() != null) {
			
			oprot.writeFieldBegin("pick_no");
			oprot.writeString(struct.getPick_no());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldBegin("amount");
		oprot.writeI32(struct.getAmount()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Delivery bean) throws OspException {
		
		
	}
	
	
}