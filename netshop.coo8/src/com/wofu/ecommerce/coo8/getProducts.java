package com.wofu.ecommerce.coo8;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.items.ItemsGetOnsaleRequest;
import com.coo8.api.request.proudct.ProductGetRequest;
import com.coo8.api.request.proudct.ProductsGetRequest;
import com.coo8.api.response.items.ItemsGetOnsaleResponse;
import com.coo8.api.response.product.ProducstGetResponse;
import com.coo8.api.response.product.ProductGetResponse;
import com.coo8.open.product.GoodsPop;
import com.coo8.open.product.ProductPop;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class getProducts extends Thread {
	private static String jobName = "��ȡ�����Ʒ�б�";
	
	private static String lasttimeconfvalue=Params.username+"ȡ��Ʒ����ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private boolean is_importing=false;
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static int interval = 30 ;
	
	
	private static String returnFields="productId,productName,items,catalogId,brandId,productarea,provinceName," +
	"munit,weight,descUrl,gift,phaseAdver,startPhaseTime,endPhaseTime,volume,updater,templateId," +
	"pros,brandName,description,item.outId,item.itemId,item.goodsName,item.originalPrice,item.color," +
	"item.status,item.updater,item.updateTime,item.version,item.brandId,item.catalogId,item.quantity," +
	"item.detail,item.pic.imgId,item.pic.imgUrl,item.pic.index";
	
	private static String lasttime;
	
	private static String access_token=null;
	
	public void run() {
		//��ȡ��Ȩ���ƵĲ���
		Log.info(jobName, "����[" + jobName + "]ģ��");

		do {		
			Connection conn = null;
			is_importing = true;
			try {		
				conn = PoolHelper.getInstance().getConnection(Params.dbname);
				lasttime=PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
	
				getOnSaleProducts(conn);

			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	private void getOnSaleProducts(Connection conn)throws Exception{

		
		int i=0;
		int j=0;
		int pageIndex=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		ECSDao dao=new ECSDao(conn);
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn, sql);
		Log.info("��ʼȡ����ϼ���Ʒ");
		
		for(int k=0;k<5;)
		{
			try
			{	
				Coo8Client cc = new DefaultCoo8Client(Params.url, Params.appKey, Params.secretKey);
				ItemsGetOnsaleRequest request=new ItemsGetOnsaleRequest();
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				request.setStartModified(startdate);
				request.setEndModified(enddate);
				request.setFields(returnFields);				//�����ֶ�
				request.setPageNo(pageIndex);				//�ڼ�ҳ
				request.setPageSize(5);					//ÿҳ���ٸ�
				ItemsGetOnsaleResponse response=cc.execute(request);
				Log.info(startdate+" "+enddate);
				while(true)
				{
					if (response.getTotalResult()==0)
					{				//Log.info("��һ");
						if (i==0)		
						{			//Log.info("�ڶ�");
							try
							{
									//Log.info("����");
									if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
											compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
									{	
										try
						                {	
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
											PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
						                }catch(JException je)
						                {
						                	Log.error(jobName, je.getMessage());
						                }
									}
							}catch(ParseException e)
							{	
								Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
					
					
					
					for(int m=0;m<response.getProductPop().size();m++){
						try{
							//������Ʒ
							ProductPop pp=response.getProductPop().get(m);
							i=i+1;
							
							String produntid=pp.getProduct_no();
							//����ID��õ�����Ʒ����ϸ��Ϣ��SKU
							Coo8Client coo8=new DefaultCoo8Client(Params.url,Params.appKey,Params.secretKey);
							ProductGetRequest req=new ProductGetRequest();
							req.setProductId(produntid);
							ProductGetResponse res=coo8.execute(req);
							
							if(res.getGoods()==null){
								Log.info("��ȡ�����Ʒ��ϸ����ʧ��,productid:"+produntid+"������Ϣ:"+ res.getMsg() + "," + res.getSubMsg());
								continue;
							}
							
							
							StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),pp.getProduct_no(),
									"",pp.getProduct_name(),0) ;
							
							String modifytime="";
							//��ƷSKU
							if(res.getGoods().size()>0){
								for(GoodsPop gp:res.getGoods()){
									modifytime=gp.getModify_time();
									
									j=j+1;
											
									Log.info("SKU "+gp.getSku()+" "+gp.getModify_time());
											
									StockManager.addStockConfigSku(dao, orgid,pp.getProduct_no(),
											gp.getSku(),gp.getGoods_no(),gp.getShow_quantity()) ;
									
								}
								
							}
							//System.out.println("����޸�ʱ�䣺"+modifytime);
							//System.out.println("modifiedʱ��"+modified);
							//����ͬ����������ʱ��
				            if (Formatter.parseDate(modifytime,Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
				            {
				                modified=Formatter.parseDate(modifytime,Formatter.DATE_TIME_FORMAT);
				                System.out.println("modifiedʱ��2"+modified);
				            }
						}catch(Exception ex){
							if(conn!=null && !conn.getAutoCommit()) conn.rollback();
							Log.error(jobName, ex.getMessage());
						}
						
					}
					
					//��ȡ������
					int total=response.getTotalResult();
					//��ҳ��
					int pageTotal=Double.valueOf(Math.ceil(total/5)).intValue();
					Log.info("��ҳ����ǰҳ"+pageIndex+" ��ҳ��"+pageTotal+" ��������"+total);
					//�ж��Ƿ�����һҳ
					if(pageTotal>pageIndex)
						pageIndex ++ ;
					else
					{
						break;
					}
					
						
				}//whileδ
				
				Log.info("ȡ����ϼ�����Ʒ��:"+String.valueOf(i)+" ��SKU��:"+String.valueOf(j));
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
						Log.info(modified.toString()+"ʱ��"+lasttime);
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
}
