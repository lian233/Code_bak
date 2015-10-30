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

public  class CategoryMappingHelper implements com.vip.osp.sdk.base.BeanSerializer<CategoryMapping>
{
	
	public static final CategoryMappingHelper OBJ = new CategoryMappingHelper();
	
	public static CategoryMappingHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(CategoryMapping struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("sourcecategory".equals(schemeField.trim())){
					
					vipapis.category.Category value;
					
					value = new vipapis.category.Category();
					vipapis.category.CategoryHelper.getInstance().read(value, iprot);
					
					struct.setSourcecategory(value);
				}
				
				
				
				
				
				if ("filter".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setFilter(value);
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
	
	
	public void write(CategoryMapping struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("sourcecategory");
		
		vipapis.category.CategoryHelper.getInstance().write(struct.getSourcecategory(), oprot);
		
		oprot.writeFieldEnd();
		
		if(struct.getFilter() != null) {
			
			oprot.writeFieldBegin("filter");
			oprot.writeString(struct.getFilter());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(CategoryMapping bean) throws OspException {
		
		
	}
	
	
}