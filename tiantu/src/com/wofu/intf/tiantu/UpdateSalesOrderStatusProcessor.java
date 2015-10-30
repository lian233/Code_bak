package com.wofu.intf.tiantu;
/**
 * 销售出库单-数据要写到wms_outstock0 wms_outstockitem0 it_upnote表中
 * 订单类型：
   NORMAL-普通订单/交易订单  货物是发到普通用户的
   WDO-出库单/非交易订单     退供应商出库单-把货物退到供应商中   调拨出库单  把货物调到其它的仓库

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
	
		Document outStockStatusDoc = DOMHelper.newDocument(this.getBizData().substring(1), "GBK");

		Element outStockStatusele = outStockStatusDoc.getDocumentElement();	
		
		String customerCode=DOMHelper.getSubElementVauleByName(outStockStatusele, "customerCode");
		
		String warehouseCode=DOMHelper.getSubElementVauleByName(outStockStatusele, "warehouseCode");
		String orderStatus=DOMHelper.getSubElementVauleByName(outStockStatusele, "orderStatus");
		String orderCode=DOMHelper.getSubElementVauleByName(outStockStatusele, "orderCode");
		String orderType=DOMHelper.getSubElementVauleByName(outStockStatusele, "orderType");
		String extOrderType=DOMHelper.getSubElementVauleByName(outStockStatusele, "extOrderType");
		
		Connection extconn=null;
		try
		{
			//设置外部数据库连接--对应百世仓库
			extconn=PoolHelper.getInstance().getConnection(
				TianTuUtil.getDSName(this.getConnection(), customerCode,warehouseCode));
			this.setExtconnection(extconn);
			if (orderType.equalsIgnoreCase("NORMAL")&&(
						orderStatus.equalsIgnoreCase("DELIVERED")
					||orderStatus.equalsIgnoreCase("CLOSED")
					||orderStatus.equalsIgnoreCase("CANCELED")
					||orderStatus.equalsIgnoreCase("WMS_ACCEPT")
					||orderStatus.equalsIgnoreCase("WMS_REJECT")
				))   //普通出库/出货订单
				processSaleOrderStatus(outStockStatusele);		
			else if (orderType.equalsIgnoreCase("WDO")&&(
						orderStatus.equalsIgnoreCase("DELIVERED")
					||orderStatus.equalsIgnoreCase("CLOSED")
					||orderStatus.equalsIgnoreCase("CANCELED")
					||orderStatus.equalsIgnoreCase("WMS_ACCEPT")
					||orderStatus.equalsIgnoreCase("WMS_REJECT")
				))  		
				{
					//退供应商出库单  把货物退给供应商
					if (extOrderType.equals("2322"))
						processReturnStatus(outStockStatusele);
					else if (extOrderType.equals("2341"))  //调拨出库单  把货物转给其它的仓库
						processTransferOutStatus(outStockStatusele);
					else if(extOrderType.equals("2209"))  //普通出库单的处理方法一样
						processSaleOrderStatus(outStockStatusele);
				}
			else if (orderType.equalsIgnoreCase("TRAN")&&(
					orderStatus.equalsIgnoreCase("DELIVERED")
				||orderStatus.equalsIgnoreCase("CLOSED")
				||orderStatus.equalsIgnoreCase("CANCELED")
				||orderStatus.equalsIgnoreCase("WMS_ACCEPT")
				||orderStatus.equalsIgnoreCase("WMS_REJECT")
			))
			{
				if (extOrderType.equals("2341"))  //调拨出库单  把货物转给其它的仓库
					processTransferOutStatus2(outStockStatusele);
				
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
			
			String sql="select count(*) from ecs_bestlogisticsinfo with(nolock) where ordercode='"+orderCode+"' "
						+"and orderstatus='"+orderStatus+"' and orderType='"+orderType+"'";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)==0)
			{
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
						Log.error("tiantu","回滚事务失败:"+rollbackexception.getMessage());
					}
					try
					{
						extconn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("tiantu","设置自动提交事务失败:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("tiantu","设置自动提交事务失败:"+sqle.getMessage());
			}
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception closeexception) {
				Log.error("tiantu", "关闭数据库连接失败:"+closeexception.getMessage());
			}
			//---测试
			throw new JException("处理出库单失败,出库单号:"+orderCode+",状态:"+orderStatus+" 错误信息"+e.getMessage());
		
		}
		finally {			
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception e) {
				Log.error("tiantu", "关闭数据库连接失败:"+e.getMessage());
			}
		}
		
	}
	
	//transfertype=2209
	private void processSaleOrderStatus(Element updatesaleorderstatusele) throws Exception
	{
		String sql="";
		
		String customerCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "customerCode").trim();
		
		String warehouseCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "warehouseCode").trim();
		
		String orderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderCode");

		String orderStatus=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderStatus");
		System.out.println("yyy");
		if (orderStatus.equalsIgnoreCase("DELIVERED"))		//已发货  货物已经发到客户了
		{
			sql = new StringBuilder().append("select COUNT(*) from (select 1 aa from wms_outstock0 (nolock) where refsheetid='")
			.append(orderCode).append("' and transfertype=2209 and flag=100 union select 1 aa from wms_outstock (nolock) where refsheetid='")
			.append(orderCode).append("' and transfertype=2209 and flag=100) a").toString();
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			String logisticsProviderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "logisticsProviderCode").trim();
			logisticsProviderCode= "ZJS-COD".equalsIgnoreCase(logisticsProviderCode)?"ZJS":("JD-COD".equalsIgnoreCase(logisticsProviderCode)?"JDKD":logisticsProviderCode);

			String shippingOrderNo=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "shippingOrderNo");
			double weight=0.00;
			if (DOMHelper.ElementIsExists(updatesaleorderstatusele, "weight")){
				weight=Double.valueOf(DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "weight")).doubleValue()*1000;
				if(weight>10000) weight=10000;
			}
				
			/**
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			**/
			this.getExtconnection().setAutoCommit(false);
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
			//把数据从customerdelive0表写入百世仓的wms_outstock0表					
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
				+"linktele,linkman,delivery,deliverysheetid,zipcode,detailid,weigh)"
				+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+this.getVertifycode()+"',"
				+"outshopid,inshopid,purday,2209,100,'tiantu',getdate(),'接口','tiantu',getdate(),"
				+"notes,address,linktele,linkman,'"+logisticsProviderCode+"','"+shippingOrderNo+"',"
				+"zipcode,detailid,"+weight+" from customerdelive0 (nolock) "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			//从barcode表创建百世仓wms_outstockitem明细
			getDeliveryDetail(commsheetid,updatesaleorderstatusele,this.getIsBarcodeId());
			//写入百世仓it_upnote表--由存储过程处理  sheetid为wms_outstock0表的sheetid
			IntfUtils.upNote(this.getExtconnection(),this.getVertifycode(), commsheetid, 2209, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			//updateStockFlag(orderCode,"100");
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			
		}
		//订单取消
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//取消、关闭
		{
			/**
			sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) {
				Log.info("单据已经存在wms_outstock0表，单号: "+orderCode);
				return;
			}
			sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) {
				Log.info("单据已经存在wms_outstock表，单号: "+orderCode);
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
			//从customerdelive0表写数据到wms_outstock0表		
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
				+"linktele,linkman,zipcode,detailid)"
				+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+this.getVertifycode()+"',"
				+"outshopid,inshopid,purday,2209,97,'tiantu',getdate(),'接口','tiantu',getdate(),"
				+"notes,address,linktele,linkman,zipcode,detailid from customerdelive0 (nolock) "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			//从customerdeliveitem0表写数据到wms_outstockitem0表
			getCancelDeliveryDetail(commsheetid,orderCode);
			processPartRefund(orderCode);
			IntfUtils.upNote(this.getExtconnection(), this.getVertifycode(), commsheetid, 2209, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			//updateStockFlag(orderCode,"97");
		}
		else if(orderStatus.equalsIgnoreCase("CANCELEDFAIL")){
			String notes = DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "note");
			Log.info("notes: "+notes);
			    //"SO_CODE_INVALID||订单或出库单（编码[577R0L1410201208]）不存在" 
			if(("SO_CODE_INVALID||订单或出库单（编码["+orderCode+"]）不存在 ").equals(notes)){  //百世没有接收的订单直接取消
				sql="select count(*) from wms_outstock0 where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) {
					Log.info("单据已经存在wms_outstock0表，单号: "+orderCode);
					return;
				}
				sql="select count(*) from wms_outstock where refsheetid='"+orderCode+"' and transfertype=2209 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) {
					Log.info("单据已经存在wms_outstock表，单号: "+orderCode);
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
				//从customerdelive0表写数据到wms_outstock0表		
				sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
					+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
					+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
					+"linktele,linkman,zipcode,detailid)"
					+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+this.getVertifycode()+"',"
					+"outshopid,inshopid,purday,2209,97,'tiantu',getdate(),'接口','tiantu',getdate(),"
					+"notes,address,linktele,linkman,zipcode,detailid from customerdelive0 (nolock) "
					+" where sheetid='"+orderCode+"'";
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				//从customerdeliveitem0表写数据到wms_outstockitem0表
				getCancelDeliveryDetail(commsheetid,orderCode);
				
				//processPartRefund(orderCode);
				
				IntfUtils.upNote(this.getExtconnection(), this.getVertifycode(), commsheetid, 2209, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
				this.getExtconnection().commit();
				this.getExtconnection().setAutoCommit(true);
			}
		}
		/**
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //接单成功
			BestUtil.updateMsg(this.getExtconnection(), orderCode, 2209, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
			BestUtil.updateMsg(this.getExtconnection(), orderCode, 2209, -1);
			**/
		
		
		Log.info("tiantu","取发货单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
	}
	
	//重入部分退货的其他商品   先查到订单的tid,再根据tid查询退货接口表的数据是否有相应的数据，如果有，查询这个订单是否已经被合单，分别处理
	private void processPartRefund(String orderCode) throws Exception
	{	
		//查找退货订单的客户订单号  tid
		String sql="select refsheetid,customersheetid from customerdelive0 with(nolock) where sheetid='"+orderCode+"'";
		Hashtable result =SQLHelper.oneRowSelect(this.getExtconnection(), sql);
		if(result.size()==0) return ;
		String tid = result.get("customersheetid").toString();
		
		sql="select count(*) from ns_refund with(nolock) where tid='"+tid+"'";
		//查找这个订单在退货接口表的记录
		if(SQLHelper.intSelect(this.getConnection(), sql)>0)   //如果有退货
		{
			String sheetid=result.get("refsheetid").toString();
			/**
			sql="select count(*) from customerorderreflist with(nolock)  where refsheetid='"+tid+"'";
			
			String sheetid="";
			
			if(SQLHelper.intSelect(this.getConnection(), sql)>0)  //如果被合并掉
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
			
			//查询这个订单退货的商品明细  如果有相应的退货明细商品，则生成相应的customerorder0 customerorderitem0表数据，即新的订单
			//customerorderitem.paypresentid非空的时候为赠品 ，不要生成新的订单
			sql="select count(*) from customerorderitem with(nolock) "
				+"where sheetid='"+sheetid+"' "
				+" and oid not in(select oid from ns_refund with(nolock) "
				+ "where tid='"+tid+"' or tid in(select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"')) and paypresentid is null";
			
			if (SQLHelper.intSelect(this.getConnection(), sql)>0) //如果存在部分退货，重新生成其他商品的订单
			{
				sql="select outshopid from customerorder with(nolock)  where sheetid='"+sheetid+"'";
				//查找订单对应的仓库
				String outshopid=SQLHelper.strSelect(this.getConnection(), sql);
				
				//生成新的单据编号
				sql="declare @Err int ; declare @newsheetid char(16); "
					+"execute  @Err = TL_GetNewMSheetID 2209, '"+outshopid+"' , '020V01' , @newsheetid output;select @newsheetid;";			
				String newsheetid=SQLHelper.strSelect(this.getConnection(), sql);
				
				
				
				sql="if object_id('tempdb..#tmp_order') is not null  drop table #tmp_order;";
				
				SQLHelper.executeSQL(this.getConnection(), sql);
				//把customerorder表的这条记录写入表临时表
				sql="select * into #tmp_order from customerorder (nolock) where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				
				sql="update #tmp_order set sheetid='"+newsheetid+"',flag=0,notes=notes+'接口自动部分退货重入'";
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
	
	//创建发货单明细
	
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
	
	//供应商退货  transfertype=2322
	private void processReturnStatus(Element updatesaleorderstatusele) throws Exception
	{
		String sql="";
		String customerCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "customerCode");
		String warehouseCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "warehouseCode");
		String orderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderCode");
		String orderStatus=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderStatus");
		
		if (orderStatus.equalsIgnoreCase("DELIVERED"))		//已发货
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
				+"shopid,venderid,2322,100,7,'tiantu',getdate(),'接口','tiantu',getdate(),"
				+"notes from ret0 (nolock) "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele,this.getIsBarcodeId());
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2322, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//取消、关闭
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
				+"shopid,venderid,2322,97,7,'tiantu',getdate(),'接口','tiantu',getdate(),"
				+"notes from ret0 (nolock) "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			
			getReturnDetail(commsheetid,orderCode);
					
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2322, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //接单成功
			TianTuUtil.updateMsg(this.getExtconnection(), orderCode, 2322, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
			TianTuUtil.updateMsg(this.getExtconnection(), orderCode, 2322, -1);
		
		
		Log.info("tiantu","取供应商退货单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
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
	//调拨出库   transfertype=2341
	private void processTransferOutStatus(Element updatesaleorderstatusele) throws Exception
	{
		String sql="";
		
		String customerCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "customerCode");
		String warehouseCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "warehouseCode");
		String orderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderCode");
		String orderStatus=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderStatus");
		
		if (orderStatus.equalsIgnoreCase("DELIVERED"))		//已发货
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
				+"outshopid,inshopid,2341,100,7,'tiantu',getdate(),'接口','tiantu',getdate(),"
				+"note from transfer0 (nolock) "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele,this.getIsBarcodeId());
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2341, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//取消、关闭
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
				+"outshopid,inshopid,2341,97,'tiantu',getdate(),'接口','tiantu',getdate(),"
				+"note,7 from transfer0 (nolock) "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			getTranferOutDetail(commsheetid,orderCode);
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2341, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //接单成功
			TianTuUtil.updateMsg(this.getExtconnection(), orderCode, 2341, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
			TianTuUtil.updateMsg(this.getExtconnection(), orderCode, 2341, -1);
		
		Log.info("tiantu","取调拨出库单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
	}
	
	//内部调拨出库   transfertype=2341
	private void processTransferOutStatus2(Element updatesaleorderstatusele) throws Exception
	{
		String sql="";
		
		String customerCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "customerCode");
		String warehouseCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "warehouseCode");
		String orderCode=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderCode");
		String orderStatus=DOMHelper.getSubElementVauleByName(updatesaleorderstatusele, "orderStatus");
		
		if (orderStatus.equalsIgnoreCase("DELIVERED"))		//已发货
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
				+"outshopid,inshopid,2341,100,7,'tiantu',getdate(),'接口','tiantu',getdate(),"
				+"note from transfer0 (nolock) "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele,this.getIsBarcodeId());
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2341, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//取消、关闭
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
				+"notifyOper,notifydate,operator,checker,checkdate,note,PurDay,SaleStockFlag)"
				+" select '"+commsheetid+"',sheetid,refsheetid,'"+owner+"',"
				+"outshopid,inshopid,2341,97,'tiantu',getdate(),'接口','tiantu',getdate(),"
				+"note,7,'1' from transfer0 (nolock) "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			getTranferOutDetail(commsheetid,orderCode);
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2341, this.getInterfaceSystem(), this.getWarehouseMulti()?TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode,"SyncSalesOrderInfo"):TianTuUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			this.getExtconnection().commit();
			this.getExtconnection().setAutoCommit(true);
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //接单成功
			TianTuUtil.updateMsg(this.getExtconnection(), orderCode, 2341, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
			TianTuUtil.updateMsg(this.getExtconnection(), orderCode, 2341, -1);
		
		Log.info("tiantu","取调拨出库单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
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
	
	//创建出库单明细
	
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
