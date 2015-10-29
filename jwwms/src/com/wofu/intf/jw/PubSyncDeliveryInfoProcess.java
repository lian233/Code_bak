package com.wofu.intf.jw;
/**
 * 快递单号回传处理类
 */
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class PubSyncDeliveryInfoProcess extends JWProcess{

	@Override
	public void process() throws Exception {
		JSONObject response = new JSONObject(this.getBizData());
		String type = response.getString("type");
		String orderCode =response.getString("orderCode");    //订单号
		String companyNumber =null;//快递单号
		String companyCode = null;  //物流公司
		if("expressNo".equals(type)){   //回传快递单号
			companyNumber = response.getString("companyNumber");
			companyCode = response.getString("companyCode");
			String sql = "exec IF_OuterToOutStock '"+orderCode+"',"+90+",'"+companyCode+"','"+companyNumber+"'";
			SQLHelper.executeSQL(this.getConn(),sql);
			Log.info("订单回传数据处理","回传快递单号,订单号: "+orderCode+", 快递单号: "+companyNumber);
			//写入ems数据接口，发送快递信息到ems接口   900002代表发送到ems信息接口
			sql = "insert into inf_downnote(sheettype,notetime,opertype,operdata,flag)"
				+" select 900002,getdate(),100,sheetid,0 from outstock0 where custompursheetid='"
				+orderCode+"'";
			SQLHelper.executeSQL(this.getConn(),sql);
			
		}else{
			String sql = "exec IF_OuterToOutStock '"+orderCode+"',"+92;
			SQLHelper.executeSQL(this.getConn(),sql);
			Log.info("订单回传数据处理","订单已出库,订单号: "+orderCode+"");
		}
		
	}



}
