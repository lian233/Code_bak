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

public  class GetReturnListResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetReturnListResponse>
{
	
	public static final GetReturnListResponseHelper OBJ = new GetReturnListResponseHelper();
	
	public static GetReturnListResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetReturnListResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("dvd_return_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.DvdReturn> value;
					
					value = new ArrayList<vipapis.delivery.DvdReturn>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.DvdReturn elem0;
							
							elem0 = new vipapis.delivery.DvdReturn();
							vipapis.delivery.DvdReturnHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setDvd_return_list(value);
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
	
	
	public void write(GetReturnListResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getDvd_return_list() != null) {
			
			oprot.writeFieldBegin("dvd_return_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.DvdReturn _item0 : struct.getDvd_return_list()){
				
				
				vipapis.delivery.DvdReturnHelper.getInstance().write(_item0, oprot);
				
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
	
	
	public void validate(GetReturnListResponse bean) throws OspException {
		
		
	}
	
	
}