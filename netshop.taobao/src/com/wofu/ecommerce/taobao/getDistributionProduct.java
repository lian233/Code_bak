package com.wofu.ecommerce.taobao;


import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.FenxiaoProduct;
import com.taobao.api.domain.FenxiaoSku;
import com.taobao.api.request.FenxiaoProductsGetRequest;

import com.taobao.api.response.FenxiaoProductsGetResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;

import com.wofu.common.tools.util.log.Log;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;


public class getDistributionProduct extends Thread {

	private static String jobname = "��ȡ������Ʒ�б���ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private  String lasttimeconfvalue=Params.username+"ȡ������Ʒ�б�����ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
			
				getProduct(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮������в�Ʒ�б�
	 * taobao.fenxiao.products.get  �շ�
	 */
	private void getProduct(Connection conn) throws Exception
	{		
		int i=0;
		int j=0;
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		Log.info("��ʼȡ������Ʒ");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=dao.intSelect(sql);
		
		for(int k=0;k<10;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret,"xml");
				FenxiaoProductsGetRequest req=new FenxiaoProductsGetRequest();
				req.setFields("skus");	
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartModified(startdate);
				req.setEndModified(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				FenxiaoProductsGetResponse response = client.execute(req , Params.authcode);
				

				while(true)
				{
					Log.info("test");		
					if (response.getProducts()==null || response.getProducts().size()<=0)
					{				
						if (i==0)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
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
						break;
					}
					
					Log.info("test1");
					for(Iterator it=response.getProducts().iterator();it.hasNext();)
					{
						FenxiaoProduct product=(FenxiaoProduct) it.next();
						i=i+1;

				
						StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),String.valueOf(product.getPid()),
								product.getOuterId(),product.getName(),product.getQuantity().intValue()) ;
						
						if (product.getSkus()!=null)						
						{
							for(Iterator itsku=product.getSkus().iterator();itsku.hasNext();)
							{
								j=j+1;
								
								FenxiaoSku sku=(FenxiaoSku) itsku.next();
															
								Log.info("SKU "+sku.getOuterId()+" "+Formatter.format(product.getModified(),Formatter.DATE_TIME_FORMAT));
	
								
								StockManager.addStockConfigSku(dao, Integer.valueOf(Params.tradecontactid),String.valueOf(product.getPid()),
										String.valueOf(sku.getId()),sku.getOuterId(),sku.getQuantity().intValue()) ;
							}
						}
					
						
						//����ͬ����������ʱ��
		                if (product.getModified().compareTo(modified)>0)
		                {
		                	modified=product.getModified();
		                }
		                		                
					}
					
					if (pageno>=(Double.valueOf(Math.ceil(response.getTotalResults()/40.0))).intValue()) break;
					
					
					pageno++;
					req.setPageNo(pageno);
					response=client.execute(req , Params.authcode);					
				}
				
				Log.info("ȡ����������Ʒ��:"+String.valueOf(i)+" ��SKU��:"+String.valueOf(j));
				
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
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
