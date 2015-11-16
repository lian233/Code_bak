package com.wofu.intf.best;
/**
 * ���۳��ⵥ-����Ҫд��wms_outstock0 wms_outstockitem0 it_upnote����
 * �������ͣ�
   NORMAL-��ͨ����/���׶���  �����Ƿ�����ͨ�û���
   WDO-���ⵥ/�ǽ��׶���     �˹�Ӧ�̳��ⵥ-�ѻ����˵���Ӧ����   �������ⵥ  �ѻ�����������Ĳֿ�

 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class UpdateSalesOrderStatusProcessor extends BizProcessor {

	@Override
	public void process() throws Exception {
	
		
		Document outStockStatusDoc = DOMHelper.newDocument(this.getBizData(), "GBK");

		Element outStockStatusele = outStockStatusDoc.getDocumentElement();	
		
		String customerCode=DOMHelper.getSubElementVauleByName(outStockStatusele, "customerCode");
		
		String warehouseCode=DOMHelper.getSubElementVauleByName(outStockStatusele, "warehouseCode");
		String orderStatus=DOMHelper.getSubElementVauleByName(outStockStatusele, "orderStatus");
		String orderCode=DOMHelper.getSubElementVauleByName(outStockStatusele, "orderCode");
		String orderType=DOMHelper.getSubElementVauleByName(outStockStatusele, "orderType");
		String extOrderType=DOMHelper.getSubElementVauleByName(outStockStatusele, "extOrderType");
		
		long allTime=0;
		Connection extconn=null;
		try
		{
			
			//�����ⲿ���ݿ�����--��Ӧ�����ֿ�
			extconn=PoolHelper.getInstance().getConnection(
				BestUtil.getDSName(this.getConnection(), customerCode,warehouseCode));
			this.setExtconnection(extconn);
			if (orderType.equalsIgnoreCase("NORMAL")&&(
						orderStatus.equalsIgnoreCase("DELIVERED")
					||orderStatus.equalsIgnoreCase("CLOSED")
					||orderStatus.equalsIgnoreCase("CANCELED")
					||orderStatus.equalsIgnoreCase("WMS_ACCEPT")
					||orderStatus.equalsIgnoreCase("WMS_REJECT")
				))   //��ͨ����/��������
			{   allTime=System.currentTimeMillis();
				processSaleOrderStatus(outStockStatusele);	
				System.out.println("ִ����ͨ������ʱ�� : "+(System.currentTimeMillis()-allTime)/1000f+" �� ");
			}
			else if (orderType.equalsIgnoreCase("WDO")&&(
						orderStatus.equalsIgnoreCase("DELIVERED")
					||orderStatus.equalsIgnoreCase("CLOSED")
					||orderStatus.equalsIgnoreCase("CANCELED")
					||orderStatus.equalsIgnoreCase("WMS_ACCEPT")
					||orderStatus.equalsIgnoreCase("WMS_REJECT")
				))  		
				{  
					//�˹�Ӧ�̳��ⵥ  �ѻ����˸���Ӧ��
					if (extOrderType.equals("2322"))
						processReturnStatus(outStockStatusele);
					else if (extOrderType.equals("2341"))  //�������ⵥ  �ѻ���ת�������Ĳֿ�
						processTransferOutStatus(outStockStatusele);
					
					else if(extOrderType.equals("2209"))  //��ͨ���ⵥ�Ĵ�����һ��
					{	allTime=System.currentTimeMillis();
						processSaleOrderStatus(outStockStatusele);
					System.out.println("ִ�г��ⵥ�ǽ��׵���ʱ�� : "+(System.currentTimeMillis()-allTime)/1000f+" �� ");
					}
				}

			String operator="";
			String operatortime=Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT);
			
			if(orderStatus.equalsIgnoreCase("WMS_ACCEPT"))
			{
				operator="best";
			}
			else
			{
				operator=DOMHelper.getSubElementVauleByName(outStockStatusele, "operator");
				operatortime=DOMHelper.getSubElementVauleByName(outStockStatusele, "operatorTime");
			}
			allTime=System.currentTimeMillis();
			//select count(*) from ecs_bestlogisticsinfo with(nolock) where ordercode='"+orderCode+"' "
			//+"and orderstatus='"+orderStatus+"' and orderType='"+orderType+"'
			String sql="select count(*) from ecs_bestlogisticsinfo with(nolock) where orderstatus='"+orderStatus+"' "
						+"and ordercode='"+orderCode+"' and orderType='"+orderType+"'";//���ﴦ��ʱ��̫��
			if (SQLHelper.intSelect(this.getExtconnection(), sql)==0)
			{   System.out.println("����һ�������ѵ�ʱ��: "+(System.currentTimeMillis()-allTime)/1000f+" �� ");
				sql="insert into ecs_bestlogisticsinfo(ordercode,orderstatus,ordertype,operator,operatortime) "
					+"values('"+orderCode+"','"+orderStatus+"','"+orderType+"','"+operator+"','"+operatortime+"')";
				SQLHelper.executeSQL(this.getExtconnection(), sql);
			}
		}catch(Exception e)
		{
			try
			{
				if (extconn!=null && !extconn.getAutoCommit())
				{
					try
					{
						extconn.rollback();
					}
					catch (Exception rollbackexception) 
					{ 
						Log.error("best logistics","�ع�����ʧ��:"+rollbackexception.getMessage());
					}
					try
					{
						extconn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("best logistics","�����Զ��ύ����ʧ��:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("best logistics","�����Զ��ύ����ʧ��:"+sqle.getMessage());
			}
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception closeexception) {
				Log.error("best logistics", "�ر����ݿ�����ʧ��:"+closeexception.getMessage());
			}
			//---����
			throw new JException("������ⵥʧ��,���ⵥ��:"+orderCode+",״̬:"+orderStatus+" ������Ϣ"+e.getMessage());
		
		}
		finally {			
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception e) {
				Log.error("best logistics", "�ر����ݿ�����ʧ��:"+e.getMessage());
			}
		}
		
	}
	
	//transfertype=2209
	private void processSaleOrderStatus(Element updatesaleorderstatusele) throws Exception
	{
		String sql="";
		long time =0;
		long methodTime =0;
		time=System.currentTimeMillis();
		methodTime=System.currentTimeMillis();
		String customerCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "customerCode").trim();
		
		String warehouseCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "warehouseCode").trim();
		
		String orderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderCode");

		String orderStatus=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderStatus");
		if (orderStatus.equalsIgnoreCase("DELIVERED"))		//�ѷ���  �����Ѿ������ͻ���
		{
			//2015_9_14ע�����ԣ�Ϊ�����ִ���ٶ�
			sql = new StringBuilder().append("select COUNT(*) from (select 1 aa from wms_outstock0 (nolock) where refsheetid='")
			.append(orderCode).append("' and transfertype=2209 and flag=100 union select 1 aa from wms_outstock (nolock) where refsheetid='")
			.append(orderCode).append("' and transfertype=2209 and flag=100) a").toString();
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			String logisticsProviderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "logisticsProviderCode");
			logisticsProviderCode= "ZJS-COD".equalsIgnoreCase(logisticsProviderCode)?"ZJS":("JD-COD".equalsIgnoreCase(logisticsProviderCode)?"JDKD":logisticsProviderCode);

			String shippingOrderNo=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "shippingOrderNo");
			double weight=0.00;
			if (DOMHelper.ElementIsExists(updatesaleorderstatusele, "weight")){
				weight=Double.valueOf(DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "weight")).doubleValue()*1000;
				if(weight>10000) weight=10000;
			}
			System.out.println("�������ݺ�ʱ : "+(System.currentTimeMillis()-time)/1000f+" �� ");				
			/**
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			**/
			this.getExtconnection().setAutoCommit(false);
			time=System.currentTimeMillis();
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
			System.out.println("���һ���µ�sheetidִ�к�ʱ : "+(System.currentTimeMillis()-time)/1000f+" �� ");
			//�����ݴ�customerdelive0��д������ֵ�wms_outstock0��	
			time=System.currentTimeMillis();
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
				+"linktele,linkman,delivery,deliverysheetid,zipcode,detailid,weigh)"
				+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+this.getVertifycode()+"',"
				+"outshopid,inshopid,purday,2209,100,'best',getdate(),'�ӿ�','best',getdate(),"
				+"notes,address,linktele,linkman,'"+logisticsProviderCode+"','"+shippingOrderNo+"',"
				+"zipcode,detailid,"+weight+" from customerdelive0 (nolock) "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			System.out.println("��customerdelive0д��wms_outstock0ִ�к�ʱ : "+(System.currentTimeMillis()-time)/1000f+" �� ");
			//��barcode����������wms_outstockitem��ϸ
			time=System.currentTimeMillis();
			getDeliveryDetail(commsheetid,updatesaleorderstatusele,this.getIsBarcodeId());
			System.out.println("������������ϸִ�к�ʱ : "+(System.currentTimeMillis()-time)/1000f+" �� ");
			//д�����
