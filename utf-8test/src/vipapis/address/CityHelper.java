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

public  class CityHelper implements com.vip.osp.sdk.base.BeanSerializer<City>
{
	
	public static final CityHelper OBJ = new CityHelper();
	
	public static CityHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(City struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("city_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCity_id(value);
				}
				
				
				
				
				
				if ("city_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCity_name(value);
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
	
	
	public void write(City struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("city_id");
		oprot.writeString(struct.getCity_id());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("city_name");
		oprot.writeString(struct.getCity_name());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(City bean) throws OspException {
		
		
	}
	
	
}