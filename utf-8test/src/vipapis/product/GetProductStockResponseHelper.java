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

public  class GetProductStockResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetProductStockResponse>
{
	
	public static final GetProductStockResponseHelper OBJ = new GetProductStockResponseHelper();
	
	public static GetProductStockResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetProductStockResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("products".equals(schemeField.trim())){
					
					List<vipapis.product.ProductStock> value;
					
					value = new ArrayList<vipapis.product.ProductStock>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.product.ProductStock elem1;
							
							elem1 = new vipapis.product.ProductStock();
							vipapis.product.ProductStockHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
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
				
				
				
				
				
				if ("nextCursorMark".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setNextCursorMark(value);
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
	
	
	public void write(GetProductStockResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("products");
		
		oprot.writeListBegin();
		for(vipapis.product.ProductStock _item0 : struct.getProducts()){
			
			
			vipapis.product.ProductStockHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("total");
		oprot.writeI32(struct.getTotal()); 
		
		oprot.writeFieldEnd();
		
		if(struct.getNextCursorMark() != null) {
			
			oprot.writeFieldBegin("nextCursorMark");
			oprot.writeString(struct.getNextCursorMark());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(GetProductStockResponse bean) throws OspException {
		
		
	}
	
	
}