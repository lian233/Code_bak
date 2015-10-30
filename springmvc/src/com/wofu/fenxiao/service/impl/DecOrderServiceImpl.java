package com.wofu.fenxiao.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.wofu.fenxiao.domain.DecCustomer;
import com.wofu.fenxiao.domain.DecDelivery;
import com.wofu.fenxiao.domain.DecOrder;
import com.wofu.fenxiao.domain.DecOrderItem;
import com.wofu.fenxiao.domain.DecShop;
import com.wofu.fenxiao.mapping.DecCustomerMapper;
import com.wofu.fenxiao.mapping.DecDeliveryMapper;
import com.wofu.fenxiao.mapping.DecOrderItemMapper;
import com.wofu.fenxiao.mapping.DecOrderMapper;
import com.wofu.fenxiao.mapping.DecShopMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecOrderService;
import com.wofu.fenxiao.utils.CaiNiaoGetDeliverySheetIdRunnable;
import com.wofu.fenxiao.utils.DeliveryInfoHelper;
import com.wofu.fenxiao.utils.DeliveryInfoUtil;
import com.wofu.fenxiao.utils.HtkyGetDeliverySheetIdRunnable;
import com.wofu.fenxiao.utils.LoggerNames;
import com.wofu.fenxiao.utils.OrderSendUtil;
import com.wofu.fenxiao.utils.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service("decOrderService")//springmvc注解，这里会自动生成这个类的对象，由spring管理


public class DecOrderServiceImpl  implements DecOrderService{
	@Autowired
	private DecOrderMapper decOrderMapper;

	@Autowired
	private DecOrderItemMapper decOrderItemMapper;
	@Autowired
	private DecCustomerMapper decCustomerMapper;
	@Autowired
	private DecDeliveryMapper decDeliveryMapper;
	@Autowired
	private DecShopMapper decShopapper;
	
	private static final Logger logger = LoggerFactory.getLogger(LoggerNames.LOGISTICS_COMPONENT);
	
	//查询订单数据
	@Override
	public List<DecOrder> qryDecOrder(HashMap<String,Object> map)throws Exception{
		return decOrderMapper.qryDecOrder(map);
	}
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void delete(int id) throws Exception {
		decOrderMapper.delete(id);

	}

