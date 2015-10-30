package vipapis.category;

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

public  class AttributeHelper implements com.vip.osp.sdk.base.BeanSerializer<Attribute>
{
	
	public static final AttributeHelper OBJ = new AttributeHelper();
	
	public static AttributeHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Attribute struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("attriute_id".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setAttriute_id(value);
				}
				
				
				
				
				
				if ("attribute_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAttribute_name(value);
				}
				
				
				
				
				
				if ("english_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setEnglish_name(value);
				}
				
				
				
				
				
				if ("description".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setDescription(value);
				}
				
				
				
				
				
				if ("attribute_type".equals(schemeField.trim())){
					
					vipapis.category.AttributeType value;
					
					value = null;
					String name = iprot.readString();
					vipapis.category.AttributeType[] values = vipapis.category.AttributeType.values(); 
					for(vipapis.category.AttributeType v : values){
						
						if(v.name().equals(name)){
							
							value = v;
							break;
						}
						
					}
					
					
					struct.setAttribute_type(value);
				}
				
				
				
				
				
				if ("data_type".equals(schemeField.trim())){
					
					vipapis.category.DataType value;
					
					value = null;
					String name = iprot.readString();
					vipapis.category.DataType[] values = vipapis.category.DataType.values(); 
					for(vipapis.category.DataType v : values){
						
						if(v.name().equals(name)){
							
							value = v;
							break;
						}
						
					}
					
					
					struct.setData_type(value);
				}
				
				
				
				
				
				if ("unit".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setUnit(value);
				}
				
				
				
				
				
				if ("sort".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setSort(value);
				}
				
				
				
				
				
				if ("flags".equals(schemeField.trim())){
					
					Long value;
					value = iprot.readI64(); 
					
					struct.setFlags(value);
				}
				
				
				
				
				
				if ("parent_attribute_id".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setParent_attribute_id(value);
				}
				
				
				
				
				
				if ("options".equals(schemeField.trim())){
					
					List<vipapis.category.Option> value;
					
					value = new ArrayList<vipapis.category.Option>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.category.Option elem1;
							
							elem1 = new vipapis.category.Option();
							vipapis.category.OptionHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setOptions(value);
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
	
	
	public void write(Attribute struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("attriute_id");
		oprot.writeI32(struct.getAttriute_id()); 
		
		oprot.writeFieldEnd();
		
		if(struct.getAttribute_name() != null) {
			
			oprot.writeFieldBegin("attribute_name");
			oprot.writeString(struct.getAttribute_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getEnglish_name() != null) {
			
			oprot.writeFieldBegin("english_name");
			oprot.writeString(struct.getEnglish_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getDescription() != null) {
			
			oprot.writeFieldBegin("description");
			oprot.writeString(struct.getDescription());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAttribute_type() != null) {
			
			oprot.writeFieldBegin("attribute_type");
			oprot.writeString(struct.getAttribute_type().name());  
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getData_type() != null) {
			
			oprot.writeFieldBegin("data_type");
			oprot.writeString(struct.getData_type().name());  
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getUnit() != null) {
			
			oprot.writeFieldBegin("unit");
			oprot.writeString(struct.getUnit());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSort() != null) {
			
			oprot.writeFieldBegin("sort");
			oprot.writeI32(struct.getSort()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getFlags() != null) {
			
			oprot.writeFieldBegin("flags");
			oprot.writeI64(struct.getFlags()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getParent_attribute_id() != null) {
			
			oprot.writeFieldBegin("parent_attribute_id");
			oprot.writeI32(struct.getParent_attribute_id()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOptions() != null) {
			
			oprot.writeFieldBegin("options");
			
			oprot.writeListBegin();
			for(vipapis.category.Option _item0 : struct.getOptions()){
				
				
				vipapis.category.OptionHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Attribute bean) throws OspException {
		
		
	}
	
	
}