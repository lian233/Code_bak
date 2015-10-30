package com.wofu.intf.tiantu;
/**
 * 处理客户退货信息  这里客户退货是指（销售退货）客户在商城完成收货了，再退货。这里货物到了百世要做入库处理的。
 * 数据也要写入到wms_instock0 wms_instockitem0 it_upnote表中，其中的transfertype=2222
 */
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

public class UpdateRmaStatusProcessor extends BizProcessor {

public void process() throws Exception {
		
		Document updateRmaStatusDoc = DOMHelper.newDocument(this.getBizData(), "GBK");

		Element updateRmaStatusele = updateRmaStatusDoc.getDocumentElement();	
		
		String customerCode=DOMHelper.getSubElementVauleByName(updateRmaStatusele, "customerCode");
		String warehouseCode=DOMHelper.getSubElementVauleByName(updateRmaStatusele, "warehouseCode");
		String rmaStatus=DOMHelper.getSubElementVauleByName(updateRmaStatusele, "rmaStatus");
		String rmaCode=DOMHelper.getSubElementVauleByName(updateRmaStatusele, "rmaCode");
		
		Connection extconn=null;
		try
		{
			extconn=PoolHelper.getInstance().getConnection(
				TianTuUtil.getDSName(this.getConnection(),customerCode, warehouseCode));
			extconn.setAutoCommit(false);
			this.setExtconnection(extconn);
		
			String sql="";
			
			if (rmaStatus.equalsIgnoreCase("FULFILLED"))		//收货完成
			{
				sql="select count(*) from wms_instock0 where refsheetid='"+rmaCode+"' and transfertype=2222 and flag=100";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select count(*) from wms_instock where refsheetid='"+rmaCode+"' and transfertype=2222 and flag=100";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
				String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
				
				sql="select refsheetid,outshopid,inshopid from CustomerRetrcv0 where sheetid='"+rmaCode+"'";
				Hashtable htplan=SQLHelper.oneRowSelect(this.getExtconnection(), sql);
				
				
				String refsheetid=htplan.get("refsheetid").toString();
				String outshopid=htplan.get("outshopid").toString();
				String inshopid=htplan.get("inshopid").toString();
				
				sql="declare @Err int ; declare @NewSheetID char(16); "
					+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
				String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
									
				sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,"
					+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
					+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
					+"values('"+commsheetid+"','"+rmaCode+"','"+refsheetid+"','"+rmaCode+"','"+owner+"',"
					+"'"+outshopid+"','"+inshopid+"',30,2222,100,getdate(),'tiantu',getdate(),'接口',getdate(),'tiantu',getdate(),"
					+"'')";
			
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				
				getInStockDetail(commsheetid,updateRmaStatusele,this.getIsBarcodeId());
					
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2222, this.getInterfaceSystem(), inshopid);
				
			}
			else if (rmaStatus.equalsIgnoreCase("CANCELED") || rmaStatus.equalsIgnoreCase("CLOSED"))		//取消
			{
	
				sql="select count(*) from wms_instock0 where refsheetid='"+rmaCode+"' and transfertype=2222 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select count(*) from wms_instock where refsheetid='"+rmaCode+"' and transfertype=2222 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
				String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
				
				sql="select refsheetid,outshopid,inshopid from CustomerRetrcv0 where sheetid='"+rmaCode+"'";
				Hashtable htplan=SQLHelper.oneRowSelect(this.getExtconnection(), sql);
				
				
				String refsheetid=htplan.get("refsheetid").toString();
				String outshopid=htplan.get("outshopid").toString();
				String inshopid=htplan.get("inshopid").toString();
				
				
				sql="declare @Err int ; declare @NewSheetID char(16); "
					+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
				String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
				
				sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,"
					+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
					+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
					+"values('"+commsheetid+"','"+rmaCode+"','"+refsheetid+"','"+rmaCode+"','"+owner+"',"
					+"'"+outshopid+"','"+inshopid+"',30,2222,100,getdate(),'tiantu',getdate(),'接口',getdate(),'tiantu',getdate(),"
					+"'')";
			
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				
				getInStockDetail(commsheetid,updateRmaStatusele,this.getIsBarcodeId());
					
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2222, this.getInterfaceSystem(), inshopid);
			}
			else if (rmaStatus.equalsIgnoreCase("WMS_ACCEPT") || rmaStatus.equalsIgnoreCase("INPROCESS"))  //接单成功
				TianTuUtil.updateMsg(this.getExtconnection(), rmaCode, 2222, 1);
			else if (rmaStatus.equalsIgnoreCase("WMS_REJECT"))   //接单失败
				TianTuUtil.updateMsg(this.getExtconnection(), rmaCode, 2222, -1);
			
			
			Log.info("tiantu","接收客户退货单状态成功,单号:"+rmaCode+" 状态:"+rmaStatus);
			
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
			
			throw new JException("处理退货单失败,单号:"+rmaCode+",退货单状态:"+rmaStatus);
		
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
	
	//创建发货单明细
	
	private void getInStockDetail(String commsheetid,Element asnele,Boolean IsBarcodeId) throws Exception
	{
		Element produectsele=(Element) asnele.getElementsByTagName("products").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("product");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "skuCode"); 
			int normalQuantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(produectele, "normalQuantity")).intValue();
			int defectiveQuantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(produectele, "defectiveQuantity")).intValue();
		
			
			//int totalqty=normalQuantity+defectiveQuantity;
			
			/*String sql="insert into wms_instockitem0(sheetid,customermid,"
				+""
				+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,"+totalqty+",0,"+totalqty+
				",0,"+defectiveQuantity+",pknum,pkname,pkspec,17.00 "
				+"from barcode where custombc='"+skuCode+"' ";
			*/
			String sql = new StringBuilder().append("insert into wms_instockitem0(sheetid,customermid,barcodeid,")
			.append("badflag,NotifyPrice,price,NotifyPQty,inqty,InPQty,InBadQty,pknum,pkname,pkspec,Taxrate) ")
			.append(" select '").append(commsheetid).append("',goodsid,barcodeid,1,0.00,0.00,0,")
			.append(normalQuantity)
			.append(",0,").append(defectiveQuantity).append(",pknum,pkname,pkspec,17.00 ")
			.append("from barcode where ").append(IsBarcodeId?"barcodeid='":"custombc='")
			.append(skuCode).append("'").toString();
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		}
	}

}
