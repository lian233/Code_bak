package vipapis.brand;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface BrandService {
	
	
	
	vipapis.brand.BrandInfo getBrandInfo( String brand_id ) throws OspException;
	
	
	vipapis.brand.MultiGetBrandResponse multiGetBrand( vipapis.brand.BrandSearchType search_type,   String word,   Integer page,   Integer limit ) throws OspException;
	
}