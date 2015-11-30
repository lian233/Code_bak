package com.wofu.intf.yjn;

import java.sql.Connection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

/**
 * 
 * �����������ݴ���
 *
 */
public class OrderProcess extends DtcProcess{

	@Override
	public void process() throws Exception {
		Document doc = DOMHelper.newDocument(this.getBizdata(),"gbk");
		Element ele = doc.getDocumentElement();
		Element dtcFlow = DOMHelper.getSubElementsByName(ele, "DTCFlow")[0];
		Element orderEle = DOMHelper.getSubElementsByName(dtcFlow, "ORDER_INFO_FB")[0];
		String original_order_no = DOMHelper.getSubElementVauleByName(orderEle, "ORIGINAL_ORDER_NO");
		String order_status = DOMHelper.getSubElementVauleByName(orderEle, "STATUS_CODE");
		String sql = "select count(*) from outstock0 where custompursheetid='"+original_order_no+"'";
		if(SQLHelper.intSelect(getConn(), sql)!=0){
			if("70".equals(order_status)){   //���
				sql = "exec IF_OuterToOutStock '"+original_order_no+"',"+100;
				SQLHelper.executeSQL(this.getConn(), sql);
			}else{
				sql = new StringBuilder().append("update outstock0 set InfFlag=")
				.append(order_status).append(" where custompursheetid='").append(original_order_no)
				.append("'").toString();
				SQLHelper.executeSQL(this.getConn(), sql);
			}
		}else{
			Connection conn=null;
			try{
				sql ="select dsname from ecs_extds where dsid="+this.getExtConnId();
				String extDsNama = SQLHelper.strSelect(this.getConn(), sql);
				conn= PoolHelper.getInstance().getConnection(extDsNama);
				if("70".equals(order_status)){   //���
					sql = "exec IF_OuterToOutStock '"+original_order_no+"',"+100;
					SQLHelper.executeSQL(conn, sql);
				}else{
					sql = new StringBuilder().append("update outstock0 set InfFlag=")
					.append(order_status).append(" where custompursheetid='").append(original_order_no)
					.append("'").toString();
					SQLHelper.executeSQL(conn, sql);
				}
			}finally{
				if(conn!=null){
					conn.close();
				}
			}
		}
		
		
		Log.info("������ִ���ݴ���ɹ�,ԭʼ������: "+original_order_no+" ����״̬Ϊ: "+order_status);
		
	}

}
