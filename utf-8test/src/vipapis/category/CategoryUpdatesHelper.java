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

public  class CategoryUpdatesHelper implements com.vip.osp.sdk.base.BeanSerializer<CategoryUpdates>
{
	
	public static final CategoryUpdatesHelper OBJ = new CategoryUpdatesHelper();
	
	public static CategoryUpdatesHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(CategoryUpdates struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("sinceUpdateTime".equals(schemeField.trim())){
					
					long value;
					value = iprot.readI64(); 
					
					struct.setSinceUpdateTime(value);
				}
				
				
				
				
				
				if ("lastUpdateTime".equals(schemeField.trim())){
					
					long value;
					value = iprot.readI64(); 
					
					struct.setLastUpdateTime(value);
				}
				
				
				
				
				
				if ("categories".equals(schemeField.trim())){
					
					List<vipapis.category.CategoryUpdate> value;
					
					value = new ArrayList<vipapis.category.CategoryUpdate>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.category.CategoryUpdate elem0;
							
							elem0 = new vipapis.category.CategoryUpdate();
							vipapis.category.CategoryUpdateHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setCategories(value);
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
	
	
	public void write(CategoryUpdates struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("sinceUpdateTime");
		oprot.writeI64(struct.getSinceUpdateTime()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("lastUpdateTime");
		oprot.writeI64(struct.getLastUpdateTime()); 
		
		oprot.writeFieldEnd();
		
		if(struct.getCategories() != null) {
			
			oprot.writeFieldBegin("categories");
			
			oprot.writeListBegin();
			for(vipapis.category.CategoryUpdate _item0 : struct.getCategories()){
				
				
				vipapis.category.CategoryUpdateHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(CategoryUpdates bean) throws OspException {
		
		
	}
	
	
}