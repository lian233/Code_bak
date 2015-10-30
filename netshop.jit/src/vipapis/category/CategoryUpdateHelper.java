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

public  class CategoryUpdateHelper implements com.vip.osp.sdk.base.BeanSerializer<CategoryUpdate>
{
	
	public static final CategoryUpdateHelper OBJ = new CategoryUpdateHelper();
	
	public static CategoryUpdateHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(CategoryUpdate struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("updateType".equals(schemeField.trim())){
					
					vipapis.category.UpdateType value;
					
					value = null;
					String name = iprot.readString();
					vipapis.category.UpdateType[] values = vipapis.category.UpdateType.values(); 
					for(vipapis.category.UpdateType v : values){
						
						if(v.name().equals(name)){
							
							value = v;
							break;
						}
						
					}
					
					
					struct.setUpdateType(value);
				}
				
				
				
				
				
				if ("category".equals(schemeField.trim())){
					
					vipapis.category.Category value;
					
					value = new vipapis.category.Category();
					vipapis.category.CategoryHelper.getInstance().read(value, iprot);
					
					struct.setCategory(value);
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
	
	
	public void write(CategoryUpdate struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("updateType");
		oprot.writeString(struct.getUpdateType().name());  
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("category");
		
		vipapis.category.CategoryHelper.getInstance().write(struct.getCategory(), oprot);
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(CategoryUpdate bean) throws OspException {
		
		
	}
	
	
}