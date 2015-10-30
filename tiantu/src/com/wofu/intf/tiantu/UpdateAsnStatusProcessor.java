package com.wofu.intf.tiantu;

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
 * ������״̬����   �������������ƻ�������ͼ���̣߳���tomcat���հ�����������д��ecs_bestlogisticsinterface��
 * job����״̬������Щ����
 *
 */
public class UpdateAsnStatusProcessor extends BizProcessor {

	@Override
	public void process() throws Exception {
		//������BizData�ֶ�����ת��dom����
		Document updateAsnStatusDoc = DOMHelper.newDocument(this.getBizData().substring(1), "GBK");//ȥ��ǰ���?

		Element updateAsnStatusele = updateAsnStatusDoc.getDocumentElement();	
		//���ұ���
		String customerCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "customerCode");
		//�ֿ����
		String warehouseCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "wareHouseCode");
		//������״̬    FULFILLED-�ջ����
		//CANCELED-ȡ��
		//CLOSED-�ر�
		String asnStatus=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnStatus");
		//��������
		String asnCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnCode");
		//�������ⲿ����  ������-�����ƻ���  ������ⵥ
		String extOrderType=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "extOrderType");
		String udf1=null;
		if(DOMHelper.ElementIsExists(updateAsnStatusele, "udf1"))
			udf1= DOMHelper.getSubElementVauleByName(updateAsnStatusele, "udf1");
		Connection extconn=null;
		try
		{	
			if(udf1!=null && !"".equals(udf1) && !"310000".equals(udf1)){
				Log.info("dcname: "+udf1.split("=")[1]);
				extconn=PoolHelper.getInstance().getConnection(
						udf1.split("=")[1]);
			}else{
				//ȡ�ð����ֵ����ݿ�����  
				extconn=PoolHelper.getInstance().getConnection(
					TianTuUtil.getDSName(this.getConnection(), customerCode,warehouseCode));
			}
			
			extconn.setAutoCommit(false);
			this.setExtconnection(extconn);
		
		
			if (extOrderType.equals("2227"))  //�����ƻ���
			{
				processAsnStatus(updateAsnStatusele,this.getIsBarcodeId());
			}
				
			else if (extOrderType.equals("2342"))  //������ⵥ
				processTransferInStatus(updateAsnStatusele,this.getIsBarcodeId());
			
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
						Log.error("tiantu","�ع�����ʧ��:"+rollbackexception.getMessage());
					}
					try
					{
						extconn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error("tiantu","�����Զ��ύ����ʧ��:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error("tiantu","�����Զ��ύ����ʧ��:"+sqle.getMessage());
			}
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception closeexception) {
				Log.error("tiantu", "�ر����ݿ�����ʧ��:"+closeexception.getMessage());
			}
			
			throw new JException("��������ʧ��,����:"+asnCode+",������״̬:"+asnStatus);
		
		}
		finally {			
			try {
				if (extconn != null)
				{
					extconn.close();
				}
			} catch (Exception e) {
				Log.error("tiantu", "�ر����ݿ�����ʧ��:"+e.getMessage());
			}
		}

	}
	/**
	 * ���������ƻ���  �������д�뵽wms_instock0 wms_instockitem0 it_upnote   flag��ͬ  transfertype=2314
	 * @param updateAsnStatusele   Ҫ���������element����
	 * @param isBarcodeId  ���͵��Ƿ���barcodeId  
	 * @throws Exception
	 */
	private void processAsnStatus(Element updateAsnStatusele,Boolean isBarcodeId) throws Exception
	{
		try{
			//�ֿ����
			String warehouseCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "warehouseCode");
			//ͬ��״̬
			String asnStatus=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnStatus");
			//ͬ����
			String asnCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnCode");
			String sql="";
			if (asnStatus.equalsIgnoreCase("FULFILLED"))		//�ջ����  �����Ѿ��ջ������  ��Щ��Ʒ��Ϣ������д��dc���wms_instock0 flag=100,wms_instockitem0,it_upnote��
			{                                                   //it_upnote��sheetidΪwms_instock0��sheetid
				sql="select count(*) from wms_instock0 (nolock) where refsheetid='"+asnCode+"' and transfertype=2314 and flag=100";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				sql="select count(*) from wms_instock (nolock) where refsheetid='"+asnCode+"' and transfertype=2314 and flag=100";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
				String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
				sql="select shopid,venderid from planreceipt (nolock) where sheetid='"+asnCode+"'";
				Hashtable htplan=SQLHelper.oneRowSelect(this.getExtconnection(), sql);
				
				String venderid="6";//htplan.get("venderid").toString();
				String shopid="020B7L";//htplan.get("shopid").toString();
				
				sql="declare @Err int ; declare @NewSheetID char(16); "
					+"execute  @Err = TL_GetNewSheetID 1103, @NewSheetID output;select @NewSheetID;";			
				String commsheetid=SQLHelper.strSelect(this.getExtconnection(), sql);
									
				sql="insert into wms_instock0(sheetid,refsheetid,pursheetid,plansheetid,"
					+"custompursheetid,owner,outid,inid,purday,transfertype,flag,"
					+"purdate,notifyOper,notifydate,operator,dealdate,checker,checkdate,note)"
					+"values('"+commsheetid+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+asnCode+"','"+owner+"',"
					+"'"+venderid+"','"+shopid+"',30,2314,100,getdate(),'tiantu',getdate(),'�ӿ�',getdate(),'tiantu',getdate(),"
					+"'')";
			
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				
				getInStockDetail(commsheetid,updateAsnStatusele,isBarcodeId);
					
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), shopid);
				
			}
			else if (asnStatus.equalsIgnoreCase("CANCELED") || asnStatus.equalsIgnoreCase("CLOSED"))		//ȡ��  flag=97
			{
		
				sql="select count(*) from wms_instock0 (nolock) where refsheetid='"+asnCode+"' and transfertype=2314 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select count(*) from wms_instock (nolock) where refsheetid='"+asnCode+"' and transfertype=2314 and flag=97";
				if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
				
				sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
				String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
				
				sql="select shopid,venderid from planreceipt (nolock) where sheetid='"+asnCode+"'";
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
					+"'"+venderid+"','"+shopid+"',30,2314,97,getdate(),'tiantu',getdate(),'�ӿ�',getdate(),'tiantu',getdate(),"
					+"'')";
			
				SQLHelper.executeSQL(this.getExtconnection(), sql);
				
				sql="insert into wms_instockitem0(sheetid,customermid,"
					+"barcodeid,badflag,NotifyPrice,price,notifyqty,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) "
					+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,qty,qty,"
					+",0,0,0,pknum,pkname,pkspec,17.00 "
					+"from planreceiptitem (nolock) where sheetid='"+asnCode+"' ";
				
					
				IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), shopid);
			}
			else if (asnStatus.equalsIgnoreCase("WMS_ACCEPT") || asnStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�  ����ecs_bestlogisticsmsg�е���Ϣ
				{
					TianTuUtil.updateMsg(this.getExtconnection(), asnCode, 2227, 1);
				}
				
			else if (asnStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
				TianTuUtil.updateMsg(this.getExtconnection(), asnCode, 2227, -1);  //����ecs_bestlogisticsmsg�е���Ϣ
			
			
			Log.info("tiantu","���ղ�����״̬�ɹ�,����:"+asnCode+" ״̬:"+asnStatus);
		}catch(Exception ex){
			Log.error("��������ʧ��", ex.getMessage());
			throw ex;
		}
		
	}
	
	//���������ⵥ  ������⣬Ҳ���Ǵ��������ѻ�����뵽�����⣬Ҳ������һ����ʽ������Ҳ��д�뵽wms_instock0 wms_instockitem0 it_upnote��   transfertype=2342
	private void processTransferInStatus(Element updateAsnStatusele,Boolean isBarCodeId) throws Exception
	{
		String warehouseCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "warehouseCode");
		String asnStatus=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnStatus");
		String asnCode=DOMHelper.getSubElementVauleByName(updateAsnStatusele, "asnCode");
		String sql="";
		
		if (asnStatus.equalsIgnoreCase("FULFILLED"))		//�ջ����
		{
			
			sql="select count(*) from wms_instock0 (nolock) where refsheetid='"+asnCode+"' and transfertype=2342 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_instock (nolock) where refsheetid='"+asnCode+"' and transfertype=2342 and flag=100";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			sql="select outshopid,inshopid from transfer0 (nolock) where sheetid='"+asnCode+"'";
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
				+"'"+outshopid+"','"+inshopid+"',30,2342,100,getdate(),'tiantu',getdate(),'�ӿ�',getdate(),'tiantu',getdate(),"
				+"'')";
		
			SQLHelper.executeSQL(this.getExtconnection(), sql);
	
			
			getInStockDetail(commsheetid,updateAsnStatusele,isBarCodeId);
			
	
				
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2342, this.getInterfaceSystem(), inshopid);
			
		}
		else if (asnStatus.equalsIgnoreCase("CANCELED") || asnStatus.equalsIgnoreCase("CLOSED"))		//ȡ��
		{
	
			sql="select count(*) from wms_instock0 (nolock) where refsheetid='"+asnCode+"' and transfertype=2342 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select count(*) from wms_instock (nolock) where refsheetid='"+asnCode+"' and transfertype=2342 and flag=97";
			if (SQLHelper.intSelect(this.getExtconnection(), sql)>0) return;
			
			sql="select vertifycode from IT_SystemInfo with(nolock) where interfacesystem='"+this.getInterfaceSystem()+"'";
			String owner=SQLHelper.strSelect(this.getExtconnection(), sql);
			
			sql="select outshopid,inshopid from transfer0 (nolock) where sheetid='"+asnCode+"'";
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
				+"'"+outshopid+"','"+inshopid+"',30,2342,97,getdate(),'tiantu',getdate(),'�ӿ�',getdate(),'tiantu',getdate(),"
				+"'')";
		
			SQLHelper.executeSQL(this.getExtconnection(), sql);
			
			sql="insert into wms_instockitem0(sheetid,customermid,"
				+"barcodeid,badflag,NotifyPrice,price,notifyqty,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) "
				+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,outqty,inqty,"
				+",0,0,0,pknum,pkname,pkspec,17.00 "
				+"from transferitem0 (nolock) where sheetid='"+asnCode+"' ";
			
				
			IntfUtils.upNote(this.getExtconnection(), owner, commsheetid, 2314, this.getInterfaceSystem(), inshopid);
		}
		else if (asnStatus.equalsIgnoreCase("WMS_ACCEPT") || asnStatus.equalsIgnoreCase("INPROCESS"))  //�ӵ��ɹ�
			TianTuUtil.updateMsg(this.getExtconnection(), asnCode, 2342, 1);
		else if (asnStatus.equalsIgnoreCase("WMS_REJECT"))   //�ӵ�ʧ��
			TianTuUtil.updateMsg(this.getExtconnection(), asnCode, 2342, -1);
		
		
		Log.info("tiantu","���յ�����״̬�ɹ�,����:"+asnCode+" ״̬:"+asnStatus);
	}
	
	//������ⵥ��ϸ
	
	private void getInStockDetail(String commsheetid,Element updateAsnStatusele,Boolean isBarcodeId) throws Exception
	{
		Element produectsele=(Element) updateAsnStatusele.getElementsByTagName("products").item(0);
		
		NodeList productnodelist=produectsele.getElementsByTagName("product");
		
		for (int i=0;i<productnodelist.getLength();i++)
		{
			Element produectele=(Element) productnodelist.item(i);
			
			String skuCode=DOMHelper.getSubElementVauleByName(produectele, "skuCode"); 
			int normalQuantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(produectele, "normalQuantity")).intValue();
			int defectiveQuantity=Integer.valueOf(DOMHelper.getSubElementVauleByName(produectele, "defectiveQuantity")).intValue();
			
			//int totalqty=normalQuantity+defectiveQuantity;
			
			/*String sql="insert into wms_instockitem0(sheetid,customermid,"
				+"barcodeid,badflag,NotifyPrice,price,notifyqty,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) "
				+" select '"+commsheetid+"',goodsid,barcodeid,1,0.00,0.00,"+totalqty+","+totalqty+
				","+defectiveQuantity+",0,0,pknum,pkname,pkspec,17.00 "
				+"from barcode where custombc='"+skuCode+"' ";*/
			String sql = new StringBuilder().append("insert into wms_instockitem0(sheetid,customermid,")
			.append("barcodeid,badflag,NotifyPrice,price,inqty,InBadQty,NotifyPQty,InPQty,pknum,pkname,pkspec,Taxrate) ")
			.append(" select '").append(commsheetid)
			.append("',goodsid,barcodeid,1,0.00,0.00,").append(normalQuantity)
			.append(",").append(defectiveQuantity).append(",0,0,pknum,pkname,pkspec,17.00 ")
			.append("from barcode (nolock) where ").append(isBarcodeId?"barcodeid='":"custombc='")
			.append(skuCode).append("'").toString();
			
			SQLHelper.executeSQL(this.getExtconnection(), sql);
		}
	}

}
