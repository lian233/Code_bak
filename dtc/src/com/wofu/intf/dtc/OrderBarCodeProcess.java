package com.wofu.intf.dtc;
import java.sql.Connection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
/**
 * 
 * ���봦����
 *
 */
public class OrderBarCodeProcess extends DtcProcess{

	@Override
	public void process() throws Exception {
		Document doc = DOMHelper.newDocument(this.getBizdata(),"gbk");
		Element ele = doc.getDocumentElement();
		Element dtcFlow = DOMHelper.getSubElementsByName(ele, "DTCFlow")[0];
		Element barCodeEle = DOMHelper.getSubElementsByName(dtcFlow, "ORDER_BAR_CODE_FB")[0];
		String originalOrderNo = DOMHelper.getSubElementVauleByName(barCodeEle, "ORIGINAL_ORDER_NO");
		String orderNum = DOMHelper.getSubElementVauleByName(barCodeEle, "ORDER_NO");
		String barCode = DOMHelper.getSubElementVauleByName(barCodeEle, "BAR_CODE");
		String sql = "select count(*) from outstock0 where custompursheetid='"+originalOrderNo+"'";
		if(SQLHelper.intSelect(getConn(), sql)!=0){
			sql = new StringBuilder().append("update outstock0 set CustomsOrderNo='").append(orderNum)
			.append("',CustomsBarCode='").append(barCode).append("' where custompursheetid='")
			.append(originalOrderNo).append("'").toString();
		SQLHelper.executeSQL(this.getConn(), sql);
		sql = new StringBuilder().append("update outstocknote set CustomsOrderNo='").append(orderNum)
		.append("',CustomsBarCode='").append(barCode).append("' where custompursheetid='")
		.append(originalOrderNo).append("'").toString();
	SQLHelper.executeSQL(this.getConn(), sql);
		//д��ӿ�����  wms�ӿڵ���
		sql = "insert into  inf_downnote(sheettype,notetime,opertype,operdata,flag)"
			+" select 900000,getdate(),100,sheetid,0 from outstock0 where custompursheetid='"
			+originalOrderNo+"'";
		SQLHelper.executeSQL(this.getConn(),sql);
		}else{
			Connection conn=null;
			try{
				sql ="select dsname from ecs_extds where dsid="+this.getExtConnId();
				String extDsNama = SQLHelper.strSelect(this.getConn(), sql);
				conn= PoolHelper.getInstance().getConnection(extDsNama);
				sql = new StringBuilder().append("update outstock0 set CustomsOrderNo='").append(orderNum)
				.append("',CustomsBarCode='").append(barCode).append("' where custompursheetid='")
				.append(originalOrderNo).append("'").toString();
				SQLHelper.executeSQL(conn, sql);
				Log.info("��һ��"+sql);
				
				sql = new StringBuilder().append("update outstocknote set CustomsOrderNo='").append(orderNum)
				.append("',CustomsBarCode='").append(barCode).append("' where custompursheetid='")
				.append(originalOrderNo).append("'").toString();
				SQLHelper.executeSQL(conn, sql);
				Log.info("�ڶ���"+sql);
			
				//д��ӿ�����  wms�ӿڵ���
				sql = "insert into inf_downnote(sheettype,notetime,opertype,operdata,flag)"
					+" select 900000,getdate(),100,sheetid,0 from outstock0 where custompursheetid='"
					+originalOrderNo+"'";
				SQLHelper.executeSQL(conn,sql);
				Log.info("����Ҫ�����һ��"+sql);
			}finally{
				if(conn!=null){
					conn.close();
				}
			}
			
		}
		
		Log.info("�����ִ���ݴ���ɹ�,ԭʼ������: "+originalOrderNo+"ƽ̨������: "+orderNum+"����: "+barCode+" ����ɹ�");
		
		
	}

}
