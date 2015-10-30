package com.wofu.ecommerce.taobao.fenxiao;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import com.wofu.business.fenxiao.stock.StockManager;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.DecItem;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class SynDistributionStockExecuter extends Executer {
	private String url="";
	private String appkey="";
	private String appsecret="";
	private String authcode="";
	private int shopid;
	private int customerid;
	private String username="";
	private static String jobName="ͬ���������";
	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		shopid=Integer.parseInt(prop.getProperty("shopid"));
		customerid=Integer.parseInt(prop.getProperty("customerid"));
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
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
		
	}
	
	private void synStock() throws Exception
	{
		Log.info(username,"��ʼͬ��������Ʒ���",null);
		//����ͬ������
		double synrate=1;
		
		String sql="update DecItem set errflag=0,errmsg='' where CustomerID="+customerid+" and shopid="+shopid;
		this.getDao().execute(sql);
		
		for (int k=0;k<5;)
		{
			try
			{
				sql="select CustomerID,ShopID,Title,itemcode,OuterSkuID,num_iid,sku_id,Modified,isneedsyn,synrate from DecItem with(nolock) where CustomerID="+customerid+" and shopid="+shopid;
				Vector vtstockconfig=this.getDao().multiRowSelect(sql);
				System.out.println(username+"����ִ�����"+sql+" ��������Ϊ  "+vtstockconfig.size());
				boolean isfind;
				boolean ismulti;
				for(int i=0;i<vtstockconfig.size();i++)
				{
					try{
						Hashtable decitem=(Hashtable) vtstockconfig.get(i);
						isfind=true;
						ismulti=false;
						DecItem decItem=new DecItem();
						decItem.getMapData(decitem);
						Log.info("��ƷID:"+decItem.getNum_iid()+" ����:"+decItem.getItemcode());
								
						if (decItem.getIsneedsyn()==0)
						{
							Log.info(username,"���ò���Ҫͬ�����,����:"+decItem.getItemcode()+"sku: "+decItem.getOuterskuid());
							continue;  //����Ҫͬ��
						}


						sql="select count(*) from barcode with(nolock) where custombc='"+decItem.getOuterskuid()+"'";
						if (this.getDao().intSelect(sql)==0)
						{
							sql="select count(*) from MultiSKURef where refcustomercode='"+decItem.getOuterskuid()+"'";
							if (this.getDao().intSelect(sql)==0)
							{
								Log.warn(username,"�Ҳ���SKU��"+decItem.getOuterskuid()+"����Ӧ������,��Ʒ����:"+decItem.getTitle());	
								decItem.setErrflag(1);
								decItem.setErrmsg("�Ҳ���SKU��"+decItem.getOuterskuid()+"����Ӧ������");
								this.getDao().updateByKeys(decItem, "orgid,itemid,skuid");
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
								sql="select customercode,qty from MultiSKURef where refcustomercode='"+decItem.getOuterskuid()+"'";
								Vector multiskulist=this.getDao().multiRowSelect(sql);
								for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
								{
									Hashtable skuref=(Hashtable) itmulti.next();
									String customercode= skuref.get("customercode").toString();
									double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
									qty=StockManager.getFenxiaoTradeContactUseableStock(this.getDao().getConnection(),customerid,shopid,customercode);

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
								qty=StockManager.getFenxiaoTradeContactUseableStock(this.getDao().getConnection(), customerid,shopid,decItem.getOuterskuid());
							}

							if (qty<0) qty=0;	
						}

						//������ÿ����������ӵĿ��С�ڵ��ھ�����,�򽫿��ͬ��Ϊ0
						if (qty<=decItem.getAlarmqty())
						{
							qty=0;
						}
						if (qty<0) qty=0;
						StockUtils.updateSkuStock(this.getDao(),url,appkey,appsecret,authcode,decItem,qty);
					}catch(Exception ex){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						Log.error(jobName, ex.getMessage());
					}

				}
				k=10;
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	
	
}
