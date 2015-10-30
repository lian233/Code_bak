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
	
	//��ѯ��Ȩ��Ϣ
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
	
	//���»�ȡtoken
	public void getTokenInfo() throws Exception{
		String reqData = getReqData();
		Properties pro = StringUtil.getIniProperties(reqData);
		int shopid = Integer.parseInt(pro.getProperty("shopid", "101"));
		String authstr = pro.getProperty("authstr", "101").trim();
		if("".equals(authstr)){
			throw new Exception("��Ȩ�ַ�������Ϊ��");
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
			case 1://�Ա�
				obj = GetTokenHelper.getTaobaoToken(appkey,appsecret,authstr);
				if(!obj.isNull("error")) throw new Exception(obj.getString("error_description"));
				token = obj.getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate(),invaliddate=dateadd(dd,365,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 2://����
				obj  = GetTokenHelper.getJingDongToken(appkey, appsecret, authstr);
				if(obj.getInt("code")!=0) throw new Exception(obj.getString("error_description"));
				token = obj.getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate(),invaliddate=dateadd(dd,365,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 3://����
				throw new Exception("��ʱ��֧�ִ�ƽ̨");
			case 4://����
				throw new Exception("��ʱ��֧�ִ�ƽ̨");
			case 5://����ѷ
				throw new Exception("��ʱ��֧�ִ�ƽ̨");
			case 6://�Ա�����
				obj = GetTokenHelper.getTaobaoToken(appkey,appsecret,authstr);
				if(!obj.isNull("error")) throw new Exception(obj.getString("error_description"));
				token = obj.getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate(),invaliddate=dateadd(dd,365,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 7://����
				throw new Exception("��ʱ��֧�ִ�ƽ̨");
			case 8://�ַ���
				throw new Exception("��ʱ��֧�ִ�ƽ̨");
			case 9://һ�ŵ�
				throw new Exception("��ʱ��֧�ִ�ƽ̨");
			case 10://����
				throw new Exception("��ʱ��֧�ִ�ƽ̨");
			case 11://����Ͱ�
				obj = GetTokenHelper.getAlibabaToken(appkey,appsecret,authstr);
				if(obj.isNull("access_token")) throw new Exception(obj.getString("message"));
				token = obj.getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate()," +
						"refreshtoken='"+obj.getString("refresh_token")+"',invaliddate=dateadd(dd,180,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 12://Ģ����
				obj = GetTokenHelper.getMogujieToken(appkey,appsecret,authstr);
				JSONObject status = obj.getJSONObject("status");
				if(10001!=status.getInt("code")) throw new Exception(status.getString("msg"));
				token = obj.getJSONObject("result").getString("access_token");
				sql = "update ecs_org_params set token='"+token+"',lastgettokentime=getdate(),invaliddate=dateadd(dd,30,getdate()) where orgid="+shopid;
				this.getDao().execute(sql);
				break;
			case 13://����˵
				obj  = GetTokenHelper.getMeiLiShuoToken(appkey, appsecret, authstr);
				if(obj.getInt("error_code")!=0) throw new Exception(obj.getString("error_description"));
				JSONObject data = obj.getJSONObject("data");
				token = data.getString("access_token");
				System.out.println("��TOKEN"+token);
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
