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

public  class RefuseOrReturnOrderHelper implements com.vip.osp.sdk.base.BeanSerializer<RefuseOrReturnOrder>
{
	
	public static final RefuseOrReturnOrderHelper OBJ = new RefuseOrReturnOrderHelper();
	
	public static RefuseOrReturnOrderHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(RefuseOrReturnOrder struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("order_id".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_id(value);
				}
				
				
				
				
				
				if ("carrier_shortname".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCarrier_shortname(value);
				}
				
				
				
				
				
				if ("transport_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setTransport_no(value);
				}
				
				
				
				
				
				if ("refuse_or_return_product_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.RefuseOrReturnProduct> value;
					
					value = new ArrayList<vipapis.delivery.RefuseOrReturnProduct>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.RefuseOrReturnProduct elem0;
							
							elem0 = new vipapis.delivery.RefuseOrReturnProduct();
							vipapis.delivery.RefuseOrReturnProductHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setRefuse_or_return_product_list(value);
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
	
	
	public void write(RefuseOrReturnOrder struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("order_id");
		oprot.writeString(struct.getOrder_id());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("carrier_shortname");
		oprot.writeString(struct.getCarrier_shortname());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("transport_no");
		oprot.writeString(struct.getTransport_no());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("refuse_or_return_product_list");
		
		oprot.writeListBegin();
		for(vipapis.delivery.RefuseOrReturnProduct _item0 : struct.getRefuse_or_return_product_list()){
			
			
			vipapis.delivery.RefuseOrReturnProductHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(RefuseOrReturnOrder bean) throws OspException {
		
		
	}
	
	
}