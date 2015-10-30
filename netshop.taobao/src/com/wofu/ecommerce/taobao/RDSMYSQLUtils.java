package com.wofu.ecommerce.taobao;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.Types;
import com.wofu.common.tools.util.log.Log;

public class RDSMYSQLUtils {

	/*
	 * ת��һ���������ӿڱ�ecs_order_info  ecs_order_goods��
	 */
	private static void createOrder(DataCentre dao,
			Trade t, String tradecontactid,String sellernick,boolean isFormal,int userId) throws Exception {
		try {
			String order_sn = String.valueOf(t.getTid());
			String buyermessage = t.getBuyer_message();//���������֤��Ϣ
			String sellermemo = StringUtil.notNullString(t.getSeller_memo());//���������֤��Ϣ
			Log.info("��ұ�ע: "+buyermessage);
			String sql = "select region_id from ecs_region where region_name like '%"+t.getReceiver_state().substring(0,2)+"%'";
			int province = dao.intSelect(sql); 
			sql = "select region_id from ecs_region where region_name like '%"+t.getReceiver_city().substring(0,2)+"%'";
			int city  = dao.intSelect(sql);
			sql = "select region_id from ecs_region where region_name like '%"+t.getReceiver_district().substring(0,2)+"%'";
			int district  = dao.intSelect(sql);
			String address = t.getReceiver_address();
			String zipcode = StringUtil.notNullString(t.getReceiver_zip());
			String email = StringUtil.notNullString(t.getBuyer_email());
			sql = "select shipping_id from ecs_shipping where shipping_code like '%"+t.getShipping_type()+"%'";
			int shipping_id = dao.intSelect(sql);
			sql = "select shopid from ContactShopContrast where tradecontactid="+tradecontactid;
			String shopId = dao.strSelect(sql);
			int paymode = "cod".equals(StringUtil.notNullString(t.getType()))?3:11;//�ڱ�ecs_payment��
			String payname = "cod".equals(StringUtil.notNullString(t.getType()))?"��������":"֧����";//�ڱ�ecs_payment��
			String receiverphone="";
			if (t.getReceiver_phone()!=null)
				receiverphone=t.getReceiver_phone();
			String mobile = StringUtil.notNullString(t.getReceiver_mobile());
			String buyermemo="";
			if (t.getBuyer_memo()!=null && !t.getBuyer_memo().equals("null"))
				buyermemo=StringUtil.replace(t.getBuyer_memo(),"'","");
			
			sql="select ifnull(value,0) from config where name='�Ա����ұ�ע�����Ƿ�ȥ����һ���ַ�'";
			int isremovefirst=Integer.valueOf(dao.strSelect(sql)).intValue();
			if (isremovefirst==1 && t.getSeller_flag()==1 && !sellermemo.equals(""))
			{
				if (sellermemo.substring(0, 1).matches("[A-Za-z]"))
					sellermemo=sellermemo.substring(1);
			}
			//�����Ż�����
			double discount =t.getDiscount_fee();
			for (Iterator itorder = t.getOrders().getRelationData().iterator(); itorder.hasNext();) {
				Order o = (Order) itorder.next();  
				discount+=o.getDiscount_fee();//ϵͳ�Ż�

			
			}
			
			Object[] params = {order_sn,userId,0,0,2,
							t.getReceiver_name(),1,province,city,district,
					         address,zipcode,buyermessage!=null?buyermessage:"",mobile,email,
					         "","","",shipping_id,
					         t.getShipping_type(),paymode,payname,"�ȴ�������Ʒ������ٷ�",
					         "","","","","",
					         t.getTotal_fee(),t.getPost_fee(),0,0,0,0,
					         0,0,0,0,0,t.getPayment(),
					         0,shopId,t.getCreated().getTime()/1000L,t.getCreated().getTime()/1000L,t.getPay_time().getTime()/1000L,0,
					         0,0,0,"",0,0,
					         buyermemo,sellermemo,0,"",0,0,0,discount,t.getModified()};
				Log.info("discount: "+discount)	         ;
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
			sql = "select @@IDENTITY";//ȡ�����ɵ�����
			int order_id = dao.intSelect(sql);
			for (Iterator itorder = t.getOrders().getRelationData().iterator(); itorder.hasNext();) {
				Order o = (Order) itorder.next();
				String outerskuid="";
				if (o.getOuter_iid()!=null)
					outerskuid=o.getOuter_iid();
				else
					outerskuid=o.getOuter_sku_id();
				sql = "select goods_id from ecs_goods where goods_sn='"+outerskuid+"'";
				String goods_id = dao.strSelect(sql);
				if("".equals(goods_id)) throw new Exception("��Ʒskuû����ص�����");
				sql = new StringBuilder().append("insert into ecs_order_goods(")
				.append("order_id, goods_id, goods_name, goods_sn,")
				.append("product_id, goods_number, market_price, goods_price, goods_attr,")
				.append("send_number, is_real, extension_code, parent_id, is_gift,goods_attr_id)")
				.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)").toString();
				Object[] params2 = {order_id,goods_id,o.getTitle(),outerskuid,
						0,o.getNum(),o.getPrice(),o.getPrice(),"",
						0,1,"",0,0,""};
				dao.executePreparedSQL(sql, params2);

			
			}
			dao.commit();
			dao.setTransation(true);
			Log.info("���ɶ�����" + t.getTid() + "���ӿ����ݳɹ���������ID��"
					+ order_id + "��");

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
			throw new JException("���ɶ�����" + t.getTid() + "���ӿ�����ʧ��!"
					+ e1.getMessage());
		}
	}

	public static void processOrder(String jobname,DataCentre dao, Trade td,
			String tradecontactid,String sellernick,boolean waitbuyerpayisin,boolean isc,int userId) throws Exception {
		Log.info(td.getTid()+" "+td.getStatus()+" "+Formatter.format(td.getModified(),Formatter.DATE_TIME_FORMAT));
		/*
		 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
		 *2��ɾ���ȴ���Ҹ���ʱ��������� 
		 */		
		String sku;
		String sql="";
		if (td.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
		{	
			
				if (!OrderManager.TidLastModifyIntfExists("����Ա�����", dao.getConnection(), String.valueOf(td.getTid()),td.getModified(),true))
				{
					createOrder(dao,td,tradecontactid,sellernick,true,userId);
					/**
					for(Iterator ito=td.getOrders().getRelationData().iterator();ito.hasNext();)
					{
						Order o=(Order) ito.next();
						sku=o.getOuter_sku_id();
						
						//-------------yyk��
						//StockManager.deleteWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(td.getTid()),sku);
						//StockManager.addSynReduceStore(jobname, dao.getConnection(), tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, -o.getNum(),false);
					}
					**/
				}

			//�ȴ���Ҹ���ʱ��¼�������
		}
		
		/**
		else if (td.getStatus().equals("WAIT_BUYER_PAY") || td.getStatus().equals("TRADE_NO_CREATE_PAY"))
		{						
				
			if (waitbuyerpayisin)
			{
				if (!OrderManager.TidLastModifyIntfExists("����Ա�����", dao.getConnection(), String.valueOf(td.getTid()),td.getModified()))
				{
					createOrder(dao,td,tradecontactid,sellernick,false,userId);
					
				}
			}
			
			for(Iterator ito=td.getOrders().getRelationData().iterator();ito.hasNext();)
			{
				Order o=(Order) ito.next();
				sku=o.getOuter_sku_id();
				//-------------yyk��
				StockManager.addWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(td.getTid()), sku, o.getNum());
				StockManager.addSynReduceStore(jobname, dao.getConnection(), tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, -o.getNum(),false);
			}
			
		
  
			//�����Ժ��û��˿�ɹ��������Զ��ر�
			//�ͷſ��,����Ϊ����
		} else if (td.getStatus().equals("TRADE_CLOSED"))
		{					
			OrderManager.CancelOrderByCID(jobname, dao.getConnection(), String.valueOf(td.getTid()));
			for(Iterator ito=td.getOrders().getRelationData().iterator();ito.hasNext();)
			{
				Order o=(Order) ito.next();		
				sku=o.getOuter_sku_id();
				//StockManager.deleteWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(td.getTid()), sku);
				
			}

			//������ǰ�����һ���������رս���
			//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
		}else if (td.getStatus().equals("TRADE_CLOSED_BY_TAOBAO"))
		{
			if (waitbuyerpayisin)
			{
				
				if (!OrderManager.TidLastModifyIntfExists("����Ա�����", dao.getConnection(), String.valueOf(td.getTid()),td.getModified()))
				{
					//createOrder(dao,td,tradecontactid,sellernick,false,userId);
					
				}
			}

/**
			for(Iterator ito=td.getOrders().getRelationData().iterator();ito.hasNext();)
			{
				Order o=(Order) ito.next();
				sku=o.getOuter_sku_id();
			
				 
				//StockManager.deleteWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(td.getTid()), sku);
				
				
				if (StockManager.WaitPayStockExists(jobname,dao.getConnection(),tradecontactid, String.valueOf(td.getTid()), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
				{
					//-------------yyk��
					//StockManager.addSynReduceStore(jobname, dao.getConnection(), tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, o.getNum(),false);
				}
			}
			**/
	
			
		
		//}
	/**
		else if (td.getStatus().equals("TRADE_FINISHED"))
		{
			for(Iterator ito=td.getOrders().getRelationData().iterator();ito.hasNext();)
			{
				Order o=(Order) ito.next();
				sku=o.getOuter_sku_id();
	
				//StockManager.deleteWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(td.getTid()), sku);		
				
				//���½���ʱ��
				//OrderUtils.updateFinishedStatus(dao.getConnection(),tradecontactid,td.getTid(),td.getEnd_time());
								
			}

		}	
		
			//�����˻�
			for(Iterator oit=td.getOrders().getRelationData().iterator();oit.hasNext();)
			{						
				Order o=(Order) oit.next();	
							
				if (o.getRefund_id()>0)
				{
					sql="select count(*) from eco_rds_refund with(nolock) "
						+"where seller_nick='"+sellernick+"' and refund_id='"+o.getRefund_id()+"'";
					
					if (dao.intSelect(sql)==0) continue;
					
					Refund r=getRefund(dao,sellernick,o.getRefund_id());
					
					createRefund(dao,tradecontactid,td,o,r);
				}
			}
			**/
			

	}
	

	public static void processItem(String jobname,DataCentre dao, Item item,
			int orgId,int tradecontactid,String sellernick) throws Exception {
	
		StockManager.stockConfig(dao, orgId,tradecontactid,String.valueOf(item.getNum_iid()),item.getOuter_id(),item.getTitle(),Long.valueOf(item.getNum()).intValue());
		for(Iterator it=item.getSkus().getRelationData().iterator();it.hasNext();)
		{
			Sku skuinfo=(Sku) it.next();						
			
			Log.info("SKU "+skuinfo.getOuter_id()+" "+Formatter.format(skuinfo.getModified(),Formatter.DATE_TIME_FORMAT));
			
			StockManager.addStockConfigSku(dao, orgId,String.valueOf(item.getNum_iid()),String.valueOf(skuinfo.getSku_id()),skuinfo.getOuter_id(),Long.valueOf(skuinfo.getQuantity()).intValue()) ;
			
		}

	}
	public static String createDistributionOrder(String modulename,
			Connection conn, PurchaseOrder po,  String tradecontactid)
			throws Exception {
		String sql = "select count(*) from ecs_distributor with(nolock) "
				+ "where distributorname='"
				+ po.getDistributor_username().trim() + "' and shopname<>''";

		if (SQLHelper.intSelect(conn, sql) == 0) {
			Log.info("�����ڷ�����:" + po.getDistributor_username().trim()
					+ ",���߷����̵�������Ϊ��!");
			OrderUtils.getDistributorByNick(conn,tradecontactid,po.getDistributor_username().trim());
	
		}

		try {

			String sheetid = "";

			conn.setAutoCommit(false);

			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");

			// ���뵽֪ͨ��
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid
					+ "',1 , '"
					+ tradecontactid
					+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			int haspostFee = 0;

			String promotionDetails = "";
			
			sql = "select distributorid,shopname from ecs_distributor with(nolock) where distributorname='"
					+ po.getDistributor_username().trim() + "'";
			Hashtable distributorinfo = SQLHelper.oneRowSelect(conn, sql);

			String distributorid = distributorinfo.get("distributorid")
					.toString();
			String distributorshopname = distributorinfo.get("shopname")
					.toString();
			
			String buyermemo = "";
			if (!po.getMemo().equals(""))
				buyermemo = po.getMemo().substring(
						po.getMemo().indexOf(" :") + 2, po.getMemo().length());
			
			String phone=po.getReceiverinfo().getPhone();
			String receiverName = po.getReceiverinfo().getName()!=null?po.getReceiverinfo().getName():"";
			if (phone==null) phone="";
			sql = "insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , "
					+ " type , created , buyermessage , shippingtype , payment , "
					+ " discountfee , adjustfee , status ,  "
					+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
					+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
					+ " buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone  , "
					+ " buyeremail , commissionfee , availableconfirmfee , haspostFee , receivedpayment , "
					+ " buyermemo,sellermemo, "
					+ " snapshoturl, tradefrom ,PromotionDetails,"
					+ " tradeContactid,distributorid,distributetid,distributorshopname) values("
					+ "'"
					+ sheetid
					+ "','"
					+ sheetid
					+ "','yongjun','"
					+ po.getId()
					+ "','', '"
					+ po.getSupplier_username()
					+ "', "
					+ "'fixed' ,'"
					+ Formatter.format(po.getCreated(),
							Formatter.DATE_TIME_FORMAT)
					+ "','','"
					+ po.getShipping()
					+ "','"
					+ po.getDistributor_payment()
					+ "', "
					+ "'"
					+ String.valueOf(Double.valueOf(po.getTotal_fee())
							.doubleValue()
							- Double.valueOf(po.getDistributor_payment())
									.doubleValue())
					+ "', '0.00' , '"
					+ po.getStatus()
					+ "' , "
					+ "'' , '"
					+ Formatter.format(po.getPay_time(),
							Formatter.DATE_TIME_FORMAT)
					+ "' , '"
					+ Formatter.format(po.getModified(),
							Formatter.DATE_TIME_FORMAT)
					+ "', '"
					+ Formatter.format(po.getModified(),
							Formatter.DATE_TIME_FORMAT)
					+ "' , 0 , 0, 0,"
					+ po.getTotal_fee()
					+ " , '"
					+ po.getPost_fee()
					+ "','"
					+ po.getAlipay_no()
					+ "',"
					+ "'"
					+ receiverName.replaceAll("'", " ")
					+ "' ,'"
					+ receiverName.replaceAll("'", " ")
					+ "' , '"
					+ po.getReceiverinfo().getState()
					+ "', '"
					+ po.getReceiverinfo().getCity()
					+ "' , '"
					+ po.getReceiverinfo().getDistrict()
					+ "', "
					+ "'"
					+ po.getReceiverinfo().getAddress().replaceAll("'", " ")
					+ "','"
					+ po.getReceiverinfo().getZip()
					+ "' , '"
					+ po.getReceiverinfo().getMobile_phone()
					+ "' , '"
					+ phone
					+ "' ,  "
					+ "'' , '0.00' , '0.00' , "
					+ String.valueOf(haspostFee)
					+ ",'"
					+ po.getDistributor_payment()
					+ "', "
					+ "  '"
					+ buyermemo.replaceAll("'", "")
					+ "', '"
					+ po.getSupplier_memo()
					+ "' ,'"
					+ po.getSnapshot_url()
					+ "', '"
					+ po.getDistributor_username()
					+ "', '"
					+ promotionDetails
					+ "',"
					+ tradecontactid
					+ ","
					+ distributorid
					+ ",'"
					+ po.getTc_order_id()
					+ "','"
					+ distributorshopname + "')";
			SQLHelper.executeSQL(conn, sql);

		
			for (Iterator itorder = po.getSub_purchase_orders().getRelationData().iterator(); itorder
					.hasNext();) {
				
				SubPurchaseOrder o = (SubPurchaseOrder) itorder.next();
				String order_200_status=o.getOrder_200_status();
				//δ����ǰ���Թرն����е�ĳ����Ʒ�������Ʒ�Ͳ�Ҫд���ӿڱ���
				if("TRADE_CLOSED".equals(order_200_status)) continue;
				String refundstatus = "";
				if (o.getStatus().equalsIgnoreCase("RADE_REFUNDING")
						|| o.getStatus().equalsIgnoreCase("TRADE_REFUNDED"))
					refundstatus = o.getStatus();
				else
					refundstatus = "NO_REFUND";

				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid  , "
						+ " title , sellernick , buyernick , created , "
						+ " outeriid , outerskuid , totalfee , payment , "
						+ " status  , owner , "
						+ " iid , skuPropertiesName , num , price , picPath , "
						+ " oid , snapShotUrl,refundstatus,"
						+ "  numiid , distributePrice) values( "
						+ "'"
						+ sheetid
						+ "','"
						+ sheetid
						+ "_"
						+ o.getFenxiao_id()
						+ "','"
						+ sheetid
						+ "','"
						+ o.getSku_id()
						+ "' , "
						+ "'"
						+ o.getTitle()
						+ "' , '"
						+ po.getSupplier_username()
						+ "', '"
						+ po.getDistributor_username()
						+ "' , '"
						+ Formatter.format(o.getCreated(),
								Formatter.DATE_TIME_FORMAT)
						+ "', "
						+ "'"
						+ o.getItem_outer_id()
						+ "' , '"
						+ o.getSku_outer_id()
						+ "' , '"
						+ o.getTotal_fee()
						+ "' , '"
						+ o.getDistributor_payment()
						+ "' , "
						+ "'"
						+ o.getStatus()
						+ "','yongjun',"
						+ "'"
						+ o.getItem_id()
						+ "' , '"
						+ o.getSku_properties()
						+ "' ,"
						+ o.getNum()
						+ " , '"
						+ o.getPrice()
						+ "' , '"
						+ o.getSnapshot_url()
						+ "' , "
						+ "'"
						+ o.getTc_order_id()//o.getId()
						+ "' , '"
						+ o.getSnapshot_url()
						+ "' , '"
						+ refundstatus
						+ "',"
						+ o.getItem_id()
						+ ","
						+ Double.valueOf(o.getBuyer_payment()).doubleValue()
						/ o.getNum() + ")";
				SQLHelper.executeSQL(conn, sql);
			}
			
			conn.commit();
			conn.setAutoCommit(true);

			Log.info(modulename, "���ɶ�����" + po.getId() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid+ "��");
			return sheetid;

		} catch (Exception e1) {
			
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			//throw e1;
			throw new JException("���ɶ�����" + po.getId() + "���ӿ�����ʧ��!" + e1.getMessage());
		}
	}

	public static void processFenXiaoOrder(String jobname,DataCentre dao, 
			PurchaseOrder po,String tradecontactid,String sellernick) throws Exception {
		Date date= po.getModified()!=null?po.getModified():new Date();
		//String tcOrder_id = po.getTc_order_id()!=null?po.getTc_order_id():0L;
		Log.info(po.getId()+" "+po.getStatus()+" "+Formatter.format(date,Formatter.DATE_TIME_FORMAT));
		/*
		 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
		 *2��ɾ���ȴ���Ҹ���ʱ��������� 
		 */		
		String sku;
		String sql="";

		if (po.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
		{	
		
			if (!OrderManager.isCheck("����Ա�����", dao.getConnection(), String.valueOf(po.getId())))
			{
				if (!OrderManager.TidLastModifyIntfExists("����Ա�����", dao.getConnection(), String.valueOf(po.getId()),po.getModified()))
				{
				
					createDistributionOrder(jobname,dao.getConnection(),po,tradecontactid);
					
					for(Iterator ito=po.getSub_purchase_orders().getRelationData().iterator();ito.hasNext();)
					{
						SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
						sku=o.getSku_outer_id();
					
						StockManager.deleteWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(po.getTc_order_id()),sku);		
						StockManager.addSynReduceStore(jobname, dao.getConnection(), tradecontactid, po.getStatus(),String.valueOf(po.getTc_order_id()), sku, -o.getNum(),false);
					}
				}
			}
			
			//�ȴ���Ҹ���ʱ��¼�������
		}
		
		else if (po.getStatus().equals("WAIT_BUYER_PAY"))
		{						
			for(Iterator ito=po.getSub_purchase_orders().getRelationData().iterator();ito.hasNext();)
			{
				SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
				sku=o.getSku_outer_id();
				
			
			
				StockManager.addWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(po.getTc_order_id()), sku, o.getNum());
				
				StockManager.addSynReduceStore(jobname, dao.getConnection(), tradecontactid, po.getStatus(),String.valueOf(po.getTc_order_id()), sku, -o.getNum(),false);
				
			}
								
			//�����Ժ��û��˿�ɹ��������Զ��ر�
			//�ͷſ��,����Ϊ����
		} else if (po.getStatus().equals("TRADE_CLOSED"))
		{					
			OrderManager.CancelOrderByCID(jobname, dao.getConnection(), String.valueOf(po.getId()));
			for(Iterator ito=po.getSub_purchase_orders().getRelationData().iterator();ito.hasNext();)
			{
				SubPurchaseOrder o=(SubPurchaseOrder) ito.next();		
				sku=o.getSku_outer_id();
				StockManager.deleteWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(po.getTc_order_id()), sku);
				StockManager.addSynReduceStore(jobname, dao.getConnection(), tradecontactid, po.getStatus(),String.valueOf(po.getTc_order_id()),sku, o.getNum(),false);
			}
			//������ǰ�����һ���������رս���
			//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
		}else if (po.getStatus().equals("TRADE_FINISHED"))
		{
			for(Iterator ito=po.getSub_purchase_orders().getRelationData().iterator();ito.hasNext();)
			{
				SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
				sku=o.getSku_outer_id();
	
				StockManager.deleteWaitPayStock(jobname, dao.getConnection(),tradecontactid, String.valueOf(po.getTc_order_id()), sku);								
			}
		}
		
		//�����˻�
		/**
		for(Iterator ito=po.getSub_purchase_orders().getRelationData().iterator();ito.hasNext();)
		{
			SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
			sku=o.getSku_outer_id();
			
			if (o.getStatus().equals("TRADE_REFUNDED")||o.getStatus().equals("TRADE_REFUNDING"))
			{
				sql="select count(*) from eco_rds_fx_refund with(nolock) "
					+"where supplier_nick='"+sellernick+"' and sub_order_id='"+o.getFenxiao_id()+"'";
				
				if (dao.intSelect(sql)==0) continue;
				
				
				RefundDetail r=getFenXiaoRefund(dao,sellernick,o.getFenxiao_id());
				
				createFenXiaoRefund(dao,tradecontactid,po,o,r);
			}
		}
		**/
		

	}
	
	private static void createFenXiaoRefund(DataCentre dao,
			String tradecontactid, PurchaseOrder po, SubPurchaseOrder o,RefundDetail r)
			throws Exception {
		

			int hasGoodReturn=0;
			if (r.isIs_return_goods()) hasGoodReturn=1;
			
			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
					+ tradecontactid;
			String inshopid = dao.strSelect(sql);

			dao.setTransation(false);

			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			String sheetid = dao.strSelect(sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");

			// ���뵽֪ͨ��
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			dao.execute(sql);

			sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , "
					+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
					+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
					+ "Price , Num , TotalFee ,  OuterIid , OuterSkuId  , "
					+ " ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
					+"values("
					+"'"
					+sheetid
					+"','"
					+String.valueOf(o.getFenxiao_id())
					+"','"
					+String.valueOf(o.getTc_order_id())
					+"','"
					+po.getAlipay_no()
					+"','"
					+r.getDistributor_nick()
					+"','"
					+Formatter.format(r.getRefund_create_time(),Formatter.DATE_TIME_FORMAT)
					+"','"
					+Formatter.format(r.getModified(),Formatter.DATE_TIME_FORMAT)
					+"','"
					+po.getStatus()
					+"','"
					+r.getRefund_status()
					+"','"
					+r.getRefund_status()
					+"',"
					+hasGoodReturn
					+","
					+Double.valueOf(r.getRefund_fee())
					+","
					+Double.valueOf(r.getPay_sup_fee())
					+",'"
					+r.getRefund_reason()
					+"','"
					+StringUtil.replace(r.getRefund_desc(),"'"," ")
					+"','"
					+o.getTitle()
					+"',"
					+Double.valueOf(r.getRefund_fee())
					+","
					+o.getNum()
					+","
					+Double.valueOf(r.getRefund_fee())
					+",'"
					+o.getItem_outer_id()
					+"','"
					+o.getSku_outer_id()
					+"','"
					+po.getReceiverinfo().getState() + "" + po.getReceiverinfo().getCity() + ""+ po.getReceiverinfo().getDistrict() + ""+ po.getReceiverinfo().getAddress().replaceAll("'", " ")
					+"','"
					+inshopid
					+"','"
					+String.valueOf(po.getId())
					+"','"
					+po.getReceiverinfo().getName()
					+"','"
					+po.getReceiverinfo().getPhone() + " " + po.getReceiverinfo().getMobile_phone()
					+"','"
					+po.getAlipay_no()
					+"')";

			dao.execute(sql);
			
			Log.info( "�ӿڵ���:"
					+ sheetid
					+ " ������:"
					+ po.getId()
					+ " ����״̬��"
					+ po.getStatus()
					+ " �˿�״̬:"
					+ r.getRefund_status()
					+ " ��������ʱ��:"
					+ Formatter.format(po.getCreated(),
							Formatter.DATE_TIME_FORMAT)
					+ " �˻�����ʱ��:"
					+ Formatter.format(r.getRefund_create_time(),
							Formatter.DATE_TIME_FORMAT));

			dao.commit();
			dao.setTransation(true);

		
	}
	
	private static RefundDetail getFenXiaoRefund(DataCentre dao,String sellernick,long suborderid) throws Exception
	{
		RefundDetail r=new RefundDetail();
		
		String sql="select top 1 jdp_response from eco_rds_fx_refund with(nolock) "
			+"where supplier_nick='"+sellernick+"' and sub_order_id='"+suborderid+"' order by modified desc";
		
		String jdpresponse=dao.strSelect(sql);
	
		
		JSONObject jsonobj=new JSONObject(jdpresponse);
		
		JSONObject refunddetailjsobobj=jsonobj.getJSONObject("fenxiao_refund_get_response").getJSONObject("refund_detail");
		
		r.setObjValue(r, refunddetailjsobobj);
		
		return r;
	}
	
	public static void processRefund(String jobname,DataCentre dao, Refund r,
			String tradecontactid,String sellernick) throws Exception {
		
		Log.info(r.getTid()+" "+r.getStatus()+" "+Formatter.format(r.getModified(),Formatter.DATE_TIME_FORMAT));
		
		String sql="select count(*) from eco_rds_trade with(nolock) "
			+"where seller_nick='"+sellernick+"' and tid='"+r.getTid()+"'";
		
		if (dao.intSelect(sql)==0)
		{
			sql="update eco_rds_refund set flag=-1 where seller_nick='"+sellernick+"' and refund_id='"+r.getRefund_id()+"'";
			dao.execute(sql);
			
			return;
		}
		
		Order o=null;

		Trade td=getTrade(dao,sellernick,r.getTid());
		
		for(Iterator it=td.getOrders().getRelationData().iterator();it.hasNext();)
		{
			Order or=(Order) it.next();
						
			if (r.getOid()==or.getOid())
			{
				o=or;
				break;
			}
		}
		
		createRefund(dao,tradecontactid,td,o,r);
		
	}
	

	/**
	 * @param jobname
	 * @param dao
	 * @param r
	 * @param tradecontactid
	 * @param sellernick
	 * @throws Exception
	 */
	public static void processFenXiaoRefund(String jobname,DataCentre dao, RefundDetail r,
			String tradecontactid,String sellernick) throws Exception {
		
		
		Log.info(r.getSub_order_id()+" "+r.getRefund_status()+" "+Formatter.format(r.getRefund_create_time(),Formatter.DATE_TIME_FORMAT));
		
		String sql="select count(*) from ns_customerorder a with(nolock),ns_orderitem b with(nolock) "
			+"where a.sheetid=b.sheetid and a.sellernick='"+r.getSupplier_nick()+"' "
			+"and substring(b.orderitemid,charindex('_',b.orderitemid)+1,len(b.orderitemid)-charindex('_',b.orderitemid)) = '"+r.getSub_order_id()+"'";
		
		if (dao.intSelect(sql)==0)
		{

			sql="update eco_rds_fx_refund set flag=-1 where supplier_nick='"+sellernick+"' and sub_order_id='"+r.getSub_order_id()+"'";
			dao.execute(sql);
			
			return;
		}
		
		sql="select top 1 tid from ns_customerorder a with(nolock),ns_orderitem b with(nolock) "
			+"where a.sheetid=b.sheetid and a.sellernick='"+r.getSupplier_nick()+"' "
			+"and substring(b.orderitemid,charindex('_',b.orderitemid)+1,len(b.orderitemid)-charindex('_',b.orderitemid)) = '"+r.getSub_order_id()+"'";
		
		long fenxiao_id=Long.valueOf(dao.strSelect(sql)).longValue();
		
		SubPurchaseOrder o=null;
		
	
		PurchaseOrder po=getPurchaseOrder(dao,sellernick,fenxiao_id);
		
		if (null == po){ //�Ҳ�����Ӧ�Ķ����ӿ����ݣ����˳�������������
			Log.error(jobname,"�Ҳ�����Ӧ�Ķ����ӿ����ݣ�sub_order_id["+Long.toString(r.getSub_order_id()) 
					+"] fenxiao_id:["+Long.toString(fenxiao_id)+"]");
			sql="update eco_rds_fx_refund set flag=-1 where supplier_nick='"+sellernick+"' and sub_order_id='"+r.getSub_order_id()+"'";
			dao.execute(sql);			
			return ;
		}
		
		for(Iterator it=po.getSub_purchase_orders().getRelationData().iterator();it.hasNext();)
		{
			SubPurchaseOrder or=(SubPurchaseOrder) it.next();
						
			if (r.getSub_order_id()==or.getFenxiao_id())
			{
				o=or;
				break;
			}
		}
		
		createFenXiaoRefund(dao,tradecontactid,po,o,r);
		
	}
	
	public static void processFenXiaoRefundOne(String jobname,DataCentre dao, RefundDetail r,
			String tradecontactid,String sellernick,long fenxiao_id) throws Exception {
		
		Log.info(r.getSub_order_id()+" "+r.getRefund_status()+" "+Formatter.format(r.getRefund_create_time(),Formatter.DATE_TIME_FORMAT));
		SubPurchaseOrder o=null;
		PurchaseOrder po=getPurchaseOrder(dao,sellernick,fenxiao_id);
		
		if (null == po){ //�Ҳ�����Ӧ�Ķ����ӿ����ݣ����˳�������������
			Log.error(jobname,"�Ҳ�����Ӧ�Ķ����ӿ����ݣ�sub_order_id["+Long.toString(r.getSub_order_id()) 
					+"] fenxiao_id:["+Long.toString(fenxiao_id)+"]");
			String sql="update eco_rds_fx_refund set flag=-1 where supplier_nick='"+sellernick+"' and sub_order_id='"+r.getSub_order_id()+"'";
			dao.execute(sql);			
			return ;
		}
		
		for(Iterator it=po.getSub_purchase_orders().getRelationData().iterator();it.hasNext();)
		{
			SubPurchaseOrder or=(SubPurchaseOrder) it.next();
						
			if (r.getSub_order_id()==or.getFenxiao_id())
			{
				o=or;
				break;
			}
		}
		
		createFenXiaoRefund(dao,tradecontactid,po,o,r);
		
	}
	
	public static void processReturnBill(String jobname,DataCentre dao, NS_ReturnBill returnbill,
			String tradecontactid,String sellernick) throws Exception {
		
		Log.info(returnbill.getTid()+" "+returnbill.getStatus()+" "+Formatter.format(returnbill.getModified(),Formatter.DATE_TIME_FORMAT));
		
		String sql="select count(*) from eco_rds_trade with(nolock) "
			+"where seller_nick='"+sellernick+"' and tid='"+returnbill.getTid()+"'";
		
		if (dao.intSelect(sql)==0)
		{
			sql="update eco_rds_tm_return set flag=-1 where seller_nick='"+sellernick+"' and refund_id='"+returnbill.getRefund_id()+"'";
			dao.execute(sql);
			
			return;
		}
		
		Order o=null;

		Trade td=getTrade(dao,sellernick,Long.valueOf(returnbill.getTid()));
		
	
		createReturnBill(dao,tradecontactid,td,returnbill);
		
	}
	
	public static void processRefundBill(String jobname,DataCentre dao, NS_RefundBill refundbill,
			String tradecontactid,String sellernick) throws Exception {
		
		Log.info(refundbill.getTid()+" "+refundbill.getStatus()+" "+Formatter.format(refundbill.getModified(),Formatter.DATE_TIME_FORMAT));
		
		String sql="select count(*) from eco_rds_trade with(nolock) "
			+"where seller_nick='"+sellernick+"' and tid='"+refundbill.getTid()+"'";
		
		if (dao.intSelect(sql)==0)
		{
			sql="update eco_rds_tm_refund set flag=-1 where seller_nick='"+sellernick+"' and refund_id='"+refundbill.getRefund_id()+"'";
			dao.execute(sql);
			
			return;
		}
		
		Order o=null;

		Trade td=getTrade(dao,sellernick,Long.valueOf(refundbill.getTid()));
		
	
		createRefundBill(dao,tradecontactid,td,refundbill);
		
	}
	
	private static Trade getTrade(DataCentre dao,String sellernick,long tid) throws Exception
	{
		Trade td=new Trade();
		
		String sql="select top 1 jdp_response from eco_rds_trade with(nolock) "
			+"where seller_nick='"+sellernick+"' and tid="+tid+" order by modified desc";
		
		String jdpresponse=dao.strSelect(sql);
		
		JSONObject jsonobj=new JSONObject(jdpresponse);
		
		JSONObject tradejsobobj=jsonobj.getJSONObject("trade_fullinfo_get_response").getJSONObject("trade");
		
		td.setObjValue(td, tradejsobobj);
		
		JSONArray orders=tradejsobobj.getJSONObject("orders").getJSONArray("order");
		
		
		td.setFieldValue(td, "orders", orders);
		
		
		return td;
	}
	
	private static PurchaseOrder getPurchaseOrder(DataCentre dao,String sellernick,long fenxiao_id) throws Exception
	{
		PurchaseOrder po=new PurchaseOrder();
		
		String sql="select top 1 jdp_response from eco_rds_fx_trade with(nolock) "
			+"where supplier_username='"+sellernick+"' and fenxiao_id="+fenxiao_id+" order by modified desc";
		
		
		String jdpresponse=dao.strSelect(sql);
		
		if ((jdpresponse=="") || (null==jdpresponse)){
			return null;
		}
		

		JSONObject jsonobj=new JSONObject(jdpresponse);
		
		JSONObject purchaseorderjsonobj=jsonobj.getJSONObject("fenxiao_orders_get_response").getJSONObject("purchase_orders").getJSONArray("purchase_order").getJSONObject(0);
		
		po.setObjValue(po, purchaseorderjsonobj);
		
		JSONObject receiverjsonobj=purchaseorderjsonobj.getJSONObject("receiver");

		po.getReceiverinfo().setAddress(receiverjsonobj.getString("address").replaceAll("'", " "));
	
		po.getReceiverinfo().setState(receiverjsonobj.getString("state"));
		
		po.getReceiverinfo().setCity(receiverjsonobj.getString("city"));

		if (!receiverjsonobj.isNull("name"))
			po.getReceiverinfo().setName(receiverjsonobj.getString("name"));

		if (!receiverjsonobj.isNull("district"))
			po.getReceiverinfo().setDistrict(receiverjsonobj.getString("district"));	

		if (!receiverjsonobj.isNull("phone"))
			po.getReceiverinfo().setPhone(receiverjsonobj.getString("phone"));

		if (!receiverjsonobj.isNull("mobile_phone"))						
			po.getReceiverinfo().setMobile_phone(receiverjsonobj.getString("mobile_phone"));
		if (!receiverjsonobj.isNull("zip"))	
			po.getReceiverinfo().setZip(receiverjsonobj.getString("zip"));
		
		
		JSONArray suborders=purchaseorderjsonobj.getJSONObject("sub_purchase_orders").getJSONArray("sub_purchase_order");
				
		po.setFieldValue(po, "sub_purchase_orders", suborders);
		
		return po;
	}
	
	private static Refund getRefund(DataCentre dao,String sellernick,long refundid) throws Exception
	{
		Refund r=new Refund();
		
		String sql="select top 1 jdp_response from eco_rds_refund with(nolock) "
			+"where seller_nick='"+sellernick+"' and refund_id='"+refundid+"' order by modified desc";
		
		String jdpresponse=dao.strSelect(sql);
	
		
		JSONObject jsonobj=new JSONObject(jdpresponse);
		
		JSONObject refundjsobobj=jsonobj.getJSONObject("refund_get_response").getJSONObject("refund");
		
		r.setObjValue(r, refundjsobobj);
		
		return r;
	}
	
	private static void createRefund(DataCentre dao, String tradecontactid,Trade td,Order o,Refund r) 
		throws Exception
	{
		
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
				+ tradecontactid;
		String inshopid = dao.strSelect(sql);

		sql = "declare @Err int ; declare @NewSheetID char(16); "
				+ "execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
		String sheetid = dao.strSelect(sql);
	
		// ���뵽֪ͨ��
		sql = "insert into it_downnote(Owner,sheetid,sheettype,sender,receiver,notetime,handletime) "
				+ "values('yongjun','"
				+ sheetid
				+ "',2 , '"
				+ tradecontactid + "','yongjun', getdate(),null) ";
		dao.execute(sql);
	
		sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , "
				+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
				+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
				+ "Price , Num , GoodReturnTime , Sid , "
				+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName , "
				+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		String address="";
		String sid="";
		String companyname="";
		String outerskuid="";
		Date goodsreturntime=new Date();
		if (r.getAddress()!=null) address=r.getAddress().replaceAll("'", " ");
		if (r.getSid()!=null) sid=r.getSid();
		if (r.getCompany_name()!=null) companyname=r.getCompany_name();
		if (r.getGoods_return_time()!=null) goodsreturntime=r.getGoods_return_time();
		if (o.getOuter_sku_id()!=null)
			outerskuid=o.getOuter_sku_id();
		else
			outerskuid=o.getOuter_iid();
		
		Object[] sqlv = {
				sheetid,
				r.getRefund_id(),
				r.getOid(),
				r.getAlipay_no(),
				r.getBuyer_nick(),
				r.getCreated(),
				r.getModified(),
				r.getOrder_status(),
				r.getStatus(),
				r.getGoods_status(),
				Types.convertBooleanToShort(r.getHas_goods_return()),
				r.getRefund_fee(),
				r.getPayment(),
				r.getReason(),
				r.getDesc(),
				r.getTitle(),
				r.getPrice(),
				r.getNum(),
				goodsreturntime,
				sid,
				r.getTotal_fee(),
				o.getOuter_iid(),
				outerskuid,
				companyname,
				address,
				td.getReceiver_state() + " " + td.getReceiver_city() + " "
						+ td.getReceiver_district() + " "
						+ td.getReceiver_address().replaceAll("'", " "), inshopid, r.getTid(),
						td.getReceiver_name(),
						td.getReceiver_phone() + " " + td.getReceiver_mobile(),
						td.getBuyer_alipay_no() };
	
		dao.executePreparedSQL(sql, sqlv);
		
		
		Log.info("�ӿڵ���:"+ sheetid+ " ������:"+ r.getTid()+ " ����״̬��"
				+ r.getStatus()+ " �˿�״̬:"+ r.getStatus()+ " ��������ʱ��:"
				+ Formatter.format(td.getCreated(),
						Formatter.DATE_TIME_FORMAT));
		
	}
		

	private static void createReturnBill(DataCentre dao, String tradecontactid,Trade td,NS_ReturnBill returnbill) 
		throws Exception
	{
		
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
				+ tradecontactid;
		String inshopid = dao.strSelect(sql);
		
		sql = "declare @Err int ; declare @NewSheetID char(16); "
				+ "execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
		String sheetid = dao.strSelect(sql);
	
		// ���뵽֪ͨ��
		sql = "insert into it_downnote(Owner,sheetid,sheettype,sender,receiver,notetime,handletime) "
				+ "values('yongjun','"+ sheetid	+ "',11 , '"+ tradecontactid + "','yongjun', getdate(),null) ";
		dao.execute(sql);
		
		returnbill.setSheetid(sheetid);
		returnbill.setShopid(inshopid);
		returnbill.setBuyer_nick(td.getBuyer_nick());
		returnbill.setSeller_nick(td.getSeller_nick());
		returnbill.setLinkman(td.getReceiver_name());
		returnbill.setLinktele(td.getReceiver_phone());
		returnbill.setMobile(td.getReceiver_mobile());
		returnbill.setAddress(td.getReceiver_state()+" "+td.getReceiver_city()+" "+td.getReceiver_district()+" "+td.getReceiver_address());
		
		dao.insert(returnbill);
		
		for (int i=0;i<returnbill.getItem_list().size();i++)
		{
			NS_ReturnBillItem returnbillitem=(NS_ReturnBillItem) returnbill.getItem_list().getRelationData().get(i);
			returnbillitem.setSheetid(sheetid);
			returnbillitem.setPrice(returnbillitem.getPrice()/100);
			dao.insert(returnbillitem);
		}
	
		Log.info("�ӿڵ���:"+ sheetid+ " ������:"+ returnbill.getTid()+ " ����״̬��"
				+ td.getStatus()+ " �˻�״̬:"+ returnbill.getStatus()+ " ��������ʱ��:"
				+ Formatter.format(td.getCreated(),
						Formatter.DATE_TIME_FORMAT));
		
	}
	
	//����������
	public static void processJingXiaoOrder(String jobName,DataCentre dao,Dealer_order dealer_order,String tradecontactid,String sellernick) 
	     throws Exception
	{
		Log.info("jingxiaoId: "+dealer_order.getDealer_order_id()+"  status:��"+dealer_order.getOrder_status()+"  modified: "+dealer_order.getModified_time());
		String orderStatus=dealer_order.getOrder_status();
		if(orderStatus.equals("WAIT_FOR_SUPPLIER_DELIVER")){  //�ȴ�����  д�ӿ���
			if(!OrderManager.isCheck("����Ա���������",dao.getConnection(),String.valueOf(dealer_order.getDealer_order_id()))){
				if(!OrderManager.TidLastModifyIntfExists("����Ա���������", dao.getConnection(), String.valueOf(dealer_order.getDealer_order_id()),dealer_order.getModified_time())){
					createJingXiaoOrer(jobName,dao,dealer_order,tradecontactid);
				}
			}
			
			for(Iterator iter=dealer_order.getDealer_order_details().getRelationData().iterator();iter.hasNext();){
				Dealer_order_detail detail = (Dealer_order_detail)iter.next();
				String sku = String.valueOf(detail.getSku_number());
				int qty = detail.getQuantity();
				StockManager.deleteWaitPayStock(jobName, dao.getConnection(), tradecontactid, String.valueOf(dealer_order.getDealer_order_id()), sku);
				StockManager.addSynReduceStore(jobName, dao.getConnection(), tradecontactid, dealer_order.getOrder_status(), String.valueOf(dealer_order.getDealer_order_id()), sku, qty, false);
			}
		}
		
		else if(orderStatus.equals("BOTH_AGREE_WAIT_PAY")){   //�ȴ�����������
			
			for(Iterator iter=dealer_order.getDealer_order_details().getRelationData().iterator();iter.hasNext();){
				Dealer_order_detail detail = (Dealer_order_detail)iter.next();
				String sku = String.valueOf(detail.getSku_number());
				int qty = detail.getQuantity();
				StockManager.addWaitPayStock(jobName, dao.getConnection(), tradecontactid, String.valueOf(dealer_order.getDealer_order_id()), sku, qty);
				StockManager.addSynReduceStore(jobName, dao.getConnection(), tradecontactid, dealer_order.getOrder_status(), String.valueOf(dealer_order.getDealer_order_id()), sku, qty, false);
			}
			
			
		}else if(orderStatus.equals("TRADE_CLOSED")){        //���׹ر�  
			for(Iterator iter=dealer_order.getDealer_order_details().getRelationData().iterator();iter.hasNext();){
				Dealer_order_detail detail = (Dealer_order_detail)iter.next();
				String sku = String.valueOf(detail.getSku_number());
				int qty = detail.getQuantity();
				StockManager.deleteWaitPayStock(jobName, dao.getConnection(), tradecontactid, String.valueOf(dealer_order.getDealer_order_id()), sku);
			}
			
		}else if(orderStatus.equals("TRADE_FINISHED")){      //�������
			for(Iterator iter=dealer_order.getDealer_order_details().getRelationData().iterator();iter.hasNext();){
				Dealer_order_detail detail = (Dealer_order_detail)iter.next();
				String sku = String.valueOf(detail.getSku_number());
				StockManager.deleteWaitPayStock(jobName, dao.getConnection(), tradecontactid, String.valueOf(dealer_order.getDealer_order_id()), sku);
			}
		}
	}

	//��Ӿ�������
	private static void createJingXiaoOrer(String jobName,
			DataCentre dao, Dealer_order dealer_order,
			String tradecontactid) throws Exception{
		try{
			String sheetid="";
			dao.setTransation(false);
			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = dao.strSelect(sql);
			if (sheetid.trim().equals("")){
				throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");
			}
				
			//����֪ͨ��
			sql = new StringBuilder().append("insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','")
				.append(sheetid).append("',").append(1).append(",'").append(tradecontactid)
				.append("','yongjun','").append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT))
				.append("',null)").toString();
			dao.execute(sql);
			//����ӿ�����
			/**
			 * 
			 */
			//--��Ʒ���
			float total=0.0f;
			for(Iterator iter=dealer_order.getDealer_order_details().getRelationData().iterator();iter.hasNext();){
				Dealer_order_detail order_detail = (Dealer_order_detail)iter.next();
				total+=order_detail.getQuantity()*Float.parseFloat(order_detail.getFinal_price());
			}
			String totalFee = String.valueOf(total);
			sql = new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid  , sellernick , ")
				.append("type , created , buyermessage , shippingtype , payment , ")
				.append("discountfee , adjustfee , status , buyermemo , sellermemo , ")
				.append("tradememo , paytime , endtime , modified ,buyerobtainpointfee , ")
				.append("pointfee , realpointfee , totalfee , postfee , buyeralipayno , ")
				.append("buyernick , receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , ")
				.append("buyeremail , haspostFee , receivedpayment , ")
				.append(" alipayNo , buyerflag , sellerflag,brandsaleflag,")
				.append(" sellerrate , buyerrate , promotion , tradefrom , alipayurl , ")
				.append("PromotionDetails,tradeContactid) ")  //49
				.append("values('")
				.append(sheetid).append("','")
				.append(sheetid).append("','")
				.append("yongjun").append("','")
				.append(dealer_order.getDealer_order_id()).append("','")
				.append(dealer_order.getSupplier_nick()).append("','','")  //5
				.append(Formatter.format(dealer_order.getApplied_time(), Formatter.DATE_TIME_FORMAT))
				.append("','','").append(dealer_order.getLogistics_type()).append("','")  //8
				.append(dealer_order.getTotal_price()).append("','','','")
				.append(dealer_order.getOrder_status()).append("','','','','")
				.append(Formatter.format(dealer_order.getPay_time(),Formatter.DATE_TIME_FORMAT))
				.append("','").append(Formatter.format(dealer_order.getModified_time(),Formatter.DATE_TIME_FORMAT))
				.append("','").append(Formatter.format(dealer_order.getModified_time(),Formatter.DATE_TIME_FORMAT))
				.append("','','")  //buberobtainpointfee
				.append("','','").append(totalFee).append("','").append(dealer_order.getLogistics_fee())
				.append("','").append(dealer_order.getAlipay_no()).append("','")
				.append(dealer_order.getApplier_nick()).append("','")
				.append(dealer_order.getReceiverinfo().getName()).append("','")
				.append(dealer_order.getReceiverinfo().getState()).append("','")
				.append(dealer_order.getReceiverinfo().getCity()).append("','")
				.append(dealer_order.getReceiverinfo().getDistrict()).append("','")
				.append(dealer_order.getReceiverinfo().getAddress()).append("','")
				.append(dealer_order.getReceiverinfo().getZip()).append("','")
				.append(dealer_order.getReceiverinfo().getMobile_phone()).append("','")
				.append(dealer_order.getReceiverinfo().getPhone()!=null?dealer_order.getReceiverinfo().getPhone():"").append("','','','','','")
				.append(dealer_order.getAlipay_no()).append("','','','','','','','taobao','','','")
				.append(tradecontactid).append("')").toString();
			dao.execute(sql);
			//������ϸ��
			for(Iterator iter=dealer_order.getDealer_order_details().getRelationData().iterator();iter.hasNext();){
				Dealer_order_detail dealer_detail=(Dealer_order_detail)iter.next();
				sql = new StringBuilder().append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
						.append(" title , sellernick , buyernick , type , created , ")
						.append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")
						.append(" discountfee , adjustfee , status  ,")
						.append(" skuPropertiesName , num , price , picPath , ")
						.append(" oid , snapShotUrl , snapShot , buyerRate ,sellerRate ,refundId,")
						.append("  numiid , cid , isoversold) values('")
						.append(sheetid).append("','")
						.append(sheetid).append("_").append(dealer_detail.getDealer_detail_id()).append("','")
						.append(sheetid).append("','")
						.append(dealer_detail.getSku_id()).append("','','")
						.append(dealer_detail.getProduct_title()).append("','")
						.append(dealer_order.getSupplier_nick()).append("','")
						.append(dealer_order.getApplier_nick()).append("','','")
						.append(Formatter.format(dealer_order.getApplied_time(), Formatter.DATE_TIME_FORMAT)).append("','','")
						.append(dealer_detail.getSku_id()).append("','")
						.append(dealer_detail.getSku_number()).append("','")
						.append(dealer_detail.getPrice_count()).append("','")
						.append(dealer_detail.getPrice_count()).append("','','','")
						.append(dealer_order.getOrder_status()).append("','")
						.append(dealer_detail.getSku_spec()!=null?dealer_detail.getSku_spec():"").append("',")
						.append(dealer_detail.getQuantity()).append(",'")
						.append(dealer_detail.getFinal_price()).append("','','")
						.append(dealer_detail.getDealer_detail_id()).append("','")
						.append(dealer_detail.getSnapshot_url()).append("','','','','0','")
						.append(dealer_detail.getProduct_id()).append("','','')").toString();
				dao.execute(sql);
						
			}
			dao.commit();
			dao.setTransation(true);
			
			Log.info("���ɾ���������" + dealer_order.getDealer_order_id() + "���ӿ����ݳɹ����ӿڵ��š�"
					+ sheetid + "��");
				
		}catch(Exception ex){
			try{
				if(!dao.getConnection().getAutoCommit())
				dao.rollback();
			}catch(Exception e){
				Log.error(jobName, e.getMessage());
			}
			try{
				dao.setTransation(true);
			}catch(Exception el){
				Log.error(jobName, ex.getMessage());
			}
			throw new Exception("���ɾ����������󣬶���id��"+dealer_order.getDealer_order_id()+"\r\n������ϸ��Ϣ: "+ex.getMessage());
			
			
			
		}
		
	}

	private static void createRefundBill(DataCentre dao, String tradecontactid,Trade td,NS_RefundBill refundbill) 
		throws Exception
	{
		
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
				+ tradecontactid;
		String inshopid = dao.strSelect(sql);
		
		sql = "declare @Err int ; declare @NewSheetID char(16); "
				+ "execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
		String sheetid = dao.strSelect(sql);
	
		// ���뵽֪ͨ��
		sql = "insert into it_downnote(Owner,sheetid,sheettype,sender,receiver,notetime,handletime) "
				+ "values('yongjun','"+ sheetid	+ "',10 , '"+ tradecontactid + "','yongjun', getdate(),null) ";
		dao.execute(sql);
		
		refundbill.setSheetid(sheetid);
		refundbill.setShopid(inshopid);
		refundbill.setRefund_fee(refundbill.getRefund_fee()/100);
		refundbill.setActual_refund_fee(refundbill.getActual_refund_fee()/100);
		refundbill.setLinkman(td.getReceiver_name());
		refundbill.setLinktele(td.getReceiver_phone());
		refundbill.setMobile(td.getReceiver_mobile());
		refundbill.setAddress(td.getReceiver_state()+" "+td.getReceiver_city()+" "+td.getReceiver_district()+" "+td.getReceiver_address()!=null?td.getReceiver_address().replaceAll("'", " "):"");
		
		dao.insert(refundbill);
		
		for (int i=0;i<refundbill.getItem_list().size();i++)
		{
			NS_RefundBillItem refundbillitem=(NS_RefundBillItem) refundbill.getItem_list().getRelationData().get(i);
			refundbillitem.setSheetid(sheetid);
			refundbillitem.setPrice(refundbillitem.getPrice()/100);
			dao.insert(refundbillitem);
		}
	
		Log.info("�ӿڵ���:"+ sheetid+ " ������:"+ refundbill.getTid()+ " ����״̬��"
				+ td.getStatus()+ " �˿�״̬:"+ refundbill.getStatus()+ " ��������ʱ��:"
				+ Formatter.format(td.getCreated(),
						Formatter.DATE_TIME_FORMAT));
		
	}	

}
