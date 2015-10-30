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

public  class OptionHelper implements com.vip.osp.sdk.base.BeanSerializer<Option>
{
	
	public static final OptionHelper OBJ = new OptionHelper();
	
	public static OptionHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Option struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("attributeId".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setAttributeId(value);
				}
				
				
				
				
				
				if ("optionId".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setOptionId(value);
				}
				
				
				
				
				
				if ("name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setName(value);
				}
				
				
				
				
				
				if ("englishname".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setEnglishname(value);
				}
				
				
				
				
				
				if ("description".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setDescription(value);
				}
				
				
				
				
				
				if ("hierarchyGroup".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setHierarchyGroup(value);
				}
				
				
				
				
				
				if ("sort".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setSort(value);
				}
				
				
				
				
				
				if ("parentOptionId".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setParentOptionId(value);
				}
				
				
				
				
				
				if ("isVirtual".equals(schemeField.trim())){
					
					Boolean value;
					value = iprot.readBool();
					
					struct.setIsVirtual(value);
				}
				
				
				
				
				
				if ("realOptions".equals(schemeField.trim())){
					
					List<Integer> value;
					
					value = new ArrayList<Integer>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							int elem1;
							elem1 = iprot.readI32(); 
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setRealOptions(value);
				}
				
				
				
				
				
				if ("foreignname".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setForeignname(value);
				}
				
				
				
				
				
				if ("externaldata".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setExternaldata(value);
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
	
	
	public void write(Option struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("attributeId");
		oprot.writeI32(struct.getAttributeId()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("optionId");
		oprot.writeI32(struct.getOptionId()); 
		
		oprot.writeFieldEnd();
		
		if(struct.getName() != null) {
			
			oprot.writeFieldBegin("name");
			oprot.writeString(struct.getName());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getEnglishname() != null) {
			
			oprot.writeFieldBegin("englishname");
			oprot.writeString(struct.getEnglishname());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getDescription() != null) {
			
			oprot.writeFieldBegin("description");
			oprot.writeString(struct.getDescription());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getHierarchyGroup() != null) {
			
			oprot.writeFieldBegin("hierarchyGroup");
			oprot.writeString(struct.getHierarchyGroup());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSort() != null) {
			
			oprot.writeFieldBegin("sort");
			oprot.writeI32(struct.getSort()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getParentOptionId() != null) {
			
			oprot.writeFieldBegin("parentOptionId");
			oprot.writeI32(struct.getParentOptionId()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIsVirtual() != null) {
			
			oprot.writeFieldBegin("isVirtual");
			oprot.writeBool(struct.getIsVirtual());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getRealOptions() != null) {
			
			oprot.writeFieldBegin("realOptions");
			
			oprot.writeListBegin();
			for(int _item0 : struct.getRealOptions()){
				
				oprot.writeI32(_item0); 
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getForeignname() != null) {
			
			oprot.writeFieldBegin("foreignname");
			oprot.writeString(struct.getForeignname());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getExternaldata() != null) {
			
			oprot.writeFieldBegin("externaldata");
			oprot.writeString(struct.getExternaldata());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Option bean) throws OspException {
		
		
	}
	
	
}