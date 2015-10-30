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

public  class VendorProductResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<VendorProductResponse>
{
	
	public static final VendorProductResponseHelper OBJ = new VendorProductResponseHelper();
	
	public static VendorProductResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(VendorProductResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("success_num".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setSuccess_num(value);
				}
				
				
				
				
				
				if ("success_barcode_list".equals(schemeField.trim())){
					
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
					
					struct.setSuccess_barcode_list(value);
				}
				
				
				
				
				
				if ("fail_num".equals(schemeField.trim())){
					
					int value;
					value = iprot.readI32(); 
					
					struct.setFail_num(value);
				}
				
				
				
				
				
				if ("fail_item_list".equals(schemeField.trim())){
					
					List<vipapis.product.VendorProductFailItem> value;
					
					value = new ArrayList<vipapis.product.VendorProductFailItem>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.product.VendorProductFailItem elem1;
							
							elem1 = new vipapis.product.VendorProductFailItem();
							vipapis.product.VendorProductFailItemHelper.getInstance().read(elem1, iprot);
							
							value.add(elem1);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setFail_item_list(value);
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
	
	
	public void write(VendorProductResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		oprot.writeFieldBegin("success_num");
		oprot.writeI32(struct.getSuccess_num()); 
		
		oprot.writeFieldEnd();
		
		if(struct.getSuccess_barcode_list() != null) {
			
			oprot.writeFieldBegin("success_barcode_list");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getSuccess_barcode_list()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldBegin("fail_num");
		oprot.writeI32(struct.getFail_num()); 
		
		oprot.writeFieldEnd();
		
		if(struct.getFail_item_list() != null) {
			
			oprot.writeFieldBegin("fail_item_list");
			
			oprot.writeListBegin();
			for(vipapis.product.VendorProductFailItem _item0 : struct.getFail_item_list()){
				
				
				vipapis.product.VendorProductFailItemHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
		}
		
		
		oprot.writeFieldStop();
		oprot.writeStructEnd();
	}
	
	
	public void validate(VendorProductResponse bean) throws OspException {
		
		
	}
	
	
}