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

public class JitDeliveryServiceHelper {
	
	
	
	
	public static class confirmDelivery_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* 入库单编号
		*/
		
		private String storage_no;
		
		/**
		* PO订单号
		*/
		
		private String po_no;
		
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		public String getStorage_no(){
			return this.storage_no;
		}
		
		public void setStorage_no(String value){
			this.storage_no = value;
		}
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		
	}
	
	
	
	
	public static class createDelivery_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* po号
		*/
		
		private String po_no;
		
		/**
		* 送货单编号
		*/
		
		private String delivery_no;
		
		/**
		* 送货仓库
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* 送货时间
		*/
		
		private String delivery_time;
		
		/**
		* 预计到货时间
		*/
		
		private String arrival_time;
		
		/**
		* 预计收货时间
		*/
		
		private String race_time;
		
		/**
		* 承运商名称
		*/
		
		private String carrier_name;
		
		/**
		* 联系电话
		*/
		
		private String tel;
		
		/**
		* 司机姓名
		*/
		
		private String driver;
		
		/**
		* 司机联系电话
		*/
		
		private String driver_tel;
		
		/**
		* 车牌号
		*/
		
		private String plate_number;
		
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
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		public String getDelivery_no(){
			return this.delivery_no;
		}
		
		public void setDelivery_no(String value){
			this.delivery_no = value;
		}
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getDelivery_time(){
			return this.delivery_time;
		}
		
		public void setDelivery_time(String value){
			this.delivery_time = value;
		}
		public String getArrival_time(){
			return this.arrival_time;
		}
		
		public void setArrival_time(String value){
			this.arrival_time = value;
		}
		public String getRace_time(){
			return this.race_time;
		}
		
		public void setRace_time(String value){
			this.race_time = value;
		}
		public String getCarrier_name(){
			return this.carrier_name;
		}
		
		public void setCarrier_name(String value){
			this.carrier_name = value;
		}
		public String getTel(){
			return this.tel;
		}
		
		public void setTel(String value){
			this.tel = value;
		}
		public String getDriver(){
			return this.driver;
		}
		
		public void setDriver(String value){
			this.driver = value;
		}
		public String getDriver_tel(){
			return this.driver_tel;
		}
		
		public void setDriver_tel(String value){
			this.driver_tel = value;
		}
		public String getPlate_number(){
			return this.plate_number;
		}
		
		public void setPlate_number(String value){
			this.plate_number = value;
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
	
	
	
	
	public static class createPick_args {
		
		/**
		* PO单编号
		*/
		
		private String po_no;
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		
	}
	
	
	
	
	public static class deleteDeliveryDetail_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* 入库单编号
		*/
		
		private String storage_no;
		
		/**
		* PO订单号
		*/
		
		private String po_no;
		
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		public String getStorage_no(){
			return this.storage_no;
		}
		
		public void setStorage_no(String value){
			this.storage_no = value;
		}
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		
	}
	
	
	
	
	public static class editDelivery_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* 入库单编号
		*/
		
		private String storage_no;
		
		/**
		* 送货单编号
		*/
		
		private String delivery_no;
		
		/**
		* 送货仓库
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* 送货时间
		*/
		
		private String delivery_time;
		
		/**
		* 预计到货时间
		*/
		
		private String arrival_time;
		
		/**
		* 预计收货时间
		*/
		
		private String race_time;
		
		/**
		* 承运商名称
		*/
		
		private String carrier_name;
		
		/**
		* 联系电话
		*/
		
		private String tel;
		
		/**
		* 司机姓名
		*/
		
		private String driver;
		
		/**
		* 司机联系电话
		*/
		
		private String driver_tel;
		
		/**
		* 车牌号
		*/
		
		private String plate_number;
		
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
		public String getStorage_no(){
			return this.storage_no;
		}
		
		public void setStorage_no(String value){
			this.storage_no = value;
		}
		public String getDelivery_no(){
			return this.delivery_no;
		}
		
		public void setDelivery_no(String value){
			this.delivery_no = value;
		}
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getDelivery_time(){
			return this.delivery_time;
		}
		
		public void setDelivery_time(String value){
			this.delivery_time = value;
		}
		public String getArrival_time(){
			return this.arrival_time;
		}
		
		public void setArrival_time(String value){
			this.arrival_time = value;
		}
		public String getRace_time(){
			return this.race_time;
		}
		
		public void setRace_time(String value){
			this.race_time = value;
		}
		public String getCarrier_name(){
			return this.carrier_name;
		}
		
		public void setCarrier_name(String value){
			this.carrier_name = value;
		}
		public String getTel(){
			return this.tel;
		}
		
		public void setTel(String value){
			this.tel = value;
		}
		public String getDriver(){
			return this.driver;
		}
		
		public void setDriver(String value){
			this.driver = value;
		}
		public String getDriver_tel(){
			return this.driver_tel;
		}
		
		public void setDriver_tel(String value){
			this.driver_tel = value;
		}
		public String getPlate_number(){
			return this.plate_number;
		}
		
		public void setPlate_number(String value){
			this.plate_number = value;
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
	
	
	
	
	public static class getPickDetail_args {
		
		/**
		* PO单编号
		*/
		
		private String po_no;
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* 拣货单编号
		*/
		
		private String pick_no;
		
		/**
		* 页码参数
		*/
		
		private Integer page;
		
		/**
		* 每页记录条数，默认50，最大100
		*/
		
		private Integer limit;
		
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		public String getPick_no(){
			return this.pick_no;
		}
		
		public void setPick_no(String value){
			this.pick_no = value;
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
	
	
	
	
	public static class getPickList_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* PO订单号
		*/
		
		private String po_no;
		
		/**
		* 拣货单编号
		*/
		
		private String pick_no;
		
		/**
		* 售卖站点
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* 合作模式
		*/
		
		private String co_mode;
		
		/**
		* 订单类别
		*/
		
		private String order_cate;
		
		/**
		* 开始创建日期
		*/
		
		private String st_create_time;
		
		/**
		* 结束创建日期
		*/
		
		private String et_create_time;
		
		/**
		* 开始开售日期
		*/
		
		private String st_sell_time_from;
		
		/**
		* 结束开售日期
		*/
		
		private String et_sell_time_from;
		
		/**
		* 开始停售日期
		*/
		
		private String st_sell_time_to;
		
		/**
		* 结束停售日期
		*/
		
		private String et_sell_time_to;
		
		/**
		* 导出状态
		*/
		
		private Integer is_export;
		
		/**
		* 页码
		* @sampleValue page page=1
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
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		public String getPick_no(){
			return this.pick_no;
		}
		
		public void setPick_no(String value){
			this.pick_no = value;
		}
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getCo_mode(){
			return this.co_mode;
		}
		
		public void setCo_mode(String value){
			this.co_mode = value;
		}
		public String getOrder_cate(){
			return this.order_cate;
		}
		
		public void setOrder_cate(String value){
			this.order_cate = value;
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
		public String getSt_sell_time_from(){
			return this.st_sell_time_from;
		}
		
		public void setSt_sell_time_from(String value){
			this.st_sell_time_from = value;
		}
		public String getEt_sell_time_from(){
			return this.et_sell_time_from;
		}
		
		public void setEt_sell_time_from(String value){
			this.et_sell_time_from = value;
		}
		public String getSt_sell_time_to(){
			return this.st_sell_time_to;
		}
		
		public void setSt_sell_time_to(String value){
			this.st_sell_time_to = value;
		}
		public String getEt_sell_time_to(){
			return this.et_sell_time_to;
		}
		
		public void setEt_sell_time_to(String value){
			this.et_sell_time_to = value;
		}
		public Integer getIs_export(){
			return this.is_export;
		}
		
		public void setIs_export(Integer value){
			this.is_export = value;
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
	
	
	
	
	public static class getPoList_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* 开始查询的创建时间(格式yyyy-MM-dd)
		* @sampleValue st_create_time st_create_time=2014-06-18
		*/
		
		private String st_create_time;
		
		/**
		* 结束查询的创建时间(格式yyyy-MM-dd)
		* @sampleValue et_create_time et_create_time=2014-06-20
		*/
		
		private String et_create_time;
		
		/**
		* 仓库/销售地区
		* @sampleValue warehouse warehouse=VIP_NH
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* po编号
		*/
		
		private String po_no;
		
		/**
		* 合作模式
		*/
		
		private String co_mode;
		
		/**
		* 开始查询的销售开始时间
		* @sampleValue st_sell_st_time st_sell_st_time=2014-06-20
		*/
		
		private String st_sell_st_time;
		
		/**
		* 结束查询的销售开始时间
		* @sampleValue et_sell_st_time et_sell_st_time=2014-06-20
		*/
		
		private String et_sell_st_time;
		
		/**
		* 开始查询的销售结束时间
		* @sampleValue st_sell_et_time st_sell_et_time=2014-06-20
		*/
		
		private String st_sell_et_time;
		
		/**
		* 结束查询的销售结束时间
		* @sampleValue et_sell_et_time et_sell_et_time=2014-06-20
		*/
		
		private String et_sell_et_time;
		
		/**
		* 页码
		* @sampleValue page page=1
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
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		public String getCo_mode(){
			return this.co_mode;
		}
		
		public void setCo_mode(String value){
			this.co_mode = value;
		}
		public String getSt_sell_st_time(){
			return this.st_sell_st_time;
		}
		
		public void setSt_sell_st_time(String value){
			this.st_sell_st_time = value;
		}
		public String getEt_sell_st_time(){
			return this.et_sell_st_time;
		}
		
		public void setEt_sell_st_time(String value){
			this.et_sell_st_time = value;
		}
		public String getSt_sell_et_time(){
			return this.st_sell_et_time;
		}
		
		public void setSt_sell_et_time(String value){
			this.st_sell_et_time = value;
		}
		public String getEt_sell_et_time(){
			return this.et_sell_et_time;
		}
		
		public void setEt_sell_et_time(String value){
			this.et_sell_et_time = value;
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
	
	
	
	
	public static class getPoSkuList_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* po编号
		*/
		
		private String po_no;
		
		/**
		* 售卖站点
		*/
		
		private String sell_site;
		
		/**
		* 仓库/销售地区
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* 订单状态
		*/
		
		private String order_status;
		
		/**
		* 开始查询的订单支付（审核）时间
		*/
		
		private String st_aduity_time;
		
		/**
		* 结束查询的订单审核（支付）时间
		*/
		
		private String et_aduity_time;
		
		/**
		* 订单号
		*/
		
		private String order_id;
		
		/**
		* 拣货单号
		*/
		
		private String pick_no;
		
		/**
		* 送货单号
		*/
		
		private String delivery_no;
		
		/**
		* 开始查询的拣货时间
		*/
		
		private String st_pick_time;
		
		/**
		* 结束查询的拣货时间
		*/
		
		private String et_pick_time;
		
		/**
		* 开始查询的送货时间
		*/
		
		private String st_delivery_time;
		
		/**
		* 结束查询的送货时间
		*/
		
		private String et_delivery_time;
		
		/**
		* 页码
		* @sampleValue page page=1
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
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		public String getSell_site(){
			return this.sell_site;
		}
		
		public void setSell_site(String value){
			this.sell_site = value;
		}
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getOrder_status(){
			return this.order_status;
		}
		
		public void setOrder_status(String value){
			this.order_status = value;
		}
		public String getSt_aduity_time(){
			return this.st_aduity_time;
		}
		
		public void setSt_aduity_time(String value){
			this.st_aduity_time = value;
		}
		public String getEt_aduity_time(){
			return this.et_aduity_time;
		}
		
		public void setEt_aduity_time(String value){
			this.et_aduity_time = value;
		}
		public String getOrder_id(){
			return this.order_id;
		}
		
		public void setOrder_id(String value){
			this.order_id = value;
		}
		public String getPick_no(){
			return this.pick_no;
		}
		
		public void setPick_no(String value){
			this.pick_no = value;
		}
		public String getDelivery_no(){
			return this.delivery_no;
		}
		
		public void setDelivery_no(String value){
			this.delivery_no = value;
		}
		public String getSt_pick_time(){
			return this.st_pick_time;
		}
		
		public void setSt_pick_time(String value){
			this.st_pick_time = value;
		}
		public String getEt_pick_time(){
			return this.et_pick_time;
		}
		
		public void setEt_pick_time(String value){
			this.et_pick_time = value;
		}
		public String getSt_delivery_time(){
			return this.st_delivery_time;
		}
		
		public void setSt_delivery_time(String value){
			this.st_delivery_time = value;
		}
		public String getEt_delivery_time(){
			return this.et_delivery_time;
		}
		
		public void setEt_delivery_time(String value){
			this.et_delivery_time = value;
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
	
	
	
	
	public static class getPrintBox_args {
		
		/**
		* 拣货单编码
		*/
		
		private String pick_no;
		
		/**
		* 供应商ID
		*/
		
		private String vendor_id;
		
		public String getPick_no(){
			return this.pick_no;
		}
		
		public void setPick_no(String value){
			this.pick_no = value;
		}
		public String getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(String value){
			this.vendor_id = value;
		}
		
	}
	
	
	
	
	public static class getPrintDelivery_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* 入库单编号
		*/
		
		private String storage_no;
		
		/**
		* PO订单号
		*/
		
		private String po_no;
		
		/**
		* 供应商箱号
		*/
		
		private String box_no;
		
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		public String getStorage_no(){
			return this.storage_no;
		}
		
		public void setStorage_no(String value){
			this.storage_no = value;
		}
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		public String getBox_no(){
			return this.box_no;
		}
		
		public void setBox_no(String value){
			this.box_no = value;
		}
		
	}
	
	
	
	
	public static class importDeliveryDetail_args {
		
		/**
		* 供应商ID
		*/
		
		private int vendor_id;
		
		/**
		* PO单编号
		*/
		
		private String po_no;
		
		/**
		* 入库单号
		*/
		
		private String storage_no;
		
		/**
		* 创建送货单返回的ID
		*/
		
		private String delivery_no;
		
		/**
		* 出仓产品列表
		*/
		
		private List<vipapis.delivery.Delivery> delivery_list;
		
		public int getVendor_id(){
			return this.vendor_id;
		}
		
		public void setVendor_id(int value){
			this.vendor_id = value;
		}
		public String getPo_no(){
			return this.po_no;
		}
		
		public void setPo_no(String value){
			this.po_no = value;
		}
		public String getStorage_no(){
			return this.storage_no;
		}
		
		public void setStorage_no(String value){
			this.storage_no = value;
		}
		public String getDelivery_no(){
			return this.delivery_no;
		}
		
		public void setDelivery_no(String value){
			this.delivery_no = value;
		}
		public List<vipapis.delivery.Delivery> getDelivery_list(){
			return this.delivery_list;
		}
		
		public void setDelivery_list(List<vipapis.delivery.Delivery> value){
			this.delivery_list = value;
		}
		
	}
	
	
	
	
	public static class confirmDelivery_result {
		
		/**
		* 送货单流水id
		*/
		
		private String success;
		
		public String getSuccess(){
			return this.success;
		}
		
		public void setSuccess(String value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class createDelivery_result {
		
		/**
		*/
		
		private vipapis.delivery.CreateDeliveryResponse success;
		
		public vipapis.delivery.CreateDeliveryResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.CreateDeliveryResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class createPick_result {
		
		/**
		*/
		
		private List<vipapis.delivery.SimplePick> success;
		
		public List<vipapis.delivery.SimplePick> getSuccess(){
			return this.success;
		}
		
		public void setSuccess(List<vipapis.delivery.SimplePick> value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class deleteDeliveryDetail_result {
		
		/**
		*/
		
		private List<vipapis.delivery.DeleteDeliveryDetail> success;
		
		public List<vipapis.delivery.DeleteDeliveryDetail> getSuccess(){
			return this.success;
		}
		
		public void setSuccess(List<vipapis.delivery.DeleteDeliveryDetail> value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class editDelivery_result {
		
		/**
		* 出库单号
		*/
		
		private String success;
		
		public String getSuccess(){
			return this.success;
		}
		
		public void setSuccess(String value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getPickDetail_result {
		
		/**
		*/
		
		private vipapis.delivery.PickDetail success;
		
		public vipapis.delivery.PickDetail getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.PickDetail value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getPickList_result {
		
		/**
		*/
		
		private vipapis.delivery.GetPickListResponse success;
		
		public vipapis.delivery.GetPickListResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetPickListResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getPoList_result {
		
		/**
		*/
		
		private vipapis.delivery.GetPoListResponse success;
		
		public vipapis.delivery.GetPoListResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetPoListResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getPoSkuList_result {
		
		/**
		*/
		
		private vipapis.delivery.GetPoSkuListResponse success;
		
		public vipapis.delivery.GetPoSkuListResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetPoSkuListResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getPrintBox_result {
		
		/**
		*/
		
		private vipapis.delivery.GetPrintBoxResponse success;
		
		public vipapis.delivery.GetPrintBoxResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetPrintBoxResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getPrintDelivery_result {
		
		/**
		*/
		
		private vipapis.delivery.GetPrintDeliveryResponse success;
		
		public vipapis.delivery.GetPrintDeliveryResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.delivery.GetPrintDeliveryResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class importDeliveryDetail_result {
		
		/**
		* 入库单号
		*/
		
		private String success;
		
		public String getSuccess(){
			return this.success;
		}
		
		public void setSuccess(String value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class confirmDelivery_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<confirmDelivery_args>
	{
		
		public static final confirmDelivery_argsHelper OBJ = new confirmDelivery_argsHelper();
		
		public static confirmDelivery_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(confirmDelivery_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStorage_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(confirmDelivery_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("storage_no");
			oprot.writeString(struct.getStorage_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(confirmDelivery_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class createDelivery_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<createDelivery_args>
	{
		
		public static final createDelivery_argsHelper OBJ = new createDelivery_argsHelper();
		
		public static createDelivery_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(createDelivery_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDelivery_no(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.common.Warehouse value;
				
				value = null;
				String name = iprot.readString();
				vipapis.common.Warehouse[] values = vipapis.common.Warehouse.values(); 
				for(vipapis.common.Warehouse v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setWarehouse(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDelivery_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setArrival_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setRace_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCarrier_name(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setTel(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDriver(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDriver_tel(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPlate_number(value);
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
		
		
		public void write(createDelivery_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("delivery_no");
			oprot.writeString(struct.getDelivery_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("warehouse");
			oprot.writeString(struct.getWarehouse().name());  
			
			oprot.writeFieldEnd();
			
			if(struct.getDelivery_time() != null) {
				
				oprot.writeFieldBegin("delivery_time");
				oprot.writeString(struct.getDelivery_time());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldBegin("arrival_time");
			oprot.writeString(struct.getArrival_time());
			
			oprot.writeFieldEnd();
			
			if(struct.getRace_time() != null) {
				
				oprot.writeFieldBegin("race_time");
				oprot.writeString(struct.getRace_time());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldBegin("carrier_name");
			oprot.writeString(struct.getCarrier_name());
			
			oprot.writeFieldEnd();
			
			if(struct.getTel() != null) {
				
				oprot.writeFieldBegin("tel");
				oprot.writeString(struct.getTel());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getDriver() != null) {
				
				oprot.writeFieldBegin("driver");
				oprot.writeString(struct.getDriver());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldBegin("driver_tel");
			oprot.writeString(struct.getDriver_tel());
			
			oprot.writeFieldEnd();
			
			if(struct.getPlate_number() != null) {
				
				oprot.writeFieldBegin("plate_number");
				oprot.writeString(struct.getPlate_number());
				
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
		
		
		public void validate(createDelivery_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class createPick_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<createPick_args>
	{
		
		public static final createPick_argsHelper OBJ = new createPick_argsHelper();
		
		public static createPick_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(createPick_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(createPick_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(createPick_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class deleteDeliveryDetail_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<deleteDeliveryDetail_args>
	{
		
		public static final deleteDeliveryDetail_argsHelper OBJ = new deleteDeliveryDetail_argsHelper();
		
		public static deleteDeliveryDetail_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(deleteDeliveryDetail_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStorage_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(deleteDeliveryDetail_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("storage_no");
			oprot.writeString(struct.getStorage_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(deleteDeliveryDetail_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class editDelivery_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<editDelivery_args>
	{
		
		public static final editDelivery_argsHelper OBJ = new editDelivery_argsHelper();
		
		public static editDelivery_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(editDelivery_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStorage_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDelivery_no(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.common.Warehouse value;
				
				value = null;
				String name = iprot.readString();
				vipapis.common.Warehouse[] values = vipapis.common.Warehouse.values(); 
				for(vipapis.common.Warehouse v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setWarehouse(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDelivery_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setArrival_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setRace_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCarrier_name(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setTel(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDriver(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDriver_tel(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPlate_number(value);
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
		
		
		public void write(editDelivery_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("storage_no");
			oprot.writeString(struct.getStorage_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("delivery_no");
			oprot.writeString(struct.getDelivery_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("warehouse");
			oprot.writeString(struct.getWarehouse().name());  
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("delivery_time");
			oprot.writeString(struct.getDelivery_time());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("arrival_time");
			oprot.writeString(struct.getArrival_time());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("race_time");
			oprot.writeString(struct.getRace_time());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("carrier_name");
			oprot.writeString(struct.getCarrier_name());
			
			oprot.writeFieldEnd();
			
			if(struct.getTel() != null) {
				
				oprot.writeFieldBegin("tel");
				oprot.writeString(struct.getTel());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getDriver() != null) {
				
				oprot.writeFieldBegin("driver");
				oprot.writeString(struct.getDriver());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldBegin("driver_tel");
			oprot.writeString(struct.getDriver_tel());
			
			oprot.writeFieldEnd();
			
			if(struct.getPlate_number() != null) {
				
				oprot.writeFieldBegin("plate_number");
				oprot.writeString(struct.getPlate_number());
				
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
		
		
		public void validate(editDelivery_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPickDetail_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getPickDetail_args>
	{
		
		public static final getPickDetail_argsHelper OBJ = new getPickDetail_argsHelper();
		
		public static getPickDetail_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPickDetail_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPick_no(value);
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
		
		
		public void write(getPickDetail_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("pick_no");
			oprot.writeString(struct.getPick_no());
			
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
		
		
		public void validate(getPickDetail_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPickList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getPickList_args>
	{
		
		public static final getPickList_argsHelper OBJ = new getPickList_argsHelper();
		
		public static getPickList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPickList_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPick_no(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.common.Warehouse value;
				
				value = null;
				String name = iprot.readString();
				vipapis.common.Warehouse[] values = vipapis.common.Warehouse.values(); 
				for(vipapis.common.Warehouse v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setWarehouse(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCo_mode(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setOrder_cate(value);
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
				
				String value;
				value = iprot.readString();
				
				struct.setSt_sell_time_from(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_sell_time_from(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSt_sell_time_to(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_sell_time_to(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setIs_export(value);
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
		
		
		public void write(getPickList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			if(struct.getPo_no() != null) {
				
				oprot.writeFieldBegin("po_no");
				oprot.writeString(struct.getPo_no());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getPick_no() != null) {
				
				oprot.writeFieldBegin("pick_no");
				oprot.writeString(struct.getPick_no());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getWarehouse() != null) {
				
				oprot.writeFieldBegin("warehouse");
				oprot.writeString(struct.getWarehouse().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getCo_mode() != null) {
				
				oprot.writeFieldBegin("co_mode");
				oprot.writeString(struct.getCo_mode());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getOrder_cate() != null) {
				
				oprot.writeFieldBegin("order_cate");
				oprot.writeString(struct.getOrder_cate());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSt_create_time() != null) {
				
				oprot.writeFieldBegin("st_create_time");
				oprot.writeString(struct.getSt_create_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_create_time() != null) {
				
				oprot.writeFieldBegin("et_create_time");
				oprot.writeString(struct.getEt_create_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSt_sell_time_from() != null) {
				
				oprot.writeFieldBegin("st_sell_time_from");
				oprot.writeString(struct.getSt_sell_time_from());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_sell_time_from() != null) {
				
				oprot.writeFieldBegin("et_sell_time_from");
				oprot.writeString(struct.getEt_sell_time_from());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSt_sell_time_to() != null) {
				
				oprot.writeFieldBegin("st_sell_time_to");
				oprot.writeString(struct.getSt_sell_time_to());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_sell_time_to() != null) {
				
				oprot.writeFieldBegin("et_sell_time_to");
				oprot.writeString(struct.getEt_sell_time_to());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getIs_export() != null) {
				
				oprot.writeFieldBegin("is_export");
				oprot.writeI32(struct.getIs_export()); 
				
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
		
		
		public void validate(getPickList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPoList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getPoList_args>
	{
		
		public static final getPoList_argsHelper OBJ = new getPoList_argsHelper();
		
		public static getPoList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPoList_args struct, Protocol iprot) throws OspException {
			
			
			
			
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
				
				vipapis.common.Warehouse value;
				
				value = null;
				String name = iprot.readString();
				vipapis.common.Warehouse[] values = vipapis.common.Warehouse.values(); 
				for(vipapis.common.Warehouse v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setWarehouse(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCo_mode(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSt_sell_st_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_sell_st_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSt_sell_et_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_sell_et_time(value);
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
		
		
		public void write(getPoList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			if(struct.getSt_create_time() != null) {
				
				oprot.writeFieldBegin("st_create_time");
				oprot.writeString(struct.getSt_create_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_create_time() != null) {
				
				oprot.writeFieldBegin("et_create_time");
				oprot.writeString(struct.getEt_create_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getWarehouse() != null) {
				
				oprot.writeFieldBegin("warehouse");
				oprot.writeString(struct.getWarehouse().name());  
				
				oprot.writeFieldEnd();
			}
			
			
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
			
			
			if(struct.getSt_sell_st_time() != null) {
				
				oprot.writeFieldBegin("st_sell_st_time");
				oprot.writeString(struct.getSt_sell_st_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_sell_st_time() != null) {
				
				oprot.writeFieldBegin("et_sell_st_time");
				oprot.writeString(struct.getEt_sell_st_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSt_sell_et_time() != null) {
				
				oprot.writeFieldBegin("st_sell_et_time");
				oprot.writeString(struct.getSt_sell_et_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_sell_et_time() != null) {
				
				oprot.writeFieldBegin("et_sell_et_time");
				oprot.writeString(struct.getEt_sell_et_time());
				
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
		
		
		public void validate(getPoList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPoSkuList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getPoSkuList_args>
	{
		
		public static final getPoSkuList_argsHelper OBJ = new getPoSkuList_argsHelper();
		
		public static getPoSkuList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPoSkuList_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSell_site(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.common.Warehouse value;
				
				value = null;
				String name = iprot.readString();
				vipapis.common.Warehouse[] values = vipapis.common.Warehouse.values(); 
				for(vipapis.common.Warehouse v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setWarehouse(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setOrder_status(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSt_aduity_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_aduity_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setOrder_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPick_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDelivery_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSt_pick_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_pick_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSt_delivery_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEt_delivery_time(value);
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
		
		
		public void write(getPoSkuList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
			
			if(struct.getSell_site() != null) {
				
				oprot.writeFieldBegin("sell_site");
				oprot.writeString(struct.getSell_site());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getWarehouse() != null) {
				
				oprot.writeFieldBegin("warehouse");
				oprot.writeString(struct.getWarehouse().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getOrder_status() != null) {
				
				oprot.writeFieldBegin("order_status");
				oprot.writeString(struct.getOrder_status());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSt_aduity_time() != null) {
				
				oprot.writeFieldBegin("st_aduity_time");
				oprot.writeString(struct.getSt_aduity_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_aduity_time() != null) {
				
				oprot.writeFieldBegin("et_aduity_time");
				oprot.writeString(struct.getEt_aduity_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getOrder_id() != null) {
				
				oprot.writeFieldBegin("order_id");
				oprot.writeString(struct.getOrder_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getPick_no() != null) {
				
				oprot.writeFieldBegin("pick_no");
				oprot.writeString(struct.getPick_no());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getDelivery_no() != null) {
				
				oprot.writeFieldBegin("delivery_no");
				oprot.writeString(struct.getDelivery_no());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSt_pick_time() != null) {
				
				oprot.writeFieldBegin("st_pick_time");
				oprot.writeString(struct.getSt_pick_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_pick_time() != null) {
				
				oprot.writeFieldBegin("et_pick_time");
				oprot.writeString(struct.getEt_pick_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSt_delivery_time() != null) {
				
				oprot.writeFieldBegin("st_delivery_time");
				oprot.writeString(struct.getSt_delivery_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEt_delivery_time() != null) {
				
				oprot.writeFieldBegin("et_delivery_time");
				oprot.writeString(struct.getEt_delivery_time());
				
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
		
		
		public void validate(getPoSkuList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPrintBox_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getPrintBox_args>
	{
		
		public static final getPrintBox_argsHelper OBJ = new getPrintBox_argsHelper();
		
		public static getPrintBox_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPrintBox_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPick_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setVendor_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPrintBox_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("pick_no");
			oprot.writeString(struct.getPick_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeString(struct.getVendor_id());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPrintBox_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPrintDelivery_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getPrintDelivery_args>
	{
		
		public static final getPrintDelivery_argsHelper OBJ = new getPrintDelivery_argsHelper();
		
		public static getPrintDelivery_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPrintDelivery_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStorage_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setBox_no(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPrintDelivery_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("storage_no");
			oprot.writeString(struct.getStorage_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
			
			if(struct.getBox_no() != null) {
				
				oprot.writeFieldBegin("box_no");
				oprot.writeString(struct.getBox_no());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPrintDelivery_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class importDeliveryDetail_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<importDeliveryDetail_args>
	{
		
		public static final importDeliveryDetail_argsHelper OBJ = new importDeliveryDetail_argsHelper();
		
		public static importDeliveryDetail_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(importDeliveryDetail_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setVendor_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setPo_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStorage_no(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setDelivery_no(value);
			}
			
			
			
			
			
			if(true){
				
				List<vipapis.delivery.Delivery> value;
				
				value = new ArrayList<vipapis.delivery.Delivery>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.delivery.Delivery elem0;
						
						elem0 = new vipapis.delivery.Delivery();
						vipapis.delivery.DeliveryHelper.getInstance().read(elem0, iprot);
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setDelivery_list(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(importDeliveryDetail_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("vendor_id");
			oprot.writeI32(struct.getVendor_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("po_no");
			oprot.writeString(struct.getPo_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("storage_no");
			oprot.writeString(struct.getStorage_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("delivery_no");
			oprot.writeString(struct.getDelivery_no());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("delivery_list");
			
			oprot.writeListBegin();
			for(vipapis.delivery.Delivery _item0 : struct.getDelivery_list()){
				
				
				vipapis.delivery.DeliveryHelper.getInstance().write(_item0, oprot);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(importDeliveryDetail_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class confirmDelivery_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<confirmDelivery_result>
	{
		
		public static final confirmDelivery_resultHelper OBJ = new confirmDelivery_resultHelper();
		
		public static confirmDelivery_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(confirmDelivery_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(confirmDelivery_result struct, Protocol oprot) throws OspException {
			
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
		
		
		public void validate(confirmDelivery_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class createDelivery_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<createDelivery_result>
	{
		
		public static final createDelivery_resultHelper OBJ = new createDelivery_resultHelper();
		
		public static createDelivery_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(createDelivery_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.CreateDeliveryResponse value;
				
				value = new vipapis.delivery.CreateDeliveryResponse();
				vipapis.delivery.CreateDeliveryResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(createDelivery_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.CreateDeliveryResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(createDelivery_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class createPick_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<createPick_result>
	{
		
		public static final createPick_resultHelper OBJ = new createPick_resultHelper();
		
		public static createPick_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(createPick_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.delivery.SimplePick> value;
				
				value = new ArrayList<vipapis.delivery.SimplePick>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.delivery.SimplePick elem0;
						
						elem0 = new vipapis.delivery.SimplePick();
						vipapis.delivery.SimplePickHelper.getInstance().read(elem0, iprot);
						
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
		
		
		public void write(createPick_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				oprot.writeListBegin();
				for(vipapis.delivery.SimplePick _item0 : struct.getSuccess()){
					
					
					vipapis.delivery.SimplePickHelper.getInstance().write(_item0, oprot);
					
				}
				
				oprot.writeListEnd();
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(createPick_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class deleteDeliveryDetail_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<deleteDeliveryDetail_result>
	{
		
		public static final deleteDeliveryDetail_resultHelper OBJ = new deleteDeliveryDetail_resultHelper();
		
		public static deleteDeliveryDetail_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(deleteDeliveryDetail_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.delivery.DeleteDeliveryDetail> value;
				
				value = new ArrayList<vipapis.delivery.DeleteDeliveryDetail>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.delivery.DeleteDeliveryDetail elem1;
						
						elem1 = new vipapis.delivery.DeleteDeliveryDetail();
						vipapis.delivery.DeleteDeliveryDetailHelper.getInstance().read(elem1, iprot);
						
						value.add(elem1);
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
		
		
		public void write(deleteDeliveryDetail_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				oprot.writeListBegin();
				for(vipapis.delivery.DeleteDeliveryDetail _item0 : struct.getSuccess()){
					
					
					vipapis.delivery.DeleteDeliveryDetailHelper.getInstance().write(_item0, oprot);
					
				}
				
				oprot.writeListEnd();
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(deleteDeliveryDetail_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class editDelivery_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<editDelivery_result>
	{
		
		public static final editDelivery_resultHelper OBJ = new editDelivery_resultHelper();
		
		public static editDelivery_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(editDelivery_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(editDelivery_result struct, Protocol oprot) throws OspException {
			
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
		
		
		public void validate(editDelivery_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPickDetail_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getPickDetail_result>
	{
		
		public static final getPickDetail_resultHelper OBJ = new getPickDetail_resultHelper();
		
		public static getPickDetail_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPickDetail_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.PickDetail value;
				
				value = new vipapis.delivery.PickDetail();
				vipapis.delivery.PickDetailHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPickDetail_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.PickDetailHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPickDetail_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPickList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getPickList_result>
	{
		
		public static final getPickList_resultHelper OBJ = new getPickList_resultHelper();
		
		public static getPickList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPickList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetPickListResponse value;
				
				value = new vipapis.delivery.GetPickListResponse();
				vipapis.delivery.GetPickListResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPickList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetPickListResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPickList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPoList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getPoList_result>
	{
		
		public static final getPoList_resultHelper OBJ = new getPoList_resultHelper();
		
		public static getPoList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPoList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetPoListResponse value;
				
				value = new vipapis.delivery.GetPoListResponse();
				vipapis.delivery.GetPoListResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPoList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetPoListResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPoList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPoSkuList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getPoSkuList_result>
	{
		
		public static final getPoSkuList_resultHelper OBJ = new getPoSkuList_resultHelper();
		
		public static getPoSkuList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPoSkuList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetPoSkuListResponse value;
				
				value = new vipapis.delivery.GetPoSkuListResponse();
				vipapis.delivery.GetPoSkuListResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPoSkuList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetPoSkuListResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPoSkuList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPrintBox_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getPrintBox_result>
	{
		
		public static final getPrintBox_resultHelper OBJ = new getPrintBox_resultHelper();
		
		public static getPrintBox_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPrintBox_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetPrintBoxResponse value;
				
				value = new vipapis.delivery.GetPrintBoxResponse();
				vipapis.delivery.GetPrintBoxResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPrintBox_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetPrintBoxResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPrintBox_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getPrintDelivery_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getPrintDelivery_result>
	{
		
		public static final getPrintDelivery_resultHelper OBJ = new getPrintDelivery_resultHelper();
		
		public static getPrintDelivery_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getPrintDelivery_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.delivery.GetPrintDeliveryResponse value;
				
				value = new vipapis.delivery.GetPrintDeliveryResponse();
				vipapis.delivery.GetPrintDeliveryResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getPrintDelivery_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.delivery.GetPrintDeliveryResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getPrintDelivery_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class importDeliveryDetail_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<importDeliveryDetail_result>
	{
		
		public static final importDeliveryDetail_resultHelper OBJ = new importDeliveryDetail_resultHelper();
		
		public static importDeliveryDetail_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(importDeliveryDetail_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(importDeliveryDetail_result struct, Protocol oprot) throws OspException {
			
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
		
		
		public void validate(importDeliveryDetail_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class JitDeliveryServiceClient extends OspRestStub implements JitDeliveryService  {
		
		
		public JitDeliveryServiceClient() {
			
			super("1.0.0", "vipapis.delivery.JitDeliveryService");
		}
		
		
		
		public String confirmDelivery(int vendor_id,String storage_no,String po_no) throws OspException {
			
			send_confirmDelivery(vendor_id,storage_no,po_no);
			return recv_confirmDelivery(); 
			
		}
		
		
		private void send_confirmDelivery(int vendor_id,String storage_no,String po_no) throws OspException {
			
			initInvocation("confirmDelivery");
			
			confirmDelivery_args args = new confirmDelivery_args();
			args.setVendor_id(vendor_id);
			args.setStorage_no(storage_no);
			args.setPo_no(po_no);
			
			sendBase(args, confirmDelivery_argsHelper.getInstance());
		}
		
		
		private String recv_confirmDelivery() throws OspException {
			
			confirmDelivery_result result = new confirmDelivery_result();
			receiveBase(result, confirmDelivery_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.CreateDeliveryResponse createDelivery(int vendor_id,String po_no,String delivery_no,vipapis.common.Warehouse warehouse,String delivery_time,String arrival_time,String race_time,String carrier_name,String tel,String driver,String driver_tel,String plate_number,Integer page,Integer limit) throws OspException {
			
			send_createDelivery(vendor_id,po_no,delivery_no,warehouse,delivery_time,arrival_time,race_time,carrier_name,tel,driver,driver_tel,plate_number,page,limit);
			return recv_createDelivery(); 
			
		}
		
		
		private void send_createDelivery(int vendor_id,String po_no,String delivery_no,vipapis.common.Warehouse warehouse,String delivery_time,String arrival_time,String race_time,String carrier_name,String tel,String driver,String driver_tel,String plate_number,Integer page,Integer limit) throws OspException {
			
			initInvocation("createDelivery");
			
			createDelivery_args args = new createDelivery_args();
			args.setVendor_id(vendor_id);
			args.setPo_no(po_no);
			args.setDelivery_no(delivery_no);
			args.setWarehouse(warehouse);
			args.setDelivery_time(delivery_time);
			args.setArrival_time(arrival_time);
			args.setRace_time(race_time);
			args.setCarrier_name(carrier_name);
			args.setTel(tel);
			args.setDriver(driver);
			args.setDriver_tel(driver_tel);
			args.setPlate_number(plate_number);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, createDelivery_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.CreateDeliveryResponse recv_createDelivery() throws OspException {
			
			createDelivery_result result = new createDelivery_result();
			receiveBase(result, createDelivery_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public List<vipapis.delivery.SimplePick> createPick(String po_no,int vendor_id) throws OspException {
			
			send_createPick(po_no,vendor_id);
			return recv_createPick(); 
			
		}
		
		
		private void send_createPick(String po_no,int vendor_id) throws OspException {
			
			initInvocation("createPick");
			
			createPick_args args = new createPick_args();
			args.setPo_no(po_no);
			args.setVendor_id(vendor_id);
			
			sendBase(args, createPick_argsHelper.getInstance());
		}
		
		
		private List<vipapis.delivery.SimplePick> recv_createPick() throws OspException {
			
			createPick_result result = new createPick_result();
			receiveBase(result, createPick_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public List<vipapis.delivery.DeleteDeliveryDetail> deleteDeliveryDetail(int vendor_id,String storage_no,String po_no) throws OspException {
			
			send_deleteDeliveryDetail(vendor_id,storage_no,po_no);
			return recv_deleteDeliveryDetail(); 
			
		}
		
		
		private void send_deleteDeliveryDetail(int vendor_id,String storage_no,String po_no) throws OspException {
			
			initInvocation("deleteDeliveryDetail");
			
			deleteDeliveryDetail_args args = new deleteDeliveryDetail_args();
			args.setVendor_id(vendor_id);
			args.setStorage_no(storage_no);
			args.setPo_no(po_no);
			
			sendBase(args, deleteDeliveryDetail_argsHelper.getInstance());
		}
		
		
		private List<vipapis.delivery.DeleteDeliveryDetail> recv_deleteDeliveryDetail() throws OspException {
			
			deleteDeliveryDetail_result result = new deleteDeliveryDetail_result();
			receiveBase(result, deleteDeliveryDetail_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public String editDelivery(int vendor_id,String storage_no,String delivery_no,vipapis.common.Warehouse warehouse,String delivery_time,String arrival_time,String race_time,String carrier_name,String tel,String driver,String driver_tel,String plate_number,Integer page,Integer limit) throws OspException {
			
			send_editDelivery(vendor_id,storage_no,delivery_no,warehouse,delivery_time,arrival_time,race_time,carrier_name,tel,driver,driver_tel,plate_number,page,limit);
			return recv_editDelivery(); 
			
		}
		
		
		private void send_editDelivery(int vendor_id,String storage_no,String delivery_no,vipapis.common.Warehouse warehouse,String delivery_time,String arrival_time,String race_time,String carrier_name,String tel,String driver,String driver_tel,String plate_number,Integer page,Integer limit) throws OspException {
			
			initInvocation("editDelivery");
			
			editDelivery_args args = new editDelivery_args();
			args.setVendor_id(vendor_id);
			args.setStorage_no(storage_no);
			args.setDelivery_no(delivery_no);
			args.setWarehouse(warehouse);
			args.setDelivery_time(delivery_time);
			args.setArrival_time(arrival_time);
			args.setRace_time(race_time);
			args.setCarrier_name(carrier_name);
			args.setTel(tel);
			args.setDriver(driver);
			args.setDriver_tel(driver_tel);
			args.setPlate_number(plate_number);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, editDelivery_argsHelper.getInstance());
		}
		
		
		private String recv_editDelivery() throws OspException {
			
			editDelivery_result result = new editDelivery_result();
			receiveBase(result, editDelivery_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.PickDetail getPickDetail(String po_no,int vendor_id,String pick_no,Integer page,Integer limit) throws OspException {
			
			send_getPickDetail(po_no,vendor_id,pick_no,page,limit);
			return recv_getPickDetail(); 
			
		}
		
		
		private void send_getPickDetail(String po_no,int vendor_id,String pick_no,Integer page,Integer limit) throws OspException {
			
			initInvocation("getPickDetail");
			
			getPickDetail_args args = new getPickDetail_args();
			args.setPo_no(po_no);
			args.setVendor_id(vendor_id);
			args.setPick_no(pick_no);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getPickDetail_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.PickDetail recv_getPickDetail() throws OspException {
			
			getPickDetail_result result = new getPickDetail_result();
			receiveBase(result, getPickDetail_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetPickListResponse getPickList(int vendor_id,String po_no,String pick_no,vipapis.common.Warehouse warehouse,String co_mode,String order_cate,String st_create_time,String et_create_time,String st_sell_time_from,String et_sell_time_from,String st_sell_time_to,String et_sell_time_to,Integer is_export,Integer page,Integer limit) throws OspException {
			
			send_getPickList(vendor_id,po_no,pick_no,warehouse,co_mode,order_cate,st_create_time,et_create_time,st_sell_time_from,et_sell_time_from,st_sell_time_to,et_sell_time_to,is_export,page,limit);
			return recv_getPickList(); 
			
		}
		
		
		private void send_getPickList(int vendor_id,String po_no,String pick_no,vipapis.common.Warehouse warehouse,String co_mode,String order_cate,String st_create_time,String et_create_time,String st_sell_time_from,String et_sell_time_from,String st_sell_time_to,String et_sell_time_to,Integer is_export,Integer page,Integer limit) throws OspException {
			
			initInvocation("getPickList");
			
			getPickList_args args = new getPickList_args();
			args.setVendor_id(vendor_id);
			args.setPo_no(po_no);
			args.setPick_no(pick_no);
			args.setWarehouse(warehouse);
			args.setCo_mode(co_mode);
			args.setOrder_cate(order_cate);
			args.setSt_create_time(st_create_time);
			args.setEt_create_time(et_create_time);
			args.setSt_sell_time_from(st_sell_time_from);
			args.setEt_sell_time_from(et_sell_time_from);
			args.setSt_sell_time_to(st_sell_time_to);
			args.setEt_sell_time_to(et_sell_time_to);
			args.setIs_export(is_export);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getPickList_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetPickListResponse recv_getPickList() throws OspException {
			
			getPickList_result result = new getPickList_result();
			receiveBase(result, getPickList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetPoListResponse getPoList(int vendor_id,String st_create_time,String et_create_time,vipapis.common.Warehouse warehouse,String po_no,String co_mode,String st_sell_st_time,String et_sell_st_time,String st_sell_et_time,String et_sell_et_time,Integer page,Integer limit) throws OspException {
			
			send_getPoList(vendor_id,st_create_time,et_create_time,warehouse,po_no,co_mode,st_sell_st_time,et_sell_st_time,st_sell_et_time,et_sell_et_time,page,limit);
			return recv_getPoList(); 
			
		}
		
		
		private void send_getPoList(int vendor_id,String st_create_time,String et_create_time,vipapis.common.Warehouse warehouse,String po_no,String co_mode,String st_sell_st_time,String et_sell_st_time,String st_sell_et_time,String et_sell_et_time,Integer page,Integer limit) throws OspException {
			
			initInvocation("getPoList");
			
			getPoList_args args = new getPoList_args();
			args.setVendor_id(vendor_id);
			args.setSt_create_time(st_create_time);
			args.setEt_create_time(et_create_time);
			args.setWarehouse(warehouse);
			args.setPo_no(po_no);
			args.setCo_mode(co_mode);
			args.setSt_sell_st_time(st_sell_st_time);
			args.setEt_sell_st_time(et_sell_st_time);
			args.setSt_sell_et_time(st_sell_et_time);
			args.setEt_sell_et_time(et_sell_et_time);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getPoList_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetPoListResponse recv_getPoList() throws OspException {
			
			getPoList_result result = new getPoList_result();
			receiveBase(result, getPoList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetPoSkuListResponse getPoSkuList(int vendor_id,String po_no,String sell_site,vipapis.common.Warehouse warehouse,String order_status,String st_aduity_time,String et_aduity_time,String order_id,String pick_no,String delivery_no,String st_pick_time,String et_pick_time,String st_delivery_time,String et_delivery_time,Integer page,Integer limit) throws OspException {
			
			send_getPoSkuList(vendor_id,po_no,sell_site,warehouse,order_status,st_aduity_time,et_aduity_time,order_id,pick_no,delivery_no,st_pick_time,et_pick_time,st_delivery_time,et_delivery_time,page,limit);
			return recv_getPoSkuList(); 
			
		}
		
		
		private void send_getPoSkuList(int vendor_id,String po_no,String sell_site,vipapis.common.Warehouse warehouse,String order_status,String st_aduity_time,String et_aduity_time,String order_id,String pick_no,String delivery_no,String st_pick_time,String et_pick_time,String st_delivery_time,String et_delivery_time,Integer page,Integer limit) throws OspException {
			
			initInvocation("getPoSkuList");
			
			getPoSkuList_args args = new getPoSkuList_args();
			args.setVendor_id(vendor_id);
			args.setPo_no(po_no);
			args.setSell_site(sell_site);
			args.setWarehouse(warehouse);
			args.setOrder_status(order_status);
			args.setSt_aduity_time(st_aduity_time);
			args.setEt_aduity_time(et_aduity_time);
			args.setOrder_id(order_id);
			args.setPick_no(pick_no);
			args.setDelivery_no(delivery_no);
			args.setSt_pick_time(st_pick_time);
			args.setEt_pick_time(et_pick_time);
			args.setSt_delivery_time(st_delivery_time);
			args.setEt_delivery_time(et_delivery_time);
			args.setPage(page);
			args.setLimit(limit);
			
			sendBase(args, getPoSkuList_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetPoSkuListResponse recv_getPoSkuList() throws OspException {
			
			getPoSkuList_result result = new getPoSkuList_result();
			receiveBase(result, getPoSkuList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetPrintBoxResponse getPrintBox(String pick_no,String vendor_id) throws OspException {
			
			send_getPrintBox(pick_no,vendor_id);
			return recv_getPrintBox(); 
			
		}
		
		
		private void send_getPrintBox(String pick_no,String vendor_id) throws OspException {
			
			initInvocation("getPrintBox");
			
			getPrintBox_args args = new getPrintBox_args();
			args.setPick_no(pick_no);
			args.setVendor_id(vendor_id);
			
			sendBase(args, getPrintBox_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetPrintBoxResponse recv_getPrintBox() throws OspException {
			
			getPrintBox_result result = new getPrintBox_result();
			receiveBase(result, getPrintBox_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.delivery.GetPrintDeliveryResponse getPrintDelivery(int vendor_id,String storage_no,String po_no,String box_no) throws OspException {
			
			send_getPrintDelivery(vendor_id,storage_no,po_no,box_no);
			return recv_getPrintDelivery(); 
			
		}
		
		
		private void send_getPrintDelivery(int vendor_id,String storage_no,String po_no,String box_no) throws OspException {
			
			initInvocation("getPrintDelivery");
			
			getPrintDelivery_args args = new getPrintDelivery_args();
			args.setVendor_id(vendor_id);
			args.setStorage_no(storage_no);
			args.setPo_no(po_no);
			args.setBox_no(box_no);
			
			sendBase(args, getPrintDelivery_argsHelper.getInstance());
		}
		
		
		private vipapis.delivery.GetPrintDeliveryResponse recv_getPrintDelivery() throws OspException {
			
			getPrintDelivery_result result = new getPrintDelivery_result();
			receiveBase(result, getPrintDelivery_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public String importDeliveryDetail(int vendor_id,String po_no,String storage_no,String delivery_no,List<vipapis.delivery.Delivery> delivery_list) throws OspException {
			
			send_importDeliveryDetail(vendor_id,po_no,storage_no,delivery_no,delivery_list);
			return recv_importDeliveryDetail(); 
			
		}
		
		
		private void send_importDeliveryDetail(int vendor_id,String po_no,String storage_no,String delivery_no,List<vipapis.delivery.Delivery> delivery_list) throws OspException {
			
			initInvocation("importDeliveryDetail");
			
			importDeliveryDetail_args args = new importDeliveryDetail_args();
			args.setVendor_id(vendor_id);
			args.setPo_no(po_no);
			args.setStorage_no(storage_no);
			args.setDelivery_no(delivery_no);
			args.setDelivery_list(delivery_list);
			
			sendBase(args, importDeliveryDetail_argsHelper.getInstance());
		}
		
		
		private String recv_importDeliveryDetail() throws OspException {
			
			importDeliveryDetail_result result = new importDeliveryDetail_result();
			receiveBase(result, importDeliveryDetail_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}