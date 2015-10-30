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

public  class PackageHelper implements com.vip.osp.sdk.base.BeanSerializer<Package>
{
	
	public static final PackageHelper OBJ = new PackageHelper();
	
	public static PackageHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Package struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("package_product_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.PackageProduct> value;
					
					value = new ArrayList<vipapis.delivery.PackageProduct>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.PackageProduct elem0;
							
							elem0 = new vipapis.delivery.PackageProduct();
							vipapis.delivery.PackageProductHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setPackage_product_list(value);
				}
				
				
				
				
				
				if ("transport_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_no(value);
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
	
	
	public void write(Package struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("package_product_list");
		
		oprot.writeListBegin();
		for(vipapis.delivery.PackageProduct _item0 : struct.getPackage_product_list()){
			
			
			vipapis.delivery.PackageProductHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("transport_no");
		oprot.writeString(struct.getTransport_no());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Package bean) throws OspException {
		
		
	}
	
	
}