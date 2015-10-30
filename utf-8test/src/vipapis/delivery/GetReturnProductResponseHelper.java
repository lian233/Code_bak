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

public  class GetReturnProductResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetReturnProductResponse>
{
	
	public static final GetReturnProductResponseHelper OBJ = new GetReturnProductResponseHelper();
	
	public static GetReturnProductResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetReturnProductResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("dvd_return_product_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.DvdReturnProduct> value;
					
					value = new ArrayList<vipapis.delivery.DvdReturnProduct>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.DvdReturnProduct elem1;
							
							elem1 = new vipapis.delivery.DvdReturnProduct();
							vipapis.delivery.DvdReturnProductHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setDvd_return_product_list(value);
				}
				
				
				
				
				
				if ("total".equals(schemeField.trim())){
					
					Integer value;
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
	
	
	public void write(GetReturnProductResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getDvd_return_product_list() != null) {
			
			oprot.writeFieldBegin("dvd_return_product_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.DvdReturnProduct _item0 : struct.getDvd_return_product_list()){
				
				
				vipapis.delivery.DvdReturnProductHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTotal() != null) {
			
			oprot.writeFieldBegin("total");
			oprot.writeI32(struct.getTotal()); 
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(GetReturnProductResponse bean) throws OspException {
		
		
	}
	
	
}