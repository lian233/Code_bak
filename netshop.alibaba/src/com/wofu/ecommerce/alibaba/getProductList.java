package com.wofu.ecommerce.alibaba;
/**
 * ��ȡ����Ͱ��й���վ��Ա���еĲ�Ʒ
 */
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;

public class getProductList extends Thread {
	private static String jobname = "��ȡ����Ͱ���Ʒ�б�";
	
	private static String apiName="offer.getPublishOfferList";//"offer.getAllOfferList";
	
	private static String lasttimeconfvalue=Params.username+"ȡ�ϼ���Ʒ����ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private boolean is_importing=false;
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static int interval = 30 ;
	
	private static String type="ALL";  //ֻ֧��SALE
	
	private static String returnFields="offerId,offerStatus,subject,amount,gmtModified,productFeatureList";
	
	private static String lasttime;
	private static String access_token=null;
	
	public getProductList(){
		setDaemon(true);
		setName(jobname);
	}
	
	
	public void run() {
		//��ȡ��Ȩ���ƵĲ���
		Log.info(jobname, "����[" + jobname + "]ģ��");

		do {		
			Connection conn = null;
			is_importing = true;
			try {		
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("client_id", Params.appkey);
			    params.put("redirect_uri", Params.redirect_uri);
			    params.put("client_secret", Params.secretKey);
			    params.put("refresh_token", Params.refresh_token);
			    String returns=AuthService.refreshToken(Params.host, params);
			    JSONObject access=new JSONObject(returns);
		    	Params.token=access.getString("access_token");
		    	
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttime=PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
	
				getOnSaleProducts(conn);

			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	
	private void getOnSaleProducts(Connection conn) throws Exception{
		
		int i=0;
		int j=0;
		int pageIndex=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		ECSDao dao=new ECSDao(conn);

		Log.info("��ʼȡ����Ͱ��ϼ���Ʒ");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn, sql);
		for(int k=0;k<10;)
		{
			try
			{
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("type", type) ;
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Log.info("ȡ��Ʒʱ��Ϊ: "+startdate);
				params.put("returnFields", returnFields);
				params.put("timeStamp",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT));
				params.put("page", String.valueOf(pageIndex)) ;
				params.put("pageSize", "25") ;
				params.put("access_token", Params.token);
				params.put("orderBy", "gmt_modified:asc");
				String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,apiName,Params.version,Params.requestmodel,Params.appkey);
			    
				String responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//				Log.info("ȡ��Ʒ���Ϸ��ؽ��: "+responseText);
				//���ؽ����
				JSONObject jresp=new JSONObject(responseText);
				
				
				JSONObject jres=(JSONObject) jresp.getJSONObject("result");
			
				//���ص���Ʒ�б�����
				JSONArray jresult=jres.getJSONArray("toReturn");
				
				while(true)
				{
								
					if (jres.getInt("total")==0)
					{				
						if (i==0)		
						{
							try
							{
								//
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
					
					
					
//					if (!jres.getBoolean("success")){
//						Log.error(jobname,"��ȡ����Ͱ���Ʒ�б�ʧ��,������Ϣ:"+jresp + "," + "");
//						break;
//					}
//					else
//					{			
						
						for(int m=0;m<jresult.length();m++){
							//������Ʒ
							JSONObject j1=jresult.getJSONObject(m);
							Goods gd=new Goods();
							gd.setObjValue(gd, j1);
							String goodStatus=gd.getOfferStatus();
							Log.info("��Ʒ״̬: "+goodStatus);
							String gmtModified=CommonUtil.convertToTime(gd.getGmtModified());
							Log.info("��Ʒ����޸�ʱ��: "+gmtModified);
								//����ͬ����������ʱ��
							
			                if (Formatter.parseDate(gmtModified,Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
			                {
			                	modified=Formatter.parseDate(gmtModified,Formatter.DATE_TIME_FORMAT);
			                }
							if(goodStatus.indexOf("delete")!=-1 || goodStatus.indexOf("outdated")!=-1  ) continue;  //�̼�ɾ������Ʒ����ϵͳ
							i=i+1;
							//������Ʒ����
							long offerid=gd.getOfferId();
							//�̼�sku-itemcode
							String itemCode = "";
							JSONArray arr = new JSONArray(gd.getProductFeatureList().toString());
							for(int k1=0; k1<arr.length();k1++){
								String name = arr.getJSONObject(k1).getString("name");
								if("����".equals(name)) itemCode=(String)arr.getJSONObject(k1).getString("value");
							}
							Log.info("itemCode: "+itemCode);
							
							//����ID��õ�����Ʒ����ϸ��Ϣ��SKU   offer.get
							Hashtable<String, String> params1 = new Hashtable<String, String>() ;
							params1.put("offerId", String.valueOf(gd.getOfferId()));
							params1.put("returnFields", "offerId,offerStatus,subject,amount,type,gmtCreate,gmtModified,skuArray,productFeatureList");
							
							String urlPath1=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
							String response = ApiCallService.callApiTest(Params.url, urlPath1, Params.secretKey, params1);
							
//							Log.info("��Ʒ����: "+response);
							JSONObject res=new JSONObject(response);
							
							
							if(!res.getJSONObject("result").getBoolean("success")){
								Log.info("��ȡ����Ͱ���Ʒ��ϸ����ʧ��,offerID:"+gd.getOfferId()+"������Ϣ:"+ res.getString("error_code") + "," + res.getString("error_message"));
								continue;
							}
	
							JSONArray jarray=res.getJSONObject("result").getJSONArray("toReturn");
							Goods oo=new Goods();
							oo.setObjValue(oo,jarray.getJSONObject(0));
							StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),String.valueOf(offerid),itemCode,
									gd.getSubject(),Long.valueOf(gd.getAmountOnSale()).intValue()) ;
							
							//��ƷSKU
							if(oo.getSkuArray().getRelationData().size()>0){
								for(Iterator s=oo.getSkuArray().getRelationData().iterator();s.hasNext();){
									GoodsSKU ch=(GoodsSKU)s.next();
										Log.info("SKU "+ch.getCargoNumber()+" "+toDateFormat(oo.getGmtModified()));
										StockManager.addStockConfigSku(dao, orgid,String.valueOf(oo.getOfferId()),
												String.valueOf(ch.getSpecId()),ch.getCargoNumber(),Long.valueOf(ch.getCanBookCount()).intValue()) ;
								}
							}
							
							
			                Log.info("modified: "+modified);
						}
						
						//��ȡ������
						int total=jres.getInt("total");
						//��ҳ��
						int pageTotal=total%25==0?total/25==0?1:total/25:total/25+1;
						Log.info("��ǰҳ:��"+pageIndex);
						Log.info("��ҳ��:��"+pageTotal);
						//�ж��Ƿ�����һҳ
						
						if(pageTotal>pageIndex)
							pageIndex ++ ;
						else
						{
							break;
						}
					
				//	}	
				}//whileδ
				
				Log.info("ȡ����Ͱ��ϼ�����Ʒ��:"+String.valueOf(i)+" ��SKU��:"+String.valueOf(j));
		
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
		Log.info("����ȡ��Ʒ���!");
	}
	public String toDateFormat(String s)throws Exception{
		String m=s.substring(0, 14);
		String date=m.substring(0, 4)+"-"
					+m.substring(4,6)+"-"+
					m.substring(6,8)+" "+
					m.substring(8,10)+":"+
					m.substring(10,12)+":"+
					m.substring(12,14);

		return date;
	}
	
	//���ݲ�Ʒid��ѯ��Ʒ����
	private void getProductDetail(Long offerId){
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("offerId",offerId+"");
			params.put("returnFields", "offerId,offerStatus,subject,amount,type,gmtCreate,gmtModified,skuArray,productFeatureList");
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
			String response =ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//			Log.info("��Ʒ����: "+response);
			JSONObject res=new JSONObject(response);
			JSONObject jo=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0);
		}catch(Exception ex){
			Log.error("��ѯ��Ʒ�������",ex.getMessage());
		}
		
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
