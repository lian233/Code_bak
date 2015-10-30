package vipapis.delivery;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface JitDeliveryService {
	
	
	
	String confirmDelivery( int vendor_id,   String storage_no,   String po_no ) throws OspException;
	
	
	vipapis.delivery.CreateDeliveryResponse createDelivery( int vendor_id,   String po_no,   String delivery_no,   vipapis.common.Warehouse warehouse,   String delivery_time,   String arrival_time,   String race_time,   String carrier_name,   String tel,   String driver,   String driver_tel,   String plate_number,   Integer page,   Integer limit ) throws OspException;
	
	
	List<vipapis.delivery.SimplePick> createPick( String po_no,   int vendor_id ) throws OspException;
	
	
	List<vipapis.delivery.DeleteDeliveryDetail> deleteDeliveryDetail( int vendor_id,   String storage_no,   String po_no ) throws OspException;
	
	
	String editDelivery( int vendor_id,   String storage_no,   String delivery_no,   vipapis.common.Warehouse warehouse,   String delivery_time,   String arrival_time,   String race_time,   String carrier_name,   String tel,   String driver,   String driver_tel,   String plate_number,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.delivery.PickDetail getPickDetail( String po_no,   int vendor_id,   String pick_no,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.delivery.GetPickListResponse getPickList( int vendor_id,   String po_no,   String pick_no,   vipapis.common.Warehouse warehouse,   String co_mode,   String order_cate,   String st_create_time,   String et_create_time,   String st_sell_time_from,   String et_sell_time_from,   String st_sell_time_to,   String et_sell_time_to,   Integer is_export,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.delivery.GetPoListResponse getPoList( String st_create_time,   String et_create_time,   vipapis.common.Warehouse warehouse,   String po_no,   String co_mode,   String vendor_id,   String st_sell_st_time,   String et_sell_st_time,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.delivery.GetPoSkuListResponse getPoSkuList( int vendor_id,   String po_no,   String sell_site,   vipapis.common.Warehouse warehouse,   String order_status,   String st_aduity_time,   String et_aduity_time,   String order_id,   String pick_no,   String delivery_no,   String st_pick_time,   String et_pick_time,   String st_delivery_time,   String et_delivery_time,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.delivery.GetPrintBoxResponse getPrintBox( String pick_no,   String vendor_id ) throws OspException;
	
	
	vipapis.delivery.GetPrintDeliveryResponse getPrintDelivery( int vendor_id,   String storage_no,   String po_no,   String box_no ) throws OspException;
	
	
	String importDeliveryDetail( int vendor_id,   String po_no,   String storage_no,   String delivery_no,   List<vipapis.delivery.Delivery> delivery_list ) throws OspException;
	
}