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

public  class PurchaseOrderHelper implements com.vip.osp.sdk.base.BeanSerializer<PurchaseOrder>
{
	
	public static final PurchaseOrderHelper OBJ = new PurchaseOrderHelper();
	
	public static PurchaseOrderHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(PurchaseOrder struct, Protocol iprot) throws OspException {
		
		
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
				
				
				
				
				
				if ("co_mode".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setCo_mode(value);
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
				
				
				
				
				
				if ("stock".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setStock(value);
				}
				
				
				
				
				
				if ("sales_volume".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setSales_volume(value);
				}
				
				
				
				
				
				if ("not_pick".equals(schemeField.trim())){
					
					String value;
					value = iprot.readString();
					
					struct.setNot_pick(value);
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
	
	
	public void write(PurchaseOrder struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getPo_no() != null) {
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getCo_mode() != null) {
			
			oprot.writeFieldBegin("co_mode");
			oprot.writeString(struct.getCo_mode());
			
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
		
		
		if(struct.getStock() != null) {
			
			oprot.writeFieldBegin("stock");
			oprot.writeString(struct.getStock());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getSales_volume() != null) {
			
			oprot.writeFieldBegin("sales_volume");
			oprot.writeString(struct.getSales_volume());
			
			oprot.writeFieldEnd();
		}
		
		
		if(struct.getNot_pick() != null) {
			
			oprot.writeFieldBegin("not_pick");
			oprot.writeString(struct.getNot_pick());
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(PurchaseOrder bean) throws OspException {
		
		
	}
	
	
}