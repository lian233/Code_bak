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

public  class EditProductItemHelper implements com.vip.osp.sdk.base.BeanSerializer<EditProductItem>
{
	
	public static final EditProductItemHelper OBJ = new EditProductItemHelper();
	
	public static EditProductItemHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(EditProductItem struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("vendor_id".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setVendor_id(value);
				}
				
				
				
				
				
				if ("barcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBarcode(value);
				}
				
				
				
				
				
				if ("product_description".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setProduct_description(value);
				}
				
				
				
				
				
				if ("category_id".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setCategory_id(value);
				}
				
				
				
				
				
				if ("sn".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSn(value);
				}
				
				
				
				
				
				if ("area_output".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setArea_output(value);
				}
				
				
				
				
				
				if ("size".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSize(value);
				}
				
				
				
				
				
				if ("material".equals(schemeField.trim())){
					
					Map<String, String> value;
					
					value = new HashMap<String, String>();
					iprot.readMapBegin();
					while(true){
						
						try{
							
							String _key0;
							String _val0;
							_key0 = iprot.readString();
							
							_val0 = iprot.readString();
							
							value.put(_key0, _val0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readMapEnd();
					
					struct.setMaterial(value);
				}
				
				
				
				
				
				if ("color".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setColor(value);
				}
				
				
				
				
				
				if ("market_price".equals(schemeField.trim())){
					
					Double value;
					value = iprot.readDouble();
					
					struct.setMarket_price(value);
				}
				
				
				
				
				
				if ("sell_price".equals(schemeField.trim())){
					
					Double value;
					value = iprot.readDouble();
					
					struct.setSell_price(value);
				}
				
				
				
				
				
				if ("tax_rate".equals(schemeField.trim())){
					
					Double value;
					value = iprot.readDouble();
					
					struct.setTax_rate(value);
				}
				
				
				
				
				
				if ("unit".equals(schemeField.trim())){
					
					vipapis.product.Unit value;
					
					value = null;
					String name = iprot.readString();
					vipapis.product.Unit[] values = vipapis.product.Unit.values(); 
					for(vipapis.product.Unit v : values){
						
						if(v.name().equals(name)){
							
							value = v;
							break;
						}
						
					}
					
					
					struct.setUnit(value);
				}
				
				
				
				
				
				if ("is_embargo".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setIs_embargo(value);
				}
				
				
				
				
				
				if ("is_fragile".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setIs_fragile(value);
				}
				
				
				
				
				
				if ("is_large".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setIs_large(value);
				}
				
				
				
				
				
				if ("is_precious".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setIs_precious(value);
				}
				
				
				
				
				
				if ("is_consumption_tax".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setIs_consumption_tax(value);
				}
				
				
				
				
				
				if ("washing_instruct".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setWashing_instruct(value);
				}
				
				
				
				
				
				if ("sale_service".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSale_service(value);
				}
				
				
				
				
				
				if ("sub_title".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSub_title(value);
				}
				
				
				
				
				
				if ("accessory_info".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAccessory_info(value);
				}
				
				
				
				
				
				if ("video_url".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setVideo_url(value);
				}
				
				
				
				
				
				if ("length".equals(schemeField.trim())){
					
					Double value;
					value = iprot.readDouble();
					
					struct.setLength(value);
				}
				
				
				
				
				
				if ("width".equals(schemeField.trim())){
					
					Double value;
					value = iprot.readDouble();
					
					struct.setWidth(value);
				}
				
				
				
				
				
				if ("high".equals(schemeField.trim())){
					
					Double value;
					value = iprot.readDouble();
					
					struct.setHigh(value);
				}
				
				
				
				
				
				if ("weight".equals(schemeField.trim())){
					
					Double value;
					value = iprot.readDouble();
					
					struct.setWeight(value);
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
	
	
	public void write(EditProductItem struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("vendor_id");
		oprot.writeI32(struct.getVendor_id()); 
		
		oprot.writeFieldEnd();
		
		oprot.writeFieldBegin("barcode");
		oprot.writeString(struct.getBarcode());
		
		oprot.writeFieldEnd();
		
		if(struct.getProduct_description() != null) {
			
			oprot.writeFieldBegin("product_description");
			oprot.writeString(struct.getProduct_description());
			
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
		
		
		if(struct.getArea_output() != null) {
			
			oprot.writeFieldBegin("area_output");
			oprot.writeString(struct.getArea_output());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSize() != null) {
			
			oprot.writeFieldBegin("size");
			oprot.writeString(struct.getSize());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMaterial() != null) {
			
			oprot.writeFieldBegin("material");
			
			oprot.writeMapBegin();
			for(Map.Entry< String, String > _ir0 : struct.getMaterial().entrySet()){
				
				String _key0 = _ir0.getKey();
				String _value0 = _ir0.getValue();
				oprot.writeString(_key0);
				
				oprot.writeString(_value0);
				
			}
			
			oprot.writeMapEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getColor() != null) {
			
			oprot.writeFieldBegin("color");
			oprot.writeString(struct.getColor());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getMarket_price() != null) {
			
			oprot.writeFieldBegin("market_price");
			oprot.writeDouble(struct.getMarket_price());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSell_price() != null) {
			
			oprot.writeFieldBegin("sell_price");
			oprot.writeDouble(struct.getSell_price());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTax_rate() != null) {
			
			oprot.writeFieldBegin("tax_rate");
			oprot.writeDouble(struct.getTax_rate());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getUnit() != null) {
			
			oprot.writeFieldBegin("unit");
			oprot.writeString(struct.getUnit().name());  
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIs_embargo() != null) {
			
			oprot.writeFieldBegin("is_embargo");
			oprot.writeI32(struct.getIs_embargo()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIs_fragile() != null) {
			
			oprot.writeFieldBegin("is_fragile");
			oprot.writeI32(struct.getIs_fragile()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIs_large() != null) {
			
			oprot.writeFieldBegin("is_large");
			oprot.writeI32(struct.getIs_large()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIs_precious() != null) {
			
			oprot.writeFieldBegin("is_precious");
			oprot.writeI32(struct.getIs_precious()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getIs_consumption_tax() != null) {
			
			oprot.writeFieldBegin("is_consumption_tax");
			oprot.writeI32(struct.getIs_consumption_tax()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getWashing_instruct() != null) {
			
			oprot.writeFieldBegin("washing_instruct");
			oprot.writeString(struct.getWashing_instruct());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSale_service() != null) {
			
			oprot.writeFieldBegin("sale_service");
			oprot.writeString(struct.getSale_service());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSub_title() != null) {
			
			oprot.writeFieldBegin("sub_title");
			oprot.writeString(struct.getSub_title());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAccessory_info() != null) {
			
			oprot.writeFieldBegin("accessory_info");
			oprot.writeString(struct.getAccessory_info());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getVideo_url() != null) {
			
			oprot.writeFieldBegin("video_url");
			oprot.writeString(struct.getVideo_url());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getLength() != null) {
			
			oprot.writeFieldBegin("length");
			oprot.writeDouble(struct.getLength());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getWidth() != null) {
			
			oprot.writeFieldBegin("width");
			oprot.writeDouble(struct.getWidth());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getHigh() != null) {
			
			oprot.writeFieldBegin("high");
			oprot.writeDouble(struct.getHigh());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getWeight() != null) {
			
			oprot.writeFieldBegin("weight");
			oprot.writeDouble(struct.getWeight());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(EditProductItem bean) throws OspException {
		
		
	}
	
	
}