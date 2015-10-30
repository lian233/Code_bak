package vipapis.brand;

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

public  class BrandInfoHelper implements com.vip.osp.sdk.base.BeanSerializer<BrandInfo>
{
	
	public static final BrandInfoHelper OBJ = new BrandInfoHelper();
	
	public static BrandInfoHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(BrandInfo struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("brand_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_id(value);
				}
				
				
				
				
				
				if ("brand_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_name(value);
				}
				
				
				
				
				
				if ("brand_name_eng".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_name_eng(value);
				}
				
				
				
				
				
				if ("brand_name_pinyin".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_name_pinyin(value);
				}
				
				
				
				
				
				if ("brand_logo".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_logo(value);
				}
				
				
				
				
				
				if ("brand_description".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_description(value);
				}
				
				
				
				
				
				if ("brand_url".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_url(value);
				}
				
				
				
				
				
				if ("brand_level".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_level(value);
				}
				
				
				
				
				
				if ("last_modify_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setLast_modify_time(value);
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
	
	
	public void write(BrandInfo struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("brand_id");
		oprot.writeString(struct.getBrand_id());
		
		oprot.writeFieldEnd();
		
		if(struct.getBrand_name() != null) {
			
			oprot.writeFieldBegin("brand_name");
			oprot.writeString(struct.getBrand_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_name_eng() != null) {
			
			oprot.writeFieldBegin("brand_name_eng");
			oprot.writeString(struct.getBrand_name_eng());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_name_pinyin() != null) {
			
			oprot.writeFieldBegin("brand_name_pinyin");
			oprot.writeString(struct.getBrand_name_pinyin());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_logo() != null) {
			
			oprot.writeFieldBegin("brand_logo");
			oprot.writeString(struct.getBrand_logo());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_description() != null) {
			
			oprot.writeFieldBegin("brand_description");
			oprot.writeString(struct.getBrand_description());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_url() != null) {
			
			oprot.writeFieldBegin("brand_url");
			oprot.writeString(struct.getBrand_url());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_level() != null) {
			
			oprot.writeFieldBegin("brand_level");
			oprot.writeString(struct.getBrand_level());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getLast_modify_time() != null) {
			
			oprot.writeFieldBegin("last_modify_time");
			oprot.writeString(struct.getLast_modify_time());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(BrandInfo bean) throws OspException {
		
		
	}
	
	
}