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

public  class ShipResultHelper implements com.vip.osp.sdk.base.BeanSerializer<ShipResult>
{
	
	public static final ShipResultHelper OBJ = new ShipResultHelper();
	
	public static ShipResultHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(ShipResult struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("success_num".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setSuccess_num(value);
				}
				
				
				
				
				
				if ("success_data".equals(schemeField.trim())){
					
					List<vipapis.delivery.Ship> value;
					
					value = new ArrayList<vipapis.delivery.Ship>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.Ship elem0;
							
							elem0 = new vipapis.delivery.Ship();
							vipapis.delivery.ShipHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setSuccess_data(value);
				}
				
				
				
				
				
				if ("fail_num".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setFail_num(value);
				}
				
				
				
				
				
				if ("fail_data".equals(schemeField.trim())){
					
					List<vipapis.delivery.Ship> value;
					
					value = new ArrayList<vipapis.delivery.Ship>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.Ship elem2;
							
							elem2 = new vipapis.delivery.Ship();
							vipapis.delivery.ShipHelper.getInstance().read(elem2, iprot);
							
							value.add(elem2);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setFail_data(value);
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
	
	
	public void write(ShipResult struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getSuccess_num() != null) {
			
			oprot.writeFieldBegin("success_num");
			oprot.writeI32(struct.getSuccess_num()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSuccess_data() != null) {
			
			oprot.writeFieldBegin("success_data");
			
			oprot.writeListBegin();
			for(vipapis.delivery.Ship _item0 : struct.getSuccess_data()){
				
				
				vipapis.delivery.ShipHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getFail_num() != null) {
			
			oprot.writeFieldBegin("fail_num");
			oprot.writeI32(struct.getFail_num()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getFail_data() != null) {
			
			oprot.writeFieldBegin("fail_data");
			
			oprot.writeListBegin();
			for(vipapis.delivery.Ship _item0 : struct.getFail_data()){
				
				
				vipapis.delivery.ShipHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(ShipResult bean) throws OspException {
		
		
	}
	
	
}