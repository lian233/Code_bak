package vipapis.product;

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

public  class ProductHelper implements com.vip.osp.sdk.base.BeanSerializer<Product>
{
	
	public static final ProductHelper OBJ = new ProductHelper();
	
	public static ProductHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(Product struct, Protocol iprot) throws OspException {
		
		
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
				
				
				
				
				
				if ("product_id".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setProduct_id(value);
				}
				
				
				
				
				
				if ("product_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_name(value);
				}
				
				
				
				
				
				if ("brand_store_sn".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_store_sn(value);
				}
				
				
				
				
				
				if ("brand_name".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_name(value);
				}
				
				
				
				
				
				if ("brand_name_eng".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_name_eng(value);
				}
				
				
				
				
				
				if ("brand_url".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBrand_url(value);
				}
				
				
				
				
				
				if ("market_price".equals(schemeField.trim())){
					
					double value;
					value = iprot.readDouble();
					
					struct.setMarket_price(value);
				}
				
				
				
				
				
				if ("sell_price".equals(schemeField.trim())){
					
					double value;
					value = iprot.readDouble();
					
					struct.setSell_price(value);
				}
				
				
				
				
				
				if ("agio".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAgio(value);
				}
				
				
				
				
				
				if ("has_stock".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setHas_stock(value);
				}
				
				
				
				
				
				if ("product_url".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_url(value);
				}
				
				
				
				
				
				if ("small_image".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSmall_image(value);
				}
				
				
				
				
				
				if ("product_image".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_image(value);
				}
				
				
				
				
				
				if ("show_image".equals(schemeField.trim())){
					
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
					
					struct.setShow_image(value);
				}
				
				
				
				
				
				if ("product_mobile_url".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_mobile_url(value);
				}
				
				
				
				
				
				if ("product_mobile_image".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_mobile_image(value);
				}
				
				
				
				
				
				if ("category_id".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setCategory_id(value);
				}
				
				
				
				
				
				if ("nav_category_id1".equals(schemeField.trim())){
					
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
					
					struct.setNav_category_id1(value);
				}
				
				
				
				
				
				if ("nav_category_id2".equals(schemeField.trim())){
					
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
					
					struct.setNav_category_id2(value);
				}
				
				
				
				
				
				if ("nav_category_id3".equals(schemeField.trim())){
					
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
					
					struct.setNav_category_id3(value);
				}
				
				
				
				
				
				if ("nav_first_name".equals(schemeField.trim())){
					
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
					
					struct.setNav_first_name(value);
				}
				
				
				
				
				
				if ("nav_second_name".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem5;
							elem5 = iprot.readString();
							
							value.add(elem5);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setNav_second_name(value);
				}
				
				
				
				
				
				if ("nav_third_name".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem6;
							elem6 = iprot.readString();
							
							value.add(elem6);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setNav_third_name(value);
				}
				
				
				
				
				
				if ("warehouses".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem7;
							elem7 = iprot.readString();
							
							value.add(elem7);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setWarehouses(value);
				}
				
				
				
				
				
				if ("sell_time_from".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSell_time_from(value);
				}
				
				
				
				
				
				if ("sell_time_to".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSell_time_to(value);
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
				
				
				
				
				
				if ("channels".equals(schemeField.trim())){
					
					List<String> value;
					
					value = new ArrayList<String>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							String elem8;
							elem8 = iprot.readString();
							
							value.add(elem8);
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
	
	
	public void write(Product struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("schedule_id");
		oprot.writeI32(struct.getSchedule_id()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("product_id");
		oprot.writeI32(struct.getProduct_id()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("product_name");
		oprot.writeString(struct.getProduct_name());
		
		oprot.writeFieldEnd();
		
		if(struct.getBrand_store_sn() != null) {
			
			oprot.writeFieldBegin("brand_store_sn");
			oprot.writeString(struct.getBrand_store_sn());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_name() != null) {
			
			oprot.writeFieldBegin("brand_name");
			oprot.writeString(struct.getBrand_name());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_name_eng() != null) {
			
			oprot.writeFieldBegin("brand_name_eng");
			oprot.writeString(struct.getBrand_name_eng());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBrand_url() != null) {
			
			oprot.writeFieldBegin("brand_url");
			oprot.writeString(struct.getBrand_url());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldBegin("market_price");
		oprot.writeDouble(struct.getMarket_price());
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("sell_price");
		oprot.writeDouble(struct.getSell_price());
		
		oprot.writeFieldEnd();
		
		if(struct.getAgio() != null) {
			
			oprot.writeFieldBegin("agio");
			oprot.writeString(struct.getAgio());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldBegin("has_stock");
		oprot.writeI32(struct.getHas_stock()); 
		
		oprot.writeFieldEnd();
		
		if(struct.getProduct_url() != null) {
			
			oprot.writeFieldBegin("product_url");
			oprot.writeString(struct.getProduct_url());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSmall_image() != null) {
			
			oprot.writeFieldBegin("small_image");
			oprot.writeString(struct.getSmall_image());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_image() != null) {
			
			oprot.writeFieldBegin("product_image");
			oprot.writeString(struct.getProduct_image());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getShow_image() != null) {
			
			oprot.writeFieldBegin("show_image");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getShow_image()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_mobile_url() != null) {
			
			oprot.writeFieldBegin("product_mobile_url");
			oprot.writeString(struct.getProduct_mobile_url());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getProduct_mobile_image() != null) {
			
			oprot.writeFieldBegin("product_mobile_image");
			oprot.writeString(struct.getProduct_mobile_image());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCategory_id() != null) {
			
			oprot.writeFieldBegin("category_id");
			oprot.writeI32(struct.getCategory_id()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getNav_category_id1() != null) {
			
			oprot.writeFieldBegin("nav_category_id1");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getNav_category_id1()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getNav_category_id2() != null) {
			
			oprot.writeFieldBegin("nav_category_id2");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getNav_category_id2()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getNav_category_id3() != null) {
			
			oprot.writeFieldBegin("nav_category_id3");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getNav_category_id3()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getNav_first_name() != null) {
			
			oprot.writeFieldBegin("nav_first_name");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getNav_first_name()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getNav_second_name() != null) {
			
			oprot.writeFieldBegin("nav_second_name");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getNav_second_name()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getNav_third_name() != null) {
			
			oprot.writeFieldBegin("nav_third_name");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getNav_third_name()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getWarehouses() != null) {
			
			oprot.writeFieldBegin("warehouses");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getWarehouses()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSell_time_from() != null) {
			
			oprot.writeFieldBegin("sell_time_from");
			oprot.writeString(struct.getSell_time_from());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSell_time_to() != null) {
			
			oprot.writeFieldBegin("sell_time_to");
			oprot.writeString(struct.getSell_time_to());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPc_show_from() != null) {
			
			oprot.writeFieldBegin("pc_show_from");
			oprot.writeString(struct.getPc_show_from());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPc_show_to() != null) {
			
			oprot.writeFieldBegin("pc_show_to");
			oprot.writeString(struct.getPc_show_to());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMobile_show_from() != null) {
			
			oprot.writeFieldBegin("mobile_show_from");
			oprot.writeString(struct.getMobile_show_from());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMobile_show_to() != null) {
			
			oprot.writeFieldBegin("mobile_show_to");
			oprot.writeString(struct.getMobile_show_to());
			
			oprot.writeFieldEnd();
		}
		
		
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
	
	
	public void validate(Product bean) throws OspException {
		
		
	}
	
	
}