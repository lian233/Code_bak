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

public  class GetOrderListResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetOrderListResponse>
{
	
	public static final GetOrderListResponseHelper OBJ = new GetOrderListResponseHelper();
	
	public static GetOrderListResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetOrderListResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("dvd_order_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.DvdOrder> value;
					
					value = new ArrayList<vipapis.delivery.DvdOrder>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.DvdOrder elem1;
							
							elem1 = new vipapis.delivery.DvdOrder();
							vipapis.delivery.DvdOrderHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setDvd_order_list(value);
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
	
	
	public void write(GetOrderListResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getDvd_order_list() != null) {
			
			oprot.writeFieldBegin("dvd_order_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.DvdOrder _item0 : struct.getDvd_order_list()){
				
				
				vipapis.delivery.DvdOrderHelper.getInstance().write(_item0, oprot);
				
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
	
	
	public void validate(GetOrderListResponse bean) throws OspException {
		
		
	}
	
	
}