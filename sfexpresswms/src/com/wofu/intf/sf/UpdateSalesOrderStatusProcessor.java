package com.wofu.intf.sf;
/**
 * ���۳��ⵥ-����Ҫд��wms_outstock0 wms_outstockitem0 it_upnote����
 * �������ͣ�
   NORMAL-��ͨ����/���׶���  �����Ƿ�����ͨ�û���
   WDO-���ⵥ/�ǽ��׶���     �˹�Ӧ�̳��ⵥ-�ѻ����˵���Ӧ����   �������ⵥ  �ѻ�����������Ĳֿ�

 */
import java.sql.Connection;
import java.sql.SQLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class UpdateSalesOrderStatusProcessor extends BizProcessor {

	@Override
	public void process() throws Exception {
		String[] sfData =this.getBizData().split(";") ;
		Document outStockStatusDoc = DOMHelper.newDocument(sfData[1], "GBK");

		Element outStockStatusele = outStockStatusDoc.getDocumentElement();	
		Element header = DOMHelper.getSubElementsByName(outStockStatusele, "header")[0];
		//���ұ���
		String customerCode=this.getCustomerCode();
		Log.info("customerCode: "+customerCode);
		String warehouseCode=DOMHelper.getSubElementVauleByName(header, "warehouse");
		Log.info("warehouseCode: "+warehouseCode);
		String orderStatus=DOMHelper.getSubElementVauleByName(header, "status_code");
		String orderCode=DOMHelper.getSubElementVauleByName(header, "erp_order");
		
		Connection extconn=null;
		try
		{
			//�����ⲿ���ݿ�����--��Ӧ�����ֿ�
			extconn=PoolHelper.getInstance().getConnection(
				sfUtil.getDSName(this.getConnection(), customerCode,warehouseCode));
			extconn.setAutoCommit(false);
			this.setExtconnection(extconn);
			  //��ͨ����/��������
			processSaleOrderStatus(outStockStatusele);		
			
	
			String operator="sf";
			if(DOMHelper.ElementIsExists(outStockStatusele, "user_stamp"))
			operator=DOMHelper.getSubElementVauleByName(outStockStatusele, "user_stamp");
			String operatortime=DOMHelper.getSubElementVauleByName(outStockStatusele, "status_time");
			
			String sql="select count(*) from ecs_bestlogisticsinfo with(nolock) where ordercode='"+orderCode+"' "
						+"and orderstatus='"+orderStatus+"'";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)==0)
			{
				sql="insert into ecs_bestlogisticsinfo(ordercode,orderstatus,operator,operatortime,ordertype) "
					+"values('"+orderCode+"','"+orderStatus+"','"+operator+"','"+operatortime+"','sf')";
				SQLHelper.executeSQL(this.getExtconnection(), sql);
			}
			
			extconn.commit();
			extconn.setAutoCommit(true);
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
						Log.error("sf logistics","�ع�����ʧ��:"+rollbackexception.getMessage());
					}
					try
					{
						extconn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("sf logistics","�����Զ��ύ����ʧ��:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("sf logistics","�����Զ��ύ����ʧ��:"+sqle.getMessage());
			}
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception closeexception) {
				Log.error("sf logistics", "�ر����ݿ�����ʧ��:"+closeexception.getMessage());
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
				Log.error("sf logistics", "�ر����ݿ�����ʧ��:"+e.getMessage());
			}
		}
		
	}
	
	//transfertype=2209
	private void processSaleOrderStatus(Element updatesaleorderstatusele) throws Exception
	{
		String sql="";
		Element header = DOMHelper.getSubElementsByName(updatesaleorderstatusele, "header")[0];
		String warehouseCode=DOMHelper.getSubElementVauleByName(header, "warehouse").trim();
		
		String orderCode=DOMHelper.getSubElementVauleByName(header, "erp_order");

		String orderStatus=DOMHelper.getSubElementVauleByName(header, "status_code");
		
		if (orderStatus.equalsIgnoreCase("900"))		//�ѷ���  �����Ѿ������ͻ���
		{
			sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			String logisticsProviderCode=DOMHelper.getSubElementVauleByName(header, "carrier");

			String shippingOrderNo=DOMHelper.getSubElementVauleByName(header, "waybill_no");
			Log.info("test---");
			float weight=0.00f;
			if (DOMHelper.ElementIsExists(updatesaleorderstatusele, "containerList")){
				Element containerList = DOMHelper.getSubElementsByName(updatesaleorderstatusele, "containerList")[0];
				NodeList items = containerList.getElementsByTagName("item");
				Log.info("item's length: "+items.getLength());
				for(int i=0;i<items.getLength();i++){
					Element item = (Element)items.item(i);
					if(item.getChildNodes().getLength()==1) continue;
					if(!DOMHelper.ElementIsExists(item, "item")) weight+=Float.parseFloat(DOMHelper.getSubElementVauleByName(item, "weight"));
				}
				if(weight>10000) weight=10000;
			}
			Log.info("weight: "+weight);
				
			
			
			/**
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			**/
			
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
			Log.info("tst33");
			//�����ݴ�customerdelive0��д������ֵ�wms_outstock0��					
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
				+"linktele,linkman,delivery,deliverysheetid,zipcode,detailid,weigh)"
				+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+this.getVertifycode()+"',"
				+"outshopid,inshopid,purday,2209,100,'sf',getdate(),'�ӿ�','sf',getdate(),"
				+"notes,address,linktele,linkman,'"+logisticsProviderCode+"','"+shippingOrderNo+"',"
				+"zipcode,detailid,"+weight+" from customerdelive0 "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			Log.info("tst44");
			getDeliveryDetail(commsheetid,updatesaleorderstatusele);
			//
			Log.info("tst11");
			IntfUtils.upNote(this.getExtconnection(),this.getVertifycode(), commsheetid, 2209, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),this.getCustomerCode(),warehouseCode));
			Log.info("tst22");
			//updateStockFlag(orderCode,"100");
			
		}
		//����ȡ��
		else if (orderStatus.equalsIgnoreCase("10013") || orderStatus.equalsIgnoreCase("10012"))		//ȡ�����ر�
		{
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

			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);

			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
			//		
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
				+"linktele,linkman,zipcode,detailid)"
				+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+owner+"',"
				+"outshopid,inshopid,purday,2209,97,'sf',getdate(),'�ӿ�','sf',getdate(),"
				+"notes,address,linktele,linkman,zipcode,detailid from customerdelive0 "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			//
			getCancelDeliveryDetail(commsheetid,orderCode);
			
			processPartRefund(orderCode);
			
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2209, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),this.getCustomerCode(),warehouseCode));
			
			//updateStockFlag(orderCode,"97");
		}
		else if(orderStatus.equalsIgnoreCase("10011")){
			Log.info("����: "+orderCode+",�Ѿ�������,��Ҫ���·���������ϵ˳��ִ�!");
		}
		else if (orderStatus.equalsIgnoreCase("10001") || orderStatus.equalsIgnoreCase("10003")|| orderStatus.equalsIgnoreCase("400")|| orderStatus.equalsIgnoreCase("700")|| orderStatus.equalsIgnoreCase("300"))  //�ӵ��ɹ�
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2209, 1);
		
		
		Log.info("sf logistics","ȡ������״̬�ɹ�,����:"+orderCode+" ״̬:"+orderStatus);
	}
	
	//���벿���˻���������Ʒ   �Ȳ鵽������tid,�ٸ���tid��ѯ�˻��ӿڱ�������Ƿ�����Ӧ�����ݣ�����У���ѯ��������Ƿ��Ѿ����ϵ����ֱ���
	private void processPartRefund(String orderCode) throws Exception
	{	
		//�����˻������Ŀͻ�������  tid
		String sql="select customersheetid from customerdelive0 with(nolock) where sheetid='"+orderCode+"'";
		
		String tid=SQLHelper.strSelect(this.getExtconnection(), sql);
		
		sql="select count(*) from ns_refund with(nolock) where tid='"+tid+"'";
		//��������������˻��ӿڱ�ļ�¼
		if(SQLHelper.intSelect(this.getConnection(), sql)>0)   //������˻�
		{
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
			//��ѯ��������˻�����Ʒ��ϸ  �������Ӧ���˻���ϸ��Ʒ����������Ӧ��customerorder0 customerorderitem0�����ݣ����µĶ���
			sql="select count(*) from customerorderitem with(nolock) "
				+"where sheetid='"+sheetid+"' "
				+" and oid not in(select oid from ns_refund with(nolock) "
				+ "where tid='"+tid+"' or tid in(select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"'))";
			
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
				sql="select * into #tmp_order from customerorder where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				
				sql="update #tmp_order set sheetid='"+newsheetid+"',flag=0,notes=notes+'�����˻�����'";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				sql="insert into customerorder0 select * from #tmp_order";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				
				sql="if object_id('tempdb..#tmp_orderitem') is not null  drop table #tmp_orderitem;";
				
				sql="select * into #tmp_orderitem from customerorderitem with(nolock) "
					+"where sheetid='"+sheetid+"' "
					+" and oid not in(select oid from ns_refund with(nolock) "
					+ "where tid='"+tid+"' or tid in(select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"'))";
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
	
	private void getDeliveryDetail(String commsheetid,Element salesorderele) throws Exception
	{
		Element produectsele=(Element) salesorderele.getElementsByTagName("detailList").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("item");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			if(produectele.getChildNodes().getLength()==1) continue;
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "item"); 
			String normalQuantity=DOMHelper.getSubElementVauleByName(produectele, "quantity");
					
			String sql = new StringBuilder().append("insert into wms_outstockitem0(sheetid,customermid,")
				.append("barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) ")
				.append("select '").append(commsheetid).append("',goodsid,barcodeid,1,0,")
				.append(normalQuantity).append(",").append(normalQuantity)
				.append(",pknum,pkname,pkspec from barcode where ")
				.append("custombc='").append(skuCode).append("'").toString();
			
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		}
	}
	
	private void getCancelDeliveryDetail(String commsheetid,String ordercode) throws Exception
	{
		
		String sql="insert into wms_outstockitem0(sheetid,customermid,"
			+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
			+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
			+",purqty,outqty,pknum,pkname,pkspec "
			+"from customerdeliveitem0 where sheetid='"+ordercode+"'";
		
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
			sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2322 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2322 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
								
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"owner,outid,inid,transfertype,flag,purday,"
				+"notifyOper,notifydate,operator,checker,checkdate,note)"
				+"select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"shopid,venderid,2322,100,7,'best',getdate(),'�ӿ�','best',getdate(),"
				+"notes from ret0 "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele);
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2322, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//ȡ�����ر�
		{
			sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2322 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2322 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
								
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"owner,outid,inid,transfertype,flag,purday,"
				+"notifyOper,notifydate,operator,checker,checkdate,note)"
				+"select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"shopid,venderid,2322,97,7,'best',getdate(),'�ӿ�','best',getdate(),"
				+"notes from ret0 "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			
			getReturnDetail(commsheetid,orderCode);
					
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2322, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2322, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2322, -1);
		
		
		Log.info("best logistics","ȡ��Ӧ���˻���״̬�ɹ�,����:"+orderCode+" ״̬:"+orderStatus);
	}
	
	
	private void getReturnDetail(String commsheetid,String ordercode) throws Exception
	{
		
		String sql="insert into wms_outstockitem0(sheetid,customermid,"
			+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
			+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
			+",planqty,planqty,pknum,pkname,pkspec "
			+"from retitem0 where sheetid='"+ordercode+"'";
		
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
			
			sql="select count(*) from wms_outstock0 where pursheetid='"+orderCode+"' and transfertype=2341 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_outstock where pursheetid='"+orderCode+"' and transfertype=2341 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
								
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"owner,outid,inid,transfertype,flag,purday,"
				+"notifyOper,notifydate,operator,checker,checkdate,note)"
				+"select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"outshopid,inshopid,2341,100,7,'best',getdate(),'�ӿ�','best',getdate(),"
				+"note from transfer0 "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele);
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2341, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//ȡ�����ر�
		{	Log.info("eeee");
			sql="select count(*) from wms_outstock0 where pursheetid='"+orderCode+"' and transfertype=2341 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_outstock where pursheetid='"+orderCode+"' and transfertype=2341 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
			Log.info("eeee1");					
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"owner,outid,inid,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,PurDay)"
				+" select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"outshopid,inshopid,2341,97,'best',getdate(),'�ӿ�','best',getdate(),"
				+"note,7 from transfer0 "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			getTranferOutDetail(commsheetid,orderCode);
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2341, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2341, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2341, -1);
		
		
		Log.info("best logistics","ȡ�������ⵥ״̬�ɹ�,����:"+orderCode+" ״̬:"+orderStatus);
	}
	
	private void getTranferOutDetail(String commsheetid,String ordercode) throws Exception
	{
		
		String sql="insert into wms_outstockitem0(sheetid,customermid,"
			+"barcodeid,badflag,price,notifyqty,outqty,pknum,pkname,pkspec) "
			+" select '"+commsheetid+"',goodsid,barcodeid,1,0"
			+",outqty,outqty,pknum,pkname,pkspec "
			+"from transferitem0 where sheetid='"+ordercode+"'";
		
		SQLHelper.executeSQL(this.getExtconnection(), sql);		
	}
	
	//�������ⵥ��ϸ
	
	private void getOutStockDetail(String commsheetid,Element salesorderele) throws Exception
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
				.append("from barcode where ").append("custombc='")
				.append(skuCode).append("'").toString();
			
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		}
	}
	


}
