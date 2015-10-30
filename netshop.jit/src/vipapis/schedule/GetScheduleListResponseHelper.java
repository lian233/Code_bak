package vipapis.schedule;

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

public  class GetScheduleListResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetScheduleListResponse>
{
	
	public static final GetScheduleListResponseHelper OBJ = new GetScheduleListResponseHelper();
	
	public static GetScheduleListResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetScheduleListResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("schedules".equals(schemeField.trim())){
					
					List<vipapis.schedule.Schedule> value;
					
					value = new ArrayList<vipapis.schedule.Schedule>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.schedule.Schedule elem1;
							
							elem1 = new vipapis.schedule.Schedule();
							vipapis.schedule.ScheduleHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setSchedules(value);
				}
				
				
				
				
				
				if ("total".equals(schemeField.trim())){
					
					int value;
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
	
	
	public void write(GetScheduleListResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("schedules");
		
		oprot.writeListBegin();
		for(vipapis.schedule.Schedule _item0 : struct.getSchedules()){
			
			
			vipapis.schedule.ScheduleHelper.getInstance().write(_item0, oprot);
			
		}
		
		oprot.writeListEnd();
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("total");
		oprot.writeI32(struct.getTotal()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(GetScheduleListResponse bean) throws OspException {
		
		
	}
	
	
}