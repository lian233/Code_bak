package com.wofu.intf.sf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
/**
 * 
 * 补货单状态更新   首先推送来货计划单到百世（线程），tomcat接收百世返回数据写到ecs_bestlogisticsinterface表
 * job根据状态处理这些数据
 *
 */
public class UpdateAsnStatusProcessor extends BizProcessor {

	@Override
	public void process() throws Exception {
		//把数据BizData字段数据转成dom对象
		String[] sfData =this.getBizData().split(";") ;
		Document updateAsnStatusDoc = DOMHelper.newDocument(sfData[1], "GBK");
		Element updateAsnStatusele = updateAsnStatusDoc.getDocumentElement();	
		Element detailList = (Element)updateAsnStatusele.getElementsByTagName("detailList").item(0);
		Element item = (Element)detailList.getElementsByTagName("item").item(0);
		//卖家编码
		String customerCode=this.getCustomerCode();
		//仓库编码
		String warehouseCode=sfData[0];
			warehouseCode = warehouseCode.substring(warehouseCode.indexOf("=")+1);
		//补货单号
		String asnCode=DOMHelper.getSubElementVauleByName(item, "inventory_sts");
		//补货单外部类型  有两种-来货计划单  调拨入库单
		String extOrderType=DOMHelper.getSubElementVauleByName(item, "inventory_sts");
		Connection extconn=null;
		try
		{
			//取得百世仓的数据库连接
			extconn=PoolHelper.getInstance().getConnection(
				sfUtil.getDSName(this.getConnection(), customerCode,warehouseCode));
			extconn.setAutoCommit(false);
			this.setExtconnection(extconn);
		
		
			if (extOrderType.equals("10"))  //来货计划单
			{
				processAsnStatus(updateAsnStatusele);
			}
				
			else if (extOrderType.equals("2342"))  //调拨入库单
				processTransferInStatus(updateAsnStatusele);
			
			extconn.commit();
			extconn.setAutoCommit(true);
		}catch(Exception e)
		{
			try
			{
				if (!extconn.getAutoCommit())
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
			
			throw new JException("处理补货单失败,单号:"+asnCode);
		
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
	/**
	 * 处理来货计划单  会把数据写入到wms_instock0 wms_instockitem0 it_upnote   flag不同  transfertype=2314
	 * @param updateAsnStatusele   要处理的数据element对象
	 * @param isBarcodeId  推送的是否是barcodeId  
	 * @throws Exception
	 */
	private void processAsnStatus(Element updateAsnStatusele) throws Exception
	{
		try{
			Element header =(Element) updateAsnStatusele.getElementsByTagName("header").item(0);
			//仓库编码
			//String warehouseCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "warehouseCode");
			//同步码
			String asnCode=DOMHelper.getSubElementVauleByName(header, "erp_order_num");
			String sql="";
			                                                   //it_upnote的sheetid为wms_instock0的sheetid
				
				sql="select count(*) from wms_instock0 where refsheetid='"+asnCode+"' and transfertype=2314 and flag=100";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select count(*) from wms_instock where refsheetid='"+asnCode+"' and transfertype=2314 and flag=100";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
				String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
				
				sql="select shopid,venderid from planreceipt where sheetid='"+asnCode+"'";
				Hashtable htplan=SQLHelper.oneRowSelect(this.getExtconnection(), sql);
				
				Log.info("ssss33");
				String venderid=htplan.get("venderid").toString();
				String shopid=htplan.get("shopid").toString();
				
				sql="declare @Err int ; declare @NewSheetID char(16); "
					+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
				String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
									
				sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,plansheetid,"
					+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
					+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
					+"values('"+commsheetid+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+owner+"',"
					+"'"+venderid+"','"+shopid+"',30,2314,100,getdate(),'sf',getdate(),'接口',getdate(),'s',getdate(),"
					+"'')";
			
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				Log.info("ssss");
				getInStockDetail(commsheetid,updateAsnStatusele);
				Log.info("ssss11");	
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), shopid);
			/**	
			else if (asnStatus.equalsIgnoreCase("CANCELED") || asnStatus.equalsIgnoreCase("CLOSED"))		//取消  flag=97
			{
		
				sql="select count(*) from wms_instock0 where refsheetid='"+asnCode+"' and transfertype=2314 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select count(*) from wms_instock where refsheetid='"+asnCode+"' and transfertype=2314 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
				String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
				
				sql="select shopid,venderid from planreceipt where sheetid='"+asnCode+"'";
				Hashtable htplan=SQLHelper.oneRowSelect(this.getExtconnection(), sql);
				
				
				String venderid=htplan.get("venderid").toString();
				String shopid=htplan.get("shopid").toString();
				
				sql="declare @Err int ; declare @NewSheetID char(16); "
					+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
				String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
									
				sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,plansheetid,"
					+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
					+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
					+"values('"+commsheetid+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+owner+"',"
					+"'"+venderid+"','"+shopid+"',30,2314,97,getdate(),'best',getdate(),'接口',getdate(),'best',getdate(),"
					+"'')";
			
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				
				sql="insert into wms_instockitem0(sheetid,customermid,"
					+"barcodeid,badflag,NotifyPrice,price,notifyqty,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) "
					+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,qty,qty,"
					+",0,0,0,pknum,pkname,pkspec,17.00 "
					+"from planreceiptitem where sheetid='"+asnCode+"' ";
				
					
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), shopid);
			}
			else if (asnStatus.equalsIgnoreCase("WMS_ACCEPT") || asnStatus.equalsIgnoreCase("INPROCESS"))  //接单成功  更改ecs_bestlogisticsmsg中的信息
				{
					sfUtil.updateMsg(this.getExtconnection(), asnCode, 2227, 1);
				}
				
			else if (asnStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
				sfUtil.updateMsg(this.getExtconnection(), asnCode, 2227, -1);  //更改ecs_bestlogisticsmsg中的信息
			**/
			
			Log.info("best logistics","接收补货单状态成功,单号:"+asnCode);
		}catch(Exception ex){
			Log.error("处理补货单失败", ex.getMessage());
			throw ex;
		}
		
	}
	
	//处理调拨入库单  调拨入库，也就是从其它库存把货物调入到百世库，也是入库的一个形式，数据也是写入到wms_instock0 wms_instockitem0 it_upnote表   transfertype=2342
	private void processTransferInStatus(Element updateAsnStatusele) throws Exception
	{
		String warehouseCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "warehouseCode");
		String asnStatus=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnStatus");
		String asnCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnCode");
		String sql="";
		
		if (asnStatus.equalsIgnoreCase("FULFILLED"))		//收货完成
		{
			
			sql="select count(*) from wms_instock0 where refsheetid='"+asnCode+"' and transfertype=2342 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_instock where refsheetid='"+asnCode+"' and transfertype=2342 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			sql="select outshopid,inshopid from transfer0 where sheetid='"+asnCode+"'";
			Hashtable htplan=SQLHelper.oneRowSelect(this.getExtconnection(), sql);
			
			
			String outshopid=htplan.get("outshopid").toString();
			String inshopid=htplan.get("inshopid").toString();
			
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
								
			sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,plansheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
				+"values('"+commsheetid+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+owner+"',"
				+"'"+outshopid+"','"+inshopid+"',30,2342,100,getdate(),'best',getdate(),'接口',getdate(),'best',getdate(),"
				+"'')";
		
			SQLHelper.executeSQL(this.getExtconnection(), sql);
	
			
			getInStockDetail(commsheetid,updateAsnStatusele);
			
	
				
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2342, this.getInterfaceSystem(), inshopid);
			
		}
		else if (asnStatus.equalsIgnoreCase("CANCELED") || asnStatus.equalsIgnoreCase("CLOSED"))		//取消
		{
	
			sql="select count(*) from wms_instock0 where refsheetid='"+asnCode+"' and transfertype=2342 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_instock where refsheetid='"+asnCode+"' and transfertype=2342 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			sql="select outshopid,inshopid from transfer0 where sheetid='"+asnCode+"'";
			Hashtable htplan=SQLHelper.oneRowSelect(this.getExtconnection(), sql);
			
			
			String outshopid=htplan.get("outshopid").toString();
			String inshopid=htplan.get("inshopid").toString();
			
			
			sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
			String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
								
			sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,plansheetid,"
				+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
				+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
				+"values('"+commsheetid+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+owner+"',"
				+"'"+outshopid+"','"+inshopid+"',30,2342,97,getdate(),'best',getdate(),'接口',getdate(),'best',getdate(),"
				+"'')";
		
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			
			sql="insert into wms_instockitem0(sheetid,customermid,"
				+"barcodeid,badflag,NotifyPrice,price,notifyqty,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) "
				+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,outqty,inqty,"
				+",0,0,0,pknum,pkname,pkspec,17.00 "
				+"from transferitem0 where sheetid='"+asnCode+"' ";
			
				
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), inshopid);
		}
		else if (asnStatus.equalsIgnoreCase("WMS_ACCEPT") || asnStatus.equalsIgnoreCase("INPROCESS"))  //接单成功
			sfUtil.updateMsg(this.getExtconnection(), asnCode, 2342, 1);
		else if (asnStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
			sfUtil.updateMsg(this.getExtconnection(), asnCode, 2342, -1);
		
		
		Log.info("best logistics","接收调拨单状态成功,单号:"+asnCode+" 状态:"+asnStatus);
	}
	
	//创建入库单明细
	
	private void getInStockDetail(String commsheetid,Element updateAsnStatusele) throws Exception
	{
		Element produectsele=(Element) updateAsnStatusele.getElementsByTagName("detailList").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("item");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "sku_no"); 
			int normalQuantity=(int)Float.valueOf(DOMHelper.getSubElementVauleByName(produectele, "qty")).floatValue();
			
			String sql = new StringBuilder().append("insert into wms_instockitem0(sheetid,customermid,")
			.append("barcodeid,badflag,NotifyPrice,price,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) ")
			.append(" select '").append(commsheetid)
			.append("',goodsid,barcodeid,1,0.00,0.00,").append(normalQuantity)
			.append(",").append("0").append(",0,0,pknum,pkname,pkspec,17.00 ")
			.append("from barcode where ").append("custombc='")
			.append(skuCode).append("'").toString();
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		}
	}

}
