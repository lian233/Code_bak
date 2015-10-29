package com.wofu.fire.deliveryservice;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class OrderUtils 
{
	/*
	 * 转入一个订单到接口表ecs_order_info  ecs_order_goods表
	 */
	public static void createOrder(DataCentre dao,
			Order o, String tradecontactid,String sellernick,int userId) throws Exception {
		try {
			String order_sn = o.getOrder_id();
			String sql = "select region_id from ecs_region where region_name like '%"+o.getProvince().substring(0,2)+"%'";
			int province = dao.intSelect(sql); 
			sql = "select region_id from ecs_region where region_name like '%"+o.getCity().substring(0,2)+"%'";
			int city  = dao.intSelect(sql);
			sql = "select region_id from ecs_region where region_name like '%"+o.getDistrict().substring(0,2)+"%'";
			int district  = dao.intSelect(sql);
			String address = o.getAddress();
			//sql = "select shipping_id from ecs_shipping where shipping_code like '%"+t.getShipping_type()+"%'";
			int shipping_id = 12;//dao.intSelect(sql);
			sql = "select shopid from ContactShopContrast where tradecontactid="+tradecontactid;
			String shopId = dao.strSelect(sql);
			int paymode = 11;//"cod".equals(StringUtil.notNullString(t.getType()))?3:11;//在表ecs_payment中
			String payname = "支付宝";//"cod".equals(StringUtil.notNullString(t.getType()))?"货到付款":"支付宝";//在表ecs_payment中
			String receiverphone="";
			String mobile = o.getMobile();
			String buyermemo="";
			//计算优惠总数
			//统计订单金额
			float payment =0.0f;
			for (Iterator itorder = o.getDetail().iterator(); itorder.hasNext();) {
				Detail detail = (Detail) itorder.next();
				payment+=detail.getPrice()*detail.getNum();
			}
			
			Object[] params = {order_sn,userId,0,0,2,
							o.getName(),1,province,city,district,
					         address,"",o.getCert_no(),mobile,"",
					         "","","",shipping_id,
					         "ems",paymode,payname,"等待所有商品备齐后再发",
					         "","","","","",
					         payment,o.getExpress_price(),0,0,0,0,
					         0,0,0,0,0,payment,
					         0,shopId,o.getCtime().getTime()/1000L,o.getCtime().getTime()/1000L,o.getMtime().getTime()/1000L,0,
					         0,0,0,"",0,0,
					         buyermemo,"",0,"",0,0,0,"0.0",o.getMtime()};
			sql = new StringBuilder().append("insert into ecs_order_info(")
                     .append("order_sn, user_id, order_status, shipping_status, pay_status,")
                    .append("consignee, country, province, city, district,")
                    .append("address, zipcode, tel, mobile, email,")
                    .append("best_time, sign_building, postscript, shipping_id,")
                    .append("shipping_name, pay_id, pay_name, how_oos,")
                    .append("pack_name, card_name, card_message, inv_payee, inv_content,")
                    .append("goods_amount, shipping_fee, insure_fee, pay_fee, pack_fee, card_fee,")
                    .append("money_paid, surplus, integral, integral_money, bonus, order_amount,")
                    .append("from_ad, referer, add_time, confirm_time, pay_time, shipping_time,")
                    .append("pack_id, card_id, bonus_id, invoice_no, extension_code, extension_id,")
                    .append("to_buyer, pay_note, agency_id, inv_type, tax, is_separate, parent_id, discount,modified)")
                    .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)").toString();
                 
			dao.setTransation(false);
			dao.executePreparedSQL(sql, params);
			sql = "select @@IDENTITY";//取得生成的主键
			int order_id = dao.intSelect(sql);
			for (Iterator itorder = o.getDetail().iterator(); itorder.hasNext();) {
				Detail detail = (Detail) itorder.next();
				String outerskuid=detail.getSku();
				Log.info("sku: "+outerskuid);
				sql = "select goods_id from ecs_goods where goods_sn='"+outerskuid+"'";
				String goods_id = dao.strSelect(sql);
				if("".equals(goods_id)) throw new Exception("商品sku没有相关的资料");
				sql = new StringBuilder().append("insert into ecs_order_goods(")
				.append("order_id, goods_id, goods_name, goods_sn,")
				.append("product_id, goods_number, market_price, goods_price, goods_attr,")
				.append("send_number, is_real, extension_code, parent_id, is_gift,goods_attr_id)")
				.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)").toString();
				Object[] params2 = {order_id,goods_id,detail.getTitle(),outerskuid,
						0,detail.getNum(),detail.getPrice(),detail.getPrice(),"",
						0,1,"",0,0,""};
				dao.executePreparedSQL(sql, params2);

			
			}
			dao.commit();
			dao.setTransation(true);
			Log.info("生成订单【" + o.getOrder_id() + "】接口数据成功，订单号ID【"
					+ order_id + "】");

		} catch (JSQLException e1) {
			if (!dao.getConnection().getAutoCommit())
				try {
					dao.rollback();
				} catch (Exception e2) {
				}
			try {
				dao.setTransation(true);
			} catch (Exception e3) {
			}
			throw new JException("生成订单【" + o.getOrder_id() + "】接口数据失败!"
					+ e1.getMessage());
		}
	}
	
}
