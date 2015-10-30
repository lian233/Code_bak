package vipapis.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public class CategoryServiceHelper {
	
	
	
	
	public static class getCategoryAttributeListById_args {
		
		/**
		* 分类ID
		*/
		
		private int category_id;
		
		public int getCategory_id(){
			return this.category_id;
		}
		
		public void setCategory_id(int value){
			this.category_id = value;
		}
		
	}
	
	
	
	
	public static class getCategoryById_args {
		
		/**
		* 分类ID
		*/
		
		private int category_id;
		
		public int getCategory_id(){
			return this.category_id;
		}
		
		public void setCategory_id(int value){
			this.category_id = value;
		}
		
	}
	
	
	
	
	public static class getCategoryListByName_args {
		
		/**
		* 分类名称
		*/
		
		private String category_name;
		
		/**
		* 限制最大返回数量,为0,返回所有匹配的记录数;大于0,返回匹配的limit条记录数
		*/
		
		private int limit;
		
		public String getCategory_name(){
			return this.category_name;
		}
		
		public void setCategory_name(String value){
			this.category_name = value;
		}
		public int getLimit(){
			return this.limit;
		}
		
		public void setLimit(int value){
			this.limit = value;
		}
		
	}
	
	
	
	
	public static class getCategoryTreeById_args {
		
		/**
		* 分类ID
		*/
		
		private int category_id;
		
		public int getCategory_id(){
			return this.category_id;
		}
		
		public void setCategory_id(int value){
			this.category_id = value;
		}
		
	}
	
	
	
	
	public static class getUpdatedCategoryList_args {
		
		/**
		* 变更开始时间
		*/
		
		private long since_updatetime;
		
		/**
		* 导航ID,hierarchyid=0 录入导航 ;hierarchyid>0 展示导航
		* @sampleValue hierarchyId hierarchyId=0
		*/
		
		private int hierarchyId;
		
		public long getSince_updatetime(){
			return this.since_updatetime;
		}
		
		public void setSince_updatetime(long value){
			this.since_updatetime = value;
		}
		public int getHierarchyId(){
			return this.hierarchyId;
		}
		
		public void setHierarchyId(int value){
			this.hierarchyId = value;
		}
		
	}
	
	
	
	
	public static class getCategoryAttributeListById_result {
		
		/**
		*/
		
		private List<vipapis.category.Attribute> success;
		
		public List<vipapis.category.Attribute> getSuccess(){
			return this.success;
		}
		
		public void setSuccess(List<vipapis.category.Attribute> value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getCategoryById_result {
		
		/**
		*/
		
		private vipapis.category.Category success;
		
		public vipapis.category.Category getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.category.Category value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getCategoryListByName_result {
		
		/**
		*/
		
		private List<vipapis.category.Category> success;
		
		public List<vipapis.category.Category> getSuccess(){
			return this.success;
		}
		
		public void setSuccess(List<vipapis.category.Category> value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getCategoryTreeById_result {
		
		/**
		*/
		
		private vipapis.category.Category success;
		
		public vipapis.category.Category getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.category.Category value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getUpdatedCategoryList_result {
		
		/**
		*/
		
		private vipapis.category.CategoryUpdates success;
		
		public vipapis.category.CategoryUpdates getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.category.CategoryUpdates value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getCategoryAttributeListById_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getCategoryAttributeListById_args>
	{
		
		public static final getCategoryAttributeListById_argsHelper OBJ = new getCategoryAttributeListById_argsHelper();
		
		public static getCategoryAttributeListById_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCategoryAttributeListById_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setCategory_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCategoryAttributeListById_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("category_id");
			oprot.writeI32(struct.getCategory_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCategoryAttributeListById_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCategoryById_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getCategoryById_args>
	{
		
		public static final getCategoryById_argsHelper OBJ = new getCategoryById_argsHelper();
		
		public static getCategoryById_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCategoryById_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setCategory_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCategoryById_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("category_id");
			oprot.writeI32(struct.getCategory_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCategoryById_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCategoryListByName_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getCategoryListByName_args>
	{
		
		public static final getCategoryListByName_argsHelper OBJ = new getCategoryListByName_argsHelper();
		
		public static getCategoryListByName_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCategoryListByName_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCategory_name(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setLimit(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCategoryListByName_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("category_name");
			oprot.writeString(struct.getCategory_name());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("limit");
			oprot.writeI32(struct.getLimit()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCategoryListByName_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCategoryTreeById_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getCategoryTreeById_args>
	{
		
		public static final getCategoryTreeById_argsHelper OBJ = new getCategoryTreeById_argsHelper();
		
		public static getCategoryTreeById_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCategoryTreeById_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setCategory_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCategoryTreeById_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("category_id");
			oprot.writeI32(struct.getCategory_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCategoryTreeById_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getUpdatedCategoryList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getUpdatedCategoryList_args>
	{
		
		public static final getUpdatedCategoryList_argsHelper OBJ = new getUpdatedCategoryList_argsHelper();
		
		public static getUpdatedCategoryList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getUpdatedCategoryList_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				long value;
				value = iprot.readI64(); 
				
				struct.setSince_updatetime(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setHierarchyId(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getUpdatedCategoryList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("since_updatetime");
			oprot.writeI64(struct.getSince_updatetime()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("hierarchyId");
			oprot.writeI32(struct.getHierarchyId()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getUpdatedCategoryList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCategoryAttributeListById_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getCategoryAttributeListById_result>
	{
		
		public static final getCategoryAttributeListById_resultHelper OBJ = new getCategoryAttributeListById_resultHelper();
		
		public static getCategoryAttributeListById_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCategoryAttributeListById_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.category.Attribute> value;
				
				value = new ArrayList<vipapis.category.Attribute>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.category.Attribute elem0;
						
						elem0 = new vipapis.category.Attribute();
						vipapis.category.AttributeHelper.getInstance().read(elem0, iprot);
						
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
		
		
		public void write(getCategoryAttributeListById_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				oprot.writeListBegin();
				for(vipapis.category.Attribute _item0 : struct.getSuccess()){
					
					
					vipapis.category.AttributeHelper.getInstance().write(_item0, oprot);
					
				}
				
				oprot.writeListEnd();
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCategoryAttributeListById_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCategoryById_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getCategoryById_result>
	{
		
		public static final getCategoryById_resultHelper OBJ = new getCategoryById_resultHelper();
		
		public static getCategoryById_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCategoryById_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.category.Category value;
				
				value = new vipapis.category.Category();
				vipapis.category.CategoryHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCategoryById_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.category.CategoryHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCategoryById_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCategoryListByName_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getCategoryListByName_result>
	{
		
		public static final getCategoryListByName_resultHelper OBJ = new getCategoryListByName_resultHelper();
		
		public static getCategoryListByName_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCategoryListByName_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.category.Category> value;
				
				value = new ArrayList<vipapis.category.Category>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.category.Category elem0;
						
						elem0 = new vipapis.category.Category();
						vipapis.category.CategoryHelper.getInstance().read(elem0, iprot);
						
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
		
		
		public void write(getCategoryListByName_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				oprot.writeListBegin();
				for(vipapis.category.Category _item0 : struct.getSuccess()){
					
					
					vipapis.category.CategoryHelper.getInstance().write(_item0, oprot);
					
				}
				
				oprot.writeListEnd();
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCategoryListByName_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCategoryTreeById_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getCategoryTreeById_result>
	{
		
		public static final getCategoryTreeById_resultHelper OBJ = new getCategoryTreeById_resultHelper();
		
		public static getCategoryTreeById_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCategoryTreeById_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.category.Category value;
				
				value = new vipapis.category.Category();
				vipapis.category.CategoryHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCategoryTreeById_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.category.CategoryHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCategoryTreeById_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getUpdatedCategoryList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getUpdatedCategoryList_result>
	{
		
		public static final getUpdatedCategoryList_resultHelper OBJ = new getUpdatedCategoryList_resultHelper();
		
		public static getUpdatedCategoryList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getUpdatedCategoryList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.category.CategoryUpdates value;
				
				value = new vipapis.category.CategoryUpdates();
				vipapis.category.CategoryUpdatesHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getUpdatedCategoryList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.category.CategoryUpdatesHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getUpdatedCategoryList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class CategoryServiceClient extends OspRestStub implements CategoryService  {
		
		
		public CategoryServiceClient() {
			
			super("1.0.0", "vipapis.category.CategoryService");
		}
		
		
		
		public List<vipapis.category.Attribute> getCategoryAttributeListById(int category_id) throws OspException {
			
			send_getCategoryAttributeListById(category_id);
			return recv_getCategoryAttributeListById(); 
			
		}
		
		
		private void send_getCategoryAttributeListById(int category_id) throws OspException {
			
			initInvocation("getCategoryAttributeListById");
			
			getCategoryAttributeListById_args args = new getCategoryAttributeListById_args();
			args.setCategory_id(category_id);
			
			sendBase(args, getCategoryAttributeListById_argsHelper.getInstance());
		}
		
		
		private List<vipapis.category.Attribute> recv_getCategoryAttributeListById() throws OspException {
			
			getCategoryAttributeListById_result result = new getCategoryAttributeListById_result();
			receiveBase(result, getCategoryAttributeListById_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.category.Category getCategoryById(int category_id) throws OspException {
			
			send_getCategoryById(category_id);
			return recv_getCategoryById(); 
			
		}
		
		
		private void send_getCategoryById(int category_id) throws OspException {
			
			initInvocation("getCategoryById");
			
			getCategoryById_args args = new getCategoryById_args();
			args.setCategory_id(category_id);
			
			sendBase(args, getCategoryById_argsHelper.getInstance());
		}
		
		
		private vipapis.category.Category recv_getCategoryById() throws OspException {
			
			getCategoryById_result result = new getCategoryById_result();
			receiveBase(result, getCategoryById_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public List<vipapis.category.Category> getCategoryListByName(String category_name,int limit) throws OspException {
			
			send_getCategoryListByName(category_name,limit);
			return recv_getCategoryListByName(); 
			
		}
		
		
		private void send_getCategoryListByName(String category_name,int limit) throws OspException {
			
			initInvocation("getCategoryListByName");
			
			getCategoryListByName_args args = new getCategoryListByName_args();
			args.setCategory_name(category_name);
			args.setLimit(limit);
			
			sendBase(args, getCategoryListByName_argsHelper.getInstance());
		}
		
		
		private List<vipapis.category.Category> recv_getCategoryListByName() throws OspException {
			
			getCategoryListByName_result result = new getCategoryListByName_result();
			receiveBase(result, getCategoryListByName_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.category.Category getCategoryTreeById(int category_id) throws OspException {
			
			send_getCategoryTreeById(category_id);
			return recv_getCategoryTreeById(); 
			
		}
		
		
		private void send_getCategoryTreeById(int category_id) throws OspException {
			
			initInvocation("getCategoryTreeById");
			
			getCategoryTreeById_args args = new getCategoryTreeById_args();
			args.setCategory_id(category_id);
			
			sendBase(args, getCategoryTreeById_argsHelper.getInstance());
		}
		
		
		private vipapis.category.Category recv_getCategoryTreeById() throws OspException {
			
			getCategoryTreeById_result result = new getCategoryTreeById_result();
			receiveBase(result, getCategoryTreeById_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.category.CategoryUpdates getUpdatedCategoryList(long since_updatetime,int hierarchyId) throws OspException {
			
			send_getUpdatedCategoryList(since_updatetime,hierarchyId);
			return recv_getUpdatedCategoryList(); 
			
		}
		
		
		private void send_getUpdatedCategoryList(long since_updatetime,int hierarchyId) throws OspException {
			
			initInvocation("getUpdatedCategoryList");
			
			getUpdatedCategoryList_args args = new getUpdatedCategoryList_args();
			args.setSince_updatetime(since_updatetime);
			args.setHierarchyId(hierarchyId);
			
			sendBase(args, getUpdatedCategoryList_argsHelper.getInstance());
		}
		
		
		private vipapis.category.CategoryUpdates recv_getUpdatedCategoryList() throws OspException {
			
			getUpdatedCategoryList_result result = new getUpdatedCategoryList_result();
			receiveBase(result, getUpdatedCategoryList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}