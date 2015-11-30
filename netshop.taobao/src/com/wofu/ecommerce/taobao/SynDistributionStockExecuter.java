package com.wofu.ecommerce.taobao;
/**
 * ͬ���������  api���
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class SynDistributionStockExecuter extends Executer {
	private String url="";
	private String appkey="";
	private String appsecret="";
	private String authcode="";
	private String tradecontactid="";
	private String username="";
	private static String jobName="ͬ���������";
	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		tradecontactid=prop.getProperty("tradecontactid");
		username=prop.getProperty("username");
		try {		
			updateJobFlag(1);	
			synStock();
			UpdateTimerJob();
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
				Log.error(jobName, e1.getMessage());
			}
			
			try{
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			}catch(Exception ex){
				Log.error(jobName,"����������Ϣʧ��");
				Log.error(jobName, ex.getMessage());
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
		} finally {
			
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"���´����־ʧ��");
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
			}
			
			try {
				if (this.getConnection() != null){
					this.getConnection().setAutoCommit(true);
					this.getConnection().close();
				}
					
				if (this.getExtconnection() != null){
					this.getExtconnection().setAutoCommit(true);
					this.getExtconnection().close();
				}
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
		
	}
	
	private void synStock() throws Exception
	{
		Log.info(username+" ,��ʼͬ��������Ʒ���");
		//����ͬ������
		double synrate=1;
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		sql="update ecs_stockconfig set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		sql="update ecs_stockconfigsku set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		//����������Ʒ�Ƿ�Ҫ�¼�  1����Ҫ�¼� 0����  Ĭ��1
		sql = "select value from config where name='������Ʒ�����Ƿ��¼�'";
		String value = this.getDao().strSelect(sql);
		Log.info("value: "+value);
		
		//����ͬ��������
		sql  = "select synrate,isNeedSyn from ecs_shopglobalconfig where shopOrgId="+orgid;
		Hashtable tab = this.getDao().oneRowSelect(sql);
		if(tab!=null && tab.size()>0){
			Log.info("isNeedSyn: "+tab.get("isNeedSyn"));
			String isNeedSyn = tab.get("isNeedSyn").toString();
			Log.info("isNeedSyn: "+tab.get("synrate"));
			String temp = tab.get("synrate").toString();
			if(!"".equals(temp))
			synrate = Float.parseFloat(temp);
			if("0".equals(isNeedSyn)) {
				Log.info(username,"���겻��Ҫͬ�����");
				return;
			}
		}
		
		for (int k=0;k<5;)
		{
			try
			{
				sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid;
				Vector vtstockconfig=this.getDao().multiRowSelect(sql);
				
				for(int i=0;i<vtstockconfig.size();i++)
				{
					try{
						Hashtable htstockconfig=(Hashtable) vtstockconfig.get(i);
						
						ECS_StockConfig stockconfig=new ECS_StockConfig();
						
						stockconfig.getMapData(htstockconfig);
						
						Log.info("��ƷID:"+stockconfig.getItemid()+" ����:"+stockconfig.getItemcode());
								
						if (stockconfig.getIsneedsyn()==0)
						{
							Log.info(username,"���ò���Ҫͬ�����,����:"+stockconfig.getItemcode());
							continue;  //����Ҫͬ��
						}
						
						sql="select count(*) from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
						
						if (this.getDao().intSelect(sql)==0)
						{	
							Log.info(username,"����:"+stockconfig.getItemcode()+" ԭ���:"+stockconfig.getStockcount());
							boolean ismulti=false;
							boolean isfind=true;
							
							sql="select count(*) from barcode with(nolock) where custombc='"+stockconfig.getItemcode()+"'";
							if (this.getDao().intSelect(sql)==0)
							{
								sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfig.getItemcode()+"'";
								if (this.getDao().intSelect(sql)==0)
								{
									Log.warn(username,"�Ҳ������š�"+stockconfig.getItemcode()+"����Ӧ������,��Ʒ����:"+stockconfig.getTitle());
					
									
									stockconfig.setErrflag(1);
									stockconfig.setErrmsg("�Ҳ������š�"+stockconfig.getItemcode()+"����Ӧ������");
									this.getDao().updateByKeys(stockconfig, "orgid,itemid");
									isfind=false;
									continue;
								}
								else
									ismulti=true;						
							}
							int qty =0;
							
							if (isfind)
							{
								if (ismulti)
								{
									int minqty=1000000;
									sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfig.getItemcode()+"'";
									Vector multiskulist=this.getDao().multiRowSelect(sql);
									for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
									{
										Hashtable skuref=(Hashtable) itmulti.next();
										String customercode= skuref.get("customercode").toString();
										double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
										qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
										
										qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
										
										if (qty<minqty)
										{
											minqty=qty;
										}
									}
									
									qty=minqty;
								}
								else
								{
									qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfig.getItemcode());
									
								}
								
								if (qty<0) qty=0;
							
							}
							int addstockqty=0;
							if(Math.abs(stockconfig.getAddstockqty())<=1)
								addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
							else
								addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
							
							//������ÿ����������ӵĿ��С�ڵ��ھ�����,�򽫿��ͬ��Ϊ0
							if ((qty+addstockqty)<=stockconfig.getAlarmqty())
							{
								qty=0;
							}
							else
							{
								qty=qty+addstockqty;
							}
							if(synrate!=1){
							qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
							}
							
							if (qty<0) qty=0;	
							if(qty==0){
								//���Ϊ�㣬��Ʒ�¼�
								if("1".equals(value)) 
								StockUtils.updateDistributionProStatus(url, appkey, appsecret, authcode, stockconfig);
							}else{
								StockUtils.updateDistributionItemStock(this.getDao(),url,appkey,appsecret,authcode,stockconfig,qty);
							}
							
								
						}
						else
						{
							sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
							Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
							//ͳ�������е�sku���ܿ���Ƿ�Ϊ0��Ϊ0�Ļ���Ʒ�¼�
							int stockCount=0;
							for(int j=0;j<vtstockconfigsku.size();j++)
							{
								try{
									Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
									ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
									stockconfigsku.getMapData(htstockconfigsku);
									Log.info(username,"SKU:"+stockconfigsku.getSku()+" ԭ���:"+stockconfigsku.getStockcount());
									boolean ismulti=false;
									boolean isfind=true;
									sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
									if (this.getDao().intSelect(sql)==0)
									{
										sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
										if (this.getDao().intSelect(sql)==0)
										{
											Log.warn(username,"�Ҳ���SKU��"+stockconfigsku.getSku()+"����Ӧ������,��Ʒ����:"+stockconfig.getTitle());	
											stockconfigsku.setErrflag(1);
											stockconfigsku.setErrmsg("�Ҳ���SKU��"+stockconfigsku.getSku()+"����Ӧ������");
											this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
											
											stockconfig.setErrflag(1);
											stockconfig.setErrmsg("�Ҳ���SKU��"+stockconfigsku.getSku()+"����Ӧ������");
											this.getDao().updateByKeys(stockconfig, "orgid,itemid");
											
											isfind=false;
										}else								
											ismulti=true;
																	
									}
										
										
										
									int qty =0;
									
									if(isfind)
									{
										if (ismulti)
										{
											int minqty=1000000;
											sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
											Vector multiskulist=this.getDao().multiRowSelect(sql);
											for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
											{
												Hashtable skuref=(Hashtable) itmulti.next();
												String customercode= skuref.get("customercode").toString();
												double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
												qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
												
												qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
												
												if (qty<minqty)
												{
													minqty=qty;
												}
											}
											
											qty=minqty;
										}
										else
										{
											qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
										}
									
										if (qty<0) qty=0;	
									}
									
							
									int addstockqty=0;
									if(Math.abs(stockconfig.getAddstockqty())<=1)
										addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
									else
										addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
									
									//������ÿ����������ӵĿ��С�ڵ��ھ�����,�򽫿��ͬ��Ϊ0
									if ((qty+addstockqty)<=stockconfig.getAlarmqty())
									{
										qty=0;
									}
									else
									{
										qty=qty+addstockqty;
									}
									if(synrate!=1){
										qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
										}
											
									if (qty<0) qty=0;
									
									stockCount+=qty;
									StockUtils.updateDistributionSkuStock(this.getDao(),url,appkey,appsecret,authcode,stockconfig,stockconfigsku,qty);
								}catch(Exception ex){
									if (this.getConnection() != null && !this.getConnection().getAutoCommit())
										this.getConnection().rollback();
									
									if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
										this.getExtconnection().rollback();
									StringWriter sw = new StringWriter();
									ex.printStackTrace(new PrintWriter(sw));
									Log.error(jobName, sw.toString());
								}
								
							}
							//������е�sku�Ŀ����ܺͶ�Ϊ0���Ѳ�Ʒ�¼�
							if(stockCount==0){
								if("1".equals(value))   //�������Ƿ��¼ܷ�����Ʒ
								StockUtils.updateDistributionProStatus(url, appkey, appsecret, authcode, stockconfig);
							}
						}
								
						k=0;
					}catch(Exception ex){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						Log.error(jobName, ex.getMessage());
					}
				
							
				}	
				
				break;
					
			
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				//e.printStackTrace();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	
	
}