	@Override
	public PageView query(PageView pageView, DecOrder t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void add(DecOrder t) throws Exception {
		decOrderMapper.add(t);
		
	}

	@Override
	public List<DecOrder> queryAll(DecOrder t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void update(DecOrder t) throws Exception {
		decOrderMapper.update(t);
		
	}

	@Override
	public DecOrder getById(int id) throws Exception {
		// TODO Auto-generated method stub
		return decOrderMapper.getById(id);
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public JSONArray CheckOrder(HttpSession session, JSONArray ids){
		JSONArray checkResult = new JSONArray() ;
		try{
			//订单表前缀
			String tableprefix = Tools.getFrontStr(session) ;
			System.out.println("prefix: "+tableprefix);
			//取得同步发货状态所需的信息
			List<HashMap<String,Object>> sendsList = decOrderMapper.getSendOrders(ids.toArray(),tableprefix);
			
			String idList = ""; 
			for(Iterator it=sendsList.iterator();it.hasNext();){
				JSONObject obj=null;
				HashMap item =null;
				try{
					item = (HashMap)it.next();
					/**
					HashMap<String,Object> map = new HashMap<String,Object> ();			
					map.put("CustomerID", Tools.getCurCustomerID(session));
					map.put("ID", id);
					map.put("Checker", Tools.getCurLoginCName(session));
					map.put("SetFlag", flag);
					decOrderMapper.stDecOrder(map);
					**/
					
					if((Integer)item.get("channelid")==1){//淘宝发货
						obj = OrderSendUtil.taobao(item);
					}
					else if((Integer)item.get("channelid")==2){//京东
						obj = OrderSendUtil.jingDong(item);
					}
					else if((Integer)item.get("channelid")==3){//蘑菇街
						obj = OrderSendUtil.mogujie(item);
						//obj = OrderSendUtil.getmogujieorder(item);//test
						
					}
					else if((Integer)item.get("channelid")==4){//美丽说
						obj = OrderSendUtil.meilisuo(item);
					}else{
						obj = new JSONObject();
						obj.put("errorCode", 0);
					}
					
					logger.info("obj返回code:"+obj.getInt("errorCode"));
					if(0==obj.getInt("errorCode")){  //发货成功更新发货标志
						String id= String.valueOf((Integer)item.get("id"));
						if ((","+idList+",").indexOf(","+id+",")<0){//找不到则加入
							if ("".equals(idList)){
								idList = id;
							}
							else{
								idList = idList +","+id;
							}
						}
					}					
					
				} catch (Exception e) { 
					e.printStackTrace();
					obj = new JSONObject();
					obj.put("id",item.get("id"));
					obj.put("errorCode", 10);
					obj.put("msg","发货失败："+e.getMessage());
				}
				
				checkResult.add(obj);
			}
			
			//更新发货
			logger.info("成功列表："+idList);
			if (!"".equals(idList)){
				HashMap<String, Object> params2 = new HashMap<String,Object>();
				params2.put("front", Tools.getFrontStr(session) );
				params2.put("bak", "0");
				params2.put("idList", idList);
				decOrderMapper.updateOutFlag(params2);
				String[] is = idList.split(",");
				//public void BakOrder(HttpSession session, int id, int flag) throws Exception {
				for(int i=0;i<is.length;i++){//转到正式表
					BakOrder(session,Integer.parseInt(is[i]),100);
					logger.info("转正式成功："+is[i]);
				}
			}
			
			
		}catch(Exception e){
			logger.info("订单发货失败: "+e.getMessage());
		}
		return checkResult;				
		
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void BakOrder(HttpSession session, int id, int flag) throws Exception {
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();			
			map.put("CustomerID", Tools.getCurCustomerID(session));
			map.put("ID", id);
			map.put("Checker", Tools.getCurLoginCName(session));
			map.put("SetFlag", flag);
			decOrderMapper.tlBakDecOrder(map);
		} catch (Exception e) { 
			throw new Exception("取消订单失败：" + e.getMessage());
		}
		
	}	
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public String StopDecOrder(HttpSession session , int id) throws Exception {
		String ret = "";
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();			
			map.put("ID", id);
			map.put("Checker", Tools.getCurLoginCName(session));
			decOrderMapper.tlStopDecOrder(map);
			ret = map.get("Msg").toString();
		} catch (Exception e) { 
			//throw new Exception("取消订单失败：" + e.getMessage());
			ret = "取消订单失败：" + e.getMessage();
		}
		return ret;		
	}		

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public String CheckDecOrder(HttpSession session , int id) throws Exception {
		String ret = "";
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();			
			map.put("ID", id);
			map.put("Checker", Tools.getCurLoginCName(session));	
			decOrderMapper.tlCheckDecOrder(map);
			ret = map.get("Msg").toString();
			
		} catch (Exception e) { 
			//throw new Exception("取消订单失败：" + e.getMessage());
			ret = "审核订单失败：" + e.getMessage();
			
		}
		return ret;		
	}		
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void mergeDecOrder(HttpSession session) throws Exception {
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();			
			map.put("CustomerID", Tools.getCurCustomerID(session));
			map.put("Checker", Tools.getCurLoginCName(session));
			decOrderMapper.tlMergeDecOrderAuto(map);
		} catch (Exception e) { 
			throw new Exception("合并订单失败：" + e.getMessage());
		}		
				
	}


	@Override
	public List<DecOrderItem> qryDecOrderItem(HashMap<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return decOrderItemMapper.qryDecOrderItem(map);
		
	}
	
