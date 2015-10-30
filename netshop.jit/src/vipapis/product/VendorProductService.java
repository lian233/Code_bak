package vipapis.product;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface VendorProductService {
	
	
	
	vipapis.product.VendorProductResponse createProduct( List<vipapis.product.CreateProductItem> vendor_products ) throws OspException;
	
	
	vipapis.product.VendorProductResponse editProduct( List<vipapis.product.EditProductItem> vendor_products ) throws OspException;
	
	
	vipapis.product.MultiGetProductSkuResponse multiGetProductSkuInfo( int vendor_id,   String barcode,   Integer brand_ID,   Integer category_id,   String sn,   vipapis.product.ProductStatus status,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.product.MultiGetProductSpuResponse multiGetProductSpuInfo( int vendor_id,   Integer brand_id,   Integer category_id,   String sn,   vipapis.product.ProductStatus status,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.product.VendorProductResponse submitProduct( List<vipapis.product.VendorProductSkuKey> vendor_product_keys ) throws OspException;
	
}