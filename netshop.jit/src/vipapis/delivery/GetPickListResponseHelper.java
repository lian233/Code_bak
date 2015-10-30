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

public  class GetPickListResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetPickListResponse>
{
	
	public static final GetPickListResponseHelper OBJ = new GetPickListResponseHelper();
	
	public static GetPickListResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetPickListResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("picks".equals(schemeField.trim())){
					
					List<vipapis.delivery.Pick> value;
					
					value = new ArrayList<vipapis.delivery.Pick>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.Pick elem0;
							
							elem0 = new vipapis.delivery.Pick();
							vipapis.delivery.PickHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setPicks(value);
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
	
	
	public void write(GetPickListResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getPicks() != null) {
			
			oprot.writeFieldBegin("picks");
			
			oprot.writeListBegin();
			for(vipapis.delivery.Pick _item0 : struct.getPicks()){
				
				
				vipapis.delivery.PickHelper.getInstance().write(_item0, oprot);
				
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
	
	
	public void validate(GetPickListResponse bean) throws OspException {
		
		
	}
	
	
}