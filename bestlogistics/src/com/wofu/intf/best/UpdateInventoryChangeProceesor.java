package com.wofu.intf.best;

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
import com.wofu.common.tools.util.log.Log;

public class UpdateInventoryChangeProceesor extends BizProcessor {

	@Override
	public void process() throws Exception {
		
		
		Document updateInventoryChangeDoc = DOMHelper.newDocument(this.getBizData(), "GBK");

		Element updateInventoryChangeele = updateInventoryChangeDoc.getDocumentElement();	
		
		String customerCode=DOMHelper.getSubElementVauleByName(updateInventoryChangeele, "customerCode");
		String warehouseCode=DOMHelper.getSubElementVauleByName(updateInventoryChangeele, "warehouseCode");
		String checkCode=DOMHelper.getSubElementVauleByName(updateInventoryChangeele, "checkCode");
		String checkType=DOMHelper.getSubElementVauleByName(updateInventoryChangeele, "checkType");
		
		Connection extconn=null;
		try
		{
			extconn=PoolHelper.getInstance().getConnection(
				BestUtil.getDSName(this.getConnection(),customerCode,warehouseCode));
			extconn.setAutoCommit(false);
			this.setExtconnection(extconn);
		
			if (checkType.equalsIgnoreCase("WMS_ADJUSTFEEDBACK"))
			{
			
				//取原盘点单
				String sql="select count(*) from WMS_CheckProfitLoss where refsheetid='"+checkCode+"'";
				
				if (SQLHelper.intSelect(this.getExtconnection(), sql)!=0) return; //如果已经入库 则不做处理
				
				sql="select count(*) from WMS_CheckProfitLoss0 where refsheetid='"+checkCode+"'";
				
				if (SQLHelper.intSelect(this.getExtconnection(), sql)!=0) return; //如果已经入库 则不做处理
				
				sql="select vertifycode from IT_SystemInfo where interfacesystem='"+this.getInterfaceSystem()+"'";
				String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
				
				
				sql="declare @Err int ; declare @NewSheetID char(16); "
					+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
				String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
		
				
				sql="insert into WMS_CheckProfitLoss0(sheetid,refsheetid,"
					+"owner,ManageDeptID,transfertype,flag,"
					+"editor,editdate,operator,checker,checkdate,note)"
					+"values('"+commsheetid+"','"+checkCode+"','"+owner+"',0"
					+",2449,100,'best',getdate(),'接口','best',getdate(),"
					+"'')";
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				
				
				Element productsele=(Element) updateInventoryChangeele.getElementsByTagName("products").item(0);
				
				NodeList productNodeList=productsele.getElementsByTagName("product");
				
				for(int i=0;i<productNodeList.getLength();i++)
				{
					
					Element prductele=(Element) productNodeList.item(i);
					
					String skuCode=DOMHelper.getSubElementVauleByName(prductele, "skuCode");
					String fixStatusCode=DOMHelper.getSubElementVauleByName(prductele, "fixStatusCode");				
					int quantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(prductele, "quantity")).intValue();
					int fullQty=Integer.valueOf(DOMHelper.getSubElementVauleByName(prductele, "fullQty")).intValue();
					
					//sql="select goodsid,barcodeid from barcode where custombc='"+skuCode+"'";
					sql = new StringBuilder().append("select goodsid,barcodeid from barcode where ")
						.append(this.getIsBarcodeId()?"barcodeid='":"custombc='").append(skuCode)
						.append("'").toString();
					Hashtable htbarcode=SQLHelper.oneRowSelect(this.getExtconnection(), sql);
					
					String goodsid=htbarcode.get("goodsid").toString();
					String barcodeid=htbarcode.get("barcodeid").toString();
					
					if (fixStatusCode.equalsIgnoreCase("Y"))
					{
						sql="select count(*) from WMS_CheckProfitLossItem0 where sheetid='"+commsheetid+"' and barcodeid='"+barcodeid+"'";
						if(SQLHelper.intSelect(this.getExtconnection(), sql)>0)
							sql="update WMS_CheckProfitLossItem0 set qty="+quantity+",stockqty="+(fullQty-quantity)
								+"where sheetid='"+commsheetid+"' and barcodeid='"+barcodeid+"'";
						else
							/*sql="insert into WMS_CheckProfitLossItem0(sheetid,customermid,barcodeid,Qty,stockqty) "
								+"values('"+commsheetid+"',goodsid,barcodeid,"+quantity+","+(fullQty-quantity)+")";	*/
							sql = new StringBuilder().append("insert into WMS_CheckProfitLossItem0(sheetid,customermid,barcodeid,Qty,stockqty) ")
								.append("values('").append(commsheetid).append("','")
								.append(goodsid).append("','").append(barcodeid)
								.append("',").append(quantity).append(",")
								.append((fullQty-quantity)).append(")").toString();
						SQLHelper.executeSQL(this.getExtconnection(), sql);
					}
					else
					{
						sql="select count(*) from WMS_CheckProfitLossItem0 where sheetid='"+commsheetid+"' and barcodeid='"+barcodeid+"'";
						if(SQLHelper.intSelect(this.getExtconnection(), sql)>0)
							sql="update WMS_CheckProfitLossItem0 set badqty="+quantity+",stockbadqty="+(fullQty-quantity)
								+"where sheetid='"+commsheetid+"' and barcodeid='"+barcodeid+"'";
						else
							/*sql="insert into WMS_CheckProfitLossItem0(sheetid,customermid,barcodeid,badqty,stockbadqty) "
								+"values('"+commsheetid+"',goodsid,barcodeid,"+quantity+","+(fullQty-quantity)+")";*/
							sql = new StringBuilder().append("insert into WMS_CheckProfitLossItem0(sheetid,customermid,barcodeid,badqty,stockbadqty) ")
								.append("values('").append(commsheetid).append("','")
								.append(goodsid).append("','").append(barcodeid)
								.append("',").append(quantity).append(",")
								.append(fullQty-quantity).append(")").toString();
						SQLHelper.executeSQL(this.getExtconnection(), sql);
					}
									
				}
				
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2449, this.getInterfaceSystem(), BestUtil.getShopID(this.getExtconnection(),customerCode,warehouseCode));
				
				
				Log.info("best logistics","接收调整单成功,单号:"+checkCode);
							
			}
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
						Log.error("best logistics","回滚事务失败:"+rollbackexception.getMessage());
					}
					try
					{
						extconn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("best logistics","设置自动提交事务失败:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("best logistics","设置自动提交事务失败:"+sqle.getMessage());
			}
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception closeexception) {
				Log.error("best logistics", "关闭数据库连接失败:"+closeexception.getMessage());
			}
			
			Log.info("接收失败 ...");
		
		}
		finally {			
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception e) {
				Log.error("best logistics", "关闭数据库连接失败:"+e.getMessage());
			}
		}

	}

}
