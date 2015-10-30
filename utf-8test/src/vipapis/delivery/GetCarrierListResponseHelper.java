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

public  class GetCarrierListResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetCarrierListResponse>
{
	
	public static final GetCarrierListResponseHelper OBJ = new GetCarrierListResponseHelper();
	
	public static GetCarrierListResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetCarrierListResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("carriers".equals(schemeField.trim())){
					
					List<vipapis.delivery.Carrier> value;
					
					value = new ArrayList<vipapis.delivery.Carrier>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.Carrier elem1;
							
							elem1 = new vipapis.delivery.Carrier();
							vipapis.delivery.CarrierHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setCarriers(value);
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
	
	
	public void write(GetCarrierListResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getCarriers() != null) {
			
			oprot.writeFieldBegin("carriers");
			
			oprot.writeListBegin();
			for(vipapis.delivery.Carrier _item0 : struct.getCarriers()){
				
				
				vipapis.delivery.CarrierHelper.getInstance().write(_item0, oprot);
				
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
	
	
	public void validate(GetCarrierListResponse bean) throws OspException {
		
		
	}
	
	
}