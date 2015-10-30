package vipapis.delivery;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface DvdDeliveryService {
	
	
	
	vipapis.delivery.ShipResult editShipInfo( List<vipapis.delivery.Ship> ship_list ) throws OspException;
	
	
	vipapis.delivery.ExportOrderByIdResponse exportOrderById( String order_id ) throws OspException;
	
	
	vipapis.delivery.GetCarrierListResponse getCarrierList( String vendor_id,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.delivery.GetOrderDetailResponse getOrderDetail( String order_id,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.delivery.GetOrderListResponse getOrderList( String st_add_time,   String et_add_time,   vipapis.common.OrderStatus state,   String po_id,   String order_id,   String vendor_id,   Integer page,   Integer limit ) throws OspException;
	
	
	List<vipapis.delivery.DvdOrderStatus> getOrderStatusById( String order_id ) throws OspException;
	
	
	String getPrintTemplate( String print_type,   String order_id ) throws OspException;
	
	
	vipapis.delivery.GetReturnListResponse getReturnList( int vendor_id,   String st_create_time,   String et_create_time,   Integer state,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.delivery.GetReturnProductResponse getReturnProduct( String back_sn,   Integer page,   Integer limit ) throws OspException;
	
	
	Boolean mergeAfterSaleAddress( String vendor_id,   String username,   String address,   String postcode,   String tel ) throws OspException;
	
	
	vipapis.delivery.RefuseOrReturnProductResponse refuseOrder( List<vipapis.delivery.RefuseOrReturnOrder> refuse_product_list ) throws OspException;
	
	
	vipapis.delivery.RefuseOrReturnProductResponse returnOrder( List<vipapis.delivery.RefuseOrReturnOrder> dvd_return_list ) throws OspException;
	
	
	vipapis.delivery.ShipResult ship( List<vipapis.delivery.Ship> ship_list ) throws OspException;
	
}