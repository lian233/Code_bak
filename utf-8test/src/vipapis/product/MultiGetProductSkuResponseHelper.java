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

public  class MultiGetProductSkuResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<MultiGetProductSkuResponse>
{
	
	public static final MultiGetProductSkuResponseHelper OBJ = new MultiGetProductSkuResponseHelper();
	
	public static MultiGetProductSkuResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(MultiGetProductSkuResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("products".equals(schemeField.trim())){
					
					List<vipapis.product.ProductSkuInfo> value;
					
					value = new ArrayList<vipapis.product.ProductSkuInfo>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.product.ProductSkuInfo elem0;
							
							elem0 = new vipapis.product.ProductSkuInfo();
							vipapis.product.ProductSkuInfoHelper.getInstance().read(elem0, iprot);
							
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
	
	
	public void write(MultiGetProductSkuResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("products");
		
		oprot.writeListBegin();
		for(vipapis.product.ProductSkuInfo _item0 : struct.getProducts()){
			
			
			vipapis.product.ProductSkuInfoHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("total");
		oprot.writeI32(struct.getTotal()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(MultiGetProductSkuResponse bean) throws OspException {
		
		
	}
	
	
}