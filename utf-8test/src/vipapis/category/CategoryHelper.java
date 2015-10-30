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

public  class CategoryHelper implements com.vip.osp.sdk.base.BeanSerializer<Category>
{
	
	public static final CategoryHelper OBJ = new CategoryHelper();
	
	public static CategoryHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Category struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("category_id".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setCategory_id(value);
				}
				
				
				
				
				
				if ("category_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCategory_name(value);
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
				
				
				
				
				
				if ("category_type".equals(schemeField.trim())){
					
					vipapis.category.CategoryType value;
					
					value = null;
					String name = iprot.readString();
					vipapis.category.CategoryType[] values = vipapis.category.CategoryType.values(); 
					for(vipapis.category.CategoryType v : values){
						
						if(v.name().equals(name)){
							
							value = v;
							break;
						}
						
					}
					
					
					struct.setCategory_type(value);
				}
				
				
				
				
				
				if ("keywords".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setKeywords(value);
				}
				
				
				
				
				
				if ("flags".equals(schemeField.trim())){
					
					Long value;
					value = iprot.readI64(); 
					
					struct.setFlags(value);
				}
				
				
				
				
				
				if ("hierarchy_id".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setHierarchy_id(value);
				}
				
				
				
				
				
				if ("last_updatetime".equals(schemeField.trim())){
					
					Long value;
					value = iprot.readI64(); 
					
					struct.setLast_updatetime(value);
				}
				
				
				
				
				
				if ("related_categories".equals(schemeField.trim())){
					
					List<Integer> value;
					
					value = new ArrayList<Integer>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							int elem0;
							elem0 = iprot.readI32(); 
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setRelated_categories(value);
				}
				
				
				
				
				
				if ("children".equals(schemeField.trim())){
					
					List<vipapis.category.Category> value;
					
					value = new ArrayList<vipapis.category.Category>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.category.Category elem1;
							
							elem1 = new vipapis.category.Category();
							vipapis.category.CategoryHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setChildren(value);
				}
				
				
				
				
				
				if ("mapping".equals(schemeField.trim())){
					
					List<vipapis.category.CategoryMapping> value;
					
					value = new ArrayList<vipapis.category.CategoryMapping>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.category.CategoryMapping elem3;
							
							elem3 = new vipapis.category.CategoryMapping();
							vipapis.category.CategoryMappingHelper.getInstance().read(elem3, iprot);
							
							value.add(elem3);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setMapping(value);
				}
				
				
				
				
				
				if ("major_parent_category_id".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setMajor_parent_category_id(value);
				}
				
				
				
				
				
				if ("salve_parent_category_ids".equals(schemeField.trim())){
					
					List<Integer> value;
					
					value = new ArrayList<Integer>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							int elem5;
							elem5 = iprot.readI32(); 
							
							value.add(elem5);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setSalve_parent_category_ids(value);
				}
				
				
				
				
				
				if ("attributes".equals(schemeField.trim())){
					
					List<vipapis.category.Attribute> value;
					
					value = new ArrayList<vipapis.category.Attribute>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.category.Attribute elem6;
							
							elem6 = new vipapis.category.Attribute();
							vipapis.category.AttributeHelper.getInstance().read(elem6, iprot);
							
							value.add(elem6);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setAttributes(value);
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
	
	
	public void write(Category struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("category_id");
		oprot.writeI32(struct.getCategory_id()); 
		
		oprot.writeFieldEnd();
		
		if(struct.getCategory_name() != null) {
			
			oprot.writeFieldBegin("category_name");
			oprot.writeString(struct.getCategory_name());
			
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
		
		
		if(struct.getCategory_type() != null) {
			
			oprot.writeFieldBegin("category_type");
			oprot.writeString(struct.getCategory_type().name());  
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getKeywords() != null) {
			
			oprot.writeFieldBegin("keywords");
			oprot.writeString(struct.getKeywords());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getFlags() != null) {
			
			oprot.writeFieldBegin("flags");
			oprot.writeI64(struct.getFlags()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getHierarchy_id() != null) {
			
			oprot.writeFieldBegin("hierarchy_id");
			oprot.writeI32(struct.getHierarchy_id()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getLast_updatetime() != null) {
			
			oprot.writeFieldBegin("last_updatetime");
			oprot.writeI64(struct.getLast_updatetime()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getRelated_categories() != null) {
			
			oprot.writeFieldBegin("related_categories");
			
			oprot.writeListBegin();
			for(int _item0 : struct.getRelated_categories()){
				
				oprot.writeI32(_item0); 
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getChildren() != null) {
			
			oprot.writeFieldBegin("children");
			
			oprot.writeListBegin();
			for(vipapis.category.Category _item0 : struct.getChildren()){
				
				
				vipapis.category.CategoryHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMapping() != null) {
			
			oprot.writeFieldBegin("mapping");
			
			oprot.writeListBegin();
			for(vipapis.category.CategoryMapping _item0 : struct.getMapping()){
				
				
				vipapis.category.CategoryMappingHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMajor_parent_category_id() != null) {
			
			oprot.writeFieldBegin("major_parent_category_id");
			oprot.writeI32(struct.getMajor_parent_category_id()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSalve_parent_category_ids() != null) {
			
			oprot.writeFieldBegin("salve_parent_category_ids");
			
			oprot.writeListBegin();
			for(int _item0 : struct.getSalve_parent_category_ids()){
				
				oprot.writeI32(_item0); 
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAttributes() != null) {
			
			oprot.writeFieldBegin("attributes");
			
			oprot.writeListBegin();
			for(vipapis.category.Attribute _item0 : struct.getAttributes()){
				
				
				vipapis.category.AttributeHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Category bean) throws OspException {
		
		
	}
	
	
}