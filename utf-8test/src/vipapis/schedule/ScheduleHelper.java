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

public  class ScheduleHelper implements com.vip.osp.sdk.base.BeanSerializer<Schedule>
{
	
	public static final ScheduleHelper OBJ = new ScheduleHelper();
	
	public static ScheduleHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Schedule struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("schedule_id".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setSchedule_id(value);
				}
				
				
				
				
				
				if ("schedule_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSchedule_name(value);
				}
				
				
				
				
				
				if ("start_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setStart_time(value);
				}
				
				
				
				
				
				if ("end_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setEnd_time(value);
				}
				
				
				
				
				
				if ("index_image".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setIndex_image(value);
				}
				
				
				
				
				
				if ("index_advance_image".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setIndex_advance_image(value);
				}
				
				
				
				
				
				if ("schedule_self_logo".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSchedule_self_logo(value);
				}
				
				
				
				
				
				if ("logo".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setLogo(value);
				}
				
				
				
				
				
				if ("agio".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAgio(value);
				}
				
				
				
				
				
				if ("brand_store_sn".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem0;
							elem0 = iprot.readString();
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setBrand_store_sn(value);
				}
				
				
				
				
				
				if ("brand_name".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem1;
							elem1 = iprot.readString();
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setBrand_name(value);
				}
				
				
				
				
				
				if ("brand_name_eng".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem2;
							elem2 = iprot.readString();
							
							value.add(elem2);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setBrand_name_eng(value);
				}
				
				
				
				
				
				if ("brand_url".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem3;
							elem3 = iprot.readString();
							
							value.add(elem3);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setBrand_url(value);
				}
				
				
				
				
				
				if ("schedule_url".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSchedule_url(value);
				}
				
				
				
				
				
				if ("schedule_flash".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSchedule_flash(value);
				}
				
				
				
				
				
				if ("schedule_mobile_url".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSchedule_mobile_url(value);
				}
				
				
				
				
				
				if ("mobile_image_one".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setMobile_image_one(value);
				}
				
				
				
				
				
				if ("mobile_image_two".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setMobile_image_two(value);
				}
				
				
				
				
				
				if ("mobile_show_from".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setMobile_show_from(value);
				}
				
				
				
				
				
				if ("mobile_show_to".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setMobile_show_to(value);
				}
				
				
				
				
				
				if ("pc_show_from".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPc_show_from(value);
				}
				
				
				
				
				
				if ("pc_show_to".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setPc_show_to(value);
				}
				
				
				
				
				
				if ("channels".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem4;
							elem4 = iprot.readString();
							
							value.add(elem4);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setChannels(value);
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
	
	
	public void write(Schedule struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("schedule_id");
		oprot.writeI32(struct.getSchedule_id()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("schedule_name");
		oprot.writeString(struct.getSchedule_name());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("start_time");
		oprot.writeString(struct.getStart_time());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("end_time");
		oprot.writeString(struct.getEnd_time());
		
		oprot.writeFieldEnd();
		
		if(struct.getIndex_image() != null) {
			
			oprot.writeFieldBegin("index_image");
			oprot.writeString(struct.getIndex_image());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIndex_advance_image() != null) {
			
			oprot.writeFieldBegin("index_advance_image");
			oprot.writeString(struct.getIndex_advance_image());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSchedule_self_logo() != null) {
			
			oprot.writeFieldBegin("schedule_self_logo");
			oprot.writeString(struct.getSchedule_self_logo());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getLogo() != null) {
			
			oprot.writeFieldBegin("logo");
			oprot.writeString(struct.getLogo());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAgio() != null) {
			
			oprot.writeFieldBegin("agio");
			oprot.writeString(struct.getAgio());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_store_sn() != null) {
			
			oprot.writeFieldBegin("brand_store_sn");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getBrand_store_sn()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_name() != null) {
			
			oprot.writeFieldBegin("brand_name");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getBrand_name()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_name_eng() != null) {
			
			oprot.writeFieldBegin("brand_name_eng");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getBrand_name_eng()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_url() != null) {
			
			oprot.writeFieldBegin("brand_url");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getBrand_url()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSchedule_url() != null) {
			
			oprot.writeFieldBegin("schedule_url");
			oprot.writeString(struct.getSchedule_url());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSchedule_flash() != null) {
			
			oprot.writeFieldBegin("schedule_flash");
			oprot.writeString(struct.getSchedule_flash());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSchedule_mobile_url() != null) {
			
			oprot.writeFieldBegin("schedule_mobile_url");
			oprot.writeString(struct.getSchedule_mobile_url());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMobile_image_one() != null) {
			
			oprot.writeFieldBegin("mobile_image_one");
			oprot.writeString(struct.getMobile_image_one());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMobile_image_two() != null) {
			
			oprot.writeFieldBegin("mobile_image_two");
			oprot.writeString(struct.getMobile_image_two());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldBegin("mobile_show_from");
		oprot.writeString(struct.getMobile_show_from());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("mobile_show_to");
		oprot.writeString(struct.getMobile_show_to());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("pc_show_from");
		oprot.writeString(struct.getPc_show_from());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("pc_show_to");
		oprot.writeString(struct.getPc_show_to());
		
		oprot.writeFieldEnd();
		
		if(struct.getChannels() != null) {
			
			oprot.writeFieldBegin("channels");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getChannels()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(Schedule bean) throws OspException {
		
		
	}
	
	
}