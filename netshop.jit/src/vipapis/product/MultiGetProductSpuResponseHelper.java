package vipapis.product;

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

public  class MultiGetProductSpuResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<MultiGetProductSpuResponse>
{
	
	public static final MultiGetProductSpuResponseHelper OBJ = new MultiGetProductSpuResponseHelper();
	
	public static MultiGetProductSpuResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(MultiGetProductSpuResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("products".equals(schemeField.trim())){
					
					List<vipapis.product.ProductSpuInfo> value;
					
					value = new ArrayList<vipapis.product.ProductSpuInfo>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.product.ProductSpuInfo elem0;
							
							elem0 = new vipapis.product.ProductSpuInfo();
							vipapis.product.ProductSpuInfoHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setProducts(value);
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
	
	
	public void write(MultiGetProductSpuResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("products");
		
		oprot.writeListBegin();
		for(vipapis.product.ProductSpuInfo _item0 : struct.getProducts()){
			
			
			vipapis.product.ProductSpuInfoHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("total");
		oprot.writeI32(struct.getTotal()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(MultiGetProductSpuResponse bean) throws OspException {
		
		
	}
	
	
}