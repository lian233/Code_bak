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

public  class MultiGetBrandResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<MultiGetBrandResponse>
{
	
	public static final MultiGetBrandResponseHelper OBJ = new MultiGetBrandResponseHelper();
	
	public static MultiGetBrandResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(MultiGetBrandResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("brands".equals(schemeField.trim())){
					
					List<vipapis.brand.BrandInfo> value;
					
					value = new ArrayList<vipapis.brand.BrandInfo>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.brand.BrandInfo elem0;
							
							elem0 = new vipapis.brand.BrandInfo();
							vipapis.brand.BrandInfoHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setBrands(value);
				}
				
				
				
				
				
				if ("total".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setTotal(value);
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
	
	
	public void write(MultiGetBrandResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getBrands() != null) {
			
			oprot.writeFieldBegin("brands");
			
			oprot.writeListBegin();
			for(vipapis.brand.BrandInfo _item0 : struct.getBrands()){
				
				
				vipapis.brand.BrandInfoHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldBegin("total");
		oprot.writeI32(struct.getTotal()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(MultiGetBrandResponse bean) throws OspException {
		
		
	}
	
	
}