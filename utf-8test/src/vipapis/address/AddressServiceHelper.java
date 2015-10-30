package vipapis.address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public class AddressServiceHelper {
	
	
	
	
	public static class getFullAddress_args {
		
		/**
		* 父级地址id
		* @sampleValue area_code area_code=914101101101
		*/
		
		private String area_code;
		
		/**
		* 是否显示港澳台地址,0:只显示大陆地址；1:只显示港澳台；-1:显示全部
		* @sampleValue is_show_gat Is_Show_GAT.SHOW_MAINLAND
		*/
		
		private vipapis.address.Is_Show_GAT is_show_gat;
		
		/**
		* 是否在遇到直辖的时候追加显示下级
		* @sampleValue is_bind is_bind=true
		*/
		
		private Boolean is_bind;
		
		public String getArea_code(){
			return this.area_code;
		}
		
		public void setArea_code(String value){
			this.area_code = value;
		}
		public vipapis.address.Is_Show_GAT getIs_show_gat(){
			return this.is_show_gat;
		}
		
		public void setIs_show_gat(vipapis.address.Is_Show_GAT value){
			this.is_show_gat = value;
		}
		public Boolean getIs_bind(){
			return this.is_bind;
		}
		
		public void setIs_bind(Boolean value){
			this.is_bind = value;
		}
		
	}
	
	
	
	
	public static class getProvinceWarehouse_args {
		
		/**
		* 是否显示港澳台地址,0:只显示大陆地址；1:只显示港澳台；-1:显示全部
		* @sampleValue is_show_gat Is_Show_GAT.SHOW_MAINLAND
		*/
		
		private vipapis.address.Is_Show_GAT is_show_gat;
		
		public vipapis.address.Is_Show_GAT getIs_show_gat(){
			return this.is_show_gat;
		}
		
		public void setIs_show_gat(vipapis.address.Is_Show_GAT value){
			this.is_show_gat = value;
		}
		
	}
	
	
	
	
	public static class getFullAddress_result {
		
		/**
		*/
		
		private vipapis.address.FullAddress success;
		
		public vipapis.address.FullAddress getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.address.FullAddress value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getProvinceWarehouse_result {
		
		/**
		*/
		
		private List<vipapis.address.ProvinceWarehouse> success;
		
		public List<vipapis.address.ProvinceWarehouse> getSuccess(){
			return this.success;
		}
		
		public void setSuccess(List<vipapis.address.ProvinceWarehouse> value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getFullAddress_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getFullAddress_args>
	{
		
		public static final getFullAddress_argsHelper OBJ = new getFullAddress_argsHelper();
		
		public static getFullAddress_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getFullAddress_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setArea_code(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.address.Is_Show_GAT value;
				
				value = null;
				String name = iprot.readString();
				vipapis.address.Is_Show_GAT[] values = vipapis.address.Is_Show_GAT.values(); 
				for(vipapis.address.Is_Show_GAT v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setIs_show_gat(value);
			}
			
			
			
			
			
			if(true){
				
				Boolean value;
				value = iprot.readBool();
				
				struct.setIs_bind(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getFullAddress_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("area_code");
			oprot.writeString(struct.getArea_code());
			
			oprot.writeFieldEnd();
			
			if(struct.getIs_show_gat() != null) {
				
				oprot.writeFieldBegin("is_show_gat");
				oprot.writeString(struct.getIs_show_gat().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getIs_bind() != null) {
				
				oprot.writeFieldBegin("is_bind");
				oprot.writeBool(struct.getIs_bind());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getFullAddress_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getProvinceWarehouse_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getProvinceWarehouse_args>
	{
		
		public static final getProvinceWarehouse_argsHelper OBJ = new getProvinceWarehouse_argsHelper();
		
		public static getProvinceWarehouse_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getProvinceWarehouse_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.address.Is_Show_GAT value;
				
				value = null;
				String name = iprot.readString();
				vipapis.address.Is_Show_GAT[] values = vipapis.address.Is_Show_GAT.values(); 
				for(vipapis.address.Is_Show_GAT v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setIs_show_gat(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getProvinceWarehouse_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getIs_show_gat() != null) {
				
				oprot.writeFieldBegin("is_show_gat");
				oprot.writeString(struct.getIs_show_gat().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getProvinceWarehouse_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getFullAddress_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getFullAddress_result>
	{
		
		public static final getFullAddress_resultHelper OBJ = new getFullAddress_resultHelper();
		
		public static getFullAddress_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getFullAddress_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.address.FullAddress value;
				
				value = new vipapis.address.FullAddress();
				vipapis.address.FullAddressHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getFullAddress_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.address.FullAddressHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getFullAddress_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getProvinceWarehouse_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getProvinceWarehouse_result>
	{
		
		public static final getProvinceWarehouse_resultHelper OBJ = new getProvinceWarehouse_resultHelper();
		
		public static getProvinceWarehouse_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getProvinceWarehouse_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.address.ProvinceWarehouse> value;
				
				value = new ArrayList<vipapis.address.ProvinceWarehouse>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.address.ProvinceWarehouse elem0;
						
						elem0 = new vipapis.address.ProvinceWarehouse();
						vipapis.address.ProvinceWarehouseHelper.getInstance().read(elem0, iprot);
						
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
		
		
		public void write(getProvinceWarehouse_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				oprot.writeListBegin();
				for(vipapis.address.ProvinceWarehouse _item0 : struct.getSuccess()){
					
					
					vipapis.address.ProvinceWarehouseHelper.getInstance().write(_item0, oprot);
					
				}
				
				oprot.writeListEnd();
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getProvinceWarehouse_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class AddressServiceClient extends OspRestStub implements AddressService  {
		
		
		public AddressServiceClient() {
			
			super("1.0.0", "vipapis.address.AddressService");
		}
		
		
		
		public vipapis.address.FullAddress getFullAddress(String area_code,vipapis.address.Is_Show_GAT is_show_gat,Boolean is_bind) throws OspException {
			
			send_getFullAddress(area_code,is_show_gat,is_bind);
			return recv_getFullAddress(); 
			
		}
		
		
		private void send_getFullAddress(String area_code,vipapis.address.Is_Show_GAT is_show_gat,Boolean is_bind) throws OspException {
			
			initInvocation("getFullAddress");
			
			getFullAddress_args args = new getFullAddress_args();
			args.setArea_code(area_code);
			args.setIs_show_gat(is_show_gat);
			args.setIs_bind(is_bind);
			
			sendBase(args, getFullAddress_argsHelper.getInstance());
		}
		
		
		private vipapis.address.FullAddress recv_getFullAddress() throws OspException {
			
			getFullAddress_result result = new getFullAddress_result();
			receiveBase(result, getFullAddress_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public List<vipapis.address.ProvinceWarehouse> getProvinceWarehouse(vipapis.address.Is_Show_GAT is_show_gat) throws OspException {
			
			send_getProvinceWarehouse(is_show_gat);
			return recv_getProvinceWarehouse(); 
			
		}
		
		
		private void send_getProvinceWarehouse(vipapis.address.Is_Show_GAT is_show_gat) throws OspException {
			
			initInvocation("getProvinceWarehouse");
			
			getProvinceWarehouse_args args = new getProvinceWarehouse_args();
			args.setIs_show_gat(is_show_gat);
			
			sendBase(args, getProvinceWarehouse_argsHelper.getInstance());
		}
		
		
		private List<vipapis.address.ProvinceWarehouse> recv_getProvinceWarehouse() throws OspException {
			
			getProvinceWarehouse_result result = new getProvinceWarehouse_result();
			receiveBase(result, getProvinceWarehouse_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}