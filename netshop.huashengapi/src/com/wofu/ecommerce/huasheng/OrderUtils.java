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
	//����״̬�б�
	private static String[][] OrderStatusList = new String[][]{
		{"0","δ֧������"},
		{"1","�Ѹ����"}
	};
	
	//���ʽ�б�
	private static String[][] PayWayList = new String[][]{
		{"0","��������"},
		{"1","����֧��"}
	};
	
	//����״̬�б�
	private static String[][] DeliverStatusList = new String[][]{
		{"0","δ����"},
		{"1","�ѷ���"}
	};
	
	
	/**
	 * ��ȡ����״̬
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
	 * ��ȡ���ʽ
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
	 * ����״̬״̬
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
	
	//���ɽӿڶ���
	@SuppressWarnings("unchecked")
	public static String createInterOrder(Connection conn,Order o,String tradeContactID,String username) throws Exception
	{
		try 
		{
			//��������
			conn.setAutoCommit(false);		
			//ȡ�ӿڵ���
			String sheetid="";
			String sql="declare @Err int ; declare @NewSheetID char(16); "+
				"execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid=SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
			
			
			//������Ʒ���
			double totalfee = 0.0;
			for(Iterator ito=o.getDetail().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item = (OrderItem) ito.next();
				totalfee += item.getPrice();
			}
			
			//д�뵽�ӿڶ�����
			sql  =  "insert into ns_customerorder(CustomerOrderId,Sheetid,Owner,tid,sellernick," +
					"created,buyermessage,payment,status,paytime,modified," +
					"totalfee,postfee,buyernick,receivername,receiverstate,receivercity,receiverdistrict," +
					"receiveraddress,receivermobile,receiverzip,delivery,deliverySheetID,TradeContactID," +
					"paymode,tradefrom,CertType,CertNo,CertName) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
			Object[] sqlv = {
					sheetid,sheetid,	//�ӿڵ���
					"yongjun",			//��������
					o.getOrder_id(),	//������
					username,			//��������
					Formatter.format(o.getCtime(), Formatter.DATE_TIME_FORMAT),	//��������ʱ��
					o.getComment(),		//�������
					o.getTotal_price(),	//ʵ�����
					OrderUtils.getOrderStateByCode(o.getStatus()) + "[" + OrderUtils.getPayWayByCode(o.getPay_id()) + "]",	//����״̬
					Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT),	//����ʱ��
					Formatter.format(o.getMtime(), Formatter.DATE_TIME_FORMAT),		//��������޸�ʱ��
					totalfee,			//��Ʒ���
					o.getExpress_price(),	//�ʷ�
					o.getBuyer_nickname(),	//����ǳ�
					o.getName(),		//�ջ�������
					o.getProvince(),	//�ջ�������ʡ��
					o.getCity(),		//�ջ������ڳ���
					o.getDistrict(),	//�ջ������ڳ�������
					o.getAddress(),		//�ջ��˵�ַ
					o.getPhone(),		//�ջ����ƶ��绰
					o.getPostcode(),	//�ջ����ʱ�
					o.getExpress_company(),	//������˾
					o.getExpress_id(),		//��������
					tradeContactID,		//������������ID
					(o.getPay_id().equals("0") ? 2 : 1),	//����ģʽ 1:����֧��  2:��������
					"huasheng",			//ƽ̨
					"1",				//֤������ 01:���֤��02:���ա�03:����
					o.getBuyer_card(),	//֤������
					o.getBuyer_truename()	//ʵ��
			};
			Log.info("����д��ns_customerorder��");
			SQLHelper.executePreparedSQL(conn, sql, sqlv);
			
			
			//д�뵽�ӿڶ�����ϸ��
			int j=0;
			for(Iterator ito=o.getDetail().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				sql  =  "insert into ns_orderitem(CustomerOrderId,orderItemId,sheetid,skuid," +
						"title,sellernick,buyernick,created,outeriid,totalfee,payment," +
						"status,owner,skuPropertiesName,num,price,modified) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				//�Ӷ�����
				String orderItemId = sheetid + "-" + o.getOrder_id() + String.valueOf(++j);
				
				Object[] sqlvItem = {
						sheetid,				//�ӿڵ���
						orderItemId,			//�Ӷ�����
						sheetid,				//�ӿڵ���
						item.getSku(),			//sku
						item.getTitle(),		//��Ʒ����
						username,				//��������
						o.getBuyer_nickname(),	//����ǳ�
						Formatter.format(o.getCtime(), Formatter.DATE_TIME_FORMAT),	//����ʱ��
						item.getMid(),			//��Ʒ�����ڲ�����
						item.getPrice(),		//Ӧ�����
						item.getPrice(),		//�Ӷ���ʵ�����
						OrderUtils.getOrderStateByCode(o.getStatus()) + "[" + OrderUtils.getPayWayByCode(o.getPay_id()) + "]",	//����״̬
						"yongjun",				//��������
						item.getProp(),			//SKU��ֵ
						item.getNum(),			//��������
						item.getGoodsprice(),	//����
						Formatter.format(o.getMtime(), Formatter.DATE_TIME_FORMAT),			//��������޸�ʱ��
				};
				Log.info("����д��ns_orderitem��:" + orderItemId);    
				SQLHelper.executePreparedSQL(conn, sql, sqlvItem);
			}
			
			//���뵽֪ͨ��
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "+
        		  "values('yongjun','" + sheetid + "',1 , '" + tradeContactID + "' , 'yongjun' , getdate() , null) ";
            Log.info("����д��֪ͨ��");
            SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrder_id() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");

			return sheetid;
			
		} catch (JSQLException e1) {
			e1.printStackTrace();
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
					Log.info("����:" + o.getOrder_id() + " ���ɶ����ӿڲ����ع��ɹ�!");
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			throw new JException("���ɶ�����" + o.getOrder_id() + "���ӿ�����ʧ��!"
					+ e1.getMessage());
		}
	}
}
