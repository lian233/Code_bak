package com.wofu.netshop.common.fenxiao;
/**
 * 查询快递路由  直到终结为止
 */
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.job.Executer;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.intf.bestdelivery.utils.DeliveryInfoHelper;
import com.wofu.intf.bestdelivery.utils.DeliveryInfoUtil;
import com.wofu.netshop.common.fenxiao.entity.CustomerDeliveryNumBook;
import com.wofu.netshop.common.fenxiao.entity.DeliveryRoute;
public class QueryDeliveryInfo extends Executer{
	private String jobName="查询已经使用过的快递信息";
	@Override
	public void run() {
		try{
			updateJobFlag(1);
			String[] dateFormats = new String[]{"yyyy-MM-dd HH:mm:ss"};  
		    JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(dateFormats)); 
			//加载所有快递参数
			/**
			String sql =new StringBuilder("select code,clientid,partnerkey,userid,appkey,v,url,secretKey,queryUrl,")
			.append("case when len(deliverysheetid)>0 then left(deliverysheetid,(len(deliverysheetid)-1)) ")
			.append("else left(deliverysheetid,(len(deliverysheetid))) end as deliverydata ")
			.append("from (select code,clientid,partnerkey,userid,appkey,v,url,secretKey,queryUrl,")
			.append("(select deliverysheetid+',' from ")
			.append("( select code,clientid,partnerkey,userid,appkey,v,url,secretKey,queryUrl,deliverysheetid from")
			.append("(select a.code,a.clientid,a.partnerkey,a.userid,a.appkey,a.v,a.url,")
			.append("a.secretKey,a.queryUrl, b.deliverysheetid from decdelivery a with (nolock) ,")
			.append("CustomerDeliveryNumBook b with (nolock)  where a.id=b.deliveryid and b.SheetType = 400100 ")
			.append("and b.RouteFlag <2) as a")
			.append(") as b where c.code=b.code for xml path('')) as deliverysheetid")
			.append(" from(select a.code,a.clientid,a.partnerkey,a.userid,a.appkey,")
			.append("a.v,a.url,a.secretKey,a.queryUrl ,b.deliverysheetid from decdelivery a with (nolock) ")
			.append(",CustomerDeliveryNumBook b with (nolock)  where a.id=b.deliveryid and b.SheetType = ")
			.append("400100 and b.RouteFlag <2 ) as c  group by code,clientid,partnerkey,userid,appkey,v,")
			.append("url,secretKey,queryUrl) as subquery").toString();
			**/
			//加载快递参数
			String sql ="select id,code,clientid,partnerkey,userid,appkey,v,url,secretKey,queryUrl from decdelivery";
			Vector result = this.getDao().multiRowSelect(sql);
			HashMap<String,Object> deliveryparam = new HashMap<String,Object>();
			for(Iterator  it = result.iterator();it.hasNext();){
				Hashtable ht = (Hashtable)it.next();
				deliveryparam.put(ht.get("id").toString(), ht);
			}
			sql = "select id ,deliveryid,isnull(deliverysheetid,'') deliverysheetid from CustomerDeliveryNumBook where SheetType = 400100"
			+" and RouteFlag <2";
			result = this.getDao().multiRowSelect(sql);
			
			for(Iterator it = result.iterator();it.hasNext();){
				Hashtable ht = (Hashtable)it.next();
				Integer id = (Integer)ht.get("id");
				String deliveryCode = ht.get("deliveryid").toString();
				String deliverydata = ht.get("deliverysheetid").toString();
				if("".equals(deliverydata)) continue;
				Hashtable par = (Hashtable)deliveryparam.get(deliveryCode);
				par.put("deliverydata", deliverydata);
				if("YTO".equalsIgnoreCase(par.get("code").toString())){//圆通
					continue;
					/**
					String info = DeliveryInfoUtil.getYTODeliveryRouteInfo(par);
					Log.info("id: "+id+"路由信息: "+info);
					JSONArray array = DeliveryInfoHelper.hktyXmlToJson(info);
					processYTOInfo(this.getDao(),array,id);
					**/
				}else if("HTKY".equalsIgnoreCase(par.get("code").toString())){//汇通
					String info = DeliveryInfoUtil.getHKDeliveryRouteInfo(par);
					//Log.info("id: "+id+"路由信息: "+info);
					JSONArray array = DeliveryInfoHelper.hktyXmlToJson(info);
					Log.info("array: "+array.toString());
					processHTYKInfo(this.getDao(),array,id);
				}
			}
			UpdateTimerJob();
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		}catch(Exception e){
			e.printStackTrace();
			Log.error(jobName, e.getMessage());
			try {
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			try {
				updateJobFlag(0);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(this.getConnection()!=null){
				try {
					this.getConnection().close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	//处理汇通路由信息
	private void processHTYKInfo(DataCentre dao,JSONArray array,int id) throws Exception{
		String mailNo = array.getJSONObject(0).getString("mailNo");
		Pattern pattern = Pattern.compile("[0-9a-zA-Z]{1,}");
		Matcher match = pattern.matcher(mailNo);
		if(!match.matches()){
			Log.info("id: "+id+",快递单号不全部是数字字母");
			return;
		}
		String sql ="";
		JSONArray traces = array.getJSONObject(0).getJSONArray("traces");
		CustomerDeliveryNumBook book = new CustomerDeliveryNumBook();
		book.setId(id);
		List sqlList = new ArrayList<String>();
		for(int i=0;i<traces.size();i++){
			JSONObject obj = traces.getJSONObject(i);
			DeliveryRoute route = (DeliveryRoute)JSONObject.toBean(obj, DeliveryRoute.class);
			route.setDeliveryid(2);
			route.setDeliverySheetid(mailNo);
			if(dao.countByKeys(route,"deliveryid,deliverysheetid,routetime")==0){
				dao.insert(route, "id");
			}
			if(i==0)
			if("".equals(dao.strSelect("select BegingRouteTime from CustomerDeliveryNumBook (nolock) where id="+id))){
				sql ="update CustomerDeliveryNumBook set BegingRouteTime='"+Formatter.format(route.getRoutetime()
						, Formatter.DATE_TIME_FORMAT)+"',RouteFlag=1 where id="+id;
				Log.info("sql: "+sql);
				sqlList.add(sql);
			}
			if(i==traces.size()-1 && traces.size()>1){
				sql ="update CustomerDeliveryNumBook set EndRouteTime='"+Formatter.format(route.getRoutetime()
						, Formatter.DATE_TIME_FORMAT)+"', Position='"+route.getPosition()+"',routespan=datediff(s,begingroutetime,endroutetime)";
				if("签收".equals(route.getScantype()))
					sql +=",RouteFlag=2";
				else
					sql +=",RouteFlag=1";
				sql +=" where id="+id;
				Log.info("sql: "+sql);
				//dao.execute(sql+" where id="+id);
				sqlList.add(sql);
			}
		}
		if(sqlList.size()>0)
			dao.executeBatch(sqlList);
	}
	
	//处理圆通路由信息
	private void processYTOInfo(DataCentre dao,JSONArray array,int id) throws Exception{
		String mailNo = array.getJSONObject(0).getString("mailNo");
		String sql ="";
		JSONArray traces = array.getJSONObject(0).getJSONArray("traces");
		CustomerDeliveryNumBook book = new CustomerDeliveryNumBook();
		book.setId(id);
		for(int i=0;i<traces.size();i++){
			JSONObject obj = array.getJSONObject(i);
			DeliveryRoute route = (DeliveryRoute)obj.toBean(obj, DeliveryRoute.class);
			route.setDeliveryid(2);
			route.setDeliverySheetid(obj.getString(mailNo));
			if(dao.countByKeys(route,"deliveryid,deliverysheetid,starttime")==0){
				//dao.insert(route, "deliveryid,deliverySheetid,routetime,position,scantype,note");
				dao.insert(route, "ID");
			}
			if(i==0)
			if("".equals(dao.strSelect("select BegingRouteTime from CustomerDeliveryNumBook where id="+id))){
				sql ="update CustomerDeliveryNumBook set BegingRouteTime='"+Formatter.format(route.getRoutetime()
						, Formatter.DATE_TIME_FORMAT)+"',RouteFlag=1 where id="+id;
				dao.execute(sql);
			}
			if(i==traces.size()-1){
				sql ="update CustomerDeliveryNumBook set EndRouteTime='"+Formatter.format(route.getRoutetime()
						, Formatter.DATE_TIME_FORMAT)+"',RouteSpan=datediff(s,beginroutetime,endroutetime)"+" where id="+id+" and Position='"+route.getPosition()+"'";
				if("签收".equals(route.getScantype()))
					sql +=" and RouteFlag=2";
				dao.execute(sql);
			}
			
			
		}
	}
	
}
