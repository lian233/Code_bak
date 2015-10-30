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

public  class ProductStockHelper implements com.vip.osp.sdk.base.BeanSerializer<ProductStock>
{
	
	public static final ProductStockHelper OBJ = new ProductStockHelper();
	
	public static ProductStockHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(ProductStock struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("product_id".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setProduct_id(value);
				}
				
				
				
				
				
				if ("product_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_name(value);
				}
				
				
				
				
				
				if ("sell_price".equals(schemeField.trim())){
					
					double value;
					value = iprot.readDouble();
					
					struct.setSell_price(value);
				}
				
				
				
				
				
				if ("hasStock".equals(schemeField.trim())){
					
					boolean value;
					value = iprot.readBool();
					
					struct.setHasStock(value);
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
	
	
	public void write(ProductStock struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("product_id");
		oprot.writeI32(struct.getProduct_id()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("product_name");
		oprot.writeString(struct.getProduct_name());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("sell_price");
		oprot.writeDouble(struct.getSell_price());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("hasStock");
		oprot.writeBool(struct.getHasStock());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(ProductStock bean) throws OspException {
		
		
	}
	
	
}