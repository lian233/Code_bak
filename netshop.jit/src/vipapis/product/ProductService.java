package vipapis.product;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface ProductService {
	
	
	
	vipapis.product.GetProductListResponse getProductList( vipapis.common.Warehouse warehouse,   String schedule_id,   Integer channel_id,   Integer category_id,   String start_time,   String end_time,   String product_id,   String product_name,   Integer sell_price_min,   Integer sell_price_max,   Double discount_min,   Double discount_max,   vipapis.product.SortType sort_type,   vipapis.product.StockShowType stock_show_type,   Integer page,   Integer limit,   String cursorMark ) throws OspException;
	
	
	vipapis.product.GetProductStockResponse getProductStock( vipapis.common.Warehouse warehouse,   String schedule_id,   Integer channel_id,   Integer category_id,   String start_time,   String end_time,   String product_id,   String product_name,   Integer sell_price_min,   Integer sell_price_max,   Double discount_min,   Double discount_max,   vipapis.product.SortType sort_type,   vipapis.product.StockShowType stock_show_type,   Integer page,   Integer limit,   String cursorMark ) throws OspException;
	
}