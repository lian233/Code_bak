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

public  class PurchaseOrderSkuHelper implements com.vip.osp.sdk.base.BeanSerializer<PurchaseOrderSku>
{
	
	public static final PurchaseOrderSkuHelper OBJ = new PurchaseOrderSkuHelper();
	
	public static PurchaseOrderSkuHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(PurchaseOrderSku struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("sell_site".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSell_site(value);
				}
				
				
				
				
				
				if ("warehouse".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setWarehouse(value);
				}
				
				
				
				
				
				if ("barcode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setBarcode(value);
				}
				
				
				
				
				
				if ("amount".equals(schemeField.trim())){
					
					Integer value;
					value = iprot.readI32(); 
					
					struct.setAmount(value);
				}
				
				
				
				
				
				if ("order_cate".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_cate(value);
				}
				
				
				
				
				
				if ("order_status".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setOrder_status(value);
				}
				
				
				
				
				
				if ("create_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCreate_time(value);
				}
				
				
				
				
				
				if ("audit_time".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setAudit_time(value);
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
	
	
	public void write(PurchaseOrderSku struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getSell_site() != null) {
			
			oprot.writeFieldBegin("sell_site");
			oprot.writeString(struct.getSell_site());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getWarehouse() != null) {
			
			oprot.writeFieldBegin("warehouse");
			oprot.writeString(struct.getWarehouse());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getBarcode() != null) {
			
			oprot.writeFieldBegin("barcode");
			oprot.writeString(struct.getBarcode());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAmount() != null) {
			
			oprot.writeFieldBegin("amount");
			oprot.writeI32(struct.getAmount()); 
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOrder_cate() != null) {
			
			oprot.writeFieldBegin("order_cate");
			oprot.writeString(struct.getOrder_cate());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getOrder_status() != null) {
			
			oprot.writeFieldBegin("order_status");
			oprot.writeString(struct.getOrder_status());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCreate_time() != null) {
			
			oprot.writeFieldBegin("create_time");
			oprot.writeString(struct.getCreate_time());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getAudit_time() != null) {
			
			oprot.writeFieldBegin("audit_time");
			oprot.writeString(struct.getAudit_time());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(PurchaseOrderSku bean) throws OspException {
		
		
	}
	
	
}