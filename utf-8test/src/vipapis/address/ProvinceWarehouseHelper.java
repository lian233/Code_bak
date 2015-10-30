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

public  class ProvinceWarehouseHelper implements com.vip.osp.sdk.base.BeanSerializer<ProvinceWarehouse>
{
	
	public static final ProvinceWarehouseHelper OBJ = new ProvinceWarehouseHelper();
	
	public static ProvinceWarehouseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(ProvinceWarehouse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("warehouse".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setWarehouse(value);
				}
				
				
				
				
				
				if ("children".equals(schemeField.trim())){
					
					List<vipapis.address.City> value;
					
					value = new ArrayList<vipapis.address.City>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.address.City elem0;
							
							elem0 = new vipapis.address.City();
							vipapis.address.CityHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setChildren(value);
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
	
	
	public void write(ProvinceWarehouse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("warehouse");
		oprot.writeString(struct.getWarehouse());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("children");
		
		oprot.writeListBegin();
		for(vipapis.address.City _item0 : struct.getChildren()){
			
			
			vipapis.address.CityHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(ProvinceWarehouse bean) throws OspException {
		
		
	}
	
	
}