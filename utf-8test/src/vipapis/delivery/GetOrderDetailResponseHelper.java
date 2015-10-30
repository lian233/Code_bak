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

public  class GetOrderDetailResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetOrderDetailResponse>
{
	
	public static final GetOrderDetailResponseHelper OBJ = new GetOrderDetailResponseHelper();
	
	public static GetOrderDetailResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetOrderDetailResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("orderDetails".equals(schemeField.trim())){
					
					List<vipapis.delivery.DvdOrderDetail> value;
					
					value = new ArrayList<vipapis.delivery.DvdOrderDetail>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.DvdOrderDetail elem0;
							
							elem0 = new vipapis.delivery.DvdOrderDetail();
							vipapis.delivery.DvdOrderDetailHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setOrderDetails(value);
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
	
	
	public void write(GetOrderDetailResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getOrderDetails() != null) {
			
			oprot.writeFieldBegin("orderDetails");
			
			oprot.writeListBegin();
			for(vipapis.delivery.DvdOrderDetail _item0 : struct.getOrderDetails()){
				
				
				vipapis.delivery.DvdOrderDetailHelper.getInstance().write(_item0, oprot);
				
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
	
	
	public void validate(GetOrderDetailResponse bean) throws OspException {
		
		
	}
	
	
}