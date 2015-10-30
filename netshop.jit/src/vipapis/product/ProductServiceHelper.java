package vipapis.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public class ProductServiceHelper {
	
	
	
	
	public static class getProductList_args {
		
		/**
		* 仓库
		* @sampleValue warehouse VIP_NH
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* 档期ID,支持多个值,以竖线分隔
		* @sampleValue schedule_id schedule_id=13092
		*/
		
		private String schedule_id;
		
		/**
		* 频道ID
		* @sampleValue channel_id channel_id=4
		*/
		
		private Integer channel_id;
		
		/**
		* 商品分类ID
		* @sampleValue category_id category_id=11
		*/
		
		private Integer category_id;
		
		/**
		* 查询开始时间，按开售时间sell_time_from查(格式：yyyyMMddHHmmss)
		* @sampleValue start_time start_time=20140602160000
		*/
		
		private String start_time;
		
		/**
		* 查询结束时间，按开售时间sell_time_from查(格式：yyyyMMddHHmmss)
		* @sampleValue end_time end_time=20140702160000
		*/
		
		private String end_time;
		
		/**
		* 商品ID,支持多个值,以竖线分隔
		* @sampleValue product_id product_id=13593233|1564867|123545
		*/
		
		private String product_id;
		
		/**
		* 商品名称关键字
		* @sampleValue product_name product_name="女装"
		*/
		
		private String product_name;
		
		/**
		* 销售价格区间下限
		* @sampleValue sell_price_min sell_price_min=29
		*/
		
		private Integer sell_price_min;
		
		/**
		* 销售价格区间上限
		* @sampleValue sell_price_max sell_price_max=289
		*/
		
		private Integer sell_price_max;
		
		/**
		* 销售折扣区间下限
		* @sampleValue discount_min discount_min=0.22
		*/
		
		private Double discount_min;
		
		/**
		* 销售折扣区间上限
		* @sampleValue discount_max discount_max=0.60
		*/
		
		private Double discount_max;
		
		/**
		* 排序ID DEFAULT=默认、DISCOUNT_DOWN=折扣降序、DISCOUNT_UP=折扣升序、PRICE_DOWN=价格降序、PRICE_UP=价格升序、SALECOUNT_DOWN=销量降序 、SALECOUNT_UP=销量升序
		* @sampleValue sort_type SortType.SORT_DEFAULT
		*/
		
		private vipapis.product.SortType sort_type;
		
		/**
		* SHOW_ALL=全部显示 SHOW_STOCK=只显示有库存
		* @sampleValue stock_show_type stock_show_type=StockShowType.SHOW_ALL
		*/
		
		private vipapis.product.StockShowType stock_show_type;
		
		/**
		* 页码，当使用cursorMark时page值是无效的，建议弃用page改用cursorMark。如果坚持使用page参数从性能考虑只能查询前100页。
		* @sampleValue page page=1
		*/
		
		private Integer page;
		
		/**
		* 每页记录数，默认每页20条，最大每页100条
		* @sampleValue limit limit=20
		*/
		
		private Integer limit;
		
		/**
		* 游标，强烈建议使用，用于加速翻页性能，取第一页数据时输入“*”，取下一页数据则输入当前返回的nextCursorMark的值，使用游标时page字段是失效的
		* @sampleValue cursorMark cursorMark="AoF//Jpm"
		*/
		
		private String cursorMark;
		
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getSchedule_id(){
			return this.schedule_id;
		}
		
		public void setSchedule_id(String value){
			this.schedule_id = value;
		}
		public Integer getChannel_id(){
			return this.channel_id;
		}
		
		public void setChannel_id(Integer value){
			this.channel_id = value;
		}
		public Integer getCategory_id(){
			return this.category_id;
		}
		
		public void setCategory_id(Integer value){
			this.category_id = value;
		}
		public String getStart_time(){
			return this.start_time;
		}
		
		public void setStart_time(String value){
			this.start_time = value;
		}
		public String getEnd_time(){
			return this.end_time;
		}
		
		public void setEnd_time(String value){
			this.end_time = value;
		}
		public String getProduct_id(){
			return this.product_id;
		}
		
		public void setProduct_id(String value){
			this.product_id = value;
		}
		public String getProduct_name(){
			return this.product_name;
		}
		
		public void setProduct_name(String value){
			this.product_name = value;
		}
		public Integer getSell_price_min(){
			return this.sell_price_min;
		}
		
		public void setSell_price_min(Integer value){
			this.sell_price_min = value;
		}
		public Integer getSell_price_max(){
			return this.sell_price_max;
		}
		
		public void setSell_price_max(Integer value){
			this.sell_price_max = value;
		}
		public Double getDiscount_min(){
			return this.discount_min;
		}
		
		public void setDiscount_min(Double value){
			this.discount_min = value;
		}
		public Double getDiscount_max(){
			return this.discount_max;
		}
		
		public void setDiscount_max(Double value){
			this.discount_max = value;
		}
		public vipapis.product.SortType getSort_type(){
			return this.sort_type;
		}
		
		public void setSort_type(vipapis.product.SortType value){
			this.sort_type = value;
		}
		public vipapis.product.StockShowType getStock_show_type(){
			return this.stock_show_type;
		}
		
		public void setStock_show_type(vipapis.product.StockShowType value){
			this.stock_show_type = value;
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
		public String getCursorMark(){
			return this.cursorMark;
		}
		
		public void setCursorMark(String value){
			this.cursorMark = value;
		}
		
	}
	
	
	
	
	public static class getProductStock_args {
		
		/**
		* 仓库
		* @sampleValue warehouse VIP_NH
		*/
		
		private vipapis.common.Warehouse warehouse;
		
		/**
		* 档期ID,支持多个值,以竖线分隔
		* @sampleValue schedule_id schedule_id=13092
		*/
		
		private String schedule_id;
		
		/**
		* 频道ID
		* @sampleValue channel_id channel_id=4
		*/
		
		private Integer channel_id;
		
		/**
		* 商品分类ID
		* @sampleValue category_id category_id=11
		*/
		
		private Integer category_id;
		
		/**
		* 查询开始时间，按开售时间sell_time_from查(格式：yyyyMMddHHmmss)
		* @sampleValue start_time start_time=20140602160000
		*/
		
		private String start_time;
		
		/**
		* 查询结束时间，按开售时间sell_time_from查(格式：yyyyMMddHHmmss)
		* @sampleValue end_time end_time=20140702160000
		*/
		
		private String end_time;
		
		/**
		* 商品ID,支持多个值,以竖线分隔
		* @sampleValue product_id product_id=13593233|1564867|123545
		*/
		
		private String product_id;
		
		/**
		* 商品名称关键字
		* @sampleValue product_name product_name="女装"
		*/
		
		private String product_name;
		
		/**
		* 销售价格区间下限
		* @sampleValue sell_price_min sell_price_min=29
		*/
		
		private Integer sell_price_min;
		
		/**
		* 销售价格区间上限
		* @sampleValue sell_price_max sell_price_max=289
		*/
		
		private Integer sell_price_max;
		
		/**
		* 销售折扣区间下限
		* @sampleValue discount_min discount_min=0.22
		*/
		
		private Double discount_min;
		
		/**
		* 销售折扣区间上限
		* @sampleValue discount_max discount_max=0.60
		*/
		
		private Double discount_max;
		
		/**
		* 排序ID DEFAULT=默认、DISCOUNT_DOWN=折扣降序、DISCOUNT_UP=折扣升序、PRICE_DOWN=价格降序、PRICE_UP=价格升序、SALECOUNT_DOWN=销量降序 、SALECOUNT_UP=销量升序
		* @sampleValue sort_type SortType.SORT_DEFAULT
		*/
		
		private vipapis.product.SortType sort_type;
		
		/**
		* SHOW_ALL=全部显示 SHOW_STOCK=只显示有库存
		* @sampleValue stock_show_type stock_show_type=StockShowType.SHOW_ALL
		*/
		
		private vipapis.product.StockShowType stock_show_type;
		
		/**
		* 页码，当使用cursorMark时page值是无效的，建议弃用page改用cursorMark。如果坚持使用page参数从性能考虑只能查询前100页。
		* @sampleValue page page=1
		*/
		
		private Integer page;
		
		/**
		* 每页记录数，默认每页20条，最大100条
		* @sampleValue limit limit=20
		*/
		
		private Integer limit;
		
		/**
		* 游标，强烈建议使用，用于加速翻页性能，取第一页数据时输入“*”，取下一页数据则输入当前返回的nextCursorMark的值，使用游标时page字段是失效的
		* @sampleValue cursorMark cursorMark="AoF//Jpm"
		*/
		
		private String cursorMark;
		
		public vipapis.common.Warehouse getWarehouse(){
			return this.warehouse;
		}
		
		public void setWarehouse(vipapis.common.Warehouse value){
			this.warehouse = value;
		}
		public String getSchedule_id(){
			return this.schedule_id;
		}
		
		public void setSchedule_id(String value){
			this.schedule_id = value;
		}
		public Integer getChannel_id(){
			return this.channel_id;
		}
		
		public void setChannel_id(Integer value){
			this.channel_id = value;
		}
		public Integer getCategory_id(){
			return this.category_id;
		}
		
		public void setCategory_id(Integer value){
			this.category_id = value;
		}
		public String getStart_time(){
			return this.start_time;
		}
		
		public void setStart_time(String value){
			this.start_time = value;
		}
		public String getEnd_time(){
			return this.end_time;
		}
		
		public void setEnd_time(String value){
			this.end_time = value;
		}
		public String getProduct_id(){
			return this.product_id;
		}
		
		public void setProduct_id(String value){
			this.product_id = value;
		}
		public String getProduct_name(){
			return this.product_name;
		}
		
		public void setProduct_name(String value){
			this.product_name = value;
		}
		public Integer getSell_price_min(){
			return this.sell_price_min;
		}
		
		public void setSell_price_min(Integer value){
			this.sell_price_min = value;
		}
		public Integer getSell_price_max(){
			return this.sell_price_max;
		}
		
		public void setSell_price_max(Integer value){
			this.sell_price_max = value;
		}
		public Double getDiscount_min(){
			return this.discount_min;
		}
		
		public void setDiscount_min(Double value){
			this.discount_min = value;
		}
		public Double getDiscount_max(){
			return this.discount_max;
		}
		
		public void setDiscount_max(Double value){
			this.discount_max = value;
		}
		public vipapis.product.SortType getSort_type(){
			return this.sort_type;
		}
		
		public void setSort_type(vipapis.product.SortType value){
			this.sort_type = value;
		}
		public vipapis.product.StockShowType getStock_show_type(){
			return this.stock_show_type;
		}
		
		public void setStock_show_type(vipapis.product.StockShowType value){
			this.stock_show_type = value;
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
		public String getCursorMark(){
			return this.cursorMark;
		}
		
		public void setCursorMark(String value){
			this.cursorMark = value;
		}
		
	}
	
	
	
	
	public static class getProductList_result {
		
		/**
		*/
		
		private vipapis.product.GetProductListResponse success;
		
		public vipapis.product.GetProductListResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.product.GetProductListResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getProductStock_result {
		
		/**
		*/
		
		private vipapis.product.GetProductStockResponse success;
		
		public vipapis.product.GetProductStockResponse getSuccess(){
			return this.success;
		}
		
		public void setSuccess(vipapis.product.GetProductStockResponse value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getProductList_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getProductList_args>
	{
		
		public static final getProductList_argsHelper OBJ = new getProductList_argsHelper();
		
		public static getProductList_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getProductList_args struct, Protocol iprot) throws OspException {
			
			
			
			
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
				
				struct.setSchedule_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setChannel_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setCategory_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStart_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEnd_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setProduct_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setProduct_name(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setSell_price_min(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setSell_price_max(value);
			}
			
			
			
			
			
			if(true){
				
				Double value;
				value = iprot.readDouble();
				
				struct.setDiscount_min(value);
			}
			
			
			
			
			
			if(true){
				
				Double value;
				value = iprot.readDouble();
				
				struct.setDiscount_max(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.product.SortType value;
				
				value = null;
				String name = iprot.readString();
				vipapis.product.SortType[] values = vipapis.product.SortType.values(); 
				for(vipapis.product.SortType v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setSort_type(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.product.StockShowType value;
				
				value = null;
				String name = iprot.readString();
				vipapis.product.StockShowType[] values = vipapis.product.StockShowType.values(); 
				for(vipapis.product.StockShowType v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setStock_show_type(value);
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
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCursorMark(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getProductList_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getWarehouse() != null) {
				
				oprot.writeFieldBegin("warehouse");
				oprot.writeString(struct.getWarehouse().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSchedule_id() != null) {
				
				oprot.writeFieldBegin("schedule_id");
				oprot.writeString(struct.getSchedule_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getChannel_id() != null) {
				
				oprot.writeFieldBegin("channel_id");
				oprot.writeI32(struct.getChannel_id()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getCategory_id() != null) {
				
				oprot.writeFieldBegin("category_id");
				oprot.writeI32(struct.getCategory_id()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getStart_time() != null) {
				
				oprot.writeFieldBegin("start_time");
				oprot.writeString(struct.getStart_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEnd_time() != null) {
				
				oprot.writeFieldBegin("end_time");
				oprot.writeString(struct.getEnd_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getProduct_id() != null) {
				
				oprot.writeFieldBegin("product_id");
				oprot.writeString(struct.getProduct_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getProduct_name() != null) {
				
				oprot.writeFieldBegin("product_name");
				oprot.writeString(struct.getProduct_name());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSell_price_min() != null) {
				
				oprot.writeFieldBegin("sell_price_min");
				oprot.writeI32(struct.getSell_price_min()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSell_price_max() != null) {
				
				oprot.writeFieldBegin("sell_price_max");
				oprot.writeI32(struct.getSell_price_max()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getDiscount_min() != null) {
				
				oprot.writeFieldBegin("discount_min");
				oprot.writeDouble(struct.getDiscount_min());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getDiscount_max() != null) {
				
				oprot.writeFieldBegin("discount_max");
				oprot.writeDouble(struct.getDiscount_max());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSort_type() != null) {
				
				oprot.writeFieldBegin("sort_type");
				oprot.writeString(struct.getSort_type().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getStock_show_type() != null) {
				
				oprot.writeFieldBegin("stock_show_type");
				oprot.writeString(struct.getStock_show_type().name());  
				
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
			
			
			if(struct.getCursorMark() != null) {
				
				oprot.writeFieldBegin("cursorMark");
				oprot.writeString(struct.getCursorMark());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getProductList_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getProductStock_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getProductStock_args>
	{
		
		public static final getProductStock_argsHelper OBJ = new getProductStock_argsHelper();
		
		public static getProductStock_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getProductStock_args struct, Protocol iprot) throws OspException {
			
			
			
			
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
				
				struct.setSchedule_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setChannel_id(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setCategory_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setStart_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setEnd_time(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setProduct_id(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setProduct_name(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setSell_price_min(value);
			}
			
			
			
			
			
			if(true){
				
				Integer value;
				value = iprot.readI32(); 
				
				struct.setSell_price_max(value);
			}
			
			
			
			
			
			if(true){
				
				Double value;
				value = iprot.readDouble();
				
				struct.setDiscount_min(value);
			}
			
			
			
			
			
			if(true){
				
				Double value;
				value = iprot.readDouble();
				
				struct.setDiscount_max(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.product.SortType value;
				
				value = null;
				String name = iprot.readString();
				vipapis.product.SortType[] values = vipapis.product.SortType.values(); 
				for(vipapis.product.SortType v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setSort_type(value);
			}
			
			
			
			
			
			if(true){
				
				vipapis.product.StockShowType value;
				
				value = null;
				String name = iprot.readString();
				vipapis.product.StockShowType[] values = vipapis.product.StockShowType.values(); 
				for(vipapis.product.StockShowType v : values){
					
					if(v.name().equals(name)){
						
						value = v;
						break;
					}
					
				}
				
				
				struct.setStock_show_type(value);
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
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCursorMark(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getProductStock_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getWarehouse() != null) {
				
				oprot.writeFieldBegin("warehouse");
				oprot.writeString(struct.getWarehouse().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSchedule_id() != null) {
				
				oprot.writeFieldBegin("schedule_id");
				oprot.writeString(struct.getSchedule_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getChannel_id() != null) {
				
				oprot.writeFieldBegin("channel_id");
				oprot.writeI32(struct.getChannel_id()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getCategory_id() != null) {
				
				oprot.writeFieldBegin("category_id");
				oprot.writeI32(struct.getCategory_id()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getStart_time() != null) {
				
				oprot.writeFieldBegin("start_time");
				oprot.writeString(struct.getStart_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getEnd_time() != null) {
				
				oprot.writeFieldBegin("end_time");
				oprot.writeString(struct.getEnd_time());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getProduct_id() != null) {
				
				oprot.writeFieldBegin("product_id");
				oprot.writeString(struct.getProduct_id());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getProduct_name() != null) {
				
				oprot.writeFieldBegin("product_name");
				oprot.writeString(struct.getProduct_name());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSell_price_min() != null) {
				
				oprot.writeFieldBegin("sell_price_min");
				oprot.writeI32(struct.getSell_price_min()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSell_price_max() != null) {
				
				oprot.writeFieldBegin("sell_price_max");
				oprot.writeI32(struct.getSell_price_max()); 
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getDiscount_min() != null) {
				
				oprot.writeFieldBegin("discount_min");
				oprot.writeDouble(struct.getDiscount_min());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getDiscount_max() != null) {
				
				oprot.writeFieldBegin("discount_max");
				oprot.writeDouble(struct.getDiscount_max());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getSort_type() != null) {
				
				oprot.writeFieldBegin("sort_type");
				oprot.writeString(struct.getSort_type().name());  
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getStock_show_type() != null) {
				
				oprot.writeFieldBegin("stock_show_type");
				oprot.writeString(struct.getStock_show_type().name());  
				
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
			
			
			if(struct.getCursorMark() != null) {
				
				oprot.writeFieldBegin("cursorMark");
				oprot.writeString(struct.getCursorMark());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getProductStock_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getProductList_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getProductList_result>
	{
		
		public static final getProductList_resultHelper OBJ = new getProductList_resultHelper();
		
		public static getProductList_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getProductList_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.product.GetProductListResponse value;
				
				value = new vipapis.product.GetProductListResponse();
				vipapis.product.GetProductListResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getProductList_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.product.GetProductListResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getProductList_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getProductStock_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getProductStock_result>
	{
		
		public static final getProductStock_resultHelper OBJ = new getProductStock_resultHelper();
		
		public static getProductStock_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getProductStock_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				vipapis.product.GetProductStockResponse value;
				
				value = new vipapis.product.GetProductStockResponse();
				vipapis.product.GetProductStockResponseHelper.getInstance().read(value, iprot);
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getProductStock_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				vipapis.product.GetProductStockResponseHelper.getInstance().write(struct.getSuccess(), oprot);
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getProductStock_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class ProductServiceClient extends OspRestStub implements ProductService  {
		
		
		public ProductServiceClient() {
			
			super("1.0.1", "vipapis.product.ProductService");
		}
		
		
		
		public vipapis.product.GetProductListResponse getProductList(vipapis.common.Warehouse warehouse,String schedule_id,Integer channel_id,Integer category_id,String start_time,String end_time,String product_id,String product_name,Integer sell_price_min,Integer sell_price_max,Double discount_min,Double discount_max,vipapis.product.SortType sort_type,vipapis.product.StockShowType stock_show_type,Integer page,Integer limit,String cursorMark) throws OspException {
			
			send_getProductList(warehouse,schedule_id,channel_id,category_id,start_time,end_time,product_id,product_name,sell_price_min,sell_price_max,discount_min,discount_max,sort_type,stock_show_type,page,limit,cursorMark);
			return recv_getProductList(); 
			
		}
		
		
		private void send_getProductList(vipapis.common.Warehouse warehouse,String schedule_id,Integer channel_id,Integer category_id,String start_time,String end_time,String product_id,String product_name,Integer sell_price_min,Integer sell_price_max,Double discount_min,Double discount_max,vipapis.product.SortType sort_type,vipapis.product.StockShowType stock_show_type,Integer page,Integer limit,String cursorMark) throws OspException {
			
			initInvocation("getProductList");
			
			getProductList_args args = new getProductList_args();
			args.setWarehouse(warehouse);
			args.setSchedule_id(schedule_id);
			args.setChannel_id(channel_id);
			args.setCategory_id(category_id);
			args.setStart_time(start_time);
			args.setEnd_time(end_time);
			args.setProduct_id(product_id);
			args.setProduct_name(product_name);
			args.setSell_price_min(sell_price_min);
			args.setSell_price_max(sell_price_max);
			args.setDiscount_min(discount_min);
			args.setDiscount_max(discount_max);
			args.setSort_type(sort_type);
			args.setStock_show_type(stock_show_type);
			args.setPage(page);
			args.setLimit(limit);
			args.setCursorMark(cursorMark);
			
			sendBase(args, getProductList_argsHelper.getInstance());
		}
		
		
		private vipapis.product.GetProductListResponse recv_getProductList() throws OspException {
			
			getProductList_result result = new getProductList_result();
			receiveBase(result, getProductList_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public vipapis.product.GetProductStockResponse getProductStock(vipapis.common.Warehouse warehouse,String schedule_id,Integer channel_id,Integer category_id,String start_time,String end_time,String product_id,String product_name,Integer sell_price_min,Integer sell_price_max,Double discount_min,Double discount_max,vipapis.product.SortType sort_type,vipapis.product.StockShowType stock_show_type,Integer page,Integer limit,String cursorMark) throws OspException {
			
			send_getProductStock(warehouse,schedule_id,channel_id,category_id,start_time,end_time,product_id,product_name,sell_price_min,sell_price_max,discount_min,discount_max,sort_type,stock_show_type,page,limit,cursorMark);
			return recv_getProductStock(); 
			
		}
		
		
		private void send_getProductStock(vipapis.common.Warehouse warehouse,String schedule_id,Integer channel_id,Integer category_id,String start_time,String end_time,String product_id,String product_name,Integer sell_price_min,Integer sell_price_max,Double discount_min,Double discount_max,vipapis.product.SortType sort_type,vipapis.product.StockShowType stock_show_type,Integer page,Integer limit,String cursorMark) throws OspException {
			
			initInvocation("getProductStock");
			
			getProductStock_args args = new getProductStock_args();
			args.setWarehouse(warehouse);
			args.setSchedule_id(schedule_id);
			args.setChannel_id(channel_id);
			args.setCategory_id(category_id);
			args.setStart_time(start_time);
			args.setEnd_time(end_time);
			args.setProduct_id(product_id);
			args.setProduct_name(product_name);
			args.setSell_price_min(sell_price_min);
			args.setSell_price_max(sell_price_max);
			args.setDiscount_min(discount_min);
			args.setDiscount_max(discount_max);
			args.setSort_type(sort_type);
			args.setStock_show_type(stock_show_type);
			args.setPage(page);
			args.setLimit(limit);
			args.setCursorMark(cursorMark);
			
			sendBase(args, getProductStock_argsHelper.getInstance());
		}
		
		
		private vipapis.product.GetProductStockResponse recv_getProductStock() throws OspException {
			
			getProductStock_result result = new getProductStock_result();
			receiveBase(result, getProductStock_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}