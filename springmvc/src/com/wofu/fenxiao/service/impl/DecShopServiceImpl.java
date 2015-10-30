package com.wofu.fenxiao.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.JushitaJdpUserAddRequest;
import com.taobao.api.response.JushitaJdpUserAddResponse;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.fenxiao.annotation.DataSource;
import com.wofu.fenxiao.domain.Channel;
import com.wofu.fenxiao.domain.DecShop;
import com.wofu.fenxiao.domain.EcoSellerConfig;
import com.wofu.fenxiao.domain.EcsTimerpolicy;
import com.wofu.fenxiao.mapping.ChannelMapper;
import com.wofu.fenxiao.mapping.DecShopMapper;
import com.wofu.fenxiao.mapping.EcoSellerConfigMapper;
import com.wofu.fenxiao.mapping.EcsTimerpolicyMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DecShopService;
import com.wofu.fenxiao.utils.DataSourceHolder;
import com.wofu.fenxiao.utils.GetTokenHelper;
@Service("decShopService")//springmvc注解，这里会自动生成这个类的对象，由spring管理
public class DecShopServiceImpl  implements DecShopService{
	Logger logger = Logger.getLogger(DecShopServiceImpl.class);
	@Autowired
	private DecShopMapper decShopMapper;
	@Autowired
	EcoSellerConfigMapper ecoSellerConfigMapper;
	@Autowired
	EcsTimerpolicyMapper ecsTimerpolicyMapper ;
	@Autowired
	ChannelMapper channelMapper ;
	//取得渠道数据列表
	@Override
	public List<HashMap> qryChannelList(HashMap<String,String>  map)throws Exception{
		return decShopMapper.qryChannelList(map);
	}

	//查询店铺资料
	@Override
	public List<HashMap> qryShop(HashMap<String,Object>  map)throws Exception{
		return decShopMapper.qryShop(map);
	}

	@Override
	public List<HashMap> qryDShop(HashMap<String,Object>  map)throws Exception{
		return decShopMapper.qryDShop(map);
	}
	
	//取得店铺列表
	@Override
	public List<HashMap> qryShopList(HashMap<String,Object>  map)throws Exception{
		return decShopMapper.qryShopList(map);
	}

	//取得客户的店铺列表
	@Override
	public List<HashMap> qryCustomerShopList(int customerID)throws Exception{
		HashMap<String,Object> p = new HashMap<String,Object>();
		p.put("customerID", customerID);
		
		return decShopMapper.qryShopList(p);
	}
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void delete(int id) throws Exception {
		decShopMapper.delete(id);		
	}

