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

public  class GetProductListResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetProductListResponse>
{
	
	public static final GetProductListResponseHelper OBJ = new GetProductListResponseHelper();
	
	public static GetProductListResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetProductListResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("products".equals(schemeField.trim())){
					
					List<vipapis.product.Product> value;
					
					value = new ArrayList<vipapis.product.Product>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.product.Product elem0;
							
							elem0 = new vipapis.product.Product();
							vipapis.product.ProductHelper.getInstance().read(elem0, iprot);
							
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
	
	
	public void write(GetProductListResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("products");
		
		oprot.writeListBegin();
		for(vipapis.product.Product _item0 : struct.getProducts()){
			
			
			vipapis.product.ProductHelper.getInstance().write(_item0, oprot);
			
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
	
	
	public void validate(GetProductListResponse bean) throws OspException {
		
		
	}
	
	
}