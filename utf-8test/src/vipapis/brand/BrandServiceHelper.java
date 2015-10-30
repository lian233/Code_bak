package vipapis.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public class BrandServiceHelper {
	
	
	
	
	public static class getBrandInfo_args {
		
		/**
		* 品牌id
		* @sampleValue brand_id brand_id=E456413215
		*/
		
		private String brand_id;
		
		public String getBrand_id(){
			return this.brand_id;
		}
		
		public void setBrand_id(String value){
			this.brand_id = value;
		}
		
	}
	
	
	
	
	public static class multiGetBrand_args {
		
		/**
		* 检索类型
		* @sampleValue search_type search_type=brand_name
		*/
		
		private vipapis.brand.BrandSearchType search_type;
		
		/**
		* 检索关键词
		* @sampleValue word word=耐克
		*/
		
		private String word;
		
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
		
		public vipapis.brand.BrandSearchType getSearch_type(){
			return this.search_type;
		}
		
		public void setSearch_type(vipapis.brand.BrandSearchType value){
			this.search_type = value;
		}
		public String getWord(){
			return this.word;
		}
		
		public void setWord(String value){
			this.word = value;
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
	
	
	
	
	public static class getBrandInfo_result {
		
		/**
		*/
		
		private vipapis.brand.BrandInfo success;
		
		public vipapis.brand.BrandInfo getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.brand.BrandInfo value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class multiGetBrand_result {
		
		/**
		*/
		
		private vipapis.brand.MultiGetBrandResponse success;
		
		public vipapis.brand.MultiGetBrandResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.brand.MultiGetBrandResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getBrandInfo_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getBrandInfo_args>
	{
		
		public static final getBrandInfo_argsHelper OBJ = new getBrandInfo_argsHelper();
		
		public static getBrandInfo_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getBrandInfo_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setBrand_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getBrandInfo_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("brand_id");
			oprot.writeString(struct.getBrand_id());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getBrandInfo_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class multiGetBrand_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<multiGetBrand_args>
	{
		
		public static final multiGetBrand_argsHelper OBJ = new multiGetBrand_argsHelper();
		
		public static multiGetBrand_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(multiGetBrand_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.brand.BrandSearchType value;
				
				value = null;
				String name = iprot.readString();
				vipapis.brand.BrandSearchType[] values = vipapis.brand.BrandSearchType.values(); 
				for(vipapis.brand.BrandSearchType v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setSearch_type(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setWord(value);
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
		
		
		public void write(multiGetBrand_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("search_type");
			oprot.writeString(struct.getSearch_type().name());  
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("word");
			oprot.writeString(struct.getWord());
			
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
		
		
		public void validate(multiGetBrand_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getBrandInfo_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getBrandInfo_result>
	{
		
		public static final getBrandInfo_resultHelper OBJ = new getBrandInfo_resultHelper();
		
		public static getBrandInfo_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getBrandInfo_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.brand.BrandInfo value;
				
				value = new vipapis.brand.BrandInfo();
				vipapis.brand.BrandInfoHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getBrandInfo_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.brand.BrandInfoHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getBrandInfo_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class multiGetBrand_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<multiGetBrand_result>
	{
		
		public static final multiGetBrand_resultHelper OBJ = new multiGetBrand_resultHelper();
		
		public static multiGetBrand_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(multiGetBrand_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.brand.MultiGetBrandResponse value;
				
				value = new vipapis.brand.MultiGetBrandResponse();
				vipapis.brand.MultiGetBrandResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(multiGetBrand_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.brand.MultiGetBrandResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(multiGetBrand_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class BrandServiceClient extends OspRestStub implements BrandService  {
		
		
		public BrandServiceClient() {
			
			super("1.0.0", "vipapis.brand.BrandService");
		}
		
		
		
		public vipapis.brand.BrandInfo getBrandInfo(String brand_id) throws OspException {
			
			send_getBrandInfo(brand_id);
			return recv_getBrandInfo(); 
			
		}
		
		
		private void send_getBrandInfo(String brand_id) throws OspException {
			
			initInvocation("getBrandInfo");
			
			getBrandInfo_args args = new getBrandInfo_args();
			args.setBrand_id(brand_id);
			
			sendBase(args, getBrandInfo_argsHelper.getInstance());
		}
		
		
		private vipapis.brand.BrandInfo recv_getBrandInfo() throws OspException {
			
			getBrandInfo_result result = new getBrandInfo_result();
			receiveBase(result, getBrandInfo_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.brand.MultiGetBrandResponse multiGetBrand(vipapis.brand.BrandSearchType search_type,String word,Integer page,Integer limit) throws OspException {
			
			send_multiGetBrand(search_type,word,page,limit);
			return recv_multiGetBrand(); 
			
		}
		
		
		private void send_multiGetBrand(vipapis.brand.BrandSearchType search_type,String word,Integer page,Integer limit) throws OspException {
			
			initInvocation("multiGetBrand");
			
			multiGetBrand_args args = new multiGetBrand_args();
			args.setSearch_type(search_type);
			args.setWord(word);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, multiGetBrand_argsHelper.getInstance());
		}
		
		
		private vipapis.brand.MultiGetBrandResponse recv_multiGetBrand() throws OspException {
			
			multiGetBrand_result result = new multiGetBrand_result();
			receiveBase(result, multiGetBrand_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}