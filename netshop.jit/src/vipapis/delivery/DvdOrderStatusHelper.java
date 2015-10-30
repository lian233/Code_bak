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

public  class DvdOrderStatusHelper implements com.vip.osp.sdk.base.BeanSerializer<DvdOrderStatus>
{
	
	public static final DvdOrderStatusHelper OBJ = new DvdOrderStatusHelper();
	
	public static DvdOrderStatusHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(DvdOrderStatus struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setId(value);
				}
				
				
				
				
				
				if ("order_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_id(value);
				}
				
				
				
				
				
				if ("old_order_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOld_order_id(value);
				}
				
				
				
				
				
				if ("state".equals(schemeField.trim())){
					
					vipapis.common.OrderStatus value;
					
					value = null;
					String name = iprot.readString();
					vipapis.common.OrderStatus[] values = vipapis.common.OrderStatus.values(); 
					for(vipapis.common.OrderStatus v : values){
						
						if(v.name().equals(name)){
							
							value = v;
							break;
						}
						
					}
					
					
					struct.setState(value);
				}
				
				
				
				
				
				if ("warehouse_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setWarehouse_name(value);
				}
				
				
				
				
				
				if ("ebs_warehouse_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setEbs_warehouse_code(value);
				}
				
				
				
				
				
				if ("b2c_warehouse_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setB2c_warehouse_code(value);
				}
				
				
				
				
				
				if ("user_type".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setUser_type(value);
				}
				
				
				
				
				
				if ("user_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setUser_name(value);
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
	
	
	public void write(DvdOrderStatus struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getId() != null) {
			
			oprot.writeFieldBegin("id");
			oprot.writeString(struct.getId());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOrder_id() != null) {
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOld_order_id() != null) {
			
			oprot.writeFieldBegin("old_order_id");
			oprot.writeString(struct.getOld_order_id());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getState() != null) {
			
			oprot.writeFieldBegin("state");
			oprot.writeString(struct.getState().name());  
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getWarehouse_name() != null) {
			
			oprot.writeFieldBegin("warehouse_name");
			oprot.writeString(struct.getWarehouse_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getEbs_warehouse_code() != null) {
			
			oprot.writeFieldBegin("ebs_warehouse_code");
			oprot.writeString(struct.getEbs_warehouse_code());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getB2c_warehouse_code() != null) {
			
			oprot.writeFieldBegin("b2c_warehouse_code");
			oprot.writeString(struct.getB2c_warehouse_code());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getUser_type() != null) {
			
			oprot.writeFieldBegin("user_type");
			oprot.writeI32(struct.getUser_type()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getUser_name() != null) {
			
			oprot.writeFieldBegin("user_name");
			oprot.writeString(struct.getUser_name());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(DvdOrderStatus bean) throws OspException {
		
		
	}
	
	
}