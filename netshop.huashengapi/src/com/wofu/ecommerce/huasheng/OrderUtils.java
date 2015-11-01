package com.wofu.ecommerce.huasheng;
import java.sql.Connection;
import java.util.Iterator;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.huasheng.Order;
import com.wofu.ecommerce.huasheng.OrderItem;

public class OrderUtils {
	//订单状态列表
	private static String[][] OrderStatusList = new String[][]{
		{"0","未支付订单"},
		{"1","已付款订单"}
	};
	
	//付款方式列表
	private static String[][] PayWayList = new String[][]{
		{"0","货到付款"},
		{"1","线上支付"}
	};
	
	//发货状态列表
	private static String[][] DeliverStatusList = new String[][]{
		{"0","未发货"},
		{"1","已发货"}
	};
	
	
	/**
	 * 获取订单状态
	 * @param orderStateCode
	 * @return
	 */
	public static String getOrderStateByCode(String orderStateCode)
	{
		if(orderStateCode == null) return "";
		String result = orderStateCode;
		for(int i=0;i<OrderStatusList.length;i++)
		{
			if(orderStateCode.equals(OrderStatusList[i][0]))
			{
				result = OrderStatusList[i][1];
				break;
			}
		}
		return result;
	}
	
	/**
	 * 获取付款方式
	 * @param payWayCode
	 * @return
	 */
	public static String getPayWayByCode(String payWayCode)
	{
		if(payWayCode == null) return "";
		String result = payWayCode;
		for(int i=0;i<PayWayList.length;i++)
		{
			if(payWayCode.equals(PayWayList[i][0]))
			{
				result = PayWayList[i][1];
				break;
			}
		}
		return result;
	}
	
	/**
	 * 发货状态状态
	 * @param deliverStatusCode
	 * @return
	 */
	public static String getDeliverStatusByCode(String deliverStatusCode)
	{
		if(deliverStatusCode == null) return "";
		String result = deliverStatusCode;
		for(int i=0;i<DeliverStatusList.length;i++)
		{
			if(deliverStatusCode.equals(DeliverStatusList[i][0]))
			{
				result = DeliverStatusList[i][1];
				break;
			}
		}
		return result;
	}
	
	//生成接口订单
	@SuppressWarnings("unchecked")
	public static String createInterOrder(Connection conn,Order o,String tradeContactID,String username) throws Exception
	{
		try 
		{
			//启用事务
			conn.setAutoCommit(false);		
			//取接口单号
			String sheetid="";
			String sql="declare @Err int ; declare @NewSheetID char(16); "+
				"execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid=SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql,"取接口单号出错!");
			
			
			//计算商品金额
			double totalfee = 0.0;
			for(Iterator ito=o.getDetail().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item = (OrderItem) ito.next();
				totalfee += item.getPrice();
			}
			
			//写入到接口订单表
			sql  =  "insert into ns_customerorder(CustomerOrderId,Sheetid,Owner,tid,sellernick," +
					"created,buyermessage,payment,status,paytime,modified," +
					"totalfee,postfee,buyernick,receivername,receiverstate,receivercity,receiverdistrict," +
					"receiveraddress,receivermobile,receiverzip,delivery,deliverySheetID,TradeContactID," +
					"paymode,tradefrom,CertType,CertNo,CertName) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
			Object[] sqlv = {
					sheetid,sheetid,	//接口单号
					"yongjun",			//机构对象
					o.getOrder_id(),	//订单号
					username,			//卖家名称
					Formatter.format(o.getCtime(), Formatter.DATE_TIME_FORMAT),	//订单创建时间
					o.getComment(),		//买家留言
					o.getTotal_price(),	//实付金额
					OrderUtils.getOrderStateByCode(o.getStatus()) + "[" + OrderUtils.getPayWayByCode(o.getPay_id()) + "]",	//订单状态
					Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT),	//付款时间
					Formatter.format(o.getMtime(), Formatter.DATE_TIME_FORMAT),		//交易最后修改时间
					totalfee,			//商品金额
					o.getExpress_price(),	//邮费
					o.getBuyer_nickname(),	//买家昵称
					o.getName(),		//收货人名称
					o.getProvince(),	//收货人所在省份
					o.getCity(),		//收货人所在城市
					o.getDistrict(),	//收货人所在城市区域
					o.getAddress(),		//收货人地址
					o.getPhone(),		//收货人移动电话
					o.getPostcode(),	//收货人邮编
					o.getExpress_company(),	//物流公司
					o.getExpress_id(),		//物流单号
					tradeContactID,		//交易往来对像ID
					(o.getPay_id().equals("0") ? 2 : 1),	//付款模式 1:在线支付  2:货到付款
					"huasheng",			//平台
					"1",				//证件类型 01:身份证、02:护照、03:其他
					o.getBuyer_card(),	//证件号码
					o.getBuyer_truename()	//实名
			};
			Log.info("正在写入ns_customerorder表");
			SQLHelper.executePreparedSQL(conn, sql, sqlv);
			
			
			//写入到接口订单明细表
			int j=0;
			for(Iterator ito=o.getDetail().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				sql  =  "insert into ns_orderitem(CustomerOrderId,orderItemId,sheetid,skuid," +
						"title,sellernick,buyernick,created,outeriid,totalfee,payment," +
						"status,owner,skuPropertiesName,num,price,modified) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				//子订单号
				String orderItemId = sheetid + "-" + o.getOrder_id() + String.valueOf(++j);
				
				Object[] sqlvItem = {
						sheetid,				//接口单号
						orderItemId,			//子订单号
						sheetid,				//接口单号
						item.getSku(),			//sku
						item.getTitle(),		//商品名称
						username,				//卖家名称
						o.getBuyer_nickname(),	//买家昵称
						Formatter.format(o.getCtime(), Formatter.DATE_TIME_FORMAT),	//创建时间
						item.getMid(),			//商品网店内部编码
						item.getPrice(),		//应付金额
						item.getPrice(),		//子订单实付金额
						OrderUtils.getOrderStateByCode(o.getStatus()) + "[" + OrderUtils.getPayWayByCode(o.getPay_id()) + "]",	//订单状态
						"yongjun",				//机构对象
						item.getProp(),			//SKU的值
						item.getNum(),			//购买数量
						item.getGoodsprice(),	//单价
						Formatter.format(o.getMtime(), Formatter.DATE_TIME_FORMAT),			//交易最后修改时间
				};
				Log.info("正在写入ns_orderitem表:" + orderItemId);    
				SQLHelper.executePreparedSQL(conn, sql, sqlvItem);
			}
			
			//加入到通知表
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "+
        		  "values('yongjun','" + sheetid + "',1 , '" + tradeContactID + "' , 'yongjun' , getdate() , null) ";
            Log.info("正在写入通知表");
            SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrder_id() + "】接口数据成功，接口单号【" + sheetid + "】");

			return sheetid;
			
		} catch (JSQLException e1) {
			e1.printStackTrace();
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
					Log.info("订单:" + o.getOrder_id() + " 生成订单接口操作回滚成功!");
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			throw new JException("生成订单【" + o.getOrder_id() + "】接口数据失败!"
					+ e1.getMessage());
		}
	}
}
