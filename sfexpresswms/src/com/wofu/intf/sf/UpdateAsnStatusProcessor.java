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
 * ������״̬����   �������������ƻ������������̣߳���tomcat���հ�����������д��ecs_bestlogisticsinterface��
 * job����״̬������Щ����
 *
 */
public class UpdateAsnStatusProcessor extends BizProcessor {

	@Override
	public void process() throws Exception {
		//������BizData�ֶ�����ת��dom����
		String[] sfData =this.getBizData().split(";") ;
		Document updateAsnStatusDoc = DOMHelper.newDocument(sfData[1], "GBK");
		Element updateAsnStatusele = updateAsnStatusDoc.getDocumentElement();	
		Element detailList = (Element)updateAsnStatusele.getElementsByTagName("detailList").item(0);
		Element item = (Element)detailList.getElementsByTagName("item").item(0);
		//���ұ���
		String customerCode=this.getCustomerCode();
		//�ֿ����
		String warehouseCode=sfData[0];
			warehouseCode = warehouseCode.substring(warehouseCode.indexOf("=")+1);
		//��������
		String asnCode=DOMHelper.getSubElementVauleByName(item, "inventory_sts");
		//�������ⲿ����  ������-�����ƻ���  ������ⵥ
		String extOrderType=DOMHelper.getSubElementVauleByName(item, "inventory_sts");
		Connection extconn=null;
		try
		{
			//ȡ�ð����ֵ����ݿ�����
			extconn=PoolHelper.getInstance().getConnection(
				sfUtil.getDSName(this.getConnection(), customerCode,warehouseCode));
			extconn.setAutoCommit(false);
			this.setExtconnection(extconn);
		
		
			if (extOrderType.equals("10"))  //�����ƻ���
			{
				processAsnStatus(updateAsnStatusele);
			}
				
			else if (extOrderType.equals("2342"))  //������ⵥ
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
			
			throw new JException("��������ʧ��,����:"+asnCode);
		
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
	/**
	 * ���������ƻ���  �������д�뵽wms_instock0 wms_instockitem0 it_upnote   flag��ͬ  transfertype=2314
	 * @param updateAsnStatusele   Ҫ���������element����
	 * @param isBarcodeId  ���͵��Ƿ���barcodeId  
	 * @throws Exception
	 */
	private void processAsnStatus(Element updateAsnStatusele) throws Exception
	{
		try{
			Element header =(Element) updateAsnStatusele.getElementsByTagName("header").item(0);
			//�ֿ����
			//String warehouseCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "warehouseCode");
			//ͬ����
			String asnCode=DOMHelper.getSubElementVauleByName(header, "erp_order_num");
			String sql="";
			                                                   //it_upnote��sheetidΪwms_instock0��sheetid
				
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
					+"'"+venderid+"','"+shopid+"',30,2314,100,getdate(),'sf',getdate(),'�ӿ�',getdate(),'s',getdate(),"
					+"'')";
			
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				Log.info("ssss");
				getInStockDetail(commsheetid,updateAsnStatusele);
				Log.info("ssss11");	
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), shopid);
			/**	
			else if (asnStatus.equalsIgnoreCase("CANCELED") || asnStatus.equalsIgnoreCase("CLOSED"))		//ȡ��  flag=97
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
					+"'"+venderid+"','"+shopid+"',30,2314,97,getdate(),'best',getdate(),'�ӿ�',getdate(),'best',getdate(),"
					+"'')";
			
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				
				sql="insert into wms_instockitem0(sheetid,customermid,"
					+"barcodeid,badflag,NotifyPrice,price,notifyqty,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) "
					+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,qty,qty,"
					+",0,0,0,pknum,pkname,pkspec,17.00 "
					+"from planreceiptitem where sheetid='"+asnCode+"' ";
				
					
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), shopid);
			}
			else if (asnStatus.equalsIgnoreCase("WMS_ACCEPT") || asnStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�  ����ecs_bestlogisticsmsg�е���Ϣ
				{
					sfUtil.updateMsg(this.getExtconnection(), asnCode, 2227, 1);
				}
				
			else if (asnStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
				sfUtil.updateMsg(this.getExtconnection(), asnCode, 2227, -1);  //����ecs_bestlogisticsmsg�е���Ϣ
			**/
			
			Log.info("best logistics","���ղ�����״̬�ɹ�,����:"+asnCode);
		}catch(Exception ex){
			Log.error("��������ʧ��", ex.getMessage());
			throw ex;
		}
		
	}
	
	//���������ⵥ  ������⣬Ҳ���Ǵ��������ѻ�����뵽�����⣬Ҳ������һ����ʽ������Ҳ��д�뵽wms_instock0 wms_instockitem0 it_upnote��   transfertype=2342
	private void processTransferInStatus(Element updateAsnStatusele) throws Exception
	{
		String warehouseCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "warehouseCode");
		String asnStatus=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnStatus");
		String asnCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnCode");
		String sql="";
		
		if (asnStatus.equalsIgnoreCase("FULFILLED"))		//�ջ����
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
				+"'"+outshopid+"','"+inshopid+"',30,2342,100,getdate(),'best',getdate(),'�ӿ�',getdate(),'best',getdate(),"
				+"'')";
		
			SQLHelper.executeSQL(this.getExtconnection(), sql);
	
			
			getInStockDetail(commsheetid,updateAsnStatusele);
			
	
				
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2342, this.getInterfaceSystem(), inshopid);
			
		}
		else if (asnStatus.equalsIgnoreCase("CANCELED") || asnStatus.equalsIgnoreCase("CLOSED"))		//ȡ��
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
				+"'"+outshopid+"','"+inshopid+"',30,2342,97,getdate(),'best',getdate(),'�ӿ�',getdate(),'best',getdate(),"
				+"'')";
		
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			
			sql="insert into wms_instockitem0(sheetid,customermid,"
				+"barcodeid,badflag,NotifyPrice,price,notifyqty,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) "
				+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,outqty,inqty,"
				+",0,0,0,pknum,pkname,pkspec,17.00 "
				+"from transferitem0 where sheetid='"+asnCode+"' ";
			
				
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), inshopid);
		}
		else if (asnStatus.equalsIgnoreCase("WMS_ACCEPT") || asnStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�
			sfUtil.updateMsg(this.getExtconnection(), asnCode, 2342, 1);
		else if (asnStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
			sfUtil.updateMsg(this.getExtconnection(), asnCode, 2342, -1);
		
		
		Log.info("best logistics","���յ�����״̬�ɹ�,����:"+asnCode+" ״̬:"+asnStatus);
	}
	
	//������ⵥ��ϸ
	
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
