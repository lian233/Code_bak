package vipapis.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public class VendorProductServiceHelper {
	
	
	
	
	public static class createProduct_args {
		
		/**
		* 商品信息
		*/
		
		private List<vipapis.product.CreateProductItem> vendor_products;
		
		public List<vipapis.product.CreateProductItem> getVendor_products(){
			return this.vendor_products;
		}
		
		public void setVendor_products(List<vipapis.product.CreateProductItem> value){
			this.vendor_products = value;
		}
		
	}
	
	
	
	
	public static class editProduct_args {
		
		/**
		* 商品信息
		*/
		
		private List<vipapis.product.EditProductItem> vendor_products;
		
		public List<vipapis.product.EditProductItem> getVendor_products(){
			return this.vendor_products;
		}
		
		public void setVendor_products(List<vipapis.product.EditProductItem> value){
			this.vendor_products = value;
		}
		
	}
	
	
	
	
	public static class multiGetProductSkuInfo_args {
		
		/**
		* 供应商ID
		* @sampleValue vendor_id 525
		*/
		
		private int vendor_id;
		
		/**
		* 条形码
		* @sampleValue barcode 113113302011245
		*/
		
		private String barcode;
		
		/**
		* 品牌ID
		* @sampleValue brand_ID E456413215
		*/
		
		private Integer brand_ID;
		
		/**
		* 分类ID(只可录入三级分类ID)
		* @sampleValue category_id 111
		*/
		
		private Integer category_id;
		
		/**
		* 货号
		* @sampleValue sn 113113302011
		*/
		
		private String sn;
		
		/**
		* 商品状态
		* @sampleValue status 主档待审核
		*/
		
		private vipapis.product.ProductStatus status;
		
		/**
		* 页码(暂不起作用)
		* @sampleValue page page=1
		*/
		
		private Integer page;
		
		/**
		* 每页记录数(暂不起作用)
		* @sampleValue limit limit=20
		*/
		
		private Integer limit;
		
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		public String getBarcode(){
			return this.barcode;
		}
		
		public void setBarcode(String value){
			this.barcode = value;
		}
		public Integer getBrand_ID(){
			return this.brand_ID;
		}
		
		public void setBrand_ID(Integer value){
			this.brand_ID = value;
		}
		public Integer getCategory_id(){
			return this.category_id;
		}
		
		public void setCategory_id(Integer value){
			this.category_id = value;
		}
		public String getSn(){
			return this.sn;
		}
		
		public void setSn(String value){
			this.sn = value;
		}
		public vipapis.product.ProductStatus getStatus(){
			return this.status;
		}
		
		public void setStatus(vipapis.product.ProductStatus value){
			this.status = value;
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
	
	
	
	
	public static class multiGetProductSpuInfo_args {
		
		/**
		* 供应商ID
		* @sampleValue vendor_id 525
		*/
		
		private int vendor_id;
		
		/**
		* 品牌ID
		* @sampleValue brand_id E456413215
		*/
		
		private Integer brand_id;
		
		/**
		* 分类ID(只可录入三级分类ID)
		* @sampleValue category_id 111
		*/
		
		private Integer category_id;
		
		/**
		* 货号
		* @sampleValue sn 113113302011
		*/
		
		private String sn;
		
		/**
		* 商品状态
		* @sampleValue status 主档待审核
		*/
		
		private vipapis.product.ProductStatus status;
		
		/**
		* 页码(暂不起作用)
		* @sampleValue page page=1
		*/
		
		private Integer page;
		
		/**
		* 每页记录数(暂不起作用)
		* @sampleValue limit limit=20
		*/
		
		private Integer limit;
		
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		public Integer getBrand_id(){
			return this.brand_id;
		}
		
		public void setBrand_id(Integer value){
			this.brand_id = value;
		}
		public Integer getCategory_id(){
			return this.category_id;
		}
		
		public void setCategory_id(Integer value){
			this.category_id = value;
		}
		public String getSn(){
			return this.sn;
		}
		
		public void setSn(String value){
			this.sn = value;
		}
		public vipapis.product.ProductStatus getStatus(){
			return this.status;
		}
		
		public void setStatus(vipapis.product.ProductStatus value){
			this.status = value;
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
	
	
	
	
	public static class submitProduct_args {
		
		/**
		* 需要提交的商品键
		*/
		
		private List<vipapis.product.VendorProductSkuKey> vendor_product_keys;
		
		public List<vipapis.product.VendorProductSkuKey> getVendor_product_keys(){
			return this.vendor_product_keys;
		}
		
		public void setVendor_product_keys(List<vipapis.product.VendorProductSkuKey> value){
			this.vendor_product_keys = value;
		}
		
	}
	
	
	
	
	public static class createProduct_result {
		
		/**
		*/
		
		private vipapis.product.VendorProductResponse success;
		
		public vipapis.product.VendorProductResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.product.VendorProductResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class editProduct_result {
		
		/**
		*/
		
		private vipapis.product.VendorProductResponse success;
		
		public vipapis.product.VendorProductResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.product.VendorProductResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class multiGetProductSkuInfo_result {
		
		/**
		*/
		
		private vipapis.product.MultiGetProductSkuResponse success;
		
		public vipapis.product.MultiGetProductSkuResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.product.MultiGetProductSkuResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class multiGetProductSpuInfo_result {
		
		/**
		*/
		
		private vipapis.product.MultiGetProductSpuResponse success;
		
		public vipapis.product.MultiGetProductSpuResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.product.MultiGetProductSpuResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class submitProduct_result {
		
		/**
		*/
		
		private vipapis.product.VendorProductResponse success;
		
		public vipapis.product.VendorProductResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.product.VendorProductResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class createProduct_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<createProduct_args>
	{
		
		public static final createProduct_argsHelper OBJ = new createProduct_argsHelper();
		
		public static createProduct_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(createProduct_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.product.CreateProductItem> value;
				
				value = new ArrayList<vipapis.product.CreateProductItem>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.product.CreateProductItem elem0;
						
						elem0 = new vipapis.product.CreateProductItem();
						vipapis.product.CreateProductItemHelper.getInstance().read(elem0, iprot);
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setVendor_products(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(createProduct_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_products");
			
			oprot.writeListBegin();
			for(vipapis.product.CreateProductItem _item0 : struct.getVendor_products()){
				
				
				vipapis.product.CreateProductItemHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(createProduct_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class editProduct_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<editProduct_args>
	{
		
		public static final editProduct_argsHelper OBJ = new editProduct_argsHelper();
		
		public static editProduct_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(editProduct_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.product.EditProductItem> value;
				
				value = new ArrayList<vipapis.product.EditProductItem>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.product.EditProductItem elem1;
						
						elem1 = new vipapis.product.EditProductItem();
						vipapis.product.EditProductItemHelper.getInstance().read(elem1, iprot);
						
						value.add(elem1);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setVendor_products(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(editProduct_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_products");
			
			oprot.writeListBegin();
			for(vipapis.product.EditProductItem _item0 : struct.getVendor_products()){
				
				
				vipapis.product.EditProductItemHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(editProduct_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class multiGetProductSkuInfo_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<multiGetProductSkuInfo_args>
	{
		
		public static final multiGetProductSkuInfo_argsHelper OBJ = new multiGetProductSkuInfo_argsHelper();
		
		public static multiGetProductSkuInfo_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(multiGetProductSkuInfo_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setBarcode(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setBrand_ID(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setCategory_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSn(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.product.ProductStatus value;
				
				value = null;
				String name = iprot.readString();
				vipapis.product.ProductStatus[] values = vipapis.product.ProductStatus.values(); 
				for(vipapis.product.ProductStatus v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setStatus(value);
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
		
		
		public void write(multiGetProductSkuInfo_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			if(struct.getBarcode() != null) {
				
				oprot.writeFieldBegin("barcode");
				oprot.writeString(struct.getBarcode());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getBrand_ID() != null) {
				
				oprot.writeFieldBegin("brand_ID");
				oprot.writeI32(struct.getBrand_ID()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getCategory_id() != null) {
				
				oprot.writeFieldBegin("category_id");
				oprot.writeI32(struct.getCategory_id()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSn() != null) {
				
				oprot.writeFieldBegin("sn");
				oprot.writeString(struct.getSn());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getStatus() != null) {
				
				oprot.writeFieldBegin("status");
				oprot.writeString(struct.getStatus().name());  
				
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
		
		
		public void validate(multiGetProductSkuInfo_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class multiGetProductSpuInfo_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<multiGetProductSpuInfo_args>
	{
		
		public static final multiGetProductSpuInfo_argsHelper OBJ = new multiGetProductSpuInfo_argsHelper();
		
		public static multiGetProductSpuInfo_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(multiGetProductSpuInfo_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setBrand_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setCategory_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSn(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.product.ProductStatus value;
				
				value = null;
				String name = iprot.readString();
				vipapis.product.ProductStatus[] values = vipapis.product.ProductStatus.values(); 
				for(vipapis.product.ProductStatus v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setStatus(value);
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
		
		
		public void write(multiGetProductSpuInfo_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			if(struct.getBrand_id() != null) {
				
				oprot.writeFieldBegin("brand_id");
				oprot.writeI32(struct.getBrand_id()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getCategory_id() != null) {
				
				oprot.writeFieldBegin("category_id");
				oprot.writeI32(struct.getCategory_id()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSn() != null) {
				
				oprot.writeFieldBegin("sn");
				oprot.writeString(struct.getSn());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getStatus() != null) {
				
				oprot.writeFieldBegin("status");
				oprot.writeString(struct.getStatus().name());  
				
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
		
		
		public void validate(multiGetProductSpuInfo_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class submitProduct_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<submitProduct_args>
	{
		
		public static final submitProduct_argsHelper OBJ = new submitProduct_argsHelper();
		
		public static submitProduct_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(submitProduct_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.product.VendorProductSkuKey> value;
				
				value = new ArrayList<vipapis.product.VendorProductSkuKey>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.product.VendorProductSkuKey elem0;
						
						elem0 = new vipapis.product.VendorProductSkuKey();
						vipapis.product.VendorProductSkuKeyHelper.getInstance().read(elem0, iprot);
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setVendor_product_keys(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(submitProduct_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_product_keys");
			
			oprot.writeListBegin();
			for(vipapis.product.VendorProductSkuKey _item0 : struct.getVendor_product_keys()){
				
				
				vipapis.product.VendorProductSkuKeyHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(submitProduct_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class createProduct_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<createProduct_result>
	{
		
		public static final createProduct_resultHelper OBJ = new createProduct_resultHelper();
		
		public static createProduct_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(createProduct_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.product.VendorProductResponse value;
				
				value = new vipapis.product.VendorProductResponse();
				vipapis.product.VendorProductResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(createProduct_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.product.VendorProductResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(createProduct_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class editProduct_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<editProduct_result>
	{
		
		public static final editProduct_resultHelper OBJ = new editProduct_resultHelper();
		
		public static editProduct_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(editProduct_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.product.VendorProductResponse value;
				
				value = new vipapis.product.VendorProductResponse();
				vipapis.product.VendorProductResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(editProduct_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.product.VendorProductResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(editProduct_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class multiGetProductSkuInfo_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<multiGetProductSkuInfo_result>
	{
		
		public static final multiGetProductSkuInfo_resultHelper OBJ = new multiGetProductSkuInfo_resultHelper();
		
		public static multiGetProductSkuInfo_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(multiGetProductSkuInfo_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.product.MultiGetProductSkuResponse value;
				
				value = new vipapis.product.MultiGetProductSkuResponse();
				vipapis.product.MultiGetProductSkuResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(multiGetProductSkuInfo_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.product.MultiGetProductSkuResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(multiGetProductSkuInfo_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class multiGetProductSpuInfo_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<multiGetProductSpuInfo_result>
	{
		
		public static final multiGetProductSpuInfo_resultHelper OBJ = new multiGetProductSpuInfo_resultHelper();
		
		public static multiGetProductSpuInfo_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(multiGetProductSpuInfo_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.product.MultiGetProductSpuResponse value;
				
				value = new vipapis.product.MultiGetProductSpuResponse();
				vipapis.product.MultiGetProductSpuResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(multiGetProductSpuInfo_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.product.MultiGetProductSpuResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(multiGetProductSpuInfo_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class submitProduct_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<submitProduct_result>
	{
		
		public static final submitProduct_resultHelper OBJ = new submitProduct_resultHelper();
		
		public static submitProduct_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(submitProduct_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.product.VendorProductResponse value;
				
				value = new vipapis.product.VendorProductResponse();
				vipapis.product.VendorProductResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(submitProduct_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.product.VendorProductResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(submitProduct_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class VendorProductServiceClient extends OspRestStub implements VendorProductService  {
		
		
		public VendorProductServiceClient() {
			
			super("1.0.0", "vipapis.product.VendorProductService");
		}
		
		
		
		public vipapis.product.VendorProductResponse createProduct(List<vipapis.product.CreateProductItem> vendor_products) throws OspException {
			
			send_createProduct(vendor_products);
			return recv_createProduct(); 
			
		}
		
		
		private void send_createProduct(List<vipapis.product.CreateProductItem> vendor_products) throws OspException {
			
			initInvocation("createProduct");
			
			createProduct_args args = new createProduct_args();
			args.setVendor_products(vendor_products);
			
			sendBase(args, createProduct_argsHelper.getInstance());
		}
		
		
		private vipapis.product.VendorProductResponse recv_createProduct() throws OspException {
			
			createProduct_result result = new createProduct_result();
			receiveBase(result, createProduct_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.product.VendorProductResponse editProduct(List<vipapis.product.EditProductItem> vendor_products) throws OspException {
			
			send_editProduct(vendor_products);
			return recv_editProduct(); 
			
		}
		
		
		private void send_editProduct(List<vipapis.product.EditProductItem> vendor_products) throws OspException {
			
			initInvocation("editProduct");
			
			editProduct_args args = new editProduct_args();
			args.setVendor_products(vendor_products);
			
			sendBase(args, editProduct_argsHelper.getInstance());
		}
		
		
		private vipapis.product.VendorProductResponse recv_editProduct() throws OspException {
			
			editProduct_result result = new editProduct_result();
			receiveBase(result, editProduct_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.product.MultiGetProductSkuResponse multiGetProductSkuInfo(int vendor_id,String barcode,Integer brand_ID,Integer category_id,String sn,vipapis.product.ProductStatus status,Integer page,Integer limit) throws OspException {
			
			send_multiGetProductSkuInfo(vendor_id,barcode,brand_ID,category_id,sn,status,page,limit);
			return recv_multiGetProductSkuInfo(); 
			
		}
		
		
		private void send_multiGetProductSkuInfo(int vendor_id,String barcode,Integer brand_ID,Integer category_id,String sn,vipapis.product.ProductStatus status,Integer page,Integer limit) throws OspException {
			
			initInvocation("multiGetProductSkuInfo");
			
			multiGetProductSkuInfo_args args = new multiGetProductSkuInfo_args();
			args.setVendor_id(vendor_id);
			args.setBarcode(barcode);
			args.setBrand_ID(brand_ID);
			args.setCategory_id(category_id);
			args.setSn(sn);
			args.setStatus(status);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, multiGetProductSkuInfo_argsHelper.getInstance());
		}
		
		
		private vipapis.product.MultiGetProductSkuResponse recv_multiGetProductSkuInfo() throws OspException {
			
			multiGetProductSkuInfo_result result = new multiGetProductSkuInfo_result();
			receiveBase(result, multiGetProductSkuInfo_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.product.MultiGetProductSpuResponse multiGetProductSpuInfo(int vendor_id,Integer brand_id,Integer category_id,String sn,vipapis.product.ProductStatus status,Integer page,Integer limit) throws OspException {
			
			send_multiGetProductSpuInfo(vendor_id,brand_id,category_id,sn,status,page,limit);
			return recv_multiGetProductSpuInfo(); 
			
		}
		
		
		private void send_multiGetProductSpuInfo(int vendor_id,Integer brand_id,Integer category_id,String sn,vipapis.product.ProductStatus status,Integer page,Integer limit) throws OspException {
			
			initInvocation("multiGetProductSpuInfo");
			
			multiGetProductSpuInfo_args args = new multiGetProductSpuInfo_args();
			args.setVendor_id(vendor_id);
			args.setBrand_id(brand_id);
			args.setCategory_id(category_id);
			args.setSn(sn);
			args.setStatus(status);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, multiGetProductSpuInfo_argsHelper.getInstance());
		}
		
		
		private vipapis.product.MultiGetProductSpuResponse recv_multiGetProductSpuInfo() throws OspException {
			
			multiGetProductSpuInfo_result result = new multiGetProductSpuInfo_result();
			receiveBase(result, multiGetProductSpuInfo_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.product.VendorProductResponse submitProduct(List<vipapis.product.VendorProductSkuKey> vendor_product_keys) throws OspException {
			
			send_submitProduct(vendor_product_keys);
			return recv_submitProduct(); 
			
		}
		
		
		private void send_submitProduct(List<vipapis.product.VendorProductSkuKey> vendor_product_keys) throws OspException {
			
			initInvocation("submitProduct");
			
			submitProduct_args args = new submitProduct_args();
			args.setVendor_product_keys(vendor_product_keys);
			
			sendBase(args, submitProduct_argsHelper.getInstance());
		}
		
		
		private vipapis.product.VendorProductResponse recv_submitProduct() throws OspException {
			
			submitProduct_result result = new submitProduct_result();
			receiveBase(result, submitProduct_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}