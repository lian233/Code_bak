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

public  class RefuseOrReturnProductResultInfoHelper implements com.vip.osp.sdk.base.BeanSerializer<RefuseOrReturnProductResultInfo>
{
	
	public static final RefuseOrReturnProductResultInfoHelper OBJ = new RefuseOrReturnProductResultInfoHelper();
	
	public static RefuseOrReturnProductResultInfoHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(RefuseOrReturnProductResultInfo struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("order_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_id(value);
				}
				
				
				
				
				
				if ("vendor_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setVendor_id(value);
				}
				
				
				
				
				
				if ("transport_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_no(value);
				}
				
				
				
				
				
				if ("carrier_shortname".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_shortname(value);
				}
				
				
				
				
				
				if ("product_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.RefuseOrReturnProduct> value;
					
					value = new ArrayList<vipapis.delivery.RefuseOrReturnProduct>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.RefuseOrReturnProduct elem1;
							
							elem1 = new vipapis.delivery.RefuseOrReturnProduct();
							vipapis.delivery.RefuseOrReturnProductHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setProduct_list(value);
				}
				
				
				
				
				
				if ("error_msg".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setError_msg(value);
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
	
	
	public void write(RefuseOrReturnProductResultInfo struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getOrder_id() != null) {
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getVendor_id() != null) {
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeString(struct.getVendor_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTransport_no() != null) {
			
			oprot.writeFieldBegin("transport_no");
			oprot.writeString(struct.getTransport_no());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCarrier_shortname() != null) {
			
			oprot.writeFieldBegin("carrier_shortname");
			oprot.writeString(struct.getCarrier_shortname());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_list() != null) {
			
			oprot.writeFieldBegin("product_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.RefuseOrReturnProduct _item0 : struct.getProduct_list()){
				
				
				vipapis.delivery.RefuseOrReturnProductHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getError_msg() != null) {
			
			oprot.writeFieldBegin("error_msg");
			oprot.writeString(struct.getError_msg());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(RefuseOrReturnProductResultInfo bean) throws OspException {
		
		
	}
	
	
}