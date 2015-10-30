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

public  class PickDetailHelper implements com.vip.osp.sdk.base.BeanSerializer<PickDetail>
{
	
	public static final PickDetailHelper OBJ = new PickDetailHelper();
	
	public static PickDetailHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(PickDetail struct, Protocol iprot) throws OspException {
		
		
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
				
				
				
				
				
				if ("sell_st_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSell_st_time(value);
				}
				
				
				
				
				
				if ("sell_et_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSell_et_time(value);
				}
				
				
				
				
				
				if ("export_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setExport_time(value);
				}
				
				
				
				
				
				if ("export_num".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setExport_num(value);
				}
				
				
				
				
				
				if ("warehouse".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setWarehouse(value);
				}
				
				
				
				
				
				if ("order_cate".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_cate(value);
				}
				
				
				
				
				
				if ("pick_product_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.PickProduct> value;
					
					value = new ArrayList<vipapis.delivery.PickProduct>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.PickProduct elem0;
							
							elem0 = new vipapis.delivery.PickProduct();
							vipapis.delivery.PickProductHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setPick_product_list(value);
				}
				
				
				
				
				
				if ("total".equals(schemeField.trim())){
					
					Integer value;
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
	
	
	public void write(PickDetail struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getPo_no() != null) {
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSell_st_time() != null) {
			
			oprot.writeFieldBegin("sell_st_time");
			oprot.writeString(struct.getSell_st_time());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSell_et_time() != null) {
			
			oprot.writeFieldBegin("sell_et_time");
			oprot.writeString(struct.getSell_et_time());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getExport_time() != null) {
			
			oprot.writeFieldBegin("export_time");
			oprot.writeString(struct.getExport_time());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getExport_num() != null) {
			
			oprot.writeFieldBegin("export_num");
			oprot.writeI32(struct.getExport_num()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getWarehouse() != null) {
			
			oprot.writeFieldBegin("warehouse");
			oprot.writeString(struct.getWarehouse());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOrder_cate() != null) {
			
			oprot.writeFieldBegin("order_cate");
			oprot.writeString(struct.getOrder_cate());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getPick_product_list() != null) {
			
			oprot.writeFieldBegin("pick_product_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.PickProduct _item0 : struct.getPick_product_list()){
				
				
				vipapis.delivery.PickProductHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getTotal() != null) {
			
			oprot.writeFieldBegin("total");
			oprot.writeI32(struct.getTotal()); 
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(PickDetail bean) throws OspException {
		
		
	}
	
	
}