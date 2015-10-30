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

public  class PickHelper implements com.vip.osp.sdk.base.BeanSerializer<Pick>
{
	
	public static final PickHelper OBJ = new PickHelper();
	
	public static PickHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Pick struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("po_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPo_no(value);
				}
				
				
				
				
				
				if ("pick_no".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPick_no(value);
				}
				
				
				
				
				
				if ("co_mode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCo_mode(value);
				}
				
				
				
				
				
				if ("sell_site".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSell_site(value);
				}
				
				
				
				
				
				if ("order_cate".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_cate(value);
				}
				
				
				
				
				
				if ("pick_num".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setPick_num(value);
				}
				
				
				
				
				
				if ("create_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCreate_time(value);
				}
				
				
				
				
				
				if ("first_export_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setFirst_export_time(value);
				}
				
				
				
				
				
				if ("export_num".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setExport_num(value);
				}
				
				
				
				
				
				if ("delivery_status".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setDelivery_status(value);
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
	
	
	public void write(Pick struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getPo_no() != null) {
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPick_no() != null) {
			
			oprot.writeFieldBegin("pick_no");
			oprot.writeString(struct.getPick_no());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCo_mode() != null) {
			
			oprot.writeFieldBegin("co_mode");
			oprot.writeString(struct.getCo_mode());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSell_site() != null) {
			
			oprot.writeFieldBegin("sell_site");
			oprot.writeString(struct.getSell_site());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOrder_cate() != null) {
			
			oprot.writeFieldBegin("order_cate");
			oprot.writeString(struct.getOrder_cate());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPick_num() != null) {
			
			oprot.writeFieldBegin("pick_num");
			oprot.writeI32(struct.getPick_num()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCreate_time() != null) {
			
			oprot.writeFieldBegin("create_time");
			oprot.writeString(struct.getCreate_time());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getFirst_export_time() != null) {
			
			oprot.writeFieldBegin("first_export_time");
			oprot.writeString(struct.getFirst_export_time());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getExport_num() != null) {
			
			oprot.writeFieldBegin("export_num");
			oprot.writeI32(struct.getExport_num()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getDelivery_status() != null) {
			
			oprot.writeFieldBegin("delivery_status");
			oprot.writeI32(struct.getDelivery_status()); 
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Pick bean) throws OspException {
		
		
	}
	
	
}