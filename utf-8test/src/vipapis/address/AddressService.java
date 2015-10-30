package vipapis.address;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface AddressService {
	
	
	
	vipapis.address.FullAddress getFullAddress( String area_code,   vipapis.address.Is_Show_GAT is_show_gat,   Boolean is_bind ) throws OspException;
	
	
	List<vipapis.address.ProvinceWarehouse> getProvinceWarehouse( vipapis.address.Is_Show_GAT is_show_gat ) throws OspException;
	
}