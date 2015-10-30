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

public  class GetPoSkuListResponseHelper implements com.vip.osp.sdk.base.BeanSerializer<GetPoSkuListResponse>
{
	
	public static final GetPoSkuListResponseHelper OBJ = new GetPoSkuListResponseHelper();
	
	public static GetPoSkuListResponseHelper getInstance() {
		
		return OBJ;
	}
	
	
	public void read(GetPoSkuListResponse struct, Protocol iprot) throws OspException {
		
		
		String schemeStruct = iprot.readStructBegin();
		if(schemeStruct != null){
			
			while(true){
				
				String schemeField = iprot.readFieldBegin();
				if (schemeField == null) break;
				
				
				
				if ("purchase_order_sku_list".equals(schemeField.trim())){
					
					List<vipapis.delivery.PurchaseOrderSku> value;
					
					value = new ArrayList<vipapis.delivery.PurchaseOrderSku>();
					iprot.readListBegin();
					while(true){
						
						try{
							
							vipapis.delivery.PurchaseOrderSku elem0;
							
							elem0 = new vipapis.delivery.PurchaseOrderSku();
							vipapis.delivery.PurchaseOrderSkuHelper.getInstance().read(elem0, iprot);
							
							value.add(elem0);
						}
						catch(Exception e){
							
							break;
						}
					}
					
					iprot.readListEnd();
					
					struct.setPurchase_order_sku_list(value);
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
	
	
	public void write(GetPoSkuListResponse struct, Protocol oprot) throws OspException {
		
		validate(struct);
		oprot.writeStructBegin();
		
		if(struct.getPurchase_order_sku_list() != null) {
			
			oprot.writeFieldBegin("purchase_order_sku_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.PurchaseOrderSku _item0 : struct.getPurchase_order_sku_list()){
				
				
				vipapis.delivery.PurchaseOrderSkuHelper.getInstance().write(_item0, oprot);
				
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
	
	
	public void validate(GetPoSkuListResponse bean) throws OspException {
		
		
	}
	
	
}