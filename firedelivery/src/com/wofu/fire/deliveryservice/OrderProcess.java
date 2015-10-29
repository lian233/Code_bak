package com.wofu.fire.deliveryservice;
import java.util.HashMap;
import java.util.Map;

import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.common.tools.util.log.Log;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * ����������  д��ns_customerorder
 *
 *
 */
public class OrderProcess extends CProcessor{

	@Override
	public void process() throws Exception {
		String[] dateFormats = new String[]{"yyyy-MM-dd HH:mm:ss"};
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(dateFormats));
		JSONArray orders = JSONObject.fromObject(this.getBizData()).getJSONArray("Orders");
		for(int i=0;i<orders.size();i++){
			JSONObject obj = orders.getJSONObject(i);
			Map<String,Class> classMap = new HashMap<String,Class>();
			classMap.put("detail", Detail.class);
			Order o = (Order)JSONObject.toBean(obj,Order.class,classMap);
			switch(o.getStatus()){
				case 1://��Ӷ���
					try{
						OrderUtils.createOrder(new ECSDao(this.getExtconnection()),o,this.getTradecontactid(),this.getUsername(),this.getUserId());
					}catch(Exception e){
						Log.error("�羳��������д��ʧ��", e.getMessage());
					}
					break;
					
				case 0://ȡ������
					break;
			}
			
			
			
		}
		
	}

}
