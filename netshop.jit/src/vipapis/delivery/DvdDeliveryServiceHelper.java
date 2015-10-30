package vipapis.delivery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public class DvdDeliveryServiceHelper {
	
	
	
	
	public static class editShipInfo_args {
		
		/**
		* 发货单
		* @sampleValue ship_list 
		*/
		
		private List<vipapis.delivery.Ship> ship_list;
		
		public List<vipapis.delivery.Ship> getShip_list(){
			return this.ship_list;
		}
		
		public void setShip_list(List<vipapis.delivery.Ship> value){
			this.ship_list = value;
		}
		
	}
	
	
	
	
	public static class exportOrderById_args {
		
		/**
		* 导出订单的订单号码(多个订单号码之间用半角逗号区分)
		* @sampleValue order_id 
		*/
		
		private String order_id;
		
		public String getOrder_id(){
			return this.order_id;
		}
		
		public void setOrder_id(String value){
			this.order_id = value;
		}
		
	}
	
	
	
	
	public static class getCarrierList_args {
		
		/**
		* 供应商ID
		* @sampleValue vendor_id 550
		*/
		
		private String vendor_id;
		
		/**
		* 页码
		*/
		
		private Integer page;
		
		/**
		* 每页记录数，默认50，最大100
		*/
		
		private Integer limit;
		
		public String getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(String value){
			this.vendor_id = value;
		}
		public Integer getPage(){
			return this.page;
		}
		
		public void setPage(Integer value){
			this.page = value;
		}
		public Integer getLimit(){
			return this.limit;
		}
		
		public void setLimit(Integer value){
			this.limit = value;
		}
		
	}
	
	
	
	
	public static class getOrderDetail_args {
		
		/**
		* 需要查询订单明细的订单号(多个订单号之间用半角逗号区分)
		* @sampleValue order_id 
		*/
		
		private String order_id;
		
		/**
		* 页码
		*/
		
		private Integer page;
		
		/**
		* 每页记录数，默认50，最大100
		*/
		
		private Integer limit;
		
		public String getOrder_id(){
			return this.order_id;
		}
		
		public void setOrder_id(String value){
			this.order_id = value;
		}
		public Integer getPage(){
			return this.page;
		}
		
		public void setPage(Integer value){
			this.page = value;
		}
		public Integer getLimit(){
			return this.limit;
		}
		
		public void setLimit(Integer value){
			this.limit = value;
		}
		
	}
	
	
	
	
	public static class getOrderList_args {
		
		/**
		* 开始查询的下单时间 以订单下单时间为准
		* @sampleValue st_add_time 2014-07-01 10:00:00
		*/
		
		private String st_add_time;
		
		/**
		* 结束查询的下单时间 以订单下单时间为准
		* @sampleValue et_add_time 2014-07-01 10:00:00
		*/
		
		private String et_add_time;
		
		/**
		* 订单状态编码
		* @sampleValue state 
		*/
		
		private vipapis.common.OrderStatus state;
		
		/**
		* po号(多个用逗号分隔最多限制5个)
		* @sampleValue po_id 
		*/
		
		private String po_id;
		
		/**
		* 订单号
		* @sampleValue order_id 
		*/
		
		private String order_id;
		
		/**
		* 供应商ID
		* @sampleValue vendor_id 
		*/
		
		private String vendor_id;
		
		/**
		* 页码
		*/
		
		private Integer page;
		
		/**
		* 每页记录数，默认50，最大100
		*/
		
		private Integer limit;
		
		public String getSt_add_time(){
			return this.st_add_time;
		}
		
		public void setSt_add_time(String value){
			this.st_add_time = value;
		}
		public String getEt_add_time(){
			return this.et_add_time;
		}
		
		public void setEt_add_time(String value){
			this.et_add_time = value;
		}
		public vipapis.common.OrderStatus getState(){
			return this.state;
		}
		
		public void setState(vipapis.common.OrderStatus value){
			this.state = value;
		}
		public String getPo_id(){
			return this.po_id;
		}
		
		public void setPo_id(String value){
			this.po_id = value;
		}
		public String getOrder_id(){
			return this.order_id;
		}
		
		public void setOrder_id(String value){
			this.order_id = value;
		}
		public String getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(String value){
			this.vendor_id = value;
		}
		public Integer getPage(){
			return this.page;
		}
		
		public void setPage(Integer value){
			this.page = value;
		}
		public Integer getLimit(){
			return this.limit;
		}
		
		public void setLimit(Integer value){
			this.limit = value;
		}
		
	}
	
	
	
	
	public static class getOrderStatusById_args {
		
		/**
		* 需要查询订单状态的订单号 多个订单号之间用半角逗号区分
		* @sampleValue order_id 
		*/
		
		private String order_id;
		
		public String getOrder_id(){
			return this.order_id;
		}
		
		public void setOrder_id(String value){
			this.order_id = value;
		}
		
	}
	
	
	
	
	public static class getPrintTemplate_args {
		
		/**
		* 打印类型 A4 B5，默认B5
		* @sampleValue print_type A4
		*/
		
		private String print_type;
		
		/**
		* 订单编号
		* @sampleValue order_id 14060901371013
		*/
		
		private String order_id;
		
		public String getPrint_type(){
			return this.print_type;
		}
		
		public void setPrint_type(String value){
			this.print_type = value;
		}
		public String getOrder_id(){
			return this.order_id;
		}
		
		public void setOrder_id(String value){
			this.order_id = value;
		}
		
	}
	
	
	
	
	public static class getReturnList_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* 开始查询的下单时间 以退货申请单申请时间为准
		* @sampleValue st_create_time 2013-11-28
		*/
		
		private String st_create_time;
		
		/**
		* 结束查询的下单时间 以退货申请单申请时间为准
		* @sampleValue et_create_time 2014-11-28
		*/
		
		private String et_create_time;
		
		/**
		* 退货申请单状态 ,默认为全部状态
		*/
		
		private Integer state;
		
		/**
		* 页码
		*/
		
		private Integer page;
		
		/**
		* 每页记录数，默认50，最大100
		*/
		
		private Integer limit;
		
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		public String getSt_create_time(){
			return this.st_create_time;
		}
		
		public void setSt_create_time(String value){
			this.st_create_time = value;
		}
		public String getEt_create_time(){
			return this.et_create_time;
		}
		
		public void setEt_create_time(String value){
			this.et_create_time = value;
		}
		public Integer getState(){
			return this.state;
		}
		
		public void setState(Integer value){
			this.state = value;
		}
		public Integer getPage(){
			return this.page;
		}
		
		public void setPage(Integer value){
			this.page = value;
		}
		public Integer getLimit(){
			return this.limit;
		}
		
		public void setLimit(Integer value){
			this.limit = value;
		}
		
	}
	
	
	
	
	public static class getReturnProduct_args {
		
		/**
		* 需要查询订单明细的订单号(多个订单号之间用半角逗号区分)
		* @sampleValue back_sn 5556
		*/
		
		private String back_sn;
		
		/**
		* 页码
		*/
		
		private Integer page;
		
		/**
		* 每页记录数，默认50，最大100
		*/
		
		private Integer limit;
		
		public String getBack_sn(){
			return this.back_sn;
		}
		
		public void setBack_sn(String value){
			this.back_sn = value;
		}
		public Integer getPage(){
			return this.page;
		}
		
		public void setPage(Integer value){
			this.page = value;
		}
		public Integer getLimit(){
			return this.limit;
		}
		
		public void setLimit(Integer value){
			this.limit = value;
		}
		
	}
	
	
	
	
	public static class mergeAfterSaleAddress_args {
		
		/**
		* 供应商ID
		* @sampleValue vendor_id 
		*/
		
		private String vendor_id;
		
		/**
		* 收货人
		* @sampleValue username 
		*/
		
		private String username;
		
		/**
		* 收货地址
		* @sampleValue address 
		*/
		
		private String address;
		
		/**
		* 邮政编码
		* @sampleValue postcode 
		*/
		
		private String postcode;
		
		/**
		* 联系电话
		* @sampleValue tel 
		*/
		
		private String tel;
		
		public String getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(String value){
			this.vendor_id = value;
		}
		public String getUsername(){
			return this.username;
		}
		
		public void setUsername(String value){
			this.username = value;
		}
		public String getAddress(){
			return this.address;
		}
		
		public void setAddress(String value){
			this.address = value;
		}
		public String getPostcode(){
			return this.postcode;
		}
		
		public void setPostcode(String value){
			this.postcode = value;
		}
		public String getTel(){
			return this.tel;
		}
		
		public void setTel(String value){
			this.tel = value;
		}
		
	}
	
	
	
	
	public static class refuseOrder_args {
		
		/**
		* 订单拒收信息
		*/
		
		private List<vipapis.delivery.RefuseOrReturnOrder> refuse_product_list;
		
		public List<vipapis.delivery.RefuseOrReturnOrder> getRefuse_product_list(){
			return this.refuse_product_list;
		}
		
		public void setRefuse_product_list(List<vipapis.delivery.RefuseOrReturnOrder> value){
			this.refuse_product_list = value;
		}
		
	}
	
	
	
	
	public static class returnOrder_args {
		
		/**
		* 退货信息
		*/
		
		private List<vipapis.delivery.RefuseOrReturnOrder> dvd_return_list;
		
		public List<vipapis.delivery.RefuseOrReturnOrder> getDvd_return_list(){
			return this.dvd_return_list;
		}
		
		public void setDvd_return_list(List<vipapis.delivery.RefuseOrReturnOrder> value){
			this.dvd_return_list = value;
		}
		
	}
	
	
	
	
	public static class ship_args {
		
		/**
		* 发货单
		* @sampleValue ship_list 
		*/
		
		private List<vipapis.delivery.Ship> ship_list;
		
		public List<vipapis.delivery.Ship> getShip_list(){
			return this.ship_list;
		}
		
		public void setShip_list(List<vipapis.delivery.Ship> value){
			this.ship_list = value;
		}
		
	}
	
	
	
	
	public static class editShipInfo_result {
		
		/**
		*/
		
		private vipapis.delivery.ShipResult success;
		
		public vipapis.delivery.ShipResult getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.ShipResult value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class exportOrderById_result {
		
		/**
		*/
		
		private vipapis.delivery.ExportOrderByIdResponse success;
		
		public vipapis.delivery.ExportOrderByIdResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.ExportOrderByIdResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getCarrierList_result {
		
		/**
		*/
		
		private vipapis.delivery.GetCarrierListResponse success;
		
		public vipapis.delivery.GetCarrierListResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetCarrierListResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getOrderDetail_result {
		
		/**
		*/
		
		private vipapis.delivery.GetOrderDetailResponse success;
		
		public vipapis.delivery.GetOrderDetailResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetOrderDetailResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getOrderList_result {
		
		/**
		*/
		
		private vipapis.delivery.GetOrderListResponse success;
		
		public vipapis.delivery.GetOrderListResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetOrderListResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getOrderStatusById_result {
		
		/**
		*/
		
		private List<vipapis.delivery.DvdOrderStatus> success;
		
		public List<vipapis.delivery.DvdOrderStatus> getSuccess(){
			return this.success;
		}
		
		public void setSuccess(List<vipapis.delivery.DvdOrderStatus> value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getPrintTemplate_result {
		
		/**
		* 打印页面HTML
		*/
		
		private String success;
		
		public String getSuccess(){
			return this.success;
		}
		
		public void setSuccess(String value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getReturnList_result {
		
		/**
		*/
		
		private vipapis.delivery.GetReturnListResponse success;
		
		public vipapis.delivery.GetReturnListResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetReturnListResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getReturnProduct_result {
		
		/**
		*/
		
		private vipapis.delivery.GetReturnProductResponse success;
		
		public vipapis.delivery.GetReturnProductResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetReturnProductResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class mergeAfterSaleAddress_result {
		
		/**
		* 是否推送成功
		*/
		
		private Boolean success;
		
		public Boolean getSuccess(){
			return this.success;
		}
		
		public void setSuccess(Boolean value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class refuseOrder_result {
		
		/**
		*/
		
		private vipapis.delivery.RefuseOrReturnProductResponse success;
		
		public vipapis.delivery.RefuseOrReturnProductResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.RefuseOrReturnProductResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class returnOrder_result {
		
		/**
		*/
		
		private vipapis.delivery.RefuseOrReturnProductResponse success;
		
		public vipapis.delivery.RefuseOrReturnProductResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.RefuseOrReturnProductResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class ship_result {
		
		/**
		*/
		
		private vipapis.delivery.ShipResult success;
		
		public vipapis.delivery.ShipResult getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.ShipResult value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class editShipInfo_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<editShipInfo_args>
	{
		
		public static final editShipInfo_argsHelper OBJ = new editShipInfo_argsHelper();
		
		public static editShipInfo_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(editShipInfo_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.delivery.Ship> value;
				
				value = new ArrayList<vipapis.delivery.Ship>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.delivery.Ship elem0;
						
						elem0 = new vipapis.delivery.Ship();
						vipapis.delivery.ShipHelper.getInstance().read(elem0, iprot);
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setShip_list(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(editShipInfo_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("ship_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.Ship _item0 : struct.getShip_list()){
				
				
				vipapis.delivery.ShipHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(editShipInfo_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class exportOrderById_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<exportOrderById_args>
	{
		
		public static final exportOrderById_argsHelper OBJ = new exportOrderById_argsHelper();
		
		public static exportOrderById_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(exportOrderById_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setOrder_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(exportOrderById_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(exportOrderById_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCarrierList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getCarrierList_args>
	{
		
		public static final getCarrierList_argsHelper OBJ = new getCarrierList_argsHelper();
		
		public static getCarrierList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCarrierList_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setPage(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setLimit(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCarrierList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeString(struct.getVendor_id());
			
			oprot.writeFieldEnd();
			
			if(struct.getPage() != null) {
				
				oprot.writeFieldBegin("page");
				oprot.writeI32(struct.getPage()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getLimit() != null) {
				
				oprot.writeFieldBegin("limit");
				oprot.writeI32(struct.getLimit()); 
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCarrierList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getOrderDetail_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getOrderDetail_args>
	{
		
		public static final getOrderDetail_argsHelper OBJ = new getOrderDetail_argsHelper();
		
		public static getOrderDetail_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getOrderDetail_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setOrder_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setPage(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setLimit(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getOrderDetail_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
			
			if(struct.getPage() != null) {
				
				oprot.writeFieldBegin("page");
				oprot.writeI32(struct.getPage()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getLimit() != null) {
				
				oprot.writeFieldBegin("limit");
				oprot.writeI32(struct.getLimit()); 
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getOrderDetail_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getOrderList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getOrderList_args>
	{
		
		public static final getOrderList_argsHelper OBJ = new getOrderList_argsHelper();
		
		public static getOrderList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getOrderList_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSt_add_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_add_time(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.common.OrderStatus value;
				
				value = null;
				String name = iprot.readString();
				vipapis.common.OrderStatus[] values = vipapis.common.OrderStatus.values(); 
				for(vipapis.common.OrderStatus v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setState(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setOrder_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setPage(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setLimit(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getOrderList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("st_add_time");
			oprot.writeString(struct.getSt_add_time());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("et_add_time");
			oprot.writeString(struct.getEt_add_time());
			
			oprot.writeFieldEnd();
			
			if(struct.getState() != null) {
				
				oprot.writeFieldBegin("state");
				oprot.writeString(struct.getState().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getPo_id() != null) {
				
				oprot.writeFieldBegin("po_id");
				oprot.writeString(struct.getPo_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getOrder_id() != null) {
				
				oprot.writeFieldBegin("order_id");
				oprot.writeString(struct.getOrder_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getVendor_id() != null) {
				
				oprot.writeFieldBegin("vendor_id");
				oprot.writeString(struct.getVendor_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getPage() != null) {
				
				oprot.writeFieldBegin("page");
				oprot.writeI32(struct.getPage()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getLimit() != null) {
				
				oprot.writeFieldBegin("limit");
				oprot.writeI32(struct.getLimit()); 
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getOrderList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getOrderStatusById_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getOrderStatusById_args>
	{
		
		public static final getOrderStatusById_argsHelper OBJ = new getOrderStatusById_argsHelper();
		
		public static getOrderStatusById_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getOrderStatusById_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setOrder_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getOrderStatusById_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getOrderStatusById_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPrintTemplate_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getPrintTemplate_args>
	{
		
		public static final getPrintTemplate_argsHelper OBJ = new getPrintTemplate_argsHelper();
		
		public static getPrintTemplate_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPrintTemplate_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPrint_type(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setOrder_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPrintTemplate_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getPrint_type() != null) {
				
				oprot.writeFieldBegin("print_type");
				oprot.writeString(struct.getPrint_type());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldBegin("order_id");
			oprot.writeString(struct.getOrder_id());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPrintTemplate_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getReturnList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getReturnList_args>
	{
		
		public static final getReturnList_argsHelper OBJ = new getReturnList_argsHelper();
		
		public static getReturnList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getReturnList_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSt_create_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_create_time(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setState(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setPage(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setLimit(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getReturnList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("st_create_time");
			oprot.writeString(struct.getSt_create_time());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("et_create_time");
			oprot.writeString(struct.getEt_create_time());
			
			oprot.writeFieldEnd();
			
			if(struct.getState() != null) {
				
				oprot.writeFieldBegin("state");
				oprot.writeI32(struct.getState()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getPage() != null) {
				
				oprot.writeFieldBegin("page");
				oprot.writeI32(struct.getPage()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getLimit() != null) {
				
				oprot.writeFieldBegin("limit");
				oprot.writeI32(struct.getLimit()); 
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getReturnList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getReturnProduct_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getReturnProduct_args>
	{
		
		public static final getReturnProduct_argsHelper OBJ = new getReturnProduct_argsHelper();
		
		public static getReturnProduct_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getReturnProduct_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setBack_sn(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setPage(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setLimit(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getReturnProduct_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("back_sn");
			oprot.writeString(struct.getBack_sn());
			
			oprot.writeFieldEnd();
			
			if(struct.getPage() != null) {
				
				oprot.writeFieldBegin("page");
				oprot.writeI32(struct.getPage()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getLimit() != null) {
				
				oprot.writeFieldBegin("limit");
				oprot.writeI32(struct.getLimit()); 
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getReturnProduct_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class mergeAfterSaleAddress_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<mergeAfterSaleAddress_args>
	{
		
		public static final mergeAfterSaleAddress_argsHelper OBJ = new mergeAfterSaleAddress_argsHelper();
		
		public static mergeAfterSaleAddress_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(mergeAfterSaleAddress_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setUsername(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setAddress(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPostcode(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setTel(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(mergeAfterSaleAddress_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeString(struct.getVendor_id());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("username");
			oprot.writeString(struct.getUsername());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("address");
			oprot.writeString(struct.getAddress());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("postcode");
			oprot.writeString(struct.getPostcode());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("tel");
			oprot.writeString(struct.getTel());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(mergeAfterSaleAddress_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class refuseOrder_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<refuseOrder_args>
	{
		
		public static final refuseOrder_argsHelper OBJ = new refuseOrder_argsHelper();
		
		public static refuseOrder_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(refuseOrder_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.delivery.RefuseOrReturnOrder> value;
				
				value = new ArrayList<vipapis.delivery.RefuseOrReturnOrder>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.delivery.RefuseOrReturnOrder elem0;
						
						elem0 = new vipapis.delivery.RefuseOrReturnOrder();
						vipapis.delivery.RefuseOrReturnOrderHelper.getInstance().read(elem0, iprot);
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setRefuse_product_list(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(refuseOrder_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("refuse_product_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.RefuseOrReturnOrder _item0 : struct.getRefuse_product_list()){
				
				
				vipapis.delivery.RefuseOrReturnOrderHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(refuseOrder_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class returnOrder_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<returnOrder_args>
	{
		
		public static final returnOrder_argsHelper OBJ = new returnOrder_argsHelper();
		
		public static returnOrder_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(returnOrder_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.delivery.RefuseOrReturnOrder> value;
				
				value = new ArrayList<vipapis.delivery.RefuseOrReturnOrder>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.delivery.RefuseOrReturnOrder elem1;
						
						elem1 = new vipapis.delivery.RefuseOrReturnOrder();
						vipapis.delivery.RefuseOrReturnOrderHelper.getInstance().read(elem1, iprot);
						
						value.add(elem1);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setDvd_return_list(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(returnOrder_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("dvd_return_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.RefuseOrReturnOrder _item0 : struct.getDvd_return_list()){
				
				
				vipapis.delivery.RefuseOrReturnOrderHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(returnOrder_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class ship_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<ship_args>
	{
		
		public static final ship_argsHelper OBJ = new ship_argsHelper();
		
		public static ship_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(ship_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.delivery.Ship> value;
				
				value = new ArrayList<vipapis.delivery.Ship>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.delivery.Ship elem1;
						
						elem1 = new vipapis.delivery.Ship();
						vipapis.delivery.ShipHelper.getInstance().read(elem1, iprot);
						
						value.add(elem1);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setShip_list(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(ship_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("ship_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.Ship _item0 : struct.getShip_list()){
				
				
				vipapis.delivery.ShipHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(ship_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class editShipInfo_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<editShipInfo_result>
	{
		
		public static final editShipInfo_resultHelper OBJ = new editShipInfo_resultHelper();
		
		public static editShipInfo_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(editShipInfo_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.ShipResult value;
				
				value = new vipapis.delivery.ShipResult();
				vipapis.delivery.ShipResultHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(editShipInfo_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.ShipResultHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(editShipInfo_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class exportOrderById_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<exportOrderById_result>
	{
		
		public static final exportOrderById_resultHelper OBJ = new exportOrderById_resultHelper();
		
		public static exportOrderById_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(exportOrderById_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.ExportOrderByIdResponse value;
				
				value = new vipapis.delivery.ExportOrderByIdResponse();
				vipapis.delivery.ExportOrderByIdResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(exportOrderById_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.ExportOrderByIdResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(exportOrderById_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCarrierList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getCarrierList_result>
	{
		
		public static final getCarrierList_resultHelper OBJ = new getCarrierList_resultHelper();
		
		public static getCarrierList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCarrierList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetCarrierListResponse value;
				
				value = new vipapis.delivery.GetCarrierListResponse();
				vipapis.delivery.GetCarrierListResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCarrierList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetCarrierListResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCarrierList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getOrderDetail_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getOrderDetail_result>
	{
		
		public static final getOrderDetail_resultHelper OBJ = new getOrderDetail_resultHelper();
		
		public static getOrderDetail_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getOrderDetail_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetOrderDetailResponse value;
				
				value = new vipapis.delivery.GetOrderDetailResponse();
				vipapis.delivery.GetOrderDetailResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getOrderDetail_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetOrderDetailResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getOrderDetail_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getOrderList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getOrderList_result>
	{
		
		public static final getOrderList_resultHelper OBJ = new getOrderList_resultHelper();
		
		public static getOrderList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getOrderList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetOrderListResponse value;
				
				value = new vipapis.delivery.GetOrderListResponse();
				vipapis.delivery.GetOrderListResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getOrderList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetOrderListResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getOrderList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getOrderStatusById_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getOrderStatusById_result>
	{
		
		public static final getOrderStatusById_resultHelper OBJ = new getOrderStatusById_resultHelper();
		
		public static getOrderStatusById_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getOrderStatusById_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.delivery.DvdOrderStatus> value;
				
				value = new ArrayList<vipapis.delivery.DvdOrderStatus>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.delivery.DvdOrderStatus elem0;
						
						elem0 = new vipapis.delivery.DvdOrderStatus();
						vipapis.delivery.DvdOrderStatusHelper.getInstance().read(elem0, iprot);
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getOrderStatusById_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				oprot.writeListBegin();
				for(vipapis.delivery.DvdOrderStatus _item0 : struct.getSuccess()){
					
					
					vipapis.delivery.DvdOrderStatusHelper.getInstance().write(_item0, oprot);
					
				}
				
				oprot.writeListEnd();
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getOrderStatusById_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPrintTemplate_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getPrintTemplate_result>
	{
		
		public static final getPrintTemplate_resultHelper OBJ = new getPrintTemplate_resultHelper();
		
		public static getPrintTemplate_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPrintTemplate_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPrintTemplate_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				oprot.writeString(struct.getSuccess());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPrintTemplate_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getReturnList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getReturnList_result>
	{
		
		public static final getReturnList_resultHelper OBJ = new getReturnList_resultHelper();
		
		public static getReturnList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getReturnList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetReturnListResponse value;
				
				value = new vipapis.delivery.GetReturnListResponse();
				vipapis.delivery.GetReturnListResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getReturnList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetReturnListResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getReturnList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getReturnProduct_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getReturnProduct_result>
	{
		
		public static final getReturnProduct_resultHelper OBJ = new getReturnProduct_resultHelper();
		
		public static getReturnProduct_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getReturnProduct_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetReturnProductResponse value;
				
				value = new vipapis.delivery.GetReturnProductResponse();
				vipapis.delivery.GetReturnProductResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getReturnProduct_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetReturnProductResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getReturnProduct_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class mergeAfterSaleAddress_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<mergeAfterSaleAddress_result>
	{
		
		public static final mergeAfterSaleAddress_resultHelper OBJ = new mergeAfterSaleAddress_resultHelper();
		
		public static mergeAfterSaleAddress_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(mergeAfterSaleAddress_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				Boolean value;
				value = iprot.readBool();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(mergeAfterSaleAddress_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				oprot.writeBool(struct.getSuccess());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(mergeAfterSaleAddress_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class refuseOrder_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<refuseOrder_result>
	{
		
		public static final refuseOrder_resultHelper OBJ = new refuseOrder_resultHelper();
		
		public static refuseOrder_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(refuseOrder_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.RefuseOrReturnProductResponse value;
				
				value = new vipapis.delivery.RefuseOrReturnProductResponse();
				vipapis.delivery.RefuseOrReturnProductResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(refuseOrder_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.RefuseOrReturnProductResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(refuseOrder_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class returnOrder_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<returnOrder_result>
	{
		
		public static final returnOrder_resultHelper OBJ = new returnOrder_resultHelper();
		
		public static returnOrder_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(returnOrder_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.RefuseOrReturnProductResponse value;
				
				value = new vipapis.delivery.RefuseOrReturnProductResponse();
				vipapis.delivery.RefuseOrReturnProductResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(returnOrder_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.RefuseOrReturnProductResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(returnOrder_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class ship_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<ship_result>
	{
		
		public static final ship_resultHelper OBJ = new ship_resultHelper();
		
		public static ship_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(ship_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.ShipResult value;
				
				value = new vipapis.delivery.ShipResult();
				vipapis.delivery.ShipResultHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(ship_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.ShipResultHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(ship_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class DvdDeliveryServiceClient extends OspRestStub implements DvdDeliveryService  {
		
		
		public DvdDeliveryServiceClient() {
			
			super("1.0.0", "vipapis.delivery.DvdDeliveryService");
		}
		
		
		
		public vipapis.delivery.ShipResult editShipInfo(List<vipapis.delivery.Ship> ship_list) throws OspException {
			
			send_editShipInfo(ship_list);
			return recv_editShipInfo(); 
			
		}
		
		
		private void send_editShipInfo(List<vipapis.delivery.Ship> ship_list) throws OspException {
			
			initInvocation("editShipInfo");
			
			editShipInfo_args args = new editShipInfo_args();
			args.setShip_list(ship_list);
			
			sendBase(args, editShipInfo_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.ShipResult recv_editShipInfo() throws OspException {
			
			editShipInfo_result result = new editShipInfo_result();
			receiveBase(result, editShipInfo_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.ExportOrderByIdResponse exportOrderById(String order_id) throws OspException {
			
			send_exportOrderById(order_id);
			return recv_exportOrderById(); 
			
		}
		
		
		private void send_exportOrderById(String order_id) throws OspException {
			
			initInvocation("exportOrderById");
			
			exportOrderById_args args = new exportOrderById_args();
			args.setOrder_id(order_id);
			
			sendBase(args, exportOrderById_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.ExportOrderByIdResponse recv_exportOrderById() throws OspException {
			
			exportOrderById_result result = new exportOrderById_result();
			receiveBase(result, exportOrderById_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetCarrierListResponse getCarrierList(String vendor_id,Integer page,Integer limit) throws OspException {
			
			send_getCarrierList(vendor_id,page,limit);
			return recv_getCarrierList(); 
			
		}
		
		
		private void send_getCarrierList(String vendor_id,Integer page,Integer limit) throws OspException {
			
			initInvocation("getCarrierList");
			
			getCarrierList_args args = new getCarrierList_args();
			args.setVendor_id(vendor_id);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getCarrierList_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetCarrierListResponse recv_getCarrierList() throws OspException {
			
			getCarrierList_result result = new getCarrierList_result();
			receiveBase(result, getCarrierList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetOrderDetailResponse getOrderDetail(String order_id,Integer page,Integer limit) throws OspException {
			
			send_getOrderDetail(order_id,page,limit);
			return recv_getOrderDetail(); 
			
		}
		
		
		private void send_getOrderDetail(String order_id,Integer page,Integer limit) throws OspException {
			
			initInvocation("getOrderDetail");
			
			getOrderDetail_args args = new getOrderDetail_args();
			args.setOrder_id(order_id);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getOrderDetail_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetOrderDetailResponse recv_getOrderDetail() throws OspException {
			
			getOrderDetail_result result = new getOrderDetail_result();
			receiveBase(result, getOrderDetail_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetOrderListResponse getOrderList(String st_add_time,String et_add_time,vipapis.common.OrderStatus state,String po_id,String order_id,String vendor_id,Integer page,Integer limit) throws OspException {
			
			send_getOrderList(st_add_time,et_add_time,state,po_id,order_id,vendor_id,page,limit);
			return recv_getOrderList(); 
			
		}
		
		
		private void send_getOrderList(String st_add_time,String et_add_time,vipapis.common.OrderStatus state,String po_id,String order_id,String vendor_id,Integer page,Integer limit) throws OspException {
			
			initInvocation("getOrderList");
			
			getOrderList_args args = new getOrderList_args();
			args.setSt_add_time(st_add_time);
			args.setEt_add_time(et_add_time);
			args.setState(state);
			args.setPo_id(po_id);
			args.setOrder_id(order_id);
			args.setVendor_id(vendor_id);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getOrderList_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetOrderListResponse recv_getOrderList() throws OspException {
			
			getOrderList_result result = new getOrderList_result();
			receiveBase(result, getOrderList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public List<vipapis.delivery.DvdOrderStatus> getOrderStatusById(String order_id) throws OspException {
			
			send_getOrderStatusById(order_id);
			return recv_getOrderStatusById(); 
			
		}
		
		
		private void send_getOrderStatusById(String order_id) throws OspException {
			
			initInvocation("getOrderStatusById");
			
			getOrderStatusById_args args = new getOrderStatusById_args();
			args.setOrder_id(order_id);
			
			sendBase(args, getOrderStatusById_argsHelper.getInstance());
		}
		
		
		private List<vipapis.delivery.DvdOrderStatus> recv_getOrderStatusById() throws OspException {
			
			getOrderStatusById_result result = new getOrderStatusById_result();
			receiveBase(result, getOrderStatusById_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public String getPrintTemplate(String print_type,String order_id) throws OspException {
			
			send_getPrintTemplate(print_type,order_id);
			return recv_getPrintTemplate(); 
			
		}
		
		
		private void send_getPrintTemplate(String print_type,String order_id) throws OspException {
			
			initInvocation("getPrintTemplate");
			
			getPrintTemplate_args args = new getPrintTemplate_args();
			args.setPrint_type(print_type);
			args.setOrder_id(order_id);
			
			sendBase(args, getPrintTemplate_argsHelper.getInstance());
		}
		
		
		private String recv_getPrintTemplate() throws OspException {
			
			getPrintTemplate_result result = new getPrintTemplate_result();
			receiveBase(result, getPrintTemplate_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetReturnListResponse getReturnList(int vendor_id,String st_create_time,String et_create_time,Integer state,Integer page,Integer limit) throws OspException {
			
			send_getReturnList(vendor_id,st_create_time,et_create_time,state,page,limit);
			return recv_getReturnList(); 
			
		}
		
		
		private void send_getReturnList(int vendor_id,String st_create_time,String et_create_time,Integer state,Integer page,Integer limit) throws OspException {
			
			initInvocation("getReturnList");
			
			getReturnList_args args = new getReturnList_args();
			args.setVendor_id(vendor_id);
			args.setSt_create_time(st_create_time);
			args.setEt_create_time(et_create_time);
			args.setState(state);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getReturnList_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetReturnListResponse recv_getReturnList() throws OspException {
			
			getReturnList_result result = new getReturnList_result();
			receiveBase(result, getReturnList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetReturnProductResponse getReturnProduct(String back_sn,Integer page,Integer limit) throws OspException {
			
			send_getReturnProduct(back_sn,page,limit);
			return recv_getReturnProduct(); 
			
		}
		
		
		private void send_getReturnProduct(String back_sn,Integer page,Integer limit) throws OspException {
			
			initInvocation("getReturnProduct");
			
			getReturnProduct_args args = new getReturnProduct_args();
			args.setBack_sn(back_sn);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getReturnProduct_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetReturnProductResponse recv_getReturnProduct() throws OspException {
			
			getReturnProduct_result result = new getReturnProduct_result();
			receiveBase(result, getReturnProduct_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public Boolean mergeAfterSaleAddress(String vendor_id,String username,String address,String postcode,String tel) throws OspException {
			
			send_mergeAfterSaleAddress(vendor_id,username,address,postcode,tel);
			return recv_mergeAfterSaleAddress(); 
			
		}
		
		
		private void send_mergeAfterSaleAddress(String vendor_id,String username,String address,String postcode,String tel) throws OspException {
			
			initInvocation("mergeAfterSaleAddress");
			
			mergeAfterSaleAddress_args args = new mergeAfterSaleAddress_args();
			args.setVendor_id(vendor_id);
			args.setUsername(username);
			args.setAddress(address);
			args.setPostcode(postcode);
			args.setTel(tel);
			
			sendBase(args, mergeAfterSaleAddress_argsHelper.getInstance());
		}
		
		
		private Boolean recv_mergeAfterSaleAddress() throws OspException {
			
			mergeAfterSaleAddress_result result = new mergeAfterSaleAddress_result();
			receiveBase(result, mergeAfterSaleAddress_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.RefuseOrReturnProductResponse refuseOrder(List<vipapis.delivery.RefuseOrReturnOrder> refuse_product_list) throws OspException {
			
			send_refuseOrder(refuse_product_list);
			return recv_refuseOrder(); 
			
		}
		
		
		private void send_refuseOrder(List<vipapis.delivery.RefuseOrReturnOrder> refuse_product_list) throws OspException {
			
			initInvocation("refuseOrder");
			
			refuseOrder_args args = new refuseOrder_args();
			args.setRefuse_product_list(refuse_product_list);
			
			sendBase(args, refuseOrder_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.RefuseOrReturnProductResponse recv_refuseOrder() throws OspException {
			
			refuseOrder_result result = new refuseOrder_result();
			receiveBase(result, refuseOrder_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.RefuseOrReturnProductResponse returnOrder(List<vipapis.delivery.RefuseOrReturnOrder> dvd_return_list) throws OspException {
			
			send_returnOrder(dvd_return_list);
			return recv_returnOrder(); 
			
		}
		
		
		private void send_returnOrder(List<vipapis.delivery.RefuseOrReturnOrder> dvd_return_list) throws OspException {
			
			initInvocation("returnOrder");
			
			returnOrder_args args = new returnOrder_args();
			args.setDvd_return_list(dvd_return_list);
			
			sendBase(args, returnOrder_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.RefuseOrReturnProductResponse recv_returnOrder() throws OspException {
			
			returnOrder_result result = new returnOrder_result();
			receiveBase(result, returnOrder_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.ShipResult ship(List<vipapis.delivery.Ship> ship_list) throws OspException {
			
			send_ship(ship_list);
			return recv_ship(); 
			
		}
		
		
		private void send_ship(List<vipapis.delivery.Ship> ship_list) throws OspException {
			
			initInvocation("ship");
			
			ship_args args = new ship_args();
			args.setShip_list(ship_list);
			
			sendBase(args, ship_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.ShipResult recv_ship() throws OspException {
			
			ship_result result = new ship_result();
			receiveBase(result, ship_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}