//			getOutPackageItem(orderCode,updatesaleorderstatusele);
			//д�������it_upnote��--�ɴ洢���̴���  sheetidΪwms_outstock0���sheetid it_upnote
			time=System.currentTimeMillis();
			IntfUtils.upNote(this.getExtconnection(),this.getVertifycode(), commsheetid, 2209, this.getInterfaceSystem(), this.getWarehouseMulti()?BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			System.out.println("д��it_upnoteִ�к�ʱ : "+(System.currentTimeMillis()-time)/1000f+" �� ");
			//updateStockFlag(orderCode,"100");
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			System.out.println("ִ�г����ʱ�� : "+(System.currentTimeMillis()-methodTime)/1000f+" �� ");
			
		}
		//����ȡ��
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//ȡ�����ر�
		{
			/**
			sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) {
				Log.info("�����Ѿ�����wms_outstock0������: "+orderCode);
				return;
			}
			sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) {
				Log.info("�����Ѿ�����wms_outstock������: "+orderCode);
				return;
			}**/
			
			sql = new StringBuilder().append("select COUNT(*) from (select 1 aa from wms_outstock0 (nolock) where refsheetid='")
			.append(orderCode).append("' and transfertype=2209 and flag=97 union select 1 aa from wms_outstock (nolock) where refsheetid='")
			.append(orderCode).append("' and transfertype=2209 and flag=97) a").toString();
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			/**
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			**/
			this.getExtconnection().setAutoCommit(false);
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
			//��customerdelive0��д���ݵ�wms_outstock0��		
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
				+"linktele,linkman,zipcode,detailid)"
				+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+this.getVertifycode()+"',"
				+"outshopid,inshopid,purday,2209,97,'best',getdate(),'�ӿ�','best',getdate(),"
				+"notes,address,linktele,linkman,zipcode,detailid from customerdelive0 (nolock) "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			//��customerdeliveitem0��д���ݵ�wms_outstockitem0��
			getCancelDeliveryDetail(commsheetid,orderCode);
			processPartRefund(orderCode);
			IntfUtils.upNote(this.getExtconnection(), this.getVertifycode(), commsheetid, 2209, this.getInterfaceSystem(), this.getWarehouseMulti()?BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			//updateStockFlag(orderCode,"97");
		}
		else if(orderStatus.equalsIgnoreCase("CANCELEDFAIL")){
			String notes = DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "note");
			Log.info("notes: "+notes);
			    //"SO_CODE_INVALID||��������ⵥ������[577R0L1410201208]��������" 
			if(("SO_CODE_INVALID||��������ⵥ������["+orderCode+"]�������� ").equals(notes)){  //����û�н��յĶ���ֱ��ȡ��
				sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) {
					Log.info("�����Ѿ�����wms_outstock0������: "+orderCode);
					return;
				}
				sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) {
					Log.info("�����Ѿ�����wms_outstock������: "+orderCode);
					return;
				}
				/**
				sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
				String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
				**/
				this.getExtconnection().setAutoCommit(false);
				sql="declare @Err int ; declare @NewSheetID char(16); "
					+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
				String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
				//��customerdelive0��д���ݵ�wms_outstock0��		
				sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
					+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
					+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
					+"linktele,linkman,zipcode,detailid)"
					+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+this.getVertifycode()+"',"
					+"outshopid,inshopid,purday,2209,97,'best',getdate(),'�ӿ�','best',getdate(),"
					+"notes,address,linktele,linkman,zipcode,detailid from customerdelive0 (nolock) "
					+" where sheetid='"+orderCode+"'";
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				//��customerdeliveitem0��д���ݵ�wms_outstockitem0��
				getCancelDeliveryDetail(commsheetid,orderCode);
				
				//processPartRefund(orderCode);
				
				IntfUtils.upNote(this.getExtconnection(), this.getVertifycode(), commsheetid, 2209, this.getInterfaceSystem(), this.getWarehouseMulti()?BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
				this.getExtconnection().commit();
				this.getExtconnection().setAutoCommit(true);
			}
		}
		/**
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�
			BestUtil.updateMsg(this.getExtconnection(), orderCode, 2209, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
			BestUtil.updateMsg(this.getExtconnection(), orderCode, 2209, -1);
			**/
		
		
		Log.info("best logistics","ȡ������״̬�ɹ�,����:"+orderCode+" ״̬:"+orderStatus);
	}
	


	//���벿���˻���������Ʒ   �Ȳ鵽������tid,�ٸ���tid��ѯ�˻��ӿڱ�������Ƿ�����Ӧ�����ݣ�����У���ѯ��������Ƿ��Ѿ����ϵ����ֱ���
	private void processPartRefund(String orderCode) throws Exception
	{	
		//�����˻������Ŀͻ�������  tid
		String sql="select refsheetid,customersheetid from customerdelive0 with(nolock) where sheetid='"+orderCode+"'";
		Hashtable result =SQLHelper.oneRowSelect(this.getExtconnection(), sql);
		if(result.size()==0) return ;
		String tid = result.get("customersheetid").toString();
		
		sql="select count(*) from ns_refund with(nolock) where tid='"+tid+"'";
		//��������������˻��ӿڱ�ļ�¼
		if(SQLHelper.intSelect(this.getConnection(), sql)>0)   //������˻�
		{
			String sheetid=result.get("refsheetid").toString();
			/**
			sql="select count(*) from customerorderreflist with(nolock)  where refsheetid='"+tid+"'";
			
			String sheetid="";
			
			if(SQLHelper.intSelect(this.getConnection(), sql)>0)  //������ϲ���
			{
				sql="select sheetid from customerorderreflist with(nolock)  where refsheetid='"+tid+"'";
				
				sheetid=SQLHelper.strSelect(this.getConnection(), sql);

			}
			else
			{	
				sql="select sheetid from customerorder with(nolock) where refsheetid='"+tid+"'";
			
				sheetid=SQLHelper.strSelect(this.getConnection(), sql);

			}
			**/
			
			//��ѯ��������˻�����Ʒ��ϸ  �������Ӧ���˻���ϸ��Ʒ����������Ӧ��customerorder0 customerorderitem0�����ݣ����µĶ���
			//customerorderitem.paypresentid�ǿյ�ʱ��Ϊ��Ʒ ����Ҫ�����µĶ���
			sql="select count(*) from customerorderitem with(nolock) "
				+"where sheetid='"+sheetid+"' "
				+" and oid not in(select oid from ns_refund with(nolock) "
				+ "where tid='"+tid+"' or tid in(select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"')) and paypresentid is null";
			
			if (SQLHelper.intSelect(this.getConnection(), sql)>0) //������ڲ����˻�����������������Ʒ�Ķ���
			{
				sql="select outshopid from customerorder with(nolock)  where sheetid='"+sheetid+"'";
				//���Ҷ�����Ӧ�Ĳֿ�
				String outshopid=SQLHelper.strSelect(this.getConnection(), sql);
				
				//�����µĵ��ݱ��
				sql="declare @Err int ; declare @newsheetid char(16); "
					+"execute  @Err = TL_GetNewMSheetID 2209, '"+outshopid+"' , '020V01' , @newsheetid output;select @newsheetid;";			
				String newsheetid=SQLHelper.strSelect(this.getConnection(), sql);
				
				
				
				sql="if object_id('tempdb..#tmp_order') is not null  drop table #tmp_order;";
				
				SQLHelper.executeSQL(this.getConnection(), sql);
				//��customerorder���������¼д�����ʱ��
				sql="select * into #tmp_order from customerorder (nolock) where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				
				sql="update #tmp_order set sheetid='"+newsheetid+"',flag=0,notes=notes+'�ӿ��Զ������˻�����'";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				sql="insert into customerorder0 select * from #tmp_order";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				
				sql="if object_id('tempdb..#tmp_orderitem') is not null  drop table #tmp_orderitem;";
				
				sql="select * into #tmp_orderitem from customerorderitem with(nolock) "
					+"where sheetid='"+sheetid+"' "
					+" and oid not in(select oid from ns_refund with(nolock) "
					+ "where tid='"+tid+"' or tid in(select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"'))  and paypresentid is null";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				
				sql="update #tmp_orderitem set sheetid='"+newsheetid+"'";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				sql="insert into customerorderitem0 select * from #tmp_orderitem";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				sql="if object_id('tempdb..#tmp_order') is not null  drop table #tmp_order;";
				SQLHelper.executeSQL(this.getConnection(), sql);
				sql="if object_id('tempdb..#tmp_orderitem') is not null  drop table #tmp_orderitem;";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				
			}
			
			
		}
		
	}
	
	//������������ϸ
	
	private void getDeliveryDetail(String commsheetid,Element salesorderele,Boolean isBarcodeId) throws Exception
	{
		Element produectsele=(Element) salesorderele.getElementsByTagName("products").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("product");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "skuCode"); 
			String normalQuantity=DOMHelper.getSubElementVauleByName(produectele, "normalQuantity");
					
			/*String sql="insert into wms_outstockitem0(sheetid,customermid,"
				+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
				+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
				+","+normalQuantity+","+normalQuantity+",pknum,pkname,pkspec "
				+"from barcode where custombc='"+skuCode+"'";*/
			
			String sql = new StringBuilder().append("insert into wms_outstockitem0(sheetid,customermid,")
				.append("barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) ")
				.append("select '").append(commsheetid).append("',goodsid,barcodeid,1,0,")
				.append(normalQuantity).append(",").append(normalQuantity)
				.append(",pknum,pkname,pkspec from barcode (nolock) where ")
				.append(isBarcodeId?"barcodeid='":"custombc='").append(skuCode).append("'").toString();
			
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		}
	}
	
	//��ó���������
	private void getOutPackageItem(String orderCode, Element updatesaleorderstatusele) throws Exception  {
		Element packages=(Element) updatesaleorderstatusele.getElementsByTagName("packages").item(0);
		System.out.println("���ⵥ��Ϊ "+orderCode);
		if(packages!=null){
			NodeList packageList=packages.getElementsByTagName("package");
			System.out.println("package����Ϊ"+packageList.getLength());
			for (int i=0;i<packageList.getLength();i++){
				
				Element packageele=(Element) packageList.item(i);//package�б����
				String packCode=DOMHelper.getSubElementVauleByName(packageele, "packCode");  //��ȡpackCodeװ���
				System.out.println("װ���Ϊ "+packCode);
				
				Element packProducts=(Element) packageele.getElementsByTagName("packProducts").item(0);//���� ����ȡ�����packProducts
				NodeList packProductlist=packProducts.getElementsByTagName("packProduct");//��ȡpackProducts�б�
				for (int j=0;j<packProductlist.getLength();j++)
				{
					System.out.println("packProduct��ϸ��������Ϊ "+packProductlist.getLength());
					
					Element packProduct=(Element) packProductlist.item(j);//packProduct�б����
					String packSkuCode=DOMHelper.getSubElementVauleByName(packProduct, "packSkuCode");//��ȡSKU
					System.out.println("SKU "+packSkuCode);
					
					String sql = "select barcodeid from barcode where CUSTOMBC='"+packSkuCode+"'";//��SKU��ѯ����Ʒ����
					String barcodeid = SQLHelper.strSelect(this.getConnection(), sql);//��Ʒ����
					System.out.println("barcodeid "+barcodeid);
					
					String packQuantity=DOMHelper.getSubElementVauleByName(packProduct, "packQuantity");//��ȡ����
					System.out.println("���� "+packQuantity);
					sql = "insert into OutPackageItem (sheetid,barcodeid,packcode,qty) " +
							"values('"+orderCode+"','"+barcodeid+"','"+packCode+"','"+packQuantity+"')";
					SQLHelper.executeSQL(this.getExtconnection(), sql);
				}
			}
		}
	}
	private void getCancelDeliveryDetail(String commsheetid,String ordercode) throws Exception
	{
		
		String sql="insert into wms_outstockitem0(sheetid,customermid,"
			+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
			+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
			+",purqty,outqty,pknum,pkname,pkspec "
			+"from customerdeliveitem0 (nolock) where sheetid='"+ordercode+"'";
		
		SQLHelper.executeSQL(this.getExtconnection(), sql);		
	}
	
	/*
	private void updateStockFlag(String sheetid,String flag) throws Exception
	{
		String sql="TL_SetSheetStockFlag (19,'"+sheetid+"',"+flag+",'','')";			
		SQLHelper.executeProc(this.getExtconnection(), sql);
	}
	*/
	
	//��Ӧ���˻�  transfertype=2322
	private void processReturnStatus(Element updatesaleorderstatusele) throws Exception
	{
		String sql="";
		String customerCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "customerCode");
		String warehouseCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "warehouseCode");
		String orderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderCode");
		String orderStatus=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderStatus");
		
		if (orderStatus.equalsIgnoreCase("DELIVERED"))		//�ѷ���
		{	
			sql="select count(*) from wms_outstock0 (nolock) where refsheetid='"+orderCode+"' and transfertype=2322 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			sql="select count(*) from wms_outstock (nolock) where refsheetid='"+orderCode+"' and transfertype=2322 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			this.getExtconnection().setAutoCommit(false);
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
								
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"owner,outid,inid,transfertype,flag,purday,"
				+"notifyOper,notifydate,operator,checker,checkdate,note)"
				+"select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"shopid,venderid,2322,100,7,'best',getdate(),'�ӿ�','best',getdate(),"
				+"notes from ret0 (nolock) "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele,this.getIsBarcodeId());
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2322, this.getInterfaceSystem(), this.getWarehouseMulti()?BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//ȡ�����ر�
		{
			sql="select count(*) from wms_outstock0 (nolock) where refsheetid='"+orderCode+"' and transfertype=2322 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_outstock (nolock) where refsheetid='"+orderCode+"' and transfertype=2322 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			this.getExtconnection().setAutoCommit(false);
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
								
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"owner,outid,inid,transfertype,flag,purday,"
				+"notifyOper,notifydate,operator,checker,checkdate,note)"
				+"select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"shopid,venderid,2322,97,7,'best',getdate(),'�ӿ�','best',getdate(),"
				+"notes from ret0 (nolock) "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			
			getReturnDetail(commsheetid,orderCode);
					
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2322, this.getInterfaceSystem(), this.getWarehouseMulti()?BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�
			BestUtil.updateMsg(this.getExtconnection(), orderCode, 2322, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
			BestUtil.updateMsg(this.getExtconnection(), orderCode, 2322, -1);
		
		
		Log.info("best logistics","ȡ��Ӧ���˻���״̬�ɹ�,����:"+orderCode+" ״̬:"+orderStatus);
	}
	
	
	private void getReturnDetail(String commsheetid,String ordercode) throws Exception
	{
		
		String sql="insert into wms_outstockitem0(sheetid,customermid,"
			+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
			+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
			+",planqty,planqty,pknum,pkname,pkspec "
			+"from retitem0 (nolock) where sheetid='"+ordercode+"'";
		
		SQLHelper.executeSQL(this.getExtconnection(), sql);		
	}
	//��������   transfertype=2341
	private void processTransferOutStatus(Element updatesaleorderstatusele) throws Exception
	{
		String sql="";
		
		String customerCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "customerCode");
		String warehouseCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "warehouseCode");
		String orderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderCode");
		String orderStatus=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderStatus");
		
		if (orderStatus.equalsIgnoreCase("DELIVERED"))		//�ѷ���
		{
			
			sql="select count(*) from wms_outstock0 (nolock) where pursheetid='"+orderCode+"' and transfertype=2341 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_outstock (nolock) where pursheetid='"+orderCode+"' and transfertype=2341 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			this.getExtconnection().setAutoCommit(false);
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
								
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"owner,outid,inid,transfertype,flag,purday,"
				+"notifyOper,notifydate,operator,checker,checkdate,note)"
				+"select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"outshopid,inshopid,2341,100,7,'best',getdate(),'�ӿ�','best',getdate(),"
				+"note from transfer0 (nolock) "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele,this.getIsBarcodeId());
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2341, this.getInterfaceSystem(), this.getWarehouseMulti()?BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//ȡ�����ر�
		{	Log.info("eeee");
			sql="select count(*) from wms_outstock0 (nolock) where pursheetid='"+orderCode+"' and transfertype=2341 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_outstock (nolock) where pursheetid='"+orderCode+"' and transfertype=2341 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			this.getExtconnection().setAutoCommit(false);
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"owner,outid,inid,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,PurDay)"
				+" select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"outshopid,inshopid,2341,97,'best',getdate(),'�ӿ�','best',getdate(),"
				+"note,7 from transfer0 (nolock) "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			getTranferOutDetail(commsheetid,orderCode);
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2341, this.getInterfaceSystem(), this.getWarehouseMulti()?BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�
			BestUtil.updateMsg(this.getExtconnection(), orderCode, 2341, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
			BestUtil.updateMsg(this.getExtconnection(), orderCode, 2341, -1);
		
		Log.info("best logistics","ȡ�������ⵥ״̬�ɹ�,����:"+orderCode+" ״̬:"+orderStatus);
	}
	
	private void getTranferOutDetail(String commsheetid,String ordercode) throws Exception
	{
		
		String sql="insert into wms_outstockitem0(sheetid,customermid,"
			+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
			+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
			+",outqty,outqty,pknum,pkname,pkspec "
			+"from transferitem0 (nolock) where sheetid='"+ordercode+"'";
		
		SQLHelper.executeSQL(this.getExtconnection(), sql);		
	}
	
	//�������ⵥ��ϸ
	
	private void getOutStockDetail(String commsheetid,Element salesorderele,Boolean isBarcodeid) throws Exception
	{
		Element produectsele=(Element) salesorderele.getElementsByTagName("products").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("product");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "skuCode"); 
			String normalQuantity=DOMHelper.getSubElementVauleByName(produectele, "normalQuantity");
					
			/*String sql="insert into wms_outstockitem0(sheetid,customermid,"
				+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
				+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
				+","+normalQuantity+","+normalQuantity+",pknum,pkname,pkspec "
				+"from barcode where custombc='"+skuCode+"'";*/
			String sql = new StringBuilder().append("insert into wms_outstockitem0(sheetid,customermid,")
				.append("barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) ")
				.append("select '").append(commsheetid).append("',goodsid,barcodeid,1,0,")
				.append(normalQuantity).append(",").append(normalQuantity).append(",pknum,pkname,pkspec ")
				.append("from barcode (nolock) where ").append(isBarcodeid?"barcodeid='":"custombc='")
				.append(skuCode).append("'").toString();
			
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		}
	}
	


}
