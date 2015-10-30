package com.wofu.ecommerce.meilishuo;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo.util.CommHelper;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
public class SynStockExecuter extends Executer {
	
	private String tradecontactid="23";
	private String username="";
	private static String url = "" ;
	private static String appKey = "" ;
	private static String appsecret = "" ;
	private static String vcode = "" ;
	private static String token = "" ;
	private static final String jobName="��ʱ��������˵��Ʒ���";
	
	@Override
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());

		url=prop.getProperty("url",Params.url);
		tradecontactid=prop.getProperty("tradecontactid",Params.tradecontactid);
		username=prop.getProperty("username");
		vcode = prop.getProperty("vcode") ;
		appKey = prop.getProperty("app_key","23");
		appsecret = prop.getProperty("app_sercert","23");
		Log.info("appkey: "+appKey+" appsec: "+appsecret);
	
		try {		
			token = PublicUtils.getToken(this.getDao().getConnection(), Integer.parseInt(tradecontactid));
			updateJobFlag(1);
			
			synStock();
			
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
				
				updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
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
				Log.error(jobName,"���´����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
		
	}
	

		private void synStock() throws Exception
		{

			String sql="select orgId from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
			int orgId=this.getDao().intSelect(sql);
			
			sql="update ecs_stockconfig set errflag=0,errmsg='' where orgId="+orgId;
			this.getDao().execute(sql);
			
			sql="update ecs_stockconfigsku set errflag=0,errmsg='' where orgId="+orgId;
			this.getDao().execute(sql);
			
			for (int k=0;k<10;)
			{
				try
				{
					sql="select * from ecs_stockconfig with(nolock) where orgId="+orgId;
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
							
							sql="select count(*) from ecs_stockconfigsku with(nolock) where orgId="+orgId+" and itemid='"+stockconfig.getItemid()+"'";
							if (this.getDao().intSelect(sql)==0)    //�����Ʒ��ecs_stockconfigsku��û�ж�Ӧ�ļ�¼��������ֱ�����������������Ӧ�ļ�¼��
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
										Log.warn(username,"�Ҳ���SKU��"+stockconfig.getItemcode()+"����Ӧ������,��Ʒ����:"+stockconfig.getTitle());	
							
										
										stockconfig.setErrflag(1);
										stockconfig.setErrmsg("�Ҳ���SKU��"+stockconfig.getItemcode()+"����Ӧ������");
										this.getDao().updateByKeys(stockconfig, "orgId,itemid");
										isfind=false;
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
								}
									
								if (qty<0) qty=0;
								
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
								
								if (qty<0) qty=0;

								//StockUtils.updateItemStock(jobName,this.getDao(),orgId,url,"set",stockconfig,vcode,qty);
								//Log.info("ͬ����ȥ�Ŀ����:  "+qty+"    tradecontactid:   "+tradecontactid);
							}
							
							else     
								
							{
								sql="select * from ecs_stockconfigsku with(nolock) where orgId="+orgId+" and itemid='"+stockconfig.getItemid()+"'";
								
								Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
								int skuSize= vtstockconfigsku.size();
								for(int j=0;j<skuSize;j++)
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
												this.getDao().updateByKeys(stockconfigsku, "orgId,itemid,skuid");
												
												stockconfig.setErrflag(1);
												stockconfig.setErrmsg("�Ҳ���SKU��"+stockconfigsku.getSku()+"����Ӧ������");
												this.getDao().updateByKeys(stockconfig, "orgId,itemid");
												
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
												Log.info("sku��"+stockconfigsku.getSku()+"�Ŀ��ÿ��Ϊ:"+qty);
											}
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
										Log.info("sku��"+stockconfigsku.getSku()+"�Ŀ��Ϊ:"+qty);
										
										if (qty<0) qty=0;
										if(j==skuSize-1){   //���һ��sku�������ŵĿ��Ҳһ�����
											StockUtils.updateSkuStock(token,jobName,this.getDao(),orgId,url,"set",vcode,stockconfig,stockconfigsku,qty,true,appKey,appsecret);
										}else{
											StockUtils.updateSkuStock(token,jobName,this.getDao(),orgId,url,"set",vcode,stockconfig,stockconfigsku,qty,false,appKey,appsecret);
										}
										//Log.info("ͬ����ȥ�Ŀ����:  "+qty+"    tradecontactid:   "+tradecontactid);
										
									}catch(Exception ex){
										if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
											this.getDao().getConnection().rollback();
										Log.error(jobName, ex.getMessage());
										continue;
									}
									
									
								}
							}
							
							k=0;
						}catch(Exception ex){
							if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
								this.getDao().getConnection().rollback();
							Log.error(jobName, ex.getMessage());
							continue;
						}
						
					}
				
					break;
				
				} catch (Exception e) {
					if (++k >= 10)
						throw e;
					if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
						this.getDao().getConnection().rollback();
					Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				
					Thread.sleep(10000L);
				} 
			}
		}
		

	
}