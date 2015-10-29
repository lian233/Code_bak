package com.wofu.intf.jw;
/**
 * ��ݵ��Żش�������
 */
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class PubSyncDeliveryInfoProcess extends JWProcess{

	@Override
	public void process() throws Exception {
		JSONObject response = new JSONObject(this.getBizData());
		String type = response.getString("type");
		String orderCode =response.getString("orderCode");    //������
		String companyNumber =null;//��ݵ���
		String companyCode = null;  //������˾
		if("expressNo".equals(type)){   //�ش���ݵ���
			companyNumber = response.getString("companyNumber");
			companyCode = response.getString("companyCode");
			String sql = "exec IF_OuterToOutStock '"+orderCode+"',"+90+",'"+companyCode+"','"+companyNumber+"'";
			SQLHelper.executeSQL(this.getConn(),sql);
			Log.info("�����ش����ݴ���","�ش���ݵ���,������: "+orderCode+", ��ݵ���: "+companyNumber);
			//д��ems���ݽӿڣ����Ϳ����Ϣ��ems�ӿ�   900002�����͵�ems��Ϣ�ӿ�
			sql = "insert into inf_downnote(sheettype,notetime,opertype,operdata,flag)"
				+" select 900002,getdate(),100,sheetid,0 from outstock0 where custompursheetid='"
				+orderCode+"'";
			SQLHelper.executeSQL(this.getConn(),sql);
			
		}else{
			String sql = "exec IF_OuterToOutStock '"+orderCode+"',"+92;
			SQLHelper.executeSQL(this.getConn(),sql);
			Log.info("�����ش����ݴ���","�����ѳ���,������: "+orderCode+"");
		}
		
	}



}
