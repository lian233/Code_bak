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

public  class DeleteDeliveryDetailHelper implements com.vip.osp.sdk.base.BeanSerializer<DeleteDeliveryDetail>
{
	
	public static final DeleteDeliveryDetailHelper OBJ = new DeleteDeliveryDetailHelper();
	
	public static DeleteDeliveryDetailHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(DeleteDeliveryDetail struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("delivery_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setDelivery_no(value);
				}
				
				
				
				
				
				if ("barcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBarcode(value);
				}
				
				
				
				
				
				if ("delivery_detail_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setDelivery_detail_id(value);
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
	
	
	public void write(DeleteDeliveryDetail struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("delivery_no");
		oprot.writeString(struct.getDelivery_no());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("barcode");
		oprot.writeString(struct.getBarcode());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("delivery_detail_id");
		oprot.writeString(struct.getDelivery_detail_id());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(DeleteDeliveryDetail bean) throws OspException {
		
		
	}
	
	
}