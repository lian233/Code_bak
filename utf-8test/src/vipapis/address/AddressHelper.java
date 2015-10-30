package vipapis.address;

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

public  class AddressHelper implements com.vip.osp.sdk.base.BeanSerializer<Address>
{
	
	public static final AddressHelper OBJ = new AddressHelper();
	
	public static AddressHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Address struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("address_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAddress_code(value);
				}
				
				
				
				
				
				if ("address_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAddress_name(value);
				}
				
				
				
				
				
				if ("full_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setFull_name(value);
				}
				
				
				
				
				
				if ("parent_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setParent_code(value);
				}
				
				
				
				
				
				if ("has_children".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setHas_children(value);
				}
				
				
				
				
				
				if ("is_directly".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setIs_directly(value);
				}
				
				
				
				
				
				if ("postage".equals(schemeField.trim())){
					
					double value;
					value = iprot.readDouble();
					
					struct.setPostage(value);
				}
				
				
				
				
				
				if ("is_cod".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setIs_cod(value);
				}
				
				
				
				
				
				if ("is_pos".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setIs_pos(value);
				}
				
				
				
				
				
				if ("is_big".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setIs_big(value);
				}
				
				
				
				
				
				if ("is_app".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setIs_app(value);
				}
				
				
				
				
				
				if ("cod_fee".equals(schemeField.trim())){
					
					double value;
					value = iprot.readDouble();
					
					struct.setCod_fee(value);
				}
				
				
				
				
				
				if ("is_service".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setIs_service(value);
				}
				
				
				
				
				
				if ("is_ems".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setIs_ems(value);
				}
				
				
				
				
				
				if ("big_money".equals(schemeField.trim())){
					
					double value;
					value = iprot.readDouble();
					
					struct.setBig_money(value);
				}
				
				
				
				
				
				if ("state".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setState(value);
				}
				
				
				
				
				
				if ("post_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPost_code(value);
				}
				
				
				
				
				
				if ("more_carrier".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setMore_carrier(value);
				}
				
				
				
				
				
				if ("carrier_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_name(value);
				}
				
				
				
				
				
				if ("delivery".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setDelivery(value);
				}
				
				
				
				
				
				if ("warehouse".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setWarehouse(value);
				}
				
				
				
				
				
				if ("is_support_air_embargo".equals(schemeField.trim())){
					
					byte value;
					value = iprot.readByte(); 
					
					struct.setIs_support_air_embargo(value);
				}
				
				
				
				
				
				if ("addr_type".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setAddr_type(value);
				}
				
				
				
				
				
				if ("area_type".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setArea_type(value);
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
	
	
	public void write(Address struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("address_code");
		oprot.writeString(struct.getAddress_code());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("address_name");
		oprot.writeString(struct.getAddress_name());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("full_name");
		oprot.writeString(struct.getFull_name());
		
		oprot.writeFieldEnd();
		
		if(struct.getParent_code() != null) {
			
			oprot.writeFieldBegin("parent_code");
			oprot.writeString(struct.getParent_code());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldBegin("has_children");
		oprot.writeByte(struct.getHas_children()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("is_directly");
		oprot.writeByte(struct.getIs_directly()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("postage");
		oprot.writeDouble(struct.getPostage());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("is_cod");
		oprot.writeByte(struct.getIs_cod()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("is_pos");
		oprot.writeByte(struct.getIs_pos()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("is_big");
		oprot.writeByte(struct.getIs_big()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("is_app");
		oprot.writeByte(struct.getIs_app()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("cod_fee");
		oprot.writeDouble(struct.getCod_fee());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("is_service");
		oprot.writeByte(struct.getIs_service()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("is_ems");
		oprot.writeByte(struct.getIs_ems()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("big_money");
		oprot.writeDouble(struct.getBig_money());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("state");
		oprot.writeByte(struct.getState()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("post_code");
		oprot.writeString(struct.getPost_code());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("more_carrier");
		oprot.writeByte(struct.getMore_carrier()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("carrier_name");
		oprot.writeString(struct.getCarrier_name());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("delivery");
		oprot.writeString(struct.getDelivery());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("warehouse");
		oprot.writeString(struct.getWarehouse());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("is_support_air_embargo");
		oprot.writeByte(struct.getIs_support_air_embargo()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("addr_type");
		oprot.writeI32(struct.getAddr_type()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("area_type");
		oprot.writeString(struct.getArea_type());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Address bean) throws OspException {
		
		
	}
	
	
}