	@Override
	public DecOrderItem getItemById(int id) throws Exception {
		// TODO Auto-generated method stub
		return decOrderItemMapper.getById(id);
	}
	


	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void deleteItem(int id) throws Exception {
		decOrderItemMapper.delete(id);
		
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void addItem(DecOrderItem t) throws Exception {
		decOrderItemMapper.add(t);
		
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void updateItem(DecOrderItem t) throws Exception {
		decOrderItemMapper.update(t);
		
	}

	@Override
	public List<DecOrder> qryDecOrderList(HashMap<String, Object> map)throws Exception {
		return decOrderMapper.qryDecOrderList(map);
	}
	
	//批量确认订单c.DeliveryNoType=2  and isnull(a.DeliverySheetID,'')='' and 
	public JSONArray confirmDecOrders(JSONArray ids, HttpSession session
			,String cainiao_app_key,String cainiao_app_secret,String cainiao_token,String cainiao_user_id,String cainiao_url) throws Exception{
		JSONArray array = new JSONArray();
		JSONObject obj =null;
		CountDownLatch latch=null;
		//多线程结果集存放类
		ConcurrentHashMap<String,Object> currentMap = new ConcurrentHashMap<String,Object>();
		//菜鸟物流
		List<HashMap> caiNiao = new ArrayList<HashMap>();
		//汇通
		List<HashMap> htky = new ArrayList<HashMap>();
		HashMap<String,String> params =new HashMap<String,String>();
		params.put("url", cainiao_url);//session
		params.put("app_key", cainiao_app_key);
		params.put("session", cainiao_app_secret);
		params.put("token", cainiao_token);
		params.put("user_id", cainiao_user_id);
		HashMap dn=null;
		try{
			String tableprefix = Tools.getFrontStr(session) ;
			//取得客户快递数量
			HashMap dparam = new HashMap();
			dparam.put("customerID", Tools.getCurCustomerID(session));
			List<HashMap> deliveryNum = decDeliveryMapper.queryCustomerDeliveryNum(dparam);
			logger.info("取得快递数量数据：" + deliveryNum.toString());
			dn = new HashMap();
			for (int i=0;i<deliveryNum.size();i++){
				HashMap h = deliveryNum.get(i);
				dn.put(h.get("DeliveryID").toString(), h.get("Qty").toString());
			}
			//取发件人信息
			DecCustomer customer = decCustomerMapper.getById(Tools.getCurCustomerID(session));
			//获取订单信息
			List<HashMap> orders = decOrderMapper.getDeliveryOrder(ids.toArray(),tableprefix);
			//统计菜鸟物流的单数
			int count =0;
			int caiNiaoCount =0;
			HashMap map=null;
			String sheetid=null;
			List<DecOrderItem> item =null; 
			boolean hadGet = false;
			boolean  isSuccess=false;
			String errorMsg="";
			for(Iterator it = orders.iterator();it.hasNext();){
				obj =null;
				map = (HashMap)it.next();
				params.put("deliveryname", map.get("deliveryname").toString());
				if((Short)map.get("deliveryNoType")==3){
					System.out.println("菜鸟物流多线程处理。。。。1");
					caiNiao.add(map);
					caiNiaoCount++;
				}
					
				else if((Short)map.get("deliveryNoType")==3 && "HTKY".equals(map.get("deliveryname").toString().trim())){
					htky.add(map);
					count++;
				}
				
			}
			if(count+caiNiaoCount>0){
				System.out.println("菜鸟物流多线程处理。。。2。");
				latch= new CountDownLatch((caiNiaoCount%10==0?caiNiaoCount/10:caiNiaoCount/10+1)+count);
				
			}
			if(caiNiao.size()>0){
				System.out.println("菜鸟物流多线程处理。。。。3");
				if(caiNiao.size()<=10)
					new Thread(new CaiNiaoGetDeliverySheetIdRunnable(latch,caiNiao,params,customer,currentMap)).start();
				else{
					System.out.println("菜鸟物流多线程处理。。。。");
					List<HashMap> temp = new ArrayList<HashMap>();
					for(int i=0;i<caiNiao.size();i++){
						if(temp.size()==10){
							new Thread(new CaiNiaoGetDeliverySheetIdRunnable(latch,temp,params,customer,currentMap)).start();
							temp.clear();
						}
						temp.add(caiNiao.get(i));
						
					}
					if(temp.size()>0)
						new Thread(new CaiNiaoGetDeliverySheetIdRunnable(latch,temp,params,customer,currentMap)).start();
						
				}
			}
			if(htky.size()>0){
				//if()//判断快递可用数量
				for(int i=0;i<htky.size();i++){
					new Thread(new HtkyGetDeliverySheetIdRunnable(latch,htky.get(i),customer,currentMap)).start();
				}
			}
			if(latch!=null){
				//处理多线程返回结果
				latch.await();//多线程任务完成
				for(Iterator iter  = currentMap.keySet().iterator();iter.hasNext();){
					String key = (String)iter.next();
					obj = new JSONObject();
					int qty = 0; 
					HashMap<String,Object> returnMap = (HashMap<String,Object>)currentMap.get(key);
					String deliveryID = returnMap.get("deliveryid").toString();
					if (dn.containsKey(deliveryID)){
						try{
							logger.info("数量："+dn.get(deliveryID).toString());
							qty = Integer.parseInt(dn.get(deliveryID).toString());
						}catch(Exception ee){
							logger.info("取map列表数据失败:" + ee.getMessage());
						}
					}
					
					if((Integer)returnMap.get("errcode")==0){
						//更新decorder表数据
						returnMap.put("tableprefix", tableprefix);
						returnMap.put("id",key);
						returnMap.put("flag",10);
						decOrderMapper.updateDeliveryInfo(returnMap);
						hadGet = true;
						isSuccess=true;
					}else{
						errorMsg=returnMap.get("errmsg").toString();
					}
					if(isSuccess){//没有失败
						obj.put("errorCode", 0);
						obj.put("flag", 10);
						obj.put("ID", key);
						obj.put("msg", "订单确认成功");
						obj.put("deliveryID", returnMap.get("deliveryid").toString());
						if (returnMap.containsKey("deliverysheetid")){
							obj.put("deliverySheetID", returnMap.get("deliverysheetid"));
						}
						else{
								obj.put("deliverySheetID","");
						}
					}else{
						obj.put("errorCode", 1);
						obj.put("ID", key);
						obj.put("msg", "订单确认失败: "+errorMsg);
						obj.put("deliveryid", returnMap.get("deliveryid").toString());
						obj.put("deliverysheetid ", "");
					}
					if (hadGet){
						logger.info("减数");
						try{
							qty = qty - 1;
							dn.put(returnMap.get("deliveryid").toString(), qty);
						}catch(Exception ex){
							logger.info("更新数据出错：" + qty);
						}
						//调用成功，调用存储过程统计快递信息							
						String deliverysheetid="";
						if (returnMap!=null){
							if (returnMap.get("deliverysheetid")!=null)
							deliverysheetid=returnMap.get("deliverysheetid").toString();
						}
						HashMap param = new HashMap();
						param.put("SID", key);
						param.put("SheetType", 400100);
						param.put("CustomerID", customer.getId());
						param.put("DeliveryID", returnMap.get("deliveryid"));
						param.put("Qty", -1);
						param.put("Note", deliverysheetid);
						
						param.put("DeliverySheetID", deliverysheetid);
						param.put("State", map.get("state"));
						param.put("City", map.get("city"));
						param.put("District", map.get("district"));
						param.put("Address", map.get("address"));
						param.put("LinkMan", map.get("linkman"));							
						param.put("Mobile", map.get("tele"));
						decDeliveryMapper.addCustomerDeliveryNum(param);
					}
					array.add(obj);
					
				}
				//判断订单明细数量
				//统计
				//菜鸟多线程临时存储
				System.out.println("多线程任务处理完成。。。。");
			}
			
			hadGet = false;
			isSuccess=false;
			for(Iterator it = orders.iterator();it.hasNext();){
				try{
					map = (HashMap)it.next();
					if(map.get("items").getClass()==List.class){
						item = (List<DecOrderItem>)map.get("items");
						
					}else{
						DecOrderItem tt = (DecOrderItem)map.get("items");
						item = new ArrayList<DecOrderItem>();
						item.add(tt);
						
					}
					int qty = 0; 
					String deliveryID = map.get("deliveryid").toString();
					if (dn.containsKey(deliveryID)){
						try{
							logger.info("数量："+dn.get(deliveryID).toString());
							qty = Integer.parseInt(dn.get(deliveryID).toString());
						}catch(Exception ee){
							logger.info("取map列表数据失败:" + ee.getMessage());
						}
					}
					String deliverycompany = map.get("deliveryname").toString().trim();
					String oid = map.get("deliverysheetid")!=null?map.get("deliverysheetid").toString():"";
					logger.info("原快递单号："+oid);
					HashMap<String,Object> returnMap = null;
					
					if((("".equals(oid)) || (null==oid))&& (Short)map.get("deliveryNoType")==2){ //热敏类、未取单，则取单
						logger.info("剩余快递单数量:"+qty);
						if (qty<=0){
							obj.put("errorCode", 100);
							obj.put("ID", map.get("id"));
							obj.put("msg", "订单【"+map.get("linkman")+"】确认失败，【"+deliverycompany+"】快递单号剩余数量不足");	
							array.add(obj);
							continue;
						}
						
						if(deliverycompany.equalsIgnoreCase("YTO")){
							obj = new JSONObject();
							returnMap = DeliveryInfoUtil.getYtoDeliveryInfo(map, customer, item);						
							if((Integer)returnMap.get("errcode")==0){
								//更新decorder表数据
								returnMap.put("tableprefix", tableprefix);
								returnMap.put("id",map.get("id"));
								returnMap.put("flag",10);
								decOrderMapper.updateDeliveryInfo(returnMap);
								hadGet = true;
								isSuccess=true;
							}else{
								errorMsg=returnMap.get("errmsg").toString();
							}							
						}
						else if("HTKY".equalsIgnoreCase(deliverycompany)){
							continue;
							/**
							returnMap = DeliveryInfoUtil.getHkDeliveryInfo(map, customer, item);
							//returnMap = new HashMap<String,Object>();
							//hadGet = true;
							logger.info("进入汇通取单");
							if((Integer)returnMap.get("errcode")==0){
								//更新decorder表数据
								returnMap.put("tableprefix", tableprefix);
								returnMap.put("id",map.get("id"));
								returnMap.put("flag",10);
								//取得大头笔
								if ((returnMap.get("destCode")==null)||(returnMap.get("destCode").equals(""))){
									decDeliveryMapper.tlGetDecDeliveryAddressID(map);
									returnMap.put("destCode", map.get("addressid"));
								}							
								decOrderMapper.updateDeliveryInfo(returnMap);
								hadGet = true;
								isSuccess=true;
							}else{
								errorMsg=returnMap.get("errmsg").toString();
							}
						}**/
					}else if((Short)map.get("deliveryNoType")==3){//decdelivery.DeliveryNoType=3   则是菜鸟物流
						continue;
						//取得店铺appkey等参数
						//DecShop shop = decShopapper.getById((Integer)map.get("shopid"));//shopid
						/**
						returnMap = DeliveryInfoUtil.getCAINIAODeliveryInfo(shop,map, customer, item);
						//returnMap = new HashMap<String,Object>();
						//hadGet = true;
						logger.info("进入菜鸟取单");
						if((Integer)returnMap.get("errcode")==0){
							//更新decorder表数据
							returnMap.put("tableprefix", tableprefix);
							returnMap.put("id",map.get("id"));
							returnMap.put("flag",10);
							//取得大头笔
							if ((returnMap.get("destCode")==null)||(returnMap.get("destCode").equals(""))){
								decDeliveryMapper.tlGetDecDeliveryAddressID(map);
								returnMap.put("destCode", map.get("addressid"));
							}							
							decOrderMapper.updateDeliveryInfo(returnMap);
							hadGet = true;
							isSuccess=true;
						}else{
							errorMsg=returnMap.get("errmsg").toString();
						}
						**/
						
					}else{
						//其它快递只更新flag
						isSuccess=true;
						returnMap = new HashMap<String,Object>();
						returnMap.put("flag",10);
						returnMap.put("tableprefix", tableprefix);
						returnMap.put("id",map.get("id"));
						//取得大头笔
						if ((returnMap.get("destCode")==null)||(returnMap.get("destCode").equals(""))){
							decDeliveryMapper.tlGetDecDeliveryAddressID(map);
							returnMap.put("destCode", map.get("addressid"));
						}													
						decOrderMapper.updateDeliveryInfo(returnMap);
					}
					if(isSuccess){//没有失败
						obj.put("errorCode", 0);
						obj.put("flag", 10);
						obj.put("ID", map.get("id"));
						obj.put("msg", "订单确认成功");
						obj.put("deliveryID", map.get("deliveryid"));
						if (returnMap.containsKey("deliverysheetid")){
							obj.put("deliverySheetID", returnMap.get("deliverysheetid"));
						}
						else{
								obj.put("deliverySheetID",oid);
						}
					}else{
						obj.put("errorCode", 1);
						obj.put("ID", map.get("id"));
						obj.put("msg", "订单确认失败: "+errorMsg);
						obj.put("deliveryid", map.get("deliveryid"));
						obj.put("deliverysheetid ", "");
					}
					if (hadGet){
						logger.info("减数");
						try{
							qty = qty - 1;
							dn.put(deliveryID, qty);
						}catch(Exception ex){
							logger.info("更新数据出错：" + qty);
						}
						//调用成功，调用存储过程统计快递信息							
						String deliverysheetid="";
						if (returnMap!=null){
							if (returnMap.get("deliverysheetid")!=null)
							deliverysheetid=returnMap.get("deliverysheetid").toString();
						}
						HashMap param = new HashMap();
						param.put("SID", map.get("id"));
						param.put("SheetType", 400100);
						param.put("CustomerID", customer.getId());
						param.put("DeliveryID", map.get("deliveryid"));
						param.put("Qty", -1);
						param.put("Note", deliverysheetid);
						
						param.put("DeliverySheetID", deliverysheetid);
						param.put("State", map.get("state"));
						param.put("City", map.get("city"));
						param.put("District", map.get("district"));
						param.put("Address", map.get("address"));
						param.put("LinkMan", map.get("linkman"));							
						param.put("Mobile", map.get("tele"));
						decDeliveryMapper.addCustomerDeliveryNum(param);
					}
					}
					}catch(Exception ee){
					logger.info("出错："+ee.getMessage());
					ee.printStackTrace();
					obj.put("errorCode", 1);
					obj.put("ID", map.get("id"));
					obj.put("msg", "订单确认失败: "+ee.getMessage());
					obj.put("deliveryid", map.get("deliveryid"));
					obj.put("deliverysheetid ", "");
				}
				if(obj!=null)
					array.add(obj);
				
			}//end for
		}catch(Exception e){
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		logger.info("取快递返回到页面的结果: "+array.toString());
		return array;
	}

	//输出订单打印信息
	@Override
	public JSONArray getOrderPrintInfo(HttpSession session,  List<DecOrder> orderList,JSONArray fields) throws Exception {
		
		//取得客户信息
		int customerID = Tools.getCurCustomerID(session);
		DecCustomer customer = decCustomerMapper.getById(customerID);
		if (customer==null){
			throw new Exception("找不到客户信息");
		}
		
		//取得客户店铺
		List<HashMap> shop=null;
		try{
			HashMap<String,Object> p = new HashMap<String,Object>();
			p.put("customerID", customerID);			
			shop = decShopapper.qryShopList(p);
		} catch (Exception e) {
			throw new Exception("取店铺资料出错：" + e.getMessage());
		}			
		
		//取得快递信息		
		DecDelivery delivery =  decDeliveryMapper.getById(orderList.get(0).getDeliveryID());
		if (delivery==null){
			throw new Exception("找不到快递信息");			
		}		
		
		//输出数据
		JSONArray data = new JSONArray() ;
		for(int i=0 ; i<orderList.size();i++){
			DecOrder o = orderList.get(i);
			HashMap<String,Object>  pa = new HashMap<String,Object> ();
			String shopName = Tools.getListValue(shop , "ID" , Integer.toString(o.getShopID()) , "Name");
			String shopLinkMan = Tools.getListValue(shop , "ID" , Integer.toString(o.getShopID()) , "LinkMan");
			String shopTele = Tools.getListValue(shop , "ID" , Integer.toString(o.getShopID()) , "Tele");
			
			pa.put("ID", o.getId());
			//发货人信息Sender
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			if (fields.contains("ShopName")){pa.put("ShopName", shopName);}
			if (fields.contains("CustomerName")){pa.put("CustomerName", customer.getName());}
			if (fields.contains("CustomerLinkMan")){pa.put("CustomerLinkMan", customer.getLinkMan());}
			if (fields.contains("CustomerTele")){pa.put("CustomerTele", customer.getTele());}
			if (fields.contains("CustomerMobile")){pa.put("CustomerMobile", customer.getMobile());}
			if (fields.contains("CustomerAddress")){pa.put("CustomerAddress", customer.getAddress());}
			if (fields.contains("CustomerNote")){pa.put("CustomerNote", customer.getNote());}
			if (fields.contains("ShopTele")){pa.put("ShopTele", shopTele);}
			if (fields.contains("ShopLinkMan")){pa.put("ShopLinkMan", shopLinkMan);}									
			if (fields.contains("PrintTime")){pa.put("PrintTime", format1.format(new Date()));}

			if (fields.contains("ShopName2")){pa.put("ShopName2", shopName);}
			if (fields.contains("CustomerName2")){pa.put("CustomerName2", customer.getName());}
			if (fields.contains("CustomerLinkMan2")){pa.put("CustomerLinkMan2", customer.getLinkMan());}
			if (fields.contains("CustomerTele2")){pa.put("CustomerTele2", customer.getTele());}
			if (fields.contains("CustomerMobile2")){pa.put("CustomerMobile2", customer.getMobile());}
			if (fields.contains("CustomerAddress2")){pa.put("CustomerAddress2", customer.getAddress());}
			if (fields.contains("CustomerNote2")){pa.put("CustomerNote2", customer.getNote());}
			if (fields.contains("ShopTele2")){pa.put("ShopTele2", shopTele);}
			if (fields.contains("ShopLinkMan2")){pa.put("ShopLinkMan2", shopLinkMan);}									
			if (fields.contains("PrintTime2")){pa.put("PrintTime2", format1.format(new Date()));}
			
			
			//收货人信息Receiver
			if (fields.contains("LinkMan")){pa.put("LinkMan", o.getLinkMan());}
			if (fields.contains("Mobile")){pa.put("Mobile", o.getMobile());}
			if (fields.contains("Phone")){pa.put("Phone", o.getPhone());}
			if (fields.contains("MobilePhone")){pa.put("MobilePhone", o.getMobile()+" " + o.getPhone());}
			if (fields.contains("State")){pa.put("State", o.getState());}
			if (fields.contains("City")){pa.put("City", o.getCity());}
			if (fields.contains("District")){pa.put("District", o.getDistrict());}
			if (fields.contains("Address")){pa.put("Address", o.getAddress());}
			if (fields.contains("DetailAddress")){pa.put("DetailAddress", o.getState()+" " + o.getCity() +" " + o.getDistrict()+" "+o.getAddress());}
			if (fields.contains("BuyerNick")){pa.put("BuyerNick", o.getBuyerNick());}

			if (fields.contains("LinkMan2")){pa.put("LinkMan2", o.getLinkMan());}
			if (fields.contains("Mobile2")){pa.put("Mobile2", o.getMobile());}
			if (fields.contains("Phone2")){pa.put("Phone2", o.getPhone());}
			if (fields.contains("MobilePhone2")){pa.put("MobilePhone2", o.getMobile()+" " + o.getPhone());}
			if (fields.contains("State2")){pa.put("State2", o.getState());}
			if (fields.contains("City2")){pa.put("City2", o.getCity());}
			if (fields.contains("District2")){pa.put("District2", o.getDistrict());}
			if (fields.contains("Address2")){pa.put("Address2", o.getAddress());}
			if (fields.contains("DetailAddress2")){pa.put("DetailAddress2", o.getState()+" " + o.getCity() +" " + o.getDistrict()+" "+o.getAddress());}
			if (fields.contains("BuyerNick2")){pa.put("BuyerNick2", o.getBuyerNick());}
			//订单信息Order
			if (fields.contains("RefSheetID")){pa.put("RefSheetID", o.getRefSheetID());}
			if (fields.contains("ItemContent")){pa.put("ItemContent", o.getItemContent());}
			if (fields.contains("TotalQty")){pa.put("TotalQty", o.getTotalQty());}
			String note = "";
			if (o.getNote()!=null && !o.getNote().equals("")){
				note = note+o.getNote()+" "; 	
			}
			if (o.getBuyerMemo()!=null && !o.getBuyerMemo().equals("")){
				note = note+o.getBuyerMemo()+" "; 	
			}
			if (o.getSellerMemo()!=null && !o.getSellerMemo().equals("")){
				note = note+o.getSellerMemo()+" "; 	
			}
			if (o.getBuyerMessage()!=null && !o.getBuyerMessage().equals("")){
				note = note+o.getBuyerMessage()+" "; 	
			}
			if (o.getTradeMemo()!=null && !o.getTradeMemo().equals("")){
				note = note+o.getTradeMemo()+" "; 	
			}
			
			if (fields.contains("Note")){pa.put("Note", note);}
			if (fields.contains("PayTime")){pa.put("PayTime", format1.format(o.getPayTime()));}

			if (fields.contains("RefSheetID2")){pa.put("RefSheetID2", o.getRefSheetID());}
			if (fields.contains("ItemContent2")){pa.put("ItemContent2", o.getItemContent());}
			if (fields.contains("TotalQty2")){pa.put("TotalQty2", o.getTotalQty());}
			if (fields.contains("Note2")){pa.put("Note2", note);}
			
			//快递信息Delivery
			if (fields.contains("AddressID")){pa.put("AddressID", o.getAddressID());}
			if (fields.contains("ZoneCode")){pa.put("ZoneCode", o.getZoneCode());}
			if (fields.contains("DeliveryName")){pa.put("DeliveryName", delivery.getName());}
			if (fields.contains("DeliveryCode")){pa.put("DeliveryCode", delivery.getCode());}
			if (fields.contains("DeliverySheetID")){pa.put("DeliverySheetID", o.getDeliverySheetID());}
			if (fields.contains("DeliveryNote")){pa.put("DeliveryNote", delivery.getNote());}

			if (fields.contains("AddressID2")){pa.put("AddressID2", o.getAddressID());}
			if (fields.contains("ZoneCode2")){pa.put("ZoneCode2", o.getZoneCode());}
			if (fields.contains("DeliveryName2")){pa.put("DeliveryName2", delivery.getName());}
			if (fields.contains("DeliveryCode2")){pa.put("DeliveryCode2", delivery.getCode());}
			if (fields.contains("DeliverySheetID2")){pa.put("DeliverySheetID2", o.getDeliverySheetID());}
			if (fields.contains("DeliveryNote2")){pa.put("DeliveryNote2", delivery.getNote());}
			
			//文件信息
			if (fields.contains("ReceiverText")){pa.put("ReceiverText", "收件人");}
			if (fields.contains("SendText")){pa.put("SendText", "寄件人");}
			if (fields.contains("ReceiverText2")){pa.put("ReceiverText2", "收件人");}
			if (fields.contains("SendText2")){pa.put("SendText2", "寄件人");}
			
			//自定义信息
			if (fields.contains("PrintContent1")){pa.put("PrintContent1", customer.getPrintContent1());}
			if (fields.contains("PrintContent2")){pa.put("PrintContent2", customer.getPrintContent2());}
			if (fields.contains("PrintContent3")){pa.put("PrintContent3", customer.getPrintContent3());}
			if (fields.contains("PrintContent4")){pa.put("PrintContent4", customer.getPrintContent4());}
			if (fields.contains("PrintContent5")){pa.put("PrintContent5", customer.getPrintContent5());}
			if (fields.contains("PrintContent6")){pa.put("PrintContent6", customer.getPrintContent6());}
			if (fields.contains("PrintContent7")){pa.put("PrintContent7", customer.getPrintContent7());}
			if (fields.contains("PrintContent8")){pa.put("PrintContent8", customer.getPrintContent8());}
			if (fields.contains("PrintContent9")){pa.put("PrintContent9", customer.getPrintContent9());}
			if (fields.contains("PrintContent10")){pa.put("PrintContent10", customer.getPrintContent10());}
			
			
			//条码信息
			if (fields.contains("RefSheetID_")){pa.put("RefSheetID_", o.getRefSheetID());}
			if (fields.contains("RefSheetID2_")){pa.put("RefSheetID2_", o.getRefSheetID());}
			if (fields.contains("DeliverySheetID_")){pa.put("DeliverySheetID_", o.getDeliverySheetID());}
			if (fields.contains("DeliverySheetID2_")){pa.put("DeliverySheetID2_", o.getDeliverySheetID());} 
			data.add(pa);
		}
		
		logger.info("取得打印信息数量："+data.size());  
		return data;		
	}

	@Override
	public void deleteItem(DecOrderItem t) throws Exception {
		decOrderItemMapper.delete2(t);
		
	}

	@Override
	public void delete(DecOrder t) throws Exception {
		decOrderMapper.delete2(t);
		
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void setDecKeyPicNote(String sheetID, int customerID) throws Exception {
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();		
			map.put("SheetID", sheetID);
			map.put("CustomerID", customerID);
			decOrderMapper.tlSetDecKeyPicNote(map);
		} catch (Exception e) { 
			throw new Exception("设置订单的明细信息：" + e.getMessage());
		}		
		
		
	}

	//更新打印次数
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void updatePrintTimes(HashMap<String, Object> map) throws Exception {
		decOrderMapper.updatePrintTimes(map);
		
	}

	@Override
	public DecOrder getById(int id, String bak , String front) throws Exception {
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();		
			map.put("id", id);
			map.put("bak", bak);
			map.put("front", front);
			return decOrderMapper.getByObj(map);
		} catch (Exception e) { 
			throw new Exception("取订单出错：" + e.getMessage());
		}		
					
		
	}

	@Override
	public DecOrderItem getItemById(int id, String bak , String front) throws Exception {
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();		
			map.put("id", id);
			map.put("bak", bak);
			map.put("front", front);
			return decOrderItemMapper.getByObj(map);
		} catch (Exception e) { 
			throw new Exception("取订单明细出错：" + e.getMessage());
		}		
	}

	@Override
	/**
	 * companyCode  快递公司代码
	 * outsids      快递单号集合
	 * return       查询结果
	 */
	public JSONArray qryDeliveryInfo(String companyCode, JSONArray outsids)
			throws Exception {
		JSONArray array = null;
		//获取快递参数
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<outsids.size();i++){
			if(i!=outsids.size()-1)
			sb.append(outsids.getString(i)).append(",");
			else
				sb.append(outsids.getString(i));
		}
		DecDelivery delivery = decDeliveryMapper.getByCode(companyCode);
		if("HTKY".equalsIgnoreCase(companyCode)){
				String xml = DeliveryInfoUtil.getHKDeliveryRouteInfo(delivery.getClientID(), sb.toString(), delivery.getPartnerkey(), delivery.getUrl());
				array = DeliveryInfoHelper.hktyXmlToJson(xml);
		}else if("yto".equalsIgnoreCase(companyCode)){
			JSONArray temp = new JSONArray();
			JSONObject item = new JSONObject();
			item.put("Number", sb.toString());
			temp.add(item);
			System.out.println(temp.toString());
			logger.info(DeliveryInfoUtil.getYTODeliveryRouteInfo(delivery,temp.toString()));
		}
		return array;
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void modifyDecOrderFlag(HttpSession session, int id, int flag) throws Exception {
		try{
			HashMap<String,Object> map = new HashMap<String,Object> ();			
			map.put("CustomerID", Tools.getCurCustomerID(session));
			map.put("ID", id);
			map.put("Checker", Tools.getCurLoginCName(session));
			map.put("SetFlag", flag);
			decOrderMapper.tlModifyDecOrderFlag(map);
		} catch (Exception e) { 
			throw new Exception("取消订单失败：" + e.getMessage());
		}
		
	}

	@Override
	public List<HashMap<String, Object>> qryStaDecOrderSku(HashMap<String, Object> map) throws Exception {
		return decOrderMapper.qryStaDecOrderSku(map);
	}
	
	@Override
	public List<HashMap> qryStaDecOrderSkuList(HashMap<String, Object> map) throws Exception {
		return decOrderMapper.qryStaDecOrderSkuList(map);
	}

}
