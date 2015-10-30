package com.wofu.ecommerce.authorization;

import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class ECS_authorization extends BusinessObject{
	private String shopname;
	private String appkey;
	private String appsecret;
	private String token;
	private Date lastgettokentime;
	private Date invaliddate;
	
	//查询授权信息
	public void getshopauthorization() throws Exception{
		String reqData = getReqData();
		Properties pro = StringUtil.getIniProperties(reqData);
		int shopid = Integer.parseInt(pro.getProperty("shopid", "101"));
		//String sql ="select shopname ,appkey,appsecret,token,lastgettokentime,invaliddate from ecs_org_params where orgid="+shopid;
		Hashtable ht = getTokenInfoUtil(shopid);
		this.getMapData(ht);
		Log.info("result: "+this.toJSONObject());
		this.OutputStr("["+this.toJSONObject()+"]");
		
	}
	
	//重新获取token
	public void getTokenInfo() throws Exception{
		String reqData = getReqData();
		Properties pro = StringUtil.getIniProperties(reqData);
		int shopid = Integer.parseInt(pro.getProperty("shopid", "101"));
		String authstr = pro.getProperty("authstr", "101").trim();
		if("".equals(authstr)){
			throw new Exception("授权字符串不能为空");
		}
		String sql ="select platformid,appkey,appsecret from ecs_org_params where orgid="+shopid;
		Hashtable ht = this.getDao().oneRowSelect(sql);
		int platformId = (Integer)ht.get("platformid");
		String appkey = ht.get("appkey").toString();
		String appsecret = ht.get("appsecret").toString();
		String token ="";
		JSONObject obj=null;
		//String appkey = ht.get("appkey").toString();
		switch(platformId){
			case 1://淘宝
				obj = GetTokenHelper.getTaobaoToken(appkey,appsecret,authstr);
				if(!obj.isNull("error")) throw new Exception(obj.getString("error_description"));
				token = obj.getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate(),invaliddate=dateadd(dd,365,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 2://京东
				obj  = GetTokenHelper.getJingDongToken(appkey, appsecret, authstr);
				if(obj.getInt("code")!=0) throw new Exception(obj.getString("error_description"));
				token = obj.getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate(),invaliddate=dateadd(dd,365,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 3://当当
				throw new Exception("暂时不支持此平台");
			case 4://拍拍
				throw new Exception("暂时不支持此平台");
			case 5://亚马逊
				throw new Exception("暂时不支持此平台");
			case 6://淘宝分销
				obj = GetTokenHelper.getTaobaoToken(appkey,appsecret,authstr);
				if(!obj.isNull("error")) throw new Exception(obj.getString("error_description"));
				token = obj.getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate(),invaliddate=dateadd(dd,365,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 7://凡客
				throw new Exception("暂时不支持此平台");
			case 8://乐峰网
				throw new Exception("暂时不支持此平台");
			case 9://一号店
				throw new Exception("暂时不支持此平台");
			case 10://苏宁
				throw new Exception("暂时不支持此平台");
			case 11://阿里巴巴
				obj = GetTokenHelper.getAlibabaToken(appkey,appsecret,authstr);
				if(obj.isNull("access_token")) throw new Exception(obj.getString("message"));
				token = obj.getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate()," +
						"refreshtoken='"+obj.getString("refresh_token")+"',invaliddate=dateadd(dd,180,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 12://蘑菇街
				obj = GetTokenHelper.getMogujieToken(appkey,appsecret,authstr);
				JSONObject status = obj.getJSONObject("status");
				if(10001!=status.getInt("code")) throw new Exception(status.getString("msg"));
				token = obj.getJSONObject("result").getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate(),invaliddate=dateadd(dd,30,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 13://美丽说
				obj  = GetTokenHelper.getMeiLiShuoToken(appkey, appsecret, authstr);
				if(obj.getInt("error_code")!=0) throw new Exception(obj.getString("error_description"));
				JSONObject data = obj.getJSONObject("data");
				token = data.getString("access_token");
				System.out.println("新TOKEN"+token);
				String refreshtoken = data.getString("refresh_token");
				sql = "update ecs_org_params set token='"+token+"',refreshtoken='"+refreshtoken+"' ,lastgettokentime=getdate(),invaliddate=dateadd(dd,30,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
		}
		ht = getTokenInfoUtil(shopid);
		this.getMapData(ht);
		Log.info("result: "+this.toJSONObject());
		this.OutputStr("["+this.toJSONObject()+"]");

	}
	
	private Hashtable getTokenInfoUtil(int shopid) throws Exception{
		String sql ="select shopname ,appkey,appsecret,token,lastgettokentime,invaliddate from ecs_org_params where orgid="+shopid;
		Hashtable ht = this.getDao().oneRowSelect(sql);
		return ht;
	}

	public String getShopname() {
		return shopname;
	}

	public void setShopname(String shopname) {
		this.shopname = shopname;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getLastgettokentime() {
		return lastgettokentime;
	}

	public void setLastgettokentime(Date lastgettokentime) {
		this.lastgettokentime = lastgettokentime;
	}

	public Date getInvaliddate() {
		return invaliddate;
	}

	public void setInvaliddate(Date invaliddate) {
		this.invaliddate = invaliddate;
	}
	
	
	
		
	

}
