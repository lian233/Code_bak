package vipapis.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public class ScheduleServiceHelper {
	
	
	
	
	public static class getScheduleList_args {
		
		/**
		* 分仓代码
		* @sampleValue warehouse warehouse=VIP_NH
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* 查询开始日期(格式yyyy-MM-dd)
		* @sampleValue start_date start_date=2014-06-18
		*/
		
		private String start_date;
		
		/**
		* 查询结束日期(格式yyyy-MM-dd)
		* @sampleValue end_date end_date=2014-06-20
		*/
		
		private String end_date;
		
		/**
		* 页码
		* @sampleValue page page=1
		*/
		
		private Integer page;
		
		/**
		* 每页记录数
		* @sampleValue limit limit=20
		*/
		
		private Integer limit;
		
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getStart_date(){
			return this.start_date;
		}
		
		public void setStart_date(String value){
			this.start_date = value;
		}
		public String getEnd_date(){
			return this.end_date;
		}
		
		public void setEnd_date(String value){
			this.end_date = value;
		}
		public Integer getPage(){
			return this.page;
		}
		
		public void setPage(Integer value){
			this.page = value;
		}
		public Integer getLimit(){
			return this.limit;
		}
		
		public void setLimit(Integer value){
			this.limit = value;
		}
		
	}
	
	
	
	
	public static class getSchedules_args {
		
		/**
		* 分仓代码
		* @sampleValue warehouse warehouse=VIP_NH
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* 查询开始日期(格式yyyy-MM-dd)
		* @sampleValue start_date start_date=2014-06-18
		*/
		
		private String start_date;
		
		/**
		* 查询结束日期(格式yyyy-MM-dd)
		* @sampleValue end_date end_date=2014-06-20
		*/
		
		private String end_date;
		
		/**
		* 档期ID
		* @sampleValue schedule_id 204565
		*/
		
		private String schedule_id;
		
		/**
		* 频道ID
		* @sampleValue channel_id 10
		*/
		
		private String channel_id;
		
		/**
		* 页码
		* @sampleValue page page=1
		*/
		
		private Integer page;
		
		/**
		* 每页记录数
		* @sampleValue limit limit=20
		*/
		
		private Integer limit;
		
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getStart_date(){
			return this.start_date;
		}
		
		public void setStart_date(String value){
			this.start_date = value;
		}
		public String getEnd_date(){
			return this.end_date;
		}
		
		public void setEnd_date(String value){
			this.end_date = value;
		}
		public String getSchedule_id(){
			return this.schedule_id;
		}
		
		public void setSchedule_id(String value){
			this.schedule_id = value;
		}
		public String getChannel_id(){
			return this.channel_id;
		}
		
		public void setChannel_id(String value){
			this.channel_id = value;
		}
		public Integer getPage(){
			return this.page;
		}
		
		public void setPage(Integer value){
			this.page = value;
		}
		public Integer getLimit(){
			return this.limit;
		}
		
		public void setLimit(Integer value){
			this.limit = value;
		}
		
	}
	
	
	
	
	public static class getScheduleList_result {
		
		/**
		*/
		
		private List<vipapis.schedule.Schedule> success;
		
		public List<vipapis.schedule.Schedule> getSuccess(){
			return this.success;
		}
		
		public void setSuccess(List<vipapis.schedule.Schedule> value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getSchedules_result {
		
		/**
		*/
		
		private vipapis.schedule.GetScheduleListResponse success;
		
		public vipapis.schedule.GetScheduleListResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.schedule.GetScheduleListResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getScheduleList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getScheduleList_args>
	{
		
		public static final getScheduleList_argsHelper OBJ = new getScheduleList_argsHelper();
		
		public static getScheduleList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getScheduleList_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.common.Warehouse value;
				
				value = null;
				String name = iprot.readString();
				vipapis.common.Warehouse[] values = vipapis.common.Warehouse.values(); 
				for(vipapis.common.Warehouse v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setWarehouse(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStart_date(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEnd_date(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setPage(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setLimit(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getScheduleList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("warehouse");
			oprot.writeString(struct.getWarehouse().name());  
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("start_date");
			oprot.writeString(struct.getStart_date());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("end_date");
			oprot.writeString(struct.getEnd_date());
			
			oprot.writeFieldEnd();
			
			if(struct.getPage() != null) {
				
				oprot.writeFieldBegin("page");
				oprot.writeI32(struct.getPage()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getLimit() != null) {
				
				oprot.writeFieldBegin("limit");
				oprot.writeI32(struct.getLimit()); 
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getScheduleList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getSchedules_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getSchedules_args>
	{
		
		public static final getSchedules_argsHelper OBJ = new getSchedules_argsHelper();
		
		public static getSchedules_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getSchedules_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.common.Warehouse value;
				
				value = null;
				String name = iprot.readString();
				vipapis.common.Warehouse[] values = vipapis.common.Warehouse.values(); 
				for(vipapis.common.Warehouse v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setWarehouse(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStart_date(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEnd_date(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSchedule_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setChannel_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setPage(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setLimit(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getSchedules_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getWarehouse() != null) {
				
				oprot.writeFieldBegin("warehouse");
				oprot.writeString(struct.getWarehouse().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldBegin("start_date");
			oprot.writeString(struct.getStart_date());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("end_date");
			oprot.writeString(struct.getEnd_date());
			
			oprot.writeFieldEnd();
			
			if(struct.getSchedule_id() != null) {
				
				oprot.writeFieldBegin("schedule_id");
				oprot.writeString(struct.getSchedule_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getChannel_id() != null) {
				
				oprot.writeFieldBegin("channel_id");
				oprot.writeString(struct.getChannel_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getPage() != null) {
				
				oprot.writeFieldBegin("page");
				oprot.writeI32(struct.getPage()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getLimit() != null) {
				
				oprot.writeFieldBegin("limit");
				oprot.writeI32(struct.getLimit()); 
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getSchedules_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getScheduleList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getScheduleList_result>
	{
		
		public static final getScheduleList_resultHelper OBJ = new getScheduleList_resultHelper();
		
		public static getScheduleList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getScheduleList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.schedule.Schedule> value;
				
				value = new ArrayList<vipapis.schedule.Schedule>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.schedule.Schedule elem0;
						
						elem0 = new vipapis.schedule.Schedule();
						vipapis.schedule.ScheduleHelper.getInstance().read(elem0, iprot);
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getScheduleList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				oprot.writeListBegin();
				for(vipapis.schedule.Schedule _item0 : struct.getSuccess()){
					
					
					vipapis.schedule.ScheduleHelper.getInstance().write(_item0, oprot);
					
				}
				
				oprot.writeListEnd();
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getScheduleList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getSchedules_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getSchedules_result>
	{
		
		public static final getSchedules_resultHelper OBJ = new getSchedules_resultHelper();
		
		public static getSchedules_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getSchedules_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.schedule.GetScheduleListResponse value;
				
				value = new vipapis.schedule.GetScheduleListResponse();
				vipapis.schedule.GetScheduleListResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getSchedules_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.schedule.GetScheduleListResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getSchedules_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class ScheduleServiceClient extends OspRestStub implements ScheduleService  {
		
		
		public ScheduleServiceClient() {
			
			super("1.0.1", "vipapis.schedule.ScheduleService");
		}
		
		
		
		public List<vipapis.schedule.Schedule> getScheduleList(vipapis.common.Warehouse warehouse,String start_date,String end_date,Integer page,Integer limit) throws OspException {
			
			send_getScheduleList(warehouse,start_date,end_date,page,limit);
			return recv_getScheduleList(); 
			
		}
		
		
		private void send_getScheduleList(vipapis.common.Warehouse warehouse,String start_date,String end_date,Integer page,Integer limit) throws OspException {
			
			initInvocation("getScheduleList");
			
			getScheduleList_args args = new getScheduleList_args();
			args.setWarehouse(warehouse);
			args.setStart_date(start_date);
			args.setEnd_date(end_date);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getScheduleList_argsHelper.getInstance());
		}
		
		
		private List<vipapis.schedule.Schedule> recv_getScheduleList() throws OspException {
			
			getScheduleList_result result = new getScheduleList_result();
			receiveBase(result, getScheduleList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.schedule.GetScheduleListResponse getSchedules(vipapis.common.Warehouse warehouse,String start_date,String end_date,String schedule_id,String channel_id,Integer page,Integer limit) throws OspException {
			
			send_getSchedules(warehouse,start_date,end_date,schedule_id,channel_id,page,limit);
			return recv_getSchedules(); 
			
		}
		
		
		private void send_getSchedules(vipapis.common.Warehouse warehouse,String start_date,String end_date,String schedule_id,String channel_id,Integer page,Integer limit) throws OspException {
			
			initInvocation("getSchedules");
			
			getSchedules_args args = new getSchedules_args();
			args.setWarehouse(warehouse);
			args.setStart_date(start_date);
			args.setEnd_date(end_date);
			args.setSchedule_id(schedule_id);
			args.setChannel_id(channel_id);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getSchedules_argsHelper.getInstance());
		}
		
		
		private vipapis.schedule.GetScheduleListResponse recv_getSchedules() throws OspException {
			
			getSchedules_result result = new getSchedules_result();
			receiveBase(result, getSchedules_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}