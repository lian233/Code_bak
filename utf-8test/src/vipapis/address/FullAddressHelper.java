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

public  class FullAddressHelper implements com.vip.osp.sdk.base.BeanSerializer<FullAddress>
{
	
	public static final FullAddressHelper OBJ = new FullAddressHelper();
	
	public static FullAddressHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(FullAddress struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("cities".equals(schemeField.trim())){
					
					List<vipapis.address.City> value;
					
					value = new ArrayList<vipapis.address.City>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.address.City elem1;
							
							elem1 = new vipapis.address.City();
							vipapis.address.CityHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setCities(value);
				}
				
				
				
				
				
				if ("address".equals(schemeField.trim())){
					
					vipapis.address.Address value;
					
					value = new vipapis.address.Address();
					vipapis.address.AddressHelper.getInstance().read(value, iprot);
					
					struct.setAddress(value);
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
	
	
	public void write(FullAddress struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("cities");
		
		oprot.writeListBegin();
		for(vipapis.address.City _item0 : struct.getCities()){
			
			
			vipapis.address.CityHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		if(struct.getAddress() != null) {
			
			oprot.writeFieldBegin("address");
			
			vipapis.address.AddressHelper.getInstance().write(struct.getAddress(), oprot);
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(FullAddress bean) throws OspException {
		
		
	}
	
	
}