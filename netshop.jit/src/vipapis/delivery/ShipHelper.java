package vipapis.delivery;

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

public  class ShipHelper implements com.vip.osp.sdk.base.BeanSerializer<Ship>
{
	
	public static final ShipHelper OBJ = new ShipHelper();
	
	public static ShipHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Ship struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("order_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_id(value);
				}
				
				
				
				
				
				if ("carrier_code".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_code(value);
				}
				
				
				
				
				
				if ("carrier_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_name(value);
				}
				
				
				
				
				
				if ("package_type".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPackage_type(value);
				}
				
				
				
				
				
				if ("packages".equals(schemeField.trim())){
					
					List<vipapis.delivery.Package> value;
					
					value = new ArrayList<vipapis.delivery.Package>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.Package elem0;
							
							elem0 = new vipapis.delivery.Package();
							vipapis.delivery.PackageHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setPackages(value);
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
	
	
	public void write(Ship struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("order_id");
		oprot.writeString(struct.getOrder_id());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("carrier_code");
		oprot.writeString(struct.getCarrier_code());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("carrier_name");
		oprot.writeString(struct.getCarrier_name());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("package_type");
		oprot.writeString(struct.getPackage_type());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("packages");
		
		oprot.writeListBegin();
		for(vipapis.delivery.Package _item0 : struct.getPackages()){
			
			
			vipapis.delivery.PackageHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Ship bean) throws OspException {
		
		
	}
	
	
}