	@Override
	public PageView query(PageView pageView, DecShop t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void addshop(DecShop t,String rds_name,int extdsid) throws Exception {
		decShopMapper.add(t);
		
	}
	
	@Override
	public DecShop getById(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DecShop> queryAll(DecShop t) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void update(DecShop t) throws Exception {
		decShopMapper.update(t);		
	}

	//生成店铺编码
	@Override
	public String MakeShopCode(int customerID) throws Exception {
		String ret = "";
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("CustomerID", customerID);
		decShopMapper.tlMakeShopCode(map);
		if ((Integer)map.get("err")==0){
			ret = (String)map.get("Code");			
		}
		else{
			throw new Exception("生成店铺编码"); 						
		}		
		
		return ret;
	}

	//获取指定网店的token
	@Override
	public String getToken(int shopid,int channelid, String appkey, String app_secret,String getTokenLink
			,int extdsid,String rds_name)
			throws Exception {
		String[] token =new String[2];
		
		// TODO Auto-generated method stub
		switch(channelid){
			case 1://淘宝
				token =  GetTokenHelper.getTaobaoToken(appkey,app_secret,getTokenLink);
				break;
			case 2://京东
				token =  GetTokenHelper.getJingDongToken(appkey,app_secret,getTokenLink);
				break;
			case 3://蘑菇街
				token[0] =GetTokenHelper.getMogujieToken(appkey,app_secret,getTokenLink);
				break;
			case 4://美丽说
				token =GetTokenHelper.getMeiLiShuoToken(appkey,app_secret,getTokenLink);
				break;
			case 5://阿里巴巴
				token =GetTokenHelper.getAlibabaToken(appkey,app_secret,getTokenLink);
				break;
		}
		if(!"".equals(token[0]))
		//更新token到数据库
			if(channelid==1)  //淘宝要更新user_id到decshop表
				decShopMapper.updateToken(token[0],"",shopid,token[1]);
			else 
				decShopMapper.updateToken(token[0],token[1],shopid,"");
		return token[0];
	}

	
	//添加淘宝rds数据
	@DataSource(name="rdsdatasource")//rds数据源
	@Transactional(propagation=Propagation.SUPPORTS,rollbackFor=Exception.class)
	public void addTaobaoRdsData(DecShop t,String url,String rds_name,int extdsid)throws Exception{
		DataSourceHolder.setDataSource("rdsdatasource");
		EcoSellerConfig ecoSellerConfig=null;
		EcsTimerpolicy ecsTimerpolicy =null;
		//添加rds数据
		TaobaoClient client=new DefaultTaobaoClient(url, t.getAppKey(), t.getSession());
		JushitaJdpUserAddRequest req=new JushitaJdpUserAddRequest();
		req.setRdsName(rds_name);
		JushitaJdpUserAddResponse response = client.execute(req , t.getToken());
		String responseStr = response.getBody();
		JSONObject res = JSONObject.fromObject(responseStr);
		if(res.containsKey("error_response")){
			JSONObject sub_msg = res.getJSONObject("error_response");
			throw new Exception(sub_msg.getString("sub_msg"));
		}
		//添加rds数据推送表记录
		if(response.getIsSuccess()){//{
			ecoSellerConfig= ecoSellerConfigMapper.qryTimeList(t.getNick());
			if(ecoSellerConfig==null){
				ecoSellerConfig = new EcoSellerConfig();
				ecoSellerConfig.setSellernick(t.getNick());
				ecoSellerConfig.setLastordertime(Formatter.parseDate(Formatter.format(new Date(),Formatter.DATE_FORMAT)+" 00:00:00", Formatter.DATE_TIME_FORMAT));
				ecoSellerConfig.setLastrefundtime(Formatter.parseDate("2099-01-01 00:00:00", Formatter.DATE_TIME_FORMAT));
				if(t.getIsUpdateStock()==1)
					ecoSellerConfig.setLastitemtime(Formatter.parseDate(Formatter.format(new Date(),Formatter.DATE_FORMAT)+" 00:00:00", Formatter.DATE_TIME_FORMAT));
				else
					ecoSellerConfig.setLastitemtime(Formatter.parseDate("2099-01-01 00:00:00", Formatter.DATE_TIME_FORMAT));
				ecoSellerConfig.setLastjxordertime(Formatter.parseDate("2099-01-01 00:00:00", Formatter.DATE_TIME_FORMAT));
				ecoSellerConfig.setLasttmrefundtime(Formatter.parseDate("2099-01-01 00:00:00", Formatter.DATE_TIME_FORMAT));
				ecoSellerConfig.setLasttmreturntime(Formatter.parseDate("2099-01-01 00:00:00", Formatter.DATE_TIME_FORMAT));
				ecoSellerConfig.setLastfxordertime(Formatter.parseDate("2099-01-01 00:00:00", Formatter.DATE_TIME_FORMAT));
				ecoSellerConfig.setLastfxrefundtime(Formatter.parseDate("2099-01-01 00:00:00", Formatter.DATE_TIME_FORMAT));
				ecoSellerConfigMapper.add(ecoSellerConfig);
			}else{
				if(t.getLastOrderTime()!=null){//更新下载订单时间
					ecoSellerConfig.setLastordertime(t.getLastOrderTime());
					ecoSellerConfigMapper.update(ecoSellerConfig);
				}
			}
			if(ecsTimerpolicyMapper.qryByParams("com.wofu.ecommerce.taobao.RDSGetOrdersExecuter", "sellernick="+t.getNick())==0){
				int id = ecsTimerpolicyMapper.qryMaxRecord();
				ecsTimerpolicy = new EcsTimerpolicy();
				ecsTimerpolicy.setId(id+1);
				ecsTimerpolicy.setActive(1);
				ecsTimerpolicy.setActiveTimes(0);
				ecsTimerpolicy.setClock("00:01:00");
				ecsTimerpolicy.setLastActive(new Date());
				ecsTimerpolicy.setNextActive(new Date());
				ecsTimerpolicy.setParams("sellernick="+t.getNick());
				ecsTimerpolicy.setNotes("定时处理"+t.getNick()+"rds数据");
				ecsTimerpolicy.setClocktype(0);
				ecsTimerpolicy.setMaxRetry(10);
				ecsTimerpolicy.setDsid(extdsid);
				ecsTimerpolicy.setErrorCount(0);
				ecsTimerpolicy.setErrorMessage("");
				ecsTimerpolicy.setExecuter("com.wofu.ecommerce.taobao.RDSGetOrdersExecuter");
				ecsTimerpolicy.setFlag(0);
				ecsTimerpolicy.setGroupname("rds");
				ecsTimerpolicyMapper.add(ecsTimerpolicy);
			}
	}
		DataSourceHolder.removeDataSource();
	}
	//添加淘宝本地定时任务
	@Transactional(propagation= Propagation.SUPPORTS,rollbackFor=Exception.class)
	public void addTaobaoLocalData(DecShop t,String url) throws Exception{
		int id=0;
		//添加订单流转定时任务
		EcsTimerpolicy ecs_timerpolicy =null;
		if(ecsTimerpolicyMapper.qryByParams("com.wofu.ecommerce.taobao.fenxiao.getOrdersExecuter", "shopid="+t.getId())==0){
		id = ecsTimerpolicyMapper.qryMaxRecord();
		ecs_timerpolicy = new EcsTimerpolicy();
		ecs_timerpolicy.setId(id+1);
		ecs_timerpolicy.setActive(1);
		ecs_timerpolicy.setActiveTimes(0);
		ecs_timerpolicy.setClock("00:03:00");
		ecs_timerpolicy.setClocktype(0);
		ecs_timerpolicy.setLastActive(new Date());
		ecs_timerpolicy.setNextActive(new Date());
		ecs_timerpolicy.setParams("sellernick="+t.getNick()+";finishisin=true;shopid="+t.getId()+";customerid="+t.getCustomerID());
		ecs_timerpolicy.setNotes("定时处理"+t.getNick()+"订单数据");
		ecs_timerpolicy.setDsid(0);
		ecs_timerpolicy.setMaxRetry(10);
		ecs_timerpolicy.setErrorCount(0);
		ecs_timerpolicy.setErrorMessage("");
		ecs_timerpolicy.setExecuter("com.wofu.ecommerce.taobao.fenxiao.getOrdersExecuter");
		ecs_timerpolicy.setFlag(0);
		ecs_timerpolicy.setGroupname("fenxiao");
		ecsTimerpolicyMapper.add(ecs_timerpolicy);
		}
		if(ecsTimerpolicyMapper.qryByParams("com.wofu.netshop.taobao.fenxiao.Taobao", "shopid="+t.getId())==0){
		//添加店总执行任务
		id = ecsTimerpolicyMapper.qryMaxRecord();
		ecs_timerpolicy = new EcsTimerpolicy();
		ecs_timerpolicy.setId(id+1);
		ecs_timerpolicy.setActive(1);
		ecs_timerpolicy.setActiveTimes(0);
		ecs_timerpolicy.setClock("00:03:00");
		ecs_timerpolicy.setClocktype(0);
		//处处迷人旗舰店分销淘宝任务
		ecs_timerpolicy.setParams("AppKey;Session;Token;name;isUpdateStock;isNeedDelivery;isgenCustomerRet;shopid="+t.getId());
		ecs_timerpolicy.setNotes("定时处理"+t.getNick()+"分销淘宝任务");
		ecs_timerpolicy.setLastActive(new Date());
		ecs_timerpolicy.setNextActive(new Date());
		ecs_timerpolicy.setDsid(0);
		ecs_timerpolicy.setMaxRetry(10);
		ecs_timerpolicy.setErrorCount(0);
		ecs_timerpolicy.setErrorMessage("");
		ecs_timerpolicy.setExecuter("com.wofu.netshop.taobao.fenxiao.Taobao");
		ecs_timerpolicy.setFlag(0);
		ecs_timerpolicy.setGroupname("fenxiao");
		ecsTimerpolicyMapper.add(ecs_timerpolicy);
		}
		//更新分销库存
		if(ecsTimerpolicyMapper.qryByParams("com.wofu.ecommerce.taobao.fenxiao.SynDistributionStockExecuter", "shopid="+t.getId())==0){
			id = ecsTimerpolicyMapper.qryMaxRecord();
			ecs_timerpolicy = new EcsTimerpolicy();
			ecs_timerpolicy.setId(id+1);
			ecs_timerpolicy.setActive(1);
			ecs_timerpolicy.setActiveTimes(0);
			ecs_timerpolicy.setClock("00:03:00");
			ecs_timerpolicy.setClocktype(1);
			//处处迷人旗舰店分销淘宝任务
			StringBuilder params = new StringBuilder("url=");
			params.append(url).append(";appkey=").append(t.getAppKey())
			.append(";appsecret=").append(t.getSession())
			.append(";authcode=").append(t.getToken())
			.append(";shopid=").append(t.getId())
			.append(";customerid=").append(t.getCustomerID())
			.append(";username=").append(t.getNick().trim());
			ecs_timerpolicy.setParams(params.toString());
			ecs_timerpolicy.setNotes("定时处理"+t.getNick().trim()+"更新分销库存任务");
			ecs_timerpolicy.setLastActive(new Date());
			ecs_timerpolicy.setNextActive(new Date());
			ecs_timerpolicy.setDsid(0);
			ecs_timerpolicy.setMaxRetry(10);
			ecs_timerpolicy.setErrorCount(0);
			ecs_timerpolicy.setErrorMessage("");
			ecs_timerpolicy.setExecuter("com.wofu.ecommerce.taobao.fenxiao.SynDistributionStockExecuter");
			ecs_timerpolicy.setFlag(0);
			ecs_timerpolicy.setGroupname("fenxiao");
			ecsTimerpolicyMapper.add(ecs_timerpolicy);
		}
		
		//下载分销商品
		if(ecsTimerpolicyMapper.qryByParams("com.wofu.ecommerce.taobao.fenxiao.getItemsExecuter", "shopid="+t.getId())==0){
			id = ecsTimerpolicyMapper.qryMaxRecord();
			ecs_timerpolicy = new EcsTimerpolicy();
			ecs_timerpolicy.setId(id+1);
			ecs_timerpolicy.setActive(1);
			ecs_timerpolicy.setActiveTimes(0);
			ecs_timerpolicy.setClock("00:03:00");
			ecs_timerpolicy.setClocktype(0);
			ecs_timerpolicy.setParams("shopid="+t.getId()+";sellernick="+t.getNick());
			ecs_timerpolicy.setNotes("定时处理"+t.getNick()+"下载分销商品任务");
			ecs_timerpolicy.setLastActive(new Date());
			ecs_timerpolicy.setNextActive(new Date());
			ecs_timerpolicy.setDsid(0);
			ecs_timerpolicy.setMaxRetry(10);
			ecs_timerpolicy.setErrorCount(0);
			ecs_timerpolicy.setErrorMessage("");
			ecs_timerpolicy.setExecuter("com.wofu.ecommerce.taobao.fenxiao.getItemsExecuter");
			ecs_timerpolicy.setFlag(0);
			ecs_timerpolicy.setGroupname("fenxiao");
			ecsTimerpolicyMapper.add(ecs_timerpolicy);
		}
	}

	@Override
	public void updateshop(DecShop c,String rds_name,int extdsid) throws Exception{
		//更新店参数
		update(c);
		if(null!=c.getToken()){//在客户管理主页修改资料不调用下面的方法
			//更新配置
			Channel channel = channelMapper.getChannelById(c.getChannelID());
			switch(c.getChannelID()){
				case 1://淘宝
					try{
						addTaobaoRdsData(c,channel.getUrl().trim(),rds_name,extdsid);
						DataSourceHolder.removeDataSource();
						addTaobaoLocalData(c,channel.getUrl());
					}catch(Exception ex){
						ex.printStackTrace();
						DataSourceHolder.removeDataSource();
						throw ex;
					}
					break;
				case 2://京东
					addJingDongData(c);
					break;
				case 3://蘑菇街
					addMogujieData(c);
					break;
				case 4://美丽说
					addMeiLiShuoData(c);
					break;
				case 6://当当
					addDangDangData(c);
					break;
			}
		}
		
	}
	//添加当当参数
       private void addDangDangData(DecShop c) throws Exception{
    	   int id=0;
   		//添加订单流转定时任务
   		EcsTimerpolicy ecs_timerpolicy =null;
   		if(ecsTimerpolicyMapper.qryByParams("com.wofu.netshop.dangdang.fenxiao.DangDang", "shopid="+c.getId())==0){
   		id = ecsTimerpolicyMapper.qryMaxRecord();
   		ecs_timerpolicy = new EcsTimerpolicy();
   		ecs_timerpolicy.setId(id+1);
   		ecs_timerpolicy.setActive(1);
   		ecs_timerpolicy.setActiveTimes(0);
   		ecs_timerpolicy.setClock("00:03:00");
   		ecs_timerpolicy.setClocktype(0);
   		ecs_timerpolicy.setLastActive(new Date());
   		ecs_timerpolicy.setNextActive(new Date());
   		ecs_timerpolicy.setParams("AppKey;Session;Token;name;isUpdateStock;isNeedDelivery;isgenCustomerRet;shopid="+c.getId());
   		ecs_timerpolicy.setNotes("定时处理"+c.getNick()+"当当分销任务");
   		ecs_timerpolicy.setDsid(0);
   		ecs_timerpolicy.setMaxRetry(10);
   		ecs_timerpolicy.setErrorCount(0);
   		ecs_timerpolicy.setErrorMessage("");
   		ecs_timerpolicy.setExecuter("com.wofu.netshop.dangdang.fenxiao.DangDang");
   		ecs_timerpolicy.setFlag(0);
   		ecs_timerpolicy.setGroupname("fenxiao");
   		ecsTimerpolicyMapper.add(ecs_timerpolicy);
   		}
		
	}

	//添加美丽说参数
	private void addMeiLiShuoData(DecShop c) throws Exception {
		int id=0;
		//添加订单流转定时任务
		EcsTimerpolicy ecs_timerpolicy =null;
		if(ecsTimerpolicyMapper.qryByParams("com.wofu.netshop.meilishuo.fenxiao.MeiLiShuo", "shopid="+c.getId())==0){
		id = ecsTimerpolicyMapper.qryMaxRecord();
		ecs_timerpolicy = new EcsTimerpolicy();
		ecs_timerpolicy.setId(id+1);
		ecs_timerpolicy.setActive(1);
		ecs_timerpolicy.setActiveTimes(0);
		ecs_timerpolicy.setClock("00:03:00");
		ecs_timerpolicy.setClocktype(0);
		ecs_timerpolicy.setLastActive(new Date());
		ecs_timerpolicy.setNextActive(new Date());
		ecs_timerpolicy.setParams("AppKey;Session;Token;name;isUpdateStock;isNeedDelivery;isgenCustomerRet;shopid="+c.getId());
		ecs_timerpolicy.setNotes("定时处理"+c.getNick()+"美丽说分销任务");
		ecs_timerpolicy.setDsid(0);
		ecs_timerpolicy.setMaxRetry(10);
		ecs_timerpolicy.setErrorCount(0);
		ecs_timerpolicy.setErrorMessage("");
		ecs_timerpolicy.setExecuter("com.wofu.netshop.meilishuo.fenxiao.MeiLiShuo");
		ecs_timerpolicy.setFlag(0);
		ecs_timerpolicy.setGroupname("fenxiao");
		ecsTimerpolicyMapper.add(ecs_timerpolicy);
		}
		
	}

	@Override
	public void add(DecShop t) throws Exception {
		decShopMapper.add(t);
		
	}
	
	//添加蘑菇街参数
	private void addMogujieData(DecShop s) throws Exception{
		int id=0;
		//添加订单流转定时任务
		EcsTimerpolicy ecs_timerpolicy =null;
		if(ecsTimerpolicyMapper.qryByParams("com.wofu.netshop.mogujie.fenxiao.MoguJie", "shopid="+s.getId())==0){
		id = ecsTimerpolicyMapper.qryMaxRecord();
		ecs_timerpolicy = new EcsTimerpolicy();
		ecs_timerpolicy.setId(id+1);
		ecs_timerpolicy.setActive(1);
		ecs_timerpolicy.setActiveTimes(0);
		ecs_timerpolicy.setClock("00:03:00");
		ecs_timerpolicy.setClocktype(0);
		ecs_timerpolicy.setLastActive(new Date());
		ecs_timerpolicy.setNextActive(new Date());
		ecs_timerpolicy.setParams("AppKey;Session;Token;name;isUpdateStock;isNeedDelivery;isgenCustomerRet;shopid="+s.getId());
		ecs_timerpolicy.setNotes("定时处理"+s.getNick()+"蘑菇街分销任务");
		ecs_timerpolicy.setDsid(0);
		ecs_timerpolicy.setMaxRetry(10);
		ecs_timerpolicy.setErrorCount(0);
		ecs_timerpolicy.setErrorMessage("");
		ecs_timerpolicy.setExecuter("com.wofu.netshop.mogujie.fenxiao.MoguJie");
		ecs_timerpolicy.setFlag(0);
		ecs_timerpolicy.setGroupname("fenxiao");
		ecsTimerpolicyMapper.add(ecs_timerpolicy);
		}
	}
	
	//添加京东参数
	private void addJingDongData(DecShop s) throws Exception{
		int id=0;
		//添加订单流转定时任务
		EcsTimerpolicy ecs_timerpolicy =null;
		if(ecsTimerpolicyMapper.qryByParams("com.wofu.netshop.jingdong.fenxiao.JingDong", "shopid="+s.getId())==0){
		id = ecsTimerpolicyMapper.qryMaxRecord();
		ecs_timerpolicy = new EcsTimerpolicy();
		ecs_timerpolicy.setId(id+1);
		ecs_timerpolicy.setActive(1);
		ecs_timerpolicy.setActiveTimes(0);
		ecs_timerpolicy.setClock("00:03:00");
		ecs_timerpolicy.setClocktype(0);
		ecs_timerpolicy.setLastActive(new Date());
		ecs_timerpolicy.setNextActive(new Date());
		ecs_timerpolicy.setParams("AppKey;Session;Token;name;isUpdateStock;isNeedDelivery;isgenCustomerRet;shopid="+s.getId());
		ecs_timerpolicy.setNotes("定时处理"+s.getNick()+"京东分销任务");
		ecs_timerpolicy.setDsid(0);
		ecs_timerpolicy.setMaxRetry(10);
		ecs_timerpolicy.setErrorCount(0);
		ecs_timerpolicy.setErrorMessage("");
		ecs_timerpolicy.setExecuter("com.wofu.netshop.jingdong.fenxiao.JingDong");
		ecs_timerpolicy.setFlag(0);
		ecs_timerpolicy.setGroupname("fenxiao");
		ecsTimerpolicyMapper.add(ecs_timerpolicy);
		}
	}
}
