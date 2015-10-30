package com.wofu.intf.sf;
/**
 * 销售出库单-数据要写到wms_outstock0 wms_outstockitem0 it_upnote表中
 * 订单类型：
   NORMAL-普通订单/交易订单  货物是发到普通用户的
   WDO-出库单/非交易订单     退供应商出库单-把货物退到供应商中   调拨出库单  把货物调到其它的仓库

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
		//卖家编码
		String customerCode=this.getCustomerCode();
		Log.info("customerCode: "+customerCode);
		String warehouseCode=DOMHelper.getSubElementVauleByName(header, "warehouse");
		Log.info("warehouseCode: "+warehouseCode);
		String orderStatus=DOMHelper.getSubElementVauleByName(header, "status_code");
		String orderCode=DOMHelper.getSubElementVauleByName(header, "erp_order");
		
		Connection extconn=null;
		try
		{
			//设置外部数据库连接--对应百世仓库
			extconn=PoolHelper.getInstance().getConnection(
				sfUtil.getDSName(this.getConnection(), customerCode,warehouseCode));
			extconn.setAutoCommit(false);
			this.setExtconnection(extconn);
			  //普通出库/出货订单
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
						Log.error("sf logistics","回滚事务失败:"+rollbackexception.getMessage());
					}
					try
					{
						extconn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("sf logistics","设置自动提交事务失败:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("sf logistics","设置自动提交事务失败:"+sqle.getMessage());
			}
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception closeexception) {
				Log.error("sf logistics", "关闭数据库连接失败:"+closeexception.getMessage());
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
				Log.error("sf logistics", "关闭数据库连接失败:"+e.getMessage());
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
		
		if (orderStatus.equalsIgnoreCase("900"))		//已发货  货物已经发到客户了
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
			//把数据从customerdelive0表写入百世仓的wms_outstock0表					
			sql="insert into wms_outstock0(sheetid,refsheetid,pursheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"notifyOper,notifydate,operator,checker,checkdate,note,address,"
				+"linktele,linkman,delivery,deliverysheetid,zipcode,detailid,weigh)"
				+" select '"+commsheetid+"',sheetid,refsheetid,customersheetid,'"+this.getVertifycode()+"',"
				+"outshopid,inshopid,purday,2209,100,'sf',getdate(),'接口','sf',getdate(),"
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
		//订单取消
		else if (orderStatus.equalsIgnoreCase("10013") || orderStatus.equalsIgnoreCase("10012"))		//取消、关闭
		{
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
				+"outshopid,inshopid,purday,2209,97,'sf',getdate(),'接口','sf',getdate(),"
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
			Log.info("订单: "+orderCode+",已经被冻结,如要重新发货，请联系顺风仓储!");
		}
		else if (orderStatus.equalsIgnoreCase("10001") || orderStatus.equalsIgnoreCase("10003")|| orderStatus.equalsIgnoreCase("400")|| orderStatus.equalsIgnoreCase("700")|| orderStatus.equalsIgnoreCase("300"))  //接单成功
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2209, 1);
		
		
		Log.info("sf logistics","取发货单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
	}
	
	//重入部分退货的其他商品   先查到订单的tid,再根据tid查询退货接口表的数据是否有相应的数据，如果有，查询这个订单是否已经被合单，分别处理
	private void processPartRefund(String orderCode) throws Exception
	{	
		//查找退货订单的客户订单号  tid
		String sql="select customersheetid from customerdelive0 with(nolock) where sheetid='"+orderCode+"'";
		
		String tid=SQLHelper.strSelect(this.getExtconnection(), sql);
		
		sql="select count(*) from ns_refund with(nolock) where tid='"+tid+"'";
		//查找这个订单在退货接口表的记录
		if(SQLHelper.intSelect(this.getConnection(), sql)>0)   //如果有退货
		{
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
			//查询这个订单退货的商品明细  如果有相应的退货明细商品，则生成相应的customerorder0 customerorderitem0表数据，即新的订单
			sql="select count(*) from customerorderitem with(nolock) "
				+"where sheetid='"+sheetid+"' "
				+" and oid not in(select oid from ns_refund with(nolock) "
				+ "where tid='"+tid+"' or tid in(select refsheetid from customerorderreflist with(nolock) where sheetid='"+sheetid+"'))";
			
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
				sql="select * into #tmp_order from customerorder where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(this.getConnection(), sql);
				
				
				sql="update #tmp_order set sheetid='"+newsheetid+"',flag=0,notes=notes+'部分退货重入'";
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
	
	//创建发货单明细
	
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
				+"shopid,venderid,2322,100,7,'best',getdate(),'接口','best',getdate(),"
				+"notes from ret0 "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele);
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2322, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//取消、关闭
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
				+"shopid,venderid,2322,97,7,'best',getdate(),'接口','best',getdate(),"
				+"notes from ret0 "
				+" where sheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			
			getReturnDetail(commsheetid,orderCode);
					
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2322, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //接单成功
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2322, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2322, -1);
		
		
		Log.info("best logistics","取供应商退货单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
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
				+"outshopid,inshopid,2341,100,7,'best',getdate(),'接口','best',getdate(),"
				+"note from transfer0 "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		

			getOutStockDetail(commsheetid,updatesaleorderstatusele);
			

			IntfUtils.upNote(this.getExtconnection(),owner, commsheetid, 2341, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
			
			
		}
		else if (orderStatus.equalsIgnoreCase("CANCELED") || orderStatus.equalsIgnoreCase("CLOSED"))		//取消、关闭
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
				+"outshopid,inshopid,2341,97,'best',getdate(),'接口','best',getdate(),"
				+"note,7 from transfer0 "
				+" where refsheetid='"+orderCode+"'";
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			getTranferOutDetail(commsheetid,orderCode);
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2341, this.getInterfaceSystem(), sfUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
		}
		else if (orderStatus.equalsIgnoreCase("WMS_ACCEPT") || orderStatus.equalsIgnoreCase("INPROCESS"))  //接单成功
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2341, 1);
		else if (orderStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
			sfUtil.updateMsg(this.getExtconnection(), orderCode, 2341, -1);
		
		
		Log.info("best logistics","取调拨出库单状态成功,单号:"+orderCode+" 状态:"+orderStatus);
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
	
	//创建出库单明细
	
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
