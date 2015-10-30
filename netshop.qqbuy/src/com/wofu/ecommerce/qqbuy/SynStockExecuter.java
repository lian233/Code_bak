package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;
import com.wofu.base.job.timer.TimerJob;
import com.wofu.base.job.Executer;

public class SynStockExecuter extends Executer {
	
	private String appOAuthID = "" ;
	private String secretOAuthKey = "" ;
	private String accessToken = "" ;
	private String uin = "" ;
	private String cooperatorId = "" ;
	private static String uri="/item/modifySKUStock.xhtml";
	private String tradecontactid="";
	private String dbname="";
	private String pageSize = "" ;
	private String jobname = "" ;
	private String encoding = "" ;
	private int stockAlarmQty = 7 ;
	private String startTime = "" ;
	private String endTime = "" ;
	private static long monthMillis = 30 * 24 * 60 * 60 * 1000L ; 

	
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		//TimerJob job=(TimerJob) this.getExecuteobj();
		Properties prop=StringUtil.getStringProperties(job.getParams());

		appOAuthID=prop.getProperty("appOAuthID");
		secretOAuthKey=prop.getProperty("secretOAuthKey");
		accessToken=prop.getProperty("accessToken");
		uin=prop.getProperty("uin");
		cooperatorId=prop.getProperty("cooperatorId");
		tradecontactid=prop.getProperty("tradecontactid");
		dbname=prop.getProperty("dbname");
		pageSize=prop.getProperty("pageSize");
		jobname=prop.getProperty("jobname");
		encoding=prop.getProperty("encoding");
		stockAlarmQty=Integer.parseInt(prop.getProperty("stockAlarmQty"));
		
		startTime=Formatter.format(new Date(new Date().getTime()-10*monthMillis), Formatter.DATE_TIME_FORMAT);
		endTime=Formatter.format((new Date()), Formatter.DATE_TIME_FORMAT);
		
		Log.info("startTime="+startTime) ;
		Log.info("endTime="+endTime) ;
		
		Connection conn=PoolHelper.getInstance().getConnection(dbname);
		Hashtable<String, String> inputParams = new Hashtable<String, String>() ;
		inputParams.put("jobname", "") ;
		inputParams.put("accessToken", accessToken) ;
		inputParams.put("appOAuthID", appOAuthID) ;
		inputParams.put("secretOAuthKey", secretOAuthKey) ;
		inputParams.put("cooperatorId", cooperatorId) ;
		inputParams.put("uin", uin) ;
		inputParams.put("encoding", encoding) ;
		inputParams.put("startTime", startTime) ;
		inputParams.put("endTime", endTime) ;
		inputParams.put("pageSize", pageSize) ;

		
		//��ȡQQ���������ϼ���ƷID
		List<Goods> goodsList = StockUtils.getSkuList(jobname, inputParams) ;
		Log.info(jobname,"��ȡ��"+goodsList.size()+"��QQ������Ʒ��") ;
		for (int k=0;k<10;)
		{
			int update = 0 ;
			try
			{
				String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid='"+tradecontactid+"'";
				int orgid=SQLHelper.intSelect(conn, sql);
				for(int i = 0 ; i < goodsList.size() ; i++)
				{
					try 
					{
						Goods goods = goodsList.get(i) ;
						SkuInfo skuInfo = goods.getStockList().get(0) ;
						String stockHourseId = skuInfo.getStockhouseId() ;
						int stockCount = skuInfo.getStockCount() ;
						String sku = skuInfo.getStockLocalcode() ;
						//����skuȡ��ϵͳ��棬���µ�QQ����
						int qty=StockManager.getTradeContactUseableStock(conn,Integer.valueOf(tradecontactid).intValue(),sku);
						
							//�¿��=ϵͳ���ÿ��+QQ�����Ѿ�������
							int newQty = qty + skuInfo.getStockPayedNum() ;
							if(newQty < stockAlarmQty)
							{
								newQty = 0 ;
								Log.info("��Ʒ��"+ sku +"���Ѿ��ﵽ������:"+ stockAlarmQty +",ͬ�����Ϊ:"+ newQty +"") ;
							}
							int oldQty = skuInfo.getStockCount() ;
							//QQ�����ֿ�id
							String stockHouseId = skuInfo.getStockhouseId() ;
							String skuid = goods.getSkuId() ;
							
							PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
							sdk.setCharset(encoding) ;
							HashMap<String, Object> params = sdk.getParams(uri);
							params.put("charset", encoding) ;
							params.put("format", "xml") ;
							params.put("cooperatorId", cooperatorId) ;
							params.put("skuId", skuid) ;
							params.put("stockhouseId", stockHouseId) ;
							params.put("stockCount", String.valueOf(newQty)) ;
							
							String responseText = sdk.invoke() ;
							
							Document doc = DOMHelper.newDocument(responseText, encoding);
							Element resultElement = doc.getDocumentElement();
							String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
							if("0".equals(errorCode))
							{
								update ++ ;
								Log.info("����QQ������Ʒ���ɹ�,sku��"+ sku +"��,ԭ���:"+ oldQty +",�¿��:"+ newQty +",������:"+ skuInfo.getStockPayedNum() +",״̬:"+ skuInfo.getStockSaleState()) ;
							}
							else if("3831".equals(errorCode))
							{
								Log.error(jobname, "����QQ������Ʒ���ʧ��,sku��"+ sku +"��,������Ϣ:"+errorCode+",����Ʒ�ѱ���������ܼ��ٿ��������") ;
							}
							else
							{
								String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
								Log.error(jobname, "����QQ������Ʒ���ʧ��,sku��"+ sku +"��,������Ϣ:"+errorCode+errorMessage) ;
							}
						
						
						//���δ����������Ʒ������
					/*
						if("STOCK_STATE_SELLING".equalsIgnoreCase(skuInfo.getStockSaleState()))
						{
							sql="select count(*) from ecs_stockconfig with(nolock) where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"'";
							if (SQLHelper.intSelect(conn, sql) > 0)
							{
								sql="update ecs_stockconfig set status='1' where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"'" ;
								SQLHelper.executeSQL(conn, sql) ;
							}
							else
								StockManager.StockConfig(conn, skuInfo.getStockLocalcode(), Integer.parseInt(tradecontactid), 1) ;
						}
						else
						{
							//����ϼܲ�Ʒ�¼�,����״̬
							sql="select count(*) from ecs_stockconfig with(nolock) where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"' and status=1";
							if (SQLHelper.intSelect(conn, sql) > 0)
							{
								sql="update ecs_stockconfig set status='0' where orgid="+orgid+" and sku='"+ skuInfo.getStockLocalcode() +"' and status='1'" ;
								SQLHelper.executeSQL(conn, sql) ;
							}
						}
						*/
					
					} catch (Exception e) {
						Log.error(jobname, "����QQ������Ʒ������:"+e.getMessage()) ;
						e.printStackTrace() ;
					}
				}
				Log.info("����QQ������Ʒ�����ɣ����ι�����"+ update + "��QQ������Ʒ��档");
				//����ѭ��
				k = 10 ;
			} catch (Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			} finally 
			{
				try 
				{
					if (conn != null)
						conn.close();
				} catch (Exception e) 
				{
					throw new JException("�ر����ݿ�����ʧ��");
				}
			}
		}
	}
	
}